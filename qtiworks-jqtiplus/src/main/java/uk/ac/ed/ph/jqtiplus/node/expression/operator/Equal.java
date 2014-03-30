/* Copyright (c) 2012-2013, University of Edinburgh.
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

import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ToleranceModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatOrVariableRefMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.FloatOrVariableRef;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * The equal operator takes two sub-expressions which must both have single cardinality and have A
 * numerical base-type. The result is a single boolean with a value of true if the two expressions
 * are numerically equal and false if they are not. If either sub-expression is NULL then the
 * operator results in NULL.
 * <p>
 * Attribute : toleranceMode [1]: toleranceMode = exact
 * <p>
 * When comparing two floating point numbers for equality it is often desirable to have a tolerance to ensure that spurious errors in scoring are not introduced
 * by rounding errors. The tolerance mode determines whether the comparison is done exactly, using an absolute range or a relative range.
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
public final class Equal extends AbstractFunctionalExpression {

    private static final long serialVersionUID = 2741395727993314516L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "equal";

    /** Name of toleranceMode attribute in xml schema. */
    public static final String ATTR_TOLERANCE_MODE_NAME = ToleranceMode.QTI_CLASS_NAME;

    /** Name of tolerance attribute in xml schema. */
    public static final String ATTR_TOLERANCES_NAME = "tolerance";

    /** Name of includeLowerBound attribute in xml schema. */
    public static final String ATTR_INCLUDE_LOWER_BOUND_NAME = "includeLowerBound";

    /** Default value of includeLowerBound attribute. */
    public static final boolean ATTR_INCLUDE_LOWER_BOUND_DEFAULT_VALUE = true;

    /** Name of includeUpperBound attribute in xml schema. */
    public static final String ATTR_INCLUDE_UPPER_BOUND_NAME = "includeUpperBound";

    /** Default value of incluseUpperBound attribute. */
    public static final boolean ATTR_INCLUDE_UPPER_BOUND_DEFAULT_VALUE = true;

    public Equal(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new ToleranceModeAttribute(this, ATTR_TOLERANCE_MODE_NAME, true));
        getAttributes().add(new FloatOrVariableRefMultipleAttribute(this, ATTR_TOLERANCES_NAME, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_INCLUDE_LOWER_BOUND_NAME, ATTR_INCLUDE_LOWER_BOUND_DEFAULT_VALUE, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_INCLUDE_UPPER_BOUND_NAME, ATTR_INCLUDE_UPPER_BOUND_DEFAULT_VALUE, false));
    }


    public ToleranceMode getToleranceMode() {
        return getAttributes().getToleranceModeAttribute(ATTR_TOLERANCE_MODE_NAME).getComputedValue();
    }

    public void setToleranceMode(final ToleranceMode toleranceMode) {
        getAttributes().getToleranceModeAttribute(ATTR_TOLERANCE_MODE_NAME).setValue(toleranceMode);
    }


    public List<FloatOrVariableRef> getTolerances() {
        return getAttributes().getFloatOrVariableRefMultipleAttribute(ATTR_TOLERANCES_NAME).getValue();
    }

    public void setTolerances(final List<FloatOrVariableRef> value) {
        getAttributes().getFloatOrVariableRefMultipleAttribute(ATTR_TOLERANCES_NAME).setValue(value);
    }

    /**
     * Gets first tolerance if defined; null otherwise.
     */
    protected FloatOrVariableRef getFirstTolerance() {
        final List<FloatOrVariableRef> tolerances = getTolerances();
        return tolerances!=null && tolerances.size()>0 ? tolerances.get(0) : null;
    }

    /**
     * Gets second tolerance if defined; first tolerance otherwise, or null if none are defined
     */
    protected FloatOrVariableRef getSecondTolerance() {
        final List<FloatOrVariableRef> tolerances = getTolerances();
        return tolerances!=null && tolerances.size()>1 ? tolerances.get(1) : getFirstTolerance();
    }

    public boolean getIncludeLowerBound() {
        return getAttributes().getBooleanAttribute(ATTR_INCLUDE_LOWER_BOUND_NAME).getComputedNonNullValue();
    }

    public void setIncludeLowerBound(final Boolean includeLowerBound) {
        getAttributes().getBooleanAttribute(ATTR_INCLUDE_LOWER_BOUND_NAME).setValue(includeLowerBound);
    }


    public boolean getIncludeUpperBound() {
        return getAttributes().getBooleanAttribute(ATTR_INCLUDE_UPPER_BOUND_NAME).getComputedNonNullValue();
    }

    public void setIncludeUpperBound(final Boolean includeUpperBound) {
        getAttributes().getBooleanAttribute(ATTR_INCLUDE_UPPER_BOUND_NAME).setValue(includeUpperBound);
    }


    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Attribute<?> tolerancesAttr = getAttributes().get(ATTR_TOLERANCES_NAME);
        final List<FloatOrVariableRef> tolerances = getTolerances();
        if (tolerances!=null && (tolerances.size()==0 || tolerances.size()>2)) {
            context.fireAttributeValidationError(tolerancesAttr,
                    "Attribute " + ATTR_TOLERANCES_NAME + " must contain 1 or 2 floatOrVariableRefs when specified");
        }

        final FloatOrVariableRef firstToleranceComputer = getFirstTolerance();
        if (firstToleranceComputer!=null && firstToleranceComputer.isConstantFloat()) {
            final double firstToleranceValue = firstToleranceComputer.getConstantFloatValue().doubleValue();
            if (firstToleranceValue <= 0) {
                context.fireAttributeValidationError(tolerancesAttr,
                        "Attribute " + ATTR_TOLERANCES_NAME + " (" + firstToleranceValue + ") must be positive");
            }
        }

        final FloatOrVariableRef secondToleranceComputer = getSecondTolerance();
        if (secondToleranceComputer!=null && secondToleranceComputer.isConstantFloat()) {
            final double secondToleranceValue = secondToleranceComputer.getConstantFloatValue().doubleValue();
            if (secondToleranceValue < 0) {
                context.fireAttributeValidationError(tolerancesAttr,
                        "Attribute " + ATTR_TOLERANCES_NAME + " (" + getSecondTolerance() + ") must be positive if specified");
            }
        }

        if (getToleranceMode() != null && getToleranceMode() != ToleranceMode.EXACT && tolerances==null) {
            context.fireAttributeValidationError(tolerancesAttr,
                    "Attribute " + ATTR_TOLERANCES_NAME + " must be specified when toleranceMode is absolute or relative");
        }
    }

    @Override
    protected Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        final double firstNumber = ((NumberValue) childValues[0]).doubleValue();
        final double secondNumber = ((NumberValue) childValues[1]).doubleValue();

        final FloatOrVariableRef firstToleranceComputer = getFirstTolerance();
        final FloatOrVariableRef secondToleranceComputer = getSecondTolerance();

        double firstTolerance = 0.0;
        if (firstToleranceComputer!=null) {
            final Value firstToleranceValue = firstToleranceComputer.evaluate(this, context);
            if (firstToleranceValue.isNull()) {
                context.fireRuntimeWarning(this, "Computed value of first tolerance is NULL. Returning NULL");
                return NullValue.INSTANCE;
            }
            firstTolerance = ((FloatValue) firstToleranceValue).doubleValue();
            if (firstTolerance <= 0.0) {
                context.fireRuntimeWarning(this, "Computed value of first tolerance " + firstTolerance + " is negative. Returning NULL");
                return NullValue.INSTANCE;
            }
        }

        double secondTolerance = firstTolerance;
        if (secondToleranceComputer!=null) {
            final Value secondToleranceValue = secondToleranceComputer.evaluate(this, context);
            if (secondToleranceValue.isNull()) {
                context.fireRuntimeWarning(this, "Computed value of second tolerance is NULL. Returning NULL");
                return NullValue.INSTANCE;
            }
            secondTolerance = ((FloatValue) secondToleranceValue).doubleValue();
            if (secondTolerance <= 0.0) {
                context.fireRuntimeWarning(this, "Computed value of second tolerance " + secondTolerance + " is negative. Returning NULL");
                return NullValue.INSTANCE;
            }
        }

        final ToleranceMode toleranceMode = getToleranceMode();
        if (toleranceMode==null) {
            context.fireRuntimeWarning(this, "No toleranceMode specified. Returning NULL");
            return NullValue.INSTANCE;
        }
        return BooleanValue.valueOf(toleranceMode.isEqual(firstNumber, secondNumber,
                firstTolerance, secondTolerance,
                getIncludeLowerBound(), getIncludeUpperBound()));
    }
}
