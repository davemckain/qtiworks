/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.running.AssessmentTestPlanner;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;

import java.net.URI;

/**
 * (Used for ad hoc test of test functionality)
 *
 * @author David McKain
 */
public final class TestTest {

    public static void main(final String[] args) throws Exception {
//        final URI inputUri = URI.create("classpath:/testimplementation/non_unique_identifier.xml");
        final URI inputUri = URI.create("classpath:/testimplementation/minimal.xml");

        System.out.println("Reading and validating");
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(new JqtiExtensionManager());
        final QtiObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());

        final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);

        final TestValidationResult result = objectManager.resolveAndValidateTest(inputUri);
        System.out.println("Validation result: " + ObjectDumper.dumpObject(result, DumpMode.DEEP));

        final AssessmentTest test = result.getResolvedAssessmentTest().getTestLookup().getRootNodeHolder().getRootNode();
        final AssessmentTestPlanner testPlanner = new AssessmentTestPlanner(test);
        final TestPlan testPlan = testPlanner.run();
        System.out.println(testPlan.debugStructure());
    }
}
