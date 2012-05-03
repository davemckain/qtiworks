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

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Represents an anonymous user in the system
 *
 * @author David McKain
 */
@Entity
@Table(name="anonymous_users")
@NamedQueries({
    /* Looks up the User having the given sessionId */
    @NamedQuery(name="AnonymousUser.findBySessionId",
            query="SELECT u"
                + "  FROM AnonymousUser u"
                +"   WHERE u.sessionId = :sessionId")
})
public class AnonymousUser extends User implements BaseEntity, Comparable<AnonymousUser> {

    private static final long serialVersionUID = 7821803746245696405L;

    @Basic(optional=false)
    @Column(name="session_id", unique=true)
    private String sessionId;

    //------------------------------------------------------------

    public AnonymousUser() {
        super(UserType.ANONYMOUS);
    }

    //------------------------------------------------------------

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }


    //------------------------------------------------------------

    @Override
    public String getBusinessKey() {
        ensureSessionId(this);
        return "anonymous/" + sessionId;
    }

    //------------------------------------------------------------

    @Override
    public final int compareTo(final AnonymousUser o) {
        ensureSessionId(this);
        ensureSessionId(o);
        return sessionId.compareTo(o.sessionId);
    }

    private void ensureSessionId(final AnonymousUser user) {
        if (user.sessionId==null) {
            throw new QtiWorksRuntimeException("Current logic branch requires sessionId to be non-null on " + user);
        }
    }
}
