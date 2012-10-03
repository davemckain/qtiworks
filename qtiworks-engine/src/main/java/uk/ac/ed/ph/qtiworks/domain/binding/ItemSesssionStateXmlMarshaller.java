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

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.utils.XmlUtilities;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.FileValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Marshals an {@link ItemSessionState} to/from XML
 *
 * @author David McKain
 */
public final class ItemSesssionStateXmlMarshaller {

    /** Internal namespace used in QTIWorks Rendering XSLT that we'll use for certain custom elements/attrs */
    public static final String QTIWORKS_NAMESPACE = "http://www.ph.ed.ac.uk/qtiworks";

    public static Document marshal(final ItemSessionState itemSessionState) {
        final DocumentBuilder documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
        final Document document = documentBuilder.newDocument();

        /* Create document element */
        final Element documentElement = document.createElementNS(QTIWORKS_NAMESPACE, "itemSessionState");
        documentElement.setAttribute("modelVersion", "1");
        documentElement.setAttribute("initialized", StringUtilities.toTrueFalse(itemSessionState.isInitialized()));
        document.appendChild(documentElement);

        /* Output shuffled choice orders */
        for (final Entry<Identifier, List<Identifier>> entry : itemSessionState.getShuffledInteractionChoiceOrders().entrySet()) {
            final Identifier responseIdentifier = entry.getKey();
            final List<Identifier> choiceIdentifiers = entry.getValue();
            final Element orderElement = document.createElementNS(QTIWORKS_NAMESPACE, "shuffledInteractionChoiceOrder");
            orderElement.setAttribute("responseIdentifier", responseIdentifier.toString());
            orderElement.setAttribute("choiceSequence", StringUtilities.join(choiceIdentifiers, " "));
            documentElement.appendChild(orderElement);
        }

        /* Do template variables */
        appendValues(documentElement, "templateVariable", itemSessionState.getTemplateValues());
        appendValues(documentElement, "responseVariable", itemSessionState.getResponseValues());
        appendValues(documentElement, "outcomeVariable", itemSessionState.getOutcomeValues());
        appendValues(documentElement, "overriddenTemplateDefault", itemSessionState.getOverriddenTemplateDefaultValues());
        appendValues(documentElement, "overriddenResponseDefault", itemSessionState.getOverriddenResponseDefaultValues());
        appendValues(documentElement, "overriddenOutcomeDefault", itemSessionState.getOverriddenOutcomeDefaultValues());
        appendValues(documentElement, "overriddenCorrectResponse", itemSessionState.getOverriddenCorrectResponseValues());

        return document;
    }

    private static void appendValues(final Element parentElement, final String elementName, final Map<Identifier, Value> valueMap) {
        for (final Entry<Identifier, Value> entry : valueMap.entrySet()) {
            final Identifier identifier = entry.getKey();
            final Value value = entry.getValue();

            final Element valueElement = parentElement.getOwnerDocument().createElementNS(QTIWORKS_NAMESPACE, elementName);
            valueElement.setAttribute("identifier", identifier.toString());
            appendValueToElement(valueElement, value);

            parentElement.appendChild(valueElement);
        }
    }

