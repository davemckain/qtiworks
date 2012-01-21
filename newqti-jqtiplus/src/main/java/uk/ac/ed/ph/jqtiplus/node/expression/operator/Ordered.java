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

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The ordered operator takes 0 or more sub-expressions all of which must have either single or ordered
 * cardinality. Although the sub-expressions may be of any base-type they must all be of the same base-type.
 * The result is A container with ordered cardinality containing the values of the sub-expressions,
 * sub-expressions with ordered cardinality have their individual values added (in order) to the result:
 * contains cannot contain other containers.
 * <p>
 * For example, when applied to A, B, {C,D} the ordered operator results in {A,B,C,D}.
 * <p>
 * Note that the ordered operator never results in an empty container. All sub-expressions with NULL values are ignored. If no sub-expressions are given (or all
 * are NULL) then the result is NULL.
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public class Ordered extends AbstractExpression {

    private static final long serialVersionUID = -1785588795962181470L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "ordered";

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public Ordered(ExpressionParent parent) {
        super(parent);
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public BaseType[] getRequiredBaseTypes(ValidationContext context, int index) {
        return getRequiredSameBaseTypes(context, index, true);
    }

    @Override
    public BaseType[] getProducedBaseTypes(ValidationContext context) {
        BaseType[] produced = super.getProducedBaseTypes(context);

        for (final Expression child : getChildren()) {
            produced = BaseType.intersection(produced, child.getProducedBaseTypes(context));
        }

        return produced;
    }

    @Override
    protected void validateChildren(ValidationContext context) {
        super.validateChildren(context);

        if (getChildren().size() == 0) {
            context.add(new ValidationWarning(this, "Container should contain some children."));
        }
    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth) {
        final OrderedValue container = new OrderedValue();

        for (final Expression subExpression : getChildren()) {
            final Value value = subExpression.getValue(context);
            if (!value.isNull()) {
                if (value.getCardinality() == Cardinality.SINGLE) {
                    container.add((SingleValue) value);
                }
                else if (value.getCardinality() == Cardinality.ORDERED) {
                    container.add((OrderedValue) value);
                }
                else {
                    throw new QtiLogicException("Invalid cardinality: " + value.getCardinality());
                }
            }
        }

        if (container.isNull()) {
            return NullValue.INSTANCE;
        }

        return container;
    }
}
