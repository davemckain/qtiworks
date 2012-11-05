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

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents a candidate attempt within a {@link CandidateSession}.
 * One of these is always attached to a {@link CandidateEvent}.
 *
 * FIXME: This probably needs to be renamed as we also use this to refer to saved
 * responses in tests before response processing has been run, and they're not
 * technically considered to be attempts at this time.
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_attempts")
@Inheritance(strategy=InheritanceType.JOINED)
@SequenceGenerator(name="candidateAttemptSequence", sequenceName="candidate_attempt_sequence", initialValue=1, allocationSize=50)
@NamedQueries({
    @NamedQuery(name="CandidateAttempt.getForEvent",
            query="SELECT a"
                + "  FROM CandidateAttempt a"
                + "  WHERE a.event = :candidateEvent")
})
public class CandidateAttempt implements BaseEntity {

    private static final long serialVersionUID = 8824668735905399883L;

    @Id
    @GeneratedValue(generator="candidateAttemptSequence")
    @Column(name="xaid")
    private Long id;

    /** {@link CandidateEvent} representing this attempt */
    @OneToOne(optional=false, fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
    @JoinColumn(name="xeid")
    private CandidateEvent event;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="xaid")
    private Set<CandidateResponse> responses;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }


    public CandidateEvent getEvent() {
        return event;
    }

    public void setEvent(final CandidateEvent event) {
        this.event = event;
    }


    public Set<CandidateResponse> getResponses() {
        return responses;
    }

    public void setResponses(final Set<CandidateResponse> responses) {
        this.responses = responses;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(id=" + id
                + ")";
    }
}
