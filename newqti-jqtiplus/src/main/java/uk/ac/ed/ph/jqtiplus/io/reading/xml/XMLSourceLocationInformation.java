/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.io.reading.xml;

import org.xml.sax.Locator;

/**
 * Encapsulates the information reported by a SAX {@link Locator} when
 * parsing XML.
 * 
 * @author  David McKain
 */
public class XMLSourceLocationInformation {
    
    private String publicId;
    private String systemId;
    private int columnNumber;
    private int lineNumber;
    
    public XMLSourceLocationInformation(String publicId, String systemId, int columnNumber, int lineNumber) {
        this.publicId = publicId;
        this.systemId = systemId;
        this.columnNumber = columnNumber;
        this.lineNumber = lineNumber;
    }

    public String getPublicId() {
        return publicId;
    }
    
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
    
    public String getSystemId() {
        return systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public int getColumnNumber() {
        return columnNumber;
    }
    
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(publicId=" + publicId
                + ",systemId=" + systemId
                + ",lineNumber=" + lineNumber
                + ",columnNumber=" + columnNumber
                + ")";
    }
}