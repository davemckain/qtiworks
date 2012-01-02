/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xperimental;

import uk.ac.ed.ph.jqtiplus.node.RootNode;

import java.io.Serializable;

/**
 * @author  David McKain
 * @version $Revision$
 */
public interface ResolutionResult<E extends RootNode> extends Serializable {
    
    /**
     * Returns the resolved JQTI Object, or null if resolution did not succeed. 
     * <p>
     * Implementations of this interface
     * should find a way to report failures in an appropriate way.
     */
    E getJQTIObject();

}
