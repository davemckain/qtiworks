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
package uk.ac.ed.ph.jqtiplus.node.result;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

import java.util.HashMap;
import java.util.Map;

/**
 * The session status is used to keep track of the status of the item variables in an item session.
 * <p>
 * Possible values: initial, pendingSubmission, pendingResponseProcessing, final
 *
 * @author Jiri Kajaba
 */
public enum SessionStatus implements Stringifiable {
    /**
     * "The value to use for sessions in the initial state, as described above. This value can only be used to describe
     * sessions for which the response variable numAttempts is 0. The values of the variables are set according to the
     * rules defined in the appropriate declarations (see responseDeclaration, outcomeDeclaration and templateDeclaration)."
     */
    INITIAL("initial"),

    /**
     * "The value to use when the item variables represent a snapshot of the current values during an attempt (in other
     * words, while interacting or suspended). The values of the response variables represent work in progress that has
     * not yet been submitted for response processing by the candidate. The values of the outcome variables represent
     * the values assigned during response processing at the end of the previous attempt or, in the case of the first
     * attempt, the default values given in the variable declarations."
     */
    PENDING_SUBMISSION("pendingSubmission"),

    /**
     * "The value to use when the item variables represent the values of the response variables after submission but before
     * response processing has taken place. Again, the outcomes are those assigned at the end of the previous attempt as
     * they are awaiting response processing."
     */
    PENDING_RESPONSE_PROCESSING("pendingResponseProcessing"),

    /**
     * "The value to use when the item variables represent the values at the end of an attempt after response processing
     * has taken place. In other words, after the outcome values have been updated to reflect the values of the response variables."
     */
    FINAL("final");

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "sessionStatus";

    private static Map<String, SessionStatus> sessionStatuses;

    static {
        sessionStatuses = new HashMap<String, SessionStatus>();

        for (final SessionStatus sessionStatus : SessionStatus.values()) {
            sessionStatuses.put(sessionStatus.sessionStatus, sessionStatus);
        }
    }

    private String sessionStatus;

    private SessionStatus(final String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    @Override
    public String toQtiString() {
        return sessionStatus;
    }

    /**
     * Returns parsed <code>SessionStatus</code> from given <code>String</code>.
     *
     * @param sessionStatus <code>String</code> representation of <code>SessionStatus</code>
     * @return parsed <code>SessionStatus</code> from given <code>String</code>
     * @throws QtiParseException if given <code>String</code> is not valid <code>SessionStatus</code>
     */
    public static SessionStatus parseSessionStatus(final String sessionStatus) {
        final SessionStatus result = sessionStatuses.get(sessionStatus);

        if (result == null) {
            throw new QtiParseException("Invalid " + QTI_CLASS_NAME + " '" + sessionStatus + "'.");
        }

        return result;
    }
}
