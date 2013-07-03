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
package uk.ac.ed.ph.qtiworks.web.controller.instructor;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.services.AssessmentDataService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.AssessmentReportingService;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * Helper for populating the {@link Model} for various entities for the system
 * instructor MVC layers.
 *
 * @author David McKain
 */
@Service
public class InstructorModelHelper {

    @Resource
    private InstructorRouter instructorRouter;

    @Resource
    private AssessmentDataService assessmentDataService;

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private AssessmentReportingService assessmentReportingService;

    //------------------------------------------------------

    public void setupModel(final Model model) {
        model.addAttribute("primaryRouting", instructorRouter.buildPrimaryRouting());
    }

    //------------------------------------------------------

    public Assessment setupModelForAssessment(final long aid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        return setupModelForAssessment(assessmentManagementService.lookupAssessment(aid), model);
    }

    public Assessment setupModelForAssessment(final Assessment assessment, final Model model) {
        setupModel(model);
        model.addAttribute("assessment", assessment);
        model.addAttribute("assessmentPackage", assessmentDataService.ensureSelectedAssessmentPackage(assessment));
        model.addAttribute("assessmentStatusReport", assessmentDataService.getAssessmentStatusReport(assessment));
        model.addAttribute("assessmentRouting", instructorRouter.buildAssessmentRouting(assessment));
        model.addAttribute("deliverySettingsList", assessmentDataService.getCallerUserDeliverySettingsForType(assessment.getAssessmentType()));
        return assessment;
    }

    //------------------------------------------------------

    public Delivery setupModelForDelivery(final long did, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        return setupModelForDelivery(assessmentManagementService.lookupDelivery(did), model);
    }

    public Delivery setupModelForDelivery(final Delivery delivery, final Model model) {
        setupModelForAssessment(delivery.getAssessment(), model);
        model.addAttribute(delivery);
        model.addAttribute("deliveryRouting", instructorRouter.buildDeliveryRouting(delivery));
        return delivery;
    }

    //------------------------------------------------------

    public DeliverySettings setupModelForDeliverySettings(final long dsid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        return setupModelForDeliverySettings(assessmentManagementService.lookupDeliverySettings(dsid), model);
    }

    public DeliverySettings setupModelForDeliverySettings(final DeliverySettings deliverySettings, final Model model) {
        setupModel(model);
        model.addAttribute("deliverySettings", deliverySettings);
        model.addAttribute("deliverySettingsRouting", instructorRouter.buildDeliverySettingsRouting(deliverySettings));
        return deliverySettings;
    }

    //------------------------------------------------------

    public CandidateSession setupModelForCandidateSession(final long xid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        return setupModelForCandidateSession(assessmentReportingService.lookupCandidateSession(xid), model);
    }

    public CandidateSession setupModelForCandidateSession(final CandidateSession candidateSession, final Model model) {
        final Delivery delivery = candidateSession.getDelivery();

        setupModelForDelivery(delivery, model);
        model.addAttribute(candidateSession);
        model.addAttribute("candidateSessionRouting", instructorRouter.buildCandidateSessionRouting(candidateSession.getId()));
        return candidateSession;
    }

}
