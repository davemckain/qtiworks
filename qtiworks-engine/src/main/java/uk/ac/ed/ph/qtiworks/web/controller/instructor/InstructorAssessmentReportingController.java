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
package uk.ac.ed.ph.qtiworks.web.controller.instructor;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.services.AssessmentReportingService;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport.DcsrRow;

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

import com.csvreader.CsvWriter;

/**
 * Controller providing reporting functions for {@link Assessment}s
 *
 * @author David McKain
 */
@Controller
public class InstructorAssessmentReportingController {

    @Resource
    private AssessmentReportingService assessmentReportingService;

    @Resource
    private InstructorAssessmentManagementController instructorAssessmentManagementController;

    //------------------------------------------------------

    @ModelAttribute
    public void setupPrimaryRouting(final Model model) {
        instructorAssessmentManagementController.setupPrimaryRouting(model);
    }

    @RequestMapping(value="/delivery/{did}/candidate-summary-report", method=RequestMethod.GET)
    public String downloadDeliveryCandidateSummaryReportCsv(@PathVariable final long did, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final DeliveryCandidateSummaryReport report = assessmentReportingService.buildDeliveryCandidateSummaryReport(did);
        instructorAssessmentManagementController.setupModelForDelivery(did, model);
        model.addAttribute(report);
        return "deliveryCandidateSummaryReport";
    }

    @RequestMapping(value="/delivery/candidate-summary-report-{did}.csv", method=RequestMethod.GET)
    public void downloadDeliveryCandidateSummaryReportCsv(final HttpServletResponse response, @PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        final DeliveryCandidateSummaryReport report = assessmentReportingService.buildDeliveryCandidateSummaryReport(did);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        final CsvWriter csvWriter = new CsvWriter(response.getOutputStream(), ',', Charsets.UTF_8);

        /* Write header */
        final StringBuilder headerBuilder = new StringBuilder("Session ID,Email Address,First Name,Last Name,Email Address,Launch Time,Session Status");
        for (final String outcomeName : report.getOutcomeNames()) {
            headerBuilder.append(',').append(outcomeName);
        }
        csvWriter.writeComment(headerBuilder.toString());

        /* Write each row */
        for (final DcsrRow row : report.getRows()) {
            csvWriter.write(Long.toString(row.getSessionId()));
            csvWriter.write(row.getEmailAddress());
            csvWriter.write(row.getFirstName());
            csvWriter.write(row.getLastName());
            csvWriter.write(row.getLaunchTime().toString());
            csvWriter.write(row.isSessionClosed() ? "Finished" : "In Progress");
            final List<String> outcomeValues = row.getOutcomeValues();
            if (outcomeValues!=null) {
                /* Outcomes have been recorded, so show them */
                for (final String outcomeValue : outcomeValues) {
                    csvWriter.write(outcomeValue, true);
                }
            }
            else {
                /* No outcomes recorded for this candidate */
                for (int i=0; i<report.getOutcomeNames().size(); i++) {
                    csvWriter.write("");
                }
            }
            csvWriter.endRecord();
        }
        csvWriter.close();
    }

    @RequestMapping(value="/delivery/candidate-results-{did}.zip", method=RequestMethod.GET)
    public void downloadDeliveryCandidateResults(final HttpServletResponse response, @PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        response.setContentType("application/zip");
        assessmentReportingService.streamAssessmentReports(did, response.getOutputStream());
    }
}
