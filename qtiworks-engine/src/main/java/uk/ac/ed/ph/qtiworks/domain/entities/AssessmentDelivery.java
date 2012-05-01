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

import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Corresponds to a particular "delivery" of an {@link AssessmentObject} to a group of candidates.
 * <p>
 * This is going to be very simple in the first instance, but will get more complicated in future.
 *
 * @author David McKain
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name="assessment_deliveries")
@SequenceGenerator(name="assessmentDeliverySequence", sequenceName="assessment_delivery_sequence", initialValue=1, allocationSize=5)
@NamedQueries({
    @NamedQuery(name="AssessmentDelivery.getForAssessmentPackage",
            query="SELECT d"
                + "  FROM AssessmentDelivery d"
                + "  WHERE d.assessmentPackage = :assessmentPackage"),
})
public abstract class AssessmentDelivery implements BaseEntity, TimestampedOnCreation {

    private static final long serialVersionUID = 7693569112981982946L;

    @Id
    @GeneratedValue(generator="assessmentDeliverySequence")
    @Column(name="did")
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="aid")
    private AssessmentPackage assessmentPackage;

    @Basic(optional=false)
    @Column(name="title")
    private String title;

    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    @Basic(optional=false)
    @Column(name="open")
    private boolean open;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }


    @Override
    public Date getCreationTime() {
        return creationTime;
    }

    @Override
    public void setCreationTime(final Date creationTime) {
        this.creationTime = creationTime;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }


    public AssessmentPackage getAssessmentPackage() {
        return assessmentPackage;
    }

    public void setAssessmentPackage(final AssessmentPackage assessmentPackage) {
        this.assessmentPackage = assessmentPackage;
    }


    public boolean isOpen() {
        return open;
    }

    public void setOpen(final boolean open) {
        this.open = open;
    }
}
