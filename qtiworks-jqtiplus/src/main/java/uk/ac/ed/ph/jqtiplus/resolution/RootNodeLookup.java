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

import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.provision.DynamicResourceHolder;
import uk.ac.ed.ph.jqtiplus.provision.ResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.provision.RootNodeHolder;
import uk.ac.ed.ph.jqtiplus.provision.RootNodeProvider;

import java.io.Serializable;
import java.net.URI;

/**
 * Memoises the result of a call to {@link RootNodeProvider#lookupRootNode(URI, Class)}
 * (This wraps up the resulting {@link ResourceNotFoundException} and {@link BadResourceException}s.)
 *
 * @author David McKain
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class RootNodeLookup<E extends RootNode> implements Serializable {

    private static final long serialVersionUID = -6339477418787798594L;

    private final URI systemId;
    private final Class<E> requestedRootNodeClass;
    private final RootNodeHolder<E> rootNodeHolder;
    private final ResourceNotFoundException notFoundException;
    private final BadResourceException badResourceException;

    RootNodeLookup(final E rootNode) {
        this(rootNode.getSystemId(), new DynamicResourceHolder<E>(rootNode));
    }

    RootNodeLookup(final URI systemId, final RootNodeHolder<E> rootNodeHolder) {
        this(systemId, rootNodeHolder.getRequestedRootNodeClass(), rootNodeHolder, null, null);
    }

    RootNodeLookup(final URI systemId, final Class<E> requestedRootNodeClass, final ResourceNotFoundException notFoundException) {
        this(systemId, requestedRootNodeClass, null, notFoundException, null);
    }

    RootNodeLookup(final URI systemId, final Class<E> requestedRootNodeClass, final BadResourceException badResourceException) {
        this(systemId, requestedRootNodeClass, null, null, badResourceException);
    }

    private RootNodeLookup(final URI systemId, final Class<E> rootNodeClass,
            final RootNodeHolder<E> resourceProvideResult,
            final ResourceNotFoundException notFoundException, final BadResourceException badResourceException) {
        this.systemId = systemId;
        this.requestedRootNodeClass = rootNodeClass;
        this.rootNodeHolder = resourceProvideResult;
        this.notFoundException = notFoundException;
        this.badResourceException = badResourceException;
    }

    public URI getSystemId() {
        return systemId;
    }

    public Class<E> getRequestedRootNodeClass() {
        return requestedRootNodeClass;
    }

    @ObjectDumperOptions(DumpMode.DEEP)
    public RootNodeHolder<E> getRootNodeHolder() {
        return rootNodeHolder;
    }

    @ObjectDumperOptions(DumpMode.DEEP)
    public BadResourceException getBadResourceException() {
        return badResourceException;
    }

    @ObjectDumperOptions(DumpMode.DEEP)
    public ResourceNotFoundException getNotFoundException() {
        return notFoundException;
    }

    /**
     * "Evaluates" this lookup, which behaves similarly to {@link RootNodeProvider}.
     *
     * @throws ResourceNotFoundException
     * @throws BadResourceException
     */
    public RootNodeHolder<E> evaluate() throws ResourceNotFoundException, BadResourceException {
        if (rootNodeHolder!=null) {
            return rootNodeHolder;
        }
        else if (notFoundException!=null) {
            throw notFoundException;
        }
        else if (badResourceException!=null) {
            throw badResourceException;
        }
        else {
            throw new QtiLogicException("Unexpected logic branch");
        }
    }

    public boolean wasSuccessful() {
        return rootNodeHolder!=null;
    }

    public E extractIfSuccessful() {
        return rootNodeHolder!=null ? rootNodeHolder.getRootNode() : null;
    }

    public E extractAssumingSuccessful() {
        if (rootNodeHolder==null) {
            throw new IllegalStateException("Lookup " + this + " was expected to have been successful");
        }
        return rootNodeHolder.getRootNode();
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder(getClass().getSimpleName())
            .append('@').append(hashCode())
            .append("(systemId=").append(systemId)
            .append(',');
        if (rootNodeHolder!=null) {
            stringBuilder.append("resourceProvideResult=").append(rootNodeHolder);
        }
        else if (notFoundException!=null) {
            stringBuilder.append("notFoundException=").append(notFoundException);
        }
        else if (badResourceException!=null) {
            stringBuilder.append("badResourceException=").append(badResourceException);
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }
}
