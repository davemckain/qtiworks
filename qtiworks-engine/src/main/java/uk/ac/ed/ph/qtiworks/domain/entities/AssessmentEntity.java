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

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import javax.persistence.Basic;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents each {@link AssessmentItem} or {@link AssessmentTest} handled by the system
 *
 * @author David McKain
 */
@Entity
@Table(name="assessment_entities")
@SequenceGenerator(name="assessmentEntitySequence", sequenceName="assessment_entity_sequence", initialValue=1, allocationSize=10)
public class AssessmentEntity implements BaseEntity {

    private static final long serialVersionUID = -4330181851974184912L;

    @Id
    @GeneratedValue(generator="assessmentEntitySequence")
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="owner_user_id", updatable=false)
    private InstructorUser owner;

    @Basic(optional=false)
    @Column(name="type", updatable=false, length=15)
    @Enumerated(EnumType.STRING)
    private AssessmentObjectType assessmentType;

    @Lob
    @Basic(optional=false)
    @Column(name="base_path")
    private String basePath;

    @Lob
    @Basic(optional=false)
    @Column(name="object_href")
    private String objectHref;

    @Basic(optional=false)
    @Column(name="validated")
    private boolean validated;

    @Basic(optional=false)
    @Column(name="valid")
    private boolean valid;

    @Basic(optional=false)
    @Column(name="title")
    private String title; /* (Would take this from QTI) */

//  private List<AssessmentDelivery> deliveries;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }


    public InstructorUser getOwner() {
        return owner;
    }

    public void setOwner(final InstructorUser owner) {
        this.owner = owner;
    }


    public AssessmentObjectType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(final AssessmentObjectType assessmentType) {
        this.assessmentType = assessmentType;
    }


    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }


    public String getObjectHref() {
        return objectHref;
    }

    public void setObjectHref(final String objectHref) {
        this.objectHref = objectHref;
    }


    public boolean isValidated() {
        return validated;
    }

    public void setValidated(final boolean validated) {
        this.validated = validated;
    }


    public boolean isValid() {
        return valid;
    }

    public void setValid(final boolean valid) {
        this.valid = valid;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
