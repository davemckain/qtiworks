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
 * This class describes cardinality of value. Supported cardinalities are:
 * <p>
 * single - one value
 * <p>
 * multiple - unordered list of values of same type
 * <p>
 * ordered - ordered list of values of same type
 * <p>
 * record - unordered list of values of different types
 *
 * @author Jiri Kajaba
 */
public enum Cardinality implements Stringifiable {
    /**
     * One single value.
     */
    SINGLE("single"),

    /**
     * Set of single values (order is not important).
     */
    MULTIPLE("multiple"),

    /**
     * Ordered list of single values (order is important).
     */
    ORDERED("ordered"),

    /**
     * Set of (key, value) pairs (order is not important).
     * Key must be an identifier. Value can be any baseType.
     */
    RECORD("record");

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "cardinality";

    private static Map<String, Cardinality> cardinalities;

    static {
        cardinalities = new HashMap<String, Cardinality>();

        for (final Cardinality cardinality : Cardinality.values()) {
            cardinalities.put(cardinality.cardinality, cardinality);
        }
    }

    private String cardinality;

    private Cardinality(final String cardinality) {
        this.cardinality = cardinality;
    }

    /**
     * Returns true if this cardinality is single; false otherwise.
     *
     * @return true if this cardinality is single; false otherwise
     */
    public boolean isSingle() {
        return this == SINGLE;
    }

    /**
     * Returns true if this cardinality is multiple or ordered; false otherwise.
     *
     * @return true if this cardinality is multiple or ordered; false otherwise
     */
    public boolean isList() {
        return this == MULTIPLE || this == ORDERED;
    }

    /**
     * Returns true if this cardinality is multiple; false otherwise.
     *
     * @return true if this cardinality is multiple; false otherwise
     */
    public boolean isMultiple() {
        return this == MULTIPLE;
    }

    /**
     * Returns true if this cardinality is ordered; false otherwise.
     *
     * @return true if this cardinality is ordered; false otherwise
     */
    public boolean isOrdered() {
        return this == ORDERED;
    }

    /**
     * Returns true if this cardinality is record; false otherwise.
     *
     * @return true if this cardinality is record; false otherwise
     */
    public boolean isRecord() {
        return this == RECORD;
    }

    public boolean isOneOf(final Cardinality... baseTypes) {
        for (final Cardinality other : baseTypes) {
            if (this==other) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toQtiString() {
        return cardinality;
    }

    /**
     * Returns parsed <code>Cardinality</code> from given <code>String</code>.
     *
     * @param cardinality <code>String</code> representation of <code>Cardinality</code>
     * @return parsed <code>Cardinality</code> from given <code>String</code>
     * @throws QtiParseException if given <code>String</code> is not valid <code>Cardinality</code>
     */
    public static Cardinality parseCardinality(final String cardinality) {
        final Cardinality result = cardinalities.get(cardinality);

        if (result == null) {
            throw new QtiParseException("Invalid " + QTI_CLASS_NAME + " '" + cardinality + "'.");
        }

        return result;
    }

    /**
     * Returns intersection of two given cardinalities sets (order is not important).
     *
     * @param firstSet first set of cardinalities
     * @param secondSet second set of cardinalities
     * @return intersection of two given cardinalities sets
     */
    public static Cardinality[] intersection(final Cardinality[] firstSet, final Cardinality[] secondSet) {
        final List<Cardinality> result = new ArrayList<Cardinality>();

        for (final Cardinality cardinality : firstSet) {
            if (Arrays.binarySearch(secondSet, cardinality) >= 0) {
                result.add(cardinality);
            }
        }

        return result.toArray(new Cardinality[] {});
    }
}
