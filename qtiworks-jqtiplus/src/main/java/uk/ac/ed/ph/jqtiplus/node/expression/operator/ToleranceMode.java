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
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for equal expression.
 *
 * @see Equal
 * @author Jiri Kajaba
 */
public enum ToleranceMode implements Stringifiable {
    /**
     * Exact comparing. No tolerances are needed.
     */
    EXACT("exact") {

        @Override
        public boolean isEqual(final double firstNumber, final double secondNumber,
                final double tolerance1, final double tolerance2,
                final boolean includeLowerBound, final boolean includeUpperBound) {
            return firstNumber == secondNumber;
        }
    },

    /**
     * In absolute mode the result of the comparison is true if the value of the second expression, y is within the
     * following range defined by the first value, x.
     * <p>
     * x - t0, x + t1
     */
    ABSOLUTE("absolute") {

        @Override
        public boolean isEqual(final double firstNumber, final double secondNumber,
                final double tolerance1, final double tolerance2,
                final boolean includeLowerBound, final boolean includeUpperBound) {
            final double lower = firstNumber - tolerance1;

            if (includeLowerBound && secondNumber < lower ||
                    !includeLowerBound && secondNumber <= lower) {
                return false;
            }

            final double upper = firstNumber + tolerance2;

            if (includeUpperBound && secondNumber > upper ||
                    !includeUpperBound && secondNumber >= upper) {
                return false;
            }

            return true;
        }
    },

    /**
     * In relative mode, t0 and t1 are treated as percentages and the following range is used instead.
     * <p>
     * x * (1 - t0 / 100), x * (1 + t1 / 100)
     */
    RELATIVE("relative") {

        @Override
        public boolean isEqual(final double firstNumber, final double secondNumber,
                final double tolerance1, final double tolerance2,
                final boolean includeLowerBound, final boolean includeUpperBound) {
            final double lower = firstNumber * (1 - tolerance1 / 100);

            if (includeLowerBound && secondNumber < lower ||
                    !includeLowerBound && secondNumber <= lower) {
                return false;
            }

            final double upper = firstNumber * (1 + tolerance2 / 100);

            if (includeUpperBound && secondNumber > upper ||
                    !includeUpperBound && secondNumber >= upper) {
                return false;
            }

            return true;
        }
    };

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "toleranceMode";

    private static Map<String, ToleranceMode> toleranceModes;

    static {
        toleranceModes = new HashMap<String, ToleranceMode>();

        for (final ToleranceMode toleranceMode : ToleranceMode.values()) {
            toleranceModes.put(toleranceMode.toleranceMode, toleranceMode);
        }
    }

    private String toleranceMode;

    private ToleranceMode(final String toleranceMode) {
        this.toleranceMode = toleranceMode;
    }

    /**
     * Returns true if given numbers are equal; false otherwise.
     *
     * @param firstNumber first number to compare
     * @param secondNumber second number to compare
     * @param tolerance1 tolerance for lower boundary
     * @param tolerance2 tolerance for upper boundary
     * @param includeLowerBound accept lower boundary
     * @param includeUpperBound accept upper boundary
     * @return true if given numbers are equal; false otherwise
     */
    public abstract boolean isEqual(double firstNumber, double secondNumber, double tolerance1, double tolerance2,
            boolean includeLowerBound, boolean includeUpperBound);

    @Override
    public String toQtiString() {
        return toleranceMode;
    }

    /**
     * Parses string representation of <code>ToleranceMode</code>.
     *
     * @param toleranceMode string representation of <code>ToleranceMode</code>
     * @return parsed <code>ToleranceMode</code>
     */
    public static ToleranceMode parseToleranceMode(final String toleranceMode) {
        final ToleranceMode result = toleranceModes.get(toleranceMode);

        if (result == null) {
            throw new QtiParseException("Invalid " + QTI_CLASS_NAME + " '" + toleranceMode + "'.");
        }

        return result;
    }
}
