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
import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.binding.ItemSessionStateXmlMarshaller;
import uk.ac.ed.ph.qtiworks.domain.binding.TestSessionStateXmlMarshaller;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateEventDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateEventNotificationDao;
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
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
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
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltSerializationOptions;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetManager;

import java.io.File;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

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
    private FilespaceManager filespaceManager;

    @Resource
    private AssessmentObjectManagementService assessmentObjectManagementService;

    @Resource
    private CandidateEventDao candidateEventDao;

    @Resource
    private CandidateEventNotificationDao candidateEventNotificationDao;

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

    public void storeItemSessionState(final CandidateEvent candidateEvent, final ItemSessionState itemSessionState) {
        final Document stateDocument = ItemSessionStateXmlMarshaller.marshal(itemSessionState);
        storeStateDocument(candidateEvent, stateDocument);
    }

    public ItemSessionState loadItemSessionState(final CandidateEvent candidateEvent) {
        final Document document = loadStateDocument(candidateEvent);
        return ItemSessionStateXmlMarshaller.unmarshal(document.getDocumentElement());
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

        /* Store event */
        candidateEventDao.persist(event);

        /* Save state */
        storeItemSessionState(event, itemSessionState);

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
        final ItemSessionState itemSessionState = loadItemSessionState(candidateItemEvent);
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
        return loadItemSessionState(mostRecentEvent);
    }

    public CandidateItemEvent getMostRecentItemEvent(final CandidateSession candidateSession)  {
        final CandidateItemEvent mostRecentEvent = (CandidateItemEvent) candidateEventDao.getNewestEventInSession(candidateSession);
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

    public void storeTestSessionState(final CandidateEvent candidateEvent, final TestSessionState testSessionState) {
        final Document stateDocument = TestSessionStateXmlMarshaller.marshal(testSessionState);
        storeStateDocument(candidateEvent, stateDocument);
    }

    public TestSessionState loadTestSessionState(final CandidateEvent candidateEvent) {
        final Document document = loadStateDocument(candidateEvent);
        return TestSessionStateXmlMarshaller.unmarshal(document.getDocumentElement());
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

        /* Store event */
        candidateEventDao.persist(event);

        /* Store state */
        storeTestSessionState(event, testSessionState);

        /* Now store processing notifications */
        if (notificationRecorder!=null) {
            for (final Notification notification : notificationRecorder.getNotifications()) {
                recordNotification(event, notification);
            }
        }

        return event;
    }

    public TestSessionController createTestSessionController(final CandidateTestEvent candidateTestEvent,
            final NotificationRecorder notificationRecorder) {
        Assert.notNull(candidateTestEvent, "candidateTestEvent");

        final Delivery delivery = candidateTestEvent.getCandidateSession().getDelivery();
        final TestSessionState testSessionState = loadTestSessionState(candidateTestEvent);
        return createTestSessionController(delivery, testSessionState, notificationRecorder);
    }

    public TestSessionController createTestSessionController(final Delivery delivery,
            final TestSessionState testSessionState,  final NotificationRecorder notificationRecorder) {
        ensureTestDelivery(delivery);
        Assert.notNull(testSessionState, "testSessionState");

        /* Resolve the underlying JQTI+ object */
        final AssessmentPackage assessmentPackage = entityGraphService.getCurrentAssessmentPackage(delivery);
        final TestProcessingMap testProcessingMap = assessmentObjectManagementService.getTestProcessingMap(assessmentPackage);

        /* Create config for TestSessionController */
        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) delivery.getDeliverySettings();
        final TestSessionControllerSettings testSessionControllerSettings = new TestSessionControllerSettings();
        testSessionControllerSettings.setTemplateProcessingLimit(computeTemplateProcessingLimit(testDeliverySettings));

        /* Create controller and wire up notification recorder (if passed) */
        final TestSessionController result = new TestSessionController(jqtiExtensionManager,
                testSessionControllerSettings, testProcessingMap, testSessionState);
        if (notificationRecorder!=null) {
            result.addNotificationListener(notificationRecorder);
        }

        return result;
    }

    public TestSessionState computeCurrentTestSessionState(final CandidateSession candidateSession)  {
        final CandidateTestEvent mostRecentEvent = getMostRecentTestEvent(candidateSession);
        return loadTestSessionState(mostRecentEvent);
    }

    public CandidateTestEvent getMostRecentTestEvent(final CandidateSession candidateSession)  {
        final CandidateTestEvent mostRecentEvent = (CandidateTestEvent) candidateEventDao.getNewestEventInSession(candidateSession);
        if (mostRecentEvent==null) {
            throw new QtiWorksLogicException("Session has no events registered. Current logic should not have allowed this!");
        }
        return mostRecentEvent;
    }

    public void ensureTestDelivery(final Delivery delivery) {
        Assert.notNull(delivery, "delivery");
        if (delivery.getAssessment().getAssessmentType()!=AssessmentObjectType.ASSESSMENT_TEST) {
            throw new IllegalArgumentException("Expected " + delivery + " to correspond to a Test");
        }
    }

    //----------------------------------------------------
    // General helpers

    private void storeStateDocument(final CandidateEvent candidateEvent, final Document stateXml) {
        final File sessionFile = getStateFile(candidateEvent);
        final XsltSerializationOptions xsltSerializationOptions = new XsltSerializationOptions();
        xsltSerializationOptions.setIndenting(true);
        xsltSerializationOptions.setIncludingXMLDeclaration(false);
        final Transformer serializer = new XsltStylesheetManager().getSerializer(xsltSerializationOptions);
        try {
            serializer.transform(new DOMSource(stateXml), new StreamResult(sessionFile));
        }
        catch (final TransformerException e) {
            throw new QtiWorksRuntimeException("Unexpected Exception serializing state DOM", e);
        }
    }

    private Document loadStateDocument(final CandidateEvent candidateEvent) {
        final File sessionFile = getStateFile(candidateEvent);
        if (!sessionFile.exists()) {
            throw new QtiWorksLogicException("State file " + sessionFile + " does not exist");
        }
        final DocumentBuilder documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
        try {
            return documentBuilder.parse(sessionFile);
        }
        catch (final Exception e) {
            throw new QtiWorksLogicException("Could not parse serailized state XML. This is an internal error as we currently don't expose this data to clients", e);
        }
    }

    private File getStateFile(final CandidateEvent candidateEvent) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final File sessionFolder = filespaceManager.createCandidateSessionStateStore(candidateSession);
        return new File(sessionFolder, String.valueOf("state" + candidateEvent.getId() + ".xml"));
    }
}
