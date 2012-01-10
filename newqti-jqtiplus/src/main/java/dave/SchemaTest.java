/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.AssessmentItemManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.QTIObjectManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.QTIReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.SimpleQTIObjectCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.SupportedXMLReader;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.XMLParseResult;

import java.net.URI;

public class SchemaTest {
    
    public static void main(String[] args) throws Exception {
        JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        SupportedXMLReader xmlReader = new SupportedXMLReader(true);
        QTIObjectManager objectManager = new QTIObjectManager(jqtiExtensionManager, xmlReader, new ClassPathResourceLocator(), new SimpleQTIObjectCache());
        
        URI inputFileUri = URI.create("classpath:/mathextensions.xml");
        QTIReadResult<AssessmentItem> result = objectManager.getQTIObject(inputFileUri, AssessmentItem.class);
        AssessmentItem item = result.getJQTIObject();
        
        XMLParseResult xmlParseResult = result.getXMLParseResult();
        System.out.println("Fatal errors are: " + xmlParseResult.getFatalErrors());
        System.out.println("Errors are: " + xmlParseResult.getErrors());
        System.out.println("Warnings are: " + xmlParseResult.getWarnings());
        System.out.println("Supported schemas: " + xmlParseResult.getSupportedSchemaNamespaces());
        
        System.out.println("Result: " + result);
        
        AssessmentItemManager itemManager = new AssessmentItemManager(objectManager, item);
        System.out.println("JQTI validation: " + itemManager.validateItem());
    }
}
