/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012-2013, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.qtiworks.examples;

import uk.ac.ed.ph.jqtiplus.SimpleJqtiFacade;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
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
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Example of running <code>choice.xml</code> using the {@link SimpleJqtiFacade}.
 * <p>
 * (Not documented very well yet. Sorry!)
 *
 * @author David McKain
 */
public final class ChoiceRunningExample {

    public static void main(final String[] args) {
        final ResourceLocator inputResourceLocator = new ClassPathResourceLocator();
        final URI inputUri = URI.create("classpath:/choice.xml");

        System.out.println("Reading");
        final SimpleJqtiFacade simpleJqtiFacade = new SimpleJqtiFacade();
        final ResolvedAssessmentItem resolvedAssessmentItem = simpleJqtiFacade.loadAndResolveAssessmentItem(inputResourceLocator, inputUri);

        final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(resolvedAssessmentItem, false).initialize();
        System.out.println("Run map is: " + ObjectDumper.dumpObject(itemProcessingMap, DumpMode.DEEP));

        final ItemSessionState itemSessionState = new ItemSessionState();
        System.out.println("State before init: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        final ItemSessionController itemSessionController = simpleJqtiFacade.createItemSessionController(itemSessionControllerSettings, itemProcessingMap, itemSessionState);

        System.out.println("\nInitialising");
        final Date timestamp1 = new Date();
        itemSessionController.initialize(timestamp1);
        itemSessionController.performTemplateProcessing(timestamp1);
        System.out.println("State after init: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

        System.out.println("\nEntering item");
        final Date timestamp2 = ObjectUtilities.addToTime(timestamp1, 1000L);
        itemSessionController.enterItem(timestamp2);
        System.out.println("State after entry: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

        System.out.println("\nBinding & validating responses");
        final Date timestamp3 = ObjectUtilities.addToTime(timestamp2, 1000L);
        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        responseMap.put(Identifier.parseString("RESPONSE"), new StringResponseData("ChoiceA"));
        itemSessionController.bindResponses(timestamp3, responseMap);
        System.out.println("Unbound responses: " + itemSessionState.getUnboundResponseIdentifiers());
        System.out.println("Invalid responses:" + itemSessionState.getInvalidResponseIdentifiers());
        System.out.println("State after binding: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

        System.out.println("\nCommitting responses");
        final Date timestamp4 = ObjectUtilities.addToTime(timestamp3, 1000L);
        itemSessionController.commitResponses(timestamp4);
        System.out.println("State after committing: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

        System.out.println("\nInvoking response processing");
        final Date timestamp5 = ObjectUtilities.addToTime(timestamp4, 1000L);
        itemSessionController.performResponseProcessing(timestamp5);
        System.out.println("State after RP1: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

        System.out.println("\nExplicitly closing item session");
        final Date timestamp6 = ObjectUtilities.addToTime(timestamp5, 1000L);
        itemSessionController.endItem(timestamp6);
        System.out.println("State after end of session: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

        final Date timestamp7 = ObjectUtilities.addToTime(timestamp6, 1000L);
        itemSessionController.exitItem(timestamp7);
        System.out.println("State after exit: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));
    }
}
