/* $Id: QTIXMLReadException.java 2766 2011-07-21 17:02:08Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import uk.ac.ed.ph.jqtiplus.exception.QTIRuntimeException;

/**
 * FIXME: Document this
 * 
 * @author  David McKain
 * @version $Revision: 2766 $
 */
public class QTIXMLReadException extends QTIRuntimeException {

    private static final long serialVersionUID = -6558013135849907488L;
    
    private final XMLParseResult xmlParseResult;
    
    public QTIXMLReadException(final XMLParseResult xmlParseResult) {
        super("QTI XML could not be parsed and/or schema-validated: " + xmlParseResult);
        this.xmlParseResult = xmlParseResult;
    }
    
    public XMLParseResult getXMLParseResult() {
        return xmlParseResult;
    }

}
