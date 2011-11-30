/* $Id: QTIReadResult.java 2801 2011-10-05 07:57:43Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.node.XmlObject;

import java.io.Serializable;

/**
 * FIXME: Document this type!
 *
 * @author  David McKain
 * @version $Revision: 2801 $
 */
public final class QTIReadResult<E extends XmlObject> implements Serializable {

    private static final long serialVersionUID = -6470500039269477402L;
    
    private final E jqtiObject;
    private final XMLParseResult xmlParseResult;
    private final QTIParseException qtiParseException;

    public QTIReadResult(E jqtiObject, XMLParseResult xmlParseResult, QTIParseException qtiParseException) {
        this.jqtiObject = jqtiObject;
        this.xmlParseResult = xmlParseResult;
        this.qtiParseException = qtiParseException;
    }

    public E getJQTIObject() {
        return jqtiObject;
    }
    
    public XMLParseResult getXMLParseResult() {
        return xmlParseResult;
    }
    
    public QTIParseException getQTIParseException() {
        return qtiParseException;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(jqtiObject=" + jqtiObject
            + ",xmlParseResult=" + xmlParseResult
            + ",qtiParseException=" + qtiParseException
            + ")";
    }
}
