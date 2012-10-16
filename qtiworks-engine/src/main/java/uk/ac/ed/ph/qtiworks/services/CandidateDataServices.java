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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.binding.ItemSesssionStateXmlMarshaller;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemEventDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemEventNotificationDao;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventNotification;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.utils.XmlUtilities;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.notification.Notification;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlSourceLocationInformation;

import uk.ac.ed.ph.snuggletex.XMLStringOutputOptions;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;

import java.io.StringReader;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Low level services for manipulating candidate data, such as recording
 * {@link CandidateItemEvent}s.
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.SUPPORTS)
public class CandidateDataServices {

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private EntityGraphService entityGraphService;

    @Resource
    private AssessmentObjectManagementService assessmentObjectManagementService;

    @Resource
    private CandidateItemEventDao candidateItemEventDao;

    @Resource
    private CandidateItemEventNotificationDao candidateItemEventNotificationDao;

    @Resource
    private JqtiExtensionManager jqtiExtensionManager;

    public String marshalItemSessionState(final ItemSessionState itemSessionState) {
        final Document marshalledState = ItemSesssionStateXmlMarshaller.marshal(itemSessionState);
        final XMLStringOutputOptions xmlOptions = new XMLStringOutputOptions();
        xmlOptions.setIndenting(true);
        xmlOptions.setIncludingXMLDeclaration(false);
        return XMLUtilities.serializeNode(marshalledState, xmlOptions);
    }

    public ItemSessionState unmarshalItemSessionState(final CandidateItemEvent event) {
        final String itemSessionStateXml = event.getItemSessionStateXml();
        final DocumentBuilder documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
        Document doc;
        try {
            doc = documentBuilder.parse(new InputSource(new StringReader(itemSessionStateXml)));
        }
        catch (final Exception e) {
            throw new QtiWorksLogicException("Could not parse ItemSessionState XML. This is an internal error as we currently don't expose this data to clients", e);
        }
        return ItemSesssionStateXmlMarshaller.unmarshal(doc);
    }

    public CandidateItemEvent recordCandidateItemEvent(final CandidateSession candidateItemSession,
            final CandidateItemEventType eventType, final ItemSessionState itemSessionState) {
        return recordCandidateItemEvent(candidateItemSession, eventType, itemSessionState, null, null);
    }

    public CandidateItemEvent recordCandidateItemEvent(final CandidateSession candidateItemSession,
            final CandidateItemEventType eventType, final ItemSessionState itemSessionState,
            final NotificationRecorder notificationRecorder) {
        return recordCandidateItemEvent(candidateItemSession, eventType, itemSessionState, notificationRecorder, null);
    }

    public CandidateItemEvent recordCandidateItemEvent(final CandidateSession candidateItemSession,
            final CandidateItemEventType eventType, final ItemSessionState itemSessionState,
            final CandidateItemEvent playbackEvent) {
        return recordCandidateItemEvent(candidateItemSession, eventType, itemSessionState, null, playbackEvent);
    }

    private CandidateItemEvent recordCandidateItemEvent(final CandidateSession candidateItemSession,
            final CandidateItemEventType eventType, final ItemSessionState itemSessionState,
            final NotificationRecorder notificationRecorder,
            final CandidateItemEvent playbackEvent) {
        /* Create event */
        final CandidateItemEvent event = new CandidateItemEvent();
        event.setCandidateItemSession(candidateItemSession);
        event.setEventType(eventType);
        event.setSessionStatus(candidateItemSession.getCandidateSessionStatus());
        event.setCompletionStatus(itemSessionState.getCompletionStatus());
        event.setDuration(itemSessionState.getDuration());
        event.setNumAttempts(itemSessionState.getNumAttempts());
        event.setTimestamp(requestTimestampContext.getCurrentRequestTimestamp());
        event.setPlaybackEvent(playbackEvent);

        /* Record serialized ItemSessionState */
        event.setItemSessionStateXml(marshalItemSessionState(itemSessionState));

        /* Store event */
        candidateItemEventDao.persist(event);

        /* Now store processing notifications */
        if (notificationRecorder!=null) {
            for (final Notification notification : notificationRecorder.getNotifications()) {
                recordNotification(event, notification);
            }
        }

        return event;
    }

    public CandidateItemEventNotification recordNotification(final CandidateItemEvent candidateItemEvent, final Notification notification) {
        final CandidateItemEventNotification record = new CandidateItemEventNotification();
        record.setCandidateItemEvent(candidateItemEvent);

        record.setMessage(notification.getMessage());
        record.setNotificationLevel(notification.getNotificationLevel());
        record.setNotificationType(notification.getNotificationType());

        final QtiNode qtiNode = notification.getQtiNode();
        if (qtiNode!=null) {
            record.setNodeQtiClassName(qtiNode.getQtiClassName());
            final XmlSourceLocationInformation sourceLocation = qtiNode.getSourceLocation();
            if (sourceLocation!=null) {
                record.setSystemId(sourceLocation.getSystemId());
                record.setLineNumber(sourceLocation.getLineNumber());
                record.setColumnNumber(sourceLocation.getColumnNumber());
            }
        }
        final Attribute<?> attribute = notification.getAttribute();
        if (attribute!=null) {
            record.setAttributeLocalName(attribute.getLocalName());
            record.setAttributeNamespaceUri(attribute.getNamespaceUri());
        }

        candidateItemEvent.getNotifications().add(record);
        candidateItemEventNotificationDao.persist(record);
        return record;
    }

    public ItemSessionController createItemSessionController(final CandidateItemEvent candidateItemEvent,
            final NotificationRecorder notificationRecorder) {
        Assert.notNull(candidateItemEvent, "candidateItemEvent");

        final Delivery itemDelivery = candidateItemEvent.getCandidateSession().getDelivery();
        final ItemSessionState itemSessionState = unmarshalItemSessionState(candidateItemEvent);
        return createItemSessionController(itemDelivery, itemSessionState, notificationRecorder);
    }

    public ItemSessionController createItemSessionController(final Delivery itemDelivery,
            final ItemSessionState itemSessionState,  final NotificationRecorder notificationRecorder) {
        Assert.notNull(itemDelivery, "itemDelivery");
        Assert.notNull(itemSessionState, "itemSessionState");

        /* Get the resolved JQTI+ Object for the underlying package */
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(itemDelivery);
        final ResolvedAssessmentItem resolvedAssessmentItem = assessmentObjectManagementService.getResolvedAssessmentItem(assessmentPackage);

        /* Create controller and wire up notification recorder (if passed) */
        final ItemSessionController result = new ItemSessionController(jqtiExtensionManager, resolvedAssessmentItem, itemSessionState);
        if (notificationRecorder!=null) {
            result.addNotificationListener(notificationRecorder);
        }

        return result;
    }

    public ItemSessionState computeCurrentItemSessionState(final CandidateSession candidateItemSession)  {
        final CandidateItemEvent mostRecentEvent = getMostRecentEvent(candidateItemSession);
        return unmarshalItemSessionState(mostRecentEvent);
    }

    public CandidateItemEvent getMostRecentEvent(final CandidateSession candidateItemSession)  {
        final CandidateItemEvent mostRecentEvent = candidateItemEventDao.getNewestEventInSession(candidateItemSession);
        if (mostRecentEvent==null) {
            throw new QtiWorksLogicException("Session has no events registered. Current logic should not have allowed this!");
        }
        return mostRecentEvent;
    }
}
