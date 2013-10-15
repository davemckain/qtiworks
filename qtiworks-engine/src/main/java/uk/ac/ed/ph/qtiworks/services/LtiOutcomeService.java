/* Copyright (c) 2012-2013, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksDeploymentSettings;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.LisOutcomeReportingStatus;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiDomain;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.domain.entities.QueuedLtiOutcome;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserType;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.QueuedLtiOutcomeDao;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.ParameterStyle;
import net.oauth.client.OAuthClient;
import net.oauth.client.OAuthResponseMessage;
import net.oauth.client.httpclient4.HttpClient4;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

/**
 * This service is responsible for sending outcome data back to LTI Tool Consumers.
 * <p>
 * The actual work of this service is performed asynchronously via {@link ScheduledService},
 * with some basic durability provided by persisting the data to be sent within the entity model.
 *
 * @see ScheduledService
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class LtiOutcomeService {

    private static final Logger logger = LoggerFactory.getLogger(LtiOutcomeService.class);

    private static final String ENCODING = "UTF-8";

    @Resource
    private AuditLogger auditLogger;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    @Resource
    private QueuedLtiOutcomeDao queuedLtiOutcomeDao;

    /**
     * Delays (in minutes) to wait until next retry. We try often to start with, then
     * less frequently. Then we give up.
     *
     * TODO: Maybe this should be configurable via {@link QtiWorksDeploymentSettings}?
     */
    private static final int[] retryDelays = new int[] {
            1, 5, 10, 60, 60, 60, 240, 240, 240
    };

    //-------------------------------------------------

    @Async
    public void queueLtiResult(final CandidateSession candidateSession, final double lisScore) {
        Assert.notNull(candidateSession);
        if (lisScore<0.0 || lisScore>1.0) {
            throw new IllegalArgumentException("Score must be between 0.0 and 1.0");
        }

        /* Update session status to record final score and indicate results have been queued */
        candidateSession.setLisScore(Double.valueOf(lisScore));
        candidateSession.setLisOutcomeReportingStatus(LisOutcomeReportingStatus.TC_RETURN_SCHEDULED);
        candidateSessionDao.update(candidateSession);

        /* Persist new queued outcome */
        final QueuedLtiOutcome outcome = new QueuedLtiOutcome();
        outcome.setCandidateSession(candidateSession);
        outcome.setScore(lisScore);
        outcome.setFailureCount(0);
        queuedLtiOutcomeDao.persist(outcome);

        /* (Data will be sent to TC next time the service wakes up) */
        auditLogger.recordEvent("Queued new LTI outcome #" + outcome.getId()
                + " containing score " + lisScore
                + " to be returned to for CandidateSession #" + candidateSession.getId());
        logger.info("Queued new LTI outcome #{} containing score {} to be returned to for CandidateSession #{}",
                new Object[] { outcome.getId(), lisScore, candidateSession.getId() });
    }

    //-------------------------------------------------

    /**
     * Attempts to send {@link QueuedLtiOutcome}s to the relevant LIS result services.
     * <p>
     * This will send all new {@link QueuedLtiOutcome}s and any previously failed ones if the
     * current timestamp is greater than their retry time. This behaviour can be overridden,
     * forcing ALL {@link QueuedLtiOutcome}s to be send by setting the ignoreRetryTimes argument
     * to true.
     * <p>
     * The logic here will check for duplicate {@link QueuedLtiOutcome}s for a given
     * {@link CandidateSession}, only sending the most recent outcomes back.
     * <p>
     * Usage note: This MUST be called serially.
     * <p>
     * @param ignoreRetryTimes set to true to ignore any retry times set after previous failures.
     * @return Pair of integers: (number of outcome send failures, total outcomes sent)
     *
     * @see ScheduledService#sendNextQueuedLtiOutcomes()
     */
    public Pair<Integer, Integer> sendQueuedLtiOutcomes(final boolean ignoreRetryTimes) {
        /* Look up all unsent outcomes */
        final List<QueuedLtiOutcome> pendingOutcomes = queuedLtiOutcomeDao.getAllQueuedOutcomes();

        /* Eliminate any duplicate outcomes for the same CandidateSession, always taking the newest
         * outcome over any earlier ones. (Duplicate outcomes can happen when delivering items,
         * which can sometimes be re-opened by candidates.)
         */
        final LinkedHashMap<Long, QueuedLtiOutcome> outcomesBySessionMap = new LinkedHashMap<Long, QueuedLtiOutcome>();
        for (final QueuedLtiOutcome queuedLtiOutcome : pendingOutcomes) {
            final CandidateSession candidateSession = queuedLtiOutcome.getCandidateSession();
            final Long candidateSessionId = candidateSession.getId();
            final QueuedLtiOutcome earlierQueuedOutcomeForSession = outcomesBySessionMap.get(candidateSessionId);
            if (earlierQueuedOutcomeForSession!=null) {
                /* Earlier outcome is queued up, so remove this in preference for this later outcome */
                candidateSession.setLisOutcomeReportingStatus(LisOutcomeReportingStatus.TC_RETURN_SCHEDULED);
                candidateSessionDao.update(candidateSession);
                queuedLtiOutcomeDao.remove(earlierQueuedOutcomeForSession);
                auditLogger.recordEvent("De-queued LTI outcome #" + earlierQueuedOutcomeForSession.getId()
                        + " as a later one for the same CandidateSession is already queued up");
                logger.info("De-queued LTI outcome #{} as a later one for the same CandidateSession is already queued up",
                        earlierQueuedOutcomeForSession.getId());
            }
            outcomesBySessionMap.put(candidateSessionId, queuedLtiOutcome);
        }

        /* Now attempt to send remaining outcomes to relevant result services */
        final int totalSendCount = outcomesBySessionMap.size();
        int failedSendCount = 0;
        final Date timestamp = new Date();
        for (final QueuedLtiOutcome queuedLtiOutcome : outcomesBySessionMap.values()) {
            final Date retryTime = queuedLtiOutcome.getRetryTime();
            if (ignoreRetryTimes || retryTime==null || retryTime.before(timestamp)) {
                final boolean successful = handleQueuedLtiOutcome(queuedLtiOutcome);
                if (!successful) {
                    failedSendCount++;
                }
            }
        }
        return new Pair<Integer, Integer>(Integer.valueOf(failedSendCount), Integer.valueOf(totalSendCount));
    }

    private boolean handleQueuedLtiOutcome(final QueuedLtiOutcome queuedLtiOutcome) {
        final CandidateSession candidateSession = queuedLtiOutcome.getCandidateSession();
        final boolean successful = trySendQueuedLtiOutcome(queuedLtiOutcome);
        if (successful) {
            /* Outcome sent successfully, so remove from queue */
            candidateSession.setLisOutcomeReportingStatus(LisOutcomeReportingStatus.TC_RETURN_SUCCESS);
            queuedLtiOutcomeDao.remove(queuedLtiOutcome);
            candidateSessionDao.update(candidateSession);
            auditLogger.recordEvent("Successfully sent LTI outcome #" + queuedLtiOutcome.getId()
                    + " to LIS outcome service at " + candidateSession.getLisOutcomeServiceUrl());
            logger.info("Successfully sent LTI outcome #{} to LIS outcome service at {}",
                    queuedLtiOutcome.getId(), candidateSession.getLisOutcomeServiceUrl());
        }
        else {
            /* Outcome failed. Retry up to limit of retries */
            final int failureCount = queuedLtiOutcome.getFailureCount();
            if (failureCount < retryDelays.length) {
                queuedLtiOutcome.setFailureCount(failureCount + 1);
                queuedLtiOutcome.setRetryTime(new Date(System.currentTimeMillis() + (1000L * 60 * retryDelays[failureCount])));
                candidateSession.setLisOutcomeReportingStatus(LisOutcomeReportingStatus.TC_RETURN_FAIL_TERMINAL);
                queuedLtiOutcomeDao.update(queuedLtiOutcome);
                candidateSessionDao.update(candidateSession);
                auditLogger.recordEvent("Failure #" + (failureCount+1)
                        + " to send LTI outcome #" + queuedLtiOutcome.getId()
                        + " to LIS outcome service at " + candidateSession.getLisOutcomeServiceUrl()
                        + ". Will try again at " + queuedLtiOutcome.getRetryTime());
                logger.warn("Failure #{} to send LTI outcome #{} to LIS outcome service at {}. Will try again at {}",
                        new Object[] { failureCount+1, queuedLtiOutcome.getId(), candidateSession.getLisOutcomeServiceUrl(), queuedLtiOutcome.getRetryTime() });
            }
            else {
                candidateSession.setLisOutcomeReportingStatus(LisOutcomeReportingStatus.TC_RETURN_FAIL_TERMINAL);
                queuedLtiOutcomeDao.remove(queuedLtiOutcome);
                candidateSessionDao.update(candidateSession);
                auditLogger.recordEvent("Final failure #" + (failureCount+1)
                        + " to send LTI outcome #" + queuedLtiOutcome.getId()
                        + " to LIS outcome service at " + candidateSession.getLisOutcomeServiceUrl()
                        + ". Outcome has been removed from queue");
                logger.error("Final failure #{} to send LTI outcome #{} to LIS outcome service at {}. Outcome has been removed from queue",
                        new Object[] { failureCount+1, queuedLtiOutcome.getId(), candidateSession.getLisOutcomeServiceUrl() });
            }
        }
        return successful;
    }

    /**
     * Attempts to send the given {@link QueuedLtiOutcome} back to the corresponding LIS
     * result service. Returns true if the result was successfully returned, false otherwise.
     */
    private boolean trySendQueuedLtiOutcome(final QueuedLtiOutcome queuedLtiOutcome) {
        /* Extract the information we need to send */
        final double normalizedScore = queuedLtiOutcome.getScore();
        final CandidateSession candidateSession = queuedLtiOutcome.getCandidateSession();
        final String lisResultSourcedid = candidateSession.getLisResultSourcedid();
        final String lisOutcomeServiceUrl = candidateSession.getLisOutcomeServiceUrl();
        final User candidate = candidateSession.getCandidate();
        if (candidate.getUserType()!=UserType.LTI) {
            logger.warn("Candidate must be an LTI user - ignoring {}", queuedLtiOutcome);
            return false;
        }
        final LtiUser ltiCandidate = (LtiUser) candidate;
        final String ltiConsumerKey, ltiConsumerSecret;
        switch (ltiCandidate.getLtiLaunchType()) {
            case DOMAIN:
                final LtiDomain ltiDomain = ltiCandidate.getLtiDomain();
                ltiConsumerKey = ltiDomain.getConsumerKey();
                ltiConsumerSecret = ltiDomain.getConsumerSecret();
                break;

            case LINK:
                final Delivery delivery = ltiCandidate.getDelivery();
                ltiConsumerKey= delivery.getLtiConsumerKeyToken();
                ltiConsumerSecret = delivery.getLtiConsumerSecret();
                break;

            default:
                throw new QtiWorksLogicException("Unexpected switch case " + ltiCandidate.getLtiLaunchType());
        }

        /* Now send the result */
        return sendLisResult(lisOutcomeServiceUrl, lisResultSourcedid,
                ltiConsumerKey, ltiConsumerSecret, normalizedScore);
    }

    /**
     * Attempts to send an LIS result to an outcome service.
     * <p>
     * (This method may be called in a standalone fashion, which might be useful for debugging
     * certain TCs outside the rest of the QTIWorks Engine machinery. It's recommended you log
     * at debug level when testing. Also consider setting <code>org.apache.http</code> to debug
     * as well.)
     *
     * @param lisOutcomeServiceUrl URL of the outcome service to send to
     * @param lisResultSourcedid <code>lis_result_sourcedid</code> to send
     * @param ltiConsumerKey consumer key for signing OAuth request
     * @param ltiConsumerSecret secret corresponding to the consumer key
     * @param normalizedScore score to be returned, in the range 0.0 to 1.0
     * @return true on success, false otherwise.
     */
    public static boolean sendLisResult(final String lisOutcomeServiceUrl, final String lisResultSourcedid,
            final String ltiConsumerKey, final String ltiConsumerSecret,
            final double normalizedScore) {
        /* Create POX XML message envelope */
        final String poxMessage = buildPoxMessage(lisResultSourcedid, normalizedScore);
        final byte[] poxMessageBytes;
        try {
            poxMessageBytes = poxMessage.getBytes(ENCODING);
        }
        catch (final UnsupportedEncodingException e1) {
            throw new QtiWorksLogicException("Unexpected failure encoding POX message as Unicode byte array");
        }

        /* Compute body hash for the message */
        final String bodyHash = computeBodyHash(poxMessageBytes);

        /* Build OAuth message */
        final OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
        final OAuthConsumer consumer = new OAuthConsumer(null, ltiConsumerKey, ltiConsumerSecret, serviceProvider);
        final OAuthAccessor accessor = new OAuthAccessor(consumer);
        final OAuthMessage oauthMessage;
        try {
            final Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("oauth_body_hash", bodyHash);
            oauthMessage = accessor.newRequestMessage("POST",
                    lisOutcomeServiceUrl, parameters.entrySet(),
                    new ByteArrayInputStream(poxMessageBytes));
        }
        catch (final Exception e) {
            logger.warn("Failed to construct OAuthMessage for reporting outcomes", e);
            return false;
        }

        /* Add suitably nice HTTP headers */
        final List<Entry<String, String>> httpHeaders = oauthMessage.getHeaders();
        httpHeaders.add(new OAuth.Parameter("Content-Type", "application/xml"));
        httpHeaders.add(new OAuth.Parameter("Content-Length", Integer.toString(poxMessageBytes.length)));

        /* Send message to TC result service endpoint */
        final OAuthResponseMessage oauthResponseMessage;
        try {
            logger.debug("Attempting to send OAuth message {}", oauthMessage);
            final HttpClient4 httpClient4 = new HttpClient4();
            final OAuthClient client = new OAuthClient(httpClient4);
            oauthResponseMessage = client.access(oauthMessage, ParameterStyle.AUTHORIZATION_HEADER);
        }
        catch (final IOException e) {
            logger.warn("Failed to send OAuthMessage {}", oauthMessage, e);
            return false;
        }

        /* Read HTTP status code and response body  */
        int responseStatusCode;
        String responseBody;
        try {
            responseBody = oauthResponseMessage.readBodyAsString();
            responseStatusCode = oauthResponseMessage.getHttpResponse().getStatusCode();
        }
        catch (final IOException e) {
            logger.warn("IOException reading response {} to message {}", oauthResponseMessage, oauthMessage, e);
            return false;
        }

        /* Let's log what we get as it helps with debugging the various different TCs */
        logger.debug("Received HTTP status code {} and following result body from TP outcome service for message {}:\n{}",
                responseStatusCode, oauthMessage, responseBody);

        /* Make sure HTTP response code is as expected */
        if (responseStatusCode<200 || responseStatusCode>=300) {
            logger.warn("Got HTTP response code {} to message {} - expected a success result", responseStatusCode, oauthMessage);
            return false;
        }

        /* Extract POX message status */
        final XPathFactory xPathFactory = XPathFactory.newInstance();
        final XPath xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(new PoxNamespaceContext());
        String resultStatus;
        try {
            resultStatus = xPath.evaluate("/x:imsx_POXEnvelopeResponse/x:imsx_POXHeader/x:imsx_POXResponseHeaderInfo/x:imsx_statusInfo/x:imsx_codeMajor",  new InputSource(new StringReader(responseBody)));
        }
        catch (final XPathExpressionException e) {
            throw QtiWorksLogicException.unexpectedException(e);
        }

        /* Expect status to be 'success' */
        final boolean successful = "success".equals(resultStatus);
        if (!successful) {
            logger.warn("Outcome service returned unsccessful response. message={}, responseBody={}", oauthMessage, responseBody);
        }
        return successful;
    }

    /**
     * Builds the appropriate POX message for sending the result back to the TC.
     */
    private static String buildPoxMessage(final String lisResultSourcedid, final double normalizedScore) {
        final String messageIdentifier = "QTIWORKS_RESULT_" + ServiceUtilities.createRandomAlphanumericToken(32);
        return "<?xml version='1.0' encoding='" + ENCODING + "'?>\n"
                + "<imsx_POXEnvelopeRequest xmlns='http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0'>\n"
                + "  <imsx_POXHeader>\n"
                + "    <imsx_POXRequestHeaderInfo>\n"
                + "      <imsx_version>V1.0</imsx_version>\n"
                + "      <imsx_messageIdentifier>" + messageIdentifier + "</imsx_messageIdentifier>\n"
                + "    </imsx_POXRequestHeaderInfo>\n"
                + "  </imsx_POXHeader>\n"
                + "  <imsx_POXBody>\n"
                + "    <replaceResultRequest>\n"
                + "      <resultRecord>\n"
                + "        <sourcedGUID>\n"
                + "          <sourcedId>" + StringEscapeUtils.escapeXml(lisResultSourcedid) + "</sourcedId>\n"
                + "        </sourcedGUID>\n"
                + "        <result>\n"
                + "          <resultScore>\n"
                + "            <language>en</language>\n"
                + "            <textString>" + normalizedScore + "</textString>\n"
                + "          </resultScore>\n"
                + "        </result>\n"
                + "      </resultRecord>\n"
                + "    </replaceResultRequest>\n"
                + "  </imsx_POXBody>\n"
                + "</imsx_POXEnvelopeRequest>\n\n";
    }

    /**
     * Computes the hash for the POX message body.
     * <p>
     * (The net.oauth library does not compute this for us!)
     *
     * @param poxMessage
     */
    private static String computeBodyHash(final byte[] poxMessageBytes) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA1");
            md.update(poxMessageBytes);
        }
        catch (final Exception e) {
            throw new QtiWorksLogicException("Unexpected failure computing body digest");
        }
        final byte[] output = Base64.encodeBase64(md.digest());
        return new String(output);
    }

    /**
     * Trivial implementation of {@link NamespaceContext} to handle POX messages
     */
    static final class PoxNamespaceContext implements NamespaceContext {

        public static final String POX_NAMESPACE_URI = "http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0";

        @Override
        public String getNamespaceURI(final String prefix) {
            return POX_NAMESPACE_URI;
        }

        @Override
        public String getPrefix(final String namespaceURI) {
            return "";
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Iterator getPrefixes(final String namespaceURI) {
            return Arrays.asList("").iterator();
        }
    }
}
