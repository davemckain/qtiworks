/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.control2.JQTIExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiRequireResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLParseResult;
import uk.ac.ed.ph.jqtiplus.xperimental.AssessmentTestValidator;

import java.net.URI;

public class TestTest {
    
    public static void main(String[] args) throws Exception {
        URI inputUri = URI.create("classpath:/WebDeveloperTest1/template_test1.xml");
        
        System.out.println("Reading and validating");
        JQTIExtensionManager jqtiExtensionManager = new JQTIExtensionManager();
        QtiObjectReader objectReader = new QtiObjectReader(jqtiExtensionManager, new ClassPathResourceLocator(), true);

        QtiRequireResult<AssessmentTest> qtiReadResult = objectReader.readQti(inputUri, AssessmentTest.class);
        System.out.println("Read in " + ObjectDumper.dumpObject(qtiReadResult, DumpMode.DEEP));
        
        XMLParseResult xmlParseResult = qtiReadResult.getXmlParseResult();
        if (!xmlParseResult.isSchemaValid()) {
            System.out.println("Schema validation failed: " + xmlParseResult);
            return;
        }
        
        AssessmentTest test = qtiReadResult.getRequiredQtiObject();
        AssessmentTestValidator testValidator = new AssessmentTestValidator(test, objectReader);
        ValidationResult validationResult = testValidator.validate();
        System.out.println("Validation result: " + ObjectDumper.dumpObject(validationResult, DumpMode.DEEP));
        if (!validationResult.getAllItems().isEmpty()) {
            System.out.println("JQTI validation failed: " + validationResult);
            return;          
        }

    }
}
