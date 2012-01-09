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

import uk.ac.ed.ph.jqtiplus.control.QTILogicException;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.resolution.BadResultException;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceProvider;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceRequireResult;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public final class ResourceProviderResultCache implements Serializable {

    private static final long serialVersionUID = -8407905672200096970L;
    
    private final Map<URI, Object> cacheData;
    
    public ResourceProviderResultCache() {
        this.cacheData = new HashMap<URI, Object>();
    }
    
    public void clear() {
        cacheData.clear();
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <E extends RootNode> ResourceRequireResult<E> provideQtiResource(ResourceProvider resourceProvider, URI systemId, Class<E> resultClass)
            throws ResourceNotFoundException, BadResultException {
        ResourceRequireResult<E> result;
        if (cacheData.containsKey(systemId)) {
            Object cached = cacheData.get(systemId);
            if (cached instanceof CachedResourceRequireResult) {
                result = (CachedResourceRequireResult) cached;
            }
            else if (cached instanceof BadResultException) {
                throw (BadResultException) cached;
            }
            else if (cached instanceof ResourceNotFoundException) {
                throw (ResourceNotFoundException) cached;
            }
            else {
                throw new QTILogicException("Unexpected logic branch");
            }
        }
        else {
            try {
                result = resourceProvider.provideQtiResource(systemId, resultClass);
                CachedResourceRequireResult<E> cacheEntry = new CachedResourceRequireResult<E>(result);
                cacheData.put(systemId, cacheEntry);
            }
            catch (BadResultException e) {
                cacheData.put(systemId, e);
                throw e;
            }
            catch (ResourceNotFoundException e) {
                cacheData.put(systemId, e);
                throw e;
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(cacheData=" + cacheData
                + ")";
    }
    
    //-------------------------------------------------------------------
    
    public static final class CachedResourceRequireResult<E extends RootNode> implements ResourceRequireResult<E> {
        
        private static final long serialVersionUID = 3334486218843788968L;
        
        private final URI systemId;
        private final E qtiObject;
        
        public CachedResourceRequireResult(ResourceRequireResult<E> result) {
            this.systemId = result.getSystemId();
            this.qtiObject = result.getRequiredQtiObject();
        }
        
        @Override
        public URI getSystemId() {
            return systemId;
        }
        
        @Override
        public E getRequiredQtiObject() {
            return qtiObject;
        }
    }
}
