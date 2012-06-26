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
package uk.ac.ed.ph.qtiworks.web.instructor.controller;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliveryOptions;
import uk.ac.ed.ph.qtiworks.services.AssessmentCandidateService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException.APFIFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStateException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStateException.APSFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.EnumerableClientFailure;
import uk.ac.ed.ph.qtiworks.services.domain.UpdateAssessmentCommand;
import uk.ac.ed.ph.qtiworks.web.instructor.domain.UploadAssessmentPackageCommand;

import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
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

/**
 * Controller providing management functions for {@link Assessment}s
 *
 * @author David McKain
 */
@Controller
public final class InstructorAssessmentManagementController {

    private static final String controllerPath = "/web/instructor";

    @Resource
    private String contextPath;

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private AssessmentCandidateService assessmentCandidateService;

    //------------------------------------------------------

    /** Lists all Assignments owned by the caller */
    @RequestMapping(value="/assessments", method=RequestMethod.GET)
    public String listCallerAssessments(final Model model) {
        final List<Assessment> assessments = assessmentManagementService.getCallerAssessments();
        model.addAttribute(assessments);
        model.addAttribute("assessmentRouting", buildAssessmentListRouting(assessments));
        return "assessmentList";
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
        result.put("show", buildActionPath("/assessment/" + aid));
        result.put("edit", buildActionPath("/assessment/" + aid + "/edit"));
        result.put("upload", buildActionPath("/assessment/" + aid + "/upload"));
        result.put("validate", buildActionPath("/assessment/" + aid + "/validate"));
        result.put("try", buildActionPath("/assessment/" + aid + "/try"));
        return result;
    }

    private String buildActionPath(final String requestPath) {
        return contextPath + controllerPath + requestPath;
    }

    private String buildActionRedirect(final String requestPath) {
        return "redirect:" + controllerPath + requestPath;
    }

    @ModelAttribute
    public void setupPrimaryRouting(final Model model) {
        final Map<String, String> primaryRouting = new HashMap<String, String>();
        primaryRouting.put("uploadAssessment", buildActionPath("/assessments/upload"));
        primaryRouting.put("listAssessments", buildActionPath("/assessments"));
        primaryRouting.put("listItemDeliveryOptions", buildActionPath("/deliveryoptions"));
        primaryRouting.put("createItemDeliveryOptions", buildActionPath("/deliveryoptions/create"));
        model.addAttribute("instructorAssessmentRouting", primaryRouting);
    }

    //------------------------------------------------------

    /**
     * Shows the Assessment having the given ID (aid)
     */
    @RequestMapping(value="/assessment/{aid}", method=RequestMethod.GET)
    public String showAssessment(final Model model, @PathVariable final long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);
        final AssessmentPackage assessmentPackage = assessmentManagementService.getCurrentAssessmentPackage(assessment);
        final List<ItemDeliveryOptions> itemDeliveryOptionsList = assessmentManagementService.getCallerItemDeliveryOptions();

