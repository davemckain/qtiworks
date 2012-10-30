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
import uk.ac.ed.ph.qtiworks.domain.binding.ItemSesssionStateXmlMarshaller;

import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Represents each "event" generated during a {@link CandidateSession}
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_item_events")
@NamedQueries({
    @NamedQuery(name="CandidateItemEvent.getForSession",
            query="SELECT e"
                + "  FROM CandidateItemEvent e"
                + "  WHERE e.candidateSession = :candidateSession"
                + "  ORDER BY e.id"),
    @NamedQuery(name="CandidateItemEvent.getForSessionReversed",
            query="SELECT e"
                + "  FROM CandidateItemEvent e"
                + "  WHERE e.candidateSession = :candidateSession"
                + "  ORDER BY e.id DESC")
})
public class CandidateItemEvent extends CandidateEvent implements BaseEntity {

    private static final long serialVersionUID = 6121745930649659116L;

    /** Type of event */
    @Basic(optional=false)
    @Column(name="item_event_type", updatable=false, length=16)
    @Enumerated(EnumType.STRING)
    private CandidateItemEventType itemEventType;

    /**
     * Status that the {@link CandidateSession} had when (just before)
     * this event was performed.
     */
    @Basic(optional=false)
    @Column(name="state", length=11)
    @Enumerated(EnumType.STRING)
    private CandidateSessionStatus sessionStatus;

    /** Value of the <code>completionStatus</code> item variable */
    @Basic(optional=false)
    @Column(name="completion_status", updatable=false, length=DomainConstants.QTI_COMPLETION_STATUS_MAX_LENGTH)
    private String completionStatus;

    /** Value of the <code>duration</code> item variable (at the time this event was created) */
    @Basic(optional=false)
    @Column(name="duration", updatable=false)
    private double duration;

    /** Value of the <code>numAttempts</code> item variable */
    @Basic(optional=false)
    @Column(name="num_attempts", updatable=false)
    private int numAttempts;

    /**
     * {@link ItemSessionState} serialized in a custom XML format.
     *
     * @see ItemSesssionStateXmlMarshaller
     */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=false)
    @Column(name="item_session_state_xml", updatable=false)
    private String itemSessionStateXml;

    /**
     * For a {@link CandidateItemEventType#PLAYBACK} event, this points to the event in
     * the same session that the candidate has requested to see
     * {@link CandidateItemEvent}
     */
    @OneToOne(optional=true)
    @JoinColumn(name="playback_xeid", updatable=false)
    private CandidateItemEvent playbackEvent;

    //------------------------------------------------------------

    public CandidateItemEvent() {
        super(CandidateEventCategory.ITEM);
    }

    //----------------------------------------------------------

    public CandidateItemEventType getItemEventType() {
        return itemEventType;
    }

    public void setItemEventType(final CandidateItemEventType itemEventType) {
        this.itemEventType = itemEventType;
    }


    @Override
    public CandidateSessionStatus getSessionStatus() {
        return sessionStatus;
    }

    @Override
    public void setSessionStatus(final CandidateSessionStatus sessionState) {
        this.sessionStatus = sessionState;
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


    public CandidateItemEvent getPlaybackEvent() {
        return playbackEvent;
    }

    public void setPlaybackEvent(final CandidateItemEvent playbackEvent) {
        this.playbackEvent = playbackEvent;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(id=" + getId()
                + ",eventCategory" + getEventCategory()
                + ",itemEventType=" + itemEventType
                + ")";
    }
}
