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
package uk.ac.ed.ph.qtiworks.web.authn;

import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.dao.InstructorUserDao;
import uk.ac.ed.ph.qtiworks.domain.entities.User;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
public abstract class AbstractWebAuthenticationFilter implements Filter {

    private static final Log log = LogFactory.getLog(AbstractWebAuthenticationFilter.class);

    /**
     * Name of session Attribute containing the user ID for the chosen identity of the client.
     * This is ignored if the underlying identity is not that of a system administrator.
     */
    public static final String REQUESTED_EFFECTIVE_LOGIN_NAME_ATTRIBUTE_NAME = "qtiworks.web.authn.requestedIdentityLoginName";

    /** Name of request Attribute that will contain the effective identity of the client */
    public static final String EFFECTIVE_IDENTITY_ATTRIBUTE_NAME = "qtiworks.web.authn.effectiveIdentity";

    /** Name of request Attribute that will contain the underlying identity of the client */
    public static final String UNDERLYING_IDENTITY_ATTRIBUTE_NAME = "qtiworks.web.authn.underlyingIdentity";

    /** Spring {@link ApplicationContext} */
    protected ApplicationContext applicationContext;

    /** Bean specifying the identity of the current User */
    protected IdentityContext identityContext;

    /** Access to {@link InstructorUserDao} in case any User details need to be looked up */
    protected InstructorUserDao instructorUserDao;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        /* Get main business Objects */
        applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        identityContext = (IdentityContext) applicationContext.getBean("identityContext");
        instructorUserDao = (InstructorUserDao) applicationContext.getBean("instructorUserDao");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        /* This filter can only HTTP stuff */
        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("Expected request to be a HttpServletRequest");
        }
        if (!(response instanceof HttpServletResponse)) {
            throw new ServletException("Expected response to be a HttpServletResponse");
        }
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        /* Ask subclass to perform is required to either provide authenticated User details or set
         * up the HTTP Response to do whatever is required.
         */
        final User underlyingUser = doAuthentication(httpRequest, httpResponse);
        if (underlyingUser==null) {
            /* Not authenticated. Subclass will have set the Response Object up to ensure the right
             * thing happens next so we return now.
             */
            return;
        }

        /* Work out the effective User ID, which is normally the same as the underlying User
         * but can be overridden by SysAdmins */
        User effectiveUser = underlyingUser;
        final HttpSession session = httpRequest.getSession();
        final String requestedEffectiveLoginName = (String) session.getAttribute(REQUESTED_EFFECTIVE_LOGIN_NAME_ATTRIBUTE_NAME);
        if (requestedEffectiveLoginName!=null) {
            if (underlyingUser.isSysAdmin()) {
                effectiveUser = instructorUserDao.findByLoginName(requestedEffectiveLoginName);
                if (effectiveUser==null) {
                    log.warn("Request effective User ID " + requestedEffectiveLoginName + " was not found; clearing state");
                    session.setAttribute(REQUESTED_EFFECTIVE_LOGIN_NAME_ATTRIBUTE_NAME, null);
                }
            }
            else {
                log.warn("Requested identity is not null but current identity does not have required privileges; clearing state");
                session.setAttribute(REQUESTED_EFFECTIVE_LOGIN_NAME_ATTRIBUTE_NAME, null);
            }
        }

        /* Store identity as request attributes for convenience */
        request.setAttribute(EFFECTIVE_IDENTITY_ATTRIBUTE_NAME, effectiveUser);
        request.setAttribute(UNDERLYING_IDENTITY_ATTRIBUTE_NAME, underlyingUser);

        /* Then continue with the next link in the chain, passing the wrapped request so that
         * the next handler in the chain doesn't can pull out authentication details as normal.
         * We'll set up the UserContext bean before doing the work and clear up afterwards
         *  */
        try {
            identityContext.setCurrentThreadEffectiveIdentity(effectiveUser);
            identityContext.setCurrentThreadUnderlyingIdentity(underlyingUser);
            chain.doFilter(request, response);
        }
        finally {
            /* Ensure we clear state afterwards for consistency */
            identityContext.setCurrentThreadEffectiveIdentity(null);
            identityContext.setCurrentThreadUnderlyingIdentity(null);
        }
    }

    /**
     * Default implementation that does nothing.
     */
    @Override
    public void destroy() {
        /* (Nothing to do here) */
    }

    /**
     * Subclasses should fill in to "do" the actual authentication work. Return a non-null
     * {@link User} if authorisation succeeds, otherwise set up the {@link HttpServletResponse} as
     * appropriate (e.g. redirect to login page) and return null.
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    protected abstract User doAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException;
}
