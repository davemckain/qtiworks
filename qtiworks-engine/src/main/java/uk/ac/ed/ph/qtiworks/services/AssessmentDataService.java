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
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiContext;
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliverySettingsDao;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentAndPackage;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStatusReport;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryStatusReport;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.ItemDeliverySettingsTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.Privilege;
import uk.ac.ed.ph.qtiworks.services.domain.TestDeliverySettingsTemplate;
import uk.ac.ed.ph.qtiworks.web.lti.LtiAuthenticationTicket;

import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;
import uk.ac.ed.ph.jqtiplus.value.Signature;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bottom layer service providing basic data operations on {@link Assessment} and related
 * entities.
 * <p>
 * This is NO checking of {@link Privilege}s at this level.
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class AssessmentDataService {

    @Resource
    private AssessmentPackageFileService assessmentPackageFileService;

    @Resource
    private IdentityService identityService;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private DeliverySettingsDao deliverySettingsDao;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private AssessmentPackageDao assessmentPackageDao;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    //-------------------------------------------------

    public List<AssessmentAndPackage> getCallerUserAssessments() {
        final User currentUser = identityService.getCurrentThreadUser();
        return assessmentDao.getForOwnerUser(currentUser);
    }

    public List<AssessmentAndPackage> getCallerLtiContextAssessments() {
        return assessmentDao.getForOwnerLtiContext(ensureLtiContext());
    }

    public AssessmentStatusReport getAssessmentStatusReport(final Assessment assessment) {
        final AssessmentPackage assessmentPackage = ensureSelectedAssessmentPackage(assessment);
        final long userCreatedDeliveryCount = countUserCreatedDeliveries(assessment);
        final long sessionCount = candidateSessionDao.countForAssessment(assessment);
        final long candidateRoleSessionCount = candidateSessionDao.countCandidateRoleForAssessment(assessment);
        final long nonTerminatedSessionCount = candidateSessionDao.countNonTerminatedForAssessment(assessment);
        final long nonTerminatedCandidateRoleSessionCount = candidateSessionDao.countNonTerminatedCandidateRoleForAssessment(assessment);
        return new AssessmentStatusReport(assessment, assessmentPackage, userCreatedDeliveryCount,
                sessionCount, candidateRoleSessionCount,
                nonTerminatedSessionCount, nonTerminatedCandidateRoleSessionCount);
    }

    public DeliveryStatusReport getDeliveryStatusReport(final Delivery delivery) {
        final long sessionCount = candidateSessionDao.countForDelivery(delivery);
        final long nonTerminatedSessionCount = candidateSessionDao.countNonTerminatedForDelivery(delivery);
        return new DeliveryStatusReport(delivery, sessionCount, nonTerminatedSessionCount);
    }

    //-------------------------------------------------

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
    public AssessmentPackage ensureSelectedAssessmentPackage(final Delivery delivery) {
        Assert.notNull(delivery, "delivery");
        return ensureSelectedAssessmentPackage(delivery.getAssessment());
    }

    //-------------------------------------------------
    // Validation

    public AssessmentObjectValidationResult<?> validateAssessment(final Assessment assessment) {
        final AssessmentPackage currentAssessmentPackage = ensureSelectedAssessmentPackage(assessment);
        return validateAssessmentPackage(currentAssessmentPackage);
    }

    public AssessmentObjectValidationResult<?> validateAssessmentPackage(final AssessmentPackage assessmentPackage) {
        /* Run the validation process */
        final AssessmentObjectValidationResult<?> validationResult = assessmentPackageFileService.loadAndValidateAssessment(assessmentPackage);

        /* Persist results (stored in entity) */
        assessmentPackageDao.update(assessmentPackage);

        return validationResult;
    }

    //-------------------------------------------------

    public List<OutcomeDeclaration> getOutcomeVariableDeclarations(final Assessment assessment) {
        Assert.notNull(assessment, "assessment");
        final AssessmentPackage assessmentPackage = ensureSelectedAssessmentPackage(assessment);
        return getOutcomeVariableDeclarations(assessmentPackage);
    }

    public List<OutcomeDeclaration> getOutcomeVariableDeclarations(final AssessmentPackage assessmentPackage) {
        Assert.notNull(assessmentPackage, "assessmentPackage");
        List<OutcomeDeclaration> result = null;
        switch (assessmentPackage.getAssessmentType()) {
            case ASSESSMENT_ITEM:
                final ResolvedAssessmentItem resolvedAssessmentItem = assessmentPackageFileService.loadAndResolveAssessmentObject(assessmentPackage);
                final AssessmentItem assessmentItem = resolvedAssessmentItem.getItemLookup().extractIfSuccessful();
                if (assessmentItem!=null) {
                    result = assessmentItem.getOutcomeDeclarations();
                }
                break;

            case ASSESSMENT_TEST:
                final ResolvedAssessmentTest resolvedAssessmentTest = assessmentPackageFileService.loadAndResolveAssessmentObject(assessmentPackage);
                final AssessmentTest assessmentTest = resolvedAssessmentTest.getTestLookup().extractIfSuccessful();
                if (assessmentTest!=null) {
                    result = assessmentTest.getOutcomeDeclarations();
                }
                break;

            default:
                throw new QtiWorksLogicException("Unexpected switch case " + assessmentPackage.getAssessmentType());
        }
        return filterSingleFloatOutcomeDeclarations(result);
    }

    private List<OutcomeDeclaration> filterSingleFloatOutcomeDeclarations(final List<OutcomeDeclaration> outcomeDeclarations) {
        if (outcomeDeclarations==null) {
            return null;
        }
        final List<OutcomeDeclaration> result = new ArrayList<OutcomeDeclaration>(outcomeDeclarations.size());
        for (final OutcomeDeclaration outcomeDeclaration : outcomeDeclarations) {
            if (outcomeDeclaration.hasSignature(Signature.SINGLE_FLOAT)) {
                result.add(outcomeDeclaration);
            }
        }
        return result;
    }

    //-------------------------------------------------

    public List<Delivery> getUserCreatedDeliveries(final Assessment assessment) {
        return deliveryDao.getForAssessmentAndType(assessment, DeliveryType.USER_CREATED);
    }

    public long countUserCreatedDeliveries(final Assessment assessment) {
        return deliveryDao.countForAssessmentAndType(assessment, DeliveryType.USER_CREATED);
    }

    //-------------------------------------------------

    public List<DeliverySettings> getCallerUserDeliverySettings() {
        return deliverySettingsDao.getForOwnerUser(identityService.getCurrentThreadUser());
    }

    public List<DeliverySettings> getCallerUserDeliverySettingsForType(final AssessmentObjectType assessmentType) {
        return deliverySettingsDao.getForOwnerUserAndType(identityService.getCurrentThreadUser(), assessmentType);
    }

    public long countCallerUserDeliverySettings(final AssessmentObjectType assessmentType) {
        return deliverySettingsDao.countForOwnerUserAndType(identityService.getCurrentThreadUser(), assessmentType);
    }

    public List<DeliverySettings> getCallerLtiContextDeliverySettings() {
        return deliverySettingsDao.getForOwnerLtiContext(ensureLtiContext());
    }

    public List<DeliverySettings> getCallerLtiContextDeliverySettingsForType(final AssessmentObjectType assessmentType) {
        return deliverySettingsDao.getForOwnerLtiContextAndType(ensureLtiContext(), assessmentType);
    }

    public long countCallerLtiContextDeliverySettings(final AssessmentObjectType assessmentType) {
        return deliverySettingsDao.countForOwnerLtiContextAndType(ensureLtiContext(), assessmentType);
    }

    private LtiContext ensureLtiContext() {
        final LtiAuthenticationTicket ltiAuthenticationTicket = identityService.ensureCurrentThreadLtiAuthenticationTicket();
        return ltiAuthenticationTicket.getLtiContext();
    }

    //-------------------------------------------------

    public DeliveryTemplate createDeliveryTemplate(final Assessment assessment) {
        final DeliveryTemplate template = new DeliveryTemplate();
        final long existingDeliveryCount = countUserCreatedDeliveries(assessment);
        template.setTitle("Delivery #" + (existingDeliveryCount+1));
        template.setDsid(null);
        return template;
    }

    public ItemDeliverySettingsTemplate createItemDeliverySettingsTemplate() {
        final ItemDeliverySettingsTemplate template = new ItemDeliverySettingsTemplate();
        template.setTitle("Item Delivery Settings");
        template.setAllowEnd(true);
        template.setAllowHardResetWhenEnded(true);
        template.setAllowHardResetWhenOpen(true);
        template.setAllowSoftResetWhenEnded(true);
        template.setAllowSoftResetWhenOpen(true);
        template.setAllowSolutionWhenEnded(true);
        template.setAllowSolutionWhenOpen(true);
        template.setMaxAttempts(0);
        template.setPrompt(null);
        return template;
    }

    public TestDeliverySettingsTemplate createTestDeliverySettingsTemplate() {
        final TestDeliverySettingsTemplate template = new TestDeliverySettingsTemplate();
        template.setTitle("Test Delivery Settings");
        return template;
    }

    public DeliverySettings createDefaultDeliverySettings(final User assessmentRunner, final AssessmentObjectType assessmentType) {
        switch (assessmentType) {
            case ASSESSMENT_ITEM: {
                final ItemDeliverySettingsTemplate template = createItemDeliverySettingsTemplate();
                final ItemDeliverySettings itemDeliverySettings = new ItemDeliverySettings();
                mergeItemDeliverySettings(template, itemDeliverySettings);
                itemDeliverySettings.setTitle("Default item delivery settings");
                if (assessmentRunner.getUserRole()==UserRole.INSTRUCTOR) {
                    itemDeliverySettings.setPrompt("This assessment item is being delivered using a set of. default 'delivery settings'."
                            + " You probably want to create and use your own settings here.");
                }
                else {
                    itemDeliverySettings.setPrompt("This assessment item is being delivered using a set of default 'delivery settings'."
                            + " You can create and use your settings when logged into QTIWorks"
                            + " via its LTI instructor connector or with an explicit QTIWorks system account.");
                }
                return itemDeliverySettings;
            }

            case ASSESSMENT_TEST: {
                final TestDeliverySettingsTemplate template = createTestDeliverySettingsTemplate();
                final TestDeliverySettings testDeliverySettings = new TestDeliverySettings();
                mergeTestDeliverySettings(template, testDeliverySettings);
                testDeliverySettings.setTitle("Default test delivery settings");

                return testDeliverySettings;
            }

            default:
                throw new QtiLogicException("Unexpected switch case " + assessmentType);
        }
    }

    public void mergeItemDeliverySettings(final ItemDeliverySettingsTemplate template, final ItemDeliverySettings target) {
        target.setTitle(template.getTitle().trim());
        target.setTemplateProcessingLimit(template.getTemplateProcessingLimit());
        target.setAllowEnd(template.isAllowEnd());
        target.setAllowHardResetWhenEnded(template.isAllowHardResetWhenEnded());
        target.setAllowHardResetWhenOpen(template.isAllowHardResetWhenOpen());
        target.setAllowSoftResetWhenEnded(template.isAllowSoftResetWhenEnded());
        target.setAllowSoftResetWhenOpen(template.isAllowSoftResetWhenOpen());
        target.setAllowSolutionWhenEnded(template.isAllowSolutionWhenEnded());
        target.setAllowSolutionWhenOpen(template.isAllowSolutionWhenOpen());
        target.setAllowCandidateComment(template.isAllowCandidateComment());
        target.setMaxAttempts(template.getMaxAttempts());
        target.setPrompt(StringUtilities.nullIfEmpty(template.getPrompt()));
    }

    public void mergeItemDeliverySettings(final ItemDeliverySettings template, final ItemDeliverySettingsTemplate target) {
        target.setTitle(template.getTitle());
        target.setTemplateProcessingLimit(template.getTemplateProcessingLimit());
        target.setAllowEnd(template.isAllowEnd());
        target.setAllowHardResetWhenEnded(template.isAllowHardResetWhenEnded());
        target.setAllowHardResetWhenOpen(template.isAllowHardResetWhenOpen());
        target.setAllowSoftResetWhenEnded(template.isAllowSoftResetWhenEnded());
        target.setAllowSoftResetWhenOpen(template.isAllowSoftResetWhenOpen());
        target.setAllowSolutionWhenEnded(template.isAllowSolutionWhenEnded());
        target.setAllowSolutionWhenOpen(template.isAllowSolutionWhenOpen());
        target.setAllowCandidateComment(template.isAllowCandidateComment());
        target.setMaxAttempts(template.getMaxAttempts());
        target.setPrompt(StringUtilities.nullIfEmpty(template.getPrompt()));
    }

    public void mergeTestDeliverySettings(final TestDeliverySettingsTemplate template, final TestDeliverySettings target) {
        target.setTemplateProcessingLimit(template.getTemplateProcessingLimit());
        target.setTitle(template.getTitle().trim());
    }

    public void mergeTestDeliverySettings(final TestDeliverySettings template, final TestDeliverySettingsTemplate target) {
        target.setTemplateProcessingLimit(template.getTemplateProcessingLimit());
        target.setTitle(template.getTitle());
    }

    public DeliverySettings getEffectiveDeliverySettings(final User candidate, final Delivery delivery) {
        Assert.notNull(candidate, "candidate");
        Assert.notNull(delivery, "delivery");
        DeliverySettings result = delivery.getDeliverySettings();
        if (result==null) {
            result = createDefaultDeliverySettings(candidate, delivery.getAssessment().getAssessmentType());
        }
        return result;
    }
}
