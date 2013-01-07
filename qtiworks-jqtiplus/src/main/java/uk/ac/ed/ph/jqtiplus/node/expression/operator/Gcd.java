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
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.math.BigInteger;

/**
 * Implementation of the <tt>gcd</tt> expression
 *
 * @author David McKain
 */
public final class Gcd extends AbstractSimpleFunctionalExpression {

    private static final long serialVersionUID = -3567164808598142551L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "gcd";

    public Gcd(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);
    }

    @Override
    public final BaseType[] getProducedBaseTypes(final ValidationContext context) {
        return new BaseType[] { BaseType.INTEGER };
    }

    @Override
    protected final Value evaluateValidSelf(final Value[] childValues) {
        BigInteger runningGcd = null; /* (Will become non-null whenever first non-NULL descendant is found) */
        for (final Value childValue : childValues) {
            /* (Spec says any NULL -> NULL) */
            if (childValue.isNull()) {
                return NullValue.INSTANCE;
            }

            if (childValue.getCardinality().isSingle()) {
                final int childInteger = ((IntegerValue) childValue).intValue();
                if (childInteger!=0) {
                    final BigInteger childBigInteger = BigInteger.valueOf(childInteger);
                    runningGcd = runningGcd!=null ? runningGcd.gcd(childBigInteger) : childBigInteger;
                }
            }
            else {
                final ListValue container = (ListValue) childValue;
                for (int i = 0; i < container.size(); i++) {
                    final int descendantInteger = ((IntegerValue) container.get(i)).intValue();
                    if (descendantInteger!=0) {
                        final BigInteger descendantBigInteger = BigInteger.valueOf(descendantInteger);
                        runningGcd = runningGcd!=null ? runningGcd.gcd(descendantBigInteger) : descendantBigInteger;
                    }
                }
            }
        }
        return runningGcd!=null ? new IntegerValue(runningGcd.intValue()) : IntegerValue.ZERO;
    }
}
