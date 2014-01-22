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

import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The random operator takes a sub-expression with a multiple or ordered container value and any base-type.
 * The result is a single value randomly selected from the container. The result has the same base-type
 * as the sub-expression but single cardinality.
 * If the sub-expression is NULL then the result is also NULL.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public class Random extends AbstractFunctionalExpression {

    private static final long serialVersionUID = -4841943053695949471L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "random";

    public Random(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);
    }

    protected Random(final ExpressionParent parent, final String qtiClassName) {
        super(parent, qtiClassName);
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
    protected Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        final java.util.Random randomGenerator = context.getRandomGenerator();
        final int randomIndex = randomGenerator.nextInt(((ListValue) childValues[0]).size());

        final Value value = ((ListValue) childValues[0]).get(randomIndex);

        if (value == null || value.isNull()) {
            return NullValue.INSTANCE;
        }

        return value;
    }
}
