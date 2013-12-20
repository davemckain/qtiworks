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
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.FileResponseData;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Marshals an {@link ItemSessionState} to/from XML
 *
 * @author David McKain
 */
public final class ItemSessionStateXmlMarshaller {

    public static Document marshal(final ItemSessionState itemSessionState) {
        final DocumentBuilder documentBuilder = XmlMarshallerCore.createNsAwareDocumentBuilder();
        final Document document = documentBuilder.newDocument();
        appendItemSessionState(document, itemSessionState);
        return document;
    }

    static void appendItemSessionState(final Node documentOrElement, final ItemSessionState itemSessionState) {
        final Element element = XmlMarshallerCore.appendElement(documentOrElement, "itemSessionState");
        XmlMarshallerCore.addAbstractPartSessionStateAttributes(element, itemSessionState);
        element.setAttribute("initialized", StringUtilities.toTrueFalse(itemSessionState.isInitialized()));
        element.setAttribute("responded", StringUtilities.toTrueFalse(itemSessionState.isResponded()));
        XmlMarshallerCore.maybeAddDateAttribute(element, "suspendTime", itemSessionState.getSuspendTime());
        final SessionStatus sessionStatus = itemSessionState.getSessionStatus();
        if (sessionStatus!=null) {
            element.setAttribute("sessionStatus", sessionStatus.toQtiString());
        }

        /* Append implicit variables */
        XmlMarshallerCore.maybeAddStringAttribute(element, "completionStatus", itemSessionState.getCompletionStatus());
        element.setAttribute("numAttempts", Integer.toString(itemSessionState.getNumAttempts()));

        /* Show any unbound and/or invalid responses (as attribute) */
        XmlMarshallerCore.maybeAddIdentifierListAttribute(element, "unboundResponseIdentifiers", itemSessionState.getUnboundResponseIdentifiers());
        XmlMarshallerCore.maybeAddIdentifierListAttribute(element, "invalidResponseIdentifiers", itemSessionState.getInvalidResponseIdentifiers());

        /* Output shuffled choice orders */
        for (final Entry<Identifier, List<Identifier>> entry : itemSessionState.getShuffledInteractionChoiceOrders().entrySet()) {
            final Identifier responseIdentifier = entry.getKey();
            final List<Identifier> choiceIdentifiers = entry.getValue();
            final Element orderElement = XmlMarshallerCore.appendElement(element, "shuffledInteractionChoiceOrder");
            orderElement.setAttribute("responseIdentifier", responseIdentifier.toString());
            orderElement.setAttribute("choiceSequence", StringUtilities.join(choiceIdentifiers, " "));
        }

        /* Output raw responses (as elements) */
        for (final Entry<Identifier, ResponseData> entry : itemSessionState.getRawResponseDataMap().entrySet()) {
            final Identifier identifier = entry.getKey();
            final ResponseData responseData = entry.getValue();
            final Element responseInputElement = XmlMarshallerCore.appendElement(element, "responseInput");
            responseInputElement.setAttribute("identifier", identifier.toString());
            appendResponseData(responseInputElement, responseData);
        }

        /* Output candidate comment */
        XmlMarshallerCore.maybeAppendTextElement(element, "candidateComment", itemSessionState.getCandidateComment());

        /* Do various values */
        XmlMarshallerCore.appendValues(element, "uncommittedResponseValue", itemSessionState.getUncommittedResponseValues());
        XmlMarshallerCore.appendValues(element, "templateVariable", itemSessionState.getTemplateValues());
        XmlMarshallerCore.appendValues(element, "responseVariable", itemSessionState.getResponseValues());
        XmlMarshallerCore.appendValues(element, "outcomeVariable", itemSessionState.getOutcomeValues());
        XmlMarshallerCore.appendValues(element, "overriddenTemplateDefault", itemSessionState.getOverriddenTemplateDefaultValues());
        XmlMarshallerCore.appendValues(element, "overriddenResponseDefault", itemSessionState.getOverriddenResponseDefaultValues());
        XmlMarshallerCore.appendValues(element, "overriddenOutcomeDefault", itemSessionState.getOverriddenOutcomeDefaultValues());
        XmlMarshallerCore.appendValues(element, "overriddenCorrectResponse", itemSessionState.getOverriddenCorrectResponseValues());
    }

