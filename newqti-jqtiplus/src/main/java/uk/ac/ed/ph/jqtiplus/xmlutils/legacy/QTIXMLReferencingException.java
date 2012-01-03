/* $Id: QTIXMLReferencingException.java 2766 2011-07-21 17:02:08Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils.legacy;

import uk.ac.ed.ph.jqtiplus.exception.QTIRuntimeException;
import uk.ac.ed.ph.jqtiplus.io.reading.QTIModelBuildingError;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;

import java.util.List;

/**
 * This Exception is thrown if a referenced XML resource (e.g. a {@link ResponseProcessing}
 * template) could not be successfully resolved and instantiated.
 * 
 * @author  David McKain
 * @version $Revision: 2766 $
 */
public class QTIXMLReferencingException extends QTIRuntimeException {

    private static final long serialVersionUID = 5628758708191965953L;
    
    /** Result of reading in XML, if we got that far */
    private final XMLParseResult xmlReadResult;
    
    /** QTI Parse errors, if we got that far */
    private final List<QTIModelBuildingError> qtiParseErrors;

    public QTIXMLReferencingException(String message, Throwable cause) {
        this(message, null, null, cause);
    }
    
    public QTIXMLReferencingException(String message, XMLParseResult xmlParseResult) {
        this(message, xmlParseResult, null, null);
    }
    
    public QTIXMLReferencingException(String message, XMLParseResult xmlParseResult, List<QTIModelBuildingError> qtiParseErrors) {
        this(message, xmlParseResult, qtiParseErrors, null);
    }
    
    private QTIXMLReferencingException(String message, XMLParseResult xmlParseResult, List<QTIModelBuildingError> qtiParseErrors, Throwable cause) {
        super(message, cause);
        this.xmlReadResult = xmlParseResult;
        this.qtiParseErrors = qtiParseErrors;
    }

    public XMLParseResult getXMLParseResult() {
        return xmlReadResult;
    }

    public List<QTIModelBuildingError> getQtiParseErrors() {
        return qtiParseErrors;
    }
}
