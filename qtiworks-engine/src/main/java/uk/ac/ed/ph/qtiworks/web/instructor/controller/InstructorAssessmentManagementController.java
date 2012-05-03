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
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementServices;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException.APFIFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStateException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStateException.APSFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.EnumerableClientFailure;
import uk.ac.ed.ph.qtiworks.web.instructor.domain.UploadAssessmentPackageCommand;

import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Closeables;

/**
 * Controller providing management functions for {@link Assessment}s
 *
 * @author David McKain
 */
@Controller
public final class InstructorAssessmentManagementController {

    @Resource
    private AssessmentManagementServices assessmentManagementServices;

    //------------------------------------------------------

    @RequestMapping(value="/myAssessments", method=RequestMethod.GET)
    public String listCallerAssessments(final Model model) {
        final List<Assessment> assessments = assessmentManagementServices.getCallerAssessments();
        model.addAttribute(assessments);
        return "myAssessments";
    }

    @RequestMapping(value="/assessment/{assessmentId}", method=RequestMethod.GET)
    public String showAssessment(final Model model, final @PathVariable Long assessmentId)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Assessment assessment = assessmentManagementServices.getAssessment(assessmentId);
        final AssessmentPackage assessmentPackage = assessmentManagementServices.getCurrentAssessmentPackage(assessment);
        model.addAttribute(assessment);
        model.addAttribute(assessmentPackage);
        return "showAssessment";
    }

    //------------------------------------------------------

    @RequestMapping(value="/uploadAssessment", method=RequestMethod.GET)
    public String showUploadAssessmentForm(final Model model) {
        model.addAttribute(new UploadAssessmentPackageCommand());
        return "uploadAssessmentForm";
    }

    @RequestMapping(value="/uploadAssessment", method=RequestMethod.POST)
    public String handleUploadAssessmentForm(final @ModelAttribute UploadAssessmentPackageCommand command, final BindingResult result)
            throws IOException, PrivilegeException {
        /* Make sure something was submitted */
        final MultipartFile uploadFile = command.getFile();
        if (uploadFile==null || uploadFile.isEmpty()) {
            result.reject("uploadAssessmentPackageCommand.noFile");
            return "uploadAssessmentForm";
        }

        /* Attempt to import the package */
        final InputStream uploadStream = uploadFile.getInputStream();
        final String uploadContentType = uploadFile.getContentType();
        final String uploadName = uploadFile.getOriginalFilename();
        Assessment assessment;
        try {
            assessment = assessmentManagementServices.importAssessment(uploadStream, uploadContentType, uploadName);
        }
        catch (final AssessmentPackageFileImportException e) {
            final EnumerableClientFailure<APFIFailureReason> failure = e.getFailure();
            failure.registerErrors(result, "uploadAssessmentPackageCommand");
            return "uploadAssessmentForm";
        }
        finally {
            Closeables.closeQuietly(uploadStream);
        }
        return "redirect:/web/instructor/assessment/" + assessment.getId();
    }

    //------------------------------------------------------

    @RequestMapping(value="/updateAssessmentPackage/{assessmentId}", method=RequestMethod.GET)
    public String showUpdateAssessmentPackageForm(final @PathVariable Long assessmentId, final Model model) {
        model.addAttribute(new UploadAssessmentPackageCommand());
        return "updateAssessmentPackageForm";
    }

    @RequestMapping(value="/updateAssessmentPackage/{assessmentId}", method=RequestMethod.POST)
    public String handleUploadAssessmentForm(final @PathVariable Long assessmentId,
            final @ModelAttribute UploadAssessmentPackageCommand command, final BindingResult result)
            throws IOException, PrivilegeException, DomainEntityNotFoundException {
        /* Make sure something was submitted */
        final MultipartFile uploadFile = command.getFile();
        if (uploadFile==null || uploadFile.isEmpty()) {
            result.reject("uploadAssessmentPackageCommand.noFile");
            return "updateAssessmentPackageForm";
        }

        /* Attempt to import the package */
        final InputStream uploadStream = uploadFile.getInputStream();
        final String uploadContentType = uploadFile.getContentType();
        try {
            assessmentManagementServices.updateAssessmentPackageFiles(assessmentId, uploadStream, uploadContentType);
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
        finally {
            Closeables.closeQuietly(uploadStream);
        }
        return "redirect:/web/instructor/assessment/{assessmentId}";
    }

    //------------------------------------------------------

    @RequestMapping(value="/validate/{assessmentId}", method=RequestMethod.GET)
    public String validateAssessment(final @PathVariable Long assessmentId, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final AssessmentObjectValidationResult<?> validationResult = assessmentManagementServices.validateAssessment(assessmentId);
        model.addAttribute("assessmentId", assessmentId);
        model.addAttribute("validationResult", validationResult);
        return "validationResult";
    }
}
