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
package uk.ac.ed.ph.jqtiplus.value;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;

/**
 * Implementation of <code>BaseType</code> float value.
 * <p>
 * Float data type is defined as the IEEE double-precision 64-bit floating point type.
 * <p>
 * Example values: 1, 3.14, +3.14, -3.14, 3E+08, +3E+08, -3E+08, 3E-08, +3E-08, -3E-08.
 * <p>
 * See <A href="http://www.w3.org/TR/xmlschema-2/#double">XML</A> for more more accurately definition.
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single and <code>BaseType</code> is always float.
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class FloatValue extends NumberValue {

    private static final long serialVersionUID = 3799071457858594877L;

    private final double doubleValue;

    /**
     * Constructs <code>FloatValue</code> from given <code>double</code>.
     * 
     * @param value <code>double</code>
     */
    public FloatValue(double value) {
        this.doubleValue = value;
    }

    /**
     * Constructs <code>FloatValue</code> from given <code>String</code> representation.
     * 
     * @param value <code>String</code> representation of <code>FloatValue</code>
     * @throws QtiParseException if <code>String</code> representation of <code>FloatValue</code> is not valid
     */
    public FloatValue(String value) {
        this.doubleValue = parseFloat(value);
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.FLOAT;
    }

    @Override
    public int intValue() {
        return (int) doubleValue;
    }

    @Override
    public double doubleValue() {
        return doubleValue;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FloatValue)) {
            return false;
        }
        
        FloatValue other = (FloatValue) object;
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

    /**
     * Parses the <code>String</code> argument as A <code>double</code>.
     * 
     * @param value <code>String</code> representation of <code>double</code>
     * @return parsed <code>double</code>
     * @throws QtiParseException if <code>String</code> representation of <code>double</code> is not valid
     */
    public static double parseFloat(String value) {
        if (value != null) {
            value = value.trim();
        }

        if (value == null || value.length() == 0) {
            throw new QtiParseException("Invalid float '" + value + "'. Length is not valid.");
        }

        double result;
        if (value.equals("INF")) {
            result = Double.POSITIVE_INFINITY;
        }
        else if (value.equals("-INF")) {
            result = Double.NEGATIVE_INFINITY;
        }
        else if (value.equals("NaN")) {
            result = Double.NaN;
        }
        else {
            try {
                result = Double.parseDouble(value);
            }
            catch (final NumberFormatException ex) {
                throw new QtiParseException("Invalid float '" + value + "'.", ex);
            }
        }
        return result;
    }
}
