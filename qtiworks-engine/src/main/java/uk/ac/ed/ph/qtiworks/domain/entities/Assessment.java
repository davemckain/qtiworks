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
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

/**
 * Represents an assessment within the system. This entity contains the basic data about
 * an assessment. Information about the assessment's files is encapsulated by an {@link AssessmentPackage}.
 * <p>
 * The data model here provides a 1->N relationship between an {@link Assessment} and {@link AssessmentPackage}s,
 * as I had originally planned to include basic revisioning of resources. However, I've decided to simplify this for
 * now with a 1->1 relationship, but have left the 1->N mapping between the entities in case someone else wants to
 * add this revisioning functionality.
 *
 * @see AssessmentPackage
 *
 * @author David McKain
 */
@Entity
@Table(name="assessments")
@SequenceGenerator(name="assessmentSequence", sequenceName="assessment_sequence", initialValue=1, allocationSize=1)
@NamedQueries({
    @NamedQuery(name="Assessment.getForOwnerUser",
            query="SELECT a, ap"
                + "  FROM Assessment a"
                + "  LEFT JOIN a.selectedAssessmentPackage ap"
                + "  WHERE a.ownerUser = :user"
                + "  ORDER BY a.creationTime"),
    @NamedQuery(name="Assessment.getForOwnerLtiContext",
            query="SELECT a, ap"
                + "  FROM Assessment a"
                + "  LEFT JOIN a.selectedAssessmentPackage ap"
                + "  WHERE a.ownerLtiContext = :ltiContext"
                + "  ORDER BY a.creationTime"),
    @NamedQuery(name="Assessment.getForSampleCategory",
            query="SELECT a, ap"
                + "  FROM Assessment a"
                + "  LEFT JOIN a.selectedAssessmentPackage ap"
                + "  WHERE a.sampleCategory = :sampleCategory"
                + "  ORDER BY a.id") /* NB: All samples have same creationTime, so use order by ID to get insertion order */
})
public class Assessment implements BaseEntity, TimestampedOnCreation {

    private static final long serialVersionUID = -4330181851974184912L;

    @Id
    @GeneratedValue(generator="assessmentSequence")
    @Column(name="aid")
    private Long aid;

    @Version
    @Column(name="lock_version")
    private Long version;

    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    /**
     * Total number of {@link AssessmentPackage}s uploaded for this Assessment.
     * (This may be larger than the size of {@link #assessmentPackages})
     */
    @Basic(optional=false)
    @Column(name="package_import_version")
    private Long packageImportVersion;

