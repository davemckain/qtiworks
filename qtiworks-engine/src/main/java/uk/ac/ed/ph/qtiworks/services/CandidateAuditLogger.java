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

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.rendering.StandaloneItemRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.TestItemRenderingRequest;
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
    private static final Logger candidateLogger = LoggerFactory.getLogger("CandidateAuditLogger");

    private void logSessionAction(final CandidateSession candidateSession, final String message) {
        candidateLogger.info("user={} xid={} did={} aid={} {}",
                new Object[] {
                    candidateSession.getCandidate().getBusinessKey(),
                    candidateSession.getId(),
                    candidateSession.getDelivery().getId(),
                    candidateSession.getDelivery().getAssessment().getId(),
                    message
        });
    }

    public void logAction(final CandidateSession candidateSession, final String actionName) {
        logSessionAction(candidateSession, "action=" + actionName);
    }

    public void logStandaloneItemRendering(final CandidateEvent candidateEvent, final StandaloneItemRenderingRequest renderingRequest) {
        logSessionAction(candidateEvent.getCandidateSession(), "action=RENDER_STANDALONE_ITEM mode=" + renderingRequest.getRenderingMode());
    }

    public void logTestEntryRendering(final CandidateEvent candidateEvent) {
        logSessionAction(candidateEvent.getCandidateSession(), "action=RENDER_TEST_ENTRY_PAGE");
    }

    public void logTestItemRendering(final CandidateEvent candidateEvent, final TestItemRenderingRequest renderingRequest) {
        logSessionAction(candidateEvent.getCandidateSession(), "action=RENDER_TEST_ITEM mode=" + renderingRequest.getRenderingMode());
    }

    public void logTestPartNavigationRendering(final CandidateEvent candidateEvent) {
        logSessionAction(candidateEvent.getCandidateSession(), "action=RENDER_CURRENT_TEST_PART_NAVIGATION");
    }

    public void logTestFeedbackRendering(final CandidateEvent candidateEvent) {
        logSessionAction(candidateEvent.getCandidateSession(), "action=RENDER_TEST_FEEDBACK");
    }


    public void logCandidateEvent(final CandidateEvent candidateEvent) {
        final StringBuilder messageBuilder = new StringBuilder("action=CANDIDATE_EVENT xeid=")
            .append(candidateEvent.getId());
        if (candidateEvent.getTestEventType()!=null) {
            messageBuilder.append(" testEvent=").append(candidateEvent.getTestEventType());
        }
        if (candidateEvent.getItemEventType()!=null) {
            messageBuilder.append(" itemEvent=").append(candidateEvent.getItemEventType());
        }
        if (candidateEvent.getTestItemKey()!=null) {
            messageBuilder.append(" testItemKey=").append(candidateEvent.getTestItemKey());
        }
        messageBuilder.append(" notifications=").append(candidateEvent.getNotifications().size());
        logSessionAction(candidateEvent.getCandidateSession(), messageBuilder.toString());
    }

    public void logPlaybackEvent(final CandidateSession candidateSession, final CandidateEvent candidateEvent,
            final CandidateEvent targetEvent) {
        logSessionAction(candidateSession, "action=CANDIDATE_ITEM_PLAYBACK xeid=" + candidateEvent.getId()
                + " event=" + candidateEvent.getItemEventType()
                + " target_xeid=" + targetEvent.getId());
    }

    public void logAndForbid(final CandidateSession candidateSession, final CandidatePrivilege privilege)
            throws CandidateForbiddenException {
        logSessionAction(candidateSession, "forbid=" + privilege);
        throw new CandidateForbiddenException(candidateSession, privilege);
    }
}
