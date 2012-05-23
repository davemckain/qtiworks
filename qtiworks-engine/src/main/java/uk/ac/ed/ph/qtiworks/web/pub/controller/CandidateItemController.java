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
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateFileSubmission;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.services.AssessmentCandidateService;
import uk.ac.ed.ph.qtiworks.services.domain.CandidateSessionStateException;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Controller for candidate item sessions
 *
 * @author David McKain
 */
@Controller
public class CandidateItemController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateItemController.class);

    @Resource
    private AssessmentCandidateService assessmentCandidateService;

    /**
     * Starts a new {@link CandidateItemSession} for the given {@link ItemDelivery}.
     */
    @RequestMapping(value="/delivery/{did}", method=RequestMethod.POST)
    public String startCandidateItemSession(@PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        logger.debug("Creating new CandidateItemSession for delivery {}", did);
        final ItemDelivery itemDelivery = assessmentCandidateService.lookupItemDelivery(did);
        final CandidateItemSession candidateSession = assessmentCandidateService.createCandidateSession(itemDelivery);
        assessmentCandidateService.initialiseSession(candidateSession);

        return redirectToSession(candidateSession);
    }

    private String redirectToSession(final CandidateItemSession candidateSession) {
        return "redirect:/web/public/session/" + candidateSession.getId();
    }

    /**
     * Renders the current state of the given session
     */
    @RequestMapping(value="/session/{xid}", method=RequestMethod.GET)
    @ResponseBody
    public String renderItem(@PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException, CandidateSessionStateException {
        logger.debug("Rendering current state for session {}", xid);
        final CandidateItemSession candidateSession = assessmentCandidateService.lookupCandidateSession(xid);
        return assessmentCandidateService.renderCurrentState(candidateSession);
    }

    /**
     * Handles submission of candidate responses
     * @throws DomainEntityNotFoundException
     * @throws PrivilegeException
     * @throws CandidateSessionStateException
     * @throws RuntimeValidationException
     */
    @RequestMapping(value="/session/{xid}", method=RequestMethod.POST)
    public String handleAttempt(final HttpServletRequest request, @PathVariable final long xid)
            throws PrivilegeException, DomainEntityNotFoundException,
            RuntimeValidationException, CandidateSessionStateException {
        logger.debug("Handling attempt against session {}", xid);
        final CandidateItemSession candidateSession = assessmentCandidateService.lookupCandidateSession(xid);

        /* First need to extract responses */
        final Map<Identifier, StringResponseData> stringResponseMap = extractStringResponseData(request);
        logger.debug("Extract string responses {}", stringResponseMap);

        /* Extract file responses (if appropriate) */
        Map<Identifier, CandidateFileSubmission> fileResponseMap = null;
        if (request instanceof MultipartHttpServletRequest) {
            fileResponseMap = extractFileResponseData(candidateSession, (MultipartHttpServletRequest) request);
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
    private Map<Identifier, CandidateFileSubmission> extractFileResponseData(final CandidateItemSession candidateSession,
            final MultipartHttpServletRequest multipartRequest) {
        final Map<Identifier, CandidateFileSubmission> fileResponseMap = new HashMap<Identifier, CandidateFileSubmission>();
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
                final CandidateFileSubmission candidateFileSubmission = assessmentCandidateService.importFileResponse(candidateSession, multipartFile);
                fileResponseMap.put(responseIdentifier, candidateFileSubmission);
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
}
