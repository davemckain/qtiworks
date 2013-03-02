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

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

/**
 * Represents each "event" generated during a {@link CandidateSession}
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_events")
@SequenceGenerator(name="candidateEventSequence", sequenceName="candidate_event_sequence", initialValue=1, allocationSize=1)
@NamedQueries({
    @NamedQuery(name="CandidateEvent.getForSession",
            query="SELECT xe"
                + "  FROM CandidateEvent xe"
                + "  WHERE xe.candidateSession = :candidateSession"
                + "  ORDER BY xe.id"),
    @NamedQuery(name="CandidateEvent.getForSessionReversed",
            query="SELECT xe"
                + "  FROM CandidateEvent xe"
                + "  WHERE xe.candidateSession = :candidateSession"
                + "  ORDER BY xe.id DESC")
})
public class CandidateEvent implements BaseEntity {

    private static final long serialVersionUID = -4620030911222629913L;

    @Id
    @GeneratedValue(generator="candidateEventSequence")
    @Column(name="xeid")
    private Long xeid;

    /** {@link CandidateSession} owning this event */
    @ManyToOne(optional=false)
    @JoinColumn(name="xid")
    private CandidateSession candidateSession;

    /** Timestamp for this event */
    @Basic(optional=false)
    @Column(name="timestamp", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    /**
     * Records the type of a test event within a test, or null if we are not running a test.
     */
    @Basic(optional=true)
    @Column(name="test_event_type", updatable=false, length=32)
    @Enumerated(EnumType.STRING)
    private CandidateTestEventType testEventType;

    /**
     * If {@link #testEventType} is a {@link CandidateTestEventType#ITEM_EVENT}, then this gives
     * details about exactly what item event this was. Otherwise this will be null.
     */
    @Basic(optional=true)
    @Column(name="item_event_type", updatable=false, length=32)
    @Enumerated(EnumType.STRING)
    private CandidateItemEventType itemEventType;

    /**
     * For "modal" events within a test, this records the key for the item upon which the event
     * was performed. Otherwise it is null.
     */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="test_item_key", updatable=false)
    private String testItemKey;

    /**
     * Notifications generated during this event
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="candidateEvent", cascade=CascadeType.REMOVE)
    @OrderBy("id")
    private List<CandidateEventNotification> notifications;

    /** (Currently used for cascading deletion only - upgrade if required) */
    @SuppressWarnings("unused")
    @OneToMany(mappedBy="candidateEvent", cascade=CascadeType.REMOVE)
    private Set<CandidateResponse> candidateResponses;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return xeid;
    }

    @Override
    public void setId(final Long id) {
        this.xeid = id;
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
                + "(xeid=" + xeid
                + ")";
    }
}
