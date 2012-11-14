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
package uk.ac.ed.ph.qtiworks.web.controller.instructor;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.base.services.QtiWorksSettings;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;
import uk.ac.ed.ph.qtiworks.services.EntityGraphService;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException.APFIFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStateException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStateException.APSFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.EnumerableClientFailure;
import uk.ac.ed.ph.qtiworks.services.domain.ItemDeliverySettingsTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.TestDeliverySettingsTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.UpdateAssessmentCommand;
import uk.ac.ed.ph.qtiworks.web.domain.UploadAssessmentPackageCommand;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
public final class InstructorAssessmentManagementController {

    @Resource
    private QtiWorksSettings qtiWorksSettings;

    @Resource
    private InstructorRouter instructorRouter;

    @Resource
    private EntityGraphService entityGraphService;

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private CandidateSessionStarter candidateSessionStarter;

    //------------------------------------------------------

    /** Lists all Assignments owned by the caller */
    @RequestMapping(value="/assessments", method=RequestMethod.GET)
    public String listOwnAssessments(final Model model) {
        final List<Assessment> assessments = entityGraphService.getCallerAssessments();
        model.addAttribute(assessments);
        model.addAttribute("assessmentRouting", buildAssessmentListRouting(assessments));
        return "listAssessments";
    }

    public Map<Long, Map<String, String>> buildAssessmentListRouting(final List<Assessment> assessments) {
        final Map<Long, Map<String, String>> result = new HashMap<Long, Map<String, String>>();
        for (final Assessment assessment : assessments) {
            result.put(assessment.getId(), buildAssessmentRouting(assessment));
        }
        return result;
    }

    public Map<String, String> buildAssessmentRouting(final Assessment assessment) {
        return buildAssessmentRouting(assessment.getId().longValue());
    }

    public Map<String, String> buildAssessmentRouting(final long aid) {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("show", instructorRouter.buildWebUrl("/assessment/" + aid));
        result.put("edit", instructorRouter.buildWebUrl("/assessment/" + aid + "/edit"));
        result.put("upload", instructorRouter.buildWebUrl("/assessment/" + aid + "/upload"));
        result.put("validate", instructorRouter.buildWebUrl("/assessment/" + aid + "/validate"));
        result.put("try", instructorRouter.buildWebUrl("/assessment/" + aid + "/try"));
        result.put("deliveries", instructorRouter.buildWebUrl("/assessment/" + aid + "/deliveries"));
        result.put("createDelivery", instructorRouter.buildWebUrl("/assessment/" + aid + "/deliveries/create"));
        return result;
    }

    @ModelAttribute
    public void setupPrimaryRouting(final Model model) {
        final Map<String, String> primaryRouting = new HashMap<String, String>();
        primaryRouting.put("uploadAssessment", instructorRouter.buildWebUrl("/assessments/upload"));
        primaryRouting.put("listAssessments", instructorRouter.buildWebUrl("/assessments"));
        primaryRouting.put("listDeliverySettings", instructorRouter.buildWebUrl("/deliverysettings"));
        primaryRouting.put("createItemDeliverySettings", instructorRouter.buildWebUrl("/itemdeliverysettings/create"));
        primaryRouting.put("createTestDeliverySettings", instructorRouter.buildWebUrl("/testdeliverysettings/create"));
        model.addAttribute("instructorAssessmentRouting", primaryRouting);

    }

    private void setupModelForAssessment(final long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        setupModelForAssessment(assessmentManagementService.lookupOwnAssessment(aid), model);
    }

    private void setupModelForAssessment(final Assessment assessment, final Model model) {
        model.addAttribute("assessment", assessment);
        model.addAttribute("assessmentRouting", buildAssessmentRouting(assessment));
        model.addAttribute("assessmentPackage", entityGraphService.getCurrentAssessmentPackage(assessment));
        model.addAttribute("deliverySettingsList", entityGraphService.getCallerDeliverySettingsForType(assessment.getAssessmentType()));
    }

    //------------------------------------------------------

    @RequestMapping(value="/assessments/upload", method=RequestMethod.GET)
    public String showUploadAssessmentForm(final Model model) {
        model.addAttribute(new UploadAssessmentPackageCommand());
        return "uploadAssessmentForm";
    }

