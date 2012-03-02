package org.imsglobal.basiclti.provider.servlet.filter;
/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2011 IMS GLobal Learning Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. 
 *
 **********************************************************************************/
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthServiceProvider;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.server.OAuthServlet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.imsglobal.basiclti.consumersecret.api.ConsumerSecretService;
import org.imsglobal.basiclti.provider.api.LtiContext;
import org.imsglobal.basiclti.provider.servlet.util.LtiContextWebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * <p>The Basic LTI Security filter class enforces either a valid Basic LTI request to be present or a BasicLTI object to be present in the session.</p>
 * <p>The class {@link LtiContextWebUtil} is responsible for storing and retrieving the ${@link LtiContext} object in the session.</p>
 * 
 * @author Roland Groen (roland@edia.nl)
 *
 */
public class LtiSecurityFilter extends GenericFilterBean {
	protected FilterConfig filterConfig;

	private static final Logger logger = LoggerFactory.getLogger(LtiSecurityFilter.class);

	private LtiContext buildBasicLtiContext(OAuthMessage requestMessage) throws IOException {
		LtiContext context = new LtiContext();
		context.getUser().setId(requestMessage.getParameter("user_id"));
		context.getUser().setImage(requestMessage.getParameter("user_image"));
		String roles = requestMessage.getParameter("roles");
		if (org.apache.commons.lang.StringUtils.isNotEmpty(roles)) {
			context.getUser().setRoles(Arrays.asList(StringUtils.split(roles, ",")));
		}

		context.getContext().setId(requestMessage.getParameter("context_id"));
		context.getContext().setType(requestMessage.getParameter("context_type"));
		context.getContext().setTitle(requestMessage.getParameter("context_title"));
		context.getContext().setLabel(requestMessage.getParameter("context_label"));

		context.getResourceLink().setId(requestMessage.getParameter("resource_link_id"));
		context.getResourceLink().setTitle(requestMessage.getParameter("resource_link_title"));
		context.getResourceLink().setDescription(requestMessage.getParameter("resource_link_description"));

		context.getLisPerson().setNameFull(requestMessage.getParameter("lis_person_name_full"));
		context.getLisPerson().setNameGiven(requestMessage.getParameter("lis_person_name_given"));
		context.getLisPerson().setNameFamily(requestMessage.getParameter("lis_person_name_family"));
		context.getLisPerson().setContactEmailPrimary(requestMessage.getParameter("lis_person_contact_email_primary"));
		context.getLisPerson().setSourcedId(requestMessage.getParameter("lis_person_sourcedid"));

		context.getLisCourseOffering().setSourcedId(requestMessage.getParameter("lis_course_offering_sourcedid"));
		context.getLisCourseSection().setSourcedId(requestMessage.getParameter("lis_course_section_sourcedid"));
		context.getLisResult().setSourcedId(requestMessage.getParameter("lis_result_sourcedid"));

		context.getLaunchPresentation().setDocumentTarget(requestMessage.getParameter("launch_presentation_document_target"));
		context.getLaunchPresentation().setWidth(requestMessage.getParameter("launch_presentation_width"));
		context.getLaunchPresentation().setHeight(requestMessage.getParameter("launch_presentation_height"));
		context.getLaunchPresentation().setReturnUrl(requestMessage.getParameter("launch_presentation_return_url"));
		context.getLaunchPresentation().setLocale(requestMessage.getParameter("launch_presentation_locale"));

		context.getToolConsumerInstance().setGuid(requestMessage.getParameter("tool_consumer_instance_guid"));
		context.getToolConsumerInstance().setName(requestMessage.getParameter("tool_consumer_instance_name"));
		context.getToolConsumerInstance().setDescription(requestMessage.getParameter("tool_consumer_instance_description"));
		context.getToolConsumerInstance().setUrl(requestMessage.getParameter("tool_consumer_instance_url"));
		context.getToolConsumerInstance().setContactEmail(requestMessage.getParameter("tool_consumer_instance_contact_email"));
		
		context.getOauth().setConsumerKey(requestMessage.getParameter("oauth_consumer_key"));
		context.getOauth().setCallback(requestMessage.getParameter("oauth_callback"));
		context.getOauth().setNonce(requestMessage.getParameter("oauth_nonce"));
		context.getOauth().setSignature(requestMessage.getParameter("oauth_signature"));
		context.getOauth().setSignatureMethod(requestMessage.getParameter("oauth_signature_method"));
		context.getOauth().setTimestamp(NumberUtils.toLong(requestMessage.getParameter("oauth_timestamp"), 0));
		context.getOauth().setVersion(requestMessage.getParameter("oauth_version"));
		return context;
	}

