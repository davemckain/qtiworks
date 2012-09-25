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
import uk.ac.ed.ph.qtiworks.base.services.Auditor;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.Privilege;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemSessionDao;
import uk.ac.ed.ph.qtiworks.domain.dao.ItemDeliveryDao;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSessionState;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserType;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.notification.ModelNotification;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.notification.NotificationType;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO: Document and possibly refactor?
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class CandidateSessionStarter {

    @Resource
    private Auditor auditor;

    @Resource
    private IdentityContext identityContext;

    @Resource
    private CandidateDataServices candidateDataServices;

    @Resource
    private EntityGraphService entityGraphService;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private ItemDeliveryDao itemDeliveryDao;

    @Resource
    private CandidateItemSessionDao candidateItemSessionDao;

    //-------------------------------------------------
    // System samples

    public ItemDelivery lookupSystemSampleDelivery(final long aid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final Assessment assessment = lookupSampleAssessment(aid);
        final List<ItemDelivery> systemDemoDeliveries = itemDeliveryDao.getForAssessmentAndType(assessment, DeliveryType.SYSTEM_DEMO);
        if (systemDemoDeliveries.size()!=1) {
            throw new QtiWorksLogicException("Expected system sample Assessment with ID " + aid
                    + " to have exactly 1 system demo deliverable associated with it");
        }
        return systemDemoDeliveries.get(0);
    }

    private Assessment lookupSampleAssessment(final long aid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final Assessment assessment = assessmentDao.requireFindById(aid);
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!assessment.isPublic() || assessment.getSampleCategory()==null) {
            throw new PrivilegeException(caller, Privilege.LAUNCH_ASSESSMENT_AS_SAMPLE, assessment);
        }
        return assessment;
    }

    //----------------------------------------------------
    // Candidate delivery access

    public ItemDelivery lookupItemDelivery(final long did)
            throws DomainEntityNotFoundException, PrivilegeException {
        final ItemDelivery itemDelivery = itemDeliveryDao.requireFindById(did);
        ensureCandidateMayAccess(itemDelivery);
        return itemDelivery;
    }

    /**
     * FIXME: Currently we're only allowing access to public or owned deliveries! This will need
     * to be relaxed in order to allow "real" deliveries to be done.
     */
    private User ensureCandidateMayAccess(final ItemDelivery itemDelivery)
            throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!itemDelivery.isOpen()) {
            throw new PrivilegeException(caller, Privilege.LAUNCH_CLOSED_DELIVERY, itemDelivery);
        }
        final Assessment assessment = itemDelivery.getAssessment();
        if (!(assessment.isPublic()
                || (itemDelivery.isLtiEnabled() && caller.getUserType()==UserType.LTI)
                || caller.equals(assessment.getOwner()))) {
            throw new PrivilegeException(caller, Privilege.LAUNCH_DELIVERY, itemDelivery);
        }
        return caller;
    }

    //----------------------------------------------------
    // Session creation and initialisation

    public CandidateItemSession createSystemSampleSession(final long aid, final String exitUrl)
            throws PrivilegeException, DomainEntityNotFoundException {
        final ItemDelivery sampleItemDelivery = lookupSystemSampleDelivery(aid);
        return createCandidateSession(sampleItemDelivery, exitUrl);
    }

    /**
     * Starts a new {@link CandidateItemSession} for the {@link ItemDelivery}
     * having the given ID (did).
     */
    public CandidateItemSession createCandidateSession(final long did, final String exitUrl)
            throws PrivilegeException, DomainEntityNotFoundException {
        final ItemDelivery itemDelivery = lookupItemDelivery(did);
        return createCandidateSession(itemDelivery, exitUrl);
    }

    /**
     * Starts new {@link CandidateItemSession} for the given {@link ItemDelivery}
     *
     * @param itemDelivery
     *
     * @return
     * @throws PrivilegeException
     */
    public CandidateItemSession createCandidateSession(final ItemDelivery itemDelivery, final String exitUrl)
            throws PrivilegeException {
        Assert.notNull(itemDelivery, "itemDelivery");

        final User candidate = identityContext.getCurrentThreadEffectiveIdentity();

        /* Make sure delivery is open */
        /* FIXME: This prevents instructors from trying out their own sessions! */
        if (!itemDelivery.isOpen()) {
            throw new PrivilegeException(candidate, Privilege.LAUNCH_CLOSED_DELIVERY, itemDelivery);
        }

        /* Make sure underlying Assessment is valid */
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(itemDelivery);
        if (!assessmentPackage.isValid()) {
            throw new PrivilegeException(candidate, Privilege.LAUNCH_INVALID_ASSESSMENT, itemDelivery);
        }

        /* Create fresh JQTI+ state Object */
        final ItemSessionState itemSessionState = new ItemSessionState();

        /* Set up listener to record any notifications */
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);

        /* Initialise state */
        final ItemSessionController itemSessionController = candidateDataServices.createItemSessionController(itemDelivery, itemSessionState, notificationRecorder);
        itemSessionController.initialize();

        /* TEMP! */
        notificationRecorder.onNotification(new ModelNotification(itemSessionController.getItem(), null, NotificationType.RUNTIME, NotificationLevel.INFO, "Hello!"));

        /* Check whether an attempt is allowed. This is a bit pathological here,
         * but it makes sense to be consistent.
         */
        final boolean attemptAllowed = itemSessionController.isAttemptAllowed(itemDelivery.getItemDeliverySettings().getMaxAttempts());

        /* Create new session and put into appropriate initial state */
        final CandidateItemSession candidateSession = new CandidateItemSession();
        candidateSession.setSessionToken(ServiceUtilities.createRandomAlphanumericToken(DomainConstants.CANDIDATE_SESSION_TOKEN_LENGTH));
        candidateSession.setExitUrl(exitUrl);
        candidateSession.setCandidate(candidate);
        candidateSession.setItemDelivery(itemDelivery);
        candidateSession.setState(attemptAllowed ? CandidateSessionState.INTERACTING : CandidateSessionState.CLOSED);
        candidateItemSessionDao.persist(candidateSession);

        /* Record initialisation event */
        candidateDataServices.recordCandidateItemEvent(candidateSession, CandidateItemEventType.INIT, itemSessionState, notificationRecorder);

        auditor.recordEvent("Created and initialised new CandidateItemSession #" + candidateSession.getId()
                + " on ItemDelivery #" + itemDelivery.getId());
        return candidateSession;
    }
}
