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

import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.services.IdentityService;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentAndPackage;
import uk.ac.ed.ph.qtiworks.services.domain.CandidateSessionSummaryData;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * Router for the LTI Instructor MVC
 *
 * @author David McKain
 */
@Service
public class LtiInstructorRouter {

    @Resource
    private IdentityService identityService;

    @Resource
    private String contextPath;

    public String buildWithinContextUrl(final String actionUrl) {
        final LtiResource currentLtiResource = identityService.ensureCurrentThreadLtiAuthenticationTicket().getLtiResource();
        return "/lti/resource/" + currentLtiResource.getId() + actionUrl;
    }

    public String buildWebUrl(final String actionUrl) {
        return contextPath + buildWithinContextUrl(actionUrl);
    }

    public String buildInstructorRedirect(final String actionUrl) {
        return "redirect:" + buildWithinContextUrl(actionUrl);
    }

    public Map<String, String> buildPrimaryRouting() {
        final LtiResource currentLtiResource = identityService.ensureCurrentThreadLtiAuthenticationTicket().getLtiResource();
        final Long lrid = currentLtiResource.getId();

        final Map<String, String> primaryRouting = new HashMap<String, String>();
        primaryRouting.put("resourceDashboard", buildWebUrl(""));
        primaryRouting.put("exit", buildWebUrl("/exit"));
        primaryRouting.put("debug", buildWebUrl("/debug"));
        primaryRouting.put("try", buildWebUrl("/try"));
        primaryRouting.put("toggleAvailability", buildWebUrl("/toggle-availability"));
        primaryRouting.put("uploadAssessment", buildWebUrl("/assessments/upload"));
        primaryRouting.put("uploadAndUseAssessment", buildWebUrl("/assessments/upload-and-use"));
        primaryRouting.put("listAssessments", buildWebUrl("/assessments"));
        primaryRouting.put("deliverySettingsManager", buildWebUrl("/deliverysettings"));
        primaryRouting.put("listItemDeliverySettings", buildWebUrl("/deliverysettings/item"));
        primaryRouting.put("listTestDeliverySettings", buildWebUrl("/deliverysettings/test"));
        primaryRouting.put("createItemDeliverySettings", buildWebUrl("/deliverysettings/item/create"));
        primaryRouting.put("createTestDeliverySettings", buildWebUrl("/deliverysettings/test/create"));
        primaryRouting.put("listCandidateSessions", buildWebUrl("/candidate-sessions"));
        primaryRouting.put("candidateSummaryReportCsv", buildWebUrl("/candidate-summary-report-" + lrid + ".csv"));
        primaryRouting.put("candidateResultsZip", buildWebUrl("/candidate-results-" + lrid + ".zip"));
        primaryRouting.put("terminateAllSessions", buildWebUrl("/terminate-all-sessions"));
        primaryRouting.put("deleteAllSessions", buildWebUrl("/delete-all-sessions"));
        return primaryRouting;
    }

    public Map<Long, Map<String, String>> buildAssessmentListRouting(final List<AssessmentAndPackage> assessmentAndPackageList) {
        final Map<Long, Map<String, String>> result = new HashMap<Long, Map<String, String>>();
        for (final AssessmentAndPackage assessmentAndPackage : assessmentAndPackageList) {
            final Assessment assessment = assessmentAndPackage.getAssessment();
            result.put(assessment.getId(), buildAssessmentRouting(assessment));
        }
        return result;
    }

    public Map<String, String> buildAssessmentRouting(final Assessment assessment) {
        if (assessment==null) {
            return new HashMap<String, String>();
        }
        return buildAssessmentRouting(assessment.getId().longValue());
    }

    public Map<String, String> buildAssessmentRouting(final long aid) {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("show", buildWebUrl("/assessment/" + aid));
        result.put("select", buildWebUrl("/assessment/" + aid + "/select"));
        result.put("replace", buildWebUrl("/assessment/" + aid + "/replace"));
        result.put("validate", buildWebUrl("/assessment/" + aid + "/validate"));
        result.put("try", buildWebUrl("/assessment/" + aid + "/try"));
        result.put("outcomesSettings", buildWebUrl("/assessment/" + aid + "/outcomes-settings"));
        result.put("delete", buildWebUrl("/assessment/" + aid + "/delete"));
        return result;
    }

    public Map<Long, Map<String, String>> buildDeliverySettingsListRouting(final List<DeliverySettings> deliverySettingsList) {
        final Map<Long, Map<String, String>> result = new HashMap<Long, Map<String, String>>();
        for (final DeliverySettings deliverySettings : deliverySettingsList) {
            result.put(deliverySettings.getId(), buildDeliverySettingsRouting(deliverySettings));
        }
        return result;
    }

    public Map<String, String> buildDeliverySettingsRouting(final DeliverySettings deliverySettings) {
        final Map<String, String> result = new HashMap<String, String>();
        if (deliverySettings==null) {
            return result;
        }
        final long dsid = deliverySettings.getId().longValue();
        final String itemOrTestString = deliverySettings.getAssessmentType()==AssessmentObjectType.ASSESSMENT_ITEM ? "item" : "test";
        result.put("showOrEdit", buildWebUrl("/deliverysettings/" + itemOrTestString + "/" + dsid));
        result.put("delete", buildWebUrl("/deliverysettings/" + dsid + "/delete"));
        result.put("select", buildWebUrl("/deliverysettings/" + dsid + "/select"));
        return result;
    }

    public Map<Long, Map<String, String>> buildCandidateSessionListRouting(final DeliveryCandidateSummaryReport report) {
        final Map<Long, Map<String, String>> result = new HashMap<Long, Map<String, String>>();
        for (final CandidateSessionSummaryData row : report.getRows()) {
            result.put(row.getSessionId(), buildCandidateSessionRouting(row));
        }
        return result;
    }

    public Map<String, String> buildCandidateSessionRouting(final CandidateSessionSummaryData row) {
        return buildCandidateSessionRouting(row.getSessionId());
    }

    public Map<String, String> buildCandidateSessionRouting(final long xid) {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("show", buildWebUrl("/candidate-session/" + xid));
        result.put("events", buildWebUrl("/candidate-session/" + xid + "/events"));
        result.put("result", buildWebUrl("/candidate-session/" + xid + "/result"));
        result.put("terminate", buildWebUrl("/candidate-session/" + xid + "/terminate"));
        result.put("delete", buildWebUrl("/candidate-session/" + xid + "/delete"));
        return result;
    }
}
