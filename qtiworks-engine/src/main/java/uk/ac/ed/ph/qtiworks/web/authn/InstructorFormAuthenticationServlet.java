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

import uk.ac.ed.ph.qtiworks.base.services.Auditor;
import uk.ac.ed.ph.qtiworks.domain.dao.InstructorUserDao;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet to handle the incoming userId/password data from the form log-in
 * process and determine
 * what to do next.
 *
 * @see InstructorFormAuthenticator
 * @author David McKain
 */
public final class InstructorFormAuthenticationServlet extends HttpServlet {

    private static final long serialVersionUID = -4786390063305269269L;

    private static final Logger logger = LoggerFactory.getLogger(InstructorFormAuthenticationServlet.class);

    /**
     * Name of parameter providing the location of the Form Login JSP (when
     * used)
     */
    public static final String FORM_LOGIN_ERROR_JSP_PATH_PARAMETER_NAME = "formLoginErrorJspPath";

    public static final String USER_ID_PARAM = "userId";

    public static final String PASSWORD_PARAM = "password";

    public static final String PROTECTED_REQUEST_URL_PARAM = "protectedRequestUrl";

    /**
     * Location of form login error JSP page, passed via context <init-param/>.
     * If authentication fails, the user will be forwarded to this page.
     * The <tt>errors</tt> attribute contains details about what went wrong.
     */
    private String loginErrorJspPath;

    private InstructorUserDao instructorUserDao;

    private Auditor auditor;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        /* Check required <init-param>s */
        loginErrorJspPath = config.getInitParameter(FORM_LOGIN_ERROR_JSP_PATH_PARAMETER_NAME);
        if (loginErrorJspPath==null) {
            logger.error("Required <init-param/> {} has not been passed to {}", FORM_LOGIN_ERROR_JSP_PATH_PARAMETER_NAME, getClass().getName());
            throw new ServletException("Required <init-param/> was not set for servlet");
        }

        /* Extract relevant business Objects */
        try {
            final ApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
            auditor = appContext.getBean(Auditor.class);
            instructorUserDao = appContext.getBean(InstructorUserDao.class);
        }
        catch (final Exception e) {
            logger.error("init() failed on " + this.getClass().getSimpleName(), e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        /* Recover the URL of the original protected resource. We'll redirect to this on success */
        final String protectedRequestUrl = request.getParameter(PROTECTED_REQUEST_URL_PARAM);
        if (protectedRequestUrl == null) {
            /* Hmmm.... not supplied. Let's fail appropriately */
            logger.warn("Parameter {} not found", PROTECTED_REQUEST_URL_PARAM);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        /* Get supplied login credentials */
        final String userId = request.getParameter(USER_ID_PARAM);
        final String password = request.getParameter(PASSWORD_PARAM);
        final List<String> errors = new ArrayList<String>();
        final InstructorUser authenticatedUser = tryAuthentication(userId, password, errors);
        if (authenticatedUser != null) {
            /* Store user details in session and redirect to the page we were supposed to be
             * going originally, and remove referral details from session
             */
            auditor.recordEvent("Instructor authentication succeeded for " + userId);
            logger.debug("Authentication succeeded - redirecting to {}", protectedRequestUrl);
            request.getSession().setAttribute(InstructorAuthenticationFilter.UNDERLYING_IDENTITY_ATTRIBUTE_NAME, authenticatedUser);
            response.sendRedirect(protectedRequestUrl);
        }
        else {
            /* Forward to login error page, keeping the referral details in session */
            auditor.recordEvent("Instructor authentication failed for " + userId);
            logger.debug("Authentication failed - redirecting to {}", loginErrorJspPath);
            request.setAttribute("errors", errors);
            request.getRequestDispatcher(loginErrorJspPath).forward(request, response);
        }
    }

    protected InstructorUser tryAuthentication(final String loginName, final String password, final List<String> errors) {
        /* Make sure details have been specified */
        if (loginName == null) {
            errors.add("No user ID specified");
        }
        if (password == null) {
            errors.add("No password specified");
        }
        /* Then look up user */
        final InstructorUser user = instructorUserDao.findByLoginName(loginName);
        if (user == null) {
            errors.add("User '" + loginName + " ' does not exist");
            return null;
        }
        /* Then check password */
        final String passwordDigest = ServiceUtilities.computePasswordDigest(user.getPasswordSalt(), password);
        if (!passwordDigest.equals(user.getPasswordDigest())) {
            errors.add("Invalid Password");
            return null;
        }
        /* Make sure account is not disabled */
        if (user.isLoginDisabled()) {
            errors.add("Sorry, your account is currently disabled");
            return null;
        }
        return user;
    }
}