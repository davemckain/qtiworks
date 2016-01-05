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

import uk.ac.ed.ph.qtiworks.domain.entities.LtiContext;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.services.IdentityService;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

/**
 * This provides LTI-specific information about the "current" LTI instructor user. It is
 * stored in the current thread by the {@link IdentityService} whenever the current user
 * is of this type.
 * <p>
 * It provides access to the that this user has been given access to {@link LtiResource}, as
 * well as some other useful information.
 *
 * @see IdentityService
 * @see LtiAuthenticationTicket
 * @see LtiResourceAuthenticationFilter
 *
 * @author David McKain
 */
public final class LtiIdentityContext {

    /**
     * Indicates which {@link LtiResource} this tickets provides access to,
     * i.e. that on which the LTI domain launch was invoked on.
     */
    private final LtiResource ltiResource;

    /** Optional return URL, as provided by DecodedLtiLaunch#getLaunchPresentationReturnUrl() */
    private final String returnUrl;

    public LtiIdentityContext(final LtiResource ltiResource, final String returnUrl) {
        Assert.notNull(ltiResource, "ltiResource");
        this.ltiResource = ltiResource;
        this.returnUrl = returnUrl;
    }

    public LtiResource getLtiResource() {
        return ltiResource;
    }

    public LtiContext getLtiContext() {
        return ltiResource.getLtiContext();
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
