/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;

import java.net.URI;

public class TemplateConstraintTest {

    public static void main(final String[] args) {
        final URI inputUri = URI.create("classpath:/templateConstraint-1.xml");

        System.out.println("Reading and validating");
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        final QtiObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());

        final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);

        final ItemValidationResult itemValidationResult = objectManager.resolveAndValidateItem(inputUri);
        System.out.println("Validation result: " + ObjectDumper.dumpObject(itemValidationResult, DumpMode.DEEP));

        final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(itemValidationResult).initialize();
        System.out.println("Run map is " + ObjectDumper.dumpObject(itemProcessingMap, DumpMode.DEEP));

        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        final ItemSessionState itemSessionState = new ItemSessionState();
        final ItemSessionController itemSessionController = new ItemSessionController(jqtiExtensionManager, itemSessionControllerSettings, itemProcessingMap, itemSessionState);

        System.out.println("\nInitialising");
        itemSessionController.initialize();
        itemSessionController.performTemplateProcessing();
        System.out.println("Item state after init: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));
    }
}
