/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.control.AssessmentTestController;
import uk.ac.ed.ph.jqtiplus.control.JQTIController;
import uk.ac.ed.ph.jqtiplus.control.Timer;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.state.AssessmentTestState;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.AssessmentTestManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathHTTPResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.QTIObjectManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.QTIReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.SimpleQTIObjectCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.SupportedXMLReader;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLParseResult;

import java.net.URI;

/**
 * @author  David McKain
 * @version $Revision: 2783 $
 */
public class TestTest {
    
    public static void main(String[] args) {
        URI inputUri = StandaloneXMLResourceLocator.getMAFileUri("web-app/WEB-INF/content/tests/test_package_minfilesG/example/rtest.xml");
        
        JQTIController jqtiController = new JQTIController();
        
        System.out.println("Reading and validating");
        SupportedXMLReader xmlReader = new SupportedXMLReader(new ClassPathHTTPResourceLocator(), true);
        QTIObjectManager objectManager = new QTIObjectManager(jqtiController, xmlReader, new StandaloneXMLResourceLocator(), new SimpleQTIObjectCache());

        QTIReadResult<AssessmentTest> qtiReadResult = objectManager.getQTIObject(inputUri, AssessmentTest.class);
        XMLParseResult xmlParseResult = qtiReadResult.getXMLParseResult();
        if (!xmlParseResult.isSchemaValid()) {
            System.out.println("Schema validation failed: " + xmlParseResult);
            return;
        }
        
        AssessmentTest test = qtiReadResult.getJQTIObject();
        AssessmentTestManager testManager = new AssessmentTestManager(objectManager, test);
        ValidationResult validationResult = testManager.validateTest();
        if (!validationResult.getAllItems().isEmpty()) {
            System.out.println("JQTI validation failed: " + validationResult);
            return;          
        }
        
        System.out.println("Initializing test");
        AssessmentTestState testState = new AssessmentTestState(test);
        AssessmentTestController testController = new AssessmentTestController(testManager, testState, new Timer());
        testController.initialize();
        
        System.out.println("Test state: " + ObjectDumper.dumpObject(testState, DumpMode.DEEP));
        System.out.println("Test structure: " + testState.debugStructure());
    }
}
