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
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateResponse;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateTestEvent;
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
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service the manages the real-time delivery of a an {@link AssessmentTest}
 * to candidates.
 * <p>
 * NOTE: Remember there is no {@link IdentityContext} for candidates.
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
        final CandidateTestEvent latestEvent = candidateDataServices.getMostRecentTestEvent(candidateSession);

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

    private void renderEvent(final CandidateSession candidateSession,
            final CandidateTestEvent candidateTestEvent,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestSessionState testSessionState = candidateDataServices.unmarshalTestSessionState(candidateTestEvent);
        if (candidateSession.isTerminated()) {
            renderTerminated(candidateTestEvent, renderingOptions, resultStream);
        }
        else if (testSessionState.isFinished()) {
            renderFinishedTest(candidateTestEvent, testSessionState, renderingOptions, resultStream);
        }
        else {
            renderEventWhenTestOpen(candidateTestEvent, testSessionState, renderingOptions, resultStream);
        }
    }

    /** FIXME: Implement this! */
    private void renderFinishedTest(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        throw new QtiWorksLogicException("NOT IMPLEMENTED YET");
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

    private void renderEventWhenTestOpen(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
        if (currentItemKey!=null) {
            /* Item selected, so render current state of item */
            final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(currentItemKey);
            renderSelectedItem(candidateTestEvent, testSessionState, itemSessionState, renderingOptions, resultStream);
        }
        else {
            /* Show navigation menu */
            renderTestPartNavigationMenu(candidateTestEvent, testSessionState, renderingOptions, resultStream);
        }
    }

    private void renderTestPartNavigationMenu(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final RenderingOptions renderingOptions,
            final OutputStream resultStream) {
        final CandidateSession candidateSession = candidateTestEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);

        final TestPartNavigationRenderingRequest renderingRequest = new TestPartNavigationRenderingRequest();
        initBaseRenderingRequest(renderingRequest, assessmentPackage, testDeliverySettings, renderingOptions);
        renderingRequest.setTestSessionState(testSessionState);

        candidateAuditLogger.logTestPartNavigationRendering(candidateTestEvent);
        final List<CandidateEventNotification> notifications = candidateTestEvent.getNotifications();
        assessmentRenderer.renderTestPartNavigation(renderingRequest, notifications, resultStream);
    }

    private void renderSelectedItem(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        if (itemSessionState.isClosed()) {
            /* Item is closed */
            renderEventWhenClosed(candidateTestEvent, testSessionState, itemSessionState, renderingOptions, resultStream);
        }
        else {
            /* Interacting */
            renderEventWhenInteracting(candidateTestEvent, testSessionState, itemSessionState, renderingOptions, resultStream);
        }
    }

    private void renderEventWhenInteracting(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final CandidateTestEventType eventType = candidateTestEvent.getTestEventType();
        switch (eventType) {
            case INIT:
            case REINIT:
            case RESET:
            case SELECT_ITEM:
                renderInteractingPresentation(candidateTestEvent, testSessionState, itemSessionState,
                        renderingOptions, resultStream);
                break;

            case ATTEMPT_VALID:
            case ATTEMPT_INVALID:
            case ATTEMPT_BAD:
                renderInteractingAfterAttempt(candidateTestEvent, testSessionState, itemSessionState,
                        renderingOptions, resultStream);
                break;

            default:
                throw new QtiWorksLogicException("Unexpected logic branch. Event type " + eventType);
        }
    }

    private void renderInteractingPresentation(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestItemRenderingRequest renderingRequest = initTestRenderingRequestWhenInteracting(candidateTestEvent,
                testSessionState, itemSessionState, renderingOptions, RenderingMode.AFTER_INITIALISATION);
        doRendering(candidateTestEvent, renderingRequest, resultStream);
    }

    private void renderInteractingAfterAttempt(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestItemRenderingRequest renderingRequest = initTestRenderingRequestWhenInteracting(candidateTestEvent,
                testSessionState, itemSessionState, renderingOptions, RenderingMode.AFTER_ATTEMPT);
        fillAttemptResponseData(renderingRequest, candidateTestEvent);
        doRendering(candidateTestEvent, renderingRequest, resultStream);
    }

    private TestItemRenderingRequest initTestRenderingRequestWhenInteracting(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final RenderingMode renderingMode) {
        final CandidateSession candidateSession = candidateTestEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
//        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();

        /* Compute current value for 'duration' */
        final double duration = computeTestSessionDuration(candidateSession);

        /* Will need to query certain parts of state */
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(delivery,
                testSessionState, new NotificationRecorder(NotificationLevel.INFO));

        final TestItemRenderingRequest renderingRequest = initTestItemRenderingRequestCustomDuration(candidateTestEvent,
                testSessionState, itemSessionState, renderingOptions, renderingMode, duration);
        renderingRequest.setExitTestPartAllowed(testSessionController.canExitTestPart());
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

    private void renderEventWhenClosed(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions,  final OutputStream resultStream) {
        final CandidateTestEventType eventType = candidateTestEvent.getTestEventType();
        switch (eventType) {
            case ATTEMPT_VALID:
            case ATTEMPT_INVALID:
            case ATTEMPT_BAD:
                renderClosedAfterAttempt(candidateTestEvent, testSessionState, itemSessionState, renderingOptions, resultStream);
                break;

            case INIT:
            case REINIT:
            case RESET:
            case CLOSE:
                renderClosed(candidateTestEvent, testSessionState, itemSessionState, renderingOptions, resultStream);
                break;

            case SOLUTION:
                renderSolution(candidateTestEvent, testSessionState, itemSessionState, renderingOptions, resultStream);
                break;

            default:
                throw new QtiWorksLogicException("Unexpected logic branch. Event type " + eventType);
        }
    }

    private void renderClosedAfterAttempt(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestItemRenderingRequest renderingRequest = initTestRenderingRequestWhenClosed(candidateTestEvent,
                testSessionState, itemSessionState, renderingOptions, RenderingMode.AFTER_ATTEMPT);
        fillAttemptResponseData(renderingRequest, candidateTestEvent);
        doRendering(candidateTestEvent, renderingRequest, resultStream);
    }

    private void renderClosed(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestItemRenderingRequest renderingRequest = initTestRenderingRequestWhenClosed(candidateTestEvent,
                testSessionState, itemSessionState, renderingOptions, RenderingMode.CLOSED);
        doRendering(candidateTestEvent, renderingRequest, resultStream);
    }

    private void renderSolution(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final OutputStream resultStream) {
        final TestItemRenderingRequest renderingRequest = initTestRenderingRequestWhenClosed(candidateTestEvent,
                testSessionState, itemSessionState, renderingOptions, RenderingMode.SOLUTION);
        doRendering(candidateTestEvent, renderingRequest, resultStream);
    }

    private TestItemRenderingRequest initTestRenderingRequestWhenClosed(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final RenderingMode renderingMode) {
        final CandidateSession candidateSession = candidateTestEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
//        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();

        /* Will need to query certain parts of state */
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(delivery,
                testSessionState, new NotificationRecorder(NotificationLevel.INFO));

        final TestItemRenderingRequest renderingRequest = initTestItemRenderingRequest(candidateTestEvent,
                testSessionState, itemSessionState, renderingOptions, renderingMode);
        renderingRequest.setExitTestPartAllowed(testSessionController.canExitTestPart());
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

    private TestItemRenderingRequest initTestItemRenderingRequest(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final RenderingMode renderingMode) {
        return initTestItemRenderingRequestCustomDuration(candidateTestEvent, testSessionState,
                itemSessionState, renderingOptions, renderingMode, -1.0);
    }

    private TestItemRenderingRequest initTestItemRenderingRequestCustomDuration(final CandidateTestEvent candidateTestEvent,
            final TestSessionState testSessionState, final ItemSessionState itemSessionState,
            final RenderingOptions renderingOptions, final RenderingMode renderingMode,
            final double durationOverride) {
        final CandidateSession candidateSession = candidateTestEvent.getCandidateSession();
        final Delivery delivery = candidateSession.getDelivery();
        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);

        /* Get System ID of current item */
        final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
        final URI itemSystemId = testSessionState.getTestPlan().getTestPlanNodeMap().get(currentItemKey).getItemSystemId();

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
        renderingRequest.setAuthorMode(deliverySettings.isAuthorMode());
        renderingRequest.setRenderingOptions(renderingOptions);
    }

    private void doRendering(final CandidateTestEvent candidateTestEvent,
            final TestItemRenderingRequest renderingRequest, final OutputStream resultStream) {
        candidateAuditLogger.logTestItemRendering(candidateTestEvent, renderingRequest);
        final List<CandidateEventNotification> notifications = candidateTestEvent.getNotifications();
        assessmentRenderer.renderTestItem(renderingRequest, notifications, resultStream);
    }

    private void fillAttemptResponseData(final TestItemRenderingRequest renderingRequest, final CandidateTestEvent candidateTestEvent) {
        final CandidateAttempt attempt = candidateAttemptDao.getForEvent(candidateTestEvent);
        if (attempt==null) {
            throw new QtiWorksLogicException("Expected to find a CandidateAttempt corresponding to event #" + candidateTestEvent.getId());
        }
        fillAttemptResponseData(renderingRequest, attempt);
    }

    private void fillAttemptResponseData(final TestItemRenderingRequest renderingRequest, final CandidateAttempt candidateTestAttempt) {
        final Map<Identifier, ResponseData> responseDataBuilder = new HashMap<Identifier, ResponseData>();
        final Set<Identifier> badResponseIdentifiersBuilder = new HashSet<Identifier>();
        final Set<Identifier> invalidResponseIdentifiersBuilder = new HashSet<Identifier>();
        extractResponseDataForRendering(candidateTestAttempt, responseDataBuilder, badResponseIdentifiersBuilder, invalidResponseIdentifiersBuilder);

        renderingRequest.setResponseInputs(responseDataBuilder);
        renderingRequest.setBadResponseIdentifiers(badResponseIdentifiersBuilder);
        renderingRequest.setInvalidResponseIdentifiers(invalidResponseIdentifiersBuilder);
    }

    private void extractResponseDataForRendering(final CandidateAttempt attempt, final Map<Identifier, ResponseData> responseDataBuilder,
            final Set<Identifier> badResponseIdentifiersBuilder, final Set<Identifier> invalidResponseIdentifiersBuilder) {
        for (final CandidateResponse response : attempt.getResponses()) {
            final Identifier responseIdentifier = Identifier.parseString(response.getResponseIdentifier());
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
    // Navigation

    public CandidateSession selectItem(final long xid, final String sessionToken, final TestPlanNodeKey key)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        return selectItem(candidateSession, key);
    }

    public CandidateSession selectItem(final CandidateSession candidateSession, final TestPlanNodeKey key)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(key, "key");

        /* Get current session state */
        final TestSessionState testSessionState = candidateDataServices.computeCurrentTestSessionState(candidateSession);

        /* Make sure caller may do this */
        ensureSessionNotTerminated(candidateSession);

        /* Update state */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        final Delivery delivery = candidateSession.getDelivery();
        final TestSessionController testSessionController = candidateDataServices.createTestSessionController(delivery,
                testSessionState, notificationRecorder);
        testSessionController.selectItem(key);

        /* Record and log event */
        final CandidateTestEvent candidateTestEvent = candidateDataServices.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.SELECT_ITEM, testSessionState);
        candidateAuditLogger.logCandidateTestEvent(candidateSession, candidateTestEvent);

        return candidateSession;
    }
}
