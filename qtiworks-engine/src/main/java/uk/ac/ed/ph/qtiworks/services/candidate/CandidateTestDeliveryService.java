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
package uk.ac.ed.ph.qtiworks.services.candidate;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateAttemptDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateAttempt;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEventNotification;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateFileSubmission;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateResponse;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateTestEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.ResponseLegality;
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
import uk.ac.ed.ph.qtiworks.rendering.AbstractRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.RenderingMode;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.TerminatedRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.TestFeedbackRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.TestItemRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.TestPartNavigationRenderingRequest;
import uk.ac.ed.ph.qtiworks.services.AssessmentPackageFileService;
import uk.ac.ed.ph.qtiworks.services.CandidateAuditLogger;
import uk.ac.ed.ph.qtiworks.services.CandidateDataServices;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;
import uk.ac.ed.ph.qtiworks.services.EntityGraphService;
import uk.ac.ed.ph.qtiworks.services.FilespaceManager;
import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
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
 * Service the manages the real-time delivery of a an {@link AssessmentTest}
 * to candidates.
 * <p>
 * NOTE: Remember there is no {@link IdentityContext} for candidates.
 *
 * CURRENTLY SUPPORTED:
 * - nonlinear navigation mode (menu, select item)
 * - simultaneous submission mode
 * - test "atEnd" feedback only
 *
 * STILL TO DO:
 * - linear navigation
 * - individual submission
 * - test part "atEnd" feedback
 * - test / test part "during" feedback
 * - skipping
 * - solutions
 * - branchRule/preCondition
 *
 * @author David McKain
 *
 * @see CandidateSessionStarter
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class CandidateTestDeliveryService {

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private CandidateAuditLogger candidateAuditLogger;

    @Resource
    private EntityGraphService entityGraphService;

    @Resource
    private AssessmentPackageFileService assessmentPackageFileService;

    @Resource
    private FilespaceManager filespaceManager;

    @Resource
    private CandidateUploadService candidateUploadService;

    @Resource
    private CandidateDataServices candidateDataServices;

    @Resource
    private AssessmentRenderer assessmentRenderer;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    @Resource
    private CandidateAttemptDao candidateAttemptDao;

    //----------------------------------------------------
    // Session access

    /**
     * Looks up the {@link CandidateSession} having the given ID (xid)
     * and checks the given sessionToken against that stored in the session as a means of
     * "authentication" and that
     *
     * @param xid
     * @return
     * @throws DomainEntityNotFoundException
     * @throws CandidateForbiddenException
     * @throws CandidateCandidatePrivilegeException
     */
    public CandidateSession lookupCandidateSession(final long xid, final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException {
        Assert.notNull(sessionToken, "sessionToken");
        final CandidateSession candidateSession = candidateSessionDao.requireFindById(xid);
        if (!sessionToken.equals(candidateSession.getSessionToken())) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.ACCESS_CANDIDATE_SESSION);
        }
        if (candidateSession.getDelivery().getAssessment().getAssessmentType()!=AssessmentObjectType.ASSESSMENT_TEST) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.ACCESS_CANDIDATE_SESSION_AS_TEST);
        }
        return candidateSession;
    }

    private void ensureSessionNotTerminated(final CandidateSession candidateSession) throws CandidateForbiddenException {
        if (candidateSession.isTerminated()) {
            /* No access when session has been is closed */
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.ACCESS_TERMINATED_SESSION);
        }
    }

    //----------------------------------------------------
    // Rendering

    /**
     * Renders the current state of the {@link CandidateSession} having
     * the given ID (xid).
     */
    public void renderCurrentState(final long xid, final String sessionToken,
            final RenderingOptions renderingOptions, final OutputStreamer outputStreamer)
            throws CandidateForbiddenException, DomainEntityNotFoundException, IOException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        renderCurrentState(candidateSession, renderingOptions, outputStreamer);
    }

    public void renderCurrentState(final CandidateSession candidateSession,
            final RenderingOptions renderingOptions,
            final OutputStreamer outputStreamer) throws IOException {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(renderingOptions, "renderingOptions");
        Assert.notNull(outputStreamer, "outputStreamer");

        /* Look up most recent event */
        final CandidateEvent latestEvent = candidateDataServices.getMostRecentTestEvent(candidateSession);

        /* Create temporary file to hold the output before it gets streamed */
        final File resultFile = filespaceManager.createTempFile();
        try {
            /* Render to temp file */
            FileOutputStream resultOutputStream = null;
            try {
                resultOutputStream = new FileOutputStream(resultFile);
                renderEvent(candidateSession, latestEvent, renderingOptions, resultOutputStream);
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

    private void renderEvent(final CandidateSession candidateSession, final CandidateEvent candidateEvent,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        if (candidateSession.isTerminated()) {
            /* Session is terminated */
            renderTerminated(candidateEvent, renderingOptions, resultStream);
        }
        else {
            switch (candidateEvent.getCategoryEventCategory()) {
                case TEST:
                    renderTestEvent(candidateEvent, renderingOptions, resultStream);
                    break;

                case ITEM:
                    throw new QtiWorksLogicException("Did not expect to get an event of category " + candidateEvent.getCategoryEventCategory()
                            + " within a test");

                default:
                    throw new QtiWorksLogicException("Unexpected logic branch. Event type " + candidateEvent.getCategoryEventCategory());
            }
        }
    }

    private void renderTerminated(final CandidateEvent candidateEvent,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
        final DeliverySettings deliverySettings = delivery.getDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);

        final TerminatedRenderingRequest renderingRequest = new TerminatedRenderingRequest();
        initBaseRenderingRequest(renderingRequest, assessmentPackage, deliverySettings, renderingOptions);

        assessmentRenderer.renderTeminated(renderingRequest, resultStream);
    }

    private void renderTestEvent(final CandidateEvent candidateEvent,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestSessionState testSessionState = candidateDataServices.loadTestSessionState(candidateEvent);
        final CandidateTestEventType testEventType = candidateEvent.getTestEventType();
        switch (testEventType) {
            case INIT:
                renderAfterTestInit(candidateEvent, testSessionState, renderingOptions, resultStream);
                break;

            case ITEM_EVENT:
                renderEventWhenItemSelected(candidateEvent, testSessionState, renderingOptions, resultStream);
                break;

            case SELECT_ITEM:
                renderAfterSelectItem(candidateEvent, testSessionState, renderingOptions, resultStream);
                break;

            case REVIEW_ITEM:
                renderItemReview(candidateEvent, testSessionState, renderingOptions, resultStream);
                break;

            case SELECT_MENU:
                renderTestPartNavigationMenu(candidateEvent, testSessionState, renderingOptions, resultStream);
                break;

            case END_TEST_PART:
                renderTestPartFeedback(candidateEvent, testSessionState, renderingOptions, resultStream);
                break;

            case EXIT_TEST_PART:
                /* FIXME: Currently EXIT_TEST_PART exits the test completely */
                throw new QtiWorksLogicException("Unimplemented");

            default:
                throw new QtiWorksLogicException("Unexpected logic branch. Event type " + testEventType);
        }
    }

    private void renderAfterTestInit(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        /* FIXME: Only supporting NONLINEAR so far, so only outcome is to show navigation menu */
        renderTestPartNavigationMenu(candidateEvent, testSessionState, renderingOptions, resultStream);
    }

    private void renderTestPartNavigationMenu(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState, final RenderingOptions renderingOptions,
            final OutputStream resultStream) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);

        /* Will need to query certain parts of state */
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(delivery,
                testSessionState, new NotificationRecorder(NotificationLevel.INFO));

        final TestPartNavigationRenderingRequest renderingRequest = new TestPartNavigationRenderingRequest();
        initBaseRenderingRequest(renderingRequest, assessmentPackage, testDeliverySettings, renderingOptions);
        renderingRequest.setEndTestPartAllowed(testSessionController.mayEndTestPart());
        renderingRequest.setTestSessionState(testSessionState);

        candidateAuditLogger.logTestPartNavigationRendering(candidateEvent);
        final List<CandidateEventNotification> notifications = candidateEvent.getNotifications();
        assessmentRenderer.renderTestPartNavigation(renderingRequest, notifications, resultStream);
    }

    /**
     * FIXME: Only supporting single part tests, so the only thing this will do is show the
     * feedback for the test as a whole.
     */
    private void renderTestPartFeedback(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);

        final TestFeedbackRenderingRequest renderingRequest = new TestFeedbackRenderingRequest();
        initBaseRenderingRequest(renderingRequest, assessmentPackage, testDeliverySettings, renderingOptions);
        renderingRequest.setTestSessionState(testSessionState);

        candidateAuditLogger.logTestFeedbackRendering(candidateEvent);
        final List<CandidateEventNotification> notifications = candidateEvent.getNotifications();
        assessmentRenderer.renderTestFeedback(renderingRequest, notifications, resultStream);
    }

    private void renderAfterSelectItem(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
        if (currentItemKey==null) {
            throw new QtiWorksLogicException("Did not expect currentItemKey==null");
        }
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(currentItemKey);
        if (itemSessionState.isClosed()) {
            /* Item session closed */
            renderClosed(candidateEvent, currentItemKey, testSessionState, itemSessionState, renderingOptions, resultStream);
        }
        else {
            /* Interacting */
            renderInteractingPresentation(candidateEvent, currentItemKey, testSessionState,
                    itemSessionState, renderingOptions, resultStream);
        }
    }

    private void renderEventWhenItemSelected(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
        if (currentItemKey==null) {
            throw new QtiWorksLogicException("Did not expect currentItemKey==null");
        }
        /* Item selected, so render current state of item */
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(currentItemKey);
        renderSelectedItem(candidateEvent, currentItemKey, testSessionState, itemSessionState, renderingOptions, resultStream);
    }

    private void renderSelectedItem(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        if (itemSessionState.isClosed()) {
            /* Item session closed */
            renderItemEventWhenClosed(candidateEvent, itemKey, testSessionState, itemSessionState, renderingOptions, resultStream);
        }
        else {
            /* Interacting */
            renderItemEventWhenInteracting(candidateEvent, itemKey, testSessionState, itemSessionState, renderingOptions, resultStream);
        }
    }

    private void renderItemEventWhenInteracting(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final CandidateItemEventType itemEventType = candidateEvent.getItemEventType();
        switch (itemEventType) {
            case ATTEMPT_VALID:
            case ATTEMPT_INVALID:
            case ATTEMPT_BAD:
                renderInteractingAfterAttempt(candidateEvent, itemKey, testSessionState, itemSessionState,
                        renderingOptions, resultStream);
                break;

            case CLOSE:
            case PLAYBACK:
            case SOLUTION:
            case REINIT:
            case RESET:
                throw new QtiWorksLogicException("The item event " + itemEventType + " is not yet supported within tests");

            case INIT:
                throw new QtiWorksLogicException("The item event " + itemEventType + " should not occur in tests");

            default:
                throw new QtiWorksLogicException("Unexpected switch case. Event type " + itemEventType);
        }
    }

    private void renderInteractingPresentation(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestItemRenderingRequest renderingRequest = initTestRenderingRequestWhenInteracting(candidateEvent,
                itemKey, testSessionState, itemSessionState, renderingOptions, RenderingMode.AFTER_INITIALISATION);
        doRendering(candidateEvent, renderingRequest, resultStream);
    }

    private void renderInteractingAfterAttempt(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestItemRenderingRequest renderingRequest = initTestRenderingRequestWhenInteracting(candidateEvent,
                itemKey, testSessionState, itemSessionState, renderingOptions, RenderingMode.AFTER_ATTEMPT);
        doRendering(candidateEvent, renderingRequest, resultStream);
    }

    private TestItemRenderingRequest initTestRenderingRequestWhenInteracting(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final RenderingMode renderingMode) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
