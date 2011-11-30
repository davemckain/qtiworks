/* $Id: StatsOperatorNameAttribute.java 2766 2011-07-21 17:02:08Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.attribute.EnumerateAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.SingleAttribute;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;


/**
 * Wraps {@link StatsOperator} attribute up in QTI style
 * 
 * @author   David McKain
 * @revision $Revision: 2766 $
 */
public class StatsOperatorNameAttribute extends SingleAttribute implements EnumerateAttribute {
    
    private static final long serialVersionUID = -2367561162140765443L;

    public StatsOperatorNameAttribute(XmlNode parent, String name) {
        super(parent, name);
    }

    @Override
    public StatsOperatorTarget getValue() {
        return (StatsOperatorTarget) super.getValue();
    }

    public void setValue(StatsOperatorTarget value){
        super.setValue(value);
    }

    @Override
    public StatsOperatorTarget getDefaultValue() {
        return (StatsOperatorTarget) super.getDefaultValue();
    }

    public void setDefaultValue(StatsOperatorTarget defaultValue) {
        super.setDefaultValue(defaultValue);
    }

    @Override
    protected StatsOperatorTarget parseValue(String value) {
        return StatsOperatorTarget.parseOperation(value);
    }

    public StatsOperatorTarget[] getSupportedValues() {
        return StatsOperatorTarget.values();
    }
}