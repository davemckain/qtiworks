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
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.dao.AnonymousUserDao;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliverySettingsDao;
import uk.ac.ed.ph.qtiworks.services.dao.UserDao;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.util.Date;

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
 * This is not authenticated.
 * <p>
 * TODO: In the future we might want to make a low-level version of {@link AssessmentManagementService}
 * that does the raw storage work and merge it with this service.
 *
 * @see EntityGraphService
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
    private CandidateSessionDao candidateSessionDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private DeliverySettingsDao deliverySettingsDao;

    @Resource
    private AssessmentPackageDao assessmentPackageDao;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private AnonymousUserDao anonymousUserDao;

    @Resource
    private UserDao userDao;

    /**
     * Deletes the given {@link CandidateSession} and all data that was stored for it.
     * @param candidateSession
     */
    public void deleteCandidateSession(final CandidateSession candidateSession) {
        Assert.notNull(candidateSession, "candidateSession");

        /* Delete candidate uploads & stored state information */
        if (!filespaceManager.deleteCandidateUploads(candidateSession)) {
            logger.error("Failed to delete upload folder for CandidateSession {}", candidateSession.getId());
        }
        if (!filespaceManager.deleteCandidateSessionStore(candidateSession)) {
            logger.error("Failed to delete stored session data for CandiateSession {}", candidateSession.getId());
        }

        /* Delete entities, taking advantage of cascading */
        candidateSessionDao.remove(candidateSession); /* (This will cascade) */
    }

    public void deleteDelivery(final Delivery delivery) {
        Assert.notNull(delivery, "delivery");

        /* Delete candidate uploads & stored state information */
        if (!filespaceManager.deleteCandidateUploads(delivery)) {
            logger.error("Failed to delete upload folder for Delivery {}", delivery.getId());
        }
        if (!filespaceManager.deleteCandidateSessionData(delivery)) {
            logger.error("Failed to delete stored session data for Delivery {}", delivery.getId());
        }

        /* Delete entities, taking advantage of cascading */
        deliveryDao.remove(delivery); /* (This will cascade) */
    }

    public void deleteAssessmentPackage(final AssessmentPackage assessmentPackage) {
        Assert.notNull(assessmentPackage, "assessmentPackage");

        /* Delete package sandbox in filesystem (if appropriate) */
        if (assessmentPackage.getSandboxPath()!=null) {
            if (!filespaceManager.deleteAssessmentPackageSandbox(assessmentPackage)) {
                logger.error("Failed to delete sandbox for AssessmentPackage {}", assessmentPackage.getId());
            }
        }

        /* Delete entities, taking advantage of cascading */
        assessmentPackageDao.remove(assessmentPackage); /* (This will cascade) */
    }

    public void deleteAssessment(final Assessment assessment) {
        Assert.notNull(assessment, "assessment");

        /* Delete sandboxes for all packages */
        for (final AssessmentPackage assessmentPackage : assessment.getAssessmentPackages()) {
            if (assessmentPackage.getSandboxPath()!=null) {
                if (!filespaceManager.deleteAssessmentPackageSandbox(assessmentPackage)) {
                    logger.error("Failed to delete sandbox for AssessmentPackage {}", assessmentPackage.getId());
                }
            }
        }

        /* Delete entities, taking advantage of cascading */
        assessmentDao.remove(assessment); /* (This will cascade) */
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
        resetUser(user);
        userDao.remove(user);
    }

    /**
     * "Resets" the given {@link User} from the system, removing all data owned
     * or accumulated by it.
     *
     * @see #deleteUser(User)
     *
     * @param user User to reset, which must not be null
     */
    public void resetUser(final User user) {
        Assert.notNull(user, "user");

        for (final Assessment assessment : assessmentDao.getForOwner(user)) {
            deleteAssessment(assessment);
        }
        for (final CandidateSession candidateSession : candidateSessionDao.getForCandidate(user)) {
            deleteCandidateSession(candidateSession);
        }
        for (final DeliverySettings deliverySettings : deliverySettingsDao.getForOwner(user)) {
            deliverySettingsDao.remove(deliverySettings);
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
    public void purgeAnonymousData(final Date creationTimeThreshold) {
        final int usersDeleted = deleteAnonymousUsers(creationTimeThreshold);
        if (usersDeleted > 0) {
            logger.info("Purged {} anonymous users from the system", usersDeleted);
        }
        final int transientDeliveriesDeleted = deleteTransientDeliveries(creationTimeThreshold);
        if (transientDeliveriesDeleted > 0) {
            logger.info("Purged {} transient deliveries from the system", transientDeliveriesDeleted);
        }
    }
}
