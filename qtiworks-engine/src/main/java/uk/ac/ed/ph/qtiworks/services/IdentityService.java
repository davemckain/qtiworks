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

import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.web.authn.AnonymousAuthenticationFilter;
import uk.ac.ed.ph.qtiworks.web.authn.SystemUserAuthenticationFilter;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionContext;
import uk.ac.ed.ph.qtiworks.web.lti.LtiIdentityContext;
import uk.ac.ed.ph.qtiworks.web.lti.LtiResourceAuthenticationFilter;

import org.springframework.stereotype.Service;

/**
 * {@link ThreadLocal} storing details about the current {@link User} and "contexts" for which
 * the user currently has been granted access to:
 * <ul>
 *   <li>{@link LtiIdentityContext} (when accessing QTIWorks via a domain-level launch on a particular resource)</li>
 *   <li>{@link CandidateSessionContext} (when accessing the QTIWorks candidate services)</li>
 * </ul>
 * Identity setting is done in the web layer via {@link AnonymousAuthenticationFilter},
 * {@link SystemUserAuthenticationFilter} and {@link LtiResourceAuthenticationFilter}.
 * <p>
 * If you use this service outside the web layer, you must ensure you call
 * {@link #setCurrentThreadUser(User)} before using any other service bean that needs
 * to know about user identity.
 *
 * @see User
 * @see LtiIdentityContext
 * @see AnonymousAuthenticationFilter
 * @see SystemUserAuthenticationFilter
 * @see LtiResourceAuthenticationFilter
 *
 * @author David McKain
 */
@Service
public final class IdentityService {

    private final ThreadLocal<User> currentUserThreadLocal = new ThreadLocal<User>();
    private final ThreadLocal<LtiIdentityContext> currentLtiIdentityContextThreadLocal = new ThreadLocal<LtiIdentityContext>();

    /**
     * Returns the {@link User} registered for the current Thread, if it has been set.
     *
     * @return {@link User} for the current thread, which may be null.
     *
     * @see #assertCurrentThreadUser()
     * @see #setCurrentThreadUser(User)
     */
    public User getCurrentThreadUser() {
        return currentUserThreadLocal.get();
    }

    /**
     * Returns the {@link User} registered for the current Thread, checking that this returns
     * a non-null result.
     * <p>
     * A user must previously have been set for this Thread via {@link #setCurrentThreadUser(User)}.
     *
     * @return {@link User} for the current thread, which will not be null.
     *
     * @throws IllegalArgumentException if no {@link User} is currently registered.
     *
     * @see #getCurrentThreadUser()
     * @see #setCurrentThreadUser(User)
     */
    public User assertCurrentThreadUser() {
        final User result = getCurrentThreadUser();
        if (result==null) {
            throw new IllegalStateException("No User registered for the current Thread in IdentityService");
        }
        return result;
    }

    public void setCurrentThreadUser(final User user) {
        if (user!=null) {
            currentUserThreadLocal.set(user);
        }
        else {
            currentUserThreadLocal.remove();
        }
    }


    /**
     * Returns the {@link LtiIdentityContext} for the current Thread, if it has been set.
     * <p>
     * This is only set on LTI domain instructor launches, and will require null otherwise.
     *
     * @return {@link LtiIdentityContext} for the current Thread, which may be null.
     *
     * @see #setCurrentThreadLtiIdentityContext(LtiIdentityContext)
     * @see #assertCurrentThreadLtiIdentityContext()
     */
    public LtiIdentityContext getCurrentThreadLtiIdentityContext() {
        return currentLtiIdentityContextThreadLocal.get();
    }

    /**
     * Returns the {@link LtiIdentityContext} for the current Thread, expecting it
     * to return null.
     *
     * @return {@link LtiIdentityContext} for the current Thread, which will not be null.
     *
     * @throws IllegalStateException if an {@link LtiIdentityContext} is not set for the current Thread.
     *
     * @see #setCurrentThreadLtiIdentityContext(LtiIdentityContext)
     * @see #assertCurrentThreadLtiIdentityContext()
     */
    public LtiIdentityContext assertCurrentThreadLtiIdentityContext() {
        final LtiIdentityContext result = getCurrentThreadLtiIdentityContext();
        if (result==null) {
            throw new IllegalStateException("An LtiIdentityContext is required for the current Thread, but has not been set");
        }
        return result;
    }

    public void setCurrentThreadLtiIdentityContext(final LtiIdentityContext ltiIdentityContext) {
        if (ltiIdentityContext!=null) {
            currentLtiIdentityContextThreadLocal.set(ltiIdentityContext);
        }
        else {
            currentLtiIdentityContextThreadLocal.remove();
        }
    }

}
