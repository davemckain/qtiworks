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
    private final List<QTIModelBuildingError> qtiModelBuildingErrors;

    public QTIReadResult(E jqtiObject, XMLParseResult xmlParseResult, List<QTIModelBuildingError> qtiModelBuildingErrors) {
        this.jqtiObject = jqtiObject;
        this.xmlParseResult = xmlParseResult;
        this.qtiModelBuildingErrors = qtiModelBuildingErrors;
    }

    public E getJQTIObject() {
        return jqtiObject;
    }
    
    public XMLParseResult getXMLParseResult() {
        return xmlParseResult;
    }
    
    public List<QTIModelBuildingError> getQTIModelBuildingErrors() {
        return qtiModelBuildingErrors;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(jqtiObject=" + jqtiObject
            + ",xmlParseResult=" + xmlParseResult
            + ",qtiModelBuildingErrors=" + qtiModelBuildingErrors
            + ")";
    }
}
