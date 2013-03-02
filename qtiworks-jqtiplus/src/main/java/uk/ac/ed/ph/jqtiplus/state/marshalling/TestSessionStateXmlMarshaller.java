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
package uk.ac.ed.ph.jqtiplus.state.marshalling;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPartSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Marshals an {@link TestSessionState} to/from XML
 *
 * @author David McKain
 */
public final class TestSessionStateXmlMarshaller {

    public static Document marshal(final TestSessionState testSessionState) {
        final DocumentBuilder documentBuilder = XmlMarshallerCore.createNsAwareDocumentBuilder();
        final Document document = documentBuilder.newDocument();
        appendTestSessionState(document, testSessionState);
        return document;
    }

    static void maybeAddStringifiableAttribute(final Element element, final String attrName, final Object attrValue) {
        if (attrValue!=null) {
            element.setAttribute(attrName, attrValue.toString());
        }
    }

    static void appendTestSessionState(final Node documentOrElement, final TestSessionState testSessionState) {
        final Element element = XmlMarshallerCore.appendElement(documentOrElement, "testSessionState");
        element.setAttribute("entered", StringUtilities.toTrueFalse(testSessionState.isEntered()));
        element.setAttribute("ended", StringUtilities.toTrueFalse(testSessionState.isEnded()));
        element.setAttribute("exited", StringUtilities.toTrueFalse(testSessionState.isExited()));
        maybeAddStringifiableAttribute(element, "currentTestPartKey", testSessionState.getCurrentTestPartKey());
        maybeAddStringifiableAttribute(element, "currentItemKey", testSessionState.getCurrentItemKey());
        element.setAttribute("duration", testSessionState.getDurationValue().toQtiString());

        /* Do test plan */
        TestPlanXmlMarshaller.appendTestPlan(element, testSessionState.getTestPlan());

        /* Do outcome variables */
        XmlMarshallerCore.appendValues(element, "outcomeVariable", testSessionState.getOutcomeValues());

        /* Do states for each TestPart */
        final Map<TestPlanNodeKey, TestPartSessionState> testPartSessionStates = testSessionState.getTestPartSessionStates();
        for (final Entry<TestPlanNodeKey, TestPartSessionState> entry : testPartSessionStates.entrySet()) {
            final TestPlanNodeKey key = entry.getKey();
            final TestPartSessionState testPartSessionState = entry.getValue();
            final Element testPartElement = XmlMarshallerCore.appendElement(element, "testPart");
            testPartElement.setAttribute("key", key.toString());
            TestPartSessionStateXmlMarshaller.appendTestSessionState(testPartElement, testPartSessionState);
        }

        /* Do states for each item */
        final Map<TestPlanNodeKey, ItemSessionState> itemSessionStates = testSessionState.getItemSessionStates();
        for (final Entry<TestPlanNodeKey, ItemSessionState> entry : itemSessionStates.entrySet()) {
            final TestPlanNodeKey key = entry.getKey();
            final ItemSessionState itemSessionState = entry.getValue();
            final Element itemElement = XmlMarshallerCore.appendElement(element, "item");
            itemElement.setAttribute("key", key.toString());
            ItemSessionStateXmlMarshaller.appendItemSessionState(itemElement, itemSessionState);
        }
    }

    //----------------------------------------------

    public static TestSessionState unmarshal(final String xmlString) {
        final DocumentBuilder documentBuilder = XmlMarshallerCore.createNsAwareDocumentBuilder();
        Document document;
        try {
            document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (final Exception e) {
            throw new XmlUnmarshallingException("XML parsing failed", e);
        }
        return unmarshal(document.getDocumentElement());
    }

    public static TestSessionState unmarshal(final Element element) {
        XmlMarshallerCore.expectThisElement(element, "testSessionState");

        /* Pull out the TestPlan first, which should be the first element. We need this
         * to create the resulting TestSessionState.
         */
        final List<Element> childElements = XmlMarshallerCore.expectElementChildren(element);
        if (childElements.isEmpty() || !"testPlan".equals(childElements.get(0).getLocalName())) {
            throw new XmlUnmarshallingException("Expected first child of <testSessionState> to be <testPlan>");
        }
        final TestPlan testPlan = TestPlanXmlMarshaller.unmarshal(childElements.get(0));

        /* Create TestSessionState from TestPlan */
        final TestSessionState result = new TestSessionState(testPlan);

        /* Extract state attributes */
        result.setEntered(XmlMarshallerCore.parseOptionalBooleanAttribute(element, "entered", true)); /* FIXME: Remove legacy true */
        result.setEnded(XmlMarshallerCore.parseOptionalBooleanAttribute(element, "ended", true)); /* FIXME: Remove legacy false */
        result.setExited(XmlMarshallerCore.parseOptionalBooleanAttribute(element, "exited", false)); /* FIXME: Remove legacy false */
        result.setCurrentTestPartKey(TestPlanXmlMarshaller.parseOptionalTestPlanNodeKeyAttribute(element, "currentTestPartKey"));
        result.setCurrentItemKey(TestPlanXmlMarshaller.parseOptionalTestPlanNodeKeyAttribute(element, "currentItemKey"));
        final String durationString = XmlMarshallerCore.requireAttribute(element, "duration");
        try {
            result.setDurationValue(new FloatValue(durationString));
        }
        catch (final QtiParseException e) {
            throw new XmlUnmarshallingException("Could not parse duration " + durationString, e);
        }

        /* Handle rest of children */
        for (int i=1; i<childElements.size(); i++) {
            final Element childElement = childElements.get(i);
            final String childElementName = childElement.getLocalName();
            if ("outcomeVariable".equals(childElementName)) {
                final Identifier identifier = XmlMarshallerCore.parseIdentifierAttribute(childElement, "identifier");
                final Value value = XmlMarshallerCore.parseValue(childElement);
                result.setOutcomeValue(identifier, value);
            }
            else if ("testPart".equals(childElementName)) {
                final List<Element> testPartElements = XmlMarshallerCore.expectElementChildren(childElement);
                if (testPartElements.size()!=1) {
                    throw new XmlUnmarshallingException("Expected exactly one child of <testPart>");
                }
                final TestPlanNodeKey key = TestPlanXmlMarshaller.requireTestPlanNodeKeyAttribute(childElement, "key");
                final TestPartSessionState testPartSessionState = TestPartSessionStateXmlMarshaller.unmarshal(testPartElements.get(0));
                result.getTestPartSessionStates().put(key, testPartSessionState);
            }
            else if ("item".equals(childElementName)) {
                final List<Element> itemElements = XmlMarshallerCore.expectElementChildren(childElement);
                if (itemElements.size()!=1) {
                    throw new XmlUnmarshallingException("Expected exactly one child of <item>");
                }
                final TestPlanNodeKey key = TestPlanXmlMarshaller.requireTestPlanNodeKeyAttribute(childElement, "key");
                final ItemSessionState itemSessionState = ItemSessionStateXmlMarshaller.unmarshal(itemElements.get(0));
                result.getItemSessionStates().put(key, itemSessionState);
            }
            else {
                throw new XmlUnmarshallingException("Unexpected element with localName " + childElementName);
            }
        }
        return result;
    }
}
