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
package uk.ac.ed.ph.qtiworks.web.pub.controller;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.services.AssessmentCandidateService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.web.CacheableServletOutputStreamer;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Controller for candidate item sessions
 *
 * FIXME: Need to implement 'end'
 *
 * @author David McKain
 */
@Controller
public class CandidateItemController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateItemController.class);

    /** Default age for any cacheable resources */
    public static final long CACHE_AGE = 60 * 60;

    @Resource
    private AssessmentCandidateService assessmentCandidateService;

    //----------------------------------------------------
    // Session initialisation

    /**
     * Starts a new {@link CandidateItemSession} for the given {@link ItemDelivery}.
     */
    @RequestMapping(value="/delivery/{did}", method=RequestMethod.POST)
    public String startCandidateItemSession(@PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        logger.debug("Creating new CandidateItemSession for delivery {}", did);
        final CandidateItemSession candidateSession = assessmentCandidateService.createCandidateSession(did);
        return redirectToSession(candidateSession);
    }

    private String redirectToSession(final CandidateItemSession candidateSession) {
        return "redirect:/web/public/session/" + candidateSession.getId();
    }

    //----------------------------------------------------
    // Rendering

    /**
     * Renders the current state of the given session
     */
    @RequestMapping(value="/session/{xid}", method=RequestMethod.GET)
    @ResponseBody
    public String renderItem(final WebRequest webRequest, @PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        logger.debug("Rendering current state for session {}", xid);
        final CandidateItemSession candidateSession = assessmentCandidateService.lookupCandidateSession(xid);
        final Long did = candidateSession.getItemDelivery().getId();

        /* Create appropriate options that link back to this controller */
        final String sessionBaseUrl = "/web/public/session/" + xid;
        final String deliveryBaseUrl = "/web/public/delivery/" + did;
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

        return assessmentCandidateService.renderCurrentState(xid, renderingOptions);
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
        logger.debug("Handling attempt against session {}", xid);
        final CandidateItemSession candidateSession = assessmentCandidateService.lookupCandidateSession(xid);

        /* First need to extract responses */
        final Map<Identifier, StringResponseData> stringResponseMap = extractStringResponseData(request);
        logger.debug("Extract string responses {}", stringResponseMap);

        /* Extract and import file responses (if appropriate) */
        Map<Identifier, MultipartFile> fileResponseMap = null;
        if (request instanceof MultipartHttpServletRequest) {
            fileResponseMap = extractFileResponseData((MultipartHttpServletRequest) request);
            logger.debug("Extracted file responses {}", fileResponseMap);
        }

        /* Call up service layer */
        assessmentCandidateService.handleAttempt(candidateSession, stringResponseMap, fileResponseMap);

        /* Redirect to rendering of current session state */
        return redirectToSession(candidateSession);
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
     *
     * @param response
     * @param did
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     * @throws RuntimeValidationException
     */
    @RequestMapping(value="/session/{xid}/reset", method=RequestMethod.POST)
    public String resetSession(@PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        logger.debug("Requesting reset of session #{}", xid);
        final CandidateItemSession candidateSession = assessmentCandidateService.resetCandidateSession(xid);

        /* Redirect to rendering of current session state */
        return redirectToSession(candidateSession);
    }

    /**
     * Re-initialises the given {@link CandidateItemSession}
     *
     * @see AssessmentCandidateService#reinitCandidateSession(long)
     *
     * @param response
     * @param did
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     * @throws RuntimeValidationException
     */
    @RequestMapping(value="/session/{xid}/reinit", method=RequestMethod.POST)
    public String reinitSession(@PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        logger.debug("Requesting reinit of session #{}", xid);
        final CandidateItemSession candidateSession = assessmentCandidateService.reinitCandidateSession(xid);

        /* Redirect to rendering of current session state */
        return redirectToSession(candidateSession);
    }

    /**
     * Closes (but does not exit) the given {@link CandidateItemSession}
     *
     * @param response
     * @param did
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     * @throws RuntimeValidationException
     */
    @RequestMapping(value="/session/{xid}/close", method=RequestMethod.POST)
    public String closeSession(@PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        logger.debug("Requesting close of session #{}", xid);
        final CandidateItemSession candidateSession = assessmentCandidateService.closeCandidateSession(xid);

        /* Redirect to rendering of current session state */
        return redirectToSession(candidateSession);
    }

    /**
     * Transitions the given {@link CandidateItemSession} to solution state
     *
     * @param response
     * @param did
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     * @throws RuntimeValidationException
     */
    @RequestMapping(value="/session/{xid}/solution", method=RequestMethod.POST)
    public String transitionSessionToSolutionState(@PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        logger.debug("Requesting transition of session #{} to solution state", xid);
        final CandidateItemSession candidateSession = assessmentCandidateService.transitionCandidateSessionToSolutionState(xid);

        /* Redirect to rendering of current session state */
        return redirectToSession(candidateSession);
    }

    /**
     * Transitions the state of the {@link CandidateItemSession} so that it plays back the
     * {@link CandidateItemEvent} having the given ID (xeid).
     *
     * @param response
     * @param did
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     * @throws RuntimeValidationException
     */
    @RequestMapping(value="/session/{xid}/playback/{xeid}", method=RequestMethod.POST)
    public String setPlaybackEvent(@PathVariable final long xid, @PathVariable final long xeid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        logger.debug("Requesting to set playback position of session #{} to event #{}", xid, xeid);
        final CandidateItemSession candidateSession = assessmentCandidateService.setPlaybackState(xid, xeid);

        /* Redirect to rendering of current session state */
        return redirectToSession(candidateSession);
    }

    /**
     * Terminates the given {@link CandidateItemSession}
     *
     * @param response
     * @param did
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     * @throws RuntimeValidationException
     */
    @RequestMapping(value="/session/{xid}/terminate", method=RequestMethod.POST)
    public String terminateSession(@PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        logger.debug("Requesting termination of session #{}", xid);
        assessmentCandidateService.terminateCandidateSession(xid);

        /* TODO: Need to redirect somewhere useful! */
        return "redirect:/";
    }

    /**
     * Streams an {@link ItemResult} representing the current state of the given
     * {@link CandidateItemSession}
     *
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     * @throws CandidateSessionStateException
     * @throws IOException
     */
    @RequestMapping(value="/session/{xid}/result", method=RequestMethod.GET)
    public void streamResult(final HttpServletResponse response, @PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        logger.debug("Streaming result for session #{}", xid);

        response.setContentType("application/xml");
        assessmentCandidateService.streamItemResult(xid, response.getOutputStream());
    }

    /**
     * Serves the source of the given {@link AssessmentPackage}
     *
     * @see AssessmentManagementService#streamPackageSource(AssessmentPackage, java.io.OutputStream)
     *
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     * @throws IOException
     */
    @RequestMapping(value="/delivery/{did}/source", method=RequestMethod.GET)
    public void streamPackageSource(@PathVariable final long did,
            final HttpServletRequest request, final HttpServletResponse response)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        logger.debug("Request source for delivery #{}", did);

        final String resourceEtag = ServiceUtilities.computeSha1Digest(request.getRequestURI());
        final String requestEtag = request.getHeader("If-None-Match");
        if (resourceEtag.equals(requestEtag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else {
            final CacheableServletOutputStreamer outputStreamer = new CacheableServletOutputStreamer(response, resourceEtag, CACHE_AGE);
            assessmentCandidateService.streamAssessmentSource(did, outputStreamer);
        }
    }

    /**
     * Serves the given (white-listed) file in the given {@link AssessmentPackage}
     *
     * @see AssessmentManagementService#streamPackageSource(AssessmentPackage, java.io.OutputStream)
     *
     * @throws IOException
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     */
    @RequestMapping(value="/delivery/{did}/file", method=RequestMethod.GET)
    public void streamPackageFile(@PathVariable final long did,
            @RequestParam("href") final String href,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, PrivilegeException, DomainEntityNotFoundException {
        final String resourceEtag = ServiceUtilities.computeSha1Digest(request.getRequestURI());
        final String requestEtag = request.getHeader("If-None-Match");
        if (resourceEtag.equals(requestEtag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else {
            final CacheableServletOutputStreamer outputStreamer = new CacheableServletOutputStreamer(response, resourceEtag, CACHE_AGE);
            assessmentCandidateService.streamAssessmentResource(did, href, outputStreamer);
        }
    }
}
