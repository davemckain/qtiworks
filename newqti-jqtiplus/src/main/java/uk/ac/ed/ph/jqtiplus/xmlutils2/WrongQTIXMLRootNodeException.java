/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils2;

import uk.ac.ed.ph.jqtiplus.node.RootNode;

/**
 * FIXME: Document this!
 * 
 * @author  David McKain
 */
public final class WrongQTIXMLRootNodeException extends Exception {

    private static final long serialVersionUID = 4190015193303035142L;
    
    private final Class<? extends RootNode> expectedRootNodeClass;
    private final String actualRootNodeLocalName;
    
    public WrongQTIXMLRootNodeException(final Class<? extends RootNode> expectedRootNodeClass, final String actualRootNodeLocalName) {
        super("Expected QTI root Node of class " + expectedRootNodeClass + " but got element with local name " + actualRootNodeLocalName);
        this.expectedRootNodeClass = expectedRootNodeClass;
        this.actualRootNodeLocalName = actualRootNodeLocalName;
    }

    public Class<? extends RootNode> getExpectedRootNodeClass() {
        return expectedRootNodeClass;
    }

    public String getActualRootNodeLocalName() {
        return actualRootNodeLocalName;
    }
}
