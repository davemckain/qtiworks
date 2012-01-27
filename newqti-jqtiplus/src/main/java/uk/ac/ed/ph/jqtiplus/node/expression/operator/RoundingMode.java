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
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.validation.AttributeValidationError;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for equalRounded expression.
 * 
 * @see EqualRounded
 * @author Jiri Kajaba
 */
public enum RoundingMode {
    /**
     * Number is rounded to A given number of significant figures.
     */
    SIGNIFICANT_FIGURES("significantFigures") {

        @Override
        public void validateFigures(IntegerAttribute attribute, AbstractValidationResult result, int figures) {
            if (figures < 1) {
                result.add(new AttributeValidationError(attribute, "Figures count (" + figures + ") must be positive."));
            }
        }

        @Override
        public BigDecimal round(double number, int figures) {
            if (number == 0) {
                return BigDecimal.ZERO;
            }

            final BigDecimal numberDecimal = new BigDecimal(Double.toString(number));

            /* Need to work out the number of digits required to shift left to get +-0.[1-9]nnnn */
            int requiredShift;
            BigDecimal absValue = numberDecimal.abs();
            if (absValue.compareTo(BigDecimal.ONE) >= 0) {
                /* Has something before the decimal point */
                absValue = absValue.setScale(0, java.math.RoundingMode.DOWN);
                requiredShift = absValue.precision();
            }
            else {
                /* 0.something */
                requiredShift = 0;
                for (;;) {
                    absValue = absValue.movePointRight(1);
                    if (absValue.compareTo(BigDecimal.ONE) >= 0) {
                        break;
                    }
                    requiredShift--;
                }
            }

            /* Now do a shift, round, then shift back */
            return numberDecimal.movePointLeft(requiredShift)
                    .setScale(figures, java.math.RoundingMode.HALF_UP)
                    .movePointRight(requiredShift);
        }
    },

    /**
     * Number is rounded to A given number of decimal places.
     */
    DECIMAL_PLACES("decimalPlaces") {

        @Override
        public void validateFigures(IntegerAttribute attribute, AbstractValidationResult result, int figures) {
            if (figures < 0) {
                result.add(new AttributeValidationError(attribute, "Figures count (" + figures + ") cannot be negative."));
            }
        }

        @Override
        public BigDecimal round(double number, int figures) {
            final BigDecimal numberDecimal = new BigDecimal(Double.toString(number));

            return numberDecimal.setScale(figures, java.math.RoundingMode.HALF_UP);
        }
    };

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "roundingMode";

    private static Map<String, RoundingMode> roundingModes;

    static {
        roundingModes = new HashMap<String, RoundingMode>();

        for (final RoundingMode roundingMode : RoundingMode.values()) {
            roundingModes.put(roundingMode.roundingMode, roundingMode);
        }
    }

    private String roundingMode;

    private RoundingMode(String roundingMode) {
        this.roundingMode = roundingMode;
    };

    /**
     * Validates figures attribute.
     * 
     * @param attribute attribute to be validated
     * @param figures attribute's value to be validated
     */
    public abstract void validateFigures(IntegerAttribute attribute, AbstractValidationResult result, int figures);

    /**
     * Rounds given number for given number of figures.
     * 
     * @param number number
     * @param figures number of figures
     * @return rounded number
     */
    public abstract BigDecimal round(double number, int figures);

    /**
     * Returns true if given two numbers are equal after rounding; false otherwise.
     * 
     * @param firstNumber first number to compare
     * @param secondNumber second number to compare
     * @param figures rounding figures
     * @return true if given two numbers are equal after rounding; false otherwise
     */
    public boolean isEqual(double firstNumber, double secondNumber, int figures) {
        final BigDecimal firstBigDecimalNumber = round(firstNumber, figures);
        final BigDecimal secondBigDecimalNumber = round(secondNumber, figures);

        return firstBigDecimalNumber.compareTo(secondBigDecimalNumber) == 0;
    }

    @Override
    public String toString() {
        return roundingMode;
    }

    /**
     * Parses string representation of <code>RoundingMode</code>.
     * 
     * @param roundingMode string representation of <code>RoundingMode</code>
     * @return parsed <code>RoundingMode</code>
     */
    public static RoundingMode parseRoundingMode(String roundingMode) {
        final RoundingMode result = roundingModes.get(roundingMode);

        if (result == null) {
            throw new QTIParseException("Invalid " + CLASS_TAG + " '" + roundingMode + "'.");
        }

        return result;
    }
}
