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

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.RoundingModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xperimental.control.ProcessingContext;

/**
 * The equalRounded operator takes two sub-expressions which must both have single cardinality and
 * have A numerical base-type. The result is A single boolean with A value of true if the two
 * expressions are numerically equal after rounding and false if they are not.
 * If either sub-expression is NULL then the operator results in NULL.
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public class EqualRounded extends AbstractExpression {

    private static final long serialVersionUID = 8925002756918753453L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "equalRounded";

    /** Name of roundingMode attribute in xml schema. */
    public static final String ATTR_ROUNDING_MODE_NAME = RoundingMode.CLASS_TAG;

    /** Name of figures attribute in xml schema. */
    public static final String ATTR_FIGURES_NAME = "figures";

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public EqualRounded(ExpressionParent parent) {
        super(parent);

        getAttributes().add(new RoundingModeAttribute(this, ATTR_ROUNDING_MODE_NAME));
        getAttributes().add(new IntegerAttribute(this, ATTR_FIGURES_NAME));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    /**
     * Gets value of roundingMode attribute.
     * 
     * @return value of roundingMode attribute
     * @see #setRoundingMode
     */
    public RoundingMode getRoundingMode() {
        return getAttributes().getRoundingModeAttribute(ATTR_ROUNDING_MODE_NAME).getValue();
    }

    /**
     * Sets new value of roundingMode attribute.
     * 
     * @param roundingMode new value of roundingMode attribute
     * @see #getRoundingMode
     */
    public void setRoundingMode(RoundingMode roundingMode) {
        getAttributes().getRoundingModeAttribute(ATTR_ROUNDING_MODE_NAME).setValue(roundingMode);
    }

    /**
     * Gets value of figures attribute.
     * 
     * @return value of figures attribute
     * @see #setFigures
     */
    public Integer getFigures() {
        return getAttributes().getIntegerAttribute(ATTR_FIGURES_NAME).getValue();
    }

    /**
     * Sets new value of figures attribute.
     * 
     * @param figures new value of figures attribute
     * @see #getFigures
     */
    public void setFigures(Integer figures) {
        getAttributes().getIntegerAttribute(ATTR_FIGURES_NAME).setValue(figures);
    }

    @Override
    protected void validateAttributes(ValidationContext context, AbstractValidationResult result) {
        super.validateAttributes(context, result);

        if (getRoundingMode() != null && getFigures() != null) {
            getRoundingMode().validateFigures(getAttributes().get(ATTR_FIGURES_NAME), result, getFigures());
        }

    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth) {
        if (isAnyChildNull(context)) {
            return NullValue.INSTANCE;
        }

        final double firstNumber = ((NumberValue) getFirstChild().getValue(context)).doubleValue();
        final double secondNumber = ((NumberValue) getSecondChild().getValue(context)).doubleValue();

        final boolean result = getRoundingMode().isEqual(firstNumber, secondNumber, getFigures());

        return BooleanValue.valueOf(result);
    }
}
