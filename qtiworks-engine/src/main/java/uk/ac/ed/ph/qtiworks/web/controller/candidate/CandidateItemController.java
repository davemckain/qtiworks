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
package uk.ac.ed.ph.qtiworks.web.controller.candidate;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.services.AssessmentCandidateService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.domain.CandidatePrivilegeException;
import uk.ac.ed.ph.qtiworks.web.CacheableWebOutputStreamer;
import uk.ac.ed.ph.qtiworks.web.NonCacheableWebOutputStreamer;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Controller for candidate item sessions
 *
 * @author David McKain
 */
@Controller
public class CandidateItemController {

    /** Default age for any cacheable resources */
    public static final long CACHEABLE_MAX_AGE = 60 * 60;

    @Resource
    private AssessmentCandidateService assessmentCandidateService;

    //----------------------------------------------------
    // Rendering

    /**
     * Renders the current state of the given session
     *
     * @throws IOException
     * @throws CandidatePrivilegeException
     */
    @RequestMapping(value="/session/{xid}/{sessionHash}", method=RequestMethod.GET)
    public void renderItem(@PathVariable final long xid, @PathVariable final String sessionHash,
            final WebRequest webRequest, final HttpServletResponse response)
            throws DomainEntityNotFoundException, IOException, CandidatePrivilegeException {
        /* Create appropriate options that link back to this controller */
        final String sessionBaseUrl = "/candidate/session/" + xid + "/" + sessionHash;
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
        renderingOptions.setSourceUrl(sessionBaseUrl + "/source");
        renderingOptions.setServeFileUrl(sessionBaseUrl + "/file");

        final NonCacheableWebOutputStreamer outputStreamer = new NonCacheableWebOutputStreamer(response);
        assessmentCandidateService.renderCurrentState(xid, sessionHash, renderingOptions, outputStreamer);
    }


    //----------------------------------------------------
    // Attempt handling

