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
package uk.ac.ed.ph.qtiworks.web.controller.legacy;

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.entities.AnonymousUser;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.services.AssessmentDataService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateException;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageDataImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageDataImportException.ImportFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.EnumerableClientFailure;
import uk.ac.ed.ph.qtiworks.services.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.web.GlobalRouter;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionLaunchService;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionTicket;
import uk.ac.ed.ph.qtiworks.web.domain.StandaloneRunCommand;

import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This RESTish controller is used by the current (and slightly out of date) version of
 * Uniqurate only.
 * <p>
 * DO NOT USE this legacy controller for any new systems!
 * <p>
 * Note that the current version of UQ was developed before QTIWorks started using cookies for
 * authenticating users to {@link CandidateSession}s. As a result, we now have a 2-step launch
 * process using a hard-to-guess token for gaining access to the {@link Delivery} created by UQ.
 * <p>
 * Also note that the {@link AnonymousUser} who ends up running the {@link CandidateSession}
 * is different from the one created when UQ triggers the initial {@link Delivery} creation.
 * This is because UQ doesn't pass the JSESSIONID cookie password on, so a fresh session is created
 * when the user's browser follows the redirect into the QTIWorks candidate session.
 *
 * @author David McKain
 */
@Controller
public class UniqurateStandaloneRunner {

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private CandidateSessionLaunchService candidateSessionLaunchService;

    @Resource
    private AssessmentDataService assessmentDataService;

    @Resource
    private DeliveryDao deliveryDao;

    //--------------------------------------------------------------------

    @RequestMapping(value="/standalonerunner", method=RequestMethod.POST)
    public String uniqurateUploadAndRun(final Model model,
            @Valid @ModelAttribute final StandaloneRunCommand command,
            final BindingResult errors) {
        /* Catch any binding errors */
        if (errors.hasErrors()) {
            /* FIXME: Showing an upload form is the wrong thing to do. But contract with UQ says this will never happen */
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
            final String deliveryToken = candidateSessionLaunchService.generateWebServiceDeliveryToken(delivery);
            return "redirect:/web/anonymous/standalonerunner/uqlauncher/" + delivery.getId() + "/" + deliveryToken;
        }
        catch (final AssessmentPackageDataImportException e) {
            /* FIXME: Showing an upload form is the wrong thing to do. But contract with UQ says this will never happen */
            final EnumerableClientFailure<ImportFailureReason> failure = e.getFailure();
            failure.registerErrors(errors, "assessmentPackageUpload");
            return "standalonerunner/uploadForm";
        }
        catch (final PrivilegeException e) {
            /* This should not happen if access control logic has been done correctly */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
    }

    /**
     * FIXME: Yes, this really is a non-idempotent GET. Boo!
     */
    @RequestMapping(value="/standalonerunner/uqlauncher/{did}/{token}", method=RequestMethod.GET)
    public String uniqurateLauncher(final HttpSession httpSession, final HttpServletResponse httpResponse,
            @PathVariable final long did, @PathVariable final String token)
            throws IOException {
        try {
            final String sessionExitReturnUrl = "/web/anonymous/standalonerunner/exit";
            final CandidateSessionTicket candidateSessionTicket = candidateSessionLaunchService.launchWebServiceCandidateSession(httpSession, did, token, sessionExitReturnUrl);

            /* Redirect to candidate dispatcher */
            return GlobalRouter.buildSessionStartRedirect(candidateSessionTicket);
        }
        catch (final DomainEntityNotFoundException e) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        catch (final CandidateException e) {
            /* This should not happen if underlying logic has been done correctly */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
    }

    @RequestMapping(value="/standalonerunner/exit", method=RequestMethod.GET)
    @ResponseBody()
    public String showExitPage() {
        return "You may now close this window.";
    }
}