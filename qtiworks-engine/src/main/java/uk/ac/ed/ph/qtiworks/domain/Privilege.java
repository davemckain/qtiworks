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
package uk.ac.ed.ph.qtiworks.domain;

import uk.ac.ed.ph.qtiworks.domain.entities.User;

/**
 * Represents a "privilege" that a {@link User} needs to have to do something
 * or access a particular Object.
 *
 * @see PrivilegeException
 *
 * @author David McKain
 */
public enum Privilege {

    USER_INSTRUCTOR,
    USER_CANDIDATE,
    USER_ANONYMOUS,

    CREATE_ASSESSMENT,
    CHANGE_ASSESSMENT,
    VIEW_ASSESSMENT,
    VIEW_ASSESSMENT_SOURCE,

    ACCESS_CANDIDATE_SESSION,
    CANDIDATE_ACCESS_CLOSED_SESSION,
    CANDIDATE_END_SESSION_WHEN_INTERACTING,
    CANDIDATE_END_SESSION_AFTER_INTERACTING,
    CANDIDATE_RESET_SESSION_WHEN_INTERACTING,
    CANDIDATE_RESET_SESSION_AFTER_INTERACTING,
    CANDIDATE_REINIT_SESSION_WHEN_INTERACTING,
    CANDIDATE_REINIT_SESSION_AFTER_INTERACTING,
    CANDIDATE_MAKE_ATTEMPT,
    CANDIDATE_ACCESS_ITEM_DELIVERY,
    CANDIDATE_ACCESS_ASSESSMENT_FILE,
    CANDIDATE_VIEW_ASSESSMENT_SOURCE,
    CANDIDATE_VIEW_ASSESSMENT_RESULT,

    ;

}
