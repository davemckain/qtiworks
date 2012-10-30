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

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemAttempt;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.rendering.ItemRenderingRequest;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateForbiddenException;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidatePrivilege;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Helper to audit candidate events
 *
 * @author David McKain
 */
@Service
public class CandidateAuditLogger {

    /** Special logger for auditing candidate actions */
    private static final Logger candidateLogger = LoggerFactory.getLogger("CandidateAuditor");

    private void logEvent(final CandidateSession candidateSession, final String message) {
        candidateLogger.info("user={} xid={} did={} aid={} {}",
                new Object[] {
                    candidateSession.getCandidate().getBusinessKey(),
                    candidateSession.getId(),
                    candidateSession.getDelivery().getId(),
                    candidateSession.getDelivery().getAssessment().getId(),
                    message
        });
    }

    public void logRendering(final CandidateItemEvent candidateItemEvent, final ItemRenderingRequest renderingRequest) {
        logEvent(candidateItemEvent.getCandidateSession(), "action=RENDER mode=" + renderingRequest.getRenderingMode());
    }

    public void logAction(final CandidateSession candidateSession, final String actionName) {
        logEvent(candidateSession, "action=" + actionName);
    }

    public void logCandidateItemEvent(final CandidateSession candidateSession, final CandidateItemEvent candidateItemEvent) {
        logEvent(candidateSession, "action=CANDIDATE_ITEM_EVENT xeid=" + candidateItemEvent.getId()
                + " event=" + candidateItemEvent.getItemEventType()
                + " notifications=" + candidateItemEvent.getNotifications().size());
    }

    public void logPlaybackEvent(final CandidateSession candidateSession, final CandidateItemEvent candidateItemEvent,
            final CandidateItemEvent targetEvent) {
        logEvent(candidateSession, "action=CANDIDATE_ITEM_PLAYBACK xeid=" + candidateItemEvent.getId()
                + " event=" + candidateItemEvent.getItemEventType()
                + " target_xeid=" + targetEvent.getId());
    }

    public void logCandidateItemAttempt(final CandidateSession candidateSession, final CandidateItemAttempt candidateItemAttempt) {
        final CandidateItemEvent candidateItemEvent = candidateItemAttempt.getEvent();
        logEvent(candidateSession, "action=CANDIDATE_ITEM_ATTEMPT xeid=" + candidateItemEvent.getId()
                + " event=" + candidateItemEvent.getItemEventType()
                + " xaid=" + candidateItemAttempt.getId()
                + " notifications=" + candidateItemEvent.getNotifications().size());
    }

    public void logAndForbid(final CandidateSession candidateSession, final CandidatePrivilege privilege)
            throws CandidateForbiddenException {
        logEvent(candidateSession, "forbid=" + privilege);
        throw new CandidateForbiddenException(candidateSession, privilege);
    }
}
