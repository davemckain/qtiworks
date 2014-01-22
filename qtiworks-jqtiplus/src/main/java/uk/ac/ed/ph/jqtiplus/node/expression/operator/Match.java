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
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The match operator takes two sub-expressions which must both have the same base-type and cardinality.
 * The result is a single boolean with a value of true if the two expressions represent the same value
 * and false if they do not. If either sub-expression is NULL then the operator results in NULL.
 * <p>
 * The match operator must not be confused with broader notions of equality such as numerical equality. To avoid confusion, the match operator should not be
 * used to compare subexpressions with base-types of float and must not be used on sub-expressions with a base-type of duration.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class Match extends AbstractSimpleFunctionalExpression {

    private static final long serialVersionUID = 6569951232209204404L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "match";

    public Match(final ExpressionParent parent) {
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
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        for (final Expression expression : getChildren()) {
            final Cardinality[] cardinalities = expression.getProducedCardinalities(context);
            if (cardinalities.length == 1 && cardinalities[0].isRecord()) {
                for (final Expression ex : expression.getExpressions()) {
                    final BaseType[] baseTypes = ex.getProducedBaseTypes(context);
                    if (baseTypes.length == 1 && baseTypes[0].isDuration()) {
                        final BaseType[] expected = BaseType.except(new BaseType[] { BaseType.DURATION });
                        context.fireBaseTypeValidationError(this, expected, baseTypes);
                    }
                }
            }
        }
    }

    @Override
    protected Value evaluateValidSelf(final Value[] childValues) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        final Value firstValue = childValues[0];
        final Value secondValue = childValues[1];
        return BooleanValue.valueOf(firstValue.qtiEquals(secondValue));
    }
}
