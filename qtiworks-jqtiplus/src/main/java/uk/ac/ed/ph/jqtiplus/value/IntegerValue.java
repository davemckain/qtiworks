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

import uk.ac.ed.ph.jqtiplus.types.DataTypeBinder;

/**
 * Implementation of <code>BaseType</code> integer value.
 * <p>
 * An integer value is a whole number in the range [-2147483648, 2147483647].
 * <p>
 * Example values: 1, +3, -4.
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single and <code>BaseType</code> is always integer.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class IntegerValue extends NumberValue {

    private static final long serialVersionUID = -2229184387480773991L;

    public static final IntegerValue ZERO = new IntegerValue(0);

    public static IntegerValue parseString(final String value) {
        return new IntegerValue(DataTypeBinder.parseInteger(value));
    }

    public static IntegerValue parseString(final String value, final int radix) {
        return new IntegerValue(DataTypeBinder.parseInteger(value, radix));
    }

    private final int intValue;

    public IntegerValue(final int value) {
        this.intValue = value;
    }

    public IntegerValue(final Integer integerValue) {
        this.intValue = integerValue.intValue();
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.INTEGER;
    }

    @Override
    public int intValue() {
        return intValue;
    }

    @Override
    public double doubleValue() {
        return intValue;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof IntegerValue)) {
            return false;
        }

        final IntegerValue other = (IntegerValue) object;
        return intValue == other.intValue;
    }

    @Override
    public int hashCode() {
        return intValue();
    }

    @Override
    public String toQtiString() {
        return DataTypeBinder.toString(intValue);
    }
}
