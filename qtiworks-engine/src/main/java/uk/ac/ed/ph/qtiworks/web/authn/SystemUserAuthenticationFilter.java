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
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.services.IdentityService;
import uk.ac.ed.ph.qtiworks.services.dao.SystemUserDao;
import uk.ac.ed.ph.qtiworks.services.dao.UserDao;
import uk.ac.ed.ph.qtiworks.web.WebUtilities;

import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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
 * Authentication filter for system users.
 *
 * @author David McKain
 */
public final class SystemUserAuthenticationFilter extends AbstractWebAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(SystemUserAuthenticationFilter.class);

    public static final String ALLOWED_ROLES_INIT_PARAM_NAME = "allowedRoles";

    /** Name of request Attribute that will contain the underlying {@link SystemUser} identity of the client */
    public static final String SYSTEM_USER_ID_IDENTITY_ATTRIBUTE_NAME = "qtiworks.web.authn.systemUserId";

    protected IdentityService identityService;
    protected UserDao userDao;
    protected SystemUserDao systemUserDao;
    protected AbstractSystemUserAuthenticator abstractSystemUserAuthenticator;
    protected EnumSet<UserRole> allowedRoles;

    @Override
    protected void initWithApplicationContext(final FilterConfig filterConfig, final WebApplicationContext webApplicationContext)
            throws Exception {
        identityService = webApplicationContext.getBean(IdentityService.class);
        userDao = webApplicationContext.getBean(UserDao.class);
        systemUserDao = webApplicationContext.getBean(SystemUserDao.class);

        /* Check configuration to find out what roles are allowed */
        allowedRoles = parseAllowedRoles(WebUtilities.getRequiredInitParameter(filterConfig, ALLOWED_ROLES_INIT_PARAM_NAME));

        /* Decide whether to do fake or form authentication */
        final QtiWorksDeploymentSettings qtiWorksDeploymentSettings = webApplicationContext.getBean(QtiWorksDeploymentSettings.class);
        final String fakeLoginName = qtiWorksDeploymentSettings.getFakeLoginName();
        if (StringUtilities.isNullOrBlank(fakeLoginName)) {
            /* Use standard form authentication */
            abstractSystemUserAuthenticator = new SystemUserFormAuthenticator(webApplicationContext);
        }
        else {
            /* Use fake authentication */
            logger.warn("Fake authentication is being enabled and attached to user {}. This should not be used in production deployments!", fakeLoginName);
            abstractSystemUserAuthenticator = new SystemUserFakeAuthenticator(webApplicationContext, fakeLoginName);
        }
    }

    @Override
    protected void doFilterAuthentication(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain, final HttpSession httpSession) throws IOException, ServletException {
        /* Try to extract authenticated User ID from Session */
        User currentUser = null;
        final Long currentUserId = (Long) httpSession.getAttribute(SYSTEM_USER_ID_IDENTITY_ATTRIBUTE_NAME);
        if (currentUserId!=null) {
            /* Already authenticated */
            currentUser = userDao.findById(currentUserId);
            if (currentUser==null) {
                /* User no longer exists. (Unlikely to happen!) */
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Your account no longer exists");
                return;
            }
        }
        else {
            /* Not authenticated. Ask subclass to do whatever is required to authenticate
             */
            currentUser = abstractSystemUserAuthenticator.doAuthentication(request, response);
            if (currentUser==null) {
                /* Not authenticated. Subclass will have set the Response Object up to ensure the
                 * correct thing happens next so we return now.
                 */
                return;
            }
        }
        logger.trace("Extracted SystemUser from Session: {}", currentUser);

        /* Make sure account is available */
        if (currentUser.isLoginDisabled()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Your account is currently disabled");
            return;
        }

        /* Make sure user role is acceptable */
        if (!allowedRoles.contains(currentUser.getUserRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have the required system role to access this resource");
            return;
        }

        /* Indicate successful authn by storing user ID in session */
        httpSession.setAttribute(SYSTEM_USER_ID_IDENTITY_ATTRIBUTE_NAME, currentUserId);

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

    private EnumSet<UserRole> parseAllowedRoles(final String allowedRolesString)
            throws ServletException {
        final String[] allowedRolesStrings = allowedRolesString.split("\\s+");
        final List<UserRole> result = new ArrayList<UserRole>(allowedRolesStrings.length);
        try {
            for (final String allowedRoleString : allowedRolesStrings) {
                result.add(UserRole.valueOf(allowedRoleString));
            }
        }
        catch (final IllegalArgumentException e) {
            throw new ServletException("Value of <init-param/> " + ALLOWED_ROLES_INIT_PARAM_NAME
                    + " must be a space-separated list of UserRole enumeration constants");
        }
        return EnumSet.copyOf(result);
    }
}
