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
import javax.persistence.UniqueConstraint;

/**
 * Represents an LTI/OAuth nonce. We're storing these in the persistence layer,
 * even though they're so simple they could be done in other ways.
 * <p>
 * Developer note: The ID of a {@link LtiNonce} is generally referred to as an
 * <code>lnid</code> in the code. This is also used as the name of the primary key column
 * in the database mappings.
 *
 * @author David McKain
 */
@Entity
@Table(name="lti_nonces",
    uniqueConstraints=@UniqueConstraint(columnNames={"consumer_key", "nonce"})
)
@SequenceGenerator(name="ltiNonceSequence", sequenceName="lti_nonce_sequence", initialValue=1, allocationSize=1)
@NamedQueries({
    @NamedQuery(name="LtiNonce.findByNonceAndConsumerKey",
            query="SELECT ln"
                + "  FROM LtiNonce ln"
                + "  WHERE ln.nonce = :nonce"
                + "    AND ln.consumerKey = :consumerKey"),
    @NamedQuery(name="LtiNonce.deleteOldNonces",
            query="DELETE"
                + "  FROM LtiNonce ln"
                + "  WHERE ln.messageTimestamp < :threshold")
})
public class LtiNonce implements BaseEntity {

    private static final long serialVersionUID = -5661266580944124938L;

    @Id
    @GeneratedValue(generator="ltiNonceSequence")
    @Column(name="lnid")
    private Long lnid;

    @Basic(optional=false)
    @Column(name="message_timestamp", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date messageTimestamp;

    @Basic(optional=false)
    @Column(name="consumer_key", updatable=false, length=DomainConstants.LTI_TOKEN_MAX_LENGTH)
    private String consumerKey;

    @Basic(optional=false)
    @Column(name="nonce", updatable=false, length=DomainConstants.OAUTH_NONCE_MAX_LENGTH)
    private String nonce;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return lnid;
    }

    @Override
    public void setId(final Long id) {
        this.lnid = id;
    }


    public Date getMessageTimestamp() {
        return ObjectUtilities.safeClone(messageTimestamp);
    }

    public void setMessageTimestamp(final Date messageTimestamp) {
        this.messageTimestamp = ObjectUtilities.safeClone(messageTimestamp);
    }


    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(final String consumerKey) {
        this.consumerKey = consumerKey;
    }


    public String getNonce() {
        return nonce;
    }

    public void setNonce(final String nonce) {
        this.nonce = nonce;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(lnid=" + lnid
                + ",nonce=" + nonce
                + ")";
    }
}
