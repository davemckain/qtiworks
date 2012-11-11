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

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Encapsulates the stringified value of a particular (non-file)
 * outcome variable, computed at the end of an assessment.
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_session_outcomes")
@SequenceGenerator(name="candidateSessionOutcomeSequence", sequenceName="candidate_session_outcome_sequence", initialValue=1, allocationSize=50)
public class CandidateSessionOutcome implements BaseEntity {

    private static final long serialVersionUID = -4310598861282271053L;

    @Id
    @GeneratedValue(generator="candidateSessionOutcomeSequence")
    @Column(name="xoid")
    private Long id;

    /** Session owning this outcome */
    @ManyToOne(optional=false)
    @JoinColumn(name="xid")
    private CandidateSession candidateSession;

    /** Identifier of the underlying outcome variable */
    @Basic(optional=false)
    @Column(name="outcome_identifier", updatable=false, length=DomainConstants.QTI_IDENTIFIER_MAX_LENGTH)
    private String outcomeIdentifier;

    /** Stringified variable data */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="candidate_session_outcome_items", joinColumns=@JoinColumn(name="xoid"))
    @Column(name="string")
    private List<String> stringOutcomeData;

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

    public void setCandidateAttempt(final CandidateSession candidateSession) {
        this.candidateSession = candidateSession;
    }


    public String getOutcomeIdentifier() {
        return outcomeIdentifier;
    }

    public void setOutcomeIdentifier(final String outcomeIdentifier) {
        this.outcomeIdentifier = outcomeIdentifier;
    }


    public List<String> getStringOutcomeData() {
        return stringOutcomeData;
    }

    public void setStringOutcomeData(final List<String> stringOutcomeData) {
        this.stringOutcomeData = stringOutcomeData;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(id=" + id
                + ",outcomeIdentifier=" + outcomeIdentifier
                + ")";
    }
}