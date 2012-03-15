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
 * Implementation of <code>BaseType</code> boolean value.
 * <p>
 * Legal <code>String</code> representations of boolean value are: true, 1, false, 0.
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single and <code>BaseType</code> is always boolean.
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public class BooleanValue extends SingleValue {

    private static final long serialVersionUID = -5150274870390179580L;

    public static final BooleanValue FALSE = new BooleanValue(false);

    public static final BooleanValue TRUE = new BooleanValue(true);

    public static BooleanValue valueOf(boolean value) {
        return value ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

    public static BooleanValue valueOf(String value) {
        return valueOf(parseBoolean(value));
    }

    private final boolean booleanValue;

    /**
     * Constructs <code>BooleanValue</code> from given <code>boolean</code>.
     * 
     * @param value <code>boolean</code>
     */
    private BooleanValue(boolean value) {
        this.booleanValue = value;
    }

    /**
     * Constructs <code>BooleanValue</code> from given <code>String</code> representation.
     * 
     * @param value <code>String</code> representation of <code>BooleanValue</code>
     * @throws QtiParseException if <code>String</code> representation of <code>BooleanValue</code> is not valid
     */
    private BooleanValue(String value) {
        this.booleanValue = parseBoolean(value);
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.BOOLEAN;
    }

    /**
     * Returns the value of this <code>BooleanValue</code> as A <code>boolean</code>.
     * 
     * @return the value of this <code>BooleanValue</code> as A <code>boolean</code>
     */
    public boolean booleanValue() {
        return booleanValue;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !getClass().equals(object.getClass())) {
            return false;
        }

        final BooleanValue value = (BooleanValue) object;

        return booleanValue == value.booleanValue;
    }

    @Override
    public int hashCode() {
        return booleanValue ? 1 : 0;
    }

    @Override
    public String toString() {
        return Boolean.toString(booleanValue);
    }

    /**
     * Parses the <code>String</code> argument as A <code>boolean</code>.
     * 
     * @param value <code>String</code> representation of <code>boolean</code>
     * @return parsed <code>boolean</code>
     * @throws QtiParseException if <code>String</code> representation of <code>boolean</code> is not valid
     */
    public static boolean parseBoolean(String value) {
        if (value != null) {
            value = value.trim();
        }

        if (value == null || value.length() == 0) {
            throw new QtiParseException("Invalid boolean '" + value + "'. Length is not valid.");
        }

        if (value.equals("true") || value.equals("1")) {
            return true;
        }
        else if (value.equals("false") || value.equals("0")) {
            return false;
        }
        else {
            throw new QtiParseException("Invalid boolean '" + value + "'.");
        }
    }
}
