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
package uk.ac.ed.ph.qtiworks.web.candidate;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.UserDao;
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
 * Web layer authentication filter for gaining access to a particular {@link CandidateSession}.
 * <p>
 * We use a combination of a {@link CandidateSessionTicket} and an XSRF token to authorise access
 * to a particular {@link CandidateSession}.
 *
 * @author David McKain
 */
public final class CandidateSessionAuthenticationFilter extends AbstractWebAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(CandidateSessionAuthenticationFilter.class);

    /**
     * Base name of the session attributes used to store {@link CandidateSessionTicket}s for the
     * current session.
     */
    public static final String CANDIDATE_SESSION_TICKET_ATTRIBUTE_BASE_NAME = "qtiworks.web.authn.candidateSessionTickets.lrid.";

    /**
     * Name of the request attribute used to store the {@link CandidateSessionContext} for the
     * current request
     */
    public static final String CANDIDATE_SESSION_CONTEXT_REQUEST_ATTRIBUTE_NAME = "qtiworks.web.authn.candidateSessionContext";

    private UserDao userDao;
    private CandidateSessionDao candidateSessionDao;

    @Override
    protected void initWithApplicationContext(final FilterConfig filterConfig, final WebApplicationContext webApplicationContext)
            throws Exception {
        candidateSessionDao = webApplicationContext.getBean(CandidateSessionDao.class);
        userDao = webApplicationContext.getBean(UserDao.class);
    }

    @Override
    protected void doFilterAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain,
            final HttpSession httpSession)
            throws IOException, ServletException {
        /* Determine which CandidateSession we're authenticating from  pathInfo, which should be of the form /(item|test)session/{xid}/{xsrfToken}/... */
        final String pathInfo = request.getPathInfo();
        final Pattern pathPattern = Pattern.compile("^/(?:item|test)session/(\\d+)/([A-Za-z0-9]+)(/|$)");
        final Matcher pathMatcher = pathPattern.matcher(pathInfo);
        if (!pathMatcher.find()) {
            logger.warn("Failed regex match on resource path {}", pathInfo);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final String xidString = pathMatcher.group(1);
        final long xid;
        try {
            xid = Long.parseLong(xidString);

        }
        catch (final NumberFormatException e) {
            logger.warn("Failed to parse CandidateSession ID from path {}", pathInfo);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final String xsrfToken = pathMatcher.group(2);

        /* Make sure CandidateSession (still) exists */
        final CandidateSession candidateSession = candidateSessionDao.findById(xid);
        if (candidateSession==null) {
            logger.warn("Failed to look up CandidateSession with ID {}", xid);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        /* The user's ticket for accessing this CandidateSession should have been stored in the HTTP session previously */
        final CandidateSessionTicket candidateSessionTicket = getCandidateSessionTicketForSession(httpSession, xid);
        if (candidateSessionTicket==null) {
            logger.warn("Failed to retrieve CandidateSessionTicket from HttpSession for CandidateSession {}", xid);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden. You do not have access to this assessment session. Please launch this assessment again.");
            return;
        }

        /* Make sure supplied XSRF token agrees with the one already generated */
        if (!candidateSessionTicket.getXsrfToken().equals(xsrfToken)) {
            logger.warn("XSRF Token mismatch on CandidateSession {}", xid);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden. You do not have permission to access to this assessment session. Please launch this assessment again.");
        }

        /* Look up user running this session */
        final long userId = candidateSessionTicket.getUserId();
        final User user = userDao.findById(userId);
        if (user==null) {
            logger.warn("User {} in CandidateSessionTicket does not exist", xid);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        /* Finally store information about this session in the request and continue with filter chain */
        final CandidateSessionContext candidateSessionContext = new CandidateSessionContext(candidateSession, candidateSessionTicket.getSessionExitReturnUrl());
        setCurrentRequestCandidateSessionContext(request, candidateSessionContext);
        chain.doFilter(request, response);
    }

    //-------------------------------------------------
    // CandidateSession authentication at HTTP Session level

    public static void authenticateUserForHttpSession(final HttpSession httpSsession, final CandidateSessionTicket candidateSessionTicket) {
        final Long xid = candidateSessionTicket.getCandidateSessionId();
        httpSsession.setAttribute(getCandidateSessionTicketSessionKey(xid), candidateSessionTicket);
    }

    /** TODO: This is not currently being used. */
    public static void deauthenticateUserFromHttpSession(final HttpSession httpSession, final CandidateSession candidateSession) {
        final Long xid = candidateSession.getId();
        httpSession.removeAttribute(getCandidateSessionTicketSessionKey(xid));
    }

    private static CandidateSessionTicket getCandidateSessionTicketForSession(final HttpSession session, final long xid) {
        return (CandidateSessionTicket) session.getAttribute(getCandidateSessionTicketSessionKey(xid));
    }

    private static String getCandidateSessionTicketSessionKey(final long xid) {
        return CANDIDATE_SESSION_TICKET_ATTRIBUTE_BASE_NAME + Long.toString(xid);
    }

    //-------------------------------------------------
    // CandidateSession "authentication" for current HTTP request

    private static void setCurrentRequestCandidateSessionContext(final HttpServletRequest request, final CandidateSessionContext candidateSessionContext) {
        request.setAttribute(CANDIDATE_SESSION_CONTEXT_REQUEST_ATTRIBUTE_NAME, candidateSessionContext);
    }

    public static CandidateSessionContext requireCurrentRequestCandidateSessionContext(final HttpServletRequest request) {
        final CandidateSessionContext result = (CandidateSessionContext) request.getAttribute(CANDIDATE_SESSION_CONTEXT_REQUEST_ATTRIBUTE_NAME);
        if (result==null) {
            throw new QtiWorksLogicException("Failed to retrieve CandidateSessionContext from HttpServletRequest!");

        }
        return result;
    }
}
