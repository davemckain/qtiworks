/* $Id: QTIReadResult.java 2801 2011-10-05 07:57:43Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils.legacy;

import uk.ac.ed.ph.jqtiplus.io.reading.QTIModelBuildingError;
import uk.ac.ed.ph.jqtiplus.node.RootNode;

import java.io.Serializable;
import java.util.List;

/**
 * FIXME: Document this type!
 *
 * @author  David McKain
 * @version $Revision: 2801 $
 */
public final class QTIReadResult<E extends RootNode> implements Serializable {

    private static final long serialVersionUID = -6470500039269477402L;
    
    private final E jqtiObject;
    private final XMLParseResult xmlParseResult;
    private final List<QTIModelBuildingError> qtiParseErrors;

    public QTIReadResult(E jqtiObject, XMLParseResult xmlParseResult, List<QTIModelBuildingError> qtiParseErrors) {
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
    
    public List<QTIModelBuildingError> getQTIParseErrors() {
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
