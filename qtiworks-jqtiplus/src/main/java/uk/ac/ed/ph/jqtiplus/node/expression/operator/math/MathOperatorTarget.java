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

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates all of the operations supported by the <tt>mathOperator</tt> operator
 *
 * @author David McKain
 */
enum MathOperatorTarget implements Stringifiable {

    SIN("sin", 1, new ReflectionEvaluator("sin")),
    COS("cos", 1, new ReflectionEvaluator("cos")),
    TAN("tan", 1, new ReflectionEvaluator("tan")),
    SEC("sec", 1, new MathOperatorEvaluator() {

        private static final long serialVersionUID = 5952861432197925478L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(1.0 / Math.cos(arguments[0]));
        }
    }),
    CSC("csc", 1, new MathOperatorEvaluator() {

        private static final long serialVersionUID = 2211612606822387935L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(1.0 / Math.sin(arguments[0]));
        }
    }),
    COT("cot", 1, new MathOperatorEvaluator() {

        private static final long serialVersionUID = -7829778905281551485L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(1.0 / Math.tan(arguments[0]));
        }
    }),
    ASIN("asin", 1, new ReflectionEvaluator("asin")),
    ACOS("acos", 1, new ReflectionEvaluator("acos")),
    ATAN("atan", 1, new ReflectionEvaluator("atan")),
    ATAN2("atan2", 2, new MathOperatorEvaluator() {

        private static final long serialVersionUID = -8627119856230662538L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(Math.atan2(arguments[0], arguments[1]));
        }
    }),
    ASEC("asec", 1, new MathOperatorEvaluator() {

        private static final long serialVersionUID = 810517144482158120L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(Math.acos(1.0 / arguments[0]));
        }
    }),
    ACSC("acsc", 1, new MathOperatorEvaluator() {

        private static final long serialVersionUID = 26830091793676045L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(Math.asin(1.0 / arguments[0]));
        }
    }),
    ACOT("acot", 1, new MathOperatorEvaluator() {

        private static final long serialVersionUID = -8608437044005964279L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(Math.atan(1.0 / arguments[0]));
        }
    }),
    SINH("sinh", 1, new ReflectionEvaluator("sinh")),
    COSH("cosh", 1, new ReflectionEvaluator("cosh")),
    TANH("tanh", 1, new ReflectionEvaluator("tanh")),
    SECH("sech", 1, new MathOperatorEvaluator() {

        private static final long serialVersionUID = -2842606985174060779L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(1.0 / Math.cosh(arguments[0]));
        }
    }),
    CSCH("csch", 1, new MathOperatorEvaluator() {

        private static final long serialVersionUID = 6866168553708026037L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(1.0 / Math.sinh(arguments[0]));
        }
    }),
    COTH("coth", 1, new MathOperatorEvaluator() {

        private static final long serialVersionUID = 2969228173093469241L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(1.0 / Math.tanh(arguments[0]));
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

        private static final long serialVersionUID = -2561656693154640426L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(arguments[0] * 180.0 / Math.PI);
        }
    }),
    TO_RADIANS("toRadians", 1, new MathOperatorEvaluator() {

        private static final long serialVersionUID = -5278220909027742114L;

        @Override
        public Value evaluate(final double[] arguments) {
            return new FloatValue(arguments[0] * Math.PI / 180.0);
        }
    })

    ;

    private static Map<String, MathOperatorTarget> operations;
    static {
        operations = new HashMap<String, MathOperatorTarget>();
        for (final MathOperatorTarget operation : MathOperatorTarget.values()) {
            operations.put(operation.name, operation);
        }
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

    @Override
    public String toQtiString() {
        return name;
    }

    public static MathOperatorTarget parseOperation(final String name) {
        final MathOperatorTarget result = operations.get(name);
        if (result == null) {
            throw new QtiParseException("Invalid mathOperator " + name);
        }
        return result;
    }
}