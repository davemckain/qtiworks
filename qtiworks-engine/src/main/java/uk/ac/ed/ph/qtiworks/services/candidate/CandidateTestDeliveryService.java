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
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateFileSubmission;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateResponse;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateTestEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.ResponseLegality;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionFinisher;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;
import uk.ac.ed.ph.qtiworks.services.IdentityService;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateResponseDao;

import uk.ac.ed.ph.jqtiplus.exception.QtiCandidateStateException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.SubmissionMode;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
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
 * Service for the real-time control of an {@link AssessmentTest}
 * <p>
 * NOTE: Remember there is no {@link IdentityService} for candidates.
 *
 * @see CandidateSessionStarter
 * @see CandidateRenderingService
 * @see CandidateItemDeliveryService
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class CandidateTestDeliveryService extends CandidateServiceBase {

    @Resource
    private CandidateSessionFinisher candidateSessionFinisher;

    @Resource
    private CandidateUploadService candidateUploadService;

    @Resource
    private CandidateResponseDao candidateResponseDao;

    //----------------------------------------------------
    // Session entry

    public CandidateSession enterOrReenterCandidateSession(final CandidateSession candidateSession)
            throws CandidateException {
        Assert.notNull(candidateSession, "candidateSession");
        assertSessionType(candidateSession, AssessmentObjectType.ASSESSMENT_TEST);

        final CandidateEvent mostRecentEvent = candidateDataService.getMostRecentEvent(candidateSession);
        if (mostRecentEvent==null && !candidateSession.isTerminated()) {
            enterCandidateSession(candidateSession);
        }
        return candidateSession;
    }

    private CandidateSession enterCandidateSession(final CandidateSession candidateSession) {
        final User candidate = candidateSession.getCandidate();
        final Delivery delivery = candidateSession.getDelivery();

        /* Set up listener to record any notifications */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);

        /* Create fresh JQTI+ state & controller for it */
        final TestSessionController testSessionController = candidateDataService.createNewTestSessionStateAndController(candidate, delivery, notificationRecorder);
        if (testSessionController==null) {
            return handleExplosion(null, candidateSession);
        }

        /* Initialise test state and enter test */
        final TestSessionState testSessionState = testSessionController.getTestSessionState();
        final Date timestamp = requestTimestampContext.getCurrentRequestTimestamp();
        try {
            testSessionController.initialize(timestamp);
            final int testPartCount = testSessionController.enterTest(timestamp);
            if (testPartCount==1) {
                /* If there is only testPart, then enter this (if possible).
                 * (Note that this may cause the test to exit immediately if there is a failed
                 * preCondition on this part.)
                 */
                testSessionController.enterNextAvailableTestPart(timestamp);
            }
            else {
                /* Don't enter first testPart yet - we shall tell candidate that
                 * there are multiple parts and let them enter manually.
                 */
            }
        }
        catch (final RuntimeException e) {
            return handleExplosion(e, candidateSession);
        }

        /* Record and log event */
        final CandidateEvent candidateEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.ENTER_TEST, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        /* Record current result state */
        final AssessmentResult assessmentResult = candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);

        /* Handle immediate end of test session */
        if (testSessionState.isEnded()) {
            candidateSessionFinisher.finishCandidateSession(candidateSession, assessmentResult);
        }

        return candidateSession;
    }

    //----------------------------------------------------
    // Response handling

    public CandidateSession handleResponses(final CandidateSession candidateSession,
            final Map<Identifier, StringResponseData> stringResponseMap,
            final Map<Identifier, MultipartFile> fileResponseMap,
            final String candidateComment)
            throws CandidateException {
        Assert.notNull(candidateSession, "candidateSession");
        assertSessionType(candidateSession, AssessmentObjectType.ASSESSMENT_TEST);
        assertSessionNotTerminated(candidateSession);

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = assertSessionEntered(candidateSession);
        final TestSessionController testSessionController = candidateDataService.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* FIXME: Next wodge of code has some cut & paste! */

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
         * NB: Not ready for persisting yet.
         */
        final Map<Identifier, CandidateResponse> candidateResponseMap = new HashMap<Identifier, CandidateResponse>();
        for (final Entry<Identifier, ResponseData> responseEntry : responseDataMap.entrySet()) {
            final Identifier responseIdentifier = responseEntry.getKey();
            final ResponseData responseData = responseEntry.getValue();

            final CandidateResponse candidateItemResponse = new CandidateResponse();
            candidateItemResponse.setResponseIdentifier(responseIdentifier.toString());
            candidateItemResponse.setResponseDataType(responseData.getType());
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
            candidateResponseMap.put(responseIdentifier, candidateItemResponse);
        }

        try {
            /* Submit comment (if provided).
             * NB: Need to do this first in case later response handling ends the item session.
             */
            final Date timestamp = requestTimestampContext.getCurrentRequestTimestamp();
            if (candidateComment!=null) {
                testSessionController.setCandidateCommentForCurrentItem(timestamp, candidateComment);
            }

            /* Attempt to bind responses (and maybe perform RP & OP) */
            testSessionController.handleResponsesToCurrentItem(timestamp, responseDataMap);
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.RESPONSES_NOT_EXPECTED);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(e, candidateSession);
        }

        /* Note any responses that failed to bind */
        final ItemSessionState itemSessionState = testSessionState.getCurrentItemSessionState();
        final Set<Identifier> badResponseIdentifiers = itemSessionState.getUnboundResponseIdentifiers();
        final boolean allResponsesBound = badResponseIdentifiers.isEmpty();
        for (final Identifier badResponseIdentifier : badResponseIdentifiers) {
            candidateResponseMap.get(badResponseIdentifier).setResponseLegality(ResponseLegality.BAD);
        }

        /* Now validate the responses according to any constraints specified by the interactions */
        boolean allResponsesValid = false;
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

        /* Classify this event */
        final SubmissionMode submissionMode = testSessionController.getCurrentTestPart().getSubmissionMode();
        final CandidateItemEventType candidateItemEventType;
        if (allResponsesValid) {
            candidateItemEventType = submissionMode==SubmissionMode.INDIVIDUAL ? CandidateItemEventType.ATTEMPT_VALID : CandidateItemEventType.RESPONSE_VALID;
        }
        else {
            candidateItemEventType = allResponsesBound ? CandidateItemEventType.RESPONSE_INVALID : CandidateItemEventType.RESPONSE_BAD;
        }

        /* Record resulting event */
        final CandidateEvent candidateEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.ITEM_EVENT, candidateItemEventType, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        /* Persist CandidateResponse entities */
        for (final CandidateResponse candidateResponse : candidateResponseMap.values()) {
            candidateResponse.setCandidateEvent(candidateEvent);
            candidateResponseDao.persist(candidateResponse);
        }

        /* Record current result state */
        candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);

        /* Save any change to session state */
        candidateSessionDao.update(candidateSession);
        return candidateSession;
    }

    //----------------------------------------------------
    // Navigation

    public CandidateSession selectNavigationMenu(final CandidateSession candidateSession)
            throws CandidateException {
        Assert.notNull(candidateSession, "candidateSession");
        assertSessionType(candidateSession, AssessmentObjectType.ASSESSMENT_TEST);
        assertSessionNotTerminated(candidateSession);

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = assertSessionEntered(candidateSession);
        final TestSessionController testSessionController = candidateDataService.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        try {
            /* Perform action */
            final Date requestTimestamp = requestTimestampContext.getCurrentRequestTimestamp();
            testSessionController.selectItemNonlinear(requestTimestamp, null);
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_SELECT_NONLINEAR_MENU);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(e, candidateSession);
        }

        /* Record current result state */
        candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);

        /* Record and log event */
        final CandidateEvent candidateEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.SELECT_MENU, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        return candidateSession;
    }

    public CandidateSession selectNonlinearItem(final CandidateSession candidateSession, final TestPlanNodeKey itemKey)
            throws CandidateException {
        Assert.notNull(candidateSession, "candidateSession");
        assertSessionType(candidateSession, AssessmentObjectType.ASSESSMENT_TEST);
        assertSessionNotTerminated(candidateSession);

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = assertSessionEntered(candidateSession);
        final TestSessionController testSessionController = candidateDataService.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        try {
            /* Perform action */
            final Date requestTimestamp = requestTimestampContext.getCurrentRequestTimestamp();
            testSessionController.selectItemNonlinear(requestTimestamp, itemKey);
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_SELECT_NONLINEAR_TEST_ITEM);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(e, candidateSession);
        }

        /* Record current result state */
        candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.SELECT_ITEM, null, itemKey, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    public CandidateSession finishLinearItem(final CandidateSession candidateSession)
            throws CandidateException {
        Assert.notNull(candidateSession, "candidateSession");
        assertSessionType(candidateSession, AssessmentObjectType.ASSESSMENT_TEST);

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = assertSessionEntered(candidateSession);
        final TestSessionController testSessionController = candidateDataService.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        assertSessionNotTerminated(candidateSession);
        try {
            if (!testSessionController.mayAdvanceItemLinear()) {
                candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_FINISH_LINEAR_TEST_ITEM);
                return null;
            }
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_FINISH_LINEAR_TEST_ITEM);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(e, candidateSession);
        }

        /* Update state */
        final Date requestTimestamp = requestTimestampContext.getCurrentRequestTimestamp();
        final TestPlanNode nextItemNode = testSessionController.advanceItemLinear(requestTimestamp);

        /* Record current result state */
        final AssessmentResult assessmentResult = candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);

        /* If we ended the testPart and there are now no more available testParts, then finish the session now */
        if (nextItemNode==null && testSessionController.findNextEnterableTestPart()==null) {
            candidateSessionFinisher.finishCandidateSession(candidateSession, assessmentResult);
        }

        /* Record and log event */
        final CandidateTestEventType eventType = nextItemNode!=null ? CandidateTestEventType.FINISH_ITEM : CandidateTestEventType.FINISH_FINAL_ITEM;
        final CandidateEvent candidateTestEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                eventType, null, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    public CandidateSession endCurrentTestPart(final CandidateSession candidateSession)
            throws CandidateException {
        Assert.notNull(candidateSession, "candidateSession");
        assertSessionType(candidateSession, AssessmentObjectType.ASSESSMENT_TEST);

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = assertSessionEntered(candidateSession);
        final TestSessionController testSessionController = candidateDataService.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        assertSessionNotTerminated(candidateSession);
        try {
            if (!testSessionController.mayEndCurrentTestPart()) {
                candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_END_TEST_PART);
                return null;
            }
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_END_TEST_PART);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(e, candidateSession);
        }

        /* Update state */
        final Date requestTimestamp = requestTimestampContext.getCurrentRequestTimestamp();
        testSessionController.endCurrentTestPart(requestTimestamp);

        /* Record current result */
        final AssessmentResult assessmentResult = candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);

        /* If there are now no more available testParts, finish the session now */
        if (testSessionController.findNextEnterableTestPart()==null) {
            candidateSessionFinisher.finishCandidateSession(candidateSession, assessmentResult);
        }

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.END_TEST_PART, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    //----------------------------------------------------
    // Review

    public CandidateSession reviewTestPart(final CandidateSession candidateSession)
            throws CandidateException {
        Assert.notNull(candidateSession, "candidateSession");
        assertSessionType(candidateSession, AssessmentObjectType.ASSESSMENT_TEST);

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = assertSessionEntered(candidateSession);
        final TestSessionController testSessionController = candidateDataService.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        assertSessionNotTerminated(candidateSession);
        if (testSessionState.getCurrentTestPartKey()==null || !testSessionState.getCurrentTestPartSessionState().isEnded()) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_REVIEW_TEST_PART);
            return null;
        }

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.REVIEW_TEST_PART, null, null, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    public CandidateSession reviewItem(final CandidateSession candidateSession, final TestPlanNodeKey itemKey)
            throws CandidateException {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(itemKey, "itemKey");
        assertSessionType(candidateSession, AssessmentObjectType.ASSESSMENT_TEST);

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = assertSessionEntered(candidateSession);
        final TestSessionController testSessionController = candidateDataService.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        assertSessionNotTerminated(candidateSession);
        try {
            if (!testSessionController.mayReviewItem(itemKey)) {
                candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_REVIEW_TEST_ITEM);
                return null;
            }
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_REVIEW_TEST_ITEM);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(e, candidateSession);
        }

        /* Record current result state */
        candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.REVIEW_ITEM, null, itemKey, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    //----------------------------------------------------
    // Solution request

    public CandidateSession requestSolution(final CandidateSession candidateSession, final TestPlanNodeKey itemKey)
            throws CandidateException {
        Assert.notNull(candidateSession, "candidateSession");
        assertSessionType(candidateSession, AssessmentObjectType.ASSESSMENT_TEST);
        Assert.notNull(itemKey, "itemKey");

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = assertSessionEntered(candidateSession);
        final TestSessionController testSessionController = candidateDataService.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        assertSessionNotTerminated(candidateSession);
        try {
            if (!testSessionController.mayAccessItemSolution(itemKey)) {
                candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_SOLUTION_TEST_ITEM);
                return null;
            }
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_SOLUTION_TEST_ITEM);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(e, candidateSession);
        }

        /* Record current result state */
        candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.SOLUTION_ITEM, null, itemKey, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    //----------------------------------------------------
    // Advance TestPart

    public CandidateSession advanceTestPart(final CandidateSession candidateSession)
            throws CandidateException {
        Assert.notNull(candidateSession, "candidateSession");
        assertSessionType(candidateSession, AssessmentObjectType.ASSESSMENT_TEST);

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = assertSessionEntered(candidateSession);
        final TestSessionController testSessionController = candidateDataService.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Perform action */
        final TestPlanNode nextTestPart;
        final Date currentTimestamp = requestTimestampContext.getCurrentRequestTimestamp();
        try {
            nextTestPart = testSessionController.enterNextAvailableTestPart(currentTimestamp);
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_ADVANCE_TEST_PART);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(e, candidateSession);
        }

        CandidateTestEventType eventType;
        if (nextTestPart!=null) {
            /* Moved into next test part */
            eventType = CandidateTestEventType.ADVANCE_TEST_PART;
        }
        else {
            /* No more test parts.
             *
             * For single part tests, we terminate the test completely now as the test feedback was shown with the testPart feedback.
             * For multi-part tests, we shall keep the test open so that the test feedback can be viewed.
             */
            if (testSessionState.getTestPlan().getTestPartNodes().size()==1) {
                eventType = CandidateTestEventType.EXIT_TEST;
                testSessionController.exitTest(currentTimestamp);
                candidateSession.setTerminationTime(currentTimestamp);
                candidateSessionDao.update(candidateSession);
            }
            else {
                eventType = CandidateTestEventType.ADVANCE_TEST_PART;
            }
        }

        /* Record current result state */
        candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                eventType, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    //----------------------------------------------------
    // Exit (multi-part) test

    public CandidateSession exitTest(final CandidateSession candidateSession)
            throws CandidateException {
        Assert.notNull(candidateSession, "candidateSession");
        assertSessionType(candidateSession, AssessmentObjectType.ASSESSMENT_TEST);

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = assertSessionEntered(candidateSession);
        final TestSessionController testSessionController = candidateDataService.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Perform action */
        final Date currentTimestamp = requestTimestampContext.getCurrentRequestTimestamp();
        try {
            testSessionController.exitTest(currentTimestamp);
        }
        catch (final QtiCandidateStateException e) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.CANNOT_EXIT_TEST);
            return null;
        }
        catch (final RuntimeException e) {
            return handleExplosion(e, candidateSession);
        }

        /* Update CandidateSession as appropriate */
        candidateSession.setTerminationTime(currentTimestamp);
        candidateSessionDao.update(candidateSession);

        /* Record current result state (final) */
        candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.EXIT_TEST, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }
}
