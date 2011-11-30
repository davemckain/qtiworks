/* $Id: StatsOperator.java 2766 2011-07-21 17:02:08Z davemckain $
*
* Copyright (c) 2011, The University of Edinburgh.
* All Rights Reserved
*/
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;


import java.util.List;

/**
 * Implementation of <tt>statsOperator</tt>
 * 
 * @author   David McKain
 * @revision $Revision: 2766 $
 */
public class StatsOperator extends AbstractExpression {
    
    private static final long serialVersionUID = 709298090798424712L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "statsOperator";
    
    /** Name of 'name' attribute */
    public static final String ATTR_NAME_NAME = "name";
    

    public StatsOperator(ExpressionParent parent) {
        super(parent);
        
        getAttributes().add(new StatsOperatorNameAttribute(this, "name"));
    }
    
    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }
    
    @Override
    public ExpressionType getType() {
        return ExpressionType.STATS_OPERATOR;
    }
    
    public StatsOperatorTarget getTarget() {
        return ((StatsOperatorNameAttribute) getAttributes().get(ATTR_NAME_NAME)).getValue();
    }

    public void setTarget(StatsOperatorTarget target) {
        ((StatsOperatorNameAttribute) getAttributes().get(ATTR_NAME_NAME)).setValue(target);
    }

    @Override
    public final Value evaluateSelf(ProcessingContext context, int depth) {
        ListValue containerValue = (ListValue) getFirstChild().getValue(context);
        if (containerValue.isNull()) {
            return NullValue.INSTANCE;
        }
        
        List<SingleValue> containerItemValues = containerValue.getAll();
        double[] arguments = new double[containerItemValues.size()];
        for (int i=0; i<containerItemValues.size(); i++) {
            Value containerItemValue = containerItemValues.get(i);
            if (containerItemValue.isNull() || !containerItemValue.getBaseType().isNumeric()) {
                return NullValue.INSTANCE;
            }
            arguments[i] = ((NumberValue) containerItemValue).doubleValue();
        }

        /* Call up the appropriate operation's evaluator */
        StatsOperatorEvaluator evaluator = getTarget().getEvaluator();
        double result = evaluator.evaluate(arguments);
        return new FloatValue(result);
    }
}
