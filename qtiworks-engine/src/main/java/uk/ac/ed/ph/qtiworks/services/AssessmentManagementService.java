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
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.reading.AssessmentObjectXmlLoader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

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
import org.w3c.dom.Document;

/**
 * Top layer services for *managing* {@link Assessment}s and related entities.
 *
 * FIXME: The permission controls here are now a bit odd. These need rethought and refactored!
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
    private AssessmentPackageFileImporter assessmentPackageFileImporter;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private AssessmentPackageDao assessmentPackageDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private DeliverySettingsDao deliverySettingsDao;

    @Resource
    private QtiXmlReader qtiXmlReader;

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
    // AssessmentPackage access

    public AssessmentPackage lookupAssessmentPackage(final long apid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final AssessmentPackage result = assessmentPackageDao.requireFindById(apid);
        ensureCallerMayAccess(result.getAssessment());
        return result;
    }

    //-------------------------------------------------

    /**
     * Creates and persists a new {@link Assessment} and initial {@link AssessmentPackage}
     * from the data provided by the given {@link InputStream} and having the given content type.
     * <p>
     * Success post-conditions:
     * - the {@link InputStream} is left open
     * - a new {@link AssessmentPackage} is persisted, and its data is safely stored in a sandbox
     *
     * @param multipartFile
     * @param contentType
     * @param name for the resulting package. A default will be chosen if one is not provided.
     *   The name will be silently truncated if it is too large for the underlying DB field.
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

        /* Create resulting Assessment entity */
        final Assessment assessment = new Assessment();
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
        final String guessedTitle = guessAssessmentTitle(assessmentPackage);
        final String resultingTitle = !StringUtilities.isNullOrEmpty(guessedTitle) ? guessedTitle : DEFAULT_IMPORT_TITLE;
        assessment.setTitle(ServiceUtilities.trimSentence(resultingTitle, DomainConstants.ASSESSMENT_TITLE_MAX_LENGTH));

        /* Relate Assessment & AssessmentPackage */
        assessmentPackage.setAssessment(assessment);
        assessmentPackage.setImportVersion(Long.valueOf(1L));
        assessment.setPackageImportVersion(Long.valueOf(1L));

        /* Persist entities */
        try {
            assessmentDao.persist(assessment);
            assessmentPackageDao.persist(assessmentPackage);
        }
        catch (final Exception e) {
            logger.warn("Persistence of AssessmentPackage failed - deleting its sandbox", assessmentPackage);
            deleteAssessmentPackageSandbox(assessmentPackage);
            throw new QtiWorksRuntimeException("Failed to persist AssessmentPackage " + assessmentPackage, e);
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
     * NOTE: Not allowed to go item->test or test->item.
     *
     * @throws AssessmentStateException
     * @throws PrivilegeException
     * @throws AssessmentPackageFileImportException
     * @throws DomainEntityNotFoundException
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Assessment updateAssessmentPackageFiles(final long aid,
            final MultipartFile multipartFile)
            throws AssessmentStateException, PrivilegeException,
            AssessmentPackageFileImportException, DomainEntityNotFoundException {
        Assert.notNull(multipartFile, "multipartFile");
        final Assessment assessment = assessmentDao.requireFindById(aid);
        ensureCallerMayChange(assessment);

        /* Upload data into a new sandbox */
        final AssessmentPackage newAssessmentPackage = importPackageFiles(multipartFile);

        /* Make sure we haven't gone item->test or test->item */
        if (newAssessmentPackage.getAssessmentType()!=assessment.getAssessmentType()) {
            throw new AssessmentStateException(APSFailureReason.CANNOT_CHANGE_ASSESSMENT_TYPE,
                    assessment.getAssessmentType(), newAssessmentPackage.getAssessmentType());
        }

        /* Join together */
        final long newPackageVersion = assessment.getPackageImportVersion().longValue() + 1;
        newAssessmentPackage.setImportVersion(newPackageVersion);
        newAssessmentPackage.setAssessment(assessment);
        assessment.setPackageImportVersion(newPackageVersion);

        /* Finally update DB */
        try {
            assessmentDao.update(assessment);
            assessmentPackageDao.persist(newAssessmentPackage);
        }
        catch (final Exception e) {
            logger.warn("Failed to update state of AssessmentPackage {} after file replacement - deleting new sandbox", e);
            deleteAssessmentPackageSandbox(newAssessmentPackage);
            throw new QtiWorksRuntimeException("Failed to update AssessmentPackage entity " + assessment, e);
        }
        logger.debug("Updated Assessment #{} to have package #{}", assessment.getId(), newAssessmentPackage.getId());
        auditLogger.recordEvent("Updated Assessment #" + assessment.getId() + " with AssessmentPackage #" + newAssessmentPackage.getId());
        return assessment;
    }

    //-------------------------------------------------
    // Not implemented yet

    /**
     * DEV NOTES:
     *
     * - Forbid deletion of the only remaining package, as that ensures there's always a most
     *   recent package
     */
    @Transactional(propagation=Propagation.REQUIRED)
    @SuppressWarnings("unused")
    @ToRefactor
    public void deleteAssessmentPackage(final AssessmentPackage assessmentPackage)
            throws AssessmentStateException, PrivilegeException {
        /* In order to do this correctly, we need to delete all state that might have
         * been associated with this package as well, so we'll come back to this...
         */
        throw new QtiLogicException("Not yet implemented!");
    }

    //-------------------------------------------------
    // Validation

    @Transactional(propagation=Propagation.REQUIRED)
    public AssessmentObjectValidationResult<?> validateAssessment(final long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = lookupAssessment(aid);
        final AssessmentPackage currentAssessmentPackage = entityGraphService.getCurrentAssessmentPackage(assessment);

        /* Run the validation process */
        final AssessmentObjectValidationResult<?> validationResult = loadAndValidateAssessment(currentAssessmentPackage);

        /* Persist results */
        currentAssessmentPackage.setValidated(true);
        currentAssessmentPackage.setValid(validationResult.isValid());
        assessmentPackageDao.update(currentAssessmentPackage);

        return validationResult;
    }

    /**
     * (This is not private as it is also called by the anonymous upload/validate action featured
     * in the first iteration of QTI Works.
     *
     * TODO: Decide whether we'll keep this kind of functionality.)
     */
    @SuppressWarnings("unchecked")
    <E extends AssessmentObjectValidationResult<?>> AssessmentObjectValidationResult<?>
    loadAndValidateAssessment(final AssessmentPackage assessmentPackage) {
        final ResourceLocator inputResourceLocator = assessmentPackageFileService.createResolvingResourceLocator(assessmentPackage);
        final URI assessmentObjectSystemId = assessmentPackageFileService.createAssessmentObjectUri(assessmentPackage);
        final AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader, inputResourceLocator);
        final AssessmentObjectType assessmentObjectType = assessmentPackage.getAssessmentType();
        E result;
        if (assessmentObjectType==AssessmentObjectType.ASSESSMENT_ITEM) {
            result = (E) assessmentObjectXmlLoader.loadResolveAndValidateItem(assessmentObjectSystemId);
        }
        else if (assessmentObjectType==AssessmentObjectType.ASSESSMENT_TEST) {
            result = (E) assessmentObjectXmlLoader.loadResolveAndValidateTest(assessmentObjectSystemId);
        }
        else {
            throw new QtiWorksLogicException("Unexpected branch " + assessmentObjectType);
        }
        return result;
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
        target.setAllowClose(template.isAllowClose());
        target.setAllowReinitWhenClosed(template.isAllowReinitWhenClosed());
        target.setAllowReinitWhenInteracting(template.isAllowReinitWhenInteracting());
        target.setAllowResetWhenClosed(template.isAllowResetWhenClosed());
        target.setAllowResetWhenInteracting(template.isAllowResetWhenInteracting());
        target.setAllowResult(template.isAllowResult());
        target.setAllowSolutionWhenClosed(template.isAllowSolutionWhenClosed());
        target.setAllowSolutionWhenInteracting(template.isAllowSolutionWhenInteracting());
        target.setAllowSource(template.isAllowSource());
        target.setAuthorMode(template.isAuthorMode());
        target.setMaxAttempts(template.getMaxAttempts().intValue());
        target.setPrompt(StringUtilities.nullIfEmpty(template.getPrompt()));
        target.setTitle(template.getTitle().trim());
    }

    public void mergeItemDeliverySettings(final ItemDeliverySettings template, final ItemDeliverySettingsTemplate target) {
        target.setAllowClose(template.isAllowClose());
        target.setAllowReinitWhenClosed(template.isAllowReinitWhenClosed());
        target.setAllowReinitWhenInteracting(template.isAllowReinitWhenInteracting());
        target.setAllowResetWhenClosed(template.isAllowResetWhenClosed());
        target.setAllowResetWhenInteracting(template.isAllowResetWhenInteracting());
        target.setAllowResult(template.isAllowResult());
        target.setAllowSolutionWhenClosed(template.isAllowSolutionWhenClosed());
        target.setAllowSolutionWhenInteracting(template.isAllowSolutionWhenInteracting());
        target.setAllowSource(template.isAllowSource());
        target.setAuthorMode(template.isAuthorMode());
        target.setMaxAttempts(Integer.valueOf(template.getMaxAttempts()));
        target.setPrompt(StringUtilities.nullIfEmpty(template.getPrompt()));
        target.setTitle(template.getTitle());
    }

    //-------------------------------------------------
    // CRUD for TEstDeliverySettings

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
        target.setPrompt(StringUtilities.nullIfEmpty(template.getPrompt()));
        target.setTitle(template.getTitle().trim());
    }

    public void mergeTestDeliverySettings(final TestDeliverySettings template, final TestDeliverySettingsTemplate target) {
        target.setAuthorMode(template.isAuthorMode());
        target.setPrompt(StringUtilities.nullIfEmpty(template.getPrompt()));
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
        final User caller = ensureCallerMayChange(assessment);

        /* Get first DeliverySettings (creating if required) */
        final DeliverySettings deliverySettings = requireFirstDeliverySettings(caller, assessment.getAssessmentType());

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
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        DeliverySettings deliverySettings = assessment.getDefaultDeliverySettings();
        if (deliverySettings==null) {
            deliverySettings = requireFirstDeliverySettings(caller, assessment.getAssessmentType());
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
        final User caller = ensureCallerMayAccess(assessment);

        /* Get most recent package */
        final AssessmentPackage currentAssessmentPackage = entityGraphService.getCurrentAssessmentPackage(assessment);

        /* Make sure package is valid */
        if (!currentAssessmentPackage.isValid()) {
            throw new PrivilegeException(caller, assessment, Privilege.LAUNCH_INVALID_ASSESSMENT);
        }

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

    public DeliverySettings requireFirstDeliverySettings(final User owner, final AssessmentObjectType assessmentType) {
        /* See if there are already suitable settings created */
        final DeliverySettings firstDeliverySettings = deliverySettingsDao.getFirstForOwner(owner, assessmentType);
        if (firstDeliverySettings!=null) {
            return firstDeliverySettings;
        }

        /* No luck, so set up some initial settings appropriate for this assessment */
        switch (assessmentType) {
            case ASSESSMENT_ITEM: {
                final ItemDeliverySettingsTemplate template = createItemDeliverySettingsTemplate();
                final ItemDeliverySettings itemDeliverySettings = new ItemDeliverySettings();
                mergeItemDeliverySettings(template, itemDeliverySettings);
                itemDeliverySettings.setOwner(owner);
                itemDeliverySettings.setTitle("Default item delivery settings");
                itemDeliverySettings.setPrompt("This assessment item is being delivered using a set of default 'delivery settings'"
                        + " we have created for you. Feel free to tweak these defaults, or create and use as many of your own sets"
                        + " of options as you please. This bit of text you are reading now is a default 'prompt' for the item,"
                        + " which you can edit or remove to suit.");

                deliverySettingsDao.persist(itemDeliverySettings);
                auditLogger.recordEvent("Created default ItemDeliverySettings for this user");
                return itemDeliverySettings;
            }

            case ASSESSMENT_TEST: {
                final TestDeliverySettingsTemplate template = createTestDeliverySettingsTemplate();
                final TestDeliverySettings testDeliverySettings = new TestDeliverySettings();
                mergeTestDeliverySettings(template, testDeliverySettings);
                testDeliverySettings.setOwner(owner);
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
        template.setAllowClose(true);
        template.setAllowReinitWhenClosed(true);
        template.setAllowReinitWhenInteracting(true);
        template.setAllowResetWhenClosed(true);
        template.setAllowResetWhenInteracting(true);
        template.setAllowResult(true);
        template.setAllowSolutionWhenClosed(true);
        template.setAllowSolutionWhenInteracting(true);
        template.setAllowSource(true);
        template.setAuthorMode(true);
        template.setMaxAttempts(0);
        template.setTitle("Item Delivery Settings");
        template.setPrompt(null);
        return template;
    }

    public TestDeliverySettingsTemplate createTestDeliverySettingsTemplate() {
        final TestDeliverySettingsTemplate template = new TestDeliverySettingsTemplate();
        template.setAuthorMode(true);
        template.setTitle("Item Delivery Settings");
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

    /**
     * @throws QtiWorksLogicException if sandboxPath is already null
     */
    private void deleteAssessmentPackageSandbox(final AssessmentPackage assessmentPackage) {
        final String sandboxPath = assessmentPackage.getSandboxPath();
        if (sandboxPath==null) {
            throw new QtiWorksLogicException("AssessmentPackage sandbox is null");
        }
        filespaceManager.deleteSandbox(new File(sandboxPath));
        assessmentPackage.setSandboxPath(null);
    }

    /**
     * @throws PrivilegeException
     * @throws AssessmentPackageFileImportException
     * @throws QtiWorksRuntimeException
     */
    private AssessmentPackage importPackageFiles(final MultipartFile multipartFile)
            throws PrivilegeException, AssessmentPackageFileImportException {
        final User owner = identityContext.getCurrentThreadEffectiveIdentity();
        final File packageSandbox = filespaceManager.createAssessmentPackageSandbox(owner);
        final InputStream inputStream = ServiceUtilities.ensureInputSream(multipartFile);
        final String contentType = multipartFile.getContentType();
        try {
            final AssessmentPackage assessmentPackage = assessmentPackageFileImporter.importAssessmentPackageData(packageSandbox, inputStream, contentType);
            assessmentPackage.setImporter(owner);
            return assessmentPackage;
        }
        catch (final AssessmentPackageFileImportException e) {
            filespaceManager.deleteSandbox(packageSandbox);
            throw e;
        }
    }

    /**
     * Attempts to extract the title from an {@link AssessmentItem} or {@link AssessmentTest} for
     * bootstrapping the initial state of the resulting {@link AssessmentPackage}.
     * <p>
     * This performs a low level XML parse to save time; proper read/validation using JQTI+
     * is expected to happen later on.
     *
     * @param assessmentPackage
     * @return guessed title, or an empty String if nothing could be guessed.
     */
    public String guessAssessmentTitle(final AssessmentPackage assessmentPackage) {
        Assert.notNull(assessmentPackage, "assessmentPackage");
        final ResourceLocator inputResourceLocator = assessmentPackageFileService.createResolvingResourceLocator(assessmentPackage);
        final URI assessmentSystemId = assessmentPackageFileService.createAssessmentObjectUri(assessmentPackage);
        XmlReadResult xmlReadResult;
        try {
            xmlReadResult = qtiXmlReader.read(assessmentSystemId, inputResourceLocator, false);
        }
        catch (final XmlResourceNotFoundException e) {
            throw new QtiWorksLogicException("Assessment resource for package " + assessmentPackage, e);
        }
        /* Let's simply extract the title attribute from the document element, and not worry about
         * anything else at this point.
         */
        final Document document = xmlReadResult.getDocument();
        return document!=null ? document.getDocumentElement().getAttribute("title") : "";
    }
}
