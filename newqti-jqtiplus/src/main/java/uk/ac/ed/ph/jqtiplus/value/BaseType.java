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

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class describes single data types.
 * <p>
 * Supported single data types: identifier, boolean, integer, float, string, point, pair, directedPair, duration, file, uri.
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.IdentifierValue
 * @see uk.ac.ed.ph.jqtiplus.value.BooleanValue
 * @see uk.ac.ed.ph.jqtiplus.value.IntegerValue
 * @see uk.ac.ed.ph.jqtiplus.value.FloatValue
 * @see uk.ac.ed.ph.jqtiplus.value.StringValue
 * @see uk.ac.ed.ph.jqtiplus.value.PointValue
 * @see uk.ac.ed.ph.jqtiplus.value.PairValue
 * @see uk.ac.ed.ph.jqtiplus.value.DirectedPairValue
 * @see uk.ac.ed.ph.jqtiplus.value.DurationValue
 * @see uk.ac.ed.ph.jqtiplus.value.FileValue
 * @see uk.ac.ed.ph.jqtiplus.value.UriValue
 * @author Jiri Kajaba
 */
public enum BaseType {
    /**
     * Identifier baseType.
     * 
     * @see IdentifierValue
     */
    IDENTIFIER("identifier") {

        @Override
        public SingleValue parseSingleValue(String singleValue) {
            return new IdentifierValue(singleValue);
        }
    },

    /**
     * Boolean baseType.
     * 
     * @see BooleanValue
     */
    BOOLEAN("boolean") {

        @Override
        public SingleValue parseSingleValue(String singleValue) {
            return BooleanValue.valueOf(singleValue);
        }
    },

    /**
     * Integer baseType.
     * 
     * @see IntegerValue
     */
    INTEGER("integer") {

        @Override
        public SingleValue parseSingleValue(String singleValue) {
            return new IntegerValue(singleValue);
        }
    },

    /**
     * Float baseType.
     * 
     * @see FloatValue
     */
    FLOAT("float") {

        @Override
        public SingleValue parseSingleValue(String singleValue) {
            return new FloatValue(singleValue);
        }
    },

    /**
     * String baseType.
     * 
     * @see StringValue
     */
    STRING("string") {

        @Override
        public SingleValue parseSingleValue(String singleValue) {
            if (singleValue == null || singleValue.length() == 0) {
                return null;
            }
            return new StringValue(singleValue);
        }
    },

    /**
     * Point baseType.
     * 
     * @see PointValue
     */
    POINT("point") {

        @Override
        public SingleValue parseSingleValue(String singleValue) {
            return new PointValue(singleValue);
        }
    },

    /**
     * Pair baseType.
     * 
     * @see PairValue
     */
    PAIR("pair") {

        @Override
        public SingleValue parseSingleValue(String singleValue) {
            return new PairValue(singleValue);
        }
    },

    /**
     * DirectedPair baseType.
     * 
     * @see DirectedPairValue
     */
    DIRECTED_PAIR("directedPair") {

        @Override
        public SingleValue parseSingleValue(String singleValue) {
            return new DirectedPairValue(singleValue);
        }
    },

    /**
     * Duration baseType.
     * 
     * @see DurationValue
     */
    DURATION("duration") {

        @Override
        public SingleValue parseSingleValue(String singleValue) {
            return new DurationValue(singleValue);
        }
    },

    /**
     * File baseType.
     * 
     * @see FileValue
     */
    FILE("file") {

        @Override
        public SingleValue parseSingleValue(String singleValue) {
            return new FileValue(singleValue);
        }
    },

    /**
     * URI baseType.
     * 
     * @see UriValue
     */
    URI("uri") {

        @Override
        public SingleValue parseSingleValue(String singleValue) {
            return new UriValue(singleValue);
        }
    };

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "baseType";

    private static Map<String, BaseType> baseTypes;

    static {
        baseTypes = new HashMap<String, BaseType>();

        for (final BaseType baseType : BaseType.values()) {
            baseTypes.put(baseType.baseType, baseType);
        }
    }

    private String baseType;

    private BaseType(String baseType) {
        this.baseType = baseType;
    }

    /**
     * Returns true if this baseType is identifier; false otherwise.
     * 
     * @return true if this baseType is identifier; false otherwise
     */
    public boolean isIdentifier() {
        return this == IDENTIFIER;
    }

