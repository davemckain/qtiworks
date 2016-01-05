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
package uk.ac.ed.ph.qtiworks.web.controller.instructor;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.AssessmentDataService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.IdentityService;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentAndPackage;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentLtiOutcomesSettingsTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageDataImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageDataImportException.ImportFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.CannotChangeAssessmentTypeException;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.EnumerableClientFailure;
import uk.ac.ed.ph.qtiworks.services.domain.IncompatiableDeliverySettingsException;
import uk.ac.ed.ph.qtiworks.services.domain.ItemDeliverySettingsTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.services.domain.TestDeliverySettingsTemplate;
import uk.ac.ed.ph.qtiworks.web.GlobalRouter;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionLaunchService;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionTicket;
import uk.ac.ed.ph.qtiworks.web.domain.UploadAssessmentPackageCommand;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller providing management functions for {@link Assessment}s
 *
 * @author David McKain
 */
@Controller
public class InstructorAssessmentManagementController {

    @Resource
    private InstructorRouter instructorRouter;

    @Resource
    private InstructorModelHelper instructorModelHelper;

    @Resource
    private AssessmentDataService assessmentDataService;

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private CandidateSessionLaunchService candidateSessionLaunchService;

    @Resource
    private IdentityService identityService;

    //------------------------------------------------------

    @ModelAttribute
    public void setupModel(final Model model) {
        instructorModelHelper.setupModel(model);
    }

    //------------------------------------------------------

    /** Instructor dashboard */
    @RequestMapping(value="", method=RequestMethod.GET)
    public String instructorDashboard() {
        return "dashboard";
    }

    /** Lists all Assignments owned by the caller */
    @RequestMapping(value="/assessments", method=RequestMethod.GET)
    public String listOwnAssessments(final Model model) {
        final List<AssessmentAndPackage> assessments = assessmentDataService.getCallerUserAssessments();
        model.addAttribute(assessments);
        model.addAttribute("assessmentListRouting", instructorRouter.buildAssessmentListRouting(assessments));
        return "listAssessments";
    }

    //------------------------------------------------------
    // Assessment management

    @RequestMapping(value="/assessments/upload", method=RequestMethod.GET)
    public String showUploadAssessmentForm(final Model model) {
        model.addAttribute(new UploadAssessmentPackageCommand());
        return "uploadAssessmentForm";
    }

    @RequestMapping(value="/assessments/upload", method=RequestMethod.POST)
    public String handleUploadAssessmentForm(final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute UploadAssessmentPackageCommand command,
            final BindingResult result)
            throws PrivilegeException {
        if (result.hasErrors()) {
            /* Return to form if any binding errors */
            return "uploadAssessmentForm";
        }

        /* Attempt to import and validate the package */
        Assessment assessment;
        try {
            assessment = assessmentManagementService.importAssessment(command.getFile(), true);
        }
        catch (final AssessmentPackageDataImportException e) {
            final EnumerableClientFailure<ImportFailureReason> failure = e.getFailure();
            failure.registerErrors(result, "assessmentPackageUpload");
            return "uploadAssessmentForm";
        }
        GlobalRouter.addFlashMessage(redirectAttributes, "Assessment successfully created");
        return instructorRouter.buildInstructorRedirect("/assessment/" + assessment.getId());
    }

