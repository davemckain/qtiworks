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

import uk.ac.ed.ph.qtiworks.domain.DomainConstants;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * Represents an {@link AssessmentItem} or {@link AssessmentTest} and their
 * associated metadata.
 * <p>
 * The actual _content_ of these is represented by an {@link AssessmentPackage}
 *
 * @see AssessmentPackage
 *
 * @author David McKain
 */
@Entity
@Table(name="assessments")
@SequenceGenerator(name="assessmentSequence", sequenceName="assessment_sequence", initialValue=1, allocationSize=1)
@NamedQueries({
    @NamedQuery(name="Assessment.getForOwner",
            query="SELECT a"
                + "  FROM Assessment a"
                + "  WHERE a.owner = :owner"
                + "  ORDER BY creationTime"),
    @NamedQuery(name="Assessment.getForSampleCategory",
            query="SELECT a"
                + "  FROM Assessment a"
                + "  WHERE a.sampleCategory = :sampleCategory"
                + "  ORDER BY creationTime")
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

    /** Total number of {@link AssessmentPackage}s uploaded for this Assessment. */
    @Basic(optional=false)
    @Column(name="package_import_version")
    private Long packageImportVersion;

    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    /** {@link User} who owns this Assessment */
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="owner_uid", updatable=false)
    private User owner;

    /** Item or Test? */
    @Basic(optional=false)
    @Column(name="type", updatable=false, length=15)
    @Enumerated(EnumType.STRING)
    private AssessmentObjectType assessmentType;

    /** Public? (This allows the Assessment to be accessed by anyone) */
    @Basic(optional=false)
    @Column(name="public")
    private boolean isPublic;

    /**
     * Short name for this Assessment.
     * Used for listings and other stuff.
     */
    @Basic(optional=false)
    @Column(name="name", length=DomainConstants.ASSESSMENT_NAME_MAX_LENGTH)
    private String name;

    /**
     * Title of this item/test.
     * Used for listings and other stuff.
     */
    @Basic(optional=false)
    @Column(name="title", length=DomainConstants.ASSESSMENT_TITLE_MAX_LENGTH)
    private String title;

    /** Default {@link DeliverySettings} to use when trying package out */
    @ManyToOne(optional=true)
    @JoinColumn(name="default_dsid")
    private DeliverySettings defaultDeliverySettings;

    /**
     * For sample items, this specifies the category it belongs to. This should be set to null
     * for non-sample items.
     * <p>
     * (This is expected to be temporary)
     */
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @JoinColumn(name="sample_category_id", updatable=false)
    private SampleCategory sampleCategory;

    @OneToMany(mappedBy="assessment", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @OrderBy("apid")
    private List<AssessmentPackage> assessmentPackages;

    /** (Currently used for cascading deletion only) */
    @SuppressWarnings("unused")
    @OneToMany(mappedBy="assessment", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private Set<Delivery> deliveries;

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


    public User getOwner() {
        return owner;
    }

    public void setOwner(final User owner) {
        this.owner = owner;
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


    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }


    public DeliverySettings getDefaultDeliverySettings() {
        return defaultDeliverySettings;
    }

    public void setDefaultDeliverySettings(final DeliverySettings defaultDeliverySettings) {
        this.defaultDeliverySettings = defaultDeliverySettings;
    }


    public SampleCategory getSampleCategory() {
        return sampleCategory;
    }

    public void setSampleCategory(final SampleCategory sampleCategory) {
        this.sampleCategory = sampleCategory;
    }


    public List<AssessmentPackage> getAssessmentPackages() {
        if (assessmentPackages==null) {
            assessmentPackages = new ArrayList<AssessmentPackage>();
        }
        return assessmentPackages;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(aid=" + aid
                + ",name=" + name
                + ")";
    }
}
