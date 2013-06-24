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

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

/**
 * FIXME: Document this!
 *
 * @author David McKain
 */
@Entity
@Table(name="lti_resources")
@SequenceGenerator(name="ltiResourceSequence", sequenceName="lti_resource_sequence", initialValue=1, allocationSize=1)
public class LtiResource implements BaseEntity, TimestampedOnCreation {

    private static final long serialVersionUID = -5661266580944124938L;

    @Id
    @GeneratedValue(generator="ltiResourceSequence")
    @Column(name="lrid")
    private Long lrid;

    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    /** {@link User} who created this */
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="creator_uid", updatable=false)
    private User creatorUser;

    /** {@link LtiDomain} in which this resource exists */
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="ldid", updatable=false)
    private LtiDomain ltiDomain;

    /** {@link LtiContext} for this resource, if provided */
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @JoinColumn(name="lcid", updatable=false)
    private LtiContext ltiContext;

    @Basic(optional=false)
    @Column(name="resource_link_id", length=DomainConstants.LTI_TOKEN_LENGTH, updatable=false)
    private String resourceLinkId;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="resource_link_title", length=DomainConstants.LTI_TOKEN_LENGTH, updatable=false)
    private String resourceLinkTitle;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="context_title", updatable=false)
    private String resourceLinkDescription;

    @Lob
    @Basic(optional=true)
    @Column(name="resource_link_description", updatable=false)
    @Type(type="org.hibernate.type.TextType")

    @ManyToOne(optional=true, fetch=FetchType.EAGER)
    @JoinColumn(name="did")
    private Delivery delivery;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return lrid;
    }

    @Override
    public void setId(final Long id) {
        this.lrid = id;
    }


    @Override
    public Date getCreationTime() {
        return ObjectUtilities.safeClone(creationTime);
    }

    @Override
    public void setCreationTime(final Date creationTime) {
        this.creationTime = ObjectUtilities.safeClone(creationTime);
    }


    public User getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(final User creatorUser) {
        this.creatorUser = creatorUser;
    }


    public LtiDomain getLtiDomain() {
        return ltiDomain;
    }

    public void setLtiDomain(final LtiDomain ltiDomain) {
        this.ltiDomain = ltiDomain;
    }


    public LtiContext getLtiContext() {
        return ltiContext;
    }

    public void setLtiContext(final LtiContext ltiContext) {
        this.ltiContext = ltiContext;
    }


    public String getResourceLinkId() {
        return resourceLinkId;
    }

    public void setResourceLinkId(final String resourceLinkId) {
        this.resourceLinkId = resourceLinkId;
    }


    public String getResourceLinkTitle() {
        return resourceLinkTitle;
    }

    public void setResourceLinkTitle(final String resourceLinkTitle) {
        this.resourceLinkTitle = resourceLinkTitle;
    }


    public String getResourceLinkDescription() {
        return resourceLinkDescription;
    }

    public void setResourceLinkDescription(final String resourceLinkDescription) {
        this.resourceLinkDescription = resourceLinkDescription;
    }


    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(final Delivery delivery) {
        this.delivery = delivery;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(lrid=" + lrid
                + ",resourceLinkId=" + resourceLinkId
                + ",resourceLinkTitle=" + resourceLinkTitle
                + ",resourceLinkDescription=" + resourceLinkDescription
                + ")";
    }
}
