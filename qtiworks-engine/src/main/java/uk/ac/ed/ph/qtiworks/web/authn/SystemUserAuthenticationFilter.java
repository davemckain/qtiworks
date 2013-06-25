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

import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksDeploymentSettings;
import uk.ac.ed.ph.qtiworks.domain.entities.SystemUser;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.base.IdentityService;
import uk.ac.ed.ph.qtiworks.services.dao.SystemUserDao;
import uk.ac.ed.ph.qtiworks.services.dao.UserDao;

import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;

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
 * Authentication filter for instructor users. This selects a delegating
 * {@link AbstractSystemUserAuthenticator} as directed by the webapp config.
 * It supports the {@link IdentityService} notion and the {@link User} entity.
 *
 * <h2>Tomcat Note</h2>
 *
 * Tomcat's AccessLogValve logs the <strong>original</strong> request, which will only contain
 * user ID information if there is some kind of front-end authentication going on. In this case,
 * you will want to log the {@link #EFFECTIVE_IDENTITY_ATTRIBUTE_NAME} request attribute instead.
 *
 * @author David McKain
 */
public final class SystemUserAuthenticationFilter extends AbstractWebAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(SystemUserAuthenticationFilter.class);

    /** Name of request Attribute that will contain the underlying {@link SystemUser} identity of the client */
    public static final String USER_IDENTITY_ATTRIBUTE_NAME = "qtiworks.web.authn.currentUser";

    protected IdentityService identityService;
    protected UserDao userDao;
    protected SystemUserDao systemUserDao;
    protected AbstractSystemUserAuthenticator abstractInstructorAuthenticator;

    @Override
    protected void initWithApplicationContext(final FilterConfig filterConfig, final WebApplicationContext webApplicationContext)
            throws Exception {
        identityService = webApplicationContext.getBean(IdentityService.class);
        userDao = webApplicationContext.getBean(UserDao.class);
        systemUserDao = webApplicationContext.getBean(SystemUserDao.class);

        /* Decide whether to do fake or form authentication */
        final QtiWorksDeploymentSettings qtiWorksDeploymentSettings = webApplicationContext.getBean(QtiWorksDeploymentSettings.class);
        final String fakeLoginName = qtiWorksDeploymentSettings.getFakeLoginName();
        if (StringUtilities.isNullOrBlank(fakeLoginName)) {
            /* Use standard form authentication */
            abstractInstructorAuthenticator = new SystemUserFormAuthenticator(webApplicationContext, filterConfig);
        }
        else {
            /* Use fake authentication */
            logger.warn("Fake authentication is being enabled and attached to user {}. This should not be used in production deployments!", fakeLoginName);
            abstractInstructorAuthenticator = new SystemUserFakeAuthenticator(webApplicationContext, fakeLoginName);
        }
    }

    @Override
    protected void doFilterAuthentication(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain, final HttpSession session) throws IOException, ServletException {
        /* Try to extract existing authenticated User Object from Session */
        SystemUser currentUser = (SystemUser) session.getAttribute(USER_IDENTITY_ATTRIBUTE_NAME);
        logger.trace("Extracted SystemUser from Session: {}", currentUser);
        if (currentUser==null) {
            /* If there are no User details, we ask subclass to do whatever is required to
             * authenticate
             */
            currentUser = abstractInstructorAuthenticator.doAuthentication(request, response);
            if (currentUser!=null) {
                /* Store back into Session so that we can avoid later lookups, and allow things
                 * further down the chain to access
                 */
                session.setAttribute(USER_IDENTITY_ATTRIBUTE_NAME, currentUser);
            }
            else {
                /* Not authenticated. Subclass will have Response Object set up to ensure the right
                 * thing happens next so we return now.
                 */
                return;
            }
        }

        /* Store identity as request attributes for convenience */
        request.setAttribute(USER_IDENTITY_ATTRIBUTE_NAME, currentUser);

        /* Then continue with the next link in the chain, passing the wrapped request so that
         * the next handler in the chain doesn't can pull out authentication details as normal.
         * We'll set up the UserContext bean before doing the work and clear up afterwards
         *  */
        try {
            identityService.setCurrentThreadUser(currentUser);
            chain.doFilter(request, response);
        }
        finally {
            /* Ensure we clear state afterwards for consistency */
            identityService.setCurrentThreadUser(null);
        }
    }
}
