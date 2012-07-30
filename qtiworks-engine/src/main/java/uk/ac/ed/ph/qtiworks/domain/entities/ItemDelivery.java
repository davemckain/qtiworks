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

import uk.ac.ed.ph.jqtiplus.internal.util.BeanToStringOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.PropertyOptions;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;

import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

/**
 * Corresponds to a particular "delivery" of an {@link AssessmentItem} to a group of candidates.
 * <p>
 * This is going to be very simple in the first instance, but will get more complicated in future.
 *
 * TODO: We'll eventually need one of these for a test, and probably an entity superclass containing
 * the common aspects of both types of deliveries.
 *
 * @author David McKain
 */
@Entity
@Table(name="item_deliveries")
@SequenceGenerator(name="itemDeliverySequence", sequenceName="item_delivery_sequence", initialValue=1, allocationSize=5)
@NamedQueries({
    @NamedQuery(name="ItemDelivery.getForAssessmentPackage",
            query="SELECT d"
                + "  FROM ItemDelivery d"
                + "  WHERE d.assessmentPackage = :assessmentPackage"),
    @NamedQuery(name="ItemDelivery.getForAssessmentPackageAndType",
            query="SELECT d"
                + "  FROM ItemDelivery d"
                + "  WHERE d.assessmentPackage = :assessmentPackage"
                + "    AND d.itemDeliveryType = :itemDeliveryType")
})
public class ItemDelivery implements BaseEntity, TimestampedOnCreation {

    private static final long serialVersionUID = 7693569112981982946L;

    //------------------------------------------------------------
    // These properties would probably apply to both items and tests

    @Id
    @GeneratedValue(generator="itemDeliverySequence")
    @Column(name="did")
    private Long id;

    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="apid")
    private AssessmentPackage assessmentPackage;

    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="dsid")
    private ItemDeliverySettings itemDeliverySettings;

    @Basic(optional=false)
    @Column(name="type", updatable=false, length=15)
    @Enumerated(EnumType.STRING)
    private ItemDeliveryType itemDeliveryType;

    @NotNull
    @Size(min=1)
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=false)
    @Column(name="title")
    private String title;

    /** Available to candidates? */
    @Basic(optional=false)
    @Column(name="open")
    private boolean open;

    /** LTI enabled? */
    @Basic(optional=false)
    @Column(name="lti_enabled")
    private boolean ltiEnabled;

    /** LTI consumer key (if used) */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="lti_consumer_key", updatable=false, unique=false)
    private String ltiConsumerKey;

    /** LTI consumer secret (if used) */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="lti_consumer_secret", updatable=false, unique=false)
    private String ltiConsumerSecret;

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
        return ObjectUtilities.safeClone(creationTime);
    }

    @Override
    public void setCreationTime(final Date creationTime) {
        this.creationTime = ObjectUtilities.safeClone(creationTime);
    }


    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public AssessmentPackage getAssessmentPackage() {
        return assessmentPackage;
    }

    public void setAssessmentPackage(final AssessmentPackage assessmentPackage) {
        this.assessmentPackage = assessmentPackage;
    }


    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public ItemDeliverySettings getItemDeliverySettings() {
        return itemDeliverySettings;
    }

    public void setItemDeliverySettings(final ItemDeliverySettings itemDeliverySettings) {
        this.itemDeliverySettings = itemDeliverySettings;
    }


    public ItemDeliveryType getItemDeliveryType() {
        return itemDeliveryType;
    }

    public void setItemDeliveryType(final ItemDeliveryType itemDeliveryType) {
        this.itemDeliveryType = itemDeliveryType;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }


    public boolean isOpen() {
        return open;
    }

    public void setOpen(final boolean open) {
        this.open = open;
    }


    public boolean isLtiEnabled() {
        return ltiEnabled;
    }

    public void setLtiEnabled(final boolean ltiEnabled) {
        this.ltiEnabled = ltiEnabled;
    }


    public String getLtiConsumerKey() {
        return ltiConsumerKey;
    }

    public void setLtiConsumerKey(final String ltiConsumerKey) {
        this.ltiConsumerKey = ltiConsumerKey;
    }


    public String getLtiConsumerSecret() {
        return ltiConsumerSecret;
    }

    public void setLtiConsumerSecret(final String ltiConsumerSecret) {
        this.ltiConsumerSecret = ltiConsumerSecret;
    }


    //------------------------------------------------------------

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
