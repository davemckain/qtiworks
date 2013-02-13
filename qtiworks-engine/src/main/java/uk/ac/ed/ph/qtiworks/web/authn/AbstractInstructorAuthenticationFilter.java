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
package uk.ac.ed.ph.qtiworks.web.authn;

import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.dao.InstructorUserDao;
import uk.ac.ed.ph.qtiworks.domain.dao.UserDao;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.domain.entities.User;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * Base authentication filter, supporting the {@link IdentityContext} notion
 * and the {@link User} entity.
 *
 * <h2>Tomcat Note</h2>
 *
 * Tomcat's AccessLogValve logs the <strong>original</strong> request, which will only contain
 * user ID information if there is some kind of front-end authentication going on. In this case,
 * you will want to log the {@link #EFFECTIVE_IDENTITY_ATTRIBUTE_NAME} request attribute instead.
 *
 * @author David McKain
 */
public abstract class AbstractInstructorAuthenticationFilter extends AbstractWebAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(AbstractInstructorAuthenticationFilter.class);

    /** Name of request Attribute that will contain the underlying {@link InstructorUser} identity of the client */
    public static final String UNDERLYING_IDENTITY_ATTRIBUTE_NAME = "qtiworks.web.authn.underlyingIdentity";

    /**
     * Name of session Attribute containing the user ID for the chosen identity of the client,
     * if she has the appropriate privileges.
     */
    public static final String REQUESTED_EFFECTIVE_USER_ID_ATTRIBUTE_NAME = "qtiworks.web.authn.requestedIdentityId";

    /** Name of request Attribute that will contain the effective identity of the client */
    public static final String EFFECTIVE_IDENTITY_ATTRIBUTE_NAME = "qtiworks.web.authn.effectiveIdentity";

    protected IdentityContext identityContext;
    protected UserDao userDao;
    protected InstructorUserDao instructorUserDao;

    @Override
    protected void initWithApplicationContext(final FilterConfig filterConfig, final WebApplicationContext webApplicationContext)
            throws Exception {
        identityContext = webApplicationContext.getBean(IdentityContext.class);
        userDao = webApplicationContext.getBean(UserDao.class);
        instructorUserDao = webApplicationContext.getBean(InstructorUserDao.class);
    }

    @Override
    protected void doFilterAuthenticated(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse,
            final FilterChain chain, final HttpSession session) throws IOException, ServletException {
        /* Try to extract existing authenticated User Object from Session */
        InstructorUser underlyingUser = (InstructorUser) session.getAttribute(UNDERLYING_IDENTITY_ATTRIBUTE_NAME);
        logger.trace("Extracted InstructorUser from Session: {}", underlyingUser);
        if (underlyingUser==null) {
            /* If there are no User details, we ask subclass to do whatever is required to
             * authenticate
             */
            underlyingUser = doAuthentication(httpRequest, httpResponse);
            if (underlyingUser!=null) {
                /* Store back into Session so that we can avoid later lookups, and allow things
                 * further down the chain to access
                 */
                session.setAttribute(UNDERLYING_IDENTITY_ATTRIBUTE_NAME, underlyingUser);
            }
            else {
                /* Not authenticated. Subclass will have Response Object set up to ensure the right
                 * thing happens next so we return now.
                 */
                return;
            }
        }

        /* Work out the effective User ID, which is normally the same as the underlying User
         * but can be overridden by SysAdmins */
        User effectiveUser = underlyingUser;
        if (underlyingUser.isSysAdmin()) {
            final Long requestedEffectiveUserId = (Long) session.getAttribute(REQUESTED_EFFECTIVE_USER_ID_ATTRIBUTE_NAME);
            if (requestedEffectiveUserId!=null) {
                final User resultingUser = userDao.findById(requestedEffectiveUserId);
                if (resultingUser!=null) {
                    effectiveUser = resultingUser;
                }
                else {
                    logger.warn("Requested effective User with ID {} was not found; clearing state", requestedEffectiveUserId);
                    session.setAttribute(REQUESTED_EFFECTIVE_USER_ID_ATTRIBUTE_NAME, null);
                }
            }
        }
        else {
            session.setAttribute(REQUESTED_EFFECTIVE_USER_ID_ATTRIBUTE_NAME, null);
        }

        /* Store identity as request attributes for convenience */
        httpRequest.setAttribute(EFFECTIVE_IDENTITY_ATTRIBUTE_NAME, effectiveUser);
        httpRequest.setAttribute(UNDERLYING_IDENTITY_ATTRIBUTE_NAME, underlyingUser);

        /* Then continue with the next link in the chain, passing the wrapped request so that
         * the next handler in the chain doesn't can pull out authentication details as normal.
         * We'll set up the UserContext bean before doing the work and clear up afterwards
         *  */
        try {
            identityContext.setCurrentThreadEffectiveIdentity(effectiveUser);
            identityContext.setCurrentThreadUnderlyingIdentity(underlyingUser);
            chain.doFilter(httpRequest, httpResponse);
        }
        finally {
            /* Ensure we clear state afterwards for consistency */
            identityContext.setCurrentThreadEffectiveIdentity(null);
            identityContext.setCurrentThreadUnderlyingIdentity(null);
        }


    }

    /**
     * Subclasses should fill in to "do" the actual authentication work. Return a non-null
     * {@link InstructorUser} if authorisation succeeds, otherwise set up the {@link HttpServletResponse} as
     * appropriate (e.g. redirect to login page) and return null.
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    protected abstract InstructorUser doAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException;
}
