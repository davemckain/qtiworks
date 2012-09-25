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
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
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

    private void logEvent(final CandidateItemSession candidateItemSession, final String message) {
        candidateLogger.info("user={} xid={} did={} aid={} state={} {}",
                new Object[] {
                    candidateItemSession.getCandidate().getBusinessKey(),
                    candidateItemSession.getId(),
                    candidateItemSession.getItemDelivery().getId(),
                    candidateItemSession.getItemDelivery().getAssessment().getId(),
                    candidateItemSession.getState(),
                    message
        });
    }

    public void logRendering(final CandidateItemEvent candidateItemEvent, final ItemRenderingRequest renderingRequest) {
        logEvent(candidateItemEvent.getCandidateItemSession(), "action=RENDER mode=" + renderingRequest.getRenderingMode());
    }

    public void logAction(final CandidateItemSession candidateItemSession, final String actionName) {
        logEvent(candidateItemSession, "action=" + actionName);
    }

    public void logCandidateItemEvent(final CandidateItemSession candidateItemSession, final CandidateItemEvent candidateItemEvent) {
        logEvent(candidateItemSession, "action=CANDIDATE_ITEM_EVENT xeid=" + candidateItemEvent.getId()
                + " event=" + candidateItemEvent.getEventType());
    }

    public void logPlaybackEvent(final CandidateItemSession candidateItemSession, final CandidateItemEvent candidateItemEvent,
            final CandidateItemEvent targetEvent) {
        logEvent(candidateItemSession, "action=CANDIDATE_ITEM_PLAYBACK xeid=" + candidateItemEvent.getId()
                + " event=" + candidateItemEvent.getEventType()
                + " target_xeid=" + targetEvent.getId());
    }

    public void logCandidateItemAttempt(final CandidateItemSession candidateItemSession, final CandidateItemAttempt candidateItemAttempt) {
        final CandidateItemEvent candidateItemEvent = candidateItemAttempt.getEvent();
        logEvent(candidateItemSession, "action=CANDIDATE_ITEM_ATTEMPT xeid=" + candidateItemEvent.getId()
                + " event=" + candidateItemEvent.getEventType()
                + " xaid=" + candidateItemAttempt.getId());
    }

    public void logAndForbid(final CandidateItemSession candidateItemSession, final CandidatePrivilege privilege)
            throws CandidateForbiddenException {
        logEvent(candidateItemSession, "forbid=" + privilege);
        throw new CandidateForbiddenException(candidateItemSession, privilege);
    }
}
