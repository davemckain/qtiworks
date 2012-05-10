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

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.web.WebUtilities;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * Trivial concrete implementation of {@link AbstractWebAuthenticationFilter} that just
 * assumes a configured identity for the current User.
 * <p>
 * This is extremely useful when debugging as it saves having to log in over and over again!
 *
 * @author David McKain
 */
public final class InstructorFakeAuthenticationFilter extends AbstractInstructorAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(InstructorFakeAuthenticationFilter.class);

    /** Filter init parameter specifying the Login Name of the assumed User */
    public static final String FAKE_LOGIN_NAME_PARAM = "fakeLoginName";

    /** Login Name for the assumed User */
    private String fakeLoginName;

    @Override
    protected void initWithApplicationContext(final FilterConfig filterConfig, final WebApplicationContext webApplicationContext) throws Exception {
        super.initWithApplicationContext(filterConfig, webApplicationContext);
        this.fakeLoginName = WebUtilities.getRequiredInitParameter(filterConfig, FAKE_LOGIN_NAME_PARAM);
        final InstructorUser fakeUser = lookupFakeUser(); /* (Make sure user exists now) */
        logger.warn("Fake authentication is enabled and attached to user {}. This should not be used in production deployments!",
                fakeUser);
    }

    @Override
    protected InstructorUser doAuthentication(final HttpServletRequest request, final HttpServletResponse response) {
        return lookupFakeUser();
    }

    private InstructorUser lookupFakeUser() {
        final InstructorUser user = instructorUserDao.findByLoginName(fakeLoginName);
        if (user==null) {
            throw new QtiWorksLogicException("Could not find specified fake InstructorUser with loginName " + fakeLoginName);
        }
        else if (user.isLoginDisabled()) {
            throw new QtiWorksLogicException("Fake InstructorUser " + fakeLoginName + " has their account marked as disabled");
        }
        return user;
    }
}
