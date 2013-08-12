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

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiContext;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiDomain;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiLaunchType;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiContextDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiDomainDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiResourceDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiUserDao;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.oauth.OAuth.Problems;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling LTI launches.
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class LtiLaunchService {

    private static final Logger logger = LoggerFactory.getLogger(LtiLaunchService.class);

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private LtiUserDao ltiUserDao;

    @Resource
    private LtiDomainDao ltiDomainDao;

    @Resource
    private LtiContextDao ltiContextDao;

    @Resource
    private LtiResourceDao ltiResourceDao;

    /**
     * Extracts the LTI launch data from the given {@link HttpServletRequest}, returning a
     * {@link DecodedLtiLaunch} encapsulating the resulting launch data.
     * <p>
     * An {@link LtiUser} corresponding to the launch will be created or reused as required.
     *
     * @param request
     * @param ltiLaunchType
     * @return
     * @throws IOException
     */
    public DecodedLtiLaunch decodeLtiLaunchData(final HttpServletRequest request, final LtiLaunchType ltiLaunchType)
            throws IOException {
        /* Log OAuth parameters for debugging purposes */
        final OAuthMessage oauthMessage = OAuthServlet.getMessage(request, null);
        if (logger.isDebugEnabled()) {
            final List<Entry<String, String>> parameters = oauthMessage.getParameters();
            for (final Entry<String, String> entry : parameters) {
                logger.debug("OAuth message parameter {} => {}", entry.getKey(), entry.getValue());
            }
        }

        /* Extract the data we're interested in. (This is validated below) */
        final LtiLaunchData ltiLaunchData = LtiLaunchService.extractLtiLaunchData(oauthMessage);
        logger.info("Extracted LTI Launch data {}", ltiLaunchData);

        /* Make sure the OAuth consumer key has been provided */
        final String consumerKey = oauthMessage.getConsumerKey();
        if (consumerKey==null) {
            logger.warn("Consumer key missing in message {}", oauthMessage);
            return new DecodedLtiLaunch(ltiLaunchData, SC_BAD_REQUEST, "Bad OAuth request: consumer key is missing");
        }

        /* Make sure this is a supported LTI launch message */
        if (!LtiLaunchService.isBasicLtiLaunchRequest(ltiLaunchData)) {
            logger.warn("Unsupported LTI message type in {}", ltiLaunchData);
            return new DecodedLtiLaunch(ltiLaunchData, SC_BAD_REQUEST, "Unsupported LTI message type " + ltiLaunchData.getLtiMessageType());
        }

        /* Extract/create LtiUser for this launch */
        try {
            switch (ltiLaunchType) {
                case DOMAIN:
                    final LtiDomain ltiDomain = ltiDomainDao.findByConsumerKey(consumerKey);
                    if (ltiDomain==null) {
                        return new DecodedLtiLaunch(ltiLaunchData, SC_NOT_FOUND, "Your Tool Consumer has not been registered with this instance of QTIWorks");
                    }
                    return new DecodedLtiLaunch(ltiLaunchData, handleDomainLaunch(oauthMessage, ltiLaunchData, ltiDomain));

                case LINK:
                    final Delivery delivery = lookupDelivery(consumerKey);
                    if (delivery==null) {
                        return new DecodedLtiLaunch(ltiLaunchData, SC_NOT_FOUND, "This LTI link-level launch sent by your Tool Consumer was not recognised");
                    }
                    return new DecodedLtiLaunch(ltiLaunchData, handleLinkLaunch(oauthMessage, ltiLaunchData, delivery));

                default:
                    throw new QtiWorksLogicException("Unexpected switch case " + ltiLaunchType);
            }
        }
        catch (final OAuthProblemException e) {
            logger.warn("OAuth message validation resulted in OAuthProblemException", e);
            final String problem = e.getProblem();
            if (Problems.SIGNATURE_INVALID.equals(problem)) {
                return new DecodedLtiLaunch(ltiLaunchData, SC_FORBIDDEN, "Your LTI tool consumer sent QTIWorks an incorrectly-signed OAuth message.");
            }
            return new DecodedLtiLaunch(ltiLaunchData, SC_FORBIDDEN, "Your LTI tool consumer sent QTIWorks an OAuth message that could not be accepted. The problem was '" + problem + "'.");
        }
        catch (final OAuthException e) {
            logger.warn("OAuth message validation resulted in OAuthException", e);
            return new DecodedLtiLaunch(ltiLaunchData, SC_BAD_REQUEST, "Your LTI tool consumer sent QTIWorks a bad OAuth message.");
        }
        catch (final URISyntaxException e) {
            logger.warn("OAuth message validation resulted in URISyntaxException", e);
            return new DecodedLtiLaunch(ltiLaunchData, SC_BAD_REQUEST, "Your LTI tool consumer sent QTIWorks a bad OAuth message.");
        }
    }

    public static boolean isBasicLtiLaunchRequest(final HttpServletRequest request) {
        return "basic-lti-launch-request".equals(request.getParameter("lti_message_type"));
    }

    public static boolean isBasicLtiLaunchRequest(final LtiLaunchData ltiLaunchData) {
        return "basic-lti-launch-request".equals(ltiLaunchData.getLtiMessageType());
    }

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
        result.setContextLabel(oauthMessage.getParameter("context_label")); /* Recommended */
        result.setContextTitle(oauthMessage.getParameter("context_title")); /* Recommended */
        result.setLaunchPresentationReturnUrl(oauthMessage.getParameter("launch_presentation_return_url")); /* Recommended */
        result.setToolConsumerInfoProductFamilyCode(oauthMessage.getParameter("tool_consumer_info_product_family_code")); /* Optional but recommended */
        result.setToolConsumerInfoVersion(oauthMessage.getParameter("tool_consumer_info_version")); /* Optional but recommended */
        result.setToolConsumerInstanceGuid(oauthMessage.getParameter("tool_consumer_instance_guid")); /* Optional but recommended */
        result.setToolConsumerInstanceName(oauthMessage.getParameter("tool_consumer_instance_name")); /* Recommended */
        result.setToolConsumerInstanceDescription(oauthMessage.getParameter("tool_consumer_instance_description")); /* Optional */
        /* Result reporting parameters */
        result.setLisResultSourcedid(oauthMessage.getParameter("lis_result_sourcedid"));
        result.setLisOutcomeServiceUrl(oauthMessage.getParameter("lis_outcome_service_url"));
        result.setLisPersonSourcedid(oauthMessage.getParameter("lis_person_sourcedid"));
        result.setLisCourseOfferingSourcedid(oauthMessage.getParameter("lis_course_offering_sourcedid"));
        result.setLisCourseSectionSourcedid(oauthMessage.getParameter("lis_course_section_sourcedid"));
        return result;
    }

    private LtiUser handleDomainLaunch(final OAuthMessage oauthMessage, final LtiLaunchData ltiLaunchData, final LtiDomain ltiDomain)
            throws IOException, OAuthException, URISyntaxException {
        final String consumerKey = ltiDomain.getConsumerKey();
        final String consumerSecret = ltiDomain.getConsumerSecret();
        validateOAuthMessage(oauthMessage, consumerKey, consumerSecret);
        return obtainDomainLevelLtiUser(ltiDomain, ltiLaunchData);
    }

    private LtiUser handleLinkLaunch(final OAuthMessage oauthMessage, final LtiLaunchData ltiLaunchData, final Delivery delivery)
            throws IOException, OAuthException, URISyntaxException {
        final String consumerKey = delivery.getLtiConsumerKeyToken();
        final String consumerSecret = delivery.getLtiConsumerSecret();
        validateOAuthMessage(oauthMessage, consumerKey, consumerSecret);
        return obtainLinkLevelLtiUser(delivery, ltiLaunchData);
    }

    private void validateOAuthMessage(final OAuthMessage oauthMessage, final String consumerKey, final String consumerSecret)
            throws IOException, OAuthException, URISyntaxException {
        final OAuthValidator oAuthValidator = new SimpleOAuthValidator();
        final OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
        final OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerSecret, serviceProvider);
        final OAuthAccessor accessor = new OAuthAccessor(consumer);
        oAuthValidator.validateMessage(oauthMessage, accessor);
    }

    private Delivery lookupDelivery(final String consumerKey) {
        final int separatorPos = consumerKey.indexOf('X');
        if (separatorPos==-1) {
            logger.debug("Unsupported syntax in LTI consumer key {}", consumerKey);
            return null;
        }
        final String deliveryIdString = consumerKey.substring(0, separatorPos);
        final String tokenPart = consumerKey.substring(separatorPos+1);
        final long deliveryId;
        try {
            deliveryId = Long.parseLong(deliveryIdString);
        }
        catch (final NumberFormatException e) {
            logger.debug("Could not parse delivery ID {} from LTI consumer key {}", deliveryIdString, consumerKey);
            return null;
        }
        /* Look up delivery */
        final Delivery delivery = deliveryDao.findById(deliveryId);
        if (delivery==null) {
            logger.debug("Delivery with ID {} extracted from LTI consumer key {} not found", deliveryId, consumerKey);
            return null;
        }
        /* Check its key token */
        if (!tokenPart.equals(delivery.getLtiConsumerKeyToken())) {
            logger.debug("Token part {} of LTI consumer key {} did not match {}",
                    new Object[] { tokenPart, consumerKey, delivery.getLtiConsumerKeyToken() });
        }

        /* Successful verification */
        return delivery;
    }

    private LtiUser obtainDomainLevelLtiUser(final LtiDomain ltiDomain, final LtiLaunchData ltiLaunchData) {
        final UserRole userRole = mapLtiRole(ltiLaunchData);
        final String userId = ltiLaunchData.getUserId();
        LtiUser result = null;
        if (userId!=null) {
            /* Try for a user having the provided user_id and role (in the context of this Delivery) */
            result = ltiUserDao.findByLtiDomainLtiUserIdAndUserRole(ltiDomain, userId, userRole);
        }
        if (result==null) {
            /* No user found, or no user_id provided */
            result = createDomainLevelLtiUser(ltiDomain, ltiLaunchData, userRole);
        }
        return result;
    }

    private LtiUser obtainLinkLevelLtiUser(final Delivery delivery, final LtiLaunchData ltiLaunchData) {
        final String userId = ltiLaunchData.getUserId();
        LtiUser result = null;
        if (userId!=null) {
            /* Try for a user having the provided user_id and role (in the context of this Delivery) */
            result = ltiUserDao.findByDeliveryAndLtiUserId(delivery, userId);
        }
        if (result==null) {
            /* No user found, or no user_id provided */
            result = createLinkLevelLtiUser(delivery, ltiLaunchData);
        }
        return result;
    }

    private LtiUser createDomainLevelLtiUser(final LtiDomain ltiDomain, final LtiLaunchData ltiLaunchData, final UserRole userRole) {
        /* Create suitable logical key */
        final String userId = ltiLaunchData.getUserId();
        final String logicalKey;
        if (userId!=null && userId.length() < DomainConstants.LTI_TOKEN_LENGTH) {
            /* The following key will be unique for a particular domain, role and TC */
            logicalKey = "domain/" + ltiDomain.getId() + "/" + userRole + "/" + userId;
        }
        else {
            /* No user_id specified or tool long, so we'll have to synthesise something unique */
            logicalKey = "domain/" + ltiDomain.getId() + "/" + userRole + "-" + Thread.currentThread().getId()
                    + "-" + requestTimestampContext.getCurrentRequestTimestamp().getTime();
        }

        /* Create user */
        final LtiUser ltiUser = new LtiUser();
        ltiUser.setUserRole(userRole);
        ltiUser.setLtiLaunchType(LtiLaunchType.DOMAIN);
        ltiUser.setDelivery(null); /* (Not a link-level launch) */
        ltiUser.setLtiDomain(ltiDomain);
        ltiUser.setLogicalKey(logicalKey);
        ltiUser.setFirstName(ServiceUtilities.safelyTrimString(ltiLaunchData.getLisPersonNameGiven(), "Name", DomainConstants.USER_NAME_COMPONENT_MAX_LENGTH));
        ltiUser.setLastName(ServiceUtilities.safelyTrimString(ltiLaunchData.getLisPersonNameFamily(), "Not Provided", DomainConstants.USER_NAME_COMPONENT_MAX_LENGTH));
        ltiUser.setEmailAddress(ServiceUtilities.safelyTrimString(ltiLaunchData.getLisPersonContactEmailPrimary(), DomainConstants.USER_EMAIL_ADDRESS_MAX_LENGTH));
        ltiUser.setLtiUserId(ServiceUtilities.safelyTrimString(userId, DomainConstants.LTI_TOKEN_LENGTH)); /* (May be null, trimmed if too long, so not necessarily unique) */
        ltiUser.setLisFullName(trimLisPersonFullName(ltiLaunchData.getLisPersonNameFull())); /* (May be null) */
        ltiUserDao.persist(ltiUser);
        return ltiUser;
    }

    private LtiUser createLinkLevelLtiUser(final Delivery delivery, final LtiLaunchData ltiLaunchData) {
        /* Create suitable logical key */
        final String logicalKey;
        final String userId = ltiLaunchData.getUserId();
        if (userId!=null && userId.length() < DomainConstants.LTI_TOKEN_LENGTH) {
            /* The following key will be unique for a particular Delivery, role and TC */
            logicalKey = "link/" + delivery.getId() + "/" + userId;
        }
        else {
            /* No user_id specified or too long, so we'll have to synthesise something unique */
            logicalKey = "link/" + delivery.getId() + "-" + Thread.currentThread().getId()
                    + "-" + requestTimestampContext.getCurrentRequestTimestamp().getTime();
        }

        /* Create user */
        final LtiUser ltiUser = new LtiUser();
        ltiUser.setUserRole(UserRole.CANDIDATE); /* (These launches will always be candidate launches) */
        ltiUser.setLtiLaunchType(LtiLaunchType.LINK);
        ltiUser.setDelivery(delivery);
        ltiUser.setLtiDomain(null); /* (Not a domain-level launch) */
        ltiUser.setLogicalKey(logicalKey);
        ltiUser.setFirstName(ServiceUtilities.safelyTrimString(ltiLaunchData.getLisPersonNameGiven(), "Name", DomainConstants.USER_NAME_COMPONENT_MAX_LENGTH));
        ltiUser.setLastName(ServiceUtilities.safelyTrimString(ltiLaunchData.getLisPersonNameFamily(), "Not Provided", DomainConstants.USER_NAME_COMPONENT_MAX_LENGTH));
        ltiUser.setEmailAddress(ServiceUtilities.safelyTrimString(ltiLaunchData.getLisPersonContactEmailPrimary(), DomainConstants.USER_EMAIL_ADDRESS_MAX_LENGTH));
        ltiUser.setLtiUserId(ServiceUtilities.safelyTrimString(userId, DomainConstants.LTI_TOKEN_LENGTH)); /* (May be null, trimmed if too long, so not necessarily unique) */
        ltiUser.setLisFullName(trimLisPersonFullName(ltiLaunchData.getLisPersonNameFull())); /* (May be null) */
        ltiUserDao.persist(ltiUser);
        return ltiUser;
    }

    /**
     * Blackboard includes lots of redundant spaces in <code>lis_person_full_name</code>. This
     * method tidies things up a bit.
     */
    private static String trimLisPersonFullName(final String lisPersonFullName) {
        if (lisPersonFullName==null) {
            return null;
        }
        return lisPersonFullName.trim().replaceAll("\\s+", " ");
    }

    private static UserRole mapLtiRole(final LtiLaunchData ltiLaunchData) {
        return hasInstructorRole(ltiLaunchData) ? UserRole.INSTRUCTOR : UserRole.CANDIDATE;
    }

    private static boolean hasInstructorRole(final LtiLaunchData ltiLaunchData) {
        final Set<String> roles = ltiLaunchData.getRoles();
        if (roles!=null) {
            for (final String role : roles) {
                /* We're just testing LIS Context roles for now. See Appendix A.2 of the LTI spec */
                if (role.startsWith("urn:lti:role:ims/lis/Instructor")) {
                    return true;
                }
                /* (CHECK: Hmm... some VLEs don't seem to be sending URIs at all!) */
                else if (role.equals("Instructor")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the {@link LtiResource} for the given domain-level LTI launch encapsulated within
     * the {@link DecodedLtiLaunch}.
     * <p>
     * If the resource specified in the launch has never been accessed before - and if the caller
     * has {@link UserRole#INSTRUCTOR} - then the {@link LtiResource} and corresponding {@link Delivery}
     * will be created.
     * <p>
     * Returns null is the resource specified in the launch hasn't been initialised yet and if the
     * caller does not have {@link UserRole#INSTRUCTOR}.
     *
     * @param ltiDomain
     * @param ltiLaunchResult
     * @return
     *
     * @throws IllegalArgumentException if the LTI launch is not a domain-level link.
     */
    public LtiResource provideLtiResource(final DecodedLtiLaunch ltiLaunchResult) {
        Assert.notNull(ltiLaunchResult, "ltiLaunchResult");
        final LtiLaunchData ltiLaunchData = ltiLaunchResult.getLtiLaunchData();
        final LtiUser ltiUser = ltiLaunchResult.getLtiUser();

        /* Make sure this a domain-level launch */
        final LtiDomain ltiDomain = ltiUser.getLtiDomain();
        if (ltiDomain==null) {
            throw new IllegalArgumentException("LTI launch is not a domain-level link");
        }

        /* Extract/create LtiContext */
        final LtiContext ltiContext = provideLtiContext(ltiDomain, ltiLaunchData);

        /* Now extract/create LtiResource */
        return provideLtiResource(ltiContext, ltiLaunchData, ltiUser);
    }

    private LtiContext provideLtiContext(final LtiDomain ltiDomain, final LtiLaunchData ltiLaunchData) {
        final String consumerKey = ltiDomain.getConsumerKey();
        final String contextId = ltiLaunchData.getContextId();
        LtiContext ltiContext;
        if (contextId!=null) {
            /* TP has sent context info (which is great) */
            ltiContext = ltiContextDao.findByConsumerKeyAndContextId(consumerKey, contextId);
            if (ltiContext==null) {
                ltiContext = new LtiContext();
                ltiContext.setLtiDomain(ltiDomain);
                ltiContext.setContextId(contextId); /* FIXME: What should we do if this is too long to fit the column? */
                ltiContext.setContextLabel(ServiceUtilities.trimString(ltiLaunchData.getContextLabel(), DomainConstants.LTI_TOKEN_LENGTH));
                ltiContext.setContextTitle(ltiLaunchData.getContextTitle());
                ltiContextDao.persist(ltiContext);
                logger.info("Created new LtiContext {}", ltiContext);
            }
        }
        else {
            /* TP hasn't sent context info, so we'll create a fake resource-specific context
             * just to keep the ownership model intact.
             */
            final String resourceLinkId = ltiLaunchData.getResourceLinkId();
            ltiContext = ltiContextDao.findByConsumerKeyAndFallbackResourceLinkId(consumerKey, resourceLinkId);
            if (ltiContext==null) {
                ltiContext = new LtiContext();
                ltiContext.setLtiDomain(ltiDomain);
                ltiContext.setFallbackResourceLinkId(resourceLinkId);  /* FIXME: What if too long for the column? */
                ltiContextDao.persist(ltiContext);
                logger.info("Created new (fake) LtiContext {}", ltiContext);
            }
        }
        return ltiContext;
    }

    private LtiResource provideLtiResource(final LtiContext ltiContext, final LtiLaunchData ltiLaunchData, final LtiUser ltiUser) {
        final String resourceLinkId = ltiLaunchData.getResourceLinkId();
        final LtiDomain ltiDomain = ltiContext.getLtiDomain();
        LtiResource ltiResource = ltiResourceDao.findByLtiDomainAndResourceLinkId(ltiDomain, resourceLinkId);
        if (ltiResource==null) {
            if (!ltiUser.isInstructor()) {
                return null;
            }
            final Delivery delivery = new Delivery();
            delivery.setDeliveryType(DeliveryType.LTI_RESOURCE);
            delivery.setOpen(false); /* (Require instructor to explicitly turn on when ready) */
            delivery.setTitle(StringUtilities.defaultIfNull(ltiLaunchData.getResourceLinkTitle(), "LTI Delivery " + resourceLinkId));
            deliveryDao.persist(delivery);
            logger.info("Created new Delivery for LTI resource: {}", delivery);

            ltiResource = new LtiResource();
            ltiResource.setLtiContext(ltiContext);
            ltiResource.setCreatorUser(ltiUser);
            ltiResource.setResourceLinkId(resourceLinkId); /* FIXME: What if too long for the column? */
            ltiResource.setResourceLinkTitle(ltiLaunchData.getResourceLinkTitle());
            ltiResource.setResourceLinkDescription(ltiLaunchData.getResourceLinkDescription());
            ltiResource.setToolConsumerInfoProductFamilyCode(ServiceUtilities.safelyTrimString(ltiLaunchData.getToolConsumerInfoProductFamilyCode(), DomainConstants.LTI_TOKEN_LENGTH));
            ltiResource.setToolConsumerInfoVersion(ServiceUtilities.safelyTrimString(ltiLaunchData.getToolConsumerInfoVersion(), DomainConstants.LTI_TOKEN_LENGTH));
            ltiResource.setToolConsumerInstanceName(ltiLaunchData.getToolConsumerInstanceName());
            ltiResource.setToolConsumerInstanceDescription(ltiLaunchData.getToolConsumerInstanceDescription());
            ltiResource.setDelivery(delivery);
            ltiResourceDao.persist(ltiResource);
            logger.info("Created new LtiResource {}", ltiResource);
        }
        return ltiResource;
    }
}
