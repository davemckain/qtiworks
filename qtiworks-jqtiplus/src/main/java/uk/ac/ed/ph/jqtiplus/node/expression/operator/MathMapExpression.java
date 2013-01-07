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
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * Convenience base class for expressions like <tt>sum</tt>, <tt>max</tt> that
 * are evaluated by "mapping" over their values in a certain way.
 *
 * @author David McKain
 */
public abstract class MathMapExpression extends AbstractSimpleFunctionalExpression {

    private static final long serialVersionUID = 5311729106818194456L;

    public MathMapExpression(final ExpressionParent parent, final String qtiClassName) {
        super(parent, qtiClassName);
    }

    @Override
    public final BaseType[] getProducedBaseTypes(final ValidationContext context) {
        return getProducedNumericalBaseTypes(context);
    }

    @Override
    protected final Value evaluateValidSelf(final Value[] childValues) {
        BaseType baseType = BaseType.INTEGER;
        double running = initialValue();

        for (final Value childValue : childValues) {
            if (childValue.isNull()) {
                return NullValue.INSTANCE;
            }

            if (!childValue.getBaseType().isInteger()) {
                baseType = BaseType.FLOAT;
            }
            if (childValue.getCardinality().isSingle()) {
                running = foldr(running, ((NumberValue) childValue).doubleValue());
            }
            else {
                final ListValue container = (ListValue) childValue;
                for (int i = 0; i < container.size(); i++) {
                    running = foldr(running, ((NumberValue) container.get(i)).doubleValue());
                }
            }
        }

        return baseType.isInteger() ? new IntegerValue((int) running) : new FloatValue(running);
    }

    /** Subclasses should return the initial running "total" to use */
    protected abstract double initialValue();

    /**
     * Subclasses should fill in to "fold" the current running "total"
     * with a new "value"
     */
    protected abstract double foldr(double running, double value);

}
