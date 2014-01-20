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

import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.state.AbstractPartSessionState;
import uk.ac.ed.ph.jqtiplus.state.ControlObjectSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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

/**
 * Core for the (horribly cheap and nasty) XML marshalling we do for serializing JQTI+ state
 * Objects to/from XML. This is used in the QTIWorks Engine for storage and passing to the
 * rendering layers, but might be useful in other applications too.
 *
 * @author David McKain
 */
public final class XmlMarshallerCore {

    /** Namespace used for custom QTIWorks XML */
    public static final String QTIWORKS_NAMESPACE = "http://www.ph.ed.ac.uk/qtiworks";

    private static final String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ";

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

    static void maybeAddStringAttribute(final Element element, final String attributeName, final String value) {
        if (value!=null) {
            element.setAttribute(attributeName, value);
        }
    }

    static void maybeAddIdentifierListAttribute(final Element element, final String attributeName, final Collection<Identifier> values) {
        if (!values.isEmpty()) {
            element.setAttribute(attributeName, StringUtilities.join(values, " "));
        }
    }

    static void maybeAddDateAttribute(final Element element, final String attributeName, final Date date) {
        if (date!=null) {
            element.setAttribute(attributeName, new SimpleDateFormat(dateFormatString).format(date));
        }
    }

    static void addAbstractPartSessionStateAttributes(final Element element, final AbstractPartSessionState abstractPartSessionState) {
        addControlObjectSessionStateAttributes(element, abstractPartSessionState);
        element.setAttribute("preConditionFailed", StringUtilities.toTrueFalse(abstractPartSessionState.isPreConditionFailed()));
        element.setAttribute("jumpedByBranchRule", StringUtilities.toTrueFalse(abstractPartSessionState.isJumpedByBranchRule()));
        final String branchRuleTarget = abstractPartSessionState.getBranchRuleTarget();
        if (branchRuleTarget!=null) {
            element.setAttribute("branchRuleTarget", branchRuleTarget.toString());
        }
    }

