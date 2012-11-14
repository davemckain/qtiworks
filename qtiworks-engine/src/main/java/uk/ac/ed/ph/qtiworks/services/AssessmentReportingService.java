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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateSessionOutcomeDao;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSessionOutcome;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport.DcsrRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for reporting on assessment deliveries
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class AssessmentReportingService {

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    @Resource
    private CandidateSessionOutcomeDao candidateSessionOutcomeDao;


    public DeliveryCandidateSummaryReport buildDeliveryCandidateSummaryReport(final long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery delivery = assessmentManagementService.lookupDelivery(did);
        return buildDeliveryCandidateSummaryReport(delivery);
    }

    public DeliveryCandidateSummaryReport buildDeliveryCandidateSummaryReport(final Delivery delivery) {
        final List<CandidateSession> candidateSessions = candidateSessionDao.getForDelivery(delivery);
        final List<CandidateSessionOutcome> candidateSessionOutcomes = candidateSessionOutcomeDao.getForDelivery(delivery);

        /* Convert outcomes into an easy form for manipulating */
        final Map<Long, Map<String, String>> outcomesBySessionIdMap = new HashMap<Long, Map<String,String>>();
        final LinkedHashSet<String> outcomeNames = new LinkedHashSet<String>();
        for (final CandidateSessionOutcome candidateSessionOutcome : candidateSessionOutcomes) {
            final CandidateSession candidateSession = candidateSessionOutcome.getCandidateSession();
            final String outcomeName = candidateSessionOutcome.getOutcomeIdentifier();
            final String outcomeValue = candidateSessionOutcome.getStringValue();
            outcomeNames.add(candidateSessionOutcome.getOutcomeIdentifier());
            Map<String, String> outcomesForSession = outcomesBySessionIdMap.get(candidateSession.getId());
            if (outcomesForSession==null) {
                outcomesForSession = new HashMap<String, String>();
                outcomesBySessionIdMap.put(candidateSession.getId(), outcomesForSession);
            }
            outcomesForSession.put(outcomeName, outcomeValue);
        }

        /* Build up an ordered set of all outcomes reported */
        for (final CandidateSessionOutcome candidateSessionOutcome : candidateSessionOutcomes) {
            outcomeNames.add(candidateSessionOutcome.getOutcomeIdentifier());
        }

        /* Now build report for each session */
        final List<DcsrRow> rows = new ArrayList<DcsrRow>();
        for (int i=0; i<candidateSessions.size(); i++) {
            final CandidateSession candidateSession = candidateSessions.get(i);
            final Map<String, String> outcomesForSession = outcomesBySessionIdMap.get(candidateSession.getId());
            List<String> outcomeValues = null;
            if (outcomesForSession!=null) {
                outcomeValues = new ArrayList<String>(outcomeNames.size());
                for (final String outcomeName : outcomeNames) {
                    outcomeValues.add(outcomesForSession.get(outcomeName));
                }
            }
            final User candidate = candidateSession.getCandidate();
            final DcsrRow row = new DcsrRow(candidateSession.getId().longValue(),
                    candidateSession.getCreationTime(),
                    candidate.getFirstName(),
                    candidate.getLastName(),
                    candidate.getEmailAddress(),
                    candidateSession.isClosed(),
                    outcomeValues);
            rows.add(row);
        }

        return new DeliveryCandidateSummaryReport(new ArrayList<String>(outcomeNames), rows);
    }

}
