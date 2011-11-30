/* $Id: Operation.java 2642 2011-04-28 12:40:08Z davemckain $
*
* Copyright (c) 2011, The University of Edinburgh.
* All Rights Reserved
*/
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;


/**
 * Convenience base class for expressions like <tt>sum</tt>, <tt>max</tt> that
 * are evaluated by "mapping" over their values in a certain way.
 */
public abstract class MathMapExpression extends AbstractExpression {
    
    private static final long serialVersionUID = 5311729106818194456L;

    public MathMapExpression(ExpressionParent parent)    {
        super(parent);
    }

    @Override
    public final BaseType[] getProducedBaseTypes(ValidationContext context) {
        return getProducedNumericalBaseTypes(context);
    }
    
    @Override
    protected final Value evaluateSelf(ProcessingContext context, int depth) {
        BaseType baseType = BaseType.INTEGER;
        double running = initialValue();

        for (Expression subExpression : getChildren()) {
            if (subExpression.isNull(context)) {
                return NullValue.INSTANCE;
            }

            if (!subExpression.getBaseType(context).isInteger()) {
                baseType = BaseType.FLOAT;
            }
            if (subExpression.getCardinality(context).isSingle()) {
                running = foldr(running, ((NumberValue) subExpression.getValue(context)).doubleValue());
            }
            else {
                ListValue container = (ListValue) subExpression.getValue(context);
                for (int i = 0; i < container.size(); i++) {
                    running = foldr(running, ((NumberValue) container.get(i)).doubleValue());
                }
            }
        }
        
        return baseType.isInteger() ? new IntegerValue((int) running) : new FloatValue(running);
    }
    
    /** Subclasses should return the initial running "total" to use */
    protected abstract double initialValue();
    
    /** 
     * Subclasses should fill in to "fold" the current running "total"
     * with a new "value"
     */
    protected abstract double foldr(double running, double value);
    
}
