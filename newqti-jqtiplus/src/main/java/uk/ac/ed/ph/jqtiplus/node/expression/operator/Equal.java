/* Copyright (c) 2012, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ToleranceModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.AttributeValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * The equal operator takes two sub-expressions which must both have single cardinality and have A
 * numerical base-type. The result is A single boolean with A value of true if the two expressions
 * are numerically equal and false if they are not. If either sub-expression is NULL then the
 * operator results in NULL.
 * <p>
 * Attribute : toleranceMode [1]: toleranceMode = exact
 * <p>
 * When comparing two floating point numbers for equality it is often desirable to have A tolerance to ensure that spurious errors in scoring are not introduced
 * by rounding errors. The tolerance mode determines whether the comparison is done exactly, using an absolute range or A relative range.
 * <p>
 * Attribute : tolerance [0..2]: floatOrTemplateRef
 * <p>
 * If the tolerance mode is absolute or relative then the tolerance must be specified. The tolerance consists of two positive numbers, t0 and t1, that define
 * the lower and upper bounds. If only one value is given it is used for both.
 * <p>
 * In absolute mode the result of the comparison is true if the value of the second expression, y is within the following range defined by the first value, x.
 * <p>
 * x-t0,x+t1
 * <p>
 * In relative mode, t0 and t1 are treated as percentages and the following range is used instead.
 * <p>
 * x*(1-t0/100),x*(1+t1/100)
 * <p>
 * Attribute : includeLowerBound [0..1]: boolean = true
 * <p>
 * Controls whether or not the lower bound is included in the comparison
 * <p>
 * Attribute : includeUpperBound [0..1]: boolean = true
 * <p>
 * Controls whether or not the upper bound is included in the comparison
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public class Equal extends AbstractExpression {

    private static final long serialVersionUID = 2741395727993314516L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "equal";

    /** Name of toleranceMode attribute in xml schema. */
    public static final String ATTR_TOLERANCE_MODE_NAME = ToleranceMode.CLASS_TAG;

    /** Name of tolerance attribute in xml schema. */
    public static final String ATTR_TOLERANCES_NAME = "tolerance";

    /** Default value of tolerance attribute. */
    public static final List<Double> ATTR_TOLERANCES_DEFAULT_VALUE = null;

    /** Name of includeLowerBound attribute in xml schema. */
    public static final String ATTR_INCLUDE_LOWER_BOUND_NAME = "includeLowerBound";

    /** Default value of includeLowerBound attribute. */
    public static final boolean ATTR_INCLUDE_LOWER_BOUND_DEFAULT_VALUE = true;

    /** Name of includeUpperBound attribute in xml schema. */
    public static final String ATTR_INCLUDE_UPPER_BOUND_NAME = "includeUpperBound";

    /** Default value of incluseUpperBound attribute. */
    public static final boolean ATTR_INCLUDE_UPPER_BOUND_DEFAULT_VALUE = true;

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public Equal(ExpressionParent parent) {
        super(parent);

        getAttributes().add(new ToleranceModeAttribute(this, ATTR_TOLERANCE_MODE_NAME));
        getAttributes().add(new FloatMultipleAttribute(this, ATTR_TOLERANCES_NAME, ATTR_TOLERANCES_DEFAULT_VALUE));
        getAttributes().add(new BooleanAttribute(this, ATTR_INCLUDE_LOWER_BOUND_NAME, ATTR_INCLUDE_LOWER_BOUND_DEFAULT_VALUE));
        getAttributes().add(new BooleanAttribute(this, ATTR_INCLUDE_UPPER_BOUND_NAME, ATTR_INCLUDE_UPPER_BOUND_DEFAULT_VALUE));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    /**
     * Gets value of toleranceMode attribute.
     * 
     * @return value of toleranceMode attribute
     * @see #setToleranceMode
     */
    public ToleranceMode getToleranceMode() {
        return getAttributes().getToleranceModeAttribute(ATTR_TOLERANCE_MODE_NAME).getValue();
    }

    /**
     * Sets new value of toleranceMode attribute.
     * 
     * @param toleranceMode new value of toleranceMode attribute
     * @see #getToleranceMode
     */
    public void setToleranceMode(ToleranceMode toleranceMode) {
        getAttributes().getToleranceModeAttribute(ATTR_TOLERANCE_MODE_NAME).setValue(toleranceMode);
    }

    /**
     * Gets value of tolerance attribute.
     * 
     * @return value of tolerance attribute
     */
    public List<Double> getTolerances() {
        return getAttributes().getFloatMultipleAttribute(ATTR_TOLERANCES_NAME).getValues();
    }

    /**
     * Gets first tolerance if defined; zero otherwise.
     * 
     * @return first tolerance if defined; zero otherwise
     */
    protected double getFirstTolerance() {
        return getTolerances().size() > 0 ? getTolerances().get(0) : 0;
    }

    /**
     * Gets second tolerance if defined; first tolerance otherwise.
     * 
     * @return second tolerance if defined; first tolerance otherwise
     */
    protected double getSecondTolerance() {
        return getTolerances().size() > 1 ? getTolerances().get(1) : getFirstTolerance();
    }

    /**
     * Gets value of includeLowerBound attribute.
     * 
     * @return value of includeLowerBound attribute
     * @see #setIncludeLowerBound
     */
    public Boolean getIncludeLowerBound() {
        return getAttributes().getBooleanAttribute(ATTR_INCLUDE_LOWER_BOUND_NAME).getValue();
    }

    /**
     * Sets new value of includeLowerBound attribute.
     * 
     * @param includeLowerBound new value of includeLowerBound attribute
     * @see #getIncludeLowerBound
     */
    public void setIncludeLowerBound(Boolean includeLowerBound) {
        getAttributes().getBooleanAttribute(ATTR_INCLUDE_LOWER_BOUND_NAME).setValue(includeLowerBound);
    }

    /**
     * Gets value of includeUpperBound attribute.
     * 
     * @return value of includeUpperBound attribute
     * @see #setIncludeUpperBound
     */
    public Boolean getIncludeUpperBound() {
        return getAttributes().getBooleanAttribute(ATTR_INCLUDE_UPPER_BOUND_NAME).getValue();
    }

    /**
     * Sets new value of includeUpperBound attribute.
     * 
     * @param includeUpperBound new value of includeUpperBound attribute
     * @see #getIncludeUpperBound
     */
    public void setIncludeUpperBound(Boolean includeUpperBound) {
        getAttributes().getBooleanAttribute(ATTR_INCLUDE_UPPER_BOUND_NAME).setValue(includeUpperBound);
    }

    @Override
    protected void validateAttributes(ValidationContext context) {
        super.validateAttributes(context);

        if (getFirstTolerance() < 0) {
            context.add(new AttributeValidationError(getAttributes().get(ATTR_TOLERANCES_NAME), "Attribute " + ATTR_TOLERANCES_NAME + " (" +
                    getFirstTolerance() + ") cannot be negative."));
        }

        if (getSecondTolerance() < 0) {
            context.add(new AttributeValidationError(getAttributes().get(ATTR_TOLERANCES_NAME), "Attribute " + ATTR_TOLERANCES_NAME + " (" +
                    getSecondTolerance() + ") cannot be negative."));
        }

        if (getToleranceMode() != null && getToleranceMode() != ToleranceMode.EXACT &&
                (getTolerances().size() == 0 || getTolerances().size() > 2)) {
            context.add(new AttributeValidationError(getAttributes().get(ATTR_TOLERANCES_NAME), "Invalid attribute " +
                    ATTR_TOLERANCES_NAME + " length (" + getTolerances().size() + ")."));
        }
    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth) {
        if (isAnyChildNull(context)) {
            return NullValue.INSTANCE;
        }

        final double firstNumber = ((NumberValue) getFirstChild().getValue(context)).doubleValue();
        final double secondNumber = ((NumberValue) getSecondChild().getValue(context)).doubleValue();

        final boolean result = getToleranceMode().isEqual
                (firstNumber
                        , secondNumber
                        , getFirstTolerance()
                        , getSecondTolerance()
                        , getIncludeLowerBound()
                        , getIncludeUpperBound());

        return BooleanValue.valueOf(result);
    }
}
