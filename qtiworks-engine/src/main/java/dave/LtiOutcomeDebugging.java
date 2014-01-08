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

import uk.ac.ed.ph.qtiworks.services.LtiOauthUtilities;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public class LtiOutcomeDebugging {

    public static void mainLearn() {
        final String lisOutcomeServiceUrl = "https://www-test.learn.ed.ac.uk/webapps/osc-BasicLTI-BBLEARN/service";
        final String lisResultSourcedid = "WMmnso2tPeD13S1KDk5nsR8/uMAl+XELlBRrYSeky7Q=:_9367_1:_586054_1:qtiwor:_78642_1";
        final double normalizedScore = 0.0;
        final String ltiConsumerKey = "www-test.learn.ed.ac.uk";
        final String ltiConsumerSecret = "oTgCCeEYVSCQ8yowwmTYFDSmrUIHN2Tg";

        final boolean result = LtiOauthUtilities.sendLisResult(lisOutcomeServiceUrl, lisResultSourcedid, ltiConsumerKey, ltiConsumerSecret, normalizedScore);
        System.out.println("Result: " + result);
    }

    public static void mainMoodle() {
        final String lisOutcomeServiceUrl = "http://moodle2.gla.ac.uk/mod/lti/service.php";
        final String lisResultSourcedid = "{\"data\":{\"instanceid\":\"46\",\"userid\":\"29776\",\"launchid\":1276901679},\"hash\":\"bcd08693ff9425753be2d5430063ebfa654cc450197410cdcc1154da0f7d7114\"}";
        final double normalizedScore = 0.0;
        final String ltiConsumerKey = "rWnEhPrcTbFQRngKJLWS539cEVCQdsIS";
        final String ltiConsumerSecret = "tT50ltTeLxpFGEpfKfaHMJDos7YcKr9x";

        final boolean result = LtiOauthUtilities.sendLisResult(lisOutcomeServiceUrl, lisResultSourcedid, ltiConsumerKey, ltiConsumerSecret, normalizedScore);
        System.out.println("Result: " + result);
    }

    public static void main(final String[] args) throws Exception {
        mainMoodle();
    }
}
