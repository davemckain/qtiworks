/* $Id: Operation.java 2642 2011-04-28 12:40:08Z davemckain $
*
* Copyright (c) 2011, The University of Edinburgh.
* All Rights Reserved
*/
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.Value;


import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates all of the operations supported by the <tt>mathOperator</tt> operator
 * 
 * @author   David McKain
 * @revision $Revision: 2642 $
 */
enum MathOperatorTarget {
    
    SIN("sin", 1, new ReflectionEvaluator("sin")),
    COS("cos", 1, new ReflectionEvaluator("cos")),
    TAN("tan", 1, new ReflectionEvaluator("tan")),
    SEC("sec", 1, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(1.0/Math.cos(arguments[0]));
        }
    }),
    CSC("csc", 1, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(1.0/Math.sin(arguments[0]));
        }
    }),
    COT("cot", 1, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(1.0/Math.tan(arguments[0]));
        }
    }),
    ASIN("asin", 1, new ReflectionEvaluator("asin")),
    ACOS("acos", 1, new ReflectionEvaluator("acos")),
    ATAN("atan", 1, new ReflectionEvaluator("atan")),
    ATAN2("atan2", 2, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(Math.atan2(arguments[0], arguments[1]));
        };
    }),
    ASEC("asec", 1, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(Math.acos(1.0/arguments[0]));
        }
    }),
    ACSC("acsc", 1, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(Math.asin(1.0/arguments[0]));
        }
    }),
    ACOT("acot", 1, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(Math.atan(1.0/arguments[0]));
        }
    }),
    SINH("sinh", 1, new ReflectionEvaluator("sinh")),
    COSH("cosh", 1, new ReflectionEvaluator("cosh")),
    TANH("tanh", 1, new ReflectionEvaluator("tanh")),
    SECH("sech", 1, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(1.0/Math.cosh(arguments[0]));
        }
    }),
    CSCH("csch", 1, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(1.0/Math.sinh(arguments[0]));
        }
    }),
    COTH("coth", 1, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(1.0/Math.tanh(arguments[0]));
        }
    }),
    LOG("log", 1, new ReflectionEvaluator("log10")),
    LN("ln", 1, new ReflectionEvaluator("log")),
    EXP("exp", 1, new ReflectionEvaluator("exp")),
    ABS("abs", 1, new ReflectionEvaluator("abs")),
    FLOOR("floor", 1, new ReflectionEvaluator("floor", true)),
    CEIL("ceil", 1, new ReflectionEvaluator("ceil", true)),
    SIGNUM("signum", 1, new ReflectionEvaluator("signum", true)),
    TO_DEGREES("toDegrees", 1, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(arguments[0] * 180.0 / Math.PI);
        }
    }),
    TO_RADIANS("toRadians", 1, new MathOperatorEvaluator() {
        public Value evaluate(double[] arguments) {
            return new FloatValue(arguments[0] * Math.PI / 180.0);
        }
    })

    ;
    
    private static Map<String, MathOperatorTarget> operations;
    static {
        operations = new HashMap<String, MathOperatorTarget>();
        for (MathOperatorTarget operation : MathOperatorTarget.values())
            operations.put(operation.getName(), operation);
    }
    
    private final String name;
    private final int argumentCount;
    private final MathOperatorEvaluator evaluator;
    
    private MathOperatorTarget(final String name, final int argumentCount, final MathOperatorEvaluator evaluator) {
        this.name = name;
        this.argumentCount = argumentCount;
        this.evaluator = evaluator;
    }

    public String getName() {
        return name;
    }

    public int getArgumentCount() {
        return argumentCount;
    }

    public MathOperatorEvaluator getEvaluator() {
        return evaluator;
    }
    
    public static MathOperatorTarget parseOperation(String name) {
        MathOperatorTarget result = operations.get(name);
        if (result == null) {
            throw new QTIParseException("Invalid mathOperator " + name);
        }
        return result;
    }
}