/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xperimental;

import uk.ac.ed.ph.jqtiplus.node.RootNode;

/**
 * @author  David McKain
 * @version $Revision$
 */
public interface ResolutionResult<E extends RootNode> {
    
    E getJQTIObject();

}
