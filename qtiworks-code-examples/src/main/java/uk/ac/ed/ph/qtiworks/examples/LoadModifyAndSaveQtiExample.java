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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.examples;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.content.basic.TextRun;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.CorrectResponse;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.ChoiceInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Prompt;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleChoice;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.shared.FieldValue;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReadResult;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlInterpretationException;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;

/**
 * This example builds a JQTI+ Object model from a bundled <code>minimal.xml</code> QTI XML file,
 * then uses the JQTI+ to enhance the object model by adding a responseDeclaration and a choiceInteraction.
 * The resulting model is then serialized to XML and printed out.
 *
 * <h3>How to run</h3>
 *
 * You can run this via Maven as follows:
 * <pre>
 * mvn exec:java -Dexec.mainClass=uk.ac.ed.ph.qtiworks.examples.LoadModifyAndSaveQtiExample
 * </pre>
 * You should also be able to run this inside your favourite IDE if you have loaded the QTIWorks
 * source code into it.
 *
 * @author David McKain
 */
public final class LoadModifyAndSaveQtiExample {

    public static void main(final String[] args) throws Exception {
        /* We'll be loading a bundled example file called minimal.xml, which you can find
         * in src/main/resources and is included in the ClassPath when this project is built.
         * We use a ClassPathResourceLocator to load this, using the <code>classpath:</code>
         * pseudo-URL.
         */
        final ResourceLocator inputResourceLocator = new ClassPathResourceLocator();
        final URI inputUri = URI.create("classpath:/minimal.xml");

        /* Load the QTI XML, perform schema validation, and build a JQTI+ Object model from it,
         * expecting an AssessmentItem.
         *
         * (The current API for this requires joining a few Objects together; it would be nice to
         * have a simple facade that makes it easier 95% of the time.)
         */
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(inputResourceLocator, true /* = perform schema validation */);
        QtiObjectReadResult<AssessmentItem> readResult;
        try {
            readResult = qtiObjectReader.lookupRootNode(inputUri, AssessmentItem.class);
        }
        catch (final XmlResourceNotFoundException e) {
            /* This Exception will be thrown the example file could not be found
             * using the ResourceLocator we set up above.
             *
             * This should not happen if the ClassPath is set up properly, so I'll let this
             * Exception propagate upwards here.
             */
            throw e;
        }
        catch (final QtiXmlInterpretationException e) {
            /* This is thrown if a JQTI+ Object model could not be constructed from the QTI XML,
             * or the resulting model wasn't an AssessmetnItem.
             *
             * This shouldn't happen here, so I'll propagate this one up.
             */
            throw e;
        }

        /* We can now extract the built AssessmentItem Object */
        final AssessmentItem assessmentItem = readResult.getRootNode();

        /* Add a new response declaration called RESPONSE.
         *
         * (The process for adding nodes into the Object model is slightly asymmetric, which I
         * now think I should have done differently! If you're adding a "child" of type C to a
         * "parent" p of type P then you'll generally have to do something like:
         *
         * C c = new C(p);
         * p.getSomeGroupOfChildNodes().add(c);
         */
        final ResponseDeclaration responseDeclaration = new ResponseDeclaration(assessmentItem);
        assessmentItem.getResponseDeclarations().add(responseDeclaration);
        responseDeclaration.setIdentifier(Identifier.assumedLegal("RESPONSE"));
        responseDeclaration.setCardinality(Cardinality.SINGLE);
        responseDeclaration.setBaseType(BaseType.IDENTIFIER);
        final CorrectResponse correctResponse = new CorrectResponse(responseDeclaration);
        responseDeclaration.setCorrectResponse(correctResponse);
        correctResponse.getFieldValues().add(new FieldValue(correctResponse, new IdentifierValue("CHOICEA")));

        /* Add a choiceInteraction with a prompt and 2 simpleChoices,
         * linked to the RESPONSE variable we declared above */
        final ItemBody itemBody = assessmentItem.getItemBody();
        final ChoiceInteraction choiceInteraction = new ChoiceInteraction(itemBody);
        itemBody.getBlocks().add(choiceInteraction);
        choiceInteraction.setResponseIdentifier(responseDeclaration.getIdentifier());
        choiceInteraction.setShuffle(true);
        choiceInteraction.setMaxChoices(1);
        final Prompt prompt = new Prompt(choiceInteraction);
        choiceInteraction.setPrompt(prompt);
        prompt.getInlineStatics().add(new TextRun(prompt, "Pick the correct answer"));
        final SimpleChoice simpleChoice1 = new SimpleChoice(choiceInteraction);
        simpleChoice1.setIdentifier(Identifier.assumedLegal("CHOICE1"));
        simpleChoice1.getChildren().add(new TextRun(simpleChoice1, "Choice 1"));
        choiceInteraction.getSimpleChoices().add(simpleChoice1);
        final SimpleChoice simpleChoice2 = new SimpleChoice(choiceInteraction);
        simpleChoice2.setIdentifier(Identifier.assumedLegal("CHOICE2"));
        simpleChoice2.getChildren().add(new TextRun(simpleChoice1, "Choice 2"));
        choiceInteraction.getSimpleChoices().add(simpleChoice2);

        /* We'll add a reponseProcessing as well, using one of the default templates */
        final ResponseProcessing responseProcessing = new ResponseProcessing(assessmentItem);
        responseProcessing.setTemplate(URI.create("http://www.imsglobal.org/question/qti_v2p1/rptemplates/match_correct"));
        assessmentItem.setResponseProcessing(responseProcessing);

        /* Finally we serialize the updated assessmentItem to an XML string and print it out */
        final QtiSerializer qtiSerializer = new QtiSerializer(jqtiExtensionManager);
        System.out.println(qtiSerializer.serializeJqtiObject(assessmentItem));
    }

}
