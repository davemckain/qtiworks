/* $Id: QTIReadResult.java 2801 2011-10-05 07:57:43Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.io.reading.objects;

import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLParseResult;
import uk.ac.ed.ph.jqtiplus.xperimental.ResolutionResult;

import java.io.Serializable;
import java.util.List;

/**
 * FIXME: Document this type!
 *
 * @author  David McKain
 * @version $Revision: 2801 $
 */
public final class QTIReadResult<E extends RootNode> implements ResolutionResult<E>, Serializable {

    private static final long serialVersionUID = -6470500039269477402L;
    
    private final E jqtiObject;
    private final XMLParseResult xmlParseResult;
    private final List<QTIParseError> qtiParseErrors;

    public QTIReadResult(E jqtiObject, XMLParseResult xmlParseResult, List<QTIParseError> qtiParseErrors) {
        this.jqtiObject = jqtiObject;
        this.xmlParseResult = xmlParseResult;
        this.qtiParseErrors = qtiParseErrors;
    }

    public E getJQTIObject() {
        return jqtiObject;
    }
    
    public XMLParseResult getXMLParseResult() {
        return xmlParseResult;
    }
    
    public List<QTIParseError> getQTIParseErrors() {
        return qtiParseErrors;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(jqtiObject=" + jqtiObject
            + ",xmlParseResult=" + xmlParseResult
            + ",qtiParseErrors=" + qtiParseErrors
            + ")";
    }
}
