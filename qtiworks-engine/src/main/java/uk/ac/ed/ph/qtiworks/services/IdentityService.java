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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.web.lti.LtiAuthenticationTicket;

import org.springframework.stereotype.Service;

/**
 * {@link ThreadLocal} storing details about the current {@link User} and {@link LtiAuthenticationTicket}
 * (when accessing QTIWorks via a domain-level launch on a particular resource)
 *
 * @see User
 * @see LtiAuthenticationTicket
 *
 * @author David McKain
 */
@Service
public final class IdentityService {

    private final ThreadLocal<User> currentUserThreadLocal = new ThreadLocal<User>();
    private final ThreadLocal<LtiAuthenticationTicket> currentLtiAuthenticationTicketThreadLocal = new ThreadLocal<LtiAuthenticationTicket>();

    public User getCurrentThreadUser() {
        return currentUserThreadLocal.get();
    }

    public void setCurrentThreadUser(final User user) {
        if (user!=null) {
            currentUserThreadLocal.set(user);
        }
        else {
            currentUserThreadLocal.remove();
        }
    }


    public LtiAuthenticationTicket getCurrentThreadLtiAuthenticationTicket() {
        return currentLtiAuthenticationTicketThreadLocal.get();
    }

    public void setCurrentThreadLtiAuthenticationTicket(final LtiAuthenticationTicket ltiAuthenticationTicket) {
        if (ltiAuthenticationTicket!=null) {
            currentLtiAuthenticationTicketThreadLocal.set(ltiAuthenticationTicket);
        }
        else {
            currentLtiAuthenticationTicketThreadLocal.remove();
        }
    }

    public LtiAuthenticationTicket ensureCurrentThreadLtiAuthenticationTicket() {
        final LtiAuthenticationTicket result = getCurrentThreadLtiAuthenticationTicket();
        if (result==null) {
            throw new QtiWorksLogicException("Expected current LtiAuthenticationTicket to be set for current Thread");
        }
        return result;
    }
}
