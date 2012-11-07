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
package uk.ac.ed.ph.qtiworks.domain.entities;

/**
 * Enumerates the item-specific events that can happen when delivering
 * an item or test within a {@link CandidateSession}
 *
 * @author David McKain
 */
public enum CandidateItemEventType {

    /**
     * Item session first initialised and template processing happened
     * (NB: Only in standalone item sessions)
     */
    INIT,

    /** Attempt made, all responses bound successfully and valid */
    ATTEMPT_VALID,

    /** Attempt made, all responses bound successfully but some invalid */
    ATTEMPT_INVALID,

    /** Attempt made, some responses bound unsuccessfully */
    ATTEMPT_BAD,

    /** Candidate has re-initialised the session (i.e. template processing has been redone) */
    REINIT,

    /**
     * Candidate has reset the session back to the state immediately after the last
     * reinit (or after the {@link #INIT} if there were no reinits)
     */
    RESET,

    /**
     * Candidate has requested a model solution to be rendered.
     * (This closes the session if it hasn't already been closed)
     */
    SOLUTION,

    /**
     * Candidate has requested to "playback" a particular event.
     */
    PLAYBACK,

    /**
     * Candidate has closed the current session and moved it into
     * {@link CandidateSessionState#CLOSED} state
     */
    CLOSE,

    ;

}
