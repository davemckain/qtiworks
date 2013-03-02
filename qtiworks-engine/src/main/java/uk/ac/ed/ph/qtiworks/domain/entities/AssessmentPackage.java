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
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

/**
 * Represents an {@link AssessmentItem} or {@link AssessmentTest} stored
 * the system. All such objects are treated as IMS Content Packages.
 *
 * @author David McKain
 */
@Entity
@Table(name="assessment_packages")
@SequenceGenerator(name="assessmentPackageSequence", sequenceName="assessment_package_sequence", initialValue=1, allocationSize=1)
@NamedQueries({
    @NamedQuery(name="AssessmentPackage.getCurrentForAssessment",
            query="SELECT ap"
                + "  FROM AssessmentPackage ap"
                + "  WHERE ap.assessment = :assessment"
                + "    AND ap.importVersion = ("
                + "      SELECT MAX(importVersion) FROM AssessmentPackage apInner"
                + "        WHERE apInner.assessment = :assessment"
                + "  )"),
    @NamedQuery(name="AssessmentPackage.getForSampleCategory",
            query="SELECT ap"
                + "  FROM AssessmentPackage ap"
                + "  WHERE ap.assessment.sampleCategory = :sampleCategory"
                + "  ORDER BY ap.assessment.creationTime")
})
public class AssessmentPackage implements BaseEntity, TimestampedOnCreation {

    private static final long serialVersionUID = -4330181851974184912L;

    @Id
    @GeneratedValue(generator="assessmentPackageSequence")
    @Column(name="apid")
    private Long apid;

    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="aid", updatable=false)
    private Assessment assessment;

    @Basic(optional=false)
    @Column(name="import_version", updatable=false)
    private Long importVersion;

    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    /** {@link User} who imported this Assessment */
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="importer_uid", updatable=false)
    private User importer;

    /** Item or Test? */
    @Basic(optional=false)
    @Column(name="type", updatable=false, length=15)
    @Enumerated(EnumType.STRING)
    private AssessmentObjectType assessmentType;

    /** Content Package, standalone or bundled sample? */
    @Basic(optional=false)
    @Column(name="import_type", length=20)
    @Enumerated(EnumType.STRING)
    private AssessmentPackageImportType importType;

    /**
     * Base path where this package's files belong.
     * <p>
     * This must be set to null for bundled packages, which are instead located
     * within the ClassPath.
     * <p>
     * When not null, this path defines a private sandbox for the package.
     */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="sandbox_path")
    private String sandboxPath;

    /** Href of the assessment item/test within this package */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=false)
    @Column(name="assessment_href")
    private String assessmentHref;

    /** Has this item/test been validated? */
    @Basic(optional=false)
    @Column(name="validated")
    private boolean validated;

    /** If validated, was this item/test found to be valid? */
    @Basic(optional=false)
    @Column(name="valid")
    private boolean valid;

    /** Hrefs of all QTI XML file resources declared within this package */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name="assessment_package_qti_files", joinColumns=@JoinColumn(name="apid"))
    @Column(name="href")
    private Set<String> qtiFileHrefs;

    /** Hrefs of all safe (non-QTI) file resources declared within this package */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name="assessment_package_safe_files", joinColumns=@JoinColumn(name="apid"))
    @Column(name="href")
    private Set<String> safeFileHrefs;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return apid;
    }

    @Override
    public void setId(final Long id) {
        this.apid = id;
    }


    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(final Assessment assessment) {
        this.assessment = assessment;
    }


    public Long getImportVersion() {
        return importVersion;
    }

    public void setImportVersion(final Long importVersion) {
        this.importVersion = importVersion;
    }


    @Override
    public Date getCreationTime() {
        return ObjectUtilities.safeClone(creationTime);
    }

    @Override
    public void setCreationTime(final Date creationTime) {
        this.creationTime = ObjectUtilities.safeClone(creationTime);
    }


    public User getImporter() {
        return importer;
    }

    public void setImporter(final User importer) {
        this.importer = importer;
    }


    public AssessmentObjectType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(final AssessmentObjectType assessmentType) {
        this.assessmentType = assessmentType;
    }


    public AssessmentPackageImportType getImportType() {
        return importType;
    }

    public void setImportType(final AssessmentPackageImportType importType) {
        this.importType = importType;
    }


    public String getSandboxPath() {
        return sandboxPath;
    }

    public void setSandboxPath(final String sandboxPath) {
        this.sandboxPath = sandboxPath;
    }


    public String getAssessmentHref() {
        return assessmentHref;
    }

    public void setAssessmentHref(final String assessmentHref) {
        this.assessmentHref = assessmentHref;
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


    public Set<String> getQtiFileHrefs() {
        return qtiFileHrefs;
    }

    public void setQtiFileHrefs(final Set<String> qtiFileHrefs) {
        this.qtiFileHrefs = qtiFileHrefs;
    }


    public Set<String> getSafeFileHrefs() {
        return safeFileHrefs;
    }

    public void setSafeFileHrefs(final Set<String> fileHrefs) {
        this.safeFileHrefs = fileHrefs;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(apid=" + apid
                + ")";
    }

}
