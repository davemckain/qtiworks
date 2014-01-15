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

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.state.EffectiveItemSessionControl;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.state.marshalling.ItemSessionStateXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.state.marshalling.TestPlanXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.state.marshalling.TestSessionStateXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import uk.ac.ed.ph.snuggletex.XMLStringOutputOptions;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.w3c.dom.Document;

/**
 * Debugging for the item/test marshalling classes. Really needs to be made into a unit test...
 *
 * @author David McKain
 */
public final class XmlMarshalTest {

    public static void main(final String[] args) {
        debugItemSessionState();
        debugTestPlan();
        debugTestSessionState();
    }

    public static void debugItemSessionState() {
        final ItemSessionState itemSessionState = createItemSessionState();

        /* Marshal */
        final Document document = ItemSessionStateXmlMarshaller.marshal(itemSessionState);
        final String serialized = serializeAndDumpDocument("Marshalled ItemSessionState", document);

        /* Unmarshal */
        final ItemSessionState parsed = ItemSessionStateXmlMarshaller.unmarshal(serialized);
        System.out.println("Got back: " + ObjectDumper.dumpObject(parsed, DumpMode.DEEP));

        /* Compare */
        System.out.println("Compare? " + parsed.equals(itemSessionState));
    }

    private static ItemSessionState createItemSessionState() {
        final ItemSessionState itemSessionState = new ItemSessionState();

        final Map<Identifier, SingleValue> recordMap = new HashMap<Identifier, SingleValue>();
        recordMap.put(Identifier.parseString("PMathML"), new StringValue("Hello"));
        recordMap.put(Identifier.parseString("Number"), new IntegerValue(5));

        final Value rv = RecordValue.createRecordValue(recordMap);
        itemSessionState.setShuffledInteractionChoiceOrder(Identifier.parseString("dave"), Arrays.asList(Identifier.parseString("a")));
        itemSessionState.setResponseValue(Identifier.parseString("RESPONSE"), MultipleValue.createMultipleValue(new StringValue("Bad"), new StringValue("Thing")));
        itemSessionState.setTemplateValue(Identifier.parseString("TEMPLATE"), NullValue.INSTANCE);
        itemSessionState.setOutcomeValue(Identifier.parseString("RECORD"), rv);
        itemSessionState.setUnboundResponseIdentifiers(new HashSet<Identifier>(Arrays.asList(Identifier.assumedLegal("RESPONSE"))));
        itemSessionState.setRawResponseData(Identifier.assumedLegal("A"), new StringResponseData("1", "2"));

        return itemSessionState;
    }

    private static String serializeAndDumpDocument(final String title, final Document document) {
        final XMLStringOutputOptions outputOptions = new XMLStringOutputOptions();
        outputOptions.setIndenting(true);
        final String serialized = XMLUtilities.serializeNode(document, outputOptions);
        System.out.println(title + " => " + serialized);
        return serialized;
    }

    private static TestPlan createTestPlan() {
        final TestPlanNode rootNode = TestPlanNode.createRoot();
        final EffectiveItemSessionControl effectiveItemSessionControl = EffectiveItemSessionControl.createDefault();
        final TestPlanNode part1 = new TestPlanNode(TestNodeType.TEST_PART, new TestPlanNodeKey(Identifier.assumedLegal("PART1"), 1, 1), effectiveItemSessionControl);
        final TestPlanNode part2 = new TestPlanNode(TestNodeType.TEST_PART, new TestPlanNodeKey(Identifier.assumedLegal("PART2"), 1, 1), effectiveItemSessionControl);
        rootNode.addChild(part1);
        rootNode.addChild(part2);

        final TestPlanNode section1 = new TestPlanNode(TestNodeType.ASSESSMENT_SECTION, new TestPlanNodeKey(Identifier.assumedLegal("SECTION1"), 2, 1), effectiveItemSessionControl);
        part1.addChild(section1);

        final TestPlanNode item1 = new TestPlanNode(TestNodeType.ASSESSMENT_ITEM_REF, new TestPlanNodeKey(Identifier.assumedLegal("ITEM"), 3, 1), effectiveItemSessionControl, "Title", URI.create("urn:x"));
        final TestPlanNode item2 = new TestPlanNode(TestNodeType.ASSESSMENT_ITEM_REF, new TestPlanNodeKey(Identifier.assumedLegal("ITEM"), 3, 2), effectiveItemSessionControl, "Title 2", URI.create("urn:y"));
        section1.addChild(item1);
        section1.addChild(item2);

        return new TestPlan(rootNode);
    }

    public static void debugTestPlan() {
        final TestPlan testPlan = createTestPlan();

        /* Marshal */
        final Document document = TestPlanXmlMarshaller.marshal(testPlan);
        final String serialized = serializeAndDumpDocument("Marshalled Test Plan", document);

        /* Unmarshal */
        final TestPlan parsed = TestPlanXmlMarshaller.unmarshal(serialized);
        System.out.println("Got back:\n" + parsed.debugStructure());
    }

    public static void debugTestSessionState() {
        final TestSessionState testSessionState = new TestSessionState(createTestPlan());
        testSessionState.setOutcomeValue(Identifier.parseString("SCORE"), new FloatValue(1));

        for (final TestPlanNode itemRefNode : testSessionState.getTestPlan().searchNodes(TestNodeType.ASSESSMENT_ITEM_REF)) {
            testSessionState.getItemSessionStates().put(itemRefNode.getKey(), createItemSessionState());
        }

        /* Marshal */
        final Document document = TestSessionStateXmlMarshaller.marshal(testSessionState);
        final String serialized = serializeAndDumpDocument("Marshalled Test Session State", document);

        /* Unmarshal */
        final TestSessionState parsed = TestSessionStateXmlMarshaller.unmarshal(serialized);
        System.out.println("Got back:\n" + ObjectDumper.dumpObject(parsed, DumpMode.DEEP));
    }
}