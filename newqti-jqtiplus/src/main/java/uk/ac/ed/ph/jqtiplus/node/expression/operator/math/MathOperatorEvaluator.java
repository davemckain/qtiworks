/* $Id: MathEvaluator.java 2642 2011-04-28 12:40:08Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * Trivial little interface for evaluating each operator supported by
 * {@link MathOperator}.
 * 
 * @author   David McKain
 * @revision $Revision: 2642 $
 */
interface MathOperatorEvaluator {
    
    Value evaluate(double[] arguments);
    
}