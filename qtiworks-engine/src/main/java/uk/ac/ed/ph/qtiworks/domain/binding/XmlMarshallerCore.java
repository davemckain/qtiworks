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

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Core for the horribly cheap and nasty XML marshalling we do within the QTIWorks engine
 * to transport certain state Objects to/from XML for storage and passing to the rendering
 * layers.
 *
 * @author David McKain
 */
public final class XmlMarshallerCore {

    /** Namespace used for custom QTIWorks XML */
    public static final String QTIWORKS_NAMESPACE = "http://www.ph.ed.ac.uk/qtiworks";

    //----------------------------------------------
    // Marshalling to XML

    static Document getOwnerDocument(final Node documentOrElement) {
        return documentOrElement instanceof Document ? (Document) documentOrElement : documentOrElement.getOwnerDocument();
    }

    static Element createElement(final Node parent, final String localName) {
        return getOwnerDocument(parent).createElementNS(QTIWORKS_NAMESPACE, localName);
    }

    static Element appendElement(final Node parent, final String localName) {
        final Element element = getOwnerDocument(parent).createElementNS(QTIWORKS_NAMESPACE, localName);
        parent.appendChild(element);
        return element;
    }

    static void maybeAppendTextElement(final Element parentElement, final String elementName, final String content) {
        if (content!=null) {
            final Element element = appendElement(parentElement, elementName);
            element.appendChild(parentElement.getOwnerDocument().createTextNode(content));
        }
    }

    static void maybeAddIdentifierListAttribute(final Element element, final String attributeName, final Collection<Identifier> values) {
        if (!values.isEmpty()) {
            element.setAttribute(attributeName, StringUtilities.join(values, " "));
        }
    }

    static void appendValues(final Element parentElement, final String elementName, final Map<Identifier, Value> valueMap) {
        for (final Entry<Identifier, Value> entry : valueMap.entrySet()) {
            final Identifier identifier = entry.getKey();
            final Value value = entry.getValue();

            final Element valueElement = appendElement(parentElement, elementName);
            valueElement.setAttribute("identifier", identifier.toString());
            appendValueToElement(valueElement, value);
        }
    }

    static void appendValueToElement(final Element element, final Value value) {
        if (value.isNull()) {
            /* Currently we'll indicate null by outputting no value */
        }
        else {
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
                        final Element v = appendElement(element, "value");
                        v.setAttribute("baseType", itemValue.getBaseType().toQtiString());
                        v.setAttribute("fieldIdentifier",itemIdentifier.toString());
                        appendSingleValue(v, itemValue);
                    }
                    break;

                default:
                    throw new QtiWorksLogicException("Unexpected logic branch: " + cardinality);

            }
        }
    }

    static void appendSingleValue(final Element parent, final SingleValue value) {
        final Element singleValueElement = appendElement(parent, "value");
        if (value instanceof FileValue) {
            /* FIXME: Not sure how much we'll do with this */
            final FileValue fileValue = (FileValue) value;
            singleValueElement.setAttribute("contentType", fileValue.getContentType());
            singleValueElement.setAttribute("fileName", fileValue.getFileName());
        }
        else {
            singleValueElement.setTextContent(value.toQtiString());
        }
    }

    //----------------------------------------------
    // Unmarshalling from XML

    static void expectThisElement(final Element element, final String localName) {
        if (!(QTIWORKS_NAMESPACE.equals(element.getNamespaceURI())
                && localName.equals(element.getLocalName()))) {
            throw new MarshallingException("Expected element " + element.getLocalName()
                    + " in namespace " + element.getNamespaceURI()
                    + " to be " + localName + " in " + QTIWORKS_NAMESPACE);
        }
    }

    static String expectTextContent(final Element element) {
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

    static List<Element> expectElementChildren(final Element element) {
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

    static String requireAttribute(final Element element, final String attrName) {
        if (!element.hasAttribute(attrName)) {
            throw new MarshallingException("Attribute " + attrName + " of element " + element.getLocalName() + " is required");
        }
        return element.getAttribute(attrName);
    }

    static boolean parseOptionalBooleanAttribute(final Element element, final String attrName, final boolean defaultValue) {
        final String attrValue = element.getAttribute(attrName);
        return attrValue!=null ? StringUtilities.fromTrueFalse(attrValue) : defaultValue;
    }

    static Value parseValue(final Element element) {
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

    static SingleValue parseSingleValue(final Element element) {
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

    static List<SingleValue> parseListValues(final Element element) {
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

    static Value parseRecordValue(final Element element) {
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

    static Cardinality parseCardinalityAttribute(final Element element) {
        final String cardinalityString = requireAttribute(element, "cardinality");
        try {
            return Cardinality.parseCardinality(cardinalityString);
        }
        catch (final IllegalArgumentException e) {
            throw new MarshallingException("Bad cardinality attribute " + cardinalityString);
        }
    }

    static BaseType parseBaseTypeAttribute(final Element element) {
        final String baseTypeString = requireAttribute(element, "baseType");
        try {
            return BaseType.parseBaseType(baseTypeString);
        }
        catch (final IllegalArgumentException e) {
            throw new MarshallingException("Bad baseType attribute " + baseTypeString);
        }
    }

    static List<String> parseValueChildren(final Element element) {
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

    static Identifier parseIdentifierAttribute(final Element element, final String identifierAttrName) {
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
}
