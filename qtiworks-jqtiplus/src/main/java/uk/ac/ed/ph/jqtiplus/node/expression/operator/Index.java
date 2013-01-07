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
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The index operator takes a sub-expression with an ordered container value and any base-type.
 * The result is the nth value of the container. The result has the same base-type as the sub-expression
 * but single cardinality. The first value of a container has index 1, the second 2 and so on.
 * N must be a positive integer. If n exceeds the number of values in the container or the sub-expression
 * is NULL then the result of the index operator is NULL.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class Index extends AbstractFunctionalExpression {

    private static final long serialVersionUID = 909169733159523992L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "index";

    /** Name of n attribute in xml schema. */
    public static final String ATTR_INDEX_NAME = "n";

    public Index(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerOrVariableRefAttribute(this, ATTR_INDEX_NAME, true));
    }

    public IntegerOrVariableRef getIndex() {
        return getAttributes().getIntegerOrVariableRefAttribute(ATTR_INDEX_NAME).getValue();
    }

    public void setIndex(final IntegerOrVariableRef index) {
        getAttributes().getIntegerOrVariableRefAttribute(ATTR_INDEX_NAME).setValue(index);
    }

    @Override
    public BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        return getRequiredSameBaseTypes(context, index, true);
    }

    @Override
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        if (getChildren().size() == 1) {
            return getChildren().get(0).getProducedBaseTypes(context);
        }

        return super.getProducedBaseTypes(context);
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final IntegerOrVariableRef indexComputer = getIndex();
        if (indexComputer.isConstantInteger()) {
            final int index = indexComputer.getConstantIntegerValue().intValue();
            if (index < 1) {
                context.fireAttributeValidationError(getAttributes().get(ATTR_INDEX_NAME),
                        "Attribute " + ATTR_INDEX_NAME + " (" + index + ") must be positive");
            }

            if (getChildren().size() != 0 && getChildren().get(0) instanceof Ordered) {
                final Ordered ordered = (Ordered) getChildren().get(0);
                if (ordered.getChildren().size() > 0 && index > ordered.getChildren().size()) {
                    context.fireAttributeValidationWarning(getAttributes().get(ATTR_INDEX_NAME),
                        "Attribute " + ATTR_INDEX_NAME + " is too big. Expected at most: "
                            + ordered.getChildren().size() + ", but found: " + index);
                }
            }
        }
    }

    @Override
    protected Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        final OrderedValue childOrderedValue = (OrderedValue) childValues[0];
        final Value computedIndex = getIndex().evaluate(this, context);
        if (computedIndex.isNull()) {
            return NullValue.INSTANCE;
        }

        final int index = ((IntegerValue) computedIndex).intValue();
        if (index < 1 || index > childOrderedValue.size()) {
            return NullValue.INSTANCE;
        }

        return childOrderedValue.get(index - 1);
    }
}
