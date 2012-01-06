/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.control2.JQTIExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.io.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.io.reading.QtiRequireResult;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLParseResult;
import uk.ac.ed.ph.jqtiplus.xperimental.AssessmentItemValidator;

import java.net.URI;

public class RPTemplateTest {
    
    public static void main(String[] args) throws Exception {
        URI inputUri = URI.create("classpath:/rpTest.xml");
        
        System.out.println("Reading and validating");
        JQTIExtensionManager jqtiExtensionManager = new JQTIExtensionManager();
        QtiObjectReader objectReader = new QtiObjectReader(jqtiExtensionManager, new ClassPathResourceLocator(), true);

        QtiRequireResult<AssessmentItem> qtiReadResult = objectReader.readQti(inputUri, AssessmentItem.class);
        System.out.println("Read in " + ObjectDumper.dumpObject(qtiReadResult, DumpMode.DEEP));
        
        XMLParseResult xmlParseResult = qtiReadResult.getXmlParseResult();
        if (!xmlParseResult.isSchemaValid()) {
            System.out.println("Schema validation failed: " + xmlParseResult);
            return;
        }
        
        AssessmentItem item = qtiReadResult.getRequiredQtiObject();
        AssessmentItemValidator itemValidator = new AssessmentItemValidator(item, objectReader);
        ValidationResult validationResult = itemValidator.validate();
        System.out.println("Validation result: " + ObjectDumper.dumpObject(validationResult, DumpMode.DEEP));
        if (!validationResult.getAllItems().isEmpty()) {
            System.out.println("JQTI validation failed: " + validationResult);
            return;          
        }
        
//        AssessmentItemState itemState = new AssessmentItemState();
//        AssessmentItemController itemController = new AssessmentItemController(itemManager, itemState);
//        
//        System.out.println("\nInitialising");
//        itemController.initialize(null);
//        System.out.println("Template Values: " + itemState.getTemplateValues());
//        System.out.println("Response Values: " + itemState.getResponseValues());
//        System.out.println("Outcome Values: " + itemState.getOutcomeValues());
//        
//        System.out.println("\nSetting responses");
//        itemController.setResponses(new HashMap<String, List<String>>());
//        System.out.println("Response Values: " + itemState.getResponseValues());
//        
//        System.out.println("\nStarting response processiong");
//        itemController.processResponses();
//        System.out.println("Response processing finished");
//        System.out.println("Outcome Values: " + itemState.getOutcomeValues());
//        
//        System.out.println(item.toXmlString());
    }
}
