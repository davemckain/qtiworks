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

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiDomain;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiLaunchType;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateException;
import uk.ac.ed.ph.qtiworks.web.GlobalRouter;
import uk.ac.ed.ph.qtiworks.web.lti.DecodedLtiLaunch;
import uk.ac.ed.ph.qtiworks.web.lti.LtiAuthenticationTicket;
import uk.ac.ed.ph.qtiworks.web.lti.LtiLaunchData;
import uk.ac.ed.ph.qtiworks.web.lti.LtiLaunchService;
import uk.ac.ed.ph.qtiworks.web.lti.LtiResourceAuthenticationFilter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller handling LTI launches
 *
 * @author David McKain
 */
@Controller
public class LtiLaunchController {

    @Resource
    private CandidateSessionStarter candidateSessionStarter;

    @Resource
    private LtiLaunchService ltiLaunchService;

    /** Domain-level LTI launch */
    @RequestMapping(value="/domainlaunch", method=RequestMethod.POST)
    public String ltiDomainLevelLaunch(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        /* Decode LTI launch request, and bail out on error */
        final DecodedLtiLaunch decodedLtiLaunch = ltiLaunchService.decodeLtiLaunchData(request, LtiLaunchType.DOMAIN);
        if (decodedLtiLaunch.isError()) {
            response.sendError(decodedLtiLaunch.getErrorCode(), decodedLtiLaunch.getErrorMessage());
            return null;
        }
        final LtiLaunchData ltiLaunchData = decodedLtiLaunch.getLtiLaunchData();

        /* Make sure this is a domain launch */
        final LtiUser ltiUser = decodedLtiLaunch.getLtiUser();
        final LtiDomain ltiDomain = ltiUser.getLtiDomain();
        if (ltiDomain==null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The tool consumer has attempted a domain-level launch using a link-level key");
            return null;
        }

        /* Extract/create the corresponding LtiResource for this launch */
        final LtiResource ltiResource = ltiLaunchService.provideLtiResource(decodedLtiLaunch); /* (May be null for candidates) */
        final UserRole userRole = ltiUser.getUserRole();

        if (userRole==UserRole.INSTRUCTOR) {
            /* If user is an instructor, we'll forward to the LTI instructor MVC after
             * "authenticating" the user by creating and storing an LtiDomainTicket
             * in the session */
            final LtiAuthenticationTicket ltiDomainTicket = new LtiAuthenticationTicket(ltiUser, ltiResource, ltiResource.getLtiContext(), ltiLaunchData.getLaunchPresentationReturnUrl());
            LtiResourceAuthenticationFilter.authenticateUserForResource(request.getSession(), ltiResource, ltiDomainTicket);
            return "redirect:/lti/resource/" + ltiResource.getId();
        }
        else if (userRole==UserRole.CANDIDATE) {
            /* If user is a candidate, then we'll launch/reuse a candidate session */
            if (ltiResource==null) {
                return "candidateLaunchError";
            }

            /* Extract relevant data */
            final String exitUrl = ltiLaunchData.getLaunchPresentationReturnUrl();
            final String lisOutcomeServiceUrl = ltiLaunchData.getLisOutcomeServiceUrl();
            final String lisResultSourcedid = ltiLaunchData.getLisResultSourcedid();

            /* Start/reuse candidate session */
            try {
                final CandidateSession candidateSession = candidateSessionStarter.launchDomainLevelLtiCandidateSession(ltiUser,
                        ltiResource, exitUrl, lisOutcomeServiceUrl, lisResultSourcedid);
                return GlobalRouter.buildSessionStartRedirect(candidateSession);
            }
            catch (final CandidateException e) {
                return "candidateLaunchError";
            }
        }
        else {
            throw new QtiWorksLogicException("Unexpected LTI userRole " + userRole);
        }
    }

    /** Link-level LTI launch (always treated as candidate) */
    @RequestMapping(value="/linklaunch", method=RequestMethod.POST)
    public String ltiLinkLevelLaunch(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        /* Decode LTI launch request, and bail out on error */
        final DecodedLtiLaunch decodedLtiLaunch = ltiLaunchService.decodeLtiLaunchData(request, LtiLaunchType.LINK);
        if (decodedLtiLaunch.isError()) {
            response.sendError(decodedLtiLaunch.getErrorCode(), decodedLtiLaunch.getErrorMessage());
            return null;
        }

        /* Extract relevant data */
        final LtiLaunchData ltiLaunchData = decodedLtiLaunch.getLtiLaunchData();
        final String exitUrl = ltiLaunchData.getLaunchPresentationReturnUrl();
        final String lisOutcomeServiceUrl = ltiLaunchData.getLisOutcomeServiceUrl();
        final String lisResultSourcedid = ltiLaunchData.getLisResultSourcedid();

        /* Start/reuse candidate session */
        final LtiUser ltiUser = decodedLtiLaunch.getLtiUser();
        try {
            final CandidateSession candidateSession = candidateSessionStarter.launchLinkLevelLtiCandidateSession(ltiUser,
                    exitUrl, lisOutcomeServiceUrl, lisResultSourcedid);
            return GlobalRouter.buildSessionStartRedirect(candidateSession);
        }
        catch (final CandidateException e) {
            return "candidateLaunchError";
        }
    }

    /**
     * Older URI for a link-level LTI launch.
     * <p>
     * This is kept for backwards compatibility with existing LTI links, but should not be used
     * for new links.
     */
    @RequestMapping(value="/launch/{did}", method=RequestMethod.POST)
    public String deprecatedLtiLinkLevelLaunch(final HttpServletRequest request, final HttpServletResponse response,
            @SuppressWarnings("unused") @PathVariable final long did)
            throws IOException {
        return ltiLinkLevelLaunch(request, response);
    }

    //------------------------------------------------------

    /** LTI debugging and diagnostic help */
    @RequestMapping(value="/debug", method=RequestMethod.POST)
    public String ltiDebug(final HttpServletRequest httpRequest, final Model model) throws IOException {
        final DecodedLtiLaunch decodedLtiLaunch = ltiLaunchService.decodeLtiLaunchData(httpRequest, LtiLaunchType.DOMAIN);
        model.addAttribute("object", decodedLtiLaunch);
        return "ltiDebug";
    }
}
