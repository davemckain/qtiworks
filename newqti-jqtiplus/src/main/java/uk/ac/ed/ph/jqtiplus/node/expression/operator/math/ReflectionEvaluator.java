/* $Id: ReflectionEvaluator.java 2652 2011-05-02 16:20:45Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.Value;


import java.lang.reflect.Method;

/**
 * Implementation of {@link MathOperatorEvaluator} that uses reflection to call up a
 * method in the standard {@link Math} class.
 * 
 * @author   David McKain
 * @revision $Revision: 2652 $
 */
final class ReflectionEvaluator implements MathOperatorEvaluator {
    
    private final String methodName;
    private final boolean wantInteger;
    
    public ReflectionEvaluator(final String methodName) {
        this(methodName, false);
    }
    
    public ReflectionEvaluator(final String methodName, boolean wantInteger) {
        this.methodName = methodName;
        this.wantInteger = wantInteger;
    }
    
    public Value evaluate(double[] arguments) {
        try {
            Class<?> mathClass = Class.forName("java.lang.Math");
            Method method = mathClass.getMethod(methodName, Double.TYPE);
            double result = ((Double) method.invoke(mathClass, Double.valueOf(arguments[0]))).doubleValue();
            return wantInteger ? new IntegerValue((int) result) : new FloatValue(result);
        }
        catch (Exception e) {
            throw new QTIEvaluationException("Unexpected error evaluating math operator", e);
        }
    }
}