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

import uk.ac.ed.ph.jqtiplus.JqtiPlus;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

/**
 * Specifies settings controlling the delivery of an {@link AssessmentItem} or {@link AssessmentTest}
 * to a group of candidates.
 *
 * @author David McKain
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name="delivery_settings")
@SequenceGenerator(name="deliverySettingsSequence", sequenceName="delivery_settings_sequence", initialValue=1, allocationSize=1)
@NamedQueries({
    @NamedQuery(name="DeliverySettings.getAllPublicSettingsForType",
            query="SELECT ds"
                + "  FROM DeliverySettings ds"
                + "  WHERE ds.isPublic IS TRUE"
                + "  AND ds.assessmentType = :assessmentType"
                + "  ORDER BY creationTime, id"),
    @NamedQuery(name="DeliverySettings.getForOwner",
            query="SELECT ds"
                + "  FROM DeliverySettings ds"
                + "  WHERE ds.owner = :user"
                + "  ORDER BY creationTime, dsid"),
    @NamedQuery(name="DeliverySettings.getForOwnerAndType",
            query="SELECT ds"
                + "  FROM DeliverySettings ds"
                + "  WHERE ds.owner = :user"
                + "  AND ds.assessmentType = :assessmentType"
                + "  ORDER BY creationTime, dsid"),
    @NamedQuery(name="DeliverySettings.countForOwnerAndType",
            query="SELECT COUNT(ds)"
                + "  FROM DeliverySettings ds"
                + "  WHERE ds.owner = :user"
                + "  AND ds.assessmentType = :assessmentType")
})
public class DeliverySettings implements BaseEntity, TimestampedOnCreation {

    //------------------------------------------------------------
    // These properties would probably apply to both items and tests

    private static final long serialVersionUID = 2631174138240856511L;

    @Id
    @GeneratedValue(generator="deliverySettingsSequence")
    @Column(name="dsid")
    private Long dsid;

    /** Item or Test? */
    @Basic(optional=false)
    @Column(name="type", updatable=false, length=15)
    @Enumerated(EnumType.STRING)
    private AssessmentObjectType assessmentType;

    /** {@link User} who owns these options */
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="owner_uid", updatable=false)
    private User owner;

    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    /** Owner's title, must be unique within those created by owner */
    @NotNull
    @Size(min=1)
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=false)
    @Column(name="title")
    private String title;

    /** Available to all users */
    @Basic(optional=false)
    @Column(name="public")
    private boolean isPublic;

    //------------------------------------------------------------
    // Settings common to both items and tests

    /** Optional prompt to show to candidates */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="prompt")
    private String prompt;

    /** Author mode includes additional debugging information in the rendering */
    @Basic(optional=false)
    @Column(name="author_mode")
    private boolean authorMode;

    /**
     * If specified, overrides the default number of times that template processing
     * rules should be run on a particular item before reverting to default values.
     * (This is to accommodate <code>templateConstraint</code>.)
     * <p>
     * If non-positive then the {@link JqtiPlus#DEFAULT_TEMPLATE_PROCESSING_LIMIT}
     * is used instead.
     */
    @Basic(optional=false)
    @Column(name="template_processing_limit")
    private int templateProcessingLimit;

    //------------------------------------------------------------

    public DeliverySettings() {
        /* (Don't use this in code - required when creating instances by reflection) */
    }

    protected DeliverySettings(final AssessmentObjectType assessmentType) {
        this.assessmentType = assessmentType;
    }


    //------------------------------------------------------------

    @Override
    public Long getId() {
        return dsid;
    }

    @Override
    public void setId(final Long id) {
        this.dsid = id;
    }


    public AssessmentObjectType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(final AssessmentObjectType assessmentType) {
        this.assessmentType = assessmentType;
    }


    public User getOwner() {
        return owner;
    }

    public void setOwner(final User owner) {
        this.owner = owner;
    }


    @Override
    public Date getCreationTime() {
        return ObjectUtilities.safeClone(creationTime);
    }

    @Override
    public void setCreationTime(final Date creationTime) {
        this.creationTime = ObjectUtilities.safeClone(creationTime);
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }


    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(final boolean isPublic) {
        this.isPublic = isPublic;
    }

    //------------------------------------------------------------

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(final String prompt) {
        this.prompt = prompt;
    }


    public boolean isAuthorMode() {
        return authorMode;
    }

    public void setAuthorMode(final boolean authorMode) {
        this.authorMode = authorMode;
    }


    public int getTemplateProcessingLimit() {
        return templateProcessingLimit;
    }


    public void setTemplateProcessingLimit(final int templateProcessingLimit) {
        this.templateProcessingLimit = templateProcessingLimit;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(dsid=" + dsid
                + ")";
    }
}
