/* Copyright (c) 2012-2013, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.examples;

import uk.ac.ed.ph.qtiworks.mathassess.MathAssessExtensionPackage;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.SimpleJqtiFacade;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.serialization.SaxFiringOptions;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.SimpleXsltStylesheetCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltSerializationOptions;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic example demonstrating the MathAssess extensions
 *
 * @author David McKain
 */
public final class MathAssessExample {

    public static void main(final String[] args) throws Exception {
        final ResourceLocator inputResourceLocator = new ClassPathResourceLocator();
        final URI inputUri = URI.create("classpath:/MAD01-SRinCO-demo.xml");

        final MathAssessExtensionPackage mathAssessPackage = new MathAssessExtensionPackage(new SimpleXsltStylesheetCache());
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager(mathAssessPackage);
        try {
            jqtiExtensionManager.init();

            System.out.println("Reading " + inputUri);
            final SimpleJqtiFacade simpleJqtiFacade = new SimpleJqtiFacade(jqtiExtensionManager);
            final ItemValidationResult itemValidationResult = simpleJqtiFacade.loadResolveAndValidateItem(inputResourceLocator, inputUri);
            final ResolvedAssessmentItem resolvedAssessmentItem = itemValidationResult.getResolvedAssessmentItem();
            final RootNodeLookup<AssessmentItem> itemLookup = resolvedAssessmentItem.getItemLookup();
            final AssessmentItem assessmentItem = itemLookup.extractAssumingSuccessful();
            System.out.println("Validation result: " + ObjectDumper.dumpObject(itemValidationResult, DumpMode.DEEP));
            System.out.println("Extensions used: " + QueryUtils.findExtensionsUsed(jqtiExtensionManager, resolvedAssessmentItem));
            System.out.println("Foreign namespaces: " + QueryUtils.findForeignNamespaces(assessmentItem));

            /* Should be valid */
            assert itemValidationResult.isValid();

            /* Serialize back again */
            final XsltSerializationOptions xsltSerializationOptions = new XsltSerializationOptions();
            final SaxFiringOptions saxFiringOptions = new SaxFiringOptions();
            xsltSerializationOptions.setIndenting(true);
            final QtiSerializer qtiSerializer = simpleJqtiFacade.createQtiSerializer();
            final String serializedXml = qtiSerializer.serializeJqtiObject(assessmentItem, saxFiringOptions, xsltSerializationOptions);
            System.out.println("\n\nSerialized XML:\n" + serializedXml);

            /* Now attempt to run */
            final ItemSessionState itemSessionState = new ItemSessionState();
            final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(itemValidationResult).initialize();
            final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
            final ItemSessionController itemSessionController = new ItemSessionController(jqtiExtensionManager,
                    itemSessionControllerSettings, itemProcessingMap, itemSessionState);

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
            responseMap.put(Identifier.parseString("RESPONSE"), new StringResponseData("1+x"));
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
        finally {
            jqtiExtensionManager.destroy();
        }
    }
}
