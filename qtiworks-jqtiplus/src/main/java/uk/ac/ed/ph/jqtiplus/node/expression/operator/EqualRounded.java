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

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.RoundingModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerOrVariableRefAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.IntegerOrVariableRef;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The equalRounded operator takes two sub-expressions which must both have single cardinality and
 * have a numerical base-type. The result is a single boolean with a value of true if the two
 * expressions are numerically equal after rounding and false if they are not.
 * If either sub-expression is NULL then the operator results in NULL.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class EqualRounded extends AbstractFunctionalExpression {

    private static final long serialVersionUID = 8925002756918753453L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "equalRounded";

    /** Name of roundingMode attribute in xml schema. */
    public static final String ATTR_ROUNDING_MODE_NAME = RoundingMode.QTI_CLASS_NAME;

    /** Name of figures attribute in xml schema. */
    public static final String ATTR_FIGURES_NAME = "figures";

    public EqualRounded(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new RoundingModeAttribute(this, ATTR_ROUNDING_MODE_NAME, true));
        getAttributes().add(new IntegerOrVariableRefAttribute(this, ATTR_FIGURES_NAME, true));
    }


    public RoundingMode getRoundingMode() {
        return getAttributes().getRoundingModeAttribute(ATTR_ROUNDING_MODE_NAME).getComputedValue();
    }

    public void setRoundingMode(final RoundingMode roundingMode) {
        getAttributes().getRoundingModeAttribute(ATTR_ROUNDING_MODE_NAME).setValue(roundingMode);
    }


    public IntegerOrVariableRef getFigures() {
        return getAttributes().getIntegerOrVariableRefAttribute(ATTR_FIGURES_NAME).getValue();
    }

    public void setFigures(final IntegerOrVariableRef figures) {
        getAttributes().getIntegerOrVariableRefAttribute(ATTR_FIGURES_NAME).setValue(figures);
    }


    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final RoundingMode roundingMode = getRoundingMode();
        if (roundingMode != null) {
            roundingMode.validateFigures(context,
                    getAttributes().getIntegerOrVariableRefAttribute(ATTR_FIGURES_NAME));
        }
    }

    @Override
    protected Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        final double firstNumber = ((NumberValue) childValues[0]).doubleValue();
        final double secondNumber = ((NumberValue) childValues[1]).doubleValue();

        /* Handle NaN or infinite cases by just doing normal double comparison */
        if (RoundingMode.isNaNOrInfinite(firstNumber) || RoundingMode.isNaNOrInfinite(secondNumber)) {
            return BooleanValue.valueOf(firstNumber==secondNumber);
        }

        final Value computedFigures = getFigures().evaluate(this, context);
        if (computedFigures.isNull()) {
            context.fireRuntimeWarning(this, "Computed value of figures was NULL. Returning NULL");
            return NullValue.INSTANCE;
        }

        final RoundingMode roundingMode = getRoundingMode();
        final int figures = ((IntegerValue) computedFigures).intValue();
        if (!roundingMode.isFiguresValid(figures)) {
            context.fireRuntimeWarning(this, "The computed value of figures (" + figures + ") was not compatible with the constraints of the rounding mode. Returning NULL");
            return NullValue.INSTANCE;
        }

        return BooleanValue.valueOf(roundingMode.isEqual(firstNumber, secondNumber, figures));
    }
}
