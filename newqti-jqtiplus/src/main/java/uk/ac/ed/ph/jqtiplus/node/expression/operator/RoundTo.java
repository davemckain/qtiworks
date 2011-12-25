/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.RoundingModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.math.BigDecimal;

/**
 * Implementation of the <tt>roundTo</tt> expression
 * 
 * @author David McKain
 * @version $Revision: 2649 $
 */
public class RoundTo extends AbstractExpression {

    private static final long serialVersionUID = 7637604241884891345L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "roundTo";

    /** Name of roundingMode attribute in xml schema. */
    public static final String ATTR_ROUNDING_MODE_NAME = RoundingMode.CLASS_TAG;

    /** Name of figures attribute in xml schema. */
    public static final String ATTR_FIGURES_NAME = "figures";

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public RoundTo(ExpressionParent parent) {
        super(parent);

        getAttributes().add(new RoundingModeAttribute(this, ATTR_ROUNDING_MODE_NAME));
        getAttributes().add(new IntegerAttribute(this, ATTR_FIGURES_NAME));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    /**
     * Gets value of roundingMode attribute.
     * 
     * @return value of roundingMode attribute
     * @see #setRoundingMode
     */
    public RoundingMode getRoundingMode() {
        return getAttributes().getRoundingModeAttribute(ATTR_ROUNDING_MODE_NAME).getValue();
    }

    /**
     * Sets new value of roundingMode attribute.
     * 
     * @param roundingMode new value of roundingMode attribute
     * @see #getRoundingMode
     */
    public void setRoundingMode(RoundingMode roundingMode) {
        getAttributes().getRoundingModeAttribute(ATTR_ROUNDING_MODE_NAME).setValue(roundingMode);
    }

    /**
     * Gets value of figures attribute.
     * 
     * @return value of figures attribute
     * @see #setFigures
     */
    public Integer getFigures() {
        return getAttributes().getIntegerAttribute(ATTR_FIGURES_NAME).getValue();
    }

    /**
     * Sets new value of figures attribute.
     * 
     * @param figures new value of figures attribute
     * @see #getFigures
     */
    public void setFigures(Integer figures) {
        getAttributes().getIntegerAttribute(ATTR_FIGURES_NAME).setValue(figures);
    }

    @Override
    protected void validateAttributes(ValidationContext context, ValidationResult result) {
        super.validateAttributes(context, result);

        if (getRoundingMode() != null && getFigures() != null)
            getRoundingMode().validateFigures(getAttributes().get(ATTR_FIGURES_NAME), result,
                    getFigures());
    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth) {
        Expression child = getFirstChild();
        if (child.isNull(context)) {
            return NullValue.INSTANCE;
        }
        
        double childNumber = ((NumberValue) child.getValue(context)).doubleValue();
        BigDecimal rounded = getRoundingMode().round(childNumber, getFigures().intValue());
        return new FloatValue(rounded.doubleValue());
    }
}
