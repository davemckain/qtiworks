/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.value;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

/**
 * Implementation of <code>BaseType</code> point value.
 * <p>
 * A space separated list of two integer values. The first value is the horizontal component, the second
 * the vertical.
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
 * 
 * @author Jiri Kajaba
 */
public class PointValue extends SingleValue
{
    private static final long serialVersionUID = 1L;
    
    private int horizontalValue;
    private int verticalValue;

    /**
     * Constructs <code>PointValue</code> from given horizontal and vertical values.
     *
     * @param horizontalValue horizontal value
     * @param verticalValue vertical value
     */
    public PointValue(int horizontalValue, int verticalValue)
    {
        this.horizontalValue = horizontalValue;
        this.verticalValue = verticalValue;
    }

    /**
     * Constructs <code>PointValue</code> from given <code>String</code> representation.
     *
     * @param value <code>String</code> representation of <code>PointValue</code>
     * @throws QTIParseException if <code>String</code> representation of <code>PointValue</code> is not valid
     */
    public PointValue(String value)
    {
        if (value == null || value.length() == 0)
            throw new QTIParseException("Invalid point '" + value + "'. Length is not valid.");

        if (!value.equals(value.trim()))
            throw new QTIParseException("Invalid point '" + value + "'.");

        String[] parts = value.split(" ");
        if (parts.length != 2)
            throw new QTIParseException("Invalid point '" + value + "'. Number of parts is not valid.");

        try
        {
            this.horizontalValue = IntegerValue.parseInteger(parts[0]);
            this.verticalValue = IntegerValue.parseInteger(parts[1]);
        }
        catch (QTIParseException ex)
        {
            throw new QTIParseException("Invalid point '" + value + "'.", ex);
        }
    }

    public BaseType getBaseType()
    {
        return BaseType.POINT;
    }

    /**
     * Returns horizontal value of this point.
     *
     * @return horizontal value of this point
     */
    public int horizontalValue()
    {
        return horizontalValue;
    }

    /**
     * Returns vertical value of this point.
     *
     * @return vertical value of this point
     */
    public int verticalValue()
    {
        return verticalValue;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null || !getClass().equals(object.getClass()))
            return false;

        PointValue value = (PointValue) object;

        return horizontalValue == value.horizontalValue && verticalValue == value.verticalValue;
    }

    @Override
    public int hashCode()
    {
        return horizontalValue + verticalValue;
    }

    @Override
    public String toString()
    {
        return horizontalValue + " " + verticalValue;
    }
}
