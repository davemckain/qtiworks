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

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.dao.ItemDeliveryDao;
import uk.ac.ed.ph.qtiworks.domain.dao.LtiUserDao;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;

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
        try {
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
            if (!itemDelivery.isOpen()) {
                httpResponse.sendError(403, "Delivery is not open");
                return;
            }
            if (!itemDelivery.isLtiEnabled()) {
                httpResponse.sendError(403, "Delivery is not LTI enabled");
                return;
            }

            final String consumerSecret = itemDelivery.getLtiConsumerSecret();
            final OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerSecret, serviceProvider);

            final OAuthAccessor accessor = new OAuthAccessor(consumer);
            accessor.tokenSecret = "";
            oAuthValidator.validateMessage(message, accessor);

            final LtiUser ltiUser = obtainLtiUser(message);
            doFilterWithIdentity(httpRequest, httpResponse, chain, ltiUser);
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
    }


    private ItemDelivery lookupItemDelivery(final String consumerKey) {
        final int separatorPos = consumerKey.indexOf('-');
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

    private LtiUser obtainLtiUser(final OAuthMessage requestMessage) throws IOException {
        final String resourceLinkId = requestMessage.getParameter("resource_link_id"); /* Required */
        final String contextId = requestMessage.getParameter("context_id"); /* Recommended */
        final String userId = requestMessage.getParameter("user_id"); /* Recommended */

        /* Create a unique key for looking up this user based on resource_link_id and user_id */
        String logicalKey;
        if (userId!=null) {
            logicalKey = resourceLinkId + "/" + userId; /* This will be unique */
        }
        else {
            /* No userId specified, so we'll have to synthesise something that will be unique enough */
            logicalKey = resourceLinkId + "/" + Thread.currentThread().getId()
                    + "/" + requestTimestampContext.getCurrentRequestTimestamp().getTime();
        }

        /* See if user already exists */
        LtiUser ltiUser = ltiUserDao.findByLogicalKey(logicalKey);
        if (ltiUser==null) {
            ltiUser = new LtiUser();
            ltiUser.setLogicalKey(logicalKey);
            ltiUser.setLtiResourceLinkId(resourceLinkId);
            ltiUser.setLtiContextId(contextId);
            ltiUser.setLtiUserId(userId);
            ltiUser.setLisFullName(requestMessage.getParameter("lis_person_name_full"));
            ltiUser.setLisGivenName(requestMessage.getParameter("lis_person_name_given"));
            ltiUser.setLisFamilyName(requestMessage.getParameter("lis_person_name_family"));
            ltiUser.setLisContactEmailPrimary(requestMessage.getParameter("lis_person_contact_email_primary"));
            ltiUserDao.persist(ltiUser);
        }

        logger.info("Obtained LTI user {}", ltiUser);
        return ltiUser;
    }

    private void doFilterWithIdentity(final HttpServletRequest httpRequest,
            final HttpServletResponse httpResponse, final FilterChain chain,
            final LtiUser ltiUser)
            throws IOException, ServletException {
        identityContext.setCurrentThreadUnderlyingIdentity(ltiUser);
        identityContext.setCurrentThreadEffectiveIdentity(ltiUser);
        try {
            chain.doFilter(httpRequest, httpResponse);
        }
        finally {
            identityContext.setCurrentThreadUnderlyingIdentity(null);
            identityContext.setCurrentThreadEffectiveIdentity(null);
        }
    }

}
