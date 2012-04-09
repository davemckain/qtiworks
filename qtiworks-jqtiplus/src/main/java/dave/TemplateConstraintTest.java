/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;

import java.net.URI;

public class TemplateConstraintTest {

    public static void main(final String[] args) throws RuntimeValidationException {
//        URI inputUri = URI.create("classpath:/templateConstraint-1.xml");
        final URI inputUri = URI.create("classpath:/towns.xml");

        System.out.println("Reading and validating");
        final QtiXmlReader qtiXmlReader = new QtiXmlReader();
        final QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());

        final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);

        final ItemValidationResult result = objectManager.resolveAndValidateItem(inputUri);
        System.out.println("Validation result: " + ObjectDumper.dumpObject(result, DumpMode.DEEP));

        final ItemSessionState itemState = new ItemSessionState();
        final ItemSessionController itemController = new ItemSessionController(result.getResolvedAssessmentItem(), itemState);

        System.out.println("\nInitialising");
        itemController.initialize();
        System.out.println("Item state after init: " + ObjectDumper.dumpObject(itemState, DumpMode.DEEP));
    }
}
