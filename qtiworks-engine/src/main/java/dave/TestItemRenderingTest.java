/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012-2013, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksProperties;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.RenderingMode;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.TestItemRenderingRequest;

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
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
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
 * Developer class for debugging item rendering within a test
 *
 * @author David McKain
 */
public class TestItemRenderingTest {

    public static void main(final String[] args) {
        final URI testUri = URI.create("classpath:/uk/ac/ed/ph/qtiworks/samples/testimplementation/dave/test-testFeedback.xml");
        final URI itemUri = URI.create("classpath:/uk/ac/ed/ph/qtiworks/samples/testimplementation/dave/addition-feedback.xml");

        System.out.println("Reading");
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final NotificationLogListener notificationLogListener = new NotificationLogListener();
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

            System.out.println("\nInitialising and entering test and first part");
            testSessionController.initialize();
            testSessionController.enterTest();
            System.out.println("First available testPart is " + testSessionController.getNextAvailableTestPart());
            testSessionController.enterNextAvailableTestPart();
            System.out.println("Test session state after entry: " + ObjectDumper.dumpObject(testSessionState, DumpMode.DEEP));

            /* Select first item */
            final TestPlanNode firstItemRef = testSessionState.getTestPlan().searchNodes(TestNodeType.ASSESSMENT_ITEM_REF).get(0);
            testSessionController.selectItemNonlinear(firstItemRef.getKey());
            final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(firstItemRef.getKey());

            System.out.println("\nRendering state after selection of first item");

            final RenderingOptions renderingOptions = StandaloneItemRenderingTest.createRenderingOptions();
            final TestItemRenderingRequest renderingRequest = new TestItemRenderingRequest();
            renderingRequest.setAssessmentResourceLocator(assessmentObjectXmlLoader.getInputResourceLocator());
            renderingRequest.setAssessmentResourceUri(testUri);
            renderingRequest.setAssessmentItemUri(itemUri);
            renderingRequest.setTestSessionState(testSessionState);
            renderingRequest.setItemSessionState(itemSessionState);
            renderingRequest.setRenderingMode(RenderingMode.INTERACTING);
            renderingRequest.setRenderingOptions(renderingOptions);
            renderingRequest.setPrompt("This is an item within a test!");
            renderingRequest.setAuthorMode(true);
            renderingRequest.setSolutionAllowed(false);
            renderingRequest.setResetAllowed(false);
            renderingRequest.setReinitAllowed(false);
            renderingRequest.setResultAllowed(false);
            renderingRequest.setSourceAllowed(false);
            renderingRequest.setItemKey(firstItemRef.getKey());

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
            renderer.renderTestItem(renderingRequest, null, new WriterOutputStream(stringBuilderWriter, Charsets.UTF_8));
            final String rendered = stringBuilderWriter.toString();
            System.out.println("Rendered page: " + rendered);
        }
        finally {
            jqtiExtensionManager.destroy();
        }
    }
}
