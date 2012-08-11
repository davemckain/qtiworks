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
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerOrVariableRefAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.IntegerOrVariableRef;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.math.BigDecimal;

/**
 * Implementation of the <tt>roundTo</tt> expression
 *
 * @author David McKain
 */
public class RoundTo extends AbstractExpression {

    private static final long serialVersionUID = 7637604241884891345L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "roundTo";

    /** Name of roundingMode attribute in xml schema. */
    public static final String ATTR_ROUNDING_MODE_NAME = RoundingMode.QTI_CLASS_NAME;

    /** Name of figures attribute in xml schema. */
    public static final String ATTR_FIGURES_NAME = "figures";

    public RoundTo(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new RoundingModeAttribute(this, ATTR_ROUNDING_MODE_NAME, true));
        getAttributes().add(new IntegerOrVariableRefAttribute(this, ATTR_FIGURES_NAME, true));
    }

    /**
     * Gets value of roundingMode attribute.
     *
     * @return value of roundingMode attribute
     * @see #setRoundingMode
     */
    public RoundingMode getRoundingMode() {
        return getAttributes().getRoundingModeAttribute(ATTR_ROUNDING_MODE_NAME).getComputedValue();
    }

    /**
     * Sets new value of roundingMode attribute.
     *
     * @param roundingMode new value of roundingMode attribute
     * @see #getRoundingMode
     */
    public void setRoundingMode(final RoundingMode roundingMode) {
        getAttributes().getRoundingModeAttribute(ATTR_ROUNDING_MODE_NAME).setValue(roundingMode);
    }

    /**
     * Gets value of figures attribute.
     *
     * @return value of figures attribute
     * @see #setFigures
     */
    public IntegerOrVariableRef getFigures() {
        return getAttributes().getIntegerOrVariableRefAttribute(ATTR_FIGURES_NAME).getValue();
    }

    /**
     * Sets new value of figures attribute.
     *
     * @param figures new value of figures attribute
     * @see #getFigures
     */
    public void setFigures(final IntegerOrVariableRef figures) {
        getAttributes().getIntegerOrVariableRefAttribute(ATTR_FIGURES_NAME).setValue(figures);
    }

    @Override
    protected void validateAttributes(final ValidationContext context) {
        super.validateAttributes(context);

        final RoundingMode roundingMode = getRoundingMode();
        if (roundingMode != null) {
            roundingMode.validateFigures(getAttributes().getIntegerOrVariableRefAttribute(ATTR_FIGURES_NAME),
                    context.getValidationResult());
        }
    }

    @Override
    protected Value evaluateSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        final Value childValue = childValues[0];
        if (childValue.isNull()) {
            return NullValue.INSTANCE;
        }
        final double childNumber = ((NumberValue) childValue).doubleValue();
        final int computedFigures = getFigures().evaluate(context);
        final BigDecimal rounded = getRoundingMode().round(childNumber, computedFigures);

        return new FloatValue(rounded.doubleValue());
    }
}
