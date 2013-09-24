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
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.FieldValue;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectResolver;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidator;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NullResourceLocator;

/**
 * This example builds a simple JQTI+ {@link AssessmentItem} programmatically,
 * checks its validity, then prints out the resulting XML.
 *
 * <h3>How to run</h3>
 *
 * You can run this via Maven as follows:
 * <pre>
 * mvn exec:java -Dexec.mainClass=uk.ac.ed.ph.qtiworks.examples.DynamicItemExample
 * </pre>
 * You should also be able to run this inside your favourite IDE if you have loaded the QTIWorks
 * source code into it.
 *
 * @author David McKain
 */
public final class DynamicItemExample {

    public static void main(final String[] args) throws Exception {
        /* Create empty AssessmentItem and add necessary properties to make it valid */
        final AssessmentItem assessmentItem = new AssessmentItem();
        assessmentItem.setIdentifier("MyItem");
        assessmentItem.setTitle("Title");
        assessmentItem.setAdaptive(Boolean.FALSE);
        assessmentItem.setTimeDependent(Boolean.FALSE);

        /* Declare a SCORE outcome variable */
        final OutcomeDeclaration score = new OutcomeDeclaration(assessmentItem);
        score.setIdentifier(Identifier.assumedLegal("SCORE"));
        score.setCardinality(Cardinality.SINGLE);
        score.setBaseType(BaseType.FLOAT);
        final DefaultValue defaultValue = new DefaultValue(score);
        defaultValue.getFieldValues().add(new FieldValue(defaultValue, new FloatValue(0.0)));
        score.setDefaultValue(defaultValue);
        assessmentItem.getOutcomeDeclarations().add(score);

        /* Validate */
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(NullResourceLocator.getInstance(), false);
        final AssessmentObjectResolver resolver = new AssessmentObjectResolver(qtiObjectReader);
        final ResolvedAssessmentItem resolvedAssessmentItem = resolver.resolveAssessmentItem(assessmentItem);
        final AssessmentObjectValidator validator = new AssessmentObjectValidator(jqtiExtensionManager);
        final ItemValidationResult validationResult = validator.validateItem(resolvedAssessmentItem);

        /* Print out validation result */
        System.out.println("Validation result:");
        ObjectDumper.dumpObjectToStdout(validationResult);

        /* Finally serialize the assessmentItem to XML and print it out */
        final QtiSerializer qtiSerializer = new QtiSerializer(jqtiExtensionManager);
        System.out.println("Serialized XML:");
        System.out.println(qtiSerializer.serializeJqtiObject(assessmentItem));
    }

}
