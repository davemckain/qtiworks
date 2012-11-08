/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ChoiceRunningTest {

    public static void main(final String[] args) {
        final URI inputUri = URI.create("classpath:/choice.xml");

        System.out.println("Reading");
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        final QtiObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());
        final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
        final ResolvedAssessmentItem resolvedAssessmentItem = objectManager.resolveAssessmentItem(inputUri, ModelRichness.FULL_ASSUMED_VALID);

        final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(resolvedAssessmentItem, false).initialize();
        System.out.println("Run map is: " + ObjectDumper.dumpObject(itemProcessingMap, DumpMode.DEEP));

        final ItemSessionState itemSessionState = new ItemSessionState();
        System.out.println("Item state before init: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        final ItemSessionController itemSessionController = new ItemSessionController(jqtiExtensionManager, itemSessionControllerSettings, itemProcessingMap, itemSessionState);

        System.out.println("\nInitialising");
        itemSessionController.initialize();
        itemSessionController.performTemplateProcessing();
        System.out.println("Item state after init: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

        System.out.println("\nBinding & validating responses");
        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        responseMap.put(Identifier.parseString("RESPONSE"), new StringResponseData("ChoiceA"));
        itemSessionController.bindResponses(responseMap);
        System.out.println("Unbound responses: " + itemSessionState.getUnboundResponseIdentifiers());
        System.out.println("Invalid responses:" + itemSessionState.getInvalidResponseIdentifiers());

        System.out.println("\nInvoking response processing");
        itemSessionController.performResponseProcessing();
        System.out.println("Item state after RP1: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));
    }
}
