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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.Privilege;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateTestEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiLaunchType;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.services.base.AuditLogger;
import uk.ac.ed.ph.qtiworks.services.base.IdentityService;
import uk.ac.ed.ph.qtiworks.services.base.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateItemDeliveryService;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateTestDeliveryService;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper service for launching new candidate sessions on a {@link Delivery} of an
 * {@link AssessmentItem} or {@link AssessmentTest}
 *
 * @see CandidateItemDeliveryService
 * @see CandidateTestDeliveryService
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class CandidateSessionStarter {

    @Resource
    private AuditLogger auditLogger;

    @Resource
    private IdentityService identityService;

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private CandidateAuditLogger candidateAuditLogger;

    @Resource
    private CandidateSessionCloser candidateSessionCloser;

    @Resource
    private CandidateDataService candidateDataService;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    //-------------------------------------------------
    // System samples

    public CandidateSession createSystemSampleSession(final long aid, final String exitUrl)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery sampleDelivery = lookupSystemSampleDelivery(aid);
        return createCandidateSession(sampleDelivery, true, exitUrl, null, null);
    }

    private Delivery lookupSystemSampleDelivery(final long aid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final Assessment assessment = lookupSampleAssessment(aid);
        final List<Delivery> systemDemoDeliveries = deliveryDao.getForAssessmentAndType(assessment, DeliveryType.SYSTEM_DEMO);
        if (systemDemoDeliveries.size()!=1) {
            throw new QtiWorksLogicException("Expected system sample Assessment with ID " + aid
                    + " to have exactly 1 system demo deliverable associated with it");
        }
        return systemDemoDeliveries.get(0);
    }

    private Assessment lookupSampleAssessment(final long aid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final Assessment assessment = assessmentDao.requireFindById(aid);
        final User caller = identityService.getCurrentThreadUser();
        if (!assessment.isPublic() || assessment.getSampleCategory()==null) {
            throw new PrivilegeException(caller, Privilege.LAUNCH_ASSESSMENT_AS_SAMPLE, assessment);
        }
        return assessment;
    }


    //----------------------------------------------------
    // Single delivery launches (currently LTI only)

    /**
     * Starts a new {@link CandidateSession} for the (LTI) candidate {@link User} accessing a
     * link-level launch on the {@link Delivery} having the given ID (did).
     * <p>
     * Access controls are checked on the {@link Delivery}.
     */
    public CandidateSession createLinkLevelLtiCandidateSession(final LtiUser candidate,
            final String exitUrl, final String lisOutcomeServiceUrl, final String lisResultSourcedid)
            throws PrivilegeException {
        /* Make sure this is the correct type of user */
        Assert.notNull(candidate, "candidate");
        if (candidate.getLtiLaunchType()!=LtiLaunchType.LINK) {
            throw new IllegalArgumentException("Candidate LtiUser must be of type " + LtiLaunchType.LINK);
        }

        /* Extract Delivery to be launched */
        final Delivery delivery = candidate.getDelivery();

        /* Finally make sure delivery is open */
        if (!delivery.isOpen()) {
            throw new PrivilegeException(candidate, Privilege.LAUNCH_CLOSED_DELIVERY, delivery);
        }

        /* Start the session */
        return createCandidateSession(candidate, delivery, false, exitUrl, lisOutcomeServiceUrl, lisResultSourcedid);
    }

    public CandidateSession createDomainLevelLtiCandidateSession(final LtiUser candidate, final LtiResource ltiResource,
            final String exitUrl, final String lisOutcomeServiceUrl, final String lisResultSourcedid)
            throws PrivilegeException {
        Assert.notNull(candidate, "candidate");
        Assert.notNull(ltiResource, "ltiResource");
        if (candidate.getLtiLaunchType()!=LtiLaunchType.DOMAIN) {
            throw new IllegalArgumentException("Candidate LtiUser must be of type " + LtiLaunchType.DOMAIN);
        }

        /* Extract Delivery to be launched from LtiResource */
        final Delivery delivery = ltiResource.getDelivery();

        /* Make sure delivery is open */
        if (!delivery.isOpen()) {
            throw new PrivilegeException(candidate, Privilege.LAUNCH_CLOSED_DELIVERY, delivery);
        }

        /* Will use author mode if candidate is an instructor */
        final boolean authorMode = candidate.getUserRole()==UserRole.INSTRUCTOR;

        /* Start the session */
        return createCandidateSession(candidate, delivery, authorMode, exitUrl, lisOutcomeServiceUrl, lisResultSourcedid);
    }

    //----------------------------------------------------
    // Low-level session creation

    /**
     * Starts new {@link CandidateSession} for the given {@link Delivery}
     * <p>
     * NO ACCESS controls are checked on the {@link Delivery}
     */
    public CandidateSession createCandidateSession(final Delivery delivery, final boolean authorMode,
            final String exitUrl, final String lisOutcomeServiceUrl, final String lisResultSourcedid)
            throws PrivilegeException {
        Assert.notNull(delivery, "delivery");
        final User candidate = identityService.getCurrentThreadUser();
        return createCandidateSession(candidate, delivery, authorMode, exitUrl, lisOutcomeServiceUrl, lisResultSourcedid);
    }

    /**
     * Starts new {@link CandidateSession} for the given {@link User} on the given {@link Delivery}
     * <p>
     * NO ACCESS controls are checked on the {@link User} and {@link Delivery}
     */
    public CandidateSession createCandidateSession(final User candidate, final Delivery delivery,
            final boolean authorMode, final String exitUrl, final String lisOutcomeServiceUrl,
            final String lisResultSourcedid)
            throws PrivilegeException {
        Assert.notNull(candidate, "candidate");
        Assert.notNull(delivery, "delivery");

        /* Make sure Candidate's account is not disabled */
        if (candidate.isLoginDisabled()) {
            throw new PrivilegeException(candidate, Privilege.USER_ACCOUNT_ENABLED, delivery);
        }

        /* Make sure Delivery is runnable */
        if (delivery.getAssessment()==null) {
            throw new PrivilegeException(candidate, Privilege.LAUNCH_INCOMPLETE_DELIVERY, delivery);
        }

        /* If the candidate already has any non-terminated sessions open for this Delivery,
         * then we shall reconnect to the (most recent) session instead of creating a new one.
         */
        final List<CandidateSession> existingSessions = candidateSessionDao.getNonTerminatedForDeliveryAndCandidate(delivery, candidate);
        if (!existingSessions.isEmpty()) {
            final CandidateSession mostRecent = existingSessions.get(existingSessions.size()-1);
            auditLogger.recordEvent("Reconnected to CandidateSession #" + mostRecent.getId()
                    + " on Delivery #" + delivery.getId());
            return mostRecent;
        }

        /* Now branch depending on whether this is an item or test */
        final Assessment assessment = delivery.getAssessment();
        switch (assessment.getAssessmentType()) {
            case ASSESSMENT_ITEM:
                return createCandidateItemSession(candidate, delivery, authorMode, exitUrl, lisOutcomeServiceUrl, lisResultSourcedid);

            case ASSESSMENT_TEST:
                return createCandidateTestSession(candidate, delivery, authorMode, exitUrl, lisOutcomeServiceUrl, lisResultSourcedid);

            default:
                throw new QtiWorksLogicException("Unexpected switch case " + assessment.getAssessmentType());
        }
    }

    private CandidateSession createCandidateItemSession(final User candidate, final Delivery delivery,
            final boolean authorMode, final String exitUrl,
            final String lisOutcomeServiceUrl, final String lisResultSourcedid) {
        /* Set up listener to record any notifications */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);

        /* Create fresh JQTI+ state Object and try to create controller */
        final ItemSessionController itemSessionController = candidateDataService.createNewItemSessionStateAndController(candidate, delivery, notificationRecorder);
        if (itemSessionController==null) {
            return handleStartupExplosion(delivery, candidate, exitUrl);
        }

        /* Try to Initialise JQTI+ state */
        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();
        try {
            final Date timestamp = requestTimestampContext.getCurrentRequestTimestamp();
            itemSessionController.initialize(timestamp);
            itemSessionController.performTemplateProcessing(timestamp);
            itemSessionController.enterItem(timestamp);
        }
        catch (final RuntimeException e) {
            return handleStartupExplosion(delivery, candidate, exitUrl);
        }

        /* Create new session entity and put into appropriate initial state */
        final CandidateSession candidateSession = new CandidateSession();
        candidateSession.setSessionToken(ServiceUtilities.createRandomAlphanumericToken(DomainConstants.CANDIDATE_SESSION_TOKEN_LENGTH));
        candidateSession.setExitUrl(exitUrl);
        candidateSession.setLisOutcomeServiceUrl(lisOutcomeServiceUrl);
        candidateSession.setLisResultSourcedid(lisResultSourcedid);
        candidateSession.setCandidate(candidate);
        candidateSession.setDelivery(delivery);
        candidateSession.setAuthorMode(authorMode);
        candidateSession.setClosed(false);
        candidateSessionDao.persist(candidateSession);

        /* Handle immediate end of session */
        if (itemSessionState.isEnded()) {
            candidateSessionCloser.closeCandidateItemSession(candidateSession, itemSessionController);
        }

        /* Record and log event */
        final CandidateEvent candidateEvent = candidateDataService.recordCandidateItemEvent(candidateSession, CandidateItemEventType.ENTER, itemSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        /* Record current result state */
        candidateDataService.computeAndRecordItemAssessmentResult(candidateSession, itemSessionController);

        auditLogger.recordEvent("Created and initialised new CandidateSession #" + candidateSession.getId()
                + " on Delivery #" + delivery.getId());
        return candidateSession;
    }

    //----------------------------------------------------
    // Test session creation

    private CandidateSession createCandidateTestSession(final User candidate, final Delivery delivery,
            final boolean authorMode, final String exitUrl, final String lisOutcomeServiceUrl,
            final String lisResultSourcedid) {
        /* Set up listener to record any notifications */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);

        /* Create fresh JQTI+ state & controller for it */
        final TestSessionController testSessionController = candidateDataService.createNewTestSessionStateAndController(candidate, delivery, notificationRecorder);
        if (testSessionController==null) {
            return handleStartupExplosion(delivery, candidate, exitUrl);
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
            return handleStartupExplosion(delivery, candidate, exitUrl);
        }

        /* Create new session entity and put into appropriate initial state */
        final CandidateSession candidateSession = new CandidateSession();
        candidateSession.setSessionToken(ServiceUtilities.createRandomAlphanumericToken(DomainConstants.CANDIDATE_SESSION_TOKEN_LENGTH));
        candidateSession.setExitUrl(exitUrl);
        candidateSession.setLisOutcomeServiceUrl(lisOutcomeServiceUrl);
        candidateSession.setLisResultSourcedid(lisResultSourcedid);
        candidateSession.setCandidate(candidate);
        candidateSession.setDelivery(delivery);
        candidateSession.setAuthorMode(authorMode);
        candidateSession.setClosed(false);
        candidateSessionDao.persist(candidateSession);

        /* Handle immediate end of session */
        if (testSessionState.isEnded()) {
            candidateSessionCloser.closeCandidateTestSession(candidateSession, testSessionController);
        }

        /* Record and log event */
        final CandidateEvent candidateEvent = candidateDataService.recordCandidateTestEvent(candidateSession,
                CandidateTestEventType.ENTER_TEST, testSessionState, notificationRecorder);
        candidateAuditLogger.logCandidateEvent(candidateEvent);

        /* Record current result state */
        candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);

        auditLogger.recordEvent("Created and initialised new CandidateSession #" + candidateSession.getId()
                + " on Delivery #" + delivery.getId());
        return candidateSession;
    }

    /**
     * Helper to deal with what happens when the JQTI+ init logic throws a {@link RuntimeException}.
     */
    private CandidateSession handleStartupExplosion(final Delivery delivery, final User candidate, final String exitUrl) {
        final CandidateSession candidateSession = new CandidateSession();
        candidateSession.setSessionToken(ServiceUtilities.createRandomAlphanumericToken(DomainConstants.CANDIDATE_SESSION_TOKEN_LENGTH));
        candidateSession.setExitUrl(exitUrl);
        candidateSession.setCandidate(candidate);
        candidateSession.setDelivery(delivery);
        candidateSession.setExploded(true);
        candidateSession.setTerminated(true);
        candidateSessionDao.persist(candidateSession);
        return candidateSession;
    }

}
