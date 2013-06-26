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

import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiContext;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiDomain;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.services.base.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiContextDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiResourceDao;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * FIXME: Document this type
 *
 * FIXME: Move authentication & decoding services into here once this is kind of working?
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class LtiLaunchService {

    private static final Logger logger = LoggerFactory.getLogger(LtiLaunchService.class);

    @Resource
    private LtiContextDao ltiContextDao;

    @Resource
    private LtiResourceDao ltiResourceDao;

    @Resource
    private DeliveryDao deliveryDao;

    /**
     * Returns the {@link LtiResource} for the given domain-level LTI launch encapsulated within
     * the {@link LtiLaunchResult}.
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
    public LtiResource provideLtiResource(final LtiLaunchResult ltiLaunchResult) {
        Assert.notNull(ltiLaunchResult, "ltiLaunchResult");
        final LtiLaunchData ltiLaunchData = ltiLaunchResult.getLtiLaunchData();
        final LtiUser ltiUser = ltiLaunchResult.getLtiUser();

        /* Make sure this a domain-level launch */
        final LtiDomain ltiDomain = ltiUser.getLtiDomain();
        if (ltiDomain==null) {
            throw new IllegalArgumentException("LTI launch is not a domain-level link");
        }

        /* Extract/create LtiContext (if context_id has been provided) */
        final LtiContext ltiContext = provideLtiContext(ltiDomain, ltiLaunchData);

        /* Now extract/create LtiResource */
        return provideLtiResource(ltiDomain, ltiContext, ltiLaunchData, ltiUser);
    }

    private LtiContext provideLtiContext(final LtiDomain ltiDomain, final LtiLaunchData ltiLaunchData) {
        final String contextId = ltiLaunchData.getContextId();
        if (contextId==null) {
            return null;
        }
        final String consumerKey = ltiDomain.getConsumerKey();
        LtiContext ltiContext = ltiContextDao.findByConsumerKeyAndContextId(consumerKey, contextId);
        if (ltiContext==null) {
            ltiContext = new LtiContext();
            ltiContext.setLtiDomain(ltiDomain);
            ltiContext.setContextId(contextId); /* FIXME: What should we do if this is too long to fit the column? */
            ltiContext.setContextLabel(ServiceUtilities.trimString(ltiLaunchData.getContextLabel(), DomainConstants.LTI_TOKEN_LENGTH));
            ltiContext.setContextTitle(ltiLaunchData.getContextTitle());
            ltiContextDao.persist(ltiContext);
            logger.info("Created new LtiContext {}", ltiContext);
        }
        return ltiContext;
    }

    private LtiResource provideLtiResource(final LtiDomain ltiDomain, final LtiContext ltiContext, final LtiLaunchData ltiLaunchData, final LtiUser ltiUser) {
        final String resourceLinkId = ltiLaunchData.getResourceLinkId();
        final String consumerKey = ltiDomain.getConsumerKey();
        LtiResource ltiResource = ltiResourceDao.findByConsumerKeyAndResourceLinkId(consumerKey, resourceLinkId);
        if (ltiResource==null) {
            if (!ltiUser.isInstructor()) {
                return null;
            }
            final Delivery delivery = new Delivery();
            delivery.setDeliveryType(DeliveryType.LTI_RESOURCE);
            delivery.setOpen(true);
            delivery.setTitle(StringUtilities.defaultIfNull(ltiLaunchData.getResourceLinkTitle(), "LTI Delivery " + resourceLinkId));
            deliveryDao.persist(delivery);
            logger.info("Created new Delivery for LTI resource: {}", delivery);

            ltiResource = new LtiResource();
            ltiResource.setLtiDomain(ltiDomain);
            ltiResource.setLtiContext(ltiContext); /* (May be null if context info is not provided) */
            ltiResource.setCreatorUser(ltiUser);
            ltiResource.setResourceLinkId(resourceLinkId);
            ltiResource.setResourceLinkTitle(ltiLaunchData.getResourceLinkTitle());
            ltiResource.setResourceLinkDescription(ltiLaunchData.getResourceLinkDescription());
            ltiResource.setDelivery(delivery);
            ltiResourceDao.persist(ltiResource);
            logger.info("Created new LtiResource {}", ltiResource);
        }
        return ltiResource;
    }
}
