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

import uk.ac.ed.ph.qtiworks.domain.DomainConstants;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents the "session" for a particular candidate {@link User} against a
 * particular {@link ItemDelivery}
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_item_sessions")
@SequenceGenerator(name="candidateItemSessionSequence", sequenceName="candidate_item_session_sequence", initialValue=1, allocationSize=50)
public class CandidateItemSession implements BaseEntity {

    private static final long serialVersionUID = -3537558551866726398L;

    @Id
    @GeneratedValue(generator="candidateItemSessionSequence")
    @Column(name="xid")
    private Long id;

    /**
     * Randomly-generated hash for this session. Used in conjunction with the <code>xid</code>
     * in URLs referring to sessions to make it more difficult to hijack sessions.
     * <p>
     * The hash is not necessarily unique so should not be used as a lookup key.
     */
    @Basic(optional=false)
    @Column(name="hash", length=DomainConstants.CANDIDATE_SESSION_HASH_LENGTH)
    private String sessionHash;

    /**
     * URL to go to once the session has been terminated.
     * This URL is expected to be within the QTIWorks webapp, so must start with '/'
     */
    @Basic(optional=false)
    @Column(name="exit_url", length=DomainConstants.CANDIDATE_SESSION_EXIT_URL_LENGTH)
    private String exitUrl;

    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="did")
    private ItemDelivery itemDelivery;

    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="uid")
    private User candidate;

    /** Current state */
    @Basic(optional=false)
    @Column(name="state", length=11)
    @Enumerated(EnumType.STRING)
    private CandidateSessionState state;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }


    public String getSessionHash() {
        return sessionHash;
    }

    public void setSessionHash(final String sessionHash) {
        this.sessionHash = sessionHash;
    }


    public String getExitUrl() {
        return exitUrl;
    }


    public void setExitUrl(final String exitUrl) {
        this.exitUrl = exitUrl;
    }

    public ItemDelivery getItemDelivery() {
        return itemDelivery;
    }

    public void setItemDelivery(final ItemDelivery itemDelivery) {
        this.itemDelivery = itemDelivery;
    }


    public User getCandidate() {
        return candidate;
    }

    public void setCandidate(final User candidate) {
        this.candidate = candidate;
    }


    public CandidateSessionState getState() {
        return state;
    }

    public void setState(final CandidateSessionState state) {
        this.state = state;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(id=" + id
                + ",state=" + state
                + ")";
    }
}
