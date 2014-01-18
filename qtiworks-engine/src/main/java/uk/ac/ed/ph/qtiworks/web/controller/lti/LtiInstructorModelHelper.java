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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.web.controller.lti;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.services.AssessmentDataService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.AssessmentReportingService;
import uk.ac.ed.ph.qtiworks.services.IdentityService;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentStatusReport;
import uk.ac.ed.ph.qtiworks.services.domain.CandidateEventSummaryData;
import uk.ac.ed.ph.qtiworks.services.domain.CandidateSessionSummaryReport;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryStatusReport;
import uk.ac.ed.ph.qtiworks.services.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.web.lti.LtiAuthenticationTicket;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Helper for populating the {@link Model} for various entities for the LTI
 * instructor MVC layers.
 *
 * @author David McKain
 */
@Service
public class LtiInstructorModelHelper {

    @Resource
    private LtiInstructorRouter ltiInstructorRouter;

    @Resource
    private IdentityService identityService;

    @Resource
    private AssessmentDataService assessmentDataService;

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private AssessmentReportingService assessmentReportingService;

    //------------------------------------------------------

    @ModelAttribute
    public void setupModel(final Model model) {
        final LtiAuthenticationTicket ltiAuthenticationTicket = identityService.assertCurrentThreadLtiAuthenticationTicket();
        final LtiResource ltiResource = ltiAuthenticationTicket.getLtiResource();
        final Delivery thisDelivery = ltiResource.getDelivery();
        final Assessment thisAssessment = thisDelivery.getAssessment();
        final DeliverySettings theseDeliverySettings = thisDelivery.getDeliverySettings();
        final DeliveryStatusReport thisDeliveryStatusReport = assessmentDataService.getDeliveryStatusReport(thisDelivery);
        final AssessmentStatusReport thisAssessmentStatusReport;
        final AssessmentPackage thisAssessmentPackage;
        if (thisAssessment!=null) {
            thisAssessmentStatusReport = assessmentDataService.getAssessmentStatusReport(thisAssessment);
            thisAssessmentPackage = thisAssessmentStatusReport.getAssessmentPackage();
        }
        else {
            thisAssessmentStatusReport = null;
            thisAssessmentPackage = null;
        }
        model.addAttribute("thisLtiAuthenticationTicket", ltiAuthenticationTicket);
        model.addAttribute("thisLtiUser", identityService.assertCurrentThreadUser());
        model.addAttribute("thisLtiResource", ltiResource);
        model.addAttribute("thisDelivery", thisDelivery);
        model.addAttribute("thisDeliveryStatusReport", thisDeliveryStatusReport);
        model.addAttribute("thisAssessment", thisAssessment);
        model.addAttribute("thisAssessmentStatusReport", thisAssessmentStatusReport);
        model.addAttribute("thisAssessmentPackage", thisAssessmentPackage);
        model.addAttribute("theseDeliverySettings", theseDeliverySettings);
        model.addAttribute("primaryRouting", ltiInstructorRouter.buildPrimaryRouting());
        model.addAttribute("thisAssessmentRouting", ltiInstructorRouter.buildAssessmentRouting(thisAssessment));
        model.addAttribute("theseDeliverySettingsRouting", ltiInstructorRouter.buildDeliverySettingsRouting(theseDeliverySettings));
    }

    //------------------------------------------------------

    public Assessment setupModelForAssessment(final long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        return setupModelForAssessment(assessmentManagementService.lookupAssessment(aid), model);
    }

    public Assessment setupModelForAssessment(final Assessment assessment, final Model model) {
        setupModel(model);
        final AssessmentStatusReport assessmentStatusReport = assessmentDataService.getAssessmentStatusReport(assessment);
        model.addAttribute("assessment", assessment);
        model.addAttribute("assessmentStatusReport", assessmentStatusReport);
        model.addAttribute("assessmentPackage", assessmentStatusReport.getAssessmentPackage());
        model.addAttribute("assessmentRouting", ltiInstructorRouter.buildAssessmentRouting(assessment));
        model.addAttribute("deliverySettingsList", assessmentDataService.getCallerUserDeliverySettingsForType(assessment.getAssessmentType()));
        return assessment;
    }

    //------------------------------------------------------

    public DeliverySettings setupModelForDeliverySettings(final long dsid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        return setupModelForDeliverySettings(assessmentManagementService.lookupDeliverySettings(dsid), model);
    }

    public DeliverySettings setupModelForDeliverySettings(final DeliverySettings deliverySettings, final Model model) {
        setupModel(model);
        model.addAttribute("deliverySettings", deliverySettings);
        model.addAttribute("deliverySettingsRouting", ltiInstructorRouter.buildDeliverySettingsRouting(deliverySettings));
        return deliverySettings;
    }

    //------------------------------------------------------

    public CandidateSession setupModelForCandidateSession(final long xid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        return setupModelForCandidateSession(assessmentReportingService.lookupCandidateSession(xid), model);
    }

    public CandidateSession setupModelForCandidateSession(final CandidateSession candidateSession, final Model model) {
        final Delivery delivery = candidateSession.getDelivery();
        final Assessment assessment = delivery.getAssessment();
        setupModelForAssessment(assessment, model);

        final CandidateSessionSummaryReport candidateSessionSummaryReport = assessmentReportingService.buildCandidateSessionSummaryReport(candidateSession);
        final List<CandidateEventSummaryData> candidateEventSummaryDataList = assessmentReportingService.buildCandidateEventSummaryDataList(candidateSession);
        model.addAttribute(candidateSessionSummaryReport);
        model.addAttribute(candidateEventSummaryDataList);
        model.addAttribute(candidateSession);
        model.addAttribute("candidateSessionRouting", ltiInstructorRouter.buildCandidateSessionRouting(candidateSession.getId()));
        return candidateSession;
    }
}
