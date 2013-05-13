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

import java.io.IOException;
import java.util.Arrays;

import net.oauth.OAuthMessage;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public final class LtiOauthMessageUtilities {

    public static LtiLaunchData extractLtiLaunchData(final OAuthMessage oauthMessage) throws IOException {
        final LtiLaunchData result = new LtiLaunchData();
        result.setLtiMessageType(oauthMessage.getParameter("lti_message_type")); /* Required */
        result.setLtiVersion(oauthMessage.getParameter("lti_version")); /* Required */
        result.setResourceLinkId(oauthMessage.getParameter("resource_link_id")); /* Required */
        result.setResourceLinkTitle(oauthMessage.getParameter("resource_link_title")); /* Recommended */
        result.setResourceLinkDescription(oauthMessage.getParameter("resource_link_description")); /* Recommended */
        result.setUserId(oauthMessage.getParameter("user_id")); /* Recommended */
        final String roles = oauthMessage.getParameter("roles"); /* Recommended, comma-separated */
        if (roles!=null) {
            result.setRoles(Arrays.asList(roles.split("\\s*,\\s*")));
        }
        result.setLisPersonNameFamily(oauthMessage.getParameter("lis_person_name_family")); /* Recommended but possibly suppressed */
        result.setLisPersonNameFull(oauthMessage.getParameter("lis_person_name_full")); /* Recommended but possibly suppressed */
        result.setLisPersonNameGiven(oauthMessage.getParameter("lis_person_name_given")); /* Recommended but possibly suppressed */
        result.setLisPersonContactEmailPrimary(oauthMessage.getParameter("lis_person_contact_email_primary")); /* Recommended but possibly suppressed */
        result.setContextId(oauthMessage.getParameter("context_id")); /* Recommended */
        result.setLaunchPresentationReturnUrl(oauthMessage.getParameter("launch_presentation_return_url")); /* Recommended */
        result.setToolConsumerInfoProductFamilyCode(oauthMessage.getParameter("tool_consumer_info_product_family_code")); /* Optional but recommended */
        result.setToolConsumerInfoVersion(oauthMessage.getParameter("tool_consumer_info_version")); /* Optional but recommended */
        result.setToolConsumerInstanceGuid(oauthMessage.getParameter("tool_consumer_instance_guid")); /* Optional but recommended */
        /* Result reporting parameters */
        result.setLisResultSourceDid(oauthMessage.getParameter("lis_result_sourcedid"));
        result.setLisOutcomeServiceUrl(oauthMessage.getParameter("lis_outcome_service_url"));
        result.setLisPersonSourceDid(oauthMessage.getParameter("lis_person_sourcedid"));
        result.setLisCourseOfferingSourceDid(oauthMessage.getParameter("lis_course_offering_sourcedid"));
        result.setLisCourseSectionSourceDid(oauthMessage.getParameter("lis_course_section_sourcedid"));
        return result;
    }

}