    private static void appendValueToElement(final Element element, final Value value) {
        if (value.isNull()) {
            /* Currently we'll indicate null by outputting no value */
        }
        else {
            final Document document = element.getOwnerDocument();
            final Cardinality cardinality = value.getCardinality();
            final BaseType baseType = value.getBaseType(); /* (NB: may be null) */
            element.setAttribute("cardinality", cardinality.toQtiString());
            if (baseType!=null) {
                element.setAttribute("baseType", baseType.toQtiString());
            }
            switch (cardinality) {
                case SINGLE:
                    appendSingleValue(element, (SingleValue) value);
                    break;

                case MULTIPLE:
                case ORDERED:
                    final ListValue listValue = (ListValue) value;
                    for (final SingleValue listItem : listValue) {
                        appendSingleValue(element, listItem);
                    }
                    break;

                case RECORD:
                    final RecordValue recordValue = (RecordValue) value;
                    for (final Entry<Identifier, SingleValue> entry : recordValue.entrySet()) {
                        final Identifier itemIdentifier = entry.getKey();
                        final SingleValue itemValue = entry.getValue();
                        final Element v = document.createElementNS(QTIWORKS_NAMESPACE, "value");
                        v.setAttribute("baseType", itemValue.getBaseType().toQtiString());
                        v.setAttribute("fieldIdentifier",itemIdentifier.toString());
                        appendSingleValue(v, itemValue);
                        element.appendChild(v);
                    }
                    break;

                default:
                    throw new QtiWorksLogicException("Unexpected logic branch: " + cardinality);

            }
        }
    }

    private static void appendSingleValue(final Element parent, final SingleValue value) {
        final Document document = parent.getOwnerDocument();
        final Element singleValueElement = document.createElementNS(QTIWORKS_NAMESPACE, "value");
        if (value instanceof FileValue) {
            /* FIXME: Not sure how much we'll do with this */
            final FileValue fileValue = (FileValue) value;
            singleValueElement.setAttribute("contentType", fileValue.getContentType());
            singleValueElement.setAttribute("fileName", fileValue.getFileName());
        }
        else {
            singleValueElement.setTextContent(value.toQtiString());
        }
        parent.appendChild(singleValueElement);
    }

    //----------------------------------------------

