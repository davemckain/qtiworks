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

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.dao.ItemDeliveryDao;
import uk.ac.ed.ph.qtiworks.domain.dao.LtiUserDao;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.web.authn.AbstractWebAuthenticationFilter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * Filter authenticating LTI requests
 *
 * @author David McKain
 */
public final class LtiAuthenticationFilter extends AbstractWebAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(LtiAuthenticationFilter.class);

    public static final String LTI_LAUNCH_DATA_REQUEST_PARAMETER = "qtiworks.web.lti.launch.data";

    private RequestTimestampContext requestTimestampContext;
    private IdentityContext identityContext;
    private ItemDeliveryDao itemDeliveryDao;
    private LtiUserDao ltiUserDao;

    @Override
    protected void initWithApplicationContext(final FilterConfig filterConfig, final WebApplicationContext webApplicationContext)
            throws Exception {
        requestTimestampContext = webApplicationContext.getBean(RequestTimestampContext.class);
        identityContext = webApplicationContext.getBean(IdentityContext.class);
        itemDeliveryDao = webApplicationContext.getBean(ItemDeliveryDao.class);
        ltiUserDao = webApplicationContext.getBean(LtiUserDao.class);
    }

    @Override
    protected void doFilterAuthenticated(final HttpServletRequest httpRequest,
            final HttpServletResponse httpResponse, final FilterChain chain,
            final HttpSession session)
            throws IOException, ServletException {
        final boolean isBasicLtiRequest = isBasicLtiLaunchRequest(httpRequest);
        if (isBasicLtiRequest) {
            handleBasicLtiRequest(httpRequest, httpResponse, chain);
        }
        else {
            httpResponse.sendError(401, "Not an LTI launch request");
        }
    }

    private boolean isBasicLtiLaunchRequest(final HttpServletRequest request) {
        return "basic-lti-launch-request".equals(request.getParameter("lti_message_type"));
    }

    private void handleBasicLtiRequest(final HttpServletRequest httpRequest,
            final HttpServletResponse httpResponse,
            final FilterChain chain) throws IOException, ServletException {
        final OAuthMessage message = OAuthServlet.getMessage(httpRequest, null);
        if (logger.isDebugEnabled()) {
            final List<Entry<String, String>> parameters = message.getParameters();
            for (final Entry<String, String> entry : parameters) {
                logger.debug("LTI parameter {} => {}", entry.getKey(), entry.getValue());
            }
        }
        final OAuthValidator oAuthValidator = new SimpleOAuthValidator();
        final OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);

        /* Look up Delivery corresponding to this consumer key */
        final String consumerKey = message.getConsumerKey();
        final ItemDelivery itemDelivery = lookupItemDelivery(consumerKey);
        if (itemDelivery==null) {
            httpResponse.sendError(403, "Forbidden - bad consumer key");
            return;
        }
        if (!itemDelivery.isLtiEnabled()) {
            httpResponse.sendError(403, "Forbidden - delivery is not LTI enabled");
            return;
        }

        final String consumerSecret = itemDelivery.getLtiConsumerSecret();
        final OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerSecret, serviceProvider);
        final OAuthAccessor accessor = new OAuthAccessor(consumer);
        accessor.tokenSecret = "";
        try {
            oAuthValidator.validateMessage(message, accessor);
        }
        catch (final OAuthProblemException e) {
            /* FIXME: Log this better */
            logger.warn("OAuthProblemException", e);
            httpResponse.sendError(400, "Bad Request - Please submit a valid BasicLTI request.");
        }
        catch (final OAuthException e) {
            /* FIXME: Log this better */
            logger.warn("OAuthException", e);
            httpResponse.sendError(403, "Forbidden - Please submit a valid BasicLTI request.");
        }
        catch (final URISyntaxException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }

        /* Extract launch data */
        final LtiLaunchData ltiLaunchData = extractLtiLaunchData(message);
        final LtiUser ltiUser = obtainLtiUser(itemDelivery, ltiLaunchData);

        /* Continue... */
        doFilterWithIdentity(httpRequest, httpResponse, chain, ltiLaunchData, ltiUser);
    }

    private ItemDelivery lookupItemDelivery(final String consumerKey) {
        final int separatorPos = consumerKey.indexOf('X');
        if (separatorPos==-1) {
            logger.info("Unsupported syntax in LTI consumer key {}", consumerKey);
            return null;
        }
        final String deliveryIdString = consumerKey.substring(0, separatorPos);
        final String tokenPart = consumerKey.substring(separatorPos+1);
        final long deliveryId;
        try {
            deliveryId = Long.parseLong(deliveryIdString);
        }
        catch (final NumberFormatException e) {
            logger.info("Could not parse delivery ID {} from LTI consumer key {}", deliveryIdString, consumerKey);
            return null;
        }
        /* Look up delivery */
        final ItemDelivery itemDelivery = itemDeliveryDao.findById(deliveryId);
        logger.info("Looked up {}", itemDelivery);
        if (itemDelivery==null) {
            logger.info("Delivery with ID {} extracted from LTI consumer key {} not found", deliveryId, consumerKey);
            return null;
        }
        /* Check its key token */
        if (!tokenPart.equals(itemDelivery.getLtiConsumerKeyToken())) {
            logger.info("Token part {} of LTI consumer key {} did not match {}",
                    new Object[] { tokenPart, consumerKey, itemDelivery.getLtiConsumerKeyToken() });
        }

        /* Successful verification */
        return itemDelivery;
    }

    private LtiLaunchData extractLtiLaunchData(final OAuthMessage requestMessage) throws IOException {
        final LtiLaunchData data = new LtiLaunchData();
        data.setResourceLinkId(requestMessage.getParameter("resource_link_id")); /* Required */
        data.setContextId(requestMessage.getParameter("context_id")); /* Recommended */
        data.setLaunchPresentationReturnUrl(requestMessage.getParameter("launch_presentation_return_url")); /* Recommended */
        data.setToolConsumerInfoProductFamilyCode(requestMessage.getParameter("tool_consumer_info_product_family_code")); /* Optional but recommended */
        data.setToolConsumerInfoVersion(requestMessage.getParameter("tool_consumer_info_version")); /* Optional but recommended */
        data.setToolConsumerInstanceGuid(requestMessage.getParameter("tool_consumer_instance_guid")); /* Optional but recommended */
        data.setUserId(requestMessage.getParameter("user_id")); /* Recommended */
        data.setLisPersonNameFamily(requestMessage.getParameter("lis_person_name_family")); /* Recommended but possibly suppressed */
        data.setLisPersonNameFull(requestMessage.getParameter("lis_person_name_full")); /* Recommended but possibly suppressed */
        data.setLisPersonNameGiven(requestMessage.getParameter("lis_person_name_given")); /* Recommended but possibly suppressed */
        data.setLisPersonContactEmailPrimary(requestMessage.getParameter("lis_person_contact_email_primary")); /* Recommended but possibly suppressed */
        return data;
    }

    private LtiUser obtainLtiUser(final ItemDelivery itemDelivery, final LtiLaunchData ltiLaunchData) {
        /* Create a unique key for this user. Uniqueness will be enforced within deliveries too */
        String logicalKey;
        final String userId = ltiLaunchData.getUserId();
        if (userId!=null) {
            logicalKey = itemDelivery.getId() + "/" + userId; /* This will be unique */
        }
        else {
            /* No userId specified, so we'll have to synthesise something that will be unique enough */
            logicalKey = itemDelivery.getId() + "/" + Thread.currentThread().getId()
                    + "/" + requestTimestampContext.getCurrentRequestTimestamp().getTime();
        }

        /* See if user already exists */
        LtiUser ltiUser = ltiUserDao.findByLogicalKey(logicalKey);
        if (ltiUser==null) {
            ltiUser = new LtiUser();
            ltiUser.setLogicalKey(logicalKey);
            ltiUser.setLtiUserId(userId);
            ltiUser.setLisFullName(ltiLaunchData.getLisPersonNameFull());
            ltiUser.setLisGivenName(ltiLaunchData.getLisPersonNameGiven());
            ltiUser.setLisFamilyName(ltiLaunchData.getLisPersonNameFamily());
            ltiUser.setLisContactEmailPrimary(ltiLaunchData.getLisPersonContactEmailPrimary());
            ltiUserDao.persist(ltiUser);
        }

        logger.info("Obtained LTI user {}", ltiUser);
        return ltiUser;
    }

    private void doFilterWithIdentity(final HttpServletRequest httpRequest,
            final HttpServletResponse httpResponse, final FilterChain chain,
            final LtiLaunchData ltiLaunchData, final LtiUser ltiUser)
            throws IOException, ServletException {
        identityContext.setCurrentThreadUnderlyingIdentity(ltiUser);
        identityContext.setCurrentThreadEffectiveIdentity(ltiUser);
        putLaunchData(httpRequest, ltiLaunchData);
        logger.info("LTI authentication successful: launch data is {} and user is {}", ltiLaunchData, ltiUser);
        try {
            chain.doFilter(httpRequest, httpResponse);
        }
        finally {
            identityContext.setCurrentThreadUnderlyingIdentity(null);
            identityContext.setCurrentThreadEffectiveIdentity(null);
        }
    }

    private void putLaunchData(final HttpServletRequest httpRequest, final LtiLaunchData ltiLaunchData) {
        httpRequest.setAttribute(LTI_LAUNCH_DATA_REQUEST_PARAMETER, ltiLaunchData);
    }

    public static LtiLaunchData getLaunchData(final HttpServletRequest httpRequest) {
        final LtiLaunchData ltiLaunchData = (LtiLaunchData) httpRequest.getAttribute(LTI_LAUNCH_DATA_REQUEST_PARAMETER);
        if (ltiLaunchData==null) {
            throw new QtiWorksRuntimeException("Expected to find LtiLaunchData in HttpRequest as attribute " + LTI_LAUNCH_DATA_REQUEST_PARAMETER);
        }
        return ltiLaunchData;
    }
}
