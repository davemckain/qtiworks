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
package dave;

import uk.ac.ed.ph.qtiworks.services.base.ServiceUtilities;

import java.util.HashMap;
import java.util.Map;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.ParameterStyle;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;

import org.apache.commons.io.input.CharSequenceInputStream;

/**
 * Temporary for trying out sending results to BlackBoard CourseSites.
 *
 * @author David McKain
 */
public class LtiResultsTest {

    /* These are taken from delivery 111 */
    public static final String KEY = "111XSKf6SiE9BPmr05aVvvDNfgD3iJuTSUxs";
    public static final String SECRET  = "JA5F99HUDC0IwHeyW0ZQUblLRWS5Uwq4";

    public static final String RESULT_SERVICE_URL = "https://www.coursesites.com/webapps/gradebook/lti11grade";
    public static final String RESULT_SOURCEDID = "bbgc2478149gi1238953";

    public static void main(final String[] args) throws Exception {
        final OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
        final OAuthConsumer consumer = new OAuthConsumer(null, KEY, SECRET, serviceProvider);
        final OAuthAccessor accessor = new OAuthAccessor(consumer);

        final String poxMessage = buildPoxMessage(RESULT_SOURCEDID, "0.5");

        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("param", "O'Boily");
        final OAuthMessage message = accessor.newRequestMessage("POST", RESULT_SERVICE_URL, parameters.entrySet(), new CharSequenceInputStream(poxMessage, "UTF-8"));

        System.out.println("Sending: " + message);

        final HttpClient4 httpClient4 = new HttpClient4();
        final OAuthClient client = new OAuthClient(httpClient4);
        final OAuthMessage result = client.invoke(message, ParameterStyle.AUTHORIZATION_HEADER);

        System.out.println("Result: " + result);
        System.out.println("Result body: " + result.readBodyAsString());
    }

    public static String buildPoxMessage(final String sourceDid, final String normalizedScore) {
        final String messageIdentifier = ServiceUtilities.createRandomAlphanumericToken(32);
        return "<?final xml version = '1.0' encoding = 'UTF-8'?>\n"
                + "<imsx_POXEnvelopeRequest xmlns = 'http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0'>\n"
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
                + "          <sourcedId>" + sourceDid + "</sourcedId>\n"
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

}
