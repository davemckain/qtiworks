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

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Represents a candidate attempt on a particular {@link ItemDelivery}
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_item_attempts")
@Inheritance(strategy=InheritanceType.JOINED)
@SequenceGenerator(name="candidateItemAttemptSequence", sequenceName="candidate_item_attempt_sequence", initialValue=1, allocationSize=50)
public class CandidateItemAttempt implements BaseEntity {

    private static final long serialVersionUID = 8824668735905399883L;

    @Id
    @GeneratedValue(generator="candidateItemAttemptSequence")
    @Column(name="xaid")
    private Long id;

    /** {@link CandidateItemEvent} representing this attempt */
    @OneToOne(optional=false, fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
    @JoinColumn(name="xeid")
    private CandidateItemEvent event;

    @OneToMany(cascade=CascadeType.REMOVE)
    @JoinColumn(name="xaid")
    private Set<CandidateItemResponse> responses;

    @Type(type="org.hibernate.type.TextType")
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="invalid_response_identifiers", joinColumns=@JoinColumn(name="xaid"))
    @Column(name="response_identifier", length=DomainConstants.QTI_IDENTIFIER_MAX_LENGTH)
    private Set<String> invalidResponseIdentifiers;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }


    public CandidateItemEvent getEvent() {
        return event;
    }

    public void setEvent(final CandidateItemEvent event) {
        this.event = event;
    }


    public Set<CandidateItemResponse> getResponses() {
        return responses;
    }

    public void setResponses(final Set<CandidateItemResponse> responses) {
        this.responses = responses;
    }


    public Set<String> getInvalidResponseIdentifiers() {
        return invalidResponseIdentifiers;
    }

    public void setInvalidResponseIdentifiers(final Set<String> invalidResponseIdentifiers) {
        this.invalidResponseIdentifiers = invalidResponseIdentifiers;
    }
}
