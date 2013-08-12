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
package uk.ac.ed.ph.qtiworks.web.lti;

import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.services.IdentityService;
import uk.ac.ed.ph.qtiworks.services.dao.LtiResourceDao;
import uk.ac.ed.ph.qtiworks.web.authn.AbstractWebAuthenticationFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Authentication filter for gaining access to a particular {@link LtiResource}.
 * Such resources exist in URLs paths of the form:
 *
 * <code>lti/resource/{lrid}</code>
 *
 * Note that this filter only works <em>strong</em> after the initial LTI launch URL has been
 * accessed to set up the HTTP session correctly.
 *
 * @author David McKain
 */
public final class LtiResourceAuthenticationFilter extends AbstractWebAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(LtiResourceAuthenticationFilter.class);

    public static final String LTI_USER_ATTRIBUTE_BASE_NAME = "qtiworks.web.authn.lti.ltiUserForResource";

    private LtiResourceDao ltiResourceDao;
    private IdentityService identityService;

    @Override
    protected void initWithApplicationContext(final FilterConfig filterConfig, final WebApplicationContext webApplicationContext)
            throws Exception {
        ltiResourceDao = webApplicationContext.getBean(LtiResourceDao.class);
        identityService = webApplicationContext.getBean(IdentityService.class);
    }

    @Override
    protected void doFilterAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain,
            final HttpSession session)
            throws IOException, ServletException {
        /* Determine which LTI resource we're working on from the pathInfo, which should be of the form /resource/{lrid}... */
        final String pathInfo = request.getPathInfo();
        final Pattern pathPattern = Pattern.compile("^/resource/(\\d+)");
        final Matcher pathMatcher = pathPattern.matcher(pathInfo);
        if (!pathMatcher.find()) {
            logger.warn("Failed regex match on resource path {}", pathInfo);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final String lridString = pathMatcher.group(1);
        final long lrid;
        try {
            lrid = Long.parseLong(lridString);

        }
        catch (final NumberFormatException e) {
            logger.warn("Failed to parse resource ID from path {}", pathInfo);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        /* Make sure resource (still) exists */
        final LtiResource ltiResource = ltiResourceDao.findById(lrid);
        if (ltiResource==null) {
            logger.warn("Failed to look up LtiResource with ID {}", lrid);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        /* The user's identity for this resource should have been stored in the session previously */
        final LtiUser ltiUser = getAuthenticatedUserForResource(session, lrid);
        if (ltiUser==null) {
            logger.warn("Failed to retrieve LtiUser from HttpSession corresponding to LTIResource with lrid {}", lrid);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden. This resource is only available via an LTI link from a Tool Provider. Please try the launch again.");
            return;
        }

        /* Finally set up identity and continue with filter chain */
        identityService.setCurrentThreadUser(ltiUser);
        identityService.setCurrentThreadLtiResource(ltiResource);
        try {
            chain.doFilter(request, response);
        }
        finally {
            identityService.setCurrentThreadUser(null);
            identityService.setCurrentThreadLtiResource(null);
        }
    }

    public static void authenticateUserForResource(final HttpSession session, final LtiResource ltiResource, final LtiUser ltiUser) {
        final Long lrid = ltiResource.getId();
        session.setAttribute(getLtiUserSessionKey(lrid), ltiUser);
    }

    private static LtiUser getAuthenticatedUserForResource(final HttpSession session, final long lrid) {
        return (LtiUser) session.getAttribute(getLtiUserSessionKey(lrid));
    }

    private static String getLtiUserSessionKey(final long lrid) {
        return LTI_USER_ATTRIBUTE_BASE_NAME + Long.toString(lrid);
    }
}
