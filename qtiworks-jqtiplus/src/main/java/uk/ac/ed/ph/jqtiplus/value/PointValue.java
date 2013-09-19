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
 * Implementation of <code>BaseType</code> point value.
 * <p>
 * A space separated list of two integer values. The first value is the horizontal component, the second the vertical.
 * <p>
 * Example values: '10 50', '32 16'. Character ' is not part of point value.
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single and <code>BaseType</code> is always point.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @see uk.ac.ed.ph.jqtiplus.value.IntegerValue
 * @author Jiri Kajaba
 */
public final class PointValue extends SingleValue {

    private static final long serialVersionUID = -4496855150070341627L;

    public static PointValue parseString(final String string) {
        final int[] coords = DataTypeBinder.parsePoint(string);
        return new PointValue(coords[0], coords[1]);
    }

    private final int horizontalValue;
    private final int verticalValue;

    public PointValue(final int horizontalValue, final int verticalValue) {
        this.horizontalValue = horizontalValue;
        this.verticalValue = verticalValue;
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.POINT;
    }

    /**
     * Returns horizontal value of this point.
     *
     * @return horizontal value of this point
     */
    public int horizontalValue() {
        return horizontalValue;
    }

    /**
     * Returns vertical value of this point.
     *
     * @return vertical value of this point
     */
    public int verticalValue() {
        return verticalValue;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof PointValue)) {
            return false;
        }

        final PointValue other = (PointValue) object;
        return horizontalValue == other.horizontalValue
                && verticalValue == other.verticalValue;
    }

    @Override
    public int hashCode() {
        return toQtiString().hashCode();
    }

    @Override
    public String toQtiString() {
        return DataTypeBinder.toString(horizontalValue, verticalValue);
    }
}
