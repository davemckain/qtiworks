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

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

/**
 * Represents each "event" generated during a {@link CandidateItemSession}
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_item_events")
@SequenceGenerator(name="candidateItemEventSequence", sequenceName="candidate_item_event_sequence", initialValue=1, allocationSize=50)
@NamedQueries({
    @NamedQuery(name="CandidateItemEvent.getForSession",
            query="SELECT e"
                + "  FROM CandidateItemEvent e"
                + "  WHERE e.candidateItemSession = :candidateItemSession"
                + "  ORDER BY e.id"),
    @NamedQuery(name="CandidateItemEvent.getForSessionReversed",
            query="SELECT e"
                + "  FROM CandidateItemEvent e"
                + "  WHERE e.candidateItemSession = :candidateItemSession"
                + "  ORDER BY e.id DESC")
})
public class CandidateItemEvent implements BaseEntity {

    private static final long serialVersionUID = -4620030911222629913L;

    @Id
    @GeneratedValue(generator="candidateItemEventSequence")
    @Column(name="xeid")
    private Long id;

    /** {@link CandidateItemSession} owning this event */
    @ManyToOne(optional=false)
    @JoinColumn(name="xid")
    private CandidateItemSession candidateItemSession;

    /** Timestamp for this event */
    @Basic(optional=false)
    @Column(name="timestamp", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    /** Type of event */
    @Basic(optional=false)
    @Column(name="event_type", updatable=false, length=16)
    @Enumerated(EnumType.STRING)
    private CandidateItemEventType eventType;

    /**
     * State that the {@link CandidateItemSession} was in when (just before)
     * this event was performed.
     */
    @Basic(optional=false)
    @Column(name="state", length=11)
    @Enumerated(EnumType.STRING)
    private CandidateSessionState sessionState;

    /** Value of the <code>completionStatus</code> item variable */
    @Basic(optional=false)
    @Column(name="completion_status", updatable=false, length=DomainConstants.QTI_COMPLETION_STATUS_MAX_LENGTH)
    private String completionStatus;

    @Basic(optional=false)
    @Column(name="duration", updatable=false)
    private double duration;

    @Basic(optional=false)
    @Column(name="num_attempts", updatable=false)
    private int numAttempts;

    /** Serialized {@link ItemSessionState} */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=false)
    @Column(name="item_session_state_xml", updatable=false)
    private String itemSessionStateXml;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }


    public CandidateItemSession getCandidateItemSession() {
        return candidateItemSession;
    }

    public void setCandidateItemSession(final CandidateItemSession candidateItemSession) {
        this.candidateItemSession = candidateItemSession;
    }


    public Date getTimestamp() {
        return ObjectUtilities.safeClone(timestamp);
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = ObjectUtilities.safeClone(timestamp);
    }


    public CandidateItemEventType getEventType() {
        return eventType;
    }

    public void setEventType(final CandidateItemEventType eventType) {
        this.eventType = eventType;
    }


    public CandidateSessionState getSessionState() {
        return sessionState;
    }

    public void setSessionState(final CandidateSessionState sessionState) {
        this.sessionState = sessionState;
    }


    public String getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(final String completionStatus) {
        this.completionStatus = completionStatus;
    }


    public double getDuration() {
        return duration;
    }

    public void setDuration(final double duration) {
        this.duration = duration;
    }


    public int getNumAttempts() {
        return numAttempts;
    }

    public void setNumAttempts(final int numAttempts) {
        this.numAttempts = numAttempts;
    }


    public String getItemSessionStateXml() {
        return itemSessionStateXml;
    }

    public void setItemSessionStateXml(final String itemSessionStateXml) {
        this.itemSessionStateXml = itemSessionStateXml;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(id=" + id
                + ",eventType=" + eventType
                + ")";
    }
}
