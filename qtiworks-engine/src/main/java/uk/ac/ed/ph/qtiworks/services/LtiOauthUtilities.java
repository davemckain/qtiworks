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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.google.common.xml.XmlEscapers;

/**
 * Utility methods for handling LTI and OAuth data, and managing LIS results.
 * <p>
 * This method may be called in a standalone fashion, which might be useful for debugging
 * certain TCs outside the rest of the QTIWorks Engine machinery. It's recommended you log
 * at debug level when testing. Also consider setting <code>org.apache.http</code> to debug
 * the sending of LIS results.
 *
 * @author David McKain
 */
public final class LtiOauthUtilities {

    private static final Logger logger = LoggerFactory.getLogger(LtiOutcomeService.class);

    /** Encoding to use when sending LIS results */
    private static final String LIS_RESULT_ENCODING = "UTF-8";

    /**
     * Creates an OAuth message for sending the given LIS result data to an outcome service.
     *
     * @param lisOutcomeServiceUrl URL of the outcome service to send to
     * @param lisResultSourcedid <code>lis_result_sourcedid</code> to send
     * @param consumerKey consumer key for signing OAuth request
     * @param consumerSecret secret corresponding to the consumer key
     * @param normalizedScore score to be returned, in the range 0.0 to 1.0
     * @return resulting OAuthMessage
     *
     * @throws IllegalArgumentException if an OAuth message could not be constructed from the
     *   provided data
     * @throws QtiWorksLogicException if unexpected things happen that is (probably) not the
     *   fault of the provided data.
     */
    public static OAuthMessage createLisResultMessage(final String lisOutcomeServiceUrl, final String lisResultSourcedid,
            final String consumerKey, final String consumerSecret,
            final double normalizedScore) {
        Assert.notNull(lisOutcomeServiceUrl, "lisOutcomeServiceUrl");
        Assert.notNull(lisResultSourcedid, "lisResultSourcedid");
        Assert.notNull(consumerKey, "consumerKey");
        Assert.notNull(consumerSecret, "consumerSecret");

        /* Make sure score is in range */
        if (normalizedScore<0.0 || normalizedScore>1.0) {
            throw new IllegalArgumentException("Normalised score must be between 0.0 and 1.0");
        }

        /* Create POX XML message envelope */
        final String poxMessage = buildPoxMessage(lisResultSourcedid, normalizedScore);
        final byte[] poxMessageBytes;
        try {
            poxMessageBytes = poxMessage.getBytes(LIS_RESULT_ENCODING);
        }
        catch (final UnsupportedEncodingException e) {
            throw new QtiWorksLogicException("Unexpected failure encoding POX message as Unicode byte array", e);
        }

        /* Compute body hash for the message */
        final String bodyHash = computeBodyHash(poxMessageBytes);

        /* Build OAuth message */
        final OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
        final OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerSecret, serviceProvider);
        final OAuthAccessor accessor = new OAuthAccessor(consumer);
        final OAuthMessage lisResultMessage;
        try {
            final Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("oauth_body_hash", bodyHash);
            lisResultMessage = accessor.newRequestMessage("POST",
                    lisOutcomeServiceUrl, parameters.entrySet(),
                    new ByteArrayInputStream(poxMessageBytes));
        }
        catch (final IOException e) {
            throw new QtiWorksLogicException("Unexpected IOException while constructing OAuthMessage for reporting outcomes", e);
        }
        catch (final Exception e) {
            /* (These should be due to bad provided data) */
            throw new IllegalArgumentException("Provided data could not be converted to an OAuth request", e);
        }

        /* Add suitably nice HTTP headers */
        final List<Entry<String, String>> httpHeaders = lisResultMessage.getHeaders();
        httpHeaders.add(new OAuth.Parameter("Content-Type", "application/xml"));
        httpHeaders.add(new OAuth.Parameter("Content-Length", Integer.toString(poxMessageBytes.length)));

