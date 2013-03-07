/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012-2013, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksProperties;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.TestPartNavigationRenderingRequest;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLogListener;
import uk.ac.ed.ph.jqtiplus.reading.AssessmentObjectXmlLoader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.TestPlanner;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.running.TestSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.SimpleXsltStylesheetCache;

import java.net.URI;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Developer class for debugging test rendering
 *
 * @author David McKain
 */
public class TestNavigationRenderingTest {

    public static void main(final String[] args) {
        final URI testUri = URI.create("classpath:/uk/ac/ed/ph/qtiworks/samples/testimplementation/dave/simple-nonlinear-individual.xml");

        System.out.println("Reading");
        final NotificationLogListener notificationLogListener = new NotificationLogListener();
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        jqtiExtensionManager.init();
        try {
            final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
            final AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader, new ClassPathResourceLocator());

            final ResolvedAssessmentTest resolvedAssessmentTest = assessmentObjectXmlLoader.loadAndResolveAssessmentTest(testUri);
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
            testSessionController.enterTest();
            testSessionController.enterNextAvailableTestPart();
            System.out.println("Test session state after entry: " + ObjectDumper.dumpObject(testSessionState, DumpMode.DEEP));

            System.out.println("\nRendering");

            final RenderingOptions renderingOptions = StandaloneItemRenderingTest.createRenderingOptions();
            final TestPartNavigationRenderingRequest renderingRequest = new TestPartNavigationRenderingRequest();
            renderingRequest.setAssessmentResourceLocator(assessmentObjectXmlLoader.getInputResourceLocator());
            renderingRequest.setAssessmentResourceUri(testUri);
            renderingRequest.setTestSessionState(testSessionState);
            renderingRequest.setRenderingOptions(renderingOptions);

            final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.afterPropertiesSet();

            final QtiWorksProperties qtiWorksProperties = new QtiWorksProperties();
            qtiWorksProperties.setQtiWorksVersion("VERSION");

            final AssessmentRenderer renderer = new AssessmentRenderer();
            renderer.setQtiWorksProperties(qtiWorksProperties);
            renderer.setJsr303Validator(validator);
            renderer.setJqtiExtensionManager(jqtiExtensionManager);
            renderer.setXsltStylesheetCache(new SimpleXsltStylesheetCache());
            renderer.init();

            final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
            renderer.renderTestPartNavigation(renderingRequest, null, new WriterOutputStream(stringBuilderWriter, Charsets.UTF_8));
            final String rendered = stringBuilderWriter.toString();

            System.out.println("Rendered page: " + rendered);
        }
        finally {
            jqtiExtensionManager.destroy();
        }
    }
}
