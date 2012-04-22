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
package uk.ac.ed.ph.qtiworks.mathassess.glue.types;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract base class for values with "ordered" cardinality, i.e. lists.
 * 
 * @param <S> underlying {@link ValueWrapper} type of the {@link SingleValueWrapper} 
 *   for the elements in this List
 * @param <B> underlying Java type of the elements in this List
 *
 * @author David McKain
 */
public abstract class OrderedValueWrapper<B, S extends SingleValueWrapper<B>> 
        extends ArrayList<S> implements CompoundValueWrapper<B,S> {
    
    private static final long serialVersionUID = -1943487267906694745L;
    
    @Override
    public ValueCardinality getCardinality() {
        return ValueCardinality.ORDERED;
    }
    
    @Override
    public boolean add(S e) {
        ensureNotNull(e);
        return super.add(e);
    }
    
    @Override
    public void add(int index, S e) {
        ensureNotNull(e);
        super.add(index, e);
    }
    
    @Override
    public boolean addAll(Collection<? extends S> c) {
        for (S e : c) {
            ensureNotNull(e);
        }
        return super.addAll(c);
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends S> c) {
        for (S e : c) {
            ensureNotNull(e);
        }
        return super.addAll(index, c);
    }
    
    @Override
    public boolean isNull() {
        return false;
    }
    
    private void ensureNotNull(S e) {
        if (e==null) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " must not contain null entries");
        }
    }
}
