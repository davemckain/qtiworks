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
package uk.ac.ed.ph.qtiworks.web.controller.candidate;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.rendering.AbstractRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.AuthorViewRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.ItemRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateForbiddenException;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateItemDeliveryService;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateRenderingService;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateSessionTerminatedException;
import uk.ac.ed.ph.qtiworks.web.ServletOutputStreamer;
import uk.ac.ed.ph.qtiworks.web.WebUtilities;

import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Controller for candidate item sessions
 *
 * @author David McKain
 */
@Controller
public class CandidateItemController extends CandidateControllerBase {

    @Resource
    private CandidateRenderingService candidateRenderingService;

    @Resource
    private CandidateItemDeliveryService candidateItemDeliveryService;

    //----------------------------------------------------
    // Session containment and launching

    @RequestMapping(value="/itemsession/{xid}/{sessionToken}", method=RequestMethod.GET)
    public String driveSession(final Model model, @PathVariable final long xid, @PathVariable final String sessionToken) {
        model.addAttribute("sessionEntryPath", "/candidate/itemsession/" + xid + "/" + sessionToken + "/enter");
        return "launch";
    }

    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/enter", method=RequestMethod.POST)
    public String enterSession(@PathVariable final long xid, @PathVariable final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException {
        candidateItemDeliveryService.enterOrReenterCandidateSession(xid, sessionToken);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionToken);
    }

    //----------------------------------------------------
    // Rendering

    /**
     * Renders the current state of the given session
     *
     * @throws IOException
     * @throws CandidateForbiddenException
     */
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/render", method=RequestMethod.GET)
    public void renderCurrentItemSessionState(@PathVariable final long xid, @PathVariable final String sessionToken,
            final HttpServletResponse response)
            throws DomainEntityNotFoundException, IOException, CandidateForbiddenException {
        /* Create appropriate options that link back to this controller */
        final String sessionBaseUrl = "/candidate/itemsession/" + xid + "/" + sessionToken;
        final ItemRenderingOptions renderingOptions = new ItemRenderingOptions();
        configureBaseRenderingOptions(sessionBaseUrl, renderingOptions);
        renderingOptions.setEndUrl(sessionBaseUrl + "/close");
        renderingOptions.setSolutionUrl(sessionBaseUrl + "/solution");
        renderingOptions.setSoftResetUrl(sessionBaseUrl + "/reset-soft");
        renderingOptions.setHardResetUrl(sessionBaseUrl + "/reset-hard");
        renderingOptions.setExitUrl(sessionBaseUrl + "/exit");

        final ServletOutputStreamer outputStreamer = new ServletOutputStreamer(response, null /* No caching */);
        candidateRenderingService.renderCurrentCandidateItemSessionState(xid, sessionToken, renderingOptions, outputStreamer);
    }

    /**
     * Renders the authoring view of the given session
     *
     * @throws IOException
     * @throws CandidateForbiddenException
     */
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/author-view", method=RequestMethod.GET)
    public void renderCurrentItemAuthoringView(@PathVariable final long xid, @PathVariable final String sessionToken,
            final HttpServletResponse response)
            throws DomainEntityNotFoundException, IOException, CandidateForbiddenException {
        /* Create appropriate options that link back to this controller */
        final String sessionBaseUrl = "/candidate/itemsession/" + xid + "/" + sessionToken;
        final AuthorViewRenderingOptions renderingOptions = new AuthorViewRenderingOptions();
        configureBaseRenderingOptions(sessionBaseUrl, renderingOptions);

        final ServletOutputStreamer outputStreamer = new ServletOutputStreamer(response, null /* No caching */);
        candidateRenderingService.renderCurrentCandidateItemSessionStateAuthorView(xid, sessionToken, renderingOptions, outputStreamer);
    }

    private void configureBaseRenderingOptions(final String sessionBaseUrl, final AbstractRenderingOptions renderingOptions) {
        renderingOptions.setSerializationMethod(SerializationMethod.HTML5_MATHJAX);
        renderingOptions.setSourceUrl(sessionBaseUrl + "/source");
        renderingOptions.setStateUrl(sessionBaseUrl + "/state");
        renderingOptions.setResultUrl(sessionBaseUrl + "/result");
        renderingOptions.setValidationUrl(sessionBaseUrl + "/validation");
        renderingOptions.setServeFileUrl(sessionBaseUrl + "/file");
        renderingOptions.setAuthorViewUrl(sessionBaseUrl + "/author-view");
        renderingOptions.setResponseUrl(sessionBaseUrl + "/response");
    }

    //----------------------------------------------------

    @ExceptionHandler(CandidateSessionTerminatedException.class)
    public String handleTerminatedSession(final CandidateSessionTerminatedException e) {
        final CandidateSession candidateSession = e.getCandidateSession();
        return redirectToRenderSession(candidateSession);
    }

    //----------------------------------------------------
    // Attempt handling

