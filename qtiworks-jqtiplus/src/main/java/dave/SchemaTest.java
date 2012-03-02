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
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.serialization.SaxEventFirer;
import uk.ac.ed.ph.jqtiplus.serialization.SerializationOptions;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathResourceLocator;

import java.io.StringWriter;
import java.net.URI;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

public class SchemaTest {
    
    public static void main(String[] args) throws Exception {
        URI inputUri = URI.create("classpath:/mathextensions.xml");
        
        System.out.println("Reading and validating");
        JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        QtiXmlReader qtiXmlReader = new QtiXmlReader();
        QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());
        
        AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);

        ItemValidationResult result = objectManager.resolveAndValidateItem(inputUri);
        System.out.println("Validation result: " + ObjectDumper.dumpObject(result, DumpMode.DEEP));
        
        ResolvedAssessmentItem item = result.getResolvedAssessmentItem();
        System.out.println("Extensions used: " + QueryUtils.findExtensionsUsed(item));
        
        /* TODO: Bring some of the SnuggleTeX XML Utility classes in to make this easier */
        SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler transformerHandler = saxTransformerFactory.newTransformerHandler();
        transformerHandler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter s = new StringWriter();
        transformerHandler.setResult(new StreamResult(s));
        
        SaxEventFirer saxEventFirer = new SaxEventFirer(jqtiExtensionManager);
        saxEventFirer.fireSaxDocument(item.getItemLookup().extractEnsuringSuccessful(), transformerHandler, new SerializationOptions());
        
        System.out.println(s);
        
        
    }
}
