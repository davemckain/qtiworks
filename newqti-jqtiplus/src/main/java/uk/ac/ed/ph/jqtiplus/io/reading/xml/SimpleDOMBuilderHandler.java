/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.io.reading.xml;

import uk.ac.ed.ph.jqtiplus.control.QTILogicException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Trivial SAX {@link DefaultHandler} that builds a DOM {@link Document}, handling only the types of
 * SAX events we'd expect in QTI documents.
 * 
 * @author  David McKain
 * @version $Revision$
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
        Element newElement = document.createElementNS(uri, qName);
        for (int i=0, length=attributes.getLength(); i<length; i++) {
            newElement.setAttributeNS(attributes.getURI(i), attributes.getQName(i), attributes.getValue(i));
        }
        storeLocationInformation(newElement);
        currentNode.appendChild(newElement);
        currentNode = newElement;
    }
    
    @Override
    public void characters(char[] ch, int start, int length) {
        String text = new String(ch, start, length);
        Text textNode = document.createTextNode(text);
        storeLocationInformation(textNode);
        currentNode.appendChild(textNode);
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) {
        if (currentNode==null) {
            throw new QTILogicException("Inconsistent state: currentNode==null");
        }
        currentNode = currentNode.getParentNode();
    }
    
    @Override
    public void endDocument() {
        if (currentNode!=document) {
            throw new QTILogicException("Inconsistent state at end of document: currentNode=" + currentNode);
        }
    }
    
    private void storeLocationInformation(Node node) {
        if (locator!=null) {
            XMLSourceLocationInformation info = new XMLSourceLocationInformation(locator.getPublicId(), locator.getSystemId(), locator.getColumnNumber(), locator.getLineNumber());
            node.setUserData(QTIXMLReader.LOCATION_INFORMATION_NAME, info, null);
        }
    }
}