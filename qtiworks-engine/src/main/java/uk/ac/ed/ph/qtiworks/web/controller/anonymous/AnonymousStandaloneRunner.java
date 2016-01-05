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
package uk.ac.ed.ph.qtiworks.web.controller.anonymous;

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.services.AssessmentDataService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageDataImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageDataImportException.ImportFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.EnumerableClientFailure;
import uk.ac.ed.ph.qtiworks.services.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.web.GlobalRouter;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionLaunchService;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionTicket;
import uk.ac.ed.ph.qtiworks.web.domain.StandaloneRunCommand;

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller allowing the public to upload and run an {@link AssessmentItem}
 * or {@link AssessmentTest}.
 * <p>
 * This provides a subset of functionality provided for instructor users, but
 * might be useful.
 *
 * @author David McKain
 */
@Controller
public class AnonymousStandaloneRunner {

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private CandidateSessionLaunchService candidateSessionLaunchService;

    @Resource
    private AssessmentDataService assessmentDataService;

    @Resource
    private AnonymousRouter anonymousRouter;

    //--------------------------------------------------------------------

    @RequestMapping(value="/standalonerunner", method=RequestMethod.GET)
    public String showUploadAndRunForm(final Model model) {
        final StandaloneRunCommand command = new StandaloneRunCommand();

        model.addAttribute(command);
        return "standalonerunner/uploadForm";
    }

    @RequestMapping(value="/standalonerunner", method=RequestMethod.POST)
    public String handleUploadAndRunForm(final HttpSession httpSession, final Model model,
            @Valid @ModelAttribute final StandaloneRunCommand command,
            final BindingResult errors) {
        /* Catch any binding errors */
        if (errors.hasErrors()) {
            return "standalonerunner/uploadForm";
        }
        try {
            final Assessment assessment = assessmentManagementService.importAssessment(command.getFile(), false);
            final AssessmentObjectValidationResult<?> validationResult = assessmentDataService.validateAssessment(assessment);
            final AssessmentPackage assessmentPackage = assessmentDataService.ensureSelectedAssessmentPackage(assessment);
            if (!assessmentPackage.isLaunchable()) {
                /* Assessment isn't launchable */
                model.addAttribute("validationResult", validationResult);
                return "standalonerunner/invalidUpload";
            }
            final Delivery delivery = assessmentManagementService.createDemoDelivery(assessment);
            final String sessionExitReturnUrl = anonymousRouter.buildWithinContextUrl("/standalonerunner");
            final CandidateSessionTicket candidateSessionTicket = candidateSessionLaunchService.launchAnonymousCandidateSession(httpSession, delivery, sessionExitReturnUrl);
            return GlobalRouter.buildSessionStartRedirect(candidateSessionTicket);
        }
        catch (final AssessmentPackageDataImportException e) {
            final EnumerableClientFailure<ImportFailureReason> failure = e.getFailure();
            failure.registerErrors(errors, "assessmentPackageUpload");
            return "standalonerunner/uploadForm";
        }
        catch (final PrivilegeException e) {
            /* This should not happen if access control logic has been done correctly */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        catch (final CandidateException e) {
            /* This also should not happen if logic has been implemented correctly */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
    }
}