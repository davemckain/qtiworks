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
package uk.ac.ed.ph.qtiworks.domain.entities;

import uk.ac.ed.ph.qtiworks.domain.DomainConstants;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Represents the "session" for a particular candidate {@link User} against a
 * particular {@link Delivery} of an {@link AssessmentItem} or {@link AssessmentTest}
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_sessions")
@SequenceGenerator(name="candidateSessionSequence", sequenceName="candidate_session_sequence", initialValue=1, allocationSize=1)
@NamedQueries({
    @NamedQuery(name="CandidateSession.getForCandidate",
            query="SELECT x"
                + "  FROM CandidateSession x"
                + "  WHERE x.candidate = :candidate"
                + "  ORDER BY x.id"),
    @NamedQuery(name="CandidateSession.getForDelivery",
            query="SELECT x"
                + "  FROM CandidateSession x"
                + "  WHERE x.delivery = :delivery"
                + "  ORDER BY x.id"),
    @NamedQuery(name="CandidateSession.getNonTerminatedForDeliveryAndCandidate",
            query="SELECT x"
                + "  FROM CandidateSession x"
                + "  WHERE x.delivery = :delivery"
                + "  AND x.candidate = :candidate"
                + "  AND x.terminated IS FALSE"
                + "  ORDER BY x.id")
})
public class CandidateSession implements BaseEntity, TimestampedOnCreation {

    private static final long serialVersionUID = -3537558551866726398L;

    @Id
    @GeneratedValue(generator="candidateSessionSequence")
    @Column(name="xid")
    private Long xid;

    /** Session creation timestamp */
    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    /**
     * Randomly-generated token for this session. Used in conjunction with the <code>xid</code>
     * in URLs referring to sessions to make it more difficult to hijack sessions.
     * <p>
     * The token is not necessarily unique so should not be used as a lookup key.
     */
    @Basic(optional=false)
    @Column(name="session_token", length=DomainConstants.CANDIDATE_SESSION_TOKEN_LENGTH)
    private String sessionToken;

    /**
     * URL to go to once the session has been terminated.
     * <p>
     * If the URL starts with '/' then it is interpreted as an internal link within the webapp.
     * <p>
     * A URL starting <code>http://</code> or <code>https://</code> is interpreted as an
     * absolute link to another system. Care must be taken to ensure this is not used
     * maliciously.
     * <p>
     * A null URL will direct to a blank page afterwards.
     */
    @Basic(optional=true)
    @Column(name="exit_url", length=DomainConstants.CANDIDATE_SESSION_EXIT_URL_LENGTH)
    private String exitUrl;

    /** {@link Delivery} owning this session */
    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="did")
    private Delivery delivery;

    /** Candidate running this session */
    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="uid")
    private User candidate;

    /**
     * Flag to indicate whether session has been closed.
     * Once closed, a result will have been recorded and the item/test state
     * can no longer be changed. Candidates may still review the session though.
     */
    @Basic(optional=false)
    @Column(name="closed")
    private boolean closed;

    /**
     * Flag to indicate whether session has been terminated.
     * Once terminated, a session is no longer available to the candidate.
     */
    @Basic(optional=false)
    @Column(name="is_terminated") /* NB: MySQL reserves the keyword 'terminated'! */
    private boolean terminated;

    /** (Currently used for cascading deletion only - upgrade if required) */
    @SuppressWarnings("unused")
    @OneToMany(mappedBy="candidateSession", cascade=CascadeType.REMOVE)
    private Set<CandidateEvent> candidateEvents;

    /** (Currently used for cascading deletion only - upgrade if required) */
    @SuppressWarnings("unused")
    @OneToMany(mappedBy="candidateSession", cascade=CascadeType.REMOVE)
    private Set<CandidateFileSubmission> candidateFileSubmissions;

    /** (Currently used for cascading deletion only - upgrade if required) */
    @SuppressWarnings("unused")
    @OneToMany(mappedBy="candidateSession", cascade=CascadeType.REMOVE)
    private Set<CandidateSessionOutcome> candidateSessionOutcomes;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return xid;
    }

    @Override
    public void setId(final Long id) {
        this.xid = id;
    }


    @Override
    public Date getCreationTime() {
        return ObjectUtilities.safeClone(creationTime);
    }

    @Override
    public void setCreationTime(final Date creationTime) {
        this.creationTime = ObjectUtilities.safeClone(creationTime);
    }


    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(final String sessionToken) {
        this.sessionToken = sessionToken;
    }


    public String getExitUrl() {
        return exitUrl;
    }


    public void setExitUrl(final String exitUrl) {
        this.exitUrl = exitUrl;
    }


    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(final Delivery delivery) {
        this.delivery = delivery;
    }


    public User getCandidate() {
        return candidate;
    }

    public void setCandidate(final User candidate) {
        this.candidate = candidate;
    }


    public boolean isClosed() {
        return closed;
    }

    public void setClosed(final boolean closed) {
        this.closed = closed;
    }


    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(final boolean terminated) {
        this.terminated = terminated;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(xid=" + xid
                + ",closed=" + closed
                + ",terminated=" + terminated
                + ")";
    }
}
