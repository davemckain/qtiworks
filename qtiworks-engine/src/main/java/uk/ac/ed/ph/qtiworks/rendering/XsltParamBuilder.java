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
package uk.ac.ed.ph.qtiworks.rendering;

import uk.ac.ed.ph.qtiworks.rendering.XsltParamDocumentBuilder.SaxFirerCallback;
import uk.ac.ed.ph.qtiworks.utils.XmlUtilities;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.variable.RubricBlock;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.TestFeedback;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxFiringContext;
import uk.ac.ed.ph.jqtiplus.serialization.SaxEventFirer;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.FileResponseData;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.value.FileValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This little helper class converts various types of JQTIPlus Objects into DOM elements
 *
 * Usage: Not thread-safe
 *
 * @author  David McKain
 */
public final class XsltParamBuilder {

    /** Internal namespace used in QTIWorks Rendering XSLT that we'll use for certain custom elements/attrs */
    public static final String QTIWORKS_NAMESPACE = "http://www.ph.ed.ac.uk/qtiworks";

    /** Prefix to use for QTIWorks Rendering XSLT that we'll use for certain custom elements/attrs */
    public static final String QTIWORKS_NAMESPACE_PREFIX = "qw";

    private final JqtiExtensionManager jqtiExtensionManager;
    private final DocumentBuilder documentBuilder;

    public XsltParamBuilder(final JqtiExtensionManager jqtiExtensionManager) {
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
    }

    public List<Node> responseValuesToElements(final Map<Identifier, Value> responseValues) {
        return variableValuesToElements(responseValues, "response");
    }

    public List<Node> templateValuesToElements(final Map<Identifier, Value> templateValues) {
        return variableValuesToElements(templateValues, "template");
    }

    public List<Node> outcomeValuesToElements(final Map<Identifier, Value> outcomeValues) {
        return variableValuesToElements(outcomeValues, "outcome");
    }

    public List<Node> variableValuesToElements(final Map<Identifier, Value> variableValues, final String elementName) {
        final ArrayList<Node> result = new ArrayList<Node>();
        if (variableValues.isEmpty()) {
            return result;
        }
        final Document doc = documentBuilder.newDocument();
        for (final Entry<Identifier, Value> entry : variableValues.entrySet()) {
            final Element child = doc.createElementNS(QTIWORKS_NAMESPACE, elementName);
            child.setAttribute("identifier", entry.getKey().toString());
            appendValueToElement(entry.getValue(), child);
            result.add(child);
        }
        return result;
    }

    private void appendValueToElement(final Value value, final Element element) {
        final Document doc = element.getOwnerDocument();

        /* Add type information to container element. This won't be available for NullValues */
        if (value.getBaseType()!=null) {
            element.setAttribute("baseType", value.getBaseType().toQtiString());
        }
        if (value.getCardinality()!=null) {
            element.setAttribute("cardinality", value.getCardinality().toQtiString());
        }

        /* Then add child Elements containing actual value information */
        if (value instanceof NullValue) {
            /* (Add no children, which we use to indicate null value) */
        }
        if (value instanceof SingleValue) {
            final Element v = doc.createElementNS(QTIWORKS_NAMESPACE, "value");
            if (value instanceof FileValue) {
                final FileValue fileValue = (FileValue) value;
                v.setAttribute("contentType", fileValue.getContentType());
                v.setAttribute("fileName", fileValue.getFileName());
            }
            else {
                v.setTextContent(value.toQtiString());
            }
            element.appendChild(v);
        }
        else if (value instanceof OrderedValue) {
            final OrderedValue orderedValue = (OrderedValue) value;
            for (int i=0; i<orderedValue.size(); i++) {
                final Element v = doc.createElementNS(QTIWORKS_NAMESPACE, "value");
                v.setTextContent(((ListValue)value).get(i).toQtiString());
                element.appendChild(v);
            }
        }
        else if (value instanceof ListValue) {
            /* (Note there there is no explicit ordering for MultipleValues here) */
            final ListValue listValue = (ListValue) value;
            for (int i=0; i<listValue.size(); i++) {
                final Element v = doc.createElementNS(QTIWORKS_NAMESPACE, "value");
                v.setTextContent(listValue.get(i).toQtiString());
                element.appendChild(v);
            }
        }
        else if (value instanceof RecordValue) {
            /* Note that there is no explicit ordering here */
            final RecordValue recordValue = (RecordValue) value;
            for (final Identifier key : recordValue.keySet()) {
                final Element v = doc.createElementNS(QTIWORKS_NAMESPACE, "value");
                v.setAttribute("identifier", key.toString());
                appendValueToElement(recordValue.get(key), v);
                element.appendChild(v);
            }
        }
    }

