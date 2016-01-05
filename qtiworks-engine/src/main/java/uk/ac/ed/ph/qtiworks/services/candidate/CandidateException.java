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
package uk.ac.ed.ph.qtiworks.services.candidate;

import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.CandidateAuditLogger;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown by the candidate service API when the caller
 * (representing the candidate) attempts an inappropriate or forbidden action.
 * <p>
 * Always let {@link CandidateAuditLogger} generate and throw these for you.
 *
 * @see CandidateExceptionReason
 * @see CandidateAuditLogger
 *
 * @author David McKain
 */
@ResponseStatus(value=HttpStatus.FORBIDDEN)
public final class CandidateException extends Exception {

    private static final long serialVersionUID = 963799679125087234L;

    private final User candidate;
    private final Assessment assessment;
    private final Delivery delivery;
    private final CandidateSession candidateSession;
    private final CandidateExceptionReason candidateExceptionReason;

    /**
     * Use this for an exception on an already established {@link CandidateSession}.
     */
    public CandidateException(final CandidateSession candidateSession, final CandidateExceptionReason candidateExceptionReason) {
        super("CandidateException on CandidateSession " + candidateSession.getId() + "; reason=" + candidateExceptionReason);
        this.candidate = candidateSession.getCandidate();
        this.candidateSession = candidateSession;
        this.delivery = candidateSession.getDelivery();
        this.assessment = delivery.getAssessment();
        this.candidateExceptionReason = candidateExceptionReason;
    }

    /**
     * Use this for an exception in creating a {@link CandidateSession} on a non-null {@link Delivery}.
     *
     * @see CandidateSessionStarter
     */
    public CandidateException(final User candidate, final Delivery delivery, final CandidateExceptionReason candidateExceptionReason) {
        super("CandidateException launching CandidateSession for User " + candidate.getId() + " on Delivery " + delivery.getId() + "; reason=" + candidateExceptionReason);
        this.candidate = candidate;
        this.candidateSession = null;
        this.delivery = delivery;
        this.assessment = delivery.getAssessment();
        this.candidateExceptionReason = candidateExceptionReason;
    }

    /**
     * Use this for an exception in creating a {@link CandidateSession} on an {@link Assessment}
     * when the {@link Delivery} to be run has not yet been determined.
     *
     * @see CandidateSessionStarter
     */
    public CandidateException(final User candidate, final Assessment assessment, final CandidateExceptionReason candidateExceptionReason) {
        super("CandidateException launching CandidateSession for User " + candidate.getId() + " on Assessment " + assessment.getId() + "; reason=" + candidateExceptionReason);
        this.candidate = candidate;
        this.candidateSession = null;
        this.delivery = null;
        this.assessment = assessment;
        this.candidateExceptionReason = candidateExceptionReason;
    }

    public User getCandidate() {
        return candidate;
    }

    /**
     * NB: This can return null for exceptions during session startup.
     */
    public CandidateSession getCandidateSession() {
        return candidateSession;
    }

    /**
     * NB: This can return null for exceptions during session start on an {@link Assessment} where the
     * required {@link Delivery} has not yet been determined.
     */
    public Delivery getDelivery() {
        return delivery;
    }

    public Assessment getAssessment() {
        return assessment;
    }


    public CandidateExceptionReason getCandidateExceptionReason() {
        return candidateExceptionReason;
    }
}
