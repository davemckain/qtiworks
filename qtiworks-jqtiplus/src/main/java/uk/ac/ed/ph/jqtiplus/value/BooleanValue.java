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
public final class BooleanValue extends SingleValue {

    private static final long serialVersionUID = -5150274870390179580L;

    public static final BooleanValue FALSE = new BooleanValue(false);
    public static final BooleanValue TRUE = new BooleanValue(true);
    public static final BooleanValue[] values = { BooleanValue.FALSE, BooleanValue.TRUE };

    public static BooleanValue valueOf(final boolean value) {
        return value ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

    public static BooleanValue valueOf(final String value) {
        return valueOf(DataTypeBinder.parseBoolean(value));
    }

    private final boolean booleanValue;

    /** (Private constructor - use static factory methods instead) */
    private BooleanValue(final boolean value) {
        this.booleanValue = value;
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.BOOLEAN;
    }

    public boolean booleanValue() {
        return booleanValue;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof BooleanValue)) {
            return false;
        }
        final BooleanValue other = (BooleanValue) object;
        return booleanValue == other.booleanValue;
    }

    @Override
    public int hashCode() {
        return booleanValue ? 1 : 0;
    }

    @Override
    public String toQtiString() {
        return DataTypeBinder.toString(booleanValue);
    }
}
