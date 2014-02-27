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
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class describes paramType of a param. Supported paramTypes are:
 * <p>
 * DATA
 * <p>
 * REF
 * 
 * @author Jiri Kajaba
 */
public enum ParamType implements Stringifiable {
    
    /**
     * DATA type.
     */
    DATA("DATA"),

    /**
     * REF type.
     */
    REF("REF");

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "paramType";

    private static Map<String, ParamType> types;

    static {
        types = new HashMap<String, ParamType>();

        for (final ParamType type : ParamType.values()) {
            types.put(type.paramType, type);
        }
    }

    private String paramType;

    private ParamType(String paramType) {
        this.paramType = paramType;
    }

    /**
     * Returns true if this paramType is DATA; false otherwise.
     * 
     * @return true if this paramType is DATA; false otherwise
     */
    public boolean isData() {
        return this == DATA;
    }

    /**
     * Returns true if this paramType is REF; false otherwise.
     * 
     * @return true if this paramType is REF; false otherwise
     */
    public boolean isRef() {
        return this == REF;
    }

    @Override
    public String toQtiString() {
        return paramType;
    }

    /**
     * Returns parsed <code>ParamType</code> from given <code>String</code>.
     * 
     * @param paramType <code>String</code> representation of <code>ParamType</code>
     * @return parsed <code>ParamType</code> from given <code>String</code>
     * @throws QtiParseException if given <code>String</code> is not valid <code>ParamType</code>
     */
    public static ParamType parseParamType(String paramType) {
        final ParamType result = types.get(paramType);

        if (result == null) {
            throw new QtiParseException("Invalid " + QTI_CLASS_NAME + " '" + paramType + "'.");
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
    public static ParamType[] intersection(ParamType[] firstSet, ParamType[] secondSet) {
        final List<ParamType> paramTypes = new ArrayList<ParamType>();

        for (final ParamType type : firstSet) {
            if (Arrays.binarySearch(secondSet, type) >= 0) {
                paramTypes.add(type);
            }
        }

        return paramTypes.toArray(new ParamType[] {});
    }
}
