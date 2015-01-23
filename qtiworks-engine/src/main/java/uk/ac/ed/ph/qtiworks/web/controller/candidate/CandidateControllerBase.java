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
import uk.ac.ed.ph.qtiworks.services.IdentityService;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateException;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateRenderingService;
import uk.ac.ed.ph.qtiworks.web.ServletOutputStreamer;
import uk.ac.ed.ph.qtiworks.web.WebUtilities;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionAuthenticationFilter;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionContext;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Base for {@link CandidateItemController} and {@link CandidateTestController},
 * providing helpers and shared functionality.
 *
 * @author David McKain
 */
public abstract class CandidateControllerBase {

    @Resource
    protected IdentityService identityService;

    @Resource
    protected CandidateRenderingService candidateRenderingService;

    //----------------------------------------------------
    // Access to current session

    /**
     * Retrieves the {@link CandidateSessionContext} for this request, which will have been set up
     * by the {@link CandidateSessionAuthenticationFilter}.
     */
    protected CandidateSessionContext getCandidateSessionContext(final HttpServletRequest request) {
        return CandidateSessionAuthenticationFilter.requireCurrentRequestCandidateSessionContext(request);
    }

    protected CandidateSession getCandidateSession(final HttpServletRequest request) {
        return getCandidateSessionContext(request).getCandidateSession();
    }

    //----------------------------------------------------
    // Response helpers

    protected Map<Identifier, MultipartFile> extractFileResponseData(final MultipartHttpServletRequest multipartRequest) {
        final Map<Identifier, MultipartFile> fileResponseMap = new HashMap<Identifier, MultipartFile>();
        @SuppressWarnings("unchecked")
        final Set<String> parameterNames = multipartRequest.getParameterMap().keySet();
        for (final String name : parameterNames) {
            if (name.startsWith("qtiworks_uploadpresented_")) {
                final String responseIdentifierString = name.substring("qtiworks_uploadpresented_".length());
                final Identifier responseIdentifier;
                try {
                    responseIdentifier = Identifier.parseString(responseIdentifierString);
                }
                catch (final QtiParseException e) {
                    throw new BadResponseWebPayloadException("Bad response identifier encoded in parameter  " + name, e);
                }
                final String multipartName = "qtiworks_uploadresponse_" + responseIdentifierString;
                final MultipartFile multipartFile = multipartRequest.getFile(multipartName);
                if (multipartFile==null) {
                    throw new BadResponseWebPayloadException("Expected to find multipart file with name " + multipartName);
                }
                fileResponseMap.put(responseIdentifier, multipartFile);
            }
        }
        return fileResponseMap;
    }

    protected String extractCandidateComment(final HttpServletRequest request) {
        if (request.getParameter("qtiworks_comment_presented")==null) {
            /* No comment box given to candidate */
            return null;
        }
        return StringUtilities.emptyIfNull(request.getParameter("qtiworks_comment"));
    }

    protected Map<Identifier, StringResponseData> extractStringResponseData(final HttpServletRequest request) {
        final Map<Identifier, StringResponseData> responseMap = new HashMap<Identifier, StringResponseData>();
        @SuppressWarnings("unchecked")
        final Set<String> parameterNames = request.getParameterMap().keySet();
        for (final String name : parameterNames) {
            if (name.startsWith("qtiworks_presented_")) {
                final String responseIdentifierString = name.substring("qtiworks_presented_".length());
                final Identifier responseIdentifier;
                try {
                    responseIdentifier = Identifier.parseString(responseIdentifierString);
                }
                catch (final QtiParseException e) {
                    throw new BadResponseWebPayloadException("Bad response identifier encoded in parameter  " + name, e);
                }
                final String[] responseValues = request.getParameterValues("qtiworks_response_" + responseIdentifierString);
                final StringResponseData stringResponseData = new StringResponseData(responseValues);
                responseMap.put(responseIdentifier, stringResponseData);
            }
        }
        return responseMap;
    }

    //----------------------------------------------------
    // Assessment resource streaming

    /**
     * Serves the given (white-listed) file in the given {@link AssessmentPackage}
     */
    protected void streamAssessmentPackageFile(final HttpServletRequest request, final HttpServletResponse response,
            @PathVariable final long xid, @PathVariable final String xsrfToken,
            @RequestParam("href") final String fileHref)
            throws IOException, CandidateException {
        final String fingerprint = "session/" + xid + "/" + xsrfToken + "/file/" + fileHref;
        final String resourceEtag = WebUtilities.computeEtag(fingerprint);
        final String requestEtag = request.getHeader("If-None-Match");
        if (resourceEtag.equals(requestEtag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else {
            final ServletOutputStreamer outputStreamer = new ServletOutputStreamer(response, resourceEtag);
            candidateRenderingService.streamAssessmentPackageFile(getCandidateSession(request),
                    fileHref, outputStreamer);
        }
    }

    //----------------------------------------------------
    // Author actions

    /**
     * Serves the source of the given {@link AssessmentPackage}
     */
    protected void streamAssessmentSource(final HttpServletRequest request, final HttpServletResponse response,
            final long xid, final String xsrfToken)
            throws IOException, CandidateException {
        final String fingerprint = "session/" + xid + "/" + xsrfToken + "/source";
        final String resourceEtag = WebUtilities.computeEtag(fingerprint);
        final String requestEtag = request.getHeader("If-None-Match");
        if (resourceEtag.equals(requestEtag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else {
            final ServletOutputStreamer outputStreamer = new ServletOutputStreamer(response, resourceEtag);
            candidateRenderingService.streamAssessmentSource(getCandidateSession(request), outputStreamer);
        }
    }

    /**
     * Streams a {@link ItemSessionState} or {@link TestSessionState} representing
     * the current state of the given {@link CandidateSession}
     */
    protected void streamSessionState(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, CandidateException {
        final ServletOutputStreamer outputStreamer = new ServletOutputStreamer(response, null /* No caching */);
        candidateRenderingService.streamAssessmentState(getCandidateSession(request), outputStreamer);
    }

    /**
     * Streams an {@link AssessmentResult} representing the current state of the given
     * {@link CandidateSession}
     */
    protected void streamAssessmentResult(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, CandidateException {
        response.setContentType("application/xml");
        final ServletOutputStreamer outputStreamer = new ServletOutputStreamer(response, null /* No caching */);
        candidateRenderingService.streamAssessmentResult(getCandidateSession(request), outputStreamer);
    }

    protected String showPackageValidationResult(final HttpServletRequest request, final Model model)
            throws CandidateException {
        final CandidateSession candidateSession = getCandidateSession(request);
        model.addAttribute("validationResult", candidateRenderingService.generateValidationResult(candidateSession));
        return "validationResult";
    }

    //----------------------------------------------------
    // Redirections

    protected abstract String redirectToRenderSession(final long xid, final String xsrfToken);

    protected final String redirectToRenderSession(final CandidateSession candidateSession, final String xsrfToken) {
        return redirectToRenderSession(candidateSession.getId(), xsrfToken);
    }

    protected final String redirectToExitUrl(final CandidateSessionContext candidateSessionContext, final String xsrfToken) {
        final String sessionExitReturnUrl = candidateSessionContext.getSessionExitReturnUrl();
        if (sessionExitReturnUrl==null) {
            /* No (or unsafe) exit URL provided, so redirect to normal rendering, which will
             * show a generic "this assessment is now complete" page.
             */
            return redirectToRenderSession(candidateSessionContext.getCandidateSession(), xsrfToken);
        }
        return "redirect:" + sessionExitReturnUrl;
    }
}
