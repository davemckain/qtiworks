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
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.Privilege;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliverySettingsDao;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentAndPackage;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bottom layer service that sits just above the DAOs and does more complex lookups and
 * lookups that are aware of the identity of the caller.
 * <p>
 * Pre- and post-conditions are checked as required.
 * <p>
 * This is NO checking of {@link Privilege}s at this level.
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class EntityGraphService {

    @Resource
    private IdentityContext identityContext;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private DeliverySettingsDao deliverySettingsDao;

    @Resource
    private AssessmentDao assessmentDao;

    //-------------------------------------------------

    public List<AssessmentAndPackage> getCallerAssessments() {
        final User currentUser = identityContext.getCurrentThreadUser();
        return assessmentDao.getForOwner(currentUser);
    }

    /**
     * Retrieves the selected {@link AssessmentPackage} for the given {@link Assessment}, making
     * sure that something is set.
     * <p>
     * This will return a non-null result.
     *
     * @throws QtiWorksLogicException if no selected {@link AssessmentPackage}
     */
    public AssessmentPackage ensureSelectedAssessmentPackage(final Assessment assessment) {
        Assert.notNull(assessment, "assessment");
        final AssessmentPackage result = assessment.getSelectedAssessmentPackage();
        if (result==null) {
            throw new QtiWorksLogicException("Expected to always find at least 1 AssessmentPackage associated with an Assessment. Check the JPA-QL query and the logic in this class");
        }
        return result;
    }

    /**
     * Retrieves the selected {@link AssessmentPackage} for the given {@link Delivery},
     * making sure that something is set.
     * <p>
     * This will return a non-null result.
     *
     * @throws QtiWorksLogicException if no selected {@link AssessmentPackage}
     */
    public AssessmentPackage ensureCurrentAssessmentPackage(final Delivery delivery) {
        Assert.notNull(delivery, "delivery");
        return ensureSelectedAssessmentPackage(delivery.getAssessment());
    }

    //-------------------------------------------------

    public List<Delivery> getCallerDeliveries(final Assessment assessment) {
        return deliveryDao.getForAssessmentAndType(assessment, DeliveryType.USER_CREATED);
    }

    public long countCallerDeliveries(final Assessment assessment) {
        return deliveryDao.countForAssessmentAndType(assessment, DeliveryType.USER_CREATED);
    }

    //-------------------------------------------------

    public long countCallerDeliverySettings(final AssessmentObjectType assessmentType) {
        return deliverySettingsDao.countForOwnerAndType(identityContext.getCurrentThreadUser(), assessmentType);
    }

    public List<DeliverySettings> getCallerDeliverySettings() {
        return deliverySettingsDao.getForOwner(identityContext.getCurrentThreadUser());
    }

    public List<DeliverySettings> getCallerDeliverySettingsForType(final AssessmentObjectType assessmentType) {
        return deliverySettingsDao.getForOwnerAndType(identityContext.getCurrentThreadUser(), assessmentType);
    }

}
