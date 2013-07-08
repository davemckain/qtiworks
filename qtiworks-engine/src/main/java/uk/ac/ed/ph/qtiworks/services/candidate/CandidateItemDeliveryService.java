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
package uk.ac.ed.ph.qtiworks.services.candidate;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateFileSubmission;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateResponse;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.ResponseLegality;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.AssessmentDataService;
import uk.ac.ed.ph.qtiworks.services.CandidateAuditLogger;
import uk.ac.ed.ph.qtiworks.services.CandidateDataService;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionCloser;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;
import uk.ac.ed.ph.qtiworks.services.base.IdentityService;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateResponseDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;

import uk.ac.ed.ph.jqtiplus.exception.QtiCandidateStateException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.FileResponseData;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for the real-time control of a standalone {@link AssessmentItem}
 * to a candidate.
 * <p>
 * NOTE: Remember there is no {@link IdentityService} for candidates.
 *
 * @see CandidateSessionStarter
 * @see CandidateRenderingService
 * @see CandidateTestDeliveryService
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class CandidateItemDeliveryService {

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private AssessmentDataService assessmentDataService;

    @Resource
    private CandidateAuditLogger candidateAuditLogger;

    @Resource
    private CandidateSessionCloser candidateSessionCloser;

    @Resource
    private CandidateDataService candidateDataService;

    @Resource
    private CandidateUploadService candidateUploadService;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    @Resource
    private CandidateResponseDao candidateResponseDao;

    //----------------------------------------------------
    // Session access

    /**
     * Looks up the {@link CandidateSession} having the given ID (xid)
     * and checks the given sessionToken against that stored in the session as a means of
     * "authentication".
     *
     * @param xid
     *
     * @throws DomainEntityNotFoundException
     * @throws CandidateForbiddenException
     */
    public CandidateSession lookupCandidateItemSession(final long xid, final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException {
        Assert.notNull(sessionToken, "sessionToken");
        final CandidateSession candidateSession = candidateSessionDao.requireFindById(xid);
        if (!sessionToken.equals(candidateSession.getSessionToken())) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.ACCESS_CANDIDATE_SESSION);
        }
        if (candidateSession.getDelivery().getAssessment().getAssessmentType()!=AssessmentObjectType.ASSESSMENT_ITEM) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.ACCESS_CANDIDATE_SESSION_AS_ITEM);
        }
        return candidateSession;
    }

    private void ensureSessionNotTerminated(final CandidateSession candidateSession)
            throws CandidateSessionTerminatedException {
        if (candidateSession.isTerminated()) {
            /* No access when session has been terminated */
            candidateAuditLogger.logTerminated(candidateSession);
        }
    }

    //----------------------------------------------------
    // Response handling

    public CandidateSession handleResponses(final long xid, final String sessionToken,
            final Map<Identifier, StringResponseData> stringResponseMap,
            final Map<Identifier, MultipartFile> fileResponseMap,
            final String candidateComment)
            throws CandidateForbiddenException, DomainEntityNotFoundException, CandidateSessionTerminatedException {
        final CandidateSession candidateSession = lookupCandidateItemSession(xid, sessionToken);
        return handleResponses(candidateSession, stringResponseMap, fileResponseMap, candidateComment);
    }

    /**
     * @param candidateComment optional candidate comment, or null if no comment has been sent
     * @return
     */
    public CandidateSession handleResponses(final CandidateSession candidateSession,
            final Map<Identifier, StringResponseData> stringResponseMap,
            final Map<Identifier, MultipartFile> fileResponseMap,
            final String candidateComment)
            throws CandidateForbiddenException, CandidateSessionTerminatedException {
        Assert.notNull(candidateSession, "candidateSession");
        ensureSessionNotTerminated(candidateSession);

        /* Retrieve current JQTI state and set up JQTI controller */
        final CandidateEvent mostRecentEvent = candidateDataService.getMostRecentEvent(candidateSession);
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final ItemSessionController itemSessionController = candidateDataService.createItemSessionController(mostRecentEvent, notificationRecorder);
        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();

        /* Make sure an attempt is allowed */
        if (itemSessionState.isEnded()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.MAKE_RESPONSES);
            return null;
        }

        /* Make sure candidate may comment (if set) */
        final User candidate = candidateSession.getCandidate();
        final Delivery delivery = candidateSession.getDelivery();
        final ItemDeliverySettings itemDeliverySettings = (ItemDeliverySettings) assessmentDataService.getEffectiveDeliverySettings(candidate, delivery);
        if (candidateComment!=null && !itemDeliverySettings.isAllowCandidateComment()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.SUBMIT_COMMENT);
            return null;
        }

        /* Build response map in required format for JQTI+.
         * NB: The following doesn't test for duplicate keys in the two maps. I'm not sure
         * it's worth the effort.
         */
        final Map<Identifier, ResponseData> responseDataMap = new HashMap<Identifier, ResponseData>();
        if (stringResponseMap!=null) {
            for (final Entry<Identifier, StringResponseData> stringResponseEntry : stringResponseMap.entrySet()) {
                final Identifier identifier = stringResponseEntry.getKey();
                final StringResponseData stringResponseData = stringResponseEntry.getValue();
                responseDataMap.put(identifier, stringResponseData);
            }
        }
        final Map<Identifier, CandidateFileSubmission> fileSubmissionMap = new HashMap<Identifier, CandidateFileSubmission>();
        if (fileResponseMap!=null) {
            for (final Entry<Identifier, MultipartFile> fileResponseEntry : fileResponseMap.entrySet()) {
                final Identifier identifier = fileResponseEntry.getKey();
                final MultipartFile multipartFile = fileResponseEntry.getValue();
                if (!multipartFile.isEmpty()) {
                    final CandidateFileSubmission fileSubmission = candidateUploadService.importFileSubmission(candidateSession, multipartFile);
                    final FileResponseData fileResponseData = new FileResponseData(new File(fileSubmission.getStoredFilePath()), fileSubmission.getContentType(), fileSubmission.getFileName());
                    responseDataMap.put(identifier, fileResponseData);
                    fileSubmissionMap.put(identifier, fileSubmission);
                }
            }
        }

        /* Build Map of responses in appropriate entity form.
         * NB: Not ready for persisting yet. */
        final Map<Identifier, CandidateResponse> candidateResponseMap = new HashMap<Identifier, CandidateResponse>();
        for (final Entry<Identifier, ResponseData> responseEntry : responseDataMap.entrySet()) {
            final Identifier responseIdentifier = responseEntry.getKey();
            final ResponseData responseData = responseEntry.getValue();

            final CandidateResponse candidateResponse = new CandidateResponse();
            candidateResponse.setResponseIdentifier(responseIdentifier.toString());
            candidateResponse.setResponseDataType(responseData.getType());
            candidateResponse.setResponseLegality(ResponseLegality.VALID); /* (May change this below) */
            switch (responseData.getType()) {
                case STRING:
                    candidateResponse.setStringResponseData(((StringResponseData) responseData).getResponseData());
                    break;

                case FILE:
                    candidateResponse.setFileSubmission(fileSubmissionMap.get(responseIdentifier));
                    break;

                default:
                    throw new QtiWorksLogicException("Unexpected switch case: " + responseData.getType());
            }
            candidateResponseMap.put(responseIdentifier, candidateResponse);
        }

        /* Submit comment (if provided)
         * NB: Do this first in case next actions end the item session.
         */
        final Date timestamp = requestTimestampContext.getCurrentRequestTimestamp();
        if (candidateComment!=null) {
            try {
                itemSessionController.setCandidateComment(timestamp, candidateComment);
            }
            catch (final QtiCandidateStateException e) {
                candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.SUBMIT_COMMENT);
                return null;
            }
            catch (final RuntimeException e) {
                return handleExplosion(candidateSession);
            }
        }

        /* Attempt to bind responses */
        boolean allResponsesValid = false, allResponsesBound = false;
        try {
            itemSessionController.bindResponses(timestamp, responseDataMap);

            /* Note any responses that failed to bind */
            final Set<Identifier> badResponseIdentifiers = itemSessionState.getUnboundResponseIdentifiers();
            allResponsesBound = badResponseIdentifiers.isEmpty();
            for (final Identifier badResponseIdentifier : badResponseIdentifiers) {
                candidateResponseMap.get(badResponseIdentifier).setResponseLegality(ResponseLegality.BAD);
            }

            /* Now validate the responses according to any constraints specified by the interactions */
            if (allResponsesBound) {
                final Set<Identifier> invalidResponseIdentifiers = itemSessionState.getInvalidResponseIdentifiers();
                allResponsesValid = invalidResponseIdentifiers.isEmpty();
                if (!allResponsesValid) {
                    /* Some responses not valid, so note these down */
                    for (final Identifier invalidResponseIdentifier : invalidResponseIdentifiers) {
                        candidateResponseMap.get(invalidResponseIdentifier).setResponseLegality(ResponseLegality.INVALID);
                    }
                }
            }

            /* (We commit responses immediately here) */
            itemSessionController.commitResponses(timestamp);

            /* Invoke response processing (only if responses are valid) */
            if (allResponsesValid) {
                itemSessionController.performResponseProcessing(timestamp);
            }
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.MAKE_RESPONSES);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(candidateSession);
        }

        /* Record resulting attempt and event */
        final CandidateItemEventType eventType = allResponsesBound ?
            (allResponsesValid ? CandidateItemEventType.ATTEMPT_VALID : CandidateItemEventType.RESPONSE_INVALID)
            : CandidateItemEventType.RESPONSE_BAD;
        final CandidateEvent candidateEvent = candidateDataService.recordCandidateItemEvent(candidateSession,
                eventType, itemSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        /* Link and persist CandidateResponse entities */
        for (final CandidateResponse candidateResponse : candidateResponseMap.values()) {
            candidateResponse.setCandidateEvent(candidateEvent);
            candidateResponseDao.persist(candidateResponse);
        }

        /* Record current result state, or close session */
        if (itemSessionState.isEnded()) {
            candidateSessionCloser.closeCandidateItemSession(candidateSession, itemSessionController);
        }
        else {
            candidateDataService.computeAndRecordItemAssessmentResult(candidateSession, itemSessionController);
        }

        return candidateSession;
    }

    //----------------------------------------------------
    // Session end/close (by candidate)

    /**
     * Ends/closes the {@link CandidateSession} having the given ID (xid), moving it
     * into ended state.
     */
    public CandidateSession endCandidateSession(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException, CandidateSessionTerminatedException {
        final CandidateSession candidateSession = lookupCandidateItemSession(xid, sessionToken);
        return endCandidateSession(candidateSession);
    }

    public CandidateSession endCandidateSession(final CandidateSession candidateSession)
            throws CandidateForbiddenException, CandidateSessionTerminatedException {
        Assert.notNull(candidateSession, "candidateSession");
        ensureSessionNotTerminated(candidateSession);

        /* Retrieve current JQTI state and set up JQTI controller */
        final CandidateEvent mostRecentEvent = candidateDataService.getMostRecentEvent(candidateSession);
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final ItemSessionController itemSessionController = candidateDataService.createItemSessionController(mostRecentEvent, notificationRecorder);
        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();

        /* Check this is allowed in current state */
        final User candidate = candidateSession.getCandidate();
        final Delivery delivery = candidateSession.getDelivery();
        final ItemDeliverySettings itemDeliverySettings = (ItemDeliverySettings) assessmentDataService.getEffectiveDeliverySettings(candidate, delivery);
        if (itemSessionState.isEnded()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.END_SESSION_WHEN_ENDED);
            return null;
        }
        else if (!itemDeliverySettings.isAllowEnd()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.END_SESSION_WHEN_INTERACTING);
            return null;
        }

        /* Update state */
        final Date timestamp = requestTimestampContext.getCurrentRequestTimestamp();
        try {
            itemSessionController.endItem(timestamp);
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndForbid(candidateSession, itemSessionState.isEnded() ? CandidatePrivilege.END_SESSION_WHEN_ENDED : CandidatePrivilege.END_SESSION_WHEN_INTERACTING);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(candidateSession);
        }

        /* Record and log event */
        final CandidateEvent candidateEvent = candidateDataService.recordCandidateItemEvent(candidateSession,
                CandidateItemEventType.END, itemSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        /* Close session and record result */
        candidateSessionCloser.closeCandidateItemSession(candidateSession, itemSessionController);

        return candidateSession;
    }

    //----------------------------------------------------
    // Session hard reset

    /**
     * Performs a hard reset on the {@link CandidateSession} having the given ID (xid), returning the
     * updated {@link CandidateSession}.
     *
     * @see ItemSessionController#resetItemSessionHard(Date, boolean)
     */
    public CandidateSession resetCandidateSessionHard(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException, CandidateSessionTerminatedException {
        final CandidateSession candidateSession = lookupCandidateItemSession(xid, sessionToken);
        return resetCandidateSessionHard(candidateSession);
    }

    public CandidateSession resetCandidateSessionHard(final CandidateSession candidateSession)
            throws CandidateForbiddenException, CandidateSessionTerminatedException {
        Assert.notNull(candidateSession, "candidateSession");
        ensureSessionNotTerminated(candidateSession);

        /* Retrieve current JQTI state and set up JQTI controller */
        final CandidateEvent mostRecentEvent = candidateDataService.getMostRecentEvent(candidateSession);
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final ItemSessionController itemSessionController = candidateDataService.createItemSessionController(mostRecentEvent, notificationRecorder);
        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();

        final User candidate = candidateSession.getCandidate();
        final Delivery delivery = candidateSession.getDelivery();
        final ItemDeliverySettings itemDeliverySettings = (ItemDeliverySettings) assessmentDataService.getEffectiveDeliverySettings(candidate, delivery);
        if (!itemSessionState.isEnded() && !itemDeliverySettings.isAllowHardResetWhenOpen()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.HARD_RESET_SESSION_WHEN_INTERACTING);
            return null;
        }
        else if (itemSessionState.isEnded() && !itemDeliverySettings.isAllowHardResetWhenEnded()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.HARD_RESET_SESSION_WHEN_ENDED);
            return null;
        }

        /* Update state */
        final Date timestamp = requestTimestampContext.getCurrentRequestTimestamp();
        try {
            itemSessionController.resetItemSessionHard(timestamp, true);
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndForbid(candidateSession, itemSessionState.isEnded() ? CandidatePrivilege.HARD_RESET_SESSION_WHEN_ENDED : CandidatePrivilege.HARD_RESET_SESSION_WHEN_INTERACTING);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(candidateSession);
        }

        /* Record and log event */
        final CandidateEvent candidateEvent = candidateDataService.recordCandidateItemEvent(candidateSession,
                CandidateItemEventType.REINIT, itemSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        /* Record current result state, or close session */
        if (itemSessionState.isEnded()) {
            candidateSessionCloser.closeCandidateItemSession(candidateSession, itemSessionController);
        }
        else {
            candidateDataService.computeAndRecordItemAssessmentResult(candidateSession, itemSessionController);
        }

        return candidateSession;
    }

    //----------------------------------------------------
    // Session soft reset

    /**
     * Performs a soft reset on the {@link CandidateSession} having the given ID (xid), returning the
     * updated {@link CandidateSession}.
     *
     * @see ItemSessionController#resetItemSessionSoft(Date, boolean)
     */
    public CandidateSession resetCandidateSessionSoft(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException, CandidateSessionTerminatedException {
        final CandidateSession candidateSession = lookupCandidateItemSession(xid, sessionToken);
        return resetCandidateSessionSoft(candidateSession);
    }

    public CandidateSession resetCandidateSessionSoft(final CandidateSession candidateSession)
            throws CandidateForbiddenException, CandidateSessionTerminatedException {
        Assert.notNull(candidateSession, "candidateSession");
        ensureSessionNotTerminated(candidateSession);

        /* Retrieve current JQTI state and set up JQTI controller */
        final CandidateEvent mostRecentEvent = candidateDataService.getMostRecentEvent(candidateSession);
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final ItemSessionController itemSessionController = candidateDataService.createItemSessionController(mostRecentEvent, notificationRecorder);
        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();

        /* Make sure caller may reset the session */
        final User candidate = candidateSession.getCandidate();
        final Delivery delivery = candidateSession.getDelivery();
        final ItemDeliverySettings itemDeliverySettings = (ItemDeliverySettings) assessmentDataService.getEffectiveDeliverySettings(candidate, delivery);
        if (!itemSessionState.isEnded() && !itemDeliverySettings.isAllowSoftResetWhenOpen()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.SOFT_RESET_SESSION_WHEN_INTERACTING);
            return null;
        }
        else if (itemSessionState.isEnded() && !itemDeliverySettings.isAllowSoftResetWhenEnded()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.SOFT_RESET_SESSION_WHEN_ENDED);
            return null;
        }

        /* Update state */
        final Date timestamp = requestTimestampContext.getCurrentRequestTimestamp();
        try {
            itemSessionController.resetItemSessionSoft(timestamp, true);
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndForbid(candidateSession, itemSessionState.isEnded() ? CandidatePrivilege.SOFT_RESET_SESSION_WHEN_ENDED : CandidatePrivilege.SOFT_RESET_SESSION_WHEN_INTERACTING);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(candidateSession);
        }

        /* Record and log event */
        final CandidateEvent candidateEvent = candidateDataService.recordCandidateItemEvent(candidateSession, CandidateItemEventType.RESET, itemSessionState);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        /* Record current result state, or close session */
        if (itemSessionState.isEnded()) {
            candidateSessionCloser.closeCandidateItemSession(candidateSession, itemSessionController);
        }
        else {
            candidateDataService.computeAndRecordItemAssessmentResult(candidateSession, itemSessionController);
        }

        return candidateSession;
    }

    //----------------------------------------------------
    // Solution request

    /**
     * Logs a {@link CandidateItemEventType#SOLUTION} event, closing the item session if it hasn't
     * already been closed (and if this is allowed).
     */
    public CandidateSession requestSolution(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException, CandidateSessionTerminatedException {
        final CandidateSession candidateSession = lookupCandidateItemSession(xid, sessionToken);
        return requestSolution(candidateSession);
    }

    public CandidateSession requestSolution(final CandidateSession candidateSession)
            throws CandidateForbiddenException, CandidateSessionTerminatedException {
        Assert.notNull(candidateSession, "candidateSession");
        ensureSessionNotTerminated(candidateSession);

        /* Retrieve current JQTI state and set up JQTI controller */
        final CandidateEvent mostRecentEvent = candidateDataService.getMostRecentEvent(candidateSession);
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final ItemSessionController itemSessionController = candidateDataService.createItemSessionController(mostRecentEvent, notificationRecorder);
        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();

        /* Make sure caller may do this */
        final User candidate = candidateSession.getCandidate();
        final Delivery delivery = candidateSession.getDelivery();
        final ItemDeliverySettings itemDeliverySettings = (ItemDeliverySettings) assessmentDataService.getEffectiveDeliverySettings(candidate, delivery);
        if (!itemSessionState.isEnded() && !itemDeliverySettings.isAllowSolutionWhenOpen()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.SOLUTION_WHEN_INTERACTING);
            return null;
        }
        else if (itemSessionState.isEnded() && !itemDeliverySettings.isAllowSoftResetWhenEnded()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.SOLUTION_WHEN_ENDED);
            return null;
        }

        /* Ennd session if required */
        final Date timestamp = requestTimestampContext.getCurrentRequestTimestamp();
        boolean isEndingSession = false;
        if (!itemSessionState.isEnded()) {
            isEndingSession = true;
            try {
                itemSessionController.endItem(timestamp);
            }
            catch (final QtiCandidateStateException e) {
                candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.SOLUTION_WHEN_ENDED);
                return null;
            }
            catch (final RuntimeException e) {
                return handleExplosion(candidateSession);
            }
        }

        /* Record and log event */
        final CandidateEvent candidateEvent = candidateDataService.recordCandidateItemEvent(candidateSession, CandidateItemEventType.SOLUTION, itemSessionState);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        /* Record current result state, or close session */
        if (isEndingSession) {
            candidateSessionCloser.closeCandidateItemSession(candidateSession, itemSessionController);
        }
        else {
            candidateDataService.computeAndRecordItemAssessmentResult(candidateSession, itemSessionController);
        }
        return candidateSession;
    }

    //----------------------------------------------------
    // Session termination (by candidate)

    /**
     * Exits/terminates the {@link CandidateSession} having the given ID (xid).
     * <p>
     * Currently we're always allowing this action to be made when in
     * interacting or closed states.
     */
    public CandidateSession exitCandidateSession(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException, CandidateSessionTerminatedException {
        final CandidateSession candidateSession = lookupCandidateItemSession(xid, sessionToken);
        return exitCandidateSession(candidateSession);
    }

    public CandidateSession exitCandidateSession(final CandidateSession candidateSession)
            throws CandidateSessionTerminatedException {
        Assert.notNull(candidateSession, "candidateSession");
        ensureSessionNotTerminated(candidateSession);

        /* Retrieve current JQTI state and set up JQTI controller */
        final CandidateEvent mostRecentEvent = candidateDataService.getMostRecentEvent(candidateSession);
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final ItemSessionController itemSessionController = candidateDataService.createItemSessionController(mostRecentEvent, notificationRecorder);
        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();

        /* Are we terminating a session that hasn't already been ended? If so end the session and record final result. */
        if (!itemSessionState.isEnded()) {
            final Date timestamp = requestTimestampContext.getCurrentRequestTimestamp();
            try {
                itemSessionController.endItem(timestamp);
            }
            catch (final RuntimeException e) {
                return handleExplosion(candidateSession);
            }
            candidateSessionCloser.closeCandidateItemSession(candidateSession, itemSessionController);
        }

        /* Record and log event */
        final CandidateEvent candidateEvent = candidateDataService.recordCandidateItemEvent(candidateSession,
                CandidateItemEventType.EXIT, itemSessionState);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        /* Update session entity */
        candidateSession.setTerminated(true);
        candidateSessionDao.update(candidateSession);

        return candidateSession;
    }

    //----------------------------------------------------

    private CandidateSession handleExplosion(final CandidateSession candidateSession) {
        candidateSession.setExploded(true);
        candidateSession.setTerminated(true);
        candidateAuditLogger.logExplosion(candidateSession);
        candidateSessionDao.update(candidateSession);
        return candidateSession;
    }
}