    static void addControlObjectSessionStateAttributes(final Element element, final ControlObjectSessionState controlObjectState) {
        maybeAddDateAttribute(element, "entryTime", controlObjectState.getEntryTime());
        maybeAddDateAttribute(element, "endTime", controlObjectState.getEndTime());
        maybeAddDateAttribute(element, "exitTime", controlObjectState.getExitTime());
        maybeAddDateAttribute(element, "durationIntervalStartTime", controlObjectState.getDurationIntervalStartTime());
        element.setAttribute("durationAccumulated", Long.toString(controlObjectState.getDurationAccumulated()));
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
                    throw new QtiLogicException("Unexpected logic branch: " + cardinality);

            }
        }
    }

    static void appendSingleValue(final Element parent, final SingleValue value) {
        final Element singleValueElement = appendElement(parent, "value");
        if (value instanceof FileValue) {
            /* FIXME: Not sure how much we'll do with this */
            final FileValue fileValue = (FileValue) value;
            singleValueElement.setAttribute("absolutePath", fileValue.getFile().getAbsolutePath());
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
            throw new XmlUnmarshallingException("Expected element " + element.getLocalName()
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
                throw new XmlUnmarshallingException("Expected only text content of element " + element);
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
                /* Ignorable whitespace */
                continue;
            }
            if (childNode.getNodeType()!=Node.ELEMENT_NODE) {
                throw new XmlUnmarshallingException("Expected only element children of " + element);
            }
            final Element childElement = (Element) childNode;
            if (!QTIWORKS_NAMESPACE.equals(childElement.getNamespaceURI())) {
                throw new XmlUnmarshallingException("Expected Element " + childElement + " to have namepsace URI " + QTIWORKS_NAMESPACE);
            }
            result.add(childElement);
        }
        return result;
    }

    static String requireAttribute(final Element element, final String attrName) {
        if (!element.hasAttribute(attrName)) {
            throw new XmlUnmarshallingException("Attribute " + attrName + " of element " + element.getLocalName() + " is required");
        }
        return element.getAttribute(attrName);
    }

    static boolean parseOptionalBooleanAttribute(final Element element, final String attrName, final boolean defaultValue) {
        if (!element.hasAttribute(attrName)) {
            return defaultValue;
        }
        final String attrValue = element.getAttribute(attrName);
        try {
            return StringUtilities.fromTrueFalse(attrValue);
        }
        catch (final IllegalArgumentException e) {
            throw new XmlUnmarshallingException("Could not parse boolean attribute value " + attrValue + " for " + attrName);
        }
    }

    static int parseOptionalIntegerAttribute(final Element element, final String attrName, final int defaultValue) {
        if (!element.hasAttribute(attrName)) {
            return defaultValue;
        }
        final String attrValue = element.getAttribute(attrName);
        try {
            return Integer.parseInt(attrValue);
        }
        catch (final NumberFormatException e) {
            throw new XmlUnmarshallingException("Could not parse integer attribute value " + attrValue + " for " + attrName);
        }
    }

    static long parseOptionalLongAttribute(final Element element, final String attrName, final long defaultValue) {
        if (!element.hasAttribute(attrName)) {
            return defaultValue;
        }
        final String attrValue = element.getAttribute(attrName);
        try {
            return Long.parseLong(attrValue);
        }
        catch (final NumberFormatException e) {
            throw new XmlUnmarshallingException("Could not parse long attribute value " + attrValue + " for " + attrName);
        }
    }

    static String parseOptionalStringAttribute(final Element element, final String attrName) {
        return element.hasAttribute(attrName) ? element.getAttribute(attrName) : null;
    }

    static Date parseOptionalDateAttribute(final Element element, final String attrName) {
        if (element.hasAttribute(attrName)) {
            final String attrValue = element.getAttribute(attrName);
            if (!attrValue.isEmpty()) {
                try {
                    return new SimpleDateFormat(dateFormatString).parse(attrValue);
                }
                catch (final ParseException e) {
                    throw new XmlUnmarshallingException("Could not parse Date attribute", e);
                }
            }
        }
        return null;
    }

    static URI parseOptionalUriAttribute(final Element element, final String attrName) {
        try {
            return element.hasAttribute(attrName) ? new URI(element.getAttribute(attrName)) : null;
        }
        catch (final URISyntaxException e) {
            throw new XmlUnmarshallingException("Could not parse URI attribute", e);
        }
    }

    static TestPlanNodeKey parseOptionalTestPlanNodeKeyAttribute(final Element element, final String localName) {
        if (!element.hasAttribute(localName)) {
            return null;
        }
        return TestPlanXmlMarshaller.requireTestPlanNodeKeyAttribute(element, localName);
    }

    static void parseAbstractPartSessionStateAttributes(final AbstractPartSessionState target, final Element element) {
        parseControlObjectSessionStateAttributes(target, element);
        target.setPreConditionFailed(parseOptionalBooleanAttribute(element, "preConditionFailed", false));
        target.setJumpedByBranchRule(parseOptionalBooleanAttribute(element, "jumpedByBranchRule", false));
        target.setBranchRuleTarget(parseOptionalStringAttribute(element, "branchRuleTarget"));
    }

    static void parseControlObjectSessionStateAttributes(final ControlObjectSessionState target, final Element element) {
        target.setEntryTime(parseOptionalDateAttribute(element, "entryTime"));
        target.setEndTime(parseOptionalDateAttribute(element, "endTime"));
        target.setExitTime(parseOptionalDateAttribute(element, "exitTime"));
        target.setDurationIntervalStartTime(parseOptionalDateAttribute(element, "durationIntervalStartTime"));
        target.setDurationAccumulated(parseOptionalLongAttribute(element, "durationAccumulated", 0L));
    }

    static Value parseValue(final Element element) {
        if (!element.hasAttribute("cardinality")) {
            /* This would correspond to null, which would also have no children */
            if (element.hasChildNodes()) {
                throw new XmlUnmarshallingException("Value-containing element " + element
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
                throw new QtiLogicException("Unexpected logic branch " + cardinality);
        }
    }

    static SingleValue parseSingleValue(final Element element) {
        final BaseType baseType = parseBaseTypeAttribute(element);
        final SingleValue result;
        if (baseType==BaseType.FILE) {
            final List<Element> children = expectElementChildren(element);
            if (children.size()!=1) {
                throw new XmlUnmarshallingException("Expected precisely 1 <value> child of " + element + " but got " + children.size());
            }
            final Element fileValueElement = children.get(0);
            final File file = new File(requireAttribute(fileValueElement, "absolutePath"));
            final String contentType = requireAttribute(fileValueElement, "contentType");
            final String fileName = requireAttribute(fileValueElement, "fileName");
            result = new FileValue(file, contentType, fileName);
        }
        else {
            final List<String> valueStrings = parseValueChildren(element);
            if (valueStrings.size()!=1) {
                throw new XmlUnmarshallingException("Expected precisely 1 <value> child of " + element + " but got " + valueStrings.size());
            }
            final String singleValueString = valueStrings.get(0);
            try {
                result = baseType.parseSingleValue(singleValueString);
            }
            catch (final QtiParseException e) {
                throw new XmlUnmarshallingException("Could not parse single value " + singleValueString + " of baseType " + baseType, e);
            }
        }
        return result;
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
                throw new XmlUnmarshallingException("Could not parse single value " + itemValueString + " of baseType " + baseType, e);
            }
        }
        return itemValues;
    }

    static Value parseRecordValue(final Element element) {
        final List<Element> childElements = expectElementChildren(element);
        final Map<Identifier, SingleValue> recordBuilder = new HashMap<Identifier, SingleValue>();
        for (final Element childElement : childElements) {
            if (!"value".equals(childElement.getLocalName())) {
                throw new XmlUnmarshallingException("Expected only <value> children of " + element);
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
            throw new XmlUnmarshallingException("Bad cardinality attribute " + cardinalityString);
        }
    }

    static BaseType parseBaseTypeAttribute(final Element element) {
        final String baseTypeString = requireAttribute(element, "baseType");
        try {
            return BaseType.parseBaseType(baseTypeString);
        }
        catch (final IllegalArgumentException e) {
            throw new XmlUnmarshallingException("Bad baseType attribute " + baseTypeString);
        }
    }

    static List<String> parseValueChildren(final Element element) {
        final List<Element> childElements = expectElementChildren(element);
        final List<String> result = new ArrayList<String>();
        for (final Element childElement : childElements) {
            if (!"value".equals(childElement.getLocalName())) {
                throw new XmlUnmarshallingException("Expected only <value> children of " + element);
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
            throw new XmlUnmarshallingException("Value "
                    + identifierAttrValue + " of attribute "
                    + identifierAttrName + " is not a valid QTI Identifier");
        }
    }

    static final DocumentBuilder createNsAwareDocumentBuilder() {
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            return documentBuilderFactory.newDocumentBuilder();
        }
        catch (final ParserConfigurationException e) {
            throw new QtiLogicException("Could not create NS Aware DocumentBuilder. Check deployment/runtime ClassPath", e);
        }
    }
}
