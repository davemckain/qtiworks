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

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * FIXME: Document this!
 * FIXME: Link to {@link CandidateItemSessionState}, which I reckon we need to record for
 * each event
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_item_events")
@Inheritance(strategy=InheritanceType.JOINED)
@SequenceGenerator(name="candidateEventSequence", sequenceName="candidate_event_sequence", initialValue=1, allocationSize=50)
public class CandidateItemEvent implements BaseEntity {

    private static final long serialVersionUID = -4620030911222629913L;

    @Id
    @GeneratedValue(generator="candidateEventSequence")
    @Column(name="xeid")
    private Long id;

    /** {@link CandidateItemRecord} owning this event */
    @ManyToOne(optional=false)
    @JoinColumn(name="xid")
    private CandidateItemRecord candidateItemRecord;

    /** Timestamp for this event */
    @Basic(optional=false)
    @Column(name="timestamp",updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    /** Type of event */
    @Basic(optional=false)
    @Column(name="event_type",updatable=false,length=32)
    @Enumerated(EnumType.STRING)
    private CandidateItemEventType eventType;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }


    public CandidateItemRecord getCandidateItemRecord() {
        return candidateItemRecord;
    }

    public void setCandidateItemRecord(final CandidateItemRecord candidateItemRecord) {
        this.candidateItemRecord = candidateItemRecord;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }


    public CandidateItemEventType getEventType() {
        return eventType;
    }

    public void setEventType(final CandidateItemEventType eventType) {
        this.eventType = eventType;
    }
}
