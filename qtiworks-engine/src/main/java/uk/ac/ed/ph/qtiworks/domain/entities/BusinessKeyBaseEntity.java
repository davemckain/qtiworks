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
package uk.ac.ed.ph.qtiworks.domain.entities;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;

/**
 * Trivial base class for entities which possess simple "business key"
 *
 * @param <E> this class
 * @param <K> business key type
 *
 * @author David McKain
 */
public abstract class BusinessKeyBaseEntity<E extends BusinessKeyBaseEntity<E,K>, K extends Comparable<K>>
        implements BaseEntity, Comparable<E> {

    private static final long serialVersionUID = -6079319781967984501L;

    protected abstract Class<E> getEntityClass();

    protected abstract K getBusinessKey();

    @SuppressWarnings("unchecked")
    @Override
    public final boolean equals(final Object obj) {
        final Class<E> entityClass = getEntityClass();
        if (obj==null || !(entityClass.isAssignableFrom(obj.getClass()))) {
            return false;
        }
        final E other = getEntityClass().cast(obj);
        final K thisKey = ensureBusinessKey((E) this, "equals");
        final K otherKey = other.getBusinessKey();
        return thisKey.equals(otherKey);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int hashCode() {
        final K thisKey = ensureBusinessKey((E) this, "hashCode");
        return thisKey.hashCode();
    }

    protected K ensureBusinessKey(final E object, final String methodName) {
        final K businessKey = object.getBusinessKey();
        if (businessKey==null) {
            throw new QtiWorksLogicException("Business key for entity " + object.toString()
                    + " was null when " + methodName + " was called");
        }
        return businessKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final int compareTo(final E o) {
        final K thisKey = ensureBusinessKey((E) this, "compareTo");
        final K otherKey = ensureBusinessKey(o, "compareTo");
        return thisKey.compareTo(otherKey);
    }
}
