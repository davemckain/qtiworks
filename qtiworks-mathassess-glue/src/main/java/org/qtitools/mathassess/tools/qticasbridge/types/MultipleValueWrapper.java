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
package org.qtitools.mathassess.tools.qticasbridge.types;

import java.util.AbstractSet;
import java.util.HashSet;

/**
 * Abstract base class for values with "multiple" cardinality, i.e. sets
 * 
 * @param <S> underlying {@link ValueWrapper} type of the {@link SingleValueWrapper} 
 *   for the elements in this Set
 * @param <B> underlying Java type of the elements in this Set
 * 
 * @author David McKain
 */
public abstract class MultipleValueWrapper<B, S extends SingleValueWrapper<B>> 
        extends HashSet<S> implements CompoundValueWrapper<B,S> {
    
    private static final long serialVersionUID = -1943487267906694745L;
    
    public ValueCardinality getCardinality() {
        return ValueCardinality.MULTIPLE;
    }
    
    @Override
    public boolean add(S e) {
        ensureNotNull(e);
        return super.add(e);
    }
    
    public boolean isNull() {
        return false;
    }

    private void ensureNotNull(S e) {
        if (e==null) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " must not contain null entries");
        }
    }

    /**
     * Overridden to change the semantics normally inherited from {@link AbstractSet} to
     * what we might expect here, which is that two such wrappers are the same if and only if
     * they have the same size and each element of this wrapper <strong>compares as equal</strong>
     * to an element in the other wrapper.
     */
    @Override
    public boolean equals(Object o) {
        if (o==this) {
            return true;
        }
        else if (!(o instanceof MultipleValueWrapper<?,?>)) {
            return false;
        }
        MultipleValueWrapper<?,?> other = (MultipleValueWrapper<?,?>) o;
        if (size()!=other.size()) {
            return false;
        }
        /* FIXME: This algorithm is O(size^2), which is crap! */
        boolean foundThisInThat;
        for (S elementInThis : this) {
            foundThisInThat = false;
            for (Object elementInThat : other) {
                if (elementInThis.equals(elementInThat)) {
                    foundThisInThat = true;
                    break;
                }
            }
            if (!foundThisInThat) {
                return false;
            }
        }
        return true;
    }
}