    /**
     * Shows the Assessment having the given ID (aid)
     */
    @RequestMapping(value="/assessment/{aid}", method=RequestMethod.GET)
    public String showAssessment(@PathVariable final long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);
        final List<Delivery> deliveries = assessmentDataService.getUserCreatedDeliveries(assessment);
        instructorModelHelper.setupModelForAssessment(assessment, model);
        model.addAttribute(deliveries);
        model.addAttribute("deliveryListRouting", instructorRouter.buildDeliveryListRouting(deliveries));
        return "showAssessment";
    }

    @RequestMapping(value="/assessment/{aid}/replace", method=RequestMethod.GET)
    public String showReplaceAssessmentPackageForm(final @PathVariable long aid,
            final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        model.addAttribute(new UploadAssessmentPackageCommand());
        instructorModelHelper.setupModelForAssessment(aid, model);
        return "replaceAssessmentPackageForm";
    }

    @RequestMapping(value="/assessment/{aid}/replace", method=RequestMethod.POST)
    public String handleReplaceAssessmentPackageForm(final @PathVariable long aid,
            final Model model, final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute UploadAssessmentPackageCommand command, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException {
        if (!result.hasErrors()) {
            /* Attempt to import the package */
            final MultipartFile uploadFile = command.getFile();
            try {
                assessmentManagementService.replaceAssessmentPackage(aid, uploadFile, true);
            }
            catch (final AssessmentPackageDataImportException e) {
                final EnumerableClientFailure<ImportFailureReason> failure = e.getFailure();
                failure.registerErrors(result, "assessmentPackageUpload");
            }
            catch (final CannotChangeAssessmentTypeException e) {
                result.reject("assessmentPackageUpload.CANNOT_CHANGE_ASSESSMENT_TYPE");
            }
        }
        if (result.hasErrors()) {
            instructorModelHelper.setupModelForAssessment(aid, model);
            return "replaceAssessmentPackageForm";
        }
        GlobalRouter.addFlashMessage(redirectAttributes, "Assessment package content successfully replaced");
        return instructorRouter.buildInstructorRedirect("/assessment/{aid}");
    }

    @RequestMapping(value="/assessment/{aid}/delete", method=RequestMethod.POST)
    public String deleteAssessment(final @PathVariable long aid, final RedirectAttributes redirectAttributes)
            throws PrivilegeException, DomainEntityNotFoundException {
        assessmentManagementService.deleteAssessment(aid);
        GlobalRouter.addFlashMessage(redirectAttributes, "Assessment successfully deleted");
        return instructorRouter.buildInstructorRedirect("/assessments");
    }

    /** TODO: For performance, we should cache the validation result */
    @RequestMapping(value="/assessment/{aid}/validate", method=RequestMethod.GET)
    public String validateAssessment(final @PathVariable long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final AssessmentObjectValidationResult<?> validationResult = assessmentManagementService.validateAssessment(aid);
        model.addAttribute("validationResult", validationResult);
        instructorModelHelper.setupModelForAssessment(aid, model);
        return "validationResult";
    }

    @RequestMapping(value="/assessment/{aid}/try", method=RequestMethod.POST)
    public String tryAssessment(final @PathVariable long aid, final HttpSession httpSession)
            throws PrivilegeException, DomainEntityNotFoundException, CandidateException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);
        final Delivery demoDelivery = assessmentManagementService.createDemoDelivery(assessment);

        return runDelivery(httpSession, aid, demoDelivery, true);
    }

    @RequestMapping(value="/assessment/{aid}/try/{dsid}", method=RequestMethod.POST)
    public String tryAssessment(final @PathVariable long aid, final @PathVariable long dsid, final HttpSession httpSession)
            throws PrivilegeException, DomainEntityNotFoundException,
            CandidateException, IncompatiableDeliverySettingsException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);
        final DeliverySettings deliverySettings = assessmentManagementService.lookupAndMatchDeliverySettings(dsid, assessment);
        final Delivery demoDelivery = assessmentManagementService.createDemoDelivery(assessment, deliverySettings);

        return runDelivery(httpSession, aid, demoDelivery, true);
    }

    private String runDelivery(final HttpSession httpSession, final long aid, final Delivery delivery, final boolean authorMode)
            throws CandidateException {
        final User caller = identityService.getCurrentThreadUser();
        final String sessionExitReturnUrl = instructorRouter.buildWithinContextUrl("/assessment/" + aid);
        final CandidateSessionTicket candidateSessionTicket = candidateSessionLaunchService.launchInstructorTrialSession(httpSession, caller, delivery, authorMode, sessionExitReturnUrl);
        return GlobalRouter.buildSessionStartRedirect(candidateSessionTicket);
    }

    @RequestMapping(value="/assessment/{aid}/outcomes-settings", method=RequestMethod.GET)
    public String showSetLtiOutcomesForm(final @PathVariable long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);

        final AssessmentLtiOutcomesSettingsTemplate template = new AssessmentLtiOutcomesSettingsTemplate();
        template.setResultOutcomeIdentifier(assessment.getLtiResultOutcomeIdentifier());
        template.setResultMaximum(assessment.getLtiResultMaximum());
        template.setResultMinimum(assessment.getLtiResultMinimum());

        instructorModelHelper.setupModelForAssessment(assessment, model);
        model.addAttribute("outcomeDeclarationList", assessmentDataService.getOutcomeVariableDeclarations(assessment));
        model.addAttribute(template);
        return "assessmentOutcomesSettingsForm";
    }

    @RequestMapping(value="/assessment/{aid}/outcomes-settings", method=RequestMethod.POST)
    public String handleSetLtiOutcomesForm(final @PathVariable long aid, final Model model,
            final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute AssessmentLtiOutcomesSettingsTemplate template, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException {
        /* Validate command Object */
        if (!result.hasErrors()) {
            try {
                assessmentManagementService.updateAssessmentLtiOutcomesSettings(aid, template);
            }
            catch (final BindException e) {
                result.addAllErrors(e);
            }
        }
        if (result.hasErrors()) {
            final Assessment assessment = instructorModelHelper.setupModelForAssessment(aid, model);
            model.addAttribute("outcomeDeclarationList", assessmentDataService.getOutcomeVariableDeclarations(assessment));
            return "assessmentOutcomesSettingsForm";
        }
        /* Successful */
        GlobalRouter.addFlashMessage(redirectAttributes, "Assessment LTI outcomes settings saved successfully");
        return instructorRouter.buildInstructorRedirect("/assessment/" + aid);
    }

    //------------------------------------------------------
    // Management of Deliveries

    @RequestMapping(value="/delivery/{did}", method=RequestMethod.GET)
    public String showDelivery(final Model model, @PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery delivery = assessmentManagementService.lookupDelivery(did);
        instructorModelHelper.setupModelForDelivery(delivery, model);
        return "showDelivery";
    }

    /** FIXME: Support trying out with authorMode turned off */
    @RequestMapping(value="/delivery/{did}/try", method=RequestMethod.POST)
    public String tryDelivery(final @PathVariable long did, final HttpSession httpSession)
            throws PrivilegeException, DomainEntityNotFoundException, CandidateException {
        final User caller = identityService.getCurrentThreadUser();
        final Delivery delivery = assessmentManagementService.lookupDelivery(did);
        final String sessionExitReturnUrl = instructorRouter.buildWithinContextUrl("/delivery/" + did);
        final CandidateSessionTicket candidateSessionTicket = candidateSessionLaunchService.launchInstructorTrialSession(httpSession, caller, delivery, true, sessionExitReturnUrl);
        return GlobalRouter.buildSessionStartRedirect(candidateSessionTicket);
    }

    @RequestMapping(value="/assessment/{aid}/deliveries/create", method=RequestMethod.GET)
    public String showCreateDeliveryForm(final Model model, @PathVariable final long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);
        final DeliveryTemplate template = assessmentDataService.createDeliveryTemplate(assessment);

        model.addAttribute(template);
        instructorModelHelper.setupModelForAssessment(assessment, model);
        return "createDeliveryForm";
    }

    @RequestMapping(value="/assessment/{aid}/deliveries/create", method=RequestMethod.POST)
    public String handleCreateDeliveryForm(@PathVariable final long aid, final Model model, final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute DeliveryTemplate template, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException, IncompatiableDeliverySettingsException {
        /* Validate command Object */
        if (result.hasErrors()) {
            instructorModelHelper.setupModelForAssessment(aid, model);
            return "createDeliveryForm";
        }

        /* Perform creation */
        final Delivery delivery;
        try {
            delivery = assessmentManagementService.createDelivery(aid, template);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }

        /* Return to show */
        GlobalRouter.addFlashMessage(redirectAttributes, "Delivery successfully created");
        return instructorRouter.buildInstructorRedirect("/delivery/" + delivery.getId());
    }

    @RequestMapping(value="/delivery/{did}/delete", method=RequestMethod.POST)
    public String deleteDelivery(final @PathVariable long did, final RedirectAttributes redirectAttributes)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.deleteDelivery(did);
        redirectAttributes.addFlashAttribute(GlobalRouter.FLASH, "Delivery has been deleted");
        return instructorRouter.buildInstructorRedirect("/assessment/" + assessment.getId());
    }

    @RequestMapping(value="/delivery/{did}/toggle-availability", method=RequestMethod.POST)
    public String toggleDeliveryAvailabilityStatus(final @PathVariable long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery delivery = assessmentManagementService.lookupDelivery(did);
        try {
            assessmentManagementService.setDeliveryLtiLinkOpenStatus(delivery.getId(), !delivery.isOpen());
        }
        catch (final DomainEntityNotFoundException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        return instructorRouter.buildInstructorRedirect("/delivery/" + delivery.getId().longValue());
    }

    @RequestMapping(value="/delivery/{did}/edit", method=RequestMethod.GET)
    public String showEditDeliveryForm(final Model model, @PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery delivery = assessmentManagementService.lookupDelivery(did);
        final DeliverySettings deliverySettings = delivery.getDeliverySettings();

        final DeliveryTemplate template = new DeliveryTemplate();
        template.setTitle(delivery.getTitle());
        template.setDsid(deliverySettings!=null ? deliverySettings.getId() : null);

        model.addAttribute(template);
        instructorModelHelper.setupModelForDelivery(delivery, model);
        return "editDeliveryForm";
    }

    @RequestMapping(value="/delivery/{did}/edit", method=RequestMethod.POST)
    public String handleEditDeliveryForm(@PathVariable final long did, final Model model, final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute DeliveryTemplate template, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException, IncompatiableDeliverySettingsException {
        /* Validate command Object */
        if (result.hasErrors()) {
            instructorModelHelper.setupModelForDelivery(did, model);
            return "editDeliveryForm";
        }

        /* Perform update */
        try {
            assessmentManagementService.updateDelivery(did, template);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }

        /* Return to show */
        GlobalRouter.addFlashMessage(redirectAttributes, "Delivery successfully edited");
        return instructorRouter.buildInstructorRedirect("/delivery/" + did);
    }

    //------------------------------------------------------
    // Management of DeliverySettings (and subtypes)

    @RequestMapping(value="/deliverysettings", method=RequestMethod.GET)
    public String listCallerDeliverySettings() {
        return "deliverySettingsManager";
    }

    @RequestMapping(value="/deliverysettings/item", method=RequestMethod.GET)
    public String listCallerItemDeliverySettings(final Model model) {
        final List<DeliverySettings> deliverySettingsList = assessmentDataService.getCallerUserDeliverySettingsForType(AssessmentObjectType.ASSESSMENT_ITEM);
        model.addAttribute("deliverySettingsList", deliverySettingsList);
        model.addAttribute("deliverySettingsListRouting", instructorRouter.buildDeliverySettingsListRouting(deliverySettingsList));
        return "listItemDeliverySettings";
    }

    @RequestMapping(value="/deliverysettings/test", method=RequestMethod.GET)
    public String listCallerTestDeliverySettings(final Model model) {
        final List<DeliverySettings> deliverySettingsList = assessmentDataService.getCallerUserDeliverySettingsForType(AssessmentObjectType.ASSESSMENT_TEST);
        model.addAttribute("deliverySettingsList", deliverySettingsList);
        model.addAttribute("deliverySettingsListRouting", instructorRouter.buildDeliverySettingsListRouting(deliverySettingsList));
        return "listTestDeliverySettings";
    }

    @RequestMapping(value="/deliverysettings/item/create", method=RequestMethod.GET)
    public String showCreateItemDeliverySettingsForm(final Model model) {
        final long existingSettingsCount = assessmentDataService.countCallerUserDeliverySettings(AssessmentObjectType.ASSESSMENT_ITEM);
        final ItemDeliverySettingsTemplate template = assessmentDataService.createItemDeliverySettingsTemplate();
        template.setTitle("Item Delivery Settings #" + (existingSettingsCount+1));

        model.addAttribute(template);
        return "createItemDeliverySettingsForm";
    }

    @RequestMapping(value="/deliverysettings/test/create", method=RequestMethod.GET)
    public String showCreateTestDeliverySettingsForm(final Model model) {
        final long existingOptionCount = assessmentDataService.countCallerUserDeliverySettings(AssessmentObjectType.ASSESSMENT_TEST);
        final TestDeliverySettingsTemplate template = assessmentDataService.createTestDeliverySettingsTemplate();
        template.setTitle("Test Delivery Settings #" + (existingOptionCount+1));

        model.addAttribute(template);
        return "createTestDeliverySettingsForm";
    }

    @RequestMapping(value="/deliverysettings/item/create", method=RequestMethod.POST)
    public String handleCreateItemDeliverySettingsForm(final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute ItemDeliverySettingsTemplate template,
            final BindingResult result)
            throws PrivilegeException {
        /* Validate command Object */
        if (result.hasErrors()) {
            return "createItemDeliverySettingsForm";
        }

        /* Try to create new entity */
        try {
            assessmentManagementService.createItemDeliverySettings(template);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }

        /* Go back to list */
        GlobalRouter.addFlashMessage(redirectAttributes, "Item Delivery Settings successfully created");
        return instructorRouter.buildInstructorRedirect("/deliverysettings/item");
    }

    @RequestMapping(value="/deliverysettings/test/create", method=RequestMethod.POST)
    public String handleCreateTestDeliverySettingsForm(final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute TestDeliverySettingsTemplate template,
            final BindingResult result)
            throws PrivilegeException {
        /* Validate command Object */
        if (result.hasErrors()) {
            return "createTestDeliverySettingsForm";
        }

        /* Try to create new entity */
        try {
            assessmentManagementService.createTestDeliverySettings(template);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }

        /* Go back to list */
        GlobalRouter.addFlashMessage(redirectAttributes, "Test Delivery Settings successfully created");
        return instructorRouter.buildInstructorRedirect("/deliverysettings/test");
    }

    @RequestMapping(value="/deliverysettings/{dsid}/delete", method=RequestMethod.POST)
    public String deleteDeliverySettings(@PathVariable final long dsid, final RedirectAttributes redirectAttributes)
            throws PrivilegeException, DomainEntityNotFoundException {
        /* Delete settings and update any Deliveries using them */
        final DeliverySettings deliverySettings = assessmentManagementService.lookupDeliverySettings(dsid);
        final int deliveriesAffected = assessmentManagementService.deleteDeliverySettings(dsid);

        /* Redirect back to list of settings */
        final StringBuilder flashMessageBuilder = new StringBuilder("Delivery Settings deleted.");
        if (deliveriesAffected>0) {
            if (deliveriesAffected==1) {
                flashMessageBuilder.append(" One Delivery was using these settings and has been updated to use default settings.");
            }
            else {
                flashMessageBuilder.append(" ")
                    .append(deliveriesAffected)
                    .append(" Deliveries were using these settings and have been updated to use default settings.");
            }
        }
        GlobalRouter.addFlashMessage(redirectAttributes, flashMessageBuilder.toString());
        return instructorRouter.buildInstructorRedirect("/deliverysettings/"
                + (deliverySettings.getAssessmentType()==AssessmentObjectType.ASSESSMENT_ITEM ? "item" : "test"));
    }

    @RequestMapping(value="/deliverysettings/item/{dsid}", method=RequestMethod.GET)
    public String showEditItemDeliverySettingsForm(@PathVariable final long dsid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException, IncompatiableDeliverySettingsException {
        final ItemDeliverySettings itemDeliverySettings = assessmentManagementService.lookupItemDeliverySettings(dsid);
        final ItemDeliverySettingsTemplate template = new ItemDeliverySettingsTemplate();
        assessmentDataService.mergeItemDeliverySettings(itemDeliverySettings, template);

        instructorModelHelper.setupModelForDeliverySettings(itemDeliverySettings, model);
        model.addAttribute(template);
        return "editItemDeliverySettingsForm";
    }

    @RequestMapping(value="/deliverysettings/test/{dsid}", method=RequestMethod.GET)
    public String showEditTestDeliverySettingsForm(@PathVariable final long dsid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException, IncompatiableDeliverySettingsException {
        final TestDeliverySettings testDeliverySettings = assessmentManagementService.lookupTestDeliverySettings(dsid);
        final TestDeliverySettingsTemplate template = new TestDeliverySettingsTemplate();
        assessmentDataService.mergeTestDeliverySettings(testDeliverySettings, template);

        instructorModelHelper.setupModelForDeliverySettings(testDeliverySettings, model);
        model.addAttribute(template);
        return "editTestDeliverySettingsForm";
    }

    @RequestMapping(value="/deliverysettings/item/{dsid}", method=RequestMethod.POST)
    public String handleEditItemDeliverySettingsForm(@PathVariable final long dsid, final Model model, final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute ItemDeliverySettingsTemplate template, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException, IncompatiableDeliverySettingsException {
        /* Validate command Object */
        if (result.hasErrors()) {
            instructorModelHelper.setupModelForDeliverySettings(dsid, model);
            return "editItemDeliverySettingsForm";
        }

        /* Perform update */
        try {
            assessmentManagementService.updateItemDeliverySettings(dsid, template);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }

        GlobalRouter.addFlashMessage(redirectAttributes, "Item Delivery Settings successfully changed");
        return instructorRouter.buildInstructorRedirect("/deliverysettings/item");
    }

    @RequestMapping(value="/deliverysettings/test/{dsid}", method=RequestMethod.POST)
    public String handleEditTestDeliverySettingsForm(@PathVariable final long dsid,
            final Model model, final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute TestDeliverySettingsTemplate template, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException, IncompatiableDeliverySettingsException {
        /* Validate command Object */
        if (result.hasErrors()) {
            instructorModelHelper.setupModelForDeliverySettings(dsid, model);
            return "editTestDeliverySettingsForm";
        }

        /* Perform update */
        try {
            assessmentManagementService.updateTestDeliverySettings(dsid, template);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }

        GlobalRouter.addFlashMessage(redirectAttributes, "Test Delivery Settings successfully changed");
        return instructorRouter.buildInstructorRedirect("/deliverysettings/test");
    }
}
