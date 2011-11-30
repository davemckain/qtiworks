/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package org.qtitools.qti.node.test.flow;

import uk.ac.ed.ph.jqtiplus.state.SectionPartStateKey;

import java.io.Serializable;

/**
 * State for a {@link DefaultItemFlow}
 * 
 * @author  David McKain
 * @version $Revision$
 */
public final class DefaultItemFlowState implements Serializable {

    private static final long serialVersionUID = -783088015437077064L;
    
    private SectionPartStateKey currentItemRefKey;
    
    public DefaultItemFlowState() {
    }

    public SectionPartStateKey getCurrentItemRefKey() {
        return currentItemRefKey;
    }

    public void setCurrentItemRefKey(SectionPartStateKey currentItemRefKey) {
        this.currentItemRefKey = currentItemRefKey;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(currentItemRefKey=" + currentItemRefKey + ")";
    }
}
