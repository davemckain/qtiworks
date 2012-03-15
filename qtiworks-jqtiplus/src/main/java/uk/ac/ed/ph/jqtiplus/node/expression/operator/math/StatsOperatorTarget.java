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
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates all of the operations supported by the <tt>statsOperator</tt> operator
 * 
 * @author David McKain
 * @revision $Revision: 2766 $
 */
enum StatsOperatorTarget {

    MEAN("mean", new StatsOperatorEvaluator() {

        @Override
        public double evaluate(double[] arguments) {
            return StatsFunctions.mean(arguments);
        }
    }),

    POP_VARIANCE("populationVariance", new StatsOperatorEvaluator() {

        @Override
        public double evaluate(double[] arguments) {
            return StatsFunctions.populationVariance(arguments);
        }
    }),

    SAMPLE_VARIANCE("sampleVariance", new StatsOperatorEvaluator() {

        @Override
        public double evaluate(double[] arguments) {
            return StatsFunctions.sampleVariance(arguments);
        }
    }),

    POP_SD("popSD", new StatsOperatorEvaluator() {

        @Override
        public double evaluate(double[] arguments) {
            return StatsFunctions.populationSD(arguments);
        }
    }),

    SAMPLE_SD("sampleSD", new StatsOperatorEvaluator() {

        @Override
        public double evaluate(double[] arguments) {
            return StatsFunctions.sampleSD(arguments);
        }
    })

    ;

    private static Map<String, StatsOperatorTarget> operations;
    static {
        operations = new HashMap<String, StatsOperatorTarget>();
        for (final StatsOperatorTarget operation : StatsOperatorTarget.values()) {
            operations.put(operation.getName(), operation);
        }
    }

    private final String name;

    private final StatsOperatorEvaluator evaluator;

    private StatsOperatorTarget(final String name, final StatsOperatorEvaluator evaluator) {
        this.name = name;
        this.evaluator = evaluator;
    }

    public String getName() {
        return name;
    }

    public StatsOperatorEvaluator getEvaluator() {
        return evaluator;
    }

    public static StatsOperatorTarget parseOperation(String name) {
        final StatsOperatorTarget result = operations.get(name);
        if (result == null) {
            throw new QtiParseException("Invalid statsOperator " + name);
        }
        return result;
    }
}