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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

/**
 * Encapsulates an LTI "context". (C.f. a "course" within a VLE).
 * <p>
 * This is used to establish "group" ownership of resources created for the data created for a
 * particular LTI resource.
 * <p>
 * Context information is recommended but not mandatory in LTI. When not present, we create a "fake"
 * context based on the <code>resource_link_id</code> to enable the ownership model to work correctly.
 * This means that data created within a particular resource link in this context is visible only
 * to that resource, but it's better than nothing.
 * <p>
 * Developer note: The ID of a {@link LtiContext} is generally referred to as an
 * <code>lcid</code> in the code. This is also used as the name of the primary key column
 * in the database mappings.
 *
 * @author David McKain
 */
@Entity
@Table(name="lti_contexts")
@SequenceGenerator(name="ltiContextSequence", sequenceName="lti_context_sequence", initialValue=1, allocationSize=1)
@NamedQueries({
    @NamedQuery(name="LtiContext.findByConsumerKeyAndContextId",
            query="SELECT lc"
                + "  FROM LtiContext lc"
                + "  WHERE lc.ltiDomain.consumerKey = :consumerKey"
                + "    AND lc.contextId = :contextId"),
    @NamedQuery(name="LtiContext.findByConsumerKeyAndFallbackResourceLinkId",
            query="SELECT lc"
                + "  FROM LtiContext lc"
                + "  WHERE lc.ltiDomain.consumerKey = :consumerKey"
                + "    AND lc.fallbackResourceLinkId = :fallbackResourceLinkId")
})
public class LtiContext implements BaseEntity, TimestampedOnCreation {

    private static final long serialVersionUID = -967019819193536029L;

    @Id
    @GeneratedValue(generator="ltiContextSequence")
    @Column(name="lcid")
    private Long lcid;

    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    /** {@link LtiDomain} owning this context */
    @ManyToOne(optional=true, fetch=FetchType.EAGER)
    @JoinColumn(name="ldid", updatable=false)
    private LtiDomain ltiDomain;

    /**
     * Corresponds to the LTI <code>context_id</code> parameter.
     * <p>
     * This parameter is recommended but not mandatory in LTI.
     */
    @Basic(optional=true)
    @Column(name="context_id", updatable=false, length=DomainConstants.LTI_TOKEN_LENGTH)
    private String contextId;

    /** Corresponds to the (recommended) LTI <code>context_label</code> parameter */
    @Basic(optional=true)
    @Column(name="context_label", updatable=false, length=DomainConstants.LTI_TOKEN_LENGTH)
    private String contextLabel;

    /** Corresponds to the (recommended) LTI <code>context_title</code> parameter */
    @Lob
    @Basic(optional=true)
    @Column(name="context_title", updatable=false)
    @Type(type="org.hibernate.type.TextType")
    private String contextTitle;

    /**
     * Artificial ID used when no <code>context_id</code is set. This is made from the LTI
     * <code>resource_link_id</code>, so narrows the context to a single resource. This is not
     * particularly useful for users, but keeps the ownership model working.
     */
    @Basic(optional=true)
    @Column(name="fallback_resource_link_id", updatable=false, length=DomainConstants.LTI_TOKEN_LENGTH)
    private String fallbackResourceLinkId;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return lcid;
    }

    @Override
    public void setId(final Long id) {
        this.lcid = id;
    }


    @Override
    public Date getCreationTime() {
        return ObjectUtilities.safeClone(creationTime);
    }

    @Override
    public void setCreationTime(final Date creationTime) {
        this.creationTime = ObjectUtilities.safeClone(creationTime);
    }


    public LtiDomain getLtiDomain() {
        return ltiDomain;
    }

    public void setLtiDomain(final LtiDomain ltiDomain) {
        this.ltiDomain = ltiDomain;
    }


    public String getContextId() {
        return contextId;
    }

    public void setContextId(final String contextId) {
        this.contextId = contextId;
    }


    public String getContextLabel() {
        return contextLabel;
    }

    public void setContextLabel(final String contextLabel) {
        this.contextLabel = contextLabel;
    }


    public String getContextTitle() {
        return contextTitle;
    }

    public void setContextTitle(final String contextTitle) {
        this.contextTitle = contextTitle;
    }



    public String getFallbackResourceLinkId() {
        return fallbackResourceLinkId;
    }

    public void setFallbackResourceLinkId(final String fallbackResourceLinkId) {
        this.fallbackResourceLinkId = fallbackResourceLinkId;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(lcid=" + lcid
                + ",contextId=" + contextId
                + ",contextLabel=" + contextLabel
                + ",contextTitle=" + contextTitle
                + ",fallbackResourceLinkId=" + fallbackResourceLinkId
                + ")";
    }

    /**
     * Weaker form of {@link #equals(Object)} that tests simply for "business" equivalence, i.e.
     * having the same key(s)
     */
    public boolean businessEquals(final LtiContext other) {
        return other!=null
                && ltiDomain!=null
                && ltiDomain.businessEquals(other.getLtiDomain())
                && ObjectUtilities.nullSafeEquals(contextId, other.getContextId())
                && ObjectUtilities.nullSafeEquals(fallbackResourceLinkId, other.getFallbackResourceLinkId());
    }
}
