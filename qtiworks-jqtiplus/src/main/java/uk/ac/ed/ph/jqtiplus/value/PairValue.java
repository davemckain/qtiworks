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

import uk.ac.ed.ph.jqtiplus.internal.util.Pair;
import uk.ac.ed.ph.jqtiplus.types.DataTypeBinder;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

/**
 * Implementation of <code>BaseType</code> pair value.
 * <p>
 * A space separated list of two identifier values.
 * <p>
 * Example values: 'A B', 'choice5 choice1', 'apple pear', 'carrot potato'. Character ' is not part of pair value.
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single and <code>BaseType</code> is always pair.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @see uk.ac.ed.ph.jqtiplus.value.IdentifierValue
 * @author Jiri Kajaba
 */
public final class PairValue extends AbstractPairValue {

    private static final long serialVersionUID = -9157898996344626699L;

    public static PairValue parseString(final String value) {
        final Pair<Identifier, Identifier> parsed = DataTypeBinder.parsePair(value);
        return new PairValue(parsed.getFirst(), parsed.getSecond());
    }

    public PairValue(final Identifier sourceValue, final Identifier destValue) {
        super(sourceValue, destValue);
    }

    public PairValue(final String sourceValue, final String destValue) {
        super(sourceValue, destValue);
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.PAIR;
    }

    @Override
    public boolean isDirected() {
        return false;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof PairValue)) {
            return false;
        }

        final PairValue other = (PairValue) object;
        return sourceValue.equals(other.sourceValue) && destValue.equals(other.destValue) ||
                sourceValue.equals(other.destValue) && destValue.equals(other.sourceValue);
    }

    @Override
    public final int hashCode() {
        /* (hashCode() needs to reflect unordered pairing, so let's add hashCodes() of both elements) */
        return sourceValue.hashCode() + destValue.hashCode();
    }
}
