/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils.legacy;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link QTIObjectCache} that simply stores things in a {@link HashMap}.
 * 
 * @author  David McKain
 * @version $Revision$
 */
public class SimpleQTIObjectCache implements QTIObjectCache {
    
    private final Map<URI, QTIReadResult<?>> objectCacheMap;
    
    public SimpleQTIObjectCache() {
        this.objectCacheMap = new HashMap<URI, QTIReadResult<?>>();
    }
    
    public QTIReadResult<?> getObject(URI systemId) {
        return objectCacheMap.get(systemId);
    }
    
    public void putObject(URI systemId, QTIReadResult<?> object) {
        objectCacheMap.put(systemId, object);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(objectCacheMap=" + objectCacheMap
            + ")";
    }
    
}
