/* $Id: XMLReadResult.java 2749 2011-07-08 08:43:51Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.io.reading.xml;

import java.io.Serializable;

import org.w3c.dom.Document;

/**
 * Encapsulates the result of QTI XML reading
 * 
 * @author  David McKain
 * @version $Revision: 2749 $
 */
public class XMLReadResult implements Serializable {

    private static final long serialVersionUID = -6558013135849907488L;
    
    private final Document document;
    private final XMLParseResult xmlParseResult;
    
    public XMLReadResult(final Document document, final XMLParseResult xmlParseResult) {
        this.document = document;
        this.xmlParseResult = xmlParseResult;
    }
    
    public Document getDocument() {
        return document;
    }
    
    public XMLParseResult getXMLParseResult() {
        return xmlParseResult;
    }

    public boolean isSchemaValid() {
        return document!=null
            && xmlParseResult.isSchemaValid();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(document=" + document
            + ",xmlParseResult=" + xmlParseResult
            + ")";
    }
}
