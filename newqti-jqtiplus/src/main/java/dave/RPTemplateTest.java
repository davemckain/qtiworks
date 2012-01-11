/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathResourceLocator;

import java.net.URI;
import java.util.Collections;

public class RPTemplateTest {
    
    public static void main(String[] args) throws Exception {
        URI inputUri = URI.create("classpath:/rpTest.xml");
        
        System.out.println("Reading and validating");
        JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        QtiXmlObjectReader objectReader = new QtiXmlObjectReader(jqtiExtensionManager, new ClassPathResourceLocator());
        
        AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);

        ItemValidationResult result = objectManager.validateItem(inputUri);
        System.out.println("Validation result: " + ObjectDumper.dumpObject(result, DumpMode.DEEP));
        
        System.out.println(Collections.emptyList());
    }
}
