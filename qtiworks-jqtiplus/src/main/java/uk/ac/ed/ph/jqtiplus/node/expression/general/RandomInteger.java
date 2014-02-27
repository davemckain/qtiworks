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
package uk.ac.ed.ph.jqtiplus.node.expression.general;

import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerOrVariableRefAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.IntegerOrVariableRef;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Random;

/**
 * Selects a random integer from the specified range [min,max] satisfying min + step * n for some integer n.
 * For example, with min=2, max=11 and step=3 the values {2,5,8,11} are possible.
 * <p>
 * Additional conditions: max >= min, step >= 1
 *
 * @author Jiri Kajaba
 */
public class RandomInteger extends AbstractFunctionalExpression {

    private static final long serialVersionUID = 4707680766519679314L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "randomInteger";

    /** Name of min attribute in xml schema. */
    public static final String ATTR_MIN_NAME = "min";

    /** Name of max attribute in xml schema. */
    public static final String ATTR_MAX_NAME = "max";

    /** Name of step attribute in xml schema. */
    public static final String ATTR_STEP_NAME = "step";

    /** Default value of step attribute. */
    public static final int ATTR_STEP_DEFAULT_VALUE = 1;

    public RandomInteger(final ExpressionParent parent) {
        this(parent, QTI_CLASS_NAME);
    }

    protected RandomInteger(final ExpressionParent parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new IntegerOrVariableRefAttribute(this, ATTR_MIN_NAME, true));
        getAttributes().add(new IntegerOrVariableRefAttribute(this, ATTR_MAX_NAME, true));
        getAttributes().add(new IntegerOrVariableRefAttribute(this, ATTR_STEP_NAME, new IntegerOrVariableRef(ATTR_STEP_DEFAULT_VALUE), false));
    }

    public IntegerOrVariableRef getMin() {
        return getAttributes().getIntegerOrVariableRefAttribute(ATTR_MIN_NAME).getValue();
    }

    public void setMin(final IntegerOrVariableRef minimum) {
        getAttributes().getIntegerOrVariableRefAttribute(ATTR_MIN_NAME).setValue(minimum);
    }


    public IntegerOrVariableRef getMax() {
        return getAttributes().getIntegerOrVariableRefAttribute(ATTR_MAX_NAME).getValue();
    }

    public void setMax(final IntegerOrVariableRef maximum) {
        getAttributes().getIntegerOrVariableRefAttribute(ATTR_MAX_NAME).setValue(maximum);
    }


    public IntegerOrVariableRef getStep() {
        return getAttributes().getIntegerOrVariableRefAttribute(ATTR_STEP_NAME).getComputedValue();
    }

    public void setStep(final IntegerOrVariableRef step) {
        getAttributes().getIntegerOrVariableRefAttribute(ATTR_STEP_NAME).setValue(step);
    }


    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final IntegerOrVariableRef maxComputer = getMax();
        final IntegerOrVariableRef minComputer = getMin();
        final IntegerOrVariableRef stepComputer = getStep();

        if (maxComputer.isConstantInteger() && minComputer.isConstantInteger()) {
            final int max = maxComputer.getConstantIntegerValue().intValue();
            final int min = minComputer.getConstantIntegerValue().intValue();
            if (max < min) {
                context.fireAttributeValidationError(getAttributes().get(ATTR_MAX_NAME),
                        "Attribute " + ATTR_MAX_NAME + " (" + max + ") cannot be lower than " + ATTR_MIN_NAME + " (" + min + ")");
            }

        }

        if (stepComputer!=null && stepComputer.isConstantInteger()) {
            final int step = stepComputer.getConstantIntegerValue().intValue();
            if (step < 1) {
                context.fireAttributeValidationError(getAttributes().get(ATTR_STEP_NAME),
                        "Attribute " + ATTR_STEP_NAME + " (" + step + ") must be positive.");
            }
        }
    }

    @Override
    protected Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        final Random randomGenerator = context.getRandomGenerator();

        final int min = getMin().evaluateNotNull(this, context, "Computed value of min was NULL. Replacing with 0", 0);
        final int max = getMax().evaluateNotNull(this, context, "Computed value of max was NULL. Replacing with min+1", min+1);
        final int step = getStep().evaluateNotNull(this, context, "Computed value of step was NULL. Replacing with 1", 1);

        /* Validate computed numbers */
        if (step < 1) {
            return NullValue.INSTANCE;
        }
        if (max < min) {
            return NullValue.INSTANCE;
        }

        final int randomNumber = randomGenerator.nextInt((max - min) / step + 1);
        final int randomInteger = min + randomNumber * step;

        return new IntegerValue(randomInteger);
    }
}
