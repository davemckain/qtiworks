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
package uk.ac.ed.ph.jqtiplus.value;

/**
 * Base class for all QTI {@link Value} classes.
 *
 * @author David McKain
 */
public abstract class AbstractValue implements Value {

    private static final long serialVersionUID = -2658294172416932855L;

    @Override
    public final Signature getSignature() {
        if (isNull()) {
            return null;
        }
        return Signature.getSignature(getCardinality(), getBaseType());
    }

    @Override
    public final boolean hasCardinality(final Cardinality cardinality) {
        if (cardinality==null || isNull()) {
            return false;
        }
        return cardinality==getCardinality();
    }

    @Override
    public final boolean hasBaseType(final BaseType baseType) {
        if (baseType==null || isNull()) {
            return false;
        }
        return baseType==getBaseType();
    }

    @Override
    public final boolean hasSignature(final Signature signature) {
        if (signature==null || isNull()) {
            return false;
        }
        if (getCardinality()==Cardinality.RECORD) {
            return signature==Signature.RECORD;
        }
        else {
            return getCardinality()==signature.getCardinality()
                    && getBaseType()==signature.getBaseType();
        }
    }

    @Override
    public final boolean qtiEquals(final Value other) {
        if (isNull() && other.isNull()) {
            return true;
        }
        return equals(other);
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(" + toQtiString() + ")";
    }
}
