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

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.services.AssessmentProctoringService;
import uk.ac.ed.ph.qtiworks.services.AssessmentReportingService;
import uk.ac.ed.ph.qtiworks.services.base.IdentityService;
import uk.ac.ed.ph.qtiworks.services.domain.CandidateSessionSummaryData;
import uk.ac.ed.ph.qtiworks.services.domain.CandidateSessionSummaryMetadata;
import uk.ac.ed.ph.qtiworks.services.domain.CandidateSessionSummaryReport;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport;
import uk.ac.ed.ph.qtiworks.web.GlobalRouter;

import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.Charsets;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.csvreader.CsvWriter;

/**
 * Controller providing reporting and proctoring functions for {@link Assessment}s
 *
 * @author David McKain
 */
@Controller
@RequestMapping("/resource/{lrid}")
public class LtiInstructorAssessmentReportingController {

    @Resource
    private LtiInstructorRouter ltiInstructorRouter;

    @Resource
    private LtiInstructorModelHelper ltiInstructorModelHelper;

    @Resource
    private AssessmentProctoringService assessmentProctoringService;

    @Resource
    private AssessmentReportingService assessmentReportingService;

    @Resource
    private IdentityService identityService;

    //------------------------------------------------------

    @ModelAttribute
    public void setupModel(final Model model) {
        ltiInstructorModelHelper.setupModel(model);
    }

    //------------------------------------------------------

    @RequestMapping(value="/candidate-sessions", method=RequestMethod.GET)
    public String showCandidateSummaryReport(final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery thisDelivery = identityService.getCurrentThreadLtiResource().getDelivery();
        final DeliveryCandidateSummaryReport report = assessmentReportingService.buildDeliveryCandidateSummaryReport(thisDelivery.getId());

        model.addAttribute(report);
        model.addAttribute("candidateSessionListRouting", ltiInstructorRouter.buildCandidateSessionListRouting(report));
        return "instructor/candidateSummaryReport";
    }

    @RequestMapping(value="/terminate-all-sessions", method=RequestMethod.POST)
    public String terminateAllCandidateSessions(final RedirectAttributes redirectAttributes)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery thisDelivery = identityService.getCurrentThreadLtiResource().getDelivery();
        final int terminatedCount = assessmentProctoringService.terminateCandidateSessionsForDelivery(thisDelivery.getId());

        GlobalRouter.addFlashMessage(redirectAttributes, "Terminated " + terminatedCount + " candidate session" + (terminatedCount!=1 ? "s" : ""));
        return ltiInstructorRouter.buildInstructorRedirect("/candidate-sessions");
    }

    @RequestMapping(value="/candidate-summary-report-{lrid}.csv", method=RequestMethod.GET)
    public void downloadDeliveryCandidateSummaryReportCsv(final HttpServletResponse response)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        final Delivery thisDelivery = identityService.getCurrentThreadLtiResource().getDelivery();
        final DeliveryCandidateSummaryReport report = assessmentReportingService.buildDeliveryCandidateSummaryReport(thisDelivery.getId());

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        final CsvWriter csvWriter = new CsvWriter(response.getOutputStream(), ',', Charsets.UTF_8);

        /* Write header */
        final StringBuilder headerBuilder = new StringBuilder("Session ID,Email Address,First Name,Last Name,Launch Time,Session Status");
        final CandidateSessionSummaryMetadata metadata = report.getCandidateSessionSummaryMetadata();
        for (final String outcomeName : metadata.getNumericOutcomeIdentifiers()) {
            headerBuilder.append(',').append(outcomeName);
        }
        for (final String outcomeName : metadata.getOtherOutcomeIdentifiers()) {
            headerBuilder.append(',').append(outcomeName);
        }
        csvWriter.writeComment(headerBuilder.toString());

        /* Write each row */
        for (final CandidateSessionSummaryData row : report.getRows()) {
            csvWriter.write(Long.toString(row.getSessionId()));
            csvWriter.write(StringUtilities.emptyIfNull(row.getEmailAddress()));
            csvWriter.write(row.getFirstName());
            csvWriter.write(row.getLastName());
            csvWriter.write(row.getLaunchTime().toString());
            csvWriter.write(row.getSessionStatus());
            writeOutcomes(csvWriter, metadata.getNumericOutcomeIdentifiers(), row.getNumericOutcomeValues());
            writeOutcomes(csvWriter, metadata.getOtherOutcomeIdentifiers(), row.getOtherOutcomeValues());
            csvWriter.endRecord();
        }
        csvWriter.close();
    }

    private void writeOutcomes(final CsvWriter csvWriter, final List<String> outcomeNames, final List<String> outcomeValues)
            throws IOException {
        if (outcomeValues!=null) {
            /* Outcomes have been recorded, so output them */
            for (final String outcomeValue : outcomeValues) {
                csvWriter.write(outcomeValue, true);
            }
        }
        else {
            /* No outcomes recorded for this candidate */
            for (int i=0; i<outcomeNames.size(); i++) {
                csvWriter.write("");
            }
        }
    }

    @RequestMapping(value="/candidate-results-{lrid}.zip", method=RequestMethod.GET)
    public void downloadDeliveryCandidateResults(final HttpServletResponse response)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        final Delivery thisDelivery = identityService.getCurrentThreadLtiResource().getDelivery();
        response.setContentType("application/zip");
        assessmentReportingService.streamAssessmentReports(thisDelivery.getId(), response.getOutputStream());
    }

    //------------------------------------------------------

    @RequestMapping(value="/candidate-session/{xid}", method=RequestMethod.GET)
    public String showCandidateSession(@PathVariable final long xid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateSessionSummaryReport candidateSessionSummaryReport = assessmentReportingService.buildCandidateSessionSummaryReport(xid);
        model.addAttribute(candidateSessionSummaryReport);
        ltiInstructorModelHelper.setupModelForCandidateSession(xid, model);
        return "instructor/showCandidateSession";
    }

    @RequestMapping(value="/candidate-session/{xid}/terminate", method=RequestMethod.POST)
    public String terminateCandidateSession(@PathVariable final long xid, final RedirectAttributes redirectAttributes)
            throws PrivilegeException, DomainEntityNotFoundException {
        assessmentProctoringService.terminateCandidateSession(xid);
        GlobalRouter.addFlashMessage(redirectAttributes, "Terminated Candidate Session #" + xid);
        return ltiInstructorRouter.buildInstructorRedirect("/candidate-session/" + xid);
    }
}
