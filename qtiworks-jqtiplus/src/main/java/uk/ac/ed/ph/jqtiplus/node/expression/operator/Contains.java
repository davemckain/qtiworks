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

import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractSimpleFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The contains operator takes two sub-expressions which must both have the same base-type and cardinality
 * - either multiple or ordered. The result is a single boolean with a value of true if the container
 * given by the first sub-expression contains the value given by the second sub-expression and false
 * if it doesn't. Note that the contains operator works differently depending on the cardinality of
 * the two sub-expressions.
 * <p>
 * For unordered containers the values are compared without regard for ordering, for example, [A,B,C] contains [C,A]. Note that [A,B,C] does not contain [B,B]
 * but that [A,B,B,C] does.
 * <p>
 * For ordered containers the second sub-expression must be a strict sub-sequence within the first. In other words, [A,B,C] does not contain [C,A] but it does
 * contain [B,C].
 * <p>
 * If either sub-expression is NULL then the result of the operator is NULL.
 * <p>
 * Like the member operator, the contains operator should not be used on sub-expressions with a base-type of float and must not be used on sub-expressions with
 * A base-type of duration.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class Contains extends AbstractSimpleFunctionalExpression {

    private static final long serialVersionUID = 3829603738168267516L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "contains";

    public Contains(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);
    }

    @Override
    public Cardinality[] getRequiredCardinalities(final ValidationContext context, final int index) {
        return getRequiredSameCardinalities(context, index, false);
    }

    @Override
    public BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        return getRequiredSameBaseTypes(context, index, false);
    }

    @Override
    protected Value evaluateValidSelf(final Value[] childValues) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        final Value firstValue = childValues[0];
        final Value secondValue = childValues[1];

        boolean result;
        switch (firstValue.getCardinality()) {
            case MULTIPLE:
                result = ((MultipleValue) firstValue).contains((MultipleValue) secondValue);
                break;

            case ORDERED:
                result = ((OrderedValue) firstValue).contains((OrderedValue) secondValue);
                break;

            default:
                throw new QtiLogicException("Invalid cardinality: " + firstValue.getCardinality());
        }

        return BooleanValue.valueOf(result);
    }
}
