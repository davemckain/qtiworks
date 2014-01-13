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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksDeploymentSettings;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEventNotification;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSessionOutcome;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateTestEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.mathassess.GlueValueBinder;
import uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateEventDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateEventNotificationDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionOutcomeDao;
import uk.ac.ed.ph.qtiworks.utils.XmlUtilities;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.JqtiPlus;
import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.result.AbstractResult;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.result.ItemVariable;
import uk.ac.ed.ph.jqtiplus.node.result.OutcomeVariable;
import uk.ac.ed.ph.jqtiplus.notification.Notification;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.running.TestPlanner;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.running.TestSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.state.marshalling.ItemSessionStateXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.state.marshalling.TestSessionStateXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlSourceLocationInformation;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltSerializationOptions;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

/**
 * Low level services for manipulating candidate data, such as recording
 * {@link CandidateEvent}s.
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.SUPPORTS)
public class CandidateDataService {

    @Resource
    private QtiWorksDeploymentSettings qtiWorksDeploymentSettings;

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private FilespaceManager filespaceManager;

    @Resource
    private AssessmentDataService assessmentDataService;

    @Resource
    private AssessmentObjectManagementService assessmentObjectManagementService;

    @Resource
    private CandidateSessionOutcomeDao candidateSessionOutcomeDao;

    @Resource
    private CandidateEventDao candidateEventDao;

    @Resource
    private CandidateEventNotificationDao candidateEventNotificationDao;

    @Resource
    private QtiSerializer qtiSerializer;

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
    // Item or Test methods

    public AssessmentResult computeAssessmentResult(final CandidateEvent candidateEvent) {
        if (candidateEvent==null) {
            /* Session not entered, so return empty result */
            return new AssessmentResult();
        }
        final AssessmentObjectType assessmentType = candidateEvent.getCandidateSession().getDelivery().getAssessment().getAssessmentType();
        switch (assessmentType) {
            case ASSESSMENT_ITEM:
                final ItemSessionController itemSessionController = createItemSessionController(candidateEvent, null);
                return computeItemAssessmentResult(candidateEvent.getCandidateSession(), itemSessionController);

            case ASSESSMENT_TEST:
                final TestSessionController testSessionController = createTestSessionController(candidateEvent, null);
                return computeTestAssessmentResult(candidateEvent.getCandidateSession(), testSessionController);

            default:
                throw new QtiWorksLogicException("Unexpected switch case " + assessmentType);
        }
    }

    public void streamAssessmentResult(final CandidateSession candidateSession, final OutputStream outputStream) {
        /* Get most recent event */
        final CandidateEvent mostRecentEvent = getMostRecentEvent(candidateSession);

        /* Stream result for event */
        streamAssessmentResult(mostRecentEvent, outputStream);
    }

    private void streamAssessmentResult(final CandidateEvent candidateEvent, final OutputStream outputStream) {
        /* Generate AssessmentResult from event */
        final AssessmentResult assessmentResult = computeAssessmentResult(candidateEvent);

        /* Send result */
        qtiSerializer.serializeJqtiObject(assessmentResult, outputStream);
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

    public CandidateEvent recordCandidateItemEvent(final CandidateSession candidateSession,
            final CandidateItemEventType itemEventType, final ItemSessionState itemSessionState) {
        return recordCandidateItemEvent(candidateSession, itemEventType, itemSessionState, null);
    }

    public CandidateEvent recordCandidateItemEvent(final CandidateSession candidateSession,
            final CandidateItemEventType itemEventType, final ItemSessionState itemSessionState,
            final NotificationRecorder notificationRecorder) {
        /* Create event */
        final CandidateEvent event = new CandidateEvent();
        event.setCandidateSession(candidateSession);
        event.setItemEventType(itemEventType);
        event.setTimestamp(requestTimestampContext.getCurrentRequestTimestamp());

        /* Store event */
        candidateEventDao.persist(event);

        /* Save current ItemSessionState */
        storeItemSessionState(event, itemSessionState);

        /* Now store processing notifications */
        if (notificationRecorder!=null) {
            for (final Notification notification : notificationRecorder.getNotifications()) {
                recordNotification(event, notification);
            }
        }

        return event;
    }

    /**
     * Attempts to create a fresh {@link ItemSessionState} wrapped into a {@link ItemSessionController}
     * for the given {@link Delivery}.
     * <p>
     * This will return null if the item can't be started because its {@link ItemProcessingMap}
     * can't be created, e.g. if its XML can't be parsed.
     */
    public ItemSessionController createNewItemSessionStateAndController(final User candidate, final Delivery delivery, final NotificationRecorder notificationRecorder) {
        ensureItemDelivery(delivery);

        /* Resolve the underlying JQTI+ object */
        final AssessmentPackage assessmentPackage = assessmentDataService.ensureSelectedAssessmentPackage(delivery);
        final ItemProcessingMap itemProcessingMap = assessmentObjectManagementService.getItemProcessingMap(assessmentPackage);
        if (itemProcessingMap==null) {
            return null;
        }

        /* Create fresh state for session */
        final ItemSessionState itemSessionState = new ItemSessionState();

        /* Create config for ItemSessionController */
        final ItemDeliverySettings itemDeliverySettings = (ItemDeliverySettings) assessmentDataService.getEffectiveDeliverySettings(candidate, delivery);
        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        itemSessionControllerSettings.setTemplateProcessingLimit(computeTemplateProcessingLimit(itemDeliverySettings));
        itemSessionControllerSettings.setMaxAttempts(itemDeliverySettings.getMaxAttempts());

        /* Create controller and wire up notification recorder */
        final ItemSessionController result = new ItemSessionController(jqtiExtensionManager,
                itemSessionControllerSettings, itemProcessingMap, itemSessionState);
        if (notificationRecorder!=null) {
            result.addNotificationListener(notificationRecorder);
        }
        return result;
    }

    /**
     * Extracts the {@link ItemSessionState} corresponding to the given {@link CandidateEvent}
     * and wraps it in a {@link ItemSessionController}.
     * <p>
     * It is assumed that the item was runnable, so this will never return null.
     */
    public ItemSessionController createItemSessionController(final CandidateEvent candidateEvent,
            final NotificationRecorder notificationRecorder) {
        final ItemSessionState itemSessionState = loadItemSessionState(candidateEvent);
        return createItemSessionController(candidateEvent.getCandidateSession(), itemSessionState, notificationRecorder);
    }

    /**
     * Wraps the given {@link ItemSessionState} in a {@link ItemSessionController}.
     * <p>
     * It is assumed that the item was runnable, so this will never return null.
     */
    public ItemSessionController createItemSessionController(final CandidateSession candidateSession,
            final ItemSessionState itemSessionState,  final NotificationRecorder notificationRecorder) {
        final User candidate = candidateSession.getCandidate();
        final Delivery delivery = candidateSession.getDelivery();
        ensureItemDelivery(delivery);
        Assert.notNull(itemSessionState, "itemSessionState");

        /* Try to resolve the underlying JQTI+ object */
        final AssessmentPackage assessmentPackage = assessmentDataService.ensureSelectedAssessmentPackage(delivery);
        final ItemProcessingMap itemProcessingMap = assessmentObjectManagementService.getItemProcessingMap(assessmentPackage);
        if (itemProcessingMap==null) {
            throw new QtiWorksLogicException("Expected this item to be runnable");
        }

        /* Create config for ItemSessionController */
        final ItemDeliverySettings itemDeliverySettings = (ItemDeliverySettings) assessmentDataService.getEffectiveDeliverySettings(candidate, delivery);
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
        final Integer requestedLimit = deliverySettings.getTemplateProcessingLimit();
        if (requestedLimit==null) {
            /* Not specified, so use default */
            return JqtiPlus.DEFAULT_TEMPLATE_PROCESSING_LIMIT;
        }
        final int requestedLimitIntValue = requestedLimit.intValue();
        return requestedLimitIntValue > 0 ? requestedLimitIntValue : JqtiPlus.DEFAULT_TEMPLATE_PROCESSING_LIMIT;
    }

    public AssessmentResult computeAndRecordItemAssessmentResult(final CandidateSession candidateSession, final ItemSessionController itemSessionController) {
        final AssessmentResult assessmentResult = computeItemAssessmentResult(candidateSession, itemSessionController);
        recordItemAssessmentResult(candidateSession, assessmentResult);
        return assessmentResult;
    }

    public AssessmentResult computeItemAssessmentResult(final CandidateSession candidateSession, final ItemSessionController itemSessionController) {
        final URI sessionIdentifierSourceId = URI.create(qtiWorksDeploymentSettings.getBaseUrl());
        final String sessionIdentifier = "itemsession/" + candidateSession.getId();
        return itemSessionController.computeAssessmentResult(requestTimestampContext.getCurrentRequestTimestamp(), sessionIdentifier, sessionIdentifierSourceId);
    }

    public void recordItemAssessmentResult(final CandidateSession candidateSession, final AssessmentResult assessmentResult) {
        /* First record full result XML to filesystem */
        storeResultFile(candidateSession, assessmentResult);

        /* Then record item outcome variables to DB */
        recordOutcomeVariables(candidateSession, assessmentResult.getItemResults().get(0));
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

    /**
     * Attempts to create a fresh {@link TestSessionState} wrapped into a {@link TestSessionController}
     * for the given {@link Delivery}.
     * <p>
     * This will return null if the test can't be started because its {@link TestProcessingMap}
     * can't be created, e.g. if its XML can't be parsed.
     */
    public TestSessionController createNewTestSessionStateAndController(final User candidate, final Delivery delivery, final NotificationRecorder notificationRecorder) {
        ensureTestDelivery(delivery);

        /* Resolve the underlying JQTI+ object */
        final AssessmentPackage assessmentPackage = assessmentDataService.ensureSelectedAssessmentPackage(delivery);
        final TestProcessingMap testProcessingMap = assessmentObjectManagementService.getTestProcessingMap(assessmentPackage);
        if (testProcessingMap==null) {
            return null;
        }

        /* Generate a test plan for this session */
        final TestPlanner testPlanner = new TestPlanner(testProcessingMap);
        if (notificationRecorder!=null) {
            testPlanner.addNotificationListener(notificationRecorder);
        }
        final TestPlan testPlan = testPlanner.generateTestPlan();

        /* Create fresh state for session */
        final TestSessionState testSessionState = new TestSessionState(testPlan);

        /* Create config for TestSessionController */
        final DeliverySettings testDeliverySettings = assessmentDataService.getEffectiveDeliverySettings(candidate, delivery);
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

    /**
     * Extracts the {@link TestSessionState} corresponding to the given {@link CandidateEvent}
     * and wraps it in a {@link TestSessionController}.
     * <p>
     * It is assumed that the test was runnable, so this will never return null.
     */
    public TestSessionController createTestSessionController(final CandidateEvent candidateEvent,
            final NotificationRecorder notificationRecorder) {
        final TestSessionState testSessionState = loadTestSessionState(candidateEvent);
        return createTestSessionController(candidateEvent.getCandidateSession(), testSessionState, notificationRecorder);
    }

    /**
     * Wraps the given {@link TestSessionState} in a {@link TestSessionController}.
     * <p>
     * It is assumed that the test was runnable, so this will never return null.
     */
    public TestSessionController createTestSessionController(final CandidateSession candidateSession,
            final TestSessionState testSessionState,  final NotificationRecorder notificationRecorder) {
        final User candidate = candidateSession.getCandidate();
        final Delivery delivery = candidateSession.getDelivery();
        ensureTestDelivery(delivery);
        Assert.notNull(testSessionState, "testSessionState");

        /* Try to resolve the underlying JQTI+ object */
        final AssessmentPackage assessmentPackage = assessmentDataService.ensureSelectedAssessmentPackage(delivery);
        final TestProcessingMap testProcessingMap = assessmentObjectManagementService.getTestProcessingMap(assessmentPackage);
        if (testProcessingMap==null) {
            return null;
        }

        /* Create config for TestSessionController */
        final TestDeliverySettings testDeliverySettings = (TestDeliverySettings) assessmentDataService.getEffectiveDeliverySettings(candidate, delivery);
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

    public CandidateEvent recordCandidateExitTestEvent(final CandidateSession candidateSession,
            final TestSessionState testSessionState,
            final NotificationRecorder notificationRecorder) {
        return recordCandidateTestEvent(candidateSession, CandidateTestEventType.END_TEST_PART, null, null, testSessionState, notificationRecorder);
    }

    public CandidateEvent recordCandidateTestEvent(final CandidateSession candidateSession,
            final CandidateTestEventType testEventType, final TestSessionState testSessionState,
            final NotificationRecorder notificationRecorder) {
        return recordCandidateTestEvent(candidateSession, testEventType, null, null, testSessionState, notificationRecorder);
    }

    public CandidateEvent recordCandidateTestEvent(final CandidateSession candidateSession,
            final CandidateTestEventType testEventType, final CandidateItemEventType itemEventType,
            final TestSessionState testSessionState,
            final NotificationRecorder notificationRecorder) {
        return recordCandidateTestEvent(candidateSession, testEventType, itemEventType, null, testSessionState, notificationRecorder);
    }

    public CandidateEvent recordCandidateTestEvent(final CandidateSession candidateSession,
            final CandidateTestEventType testEventType, final CandidateItemEventType itemEventType,
            final TestPlanNodeKey itemKey, final TestSessionState testSessionState,
            final NotificationRecorder notificationRecorder) {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(testEventType, "testEventType");
        Assert.notNull(testSessionState, "testSessionState");

        /* Create event */
        final CandidateEvent event = new CandidateEvent();
        event.setCandidateSession(candidateSession);
        event.setTestEventType(testEventType);
        event.setItemEventType(itemEventType);
        if (itemKey!=null) {
            event.setTestItemKey(itemKey.toString());
        }
        event.setTimestamp(requestTimestampContext.getCurrentRequestTimestamp());

        /* Store event */
        candidateEventDao.persist(event);

        /* Store test session state */
        storeTestSessionState(event, testSessionState);

        /* Now store processing notifications */
        if (notificationRecorder!=null) {
            for (final Notification notification : notificationRecorder.getNotifications()) {
                recordNotification(event, notification);
            }
        }

        return event;
    }

    public AssessmentResult computeTestAssessmentResult(final CandidateSession candidateSession, final TestSessionController testSessionController) {
        final URI sessionIdentifierSourceId = URI.create(qtiWorksDeploymentSettings.getBaseUrl());
        final String sessionIdentifier = "testsession/" + candidateSession.getId();
        return testSessionController.computeAssessmentResult(requestTimestampContext.getCurrentRequestTimestamp(), sessionIdentifier, sessionIdentifierSourceId);
    }

    public AssessmentResult computeAndRecordTestAssessmentResult(final CandidateSession candidateSession, final TestSessionController testSessionController) {
        final AssessmentResult assessmentResult = computeTestAssessmentResult(candidateSession, testSessionController);
        recordTestAssessmentResult(candidateSession, assessmentResult);
        return assessmentResult;
    }

    public void recordTestAssessmentResult(final CandidateSession candidateSession, final AssessmentResult assessmentResult) {
        /* First record full result XML to filesystem */
        storeResultFile(candidateSession, assessmentResult);

        /* Then record test outcome variables to DB */
        recordOutcomeVariables(candidateSession, assessmentResult.getTestResult());
    }

    private void ensureTestDelivery(final Delivery delivery) {
        Assert.notNull(delivery, "delivery");
        if (delivery.getAssessment().getAssessmentType()!=AssessmentObjectType.ASSESSMENT_TEST) {
            throw new IllegalArgumentException("Expected " + delivery + " to correspond to a Test");
        }
    }

    //----------------------------------------------------
    // General helpers

    /**
     * Returns the most recently-recorded {@link CandidateEvent} for the given {@link CandidateSession}.
     * The result will be null if and only if the {@link CandidateSession} has been created but not
     * yet entered.
     */
    public CandidateEvent getMostRecentEvent(final CandidateSession candidateSession)  {
        return candidateEventDao.getNewestEventInSession(candidateSession);
    }

    private void storeStateDocument(final CandidateEvent candidateEvent, final Document stateXml) {
        final File sessionFile = getSessionStateFile(candidateEvent);
        final XsltSerializationOptions xsltSerializationOptions = new XsltSerializationOptions();
        xsltSerializationOptions.setIndenting(true);
        xsltSerializationOptions.setIncludingXMLDeclaration(false);
        final Transformer serializer = XsltStylesheetManager.createSerializer(xsltSerializationOptions);
        FileOutputStream resultStream = null;
        try {
            resultStream = new FileOutputStream(sessionFile);
            serializer.transform(new DOMSource(stateXml), new StreamResult(resultStream));
        }
        catch (final TransformerException e) {
            throw new QtiWorksRuntimeException("Unexpected Exception serializing state DOM", e);
        }
        catch (final FileNotFoundException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        finally {
            ServiceUtilities.ensureClose(resultStream);
        }
    }

    private void storeResultFile(final CandidateSession candidateSession, final QtiNode resultNode) {
        final File resultFile = getResultFile(candidateSession);
        FileOutputStream resultStream = null;
        try {
            resultStream = new FileOutputStream(resultFile);
            qtiSerializer.serializeJqtiObject(resultNode, resultStream);
        }
        catch (final Exception e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        finally {
            ServiceUtilities.ensureClose(resultStream);
        }
    }

    public File getResultFile(final CandidateSession candidateSession) {
        final File sessionFolder = filespaceManager.obtainCandidateSessionStateStore(candidateSession);
        return new File(sessionFolder, "assessmentResult.xml");
    }

    public String readResultFile(final CandidateSession candidateSession) {
        final File resultFile = getResultFile(candidateSession);
        if (!resultFile.exists()) {
            return null;
        }
        try {
            /* NB: We're using the fact that we're writing out as UTF-8 when storing these files */
            return FileUtils.readFileToString(getResultFile(candidateSession), "UTF-8");
        }
        catch (final IOException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
    }

    private void recordOutcomeVariables(final CandidateSession candidateSession, final AbstractResult resultNode) {
        candidateSessionOutcomeDao.deleteForCandidateSession(candidateSession);
        for (final ItemVariable itemVariable : resultNode.getItemVariables()) {
            if (itemVariable instanceof OutcomeVariable
                    || QtiConstants.VARIABLE_DURATION_IDENTIFIER.equals(itemVariable.getIdentifier())) {
                final CandidateSessionOutcome outcome = new CandidateSessionOutcome();
                outcome.setCandidateSession(candidateSession);
                outcome.setOutcomeIdentifier(itemVariable.getIdentifier().toString());
                outcome.setBaseType(itemVariable.getBaseType());
                outcome.setCardinality(itemVariable.getCardinality());
                outcome.setStringValue(stringifyQtiValue(itemVariable.getComputedValue()));
                candidateSessionOutcomeDao.persist(outcome);
            }
        }
    }

    private String stringifyQtiValue(final Value value) {
        if (qtiWorksDeploymentSettings.isEnableMathAssessExtension() && GlueValueBinder.isMathsContentRecord(value)) {
            /* This is a special MathAssess "Maths Content" variable. In this case, we'll record
             * just the ASCIIMath input form or the Maxima form, if either are available.
             */
            final RecordValue mathsValue = (RecordValue) value;
            final SingleValue asciiMathInput = mathsValue.get(MathAssessConstants.FIELD_CANDIDATE_INPUT_IDENTIFIER);
            if (asciiMathInput!=null) {
                return "ASCIIMath[" + asciiMathInput.toQtiString() + "]";
            }
            final SingleValue maximaForm = mathsValue.get(MathAssessConstants.FIELD_MAXIMA_IDENTIFIER);
            if (maximaForm!=null) {
                return "Maxima[" + maximaForm.toQtiString() + "]";
            }
        }
        /* Just convert to QTI string in the usual way */
        return value.toQtiString();
    }

    private Document loadStateDocument(final CandidateEvent candidateEvent) {
        final File sessionFile = ensureSessionStateFile(candidateEvent);
        final DocumentBuilder documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
        try {
            return documentBuilder.parse(sessionFile);
        }
        catch (final Exception e) {
            throw new QtiWorksLogicException("Could not parse serailized state XML. This is an internal error as we currently don't expose this data to clients", e);
        }
    }

    public File ensureSessionStateFile(final CandidateEvent candidateEvent) {
        final File sessionStateFile = getSessionStateFile(candidateEvent);
        if (!sessionStateFile.exists()) {
            throw new QtiWorksLogicException("State file " + sessionStateFile + " does not exist");
        }
        return sessionStateFile;
    }

    private File getSessionStateFile(final CandidateEvent candidateEvent) {
        final CandidateSession candidateSession = candidateEvent.getCandidateSession();
        final AssessmentObjectType assessmentType = candidateSession.getDelivery().getAssessment().getAssessmentType();
        final String stateFileBaseName = assessmentType==AssessmentObjectType.ASSESSMENT_ITEM ? "itemSessionState" : "testSessionState";
        final File sessionFolder = filespaceManager.obtainCandidateSessionStateStore(candidateSession);
        final String stateFileName = stateFileBaseName + candidateEvent.getId() + ".xml";
        return new File(sessionFolder, stateFileName);
    }
}
