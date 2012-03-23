/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.SimpleXsltStylesheetCache;

import java.net.URI;

public class RenderingTest {
    
    public static void main(String[] args) throws RuntimeValidationException {
        URI inputUri = URI.create("classpath:/templateConstraint-1.xml");
        
        System.out.println("Reading");
        JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        jqtiExtensionManager.init();
        try {
            QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
            QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());
            
            AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
            ResolvedAssessmentItem resolvedAssessmentItem = objectManager.resolveAssessmentItem(inputUri, ModelRichness.FULL_ASSUMED_VALID);
            ItemSessionState itemSessionState = new ItemSessionState();
            ItemSessionController itemController = new ItemSessionController(jqtiExtensionManager, resolvedAssessmentItem, itemSessionState);
            
            System.out.println("\nInitialising");
            itemController.initialize();
            System.out.println("Item session state after init: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

            System.out.println("\nRendering");
            AssessmentRenderer renderer = new AssessmentRenderer("/ENGINE", new SimpleXsltStylesheetCache());
            String rendered = renderer.renderFreshStandaloneItem(resolvedAssessmentItem, itemSessionState, 
                    "/RESOURCES", null, SerializationMethod.HTML5_MATHJAX);
            System.out.println("Rendered page: " + rendered);
        }
        finally {
            jqtiExtensionManager.destroy();
        }
    }
}