	private void copyBasicLtiIntoSession(HttpServletRequest request, OAuthMessage requestMessage) throws OAuthProblemException, IOException {
		requestMessage.requireParameters("roles", "resource_link_id", "user_id", "context_id");
		HttpSession session = request.getSession(true);
		LtiContext ltiContext = buildBasicLtiContext(requestMessage);
		LtiContextWebUtil.setBasicLtiContext(session, ltiContext);
	}

	@Override
    public void destroy() {
		// Clean up..

	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpSession session = request.getSession(true);
		boolean isBasicLtiRequest = isBasicLtiRequest(request);
		if (!isBasicLtiRequest && isBasicLtiSet(session)) {
			chain.doFilter(request, response);
		} else {
			if (isBasicLtiRequest) {
				// handle
				handleBasicLtiRequest(request, response, chain);
			} else {
				(response).sendError(401, "Unauthorized - No BasicLTI session found.");
			}
		}

	}

	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
		} else {
			chain.doFilter(request, response);
		}
	}

	protected ConsumerSecretService getConsumerSecretService() throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		ConsumerSecretService bean = context.getBean(ConsumerSecretService.class.getName(), ConsumerSecretService.class);
		if (bean == null) {
			Map<String, ConsumerSecretService> beansOfType = context.getBeansOfType(ConsumerSecretService.class);
			if (beansOfType.size() == 1) {
				bean = beansOfType.values().iterator().next();
			} else if (beansOfType.size() > 1) {
				throw new ServletException("More than one bean found of type: " + ConsumerSecretService.class.getName());
			}
		}
		if (bean != null)
			return bean;
		throw new ServletException("Not able to locate a suitable bean to act as " + ConsumerSecretService.class.getName() + " please register an implementation of the "
		        + ConsumerSecretService.class.getName() + "!");
	}

	private void handleBasicLtiRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			OAuthMessage message = OAuthServlet.getMessage(request, null);
			if (logger.isDebugEnabled()) {
				List<Entry<String, String>> parameters = message.getParameters();
				for (Entry<String, String> entry : parameters) {
					logger.debug(entry.getKey() + ": " + entry.getValue());
				}
			}
            OAuthValidator oAuthValidator = new SimpleOAuthValidator();
            
            OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
            // try to load from local cache if not throw exception
            String consumerKey = message.getConsumerKey();
            String consumerSecret = getConsumerSecretService().getConsumerSecret(consumerKey);
            OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerSecret, serviceProvider);
            
            OAuthAccessor accessor = new OAuthAccessor(consumer);
            accessor.tokenSecret = "";
            oAuthValidator.validateMessage(message, accessor);
            copyBasicLtiIntoSession(request, message);
			chain.doFilter(request, response);
		} catch (OAuthProblemException e) {
			logger.warn("OAuthProblemException", e);
			(response).sendError(400, "Bad Request - Please submit a valid BasicLTI request.");
		} catch (OAuthException e) {
			logger.warn("OAuthException", e);
			(response).sendError(403, "Forbidden - Please submit a valid BasicLTI request.");
		} catch (URISyntaxException e) {
			logger.warn("URISyntaxException", e);
			throw new ServletException(e.getMessage());
		}
	}

	public boolean isBasicLtiRequest(HttpServletRequest request) {
		return "basic-lti-launch-request".equals(request.getParameter("lti_message_type"));
	}

	public boolean isBasicLtiSet(HttpSession session) {
		return LtiContextWebUtil.getBasicLtiContext(session) != null;
	}

}
