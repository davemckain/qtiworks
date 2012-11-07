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
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Represents each "event" generated during a {@link CandidateSession}
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_events")
@SequenceGenerator(name="candidateEventSequence", sequenceName="candidate_event_sequence", initialValue=1, allocationSize=50)
@NamedQueries({
    @NamedQuery(name="CandidateEvent.getForSession",
            query="SELECT e"
                + "  FROM CandidateEvent e"
                + "  WHERE e.candidateSession = :candidateSession"
                + "  ORDER BY e.id"),
    @NamedQuery(name="CandidateEvent.getForSessionReversed",
            query="SELECT e"
                + "  FROM CandidateEvent e"
                + "  WHERE e.candidateSession = :candidateSession"
                + "  ORDER BY e.id DESC"),
    @NamedQuery(name="CandidateEvent.getInCategoryForSession",
            query="SELECT e"
                + "  FROM CandidateEvent e"
                + "  WHERE e.candidateSession = :candidateSession"
                + "    AND e.candidateEventCategory = :candidateEventCategory"
                + "  ORDER BY e.id"),
    @NamedQuery(name="CandidateEvent.getInCategoryForSessionReversed",
            query="SELECT e"
                + "  FROM CandidateEvent e"
                + "  WHERE e.candidateSession = :candidateSession"
                + "    AND e.candidateEventCategory = :candidateEventCategory"
                + "  ORDER BY e.id DESC"),
    @NamedQuery(name="CandidateEvent.getTestItemEventsForSessionReversed",
            query="SELECT e"
                + "  FROM CandidateEvent e"
                + "  WHERE e.candidateSession = :candidateSession"
                + "    AND e.testEventType = uk.ac.ed.ph.qtiworks.domain.entities.CandidateEventCategory.TEST"
                + "    AND e.testItemKey = :testItemKey"
                + "  ORDER BY e.id DESC")
})
public class CandidateEvent implements BaseEntity {

    private static final long serialVersionUID = -4620030911222629913L;

    @Id
    @GeneratedValue(generator="candidateEventSequence")
    @Column(name="xeid")
    private Long id;

    /** {@link CandidateSession} owning this event */
    @ManyToOne(optional=false)
    @JoinColumn(name="xid")
    private CandidateSession candidateSession;

    /** Timestamp for this event */
    @Basic(optional=false)
    @Column(name="timestamp", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    /** Category of event */
    @Basic(optional=false)
    @Column(name="event_category", updatable=false, length=16)
    @Enumerated(EnumType.STRING)
    private CandidateEventCategory candidateEventCategory;

    /** Type of item event, or null if not an item event */
    @Basic(optional=true)
    @Column(name="item_event_type", updatable=false, length=16)
    @Enumerated(EnumType.STRING)
    private CandidateItemEventType itemEventType;

    /** Type of test event, or null if not a test event */
    @Basic(optional=true)
    @Column(name="test_event_type", updatable=false, length=16)
    @Enumerated(EnumType.STRING)
    private CandidateTestEventType testEventType;

    /**
     * If this is an item event for an item within a test, then this records
     * the {@link TestPlanNodeKey} of the item. Otherwise, this is null.
     */
    @Basic(optional=true)
    @Column(name="test_item_key", updatable=false, length=DomainConstants.QTI_IDENTIFIER_MAX_LENGTH + 10)
    private String testItemKey;

    /**
     * For a {@link CandidateItemEventType#PLAYBACK} event, this points to the event in
     * the same session that the candidate has requested to see
     * {@link CandidateItemEvent}
     */
    @OneToOne(optional=true)
    @JoinColumn(name="playback_xeid", updatable=false)
    private CandidateEvent playbackEvent;

    /**
     * Notifications generated during this event
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="candidateEvent")
    @OrderBy("id")
    private List<CandidateEventNotification> notifications;

    //------------------------------------------------------------

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }


    public CandidateSession getCandidateSession() {
        return candidateSession;
    }

    public void setCandidateSession(final CandidateSession candidateSession) {
        this.candidateSession = candidateSession;
    }


    public Date getTimestamp() {
        return ObjectUtilities.safeClone(timestamp);
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = ObjectUtilities.safeClone(timestamp);
    }


    public CandidateEventCategory getCategoryEventCategory() {
        return candidateEventCategory;
    }

    public void setCandidateEventCategory(final CandidateEventCategory categoryEventCategory) {
        this.candidateEventCategory = categoryEventCategory;
    }


    public CandidateItemEventType getItemEventType() {
        return itemEventType;
    }

    public void setItemEventType(final CandidateItemEventType itemEventType) {
        this.itemEventType = itemEventType;
    }


    public CandidateTestEventType getTestEventType() {
        return testEventType;
    }

    public void setTestEventType(final CandidateTestEventType testEventType) {
        this.testEventType = testEventType;
    }


    public String getTestItemKey() {
        return testItemKey;
    }

    public void setTestItemKey(final String testItemKey) {
        this.testItemKey = testItemKey;
    }


    public CandidateEvent getPlaybackEvent() {
        return playbackEvent;
    }

    public void setPlaybackEvent(final CandidateEvent playbackEvent) {
        this.playbackEvent = playbackEvent;
    }


    public List<CandidateEventNotification> getNotifications() {
        if (notifications==null) {
            notifications = new ArrayList<CandidateEventNotification>();
        }
        return notifications;
    }

    public void setNotifications(final List<CandidateEventNotification> notifications) {
        this.notifications = notifications;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(id=" + id
                + ",eventCategory=" + candidateEventCategory
                + ")";
    }
}
