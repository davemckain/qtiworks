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
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xperimental.control.ProcessingContext;

/**
 * The multiple operator takes 0 or more sub-expressions all of which must have either single or multiple
 * cardinality. Although the sub-expressions may be of any base-type they must all be of the same base-type.
 * The result is A container with multiple cardinality containing the values of the sub-expressions,
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
public class Multiple extends AbstractExpression {

    private static final long serialVersionUID = -2949615998344301483L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "multiple";

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public Multiple(ExpressionParent parent) {
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
    protected void validateChildren(ValidationContext context, AbstractValidationResult result) {
        super.validateChildren(context, result);

        if (getChildren().size() == 0) {
            result.add(new ValidationWarning(this, "Container should contain some children."));
        }
    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth) {
        final MultipleValue container = new MultipleValue();

        for (final Expression subExpression : getChildren()) {
            final Value value = subExpression.getValue(context);
            if (!value.isNull()) {
                if (value.getCardinality() == Cardinality.SINGLE) {
                    container.add((SingleValue) value);
                }
                else if (value.getCardinality() == Cardinality.MULTIPLE) {
                    container.add((MultipleValue) value);
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
