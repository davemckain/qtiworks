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
import uk.ac.ed.ph.qtiworks.domain.binding.ItemSessionStateXmlMarshaller;
import uk.ac.ed.ph.qtiworks.domain.binding.TestSessionStateXmlMarshaller;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateEventDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateEventNotificationDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemEventDao;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEventNotification;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateTestEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateTestEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.utils.XmlUtilities;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.JqtiPlus;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.notification.Notification;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.running.TestPlanner;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.running.TestSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlSourceLocationInformation;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

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
    private CandidateEventDao candidateEventDao;

    @Resource
    private CandidateEventNotificationDao candidateEventNotificationDao;

    @Resource
    private CandidateItemEventDao candidateItemEventDao;

    @Resource
    private JqtiExtensionManager jqtiExtensionManager;

    //----------------------------------------------------
    // Notification recording

    public CandidateEventNotification recordNotification(final CandidateEvent candidateEvent, final Notification notification) {
        final CandidateEventNotification record = new CandidateEventNotification();
        record.setCandidateEvent(candidateEvent);

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

        candidateEvent.getNotifications().add(record);
        candidateEventNotificationDao.persist(record);
        return record;
    }

    //----------------------------------------------------
    // Item methods

    public String marshalItemSessionState(final ItemSessionState itemSessionState) {
        return serializeMarshalledXml(ItemSessionStateXmlMarshaller.marshal(itemSessionState));
    }

    public ItemSessionState unmarshalItemSessionState(final CandidateItemEvent event) {
        final String itemSessionStateXml = event.getItemSessionStateXml();
        final DocumentBuilder documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
        final Document doc = parseMarshalledState(itemSessionStateXml);
        return ItemSessionStateXmlMarshaller.unmarshal(doc.getDocumentElement());
    }


    public CandidateItemEvent recordCandidateItemEvent(final CandidateSession candidateSession,
            final CandidateItemEventType eventType, final ItemSessionState itemSessionState) {
        return recordCandidateItemEvent(candidateSession, eventType, itemSessionState, null, null);
    }

    public CandidateItemEvent recordCandidateItemEvent(final CandidateSession candidateSession,
            final CandidateItemEventType eventType, final ItemSessionState itemSessionState,
            final NotificationRecorder notificationRecorder) {
        return recordCandidateItemEvent(candidateSession, eventType, itemSessionState, notificationRecorder, null);
    }

    public CandidateItemEvent recordCandidateItemEvent(final CandidateSession candidateSession,
            final CandidateItemEventType eventType, final ItemSessionState itemSessionState,
            final CandidateItemEvent playbackEvent) {
        return recordCandidateItemEvent(candidateSession, eventType, itemSessionState, null, playbackEvent);
    }

    private CandidateItemEvent recordCandidateItemEvent(final CandidateSession candidateSession,
            final CandidateItemEventType eventType, final ItemSessionState itemSessionState,
            final NotificationRecorder notificationRecorder,
            final CandidateItemEvent playbackEvent) {
        /* Create event */
        final CandidateItemEvent event = new CandidateItemEvent();
        event.setCandidateSession(candidateSession);
        event.setItemEventType(eventType);
        event.setCompletionStatus(itemSessionState.getCompletionStatus());
        event.setDuration(itemSessionState.getDuration());
        event.setNumAttempts(itemSessionState.getNumAttempts());
        event.setTimestamp(requestTimestampContext.getCurrentRequestTimestamp());
        event.setPlaybackEvent(playbackEvent);

        /* Record serialized ItemSessionState */
        event.setItemSessionStateXml(marshalItemSessionState(itemSessionState));

        /* Store event */
        candidateEventDao.persist(event);

        /* Now store processing notifications */
        if (notificationRecorder!=null) {
            for (final Notification notification : notificationRecorder.getNotifications()) {
                recordNotification(event, notification);
            }
        }

        return event;
    }

    public ItemSessionController createItemSessionController(final CandidateItemEvent candidateItemEvent,
            final NotificationRecorder notificationRecorder) {
        Assert.notNull(candidateItemEvent, "candidateItemEvent");

        final Delivery delivery = candidateItemEvent.getCandidateSession().getDelivery();
        final ItemSessionState itemSessionState = unmarshalItemSessionState(candidateItemEvent);
        return createItemSessionController(delivery, itemSessionState, notificationRecorder);
    }

    public ItemSessionController createItemSessionController(final Delivery delivery,
            final ItemSessionState itemSessionState,  final NotificationRecorder notificationRecorder) {
        ensureItemDelivery(delivery);
        Assert.notNull(itemSessionState, "itemSessionState");

        /* Resolve the underlying JQTI+ object */
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);
        final ItemProcessingMap itemProcessingMap = assessmentObjectManagementService.getItemProcessingMap(assessmentPackage);

        /* Create config for ItemSessionController */
        final ItemDeliverySettings itemDeliverySettings = (ItemDeliverySettings) delivery.getDeliverySettings();
        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        itemSessionControllerSettings.setTemplateProcessingLimit(computeTemplateProcessingLimit(itemDeliverySettings));
        itemSessionControllerSettings.setMaxAttempts(itemDeliverySettings.getMaxAttempts());

        /* Create controller and wire up notification recorder (if passed) */
        final ItemSessionController result = new ItemSessionController(jqtiExtensionManager,
                itemSessionControllerSettings, itemProcessingMap, itemSessionState);
        if (notificationRecorder!=null) {
            result.addNotificationListener(notificationRecorder);
        }

        return result;
    }

    /**
     * Computes a usable template processing limit from {@link DeliverySettings}, reverting
     * to a default value if they make no sense.
     */
    public int computeTemplateProcessingLimit(final DeliverySettings deliverySettings) {
        final int requestedLimit = deliverySettings.getTemplateProcessingLimit();
        return requestedLimit > 0 ? requestedLimit : JqtiPlus.DEFAULT_TEMPLATE_PROCESSING_LIMIT;
    }

    public ItemSessionState computeCurrentItemSessionState(final CandidateSession candidateSession)  {
        final CandidateItemEvent mostRecentEvent = getMostRecentItemEvent(candidateSession);
        return unmarshalItemSessionState(mostRecentEvent);
    }

    @ToRefactor
    public CandidateItemEvent getMostRecentItemEvent(final CandidateSession candidateSession)  {
        final CandidateItemEvent mostRecentEvent = candidateItemEventDao.getNewestEventInSession(candidateSession);
        if (mostRecentEvent==null) {
            throw new QtiWorksLogicException("Session has no events registered. Current logic should not have allowed this!");
        }
        return mostRecentEvent;
    }

    public void ensureItemDelivery(final Delivery delivery) {
        Assert.notNull(delivery, "delivery");
        if (delivery.getAssessment().getAssessmentType()!=AssessmentObjectType.ASSESSMENT_ITEM) {
            throw new IllegalArgumentException("Expected " + delivery + " to correspond to an Item");
        }
    }

    //----------------------------------------------------
    // Test methods

    public String marshalTestSessionState(final TestSessionState testSessionState) {
        return serializeMarshalledXml(TestSessionStateXmlMarshaller.marshal(testSessionState));
    }

    public TestSessionState unmarshalTestSessionState(final CandidateTestEvent event) {
        final String testSessionStateXml = event.getTestSessionStateXml();
        final Document doc = parseMarshalledState(testSessionStateXml);
        return TestSessionStateXmlMarshaller.unmarshal(doc.getDocumentElement());
    }

    public TestSessionController createNewTestSessionStateAndController(final Delivery delivery, final NotificationRecorder notificationRecorder) {
        ensureTestDelivery(delivery);

        /* Resolve the underlying JQTI+ object */
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);
        final TestProcessingMap testProcessingMap = assessmentObjectManagementService.getTestProcessingMap(assessmentPackage);

        /* Generate a test plan for this session */
        final TestPlanner testPlanner = new TestPlanner(testProcessingMap);
        if (notificationRecorder!=null) {
            testPlanner.addNotificationListener(notificationRecorder);
        }
        final TestPlan testPlan = testPlanner.generateTestPlan();

        /* Create fresh state for session */
        final TestSessionState testSessionState = new TestSessionState(testPlan);

        /* Create config for TestSessionController */
        final DeliverySettings testDeliverySettings = delivery.getDeliverySettings();
        final TestSessionControllerSettings testSessionControllerSettings = new TestSessionControllerSettings();
        testSessionControllerSettings.setTemplateProcessingLimit(computeTemplateProcessingLimit(testDeliverySettings));

        /* Create controller and wire up notification recorder */
        final TestSessionController result = new TestSessionController(jqtiExtensionManager,
                testSessionControllerSettings, testProcessingMap, testSessionState);
        if (notificationRecorder!=null) {
            result.addNotificationListener(notificationRecorder);
        }
        return result;
    }

    public CandidateTestEvent recordCandidateTestEvent(final CandidateSession candidateSession,
            final CandidateTestEventType eventType, final TestSessionState testSessionState) {
        return recordCandidateTestEvent(candidateSession, eventType, testSessionState, null);
    }

    public CandidateTestEvent recordCandidateTestEvent(final CandidateSession candidateSession,
            final CandidateTestEventType eventType, final TestSessionState testSessionState,
            final NotificationRecorder notificationRecorder) {
        /* Create event */
        final CandidateTestEvent event = new CandidateTestEvent();
        event.setCandidateSession(candidateSession);
        event.setTestEventType(eventType);
        event.setDuration(testSessionState.getDuration());
        event.setTimestamp(requestTimestampContext.getCurrentRequestTimestamp());

        /* Record serialized TestSessionState */
        event.setTestSessionStateXml(marshalTestSessionState(testSessionState));

        /* Store event */
        candidateEventDao.persist(event);

        /* Now store processing notifications */
        if (notificationRecorder!=null) {
            for (final Notification notification : notificationRecorder.getNotifications()) {
                recordNotification(event, notification);
            }
        }

        return event;
    }

    public void ensureTestDelivery(final Delivery delivery) {
        Assert.notNull(delivery, "delivery");
        if (delivery.getAssessment().getAssessmentType()!=AssessmentObjectType.ASSESSMENT_TEST) {
            throw new IllegalArgumentException("Expected " + delivery + " to correspond to a Test");
        }
    }

    //----------------------------------------------------
    // General helpers

    public CandidateEvent getMostRecentEvent(final CandidateSession candidateSession)  {
        final CandidateEvent mostRecentEvent = candidateEventDao.getNewestEventInSession(candidateSession);
        if (mostRecentEvent==null) {
            throw new QtiWorksLogicException("Session has no events registered. Current logic should not have allowed this!");
        }
        return mostRecentEvent;
    }

    private String serializeMarshalledXml(final Document marshalledState) {
        final XMLStringOutputOptions xmlOptions = new XMLStringOutputOptions();
        xmlOptions.setIndenting(true);
        xmlOptions.setIncludingXMLDeclaration(false);
        return XMLUtilities.serializeNode(marshalledState, xmlOptions);
    }

    private Document parseMarshalledState(final String stateXml) {
        final DocumentBuilder documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
        try {
            return documentBuilder.parse(new InputSource(new StringReader(stateXml)));
        }
        catch (final Exception e) {
            throw new QtiWorksLogicException("Could not parse serailized state XML. This is an internal error as we currently don't expose this data to clients", e);
        }
    }
}
