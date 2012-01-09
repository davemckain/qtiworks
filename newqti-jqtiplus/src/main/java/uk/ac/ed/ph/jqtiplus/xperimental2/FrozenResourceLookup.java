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
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.resolution.BadResourceException;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceHolder;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceProvider;

import java.io.Serializable;
import java.net.URI;

/**
 * Memoises the result of a call to {@link ResourceProvider} for use with
 * {@link ResourceLookupCache}.
 *
 * @author David McKain
 */
@ObjectDumperOptions(DumpMode.TO_STRING)
public final class FrozenResourceLookup<E extends RootNode> implements Serializable {
    
    private static final long serialVersionUID = -6339477418787798594L;
    
    private final URI systemId;
    private final ResourceHolder<E> resourceProvideResult;
    private final ResourceNotFoundException notFoundException;
    private final BadResourceException badResultException;
    
    public FrozenResourceLookup(URI systemId, ResourceHolder<E> resourceProvideResult) {
        this(systemId, resourceProvideResult, null, null);
    }
    
    public FrozenResourceLookup(URI systemId, ResourceNotFoundException notFoundException) {
        this(systemId, null, notFoundException, null);
    }
    
    public FrozenResourceLookup(URI systemId, BadResourceException badResultException) {
        this(systemId, null, null, badResultException);
    }
    
    private FrozenResourceLookup(URI systemId, ResourceHolder<E> resourceProvideResult,
            ResourceNotFoundException notFoundException, BadResourceException badResultException) {
        this.systemId = systemId;
        this.resourceProvideResult = resourceProvideResult;
        this.notFoundException = notFoundException;
        this.badResultException = badResultException;
    }
    
    public URI getSystemId() {
        return systemId;
    }

    public ResourceHolder<E> thaw() throws ResourceNotFoundException, BadResourceException {
        if (resourceProvideResult!=null) {
            return resourceProvideResult;
        }
        else if (notFoundException!=null) {
            throw notFoundException;
        }
        else if (badResultException!=null) {
            throw badResultException;
        }
        else {
            throw new QTILogicException("Unexpected logic branch");
        }
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(getClass().getSimpleName())
            .append('@').append(hashCode())
            .append("(systemId=").append(systemId).append(',');
        if (resourceProvideResult!=null) {
            stringBuilder.append("resourceProvideResult=").append(resourceProvideResult);
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
