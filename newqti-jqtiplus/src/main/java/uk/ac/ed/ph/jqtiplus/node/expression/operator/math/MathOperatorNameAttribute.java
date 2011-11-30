/* $Id: OperationAttribute.java 2642 2011-04-28 12:40:08Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.attribute.EnumerateAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.SingleAttribute;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;


/**
 * Wraps {@link MathOperator} attribute up in QTI style
 * 
 * @author   David McKain
 * @revision $Revision: 2642 $
 */
public class MathOperatorNameAttribute extends SingleAttribute implements EnumerateAttribute {
    
    private static final long serialVersionUID = -2367561162140765443L;

    public MathOperatorNameAttribute(XmlNode parent, String name) {
        super(parent, name);
    }

    @Override
    public MathOperatorTarget getValue() {
        return (MathOperatorTarget) super.getValue();
    }

    public void setValue(MathOperatorTarget value){
        super.setValue(value);
    }

    @Override
    public MathOperatorTarget getDefaultValue() {
        return (MathOperatorTarget) super.getDefaultValue();
    }

    public void setDefaultValue(MathOperatorTarget defaultValue) {
        super.setDefaultValue(defaultValue);
    }

    @Override
    protected MathOperatorTarget parseValue(String value) {
        return MathOperatorTarget.parseOperation(value);
    }

    public MathOperatorTarget[] getSupportedValues() {
        return MathOperatorTarget.values();
    }
}