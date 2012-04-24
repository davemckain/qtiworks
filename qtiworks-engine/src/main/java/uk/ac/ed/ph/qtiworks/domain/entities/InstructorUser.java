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

import uk.ac.ed.ph.qtiworks.domain.DomainGlobals;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents an "instructor" user
 *
 * @author David McKain
 */
@Entity
@Table(name="instructor_users")
@SequenceGenerator(name="instructorUserSequence", sequenceName="instructor_user_sequence", initialValue=1000, allocationSize=10)
public class InstructorUser extends BusinessKeyBaseEntity<InstructorUser, String> implements Serializable {

    private static final long serialVersionUID = 7821803746245696405L;

    @Id
    @GeneratedValue(generator="instructorUserSequence")
    @Column(name="id")
    private Long id;

    @Basic(optional=false)
    @Column(name="login_name", length=DomainGlobals.LOGIN_NAME_MAX_LENGTH, updatable=false, unique=true)
    private String loginName;

    @Basic(optional=false)
    @Column(name="disabled", updatable=true)
    private boolean disabled;

    @Basic(optional=false)
    @Column(name="sysadmin", updatable=true)
    private boolean sysAdmin;

    @Basic(optional=false)
    @Column(name="email_address", length=DomainGlobals.EMAIL_ADDRESS_MAX_LENGTH)
    private String emailAddress;

    @Basic(optional=false)
    @Column(name="pasword_digest", length=DomainGlobals.SHA1_DIGEST_LENGTH)
    private String passwordDigest;

    @Basic(optional=false)
    @Column(name="first_name",length=DomainGlobals.NAME_COMPONENT_MAX_LENGTH)
    private String firstName;

    @Basic(optional=false)
    @Column(name="last_name",length=DomainGlobals.NAME_COMPONENT_MAX_LENGTH)
    private String lastName;


    @Override
    protected Class<InstructorUser> getEntityClass() {
        return InstructorUser.class;
    }

    @Override
    protected String getBusinessKey() {
        return loginName;
    }


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }


    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(final String loginName) {
        this.loginName = loginName;
    }


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }


    public String getPasswordDigest() {
        return passwordDigest;
    }

    public void setPasswordDigest(final String passwordDigest) {
        this.passwordDigest = passwordDigest;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }


    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }


    public boolean isSysAdmin() {
        return sysAdmin;
    }

    public void setSysAdmin(final boolean sysAdmin) {
        this.sysAdmin = sysAdmin;
    }
}
