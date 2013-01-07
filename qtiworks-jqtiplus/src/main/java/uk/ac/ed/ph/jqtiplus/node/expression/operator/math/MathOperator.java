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
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.node.expression.AbstractSimpleFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * Implementation of <tt>mathOperator</tt>
 *
 * @author David McKain
 */
public final class MathOperator extends AbstractSimpleFunctionalExpression {

    private static final long serialVersionUID = 709298090798424712L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "mathOperator";

    /** Name of 'name' attribute */
    public static final String ATTR_NAME_NAME = "name";

    public MathOperator(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new MathOperatorNameAttribute(this, "name", true));
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.MATH_OPERATOR;
    }

    public MathOperatorTarget getTarget() {
        return ((MathOperatorNameAttribute) getAttributes().get(ATTR_NAME_NAME)).getComputedValue();
    }

    public void setTarget(final MathOperatorTarget target) {
        ((MathOperatorNameAttribute) getAttributes().get(ATTR_NAME_NAME)).setValue(target);
    }

    @Override
    public final Value evaluateValidSelf(final Value[] childValues) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        /* Convert each argument value to double, short-circuiting if any argument is null */
        final double[] arguments = new double[childValues.length];
        for (int i = 0; i < childValues.length; i++) {
            final Value childValue = childValues[i];
            if (childValue.isNull() || !childValue.getBaseType().isNumeric()) {
                return NullValue.INSTANCE;
            }
            arguments[i] = ((NumberValue) childValue).doubleValue();
        }

        /* Call up the appropriate operation's evaluator */
        final MathOperatorEvaluator evaluator = getTarget().getEvaluator();
        return evaluator.evaluate(arguments);
    }

    @Override
    public void validateThis(final ValidationContext context) {
        super.validateThis(context);
        /* Make sure number of children is correct */
        final MathOperatorTarget operation = getTarget();
        if (operation != null && getChildren().size() != operation.getArgumentCount()) {
            context.fireValidationError(this, "Operation " + operation.getName()
                    + " expects " + operation.getArgumentCount() + " children, not "
                    + getChildren().size());
        }
    }
}
