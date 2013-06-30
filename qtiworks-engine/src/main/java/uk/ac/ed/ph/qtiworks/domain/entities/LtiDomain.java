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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Encapsulates the data (e.g. credentials) for an LTI domain.
 *
 * @author David McKain
 */
@Entity
@Table(name="lti_domains")
@SequenceGenerator(name="ltiDomainSequence", sequenceName="lti_domain_sequence", initialValue=1, allocationSize=1)
@NamedQueries({
    @NamedQuery(name="LtiDomain.findByConsumerKey",
            query="SELECT ld"
                + "  FROM LtiDomain ld"
                + "  WHERE ld.consumerKey = :consumerKey"),
    @NamedQuery(name="LtiDomain.getAll",
            query="SELECT ld"
                + "  FROM LtiDomain ld"
                + "  ORDER BY ld.ldid")
})
public class LtiDomain implements BaseEntity, TimestampedOnCreation {

    private static final long serialVersionUID = 2268430819509058464L;

    @Id
    @GeneratedValue(generator="ltiDomainSequence")
    @Column(name="ldid")
    private Long ldid;

    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    /** LTI consumer key (must be unique) */
    @Basic(optional=false)
    @Column(name="consumer_key", length=DomainConstants.LTI_TOKEN_LENGTH, updatable=false, unique=true)
    private String consumerKey;

    /** LTI consumer secret (if used) */
    @Basic(optional=false)
    @Column(name="consumer_secret", length=DomainConstants.LTI_SECRET_LENGTH, updatable=true, unique=false)
    private String consumerSecret;

    @Basic(optional=false)
    @Column(name="disabled")
    private boolean disabled;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return ldid;
    }

    @Override
    public void setId(final Long id) {
        this.ldid = id;
    }


    @Override
    public Date getCreationTime() {
        return ObjectUtilities.safeClone(creationTime);
    }

    @Override
    public void setCreationTime(final Date creationTime) {
        this.creationTime = ObjectUtilities.safeClone(creationTime);
    }


    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(final String consumerKey) {
        this.consumerKey = consumerKey;
    }


    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(final String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }


    public boolean isDisabled() {
        return disabled;
    }


    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(ldid=" + ldid
                + ",consumerKey=" + consumerKey
                + ",consumerSecret=" + consumerSecret
                + ",disabled=" + disabled
                + ")";
    }

    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof LtiDomain)) {
            return false;
        }
        final LtiDomain other = (LtiDomain) obj;
        return ObjectUtilities.nullSafeEquals(consumerKey, other.consumerKey);
    }
}
