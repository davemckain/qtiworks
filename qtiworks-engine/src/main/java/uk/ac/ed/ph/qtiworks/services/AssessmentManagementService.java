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
import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.Privilege;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserType;
import uk.ac.ed.ph.qtiworks.services.base.AuditLogger;
import uk.ac.ed.ph.qtiworks.services.base.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliverySettingsDao;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStateException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStateException.APSFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.ItemDeliverySettingsTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.TestDeliverySettingsTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.UpdateAssessmentCommand;

import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 * Top layer services for *managing* {@link Assessment}s and related entities.
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class AssessmentManagementService {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentManagementService.class);

    public static final String DEFAULT_IMPORT_TITLE = "My Assessment";

    @Resource
    private AuditLogger auditLogger;

    @Resource
    private IdentityContext identityContext;

    @Resource
    private FilespaceManager filespaceManager;

    @Resource
    private Validator jsr303Validator;

    @Resource
    private EntityGraphService entityGraphService;

    @Resource
    private AssessmentPackageFileService assessmentPackageFileService;

    @Resource
    private DataDeletionService dataDeletionService;

    @Resource
    private AssessmentPackageImporter assessmentPackageFileImporter;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private AssessmentPackageDao assessmentPackageDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private DeliverySettingsDao deliverySettingsDao;

    //-------------------------------------------------
    // Assessment access

    /**
     * Looks up the {@link Assessment} having the given ID (aid) and checks that
     * the caller owns it.
     */
    public Assessment lookupOwnAssessment(final long aid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final Assessment result = assessmentDao.requireFindById(aid);
        ensureCallerOwns(result);
        return result;
    }

    /**
     * Looks up the {@link Assessment} having the given ID (aid) and checks that
     * the caller may access it.
     */
    public Assessment lookupAssessment(final long aid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final Assessment result = assessmentDao.requireFindById(aid);
        ensureCallerMayAccess(result);
        return result;
    }

    //-------------------------------------------------

    /**
     * Creates and persists a new {@link Assessment} and initial {@link AssessmentPackage}
     * from the data provided by the given {@link InputStream} and having the given content type.
     * <p>
     * Callers will want to call {@link #validateAssessment(Assessment)} before trying to run
     * this new {@link Assessment}.
     * <p>
     * Success post-conditions:
     * - a new {@link AssessmentPackage} is persisted, and its data is safely stored in a sandbox
     *
     * @param multipartFile data to be imported
     *
     * @throws PrivilegeException if the caller is not allowed to perform this action
     * @throws AssessmentPackageFileImportException
     * @throws QtiWorksRuntimeException
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Assessment importAssessment(final MultipartFile multipartFile)
            throws PrivilegeException, AssessmentPackageFileImportException {
        Assert.notNull(multipartFile, "multipartFile");
        final User caller = ensureCallerMayCreateAssessment();

        /* First, upload the data into a sandbox */
        final AssessmentPackage assessmentPackage = importPackageFiles(multipartFile);
        final Assessment assessment;
        try {
            /* Persist new package (before linking to Assessment) */
            assessmentPackage.setImportVersion(Long.valueOf(1L));
            assessmentPackageDao.persist(assessmentPackage);

            /* Create resulting Assessment entity */
            assessment = new Assessment();
            assessment.setAssessmentType(assessmentPackage.getAssessmentType());
            assessment.setOwner(caller);

            final String fileName = multipartFile.getOriginalFilename();
            String assessmentName;
            if (StringUtilities.isNullOrBlank(fileName)) {
                assessmentName = assessmentPackage.getAssessmentType()==AssessmentObjectType.ASSESSMENT_ITEM ? "Item" : "Test";
            }
            else {
                assessmentName = ServiceUtilities.trimString(fileName, DomainConstants.ASSESSMENT_NAME_MAX_LENGTH);
            }
            assessment.setName(assessmentName);

            /* Guess a title */
            final String guessedTitle = assessmentPackageFileService.guessAssessmentTitle(assessmentPackage);
            final String resultingTitle = !StringUtilities.isNullOrEmpty(guessedTitle) ? guessedTitle : DEFAULT_IMPORT_TITLE;
            assessment.setTitle(ServiceUtilities.trimSentence(resultingTitle, DomainConstants.ASSESSMENT_TITLE_MAX_LENGTH));

            /* Relate Assessment & AssessmentPackage */
            assessment.setSelectedAssessmentPackage(assessmentPackage);
            assessment.setPackageImportVersion(Long.valueOf(1L));
            assessmentPackage.setAssessment(assessment);

            /* Persist/relate entities */
            assessmentDao.persist(assessment);
            assessmentPackageDao.update(assessmentPackage);
        }
        catch (final Exception e) {
            logger.warn("Failed to save new Assessment or AssessmentPackage - deleting sandbox");
            deleteAssessmentPackageSandbox(assessmentPackage);
            throw new QtiWorksRuntimeException("Failed to persist Assessment/AssessmentPackage {}", e);
        }

        logger.debug("Created new Assessment #{} with package #{}", assessment.getId(), assessmentPackage.getId());
        auditLogger.recordEvent("Created Assessment #" + assessment.getId() + " and AssessmentPackage #" + assessmentPackage.getId());
        return assessment;
    }

    /**
     * Deletes the {@link Assessment} having the given aid and owned by the caller.
     *
     * NOTE: This deletes ALL associated data, including candidate data. Use with care!
     */
    public void deleteAssessment(final long aid)
            throws DomainEntityNotFoundException, PrivilegeException {
        /* Look up assessment and check permissions */
        final Assessment assessment = assessmentDao.requireFindById(aid);
        ensureCallerOwns(assessment);

        /* Now delete it and all associated data */
        dataDeletionService.deleteAssessment(assessment);

        /* Log what happened */
        logger.debug("Deleted Assessment #{}", assessment.getId());
        auditLogger.recordEvent("Deleted Assessment #" + assessment.getId());
    }

    public Assessment updateAssessment(final long aid, final UpdateAssessmentCommand command)
            throws BindException, DomainEntityNotFoundException, PrivilegeException {
        /* Validate data */
        Assert.notNull(command, "command");
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(command, "updateAssessmentCommand");
        jsr303Validator.validate(command, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }

        /* Look up Assessment */
        final Assessment assessment = assessmentDao.requireFindById(aid);
        ensureCallerMayChange(assessment);

        /* Make changes */
        assessment.setName(command.getName().trim());
        assessment.setTitle(command.getTitle().trim());
        assessmentDao.update(assessment);
        return assessment;
    }

    /**
     * Imports a new {@link AssessmentPackage}, making it the selected one for
     * the given {@link Assessment}. Any existing {@link AssessmentPackage}s
     * will be deleted.
     * <p>
     * The new {@link AssessmentPackage} must be of the same type as the
     * {@link Assessment}. I.e. it is not possible to replace an itme with
     * a test, or a test with an item.
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Assessment replaceAssessmentPackage(final long aid,
            final MultipartFile multipartFile)
            throws AssessmentStateException, PrivilegeException,
            AssessmentPackageFileImportException, DomainEntityNotFoundException {
        Assert.notNull(multipartFile, "multipartFile");
        final Assessment assessment = assessmentDao.requireFindById(aid);
        ensureCallerMayChange(assessment);
        final AssessmentPackage oldPackage = assessment.getSelectedAssessmentPackage();

        /* Upload data into a new sandbox */
        final AssessmentPackage newAssessmentPackage = importPackageFiles(multipartFile);

        /* Make sure we haven't gone item->test or test->item */
        if (newAssessmentPackage.getAssessmentType()!=assessment.getAssessmentType()) {
            throw new AssessmentStateException(APSFailureReason.CANNOT_CHANGE_ASSESSMENT_TYPE,
                    assessment.getAssessmentType(), newAssessmentPackage.getAssessmentType());
        }

        /* Join Assessment to new package */
        final long newPackageVersion = assessment.getPackageImportVersion().longValue() + 1;
        assessment.setPackageImportVersion(newPackageVersion);
        assessment.setSelectedAssessmentPackage(newAssessmentPackage);
        newAssessmentPackage.setAssessment(assessment);
        newAssessmentPackage.setImportVersion(newPackageVersion);

        /* Now update DB */
        try {
            assessmentDao.update(assessment);
            assessmentPackageDao.persist(newAssessmentPackage);
        }
        catch (final Exception e) {
            logger.warn("Failed to update state of AssessmentPackage {} after file replacement - deleting new sandbox", e);
            deleteAssessmentPackageSandbox(newAssessmentPackage);
            throw new QtiWorksRuntimeException("Failed to update AssessmentPackage entity " + assessment, e);
        }

        /* Finally delete the old package (if applicable) */
        if (oldPackage!=null) {
            dataDeletionService.deleteAssessmentPackage(oldPackage);
        }

        logger.debug("Updated Assessment #{} to have package #{}", assessment.getId(), newAssessmentPackage.getId());
        auditLogger.recordEvent("Updated Assessment #" + assessment.getId() + " with AssessmentPackage #" + newAssessmentPackage.getId());
        return assessment;
    }

    //-------------------------------------------------
    // Validation
    // (These methods arguably belong somewhere as, we're not doing any permission checking here)

    public AssessmentObjectValidationResult<?> validateAssessment(final long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = lookupAssessment(aid);
        return validateAssessment(assessment);
    }

    public AssessmentObjectValidationResult<?> validateAssessment(final Assessment assessment) {
        final AssessmentPackage currentAssessmentPackage = entityGraphService.ensureSelectedAssessmentPackage(assessment);
        return validateAssessmentPackage(currentAssessmentPackage);
    }

    public AssessmentObjectValidationResult<?> validateAssessmentPackage(final AssessmentPackage assessmentPackage) {
        /* Run the validation process */
        final AssessmentObjectValidationResult<?> validationResult = assessmentPackageFileService.loadAndValidateAssessment(assessmentPackage);

        /* Persist results */
        assessmentPackage.setValidated(true);
        assessmentPackage.setLaunchable(validationResult.getResolvedAssessmentObject().getRootNodeLookup().wasSuccessful());
        assessmentPackage.setErrorCount(validationResult.getErrors().size());
        assessmentPackage.setWarningCount(validationResult.getWarnings().size());
        assessmentPackage.setValid(validationResult.isValid());
        assessmentPackageDao.update(assessmentPackage);

        return validationResult;
    }

    //-------------------------------------------------

    /**
     * TODO: Currently only permitting people to see either public Assessments, or
     * their own Assessments.
     */
    private User ensureCallerMayAccess(final Assessment assessment)
            throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!assessment.isPublic() && !assessment.getOwner().equals(caller)) {
            throw new PrivilegeException(caller, Privilege.VIEW_ASSESSMENT, assessment);
        }
        return caller;
    }

    private User ensureCallerOwns(final Assessment assessment)
            throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!assessment.getOwner().equals(caller)) {
            throw new PrivilegeException(caller, Privilege.OWN_ASSESSMENT, assessment);
        }
        return caller;
    }

    private User ensureCallerMayChange(final Assessment assessment)
            throws PrivilegeException {
        return ensureCallerOwns(assessment);
    }

    /**
     * NB: Currently allowing INSTRUCTOR and ANONYMOUS (demo) users to create assignments.
     */
    private User ensureCallerMayCreateAssessment() throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        final UserType userType = caller.getUserType();
        if (!(userType==UserType.ANONYMOUS || userType==UserType.INSTRUCTOR)) {
            throw new PrivilegeException(caller, Privilege.CREATE_ASSESSMENT);
        }
        return caller;
    }

    //-------------------------------------------------
    // Basic CRUD for DeliverySettings

    public DeliverySettings lookupDeliverySettings(final long dsid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final DeliverySettings deliverySettings = deliverySettingsDao.requireFindById(dsid);
        ensureCallerMayAccess(deliverySettings);
        return deliverySettings;
    }

    public DeliverySettings lookupAndMatchDeliverySettings(final long dsid, final Assessment assessment)
            throws DomainEntityNotFoundException, PrivilegeException {
        final DeliverySettings deliverySettings = deliverySettingsDao.requireFindById(dsid);
        ensureCallerMayAccess(deliverySettings);
        ensureCompatible(deliverySettings, assessment);
        return deliverySettings;
    }

    private User ensureCallerMayAccess(final DeliverySettings deliverySettings)
            throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!deliverySettings.isPublic() && !caller.equals(deliverySettings.getOwner())) {
            throw new PrivilegeException(caller, Privilege.ACCESS_DELIVERY_SETTINGS, deliverySettings);
        }
        return caller;
    }

    private User ensureCallerOwns(final DeliverySettings deliverySettings)
            throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!caller.equals(deliverySettings.getOwner())) {
            throw new PrivilegeException(caller, Privilege.OWN_DELIVERY_SETTINGS, deliverySettings);
        }
        return caller;
    }

    private User ensureCallerMayChange(final DeliverySettings deliverySettings)
            throws PrivilegeException {
        return ensureCallerOwns(deliverySettings);
    }

    private User ensureCallerMayCreateDeliverySettings() throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (caller.getUserType()!=UserType.INSTRUCTOR) {
            throw new PrivilegeException(caller, Privilege.CREATE_DELIVERY_SETTINGS);
        }
        return caller;
    }

    //-------------------------------------------------
    // CRUD for ItemDeliverySettings

    public ItemDeliverySettings lookupItemDeliverySettings(final long dsid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final DeliverySettings deliverySettings = deliverySettingsDao.requireFindById(dsid);
        ensureCallerMayAccess(deliverySettings);
        ensureCompatible(deliverySettings, AssessmentObjectType.ASSESSMENT_ITEM);

        return (ItemDeliverySettings) deliverySettings;
    }

    public ItemDeliverySettings createItemDeliverySettings(final ItemDeliverySettingsTemplate template)
            throws PrivilegeException, BindException {
        /* Check caller privileges */
        final User caller = ensureCallerMayCreateDeliverySettings();

        /* Validate template */
        validateItemDeliverySettingsTemplate(template);

        /* Create and persist new options from template */
        final ItemDeliverySettings result = new ItemDeliverySettings();
        result.setOwner(caller);
        mergeItemDeliverySettings(template, result);
        deliverySettingsDao.persist(result);

        auditLogger.recordEvent("Created ItemDeliverySettings #" + result.getId());
        return result;
    }

    private void validateItemDeliverySettingsTemplate(final ItemDeliverySettingsTemplate template)
            throws BindException {
        Assert.notNull(template, "template");
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(template, "itemDeliverySettingsTemplate");
        jsr303Validator.validate(template, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }
    }

    public ItemDeliverySettings updateItemDeliverySettings(final long dsid, final ItemDeliverySettingsTemplate template)
            throws PrivilegeException, DomainEntityNotFoundException, BindException {
        /* Check caller privileges */
        final ItemDeliverySettings itemDeliverySettings = lookupItemDeliverySettings(dsid);
        ensureCallerMayChange(itemDeliverySettings);

        /* Validate template */
        validateItemDeliverySettingsTemplate(template);

        /* Merge template into options and update */
        mergeItemDeliverySettings(template, itemDeliverySettings);
        deliverySettingsDao.update(itemDeliverySettings);

        auditLogger.recordEvent("Updated ItemDeliverySettings #" + itemDeliverySettings.getId());
        return itemDeliverySettings;
    }

    private void mergeItemDeliverySettings(final ItemDeliverySettingsTemplate template, final ItemDeliverySettings target) {
        target.setAuthorMode(template.isAuthorMode());
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
        target.setAuthorMode(template.isAuthorMode());
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

    //-------------------------------------------------
    // CRUD for TestDeliverySettings

    public TestDeliverySettings lookupTestDeliverySettings(final long dsid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final DeliverySettings deliverySettings = deliverySettingsDao.requireFindById(dsid);
        ensureCallerMayAccess(deliverySettings);
        ensureCompatible(deliverySettings, AssessmentObjectType.ASSESSMENT_TEST);

        return (TestDeliverySettings) deliverySettings;
    }

    public TestDeliverySettings createTestDeliverySettings(final TestDeliverySettingsTemplate template)
            throws PrivilegeException, BindException {
        /* Check caller privileges */
        final User caller = ensureCallerMayCreateDeliverySettings();

        /* Validate template */
        validateTestDeliverySettingsTemplate(template);

        /* Create and persist new options from template */
        final TestDeliverySettings result = new TestDeliverySettings();
        result.setOwner(caller);
        mergeTestDeliverySettings(template, result);
        deliverySettingsDao.persist(result);

        auditLogger.recordEvent("Created TestDeliverySettings #" + result.getId());
        return result;
    }

    private void validateTestDeliverySettingsTemplate(final TestDeliverySettingsTemplate template)
            throws BindException {
        Assert.notNull(template, "template");
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(template, "testDeliverySettingsTemplate");
        jsr303Validator.validate(template, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }
    }

    public TestDeliverySettings updateTestDeliverySettings(final long dsid, final TestDeliverySettingsTemplate template)
            throws PrivilegeException, DomainEntityNotFoundException, BindException {
        /* Check caller privileges */
        final TestDeliverySettings testDeliverySettings = lookupTestDeliverySettings(dsid);
        ensureCallerMayChange(testDeliverySettings);

        /* Validate template */
        validateTestDeliverySettingsTemplate(template);

        /* Merge template into options and update */
        mergeTestDeliverySettings(template, testDeliverySettings);
        deliverySettingsDao.update(testDeliverySettings);

        auditLogger.recordEvent("Updated TestDeliverySettings #" + testDeliverySettings.getId());
        return testDeliverySettings;
    }

    private void mergeTestDeliverySettings(final TestDeliverySettingsTemplate template, final TestDeliverySettings target) {
        target.setAuthorMode(template.isAuthorMode());
        target.setTemplateProcessingLimit(template.getTemplateProcessingLimit());
        target.setTitle(template.getTitle().trim());
    }

    public void mergeTestDeliverySettings(final TestDeliverySettings template, final TestDeliverySettingsTemplate target) {
        target.setAuthorMode(template.isAuthorMode());
        target.setTemplateProcessingLimit(template.getTemplateProcessingLimit());
        target.setTitle(template.getTitle());
    }

    //-------------------------------------------------

    public void deleteDeliverySettings(final long dsid)
            throws DomainEntityNotFoundException, PrivilegeException {
        /* Look up entity and check permissions */
        final DeliverySettings deliverySettings = deliverySettingsDao.requireFindById(dsid);
        final User caller = ensureCallerOwns(deliverySettings);

        /* Make sure settings aren't being used */
        if (deliveryDao.countUsingSettings(deliverySettings) > 0) {
            throw new PrivilegeException(caller, deliverySettings, Privilege.DELETE_USED_DELIVERY_SETTINGS);
        }

        /* Delete entity */
        deliverySettingsDao.remove(deliverySettings);

        /* Log what happened */
        logger.debug("Deleted DeliverySettings #{}", deliverySettings.getId());
        auditLogger.recordEvent("Deleted DeliverySettings #" + deliverySettings.getId());
    }

    //-------------------------------------------------
    // CRUD for Delivery
    // (access controls are governed by owning Assessment)

    public Delivery lookupDelivery(final long did)
            throws DomainEntityNotFoundException, PrivilegeException {
        final Delivery delivery = deliveryDao.requireFindById(did);
        ensureCallerMayAccess(delivery.getAssessment());
        return delivery;
    }

    public Delivery lookupOwnDelivery(final long did)
            throws DomainEntityNotFoundException, PrivilegeException {
        final Delivery delivery = deliveryDao.requireFindById(did);
        ensureCallerOwns(delivery.getAssessment());
        return delivery;
    }

    /** Creates a new {@link Delivery} for the given Assignment using reasonable default values */
    public Delivery createDelivery(final long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        /* Look up Assessment and check caller and change it */
        final Assessment assessment = lookupAssessment(aid);
        ensureCallerMayChange(assessment);

        /* Get first DeliverySettings (creating if required) */
        final DeliverySettings deliverySettings = requireFirstDeliverySettingsForCaller(assessment.getAssessmentType());

        /* Create Delivery template with reasonable defaults */
        final DeliveryTemplate template = new DeliveryTemplate();
        final long existingDeliveryCount = entityGraphService.countCallerDeliveries(assessment);
        template.setTitle("Delivery #" + (existingDeliveryCount+1));
        template.setDsid(deliverySettings.getId());
        template.setOpen(false);
        template.setLtiEnabled(false);

        /* Create and return new entity */
        return createDelivery(assessment, deliverySettings, template);
    }

    public Delivery createDelivery(final long aid, final DeliveryTemplate template)
            throws PrivilegeException, DomainEntityNotFoundException, BindException {
        /* Validate template */
        validateDeliveryTemplate(template);

        /* Look up Assessment and check caller and change it */
        final Assessment assessment = lookupAssessment(aid);
        ensureCallerMayChange(assessment);

        /* Look up settings and check privileges */
        final long dsid = template.getDsid();
        final DeliverySettings deliverySettings = lookupAndMatchDeliverySettings(dsid, assessment);

        /* Create and return new entity */
        return createDelivery(assessment, deliverySettings, template);
    }

    private Delivery createDelivery(final Assessment assessment,
            final DeliverySettings deliverySettings, final DeliveryTemplate template) {
        final Delivery delivery = new Delivery();
        delivery.setAssessment(assessment);
        delivery.setDeliverySettings(deliverySettings);
        delivery.setDeliveryType(DeliveryType.USER_CREATED);
        delivery.setOpen(template.isOpen());
        delivery.setLtiEnabled(template.isLtiEnabled());
        delivery.setTitle(template.getTitle().trim());
        delivery.setLtiConsumerKeyToken(ServiceUtilities.createRandomAlphanumericToken(DomainConstants.LTI_TOKEN_LENGTH));
        delivery.setLtiConsumerSecret(ServiceUtilities.createRandomAlphanumericToken(DomainConstants.LTI_TOKEN_LENGTH));
        deliveryDao.persist(delivery);
        return delivery;
    }

    /**
     * Deletes the {@link Delivery} having the given did and owned by the caller.
     *
     * NOTE: This deletes ALL associated data, including candidate data. Use with care!
     */
    @Transactional(propagation=Propagation.REQUIRED)
    public Assessment deleteDelivery(final long did)
            throws DomainEntityNotFoundException, PrivilegeException {
        /* Look up assessment and check permissions */
        final Delivery delivery = deliveryDao.requireFindById(did);
        final Assessment assessment = delivery.getAssessment();
        ensureCallerOwns(assessment);

        /* Now delete it and all associated data */
        dataDeletionService.deleteDelivery(delivery);

        /* Log what happened */
        logger.debug("Deleted Delivery #{}", did);
        auditLogger.recordEvent("Deleted Delivery #" + did);
        return assessment;
    }

    public Delivery updateDelivery(final long did, final DeliveryTemplate template)
            throws BindException, PrivilegeException, DomainEntityNotFoundException {
        /* Validate template */
        validateDeliveryTemplate(template);

        /* Look up delivery and check privileges */
        final Delivery delivery = lookupOwnDelivery(did);
        final Assessment assessment = delivery.getAssessment();
        ensureCallerMayChange(assessment);

        /* Look up settings and check privileges */
        final long dsid = template.getDsid();
        final DeliverySettings deliverySettings = lookupAndMatchDeliverySettings(dsid, assessment);

        /* Update data */
        delivery.setOpen(template.isOpen());
        delivery.setTitle(template.getTitle().trim());
        delivery.setLtiEnabled(template.isLtiEnabled());
        delivery.setDeliverySettings(deliverySettings);
        deliveryDao.update(delivery);
        return delivery;
    }

    private void validateDeliveryTemplate(final DeliveryTemplate template)
            throws BindException {
        Assert.notNull(template, "deliveryTemplate");
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(template, "deliveryTemplate");
        jsr303Validator.validate(template, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }
    }

    //-------------------------------------------------
    // Assessment trying

    public Delivery createDemoDelivery(final Assessment assessment)
            throws PrivilegeException {
        Assert.notNull(assessment, "assessment");

        /* Select suitable delivery settings */
        DeliverySettings deliverySettings = assessment.getDefaultDeliverySettings();
        if (deliverySettings==null) {
            deliverySettings = requireFirstDeliverySettingsForCaller(assessment.getAssessmentType());
        }

        /* Now create demo delivery using these options */
        return createDemoDelivery(assessment, deliverySettings);
    }

    public Delivery createDemoDelivery(final Assessment assessment, final DeliverySettings deliverySettings)
            throws PrivilegeException {
        Assert.notNull(assessment, "assessment");
        Assert.notNull(deliverySettings, "deliverySettings");
        ensureCompatible(deliverySettings, assessment);

        /* Make sure caller is allowed to run this Assessment */
        ensureCallerMayAccess(assessment);

        /* Create demo Delivery */
        final Delivery delivery = new Delivery();
        delivery.setAssessment(assessment);
        delivery.setDeliverySettings(deliverySettings);
        delivery.setDeliveryType(DeliveryType.USER_TRANSIENT);
        delivery.setOpen(true);
        delivery.setTitle("Temporary demo delivery");
        deliveryDao.persist(delivery);

        /* That's it! */
        auditLogger.recordEvent("Created demo Delivery #" + delivery.getId() + " for Assessment #" + assessment.getId());
        return delivery;
    }

    public DeliverySettings requireFirstDeliverySettingsForCaller(final AssessmentObjectType assessmentType) {
        /* See if there are already suitable settings created */
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        final DeliverySettings firstDeliverySettings = deliverySettingsDao.getFirstForOwner(caller, assessmentType);
        if (firstDeliverySettings!=null) {
            return firstDeliverySettings;
        }

        /* No luck, so set up some initial settings appropriate for this assessment */
        switch (assessmentType) {
            case ASSESSMENT_ITEM: {
                final ItemDeliverySettingsTemplate template = createItemDeliverySettingsTemplate();
                final ItemDeliverySettings itemDeliverySettings = new ItemDeliverySettings();
                mergeItemDeliverySettings(template, itemDeliverySettings);
                itemDeliverySettings.setOwner(caller);
                itemDeliverySettings.setTitle("Default item delivery settings");
                if (caller.getUserType()==UserType.INSTRUCTOR) {
                    itemDeliverySettings.setPrompt("This assessment item is being delivered using a set of default 'delivery settings'"
                            + " we have created for you. Feel free to tweak these defaults, or create and use as many of your own sets"
                            + " of options as you please. This bit of text you are reading now is a default 'prompt' for the item,"
                            + " which you can edit or remove to suit.");
                }
                else {
                    itemDeliverySettings.setPrompt("This assessment item is being delivered using a set of default 'delivery settings'"
                            + " we have created for you. You will be able to change and edit these settings to suit if you have a QTIWorks account.");
                }
                deliverySettingsDao.persist(itemDeliverySettings);
                auditLogger.recordEvent("Created default ItemDeliverySettings for this user");
                return itemDeliverySettings;
            }

            case ASSESSMENT_TEST: {
                final TestDeliverySettingsTemplate template = createTestDeliverySettingsTemplate();
                final TestDeliverySettings testDeliverySettings = new TestDeliverySettings();
                mergeTestDeliverySettings(template, testDeliverySettings);
                testDeliverySettings.setOwner(caller);
                testDeliverySettings.setTitle("Default test delivery settings");

                deliverySettingsDao.persist(testDeliverySettings);
                auditLogger.recordEvent("Created default TestDeliverySettings for this user");
                return testDeliverySettings;
            }

            default:
                throw new QtiLogicException("Unexpected switch case " + assessmentType);
        }

    }

    public ItemDeliverySettingsTemplate createItemDeliverySettingsTemplate() {
        final ItemDeliverySettingsTemplate template = new ItemDeliverySettingsTemplate();
        template.setAuthorMode(true);
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
        template.setAuthorMode(true);
        template.setTitle("Test Delivery Settings");
        return template;
    }

    //-------------------------------------------------
    // Internal helpers

    private void ensureCompatible(final DeliverySettings deliverySettings, final Assessment assessment)
            throws PrivilegeException {
        ensureCompatible(deliverySettings, assessment.getAssessmentType());
    }

    private void ensureCompatible(final DeliverySettings deliverySettings, final AssessmentObjectType assessmentObjectType)
            throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (assessmentObjectType!=deliverySettings.getAssessmentType()) {
            throw new PrivilegeException(caller, Privilege.MATCH_DELIVERY_SETTINGS, deliverySettings);
        }
    }

    private void deleteAssessmentPackageSandbox(final AssessmentPackage assessmentPackage) {
        final String sandboxPath = assessmentPackage.getSandboxPath();
        if (sandboxPath==null) {
            throw new QtiWorksLogicException("AssessmentPackage sandbox is null");
        }
        filespaceManager.deleteSandbox(new File(sandboxPath));
        assessmentPackage.setSandboxPath(null);
    }

    private AssessmentPackage importPackageFiles(final MultipartFile multipartFile)
            throws AssessmentPackageFileImportException {
        final User owner = identityContext.getCurrentThreadEffectiveIdentity();
        final File packageSandbox = filespaceManager.createAssessmentPackageSandbox(owner);
        try {
            final AssessmentPackage assessmentPackage = assessmentPackageFileImporter.importAssessmentPackageData(packageSandbox, multipartFile);
            assessmentPackage.setImporter(owner);
            return assessmentPackage;
        }
        catch (final AssessmentPackageFileImportException e) {
            filespaceManager.deleteSandbox(packageSandbox);
            throw e;
        }
    }
}