//        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();

        /* Compute current value for 'duration' */
        final double duration = computeTestSessionDuration(candidateSession);

        /* Will need to query certain parts of state */
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(delivery,
                testSessionState, new NotificationRecorder(NotificationLevel.INFO));

        final TestItemRenderingRequest renderingRequest = initTestItemRenderingRequestCustomDuration(candidateEvent,
                itemKey, testSessionState, itemSessionState, renderingOptions, renderingMode, duration);
        renderingRequest.setTestPartNavigationAllowed(testSessionController.maySelectQuestions());
//        renderingRequest.setEndTestPartAllowed(testSessionController.canEndTestPart());
        renderingRequest.setEndTestPartAllowed(false); /* Sue prefers this */
//        renderingRequest.setCloseAllowed(testDeliverySettings.isAllowClose());
//        renderingRequest.setReinitAllowed(testDeliverySettings.isAllowReinitWhenInteracting());
//        renderingRequest.setResetAllowed(testDeliverySettings.isAllowResetWhenInteracting());
//        renderingRequest.setSolutionAllowed(testDeliverySettings.isAllowSolutionWhenInteracting());
//        renderingRequest.setResultAllowed(false);
//        renderingRequest.setSourceAllowed(testDeliverySettings.isAllowSource());
        return renderingRequest;
    }

    /**
     * Computes the current value for the <code>duration</code> variable for this session.
     * <p>
     * Currently, this is just the length of time since the session was first opened.
     * We DO NOT yet support breaking sessions time-wise.
     *
     * @return computed value for <code>duration</code>, which will be non-negative.
     */
    private double computeTestSessionDuration(final CandidateSession candidateSession) {
        final long startTime = candidateSession.getCreationTime().getTime();
        final long currentTime = requestTimestampContext.getCurrentRequestTimestamp().getTime();

        final double duration = (currentTime - startTime) / 1000.0;
        return duration;
    }

    private void renderItemEventWhenClosed(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions,  final OutputStream resultStream) {
        final CandidateItemEventType itemEventType = candidateEvent.getItemEventType();
        switch (itemEventType) {
            case ATTEMPT_VALID:
            case ATTEMPT_INVALID:
            case ATTEMPT_BAD:
                renderClosedAfterAttempt(candidateEvent, itemKey, testSessionState, itemSessionState, renderingOptions, resultStream);
                break;

            case CLOSE:
            case PLAYBACK:
            case SOLUTION:
            case REINIT:
            case RESET:
                throw new QtiWorksLogicException("The item event " + itemEventType + " is not yet supported within tests");

            case INIT:
                throw new QtiWorksLogicException("The item event " + itemEventType + " should not occur in tests");

            default:
                throw new QtiWorksLogicException("Unexpected logic branch. Event type " + itemEventType);
        }
    }

    private void renderClosedAfterAttempt(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestItemRenderingRequest renderingRequest = initTestRenderingRequestWhenClosed(candidateEvent,
                itemKey, testSessionState, itemSessionState, renderingOptions, RenderingMode.AFTER_ATTEMPT);
        doRendering(candidateEvent, renderingRequest, resultStream);
    }

    private void renderClosed(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestItemRenderingRequest renderingRequest = initTestRenderingRequestWhenClosed(candidateEvent,
                itemKey, testSessionState, itemSessionState, renderingOptions, RenderingMode.CLOSED);
        doRendering(candidateEvent, renderingRequest, resultStream);
    }

    private void renderItemReview(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState, final RenderingOptions renderingOptions,
            final OutputStream resultStream) {
        /* Extract item to review */
        final String reviewItemKeyString = candidateEvent.getTestItemKey();
        if (reviewItemKeyString==null) {
            /* Render test part feedback */
            renderTestPartFeedback(candidateEvent, testSessionState, renderingOptions, resultStream);
        }
        else {
            /* Show this item */
            final TestPlanNodeKey reviewItemKey = TestPlanNodeKey.fromString(reviewItemKeyString);

            /* Item selected, so render current state of item */
            final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(reviewItemKey);

            /* We'll do effectively the same thing as closed, but tweak the available options a bit */
            final TestItemRenderingRequest renderingRequest = initTestRenderingRequestWhenClosed(candidateEvent,
                    reviewItemKey, testSessionState, itemSessionState, renderingOptions, RenderingMode.REVIEW);
            renderingRequest.setTestPartNavigationAllowed(false); /* (This is the pre-closed navigation) */
            renderingRequest.setEndTestPartAllowed(false); /* (Already closed) */
            renderingRequest.setReviewTestPartAllowed(true);

            /* Pass effective showFeedback to rendering */
            final TestPlanNode reviewNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(reviewItemKey);
            renderingRequest.setShowFeedback(reviewNode.getEffectiveItemSessionControl().isShowFeedback());

            doRendering(candidateEvent, renderingRequest, resultStream);
        }
    }

    private TestItemRenderingRequest initTestRenderingRequestWhenClosed(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final RenderingMode renderingMode) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
