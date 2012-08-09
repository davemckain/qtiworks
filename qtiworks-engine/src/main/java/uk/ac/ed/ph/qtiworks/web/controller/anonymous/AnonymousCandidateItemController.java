/* Copyright (c) 2012, University of Edinburgh.
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
package uk.ac.ed.ph.qtiworks.web.controller.anonymous;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.services.AssessmentCandidateService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.web.services.CandidateControllerService;

import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for candidate item sessions (as part of the public API)
 *
 * @author David McKain
 */
@Controller
public class AnonymousCandidateItemController {

    @Resource
    private CandidateControllerService candidateControllerService;

    @Resource
    private AssessmentCandidateService assessmentCandidateService;

    //----------------------------------------------------

    private String redirectToListing() {
        return "redirect:/web/anonymous/samples/list";
    }

    private String redirectToRenderSession(final CandidateItemSession candidateSession) {
        return redirectToRenderSession(candidateSession.getId().longValue());
    }

    private String redirectToRenderSession(final long xid) {
        return "redirect:/web/anonymous/session/" + xid;
    }

    //----------------------------------------------------
    // Session initialisation

    /**
     * Starts a new {@link CandidateItemSession} for the given {@link ItemDelivery}.
     */
    @RequestMapping(value="/delivery/{did}", method=RequestMethod.POST)
    public String startCandidateItemSession(@PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        final CandidateItemSession candidateSession = candidateControllerService.startCandidateItemSession(did);
        return redirectToRenderSession(candidateSession);
    }

    //----------------------------------------------------
    // Rendering

    /**
     * Renders the current state of the given session
     * @throws IOException
     */
    @RequestMapping(value="/session/{xid}", method=RequestMethod.GET)
    public void renderItem(@PathVariable final long xid,
            final WebRequest webRequest, final HttpServletResponse response)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        final CandidateItemSession candidateSession = assessmentCandidateService.lookupCandidateSession(xid);
        final Long did = candidateSession.getItemDelivery().getId();

        /* Create appropriate options that link back to this controller */
        final String sessionBaseUrl = "/web/anonymous/session/" + xid;
        final String deliveryBaseUrl = "/web/anonymous/delivery/" + did;
        final RenderingOptions renderingOptions = new RenderingOptions();
        renderingOptions.setContextPath(webRequest.getContextPath());
        renderingOptions.setSerializationMethod(SerializationMethod.HTML5_MATHJAX);
        renderingOptions.setAttemptUrl(sessionBaseUrl + "/attempt");
        renderingOptions.setCloseUrl(sessionBaseUrl + "/close");
        renderingOptions.setSolutionUrl(sessionBaseUrl + "/solution");
        renderingOptions.setResetUrl(sessionBaseUrl + "/reset");
        renderingOptions.setReinitUrl(sessionBaseUrl + "/reinit");
        renderingOptions.setResultUrl(sessionBaseUrl + "/result");
        renderingOptions.setTerminateUrl(sessionBaseUrl + "/terminate");
        renderingOptions.setPlaybackUrlBase(sessionBaseUrl+ "/playback");
        renderingOptions.setSourceUrl(deliveryBaseUrl + "/source");
        renderingOptions.setServeFileUrl(deliveryBaseUrl + "/file");

        candidateControllerService.renderItem(xid, response, renderingOptions);
    }

    //----------------------------------------------------
    // Attempt handling

    /**
     * Handles submission of candidate responses
     * @throws DomainEntityNotFoundException
     * @throws PrivilegeException
     * @throws CandidateSessionStateException
     * @throws RuntimeValidationException
     */
    @RequestMapping(value="/session/{xid}/attempt", method=RequestMethod.POST)
    public String handleAttempt(final HttpServletRequest request, @PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        candidateControllerService.handleAttempt(request, xid);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid);
    }

    //----------------------------------------------------
    // Other actions

    /**
     * Resets the given {@link CandidateItemSession}
     *
     * @see AssessmentCandidateService#resetCandidateSession(long)
     */
    @RequestMapping(value="/session/{xid}/reset", method=RequestMethod.POST)
    public String resetCandidateSession(@PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        candidateControllerService.resetCandidateSession(xid);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid);
    }

    /**
     * Re-initialises the given {@link CandidateItemSession}
     */
    @RequestMapping(value="/session/{xid}/reinit", method=RequestMethod.POST)
    public String reinitSession(@PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        candidateControllerService.reinitCandidateSession(xid);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid);
    }

    /**
     * Closes (but does not exit) the given {@link CandidateItemSession}
     */
    @RequestMapping(value="/session/{xid}/close", method=RequestMethod.POST)
    public String closeSession(@PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        candidateControllerService.closeCandidateSession(xid);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid);
    }

    /**
     * Transitions the given {@link CandidateItemSession} to solution state
     */
    @RequestMapping(value="/session/{xid}/solution", method=RequestMethod.POST)
    public String transitionSessionToSolutionState(@PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        candidateControllerService.transitionCandidateSessionToSolutionState(xid);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid);
    }

    /**
     * Transitions the state of the {@link CandidateItemSession} so that it plays back the
     * {@link CandidateItemEvent} having the given ID (xeid).
     */
    @RequestMapping(value="/session/{xid}/playback/{xeid}", method=RequestMethod.POST)
    public String setPlaybackEvent(@PathVariable final long xid, @PathVariable final long xeid)
            throws PrivilegeException, DomainEntityNotFoundException {
        candidateControllerService.setPlaybackState(xid, xeid);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid);
    }

    /**
     * Terminates the given {@link CandidateItemSession}
     */
    @RequestMapping(value="/session/{xid}/terminate", method=RequestMethod.POST)
    public String terminateSession(@PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateSession = candidateControllerService.terminateCandidateSession(xid);
        final DeliveryType deliveryType = candidateSession.getItemDelivery().getDeliveryType();

        /* Redirect somewhere sensible.
         * TODO: We should probably allow the redirect to be specified explicitly!
         */
        String view;
        if (deliveryType==DeliveryType.SYSTEM_DEMO) {
            /* (Trying a sample) */
            view = redirectToListing();
        }
        else {
            /* (This was a "upload & run", so go somewhere sensible) */
            view = "redirect:/public/";
        }
        return view;
    }

    //----------------------------------------------------
    // Informational actions

    /**
     * Streams an {@link ItemResult} representing the current state of the given
     * {@link CandidateItemSession}
     */
    @RequestMapping(value="/session/{xid}/result", method=RequestMethod.GET)
    public void streamResult(final HttpServletResponse response, @PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        candidateControllerService.streamResult(response, xid);
    }

    /**
     * Serves the source of the given {@link AssessmentPackage}
     *
     * @see AssessmentManagementService#streamPackageSource(AssessmentPackage, java.io.OutputStream)
     */
    @RequestMapping(value="/delivery/{did}/source", method=RequestMethod.GET)
    public void streamPackageSource(@PathVariable final long did,
            final HttpServletRequest request, final HttpServletResponse response)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        candidateControllerService.streamPackageSource(did, request, response);
    }

    /**
     * Serves the given (white-listed) file in the given {@link AssessmentPackage}
     *
     * @see AssessmentManagementService#streamPackageSource(AssessmentPackage, java.io.OutputStream)
     */
    @RequestMapping(value="/delivery/{did}/file", method=RequestMethod.GET)
    public void streamPackageFile(@PathVariable final long did,
            @RequestParam("href") final String href,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, PrivilegeException, DomainEntityNotFoundException {
        candidateControllerService.streamPackageFile(did, href, request, response);
    }
}
