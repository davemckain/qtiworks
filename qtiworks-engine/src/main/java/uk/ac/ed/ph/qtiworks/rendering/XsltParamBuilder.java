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

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.content.variable.RubricBlock;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Shuffleable;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.TestFeedback;
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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * This little helper class converts various types of JQTIPlus Objects into DOM elements.
 * 
 * @author  David McKain
 */
public final class XsltParamBuilder {
    
    /** Internal namespace used in JQTI-Rendering XSLT that we'll use for custom elements */
    public static final String QTIWORKS_NAMESPACE = "http://www.ph.ed.ac.uk/qtiworks";
    
    private final DocumentBuilder documentBuilder;
    
    public XsltParamBuilder() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new QtiRenderingException("Could not create DocumentBuilder for building XSLT parameters", e);
        }
    }
    
    public List<Node> responseValuesToElements(Map<Identifier, Value> responseValues) {
        return variableValuesToElements(responseValues, "response");
    }
    
    public List<Node> templateValuesToElements(Map<Identifier, Value> templateValues) {
        return variableValuesToElements(templateValues, "template");
    }
    
    public List<Node> outcomeValuesToElements(Map<Identifier, Value> outcomeValues) {
        return variableValuesToElements(outcomeValues, "outcome");
    }
    
    public List<Node> variableValuesToElements(Map<Identifier, Value> variableValues, String elementName) {
        ArrayList<Node> result = new ArrayList<Node>();
        if (variableValues.isEmpty()) {
            return result;
        }
        Document doc = documentBuilder.newDocument();
        for (Entry<Identifier, Value> entry : variableValues.entrySet()) {
            Element child = doc.createElementNS(QTIWORKS_NAMESPACE, elementName);
            child.setAttribute("identifier", entry.getKey().toString());
            appendValueToElement(entry.getValue(), child);
            result.add(child);
        }
        return result;
    }
    
    private void appendValueToElement(Value value, Element element) {
        Document doc = element.getOwnerDocument();
        
        /* Add type information to container element. This won't be available for NullValues */
        if (value.getBaseType()!=null) {
            element.setAttribute("baseType", value.getBaseType().toString());
        }
        if (value.getCardinality()!=null) {
            element.setAttribute("cardinality", value.getCardinality().toString());
        }

        /* Then add child Elements containing actual value information */
        if (value instanceof NullValue) {
            /* (Add no children, which we use to indicate null value) */
        }
        if (value instanceof SingleValue) {
            Element v = doc.createElementNS(QTIWORKS_NAMESPACE, "value");
            if (value instanceof FileValue) {
                FileValue fileValue = (FileValue) value;
                v.setAttribute("contentType", fileValue.getContentType());
                v.setAttribute("fileName", fileValue.getFileName());
            }
            else {
                v.setTextContent(value.toString());
            }
            element.appendChild(v);
        }
        else if (value instanceof OrderedValue) {
            OrderedValue orderedValue = (OrderedValue) value;
            for (int i=0; i<orderedValue.size(); i++) {
                Element v = doc.createElementNS(QTIWORKS_NAMESPACE, "value");
                v.setTextContent(((ListValue)value).get(i).toString());
                element.appendChild(v);                        
            }
        }
        else if (value instanceof ListValue) {
            /* (Note there there is no explicit ordering for MultipleValues here) */
            ListValue listValue = (ListValue) value;
            for (int i=0; i<listValue.size(); i++) {
                Element v = doc.createElementNS(QTIWORKS_NAMESPACE, "value");
                v.setTextContent(listValue.get(i).toString());
                element.appendChild(v);                        
            }
        }
        else if (value instanceof RecordValue) {
            /* Note that there is no explicit ordering here */
            RecordValue recordValue = (RecordValue) value;
            for (Identifier key : recordValue.keySet()) {
                Element v = doc.createElementNS(QTIWORKS_NAMESPACE, "value");
                v.setAttribute("identifier", key.toString());
                appendValueToElement(recordValue.get(key), v);
                element.appendChild(v);
            }
        }
    }
    
    public List<Node> responseInputsToElements(Map<String, ResponseData> responseInputs) {
        ArrayList<Node> result = new ArrayList<Node>();
        if (responseInputs==null || responseInputs.isEmpty()) {
            return result;
        }
        Document doc = documentBuilder.newDocument();
        for (Entry<String, ResponseData> entry : responseInputs.entrySet()) {
            String interactionIdentifier = entry.getKey();
            ResponseData responseData = entry.getValue();
            Element responseInputElement = doc.createElementNS(QTIWORKS_NAMESPACE, "responseInput");
            responseInputElement.setAttribute("identifier", interactionIdentifier);
            switch (responseData.getType()) {
                case STRING:
                    String [] stringResponses = ((StringResponseData) responseData).getResponseData();
                    for (String string : stringResponses) {
                        Element valueChild = doc.createElementNS(QTIWORKS_NAMESPACE, "value");
                        valueChild.appendChild(doc.createTextNode(string));
                        responseInputElement.appendChild(valueChild);
                    }
                    break;
                    
                case FILE:
                    FileResponseData fileResponseData = (FileResponseData) responseData;
                    Element fileResponseElement = doc.createElementNS(QTIWORKS_NAMESPACE, "file");
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
    
    public List<Node> choiceOrdersToElements(List<Interaction> interactions) {
        List<Node> result = new ArrayList<Node>();
        Document doc = documentBuilder.newDocument();
        for (Interaction interaction : interactions) {
            if (interaction instanceof Shuffleable) {
                Shuffleable shuffleable = (Shuffleable) interaction;
                if (shuffleable.getShuffle().booleanValue()) {
                    Element container = doc.createElementNS(QTIWORKS_NAMESPACE, "shuffledChoiceOrder");
                    container.setAttribute("responseIdentifier", interaction.getResponseIdentifier().toString());
                    for (String choiceIdentifier : shuffleable.getShuffledChoiceOrder()) {
                        Element choice = doc.createElementNS(QTIWORKS_NAMESPACE, "choice");
                        choice.setAttribute("identifier", choiceIdentifier);
                        container.appendChild(choice);
                    }
                    result.add(container);
                }
            }
        }
        return result;
    }
    
    public List<Node> convertRubric(List<List<RubricBlock>> values) {
        ArrayList<Node> result = new ArrayList<Node>();
        if (values == null || values.size() == 0) {
            return result;
        }
        
        /* FIXME: Is the following original logic still wanted? */
        int count = 0;
        for (List<RubricBlock> v : values) {
            count += v.size();
        }
        if (count==0) {
            return result;
        }
        
        Document doc = documentBuilder.newDocument();
        for (List<RubricBlock> section : values) {
            Element sectionContainer = doc.createElementNS(QTIWORKS_NAMESPACE, "section");
            
            Element tmp = doc.createElementNS(QTIConstants.QTI_21_NAMESPACE, "tmp");
            sectionContainer.appendChild(tmp);

            for (RubricBlock block : section) {
                /* (We have to clone these as we're building a new tree up) */
                Node blockNode = buildNode(block);
                Node adopted = doc.adoptNode(blockNode);
                sectionContainer.appendChild(adopted);
            }
            result.add(sectionContainer);
        }
        return result;
    }

    public NodeList convertFeedback(List<TestFeedback> values) {
        return buildNodeList(values);
    }
    
    public NodeList convertOutcomeDeclarations(List<OutcomeDeclaration> values) {
        return buildNodeList(values);
    }
    
    private Node buildNode(AbstractNode value) {
        if (value == null) {
            return null;
        }
        StringBuilder xmlBuilder = new StringBuilder("<wrapper xmlns='")
            .append(QTIConstants.QTI_21_NAMESPACE)
            .append("'>")
            .append(value.toXmlString())
            .append("</wrapper>");
        Document document;
        try {
            document = documentBuilder.parse(new InputSource(new StringReader(xmlBuilder.toString())));
        }
        catch (Exception e) {
            throw new QtiRenderingException("Unexpected Exception reparsing value");
        }
        return document.getDocumentElement().getChildNodes().item(0);
    }
    
    private NodeList buildNodeList(List<? extends AbstractNode> values) {
        if (values == null || values.size() == 0) {
            return null;
        }
        StringBuilder xmlBuilder = new StringBuilder("<wrapper xmlns='")
            .append(AssessmentItem.ATTR_DEFAULT_NAME_SPACE_NAME)
            .append("'>");
        for (AbstractNode node : values) {
            xmlBuilder.append(node.toXmlString());
        }
        xmlBuilder.append("</wrapper>");
        Document document;
        try {
            document = documentBuilder.parse(new InputSource(new StringReader(xmlBuilder.toString())));
        }
        catch (Exception e) {
            throw new QtiRenderingException("Unexpected Exception reparsing values subtree");
        }
        return document.getDocumentElement().getChildNodes();
    }
}
