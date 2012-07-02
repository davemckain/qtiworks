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
package uk.ac.ed.ph.qtiworks.web.pub.controller;

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.services.AssessmentCandidateService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException.APFIFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.EnumerableClientFailure;
import uk.ac.ed.ph.qtiworks.web.domain.StandaloneDeliveryCommand;

import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller allowing the public to upload, (validate, ) then run an item. The
 * item and all its data will be deleted soon afterwards.
 * <p>
 * This provides a subset of functionality provided for instructor users, but
 * might be useful.
 *
 * @author David McKain
 */
@Controller
public class PublicStandaloneItemRunner {

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private AssessmentCandidateService assessmentCandidateService;

    @RequestMapping(value="/standalonerunner", method=RequestMethod.GET)
    public String showUploadAndRunForm(final Model model) {
        final StandaloneDeliveryCommand command = new StandaloneDeliveryCommand();
        command.setDsid(1L); /* FIXME: Make this more clever */

        model.addAttribute(command);
        return "public/standalonerunner/uploadForm";
    }

    @RequestMapping(value="/standalonerunner", method=RequestMethod.POST)
    public String handleUploadAndRunForm(final Model model, @Valid @ModelAttribute final StandaloneDeliveryCommand command,
            final BindingResult errors) throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        /* Catch any binding errors */
        if (errors.hasErrors()) {
            return "public/standalonerunner/uploadForm";
        }

        /* Make sure the required ItemDeliverySettings exists */
        final ItemDeliverySettings itemDeliverySettings = assessmentManagementService.lookupItemDeliverySettings(command.getDsid());

        /* Now upload the Assessment and validate it */
        final Assessment assessment;
        try {
            assessment = assessmentManagementService.importAssessment(command.getFile());
            final AssessmentObjectValidationResult<?> validationResult = assessmentManagementService.validateAssessment(assessment.getId().longValue());
            if (assessment.getAssessmentType()!=AssessmentObjectType.ASSESSMENT_ITEM) {
                /* FIXME! We're only supporting items at present */
                errors.reject("testsNotSupportedYet");
                return "public/standalonerunner/uploadForm";
            }
            if (!validationResult.isValid()) {
                model.addAttribute("validationResult", validationResult);
                return "public/standalonerunner/invalidUpload";
            }
        }
        catch (final AssessmentPackageFileImportException e) {
            System.out.println("HERE with " + e);
            final EnumerableClientFailure<APFIFailureReason> failure = e.getFailure();
            failure.registerErrors(errors, "assessmentPackageUpload");
            System.out.println(errors);
            return "public/standalonerunner/uploadForm";
        }
        catch (final DomainEntityNotFoundException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }

        /* If still here, start new delivery and get going */
        final ItemDelivery delivery = assessmentManagementService.createDemoDelivery(assessment, itemDeliverySettings);
        final CandidateItemSession candidateSession = assessmentCandidateService.createCandidateSession(delivery.getId().longValue());

        /* Redirect to rendering */
        return "redirect:/web/public/session/" + candidateSession.getId().longValue();
    }
}
