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

import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.rendering.AbstractRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.AuthorViewRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.ItemRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateException;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateItemDeliveryService;
import uk.ac.ed.ph.qtiworks.web.ServletOutputStreamer;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionContext;

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
    private CandidateItemDeliveryService candidateItemDeliveryService;

    //----------------------------------------------------
    // Session containment and launching

    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}", method=RequestMethod.GET)
    public String driveSession(final Model model, @PathVariable final long xid, @PathVariable final String xsrfToken) {
        model.addAttribute("sessionEntryPath", "/candidate/itemsession/" + xid + "/" + xsrfToken + "/enter");
        return "launch";
    }

    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/enter", method=RequestMethod.POST)
    public String enterSession(final HttpServletRequest request,
            @PathVariable final long xid, @PathVariable final String xsrfToken)
            throws CandidateException {
        candidateItemDeliveryService.enterOrReenterCandidateSession(getCandidateSession(request));

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, xsrfToken);
    }

    //----------------------------------------------------
    // Rendering

    /**
     * Renders the current state of the given session
     */
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/render", method=RequestMethod.GET)
    public void renderCurrentItemSessionState(final HttpServletRequest request,
            @PathVariable final long xid, @PathVariable final String xsrfToken,
            final HttpServletResponse response)
            throws IOException, CandidateException {
        final CandidateSessionContext candidateSessionContext = getCandidateSessionContext(request);

        /* Create appropriate options that link back to this controller */
        final String sessionBaseUrl = "/candidate/itemsession/" + xid + "/" + xsrfToken;
        final ItemRenderingOptions renderingOptions = new ItemRenderingOptions();
        configureBaseRenderingOptions(sessionBaseUrl, candidateSessionContext, renderingOptions);
        renderingOptions.setEndUrl(sessionBaseUrl + "/close");
        renderingOptions.setSolutionUrl(sessionBaseUrl + "/solution");
        renderingOptions.setSoftResetUrl(sessionBaseUrl + "/reset-soft");
        renderingOptions.setHardResetUrl(sessionBaseUrl + "/reset-hard");
        renderingOptions.setExitUrl(sessionBaseUrl + "/exit");

        final ServletOutputStreamer outputStreamer = new ServletOutputStreamer(response, null /* No caching */);
        candidateRenderingService.renderCurrentCandidateItemSessionState(candidateSessionContext.getCandidateSession(),
                renderingOptions, outputStreamer);
    }

    /**
     * Renders the authoring view of the given session
     */
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/author-view", method=RequestMethod.GET)
    public void renderCurrentItemAuthoringView(final HttpServletRequest request,
            @PathVariable final long xid, @PathVariable final String xsrfToken,
            final HttpServletResponse response)
            throws IOException, CandidateException {
        final CandidateSessionContext candidateSessionContext = getCandidateSessionContext(request);

        /* Create appropriate options that link back to this controller */
        final String sessionBaseUrl = "/candidate/itemsession/" + xid + "/" + xsrfToken;
        final AuthorViewRenderingOptions renderingOptions = new AuthorViewRenderingOptions();
        configureBaseRenderingOptions(sessionBaseUrl, candidateSessionContext, renderingOptions);

        final ServletOutputStreamer outputStreamer = new ServletOutputStreamer(response, null /* No caching */);
        candidateRenderingService.renderCurrentCandidateItemSessionStateAuthorView(candidateSessionContext.getCandidateSession(),
                renderingOptions, outputStreamer);
    }

    private void configureBaseRenderingOptions(final String sessionBaseUrl,
            final CandidateSessionContext candidateSessionContext,
            final AbstractRenderingOptions renderingOptions) {
        renderingOptions.setSessionExitReturnUrl(candidateSessionContext.getSessionExitReturnUrl());
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
    // Attempt handling

    /**
     * Handles submission of candidate responses
     */
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/response", method=RequestMethod.POST)
    public String handleResponses(final HttpServletRequest request, @PathVariable final long xid,
            @PathVariable final String xsrfToken)
            throws CandidateException {
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
        candidateItemDeliveryService.handleResponses(getCandidateSession(request), stringResponseMap,
                fileResponseMap, candidateComment);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, xsrfToken);
    }

    //----------------------------------------------------
    // Other actions

    /**
     * Resets the given {@link CandidateSession}
     */
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/reset-soft", method=RequestMethod.POST)
    public String resetSessionSoft(final HttpServletRequest request,
            @PathVariable final long xid, @PathVariable final String xsrfToken)
            throws CandidateException {
        candidateItemDeliveryService.resetCandidateSessionSoft(getCandidateSession(request));

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, xsrfToken);
    }

    /**
     * Re-initialises the given {@link CandidateSession}
     */
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/reset-hard", method=RequestMethod.POST)
    public String resetSessionHard(final HttpServletRequest request,
            @PathVariable final long xid, @PathVariable final String xsrfToken)
            throws CandidateException {
        candidateItemDeliveryService.resetCandidateSessionHard(getCandidateSession(request));

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, xsrfToken);
    }

    /**
     * Closes (but does not exit) the given {@link CandidateSession}
     */
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/close", method=RequestMethod.POST)
    public String closeSession(final HttpServletRequest request,
            @PathVariable final long xid, @PathVariable final String xsrfToken)
            throws CandidateException {
        candidateItemDeliveryService.endCandidateSession(getCandidateSession(request));

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, xsrfToken);
    }

    /**
     * Transitions the given {@link CandidateSession} to solution state
     */
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/solution", method=RequestMethod.POST)
    public String transitionSessionToSolutionState(final HttpServletRequest request,
            @PathVariable final long xid, @PathVariable final String xsrfToken)
            throws CandidateException {
        candidateItemDeliveryService.requestSolution(getCandidateSession(request));

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, xsrfToken);
    }

    /**
     * Exits the given {@link CandidateSession}
     */
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/exit", method=RequestMethod.POST)
    public String exitSession(final HttpServletRequest request,
            @SuppressWarnings("unused") @PathVariable final long xid, @PathVariable final String xsrfToken)
            throws CandidateException {
        final CandidateSessionContext candidateSessionContext = getCandidateSessionContext(request);
        candidateItemDeliveryService.exitCandidateSession(candidateSessionContext.getCandidateSession());
        return redirectToExitUrl(candidateSessionContext, xsrfToken);
    }

    //----------------------------------------------------
    // Assessment resource streaming

    /**
     * Serves the given (white-listed) file in the given {@link AssessmentPackage}
     */
    @Override
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/file", method=RequestMethod.GET)
    public void streamAssessmentPackageFile(final HttpServletRequest request, final HttpServletResponse response,
            @PathVariable final long xid, @PathVariable final String xsrfToken,
            @RequestParam("href") final String fileHref)
            throws IOException, CandidateException {
         super.streamAssessmentPackageFile(request, response, xid, xsrfToken, fileHref);
    }

    //----------------------------------------------------
    // Author actions

    /**
     * Serves the source of the given {@link AssessmentPackage}
     */
    @Override
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/source", method=RequestMethod.GET)
    public void streamAssessmentSource(final HttpServletRequest request, final HttpServletResponse response,
            @PathVariable final long xid, @PathVariable final String xsrfToken)
            throws IOException, CandidateException {
        super.streamAssessmentSource(request, response, xid, xsrfToken);
    }

    /**
     * Streams a {@link ItemSessionState} representing the current state of the given
     * {@link CandidateSession}
     */
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/state", method=RequestMethod.GET)
    public void streamItemSessionState(final HttpServletRequest request, final HttpServletResponse response,
            @SuppressWarnings("unused") @PathVariable final long xid,
            @SuppressWarnings("unused") @PathVariable final String xsrfToken)
            throws IOException, CandidateException {
        super.streamSessionState(request, response);
    }

    /**
     * Streams an {@link AssessmentResult} representing the current state of the given
     * {@link CandidateSession}
     */
    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/result", method=RequestMethod.GET)
    public void streamAssessmentResult(final HttpServletRequest request, final HttpServletResponse response,
            @SuppressWarnings("unused") @PathVariable final long xid,
            @SuppressWarnings("unused") @PathVariable final String xsrfToken)
            throws IOException, CandidateException {
        super.streamAssessmentResult(request, response);
    }

    @RequestMapping(value="/itemsession/{xid}/{xsrfToken}/validation", method=RequestMethod.GET)
    public String showPackageValidationResult(final HttpServletRequest request,
            @SuppressWarnings("unused") @PathVariable final long xid,
            @SuppressWarnings("unused") @PathVariable final String xsrfToken, final Model model)
            throws CandidateException {
        return super.showPackageValidationResult(request, model);
    }

    //----------------------------------------------------
    // Redirections

    @Override
    protected String redirectToRenderSession(final long xid, final String xsrfToken) {
        return "redirect:/candidate/itemsession/" + xid + "/" + xsrfToken + "/render";
    }

}
