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

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.types.ResponseData.ResponseDataType;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * Encapsulates the response made by a particular candidate to a particular {@link Interaction}
 * within a particular {@link AssessmentItem}.
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_responses")
@SequenceGenerator(name="candidateResponseSequence", sequenceName="candidate_response_sequence", initialValue=1, allocationSize=5)
public class CandidateResponse implements BaseEntity {

    private static final long serialVersionUID = -4310598861282271053L;

    @Id
    @GeneratedValue(generator="candidateResponseSequence")
    @Column(name="xrid")
    private Long xrid;

    /** {@link CandidateEvent} on which this response was submitted */
    @ManyToOne(optional=false, fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
    @JoinColumn(name="xeid")
    private CandidateEvent candidateEvent;

    /** Identifier of the underlying response variable */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=false)
    @Column(name="response_identifier", updatable=false)
    private String responseIdentifier;

    /** Type of response */
    @Basic(optional=false)
    @Column(name="response_type", updatable=false, length=6)
    @Enumerated(EnumType.STRING)
    private ResponseDataType responseDataType;

    /** Legality of response */
    @Basic(optional=false)
    @Column(name="response_legality", updatable=false, length=7)
    @Enumerated(EnumType.STRING)
    private ResponseLegality responseLegality;

    /** Raw response string data (only used for {@link ResponseDataType#STRING} */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="candidate_string_response_items", joinColumns=@JoinColumn(name="xrid"))
    @Column(name="string")
    private List<String> stringResponseData;

    /** File submission data (only used for {@link ResponseDataType#FILE} */
    @ManyToOne(optional=true)
    @JoinColumn(name="fid")
    private CandidateFileSubmission fileSubmission;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return xrid;
    }

    @Override
    public void setId(final Long id) {
        this.xrid = id;
    }


    public CandidateEvent getCandidateEvent() {
        return candidateEvent;
    }

    public void setCandidateEvent(final CandidateEvent candidateEvent) {
        this.candidateEvent = candidateEvent;
    }


    public String getResponseIdentifier() {
        return responseIdentifier;
    }

    public void setResponseIdentifier(final String responseIdentifier) {
        this.responseIdentifier = responseIdentifier;
    }


    public ResponseDataType getResponseDataType() {
        return responseDataType;
    }

    public void setResponseDataType(final ResponseDataType responseDataType) {
        this.responseDataType = responseDataType;
    }


    public ResponseLegality getResponseLegality() {
        return responseLegality;
    }

    public void setResponseLegality(final ResponseLegality responseLegality) {
        this.responseLegality = responseLegality;
    }


    public List<String> getStringResponseData() {
        return stringResponseData;
    }


    public void setStringResponseData(final List<String> stringResponseData) {
        this.stringResponseData = stringResponseData;
    }


    public CandidateFileSubmission getFileSubmission() {
        return fileSubmission;
    }

    public void setFileSubmission(final CandidateFileSubmission fileSubmission) {
        this.fileSubmission = fileSubmission;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(xrid=" + xrid
                + ",responseIdentifier=" + responseIdentifier
                + ",responseType=" + responseDataType
                + ",responseLegality=" + responseLegality
                + ")";
    }
}