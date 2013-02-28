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

import uk.ac.ed.ph.jqtiplus.internal.util.BeanToStringOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.PropertyOptions;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import java.util.Date;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

/**
 * Corresponds to a particular "delivery" of an {@link AssessmentItem} or
 * {@link AssessmentTest} to a group of candidates.
 * <p>
 * This is going to be very simple in the first instance, but will get more complicated in future.
 *
 * @author David McKain
 */
@Entity
@Table(name="deliveries")
@SequenceGenerator(name="deliverySequence", sequenceName="delivery_sequence", initialValue=1, allocationSize=1)
@NamedQueries({
    @NamedQuery(name="Delivery.getForAssessment",
            query="SELECT d"
                + "  FROM Delivery d"
                + "  WHERE d.assessment = :assessment"
                + "  ORDER BY d.id"),
    @NamedQuery(name="Delivery.getForAssessmentAndType",
            query="SELECT d"
                + "  FROM Delivery d"
                + "  WHERE d.assessment = :assessment"
                + "    AND d.deliveryType = :deliveryType"
                + "  ORDER BY d.id"),
    @NamedQuery(name="Delivery.countForAssessmentAndType",
            query="SELECT COUNT(*)"
                + "  FROM Delivery d"
                + "  WHERE d.assessment = :assessment"
                + "    AND d.deliveryType = :deliveryType"),
    @NamedQuery(name="Delivery.getUsingSettings",
            query="SELECT d"
                + "  FROM Delivery d"
                + "  WHERE d.deliverySettings = :deliverySettings"
                + "  ORDER BY d.id"),
    @NamedQuery(name="Delivery.countUsingSettings",
            query="SELECT COUNT(*)"
                + "  FROM Delivery d"
                + "  WHERE d.deliverySettings = :deliverySettings"),
    @NamedQuery(name="Delivery.getForTypeCreatedBefore",
            query="SELECT d"
                + "  FROM Delivery d"
                + "  WHERE d.deliveryType = :deliveryType"
                + "    AND d.creationTime < :creationTime"),
    @NamedQuery(name="Delivery.getForOwnerAndTypeCreatedBefore",
            query="SELECT d"
                + "  FROM Delivery d"
                + "  WHERE d.assessment.owner = :owner"
                + "    AND d.deliveryType = :deliveryType"
                + "    AND d.creationTime < :creationTime")
})
public class Delivery implements BaseEntity, TimestampedOnCreation {

    private static final long serialVersionUID = 7693569112981982946L;

    //------------------------------------------------------------
    // These properties would probably apply to both items and tests

    @Id
    @GeneratedValue(generator="deliverySequence")
    @Column(name="did")
    private Long did;

    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="aid")
    private Assessment assessment;

    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="dsid")
    private DeliverySettings deliverySettings;

    @Basic(optional=false)
    @Column(name="type", updatable=false, length=15)
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

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

    /**
     * LTI consumer key "token" (if used)
     * The full key will be a string of the form <code>id-TOKEN</code> as this makes it easier to
     * look the keys up.
     */
    @Basic(optional=true)
    @Column(name="lti_consumer_key_token", length=DomainConstants.LTI_TOKEN_LENGTH, updatable=false, unique=false)
    private String ltiConsumerKeyToken;

    /** LTI consumer secret (if used) */
    @Basic(optional=true)
    @Column(name="lti_consumer_secret", length=DomainConstants.LTI_TOKEN_LENGTH, updatable=false, unique=false)
    private String ltiConsumerSecret;

    /** (Currently used for cascading deletion only - upgrade if required) */
    @SuppressWarnings("unused")
    @OneToMany(mappedBy="delivery", cascade=CascadeType.REMOVE)
    private Set<CandidateSession> candidateSessions;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return did;
    }

    @Override
    public void setId(final Long id) {
        this.did = id;
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
    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(final Assessment assessment) {
        this.assessment = assessment;
    }


    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public DeliverySettings getDeliverySettings() {
        return deliverySettings;
    }

    public void setDeliverySettings(final DeliverySettings deliverySettings) {
        this.deliverySettings = deliverySettings;
    }


    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(final DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
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


    public String getLtiConsumerKeyToken() {
        return ltiConsumerKeyToken;
    }

    public void setLtiConsumerKeyToken(final String ltiConsumerKeyToken) {
        this.ltiConsumerKeyToken = ltiConsumerKeyToken;
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
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(did=" + did
                + ")";
    }
}
