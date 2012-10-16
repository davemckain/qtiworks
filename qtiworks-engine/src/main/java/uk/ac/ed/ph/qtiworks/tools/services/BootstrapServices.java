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
package uk.ac.ed.ph.qtiworks.tools.services;

import uk.ac.ed.ph.qtiworks.base.services.QtiWorksSettings;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.dao.DeliverySettingsDao;
import uk.ac.ed.ph.qtiworks.domain.dao.InstructorUserDao;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;

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
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
@Service
public class BootstrapServices {

    private static final Logger logger = LoggerFactory.getLogger(BootstrapServices.class);

    @Resource
    private QtiWorksSettings qtiWorksSettings;

    @Resource
    private InstructorUserDao instructorUserDao;

    @Resource
    private DeliverySettingsDao deliverySettingsDao;

    public InstructorUser createInternalSystemUser(final String loginName, final String firstName,
            final String lastName) {
        final InstructorUser user = createUserIfRequired(loginName, firstName, lastName,
                qtiWorksSettings.getEmailAdminAddress(), "(Login is disabled)", false, true);
        logger.info("Created internal system user {}", user);
        return user;
    }

    public InstructorUser createInstructorUser(final String loginName, final String firstName,
            final String lastName, final String emailAddress, final boolean sysAdmin, final String password) {
        final InstructorUser user = createUserIfRequired(loginName, firstName, lastName,
                emailAddress, password, sysAdmin, false);
        logger.info("Created instructor user {}", user);
        return user;
    }

    private InstructorUser createUserIfRequired(final String loginName, final String firstName,
            final String lastName, final String emailAddress, final String password,
            final boolean sysAdmin, final boolean loginDisabled) {
        InstructorUser result = instructorUserDao.findByLoginName(loginName);
        if (result==null) {
            final String passwordSalt = ServiceUtilities.createSalt();
            result = new InstructorUser();
            result.setLoginName(loginName);
            result.setFirstName(firstName);
            result.setLastName(lastName);
            result.setEmailAddress(emailAddress);
            result.setPasswordSalt(passwordSalt);
            result.setPasswordDigest(ServiceUtilities.computePasswordDigest(passwordSalt, password));
            result.setSysAdmin(sysAdmin);
            result.setLoginDisabled(loginDisabled);
            instructorUserDao.persist(result);
        }
        return result;
    }

    public void setupSystemDefaults() {
        /* Create system defalt user */
        final InstructorUser systemDefaultUser = createInternalSystemUser(DomainConstants.QTI_DEFAULT_OWNER_LOGIN_NAME,
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
        fullSettings.setAllowPlayback(true);
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
}