    public List<Node> responseInputsToElements(final Map<Identifier, ResponseData> responseInputs) {
        final ArrayList<Node> result = new ArrayList<Node>();
        if (responseInputs==null || responseInputs.isEmpty()) {
            return result;
        }
        final Document doc = documentBuilder.newDocument();
        for (final Entry<Identifier, ResponseData> entry : responseInputs.entrySet()) {
            final Identifier interactionIdentifier = entry.getKey();
            final ResponseData responseData = entry.getValue();
            final Element responseInputElement = doc.createElementNS(QTIWORKS_NAMESPACE, "responseInput");
            responseInputElement.setAttribute("identifier", interactionIdentifier.toString());
            switch (responseData.getType()) {
                case STRING:
                    final List<String> stringResponses = ((StringResponseData) responseData).getResponseData();
                    for (final String string : stringResponses) {
                        final Element valueChild = doc.createElementNS(QTIWORKS_NAMESPACE, "value");
                        valueChild.appendChild(doc.createTextNode(string));
                        responseInputElement.appendChild(valueChild);
                    }
                    break;

                case FILE:
                    final FileResponseData fileResponseData = (FileResponseData) responseData;
                    final Element fileResponseElement = doc.createElementNS(QTIWORKS_NAMESPACE, "file");
                    fileResponseElement.setAttribute("contentType", fileResponseData.getContentType());
                    fileResponseElement.setAttribute("fileName", fileResponseData.getFileName());
                    responseInputElement.appendChild(fileResponseElement);
                    break;

                default:
                    throw new QtiLogicException("Unexpected swtich case " + responseData.getType());
            }
            result.add(responseInputElement);
        }
        return result;
    }

    public List<Node> choiceOrdersToElements(final ItemSessionState itemSessionState) {
        final List<Node> result = new ArrayList<Node>();
        final Document doc = documentBuilder.newDocument();

        for (final Entry<Identifier, List<Identifier>> entry : itemSessionState.getShuffledInteractionChoiceOrders().entrySet()) {
            final Identifier responseIdentifier = entry.getKey();
            final List<Identifier> shuffledInteractionChoiceOrder = entry.getValue();
            final Element container = doc.createElementNS(QTIWORKS_NAMESPACE, "shuffledChoiceOrder");
            container.setAttribute("responseIdentifier", responseIdentifier.toString());
            for (final Identifier choiceIdentifier : shuffledInteractionChoiceOrder) {
                final Element choice = doc.createElementNS(QTIWORKS_NAMESPACE, "choice");
                choice.setAttribute("identifier", choiceIdentifier.toString());
                container.appendChild(choice);
            }
            result.add(container);
        }
        return result;
    }

    public NodeList rubricsToNodeList(final List<List<RubricBlock>> values) {
        return new XsltParamDocumentBuilder(jqtiExtensionManager,new SaxFirerCallback() {

            @Override
            public List<? extends XmlNode> getQtiNodes() {
                final List<RubricBlock> allBlocks = new ArrayList<RubricBlock>();
                for (final List<RubricBlock> section : values) {
                    allBlocks.addAll(section);
                }
                return allBlocks;
            }

            @Override
            public void fireSaxEvents(final SaxEventFirer saxEventFirer, final QtiSaxFiringContext qtiSaxFiringContext) throws SAXException {
                for (final List<RubricBlock> section : values) {
                    saxEventFirer.fireStartElement(section, "section", QTIWORKS_NAMESPACE, new AttributesImpl());
                    for (final RubricBlock block : section) {
                        block.fireSaxEvents(qtiSaxFiringContext);
                    }
                    saxEventFirer.fireEndElement(section, "section", QTIWORKS_NAMESPACE);
                }
            }
        }).buildDocument().getDocumentElement().getChildNodes();
    }

    public NodeList testFeedbacksToNodeList(final List<TestFeedback> values) {
        return buildNodeList(values);
    }

    public NodeList outcomeDeclarationsToNodeList(final List<OutcomeDeclaration> values) {
        return buildNodeList(values);
    }

    private NodeList buildNodeList(final List<? extends XmlNode> values) {
        return new XsltParamDocumentBuilder(jqtiExtensionManager, new SaxFirerCallback() {
            @Override
            public List<? extends XmlNode> getQtiNodes() {
                return values;
            }

            @Override
            public void fireSaxEvents(final SaxEventFirer saxEventFirer, final QtiSaxFiringContext qtiSaxFiringContext) throws SAXException {
                for (final XmlNode node : values) {
                    node.fireSaxEvents(qtiSaxFiringContext);
                }
            }
        }).buildDocument().getDocumentElement().getChildNodes();
    }
}
