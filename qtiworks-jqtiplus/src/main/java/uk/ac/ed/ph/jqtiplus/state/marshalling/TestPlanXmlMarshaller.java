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

import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;
import uk.ac.ed.ph.jqtiplus.state.EffectiveItemSessionControl;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;

import java.io.StringReader;
import java.net.URI;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Marshals a {@link TestPlan} to/from XML
 *
 * @author David McKain
 */
public final class TestPlanXmlMarshaller {

    public static Document marshal(final TestPlan testPlan) {
        final DocumentBuilder documentBuilder = XmlMarshallerCore.createNsAwareDocumentBuilder();
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
        final EffectiveItemSessionControl effectiveItemSessionControl = testPlanNode.getEffectiveItemSessionControl();
        if (effectiveItemSessionControl!=null) {
            element.setAttribute("maxAttempts", Integer.toString(effectiveItemSessionControl.getMaxAttempts()));
            element.setAttribute("showFeedback", StringUtilities.toTrueFalse(effectiveItemSessionControl.isShowFeedback()));
            element.setAttribute("allowReview", StringUtilities.toTrueFalse(effectiveItemSessionControl.isAllowReview()));
            element.setAttribute("showSolution", StringUtilities.toTrueFalse(effectiveItemSessionControl.isShowSolution()));
            element.setAttribute("allowComment", StringUtilities.toTrueFalse(effectiveItemSessionControl.isAllowComment()));
            element.setAttribute("allowSkipping", StringUtilities.toTrueFalse(effectiveItemSessionControl.isAllowSkipping()));
            element.setAttribute("validateResponses", StringUtilities.toTrueFalse(effectiveItemSessionControl.isValidateResponses()));
        }
        final String sectionPartTitle = testPlanNode.getSectionPartTitle();
        if (sectionPartTitle!=null) {
            element.setAttribute("sectionPartTitle", sectionPartTitle);
        }
        final URI itemSystemId = testPlanNode.getItemSystemId();
        if (itemSystemId!=null) {
            element.setAttribute("itemSystemId", itemSystemId.toString());
        }

        /* Descend into children */
        for (final TestPlanNode childNode : testPlanNode.getChildren()) {
            appendTestPlanNode(element, childNode);
        }
    }

    //----------------------------------------------

    public static TestPlan unmarshal(final String xmlString) {
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
                throw new XmlUnmarshallingException("Unexpected element " + elementName);
            }
            final TestNodeType type = requireTestNodeTypeAttribute(childElement, "type");
            final TestPlanNodeKey key = requireTestPlanNodeKeyAttribute(childElement, "key");
            final String sectionPartTitle = XmlMarshallerCore.parseOptionalStringAttribute(childElement, "sectionPartTitle");
            final URI itemSystemId = XmlMarshallerCore.parseOptionalUriAttribute(childElement, "itemSystemId");

            /* Parse EffectiveItemSessionControl attributes */
            final int maxAttempts = XmlMarshallerCore.parseOptionalIntegerAttribute(childElement, "maxAttempts", ItemSessionControl.MAX_ATTEMPTS_DEFAULT_VALUE);
            final boolean showFeedback = XmlMarshallerCore.parseOptionalBooleanAttribute(childElement, "showFeedback", ItemSessionControl.SHOW_FEEDBACK_DEFAULT_VALUE);
            final boolean allowReview = XmlMarshallerCore.parseOptionalBooleanAttribute(childElement, "allowReview", ItemSessionControl.ALLOW_REVIEW_DEFAULT_VALUE);
            final boolean showSolution = XmlMarshallerCore.parseOptionalBooleanAttribute(childElement, "showSolution", ItemSessionControl.SHOW_SOLUTION_DEFAULT_VALUE);
            final boolean allowComment = XmlMarshallerCore.parseOptionalBooleanAttribute(childElement, "allowComment", ItemSessionControl.ALLOW_COMMENT_DEFAULT_VALUE);
            final boolean allowSkipping = XmlMarshallerCore.parseOptionalBooleanAttribute(childElement, "allowSkipping", ItemSessionControl.ALLOW_SKIPPING_DEFAULT_VALUE);
            final boolean validateResponses = XmlMarshallerCore.parseOptionalBooleanAttribute(childElement, "validateResponses", ItemSessionControl.VALIDATE_RESPONSES_DEFAULT_VALUE);
            final EffectiveItemSessionControl effectiveItemSessionControl = new EffectiveItemSessionControl(maxAttempts, showFeedback, allowReview, showSolution, allowComment, allowSkipping, validateResponses);

            final TestPlanNode childTestPlanNode = new TestPlanNode(type, key, effectiveItemSessionControl, sectionPartTitle, itemSystemId);
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
            throw new XmlUnmarshallingException("Bad " + TestNodeType.class.getSimpleName()
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
            throw new XmlUnmarshallingException("Bad " + TestPlanNodeKey.class.getSimpleName()
                    + " value '" + stringValue
                    + "' in attribute " + localName);
        }
    }
}
