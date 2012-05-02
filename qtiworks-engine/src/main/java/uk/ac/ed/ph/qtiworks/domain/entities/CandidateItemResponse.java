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

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.types.ResponseData.ResponseDataType;

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

/**
 * Encapsulates a response to an {@link Interaction} within a particular {@link AssessmentItem}
 *
 * @author David McKain
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name="candidate_item_responses")
@SequenceGenerator(name="candidateResponseSequence", sequenceName="candidate_response_sequence", initialValue=1, allocationSize=50)
public abstract class CandidateItemResponse implements BaseEntity {

    private static final long serialVersionUID = -4310598861282271053L;

    @Id
    @GeneratedValue(generator="candidateResponseSequence")
    @Column(name="xrid")
    private Long id;

    /** Attempt in which this response was made */
    @ManyToOne(optional=false)
    @JoinColumn(name="xid")
    private CandidateItemAttempt attempt;

    @Basic(optional=false)
    @Column(name="response_identifier", updatable=false, length=DomainConstants.QTI_IDENTIFIER_MAX_LENGTH)
    private String responseIdentifier;

    @Basic(optional=false)
    @Column(name="response_type", updatable=false, length=5)
    @Enumerated(EnumType.STRING)
    private final ResponseDataType responseType;

    //------------------------------------------------------------

    protected CandidateItemResponse(final ResponseDataType responseType) {
        this.responseType = responseType;
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


    public CandidateItemAttempt getAttempt() {
        return attempt;
    }

    public void setAttempt(final CandidateItemAttempt attempt) {
        this.attempt = attempt;
    }


    public String getResponseIdentifier() {
        return responseIdentifier;
    }

    public void setResponseIdentifier(final String responseIdentifier) {
        this.responseIdentifier = responseIdentifier;
    }


    public ResponseDataType getResponseType() {
        return responseType;
    }
}