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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.base.services.Auditor;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemAttemptDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemEventDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemSessionDao;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateFileSubmission;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemAttempt;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemResponse;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSessionState;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.ResponseLegality;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.ItemRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.RenderingMode;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.services.domain.CandidatePrivilege;
import uk.ac.ed.ph.qtiworks.services.domain.CandidatePrivilegeException;
import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;

import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.FileResponseData;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.ResponseData.ResponseDataType;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service the manages the real-time delivery of an {@link Assessment}
 * to a particular candidate.
 * <p>
 * NOTE: Remember there is no {@link IdentityContext} for candidates.
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class AssessmentCandidateService {

    /** FIXME: Change to Candidate auditor/logger */
    @Resource
    private Auditor auditor;

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private QtiSerializer qtiSerializer;

    @Resource
    private EntityGraphService entityGraphService;

    @Resource
    private AssessmentPackageFileService assessmentPackageFileService;

    @Resource
    private FilespaceManager filespaceManager;

    @Resource
    private CandidateDataServices candidateDataServices;

    @Resource
    private AssessmentRenderer assessmentRenderer;

    @Resource
    private CandidateUploadService candidateUploadService;

    @Resource
    private CandidateItemSessionDao candidateItemSessionDao;

    @Resource
    private CandidateItemEventDao candidateItemEventDao;

    @Resource
    private CandidateItemAttemptDao candidateItemAttemptDao;

    //----------------------------------------------------
    // Session access after creation

    /**
     * Looks up the {@link CandidateItemSession} having the given ID (xid)
     * and checks the given sessionHash against that stored in the session as a means of
     * "authentication".
     *
     * @param xid
     * @return
     * @throws DomainEntityNotFoundException
     * @throws CandidatePrivilegeException
     * @throws CandidateCandidatePrivilegeException
     */
    public CandidateItemSession lookupCandidateSession(final long xid, final String sessionHash)
            throws DomainEntityNotFoundException, CandidatePrivilegeException {
        Assert.ensureNotNull(sessionHash, "sessionHash");
        final CandidateItemSession session = candidateItemSessionDao.requireFindById(xid);
        ensureCallerMayAccess(session, sessionHash);
        return session;
    }

    private void ensureCallerMayAccess(final CandidateItemSession candidateItemSession, final String sessionHash)
            throws CandidatePrivilegeException {
        if (!sessionHash.equals(candidateItemSession.getSessionHash())) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.ACCESS_CANDIDATE_SESSION);
        }
    }

    private void ensureSessionNotTerminated(final CandidateItemSession candidateItemSession) throws CandidatePrivilegeException {
        if (candidateItemSession.getState()==CandidateSessionState.TERMINATED) {
            /* No access when session has been is closed */
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.ACCESS_TERMINATED_SESSION);
        }
    }

    //----------------------------------------------------
    // Attempt

    public CandidateItemAttempt handleAttempt(final long xid, final String sessionHash,
            final Map<Identifier, StringResponseData> stringResponseMap,
            final Map<Identifier, MultipartFile> fileResponseMap)
            throws RuntimeValidationException, CandidatePrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateSession = lookupCandidateSession(xid, sessionHash);
        return handleAttempt(candidateSession, stringResponseMap, fileResponseMap);
    }

    public CandidateItemAttempt handleAttempt(final CandidateItemSession candidateItemSession,
            final Map<Identifier, StringResponseData> stringResponseMap,
            final Map<Identifier, MultipartFile> fileResponseMap)
            throws RuntimeValidationException, CandidatePrivilegeException {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();

        /* Make sure an attempt is allowed */
        if (candidateItemSession.getState()!=CandidateSessionState.INTERACTING) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.MAKE_ATTEMPT);
        }

        /* Build response map in required format for JQTI+.
         * NB: The following doesn't test for duplicate keys in the two maps. I'm not sure
         * it's worth the effort.
         */
        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        if (stringResponseMap!=null) {
            for (final Entry<Identifier, StringResponseData> stringResponseEntry : stringResponseMap.entrySet()) {
                final Identifier identifier = stringResponseEntry.getKey();
                final StringResponseData stringResponseData = stringResponseEntry.getValue();
                responseMap.put(identifier, stringResponseData);
            }
        }
        final Map<Identifier, CandidateFileSubmission> fileSubmissionMap = new HashMap<Identifier, CandidateFileSubmission>();
        if (fileResponseMap!=null) {
            for (final Entry<Identifier, MultipartFile> fileResponseEntry : fileResponseMap.entrySet()) {
                final Identifier identifier = fileResponseEntry.getKey();
                final MultipartFile multipartFile = fileResponseEntry.getValue();
                final CandidateFileSubmission fileSubmission = candidateUploadService.importFileSubmission(candidateItemSession, multipartFile);
                final FileResponseData fileResponseData = new FileResponseData(new File(fileSubmission.getStoredFilePath()), fileSubmission.getContentType());
                responseMap.put(identifier, fileResponseData);
                fileSubmissionMap.put(identifier, fileSubmission);
            }
        }

        /* Build Map of responses in appropriate entity form */
        final CandidateItemAttempt candidateItemAttempt = new CandidateItemAttempt();
        final Map<Identifier, CandidateItemResponse> responseEntityMap = new HashMap<Identifier, CandidateItemResponse>();
        final Set<CandidateItemResponse> candidateItemResponses = new HashSet<CandidateItemResponse>();
        for (final Entry<Identifier, ResponseData> responseEntry : responseMap.entrySet()) {
            final Identifier responseIdentifier = responseEntry.getKey();
            final ResponseData responseData = responseEntry.getValue();

            final CandidateItemResponse candidateItemResponse = new CandidateItemResponse();
            candidateItemResponse.setResponseIdentifier(responseIdentifier.toString());
            candidateItemResponse.setAttempt(candidateItemAttempt);
            candidateItemResponse.setResponseType(responseData.getType());
            candidateItemResponse.setResponseLegality(ResponseLegality.VALID); /* (May change this below) */
            switch (responseData.getType()) {
                case STRING:
                    candidateItemResponse.setStringResponseData(((StringResponseData) responseData).getResponseData());
                    break;

                case FILE:
                    candidateItemResponse.setFileSubmission(fileSubmissionMap.get(responseIdentifier));
                    break;

                default:
                    throw new QtiWorksLogicException("Unexpected switch case: " + responseData.getType());
            }
            responseEntityMap.put(responseIdentifier, candidateItemResponse);
            candidateItemResponses.add(candidateItemResponse);
        }
        candidateItemAttempt.setResponses(candidateItemResponses);

        /* Get current JQTI state and create JQTI controller */
        final CandidateItemEvent mostRecentEvent = candidateDataServices.getMostRecentEvent(candidateItemSession);
        final ItemSessionController itemSessionController = candidateDataServices.createItemSessionController(mostRecentEvent);

        /* Attempt to bind responses */
        final Set<Identifier> badResponseIdentifiers = itemSessionController.bindResponses(responseMap);

        /* Record any responses that failed to bind */
        final boolean allResponsesBound = badResponseIdentifiers.isEmpty();
        for (final Identifier badResponseIdentifier : badResponseIdentifiers) {
            responseEntityMap.get(badResponseIdentifier).setResponseLegality(ResponseLegality.BAD);
        }

        /* Now validate the responses according to any constraints specified by the interactions */
        boolean allResponsesValid = false;
        if (allResponsesBound) {
            final Set<Identifier> invalidResponseIdentifiers = itemSessionController.validateResponses();
            allResponsesValid = invalidResponseIdentifiers.isEmpty();
            if (!allResponsesValid) {
                /* Some responses not valid, so note these down */
                for (final Identifier invalidResponseIdentifier : invalidResponseIdentifiers) {
                    responseEntityMap.get(invalidResponseIdentifier).setResponseLegality(ResponseLegality.INVALID);
                }
            }

            /* Invoke response processing (only if responses are valid) */
            if (allResponsesValid) {
                itemSessionController.processResponses();
            }
        }

        /* Record resulting attempt and event */
        final CandidateItemEventType eventType = allResponsesBound ?
            (allResponsesValid ? CandidateItemEventType.ATTEMPT_VALID : CandidateItemEventType.ATTEMPT_INVALID)
            : CandidateItemEventType.ATTEMPT_BAD;
        final CandidateItemEvent candidateItemEvent = candidateDataServices.recordCandidateItemEvent(candidateItemSession, eventType, itemSessionController.getItemSessionState());

        candidateItemAttempt.setEvent(candidateItemEvent);
        candidateItemAttemptDao.persist(candidateItemAttempt);

        /* Update session state */
        final boolean attemptAllowed = itemSessionController.isAttemptAllowed(itemDelivery.getItemDeliverySettings().getMaxAttempts());
        candidateItemSession.setState(attemptAllowed ? CandidateSessionState.INTERACTING : CandidateSessionState.CLOSED);
        candidateItemSessionDao.update(candidateItemSession);

        auditor.recordEvent("Recorded candidate attempt #" + candidateItemAttempt.getId()
                + " on session #" + candidateItemSession.getId()
                + " on delivery #" + candidateItemSession.getItemDelivery().getId());
        return candidateItemAttempt;
    }

    //----------------------------------------------------
    // Session close(by candidate)

    /**
     * Closes the {@link CandidateItemSession} having the given ID (xid), moving it
     * into {@link CandidateSessionState#CLOSED} state.
     */
    public CandidateItemSession closeCandidateSession(final long xid, final String sessionHash)
            throws CandidatePrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateItemSession = lookupCandidateSession(xid, sessionHash);
        return closeCandidateSession(candidateItemSession);
    }

    public CandidateItemSession closeCandidateSession(final CandidateItemSession candidateItemSession)
            throws CandidatePrivilegeException {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");

        /* Check this is allowed in current state */
        ensureSessionNotTerminated(candidateItemSession);
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();
        final ItemDeliverySettings itemDeliverySettings = itemDelivery.getItemDeliverySettings();
        if (candidateItemSession.getState()==CandidateSessionState.CLOSED) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.CLOSE_SESSION_WHEN_CLOSED);
        }
        else if (!itemDeliverySettings.isAllowClose()) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.CLOSE_SESSION_WHEN_INTERACTING);
        }

        /* Record event */
        final ItemSessionState itemSessionState = candidateDataServices.getCurrentItemSessionState(candidateItemSession);
        candidateDataServices.recordCandidateItemEvent(candidateItemSession, CandidateItemEventType.CLOSE, itemSessionState);

        /* Update state */
        candidateItemSession.setState(CandidateSessionState.CLOSED);
        candidateItemSessionDao.update(candidateItemSession);

        auditor.recordEvent("Candidate ended session #" + candidateItemSession.getId());
        return candidateItemSession;
    }

    //----------------------------------------------------
    // Session reinit

    /**
     * Re-initialises the {@link CandidateItemSession} having the given ID (xid), returning the
     * updated {@link CandidateItemSession}. At QTI level, this reruns template processing, so
     * randomised values will change as a result of this process.
     */
    public CandidateItemSession reinitCandidateSession(final long xid, final String sessionHash)
            throws RuntimeValidationException, CandidatePrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateItemSession = lookupCandidateSession(xid, sessionHash);
        return reinitCandidateSession(candidateItemSession);
    }

    public CandidateItemSession reinitCandidateSession(final CandidateItemSession candidateItemSession)
            throws RuntimeValidationException, CandidatePrivilegeException {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");

        /* Make sure caller may reinit the session */
        ensureSessionNotTerminated(candidateItemSession);
        final CandidateSessionState candidateItemSessionState = candidateItemSession.getState();
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();
        final ItemDeliverySettings itemDeliverySettings = itemDelivery.getItemDeliverySettings();
        if (candidateItemSessionState==CandidateSessionState.INTERACTING && !itemDeliverySettings.isAllowReinitWhenInteracting()) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.REINIT_SESSION_WHEN_INTERACTING);
        }
        else if (candidateItemSessionState==CandidateSessionState.CLOSED && !itemDeliverySettings.isAllowReinitWhenClosed()) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.REINIT_SESSION_WHEN_CLOSED);
        }

        /* Create fresh JQTI+ state */
        final ItemSessionState itemSessionState = new ItemSessionState();

        /* Get the resolved JQTI+ Object for the underlying package */
        final ItemSessionController itemSessionController = candidateDataServices.createItemSessionController(itemDelivery, itemSessionState);

        /* Initialise state */
        itemSessionController.initialize();

        /* Record event */
        candidateDataServices.recordCandidateItemEvent(candidateItemSession, CandidateItemEventType.REINIT, itemSessionState);

        /* Update state */
        final boolean attemptAllowed = itemSessionController.isAttemptAllowed(itemDeliverySettings.getMaxAttempts());
        candidateItemSession.setState(attemptAllowed ? CandidateSessionState.INTERACTING : CandidateSessionState.CLOSED);
        candidateItemSessionDao.update(candidateItemSession);

        auditor.recordEvent("Candidate re-intialized session #" + candidateItemSession.getId());
        return candidateItemSession;
    }

    //----------------------------------------------------
    // Session reset

    /**
     * Resets the {@link CandidateItemSession} having the given ID (xid), returning the
     * updated {@link CandidateItemSession}. This takes the session back to the state it
     * was in immediately after the last {@link CandidateItemEvent#REINIT_WHEN_INTERACTING} (if applicable),
     * or after the original {@link CandidateItemEvent#INIT}.
     */
    public CandidateItemSession resetCandidateSession(final long xid, final String sessionHash)
            throws CandidatePrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateItemSession = lookupCandidateSession(xid, sessionHash);
        return resetCandidateSession(candidateItemSession);
    }

    public CandidateItemSession resetCandidateSession(final CandidateItemSession candidateItemSession)
            throws CandidatePrivilegeException {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");

        /* Make sure caller may reset the session */
        ensureSessionNotTerminated(candidateItemSession);
        final CandidateSessionState candidateItemSessionState = candidateItemSession.getState();
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();
        final ItemDeliverySettings itemDeliverySettings = itemDelivery.getItemDeliverySettings();
        if (candidateItemSessionState==CandidateSessionState.INTERACTING && !itemDeliverySettings.isAllowResetWhenInteracting()) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.RESET_SESSION_WHEN_INTERACTING);
        }
        else if (candidateItemSessionState==CandidateSessionState.CLOSED && !itemDeliverySettings.isAllowResetWhenClosed()) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.RESET_SESSION_WHEN_CLOSED);
        }

        /* Find the last REINIT, falling back to original INIT if none present */
        final List<CandidateItemEvent> events = candidateItemEventDao.getForSessionReversed(candidateItemSession);
        CandidateItemEvent lastInitEvent = null;
        for (final CandidateItemEvent event : events) {
            if (event.getEventType()==CandidateItemEventType.REINIT) {
                lastInitEvent = event;
                break;
            }
        }
        if (lastInitEvent==null) {
            lastInitEvent = events.get(events.size()-1);
        }

        /* Pull the QTI state from this event */
        final ItemSessionState itemSessionState = candidateDataServices.unmarshalItemSessionState(lastInitEvent);

        /* Record event */
        candidateDataServices.recordCandidateItemEvent(candidateItemSession, CandidateItemEventType.RESET, itemSessionState);

        /* Update state */
        final ItemSessionController itemSessionController = candidateDataServices.createItemSessionController(itemDelivery, itemSessionState);
        final boolean attemptAllowed = itemSessionController.isAttemptAllowed(itemDeliverySettings.getMaxAttempts());
        candidateItemSession.setState(attemptAllowed ? CandidateSessionState.INTERACTING : CandidateSessionState.CLOSED);
        candidateItemSessionDao.update(candidateItemSession);

        auditor.recordEvent("Candidate reset session #" + candidateItemSession.getId());
        return candidateItemSession;
    }

    //----------------------------------------------------
    // Solution request

    /**
     * Transitions the {@link CandidateItemSession} having the given ID (xid) into solution state.
     */
    public CandidateItemSession transitionCandidateSessionToSolutionState(final long xid, final String sessionHash)
            throws CandidatePrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateItemSession = lookupCandidateSession(xid, sessionHash);
        return transitionCandidateSessionToSolutionState(candidateItemSession);
    }

    public CandidateItemSession transitionCandidateSessionToSolutionState(final CandidateItemSession candidateItemSession)
            throws CandidatePrivilegeException {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateItemSession);
        final CandidateSessionState candidateItemSessionState = candidateItemSession.getState();
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();
        final ItemDeliverySettings itemDeliverySettings = itemDelivery.getItemDeliverySettings();
        if (candidateItemSessionState==CandidateSessionState.INTERACTING && !itemDeliverySettings.isAllowSolutionWhenInteracting()) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.SOLUTION_WHEN_INTERACTING);
        }
        else if (candidateItemSessionState==CandidateSessionState.CLOSED && !itemDeliverySettings.isAllowResetWhenClosed()) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.SOLUTION_WHEN_CLOSED);
        }

        /* Record event */
        final ItemSessionState itemSessionState = candidateDataServices.getCurrentItemSessionState(candidateItemSession);
        candidateDataServices.recordCandidateItemEvent(candidateItemSession, CandidateItemEventType.SOLUTION, itemSessionState);

        /* Change session state to CLOSED if it's not already there */
        if (candidateItemSessionState==CandidateSessionState.INTERACTING) {
            candidateItemSession.setState(CandidateSessionState.CLOSED);
            candidateItemSessionDao.update(candidateItemSession);
        }

        auditor.recordEvent("Candidate moved session #" + candidateItemSession.getId() + " to solution state");
        return candidateItemSession;
    }

    //----------------------------------------------------
    // Playback request

    /**
     * Updates the state of the {@link CandidateItemSession} having the given ID (xid)
     * so that it will play back the {@link CandidateItemEvent} having the given ID (xeid).
     */
    public CandidateItemSession setPlaybackState(final long xid, final String sessionHash, final long xeid)
            throws CandidatePrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateItemSession = lookupCandidateSession(xid, sessionHash);
        return setPlaybackState(candidateItemSession, xeid);
    }

    public CandidateItemSession setPlaybackState(final CandidateItemSession candidateItemSession, final long xeid)
            throws CandidatePrivilegeException, DomainEntityNotFoundException {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateItemSession);
        final CandidateSessionState candidateItemSessionState = candidateItemSession.getState();
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();
        final ItemDeliverySettings itemDeliverySettings = itemDelivery.getItemDeliverySettings();
        if (candidateItemSessionState==CandidateSessionState.INTERACTING) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.PLAYBACK_WHEN_INTERACTING);
        }
        else if (candidateItemSessionState==CandidateSessionState.CLOSED && !itemDeliverySettings.isAllowPlayback()) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.PLAYBACK);
        }

        /* Look up target event, make sure it belongs to this session and make sure it can be played back */
        final CandidateItemEvent targetEvent = candidateItemEventDao.requireFindById(xeid);
        if (targetEvent.getCandidateItemSession().getId().longValue()!=candidateItemSession.getId().longValue()) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.PLAYBACK_OTHER_SESSION);
        }
        final CandidateItemEventType targetEventType = targetEvent.getEventType();
        if (targetEventType==CandidateItemEventType.PLAYBACK
                || targetEventType==CandidateItemEventType.CLOSE
                || targetEventType==CandidateItemEventType.TERMINATE) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.PLAYBACK_EVENT);
        }

        /* Record event */
        final ItemSessionState itemSessionState = candidateDataServices.getCurrentItemSessionState(candidateItemSession);
        candidateDataServices.recordCandidateItemEvent(candidateItemSession, CandidateItemEventType.PLAYBACK, itemSessionState, targetEvent);

        auditor.recordEvent("Candidate played back event #" + targetEvent.getId()
                + " in session #" + candidateItemSession.getId());
        return candidateItemSession;
    }

    //----------------------------------------------------
    // Session termination (by candidate)

    /**
     * Terminates the {@link CandidateItemSession} having the given ID (xid), moving it into
     * {@link CandidateSessionState#TERMINATED} state.
     * <p>
     * Currently we're always allowing this action to be made when in
     * {@link CandidateSessionState#INTERACTING} or {@link CandidateSessionState#CLOSED}
     * states.
     */
    public CandidateItemSession terminateCandidateSession(final long xid, final String sessionHash)
            throws CandidatePrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateItemSession = lookupCandidateSession(xid, sessionHash);
        return terminateCandidateSession(candidateItemSession);
    }

    public CandidateItemSession terminateCandidateSession(final CandidateItemSession candidateItemSession)
            throws CandidatePrivilegeException {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");

        /* Check session has not already been terminated */
        ensureSessionNotTerminated(candidateItemSession);

        /* Record event */
        final ItemSessionState itemSessionState = candidateDataServices.getCurrentItemSessionState(candidateItemSession);
        candidateDataServices.recordCandidateItemEvent(candidateItemSession, CandidateItemEventType.TERMINATE, itemSessionState);

        /* Update state */
        candidateItemSession.setState(CandidateSessionState.TERMINATED);
        candidateItemSessionDao.update(candidateItemSession);

        auditor.recordEvent("Candidate terminated session #" + candidateItemSession.getId());
        return candidateItemSession;
    }

    //----------------------------------------------------
    // Rendering

    /**
     * Renders the current state of the {@link CandidateItemSession} having
     * the given ID (xid).
     */
    public void renderCurrentState(final long xid, final String sessionHash,
            final RenderingOptions renderingOptions,
            final OutputStreamer outputStreamer)
            throws CandidatePrivilegeException, DomainEntityNotFoundException, IOException {
        final CandidateItemSession candidateItemSession = lookupCandidateSession(xid, sessionHash);
        renderCurrentState(candidateItemSession, renderingOptions, outputStreamer);
    }

    public void renderCurrentState(final CandidateItemSession candidateItemSession,
            final RenderingOptions renderingOptions,
            final OutputStreamer outputStreamer) throws IOException {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");
        Assert.ensureNotNull(renderingOptions, "renderingOptions");
        Assert.ensureNotNull(outputStreamer, "outputStreamer");

        /* Look up most recent event */
        final CandidateItemEvent latestEvent = candidateDataServices.getMostRecentEvent(candidateItemSession);

        /* Create temporary file to hold the output before it gets streamed */
        final File resultFile = filespaceManager.createTempFile();
        try {
            /* Render to temp file */
            FileOutputStream resultOutputStream = null;
            try {
                resultOutputStream = new FileOutputStream(resultFile);
                renderEvent(candidateItemSession, latestEvent, renderingOptions, resultOutputStream);
            }
            catch (final IOException e) {
                throw new QtiWorksRuntimeException("Unexpected IOException", e);
            }
            finally {
                IOUtils.closeQuietly(resultOutputStream);
            }

            /* Finally stream to caller */
            final String contentType = renderingOptions.getSerializationMethod().getContentType();
            final long contentLength = resultFile.length();
            FileInputStream resultInputStream = null;
            try {
                resultInputStream = new FileInputStream(resultFile);
                outputStreamer.stream(contentType, contentLength, requestTimestampContext.getCurrentRequestTimestamp(),
                        resultInputStream);
            }
            catch (final FileNotFoundException e) {
                throw new QtiWorksRuntimeException("Unexpected IOException", e);
            }
            catch (final IOException e) {
                /* Streamer threw Exception */
                throw e;
            }
            finally {
                IOUtils.closeQuietly(resultInputStream);
            }
        }
        finally {
            if (!resultFile.delete()) {
                throw new QtiWorksRuntimeException("Could not delete result file " + resultFile.getPath());
            }
        }
    }

    private void renderEvent(final CandidateItemSession candidateItemSession,
            final CandidateItemEvent candidateEvent,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final CandidateSessionState candidateItemSessionState = candidateItemSession.getState();
        switch (candidateItemSessionState) {
            case INTERACTING:
                renderEventWhenInteracting(candidateEvent, renderingOptions, resultStream);
                break;

            case CLOSED:
                renderEventWhenClosed(candidateEvent, renderingOptions, resultStream);
                break;

            case TERMINATED:
                renderTerminated(candidateEvent, renderingOptions, resultStream);
                break;

            default:
                throw new QtiWorksLogicException("Unexpected state " + candidateItemSessionState);
        }
    }

    private void renderEventWhenInteracting(final CandidateItemEvent candidateEvent,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final CandidateItemEventType eventType = candidateEvent.getEventType();
        switch (eventType) {
            case INIT:
            case REINIT:
            case RESET:
                renderInteractingPresentation(candidateEvent, renderingOptions, resultStream);
                break;

            case ATTEMPT_VALID:
            case ATTEMPT_INVALID:
            case ATTEMPT_BAD:
                renderInteractingAfterAttempt(candidateEvent, renderingOptions, resultStream);
                break;

            default:
                throw new QtiWorksLogicException("Unexpected logic branch. Event " + eventType
                        + " should have moved session state out of " + CandidateSessionState.INTERACTING
                        + " mode");
        }
    }

    private void renderInteractingPresentation(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequestWhenInteracting(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.AFTER_INITIALISATION);

        assessmentRenderer.renderItem(renderingRequest, resultStream);
    }

    private void renderInteractingAfterAttempt(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequestWhenInteracting(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.AFTER_ATTEMPT);

        final CandidateItemAttempt attempt = candidateItemAttemptDao.getForEvent(candidateEvent);
        if (attempt==null) {
            throw new QtiWorksLogicException("Expected to find a CandidateItemAttempt corresponding to event #" + candidateEvent.getId());
        }
        final Map<Identifier, ResponseData> responseDataBuilder = new HashMap<Identifier, ResponseData>();
        final Set<Identifier> badResponseIdentifiersBuilder = new HashSet<Identifier>();
        final Set<Identifier> invalidResponseIdentifiersBuilder = new HashSet<Identifier>();
        extractResponseData(attempt, responseDataBuilder, badResponseIdentifiersBuilder, invalidResponseIdentifiersBuilder);

        renderingRequest.setResponseInputs(responseDataBuilder);
        renderingRequest.setBadResponseIdentifiers(badResponseIdentifiersBuilder);
        renderingRequest.setInvalidResponseIdentifiers(invalidResponseIdentifiersBuilder);

        assessmentRenderer.renderItem(renderingRequest, resultStream);
    }

    private ItemRenderingRequest createItemRenderingRequestWhenInteracting(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final CandidateItemSession candidateItemSession = candidateEvent.getCandidateItemSession();
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();
        final ItemDeliverySettings itemDeliverySettings = itemDelivery.getItemDeliverySettings();

        final ItemRenderingRequest renderingRequest = createPartialItemRenderingRequest(candidateEvent, renderingOptions);
        renderingRequest.setCloseAllowed(itemDeliverySettings.isAllowClose());
        renderingRequest.setReinitAllowed(itemDeliverySettings.isAllowReinitWhenInteracting());
        renderingRequest.setResetAllowed(itemDeliverySettings.isAllowResetWhenInteracting());
        renderingRequest.setSolutionAllowed(itemDeliverySettings.isAllowSolutionWhenInteracting());
        renderingRequest.setResultAllowed(false);
        renderingRequest.setSourceAllowed(itemDeliverySettings.isAllowSource());
        renderingRequest.setBadResponseIdentifiers(null);
        renderingRequest.setInvalidResponseIdentifiers(null);
        renderingRequest.setResponseInputs(null);
        return renderingRequest;
    }

    private void renderEventWhenClosed(final CandidateItemEvent candidateEvent,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final CandidateItemEventType eventType = candidateEvent.getEventType();
        switch (eventType) {
            case ATTEMPT_VALID:
            case ATTEMPT_INVALID:
            case ATTEMPT_BAD:
                renderClosedAfterAttempt(candidateEvent, renderingOptions, resultStream);
                break;

            case CLOSE:
                renderClosed(candidateEvent, renderingOptions, resultStream);
                break;

            case SOLUTION:
                renderSolution(candidateEvent, renderingOptions, resultStream);
                break;

            case PLAYBACK:
                renderPlayback(candidateEvent, renderingOptions, resultStream);
                break;

            default:
                throw new QtiWorksLogicException("Unexpected logic branch. Event " + eventType
                        + " either hasn't been implemented here, or should have earlier moved session state out of "
                        + CandidateSessionState.INTERACTING
                        + " mode");
        }
    }

    private ItemRenderingRequest createPartialItemRenderingRequest(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final CandidateItemSession candidateItemSession = candidateEvent.getCandidateItemSession();
        final CandidateSessionState candidateItemSessionState = candidateItemSession.getState();
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();
        final ItemDeliverySettings itemDeliverySettings = itemDelivery.getItemDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(itemDelivery);

        final ItemRenderingRequest renderingRequest = new ItemRenderingRequest();
        renderingRequest.setAssessmentResourceLocator(assessmentPackageFileService.createResolvingResourceLocator(assessmentPackage));
        renderingRequest.setAssessmentResourceUri(assessmentPackageFileService.createAssessmentObjectUri(assessmentPackage));
        renderingRequest.setCandidateSessionState(candidateItemSessionState);
        renderingRequest.setItemSessionState(candidateDataServices.unmarshalItemSessionState(candidateEvent));
        renderingRequest.setRenderingOptions(renderingOptions);
        renderingRequest.setPrompt(itemDeliverySettings.getPrompt());
        renderingRequest.setAuthorMode(itemDeliverySettings.isAuthorMode());
        return renderingRequest;
    }

    private void renderClosedAfterAttempt(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequestWhenClosed(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.AFTER_ATTEMPT);

        /* FIXME: Cut & paste below! Refactor this! */
        final CandidateItemAttempt attempt = candidateItemAttemptDao.getForEvent(candidateEvent);
        if (attempt==null) {
            throw new QtiWorksLogicException("Expected to find a CandidateItemAttempt corresponding to event #" + candidateEvent.getId());
        }
        final Map<Identifier, ResponseData> responseDataBuilder = new HashMap<Identifier, ResponseData>();
        final Set<Identifier> badResponseIdentifiersBuilder = new HashSet<Identifier>();
        final Set<Identifier> invalidResponseIdentifiersBuilder = new HashSet<Identifier>();
        extractResponseData(attempt, responseDataBuilder, badResponseIdentifiersBuilder, invalidResponseIdentifiersBuilder);

        renderingRequest.setResponseInputs(responseDataBuilder);
        renderingRequest.setBadResponseIdentifiers(badResponseIdentifiersBuilder);
        renderingRequest.setInvalidResponseIdentifiers(invalidResponseIdentifiersBuilder);

        assessmentRenderer.renderItem(renderingRequest, resultStream);
    }

    private void renderClosed(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequestWhenClosed(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.CLOSED);
        assessmentRenderer.renderItem(renderingRequest, resultStream);
    }

    private void renderSolution(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequestWhenClosed(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.SOLUTION);
        assessmentRenderer.renderItem(renderingRequest, resultStream);
    }

    private void renderPlayback(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final CandidateItemEvent playbackEvent = candidateEvent.getPlaybackEvent();

        final CandidateItemSession candidateItemSession = candidateEvent.getCandidateItemSession();
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();
        final ItemDeliverySettings itemDeliverySettings = itemDelivery.getItemDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(itemDelivery);

        final ItemRenderingRequest renderingRequest = new ItemRenderingRequest();
        renderingRequest.setCandidateSessionState(CandidateSessionState.CLOSED);
        renderingRequest.setRenderingMode(RenderingMode.PLAYBACK);
        renderingRequest.setAssessmentResourceLocator(assessmentPackageFileService.createResolvingResourceLocator(assessmentPackage));
        renderingRequest.setAssessmentResourceUri(assessmentPackageFileService.createAssessmentObjectUri(assessmentPackage));
        renderingRequest.setItemSessionState(candidateDataServices.unmarshalItemSessionState(playbackEvent));
        renderingRequest.setRenderingOptions(renderingOptions);
        renderingRequest.setPrompt(itemDeliverySettings.getPrompt());
        renderingRequest.setAuthorMode(itemDeliverySettings.isAuthorMode());

        renderingRequest.setCloseAllowed(false);
        renderingRequest.setSolutionAllowed(itemDeliverySettings.isAllowSolutionWhenClosed());
        renderingRequest.setReinitAllowed(itemDeliverySettings.isAllowReinitWhenClosed());
        renderingRequest.setResetAllowed(itemDeliverySettings.isAllowResetWhenClosed());
        renderingRequest.setResultAllowed(itemDeliverySettings.isAllowResult());
        renderingRequest.setSourceAllowed(itemDeliverySettings.isAllowSource());

        /* If it's an attempt, pull out the raw response data */
        final CandidateItemAttempt playbackAttempt = candidateItemAttemptDao.getForEvent(playbackEvent);
        if (playbackAttempt!=null) {
            final Map<Identifier, ResponseData> responseDataBuilder = new HashMap<Identifier, ResponseData>();
            final Set<Identifier> badResponseIdentifiersBuilder = new HashSet<Identifier>();
            final Set<Identifier> invalidResponseIdentifiersBuilder = new HashSet<Identifier>();
            extractResponseData(playbackAttempt, responseDataBuilder, badResponseIdentifiersBuilder, invalidResponseIdentifiersBuilder);

            renderingRequest.setResponseInputs(responseDataBuilder);
            renderingRequest.setBadResponseIdentifiers(badResponseIdentifiersBuilder);
            renderingRequest.setInvalidResponseIdentifiers(invalidResponseIdentifiersBuilder);
        }

        /* Register playback events */
        renderingRequest.setPlaybackAllowed(itemDeliverySettings.isAllowPlayback());
        if (itemDeliverySettings.isAllowPlayback()) {
            renderingRequest.setPlaybackEvents(getPlaybackEvents(candidateItemSession));
            renderingRequest.setCurrentPlaybackEvent(playbackEvent);
        }

        /* Record which event we're playing back */
        renderingRequest.setCurrentPlaybackEvent(playbackEvent);

        assessmentRenderer.renderItem(renderingRequest, resultStream);
    }

    private ItemRenderingRequest createItemRenderingRequestWhenClosed(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final CandidateItemSession candidateItemSession = candidateEvent.getCandidateItemSession();
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();
        final ItemDeliverySettings itemDeliverySettings = itemDelivery.getItemDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(itemDelivery);

        final ItemRenderingRequest renderingRequest = new ItemRenderingRequest();
        renderingRequest.setCandidateSessionState(CandidateSessionState.CLOSED);
        renderingRequest.setRenderingMode(RenderingMode.CLOSED);
        renderingRequest.setAssessmentResourceLocator(assessmentPackageFileService.createResolvingResourceLocator(assessmentPackage));
        renderingRequest.setAssessmentResourceUri(assessmentPackageFileService.createAssessmentObjectUri(assessmentPackage));
        renderingRequest.setItemSessionState(candidateDataServices.unmarshalItemSessionState(candidateEvent));
        renderingRequest.setPrompt(itemDeliverySettings.getPrompt());
        renderingRequest.setAuthorMode(itemDeliverySettings.isAuthorMode());

        renderingRequest.setRenderingOptions(renderingOptions);
        renderingRequest.setCloseAllowed(false);
        renderingRequest.setSolutionAllowed(itemDeliverySettings.isAllowSolutionWhenClosed());
        renderingRequest.setReinitAllowed(itemDeliverySettings.isAllowReinitWhenClosed());
        renderingRequest.setResetAllowed(itemDeliverySettings.isAllowResetWhenClosed());
        renderingRequest.setResultAllowed(itemDeliverySettings.isAllowResult());
        renderingRequest.setSourceAllowed(itemDeliverySettings.isAllowSource());

        renderingRequest.setPlaybackAllowed(itemDeliverySettings.isAllowPlayback());
        if (itemDeliverySettings.isAllowPlayback()) {
            renderingRequest.setPlaybackEvents(getPlaybackEvents(candidateItemSession));
        }
        return renderingRequest;
    }

    private void renderTerminated(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final ItemRenderingRequest renderingRequest = createPartialItemRenderingRequest(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.TERMINATED);
        assessmentRenderer.renderItem(renderingRequest, resultStream);
    }

    private void extractResponseData(final CandidateItemAttempt attempt, final Map<Identifier, ResponseData> responseDataBuilder,
            final Set<Identifier> badResponseIdentifiersBuilder, final Set<Identifier> invalidResponseIdentifiersBuilder) {
        for (final CandidateItemResponse response : attempt.getResponses()) {
            final Identifier responseIdentifier = new Identifier(response.getResponseIdentifier());
            final ResponseLegality responseLegality = response.getResponseLegality();
            final ResponseDataType responseType = response.getResponseType();
            ResponseData responseData = null;
            switch (responseType) {
                case STRING:
                    responseData = new StringResponseData(response.getStringResponseData());
                    break;

                case FILE:
                    final CandidateFileSubmission fileSubmission = response.getFileSubmission();
                    responseData = new FileResponseData(new File(fileSubmission.getStoredFilePath()),
                            fileSubmission.getContentType());
                    break;

                default:
                    throw new QtiWorksLogicException("Unexpected ResponseDataType " + responseType);
            }
            responseDataBuilder.put(responseIdentifier, responseData);
            if (responseLegality==ResponseLegality.BAD) {
                badResponseIdentifiersBuilder.add(responseIdentifier);
            }
            else if (responseLegality==ResponseLegality.INVALID) {
                invalidResponseIdentifiersBuilder.add(responseIdentifier);
            }
        }
    }

    //----------------------------------------------------
    // Access to additional package resources (e.g. images/CSS)

    public void streamAssessmentFile(final CandidateItemSession candidateItemSession, final String fileSystemIdString,
            final OutputStreamer outputStreamer)
            throws CandidatePrivilegeException, IOException {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");
        Assert.ensureNotNull(fileSystemIdString, "fileSystemIdString");
        Assert.ensureNotNull(outputStreamer, "outputStreamer");

        /* Make sure requested file is whitelisted for access */
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(itemDelivery);
        String resultingFileHref = null;
        for (final String safeFileHref : assessmentPackage.getSafeFileHrefs()) {
            final URI fileUri = assessmentPackageFileService.createAssessmentFileUri(assessmentPackage, safeFileHref);
            if (fileUri.toString().equals(fileSystemIdString)) {
                resultingFileHref = safeFileHref;
                break;
            }
        }
        if (resultingFileHref==null) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.ACCESS_BLACKLISTED_ASSESSMENT_FILE);
        }

        /* Finally stream the required resource */
        assessmentPackageFileService.streamAssessmentPackageFile(assessmentPackage, resultingFileHref, outputStreamer);
    }

    //----------------------------------------------------
    // Candidate Source access

    public void streamAssessmentSource(final long xid, final String sessionHash, final OutputStreamer outputStreamer)
            throws CandidatePrivilegeException, IOException, DomainEntityNotFoundException {
        Assert.ensureNotNull(outputStreamer, "outputStreamer");
        final CandidateItemSession candidateItemSession = lookupCandidateSession(xid, sessionHash);
        streamAssessmentSource(candidateItemSession, outputStreamer);
    }

    public void streamAssessmentSource(final CandidateItemSession candidateItemSession, final OutputStreamer outputStreamer)
            throws CandidatePrivilegeException, IOException {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");
        Assert.ensureNotNull(outputStreamer, "outputStreamer");
        ensureCallerMayViewSource(candidateItemSession);
        final ItemDelivery itemDelivery = candidateItemSession.getItemDelivery();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(itemDelivery);

        assessmentPackageFileService.streamAssessmentPackageSource(assessmentPackage, outputStreamer);
        auditor.recordEvent("Candidate streamed source for session #" + candidateItemSession.getId());
    }

    private void ensureCallerMayViewSource(final CandidateItemSession candidateItemSession)
            throws CandidatePrivilegeException {
        final ItemDeliverySettings itemDeliverySettings = candidateItemSession.getItemDelivery().getItemDeliverySettings();
        if (!itemDeliverySettings.isAllowSource()) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.VIEW_ASSESSMENT_SOURCE);
        }
    }

    //----------------------------------------------------
    // Candidate Result access

    public void streamItemResult(final long xid, final String sessionHash, final OutputStream outputStream)
            throws CandidatePrivilegeException, DomainEntityNotFoundException {
        Assert.ensureNotNull(outputStream, "outputStream");
        final CandidateItemSession candidateItemSession = lookupCandidateSession(xid, sessionHash);
        streamItemResult(candidateItemSession, outputStream);
    }

    public void streamItemResult(final CandidateItemSession candidateItemSession, final OutputStream outputStream)
            throws CandidatePrivilegeException {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");
        Assert.ensureNotNull(outputStream, "outputStream");

        /* Forbid results if the candidate session is closed */
        ensureSessionNotTerminated(candidateItemSession);

        /* Make sure candidate is actually allowed to get results for this delivery */
        ensureCallerMayViewResult(candidateItemSession);

        /* Get current state */
        final CandidateItemEvent mostRecentEvent = candidateDataServices.getMostRecentEvent(candidateItemSession);

        /* Generate result Object from state */
        final ItemSessionController itemSessionController = candidateDataServices.createItemSessionController(mostRecentEvent);
        final ItemResult itemResult = itemSessionController.computeItemResult();

        /* Send result */
        qtiSerializer.serializeJqtiObject(itemResult, outputStream);
        auditor.recordEvent("Candidate streamed result for session #" + candidateItemSession.getId()
                + " on delivery #" + candidateItemSession.getItemDelivery().getId());
    }

    private void ensureCallerMayViewResult(final CandidateItemSession candidateItemSession)
            throws CandidatePrivilegeException {
        final ItemDeliverySettings itemDeliverySettings = candidateItemSession.getItemDelivery().getItemDeliverySettings();
        if (!itemDeliverySettings.isAllowResult()) {
            throw new CandidatePrivilegeException(candidateItemSession, CandidatePrivilege.VIEW_ASSESSMENT_RESULT);
        }
    }

    //----------------------------------------------------
    // Utilities

    /**
     * Returns a List of IDs (xeid) of all {@link CandidateItemEvent}s in the given
     * {@link CandidateItemSession} that a candidate may play back.
     *
     * @param candidateItemSession
     * @return
     */
    private List<CandidateItemEvent> getPlaybackEvents(final CandidateItemSession candidateItemSession) {
        final List<CandidateItemEvent> events = candidateItemEventDao.getForSession(candidateItemSession);
        final List<CandidateItemEvent> result = new ArrayList<CandidateItemEvent>(events.size());
        for (final CandidateItemEvent event : events) {
            if (isCandidatePlaybackCapable(event)) {
                result.add(event);
            }
        }
        return result;
    }

    private boolean isCandidatePlaybackCapable(final CandidateItemEvent event) {
        final CandidateItemEventType eventType = event.getEventType();
        return eventType==CandidateItemEventType.ATTEMPT_VALID
                || eventType==CandidateItemEventType.ATTEMPT_INVALID
                || eventType==CandidateItemEventType.ATTEMPT_BAD
                || eventType==CandidateItemEventType.INIT
                || eventType==CandidateItemEventType.REINIT
                || eventType==CandidateItemEventType.RESET;
    }
}
