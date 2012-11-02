/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLogListener;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.TestPlanner;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.running.TestSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;

import java.net.URI;

/**
 * Developer class for debugging test rendering
 *
 * @author David McKain
 */
public class TestRenderingTest {

    public static void main(final String[] args) {
        final URI inputUri = URI.create("classpath:/testimplementation/selection.xml");

        System.out.println("Reading");
        final NotificationLogListener notificationLogListener = new NotificationLogListener();
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        jqtiExtensionManager.init();
        try {
            final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
            final QtiObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());

            final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
            final ResolvedAssessmentTest resolvedAssessmentTest = objectManager.resolveAssessmentTest(inputUri, ModelRichness.FULL_ASSUMED_VALID);
            final TestProcessingMap testProcessingMap = new TestProcessingInitializer(resolvedAssessmentTest, true).initialize();

            final TestPlanner testPlanner = new TestPlanner(testProcessingMap);
            testPlanner.addNotificationListener(notificationLogListener);
            final TestPlan testPlan = testPlanner.generateTestPlan();

            final TestSessionState testSessionState = new TestSessionState(testPlan);
            final TestSessionControllerSettings testSessionControllerSettings = new TestSessionControllerSettings();
            final TestSessionController testSessionController = new TestSessionController(jqtiExtensionManager, testSessionControllerSettings, testProcessingMap, testSessionState);
            testSessionController.addNotificationListener(notificationLogListener);

            System.out.println("\nInitialising");
            testSessionController.initialize();
            testSessionController.startTestNI();
            System.out.println("Test session state after init: " + ObjectDumper.dumpObject(testSessionState, DumpMode.DEEP));

//            System.out.println("\nRendering");
//
//            final RenderingOptions renderingOptions = new RenderingOptions();
//            renderingOptions.setContextPath("/qtiworks");
//            renderingOptions.setAttemptUrl("/attempt");
//            renderingOptions.setCloseUrl("/close");
//            renderingOptions.setResetUrl("/reset");
//            renderingOptions.setReinitUrl("/reinit");
//            renderingOptions.setSolutionUrl("/solution");
//            renderingOptions.setResultUrl("/result");
//            renderingOptions.setSourceUrl("/source");
//            renderingOptions.setServeFileUrl("/serveFile");
//            renderingOptions.setPlaybackUrlBase("/playback");
//            renderingOptions.setTerminateUrl("/terminate");
//            renderingOptions.setSerializationMethod(SerializationMethod.HTML5_MATHJAX);
//
//            final TestRenderingRequest renderingRequest = new TestRenderingRequest();
//            renderingRequest.setRenderingMode(RenderingMode.PLAYBACK);
//            renderingRequest.setAssessmentResourceLocator(objectReader.getInputResourceLocator());
//            renderingRequest.setAssessmentResourceUri(inputUri);
//            renderingRequest.setTestSessionState(itemSessionState);
//            renderingRequest.setRenderingOptions(renderingOptions);
//            renderingRequest.setPrompt("This is an item!");
//            renderingRequest.setAuthorMode(true);
//            renderingRequest.setSolutionAllowed(true);
//            renderingRequest.setResetAllowed(true);
//            renderingRequest.setReinitAllowed(true);
//            renderingRequest.setResultAllowed(true);
//            renderingRequest.setSourceAllowed(true);
//            renderingRequest.setPlaybackAllowed(true);
//            renderingRequest.setBadResponseIdentifiers(null);
//            renderingRequest.setInvalidResponseIdentifiers(null);
//
//            final CandidateTestEvent playback1 = new CandidateTestEvent();
//            playback1.setId(1L);
//            playback1.setTestEventType(CandidateTestEventType.INIT);
//
//            final CandidateTestEvent playback2 = new CandidateTestEvent();
//            playback2.setId(2L);
//            playback2.setTestEventType(CandidateTestEventType.ATTEMPT_VALID);
//
//            renderingRequest.setPlaybackEvents(Arrays.asList(playback1, playback2));
//            renderingRequest.setCurrentPlaybackEvent(playback1);
//
//            final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
//            validator.afterPropertiesSet();
//
//            final AssessmentRenderer renderer = new AssessmentRenderer();
//            renderer.setJsr303Validator(validator);
//            renderer.setJqtiExtensionManager(jqtiExtensionManager);
//            renderer.setXsltStylesheetCache(new SimpleXsltStylesheetCache());
//            renderer.init();
//
//            /* Create a fake notification for debugging */
//            final CandidateEventNotification notification = new CandidateEventNotification();
//            notification.setNotificationLevel(NotificationLevel.INFO);
//            notification.setNotificationType(NotificationType.RUNTIME);
//            notification.setMessage("This is a notification");
//            final List<CandidateEventNotification> notifications = new ArrayList<CandidateEventNotification>();
//            notifications.add(notification);
//
//            final String rendered = renderer.renderTestToString(renderingRequest, notifications);
//            System.out.println("Rendered page: " + rendered);
        }
        finally {
            jqtiExtensionManager.destroy();
        }
    }
}
