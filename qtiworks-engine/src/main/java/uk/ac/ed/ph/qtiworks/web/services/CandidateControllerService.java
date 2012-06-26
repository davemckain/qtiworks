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
package uk.ac.ed.ph.qtiworks.web.services;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.services.AssessmentCandidateService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.web.CacheableWebOutputStreamer;
import uk.ac.ed.ph.qtiworks.web.NonCacheableWebOutputStreamer;
import uk.ac.ed.ph.qtiworks.web.pub.controller.BadResponseWebPayloadException;

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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * This service acts as a base for the candidate controllers.
 *
 * @author David McKain
 */
@Service
public class CandidateControllerService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateControllerService.class);

    /** Default age for any cacheable resources */
    public static final long CACHEABLE_MAX_AGE = 60 * 60;

    @Resource
    private AssessmentCandidateService assessmentCandidateService;

    //----------------------------------------------------
    // Session initialisation

    /**
     * Starts a new {@link CandidateItemSession} for the given {@link ItemDelivery}.
     */
    public CandidateItemSession startCandidateItemSession(final long did)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        logger.debug("Creating new CandidateItemSession for delivery {}", did);
        return assessmentCandidateService.createCandidateSession(did);
    }

    //----------------------------------------------------
    // Rendering

    /**
     * Renders the current state of the given session
     * @throws IOException
     */
    public void renderItem(final long xid, final HttpServletResponse response,
            final RenderingOptions renderingOptions)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        logger.debug("Rendering current state for session {}", xid);

        final NonCacheableWebOutputStreamer outputStreamer = new NonCacheableWebOutputStreamer(response);
        assessmentCandidateService.renderCurrentState(xid, renderingOptions, outputStreamer);
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
    public CandidateItemSession handleAttempt(final HttpServletRequest request, final long xid)
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
        return candidateSession;
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
    public CandidateItemSession resetCandidateSession(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        logger.debug("Requesting reset of session #{}", xid);
        return assessmentCandidateService.resetCandidateSession(xid);
    }

    /**
     * Re-initialises the given {@link CandidateItemSession}
     */
    public CandidateItemSession reinitCandidateSession(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        logger.debug("Requesting reinit of session #{}", xid);
        return assessmentCandidateService.reinitCandidateSession(xid);
    }

    /**
     * Closes (but does not exit) the given {@link CandidateItemSession}
     */
    public CandidateItemSession closeCandidateSession(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        logger.debug("Requesting close of session #{}", xid);
        return assessmentCandidateService.closeCandidateSession(xid);
    }

    /**
     * Transitions the given {@link CandidateItemSession} to solution state
     */
    public CandidateItemSession transitionCandidateSessionToSolutionState(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        logger.debug("Requesting transition of session #{} to solution state", xid);
        return assessmentCandidateService.transitionCandidateSessionToSolutionState(xid);
    }

    /**
     * Transitions the state of the {@link CandidateItemSession} so that it plays back the
     * {@link CandidateItemEvent} having the given ID (xeid).
     */
    public CandidateItemSession setPlaybackState(final long xid, final long xeid)
            throws PrivilegeException, DomainEntityNotFoundException {
        logger.debug("Requesting to set playback position of session #{} to event #{}", xid, xeid);
        return assessmentCandidateService.setPlaybackState(xid, xeid);
    }

    /**
     * Terminates the given {@link CandidateItemSession}
     * @return
     */
    public CandidateItemSession terminateCandidateSession(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        logger.debug("Requesting termination of session #{}", xid);
        return assessmentCandidateService.terminateCandidateSession(xid);
    }

    //----------------------------------------------------
    // Informational actions

    /**
     * Streams an {@link ItemResult} representing the current state of the given
     * {@link CandidateItemSession}
     */
    public void streamResult(final HttpServletResponse response, final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        logger.debug("Streaming result for session #{}", xid);

        response.setContentType("application/xml");
        assessmentCandidateService.streamItemResult(xid, response.getOutputStream());
    }

    /**
     * Serves the source of the given {@link AssessmentPackage}
     *
     * @see AssessmentManagementService#streamPackageSource(AssessmentPackage, java.io.OutputStream)
     */
    public void streamPackageSource(final long did, final HttpServletRequest request,
            final HttpServletResponse response)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        logger.debug("Requested source for delivery #{}", did);

        final String resourceEtag = ServiceUtilities.computeSha1Digest(request.getRequestURI());
        final String requestEtag = request.getHeader("If-None-Match");
        if (resourceEtag.equals(requestEtag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else {
            final CacheableWebOutputStreamer outputStreamer = new CacheableWebOutputStreamer(response, resourceEtag, CACHEABLE_MAX_AGE);
            assessmentCandidateService.streamAssessmentSource(did, outputStreamer);
        }
    }

    /**
     * Serves the given (white-listed) file in the given {@link AssessmentPackage}
     *
     * @see AssessmentManagementService#streamPackageSource(AssessmentPackage, java.io.OutputStream)
     */
    public void streamPackageFile(final long did, final String href,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, PrivilegeException, DomainEntityNotFoundException {
        final String resourceUniqueTag = request.getRequestURI() + "/" + href;
        final String resourceEtag = ServiceUtilities.computeSha1Digest(resourceUniqueTag);
        final String requestEtag = request.getHeader("If-None-Match");
        if (resourceEtag.equals(requestEtag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else {
            final CacheableWebOutputStreamer outputStreamer = new CacheableWebOutputStreamer(response, resourceEtag, CACHEABLE_MAX_AGE);
            assessmentCandidateService.streamAssessmentFile(did, href, outputStreamer);
        }
    }
}
