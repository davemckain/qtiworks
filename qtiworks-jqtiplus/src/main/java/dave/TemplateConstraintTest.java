/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.running.AssessmentItemAttemptController;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemState;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathResourceLocator;

import java.net.URI;

public class TemplateConstraintTest {
    
    public static void main(String[] args) throws RuntimeValidationException {
        URI inputUri = URI.create("classpath:/templateConstraint-1.xml");
        
        System.out.println("Reading and validating");
        JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());
        
        AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);

        ItemValidationResult result = objectManager.resolveAndValidateItem(inputUri);
        System.out.println("Validation result: " + ObjectDumper.dumpObject(result, DumpMode.DEEP));
        
        AssessmentItemState itemState = new AssessmentItemState();
        AssessmentItemAttemptController itemController = new AssessmentItemAttemptController(jqtiExtensionManager, result.getResolvedAssessmentItem(), itemState);
        
        System.out.println("\nInitialising");
        itemController.initialize(null);
        System.out.println("Template Values: " + itemState.getTemplateValues());
    }

}
