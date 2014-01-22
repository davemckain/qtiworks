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

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.types.DataTypeBinder;

/**
 * Implementation of <code>BaseType</code> duration value.
 * <p>
 * The number of seconds, expressed as a floating point number and bound according to the rules for the simple type float.
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single and <code>BaseType</code> is always duration.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @see uk.ac.ed.ph.jqtiplus.value.FloatValue
 * @author Jiri Kajaba
 */
public final class DurationValue extends SingleValue {

    private static final long serialVersionUID = 2662972970373655606L;

    private double doubleValue;

    public DurationValue(final double value) throws IllegalArgumentException {
        if (value < 0) {
            throw new IllegalArgumentException("Invalid duration '" + value + "'. Duration cannot be negative.");
        }
        this.doubleValue = value;
    }

    /**
     * Constructs <code>DurationValue</code> from given <code>String</code> representation.
     *
     * @param value <code>String</code> representation of <code>DurationValue</code>
     * @throws QtiParseException if <code>String</code> representation of <code>DurationValue</code> is not valid
     */
    public DurationValue(final String value) {
        try {
            this.doubleValue = DataTypeBinder.parseFloat(value);
            if (doubleValue < 0) {
                throw new QtiParseException("Invalid duration '" + value + "'. Duration cannot be negative.");
            }
        }
        catch (final QtiParseException ex) {
            throw new QtiParseException("Invalid duration '" + value + "'.", ex);
        }
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.DURATION;
    }

    /**
     * Returns the value of this <code>DurationValue</code> as A <code>double</code>.
     *
     * @return the value of this <code>DurationValue</code> as A <code>double</code>
     */
    public double doubleValue() {
        return doubleValue;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof DurationValue)) {
            return false;
        }

        final DurationValue other = (DurationValue) object;
        return doubleValue == other.doubleValue;
    }

    @Override
    public int hashCode() {
        return Double.valueOf(doubleValue).hashCode();
    }

    @Override
    public String toQtiString() {
        return Double.toString(doubleValue);
    }
}
