/* $Id: StatsOperatorTarget.java 2766 2011-07-21 17:02:08Z davemckain $
*
* Copyright (c) 2011, The University of Edinburgh.
* All Rights Reserved
*/
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates all of the operations supported by the <tt>statsOperator</tt> operator
 * 
 * @author   David McKain
 * @revision $Revision: 2766 $
 */
enum StatsOperatorTarget {
    
    MEAN("mean", new StatsOperatorEvaluator() {
        public double evaluate(double[] arguments) {
            return StatsFunctions.mean(arguments);
        }
    }),
    
    POP_VARIANCE("populationVariance", new StatsOperatorEvaluator() {
        public double evaluate(double[] arguments) {
            return StatsFunctions.populationVariance(arguments);
        }
    }),
    
    SAMPLE_VARIANCE("sampleVariance", new StatsOperatorEvaluator() {
        public double evaluate(double[] arguments) {
            return StatsFunctions.sampleVariance(arguments);
        }
    }),
    
    POP_SD("popSD", new StatsOperatorEvaluator() {
        public double evaluate(double[] arguments) {
            return StatsFunctions.populationSD(arguments);
        }
    }),

    SAMPLE_SD("sampleSD", new StatsOperatorEvaluator() {
        public double evaluate(double[] arguments) {
            return StatsFunctions.sampleSD(arguments);
        }
    })
    
    ;
    
    private static Map<String, StatsOperatorTarget> operations;
    static {
        operations = new HashMap<String, StatsOperatorTarget>();
        for (StatsOperatorTarget operation : StatsOperatorTarget.values())
            operations.put(operation.getName(), operation);
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
        StatsOperatorTarget result = operations.get(name);
        if (result == null) {
            throw new QTIParseException("Invalid statsOperator " + name);
        }
        return result;
    }
}