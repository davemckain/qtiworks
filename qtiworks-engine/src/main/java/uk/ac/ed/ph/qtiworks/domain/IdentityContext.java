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
package uk.ac.ed.ph.qtiworks.domain;

import uk.ac.ed.ph.qtiworks.domain.entities.AnonymousUser;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserType;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

/**
 * Encapsulates the identity of the user who initiated operations being carried
 * out by the current Thread, using a {@link ThreadLocal} to track state.
 * <p>
 * This has two components:
 * <ul>
 *   <li><strong>Underlying Identity</strong> is the actual identity of the caller.</li>
 *   <li><strong>Effective Identity</strong> is normally the same as the underlying identity,
 *     but SysAdmins can change this to imitate other users for debugging purposes.</li>
 * </ul>
 *
 * @author David McKain
 */
public final class IdentityContext {

    private final ThreadLocal<User> currentIdentityThreadLocal = new ThreadLocal<User>();
    private final ThreadLocal<InstructorUser> currentUnderlyingIdentityThreadLocal = new ThreadLocal<InstructorUser>();

    public User getCurrentThreadEffectiveIdentity() {
        return currentIdentityThreadLocal.get();
    }

    public void setCurrentThreadEffectiveIdentity(final User user) {
        currentIdentityThreadLocal.set(user);
    }


    public InstructorUser getCurrentThreadUnderlyingIdentity() {
        return currentUnderlyingIdentityThreadLocal.get();
    }

    public void setCurrentThreadUnderlyingIdentity(final InstructorUser user) {
        currentUnderlyingIdentityThreadLocal.set(user);
    }

    //-------------------------------------------------------------------------

    public InstructorUser ensureEffectiveIdentityIsInstructor() throws PrivilegeException {
        final User user = getCurrentThreadEffectiveIdentity();
        if (user.getUserType()==UserType.INSTRUCTOR) {
            return (InstructorUser) user;
        }
        throw new PrivilegeException(user, Privilege.USER_INSTRUCTOR);
    }

    public AnonymousUser ensureEffectiveIdentityIsAnonymous() throws PrivilegeException {
        final User user = getCurrentThreadEffectiveIdentity();
        if (user.getUserType()==UserType.ANONYMOUS) {
            return (AnonymousUser) user;
        }
        throw new PrivilegeException(user, Privilege.USER_ANONYMOUS);
    }

    //-------------------------------------------------------------------------

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }

}
