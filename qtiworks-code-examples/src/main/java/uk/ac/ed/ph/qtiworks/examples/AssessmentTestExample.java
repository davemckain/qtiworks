/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012-2013, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.qtiworks.examples;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLogListener;
import uk.ac.ed.ph.jqtiplus.reading.AssessmentObjectXmlLoader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
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
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * (Used for ad hoc test of test functionality)
 *
 * @author David McKain
 */
public final class AssessmentTestExample {

    public static void main(final String[] args) throws Exception {
//        final URI inputUri = URI.create("classpath:/testimplementation/non_unique_identifier.xml");
        final URI inputUri = URI.create("classpath:/testimplementation/selection.xml");

        System.out.println("Reading and validating");
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        final AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader, new ClassPathResourceLocator());

        final TestValidationResult testValidationResult = assessmentObjectXmlLoader.loadResolveAndValidateTest(inputUri);
        System.out.println("Validation result: " + ObjectDumper.dumpObject(testValidationResult, DumpMode.DEEP));

        final TestProcessingMap testProcessingMap = new TestProcessingInitializer(testValidationResult).initialize();
        System.out.println("Test processing map: " + ObjectDumper.dumpObject(testProcessingMap, DumpMode.DEEP));

        final NotificationLogListener notificationLogListener = new NotificationLogListener();
        final TestPlanner testPlanner = new TestPlanner(testProcessingMap);
        testPlanner.addNotificationListener(notificationLogListener);
        final TestPlan testPlan = testPlanner.generateTestPlan();
        System.out.println("Test plan structure:\n" + testPlan.debugStructure());

        System.out.println("Test plan: " + ObjectDumper.dumpObject(testPlan, DumpMode.DEEP));

        final TestSessionState testSessionState = new TestSessionState(testPlan);
        final TestSessionControllerSettings testSessionControllerSettings = new TestSessionControllerSettings();
        final TestSessionController testSessionController = new TestSessionController(jqtiExtensionManager, testSessionControllerSettings, testProcessingMap, testSessionState);
        testSessionController.addNotificationListener(notificationLogListener);

        testSessionController.initialize();
        testSessionController.enterTest();
        testSessionController.enterNextAvailableTestPart();
        System.out.println("Test state after entry: " + ObjectDumper.dumpObject(testSessionState, DumpMode.DEEP));

        final TestPlanNode firstItemRefNode = testPlan.getTestPartNodes().get(0).searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF).get(0);
        testSessionController.selectItemNonlinear(firstItemRefNode.getKey());
        System.out.println("First item is " + firstItemRefNode);

        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        responseMap.put(Identifier.parseString("RESPONSE"), new StringResponseData("ChoiceA"));
        testSessionController.handleResponses(responseMap);

        System.out.println("Test state at end: " + ObjectDumper.dumpObject(testSessionState, DumpMode.DEEP));

        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(firstItemRefNode.getKey());
        System.out.println("First item state: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));
    }
}
