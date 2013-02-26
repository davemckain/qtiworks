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
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.lang.reflect.Method;

/**
 * Implementation of {@link MathOperatorEvaluator} that uses reflection to call up a
 * method in the standard {@link Math} class.
 *
 * @author David McKain
 */
final class ReflectionEvaluator implements MathOperatorEvaluator {

    private static final long serialVersionUID = 5961647493853845432L;

    private final String methodName;

    private final boolean wantInteger;

    public ReflectionEvaluator(final String methodName) {
        this(methodName, false);
    }

    public ReflectionEvaluator(final String methodName, final boolean wantInteger) {
        this.methodName = methodName;
        this.wantInteger = wantInteger;
    }

    @Override
    public Value evaluate(final double[] arguments) {
        try {
            final Class<?> mathClass = Class.forName("java.lang.Math");
            final Method method = mathClass.getMethod(methodName, Double.TYPE);
            final double result = ((Double) method.invoke(mathClass, Double.valueOf(arguments[0]))).doubleValue();
            return wantInteger ? new IntegerValue((int) result) : new FloatValue(result);
        }
        catch (final Exception e) {
            throw new QtiLogicException("Unexpected error evaluating math operator", e);
        }
    }
}