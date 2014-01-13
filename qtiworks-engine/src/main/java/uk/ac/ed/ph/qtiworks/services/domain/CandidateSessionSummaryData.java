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
package uk.ac.ed.ph.qtiworks.services.domain;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.LisOutcomeReportingStatus;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Contains basic summary data about a single {@link CandidateSession}
 *
 * @author David McKain
 */
public final class CandidateSessionSummaryData implements Serializable {

    private static final long serialVersionUID = 9044689689638050265L;
    private final long sessionId;
    private final Date launchTime;
    private final String firstName; /* Not null */
    private final String lastName; /* Not null */
    private final String emailAddress; /* May be null */
    private final boolean sessionClosed;
    private final boolean sessionTerminated;
    private final boolean sessionExploded;

    /** Current {@link LisOutcomeReportingStatus} if LTI result reporting is supported, null otherwise */
    private final LisOutcomeReportingStatus lisOutcomeReportingStatus;

    /** Value of LIS/LTI result outcome variable (if specified, null if not specified) */
    private final String lisResultOutcomeValue;

    /** Copy of {@link CandidateSession#getLisScore()} */
    private final Double lisScore;

    /** List of all numeric outcome values (having single cardinality) */
    private final ImmutableList<String> numericOutcomeValues;

    /** List of all other outcome values */
    private final ImmutableList<String> otherOutcomeValues;

    public CandidateSessionSummaryData(final long sessionId, final Date launchTime, final String firstName,
            final String lastName, final String emailAddress,
            final boolean sessionClosed, final boolean sessionTerminated, final boolean sessionExploded,
            final LisOutcomeReportingStatus lisOutcomeReportingStatus,
            final String lisResultOutcomeValue, final Double lisScore,
            final List<String> numericOutcomeValues, final List<String> otherOutcomeValues) {
        Assert.notNull(numericOutcomeValues, "numericOutcomeValues");
        Assert.notNull(otherOutcomeValues, "otherOutcomesValues");
        this.sessionId = sessionId;
        this.launchTime = ObjectUtilities.safeClone(launchTime);
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.sessionClosed = sessionClosed;
        this.sessionTerminated = sessionTerminated;
        this.sessionExploded = sessionExploded;
        this.lisOutcomeReportingStatus = lisOutcomeReportingStatus;
        this.lisResultOutcomeValue = lisResultOutcomeValue;
        this.lisScore = lisScore;
        this.otherOutcomeValues = ImmutableList.<String>copyOf(otherOutcomeValues);
        this.numericOutcomeValues = ImmutableList.<String>copyOf(numericOutcomeValues);
    }

    public long getSessionId() {
        return sessionId;
    }

    public Date getLaunchTime() {
        return ObjectUtilities.safeClone(launchTime);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public boolean isSessionClosed() {
        return sessionClosed;
    }

    public boolean isSessionTerminated() {
        return sessionTerminated;
    }

    public boolean isSessionExploded() {
        return sessionExploded;
    }

    public LisOutcomeReportingStatus getLisOutcomeReportingStatus() {
        return lisOutcomeReportingStatus;
    }

    public String getLisResultOutcomeValue() {
        return lisResultOutcomeValue;
    }

    public Double getLisScore() {
        return lisScore;
    }

    public List<String> getNumericOutcomeValues() {
        return numericOutcomeValues!=null ? Collections.unmodifiableList(numericOutcomeValues) : null;
    }

    public List<String> getOtherOutcomeValues() {
        return otherOutcomeValues!=null ? Collections.unmodifiableList(otherOutcomeValues) : null;
    }

    public String getSessionStatusMessage() {
        if (sessionClosed) {
            return "Finished";
        }
        if (sessionExploded) {
            return "Exploded";
        }
        if (sessionTerminated) {
            return "Terminated";
        }
        return "In Progress";
    }

    public String getLisReportingStatusMessage() {
        if (lisOutcomeReportingStatus==null) {
            return "(Assessment not finished)";
        }
        switch (lisOutcomeReportingStatus) {
            case LTI_DISABLED: return "LTI not enabled for this Delivery";
            case LTI_OUTCOMES_DISABLED: return "LTI outcomes not enabled by Tool Consumer";
            case USER_NOT_REPORTABLE: return "LTI outcomes reporting not enabled for this candidate";
            case NO_OUTCOME_IDENTIFIER: return "No LTI result outcome variable specified";
            case BAD_OUTCOME_IDENTIFIER: return "Bad LTI result outcome variable specified";
            case SCORE_NOT_SINGLE_FLOAT: return "LTI result outcome variable is not a single float";
            case NO_NORMALIZATION: return "Range for LTI result outcome variable not specified";
            case BAD_NORMALIZED_SCORE: return "Normalized LTI result ended up outside [0,1] range";
            case TC_RETURN_SCHEDULED: return "LTI result scheduled for return";
            case TC_RETURN_SUCCESS: return "LTI result returned successfully";
            case TC_RETURN_FAIL_ATTEMPT: return "LTI result return attempt failed - will retry shortly";
            case TC_RETURN_FAIL_TERMINAL: return "LTI result return failed";
            default: throw new QtiWorksLogicException("Unexpected switch case " + lisOutcomeReportingStatus);
        }
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}