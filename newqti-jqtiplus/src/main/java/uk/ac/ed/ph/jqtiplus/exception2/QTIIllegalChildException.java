/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.exception2;

import uk.ac.ed.ph.jqtiplus.node.XmlNode;

/**
 * Exception thrown when trying to add an inappropriate child to a Node.
 * 
 * @author  David McKain
 * @version $Revision$
 */
public final class QTIIllegalChildException extends QTIModelException {

    private static final long serialVersionUID = 8810282210478664633L;
    
    private final XmlNode parent;
    private final String childClassTag;
    
    public QTIIllegalChildException(XmlNode parent, String childClassTag) {
        super("Illegal child class " + childClassTag + " for parent " + parent.getClassTag());
        this.parent = parent;
        this.childClassTag = childClassTag;
    }

    public XmlNode getParent() {
        return parent;
    }

    public String getChildClassTag() {
        return childClassTag;
    }
}
