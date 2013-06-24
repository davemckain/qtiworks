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
 * Encapsulates the context information for a particular LTI (domain) launch.
 * <p>
 * This is used to establish shared access to resources created by different users
 * within the same context. Instances of this entity are only constructed if context
 * information is passed.
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
                + "    AND lc.contextId = :contextId")
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

    /** Corresponds to the (recommended) LTI <code>context_id</code> parameter */
    @Basic(optional=false)
    @Column(name="context_id", updatable=false)
    private String contextId;

    /** Corresponds to the (recommended) LTI <code>context_label</code> parameter */
    @Basic(optional=true)
    @Column(name="context_label", updatable=false)
    private String contextLabel;

    /** Corresponds to the (recommended) LTI <code>context_title</code> parameter */
    @Lob
    @Basic(optional=true)
    @Column(name="context_title", updatable=false)
    @Type(type="org.hibernate.type.TextType")
    private String contextTitle;

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

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(lcid=" + lcid
                + ",contextId=" + contextId
                + ",contextLabel=" + contextLabel
                + ",contextTitle=" + contextTitle
                + ")";
    }
}