    public static ItemSessionState unmarshal(final String xmlString) {
        final DocumentBuilder documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
        Document document;
        try {
            document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (final Exception e) {
            throw new MarshallingException("XML parsing failed", e);
        }
        return unmarshal(document);
    }

    public static ItemSessionState unmarshal(final Document document) {
        final ItemSessionState result = new ItemSessionState();
        final Element documentElement = document.getDocumentElement();
        if (!(QTIWORKS_NAMESPACE.equals(documentElement.getNamespaceURI())
                && "itemSessionState".equals(documentElement.getLocalName()))) {
            throw new MarshallingException("Expected <itemSessionState> in " + QTIWORKS_NAMESPACE + " namespace");
        }
        if (!"1".equals(documentElement.getAttribute("modelVersion"))) {
            throw new MarshallingException("Expected modelVersion to be 1");
        }
        try {
            result.setInitialized(StringUtilities.fromTrueFalse(documentElement.getAttribute("initialized")));
        }
        catch (final IllegalArgumentException e) {
            throw new MarshallingException("Expected initialized attribute to be 'true' or 'false'");
        }

        final List<Element> childElements = expectElementChildren(documentElement);
        for (final Element childElement : childElements) {
            final String elementName = childElement.getLocalName();
            if (elementName.equals("shuffledInteractionChoiceOrder")) {
                final Identifier responseIdentifier = parseIdentifierAttribute(childElement, "responseIdentifier");
                final List<Identifier> choiceIdentifiers = parseIdentifierAttributeList(childElement, "choiceSequence");
                result.setShuffledInteractionChoiceOrder(responseIdentifier, choiceIdentifiers);
            }
            else if (elementName.equals("templateVariable")) {
                final Identifier identifier = parseIdentifierAttribute(childElement, "identifier");
                final Value value = parseValue(childElement);
                result.setTemplateValue(identifier, value);
            }
            else if (elementName.equals("responseVariable")) {
                final Identifier identifier = parseIdentifierAttribute(childElement, "identifier");
                final Value value = parseValue(childElement);
                result.setResponseValue(identifier, value);
            }
            else if (elementName.equals("outcomeVariable")) {
                final Identifier identifier = parseIdentifierAttribute(childElement, "identifier");
                final Value value = parseValue(childElement);
                result.setOutcomeValue(identifier, value);
            }
            else if (elementName.equals("overriddenTemplateDefault")) {
                final Identifier identifier = parseIdentifierAttribute(childElement, "identifier");
                final Value value = parseValue(childElement);
                result.setOverriddenTemplateDefaultValue(identifier, value);
            }
            else if (elementName.equals("overriddenResponseDefault")) {
                final Identifier identifier = parseIdentifierAttribute(childElement, "identifier");
                final Value value = parseValue(childElement);
                result.setOverriddenResponseDefaultValue(identifier, value);
            }
            else if (elementName.equals("overriddenOutcomeDefault")) {
                final Identifier identifier = parseIdentifierAttribute(childElement, "identifier");
                final Value value = parseValue(childElement);
                result.setOverriddenOutcomeDefaultValue(identifier, value);
            }
            else if (elementName.equals("overriddenCorrectResponse")) {
                final Identifier identifier = parseIdentifierAttribute(childElement, "identifier");
                final Value value = parseValue(childElement);
                result.setOverriddenCorrectResponseValue(identifier, value);
            }
            else {
                throw new MarshallingException("Unexpected element " + elementName);
            }
        }

        return result;
    }

    private static Identifier parseIdentifierAttribute(final Element element, final String identifierAttrName) {
        final String identifierAttrValue = requireAttribute(element, identifierAttrName);
        try {
            return Identifier.parseString(identifierAttrValue);
        }
        catch (final QtiParseException e) {
            throw new MarshallingException("Value "
                    + identifierAttrValue + " of attribute "
                    + identifierAttrName + " is not a valid QTI Identifier");
        }
    }

    private static List<Identifier> parseIdentifierAttributeList(final Element element, final String identifierAttrListName) {
        final String identifierListAttrValue = requireAttribute(element, identifierAttrListName);
        final String[] identifierArray = identifierListAttrValue.split("\\s+");
        final List<Identifier> result = new ArrayList<Identifier>(identifierArray.length);
        for (final String identifierString : identifierArray) {
            try {
                result.add(Identifier.parseString(identifierString));
            }
            catch (final QtiParseException e) {
                throw new MarshallingException("Value "
                        + identifierString + " in list attribute "
                        + identifierAttrListName + " is not a valid QTI Identifier");
            }
        }
        return result;
    }

    private static Value parseValue(final Element element) {
        if (!element.hasAttribute("cardinality")) {
            /* This would correspond to null, which would also have no children */
            if (element.hasChildNodes()) {
                throw new MarshallingException("Value-containing element " + element
                        + " has no cardinality attribute but has child nodes");
            }
            return NullValue.INSTANCE;
        }
        final Cardinality cardinality = parseCardinalityAttribute(element);
        switch (cardinality) {
            case SINGLE:
                return parseSingleValue(element);

            case MULTIPLE:
                return MultipleValue.createMultipleValue(parseListValues(element));

            case ORDERED:
                return OrderedValue.createOrderedValue(parseListValues(element));

            case RECORD:
                return parseRecordValue(element);

            default:
                throw new QtiWorksLogicException("Unexpected logic branch " + cardinality);
        }
    }

    private static SingleValue parseSingleValue(final Element element) {
        final BaseType baseType = parseBaseTypeAttribute(element);
        final List<String> valueStrings = parseValueChildren(element);
        if (valueStrings.size()!=1) {
            throw new MarshallingException("Expected precisely 1 <value> child of " + element + " but got " + valueStrings.size());
        }
        final String singleValueString = valueStrings.get(0);
        try {
            return baseType.parseSingleValue(singleValueString);
        }
        catch (final QtiParseException e) {
            throw new MarshallingException("Could not parse single value " + singleValueString + " of baseType " + baseType, e);
        }
    }

    private static List<SingleValue> parseListValues(final Element element) {
        final BaseType baseType = parseBaseTypeAttribute(element);
        final List<String> itemValueStrings = parseValueChildren(element);
        final List<SingleValue> itemValues = new ArrayList<SingleValue>();
        for (final String itemValueString : itemValueStrings) {
            try {
                itemValues.add(baseType.parseSingleValue(itemValueString));
            }
            catch (final QtiParseException e) {
                throw new MarshallingException("Could not parse single value " + itemValueString + " of baseType " + baseType, e);
            }
        }
        return itemValues;
    }

    private static Value parseRecordValue(final Element element) {
        final List<Element> childElements = expectElementChildren(element);
        final Map<Identifier, SingleValue> recordBuilder = new HashMap<Identifier, SingleValue>();
        for (final Element childElement : childElements) {
            if (!"value".equals(childElement.getLocalName())) {
                throw new MarshallingException("Expected only <value> children of " + element);
            }
            final Identifier itemIdentifier = parseIdentifierAttribute(childElement, "fieldIdentifier");
            final SingleValue itemValue = parseSingleValue(childElement);
            recordBuilder.put(itemIdentifier, itemValue);
        }
        return RecordValue.createRecordValue(recordBuilder);
    }

    private static Cardinality parseCardinalityAttribute(final Element element) {
        final String cardinalityString = requireAttribute(element, "cardinality");
        try {
            return Cardinality.parseCardinality(cardinalityString);
        }
        catch (final IllegalArgumentException e) {
            throw new MarshallingException("Bad cardinality attribute " + cardinalityString);
        }
    }

    private static BaseType parseBaseTypeAttribute(final Element element) {
        final String baseTypeString = requireAttribute(element, "baseType");
        try {
            return BaseType.parseBaseType(baseTypeString);
        }
        catch (final IllegalArgumentException e) {
            throw new MarshallingException("Bad baseType attribute " + baseTypeString);
        }
    }

    private static String requireAttribute(final Element element, final String attrName) {
        if (!element.hasAttribute(attrName)) {
            throw new MarshallingException("Attribute " + attrName + " of element " + element.getLocalName() + " is required");
        }
        return element.getAttribute(attrName);
    }

    private static List<String> parseValueChildren(final Element element) {
        final List<Element> childElements = expectElementChildren(element);
        final List<String> result = new ArrayList<String>();
        for (final Element childElement : childElements) {
            if (!"value".equals(childElement.getLocalName())) {
                throw new MarshallingException("Expected only <value> children of " + element);
            }
            result.add(expectTextContent(childElement));
        }
        return result;
    }

    private static String expectTextContent(final Element element) {
        final NodeList childNodes = element.getChildNodes();
        final StringBuilder resultBuilder = new StringBuilder();
        for (int i=0, size=childNodes.getLength(); i<size; i++) {
            final Node childNode = childNodes.item(i);
            if (childNode.getNodeType()==Node.TEXT_NODE) {
                resultBuilder.append(childNode.getNodeValue());
            }
            else {
                throw new MarshallingException("Expected only text content of element " + element);
            }
        }
        return resultBuilder.toString();
    }

    private static List<Element> expectElementChildren(final Element element) {
        final NodeList childNodes = element.getChildNodes();
        final List<Element> result = new ArrayList<Element>(childNodes.getLength());
        for (int i=0, size=childNodes.getLength(); i<size; i++) {
            final Node childNode = childNodes.item(i);
            if (childNode.getNodeType()==Node.TEXT_NODE && childNode.getNodeValue().trim().isEmpty()) {
                continue;
            }
            if (childNode.getNodeType()!=Node.ELEMENT_NODE) {
                throw new MarshallingException("Expected only element children of " + element);
            }
            final Element childElement = (Element) childNode;
            if (!QTIWORKS_NAMESPACE.equals(childElement.getNamespaceURI())) {
                throw new MarshallingException("Expected Element " + childElement + " to have namepsace URI " + QTIWORKS_NAMESPACE);
            }
            result.add(childElement);
        }
        return result;
    }
}
