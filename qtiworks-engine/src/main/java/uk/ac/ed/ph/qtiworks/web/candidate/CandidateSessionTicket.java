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
package uk.ac.ed.ph.qtiworks.web.candidate;

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.User;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

/**
 * This "ticket" is created and stored in the HTTP session and provides access
 * for a particular {@link User} to a particular {@link CandidateSession}.
 * <p>
 * An instance of this will used to create a {@link CandidateSessionContext}, which
 * gets passed to the candidate service layer.
 * <p>
 * Developer note: Instances of this class will be stored in the
 * session, so should be immutable.

 * @see CandidateSessionAuthenticationFilter
 * @see CandidateSessionContext
 *
 * @author David McKain
 */
public final class CandidateSessionTicket implements Serializable {

    private static final long serialVersionUID = 1412636123357858458L;

    /** XSRF token for accessing this {@link CandidateSession} within the current {@link HttpSession} */
    private final String xsrfToken;

    /** ID of the candidate {@link User} in question */
    private final long userId;

    /** ID of the {@link CandidateSession} in question */
    private final long candidateSessionId;

    /** Indicates whether this is an item or test session */
    private final AssessmentObjectType assessmentObjectType;

    /** Optional return URL to use when the session terminates */
    private final String sessionExitReturnUrl;

    public CandidateSessionTicket(final String xsrfToken, final long userId, final long candidateSessionId,
            final AssessmentObjectType assessmentObjectType, final String sessionExitReturnUrl) {
        this.xsrfToken = xsrfToken;
        this.userId = userId;
        this.candidateSessionId = candidateSessionId;
        this.assessmentObjectType = assessmentObjectType;
        this.sessionExitReturnUrl = sessionExitReturnUrl;
    }

    public String getXsrfToken() {
        return xsrfToken;
    }

    public long getUserId() {
        return userId;
    }

    public long getCandidateSessionId() {
        return candidateSessionId;
    }

    public AssessmentObjectType getAssessmentObjectType() {
        return assessmentObjectType;
    }

    public String getSessionExitReturnUrl() {
        return sessionExitReturnUrl;
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
