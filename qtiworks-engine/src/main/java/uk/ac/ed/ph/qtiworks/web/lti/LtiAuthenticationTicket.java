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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.web.lti;

import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.services.IdentityService;
import uk.ac.ed.ph.qtiworks.web.controller.lti.LtiLaunchController;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;

/**
 * This "ticket" is created and stored in the HTTP session once a
 * {@link User} with role {@link UserRole#INSTRUCTOR} successfully
 * authenticates on a particular {@link LtiResource} via an LTI
 * domain launch. It is used to allow access to this resource
 * for this user for the remainder of the session, and to provide
 * other pieces of useful information.
 * <p>
 * Developer note: Instances of this class will be stored in the
 * client's HTTP session, so should be immutable and safe for storing in this way.
 *
 * @see LtiResourceAuthenticationFilter
 * @see LtiLaunchController
 * @see LtiIdentityContext
 * @see IdentityService
 *
 * @author David McKain
 */
public final class LtiAuthenticationTicket implements Serializable {

    private static final long serialVersionUID = 1412636123357858458L;

    /** ID of the {@link LtiUser} in question */
    private final long ltiUserId;

    /** ID of the {@link LtiResource} in question */
    private final long ltiResourceId;

    /** Optional return URL, as provided by DecodedLtiLaunch#getLaunchPresentationReturnUrl() */
    private final String returnUrl;

    public LtiAuthenticationTicket(final long ltiUserId, final long ltiResourceId, final String returnUrl) {
        this.ltiUserId = ltiUserId;
        this.ltiResourceId = ltiResourceId;
        this.returnUrl = returnUrl;
    }

    public long getLtiUserId() {
        return ltiUserId;
    }

    public long getLtiResouceId() {
        return ltiResourceId;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
