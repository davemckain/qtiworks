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
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateOutcomeReportingStatus;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.services.base.AuditLogger;
import uk.ac.ed.ph.qtiworks.services.base.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.node.result.ItemVariable;
import uk.ac.ed.ph.jqtiplus.node.result.OutcomeVariable;
import uk.ac.ed.ph.jqtiplus.node.result.TestResult;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.ParameterStyle;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;

import org.apache.commons.io.input.CharSequenceInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

/**
 * FIXME: Document this!
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class CandidateSessionCloser {

    private static final Logger logger = LoggerFactory.getLogger(CandidateSessionCloser.class);

    @Resource
    private AuditLogger auditLogger;

    @Resource
    private CandidateDataServices candidateDataServices;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    //-------------------------------------------------

    public void closeCandidateItemSession(final CandidateSession candidateSession, final ItemSessionController itemSessionController) {
        final AssessmentResult assessmentResult = candidateDataServices.computeAndRecordItemAssessmentResult(candidateSession, itemSessionController);
        closeCandidateSession(candidateSession, assessmentResult);
    }

    public void closeCandidateTestSession(final CandidateSession candidateSession, final TestSessionController testSessionController) {
        final AssessmentResult assessmentResult = candidateDataServices.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);
        closeCandidateSession(candidateSession, assessmentResult);
    }

    private void closeCandidateSession(final CandidateSession candidateSession, final AssessmentResult assessmentResult) {
        candidateSession.setClosed(true);
        candidateSessionDao.update(candidateSession);
        recordLtiOutcomes(candidateSession, assessmentResult);
    }

    private void recordLtiOutcomes(final CandidateSession candidateSession, final AssessmentResult assessmentResult) {
        /* Nothing to do if LTI is not enabled */
        final Delivery delivery = candidateSession.getDelivery();
        if (!delivery.isLtiEnabled()) {
            auditLogger.recordEvent("Outcomes recording: LTI is not enabled for CandidateSession #" + candidateSession.getId());
            candidateSession.setCandidateOutcomeReportingStatus(CandidateOutcomeReportingStatus.LTI_DISABLED);
            candidateSessionDao.update(candidateSession);
            return;
        }
        final String lisOutcomeServiceUrl = candidateSession.getLisOutcomeServiceUrl();
        if (StringUtilities.isNullOrEmpty(lisOutcomeServiceUrl)) {
            auditLogger.recordEvent("Outcomes recording: Tool consumer did not provide an lis_outcome_service_url for CandidateSession #"
                    + candidateSession.getId());
            candidateSession.setCandidateOutcomeReportingStatus(CandidateOutcomeReportingStatus.LTI_OUTCOMES_DISABLED);
            candidateSessionDao.update(candidateSession);
            return;
        }
        final String lisResultSourceDid = candidateSession.getLisResultSourceDid();
        if (StringUtilities.isNullOrEmpty(lisResultSourceDid)) {
            auditLogger.recordEvent("Outcomes recording: Tool consumer did not specify a lis_resource_sourcedid for CandidateSession #"
                    + candidateSession.getId());
            candidateSession.setCandidateOutcomeReportingStatus(CandidateOutcomeReportingStatus.USER_NOT_REPORTABLE);
            candidateSessionDao.update(candidateSession);
            return;
        }
        final OutcomeVariable resultOutcomeVariable = extractResultOutcomeVariable(assessmentResult, delivery);
        if (resultOutcomeVariable==null) {
            auditLogger.recordEvent("Outcomes recording: Failed to extract outcomeVariable with identifier "
                    + delivery.getLtiResultOutcomeIdentifier()
                    + " from assessmentResult for CandidateSession #"
                    + candidateSession.getId());
            candidateSession.setCandidateOutcomeReportingStatus(CandidateOutcomeReportingStatus.BAD_OUTCOME_IDENTIFIER);
            candidateSessionDao.update(candidateSession);
            return;
        }
        if (!resultOutcomeVariable.hasSignature(Signature.SINGLE_FLOAT)) {
            auditLogger.recordEvent("Outcomes recording: outcomeVariable with identifier "
                    + delivery.getLtiResultOutcomeIdentifier()
                    + " in assessmentResult for CandidateSession #"
                    + candidateSession.getId()
                    + " is not a single float");
            candidateSession.setCandidateOutcomeReportingStatus(CandidateOutcomeReportingStatus.SCORE_NOT_SINGLE_FLOAT);
            candidateSessionDao.update(candidateSession);
            return;
        }
        final double rawScore = ((FloatValue) resultOutcomeVariable.getComputedValue()).doubleValue();
        final Double normalizedScore = computeNormalizedScore(resultOutcomeVariable, rawScore, delivery);
        if (normalizedScore==null) {
            auditLogger.recordEvent("Outcomes recording: not enough data specified to normalize score for CandidateSession #"
                    + candidateSession.getId());
            candidateSession.setCandidateOutcomeReportingStatus(CandidateOutcomeReportingStatus.NO_NORMALIZATION);
            candidateSessionDao.update(candidateSession);
            return;
        }
        /* Schedule result reporting (async) */
        candidateSession.setCandidateOutcomeReportingStatus(CandidateOutcomeReportingStatus.REPORTING_SCHEDULED);
        candidateSessionDao.update(candidateSession);
        logger.warn("Calling sendLtiResult - thread is {}", Thread.currentThread());
        sendLtiResult(candidateSession, normalizedScore);
    }

    @Async
    public void sendLtiResult(final CandidateSession candidateSession, final double normalizedScore) {
        logger.warn("This is now sendLtiResult - thread is {}", Thread.currentThread());

        /* Attempt to report results */
        final boolean wasSuccessful = doLtiResultReporting(candidateSession, normalizedScore);

        /* Record final status */
        if (wasSuccessful) {
            auditLogger.recordEvent("Outcomes recording: successfully sent outcomes for CandidateSession #"
                    + candidateSession.getId());
            candidateSession.setCandidateOutcomeReportingStatus(CandidateOutcomeReportingStatus.REPORTED_SUCCESSFULLY);
        }
        else {
            auditLogger.recordEvent("Outcomes recording: failure sending outcomes for CandidateSession #"
                    + candidateSession.getId());
            candidateSession.setCandidateOutcomeReportingStatus(CandidateOutcomeReportingStatus.REPORT_FAILURE);
        }
        candidateSessionDao.update(candidateSession);
    }

    private boolean doLtiResultReporting(final CandidateSession candidateSession, final double normalizedScore) {
        final String lisOutcomeServiceUrl = candidateSession.getLisOutcomeServiceUrl();
        final Delivery delivery = candidateSession.getDelivery();
        final String ltiConsumerKeyToken = delivery.getLtiConsumerKeyToken();
        final String ltiConsumerSecret = delivery.getLtiConsumerSecret();

        final OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
        final OAuthConsumer consumer = new OAuthConsumer(null, ltiConsumerKeyToken, ltiConsumerSecret, serviceProvider);
        final OAuthAccessor accessor = new OAuthAccessor(consumer);

        /* Create POX XML envelope around message */
        final String poxMessage = buildPoxMessage(candidateSession, normalizedScore);

        /* Wrap as OAuth message */
        final OAuthMessage oauthMessage;
        try {
            final Map<String, String> parameters = new HashMap<String, String>();
            oauthMessage = accessor.newRequestMessage("POST",
                    lisOutcomeServiceUrl, parameters.entrySet(),
                    new CharSequenceInputStream(poxMessage, "UTF-8"));
        }
        catch (final Exception e) {
            logger.warn("Failed to construct OAuthMessage for reporting outcomes", e);
            return false;
        }

        /* Send message to TC result service endpoint */
        String resultBody;
        try {
            logger.info("Attempting to send OAuth message {}", oauthMessage);
            final HttpClient4 httpClient4 = new HttpClient4();
            final OAuthClient client = new OAuthClient(httpClient4);
            final OAuthMessage result = client.invoke(oauthMessage, ParameterStyle.AUTHORIZATION_HEADER);
            resultBody = result.readBodyAsString();
        }
        catch (final Exception e) {
            logger.warn("Failed to send OAuthMessage {}", oauthMessage, e);
            return false;
        }

        /* Extract status */
        logger.info("Received following result body from TP outcome service:\n{}", resultBody);
        final XPathFactory xPathFactory = XPathFactory.newInstance();
        final XPath xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(new PoxNamespaceContext());
        String resultStatus;
        try {
            resultStatus = xPath.evaluate("/x:imsx_POXEnvelopeResponse/x:imsx_POXHeader/x:imsx_POXResponseHeaderInfo/x:imsx_statusInfo/x:imsx_codeMajor",  new InputSource(new StringReader(resultBody)));
        }
        catch (final XPathExpressionException e) {
            throw QtiWorksLogicException.unexpectedException(e);
        }

        return "success".equals(resultStatus);
    }

    private String buildPoxMessage(final CandidateSession candidateSession, final double normalizedScore) {
        final String messageIdentifier = "QTIWORKS_RESULT_" + ServiceUtilities.createRandomAlphanumericToken(32);
        final String lisResultSourceDid = candidateSession.getLisResultSourceDid();
        return "<?xml version='1.0' encoding='UTF-8'?>\n"
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
                + "          <sourcedId>" + lisResultSourceDid + "</sourcedId>\n"
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
                + "</imsx_POXEnvelopeRequest>";
    }

    private Double computeNormalizedScore(final OutcomeVariable outcomeVariable, final double rawScore, final Delivery delivery) {
        final Double normalMaximum = outcomeVariable.getNormalMaximum();
        Double result = null;
        if (normalMaximum!=null) {
            /* Use values specified within the QTI */
            final Double normalMinimum = outcomeVariable.getNormalMinimum();
            if (normalMinimum!=null) {
                result = (rawScore - normalMinimum) / (normalMaximum - normalMinimum);
            }
            else {
                result = (rawScore + normalMaximum) / (normalMaximum * 2.0);
            }
        }
        else {
            final Double ltiResultMinimum = delivery.getLtiResultMinimum();
            final Double ltiResultMaximum = delivery.getLtiResultMaximum();
            if (ltiResultMaximum!=null && ltiResultMinimum!=null) {
                result = (rawScore - ltiResultMinimum) / (ltiResultMaximum - ltiResultMinimum);
            }
        }
        return result;
    }

    private OutcomeVariable extractResultOutcomeVariable(final AssessmentResult assessmentResult, final Delivery delivery) {
        final AssessmentObjectType assessmentType = delivery.getAssessment().getAssessmentType();
        final Identifier resultOutcomeIdentifier;
        try {
            resultOutcomeIdentifier = Identifier.parseString(delivery.getLtiResultOutcomeIdentifier());
        }
        catch (final QtiParseException e) {
            return null;
        }
        switch (assessmentType) {
            case ASSESSMENT_ITEM:
                final List<ItemResult> itemResults = assessmentResult.getItemResults();
                if (itemResults.size()!=1) {
                    throw new QtiWorksLogicException("Expected exactly 1 itemResult within assessmentResult but got " + itemResults.size());
                }
                final ItemResult itemResult = itemResults.get(0);
                final List<ItemVariable> itemVariables = itemResult.getItemVariables();
                return extractOutcomeVariable(itemVariables, resultOutcomeIdentifier);

            case ASSESSMENT_TEST:
                final TestResult testResult = assessmentResult.getTestResult();
                return extractOutcomeVariable(testResult.getItemVariables(), resultOutcomeIdentifier);

            default:
                throw new QtiWorksLogicException("Unexpected swtich casse: " + assessmentType);
        }
    }

    private OutcomeVariable extractOutcomeVariable(final List<ItemVariable> itemVariables, final Identifier outcomeIdentifier) {
        for (final ItemVariable itemVariable : itemVariables) {
            if (itemVariable.getIdentifier().equals(outcomeIdentifier) && itemVariable.getVariableType()==VariableType.OUTCOME) {
                return (OutcomeVariable) itemVariable;
            }
        }
        return null;
    }

    private static final class PoxNamespaceContext implements NamespaceContext {

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
