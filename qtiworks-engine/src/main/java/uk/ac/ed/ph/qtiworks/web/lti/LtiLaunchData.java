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
package uk.ac.ed.ph.qtiworks.web.lti;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;

/**
 * Encapsulates the subset of the LTI launch data that we might be interested in.
 * <p>
 * We can add more fields here if and when we need them.
 *
 * @author David McKain
 */
public final class LtiLaunchData implements Serializable {

    private static final long serialVersionUID = -6570791171231780956L;

    private String resourceLinkId;
    private String contextId;
    private String launchPresentationReturnUrl;
    private String toolConsumerInfoProductFamilyCode;
    private String toolConsumerInfoVersion;
    private String toolConsumerInstanceGuid;
    private String userId;
    private String lisPersonNameFull;
    private String lisPersonNameGiven;
    private String lisPersonNameFamily;
    private String lisPersonContactEmailPrimary;

    public String getResourceLinkId() {
        return resourceLinkId;
    }

    public void setResourceLinkId(final String resourceLinkId) {
        this.resourceLinkId = resourceLinkId;
    }


    public String getContextId() {
        return contextId;
    }

    public void setContextId(final String contextId) {
        this.contextId = contextId;
    }


    public String getLaunchPresentationReturnUrl() {
        return launchPresentationReturnUrl;
    }

    public void setLaunchPresentationReturnUrl(final String launchPresentationReturnUrl) {
        this.launchPresentationReturnUrl = launchPresentationReturnUrl;
    }


    public String getToolConsumerInfoProductFamilyCode() {
        return toolConsumerInfoProductFamilyCode;
    }

    public void setToolConsumerInfoProductFamilyCode(final String toolConsumerInfoProductFamilyCode) {
        this.toolConsumerInfoProductFamilyCode = toolConsumerInfoProductFamilyCode;
    }


    public String getToolConsumerInfoVersion() {
        return toolConsumerInfoVersion;
    }

    public void setToolConsumerInfoVersion(final String toolConsumerInfoVersion) {
        this.toolConsumerInfoVersion = toolConsumerInfoVersion;
    }


    public String getToolConsumerInstanceGuid() {
        return toolConsumerInstanceGuid;
    }

    public void setToolConsumerInstanceGuid(final String toolConsumerInstanceGuid) {
        this.toolConsumerInstanceGuid = toolConsumerInstanceGuid;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }


    public String getLisPersonNameFull() {
        return lisPersonNameFull;
    }

    public void setLisPersonNameFull(final String lisPersonNameFull) {
        this.lisPersonNameFull = lisPersonNameFull;
    }


    public String getLisPersonNameGiven() {
        return lisPersonNameGiven;
    }

    public void setLisPersonNameGiven(final String lisPersonNameGiven) {
        this.lisPersonNameGiven = lisPersonNameGiven;
    }


    public String getLisPersonNameFamily() {
        return lisPersonNameFamily;
    }

    public void setLisPersonNameFamily(final String lisPersonNameFamily) {
        this.lisPersonNameFamily = lisPersonNameFamily;
    }


    public String getLisPersonContactEmailPrimary() {
        return lisPersonContactEmailPrimary;
    }

    public void setLisPersonContactEmailPrimary(final String lisPersonContactEmailPrimary) {
        this.lisPersonContactEmailPrimary = lisPersonContactEmailPrimary;
    }

    //--------------------------------------------------------------------

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
