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

import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

import java.io.Serializable;

/**
 * Represents any JQTI value object. Every JQTI value implementation must implement this interface.
 * <p>
 * This value can be single, multiple, ordered, record or NULL.
 * <p>
 * In JQTI+, all {@link Value}s are now immutable. (Some implementations were mutable in the
 * original JQTI.)
 * <p>
 * A NULL value is always represented by {@link NullValue}.
 *
 * @author Jiri Kajaba
 * @author David McKain (revised)
 */
public interface Value extends Serializable, Stringifiable {

    /**
     * Returns true if this value is NULL, false otherwise.
     */
    boolean isNull();

    /**
     * Returns cardinality of this value.
     * <p>
     * If this value is NULL then null is returned.
     *
     * @return the {@link Cardinality} of this value, or null if this
     * value is NULL.
     */
    Cardinality getCardinality();

    /**
     * Returns baseType of this value.
     * <p>
     * If value is NULL or record cardinality then null is returned.
     *
     * @return the {@link BaseType} of this value, or null if this value
     * is NULL or has record cardinality.
     */
    BaseType getBaseType();

    /**
     * Returns the computed {@link Signature} for this value.
     * <p>
     * If this value is NULL then null is returned.
     *
     * @return the {@link Signature} of this value, or null if this
     *   value is NULL
     */
    Signature getSignature();

    /**
     * Tests whether this value has the given {@link Cardinality}.
     *
     * @return true if this value is null, the requested cardinality
     * is not null and this value's cardinality equals the requested
     * cardinality. Otherwise returns false.
     *
     * @param cardinality cardinality to test
     */
    boolean hasCardinality(Cardinality cardinality);

    /**
     * Tests whether this value has the given {@link BaseType}.
     *
     * @return true if this value is null, the requested baseType
     * is not null and this value's baseType is not null and
     * equals the requested baseType. Otherwise returns false.
     *
     * @param baseType baseType to test
     */
    boolean hasBaseType(BaseType baseType);

    /**
     * Tests whether this value has the given {@link Signature}.
     *
     * @return true if this value is null, the requested signature
     * is not null and this value's computed {@link Signature} matches
     * the provided {@link Signature}. Otherwise returns false.
     *
     * @param baseType baseType to test
     */
    boolean hasSignature(Signature signature);

    /**
     * Returns whether this value is considered the "same" as the
     * other value, as defined in the QTI spec.
     * <p>
     * This method is new in JQTI+; the original JQTI used the
     * default {@link #equals(Object)} method for this.
     *
     * @param other value to compare with, which will not be
     *   Java null (but might be a {@link NullValue})
     */
    boolean qtiEquals(Value other);

    /**
     * Converts this value to a QTI-meaningful String.
     * This is new in JQTI+; the original JQTI used {@link #toString()} for that.
     */
    @Override
    String toQtiString();

    /**
     * In JQTI+, this returns a property dump of the Object, rather than
     * converting the value to a String as it did in JQTI.
     */
    @Override
    public String toString();
}
