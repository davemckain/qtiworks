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
package uk.ac.ed.ph.qtiworks.services.domain;

import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.services.AssessmentReportingService;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Draws together data making up a summary report of the candidate sessions for a particular
 * {@link Delivery}.
 *
 * @see AssessmentReportingService#buildDeliveryCandidateSummaryReport(Delivery)
 *
 * @author David McKain
 */
public final class DeliveryCandidateSummaryReport implements Serializable {

    private static final long serialVersionUID = -428993945767139061L;

    /** List of names of numeric outcome variables (having single cardinality) */
    private final List<String> numericOutcomeNames;

    /** List of names of other outcome variables */
    private final List<String> otherOutcomeNames;

    /** List of rows in this report */
    private final List<DcsrRow> rows;

    public static final class DcsrRow implements Serializable {

        private static final long serialVersionUID = 9044689689638050265L;
        private final long sessionId;
        private final Date launchTime;
        private final String firstName; /* Not null */
        private final String lastName; /* Not null */
        private final String emailAddress; /* May be null */
        private final boolean sessionClosed;
        private final boolean sessionTerminated;

        /** List of all numeric outcome values (and single cardinality) */
        private final List<String> numericOutcomeValues;

        /** List of all other outcome values */
        private final List<String> otherOutcomeValues;

        public DcsrRow(final long sessionId, final Date launchTime, final String firstName,
                final String lastName, final String emailAddress,
                final boolean sessionClosed, final boolean sessionTerminated,
                final List<String> numericOutcomeValues, final List<String> otherOutcomeValues) {
            this.launchTime = launchTime;
            this.sessionId = sessionId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.emailAddress = emailAddress;
            this.sessionClosed = sessionClosed;
            this.sessionTerminated = sessionTerminated;
            this.otherOutcomeValues = otherOutcomeValues;
            this.numericOutcomeValues = numericOutcomeValues;
        }

        public long getSessionId() {
            return sessionId;
        }

        public Date getLaunchTime() {
            return launchTime;
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

        public String getSessionStatus() {
            return sessionClosed ? "Finished" : (sessionTerminated ? "Forcibly Terminated" : "In Progress");
        }

        public List<String> getNumericOutcomeValues() {
            return numericOutcomeValues!=null ? Collections.unmodifiableList(numericOutcomeValues) : null;
        }

        public List<String> getOtherOutcomeValues() {
            return otherOutcomeValues!=null ? Collections.unmodifiableList(otherOutcomeValues) : null;
        }

        @Override
        public String toString() {
            return ObjectUtilities.beanToString(this);
        }
    }

    public DeliveryCandidateSummaryReport(final List<String> numericOutcomeNames,
            final List<String> otherOutcomeNames, final List<DcsrRow> rows) {
        this.numericOutcomeNames = numericOutcomeNames;
        this.otherOutcomeNames = otherOutcomeNames;
        this.rows = rows;
    }

    public List<String> getNumericOutcomeNames() {
        return Collections.unmodifiableList(numericOutcomeNames);
    }

    public List<String> getOtherOutcomeNames() {
        return Collections.unmodifiableList(otherOutcomeNames);
    }

    public List<DcsrRow> getRows() {
        return Collections.unmodifiableList(rows);
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