    /**
     * Returns true if this baseType is boolean; false otherwise.
     * 
     * @return true if this baseType is boolean; false otherwise
     */
    public boolean isBoolean() {
        return this == BOOLEAN;
    }

    /**
     * Returns true if this baseType is integer or float; false otherwise.
     * 
     * @return true if this baseType is integer or float; false otherwise
     */
    public boolean isNumeric() {
        return this == INTEGER || this == FLOAT;
    }

    /**
     * Returns true if this baseType is integer; false otherwise.
     * 
     * @return true if this baseType is integer; false otherwise
     */
    public boolean isInteger() {
        return this == INTEGER;
    }

    /**
     * Returns true if this baseType is float; false otherwise.
     * 
     * @return true if this baseType is float; false otherwise
     */
    public boolean isFloat() {
        return this == FLOAT;
    }

    /**
     * Returns true if this baseType is string; false otherwise.
     * 
     * @return true if this baseType is string; false otherwise
     */
    public boolean isString() {
        return this == STRING;
    }

    /**
     * Returns true if this baseType is point; false otherwise.
     * 
     * @return true if this baseType is point; false otherwise
     */
    public boolean isPoint() {
        return this == POINT;
    }

    /**
     * Returns true if this baseType is pair; false otherwise.
     * 
     * @return true if this baseType is pair; false otherwise
     */
    public boolean isPair() {
        return this == PAIR;
    }

    /**
     * Returns true if this baseType is directedPair; false otherwise.
     * 
     * @return true if this baseType is directedPair; false otherwise
     */
    public boolean isDirectedPair() {
        return this == DIRECTED_PAIR;
    }

    /**
     * Returns true if this baseType is duration; false otherwise.
     * 
     * @return true if this baseType is duration; false otherwise
     */
    public boolean isDuration() {
        return this == DURATION;
    }

    /**
     * Returns true if this baseType is file; false otherwise.
     * 
     * @return true if this baseType is file; false otherwise
     */
    public boolean isFile() {
        return this == FILE;
    }

    /**
     * Returns true if this baseType is uri; false otherwise.
     * 
     * @return true if this baseType is uri; false otherwise
     */
    public boolean isUri() {
        return this == URI;
    }

    /**
     * Parses the <code>String</code> argument as A <code>SingleValue</code>.
     * 
     * @param singleValue <code>String</code> representation of <code>SingleValue</code>
     * @return parsed <code>SingleValue</code>
     * @throws QTIParseException if <code>String</code> representation of <code>SingleValue</code> is not valid
     */
    public abstract SingleValue parseSingleValue(String singleValue);

    @Override
    public String toString() {
        return baseType;
    }

    /**
     * Returns parsed <code>BaseType</code> from given <code>String</code>.
     * 
     * @param baseType <code>String</code> representation of <code>BaseType</code>
     * @return parsed <code>BaseType</code> from given <code>String</code>
     * @throws QTIParseException if given <code>String</code> is not valid <code>BaseType</code>
     */
    public static BaseType parseBaseType(String baseType) {
        final BaseType result = baseTypes.get(baseType);

        if (result == null) {
            throw new QTIParseException("Invalid " + QTI_CLASS_NAME + " '" + baseType + "'.");
        }

        return result;
    }

    /**
     * Returns all supported baseTypes except of baseTypes in given parameter.
     * Result is baseType.values() - exclude.
     * 
     * @param exclude excluded baseTypes
     * @return all supported baseTypes except of baseTypes in given parameter
     */
    public static BaseType[] values(BaseType[] exclude) {
        final List<BaseType> baseTypes = new ArrayList<BaseType>();

        for (final BaseType baseType : BaseType.values()) {
            if (Arrays.binarySearch(exclude, baseType) < 0) {
                baseTypes.add(baseType);
            }
        }

        return baseTypes.toArray(new BaseType[] {});
    }

    /**
     * Returns intersection of two given baseTypes sets (order is not important).
     * 
     * @param firstSet first set of baseTypes
     * @param secondSet second set of baseType
     * @return intersection of two given baseTypes sets
     */
    public static BaseType[] intersection(BaseType[] firstSet, BaseType[] secondSet) {
        final List<BaseType> baseTypes = new ArrayList<BaseType>();

        for (final BaseType baseType : firstSet) {
            if (Arrays.binarySearch(secondSet, baseType) >= 0) {
                baseTypes.add(baseType);
            }
        }

        return baseTypes.toArray(new BaseType[] {});
    }
}
