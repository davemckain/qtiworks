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
package uk.ac.ed.ph.qtiworks.web.controller.lti;

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
import uk.ac.ed.ph.qtiworks.services.domain.EnumerableClientFailure;
import uk.ac.ed.ph.qtiworks.services.domain.IncompatiableDeliverySettingsException;
import uk.ac.ed.ph.qtiworks.services.domain.ItemDeliverySettingsTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.services.domain.TestDeliverySettingsTemplate;
import uk.ac.ed.ph.qtiworks.web.GlobalRouter;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionLaunchService;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionTicket;
import uk.ac.ed.ph.qtiworks.web.domain.UploadAssessmentPackageCommand;
import uk.ac.ed.ph.qtiworks.web.lti.LtiIdentityContext;
import uk.ac.ed.ph.qtiworks.web.lti.LtiResourceAuthenticationFilter;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
 * Controller for instructor assessment management when running over LTI (domain-level launch)
 *
 * @author David McKain
 */
@Controller
@RequestMapping("/resource/{lrid}")
public class LtiInstructorAssessmentManagementController {

    @Resource
    private LtiInstructorRouter ltiInstructorRouter;

    @Resource
    private LtiInstructorModelHelper ltiInstructorModelHelper;

    @Resource
    private IdentityService identityService;

    @Resource
    private AssessmentDataService assessmentDataService;

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private CandidateSessionLaunchService candidateSessionLaunchService;

    //------------------------------------------------------

    @ModelAttribute
    public void setupModel(final Model model) {
        ltiInstructorModelHelper.setupModel(model);
    }

    //------------------------------------------------------

    @RequestMapping(value="", method=RequestMethod.GET)
    public String resourceTopPage(final Model model) {
        ltiInstructorModelHelper.setupModel(model);
        final Assessment thisAssessment = identityService.assertCurrentThreadLtiIdentityContext().getLtiResource().getDelivery().getAssessment();
        if (thisAssessment==null) {
            return "instructor/initialSetup";
        }
        return "instructor/resourceDashboard";
    }

