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
import uk.ac.ed.ph.jqtiplus.running.ItemRunInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemRunMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChoiceRunningTest {

    public static void main(final String[] args) {
        final URI inputUri = URI.create("classpath:/choice.xml");

        System.out.println("Reading");
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        final QtiObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());
        final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
        final ResolvedAssessmentItem resolvedAssessmentItem = objectManager.resolveAssessmentItem(inputUri, ModelRichness.FULL_ASSUMED_VALID);

        final ItemRunMap itemRunMap = new ItemRunInitializer(resolvedAssessmentItem).initialize();
        System.out.println("Run map is: " + ObjectDumper.dumpObject(itemRunMap, DumpMode.DEEP));

        final ItemSessionState itemState = new ItemSessionState();
        System.out.println("Item state before init: " + ObjectDumper.dumpObject(itemState, DumpMode.DEEP));

        final ItemSessionController itemController = new ItemSessionController(jqtiExtensionManager, itemRunMap, itemState);

        System.out.println("\nInitialising");
        itemController.performTemplateProcessing();
        System.out.println("Item state after init: " + ObjectDumper.dumpObject(itemState, DumpMode.DEEP));

        System.out.println("\nBinding & validating responses");
        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        responseMap.put(Identifier.parseString("RESPONSE"), new StringResponseData("ChoiceA"));
        final Set<Identifier> badResponses = itemController.bindResponses(responseMap);
        final Set<Identifier> invalidResponses = itemController.validateResponses();
        System.out.println("Bad responses: " + badResponses);
        System.out.println("Invalid responses:" + invalidResponses);

        System.out.println("\nInvoking response processing");
        itemController.performResponseProcessing();
        System.out.println("Item state after RP1: " + ObjectDumper.dumpObject(itemState, DumpMode.DEEP));
    }
}
