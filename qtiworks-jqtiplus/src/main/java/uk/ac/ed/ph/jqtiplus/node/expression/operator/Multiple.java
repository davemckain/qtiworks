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
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * The multiple operator takes 0 or more sub-expressions all of which must have either single or multiple
 * cardinality. Although the sub-expressions may be of any base-type they must all be of the same base-type.
 * The result is a container with multiple cardinality containing the values of the sub-expressions,
 * sub-expressions with multiple cardinality have their individual values added to the result:
 * containers cannot contain other containers.
 * <p>
 * For example, when applied to A, B and {C,D} the multiple operator results in {A,B,C,D}.
 * <p>
 * All sub-expressions with NULL values are ignored. If no sub-expressions are given (or all are NULL) then the result is NULL.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class Multiple extends AbstractSimpleFunctionalExpression {

    private static final long serialVersionUID = -2949615998344301483L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "multiple";

    public Multiple(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);
    }

    @Override
    public BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        return getRequiredSameBaseTypes(context, index, true);
    }

    @Override
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        BaseType[] produced = super.getProducedBaseTypes(context);

        for (final Expression child : getChildren()) {
            produced = BaseType.intersection(produced, child.getProducedBaseTypes(context));
        }

        return produced;
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        if (getChildren().size() == 0) {
            context.fireValidationWarning(this, "Container should contain some children.");
        }
    }

    @Override
    protected Value evaluateValidSelf(final Value[] childValues) {
        final List<SingleValue> flattenedChildren = new ArrayList<SingleValue>();
        for (final Value childValue : childValues) {
            if (!childValue.isNull()) {
                if (childValue.getCardinality() == Cardinality.SINGLE) {
                    flattenedChildren.add((SingleValue) childValue);
                }
                else if (childValue.getCardinality() == Cardinality.MULTIPLE) {
                    final ListValue childList = (ListValue) childValue;
                    for (final SingleValue childListValue : childList) {
                        flattenedChildren.add(childListValue);
                    }
                }
                else {
                    /* FIXME: Record runtime error */
                    throw new QtiLogicException("Invalid cardinality: " + childValue.getCardinality());
                }
            }
        }
        return MultipleValue.createMultipleValue(flattenedChildren);
    }
}
