/* $Id: Operation.java 2642 2011-04-28 12:40:08Z davemckain $
*
* Copyright (c) 2011, The University of Edinburgh.
* All Rights Reserved
*/
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;

/**
 * Implementation of <tt>max</tt>
 */
public class Max extends MathMapExpression {
    
    private static final long serialVersionUID = 226234156269457952L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "max";

    /**
     * Constructs expression.
     *
     * @param parent parent of this expression
     */
    public Max(ExpressionParent parent)    {
        super(parent);
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }
    
    @Override
    protected double initialValue() {
        return Double.NEGATIVE_INFINITY;
    }
    
    @Override
    protected double foldr(double running, double value) {
        return Math.max(running, value);
    }
}
