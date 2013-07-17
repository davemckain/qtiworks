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
import uk.ac.ed.ph.qtiworks.services.AssessmentProctoringService;
import uk.ac.ed.ph.qtiworks.services.AssessmentReportingService;
import uk.ac.ed.ph.qtiworks.services.domain.CandidateSessionSummaryReport;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport;
import uk.ac.ed.ph.qtiworks.web.GlobalRouter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller providing reporting and proctoring functions for {@link Assessment}s
 *
 * @author David McKain
 */
@Controller
public class InstructorAssessmentReportingController {

    @Resource
    private InstructorRouter instructorRouter;

    @Resource
    private InstructorModelHelper instructorModelHelper;

    @Resource
    private AssessmentProctoringService assessmentProctoringService;

    @Resource
    private AssessmentReportingService assessmentReportingService;

    //------------------------------------------------------

    @ModelAttribute
    public void setupModel(final Model model) {
        instructorModelHelper.setupModel(model);
    }

    //------------------------------------------------------

    @RequestMapping(value="/delivery/{did}/candidate-sessions", method=RequestMethod.GET)
    public String showDeliveryCandidateSummaryReport(@PathVariable final long did, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final DeliveryCandidateSummaryReport report = assessmentReportingService.buildDeliveryCandidateSummaryReport(did);
        instructorModelHelper.setupModelForDelivery(did, model);
        model.addAttribute(report);
        model.addAttribute("candidateSessionListRouting", instructorRouter.buildCandidateSessionListRouting(report));
        return "listCandidateSessions";
    }

    @RequestMapping(value="/delivery/{did}/terminate-all-sessions", method=RequestMethod.POST)
    public String terminateAllCandidateSessions(final RedirectAttributes redirectAttributes, @PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final int terminated = assessmentProctoringService.terminateCandidateSessionsForDelivery(did);
        GlobalRouter.addFlashMessage(redirectAttributes, "Terminated " + terminated + " candidate session" + (terminated!=1 ? "s" : ""));
        return instructorRouter.buildInstructorRedirect("/delivery/" + did + "/candidate-sessions");
    }

    @RequestMapping(value="/delivery/{did}/delete-all-sessions", method=RequestMethod.POST)
    public String deleteAllCandidateSessions(final RedirectAttributes redirectAttributes, @PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final int deletedCount = assessmentProctoringService.deleteCandidateSessionsForDelivery(did);

        GlobalRouter.addFlashMessage(redirectAttributes, "Deleted " + deletedCount + " candidate session" + (deletedCount!=1 ? "s" : ""));
        return instructorRouter.buildInstructorRedirect("/delivery/" + did + "/candidate-sessions");
    }

    @RequestMapping(value="/delivery/candidate-summary-report-{did}.csv", method=RequestMethod.GET)
    public void streamDeliveryCandidateSummaryReportCsv(final HttpServletResponse response, @PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        assessmentReportingService.streamDeliveryCandidateSummaryReportCsv(did, response.getOutputStream());
    }

    @RequestMapping(value="/delivery/candidate-results-{did}.zip", method=RequestMethod.GET)
    public void streamDeliveryCandidateResults(final HttpServletResponse response, @PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        response.setContentType("application/zip");
        assessmentReportingService.streamAssessmentReports(did, response.getOutputStream());
    }

    //------------------------------------------------------

    @RequestMapping(value="/candidate-session/{xid}", method=RequestMethod.GET)
    public String showCandidateSession(@PathVariable final long xid, final Model model)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateSessionSummaryReport candidateSessionSummaryReport = assessmentReportingService.buildCandidateSessionSummaryReport(xid);
        model.addAttribute(candidateSessionSummaryReport);
        instructorModelHelper.setupModelForCandidateSession(xid, model);
        return "showCandidateSession";
    }

    @RequestMapping(value="/candidate-session/{xid}/result", method=RequestMethod.GET)
    public void streamResult(final HttpServletResponse response, @PathVariable final long xid)
            throws DomainEntityNotFoundException, IOException, PrivilegeException {
        response.setContentType("application/xml");
        assessmentReportingService.streamCandidateAssessmentResult(xid, response.getOutputStream());
    }

    @RequestMapping(value="/candidate-session/{xid}/terminate", method=RequestMethod.POST)
    public String terminateCandidateSession(@PathVariable final long xid, final RedirectAttributes redirectAttributes)
            throws PrivilegeException, DomainEntityNotFoundException {
        assessmentProctoringService.terminateCandidateSession(xid);
        GlobalRouter.addFlashMessage(redirectAttributes, "Terminated Candidate Session #" + xid);
        return instructorRouter.buildInstructorRedirect("/candidate-session/" + xid);
    }
}