//        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();

        /* Will need to query certain parts of state */
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(delivery,
                testSessionState, new NotificationRecorder(NotificationLevel.INFO));

        final TestItemRenderingRequest renderingRequest = initTestItemRenderingRequest(candidateEvent,
                itemKey, testSessionState, itemSessionState, renderingOptions, renderingMode);
        renderingRequest.setTestPartNavigationAllowed(testSessionController.maySelectQuestions());
//        renderingRequest.setEndTestPartAllowed(testSessionController.canEndTestPart());
        renderingRequest.setEndTestPartAllowed(false); /* Sue prefers this */
        renderingRequest.setReviewTestPartAllowed(false); /* Not in review state yet */
//        renderingRequest.setCloseAllowed(false);
//        renderingRequest.setSolutionAllowed(testDeliverySettings.isAllowSolutionWhenClosed());
//        renderingRequest.setReinitAllowed(testDeliverySettings.isAllowReinitWhenClosed());
//        renderingRequest.setResetAllowed(testDeliverySettings.isAllowResetWhenClosed());
//        renderingRequest.setResultAllowed(testDeliverySettings.isAllowResult());
//        renderingRequest.setSourceAllowed(testDeliverySettings.isAllowSource());
//
//        renderingRequest.setPlaybackAllowed(testDeliverySettings.isAllowPlayback());
//        if (testDeliverySettings.isAllowPlayback()) {
//            renderingRequest.setPlaybackEvents(getPlaybackEvents(candidateSession));
//        }
        return renderingRequest;
    }

    private TestItemRenderingRequest initTestItemRenderingRequest(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final RenderingMode renderingMode) {
        return initTestItemRenderingRequestCustomDuration(candidateEvent, itemKey, testSessionState,
                itemSessionState, renderingOptions, renderingMode, -1.0);
    }

    private TestItemRenderingRequest initTestItemRenderingRequestCustomDuration(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final RenderingMode renderingMode,
            final double durationOverride) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);

        /* Get System ID of current item */
        final URI itemSystemId = testSessionState.getTestPlan().getTestPlanNodeMap().get(itemKey).getItemSystemId();

        /* Extract ItemSessionState XML for this event and override the value for duration if caller
         * supplies a non-negative duration */
        if (durationOverride >= 0.0) {
            testSessionState.setDuration(durationOverride);
        }

        final TestItemRenderingRequest renderingRequest = new TestItemRenderingRequest();
        initBaseRenderingRequest(renderingRequest, assessmentPackage, testDeliverySettings, renderingOptions);
        renderingRequest.setAssessmentItemUri(itemSystemId);
        renderingRequest.setRenderingMode(renderingMode);
        renderingRequest.setTestSessionState(testSessionState);
        renderingRequest.setItemSessionState(itemSessionState);
        renderingRequest.setPrompt(testDeliverySettings.getPrompt());
        return renderingRequest;
    }

    private void initBaseRenderingRequest(final AbstractRenderingRequest renderingRequest,
            final AssessmentPackage assessmentPackage, final DeliverySettings deliverySettings,
            final RenderingOptions renderingOptions) {
        renderingRequest.setAssessmentResourceLocator(assessmentPackageFileService.createResolvingResourceLocator(assessmentPackage));
        renderingRequest.setAssessmentResourceUri(assessmentPackageFileService.createAssessmentObjectUri(assessmentPackage));
        renderingRequest.setAuthorMode(false); /* FIXME: Temporary override until we get decent authoring info! */
//        renderingRequest.setAuthorMode(deliverySettings.isAuthorMode());
        renderingRequest.setRenderingOptions(renderingOptions);
    }

    private void doRendering(final CandidateEvent candidateEvent,
            final TestItemRenderingRequest renderingRequest, final OutputStream resultStream) {
        candidateAuditLogger.logTestItemRendering(candidateEvent, renderingRequest);
        final List<CandidateEventNotification> notifications = candidateEvent.getNotifications();
        assessmentRenderer.renderTestItem(renderingRequest, notifications, resultStream);
    }

    //----------------------------------------------------
    // Attempt

    public CandidateAttempt handleAttempt(final long xid, final String sessionToken,
            final Map<Identifier, StringResponseData> stringResponseMap,
            final Map<Identifier, MultipartFile> fileResponseMap)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return handleAttempt(candidateSession, stringResponseMap, fileResponseMap);
    }

    public CandidateAttempt handleAttempt(final CandidateSession candidateSession,
            final Map<Identifier, StringResponseData> stringResponseMap,
            final Map<Identifier, MultipartFile> fileResponseMap)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = candidateDataServices.getMostRecentTestEvent(candidateSession);
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure an attempt is allowed */
        if (testSessionState.getCurrentItemKey()==null || !testSessionController.maySubmitResponsesToCurrentItem()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.MAKE_ATTEMPT);
        }

        final ItemSessionState itemSessionState = testSessionState.getCurrentItemSessionState();

        /* FIXME: Next wodge of code has some cut & paste! */

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
                final CandidateFileSubmission fileSubmission = candidateUploadService.importFileSubmission(candidateSession, multipartFile);
                final FileResponseData fileResponseData = new FileResponseData(new File(fileSubmission.getStoredFilePath()), fileSubmission.getContentType());
                responseMap.put(identifier, fileResponseData);
                fileSubmissionMap.put(identifier, fileSubmission);
            }
        }

        /* Build Map of responses in appropriate entity form */
        final CandidateAttempt candidateAttempt = new CandidateAttempt();
        final Map<Identifier, CandidateResponse> responseEntityMap = new HashMap<Identifier, CandidateResponse>();
        final Set<CandidateResponse> candidateItemResponses = new HashSet<CandidateResponse>();
        for (final Entry<Identifier, ResponseData> responseEntry : responseMap.entrySet()) {
            final Identifier responseIdentifier = responseEntry.getKey();
            final ResponseData responseData = responseEntry.getValue();

            final CandidateResponse candidateItemResponse = new CandidateResponse();
            candidateItemResponse.setResponseIdentifier(responseIdentifier.toString());
            candidateItemResponse.setCandidateAttempt(candidateAttempt);
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
            responseEntityMap.put(responseIdentifier, candidateItemResponse);
            candidateItemResponses.add(candidateItemResponse);
        }
        candidateAttempt.setCandidateResponses(candidateItemResponses);

        /* Attempt to bind responses */
        testSessionController.handleResponses(responseMap);

        /* Note any responses that failed to bind */
        final Set<Identifier> badResponseIdentifiers = itemSessionState.getUnboundResponseIdentifiers();
        final boolean allResponsesBound = badResponseIdentifiers.isEmpty();
        for (final Identifier badResponseIdentifier : badResponseIdentifiers) {
            responseEntityMap.get(badResponseIdentifier).setResponseLegality(ResponseLegality.BAD);
        }

        /* Now validate the responses according to any constraints specified by the interactions */
        boolean allResponsesValid = false;
        if (allResponsesBound) {
            final Set<Identifier> invalidResponseIdentifiers = itemSessionState.getInvalidResponseIdentifiers();
            allResponsesValid = invalidResponseIdentifiers.isEmpty();
            if (!allResponsesValid) {
                /* Some responses not valid, so note these down */
                for (final Identifier invalidResponseIdentifier : invalidResponseIdentifiers) {
                    responseEntityMap.get(invalidResponseIdentifier).setResponseLegality(ResponseLegality.INVALID);
                }
            }
        }

        /* Update state */
        testSessionState.setDuration(computeTestSessionDuration(candidateSession));

        /* Record resulting attempt and event */
        final CandidateItemEventType itemEventType = allResponsesBound ?
            (allResponsesValid ? CandidateItemEventType.ATTEMPT_VALID : CandidateItemEventType.ATTEMPT_INVALID)
            : CandidateItemEventType.ATTEMPT_BAD;
        final CandidateEvent candidateEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.ITEM_EVENT, itemEventType, testSessionState, notificationRecorder);

        candidateAttempt.setCandidateEvent(candidateEvent);
        candidateAttemptDao.persist(candidateAttempt);

        /* Log this (in existing state) */
        candidateAuditLogger.logTestItemCandidateAttempt(candidateSession, candidateAttempt);

        /* Persist session */
        candidateSessionDao.update(candidateSession);
        return candidateAttempt;
    }

    //----------------------------------------------------
    // Navigation

    public CandidateSession selectNavigationMenu(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return selectNavigationMenu(candidateSession);
    }

    public CandidateSession selectNavigationMenu(final CandidateSession candidateSession)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");

        /* Get current session state */
        final TestSessionState testSessionState = candidateDataServices.computeCurrentTestSessionState(candidateSession);

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateSession);

        /* FIXME: Probably have further checks to do here? */

        /* Update state */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final Delivery delivery = candidateSession.getDelivery();
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(delivery,
                testSessionState, notificationRecorder);
        testSessionController.selectItem(null);

        /* Record and log event */
        final CandidateEvent candidateEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.SELECT_MENU, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateSession, candidateEvent);

        return candidateSession;
    }


    public CandidateSession selectItem(final long xid, final String sessionToken, final TestPlanNodeKey itemKey)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return selectItem(candidateSession, itemKey);
    }

    public CandidateSession selectItem(final CandidateSession candidateSession, final TestPlanNodeKey itemKey)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(itemKey, "key");

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = candidateDataServices.getMostRecentTestEvent(candidateSession);
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateSession);
        if (!testSessionController.maySelectItem(itemKey)) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.SELECT_TEST_ITEM);
        }

        /* Update state */
        testSessionController.selectItem(itemKey);

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.SELECT_ITEM, null, itemKey, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateSession, candidateTestEvent);

        return candidateSession;
    }


    public CandidateSession endCurrentTestPart(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return endCurrentTestPart(candidateSession);
    }

    public CandidateSession endCurrentTestPart(final CandidateSession candidateSession)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");

        /* Get current session state */
        final TestSessionState testSessionState = candidateDataServices.computeCurrentTestSessionState(candidateSession);

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateSession);

        /* FIXME: Add checks to make sure we can do this */

        /* Update state */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final Delivery delivery = candidateSession.getDelivery();
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(delivery,
                testSessionState, notificationRecorder);
        /* FIXME: This is probably not the right logic in general but works OK in this restricted case */
        testSessionController.endTestPart();

        /* Record result and close session if this action finished the test */
        if (testSessionState.isFinished()) {
            /* Record assessmentResult */
            final AssessmentResult assessmentResult = candidateDataServices.computeTestAssessmentResult(candidateSession, testSessionController);
            candidateDataServices.recordTestAssessmentResult(candidateSession, assessmentResult);

            /* Update CandidateSession */
            candidateSession.setClosed(true);
            candidateSessionDao.update(candidateSession);
        }

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.END_TEST_PART, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateSession, candidateTestEvent);

        return candidateSession;
    }

    //----------------------------------------------------
    // Review

    public CandidateSession reviewItem(final long xid, final String sessionToken, final TestPlanNodeKey itemKey)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return reviewItem(candidateSession, itemKey);
    }

    public CandidateSession reviewItem(final CandidateSession candidateSession, final TestPlanNodeKey itemKey)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = candidateDataServices.getMostRecentTestEvent(candidateSession);
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateSession);
        if (itemKey!=null) {
            if (!testSessionController.mayReviewItem(itemKey)) {
                candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.REVIEW_TEST_ITEM);
            }
        }
        else {
            /* FIXME: Need to check that the current test part is finished. We don't currently model this. */
        }

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.REVIEW_ITEM, null, itemKey, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateSession, candidateTestEvent);

        return candidateSession;
    }


    //----------------------------------------------------
    // Exit

    public CandidateSession exitCurrentTestPart(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return exitCurrentTestPart(candidateSession);
    }

    public CandidateSession exitCurrentTestPart(final CandidateSession candidateSession)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");

        /* Get current session state */
        final TestSessionState testSessionState = candidateDataServices.computeCurrentTestSessionState(candidateSession);

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateSession);

        /* Update state */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final Delivery delivery = candidateSession.getDelivery();
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(delivery,
                testSessionState, notificationRecorder);
        /* FIXME: This is probably not the right logic in general but works OK in this restricted case */
        testSessionController.endTestPart();

        /* Update CandidateSession */
        candidateSession.setTerminated(true);
        candidateSessionDao.update(candidateSession);

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.EXIT_TEST_PART, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateSession, candidateTestEvent);

        return candidateSession;
    }

    //----------------------------------------------------
    // Access to additional package resources (e.g. images/CSS)

    public void streamAssessmentFile(final CandidateSession candidateSession, final String fileSystemIdString,
            final OutputStreamer outputStreamer)
            throws CandidateForbiddenException, IOException {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(fileSystemIdString, "fileSystemIdString");
        Assert.notNull(outputStreamer, "outputStreamer");

        /* Make sure requested file is whitelisted for access */
        final Delivery delivery = candidateSession.getDelivery();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);
        String resultingFileHref = null;
        for (final String safeFileHref : assessmentPackage.getSafeFileHrefs()) {
            final URI fileUri = assessmentPackageFileService.createAssessmentFileUri(assessmentPackage, safeFileHref);
            if (fileUri.toString().equals(fileSystemIdString)) {
                resultingFileHref = safeFileHref;
                break;
            }
        }
        if (resultingFileHref==null) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.ACCESS_BLACKLISTED_ASSESSMENT_FILE);
        }

        /* Finally stream the required resource */
        assessmentPackageFileService.streamAssessmentPackageFile(assessmentPackage, resultingFileHref, outputStreamer);
    }
}
