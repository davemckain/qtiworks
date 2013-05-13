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
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;
import uk.ac.ed.ph.qtiworks.web.GlobalRouter;
import uk.ac.ed.ph.qtiworks.web.lti.LtiAuthenticationFilter;
import uk.ac.ed.ph.qtiworks.web.lti.LtiLaunchData;
import uk.ac.ed.ph.qtiworks.web.lti.LtiOauthMessageUtilities;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for browsing and trying the public samples
 *
 * @author David McKain
 */
@Controller
public class LtiController {

    @Resource
    private CandidateSessionStarter candidateSessionStarter;

    @RequestMapping(value="/launch/{did}", method=RequestMethod.POST)
    public String ltiLaunch(final HttpServletRequest httpRequest, @PathVariable final long did)
            throws  PrivilegeException, DomainEntityNotFoundException {
        final LtiLaunchData ltiLaunchData = LtiAuthenticationFilter.getLaunchData(httpRequest);

        /* FIXME: Decide what to do if this data is not passed */
        final String exitUrl = ltiLaunchData.getLaunchPresentationReturnUrl();

        final CandidateSession candidateSession = candidateSessionStarter.createCandidateSession(did, exitUrl);
        return GlobalRouter.buildSessionStartRedirect(candidateSession);
    }

    @RequestMapping(value="/test", method=RequestMethod.POST)
    public String ltiTest(final HttpServletRequest httpRequest, final Model model) throws IOException {
        final OAuthMessage oauthMessage = OAuthServlet.getMessage(httpRequest, null);
        final LtiLaunchData ltiLaunchData = LtiOauthMessageUtilities.extractLtiLaunchData(oauthMessage);
        final boolean isBasicLtiLaunch = LtiAuthenticationFilter.isBasicLtiLaunchRequest(httpRequest);

        model.addAttribute("ltiLaunchData", ltiLaunchData);
        model.addAttribute("isBasicLtiLaunch", Boolean.valueOf(isBasicLtiLaunch));

        /* FIXME: Add validation checks as well */

        return "ltiTest";
    }
}
