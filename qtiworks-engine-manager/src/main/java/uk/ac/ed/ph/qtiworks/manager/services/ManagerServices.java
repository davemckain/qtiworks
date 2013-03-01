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
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.DataDeletionService;
import uk.ac.ed.ph.qtiworks.services.base.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.dao.DeliverySettingsDao;
import uk.ac.ed.ph.qtiworks.services.dao.InstructorUserDao;
import uk.ac.ed.ph.qtiworks.services.dao.UserDao;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

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
    private InstructorUserDao instructorUserDao;

    @Resource
    private UserDao userDao;

    @Resource
    private DeliverySettingsDao deliverySettingsDao;

    public InstructorUser ensureInternalSystemUser(final String loginName, final String firstName,
            final String lastName) {
    	InstructorUser result = instructorUserDao.findByLoginName(loginName);
    	if (result==null) {
            result = createUser(loginName, firstName, lastName,
                    qtiWorksDeploymentSettings.getAdminEmailAddress(), "(Login is disabled)", false, true);
        	logger.info("Created internal system user {}", result);
    	}
        return result;
    }

    /**
     * Creates a new {@link InstructorUser} having the given details if there does not already exist an
     * {@link InstructorUser} having the given <code>loginName</code>.
     *
     * @return newly created {@link InstructorUser}, or null if a user already existed.
     */
    public InstructorUser maybeCreateInstructorUser(final String loginName, final String firstName,
            final String lastName, final String emailAddress, final boolean sysAdmin, final String password) {
        final InstructorUser created = createUserIfRequired(loginName, firstName, lastName,
                emailAddress, password, sysAdmin, false);
        if (created!=null) {
        	logger.info("Created instructor user {}", created.getLoginName());
        }
        return created;
    }

    private InstructorUser createUserIfRequired(final String loginName, final String firstName,
            final String lastName, final String emailAddress, final String password,
            final boolean sysAdmin, final boolean loginDisabled) {
        final InstructorUser result = instructorUserDao.findByLoginName(loginName);
        if (result!=null) {
        	/* User already exists */
        	return null;
        }
        return createUser(loginName, firstName, lastName, emailAddress, password, sysAdmin, loginDisabled);
    }

    private InstructorUser createUser(final String loginName, final String firstName,
            final String lastName, final String emailAddress, final String password,
            final boolean sysAdmin, final boolean loginDisabled) {
    	final String passwordSalt = ServiceUtilities.createSalt();
        final InstructorUser result = new InstructorUser();
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


    public void setupSystemDefaults() {
        /* Create system defalt user */
        final InstructorUser systemDefaultUser = ensureInternalSystemUser(DomainConstants.QTI_DEFAULT_OWNER_LOGIN_NAME,
                DomainConstants.QTI_DEFAULT_OWNER_FIRST_NAME, DomainConstants.QTI_DEFAULT_OWNER_LAST_NAME);

        /* Add some default delivery settings (if they don't already exist) */
        if (deliverySettingsDao.getFirstForOwner(systemDefaultUser, AssessmentObjectType.ASSESSMENT_ITEM)==null) {
            importDefaultItemDeliverySettings(systemDefaultUser);
        }
        if (deliverySettingsDao.getFirstForOwner(systemDefaultUser, AssessmentObjectType.ASSESSMENT_TEST)==null) {
            importDefaultTestDeliverySettings(systemDefaultUser);
        }
    }

    private void importDefaultItemDeliverySettings(final InstructorUser systemDefaultUser) {
        /* Full-featured settings */
        final ItemDeliverySettings fullSettings = new ItemDeliverySettings();
        fullSettings.setAllowClose(true);
        fullSettings.setAllowReinitWhenClosed(true);
        fullSettings.setAllowReinitWhenInteracting(true);
        fullSettings.setAllowResetWhenClosed(true);
        fullSettings.setAllowResetWhenInteracting(true);
        fullSettings.setAllowResult(true);
        fullSettings.setAllowSolutionWhenClosed(true);
        fullSettings.setAllowSolutionWhenInteracting(true);
        fullSettings.setAllowSource(true);
        fullSettings.setAuthorMode(true);
        fullSettings.setMaxAttempts(0);
        fullSettings.setPublic(true);
        fullSettings.setOwner(systemDefaultUser);
        fullSettings.setTitle("Example QTI debugging settings");
        fullSettings.setPrompt("These delivery settings let the candidate do pretty much anything, "
                + "so might be very useful for debugging QTI items. "
                + "Just remember that some features will only make sense if the item has been authored to support it, "
                + "such as model solutions and re-initialisation.");
        deliverySettingsDao.persist(fullSettings);

        /* Summative example settings */
        final ItemDeliverySettings summativeSettings = new ItemDeliverySettings();
        summativeSettings.setAllowClose(true);
        summativeSettings.setMaxAttempts(1);
        summativeSettings.setPublic(true);
        summativeSettings.setOwner(systemDefaultUser);
        summativeSettings.setTitle("Example summative settings");
        summativeSettings.setPrompt("These delivery settings allow the candidate to make only 1 attempt "
                + "at the item, and lock down many of the optional features. You might use something like "
                + "this for summative assessment.");
        deliverySettingsDao.persist(summativeSettings);
    }

    /** FIXME: Add in some more examples later */
    private void importDefaultTestDeliverySettings(final InstructorUser systemDefaultUser) {
        /* Full-featured settings */
        final TestDeliverySettings fullSettings = new TestDeliverySettings();
        fullSettings.setAuthorMode(true);
        fullSettings.setPublic(true);
        fullSettings.setOwner(systemDefaultUser);
        fullSettings.setTitle("Example QTI debugging settings for tests");
        deliverySettingsDao.persist(fullSettings);
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
}