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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateOutcomeReportingStatus;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.services.base.AuditLogger;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.node.result.ItemVariable;
import uk.ac.ed.ph.jqtiplus.node.result.OutcomeVariable;
import uk.ac.ed.ph.jqtiplus.node.result.TestResult;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles the closure and final reporting of {@link CandidateSession}s,
 * possibly invoking the return of LTI outcomes.
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class CandidateSessionCloser {

    @Resource
    private AuditLogger auditLogger;

    @Resource
    private CandidateDataService candidateDataService;

    @Resource
    private LtiOutcomeService ltiOutcomeService;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    //-------------------------------------------------

    public void closeCandidateItemSession(final CandidateSession candidateSession, final ItemSessionController itemSessionController) {
        final AssessmentResult assessmentResult = candidateDataService.computeAndRecordItemAssessmentResult(candidateSession, itemSessionController);
        closeCandidateSession(candidateSession, assessmentResult);
    }

    public void closeCandidateTestSession(final CandidateSession candidateSession, final TestSessionController testSessionController) {
        final AssessmentResult assessmentResult = candidateDataService.computeAndRecordTestAssessmentResult(candidateSession, testSessionController);
        closeCandidateSession(candidateSession, assessmentResult);
    }

    private void closeCandidateSession(final CandidateSession candidateSession, final AssessmentResult assessmentResult) {
        candidateSession.setClosed(true);
        candidateSessionDao.update(candidateSession);
        maybeScheduleLtiOutcomes(candidateSession, assessmentResult);
    }

    private void maybeScheduleLtiOutcomes(final CandidateSession candidateSession, final AssessmentResult assessmentResult) {
        /* First check a number of pre-conditions for actually recording LTI outcomes */
        System.out.println("MAYBE: " + candidateSession);
        final Delivery delivery = candidateSession.getDelivery();
        if (delivery.getDeliveryType()!=DeliveryType.LTI_RESOURCE && !delivery.isLtiEnabled()) {
            recordLtiRecordingSkipped(candidateSession, CandidateOutcomeReportingStatus.LTI_DISABLED,
                    "LTI is not enabled for CandidateSession #" + candidateSession.getId());
            return;
        }
        /* Make sure LTI Tool Consumer has decided we can actually send results */
        final Assessment assessment = delivery.getAssessment();
        if (assessment.getLtiResultOutcomeIdentifier()==null) {
            recordLtiRecordingSkipped(candidateSession, CandidateOutcomeReportingStatus.NO_OUTCOME_IDENTIFIER,
                    "No result outcome variable set for Delivery #" + delivery.getId());
            return;
        }
        final String lisOutcomeServiceUrl = candidateSession.getLisOutcomeServiceUrl();
        if (StringUtilities.isNullOrEmpty(lisOutcomeServiceUrl)) {
            recordLtiRecordingSkipped(candidateSession, CandidateOutcomeReportingStatus.LTI_OUTCOMES_DISABLED,
                    "Tool consumer did not provide an lis_outcome_service_url for CandidateSession #"
                    + candidateSession.getId());
            return;
        }
        final String lisResultSourceDid = candidateSession.getLisResultSourcedid();
        if (StringUtilities.isNullOrEmpty(lisResultSourceDid)) {
            recordLtiRecordingSkipped(candidateSession, CandidateOutcomeReportingStatus.USER_NOT_REPORTABLE,
                    "Tool consumer did not specify a lis_resource_sourcedid for CandidateSession #"
                    + candidateSession.getId());
            return;
        }
        /* Make sure specified outcome variable exists in the assessmentResult */
        final OutcomeVariable resultOutcomeVariable = extractResultOutcomeVariable(assessmentResult, assessment);
        if (resultOutcomeVariable==null) {
            recordLtiRecordingSkipped(candidateSession, CandidateOutcomeReportingStatus.BAD_OUTCOME_IDENTIFIER,
                    "Failed to extract outcomeVariable with identifier "
                    + assessment.getLtiResultOutcomeIdentifier()
                    + " from assessmentResult for CandidateSession #"
                    + candidateSession.getId());
            return;
        }
        /* Check that outcome is a single float */
        if (!resultOutcomeVariable.hasSignature(Signature.SINGLE_FLOAT)) {
            recordLtiRecordingSkipped(candidateSession, CandidateOutcomeReportingStatus.SCORE_NOT_SINGLE_FLOAT,
                    "OutcomeVariable with identifier "
                    + assessment.getLtiResultOutcomeIdentifier()
                    + " in assessmentResult for CandidateSession #"
                    + candidateSession.getId()
                    + " is not a single float");
            return;
        }
        /* Make sure result outcomeVariable can be normalized correctly */
        final double rawScore = ((FloatValue) resultOutcomeVariable.getComputedValue()).doubleValue();
        final Double normalizedScore = computeNormalizedScore(resultOutcomeVariable, rawScore, assessment);
        if (normalizedScore==null) {
            recordLtiRecordingSkipped(candidateSession,
                    CandidateOutcomeReportingStatus.NO_NORMALIZATION,
                    "Not enough data specified to normalize score for CandidateSession #"
                    + candidateSession.getId());
            return;
        }
        /* Make sure normalized score is within reported bounds */
        if (normalizedScore<0.0 || normalizedScore>1.0) {
            recordLtiRecordingSkipped(candidateSession,
                    CandidateOutcomeReportingStatus.BAD_NORMALIZED_SCORE,
                    "Not enough data specified to normalize score for CandidateSession #"
                    + candidateSession.getId());
            return;

        }

        /* If we got this far, then queue up the LTI result for reporting */
        ltiOutcomeService.queueLtiResult(candidateSession, normalizedScore);
    }

    private Double computeNormalizedScore(final OutcomeVariable outcomeVariable, final double rawScore, final Assessment assessment) {
        final Double normalMaximum = outcomeVariable.getNormalMaximum();
        Double result = null;
        if (normalMaximum!=null) {
            /* Use values specified within the QTI */
            final Double normalMinimum = outcomeVariable.getNormalMinimum();
            if (normalMinimum!=null) {
                result = (rawScore - normalMinimum) / (normalMaximum - normalMinimum);
            }
            else {
                result = (rawScore + normalMaximum) / (normalMaximum * 2.0);
            }
        }
        else {
            final Double ltiResultMinimum = assessment.getLtiResultMinimum();
            final Double ltiResultMaximum = assessment.getLtiResultMaximum();
            if (ltiResultMaximum!=null && ltiResultMinimum!=null) {
                result = (rawScore - ltiResultMinimum) / (ltiResultMaximum - ltiResultMinimum);
            }
        }
        return result;
    }

    private OutcomeVariable extractResultOutcomeVariable(final AssessmentResult assessmentResult, final Assessment assessment) {
        final AssessmentObjectType assessmentType = assessment.getAssessmentType();
        final Identifier resultOutcomeIdentifier;
        try {
            resultOutcomeIdentifier = Identifier.parseString(assessment.getLtiResultOutcomeIdentifier());
        }
        catch (final QtiParseException e) {
            return null;
        }
        switch (assessmentType) {
            case ASSESSMENT_ITEM:
                final List<ItemResult> itemResults = assessmentResult.getItemResults();
                if (itemResults.size()!=1) {
                    throw new QtiWorksLogicException("Expected exactly 1 itemResult within assessmentResult but got " + itemResults.size());
                }
                final ItemResult itemResult = itemResults.get(0);
                final List<ItemVariable> itemVariables = itemResult.getItemVariables();
                return extractOutcomeVariable(itemVariables, resultOutcomeIdentifier);

            case ASSESSMENT_TEST:
                final TestResult testResult = assessmentResult.getTestResult();
                return extractOutcomeVariable(testResult.getItemVariables(), resultOutcomeIdentifier);

            default:
                throw new QtiWorksLogicException("Unexpected swtich casse: " + assessmentType);
        }
    }

    private OutcomeVariable extractOutcomeVariable(final List<ItemVariable> itemVariables, final Identifier outcomeIdentifier) {
        for (final ItemVariable itemVariable : itemVariables) {
            if (itemVariable.getIdentifier().equals(outcomeIdentifier) && itemVariable.getVariableType()==VariableType.OUTCOME) {
                return (OutcomeVariable) itemVariable;
            }
        }
        return null;
    }

    private void recordLtiRecordingSkipped(final CandidateSession candidateSession, final CandidateOutcomeReportingStatus status, final String message) {
        auditLogger.recordEvent("LTI Outcomes recording: " + message);
        candidateSession.setCandidateOutcomeReportingStatus(status);
        candidateSessionDao.update(candidateSession);
    }
}
