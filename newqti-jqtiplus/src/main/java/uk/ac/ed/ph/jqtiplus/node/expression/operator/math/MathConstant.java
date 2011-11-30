/* $Id: MathEvaluator.java 2642 2011-04-28 12:40:08Z davemckain $
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
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;


/**
 * Implementation of the <tt>mathConstant</tt> operator.
 * 
 * @author   David McKain
 * @revision $Revision: 2652 $
 */
public class MathConstant extends AbstractExpression {
    
    private static final long serialVersionUID = 709298090798424712L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "mathConstant";
    
    /** Name of 'name' attribute */
    public static final String ATTR_NAME_NAME = "name";

    public MathConstant(ExpressionParent parent) {
        super(parent);
        
        getAttributes().add(new MathConstantNameAttribute(this, "name"));
    }
    
    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }
    
    @Override
    public ExpressionType getType() {
        return ExpressionType.MATH_CONSTANT;
    }
    
    public MathConstantTarget getConstant() {
        return ((MathConstantNameAttribute) getAttributes().get(ATTR_NAME_NAME)).getValue();
    }

    public void setConstant(MathConstantTarget roundingMode) {
        ((MathConstantNameAttribute) getAttributes().get(ATTR_NAME_NAME)).setValue(roundingMode);
    }

    @Override
    public final Value evaluateSelf(ProcessingContext context, int depth) {
        MathConstantTarget constant = getConstant();
        return constant!=null ? new FloatValue(getConstant().getValue()) : NullValue.INSTANCE;
    }
}