    /**
     * Handles submission of candidate responses
     */
    @RequestMapping(value="/session/{xid}/{sessionHash}/attempt", method=RequestMethod.POST)
    public String handleAttempt(final HttpServletRequest request, @PathVariable final long xid,
            @PathVariable final String sessionHash)
            throws DomainEntityNotFoundException, RuntimeValidationException, CandidatePrivilegeException {
        /* First need to extract responses */
        final Map<Identifier, StringResponseData> stringResponseMap = extractStringResponseData(request);

        /* Extract and import file responses (if appropriate) */
        Map<Identifier, MultipartFile> fileResponseMap = null;
        if (request instanceof MultipartHttpServletRequest) {
            fileResponseMap = extractFileResponseData((MultipartHttpServletRequest) request);
        }

        /* Call up service layer */
        assessmentCandidateService.handleAttempt(xid, sessionHash, stringResponseMap, fileResponseMap);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionHash);
    }

    /**
     * @throws BadResponseWebPayloadException
     */
    private Map<Identifier, MultipartFile> extractFileResponseData(final MultipartHttpServletRequest multipartRequest) {
        final Map<Identifier, MultipartFile> fileResponseMap = new HashMap<Identifier, MultipartFile>();
        @SuppressWarnings("unchecked")
        final Set<String> parameterNames = multipartRequest.getParameterMap().keySet();
        for (final String name : parameterNames) {
            if (name.startsWith("qtiworks_uploadpresented_")) {
                final String responseIdentifierString = name.substring("qtiworks_uploadpresented_".length());
                final Identifier responseIdentifier;
                try {
                    responseIdentifier = new Identifier(responseIdentifierString);
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

    /**
     * @throws BadResponseWebPayloadException
     */
    private Map<Identifier, StringResponseData> extractStringResponseData(final HttpServletRequest request) {
        final Map<Identifier, StringResponseData> responseMap = new HashMap<Identifier, StringResponseData>();
        @SuppressWarnings("unchecked")
        final Set<String> parameterNames = request.getParameterMap().keySet();
        for (final String name : parameterNames) {
            if (name.startsWith("qtiworks_presented_")) {
                final String responseIdentifierString = name.substring("qtiworks_presented_".length());
                final Identifier responseIdentifier;
                try {
                    responseIdentifier = new Identifier(responseIdentifierString);
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
    // Other actions

    /**
     * Resets the given {@link CandidateItemSession}
     *
     * @see AssessmentCandidateService#resetCandidateSession(long)
     */
    @RequestMapping(value="/session/{xid}/{sessionHash}/reset", method=RequestMethod.POST)
    public String resetCandidateSession(@PathVariable final long xid, @PathVariable final String sessionHash)
            throws DomainEntityNotFoundException, CandidatePrivilegeException {
        assessmentCandidateService.resetCandidateSession(xid, sessionHash);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionHash);
    }

    /**
     * Re-initialises the given {@link CandidateItemSession}
     */
    @RequestMapping(value="/session/{xid}/{sessionHash}/reinit", method=RequestMethod.POST)
    public String reinitSession(@PathVariable final long xid, @PathVariable final String sessionHash)
            throws DomainEntityNotFoundException, RuntimeValidationException, CandidatePrivilegeException {
        assessmentCandidateService.reinitCandidateSession(xid, sessionHash);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionHash);
    }

    /**
     * Closes (but does not exit) the given {@link CandidateItemSession}
     */
    @RequestMapping(value="/session/{xid}/{sessionHash}/close", method=RequestMethod.POST)
    public String closeSession(@PathVariable final long xid, @PathVariable final String sessionHash)
            throws DomainEntityNotFoundException, CandidatePrivilegeException {
        assessmentCandidateService.closeCandidateSession(xid, sessionHash);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionHash);
    }

    /**
     * Transitions the given {@link CandidateItemSession} to solution state
     */
    @RequestMapping(value="/session/{xid}/{sessionHash}/solution", method=RequestMethod.POST)
    public String transitionSessionToSolutionState(@PathVariable final long xid, @PathVariable final String sessionHash)
            throws DomainEntityNotFoundException, CandidatePrivilegeException {
        assessmentCandidateService.transitionCandidateSessionToSolutionState(xid, sessionHash);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionHash);
    }

    /**
     * Transitions the state of the {@link CandidateItemSession} so that it plays back the
     * {@link CandidateItemEvent} having the given ID (xeid).
     */
    @RequestMapping(value="/session/{xid}/{sessionHash}/playback/{xeid}", method=RequestMethod.POST)
    public String setPlaybackEvent(@PathVariable final long xid, @PathVariable final String sessionHash, @PathVariable final long xeid)
            throws DomainEntityNotFoundException, CandidatePrivilegeException {
        assessmentCandidateService.setPlaybackState(xid, sessionHash, xeid);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionHash);
    }

    /**
     * Terminates the given {@link CandidateItemSession}
     */
    @RequestMapping(value="/session/{xid}/{sessionHash}/terminate", method=RequestMethod.POST)
    public String terminateSession(@PathVariable final long xid, @PathVariable final String sessionHash)
            throws DomainEntityNotFoundException, CandidatePrivilegeException {
        final CandidateItemSession candidateSession = assessmentCandidateService.terminateCandidateSession(xid, sessionHash);
        return redirectToExitUrl(candidateSession.getExitUrl());
    }

    //----------------------------------------------------
    // Informational actions

    /**
     * Streams an {@link ItemResult} representing the current state of the given
     * {@link CandidateItemSession}
     */
    @RequestMapping(value="/session/{xid}/{sessionHash}/result", method=RequestMethod.GET)
    public void streamResult(final HttpServletResponse response, @PathVariable final long xid, @PathVariable final String sessionHash)
            throws DomainEntityNotFoundException, IOException, CandidatePrivilegeException {
        response.setContentType("application/xml");
        assessmentCandidateService.streamItemResult(xid, sessionHash, response.getOutputStream());
    }

    /**
     * Serves the source of the given {@link AssessmentPackage}
     *
     * @see AssessmentManagementService#streamPackageSource(AssessmentPackage, java.io.OutputStream)
     */
    @RequestMapping(value="/session/{xid}/{sessionHash}/source", method=RequestMethod.GET)
    public void streamPackageSource(@PathVariable final long xid,
            @PathVariable final String sessionHash,
            final HttpServletRequest request, final HttpServletResponse response)
            throws DomainEntityNotFoundException, IOException, CandidatePrivilegeException {
        final String resourceEtag = ServiceUtilities.computeSha1Digest(request.getRequestURI());
        final String requestEtag = request.getHeader("If-None-Match");
        if (resourceEtag.equals(requestEtag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else {
            final CacheableWebOutputStreamer outputStreamer = new CacheableWebOutputStreamer(response, resourceEtag, CACHEABLE_MAX_AGE);
            assessmentCandidateService.streamAssessmentSource(xid, sessionHash, outputStreamer);
        }
    }

    /**
     * Serves the given (white-listed) file in the given {@link AssessmentPackage}
     * @throws CandidatePrivilegeException
     *
     * @see AssessmentManagementService#streamPackageSource(AssessmentPackage, java.io.OutputStream)
     */
    @RequestMapping(value="/session/{xid}/{sessionHash}/file", method=RequestMethod.GET)
    public void streamPackageFile(@PathVariable final long xid, @PathVariable final String sessionHash,
            @RequestParam("href") final String href,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, DomainEntityNotFoundException, CandidatePrivilegeException {
        final CandidateItemSession candidateSession = assessmentCandidateService.lookupCandidateSession(xid, sessionHash);
        final String resourceUniqueTag = request.getRequestURI() + "/" + href;
        final String resourceEtag = ServiceUtilities.computeSha1Digest(resourceUniqueTag);
        final String requestEtag = request.getHeader("If-None-Match");
        if (resourceEtag.equals(requestEtag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else {
            final CacheableWebOutputStreamer outputStreamer = new CacheableWebOutputStreamer(response, resourceEtag, CACHEABLE_MAX_AGE);
            assessmentCandidateService.streamAssessmentFile(candidateSession, href, outputStreamer);
        }
    }

    //----------------------------------------------------
    // Redirections

    private String redirectToRenderSession(final long xid, final String sessionHash) {
        return "redirect:/candidate/session/" + xid + "/" + sessionHash;
    }

    private String redirectToExitUrl(final String exitUrl) {
        return "redirect:" + exitUrl;
    }
}
