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

package uk.ac.ed.ph.jqtiplus.resolution;

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.RootObject;
import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.provision.DynamicResourceHolder;
import uk.ac.ed.ph.jqtiplus.provision.ResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.provision.RootObjectHolder;
import uk.ac.ed.ph.jqtiplus.provision.RootObjectProvider;

import java.io.Serializable;
import java.net.URI;

/**
 * Memoises the result of a call to {@link RootObjectProvider#lookupRootObject(URI, uk.ac.ed.ph.jqtiplus.node.ModelRichness, Class)}
 * (This wraps up the resulting {@link ResourceNotFoundException} and {@link BadResourceException}s.)
 *
 * @author David McKain
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class RootObjectLookup<E extends RootObject> implements Serializable {
    
    private static final long serialVersionUID = -6339477418787798594L;
    
    private final URI systemId;
    private final RootObjectHolder<E> rootObjectHolder;
    private final ResourceNotFoundException notFoundException;
    private final BadResourceException badResultException;
    
    public RootObjectLookup(final E rootNode) {
        this(rootNode.getSystemId(), new DynamicResourceHolder<E>(rootNode));
    }
    
    public RootObjectLookup(URI systemId, RootObjectHolder<E> resourceProvideResult) {
        this(systemId, resourceProvideResult, null, null);
    }
    
    public RootObjectLookup(URI systemId, ResourceNotFoundException notFoundException) {
        this(systemId, null, notFoundException, null);
    }
    
    public RootObjectLookup(URI systemId, BadResourceException badResultException) {
        this(systemId, null, null, badResultException);
    }
    
    private RootObjectLookup(URI systemId, RootObjectHolder<E> resourceProvideResult,
            ResourceNotFoundException notFoundException, BadResourceException badResultException) {
        this.systemId = systemId;
        this.rootObjectHolder = resourceProvideResult;
        this.notFoundException = notFoundException;
        this.badResultException = badResultException;
    }
    
    public URI getSystemId() {
        return systemId;
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public RootObjectHolder<E> getRootObjectHolder() {
        return rootObjectHolder;
    }
    
    public BadResourceException getBadResultException() {
        return badResultException;
    }
    
    public ResourceNotFoundException getNotFoundException() {
        return notFoundException;
    }
    
    /**
     * "Evaluates" this lookup, which behaves similarly to {@link RootObjectProvider}.
     * 
     * @throws ResourceNotFoundException
     * @throws BadResourceException
     */
    public RootObjectHolder<E> evaluate() throws ResourceNotFoundException, BadResourceException {
        if (rootObjectHolder!=null) {
            return rootObjectHolder;
        }
        else if (notFoundException!=null) {
            throw notFoundException;
        }
        else if (badResultException!=null) {
            throw badResultException;
        }
        else {
            throw new QtiLogicException("Unexpected logic branch");
        }
    }
    
    public boolean wasSuccessful() {
        return rootObjectHolder!=null;
    }
    
    public E extractIfSuccessful() {
        return rootObjectHolder!=null ? rootObjectHolder.getRootObject() : null;
    }
    
    public E extractEnsuringSuccessful() {
        if (rootObjectHolder==null) {
            throw new QtiLogicException("Lookup " + this + " was expected to have been successful");
        }
        return rootObjectHolder.getRootObject();
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(getClass().getSimpleName())
            .append('@').append(hashCode())
            .append("(systemId=").append(systemId)
            .append(',');
        if (rootObjectHolder!=null) {
            stringBuilder.append("resourceProvideResult=").append(rootObjectHolder);
        }
        else if (notFoundException!=null) {
            stringBuilder.append("notFoundException=").append(notFoundException);
        }
        else if (badResultException!=null) {
            stringBuilder.append("badResultException=").append(badResultException);
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }
}