        return lisResultMessage;
    }

    /**
     * Attempts to send the given LIS result message
     * (constructed by {@link #createLisResultMessage(String, String, String, String, double)}
     * to the corresponding LIS outcome service.
     *
     * @param lisResultMessage LIS result message to be send to the outcome service
     *
     * @throws QtiWorksLogicException
     */
    public static boolean sendLisResultMessage(final OAuthMessage lisResultMessage) {
        Assert.notNull(lisResultMessage, "lisResultMessage");

        /* Send message to TC result service endpoint */
        final OAuthResponseMessage oauthResponseMessage;
        try {
            logger.debug("Attempting to send OAuth message {}", lisResultMessage);
            final HttpClient4 httpClient4 = new HttpClient4();
            final OAuthClient client = new OAuthClient(httpClient4);
            oauthResponseMessage = client.access(lisResultMessage, ParameterStyle.AUTHORIZATION_HEADER);
        }
        catch (final IOException e) {
            logger.warn("Failed to send OAuthMessage {}", lisResultMessage, e);
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
            logger.warn("IOException reading response {} to message {}", oauthResponseMessage, lisResultMessage, e);
            return false;
        }

        /* Let's log what we get as it helps with debugging the various different TCs */
        logger.debug("Received HTTP status code {} and following result body from TP outcome service for message {}:\n{}",
                responseStatusCode, lisResultMessage, responseBody);

        /* Make sure HTTP response code is as expected */
        if (responseStatusCode<200 || responseStatusCode>=300) {
            logger.warn("Got HTTP response code {} to message {} - expected a success result", responseStatusCode, lisResultMessage);
            return false;
        }

        /* Extract POX message status. Do we this using a slightly-awkward XPath that completely
         * ignores the namespace URI, therefore accepting results that are in a different NS from
         * the one stated in the LTI specification. (Moodle is a culprit here!)
         */
        final XPathFactory xPathFactory = XPathFactory.newInstance();
        final XPath xPath = xPathFactory.newXPath();
        String resultStatus;
        try {
            resultStatus = xPath.evaluate("/*/*[local-name()='imsx_POXHeader']/*[local-name()='imsx_POXResponseHeaderInfo']/*[local-name()='imsx_statusInfo']/*[local-name()='imsx_codeMajor']",
                    new InputSource(new StringReader(responseBody)));
        }
        catch (final XPathExpressionException e) {
            throw QtiWorksLogicException.unexpectedException(e);
        }

        /* Expect status to be 'success' */
        final boolean successful = "success".equals(resultStatus);
        if (!successful) {
            logger.warn("Outcome service returned unsccessful response. message={}, responseBody={}", lisResultMessage, responseBody);
        }
        return successful;
    }

    /**
     * Combined {@link #createLisResultMessage(String, String, String, String, double)}
     * and {@link #sendLisResult(String, String, String, String, double)} to create and
     * send an LIS result message to an outcome service.
     *
     * @param lisOutcomeServiceUrl URL of the outcome service to send to
     * @param lisResultSourcedid <code>lis_result_sourcedid</code> to send
     * @param consumerKey consumer key for signing OAuth request
     * @param consumerSecret secret corresponding to the consumer key
     * @param normalizedScore score to be returned, in the range 0.0 to 1.0
     * @return true on success, false otherwise.
     *
     * @throws IllegalArgumentException
     * @throws QtiWorksLogicException
     */
    public static boolean sendLisResult(final String lisOutcomeServiceUrl, final String lisResultSourcedid,
            final String consumerKey, final String consumerSecret,
            final double normalizedScore) {
        final OAuthMessage lisResultMessage = createLisResultMessage(lisOutcomeServiceUrl, lisResultSourcedid, consumerKey, consumerSecret, normalizedScore);
        return sendLisResultMessage(lisResultMessage);
    }

    /**
     * Builds the appropriate POX message for sending the result back to the TC.
     */
    private static String buildPoxMessage(final String lisResultSourcedid, final double normalizedScore) {
        final String messageIdentifier = "QTIWORKS_RESULT_" + ServiceUtilities.createRandomAlphanumericToken(32);
        return "<?xml version='1.0' encoding='" + LIS_RESULT_ENCODING + "'?>\n"
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
                + "          <sourcedId>" + XmlEscapers.xmlContentEscaper().escape(lisResultSourcedid) + "</sourcedId>\n"
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
}
