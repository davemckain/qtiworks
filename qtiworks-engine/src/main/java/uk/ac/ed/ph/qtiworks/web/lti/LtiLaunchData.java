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

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Encapsulates the subset of the LTI launch data of interest to QTIWorks.
 * <p>
 * NB: Not all LTI data is included here.
 *
 * @author David McKain
 */
public final class LtiLaunchData implements Serializable {

    private static final long serialVersionUID = -6570791171231780956L;

    /** Corresponds to <code>lti_message_type</code>. Required */
    @NotNull
    @NotEmpty
    private String ltiMessageType;

    /** Corresponds to <code>lti_version</code>. Required */
    @NotNull
    @NotEmpty
    private String ltiVersion;

    /** Corresponds to <code>resource_link_id</code>. Required */
    @NotNull
    @NotEmpty
    private String resourceLinkId;

    /** Corresponds to <code>resource_link_title</code>. Recommended */
    private String resourceLinkTitle;

    /** Corresponds to <code>resource_link_description</code>. Recommended */
    private String resourceLinkDescription;

    /** Corresponds to <code>user_id</code>. Recommended */
    private String userId;

    /** Corresponds to <code>roles</code>. Recommended, will be empty if nothing is provided */
    private final Set<String> roles;

    /** Corresponds to <code>lis_person_name_full</code>. Recommended, but possibly suppressed */
    private String lisPersonNameFull;

    /** Corresponds to <code>lis_person_name_given</code>. Recommended, but possibly suppressed */
    private String lisPersonNameGiven;

    /** Corresponds to <code>lis_person_name_family</code>. Recommended, but possibly suppressed */
    private String lisPersonNameFamily;

    /** Corresponds to <code>lis_person_contact_email_primary</code>. Recommended, but possibly suppressed */
    private String lisPersonContactEmailPrimary;

    /** Corresponds to <code>context_id</code>. Recommended */
    private String contextId;

    /** Corresponds to <code>context_label</code>. Recommended */
    private String contextLabel;

    /** Corresponds to <code>context_title</code>. Recommended */
    private String contextTitle;

    /** Corresponds to <code>launch_presentation_return_url</code>. Recommended. */
    private String launchPresentationReturnUrl;

    /** Corresponds to <code>tool_consumer_info_product_family_code</code>. Recommended. */
    private String toolConsumerInfoProductFamilyCode;

    /** Corresponds to <code>tool_consumer_info_version</code>. Recommended. */
    private String toolConsumerInfoVersion;

    /** Corresponds to <code>tool_consumer_instance_guid</code>. Recommended. */
    private String toolConsumerInstanceGuid;

    /** Corresponds to <code>tool_consumer_instance_name</code>. Recommended. */
    private String toolConsumerInstanceName;

    /** Corresponds to <code>tool_consumer_instance_description</code>. Optional. */
    private String toolConsumerInstanceDescription;

    //-------------------------------------------------------------
    // Results

    /** Corresponds to <code>lis_result_source_did</code>. Optional. */
    private String lisResultSourcedid;

    /** Corresponds to <code>lis_outcome_servicee_url</code>. Optional, required to enable outcome reporting. */
    private String lisOutcomeServiceUrl;

    /** Corresponds to <code>lis_person_source_did</code>. Optional. */
    private String lisPersonSourcedid;

    /** Corresponds to <code>lis_course_offering_source_did</code>. Optional. */
    private String lisCourseOfferingSourcedid;

    /** Corresponds to <code>lis_course_section_source_did</code>. Optional. */
    private String lisCourseSectionSourcedid;

    //-------------------------------------------------------------

    public LtiLaunchData() {
        this.roles = new HashSet<String>();
    }

    //-------------------------------------------------------------

    public String getLtiMessageType() {
        return ltiMessageType;
    }

    public void setLtiMessageType(final String ltiMessageType) {
        this.ltiMessageType = ltiMessageType;
    }


    public String getLtiVersion() {
        return ltiVersion;
    }

    public void setLtiVersion(final String ltiVersion) {
        this.ltiVersion = ltiVersion;
    }


    public String getResourceLinkId() {
        return resourceLinkId;
    }

    public void setResourceLinkId(final String resourceLinkId) {
        this.resourceLinkId = resourceLinkId;
    }


    public String getResourceLinkTitle() {
        return resourceLinkTitle;
    }

    public void setResourceLinkTitle(final String resourceLinkTitle) {
        this.resourceLinkTitle = resourceLinkTitle;
    }


    public String getResourceLinkDescription() {
        return resourceLinkDescription;
    }

    public void setResourceLinkDescription(final String resourceLinkDescription) {
        this.resourceLinkDescription = resourceLinkDescription;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }


    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void setRoles(final Collection<String> newRoles) {
        roles.clear();
        if (newRoles!=null) {
            roles.addAll(newRoles);
        }
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


    public String getContextId() {
        return contextId;
    }

    public void setContextId(final String contextId) {
        this.contextId = contextId;
    }


    public String getContextLabel() {
        return contextLabel;
    }

    public void setContextLabel(final String contextLabel) {
        this.contextLabel = contextLabel;
    }


    public String getContextTitle() {
        return contextTitle;
    }

    public void setContextTitle(final String contextTitle) {
        this.contextTitle = contextTitle;
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


    public String getToolConsumerInstanceName() {
        return toolConsumerInstanceName;
    }

    public void setToolConsumerInstanceName(final String toolConsumerInstanceName) {
        this.toolConsumerInstanceName = toolConsumerInstanceName;
    }


    public String getToolConsumerInstanceDescription() {
        return toolConsumerInstanceDescription;
    }

    public void setToolConsumerInstanceDescription(final String toolConsumerInstanceDescription) {
        this.toolConsumerInstanceDescription = toolConsumerInstanceDescription;
    }


    public String getLisResultSourcedid() {
        return lisResultSourcedid;
    }

    public void setLisResultSourcedid(final String lisResultSourcedid) {
        this.lisResultSourcedid = lisResultSourcedid;
    }


    public String getLisOutcomeServiceUrl() {
        return lisOutcomeServiceUrl;
    }

    public void setLisOutcomeServiceUrl(final String lisOutcomeServiceUrl) {
        this.lisOutcomeServiceUrl = lisOutcomeServiceUrl;
    }


    public String getLisPersonSourcedid() {
        return lisPersonSourcedid;
    }

    public void setLisPersonSourcedid(final String lisPersonSourcedid) {
        this.lisPersonSourcedid = lisPersonSourcedid;
    }


    public String getLisCourseOfferingSourcedid() {
        return lisCourseOfferingSourcedid;
    }

    public void setLisCourseOfferingSourcedid(final String lisCourseOfferingSourcedid) {
        this.lisCourseOfferingSourcedid = lisCourseOfferingSourcedid;
    }


    public String getLisCourseSectionSourcedid() {
        return lisCourseSectionSourcedid;
    }

    public void setLisCourseSectionSourcedid(final String lisCourseSectionSourcedid) {
        this.lisCourseSectionSourcedid = lisCourseSectionSourcedid;
    }

    //--------------------------------------------------------------------

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
