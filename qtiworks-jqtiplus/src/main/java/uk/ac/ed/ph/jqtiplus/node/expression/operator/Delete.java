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

import uk.ac.ed.ph.jqtiplus.node.expression.AbstractSimpleFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * The delete operator takes two sub-expressions which must both have the same base-type. The first
 * sub-expression must have single cardinality and the second must be a multiple or ordered container.
 * The result is a new container derived from the second sub-expression with all instances of the first
 * sub-expression removed.
 * <p>
 * For example, when applied to a and {B,A,C,A} the result is the container {B,C}.
 * <p>
 * If either sub-expression is NULL the result of the operator is NULL.
 * <p>
 * The delete operator should not be used on sub-expressions with a base-type of float because of the poorly defined comparison of values. It must not be used
 * on sub-expressions with a base-type of duration.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class Delete extends AbstractSimpleFunctionalExpression {

    private static final long serialVersionUID = -3347943030791068562L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "delete";

    public Delete(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);
    }

    @Override
    public Cardinality[] getRequiredCardinalities(final ValidationContext context, final int index) {
        Cardinality[] required = super.getRequiredCardinalities(context, index);

        if (index == 1) {
            required = Cardinality.intersection(required, getParentRequiredCardinalities(context));
        }

        return required;
    }

    @Override
    public BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        return getRequiredSameBaseTypes(context, index, true);
    }

    @Override
    public Cardinality[] getProducedCardinalities(final ValidationContext context) {
        if (getChildren().size() != 2) {
            return super.getProducedCardinalities(context);
        }

        return getChildren().get(1).getProducedCardinalities(context);
    }

    @Override
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        if (getChildren().size() != 2) {
            return super.getProducedBaseTypes(context);
        }

        return getChildren().get(1).getProducedBaseTypes(context);
    }

    @Override
    protected Value evaluateValidSelf(final Value[] childValues) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        final SingleValue toDelete = (SingleValue) childValues[0];
        final ListValue source = (ListValue) childValues[1];

        final List<SingleValue> toKeep = new ArrayList<SingleValue>(source.size());
        for (final SingleValue sourceValue : source) {
            if (!sourceValue.equals(toDelete)) {
                toKeep.add(sourceValue);
            }
        }
        if (source.getCardinality()==Cardinality.MULTIPLE) {
            return MultipleValue.createMultipleValue(toKeep);
        }
        else {
            return OrderedValue.createOrderedValue(toKeep);
        }
    }
}
