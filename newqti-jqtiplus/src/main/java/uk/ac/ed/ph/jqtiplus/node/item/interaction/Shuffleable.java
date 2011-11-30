/* $Id: Shuffleable.java 2750 2011-07-08 15:54:33Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

/**
 * Marker interface for {@link Interaction}s that support the <tt>shuffle</tt>
 * attribute.
 *
 * @author  David McKain
 * @version $Revision: 2750 $
 */
public interface Shuffleable {
    
    Boolean getShuffle();
    
    void setShuffle(Boolean shuffle);
    
}
