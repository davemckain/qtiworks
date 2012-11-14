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
package uk.ac.ed.ph.qtiworks.services.domain;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public final class DeliveryCandidateSummaryReport implements Serializable {

    private static final long serialVersionUID = -428993945767139061L;

    public static final class DcsrRow implements Serializable {

        private static final long serialVersionUID = 9044689689638050265L;
        private final long sessionId;
        private final Date launchTime;
        private final String firstName;
        private final String lastName;
        private final String emailAddress;
        private final boolean sessionClosed;
        private final List<String> outcomeValues;

        public DcsrRow(final long sessionId, final Date launchTime, final String firstName,
                final String lastName, final String emailAddress, final boolean sessionClosed,
                final List<String> outcomeValues) {
            this.launchTime = launchTime;
            this.sessionId = sessionId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.emailAddress = emailAddress;
            this.sessionClosed = sessionClosed;
            this.outcomeValues = outcomeValues;
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

        public List<String> getOutcomeValues() {
            return outcomeValues!=null ? Collections.unmodifiableList(outcomeValues) : null;
        }

        @Override
        public String toString() {
            return ObjectUtilities.beanToString(this);
        }
    }

    private final List<String> outcomeNames;
    private final List<DcsrRow> rows;

    public DeliveryCandidateSummaryReport(final List<String> outcomeNames, final List<DcsrRow> rows) {
        this.outcomeNames = outcomeNames;
        this.rows = rows;
    }

    public List<String> getOutcomeNames() {
        return Collections.unmodifiableList(outcomeNames);
    }

    public List<DcsrRow> getRows() {
        return Collections.unmodifiableList(rows);
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
