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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * FIXME: Document this type
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

}
