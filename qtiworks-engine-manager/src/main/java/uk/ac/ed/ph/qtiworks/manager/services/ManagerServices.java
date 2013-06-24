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
package uk.ac.ed.ph.qtiworks.manager.services;

import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksDeploymentSettings;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.SystemUser;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.DataDeletionService;
import uk.ac.ed.ph.qtiworks.services.base.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.SystemUserDao;
import uk.ac.ed.ph.qtiworks.services.dao.UserDao;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Some useful (probably temporary) services used for bootstrapping the data model.
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class ManagerServices {

    private static final Logger logger = LoggerFactory.getLogger(ManagerServices.class);

    @Resource
    private QtiWorksDeploymentSettings qtiWorksDeploymentSettings;

    @Resource
    private DataDeletionService dataDeletionService;

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private SystemUserDao instructorUserDao;

    @Resource
    private UserDao userDao;

    @Resource
    private AssessmentPackageDao assessmentPackageDao;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    public SystemUser ensureInternalSystemUser(final UserRole userRole,
            final String loginName, final String firstName, final String lastName) {
    	SystemUser result = instructorUserDao.findByLoginName(loginName);
    	if (result==null) {
            result = createSystemUser(userRole, loginName, firstName, lastName,
                    qtiWorksDeploymentSettings.getAdminEmailAddress(),
                    "(Login is disabled)", false, true);
        	logger.info("Created internal system user {}", result);
    	}
        return result;
    }

    /**
     * Creates a new {@link SystemUser} having the given details if there does not already exist an
     * {@link SystemUser} having the given <code>loginName</code>.
     *
     * @return newly created {@link SystemUser}, or null if a user already existed.
     */
    public SystemUser maybeCreateSystemUser(final UserRole userRole, final String loginName, final String firstName,
            final String lastName, final String emailAddress, final boolean sysAdmin, final String password) {
        final SystemUser result = createSystemUserIfRequired(userRole, loginName, firstName, lastName,
                emailAddress, password, sysAdmin, false);
        if (result!=null) {
        	logger.info("Created system user {}", result);
        }
        return result;
    }

    private SystemUser createSystemUserIfRequired(final UserRole userRole, final String loginName, final String firstName,
            final String lastName, final String emailAddress, final String password,
            final boolean sysAdmin, final boolean loginDisabled) {
        final SystemUser result = instructorUserDao.findByLoginName(loginName);
        if (result!=null) {
        	/* User already exists */
        	return null;
        }
        return createSystemUser(userRole, loginName, firstName, lastName, emailAddress, password, sysAdmin, loginDisabled);
    }

    private SystemUser createSystemUser(final UserRole userRole, final String loginName, final String firstName,
            final String lastName, final String emailAddress, final String password,
            final boolean sysAdmin, final boolean loginDisabled) {
    	final String passwordSalt = ServiceUtilities.createSalt();
        final SystemUser result = new SystemUser(userRole);
        result.setLoginName(loginName);
        result.setFirstName(firstName);
        result.setLastName(lastName);
        result.setEmailAddress(emailAddress);
        result.setPasswordSalt(passwordSalt);
        result.setPasswordDigest(ServiceUtilities.computePasswordDigest(passwordSalt, password));
        result.setSysAdmin(sysAdmin);
        result.setLoginDisabled(loginDisabled);
        instructorUserDao.persist(result);
        return result;
    }

    //-------------------------------------------------

    public boolean findAndDeleteUser(final String loginNameOrUid) {
		final User user = findUserByLoginNameOrUid(loginNameOrUid);
		if (user==null) {
		    logger.warn("Could not find user having loginName or ID {}", loginNameOrUid);
            return false;
		}
		logger.info("Deleting user {}", user);
		dataDeletionService.deleteUser(user);
		return true;
    }

    public boolean findAndResetUser(final String loginNameOrUid) {
		/* Try to look up by loginName first */
		final User user = findUserByLoginNameOrUid(loginNameOrUid);
		if (user==null) {
	        logger.warn("Could not find user having loginName or ID {}", loginNameOrUid);
            return false;
		}
		logger.info("Resetting user {}", user);
		dataDeletionService.resetUser(user);
		return true;
    }

    private User findUserByLoginNameOrUid(final String loginNameOrUid) {
		/* Try to look up by loginName first */
		User user = instructorUserDao.findByLoginName(loginNameOrUid);
		if (user==null) {
			/* Try by ID */
			try {
				final long uid = Long.parseLong(loginNameOrUid);
				user = userDao.findById(uid);
			}
			catch (final NumberFormatException e) {
				/* (Continue) */
			}
		}
		return user;
    }

    //-------------------------------------------------
    // Helpers for M4->M5 update

    public int deleteUnusedAssessmentPackages() {
        final List<AssessmentPackage> unusedPackages = assessmentPackageDao.getAllUnused();
        for (final AssessmentPackage assessmentPackage : assessmentPackageDao.getAllUnused()) {
            dataDeletionService.deleteAssessmentPackage(assessmentPackage);
        }
        return unusedPackages.size();
    }

    public void validateAllAssessmentPackages() {
    	for (final AssessmentPackage assessmentPackage : assessmentPackageDao.getAll()) {
    		assessmentManagementService.validateAssessmentPackage(assessmentPackage);
    	}
    }

    public int deleteAllCandidateSessions() {
        final List<CandidateSession> candidateSessions = candidateSessionDao.getAll();
        for (final CandidateSession candidatSession : candidateSessions) {
            dataDeletionService.deleteCandidateSession(candidatSession);
        }
        return candidateSessions.size();
    }
}