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
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The anyN operator takes one or more sub-expressions each with A base-type of boolean and single cardinality.
 * The result is A single boolean which is true if at least min of the sub-expressions are true and
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
public class AnyN extends AbstractExpression {

    private static final long serialVersionUID = -2513872740143850055L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "anyN";

    /** Name of min attribute in xml schema. */
    public static final String ATTR_MINIMUM_NAME = "min";

    /** Name of max attribute in xml schema. */
    public static final String ATTR_MAXIMUM_NAME = "max";

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public AnyN(ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerAttribute(this, ATTR_MINIMUM_NAME));
        getAttributes().add(new IntegerAttribute(this, ATTR_MAXIMUM_NAME));
    }

    /**
     * Gets value of min attribute.
     * 
     * @return value of min attribute
     * @see #setMinimum
     */
    public Integer getMinimum() {
        return getAttributes().getIntegerAttribute(ATTR_MINIMUM_NAME).getValue();
    }

    /**
     * Sets new value of min attribute.
     * 
     * @param minimum new value of min attribute
     * @see #getMinimum
     */
    public void setMinimum(Integer minimum) {
        getAttributes().getIntegerAttribute(ATTR_MINIMUM_NAME).setValue(minimum);
    }

    /**
     * Gets value of max attribute.
     * 
     * @return value of max attribute
     * @see #setMaximum
     */
    public Integer getMaximum() {
        return getAttributes().getIntegerAttribute(ATTR_MAXIMUM_NAME).getValue();
    }

    /**
     * Sets new value of max attribute.
     * 
     * @param maximum new value of max attribute
     * @see #getMaximum
     */
    public void setMaximum(Integer maximum) {
        getAttributes().getIntegerAttribute(ATTR_MAXIMUM_NAME).setValue(maximum);
    }

    @Override
    protected void validateAttributes(ValidationContext context) {
        if (getMinimum() != null && getMinimum() < 0) {
            context.add(new ValidationWarning(this, "Attribute " + ATTR_MINIMUM_NAME + " (" + getMinimum() +
                    ") should be positive."));
        }

        if (getMinimum() != null && getMaximum() != null && getMaximum() < getMinimum()) {
            context.add(new ValidationWarning(this, "Attribute " + ATTR_MAXIMUM_NAME + " (" + getMaximum() +
                    ") should be greater than " + ATTR_MINIMUM_NAME + " (" + getMinimum() + ")."));
        }

    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth) {
        int numberOfNull = 0;
        int numberOfTrue = 0;

        for (final Expression subExpression : getChildren()) {
            if (subExpression.isNull(context)) {
                numberOfNull++;
            }
            else if (((BooleanValue) subExpression.getValue(context)).booleanValue()) {
                numberOfTrue++;
            }
        }

        final int minimum = getMinimum();
        final int maximum = getMaximum() > 0 ? getMaximum() : 0;

        if (minimum > maximum) {
            return BooleanValue.FALSE;
        }

        if (numberOfTrue >= minimum && numberOfTrue + numberOfNull <= maximum) {
            return BooleanValue.TRUE;
        }

        if (numberOfTrue + numberOfNull < minimum || numberOfTrue > maximum) {
            return BooleanValue.FALSE;
        }

        return NullValue.INSTANCE;
    }
}
