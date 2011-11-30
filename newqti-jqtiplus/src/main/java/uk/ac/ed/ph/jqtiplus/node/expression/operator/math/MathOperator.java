/* $Id: MathEvaluator.java 2642 2011-04-28 12:40:08Z davemckain $
*
* Copyright (c) 2011, The University of Edinburgh.
* All Rights Reserved
*/
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;


import java.util.List;

/**
 * Implementation of <tt>mathOperator</tt>
 * 
 * @author   David McKain
 * @revision $Revision: 2652 $
 */
public class MathOperator extends AbstractExpression {
    
    private static final long serialVersionUID = 709298090798424712L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "mathOperator";
    
    /** Name of 'name' attribute */
    public static final String ATTR_NAME_NAME = "name";
    

    public MathOperator(ExpressionParent parent) {
        super(parent);
        
        getAttributes().add(new MathOperatorNameAttribute(this, "name"));
    }
    
    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }
    
    @Override
    public ExpressionType getType() {
        return ExpressionType.MATH_OPERATOR;
    }
    
    public MathOperatorTarget getTarget() {
        return ((MathOperatorNameAttribute) getAttributes().get(ATTR_NAME_NAME)).getValue();
    }

    public void setTarget(MathOperatorTarget target) {
        ((MathOperatorNameAttribute) getAttributes().get(ATTR_NAME_NAME)).setValue(target);
    }

    @Override
    public final Value evaluateSelf(ProcessingContext context, int depth) {
        if (isAnyChildNull(context)) {
            return NullValue.INSTANCE;
        }

        /* Convert each argument value to double, short-circuiting if any argument is null */
        List<Value> childValues = getChildValues(context);
        double[] arguments = new double[childValues.size()];
        for (int i=0; i<childValues.size(); i++) {
            Value childValue = childValues.get(i);
            if (childValue.isNull() || !childValue.getBaseType().isNumeric()) {
                return NullValue.INSTANCE;
            }
            arguments[i] = ((NumberValue) childValue).doubleValue();
        }
        
        /* Call up the appropriate operation's evaluator */
        MathOperatorEvaluator evaluator = getTarget().getEvaluator();
        return evaluator.evaluate(arguments);
    }

    @Override
    public ValidationResult validate(ValidationContext context) {
        ValidationResult result = super.validate(context);

        /* Make sure number of children is correct */
        MathOperatorTarget operation = getTarget();
        if (operation!=null && (getChildren().size() != operation.getArgumentCount())) {
            result.add(new ValidationError(this, "Operation " + operation.getName()
                    + " expects " + operation.getArgumentCount() + " children, not "
                    + getChildren().size()));
        }
        return result;
    }
}