    /** {@link User} who owns this Assessment */
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="owner_uid", updatable=false)
    private User ownerUser;

    /** {@link LtiContext} owning this Assessment, if appropriate */
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @JoinColumn(name="owner_lcid", updatable=false)
    private LtiContext ownerLtiContext;

    /** Item or Test? */
    @Basic(optional=false)
    @Column(name="type", updatable=false, length=15)
    @Enumerated(EnumType.STRING)
    private AssessmentObjectType assessmentType;

    /**
     * CURRENTLY UNUSED! Ignore this for now.
     *
     * (This was envisaged as a simple means of sharing assessments, but hasn't been
     * implemented.)
     */
    @Basic(optional=false)
    @Column(name="public")
    private boolean isPublic;

    /**
     * For sample items, this specifies the category it belongs to. This should be set to null
     * for non-sample items.
     * <p>
     * (This is expected to be temporary)
     */
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @JoinColumn(name="sample_category_id", updatable=false)
    private SampleCategory sampleCategory;

    /**
     * Currently-selected {@link AssessmentPackage} for this {@link Assessment}.
     */
    @OneToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="selected_apid")
    private AssessmentPackage selectedAssessmentPackage;

    /**
     * All {@link AssessmentPackage}s uploaded for this {@link Assessment}, ordered by ID (apid),
     * which will be chronological.
     * <p>
     * NB: As of version 1.0-M5, this should generally return a single entity, which should
     * be the same as {@link #selectedAssessmentPackage}.
     */
    @OneToMany(mappedBy="assessment", fetch=FetchType.LAZY)
    @OrderBy("apid")
    private List<AssessmentPackage> assessmentPackages;

    /**
     * All {@link Delivery} entities created for this {@link Assessment}, ordered by ID (did).
     */
    @OneToMany(mappedBy="assessment", fetch=FetchType.LAZY)
    @OrderBy("did")
    private List<Delivery> deliveries;

    /** Identifier of the QTI outcome variable to use when returning scores (via LTI) */
    @Basic(optional=true)
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Column(name="lti_result_outcome_identifier")
    private String ltiResultOutcomeIdentifier;

    /**
     * Minimum value for the result outcome variable.
     * Will always be set if {@link #ltiResultOutcomeIdentifier} is not null.
     */
    @Basic(optional=true)
    @Column(name="lti_result_minimum")
    private Double ltiResultMinimum;

    /**
     * Maximum value for the result outcome variable.
     * Will always be set if {@link #ltiResultOutcomeIdentifier} is not null.
     */
    @Basic(optional=true)
    @Column(name="lti_result_maximum")
    private Double ltiResultMaximum;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return aid;
    }

    @Override
    public void setId(final Long id) {
        this.aid = id;
    }


    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }


    public Long getPackageImportVersion() {
        return packageImportVersion;
    }

    public void setPackageImportVersion(final Long packageImportVersion) {
        this.packageImportVersion = packageImportVersion;
    }


    @Override
    public Date getCreationTime() {
        return ObjectUtilities.safeClone(creationTime);
    }

    @Override
    public void setCreationTime(final Date creationTime) {
        this.creationTime = ObjectUtilities.safeClone(creationTime);
    }


    public User getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(final User ownerUser) {
        this.ownerUser = ownerUser;
    }


    public LtiContext getOwnerLtiContext() {
        return ownerLtiContext;
    }

    public void setOwnerLtiContext(final LtiContext ownerLtiContext) {
        this.ownerLtiContext = ownerLtiContext;
    }


    public AssessmentObjectType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(final AssessmentObjectType assessmentType) {
        this.assessmentType = assessmentType;
    }


    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(final boolean isPublic) {
        this.isPublic = isPublic;
    }


    public SampleCategory getSampleCategory() {
        return sampleCategory;
    }

    public void setSampleCategory(final SampleCategory sampleCategory) {
        this.sampleCategory = sampleCategory;
    }


    public AssessmentPackage getSelectedAssessmentPackage() {
        return selectedAssessmentPackage;
    }

    public void setSelectedAssessmentPackage(final AssessmentPackage selectedAssessmentPackage) {
        this.selectedAssessmentPackage = selectedAssessmentPackage;
    }


    public List<AssessmentPackage> getAssessmentPackages() {
        if (assessmentPackages==null) {
            assessmentPackages = new ArrayList<AssessmentPackage>();
        }
        return assessmentPackages;
    }


    public List<Delivery> getDeliveries() {
        if (deliveries==null) {
            deliveries = new ArrayList<Delivery>();
        }
        return deliveries;
    }


    public String getLtiResultOutcomeIdentifier() {
        return ltiResultOutcomeIdentifier;
    }

    public void setLtiResultOutcomeIdentifier(final String ltiResultOutcomeIdentifier) {
        this.ltiResultOutcomeIdentifier = ltiResultOutcomeIdentifier;
    }


    public Double getLtiResultMinimum() {
        return ltiResultMinimum;
    }

    public void setLtiResultMinimum(final Double ltiResultMinimum) {
        this.ltiResultMinimum = ltiResultMinimum;
    }


    public Double getLtiResultMaximum() {
        return ltiResultMaximum;
    }

    public void setLtiResultMaximum(final Double ltiResultMaximum) {
        this.ltiResultMaximum = ltiResultMaximum;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(aid=" + aid
                + ",version=" + version
                + ",assessmentType=" + assessmentType
                + ",isPublic=" + isPublic
                + ",packageImportVersion=" + packageImportVersion
                + ",ltiResultOutcomeIdentifier=" + ltiResultOutcomeIdentifier
                + ",ltiResultMinimum=" + ltiResultMinimum
                + ",ltiResultMaximum=" + ltiResultMaximum
                + ")";
    }
}
