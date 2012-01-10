/* Copyright (c) 2012, University of Edinburgh.
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
package dave;

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemState;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.AssessmentItemManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.QTIObjectManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.SimpleQTIObjectCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.SupportedXMLReader;
import uk.ac.ed.ph.jqtiplus.xperimental.control.AssessmentItemController;
import uk.ac.ed.ph.jqtiplus.xperimental.control.JQTIController;

import org.qtitools.mathassess.MathAssessConstants;
import org.qtitools.mathassess.MathAssessExtensionPackage;

import uk.ac.ed.ph.snuggletex.utilities.SimpleStylesheetCache;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathAssessTest {

    public static void main(String[] args) {
        final URI inputUri = URI.create("classpath:/MAD01-SRinCO-demo.xml");

        final MathAssessExtensionPackage mathAssessPackage = new MathAssessExtensionPackage();
        mathAssessPackage.setStylesheetCache(new SimpleStylesheetCache());
        mathAssessPackage.init();

        final JQTIController jqtiController = new JQTIController();
        jqtiController.getExtensionPackages().add(mathAssessPackage);

        try {
            System.out.println("Reading and validating");
            final SupportedXMLReader xmlReader = new SupportedXMLReader(true);

            /* FIXME: Can this somehow be integrated with the package stuff? */
            xmlReader.getRegisteredSchemaMap().put(MathAssessConstants.MATHASSESS_NAMESPACE_URI, MathAssessConstants.MATHASSESS_SCHEMA_LOCATION);

            final QTIObjectManager objectManager = new QTIObjectManager(jqtiController, xmlReader, new ClassPathResourceLocator(), new SimpleQTIObjectCache());
            final QTIReadResult<AssessmentItem> qtiReadResult = objectManager.getQTIObject(inputUri, AssessmentItem.class);
            final XmlParseResult xmlParseResult = qtiReadResult.getXMLParseResult();
            if (!xmlParseResult.isSchemaValid()) {
                System.out.println("Schema validation failed:" + xmlParseResult);
                return;
            }

            final AssessmentItem item = qtiReadResult.getResolvedQtiObject();

            final AssessmentItemManager itemManager = new AssessmentItemManager(objectManager, item);
            final AbstractValidationResult validationResult = itemManager.validateItem();
            if (!validationResult.getAllItems().isEmpty()) {
                System.out.println("JQTI validation failed: " + validationResult);
                return;
            }

            final AssessmentItemState itemState = new AssessmentItemState();
            final AssessmentItemController itemController = new AssessmentItemController(itemManager, itemState);

            System.out.println("\n\nInitialising");
            itemController.initialize(null);
            System.out.println("\nTemplate Values: " + itemState.getTemplateValues());
            System.out.println("\nResponse Values: " + itemState.getResponseValues());
            System.out.println("\nOutcome Values: " + itemState.getOutcomeValues());

            System.out.println("\n\nSetting Math responses");
            final Map<String, List<String>> responses = new HashMap<String, List<String>>();
            responses.put("RESPONSE", Arrays.asList(new String[] { "1+x" }));
            itemController.setResponses(responses);
            System.out.println("Response Values: " + itemState.getResponseValues());

            System.out.println("\n\nStarting response processiong");
            itemController.processResponses();
            System.out.println("Response processing finished");
            System.out.println("Outcome Values: " + itemState.getOutcomeValues());

            System.out.println("\n\nResult XML");
            System.out.println(itemController.computeItemResult().toXmlString());
        }
        finally {
            mathAssessPackage.shutdown();
        }
    }
}
