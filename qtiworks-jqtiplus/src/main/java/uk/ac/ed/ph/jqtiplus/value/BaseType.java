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
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

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
public enum BaseType implements Stringifiable {

    /**
     * Identifier baseType.
     *
     * @see IdentifierValue
     */
    IDENTIFIER("identifier") {

        @Override
        public SingleValue parseSingleValue(final String string) {
            return new IdentifierValue(Identifier.parseString(string.trim()));
        }
    },

    /**
     * Boolean baseType.
     *
     * @see BooleanValue
     */
    BOOLEAN("boolean") {

        @Override
        public SingleValue parseSingleValue(final String string) {
            return BooleanValue.valueOf(string.trim());
        }
    },

    /**
     * Integer baseType.
     *
     * @see IntegerValue
     */
    INTEGER("integer") {

        @Override
        public SingleValue parseSingleValue(final String string) {
            return IntegerValue.parseString(string);
        }
    },

    /**
     * Float baseType.
     *
     * @see FloatValue
     */
    FLOAT("float") {

        @Override
        public SingleValue parseSingleValue(final String string) {
            return new FloatValue(string);
        }
    },

    /**
     * String baseType.
     *
     * @see StringValue
     */
    STRING("string") {

        @Override
        public SingleValue parseSingleValue(final String string) {
            return new StringValue(string);
        }
    },

    /**
     * Point baseType.
     *
     * @see PointValue
     */
    POINT("point") {

        @Override
        public SingleValue parseSingleValue(final String string) {
            return PointValue.parseString(string);
        }
    },

    /**
     * Pair baseType.
     *
     * @see PairValue
     */
    PAIR("pair") {

        @Override
        public SingleValue parseSingleValue(final String string) {
            return PairValue.parseString(string);
        }
    },

    /**
     * DirectedPair baseType.
     *
     * @see DirectedPairValue
     */
    DIRECTED_PAIR("directedPair") {

        @Override
        public SingleValue parseSingleValue(final String string) {
            return DirectedPairValue.parseString(string);
        }
    },

    /**
     * Duration baseType.
     *
     * @see DurationValue
     */
    DURATION("duration") {

        @Override
        public SingleValue parseSingleValue(final String string) {
            return new DurationValue(string);
        }
    },

    /**
     * File baseType.
     *
     * @see FileValue
     */
    FILE("file") {

        @Override
        public SingleValue parseSingleValue(final String string) {
            throw new IllegalStateException("Values of baseType file cannot be instantiated from a string");
        }
    },

    /**
     * URI baseType.
     *
     * @see UriValue
     */
    URI("uri") {

        @Override
        public SingleValue parseSingleValue(final String string) {
            return new UriValue(string);
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

    private BaseType(final String baseType) {
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

    public boolean isOneOf(final BaseType... testBaseTypes) {
        for (final BaseType other : testBaseTypes) {
            if (this==other) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses the <code>String</code> argument to yield a <code>SingleValue</code> of this BaseType.
     *
     * @param string <code>String</code> representation of <code>SingleValue</code>, which must
     *   not be null.
     * @return resulting <code>SingleValue</code>, which will not be null
     *
     * @throws IllegalArgumentException if string is null
     * @throws QtiParseException if <code>String</code> representation of <code>SingleValue</code> is not valid
     *   for this BaseType.
     *
     * @see #parseSingleValueLax(String)
     */
    public abstract SingleValue parseSingleValue(String string);

    /**
     * Parses the <code>String</code> argument to yield a <code>SingleValue</code> of this BaseType,
     * after first trimming leading and trailing whitespace from the string for non-STRING BaseTypes.
     * <p>
     * This is new in JQTI+. The original JQTI always trimmed strings before the {@link #parseSingleValue(String)}
     * method was called, which could accidentally mutate the resulting value for STRING baseTypes. Hence the
     * addition of this method to make it clearer what is happening.
     *
     * @param string <code>String</code> representation of <code>SingleValue</code>, which must
     *   not be null.
     * @return resulting <code>SingleValue</code>, which will not be null
     *
     * @throws IllegalArgumentException if string is null
     * @throws QtiParseException if <code>String</code> representation of <code>SingleValue</code> is not valid
     *   for this BaseType.
     *
     * @see #parseSingleValue(String)
     */
    public SingleValue parseSingleValueLax(final String string) {
        Assert.notNull(string, "string");
        return parseSingleValue(isString() ? string : string.trim());
    }

    @Override
    public String toQtiString() {
        return baseType;
    }

    /**
     * Returns parsed <code>BaseType</code> from given <code>String</code>.
     *
     * @param baseType <code>String</code> representation of <code>BaseType</code>,
     *   which must not be null
     * @return parsed <code>BaseType</code> from given <code>String</code>
     *
     * @throws IllegalArgumentException if baseType is null
     * @throws QtiParseException if given <code>String</code> is not valid <code>BaseType</code>
     */
    public static BaseType parseBaseType(final String baseType) {
        Assert.notNull(baseType, "baseType");
        final BaseType result = baseTypes.get(baseType);
        if (result==null) {
            throw new QtiParseException("Invalid " + QTI_CLASS_NAME + " '" + baseType + "'.");
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
    public static BaseType[] except(final BaseType[] exclude) {
        final List<BaseType> result = new ArrayList<BaseType>();

        for (final BaseType baseType : BaseType.values()) {
            if (Arrays.binarySearch(exclude, baseType) < 0) {
                result.add(baseType);
            }
        }
        return result.toArray(new BaseType[] {});
    }

    /**
     * Returns intersection of two given baseTypes sets (order is not important).
     *
     * @param firstSet first set of baseTypes
     * @param secondSet second set of baseType
     * @return intersection of two given baseTypes sets
     */
    public static BaseType[] intersection(final BaseType[] firstSet, final BaseType[] secondSet) {
        final List<BaseType> result = new ArrayList<BaseType>();

        for (final BaseType baseType : firstSet) {
            if (Arrays.binarySearch(secondSet, baseType) >= 0) {
                result.add(baseType);
            }
        }
        return result.toArray(new BaseType[] {});
    }
}
