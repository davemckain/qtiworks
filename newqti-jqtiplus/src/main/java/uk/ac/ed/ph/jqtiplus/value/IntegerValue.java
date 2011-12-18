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
 * Implementation of <code>BaseType</code> integer value.
 * <p>
 * An integer value is A whole number in the range [-2147483648, 2147483647].
 * <p>
 * Example values: 1, +3, -4.
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single and <code>BaseType</code> is always integer.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * 
 * @author Jiri Kajaba
 */
public class IntegerValue extends NumberValue
{
    private static final long serialVersionUID = 1L;
    
    private int intValue;

    /**
     * Constructs <code>IntegerValue</code> from given <code>int</code>.
     *
     * @param value <code>int</code>
     */
    public IntegerValue(int value)
    {
        this.intValue = value;
    }

    /**
     * Constructs <code>IntegerValue</code> from given <code>String</code> representation.
     *
     * @param value <code>String</code> representation of <code>IntegerValue</code>
     * @throws QTIParseException if <code>String</code> representation of <code>IntegerValue</code> is not valid
     */
    public IntegerValue(String value)
    {
        this.intValue = parseInteger(value);
    }

    /**
     * Constructs <code>IntegerValue</code> from given <code>String</code> representation and radix.
     *
     * @param value <code>String</code> representation of <code>IntegerValue</code>
     * @param radix Radix or base to use when interpreting value
     * @throws QTIParseException if <code>String</code> representation of <code>IntegerValue</code> is not valid
     */
    public IntegerValue(String value, Integer radix) {
        this.intValue = parseInteger(value, radix);
    }

    public BaseType getBaseType()
    {
        return BaseType.INTEGER;
    }

    @Override
    public int intValue()
    {
        return intValue;
    }

    @Override
    public double doubleValue()
    {
        return intValue;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null || !getClass().equals(object.getClass()))
            return false;

        IntegerValue value = (IntegerValue) object;

        return intValue == value.intValue;
    }

    @Override
    public int hashCode()
    {
        return intValue();
    }

    @Override
    public String toString()
    {
        return Integer.toString(intValue);
    }

    /**
     * Parses the <code>String</code> argument as A <code>int</code>.
     *
     * @param value <code>String</code> representation of <code>int</code>
     * @return parsed <code>int</code>
     * @throws QTIParseException if <code>String</code> representation of <code>int</code> is not valid
     */
    public static int parseInteger(String value)
    {
        return parseInteger(value, 10);
    }
    
    /**
     * Parses the <code>String</code> argument as A <code>int</code>.
     *
     * @param value <code>String</code> representation of <code>int</code>
     * @param radix base to use in conversion
     * @return parsed <code>int</code>
     * @throws QTIParseException if <code>String</code> representation of <code>int</code> is not valid
     */
    public static int parseInteger(String value, int radix)
    {
        if (value != null) 
            value = value.trim();
        
        if (value == null || value.length() == 0)
            throw new QTIParseException("Invalid integer '" + value + "'. Length is not valid.");

        String originalValue = value;

        // Removes + sign because of Integer.parseInt cannot handle it.
        if (value.startsWith("+"))
        {
            value = value.substring(1);
            if (value.length() == 0 || !Character.isDigit(value.codePointAt(0)))
                throw new QTIParseException("Invalid integer '" + originalValue + "'.");
        }

        try
        {
            return Integer.parseInt(value, radix);
        }
        catch (NumberFormatException ex)
        {
            throw new QTIParseException("Invalid integer '" + originalValue + "'.", ex);
        }
    }
}
