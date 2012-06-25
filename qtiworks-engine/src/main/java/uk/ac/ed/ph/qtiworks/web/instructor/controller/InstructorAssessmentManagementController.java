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

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException.APFIFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStateException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStateException.APSFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.EnumerableClientFailure;
import uk.ac.ed.ph.qtiworks.web.instructor.domain.UploadAssessmentPackageCommand;

import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    //------------------------------------------------------

    /**
     * Lists all Assignments owned by the caller
     */
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
            result.put(assessment.getId(), buildAssessmentRouting(assessment.getId().longValue()));
        }
        return result;
    }

    public Map<String, String> buildAssessmentRouting(final long aid) {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("show", buildActionPath("/assessment/" + aid));
        result.put("upload", buildActionPath("/assessment/" + aid + "/upload"));
        result.put("validate", buildActionPath("/assessment/" + aid + "/validate"));
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
        model.addAttribute("instructorAssessmentRouting", primaryRouting);
    }

    //------------------------------------------------------

    /**
     * Shows the Assessment having the given ID (aid)
     */
    @RequestMapping(value="/assessment/{aid}", method=RequestMethod.GET)
    public String showAssessment(final Model model, final @PathVariable long aid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementService.lookupAssessment(aid);

        /* TODO: This is using a detached entity */
        final AssessmentPackage assessmentPackage = assessmentManagementService.getCurrentAssessmentPackage(assessment);

        model.addAttribute(assessment);
        model.addAttribute(assessmentPackage);
        model.addAttribute("assessmentRouting", buildAssessmentRouting(aid));
        return "showAssessment";
    }

    //------------------------------------------------------

    @RequestMapping(value="/assessments/upload", method=RequestMethod.GET)
    public String showUploadAssessmentForm(final Model model) {
        model.addAttribute(new UploadAssessmentPackageCommand());
        return "uploadAssessmentForm";
    }

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
        return buildActionRedirect("/assessment/" + assessment.getId());
    }

    //------------------------------------------------------

    @RequestMapping(value="/assessment/{aid}/upload", method=RequestMethod.GET)
    public String showUpdateAssessmentPackageForm(@SuppressWarnings("unused") final @PathVariable long aid,
            final Model model) {
        model.addAttribute(new UploadAssessmentPackageCommand());
        return "updateAssessmentPackageForm";
    }

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
        return buildActionRedirect("/assessment/{aid}");
    }

    //------------------------------------------------------

    @RequestMapping(value="/assessment/{aid}/validate", method=RequestMethod.GET)
    public String validateAssessment(final @PathVariable long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final AssessmentObjectValidationResult<?> validationResult = assessmentManagementService.validateAssessment(aid);
        model.addAttribute("assessmentId", aid);
        model.addAttribute("validationResult", validationResult);
        return "validationResult";
    }
}
