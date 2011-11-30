/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.control.AssessmentItemController;
import uk.ac.ed.ph.jqtiplus.control.JQTIController;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemState;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.AssessmentItemManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.QTIObjectManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.QTIReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.SimpleQTIObjectCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.SupportedXMLReader;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLParseResult;

import org.qtitools.mathassess.MathAssessConstants;
import org.qtitools.mathassess.MathAssessExtensionPackage;

import uk.ac.ed.ph.snuggletex.utilities.SimpleStylesheetCache;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathAssessTest {
    
    public static void main(String[] args) {
        URI inputUri = URI.create("classpath:/MAD01-SRinCO-demo.xml");
        
        MathAssessExtensionPackage mathAssessPackage = new MathAssessExtensionPackage();
        mathAssessPackage.setStylesheetCache(new SimpleStylesheetCache());
        mathAssessPackage.init();
        
        JQTIController jqtiController = new JQTIController();
        jqtiController.getExtensionPackages().add(mathAssessPackage);
        
        try {
            System.out.println("Reading and validating");
            SupportedXMLReader xmlReader = new SupportedXMLReader(true);
            
            /* FIXME: Can this somehow be integrated with the package stuff? */
            xmlReader.getRegisteredSchemaMap().put(MathAssessConstants.MATHASSESS_NAMESPACE_URI, MathAssessConstants.MATHASSESS_SCHEMA_LOCATION);
            
            QTIObjectManager objectManager = new QTIObjectManager(jqtiController, xmlReader, new ClassPathResourceLocator(), new SimpleQTIObjectCache());
            QTIReadResult<AssessmentItem> qtiReadResult = objectManager.getQTIObject(inputUri, AssessmentItem.class);
            XMLParseResult xmlParseResult = qtiReadResult.getXMLParseResult();
            if (!xmlParseResult.isSchemaValid()) {
                System.out.println("Schema validation failed:" + xmlParseResult);
                return;
            }
            
            AssessmentItem item = qtiReadResult.getJQTIObject();

            AssessmentItemManager itemManager = new AssessmentItemManager(objectManager, item);
            ValidationResult validationResult = itemManager.validateItem();
            if (!validationResult.getAllItems().isEmpty()) {
                System.out.println("JQTI validation failed: " + validationResult);
                return;          
            }
            
            AssessmentItemState itemState = new AssessmentItemState();
            AssessmentItemController itemController = new AssessmentItemController(itemManager, itemState);
            
            System.out.println("\n\nInitialising");
            itemController.initialize(null);
            System.out.println("\nTemplate Values: " + itemState.getTemplateValues());
            System.out.println("\nResponse Values: " + itemState.getResponseValues());
            System.out.println("\nOutcome Values: " + itemState.getOutcomeValues());
            
            System.out.println("\n\nSetting Math responses");
            Map<String, List<String>> responses = new HashMap<String, List<String>>();
            responses.put("RESPONSE", Arrays.asList(new String[] { "1+x" } ));
            itemController.setResponses(responses);
            System.out.println("Response Values: " + itemState.getResponseValues());
            
            System.out.println("\n\nStarting response processiong");
            itemController.processResponses();
            System.out.println("Response processing finished");
            System.out.println("Outcome Values: " + itemState.getOutcomeValues());
            
            System.out.println("\n\nResult XML");
            System.out.println(itemController.computeItemResult().toXmlString());
        }
        finally {
            mathAssessPackage.shutdown();
        }
    }
}
