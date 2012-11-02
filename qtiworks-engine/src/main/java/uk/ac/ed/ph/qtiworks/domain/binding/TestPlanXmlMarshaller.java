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
package uk.ac.ed.ph.qtiworks.domain.binding;

import uk.ac.ed.ph.qtiworks.utils.XmlUtilities;

import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public final class TestPlanXmlMarshaller {

    public static Document marshal(final TestPlan testPlan) {
        final DocumentBuilder documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
        final Document document = documentBuilder.newDocument();
        appendTestPlan(document, testPlan);
        return document;
    }

    public static void appendTestPlan(final Node documentOrElement, final TestPlan testPlan) {
        final Element element = XmlMarshallerCore.appendElement(documentOrElement, "testPlan");

        final TestPlanNode rootNode = testPlan.getTestPlanRootNode();
        for (final TestPlanNode testPlanNode : rootNode.getChildren()) {
            appendTestPlanNode(element, testPlanNode);
        }
    }

    static void appendTestPlanNode(final Element parent, final TestPlanNode testPlanNode) {
        final Element element = XmlMarshallerCore.appendElement(parent, "node");
        element.setAttribute("type", testPlanNode.getTestNodeType().toString());
        element.setAttribute("key", testPlanNode.getKey().toString());

        /* Descend into children */
        for (final TestPlanNode childNode : testPlanNode.getChildren()) {
            appendTestPlanNode(element, childNode);
        }
    }

    //----------------------------------------------

    public static TestPlan unmarshal(final String xmlString) {
        final DocumentBuilder documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
        Document document;
        try {
            document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (final Exception e) {
            throw new MarshallingException("XML parsing failed", e);
        }
        return unmarshal(document.getDocumentElement());
    }

    public static TestPlan unmarshal(final Element element) {
        XmlMarshallerCore.expectThisElement(element, "testPlan");

        final TestPlanNode rootNode = TestPlanNode.createRoot();
        expectTestPlanNodeChildren(element, rootNode);

        return new TestPlan(rootNode);
    }

    private static void expectTestPlanNodeChildren(final Element element, final TestPlanNode targetOwner) {
        final List<Element> childElements = XmlMarshallerCore.expectElementChildren(element);
        for (final Element childElement : childElements) {
            final String elementName = childElement.getLocalName();
            if (!elementName.equals("node")) {
                throw new MarshallingException("Unexpected element " + elementName);
            }
            final TestNodeType type = requireTestNodeTypeAttribute(childElement, "type");
            final TestPlanNodeKey key = requireTestPlanNodeKeyAttribute(childElement, "key");
            final TestPlanNode childTestPlanNode = new TestPlanNode(type, key);

            targetOwner.addChild(childTestPlanNode);
            expectTestPlanNodeChildren(childElement, childTestPlanNode);
        }
    }

    private static TestNodeType requireTestNodeTypeAttribute(final Element element, final String localName) {
        final String stringValue = XmlMarshallerCore.requireAttribute(element, localName);
        try {
            return TestNodeType.valueOf(stringValue);
        }
        catch (final IllegalArgumentException e) {
            throw new MarshallingException("Bad " + TestNodeType.class.getSimpleName()
                    + " value '" + stringValue
                    + "' in attribute " + localName);
        }
    }

    static TestPlanNodeKey requireTestPlanNodeKeyAttribute(final Element element, final String localName) {
        final String stringValue = XmlMarshallerCore.requireAttribute(element, localName);
        try {
            return TestPlanNodeKey.fromString(stringValue);
        }
        catch (final IllegalArgumentException e) {
            throw new MarshallingException("Bad " + TestPlanNodeKey.class.getSimpleName()
                    + " value '" + stringValue
                    + "' in attribute " + localName);
        }
    }

    static TestPlanNodeKey parseOptionalTestPlanNodeKeyAttribute(final Element element, final String localName) {
        if (!element.hasAttribute(localName)) {
            return null;
        }
        return requireTestPlanNodeKeyAttribute(element, localName);
    }
}
