/* $Id: QTIObjectCache.java 2721 2011-06-22 12:32:56Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils2;

import uk.ac.ed.ph.jqtiplus.node.RootNode;

import java.net.URI;

/**
 * FIXME: This is not used anywhere!
 *
 * @author  David McKain
 * @version $Revision: 2721 $
 */
public interface QTIObjectCache {
    
    QTIReadResult<? extends RootNode> getObject(URI systemId);
    
    void putObject(URI systemId, QTIReadResult<? extends RootNode> object);
    
}
