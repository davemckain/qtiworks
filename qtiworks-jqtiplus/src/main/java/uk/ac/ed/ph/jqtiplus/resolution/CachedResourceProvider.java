/* Copyright (c) 2012-2013, University of Edinburgh.
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
package uk.ac.ed.ph.jqtiplus.resolution;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.provision.ResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.provision.RootNodeHolder;
import uk.ac.ed.ph.jqtiplus.provision.RootNodeProvider;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class that caches the results of calls to {@link AssessmentObjectResolver} during
 * resolution so that we only need to build once.
 *
 * @author David McKain
 */
final class CachedResourceProvider {

    private static final Logger logger = LoggerFactory.getLogger(CachedResourceProvider.class);

    private final RootNodeProvider rootNodeProvider;
    private final Map<URI, RootNodeLookup<?>> cacheData;

    public CachedResourceProvider(final RootNodeProvider rootNodeProvider) {
        this.rootNodeProvider = rootNodeProvider;
        this.cacheData = new HashMap<URI, RootNodeLookup<?>>();
    }

    public RootNodeProvider getRootNodeProvider() {
        return rootNodeProvider;
    }

    @ObjectDumperOptions(DumpMode.DEEP)
    public Map<URI, RootNodeLookup<?>> getCacheData() {
        return cacheData;
    }

    @SuppressWarnings("unchecked")
    public <E extends RootNode> RootNodeLookup<E> getLookup(final URI systemId, final Class<E> resultClass) {
        RootNodeLookup<E> frozenResult = (RootNodeLookup<E>) cacheData.get(systemId);
        if (frozenResult!=null) {
            /* Cache hit */
            logger.debug("Resource cache hit for key {} yielded {}", systemId, frozenResult);
        }
        else {
            /* Cache miss */
            try {
                final RootNodeHolder<E> result = rootNodeProvider.lookupRootNode(systemId, resultClass);
                frozenResult = new RootNodeLookup<E>(systemId, result);
            }
            catch (final BadResourceException e) {
                frozenResult = new RootNodeLookup<E>(systemId, resultClass, e);
            }
            catch (final ResourceNotFoundException e) {
                frozenResult = new RootNodeLookup<E>(systemId, resultClass, e);
            }
            cacheData.put(systemId, frozenResult);
            logger.debug("Resource cache miss for key {} stored {}", systemId, frozenResult);
        }
        return frozenResult;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(rootNodeProvider=" + rootNodeProvider
                + ",cacheData=" + cacheData
                + ")";
    }
}
