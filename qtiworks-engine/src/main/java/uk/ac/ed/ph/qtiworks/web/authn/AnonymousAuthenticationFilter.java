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

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.entities.AnonymousUser;
import uk.ac.ed.ph.qtiworks.services.IdentityService;
import uk.ac.ed.ph.qtiworks.services.dao.AnonymousUserDao;

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
 * This filter "authenticates" an anonymous user when accessing stateful resources,
 * by creating a DB entity that store the user's state during her visit to the system.
 *
 * @author David McKain
 */
public final class AnonymousAuthenticationFilter extends AbstractWebAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(AnonymousAuthenticationFilter.class);

    /** Name of session Attribute that will contain the resulting {@link AnonymousUser} for the caller */
    private static final String ANONYMOUS_USER_ID_ATTRIBUTE_NAME = "qtiworks.web.authn.anonymousUserId";

    private IdentityService identityService;
    private AnonymousUserDao anonymousUserDao;

    @Override
    protected void initWithApplicationContext(final FilterConfig filterConfig, final WebApplicationContext webApplicationContext)
            throws Exception {
        identityService = webApplicationContext.getBean(IdentityService.class);
        anonymousUserDao = webApplicationContext.getBean(AnonymousUserDao.class);
    }

    @Override
    protected void doFilterAuthentication(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain, final HttpSession session)
            throws IOException, ServletException {
        /* See if we already have something in the session */
        AnonymousUser anonymousUser = null;
        final Long anonymousUserId = (Long) session.getAttribute(ANONYMOUS_USER_ID_ATTRIBUTE_NAME);
        if (anonymousUserId!=null) {
            /* Try to reuse existing anonymous user */
            anonymousUser = anonymousUserDao.findById(anonymousUserId);
        }
        if (anonymousUserId==null || anonymousUser==null) {
            /* Nothing in session or user with existing ID not found, so create new anonymous user */
            anonymousUser = createAnonymousUser(session);
            session.setAttribute(ANONYMOUS_USER_ID_ATTRIBUTE_NAME, anonymousUser.getId());
            logger.debug("Created AnonymousUser {} for this session", anonymousUser);
        }

        /* Make sure account is available (slightly pathological here) */
        if (anonymousUser.isLoginDisabled()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Your account is currently disabled");
            return;
        }

        identityService.setCurrentThreadUser(anonymousUser);
        try {
            chain.doFilter(request, response);
        }
        finally {
            identityService.setCurrentThreadUser(null);
        }
    }

    protected AnonymousUser createAnonymousUser(final HttpSession httpSession) {
        final String sessionId = httpSession.getId();
        AnonymousUser anonymousUser = anonymousUserDao.findBySessionId(sessionId);
        if (anonymousUser!=null) {
            throw new QtiWorksLogicException("AnonymousUser with session ID " + sessionId + " already exists in DB");
        }
        anonymousUser = new AnonymousUser();
        anonymousUser.setFirstName("Anonymous");
        anonymousUser.setLastName("User " + sessionId);
        anonymousUser.setSessionId(sessionId);
        anonymousUserDao.persist(anonymousUser);
        return anonymousUser;
    }
}
