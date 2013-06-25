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
package uk.ac.ed.ph.qtiworks.web.controller.lti;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;
import uk.ac.ed.ph.qtiworks.web.GlobalRouter;
import uk.ac.ed.ph.qtiworks.web.lti.LtiLaunchData;
import uk.ac.ed.ph.qtiworks.web.lti.LtiLaunchDecodingService;
import uk.ac.ed.ph.qtiworks.web.lti.LtiLaunchResult;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.server.OAuthServlet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dave.LtiResultsTest;

/**
 * Controller for browsing and trying the public samples
 * <p>
 * THIS IS WORK IN PROGRESS AND SUBJECT TO CHANGE!
 *
 * @author David McKain
 */
@Controller
public class LtiController {

    private static final Logger logger = LoggerFactory.getLogger(LtiController.class);

    @Resource
    private CandidateSessionStarter candidateSessionStarter;

    @Resource
    private LtiLaunchDecodingService ltiLaunchDecodingService;

    @RequestMapping(value="/launch/{did}", method=RequestMethod.POST)
    public String ltiLaunch(final HttpServletRequest request, final HttpServletResponse response,
            @PathVariable final long did)
            throws  PrivilegeException, DomainEntityNotFoundException, IOException {
        /* Decode LTI launch request, and bail out on error */
        final LtiLaunchResult ltiLaunchResult = ltiLaunchDecodingService.extractLtiLaunchData(request);
        if (ltiLaunchResult.isError()) {
            response.sendError(ltiLaunchResult.getErrorCode(), ltiLaunchResult.getErrorMessage());
            return null;
        }

        /* Extract relevant data */
        final LtiLaunchData ltiLaunchData = ltiLaunchResult.getLtiLaunchData();
        final String exitUrl = ltiLaunchData.getLaunchPresentationReturnUrl();
        final String lisOutcomeServiceUrl = ltiLaunchData.getLisOutcomeServiceUrl();
        final String lisResultSourcedid = ltiLaunchData.getLisResultSourcedid();

        /* Start session */
        final LtiUser ltiUser = ltiLaunchResult.getLtiUser();
        final CandidateSession candidateSession = candidateSessionStarter.createLinkLevelLtiCandidateSession(ltiUser,
                did, exitUrl, lisOutcomeServiceUrl, lisResultSourcedid);
        return GlobalRouter.buildSessionStartRedirect(candidateSession);
    }

    /** LTI debugging and diagnostic help */
    @RequestMapping(value="/debug", method=RequestMethod.POST)
    public String ltiDebug(final HttpServletRequest httpRequest, final Model model) throws IOException {
        final LtiLaunchResult ltiLaunchResult = ltiLaunchDecodingService.extractLtiLaunchData(httpRequest);
        model.addAttribute("ltiLaunchResult", ltiLaunchResult);
        return "ltiDebug";
    }

    /** TEMPORARY! Fake result service */
    @RequestMapping(value="/test", method=RequestMethod.POST)
    @ResponseBody
    public String ltiTest(final HttpServletRequest httpRequest) {
        try {
            final OAuthMessage message = OAuthServlet.getMessage(httpRequest, null);
            System.out.println("Got " + message);

            /* Note hard-coded UTF-8 here, since we're sending XML */
            final InputStream messageBodyStream = message.getBodyAsStream();
            final String messageBody = IOUtils.toString(messageBodyStream, "UTF-8");
            System.out.println("Message body is " + messageBody);

            /* Let's check but not enforce validity */
            final String consumerKey = message.getConsumerKey();
            final OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
            final OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, LtiResultsTest.SECRET, serviceProvider);
            final OAuthAccessor accessor = new OAuthAccessor(consumer);
            final OAuthValidator oAuthValidator = new SimpleOAuthValidator();
            try {
                oAuthValidator.validateMessage(message, accessor);
            }
            catch (final OAuthException e) {
                System.out.println("GOT EXCEPTION DURING VALIDATION " + e);
            }

            return "Success";
        }
        catch (final Exception e) {
            logger.error("Intercepted", e);
            return "Failure";
        }
    }
}
