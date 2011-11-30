/* $Id: StatsOperatorEvaluator.java 2766 2011-07-21 17:02:08Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

/**
 * Trivial little interface for evaluating each operator supported by
 * {@link StatsOperator}.
 * 
 * @author   David McKain
 * @revision $Revision: 2766 $
 */
interface StatsOperatorEvaluator {
    
    double evaluate(double[] arguments);
    
}