    /**
     * Handles submission of candidate responses
     */
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/response", method=RequestMethod.POST)
    public String handleResponses(final HttpServletRequest request, @PathVariable final long xid,
            @PathVariable final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException, CandidateSessionTerminatedException {
        /* First need to extract responses */
        final Map<Identifier, StringResponseData> stringResponseMap = extractStringResponseData(request);

        /* Extract and import file responses (if appropriate) */
        Map<Identifier, MultipartFile> fileResponseMap = null;
        if (request instanceof MultipartHttpServletRequest) {
            fileResponseMap = extractFileResponseData((MultipartHttpServletRequest) request);
        }

        /* Extract comment (if appropriate) */
        final String candidateComment = extractCandidateComment(request);

        /* Call up service layer */
        candidateItemDeliveryService.handleResponses(xid, sessionToken, stringResponseMap, fileResponseMap, candidateComment);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionToken);
    }

    //----------------------------------------------------
    // Other actions

    /**
     * Resets the given {@link CandidateSession}
     */
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/reset-soft", method=RequestMethod.POST)
    public String resetSessionSoft(@PathVariable final long xid, @PathVariable final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException, CandidateSessionTerminatedException {
        candidateItemDeliveryService.resetCandidateSessionSoft(xid, sessionToken);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionToken);
    }

    /**
     * Re-initialises the given {@link CandidateSession}
     */
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/reset-hard", method=RequestMethod.POST)
    public String resetSessionHard(@PathVariable final long xid, @PathVariable final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException, CandidateSessionTerminatedException {
        candidateItemDeliveryService.resetCandidateSessionHard(xid, sessionToken);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionToken);
    }

    /**
     * Closes (but does not exit) the given {@link CandidateSession}
     */
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/close", method=RequestMethod.POST)
    public String closeSession(@PathVariable final long xid, @PathVariable final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException, CandidateSessionTerminatedException {
        candidateItemDeliveryService.endCandidateSession(xid, sessionToken);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionToken);
    }

    /**
     * Transitions the given {@link CandidateSession} to solution state
     */
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/solution", method=RequestMethod.POST)
    public String transitionSessionToSolutionState(@PathVariable final long xid, @PathVariable final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException, CandidateSessionTerminatedException {
        candidateItemDeliveryService.requestSolution(xid, sessionToken);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionToken);
    }

    /**
     * Exits the given {@link CandidateSession}
     */
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/exit", method=RequestMethod.POST)
    public String exitSession(@PathVariable final long xid, @PathVariable final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException, CandidateSessionTerminatedException {
        final CandidateSession candidateSession = candidateItemDeliveryService.exitCandidateSession(xid, sessionToken);
        return redirectToExitUrl(candidateSession);
    }

    //----------------------------------------------------
    // Assessment resource streaming

    /**
     * Serves the given (white-listed) file in the given {@link AssessmentPackage}
     */
    @Override
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/file", method=RequestMethod.GET)
    public void streamAssessmentPackageFile(@PathVariable final long xid, @PathVariable final String sessionToken,
            @RequestParam("href") final String fileHref,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, DomainEntityNotFoundException, CandidateForbiddenException, CandidateSessionTerminatedException {
        final String fingerprint = "session/" + xid + "/file/" + fileHref;
        final String resourceEtag = WebUtilities.computeEtag(fingerprint);
        final String requestEtag = request.getHeader("If-None-Match");
        if (resourceEtag.equals(requestEtag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else {
            final ServletOutputStreamer outputStreamer = new ServletOutputStreamer(response, resourceEtag);
            candidateRenderingService.streamAssessmentPackageFile(xid, sessionToken, fileHref, outputStreamer);
        }
    }

    //----------------------------------------------------
    // Author actions

    /**
     * Serves the source of the given {@link AssessmentPackage}
     */
    @Override
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/source", method=RequestMethod.GET)
    public void streamAssessmentSource(@PathVariable final long xid,
            @PathVariable final String sessionToken,
            final HttpServletRequest request, final HttpServletResponse response)
            throws DomainEntityNotFoundException, IOException, CandidateForbiddenException, CandidateSessionTerminatedException {
        super.streamAssessmentSource(xid, sessionToken, request, response);
    }

    /**
     * Streams a {@link ItemSessionState} representing the current state of the given
     * {@link CandidateSession}
     */
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/state", method=RequestMethod.GET)
    public void streamItemSessionState(@PathVariable final long xid, @PathVariable final String sessionToken, final HttpServletResponse response)
            throws DomainEntityNotFoundException, IOException, CandidateForbiddenException {
        super.streamSessionState(xid, sessionToken, response);
    }

    /**
     * Streams an {@link AssessmentResult} representing the current state of the given
     * {@link CandidateSession}
     */
    @Override
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/result", method=RequestMethod.GET)
    public void streamAssessmentResult(@PathVariable final long xid, @PathVariable final String sessionToken,
            final HttpServletResponse response)
            throws DomainEntityNotFoundException, IOException, CandidateForbiddenException {
        super.streamAssessmentResult(xid, sessionToken, response);
    }


    @Override
    @RequestMapping(value="/itemsession/{xid}/{sessionToken}/validation", method=RequestMethod.GET)
    public String showPackageValidationResult(@PathVariable final long xid, @PathVariable final String sessionToken,
            final Model model)
            throws DomainEntityNotFoundException, CandidateForbiddenException {
        return super.showPackageValidationResult(xid, sessionToken, model);
    }

    //----------------------------------------------------
    // Redirections

    @Override
    protected String redirectToRenderSession(final long xid, final String sessionToken) {
        return "redirect:/candidate/itemsession/" + xid + "/" + sessionToken + "/render";
    }

}
