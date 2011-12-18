/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node;

import uk.ac.ed.ph.jqtiplus.control.ToRefactor;

import java.net.URI;

/**
 * Marker interface for a "root" QTI Node.
 * 
 * @author  David McKain
 * @version $Revision$
 */
public interface RootNode extends XmlObject {
    
    /** Header of xml file. */
    @ToRefactor
    public static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    
    /** Returns the systemId of this tree, if loaded from a URI, null otherwise */
    URI getSystemId();
    
    /** Sets the systemId for this tree */
    void setSystemId(URI systemId);

}
