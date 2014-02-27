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

import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerOrVariableRefAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.IntegerOrVariableRef;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The anyN operator takes one or more sub-expressions each with a base-type of boolean and single cardinality.
 * The result is a single boolean which is true if at least min of the sub-expressions are true and
 * at most max of the sub-expressions are true. If more than n - min sub-expressions are false (where n
 * is the total number of sub-expressions) or more than max sub-expressions are true then the result
 * is false. If one or more sub-expressions are NULL then it is possible that neither of these conditions
 * is satisfied, in which case the operator results in NULL. For example, if min is 3 and max is 4 and
 * the sub-expressions have values {true,true,false,NULL} then the operator results in NULL whereas
 * {true,false,false,NULL} results in false and {true,true,true,NULL} results in true. The result NULL
 * indicates that the correct value for the operator cannot be determined.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class AnyN extends AbstractFunctionalExpression {

    private static final long serialVersionUID = -2513872740143850055L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "anyN";

    /** Name of min attribute in xml schema. */
    public static final String ATTR_MINIMUM_NAME = "min";

    /** Name of max attribute in xml schema. */
    public static final String ATTR_MAXIMUM_NAME = "max";

    public AnyN(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerOrVariableRefAttribute(this, ATTR_MINIMUM_NAME, true));
        getAttributes().add(new IntegerOrVariableRefAttribute(this, ATTR_MAXIMUM_NAME, true));
    }

    public IntegerOrVariableRef getMin() {
        return getAttributes().getIntegerOrVariableRefAttribute(ATTR_MINIMUM_NAME).getValue();
    }

    public void setMin(final IntegerOrVariableRef minimum) {
        getAttributes().getIntegerOrVariableRefAttribute(ATTR_MINIMUM_NAME).setValue(minimum);
    }


    public IntegerOrVariableRef getMax() {
        return getAttributes().getIntegerOrVariableRefAttribute(ATTR_MAXIMUM_NAME).getValue();
    }

    public void setMax(final IntegerOrVariableRef maximum) {
        getAttributes().getIntegerOrVariableRefAttribute(ATTR_MAXIMUM_NAME).setValue(maximum);
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final IntegerOrVariableRef minComputer = getMin();
        final IntegerOrVariableRef maxComputer = getMax();
        if (minComputer.isConstantInteger()) {
            final int min = minComputer.getConstantIntegerValue().intValue();
            if (min < 0) {
                context.fireValidationWarning(this,
                        "Attribute " + ATTR_MINIMUM_NAME
                        + " (" + min + ") should be positive.");
            }
            if (maxComputer.isConstantInteger()) {
                final int max = maxComputer.getConstantIntegerValue().intValue();
                if (max < min) {
                    context.fireValidationWarning(this,
                            "Attribute " + ATTR_MAXIMUM_NAME
                            + " (" + max + ") should be greater than "
                            + ATTR_MINIMUM_NAME + " (" + min + ").");
                }
            }
        }
    }

    @Override
    protected Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        int numberOfNull = 0;
        int numberOfTrue = 0;

        for (final Value childValue : childValues) {
            if (childValue.isNull()) {
                numberOfNull++;
            }
            else if (((BooleanValue) childValue).booleanValue()) {
                numberOfTrue++;
            }
        }

        final int min = getMin().evaluateNotNull(this, context, "Computed value of minimum was NULL. Replacing with 0", 0);
        final int max = getMax().evaluateNotNull(this, context, "Computed value of maximum was NULL. Replacing with 0", 0);

        if (min > max) {
            return BooleanValue.FALSE;
        }

        if (numberOfTrue >= min && numberOfTrue + numberOfNull <= max) {
            return BooleanValue.TRUE;
        }

        if (numberOfTrue + numberOfNull < min || numberOfTrue > max) {
            return BooleanValue.FALSE;
        }

        return NullValue.INSTANCE;
    }
}
