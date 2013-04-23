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

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEventNotification;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.ItemRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.RenderingMode;
import uk.ac.ed.ph.qtiworks.rendering.StandaloneItemRenderingRequest;
import uk.ac.ed.ph.qtiworks.services.AssessmentPackageFileService;
import uk.ac.ed.ph.qtiworks.services.CandidateAuditLogger;
import uk.ac.ed.ph.qtiworks.services.CandidateDataServices;
import uk.ac.ed.ph.qtiworks.services.EntityGraphService;
import uk.ac.ed.ph.qtiworks.services.FilespaceManager;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for rendering the candidate state of assessments, connecting the domain
 * layer with the low-level {@link AssessmentRenderer}
 *
 * @see AssessmentRenderer
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class CandidateRenderingService {

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private CandidateAuditLogger candidateAuditLogger;

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
    private CandidateItemDeliveryService candidateItemDeliveryService;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    /**
     * Renders the current state of the {@link CandidateSession} having
     * the given ID (xid).
     */
    public void renderCurrentCandidateItemSessionState(final long xid, final String sessionToken,
            final ItemRenderingOptions renderingOptions, final OutputStreamer outputStreamer)
            throws CandidateForbiddenException, DomainEntityNotFoundException, IOException {
        Assert.notNull(sessionToken, "sessionToken");
        final CandidateSession candidateSession = candidateItemDeliveryService.lookupCandidateItemSession(xid, sessionToken);
        renderCurrentCandidateItemSessionState(candidateSession, renderingOptions, outputStreamer);
    }

    public void renderCurrentCandidateItemSessionState(final CandidateSession candidateSession,
            final ItemRenderingOptions renderingOptions, final OutputStreamer outputStreamer)
            throws IOException {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(renderingOptions, "renderingOptions");
        Assert.notNull(outputStreamer, "outputStreamer");

        /* Look up most recent event */
        final CandidateEvent latestEvent = candidateDataServices.getMostRecentEvent(candidateSession);

        /* Load the ItemSessionState */
        final ItemSessionState itemSessionState = candidateDataServices.loadItemSessionState(latestEvent);

        /* Touch the session's duration state if appropriate */
        if (itemSessionState.isEntered() && !itemSessionState.isEnded() && !itemSessionState.isSuspended()) {
            final Date timestamp = requestTimestampContext.getCurrentRequestTimestamp();
            final ItemSessionController itemSessionController = createItemSessionController(candidateSession, itemSessionState);
            itemSessionController.touchDuration(timestamp);
        }

        /* Create temporary file to hold the output before it gets streamed */
        final File resultFile = filespaceManager.createTempFile();
        try {
            /* Render to temp file */
            FileOutputStream resultOutputStream = null;
            try {
                resultOutputStream = new FileOutputStream(resultFile);
                renderEvent(itemSessionState, latestEvent, candidateSession, renderingOptions, resultOutputStream);
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

    private void renderEvent(final ItemSessionState itemSessionState, final CandidateEvent candidateEvent,
            final CandidateSession candidateSession, final ItemRenderingOptions renderingOptions,
            final OutputStream resultStream) {
        final CandidateItemEventType itemEventType = candidateEvent.getItemEventType();
        final Delivery delivery = candidateSession.getDelivery();
        final ItemDeliverySettings itemDeliverySettings = (ItemDeliverySettings) delivery.getDeliverySettings();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);

        /* Create and partially configure rendering request */
        final StandaloneItemRenderingRequest renderingRequest = new StandaloneItemRenderingRequest();
        renderingRequest.setRenderingOptions(renderingOptions);
        renderingRequest.setAssessmentResourceLocator(assessmentPackageFileService.createResolvingResourceLocator(assessmentPackage));
        renderingRequest.setAssessmentResourceUri(assessmentPackageFileService.createAssessmentObjectUri(assessmentPackage));
        renderingRequest.setAuthorMode(itemDeliverySettings.isAuthorMode());
        renderingRequest.setAssessmentItemUri(renderingRequest.getAssessmentResourceUri()); /* (These are the same for standalone items) */
        renderingRequest.setItemSessionState(itemSessionState);
        renderingRequest.setPrompt(itemDeliverySettings.getPrompt());
        renderingRequest.setRenderingOptions(renderingOptions);

        /* If session has terminated, render appropriate state and exit */
        if (candidateSession.isTerminated()) {
            assessmentRenderer.renderTeminated(renderingRequest, resultStream);
            return;
        }

        /* Handle "modal" events. These cause a particular rendering state to be
         * displayed, which candidate will then leave.
         */
        if (itemEventType==CandidateItemEventType.SOLUTION) {
            renderingRequest.setRenderingMode(RenderingMode.SOLUTION);
        }

        /* Now set candidate action permissions depending on state of session */
        if (itemSessionState.isOpen()) {
            /* Item session is open (interacting) */
            renderingRequest.setCloseAllowed(itemDeliverySettings.isAllowClose());
            renderingRequest.setReinitAllowed(itemDeliverySettings.isAllowReinitWhenInteracting());
            renderingRequest.setResetAllowed(itemDeliverySettings.isAllowResetWhenInteracting());
            renderingRequest.setSolutionAllowed(itemDeliverySettings.isAllowSolutionWhenInteracting());
            renderingRequest.setResultAllowed(false);
            renderingRequest.setSourceAllowed(itemDeliverySettings.isAllowSource());
            renderingRequest.setCandidateCommentAllowed(itemDeliverySettings.isAllowCandidateComment());
        }
        else if (itemSessionState.isEnded() && !itemSessionState.isExited()) {
            /* Item session is ended (closed) */
            renderingRequest.setCloseAllowed(false);
            renderingRequest.setReinitAllowed(itemDeliverySettings.isAllowReinitWhenClosed());
            renderingRequest.setResetAllowed(itemDeliverySettings.isAllowResetWhenClosed());
            renderingRequest.setSolutionAllowed(itemDeliverySettings.isAllowSolutionWhenClosed());
            renderingRequest.setResultAllowed(itemDeliverySettings.isAllowResult());
            renderingRequest.setSourceAllowed(itemDeliverySettings.isAllowSource());
            renderingRequest.setCandidateCommentAllowed(false);
        }

        /* Finally pass to rendering layer */
        candidateAuditLogger.logStandaloneItemRendering(candidateEvent, renderingRequest);
        final List<CandidateEventNotification> notifications = candidateEvent.getNotifications();
        assessmentRenderer.renderStandaloneItem(renderingRequest, notifications, resultStream);
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

    //----------------------------------------------------
    // Candidate Source access

    public void streamAssessmentSource(final long xid, final String sessionToken, final OutputStreamer outputStreamer)
            throws CandidateForbiddenException, IOException, DomainEntityNotFoundException {
        Assert.notNull(outputStreamer, "outputStreamer");
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        streamAssessmentSource(candidateSession, outputStreamer);
    }

    public void streamAssessmentSource(final CandidateSession candidateSession, final OutputStreamer outputStreamer)
            throws CandidateForbiddenException, IOException {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(outputStreamer, "outputStreamer");
        ensureCallerMayViewSource(candidateSession);
        final Delivery itemDelivery = candidateSession.getDelivery();
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(itemDelivery);

        assessmentPackageFileService.streamAssessmentPackageSource(assessmentPackage, outputStreamer);
        candidateAuditLogger.logAction(candidateSession, "ACCESS_SOURCE");
    }


    //----------------------------------------------------
    // Candidate Result access

    public void streamAssessmentResult(final long xid, final String sessionToken, final OutputStream outputStream)
            throws CandidateForbiddenException, DomainEntityNotFoundException {
        Assert.notNull(outputStream, "outputStream");
        final CandidateSession candidateSession = lookupCandidateSession(xid, sessionToken);
        streamAssessmentResult(candidateSession, outputStream);
    }

    public void streamAssessmentResult(final CandidateSession candidateSession, final OutputStream outputStream)
            throws CandidateForbiddenException {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(outputStream, "outputStream");

        /* Forbid results if the candidate session is closed */
        ensureSessionNotTerminated(candidateSession);

        /* Make sure candidate is actually allowed to get results for this delivery */
        ensureCallerMayViewResult(candidateSession);

        /* Get current state */
        final CandidateEvent mostRecentEvent = candidateDataServices.getMostRecentEvent(candidateSession);

        /* Generate result Object from state */
        final ItemSessionController itemSessionController = candidateDataServices.createItemSessionController(mostRecentEvent, null);
        final AssessmentResult assessmentResult = candidateDataServices.computeItemAssessmentResult(candidateSession, itemSessionController);

        /* Send result */
        qtiSerializer.serializeJqtiObject(assessmentResult, outputStream);
        candidateAuditLogger.logAction(candidateSession, "ACCESS_RESULT");
    }

    //----------------------------------------------------

    private ItemSessionController createItemSessionController(final CandidateSession candidateSession, final ItemSessionState itemSessionState) {
        final Delivery delivery = candidateSession.getDelivery();
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        return candidateDataServices.createItemSessionController(delivery,
                itemSessionState, notificationRecorder);
    }

    //----------------------------------------------------
    // Access controls

    private CandidateSession lookupCandidateSession(final long xid, final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException {
        Assert.notNull(sessionToken, "sessionToken");
        final CandidateSession candidateSession = candidateSessionDao.requireFindById(xid);
        if (!sessionToken.equals(candidateSession.getSessionToken())) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.ACCESS_CANDIDATE_SESSION);
        }
        return candidateSession;
    }

    private void ensureSessionNotTerminated(final CandidateSession candidateSession) throws CandidateForbiddenException {
        if (candidateSession.isTerminated()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.ACCESS_TERMINATED_SESSION);
        }
    }

    private void ensureCallerMayViewSource(final CandidateSession candidateSession)
            throws CandidateForbiddenException {
        final DeliverySettings deliverySettings = candidateSession.getDelivery().getDeliverySettings();
        if (!deliverySettings.isAllowSource()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.VIEW_ASSESSMENT_SOURCE);
        }
    }

    private void ensureCallerMayViewResult(final CandidateSession candidateSession)
            throws CandidateForbiddenException {
        final DeliverySettings deliverySettings = candidateSession.getDelivery().getDeliverySettings();
        if (!deliverySettings.isAllowResult()) {
            candidateAuditLogger.logAndForbid(candidateSession, CandidatePrivilege.VIEW_ASSESSMENT_RESULT);
        }
    }

}
