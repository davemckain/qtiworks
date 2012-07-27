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
package uk.ac.ed.ph.qtiworks.domain.entities.lti;

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.entities.BaseEntity;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserType;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Represents an LTI user
 *
 * @author David McKain
 */
@Entity
@Table(name="lti_users")
public class LtiUser extends User implements BaseEntity, Comparable<LtiUser> {

    private static final long serialVersionUID = 7821803746245696405L;

    @Basic(optional=false)
    @Column(name="session_id", unique=true)
    private String sessionId;

    /**
     * LTI <code>user_id</code> column. Intended to be a primary key but uniqueness is
     * only guaranteed within a single Tool Consumer, so we don't have uniqueness here.
     */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=false)
    @Column(name="lti_user_id", updatable=false, unique=false)
    private String ltiUserId;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="lis_given_name", updatable=false, unique=false)
    private String lisGivenName;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="lis_family_name", updatable=false, unique=false)
    private String lisFamilyName;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="lis_full_name", updatable=false, unique=false)
    private String lisFullName;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="lis_contact_email_primary", updatable=false, unique=false)
    private String lisContactEmailPrimary;

    //------------------------------------------------------------

    public LtiUser() {
        super(UserType.LTI);
    }

    //------------------------------------------------------------

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }


    public String getLtiUserId() {
        return ltiUserId;
    }

    public void setLtiUserId(final String ltiUserId) {
        this.ltiUserId = ltiUserId;
    }


    public String getLisGivenName() {
        return lisGivenName;
    }

    public void setLisGivenName(final String lisGivenName) {
        this.lisGivenName = lisGivenName;
    }


    public String getLisFamilyName() {
        return lisFamilyName;
    }

    public void setLisFamilyName(final String lisFamilyName) {
        this.lisFamilyName = lisFamilyName;
    }


    public String getLisFullName() {
        return lisFullName;
    }

    public void setLisFullName(final String lisFullName) {
        this.lisFullName = lisFullName;
    }


    public String getLisContactEmailPrimary() {
        return lisContactEmailPrimary;
    }

    public void setLisContactEmailPrimary(final String lisContactEmailPrimary) {
        this.lisContactEmailPrimary = lisContactEmailPrimary;
    }

    //------------------------------------------------------------

    @Override
    public String getBusinessKey() {
        ensureSessionId(this);
        return "lti/" + sessionId + "/" + ltiUserId;
    }

    //------------------------------------------------------------

    @Override
    public final int compareTo(final LtiUser o) {
        ensureSessionId(this);
        ensureSessionId(o);
        return sessionId.compareTo(o.sessionId);
    }

    private void ensureSessionId(final LtiUser user) {
        if (user.sessionId==null) {
            throw new QtiWorksRuntimeException("Current logic branch requires sessionId to be non-null on " + user);
        }
    }
}
