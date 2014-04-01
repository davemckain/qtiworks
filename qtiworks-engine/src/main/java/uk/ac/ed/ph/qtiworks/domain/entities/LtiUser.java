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

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.web.lti.LtiLaunchService;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Extends the {@link User} entity with additional information for users created via an LTI launch.
 *
 * @author David McKain
 */
@Entity
@Table(name="lti_users")
@NamedQueries({
    @NamedQuery(name="LtiUser.findByLogicalKey",
            query="SELECT u"
                + "  FROM LtiUser u"
                + "  WHERE u.logicalKey = :logicalKey"),
    @NamedQuery(name="LtiUser.findByLtiDomainLtiUserIdAndUserRole",
            query="SELECT u"
                + "  FROM LtiUser u"
                + "  WHERE u.ltiDomain = :ltiDomain"
                + "    AND u.ltiUserId = :ltiUserId"
                + "    AND u.userRole = :userRole"),
    @NamedQuery(name="LtiUser.findByDeliveryAndLtiUserId",
            query="SELECT u"
                + "  FROM LtiUser u"
                + "  WHERE u.delivery = :delivery"
                + "    AND u.ltiUserId = :ltiUserId"),
    @NamedQuery(name="LtiUser.getForUserRole",
            query="SELECT u"
                + "  FROM LtiUser u"
                + "  WHERE u.userRole = :userRole"),
    @NamedQuery(name="LtiUser.getCandidatesForLinkDelivery",
            query="SELECT u"
                + "  FROM LtiUser u"
                + "  WHERE u.delivery = :delivery"
                + "    AND u.ltiLaunchType = 'LINK'"
                + "    AND u.userRole = 'CANDIDATE'"),
    @NamedQuery(name="LtiUser.deleteCandidatesWithNoSessions",
            query="DELETE"
                + "  FROM LtiUser u"
                + "  WHERE u.userRole = 'CANDIDATE'"
                + "    AND u.candidateSessions IS EMPTY")
})
public class LtiUser extends User implements BaseEntity, Comparable<LtiUser> {

    private static final long serialVersionUID = 7821803746245696405L;

    /**
     * Logical key used for an LTI user.
     * <p>
     * (See {@link LtiLaunchService} for details)
     */
    @Basic(optional=false)
    @Column(name="logical_key", updatable=false, unique=true, length=DomainConstants.LTI_USER_LOGICAL_KEY_MAX_LENGTH)
    private String logicalKey;

    /** LTI <code>user_id</code> launch parameter. (Spec says this is recommended) */
    @Basic(optional=true)
    @Column(name="lti_user_id", updatable=false, unique=false, length=DomainConstants.LTI_TOKEN_MAX_LENGTH)
    private String ltiUserId;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="lis_full_name", updatable=false, unique=false)
    private String lisFullName;

    /**
     * Indicates which type of LTI launch this {@link LtiUser} has been created for.
     */
    @Basic(optional=false)
    @Column(name="lti_launch_type", updatable=false, length=6)
    @Enumerated(EnumType.STRING)
    private LtiLaunchType ltiLaunchType;

    /**
     * For {@link LtiUser}s created by domain-level launches, this indicates which {@link LtiDomain}
     * this user belongs to. This will be non-null for {@link LtiLaunchType#DOMAIN}
     *  and null for {@link LtiUser}s with {@link LtiLaunchType#LINK}
     */
    @ManyToOne(optional=true)
    @JoinColumn(name="ldid")
    private LtiDomain ltiDomain;

    /**
     * For {@link LtiUser}s created by link-level launches, the indicates the {@link Delivery}
     * that the user belongs to. This will be non-null for {@link LtiLaunchType#LINK}
     * and null for {@link LtiUser}s with {@link LtiLaunchType#DOMAIN}
     */
    @ManyToOne(optional=true)
    @JoinColumn(name="did")
    private Delivery delivery;

    //------------------------------------------------------------

    public LtiUser() {
        super(UserType.LTI, null);
    }

    //------------------------------------------------------------

    public String getLogicalKey() {
        return logicalKey;
    }

    public void setLogicalKey(final String logicalKey) {
        this.logicalKey = logicalKey;
    }


    public String getLtiUserId() {
        return ltiUserId;
    }

    public void setLtiUserId(final String ltiUserId) {
        this.ltiUserId = ltiUserId;
    }


    public String getLisFullName() {
        return lisFullName;
    }

    public void setLisFullName(final String lisFullName) {
        this.lisFullName = lisFullName;
    }


    public LtiLaunchType getLtiLaunchType() {
        return ltiLaunchType;
    }

    public void setLtiLaunchType(final LtiLaunchType ltiLaunchType) {
        this.ltiLaunchType = ltiLaunchType;
    }


    public LtiDomain getLtiDomain() {
        return ltiDomain;
    }

    public void setLtiDomain(final LtiDomain ltiDomain) {
        this.ltiDomain = ltiDomain;
    }


    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(final Delivery delivery) {
        this.delivery = delivery;
    }

    //------------------------------------------------------------

    @Override
    public String getBusinessKey() {
        ensureLogicalKey(this);
        return "lti/" + logicalKey;
    }

    //------------------------------------------------------------

    @Override
    public final int compareTo(final LtiUser o) {
        ensureLogicalKey(this);
        ensureLogicalKey(o);
        return logicalKey.compareTo(o.logicalKey);
    }

    private void ensureLogicalKey(final LtiUser user) {
        if (user.logicalKey==null) {
            throw new QtiWorksRuntimeException("Current logic branch requires logicalKey to be non-null on " + user);
        }
    }
}