    /**
     * TODO: I'm doing upload + validation together. It would make sense later to split
     * these into 2 steps and find some way of showing progress.
     */
    @RequestMapping(value="/assessments/upload", method=RequestMethod.POST)
    public String handleUploadAssessmentForm(final @Valid @ModelAttribute UploadAssessmentPackageCommand command, final BindingResult result)
            throws PrivilegeException {
        /* Validate command Object */
        if (result.hasErrors()) {
            return "uploadAssessmentForm";
        }

        /* Attempt to import the package */
        Assessment assessment;
        try {
            assessment = assessmentManagementService.importAssessment(command.getFile());
        }
        catch (final AssessmentPackageFileImportException e) {
            final EnumerableClientFailure<APFIFailureReason> failure = e.getFailure();
            failure.registerErrors(result, "uploadAssessmentPackageCommand");
            return "uploadAssessmentForm";
        }
        try {
            assessmentManagementService.validateAssessment(assessment.getId().longValue());
        }
        catch (final DomainEntityNotFoundException e) {
            /* This could only happen if there's some kind of race condition */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        return instructorRouter.buildInstructorRedirect("/assessment/" + assessment.getId());
    }

    /**
     * Shows the Assessment having the given ID (aid)
     */
    @RequestMapping(value="/assessment/{aid}", method=RequestMethod.GET)
    public String showAssessment(final Model model, @PathVariable final long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupOwnAssessment(aid);
        setupModelForAssessment(assessment, model);
        return "showAssessment";
    }

    //------------------------------------------------------

    @RequestMapping(value="/assessment/{aid}/edit", method=RequestMethod.GET)
    public String showEditAssessmentForm(final Model model, @PathVariable final long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupOwnAssessment(aid);

        final UpdateAssessmentCommand command = new UpdateAssessmentCommand();
        command.setName(assessment.getName());
        command.setTitle(assessment.getTitle());
        command.setPublic(assessment.isPublic());
        model.addAttribute(command);

        setupModelForAssessment(assessment, model);
        return "editAssessmentForm";
    }

    @RequestMapping(value="/assessment/{aid}/edit", method=RequestMethod.POST)
    public String handleEditAssessmentForm(@PathVariable final long aid, final Model model,
            final @Valid @ModelAttribute UpdateAssessmentCommand command, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException {
        /* Validate command Object */
        if (result.hasErrors()) {
            setupModelForAssessment(aid, model);
            return "editAssessmentForm";
        }
        try {
            assessmentManagementService.updateAssessment(aid, command);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }
        return instructorRouter.buildInstructorRedirect("/assessment/" + aid);
    }

    //------------------------------------------------------

    @RequestMapping(value="/assessment/{aid}/upload", method=RequestMethod.GET)
    public String showUpdateAssessmentPackageForm(final @PathVariable long aid,
            final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        model.addAttribute(new UploadAssessmentPackageCommand());
        setupModelForAssessment(aid, model);
        return "updateAssessmentPackageForm";
    }

    /**
     * TODO: I'm doing upload + validation together. It would make sense later to split
     * these into 2 steps and find some way of showing progress.
     */
    @RequestMapping(value="/assessment/{aid}/upload", method=RequestMethod.POST)
    public String handleUploadAssessmentPackageForm(final @PathVariable long aid, final Model model,
            final @Valid @ModelAttribute UploadAssessmentPackageCommand command, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException {
        /* Make sure something was submitted */
        /* Validate command Object */
        if (result.hasErrors()) {
            setupModelForAssessment(aid, model);
            return "updateAssessmentPackageForm";
        }

        /* Attempt to import the package */
        final MultipartFile uploadFile = command.getFile();
        try {
            assessmentManagementService.updateAssessmentPackageFiles(aid, uploadFile);
        }
        catch (final AssessmentPackageFileImportException e) {
            final EnumerableClientFailure<APFIFailureReason> failure = e.getFailure();
            failure.registerErrors(result, "assessmentPackageUpload");
            setupModelForAssessment(aid, model);
            return "updateAssessmentPackageForm";
        }
        catch (final AssessmentStateException e) {
            final EnumerableClientFailure<APSFailureReason> failure = e.getFailure();
            failure.registerErrors(result, "assessmentPackageUpload");
            setupModelForAssessment(aid, model);
            return "updateAssessmentPackageForm";
        }
        try {
            assessmentManagementService.validateAssessment(aid);
        }
        catch (final DomainEntityNotFoundException e) {
            /* This could only happen if there's some kind of race condition */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        return instructorRouter.buildInstructorRedirect("/assessment/{aid}");
    }

    //------------------------------------------------------

    /** TODO: For performance, we should cache the validation result */
    @RequestMapping(value="/assessment/{aid}/validate", method=RequestMethod.GET)
    public String validateAssessment(final @PathVariable long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final AssessmentObjectValidationResult<?> validationResult = assessmentManagementService.validateAssessment(aid);
        model.addAttribute("validationResult", validationResult);
        setupModelForAssessment(aid, model);
        return "validationResult";
    }

    //------------------------------------------------------

    @RequestMapping(value="/assessment/{aid}/try", method=RequestMethod.POST)
    public String tryAssessment(final @PathVariable long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupOwnAssessment(aid);
        final Delivery demoDelivery = assessmentManagementService.createDemoDelivery(assessment);

        return runDelivery(aid, demoDelivery);
    }

    @RequestMapping(value="/assessment/{aid}/try/{dsid}", method=RequestMethod.POST)
    public String tryAssessment(final @PathVariable long aid, final @PathVariable long dsid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupOwnAssessment(aid);
        final DeliverySettings deliverySettings = assessmentManagementService.lookupAndMatchDeliverySettings(dsid, assessment);
        final Delivery demoDelivery = assessmentManagementService.createDemoDelivery(assessment, deliverySettings);

        return runDelivery(aid, demoDelivery);
    }

    private String runDelivery(final long aid, final Delivery delivery)
            throws PrivilegeException {
        final String exitUrl = instructorRouter.buildWithinContextUrl("/assessment/" + aid);
        final CandidateSession candidateSession = candidateSessionStarter.createCandidateSession(delivery, exitUrl);
        return instructorRouter.buildSessionStartRedirect(candidateSession);
    }

    //------------------------------------------------------
    // Management of ItemDeliveries

    @RequestMapping(value="/assessment/{aid}/deliveries", method=RequestMethod.GET)
    public String listDeliveries(final @PathVariable long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupOwnAssessment(aid);
        final List<Delivery> deliveries = entityGraphService.getCallerDeliveries(assessment);
        model.addAttribute(assessment);
        model.addAttribute(deliveries);
        model.addAttribute("assessmentRouting", buildAssessmentRouting(assessment));
        model.addAttribute("deliveryListRouting", buildDeliveryListRouting(deliveries));
        return "listDeliveries";
    }

    @RequestMapping(value="/delivery/{did}", method=RequestMethod.GET)
    public String showOwnDelivery(final Model model, @PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery delivery = assessmentManagementService.lookupOwnDelivery(did);
        setupModelForDelivery(delivery, model);
        return "showDelivery";
    }

    @RequestMapping(value="/delivery/{did}/try", method=RequestMethod.POST)
    public String tryOwnDelivery(final @PathVariable long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery delivery = assessmentManagementService.lookupOwnDelivery(did);
        final String exitUrl = instructorRouter.buildWithinContextUrl("/delivery/" + did);
        final CandidateSession candidateSession = candidateSessionStarter.createCandidateSession(delivery, exitUrl);
        return instructorRouter.buildSessionStartRedirect(candidateSession);
    }

    /** (Deliveries are currently very simple so created using a sensible default) */
    @RequestMapping(value="/assessment/{aid}/deliveries/create", method=RequestMethod.POST)
    public String createDelivery(final @PathVariable long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery delivery = assessmentManagementService.createDelivery(aid);
        return instructorRouter.buildInstructorRedirect("/delivery/" + delivery.getId().longValue());
    }

    @RequestMapping(value="/delivery/{did}/edit", method=RequestMethod.GET)
    public String showEditDeliveryForm(final Model model, @PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery delivery = assessmentManagementService.lookupOwnDelivery(did);

        final DeliveryTemplate template = new DeliveryTemplate();
        template.setTitle(delivery.getTitle());
        template.setOpen(delivery.isOpen());
        template.setLtiEnabled(delivery.isLtiEnabled());
        template.setDsid(delivery.getDeliverySettings().getId());

        model.addAttribute(template);
        setupModelForDelivery(delivery, model);
        return "editDeliveryForm";
    }

    @RequestMapping(value="/delivery/{did}/edit", method=RequestMethod.POST)
    public String handleEditDeliveryForm(final Model model, @PathVariable final long did,
            final @Valid @ModelAttribute DeliveryTemplate template, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException {
        /* Validate command Object */
        if (result.hasErrors()) {
            setupModelForDelivery(did, model);
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
        return instructorRouter.buildInstructorRedirect("/delivery/" + did);
    }

    public Map<Long, Map<String, String>> buildDeliveryListRouting(final List<Delivery> deliveries) {
        final Map<Long, Map<String, String>> result = new HashMap<Long, Map<String, String>>();
        for (final Delivery delivery : deliveries) {
            result.put(delivery.getId(), buildDeliveryRouting(delivery));
        }
        return result;
    }

    public Map<String, String> buildDeliveryRouting(final Delivery delivery) {
        return buildDeliveryRouting(delivery.getId().longValue());
    }

    public Map<String, String> buildDeliveryRouting(final long did) {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("show", instructorRouter.buildWebUrl("/delivery/" + did));
        result.put("edit", instructorRouter.buildWebUrl("/delivery/" + did + "/edit"));
        result.put("try", instructorRouter.buildWebUrl("/delivery/" + did + "/try"));
        result.put("summaryreportcsv", instructorRouter.buildWebUrl("/delivery/" + did + "/summaryreport.csv"));
        result.put("ltiLaunch", qtiWorksSettings.getBaseUrl() + "/lti/launch/" + did);
        return result;
    }

    private void setupModelForDelivery(final long did, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        setupModelForDelivery(assessmentManagementService.lookupOwnDelivery(did), model);
    }

    private void setupModelForDelivery(final Delivery delivery, final Model model) {
        final Assessment assessment = delivery.getAssessment();
        model.addAttribute(delivery);
        model.addAttribute(assessment);
        model.addAttribute("assessmentRouting", buildAssessmentRouting(assessment));
        model.addAttribute("deliveryRouting", buildDeliveryRouting(delivery));
        model.addAttribute("deliverySettingsList", entityGraphService.getCallerDeliverySettingsForType(delivery.getAssessment().getAssessmentType()));
    }

    //------------------------------------------------------
    // Management of DeliverySettings (and subtypes)

    @RequestMapping(value="/deliverysettings", method=RequestMethod.GET)
    public String listOwnDeliverySettings(final Model model) {
        final List<DeliverySettings> deliverySettingsList = entityGraphService.getCallerDeliverySettings();
        model.addAttribute("deliverySettingsList", deliverySettingsList);
        model.addAttribute("deliverySettingsRouting", buildDeliverySettingsListRouting(deliverySettingsList));
        return "listDeliverySettings";
    }

    @RequestMapping(value="/itemdeliverysettings/create", method=RequestMethod.GET)
    public String showCreateItemDeliverySettingsForm(final Model model) {
        final long existingSettingsCount = entityGraphService.countCallerDeliverySettings(AssessmentObjectType.ASSESSMENT_ITEM);
        final ItemDeliverySettingsTemplate template = assessmentManagementService.createItemDeliverySettingsTemplate();
        template.setTitle("Item Delivery Settings #" + (existingSettingsCount+1));

        model.addAttribute(template);
        return "createItemDeliverySettingsForm";
    }

    @RequestMapping(value="/itemdeliverysettings/create", method=RequestMethod.POST)
    public String handleCreateItemDeliverySettingsForm(final @Valid @ModelAttribute ItemDeliverySettingsTemplate template,
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
        return instructorRouter.buildInstructorRedirect("/deliverysettings");
    }

    @RequestMapping(value="/itemdeliverysettings/{dsid}", method=RequestMethod.GET)
    public String showEditItemDeliverySettingsForm(final Model model, @PathVariable final long dsid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final ItemDeliverySettings itemDeliverySettings = assessmentManagementService.lookupItemDeliverySettings(dsid);
        final ItemDeliverySettingsTemplate template = new ItemDeliverySettingsTemplate();
        assessmentManagementService.mergeItemDeliverySettings(itemDeliverySettings, template);

        model.addAttribute(itemDeliverySettings);
        model.addAttribute(template);
        return "editItemDeliverySettingsForm";
    }

    @RequestMapping(value="/itemdeliverysettings/{dsid}", method=RequestMethod.POST)
    public String handleEditItemDeliverySettingsForm(final Model model, @PathVariable final long dsid,
            final @Valid @ModelAttribute ItemDeliverySettingsTemplate template, final BindingResult result,
            final RedirectAttributes redirectAttributes)
            throws PrivilegeException, DomainEntityNotFoundException {
        /* Validate command Object */
        if (result.hasErrors()) {
            setupModelForDeliverySettings(dsid, model);
            return "editItemDeliverySettingsForm";
        }

        /* Perform update */
        try {
            assessmentManagementService.updateItemDeliverySettings(dsid, template);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }

        /* Return to show/edit with a flash message */
        redirectAttributes.addFlashAttribute("flashMessage", "These Item Delivery Settings have been updated");
        return instructorRouter.buildInstructorRedirect("/itemdeliverysettings/" + dsid);
    }

    @RequestMapping(value="/testdeliverysettings/create", method=RequestMethod.GET)
    public String showCreateTestDeliverySettingsForm(final Model model) {
        final long existingOptionCount = entityGraphService.countCallerDeliverySettings(AssessmentObjectType.ASSESSMENT_TEST);
        final TestDeliverySettingsTemplate template = assessmentManagementService.createTestDeliverySettingsTemplate();
        template.setTitle("Test Delivery Settings #" + (existingOptionCount+1));

        model.addAttribute(template);
        return "createTestDeliverySettingsForm";
    }

    @RequestMapping(value="/testdeliverysettings/create", method=RequestMethod.POST)
    public String handleCreateTestDeliverySettingsForm(final @Valid @ModelAttribute TestDeliverySettingsTemplate template,
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
        return instructorRouter.buildInstructorRedirect("/deliverysettings");
    }

    @RequestMapping(value="/testdeliverysettings/{dsid}", method=RequestMethod.GET)
    public String showEditTestDeliverySettingsForm(final Model model, @PathVariable final long dsid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final TestDeliverySettings testDeliverySettings = assessmentManagementService.lookupTestDeliverySettings(dsid);
        final TestDeliverySettingsTemplate template = new TestDeliverySettingsTemplate();
        assessmentManagementService.mergeTestDeliverySettings(testDeliverySettings, template);

        model.addAttribute(testDeliverySettings);
        model.addAttribute(template);
        return "editTestDeliverySettingsForm";
    }

    @RequestMapping(value="/testdeliverysettings/{dsid}", method=RequestMethod.POST)
    public String handleEditTestDeliverySettingsForm(final Model model, @PathVariable final long dsid,
            final @Valid @ModelAttribute TestDeliverySettingsTemplate template, final BindingResult result,
            final RedirectAttributes redirectAttributes)
            throws PrivilegeException, DomainEntityNotFoundException {
        /* Validate command Object */
        if (result.hasErrors()) {
            setupModelForDeliverySettings(dsid, model);
            return "editTestDeliverySettingsForm";
        }

        /* Perform update */
        try {
            assessmentManagementService.updateTestDeliverySettings(dsid, template);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }

        /* Return to show/edit with a flash message */
        redirectAttributes.addFlashAttribute("flashMessage", "These Test Delivery Settings have been updated");
        return instructorRouter.buildInstructorRedirect("/testdeliverysettings/" + dsid);
    }

    public Map<Long, Map<String, String>> buildDeliverySettingsListRouting(final List<DeliverySettings> deliverySettingsList) {
        final Map<Long, Map<String, String>> result = new HashMap<Long, Map<String, String>>();
        for (final DeliverySettings deliverySettings : deliverySettingsList) {
            result.put(deliverySettings.getId(), buildDeliverySettingsRouting(deliverySettings));
        }
        return result;
    }

    public Map<String, String> buildDeliverySettingsRouting(final DeliverySettings deliverySettings) {
        final long dsid = deliverySettings.getId().longValue();
        final Map<String, String> result = new HashMap<String, String>();

        String showEditPath;
        switch (deliverySettings.getAssessmentType()) {
            case ASSESSMENT_ITEM:
                showEditPath = "/itemdeliverysettings/" + dsid;
                break;

            case ASSESSMENT_TEST:
                showEditPath = "/testdeliverysettings/" + dsid;
                break;

            default:
                throw new QtiWorksLogicException("Unexpected switch case " + deliverySettings.getAssessmentType());
        }
        result.put("showOrEdit", instructorRouter.buildWebUrl(showEditPath));
        return result;
    }

    private void setupModelForDeliverySettings(final long dsid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        setupModelForDeliverySettings(assessmentManagementService.lookupDeliverySettings(dsid), model);
    }

    private void setupModelForDeliverySettings(final DeliverySettings deliverySettings, final Model model) {
        model.addAttribute("deliverySettings", deliverySettings);
    }
}