    private static void appendResponseData(final Element element, final ResponseData responseData) {
        switch (responseData.getType()) {
            case STRING:
                final StringResponseData stringResponseData = (StringResponseData) responseData;
                for (final String responseDatum : stringResponseData.getResponseData()) {
                    XmlMarshallerCore.maybeAppendTextElement(element, "string", responseDatum);
                }
                break;

            case FILE:
                final FileResponseData fileResponseData = (FileResponseData) responseData;
                final Element fileElement = XmlMarshallerCore.appendElement(element, "file");
                fileElement.setAttribute("contentType", fileResponseData.getContentType());
                fileElement.setAttribute("fileName", fileResponseData.getFileName());
                fileElement.setAttribute("absoluteFilePath", fileResponseData.getFile().getAbsolutePath());
                break;

            default:
                throw new QtiLogicException("Unexpected switch case " + responseData.getType());
        }
    }

    //----------------------------------------------

    public static ItemSessionState unmarshal(final String xmlString) {
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

    public static ItemSessionState unmarshal(final Element element) {
        XmlMarshallerCore.expectThisElement(element, "itemSessionState");
        final ItemSessionState result = new ItemSessionState();

        XmlMarshallerCore.parseAbstractPartSessionStateAttributes(result, element);
        result.setInitialized(XmlMarshallerCore.parseOptionalBooleanAttribute(element, "initialized", false));
        result.setResponded(XmlMarshallerCore.parseOptionalBooleanAttribute(element, "responded", false));
        result.setSuspendTime(XmlMarshallerCore.parseOptionalDateAttribute(element, "suspendTime"));
        result.setUnboundResponseIdentifiers(parseOptionalIdentifierAttributeList(element, "unboundResponseIdentifiers"));
        result.setInvalidResponseIdentifiers(parseOptionalIdentifierAttributeList(element, "invalidResponseIdentifiers"));
        result.setNumAttempts(XmlMarshallerCore.parseOptionalIntegerAttribute(element, "numAttempts", 0));

        final String completionStatus = XmlMarshallerCore.parseOptionalStringAttribute(element, "completionStatus");
        if (completionStatus!=null) {
            try {
                result.setCompletionStatus(completionStatus);
            }
            catch (final IllegalArgumentException e) {
                throw new XmlUnmarshallingException("Unexpected value for completionStauts: " + completionStatus);
            }
        }

        if (element.hasAttribute("sessionStatus")) {
            final String sessionStatusAttr = element.getAttribute("sessionStatus");
            try {
                result.setSessionStatus(SessionStatus.parseSessionStatus(sessionStatusAttr));
            }
            catch (final IllegalArgumentException e) {
                throw new XmlUnmarshallingException("Unexpected value for sessionStatus: " + sessionStatusAttr);
            }
        }

        final List<Element> childElements = XmlMarshallerCore.expectElementChildren(element);
        for (final Element childElement : childElements) {
            final String elementName = childElement.getLocalName();
            if (elementName.equals("responseInput")) {
                final Identifier identifier = XmlMarshallerCore.parseIdentifierAttribute(childElement, "identifier");
                final List<Element> responseElements = XmlMarshallerCore.expectElementChildren(childElement);
                /* Should contain either 1 <file> or 1 or more <string> */
                if (responseElements.size()==1 && responseElements.get(0).getLocalName().equals("file")) {
                    final Element fileResponseElement = responseElements.get(0);
                    final String contentType = XmlMarshallerCore.requireAttribute(fileResponseElement, "contentType");
                    final String fileName = XmlMarshallerCore.requireAttribute(fileResponseElement, "fileName");
                    final String absoluteFilePath = XmlMarshallerCore.requireAttribute(fileResponseElement, "absoluteFilePath");
                    final FileResponseData fileResponseData = new FileResponseData(new File(absoluteFilePath), contentType, fileName);
                    result.setRawResponseData(identifier, fileResponseData);
                }
                else {
                    final List<String> stringResponseBuilder = new ArrayList<String>();
                    for (final Element responseElement : responseElements) {
                        if ("string".equals(responseElement.getLocalName())) {
                            stringResponseBuilder.add(XmlMarshallerCore.expectTextContent(responseElement));
                        }
                        else {
                            throw new XmlUnmarshallingException("Expected 1 <file> or multiple <string> children");
                        }
                    }
                    result.setRawResponseData(identifier, new StringResponseData(stringResponseBuilder));
                }
            }
            else if (elementName.equals("candidateComment")) {
                result.setCandidateComment(XmlMarshallerCore.expectTextContent(childElement));
            }
            else if (elementName.equals("shuffledInteractionChoiceOrder")) {
                final Identifier responseIdentifier = XmlMarshallerCore.parseIdentifierAttribute(childElement, "responseIdentifier");
                final List<Identifier> choiceIdentifiers = parseOptionalIdentifierAttributeList(childElement, "choiceSequence");
                result.setShuffledInteractionChoiceOrder(responseIdentifier, choiceIdentifiers);
            }
            else if (elementName.equals("uncommittedResponseValue")) {
                final Identifier identifier = XmlMarshallerCore.parseIdentifierAttribute(childElement, "identifier");
                final Value value = XmlMarshallerCore.parseValue(childElement);
                result.setUncommittedResponseValue(identifier, value);
            }
            else if (elementName.equals("templateVariable")) {
                final Identifier identifier = XmlMarshallerCore.parseIdentifierAttribute(childElement, "identifier");
                final Value value = XmlMarshallerCore.parseValue(childElement);
                result.setTemplateValue(identifier, value);
            }
            else if (elementName.equals("responseVariable")) {
                final Identifier identifier = XmlMarshallerCore.parseIdentifierAttribute(childElement, "identifier");
                final Value value = XmlMarshallerCore.parseValue(childElement);
                result.setResponseValue(identifier, value);
            }
            else if (elementName.equals("outcomeVariable")) {
                final Identifier identifier = XmlMarshallerCore.parseIdentifierAttribute(childElement, "identifier");
                final Value value = XmlMarshallerCore.parseValue(childElement);
                result.setOutcomeValue(identifier, value);
            }
            else if (elementName.equals("overriddenTemplateDefault")) {
                final Identifier identifier = XmlMarshallerCore.parseIdentifierAttribute(childElement, "identifier");
                final Value value = XmlMarshallerCore.parseValue(childElement);
                result.setOverriddenTemplateDefaultValue(identifier, value);
            }
            else if (elementName.equals("overriddenResponseDefault")) {
                final Identifier identifier = XmlMarshallerCore.parseIdentifierAttribute(childElement, "identifier");
                final Value value = XmlMarshallerCore.parseValue(childElement);
                result.setOverriddenResponseDefaultValue(identifier, value);
            }
            else if (elementName.equals("overriddenOutcomeDefault")) {
                final Identifier identifier = XmlMarshallerCore.parseIdentifierAttribute(childElement, "identifier");
                final Value value = XmlMarshallerCore.parseValue(childElement);
                result.setOverriddenOutcomeDefaultValue(identifier, value);
            }
            else if (elementName.equals("overriddenCorrectResponse")) {
                final Identifier identifier = XmlMarshallerCore.parseIdentifierAttribute(childElement, "identifier");
                final Value value = XmlMarshallerCore.parseValue(childElement);
                result.setOverriddenCorrectResponseValue(identifier, value);
            }
            else {
                throw new XmlUnmarshallingException("Unexpected element " + elementName);
            }
        }
        return result;
    }

    private static List<Identifier> parseOptionalIdentifierAttributeList(final Element element, final String identifierAttrListName) {
        final String identifierListAttrValue = element.getAttribute(identifierAttrListName);
        if (identifierListAttrValue.isEmpty()) {
            return Collections.emptyList();
        }
        final String[] identifierArray = identifierListAttrValue.split("\\s+");
        final List<Identifier> result = new ArrayList<Identifier>(identifierArray.length);
        for (final String identifierString : identifierArray) {
            try {
                result.add(Identifier.parseString(identifierString));
            }
            catch (final QtiParseException e) {
                throw new XmlUnmarshallingException("Item '"
                        + identifierString + "' extracted from value '"
                        + identifierListAttrValue + "' of list attribute "
                        + identifierAttrListName + " is not a valid QTI Identifier");
            }
        }
        return result;
    }
}
