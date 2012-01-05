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
package uk.ac.ed.ph.jqtiplus.xmlutils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Trivial SAX {@link DefaultHandler} that builds a DOM {@link Document}, handling only the types of
 * SAX events we'd expect in documents that do not contain complex DTDs.
 * 
 * @author David McKain
 */
public final class SimpleDOMBuilderHandler extends DefaultHandler {

    private final Document document;

    private Locator locator;

    private Node currentNode;

    public SimpleDOMBuilderHandler(Document document) {
        this.document = document;
        this.locator = null;
        this.currentNode = null;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void startDocument() {
        currentNode = document;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        final Element newElement = document.createElementNS(uri, qName);
        for (int i = 0, length = attributes.getLength(); i < length; i++) {
            newElement.setAttributeNS(attributes.getURI(i), attributes.getQName(i), attributes.getValue(i));
        }
        storeLocationInformation(newElement);
        currentNode.appendChild(newElement);
        currentNode = newElement;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        final String text = new String(ch, start, length);
        final Text textNode = document.createTextNode(text);
        storeLocationInformation(textNode);
        currentNode.appendChild(textNode);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (currentNode == null) {
            throw new IllegalStateException("Inconsistent state at endElement: currentNode==null");
        }
        currentNode = currentNode.getParentNode();
    }

    @Override
    public void endDocument() {
        if (currentNode != document) {
            throw new IllegalStateException("Inconsistent state at endDocument: currentNode=" + currentNode);
        }
    }

    private void storeLocationInformation(Node node) {
        if (locator != null) {
            final XMLSourceLocationInformation info = new XMLSourceLocationInformation(locator.getPublicId(), locator.getSystemId(), locator.getColumnNumber(),
                    locator.getLineNumber());
            node.setUserData(XMLResourceReader.LOCATION_INFORMATION_NAME, info, null);
        }
    }
}