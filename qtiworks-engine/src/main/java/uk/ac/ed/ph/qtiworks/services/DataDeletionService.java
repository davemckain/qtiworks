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

import uk.ac.ed.ph.qtiworks.domain.entities.AnonymousUser;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiContext;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiDomain;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.dao.AnonymousUserDao;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateEventDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateEventNotificationDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateFileSubmissionDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateResponseDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionOutcomeDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliverySettingsDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiContextDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiDomainDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiNonceDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiResourceDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiUserDao;
import uk.ac.ed.ph.qtiworks.services.dao.QueuedLtiOutcomeDao;
import uk.ac.ed.ph.qtiworks.services.dao.UserDao;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentAndPackage;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Low-level service for purging assessment and candidate data from the system.
 * <p>
 * (Recall that we have a mix of database- and filesystem-stored data. In general
 * we will log errors rather than fail if FS-stored data unexpectedly can't be deleted,
 * as this makes transaction management much much simpler.)
 * <p>
 * This is NO authorisation at this level.
 * <p>
 * TODO: In the future we might want to make a low-level version of {@link AssessmentManagementService}
 * that does the raw storage work and merge it with this service.
 *
 * FIXME: This class has grown organically and there are too many brittle interdependencies.
 *
 * @see AssessmentManagementService
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class DataDeletionService {

    private static final Logger logger = LoggerFactory.getLogger(DataDeletionService.class);

    @Resource
    private FilespaceManager filespaceManager;

    @Resource
    private AssessmentObjectManagementService assessmentObjectManagementService;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private AnonymousUserDao anonymousUserDao;

    @Resource
    private AssessmentPackageDao assessmentPackageDao;

    @Resource
    private CandidateEventNotificationDao candidateEventNotificationDao;

    @Resource
    private CandidateEventDao candidateEventDao;

    @Resource
    private CandidateFileSubmissionDao candidateFileSubmissionDao;

    @Resource
    private CandidateResponseDao candidateResponseDao;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    @Resource
    private CandidateSessionOutcomeDao candidateSessionOutcomeDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private DeliverySettingsDao deliverySettingsDao;

    @Resource
    private LtiContextDao ltiContextDao;

    @Resource
    private LtiDomainDao ltiDomainDao;

    @Resource
    private LtiUserDao ltiUserDao;

    @Resource
    private LtiNonceDao ltiNonceDao;

    @Resource
    private LtiResourceDao ltiResourceDao;

    @Resource
    private QueuedLtiOutcomeDao queuedLtiOutcomeDao;

    @Resource
    private UserDao userDao;

    /**
     * Deletes the given {@link CandidateSession} and all data that was stored for it.
     */
    public void deleteCandidateSession(final CandidateSession candidateSession) {
        Assert.notNull(candidateSession, "candidateSession");
        logger.info("Deleting candidate session {}", candidateSession.getId());

        /* Delete candidate file uploads & stored state information */
        if (!filespaceManager.deleteCandidateUploads(candidateSession)) {
            logger.error("Failed to delete upload folder for CandidateSession {}", candidateSession.getId());
        }
        if (!filespaceManager.deleteCandidateSessionStore(candidateSession)) {
            logger.error("Failed to delete stored session data for CandidateSession {}", candidateSession.getId());
        }

        /* Delete entities, taking care to do things in the right order.
         * This does not use cascading as it's rather slow.
         */
        queuedLtiOutcomeDao.deleteForCandidateSession(candidateSession);
        candidateSessionOutcomeDao.deleteForCandidateSession(candidateSession);
        candidateFileSubmissionDao.deleteForCandidateSession(candidateSession);
        candidateResponseDao.deleteForCandidateSession(candidateSession);
        candidateEventNotificationDao.deleteForCandidateSession(candidateSession);
        candidateEventDao.deleteForCandidateSession(candidateSession);
        candidateSessionDao.remove(candidateSession);
    }

    /**
     * Deletes all {@link CandidateSession}s launched under the given {@link Delivery}.
     */
    public int deleteCandidateSessions(final Delivery delivery) {
        Assert.notNull(delivery, "delivery");
        logger.info("Deleting candidate sessions for Delivery {}", delivery.getId());

        /* Delete candidate uploads & stored state information */
        if (delivery.getAssessment() != null) {
            if (!filespaceManager.deleteCandidateUploads(delivery)) {
                logger.error("Failed to delete upload folder for Delivery {}", delivery.getId());
            }
            if (!filespaceManager.deleteCandidateSessionData(delivery)) {
                logger.error("Failed to delete stored session data for Delivery {}", delivery.getId());
            }
        }

        /* Delete entities, taking care to do things in the right order.
         * This does not use cascading as it was *very* slow here.
         * Instead, we perform a number of bulk deletions.
         */
        queuedLtiOutcomeDao.deleteForDelivery(delivery);
        candidateSessionOutcomeDao.deleteForDelivery(delivery);
        candidateFileSubmissionDao.deleteForDelivery(delivery);
        candidateResponseDao.deleteForDelivery(delivery);
        candidateEventNotificationDao.deleteForDelivery(delivery);
        candidateEventDao.deleteForDelivery(delivery);
        return candidateSessionDao.deleteForDelivery(delivery);
    }

    /**
     * Deletes the given user-created {@link Delivery}.
     */
    public void deleteDelivery(final Delivery delivery) {
        Assert.notNull(delivery, "delivery");
        logger.info("Deleting Delivery {}", delivery.getId());

        /* Delete all candidate sessions on this Delivery */
        deleteCandidateSessions(delivery);

        /* Delete any LTI link candidate users created when launching this Delivery */
        deleteLtiLinkCandidateUsers(delivery);

        /* Delete entities, taking advantage of cascading */
        deliveryDao.remove(delivery);
    }

    public void deleteAssessmentPackage(final AssessmentPackage assessmentPackage) {
        Assert.notNull(assessmentPackage, "assessmentPackage");
        logger.info("Deleting AssessmentPackage {}", assessmentPackage.getId());

        /* Delete package sandbox in filesystem (if appropriate) */
        if (assessmentPackage.getSandboxPath()!=null) {
            if (!filespaceManager.deleteAssessmentPackageSandbox(assessmentPackage)) {
                logger.error("Failed to delete sandbox for AssessmentPackage {}", assessmentPackage.getId());
            }
        }

        /* Purge any cached data from this package */
        assessmentObjectManagementService.purge(assessmentPackage);

        /* Delete entities, taking advantage of cascading */
        assessmentPackageDao.remove(assessmentPackage); /* (This will cascade) */
    }

    public void deleteAssessment(final Assessment assessment) {
        Assert.notNull(assessment, "assessment");
        logger.info("Deleting Assessment {}", assessment.getId());

        /* NB: The ordering is important here due to the bi-directional relationship
         * between Assessment and AssessmentPackage. Don't try to optimise this away
         * without testing! Similarly with Deliveries and Assessments!
         */

        /* Unlink Assessment from each AssessmentPackage */
        final List<AssessmentPackage> assessmentPackages = assessment.getAssessmentPackages();
        for (final AssessmentPackage assessmentPackage : assessmentPackages) {
            assessmentPackage.setAssessment(null);
            assessmentPackageDao.update(assessmentPackage);
        }

        /* Unlink Assessment from attached Deliveries, and delete any CandidateSessions and
         * LTI link candidates created for that Delivery. */
        final List<Delivery> deliveries = assessment.getDeliveries();
        for (final Delivery delivery : deliveries) {
            deleteCandidateSessions(delivery);
            deleteLtiLinkCandidateUsers(delivery);
            delivery.setAssessment(null);
            deliveryDao.update(delivery);
        }

        /* Delete Assessment entity */
        assessmentDao.remove(assessment);

        /* Now delete each AssessmentPackage */
        for (final AssessmentPackage assessmentPackage : assessmentPackages) {
            deleteAssessmentPackage(assessmentPackage);
        }

        /* Finally delete the Deliveries entities enumerated above.
         * (We excluding any LTI_RESOURCE Deliveries, as these ones have the Assessment attached
         * to the Delivery.)
         */
        for (final Delivery delivery : deliveries) {
            if (delivery.getDeliveryType()!=DeliveryType.LTI_RESOURCE) {
                deliveryDao.remove(delivery); /* (This will cascade) */
            }
        }
    }

    public void deleteLtiResource(final LtiResource ltiResource) {
        Assert.notNull(ltiResource, "ltiResource");
        logger.info("Deleting LtiResource {}", ltiResource.getId());

        /* Delete Delivery matched to this entity */
        final Delivery delivery = ltiResource.getDelivery();
        if (delivery!=null) {
            deleteDelivery(delivery);
        }

        /* Delete LTIResource entity */
        ltiResourceDao.remove(ltiResource);
    }

    public void deleteLtiContext(final LtiContext ltiContext) {
        Assert.notNull(ltiContext, "ltiContext");
        logger.info("Deleting LtiContext {}", ltiContext.getId());

        /* First delete Assessments created in this context */
        for (final AssessmentAndPackage assessmentAndPackage : assessmentDao.getForOwnerLtiContext(ltiContext)) {
            deleteAssessment(assessmentAndPackage.getAssessment());
        }

        /* ... next we can delete LtiResources created in this context */
        final List<LtiResource> ltiResources = ltiResourceDao.getForLtiContext(ltiContext);
        for (final LtiResource ltiResource : ltiResources) {
            deleteLtiResource(ltiResource);
        }

        /* ... can then safely delete DeliverySettings created in this context */
        for (final DeliverySettings deliverySettings : deliverySettingsDao.getForOwnerLtiContext(ltiContext)) {
            deliverySettingsDao.remove(deliverySettings);
        }

        /* Finally delete the entity itself */
        ltiContextDao.remove(ltiContext);
    }

    public void deleteLtiDomain(final LtiDomain ltiDomain) {
        Assert.notNull(ltiDomain, "ltiDomain");
        logger.info("Deleting LtiDomain {}", ltiDomain.getId());

        /* Delete LTI contexts in this domain */
        final List<LtiContext> ltiContexts = ltiContextDao.getForLtiDomain(ltiDomain);
        for (final LtiContext ltiContext : ltiContexts) {
            deleteLtiContext(ltiContext);
        }

        /* Delete all users created under this domain */
        final List<LtiUser> ltiUsers = ltiUserDao.getForLtiDomain(ltiDomain);
        for (final LtiUser ltiUser : ltiUsers) {
            logger.info("Deleting user {}", ltiUser.getId());
            ltiUserDao.remove(ltiUser);
        }

        /* Finally delete the entity itself */
        ltiDomainDao.remove(ltiDomain);
    }

    /* NB: This method is called AFTER candidate session data is removed, so there are no
     * associations and it's a trivial deletion.
     *
     * This behaviour would need to change if this method was called at a
     * different time.
     */
    private int deleteLtiLinkCandidateUsers(final Delivery delivery) {
        Assert.notNull(delivery, "delivery");

        final List<LtiUser> ltiUsers = ltiUserDao.getCandidatesForLinkDelivery(delivery);
        for (final LtiUser ltiUser : ltiUsers) {
            logger.info("Deleting user {}", ltiUser.getId());
            ltiUserDao.remove(ltiUser);
        }
        return ltiUsers.size();
    }

    /**
     * Deletes the given {@link User} from the system, removing all data owned
     * or accumulated by it.
     *
     * @see #resetUser(User)
     *
     * @param user User to delete, which must not be null
     */
    public void deleteUser(final User user) {
        Assert.notNull(user, "user");
        logger.info("Deleting user {}", user.getBusinessKey());

        resetUser(user);
        userDao.remove(user);
    }

    /**
     * "Resets" the given {@link User} from the system, removing all data owned
     * or accumulated by it.
     * <p>
     * NOTE: This needs further testing to make sure it copes with the slightly different
     * ownership model used for domain-level LTI.
     *
     * @see #deleteUser(User)
     *
     * @param user User to reset, which must not be null
     */
    public void resetUser(final User user) {
        Assert.notNull(user, "user");
        logger.info("Resetting user {}", user.getBusinessKey());

        for (final LtiResource ltiResource : ltiResourceDao.getForCreatorUser(user)) {
            deleteLtiResource(ltiResource);
        }
        for (final AssessmentAndPackage item : assessmentDao.getForOwnerUser(user)) {
            deleteAssessment(item.getAssessment());
        }
        for (final CandidateSession candidateSession : candidateSessionDao.getForCandidate(user)) {
            deleteCandidateSession(candidateSession);
        }
        for (final DeliverySettings deliverySettings : deliverySettingsDao.getForOwnerUser(user)) {
            deliverySettingsDao.remove(deliverySettings);
        }
        if (!filespaceManager.deleteAssessmentPackageSandboxes(user)) {
            logger.error("Failed to delete AssessmentPackage sandboxes for user {}", user);
        }
    }

    /**
     * Deletes old transient Deliveries created for the given User, removing all data associated
     * with them.
     * <p>
     * Returns the number of deliveries deleted
     *
     * @param latestCreationTime cut-off creation time for deleting old {@link Delivery} entities
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public int deleteTransientDeliveries(final User user, final Date latestCreationTime) {
        int deleted = 0;
        for (final Delivery delivery : deliveryDao.getForOwnerAndTypeCreatedBefore(user, DeliveryType.USER_TRANSIENT, latestCreationTime)) {
            deleteDelivery(delivery);
            deleted++;
        }
        return deleted;
    }

    /**
     * Deletes old transient Deliveries for all Users, removing all data associated
     * with them.
     * <p>
     * Returns the number of deliveries deleted
     *
     * @param latestCreationTime cut-off creation time for deleting old {@link Delivery} entities
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public int deleteTransientDeliveries(final Date latestCreationTime) {
        int deleted = 0;
        for (final Delivery delivery : deliveryDao.getForTypeCreatedBefore(DeliveryType.USER_TRANSIENT, latestCreationTime)) {
            deleteDelivery(delivery);
            deleted++;
        }
        return deleted;
    }

    /**
     * Deletes all {@link AnonymousUser}s created before the given time, removing
     * all data owner or accumulated by them.
     * <p>
     * Returns the number of users deleted.
     *
     * @param latestCreationTime cut-off creation time for deleting old {@link User} entities
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public int deleteAnonymousUsers(final Date latestCreationTime) {
        int deleted = 0;
        for (final AnonymousUser toDelete : anonymousUserDao.getCreatedBefore(latestCreationTime)) {
            deleteUser(toDelete);
            deleted++;
        }
        return deleted;
    }

    /**
     * Convenience method that calls both {@link #deleteAnonymousUsers(Date)} and
     * {@link #deleteTransientDeliveries(Date)} to purge all anonymous users and transient
     * deliveries that were created before the given time, removing all associated data is removed.
     */
    public void purgeTransientData(final Date creationTimeThreshold) {
        final int usersDeleted = deleteAnonymousUsers(creationTimeThreshold);
        if (usersDeleted>0) {
            logger.info("Purged {} anonymous users from the system", usersDeleted);
        }
        final int transientDeliveriesDeleted = deleteTransientDeliveries(creationTimeThreshold);
        if (transientDeliveriesDeleted>0) {
            logger.info("Purged {} transient deliveries from the system", transientDeliveriesDeleted);
        }
    }

    /**
     * Purges all LTI candidate users who are no longer associated with any {@link CandidateSession}s.
     */
    public void purgeOrphanedLtiCandidateUsers() {
        /* NB: These users don't have any associations, so can be deleted by a single query */
        final int usersDeletedCount = ltiUserDao.deleteCandidatesWithNoSessions();
        if (usersDeletedCount>0) {
            logger.info("Deleted {} LTI orphaned candidate users", usersDeletedCount);
        }
    }

    /**
     * Purges all stored LTI nonces older than the given threshold.
     */
    public void purgeOldNonces(final Date nonceThreshold) {
        final int noncesDeletedCount = ltiNonceDao.deleteOldNonces(nonceThreshold);
        if (noncesDeletedCount>0) {
            logger.info("Deleted {} LTI nonces", noncesDeletedCount);
        }
    }

}