    /**
     * Exits this management session, returning to the URL that was specified in
     * the LTI launch data.
     * <p>
     * NB: The view layer does not include this option if no such URL was sent
     * by the TC, so we simply response with an error in that case.
     */
    @RequestMapping(value="/exit", method=RequestMethod.POST)
    public String exit(final HttpSession httpSession, final HttpServletResponse response) throws IOException {
        /* Extract return URL */
        final LtiIdentityContext ltiIdentityContext = identityService.assertCurrentThreadLtiIdentityContext();
        final String returnUrl = ltiIdentityContext.getReturnUrl();

        /* Revoke user's access to this resource */
        LtiResourceAuthenticationFilter.deauthenticateUserFromResource(httpSession, ltiIdentityContext.getLtiResource());

        /* Finally redirect if possible */
        if (returnUrl==null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "The tool consumer did not send a return URL to exit to");
            return null;
        }
        return "redirect:" + returnUrl;
    }

    /** FIXME: This is temporary for debugging purposes. Remove this some time later on... */
    @RequestMapping(value="/debug", method=RequestMethod.GET)
    public String diagnosticsPage(final Model model) {
        ltiInstructorModelHelper.setupModel(model);
        return "instructor/debug";
    }

    @RequestMapping(value="/try", method=RequestMethod.POST)
    public String tryThisAssessment(final HttpSession httpSession, final HttpServletResponse response)
            throws PrivilegeException, IOException,
            CandidateException, IncompatiableDeliverySettingsException {
        final Delivery thisDelivery = identityService.getCurrentThreadLtiIdentityContext().getLtiResource().getDelivery();
        final Assessment thisAssessment = thisDelivery.getAssessment();
        if (thisAssessment==null) {
            /* Assessment hasn't been matched to this resource yet */
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "An Assessment has not been selected for this resource yet");
            return null;
        }
        final DeliverySettings theseDeliverySettings = thisDelivery.getDeliverySettings(); /* NB: May be null */
        final Delivery demoDelivery = assessmentManagementService.createDemoDelivery(thisAssessment, theseDeliverySettings);
        final String sessionExitReturnUrl = ltiInstructorRouter.buildWithinContextUrl(""); /* (Back to dashboard) */
        return runDelivery(httpSession, demoDelivery, true, sessionExitReturnUrl);
    }

    @RequestMapping(value="/toggle-availability", method=RequestMethod.POST)
    public String toggleThisDeliveryOpenStatus()
            throws PrivilegeException {
        final Delivery thisDelivery = identityService.getCurrentThreadLtiIdentityContext().getLtiResource().getDelivery();
        try {
            assessmentManagementService.setDeliveryOpenStatus(thisDelivery.getId(), !thisDelivery.isOpen());
        }
        catch (final DomainEntityNotFoundException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        return ltiInstructorRouter.buildInstructorRedirect(""); /* Return immediately to dashboard */
    }

    //------------------------------------------------------
    // Assessment management

    /** Lists all Assignments in this LTI context */
    @RequestMapping(value="/assessments", method=RequestMethod.GET)
    public String listContextAssessments(final Model model) {
        final List<AssessmentAndPackage> assessments = assessmentDataService.getCallerLtiContextAssessments();
        model.addAttribute(assessments);
        model.addAttribute("assessmentListRouting", ltiInstructorRouter.buildAssessmentListRouting(assessments));
        return "instructor/listAssessments";
    }

    @RequestMapping(value="/assessments/upload-and-use", method=RequestMethod.GET)
    public String showUploadAndUseAssessmentForm(final Model model) {
        model.addAttribute(new UploadAssessmentPackageCommand());
        return "instructor/uploadAndUseAssessmentForm";
    }

    @RequestMapping(value="/assessments/upload-and-use", method=RequestMethod.POST)
    public String handleUploadAndUseAssessmentForm(final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute UploadAssessmentPackageCommand command,
            final BindingResult result)
            throws PrivilegeException {
        Assessment assessment = null;
        if (!result.hasErrors()) {
            try {
                /* No binding errors, so attempt to import and validate the package */
                assessment = assessmentManagementService.importAssessment(command.getFile(), true);

                /* Use this assessment */
                assessmentManagementService.selectCurrentLtiResourceAssessment(assessment.getId());
            }
            catch (final AssessmentPackageDataImportException e) {
                final EnumerableClientFailure<ImportFailureReason> failure = e.getFailure();
                failure.registerErrors(result, "assessmentPackageUpload");
            }
            catch (final DomainEntityNotFoundException e) {
                throw new QtiWorksRuntimeException("New assessment disappeared immediately?");
            }
        }
        if (result.hasErrors()) {
            /* Return to form if any binding/service errors */
            return "instructor/uploadAndUseAssessmentForm";
        }
        GlobalRouter.addFlashMessage(redirectAttributes, "Assessment successfully created for immediate use");
        return ltiInstructorRouter.buildInstructorRedirect(""); /* Return immediately to dashboard */
    }

    @RequestMapping(value="/assessments/upload", method=RequestMethod.GET)
    public String showUploadAssessmentForm(final Model model) {
        model.addAttribute(new UploadAssessmentPackageCommand());
        return "instructor/uploadAssessmentForm";
    }

    @RequestMapping(value="/assessments/upload", method=RequestMethod.POST)
    public String handleUploadAssessmentForm(final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute UploadAssessmentPackageCommand command,
            final BindingResult result)
            throws PrivilegeException {
        if (result.hasErrors()) {
            /* Return to form if any binding errors */
            return "instructor/uploadAssessmentForm";
        }
        /* No binding errors, so attempt to import and validate the package */
        Assessment assessment;
        try {
            assessment = assessmentManagementService.importAssessment(command.getFile(), true);
        }
        catch (final AssessmentPackageDataImportException e) {
            final EnumerableClientFailure<ImportFailureReason> failure = e.getFailure();
            failure.registerErrors(result, "assessmentPackageUpload");
            return "instructor/uploadAssessmentForm";
        }
        GlobalRouter.addFlashMessage(redirectAttributes, "Assessment successfully created");
        return ltiInstructorRouter.buildInstructorRedirect("/assessment/" + assessment.getId());
    }

    /** Shows the Assessment having the given ID (aid) */
    @RequestMapping(value="/assessment/{aid}", method=RequestMethod.GET)
    public String showAssessment(@PathVariable final long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);
        ltiInstructorModelHelper.setupModelForAssessment(assessment, model);
        return "instructor/showAssessment";
    }

    @RequestMapping(value="/assessment/{aid}/select", method=RequestMethod.POST)
    public String selectAssessment(@PathVariable final long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        assessmentManagementService.selectCurrentLtiResourceAssessment(aid);
        return ltiInstructorRouter.buildInstructorRedirect("/");
    }

    @RequestMapping(value="/assessment/{aid}/replace", method=RequestMethod.GET)
    public String showReplaceAssessmentPackageForm(final @PathVariable long aid,
            final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        model.addAttribute(new UploadAssessmentPackageCommand());
        ltiInstructorModelHelper.setupModelForAssessment(aid, model);
        return "instructor/replaceAssessmentPackageForm";
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
            ltiInstructorModelHelper.setupModelForAssessment(aid, model);
            return "instructor/replaceAssessmentPackageForm";
        }
        GlobalRouter.addFlashMessage(redirectAttributes, "Assessment package content successfully replaced");
        return ltiInstructorRouter.buildInstructorRedirect("/assessment/{aid}");
    }

    @RequestMapping(value="/assessment/{aid}/validate", method=RequestMethod.GET)
    public String validateAssessment(final @PathVariable long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final AssessmentObjectValidationResult<?> validationResult = assessmentManagementService.validateAssessment(aid);
        model.addAttribute("validationResult", validationResult);
        ltiInstructorModelHelper.setupModelForAssessment(aid, model);
        return "instructor/validationResult";
    }

    @RequestMapping(value="/assessment/{aid}/outcomes-settings", method=RequestMethod.GET)
    public String showSetLtiOutcomesForm(final @PathVariable long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);

        final AssessmentLtiOutcomesSettingsTemplate template = new AssessmentLtiOutcomesSettingsTemplate();
        template.setResultOutcomeIdentifier(assessment.getLtiResultOutcomeIdentifier());
        template.setResultMaximum(assessment.getLtiResultMaximum());
        template.setResultMinimum(assessment.getLtiResultMinimum());

        ltiInstructorModelHelper.setupModelForAssessment(assessment, model);
        model.addAttribute("outcomeDeclarationList", assessmentDataService.getOutcomeVariableDeclarations(assessment));
        model.addAttribute(template);
        return "instructor/assessmentOutcomesSettingsForm";
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
            final Assessment assessment = ltiInstructorModelHelper.setupModelForAssessment(aid, model);
            model.addAttribute("outcomeDeclarationList", assessmentDataService.getOutcomeVariableDeclarations(assessment));
            return "instructor/assessmentOutcomesSettingsForm";
        }
        /* Successful */
        GlobalRouter.addFlashMessage(redirectAttributes, "Assessment LTI outcomes settings saved successfully");
        return ltiInstructorRouter.buildInstructorRedirect("/assessment/" + aid);
    }

    @RequestMapping(value="/assessment/{aid}/delete", method=RequestMethod.POST)
    public String deleteAssessment(final @PathVariable long aid, final RedirectAttributes redirectAttributes)
            throws PrivilegeException, DomainEntityNotFoundException {
        assessmentManagementService.deleteAssessment(aid);
        GlobalRouter.addFlashMessage(redirectAttributes, "Assessment successfully deleted");
        return ltiInstructorRouter.buildInstructorRedirect("/assessments");
    }

    @RequestMapping(value="/assessment/{aid}/try", method=RequestMethod.POST)
    public String tryAssessment(final HttpSession httpSession, final @PathVariable long aid)
            throws PrivilegeException, DomainEntityNotFoundException, CandidateException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);
        final Delivery demoDelivery = assessmentManagementService.createDemoDelivery(assessment);
        final String sessionExitReturnUrl = ltiInstructorRouter.buildWithinContextUrl("/assessment/" + aid);
        return runDelivery(httpSession, demoDelivery, true, sessionExitReturnUrl);
    }

    @RequestMapping(value="/assessment/{aid}/try/{dsid}", method=RequestMethod.POST)
    public String tryAssessment(final HttpSession httpSession, final @PathVariable long aid, final @PathVariable long dsid)
            throws PrivilegeException, DomainEntityNotFoundException,
            CandidateException, IncompatiableDeliverySettingsException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);
        final DeliverySettings deliverySettings = assessmentManagementService.lookupAndMatchDeliverySettings(dsid, assessment);
        final Delivery demoDelivery = assessmentManagementService.createDemoDelivery(assessment, deliverySettings);
        final String sessionExitReturnUrl = ltiInstructorRouter.buildWithinContextUrl("/assessment/" + aid);
        return runDelivery(httpSession, demoDelivery, true, sessionExitReturnUrl);
    }

    private String runDelivery(final HttpSession httpSession, final Delivery delivery, final boolean authorMode, final String sessionExitReturnUrl)
            throws CandidateException {
        /* FIXME: Need to move the target method up to WS level */
        final User caller = identityService.getCurrentThreadUser();
        final CandidateSessionTicket candidateSessionTicket = candidateSessionLaunchService.launchInstructorTrialSession(httpSession, caller, delivery, authorMode, sessionExitReturnUrl);
        return GlobalRouter.buildSessionStartRedirect(candidateSessionTicket);
    }

    //------------------------------------------------------
    // Management of DeliverySettings (and subtypes)

    @RequestMapping(value="/deliverysettings", method=RequestMethod.GET)
    public String deliverySettingsManager() {
        return "instructor/deliverySettingsManager";
    }

    @RequestMapping(value="/deliverysettings/item", method=RequestMethod.GET)
    public String listContextItemDeliverySettings(final Model model) {
        final List<DeliverySettings> deliverySettingsList = assessmentDataService.getCallerLtiContextDeliverySettingsForType(AssessmentObjectType.ASSESSMENT_ITEM);
        model.addAttribute("deliverySettingsList", deliverySettingsList);
        model.addAttribute("deliverySettingsListRouting", ltiInstructorRouter.buildDeliverySettingsListRouting(deliverySettingsList));
        return "instructor/listItemDeliverySettings";
    }

    @RequestMapping(value="/deliverysettings/test", method=RequestMethod.GET)
    public String listContextTestDeliverySettings(final Model model) {
        final List<DeliverySettings> deliverySettingsList = assessmentDataService.getCallerLtiContextDeliverySettingsForType(AssessmentObjectType.ASSESSMENT_TEST);
        model.addAttribute("deliverySettingsList", deliverySettingsList);
        model.addAttribute("deliverySettingsListRouting", ltiInstructorRouter.buildDeliverySettingsListRouting(deliverySettingsList));
        return "instructor/listTestDeliverySettings";
    }

    @RequestMapping(value="/deliverysettings/item/create", method=RequestMethod.GET)
    public String showCreateItemDeliverySettingsForm(final Model model) {
        final long existingSettingsCount = assessmentDataService.countCallerLtiContextDeliverySettings(AssessmentObjectType.ASSESSMENT_ITEM);
        final ItemDeliverySettingsTemplate template = assessmentDataService.createItemDeliverySettingsTemplate();
        template.setTitle("Item Delivery Settings #" + (existingSettingsCount+1));

        model.addAttribute(template);
        return "instructor/createItemDeliverySettingsForm";
    }

    @RequestMapping(value="/deliverysettings/test/create", method=RequestMethod.GET)
    public String showCreateTestDeliverySettingsForm(final Model model) {
        final long existingOptionCount = assessmentDataService.countCallerLtiContextDeliverySettings(AssessmentObjectType.ASSESSMENT_TEST);
        final TestDeliverySettingsTemplate template = assessmentDataService.createTestDeliverySettingsTemplate();
        template.setTitle("Test Delivery Settings #" + (existingOptionCount+1));

        model.addAttribute(template);
        return "instructor/createTestDeliverySettingsForm";
    }

    @RequestMapping(value="/deliverysettings/item/create", method=RequestMethod.POST)
    public String handleCreateItemDeliverySettingsForm(final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute ItemDeliverySettingsTemplate template,
            final BindingResult result)
            throws PrivilegeException {
        /* Validate command Object */
        if (result.hasErrors()) {
            return "instructor/createItemDeliverySettingsForm";
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
        return ltiInstructorRouter.buildInstructorRedirect("/deliverysettings/item");
    }

    @RequestMapping(value="/deliverysettings/test/create", method=RequestMethod.POST)
    public String handleCreateTestDeliverySettingsForm(final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute TestDeliverySettingsTemplate template,
            final BindingResult result)
            throws PrivilegeException {
        /* Validate command Object */
        if (result.hasErrors()) {
            return "instructor/createTestDeliverySettingsForm";
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
        return ltiInstructorRouter.buildInstructorRedirect("/deliverysettings/test");
    }

    @RequestMapping(value="/deliverysettings/{dsid}/select", method=RequestMethod.POST)
    public String selectDeliverySettings(@PathVariable final long dsid)
            throws PrivilegeException, DomainEntityNotFoundException, IncompatiableDeliverySettingsException {
        assessmentManagementService.selectCurrentLtiResourceDeliverySettings(dsid);
        return ltiInstructorRouter.buildInstructorRedirect("/");
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
        return ltiInstructorRouter.buildInstructorRedirect("/deliverysettings/"
                + (deliverySettings.getAssessmentType()==AssessmentObjectType.ASSESSMENT_ITEM ? "item" : "test"));
    }

    @RequestMapping(value="/deliverysettings/item/{dsid}", method=RequestMethod.GET)
    public String showEditItemDeliverySettingsForm(@PathVariable final long dsid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException, IncompatiableDeliverySettingsException {
        final ItemDeliverySettings itemDeliverySettings = assessmentManagementService.lookupItemDeliverySettings(dsid);
        final ItemDeliverySettingsTemplate template = new ItemDeliverySettingsTemplate();
        assessmentDataService.mergeItemDeliverySettings(itemDeliverySettings, template);

        ltiInstructorModelHelper.setupModelForDeliverySettings(itemDeliverySettings, model);
        model.addAttribute(template);
        return "instructor/editItemDeliverySettingsForm";
    }

    @RequestMapping(value="/deliverysettings/test/{dsid}", method=RequestMethod.GET)
    public String showEditTestDeliverySettingsForm(@PathVariable final long dsid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException, IncompatiableDeliverySettingsException {
        final TestDeliverySettings testDeliverySettings = assessmentManagementService.lookupTestDeliverySettings(dsid);
        final TestDeliverySettingsTemplate template = new TestDeliverySettingsTemplate();
        assessmentDataService.mergeTestDeliverySettings(testDeliverySettings, template);

        ltiInstructorModelHelper.setupModelForDeliverySettings(testDeliverySettings, model);
        model.addAttribute(template);
        return "instructor/editTestDeliverySettingsForm";
    }

    @RequestMapping(value="/deliverysettings/item/{dsid}", method=RequestMethod.POST)
    public String handleEditItemDeliverySettingsForm(@PathVariable final long dsid, final Model model, final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute ItemDeliverySettingsTemplate template, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException, IncompatiableDeliverySettingsException {
        /* Validate command Object */
        if (result.hasErrors()) {
            ltiInstructorModelHelper.setupModelForDeliverySettings(dsid, model);
            return "instructor/editItemDeliverySettingsForm";
        }

        /* Perform update */
        try {
            assessmentManagementService.updateItemDeliverySettings(dsid, template);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }

        GlobalRouter.addFlashMessage(redirectAttributes, "Item Delivery Settings successfully changed");
        return ltiInstructorRouter.buildInstructorRedirect("/deliverysettings/item");
    }

    @RequestMapping(value="/deliverysettings/test/{dsid}", method=RequestMethod.POST)
    public String handleEditTestDeliverySettingsForm(@PathVariable final long dsid,
            final Model model, final RedirectAttributes redirectAttributes,
            final @Valid @ModelAttribute TestDeliverySettingsTemplate template, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException, IncompatiableDeliverySettingsException {
        /* Validate command Object */
        if (result.hasErrors()) {
            ltiInstructorModelHelper.setupModelForDeliverySettings(dsid, model);
            return "instructor/editTestDeliverySettingsForm";
        }

        /* Perform update */
        try {
            assessmentManagementService.updateTestDeliverySettings(dsid, template);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }

        GlobalRouter.addFlashMessage(redirectAttributes, "Test Delivery Settings successfully changed");
        return ltiInstructorRouter.buildInstructorRedirect("/deliverysettings/test");
    }

}
