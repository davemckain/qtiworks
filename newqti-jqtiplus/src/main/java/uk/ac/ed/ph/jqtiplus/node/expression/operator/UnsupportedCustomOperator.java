/* $Id: UnsupportedCustomOperator.java 2766 2011-07-21 17:02:08Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node.expression.operator;


import uk.ac.ed.ph.jqtiplus.control.JQTIExtensionPackage;
import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an unsupported customOperator. This belongs to no {@link JQTIExtensionPackage}
 * 
 * @author  David McKain
 * @version $Revision: 2766 $
 */
public final class UnsupportedCustomOperator extends CustomOperator {

    private static final long serialVersionUID = -8733871136419512506L;
    
    private static Logger logger = LoggerFactory.getLogger(UnsupportedCustomOperator.class);
    
    public UnsupportedCustomOperator(ExpressionParent parent) {
        super(null, parent);
    }

    @Override
    public ValidationResult validate(ValidationContext context) {
        ValidationResult result = new ValidationResult();
        result.add(new ValidationWarning(this, "customOperator with class attribute " + getClassAttr() + " is not supported"));
        return result;
    }
    
    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth) {
        logger.warn("customOperator with class attribute {} is not supported - returning NULL", getClassAttr());
        return NullValue.INSTANCE;
    }
}
