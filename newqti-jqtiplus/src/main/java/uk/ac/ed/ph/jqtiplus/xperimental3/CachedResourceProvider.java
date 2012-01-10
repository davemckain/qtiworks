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

package uk.ac.ed.ph.jqtiplus.xperimental3;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.resolution.BadResourceException;
import uk.ac.ed.ph.jqtiplus.resolution.ModelRichness;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceHolder;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceProvider;
import uk.ac.ed.ph.jqtiplus.xperimental2.FrozenResourceLookup;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class that caches the results of calls to {@link ResourceProvider} during
 * resolution so that we only need to build once.
 *
 * @author David McKain
 */
final class CachedResourceProvider implements Serializable {
   
    private static final Logger logger = LoggerFactory.getLogger(CachedResourceProvider.class);
    
    private static final long serialVersionUID = -8407905672200096970L;
    
    private final ResourceProvider resourceProvider;
    private final ModelRichness modelRichness;

    private final Map<URI, FrozenResourceLookup<?>> cacheData;
    
    public CachedResourceProvider(final ResourceProvider resourceProvider, ModelRichness modelRichness) {
        this.resourceProvider = resourceProvider;
        this.modelRichness = modelRichness;
        this.cacheData = new HashMap<URI, FrozenResourceLookup<?>>();
    }
    
    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }
    
    public ModelRichness getModelRichness() {
        return modelRichness;
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public Map<URI, FrozenResourceLookup<?>> getCacheData() {
        return cacheData;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends RootNode> FrozenResourceLookup<E> getFrozenResource(URI systemId, Class<E> resultClass) {
        FrozenResourceLookup<E> frozenResult = (FrozenResourceLookup<E>) cacheData.get(systemId);
        if (frozenResult!=null) {
            /* Cache hit */
            logger.info("Cache hit for key {} yielded {}", systemId, frozenResult);
        }
        else {
            /* Cache miss */
            try {
                ResourceHolder<E> result = resourceProvider.provideQtiResource(systemId, modelRichness, resultClass);
                frozenResult = new FrozenResourceLookup<E>(systemId, result);
            }
            catch (BadResourceException e) {
                frozenResult = new FrozenResourceLookup<E>(systemId, e);
            }
            catch (ResourceNotFoundException e) {
                frozenResult = new FrozenResourceLookup<E>(systemId, e);
            }
            cacheData.put(systemId, frozenResult);
            logger.info("Cache miss for key {} stored {}", systemId, frozenResult);
        }
        return frozenResult;
    }
    
    public <E extends RootNode> E getResource(URI systemId, Class<E> resultClass)
            throws ResourceNotFoundException, BadResourceException {
        return getFrozenResource(systemId, resultClass).thaw().getRequiredQtiObject();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(resourceProvider=" + resourceProvider
                + ",modelRichness=" + modelRichness
                + ",cacheData=" + cacheData
                + ")";
    }
}