        model.addAttribute(assessment);
        model.addAttribute(assessmentPackage);
        model.addAttribute(itemDeliveryOptionsList);
        model.addAttribute("assessmentRouting", buildAssessmentRouting(aid));
        return "showAssessment";
    }

    @RequestMapping(value="/assessment/{aid}/edit", method=RequestMethod.GET)
    public String showEditAssessmentForm(final Model model, @PathVariable final long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);

        final UpdateAssessmentCommand command = new UpdateAssessmentCommand();
        command.setName(assessment.getName());
        command.setTitle(assessment.getTitle());
        command.setPublic(assessment.isPublic());
        model.addAttribute(command);

        return "editAssessmentForm";
    }

    @RequestMapping(value="/assessment/{aid}/edit", method=RequestMethod.POST)
    public String handleEditAssessmentForm(@PathVariable final long aid,
            final @Valid @ModelAttribute UpdateAssessmentCommand command, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException {
        /* Validate command Object */
        if (result.hasErrors()) {
            return "editAssessmentForm";
        }
        try {
            assessmentManagementService.updateAssessment(aid, command);
        }
        catch (final BindException e) {
            throw new QtiWorksLogicException("Top layer validation is currently same as service layer in this case, so this Exception should not happen");
        }
        return buildActionRedirect("/assessment/" + aid);
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
        return buildActionRedirect("/assessment/" + assessment.getId());
    }

    //------------------------------------------------------

    @RequestMapping(value="/assessment/{aid}/upload", method=RequestMethod.GET)
    public String showUpdateAssessmentPackageForm(@SuppressWarnings("unused") final @PathVariable long aid,
            final Model model) {
        model.addAttribute(new UploadAssessmentPackageCommand());
        return "updateAssessmentPackageForm";
    }

    /**
     * TODO: I'm doing upload + validation together. It would make sense later to split
     * these into 2 steps and find some way of showing progress.
     */
    @RequestMapping(value="/assessment/{aid}/upload", method=RequestMethod.POST)
    public String handleUploadAssessmentPackageForm(final @PathVariable long aid,
            final @Valid @ModelAttribute UploadAssessmentPackageCommand command, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException {
        /* Make sure something was submitted */
        /* Validate command Object */
        if (result.hasErrors()) {
            return "updateAssessmentPackageForm";
        }

        /* Attempt to import the package */
        final MultipartFile uploadFile = command.getFile();
        try {
            assessmentManagementService.updateAssessmentPackageFiles(aid, uploadFile);
        }
        catch (final AssessmentPackageFileImportException e) {
            final EnumerableClientFailure<APFIFailureReason> failure = e.getFailure();
            failure.registerErrors(result, "uploadAssessmentPackageCommand");
            return "updateAssessmentPackageForm";
        }
        catch (final AssessmentStateException e) {
            final EnumerableClientFailure<APSFailureReason> failure = e.getFailure();
            failure.registerErrors(result, "uploadAssessmentPackageCommand");
            return "updateAssessmentPackageForm";
        }
        try {
            assessmentManagementService.validateAssessment(aid);
        }
        catch (final DomainEntityNotFoundException e) {
            /* This could only happen if there's some kind of race condition */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        return buildActionRedirect("/assessment/{aid}");
    }

    //------------------------------------------------------

    /** TODO: For performance, we should cache the validation result */
    @RequestMapping(value="/assessment/{aid}/validate", method=RequestMethod.GET)
    public String validateAssessment(final @PathVariable long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final AssessmentObjectValidationResult<?> validationResult = assessmentManagementService.validateAssessment(aid);
        model.addAttribute("assessmentId", aid);
        model.addAttribute("validationResult", validationResult);
        return "validationResult";
    }

    //------------------------------------------------------

    @RequestMapping(value="/assessment/{aid}/try", method=RequestMethod.POST)
    public String tryLatestAssessmentPackage(final @PathVariable long aid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);
        final ItemDelivery demoDelivery = assessmentManagementService.createDemoDelivery(assessment);
        final CandidateItemSession candidateSession = assessmentCandidateService.createCandidateSession(demoDelivery.getId().longValue());
        return "redirect:/web/instructor/session/" + candidateSession.getId().longValue();
    }

    @RequestMapping(value="/assessment/{aid}/try/{doid}", method=RequestMethod.POST)
    public String tryLatestAssessmentPackage(final @PathVariable long aid, final @PathVariable long doid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);
        final ItemDeliveryOptions itemDeliveryOptions = assessmentManagementService.lookupItemDeliveryOptions(doid);
        final ItemDelivery demoDelivery = assessmentManagementService.createDemoDelivery(assessment, itemDeliveryOptions);
        final CandidateItemSession candidateSession = assessmentCandidateService.createCandidateSession(demoDelivery.getId().longValue());
        return "redirect:/web/instructor/session/" + candidateSession.getId().longValue();
    }

    //------------------------------------------------------
    // Management of ItemDeliveryOptions

    @RequestMapping(value="/deliveryoptions", method=RequestMethod.GET)
    public String listItemDeliveryOptions(final Model model) {
        final List<ItemDeliveryOptions> itemDeliveryOptionsList = assessmentManagementService.getCallerItemDeliveryOptions();
        model.addAttribute(itemDeliveryOptionsList);
        model.addAttribute("itemDeliveryOptionsRouting", buildDeliveryOptionsListRouting(itemDeliveryOptionsList));
        return "listDeliveryOptions";
    }

    @RequestMapping(value="/deliveryoptions/{doid}", method=RequestMethod.GET)
    public String showEditItemDeliveryOptionsForm(final Model model, @PathVariable final long doid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final ItemDeliveryOptions itemDeliveryOptions = assessmentManagementService.lookupItemDeliveryOptions(doid);

        model.addAttribute(itemDeliveryOptions);
        return "editItemDeliveryOptionsForm";
    }

    @RequestMapping(value="/deliveryoptions/{doid}", method=RequestMethod.POST)
    public String handleEditItemDeliveryOptionsForm(@PathVariable final long doid,
            final @Valid @ModelAttribute ItemDeliveryOptions command, final BindingResult result)
            throws PrivilegeException, DomainEntityNotFoundException {
        /* Validate command Object */
        System.out.println(result);
        if (result.hasErrors()) {
            return "editItemDeliveryOptionsForm";
        }

        /* Perform update */
        assessmentManagementService.updateItemDeliveryOptions(doid, command);

        /* Return to show/edit
         * FIXME: Add some flash message here so that it's not confusing.
         */
        return buildActionRedirect("/deliveryoptions/" + doid);
    }

    @RequestMapping(value="/deliveryoptions/create", method=RequestMethod.GET)
    public String showCreateItemDeliveryOptionsForm(final Model model) {
        final long existingOptionCount = assessmentManagementService.countCallerItemDeliveryOptions();
        final ItemDeliveryOptions template = assessmentManagementService.createItemDeliveryOptionsTemplate();
        template.setTitle("Item Delivery Configuration #" + (existingOptionCount+1));

        model.addAttribute(template);
        return "createItemDeliveryOptionsForm";
    }

    @RequestMapping(value="/deliveryoptions/create", method=RequestMethod.POST)
    public String handleCreateItemDeliveryOptionsForm(final @Valid @ModelAttribute ItemDeliveryOptions command, final BindingResult result) {
        /* Validate command Object */
        System.out.println(result);
        if (result.hasErrors()) {
            return "createItemDeliveryOptionsForm";
        }

        /* Try to create new entity */
        assessmentManagementService.createItemDeliveryOptions(command);

        /* TODO: Redirect to options page */
        return buildActionRedirect("/deliveryoptions");
    }

    public Map<Long, Map<String, String>> buildDeliveryOptionsListRouting(final List<ItemDeliveryOptions> itemDeliveryOptionsList) {
        final Map<Long, Map<String, String>> result = new HashMap<Long, Map<String, String>>();
        for (final ItemDeliveryOptions itemDeliveryOptions : itemDeliveryOptionsList) {
            result.put(itemDeliveryOptions.getId(), buildDeliveryOptionsRouting(itemDeliveryOptions));
        }
        return result;
    }

    public Map<String, String> buildDeliveryOptionsRouting(final ItemDeliveryOptions itemDeliveryOptions) {
        return buildDeliveryOptionsRouting(itemDeliveryOptions.getId().longValue());
    }

    public Map<String, String> buildDeliveryOptionsRouting(final long doid) {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("show", buildActionPath("/deliveryoptions/" + doid));
        result.put("update", buildActionPath("/deliveryoptions/" + doid + "/update"));
        return result;
    }
}
