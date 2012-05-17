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

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.OrderColumn;
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
@NamedQueries({
    @NamedQuery(name="CandidateItemSession.getMostRecentEvent",
            query="SELECT e"
                + "  FROM CandidateItemSession s"
                + "    JOIN s.events e"
                + "  WHERE s = :candidateItemSession"
                + "    AND INDEX(e) = SIZE(s.events) - 1")
})
public class CandidateItemSession implements BaseEntity {

    private static final long serialVersionUID = -3537558551866726398L;

    @Id
    @GeneratedValue(generator="candidateItemSessionSequence")
    @Column(name="xid")
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="did")
    private ItemDelivery itemDelivery;

    @ManyToOne(optional=false)
    @JoinColumn(name="uid")
    private User candidate;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="candidateItemSession", cascade=CascadeType.REMOVE)
    @OrderColumn(name="attempt_index")
    private final List<CandidateItemEvent> events;

    //------------------------------------------------------------

    public CandidateItemSession() {
        this.events = new ArrayList<CandidateItemEvent>();
    }

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
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


    public List<CandidateItemEvent> getEvents() {
        return events;
    }
}
