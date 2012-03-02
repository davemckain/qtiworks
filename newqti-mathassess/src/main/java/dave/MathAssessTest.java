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

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.serialization.SaxEventFirer;
import uk.ac.ed.ph.jqtiplus.serialization.SerializationOptions;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathResourceLocator;

import org.qtitools.mathassess.MathAssessExtensionPackage;

import uk.ac.ed.ph.snuggletex.utilities.SimpleStylesheetCache;

import java.io.StringWriter;
import java.net.URI;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

public class MathAssessTest {

    public static void main(String[] args) throws Exception {
        final URI inputUri = URI.create("classpath:/MAD01-SRinCO-demo.xml");

        final MathAssessExtensionPackage mathAssessPackage = new MathAssessExtensionPackage();
        mathAssessPackage.setStylesheetCache(new SimpleStylesheetCache());
        mathAssessPackage.init();

        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager(mathAssessPackage);
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        final QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());
        AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
        
        ItemValidationResult result = objectManager.resolveAndValidateItem(inputUri);
        System.out.println("Validation result: " + ObjectDumper.dumpObject(result, DumpMode.DEEP));
        System.out.println("Extensions used: " + QueryUtils.findExtensionsUsed(result.getResolvedAssessmentItem()));
        System.out.println("Foreign namespaces: " + QueryUtils.findForeignNamespaces(result.getResolvedAssessmentItem().getItemLookup().extractEnsuringSuccessful()));
        
        /* TODO: Bring some of the SnuggleTeX XML Utility classes in to make this easier */
        SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler transformerHandler = saxTransformerFactory.newTransformerHandler();
        transformerHandler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter s = new StringWriter();
        transformerHandler.setResult(new StreamResult(s));
        
        SaxEventFirer saxEventFirer = new SaxEventFirer(jqtiExtensionManager);
        saxEventFirer.fireSaxDocument(result.getResolvedAssessmentItem().getItemLookup().extractEnsuringSuccessful(), transformerHandler, new SerializationOptions());
        
        System.out.println(s);

//        try {
//            final AssessmentItemState itemState = new AssessmentItemState();
//            final AssessmentItemAttemptController itemController = new AssessmentItemAttemptController(itemManager, itemState);
//
//            System.out.println("\n\nInitialising");
//            itemController.initialize(null);
//            System.out.println("\nTemplate Values: " + itemState.getTemplateValues());
//            System.out.println("\nResponse Values: " + itemState.getResponseValues());
//            System.out.println("\nOutcome Values: " + itemState.getOutcomeValues());
//
//            System.out.println("\n\nSetting Math responses");
//            final Map<String, List<String>> responses = new HashMap<String, List<String>>();
//            responses.put("RESPONSE", Arrays.asList(new String[] { "1+x" }));
//            itemController.setResponses(responses);
//            System.out.println("Response Values: " + itemState.getResponseValues());
//
//            System.out.println("\n\nStarting response processiong");
//            itemController.processResponses();
//            System.out.println("Response processing finished");
//            System.out.println("Outcome Values: " + itemState.getOutcomeValues());
//
//            System.out.println("\n\nResult XML");
//            System.out.println(itemController.computeItemResult().toXmlString());
//        }
//        finally {
//            mathAssessPackage.shutdown();
//        }
    }
}
