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

import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of {@link AbstractAuthenticationFilter} that uses a simple form-based
 * log-in process to authenticate the current user.
 *
 * <h2>How it works</h2>
 *
 * If authentication is deemed required (by the superclass), then the user is forwarded to
 * a JSP at {@link #loginJsp}. This JSP should contain a form containing the following
 * parameters to fill in:
 *
 * <ul>
 *   <li>{@link FormAuthenticationServlet#USER_ID_PARAM}: user ID</li>
 *   <li>{@link FormAuthenticationServlet#PASSWORD_PARAM}: password</li>
 *   <li>{@link FormAuthenticationServlet#PROTECTED_REQUEST_URL_PARAM}: the URL of "this" Resource</li>
 * </ul>
 *
 * The JSP should submit to the {@link FormAuthenticationServlet} to check the provided user ID
 * and password. The URL of the current Resource is also passed so that the user can be redirected
 * to it once authentication and authorisation has succeeded.
 *
 * @see FormAuthenticationServlet
 *
 * @author David McKain
 */
public final class FormAuthenticationFilter extends AbstractWebAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(FormAuthenticationFilter.class);

    /**
     * Name of attribute used to store request URL if we need to go to login form so that
     * user can be redirected back once authentication and authorisation has been completed
     * successfully.
     */
    public static final String PROTECTED_REQUEST_URL_NAME = "qtiworks.web.authn.protectedRequestUrl";

    /** Location of form login JSP page, supplied via context <init-param/> */
    private String loginJsp;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);

        /* Look up location of loginJSP */
        loginJsp = AardvarkWebappUtilities.getRequiredInitParameter(filterConfig.getServletContext(),
                AardvarkWebappContextParams.FORM_LOGIN_JSP);
    }

    @Override
    protected InstructorUser doAuthentication(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        /* Not authenticated, so forward to login JSP */

        /* Need to record full HTTP parameters! We'll make this easy by only allowing GET
         * requests here. */
        if (!request.getMethod().equals("GET")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        /* Retrieve URL of protected Resource, including query String */
        String requestUrl = request.getRequestURI();
        final String queryString = request.getQueryString();
        if (queryString!=null) {
            requestUrl += "?" + queryString;
        }
        logger.debug("Forwarding to login page at " + loginJsp);
        request.setAttribute(PROTECTED_REQUEST_URL_NAME, requestUrl);
        request.getRequestDispatcher(loginJsp).forward(request, response);
        return null;
    }
}
