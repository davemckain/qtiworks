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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class describes paramType of A param. Supported paramTypes are:
 * <p>
 * DATA
 * <p>
 * REF
 * 
 * @author Jiri Kajaba
 */
public enum ParamType
{
    /**
     * DATA type.
     */
    DATA ("DATA"),

    /**
     * REF type.
     */
    REF ("REF");

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "paramType";

    private static Map<String, ParamType> types;

    static
    {
        types = new HashMap<String, ParamType>();

        for (ParamType type : ParamType.values())
            types.put(type.paramType, type);
    }

    private String paramType;

    private ParamType(String paramType)
    {
        this.paramType = paramType;
    }

    /**
     * Returns true if this paramType is DATA; false otherwise.
     *
     * @return true if this paramType is DATA; false otherwise
     */
    public boolean isData()
    {
        return this == DATA;
    }

    /**
     * Returns true if this paramType is REF; false otherwise.
     *
     * @return true if this paramType is REF; false otherwise
     */
    public boolean isRef()
    {
        return this == REF;
    }

    @Override
    public String toString()
    {
        return paramType;
    }

    /**
     * Returns parsed <code>ParamType</code> from given <code>String</code>.
     *
     * @param paramType <code>String</code> representation of <code>ParamType</code>
     * @return parsed <code>ParamType</code> from given <code>String</code>
     * @throws QTIParseException if given <code>String</code> is not valid <code>ParamType</code>
     */
    public static ParamType parseParamType(String paramType)
    {
        ParamType result = types.get(paramType);

        if (result == null) {
            throw new QTIParseException("Invalid " + CLASS_TAG + " '" + paramType + "'.");
        }

        return result;
    }

    /**
     * Returns intersection of two given types sets (order is not important).
     *
     * @param firstSet first set of types
     * @param secondSet second set of types
     * @return intersection of two given types sets
     */
    public static ParamType[] intersection(ParamType[] firstSet, ParamType[] secondSet)
    {
        List<ParamType> paramTypes = new ArrayList<ParamType>();

        for (ParamType type : firstSet)
        {
            if (Arrays.binarySearch(secondSet, type) >= 0)
                paramTypes.add(type);
        }

        return paramTypes.toArray(new ParamType[] {});
    }
}
