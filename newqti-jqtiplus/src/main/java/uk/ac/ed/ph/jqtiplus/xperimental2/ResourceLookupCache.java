/* Copyright (c) 2012, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */

package uk.ac.ed.ph.jqtiplus.xperimental2;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.resolution.BadResourceException;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceHolder;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceProvider;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceUsage;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public final class ResourceLookupCache implements Serializable {
    
    static final class CacheKey implements Serializable {
        
        private static final long serialVersionUID = -8002345078776538313L;
        final URI systemId;
        final ResourceUsage resourceUsage;
        final String stringRepresentation;
        
        public CacheKey(URI systemId, ResourceUsage resourceUsage) {
            this.systemId = systemId;
            this.resourceUsage = resourceUsage;
            this.stringRepresentation = "[" + systemId + ", " + resourceUsage + "]";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CacheKey)) {
                return false;
            }
            CacheKey other = (CacheKey) obj;
            return stringRepresentation.equals(other.stringRepresentation);
        }
        
        @Override
        public int hashCode() {
            return toString().hashCode();
        }
        
        @Override
        public String toString() {
            return stringRepresentation;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ResourceLookupCache.class);
    
    private static final long serialVersionUID = -8407905672200096970L;

    private final Map<CacheKey, FrozenResourceLookup<?>> cacheData;
    
    public ResourceLookupCache() {
        this.cacheData = new HashMap<CacheKey, FrozenResourceLookup<?>>();
    }
    
    public void clear() {
        cacheData.clear();
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public Map<CacheKey, FrozenResourceLookup<?>> getCacheData() {
        return cacheData;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends RootNode> FrozenResourceLookup<E> getFrozenResource(ResourceProvider resourceProvider, URI systemId, ResourceUsage resourceUsage, Class<E> resultClass) {
        CacheKey key = new CacheKey(systemId, resourceUsage);
        FrozenResourceLookup<E> frozenResult = (FrozenResourceLookup<E>) cacheData.get(key);
        if (frozenResult!=null) {
            /* Cache hit */
            logger.info("Cache hit for key {} yielded {}", key, frozenResult);
        }
        else {
            /* Cache miss */
            try {
                ResourceHolder<E> result = resourceProvider.provideQtiResource(systemId, resourceUsage, resultClass);
                frozenResult = new FrozenResourceLookup<E>(systemId, result);
            }
            catch (BadResourceException e) {
                frozenResult = new FrozenResourceLookup<E>(systemId, e);
            }
            catch (ResourceNotFoundException e) {
                frozenResult = new FrozenResourceLookup<E>(systemId, e);
            }
            cacheData.put(key, frozenResult);
            logger.info("Cache miss for key {} stored {}", key, frozenResult);
        }
        return frozenResult;
    }
    
    public <E extends RootNode> E getResource(ResourceProvider resourceProvider, URI systemId, ResourceUsage resourceUsage, Class<E> resultClass)
            throws ResourceNotFoundException, BadResourceException {
        return getFrozenResource(resourceProvider, systemId, resourceUsage, resultClass).thaw().getRequiredQtiObject();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(cacheData=" + cacheData
                + ")";
    }
}
