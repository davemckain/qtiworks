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
import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
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
import uk.ac.ed.ph.qtiworks.rendering.TestEntryRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.TestFeedbackRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.TestItemRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.TestPartFeedbackRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.TestPartNavigationRenderingRequest;
import uk.ac.ed.ph.qtiworks.services.AssessmentPackageFileService;
import uk.ac.ed.ph.qtiworks.services.CandidateAuditLogger;
import uk.ac.ed.ph.qtiworks.services.CandidateDataServices;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;
import uk.ac.ed.ph.qtiworks.services.EntityGraphService;
import uk.ac.ed.ph.qtiworks.services.FilespaceManager;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateResponseDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.NavigationMode;
import uk.ac.ed.ph.jqtiplus.node.test.SubmissionMode;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPartSessionState;
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
 * - nonlinear & linear navigation mode (menu, select item)
 * - simultaneous & individual submission mode
 * - solutions
 * - itemSessionControl (all but candidateComment)
 * - multiple testParts
 * - preCondition on testParts
 * - test / test part "during" and "atEnd" feedback
 *
 * STILL TO DO:
 * - preCondition (elsewhere)
 * - branchRule
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
    private CandidateResponseDao candidateResponseDao;

    //----------------------------------------------------
    // Session access

    /**
     * Looks up the {@link CandidateSession} having the given ID (xid)
     * and checks the given sessionToken against that stored in the session as a means of
     * "authentication".
     *
     * @param xid
     * @throws DomainEntityNotFoundException
     * @throws CandidateForbiddenException
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
    public void renderCurrentCandidateSessionState(final long xid, final String sessionToken,
            final RenderingOptions renderingOptions, final OutputStreamer outputStreamer)
            throws CandidateForbiddenException, DomainEntityNotFoundException, IOException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        renderCurrentCandidateSessionState(candidateSession, renderingOptions, outputStreamer);
    }

    public void renderCurrentCandidateSessionState(final CandidateSession candidateSession,
            final RenderingOptions renderingOptions,
            final OutputStreamer outputStreamer) throws IOException {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(renderingOptions, "renderingOptions");
        Assert.notNull(outputStreamer, "outputStreamer");

        /* Create temporary file to hold the output before it gets streamed */
        final File resultFile = filespaceManager.createTempFile();
        try {
            /* Render to temp file */
            FileOutputStream resultOutputStream = null;
            try {
                resultOutputStream = new FileOutputStream(resultFile);
                renderState(candidateSession, renderingOptions, resultOutputStream);
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

    private void renderState(final CandidateSession candidateSession, final RenderingOptions renderingOptions, final OutputStream resultStream) {
        if (candidateSession.isTerminated()) {
            /* Session is terminated */
            renderTerminated(candidateSession, renderingOptions, resultStream);
        }
        else {
            /* Render most recent event */
            final CandidateEvent latestEvent = candidateDataServices.getMostRecentEvent(candidateSession);
            renderEvent(latestEvent, renderingOptions, resultStream);
        }
    }

    private void renderTerminated(final CandidateSession candidateSession,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final Delivery delivery = candidateSession.getDelivery();
        final DeliverySettings deliverySettings = delivery.getDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);

        final TerminatedRenderingRequest renderingRequest = new TerminatedRenderingRequest();
        initBaseRenderingRequest(renderingRequest, assessmentPackage, deliverySettings, renderingOptions);

        assessmentRenderer.renderTeminated(renderingRequest, resultStream);
    }

    private void renderEvent(final CandidateEvent candidateEvent,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestSessionState testSessionState = candidateDataServices.loadTestSessionState(candidateEvent);
        final CandidateTestEventType testEventType = candidateEvent.getTestEventType(); /* (Not null) */

        switch (testEventType) {
            /* Handle "modal" events first. These cause a particular rendering state to be
             * displayed, which candidate will then leave.
             */
            case REVIEW_ITEM:
                renderItemReview(candidateEvent, testSessionState, renderingOptions, resultStream);
                break;

            case REVIEW_TEST_PART:
                renderTestPartFeedback(candidateEvent, testSessionState, renderingOptions, resultStream);
                break;

            case SOLUTION_ITEM:
                renderItemSolution(candidateEvent, testSessionState, renderingOptions, resultStream);
                break;

            /* Otherwise just render current test state */
            default:
                renderCurrentTestState(candidateEvent, testSessionState, renderingOptions, resultStream);
                break;
        }
    }

    private void renderCurrentTestState(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        if (testSessionState.isEnded()) {
            /* At end of test, so show overall test feedback */
            renderTestFeedback(candidateEvent, testSessionState, renderingOptions, resultStream);
        }
        else if (currentTestPartKey!=null) {
            final TestPartSessionState currentTestPartSessionState = testSessionState.getTestPartSessionStates().get(currentTestPartKey);
            final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
            if (currentItemKey!=null) {
                /* An item is selected, so render it in appropriate state */
                renderSelectedItem(candidateEvent, testSessionState, renderingOptions, resultStream);
            }
            else {
                /* No item selected */
                if (currentTestPartSessionState.isEnded()) {
                    /* testPart has ended, so must be showing testPart feedback */
                    renderTestPartFeedback(candidateEvent, testSessionState, renderingOptions, resultStream);
                }
                else {
                    /* testPart not ended, so we must be showing the navigation menu in nonlinear mode */
                    renderTestPartNonlinearNavigationMenu(candidateEvent, testSessionState, renderingOptions, resultStream);
                }
            }
        }
        else {
            /* No current testPart == start of multipart test */
            renderTestEntry(candidateEvent, testSessionState, renderingOptions, resultStream);
        }
    }

    private void renderTestEntry(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);

        final TestEntryRenderingRequest renderingRequest = new TestEntryRenderingRequest();
        initBaseRenderingRequest(renderingRequest, assessmentPackage, testDeliverySettings, renderingOptions);
        renderingRequest.setTestSessionState(testSessionState);

        candidateAuditLogger.logTestEntryRendering(candidateEvent);
        final List<CandidateEventNotification> notifications = candidateEvent.getNotifications();
        assessmentRenderer.renderTestEntryPage(renderingRequest, notifications, resultStream);
    }

    private void renderTestPartNonlinearNavigationMenu(final CandidateEvent candidateEvent,
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
        renderingRequest.setTestSessionState(testSessionState);
        renderingRequest.setEndTestPartAllowed(testSessionController.mayEndTestPart());

        candidateAuditLogger.logTestPartNavigationRendering(candidateEvent);
        final List<CandidateEventNotification> notifications = candidateEvent.getNotifications();
        assessmentRenderer.renderTestPartNavigation(renderingRequest, notifications, resultStream);
    }

    private void renderTestFeedback(final CandidateEvent candidateEvent,
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

    private void renderTestPartFeedback(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);

        final TestPartFeedbackRenderingRequest renderingRequest = new TestPartFeedbackRenderingRequest();
        initBaseRenderingRequest(renderingRequest, assessmentPackage, testDeliverySettings, renderingOptions);
        renderingRequest.setTestSessionState(testSessionState);

        candidateAuditLogger.logTestFeedbackRendering(candidateEvent);
        final List<CandidateEventNotification> notifications = candidateEvent.getNotifications();
        assessmentRenderer.renderTestPartFeedback(renderingRequest, notifications, resultStream);
    }

    private void renderSelectedItem(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(currentItemKey);

        if (itemSessionState.isClosed()) {
            /* Item session closed */
            renderItemEventWhenClosed(candidateEvent, currentItemKey, testSessionState, itemSessionState, renderingOptions, resultStream);
        }
        else {
            /* Interacting */
            renderItemEventWhenInteracting(candidateEvent, currentItemKey, testSessionState, itemSessionState, renderingOptions, resultStream);
        }
    }

    private void renderItemEventWhenInteracting(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        /* (The logic here is simpler than for single items, as we don't support some of the more
         * exotic lifecycle methods within tests)
         */
        final TestItemRenderingRequest renderingRequest = initItemRenderingRequestWhenInteracting(candidateEvent,
                itemKey, testSessionState, itemSessionState, renderingOptions);
        doRendering(candidateEvent, renderingRequest, resultStream);
    }

    private TestItemRenderingRequest initItemRenderingRequestWhenInteracting(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
//        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();

        /* Compute current value for 'duration' */
        final double duration = computeTestSessionDuration(candidateSession);

        /* Will need to query certain parts of state */
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(delivery,
                testSessionState, new NotificationRecorder(NotificationLevel.INFO));
        final TestPart currentTestPart = testSessionController.getCurrentTestPart();
        final NavigationMode navigationMode = currentTestPart.getNavigationMode();

        final TestItemRenderingRequest renderingRequest = initTestItemRenderingRequestCustomDuration(candidateSession,
                itemKey, testSessionState, itemSessionState, renderingOptions, RenderingMode.INTERACTING, duration);
        renderingRequest.setTestPartNavigationAllowed(navigationMode==NavigationMode.NONLINEAR);
        renderingRequest.setFinishItemAllowed(navigationMode==NavigationMode.LINEAR && testSessionController.mayFinishItemLinear());
        return renderingRequest;
    }

    private void renderItemEventWhenClosed(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions,  final OutputStream resultStream) {
        /* (The logic here is simpler than for single items, as we don't support some of the more
         * exotic lifecycle methods within tests)
         */
        final TestItemRenderingRequest renderingRequest = initItemRenderingRequestWhenClosed(candidateEvent,
                itemKey, testSessionState, itemSessionState, renderingOptions);
        doRendering(candidateEvent, renderingRequest, resultStream);
    }

    private TestItemRenderingRequest initItemRenderingRequestWhenClosed(final CandidateEvent candidateEvent,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
//        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();

        /* Will need to query certain parts of state */
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(delivery,
                testSessionState, new NotificationRecorder(NotificationLevel.INFO));
        final TestPart currentTestPart = testSessionController.getCurrentTestPart();
        final NavigationMode navigationMode = currentTestPart.getNavigationMode();

        final TestItemRenderingRequest renderingRequest = initTestItemRenderingRequest(candidateSession,
                itemKey, testSessionState, itemSessionState, renderingOptions, RenderingMode.CLOSED);
        renderingRequest.setTestPartNavigationAllowed(navigationMode==NavigationMode.NONLINEAR);
        renderingRequest.setFinishItemAllowed(navigationMode==NavigationMode.LINEAR);
        renderingRequest.setReviewTestPartAllowed(false); /* Not in review state yet */
        renderingRequest.setTestItemSolutionAllowed(false); /* Ditto */
        return renderingRequest;
    }

    private void renderItemReview(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState, final RenderingOptions renderingOptions,
            final OutputStream resultStream) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();

        /* Extract item to review */
        final String reviewItemKeyString = candidateEvent.getTestItemKey();

        final TestPlanNodeKey reviewItemKey = TestPlanNodeKey.fromString(reviewItemKeyString);
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(reviewItemKey);
        final TestPlanNode reviewNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(reviewItemKey);

        /* We'll do effectively the same thing as closed, but tweak the available options a bit */
        final TestItemRenderingRequest renderingRequest = initTestItemRenderingRequest(candidateSession,
                reviewItemKey, testSessionState, itemSessionState, renderingOptions, RenderingMode.REVIEW);
        renderingRequest.setTestPartNavigationAllowed(false); /* (Not used in review state) */
        renderingRequest.setFinishItemAllowed(false); /* (Ditto) */
        renderingRequest.setReviewTestPartAllowed(true);
        renderingRequest.setTestItemSolutionAllowed(reviewNode.getEffectiveItemSessionControl().isShowSolution());

        /* Pass effective value of 'showFeedback' to rendering */
        renderingRequest.setShowFeedback(reviewNode.getEffectiveItemSessionControl().isShowFeedback());

        doRendering(candidateEvent, renderingRequest, resultStream);
    }

    private void renderItemSolution(final CandidateEvent candidateEvent,
            final TestSessionState testSessionState, final RenderingOptions renderingOptions,
            final OutputStream resultStream) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();

        /* Extract item to show solution */
        final String reviewItemKeyString = candidateEvent.getTestItemKey();
        if (reviewItemKeyString==null) {
            throw new QtiWorksLogicException("Expected item key to be non-null here");
        }

        /* Show this item */
        final TestPlanNodeKey reviewItemKey = TestPlanNodeKey.fromString(reviewItemKeyString);
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(reviewItemKey);
        /* We'll do effectively the same thing as closed, but tweak the available options a bit */

        final TestItemRenderingRequest renderingRequest = initTestItemRenderingRequest(candidateSession,
                reviewItemKey, testSessionState, itemSessionState, renderingOptions, RenderingMode.SOLUTION);
        renderingRequest.setTestPartNavigationAllowed(false); /* (Not used in review state) */
        renderingRequest.setFinishItemAllowed(false); /* (Ditto) */
        renderingRequest.setReviewTestPartAllowed(true);
        renderingRequest.setTestItemSolutionAllowed(false); /* (Already showing solution) */

        /* Pass effective value of 'showFeedback' to rendering */
        final TestPlanNode reviewNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(reviewItemKey);
        renderingRequest.setShowFeedback(reviewNode.getEffectiveItemSessionControl().isShowFeedback());

        doRendering(candidateEvent, renderingRequest, resultStream);
    }

    private TestItemRenderingRequest initTestItemRenderingRequest(final CandidateSession candidateSession,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final RenderingMode renderingMode) {
        return initTestItemRenderingRequestCustomDuration(candidateSession, itemKey, testSessionState,
                itemSessionState, renderingOptions, renderingMode, -1.0);
    }

    private TestItemRenderingRequest initTestItemRenderingRequestCustomDuration(final CandidateSession candidateSession,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final RenderingMode renderingMode,
            final double durationOverride) {
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
        renderingRequest.setItemKey(itemKey);
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
        renderingRequest.setAuthorMode(deliverySettings.isAuthorMode());
        renderingRequest.setRenderingOptions(renderingOptions);
    }

    private void doRendering(final CandidateEvent candidateEvent,
            final TestItemRenderingRequest renderingRequest, final OutputStream resultStream) {
        candidateAuditLogger.logTestItemRendering(candidateEvent, renderingRequest);
        final List<CandidateEventNotification> notifications = candidateEvent.getNotifications();
        assessmentRenderer.renderTestItem(renderingRequest, notifications, resultStream);
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

    //----------------------------------------------------
    // Attempt

    public void handleResponses(final long xid, final String sessionToken,
            final Map<Identifier, StringResponseData> stringResponseMap,
            final Map<Identifier, MultipartFile> fileResponseMap)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        handleResponses(candidateSession, stringResponseMap, fileResponseMap);
    }

    public void handleResponses(final CandidateSession candidateSession,
            final Map<Identifier, StringResponseData> stringResponseMap,
            final Map<Identifier, MultipartFile> fileResponseMap)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = candidateDataServices.getMostRecentEvent(candidateSession);
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure an attempt is allowed */
        if (testSessionState.getCurrentItemKey()==null || !testSessionController.maySubmitResponsesToCurrentItem()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.MAKE_RESPONSES);
        }

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

        /* Attempt to bind responses (and maybe perform RP & OP) */
        testSessionController.handleResponses(responseDataMap);

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

        /* Update JQTI state */
        testSessionState.setDuration(computeTestSessionDuration(candidateSession));

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
        final CandidateEvent candidateEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.ITEM_EVENT, candidateItemEventType, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        /* Persist CandidateResponse entities */
        for (final CandidateResponse candidateResponse : candidateResponseMap.values()) {
            candidateResponse.setCandidateEvent(candidateEvent);
            candidateResponseDao.persist(candidateResponse);
        }

        /* Save any change to session state */
        candidateSessionDao.update(candidateSession);
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
        testSessionController.selectItemNonlinear(null);

        /* Record and log event */
        final CandidateEvent candidateEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.SELECT_MENU, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        return candidateSession;
    }

    public CandidateSession selectNonlinearItem(final long xid, final String sessionToken, final TestPlanNodeKey itemKey)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return selectNonlinearItem(candidateSession, itemKey);
    }

    public CandidateSession selectNonlinearItem(final CandidateSession candidateSession, final TestPlanNodeKey itemKey)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(itemKey, "key");

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = candidateDataServices.getMostRecentEvent(candidateSession);
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateSession);
        if (!testSessionController.maySelectItemNonlinear(itemKey)) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.SELECT_NONLINEAR_TEST_ITEM);
        }

        /* Update state */
        testSessionController.selectItemNonlinear(itemKey);

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.SELECT_ITEM, null, itemKey, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    public CandidateSession finishLinearItem(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return finishLinearItem(candidateSession);
    }

    public CandidateSession finishLinearItem(final CandidateSession candidateSession)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = candidateDataServices.getMostRecentEvent(candidateSession);
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateSession);
        if (!testSessionController.mayFinishItemLinear()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.FINISH_LINEAR_TEST_ITEM);
        }

        /* Update state */
        testSessionController.finishItemLinear();

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.FINISH_ITEM, null, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

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

        /* Record assessmentResult (not necessarily final) */
        final AssessmentResult assessmentResult = candidateDataServices.computeTestAssessmentResult(candidateSession, testSessionController);
        candidateDataServices.recordTestAssessmentResult(candidateSession, assessmentResult);

        /* See if this action has ended the test */
        if (testSessionState.isEnded()) {
            /* Update CandidateSession */
            candidateSession.setClosed(true);
            candidateSessionDao.update(candidateSession);
        }

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.END_TEST_PART, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    //----------------------------------------------------
    // Review

    public CandidateSession reviewTestPart(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return reviewTestPart(candidateSession);
    }

    public CandidateSession reviewTestPart(final CandidateSession candidateSession)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = candidateDataServices.getMostRecentEvent(candidateSession);
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateSession);

        /* FIXME: Make sure the testPart is currently ended */

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.REVIEW_TEST_PART, null, null, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    public CandidateSession reviewItem(final long xid, final String sessionToken, final TestPlanNodeKey itemKey)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return reviewItem(candidateSession, itemKey);
    }

    public CandidateSession reviewItem(final CandidateSession candidateSession, final TestPlanNodeKey itemKey)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(itemKey, "itemKey");

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = candidateDataServices.getMostRecentEvent(candidateSession);
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateSession);
        if (!testSessionController.mayReviewItem(itemKey)) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.REVIEW_TEST_ITEM);
        }

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.REVIEW_ITEM, null, itemKey, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    //----------------------------------------------------
    // Solution request

    public CandidateSession requestSolution(final long xid, final String sessionToken, final TestPlanNodeKey itemKey)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return requestSolution(candidateSession, itemKey);
    }

    public CandidateSession requestSolution(final CandidateSession candidateSession, final TestPlanNodeKey itemKey)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");

        /* Get current JQTI state and create JQTI controller */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final CandidateEvent mostRecentEvent = candidateDataServices.getMostRecentEvent(candidateSession);
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(mostRecentEvent, notificationRecorder);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateSession);
        if (itemKey!=null) {
            if (!testSessionController.mayAccessItemSolution(itemKey)) {
                candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.SOLUTION_TEST_ITEM);
            }
        }
        else {
            /* FIXME: Need to check that the current test part is finished. We don't currently model this. */
        }

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.SOLUTION_ITEM, null, itemKey, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    //----------------------------------------------------
    // Advance TestPart

    public CandidateSession advanceTestPart(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return advanceTestPart(candidateSession);
    }

    public CandidateSession advanceTestPart(final CandidateSession candidateSession)
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
        final TestPlanNode nextTestPart = testSessionController.enterNextAvailableTestPart();

        if (nextTestPart==null) {
            /* We exited the last test part.
             *
             * For single part tests, we terminate the test completely as the test feedback was shown with the testPart feedback.
             * For multi-part tests, we shall keep the test open so that the test feedback can be viewed.
             */
            if (testSessionState.getTestPlan().getTestPartNodes().size()==1) {
                candidateSession.setTerminated(true);
                candidateSessionDao.update(candidateSession);
            }
        }

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.ADVANCE_TEST_PART, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

        return candidateSession;
    }

    //----------------------------------------------------
    // Exit (multi-part) test

    public CandidateSession exitTest(final long xid, final String sessionToken)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return exitTest(candidateSession);
    }

    public CandidateSession exitTest(final CandidateSession candidateSession)
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
        testSessionController.exitTest();

        /* Update CandidateSession as appropriate */
        candidateSession.setTerminated(true);
        candidateSessionDao.update(candidateSession);

        /* Record and log event */
        final CandidateEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.EXIT_MULTI_PART_TEST, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateTestEvent);

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
