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
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.OrientationAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.Orientation;
import uk.ac.ed.ph.jqtiplus.value.Signature;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The slider interaction presents the candidate with a control for selecting a numerical
 * value between a lower and upper bound. It must be bound to a response variable with single
 * cardinality with a base-type of either integer or float.
 * Attribute : lowerBound [1]: float
 * If the associated response variable is of type integer then the lowerBound must be rounded
 * down to the greatest integer less than or equal to the value given.
 * Attribute : upperBound [1]: float
 * If the associated response variable is of type integer then the upperBound must be rounded
 * up to the least integer greater than or equal to the value given.
 * Attribute : step [0..1]: integer
 * The steps that the control moves in. For example, if the lowerBound and upperBound are [0,10]
 * and step is 2 then the response would be constrained to the set of values {0,2,4,6,8,10}. If
 * bound to an integer response the default step is 1, otherwise the slider is assumed to
 * operate on an approximately continuous scale.
 * Attribute : stepLabel [0..1]: boolean = false
 * By default, sliders are labeled only at their ends. The stepLabel attribute controls whether
 * or not each step on the slider should also be labeled. It is unlikely that delivery engines
 * will be able to guarantee to label steps so this attribute should be treated only as request.
 * Attribute : orientation [0..1]: orientation
 * The orientation attribute provides a hint to rendering systems that the slider is being used
 * to indicate the value of a quantity with an inherent vertical or horizontal interpretation.
 * For example, an interaction that is used to indicate the value of height might set the
 * orientation to vertical to indicate that rendering it horizontally could spuriously increase
 * the difficulty of the item.
 * Attribute : reverse [0..1]: boolean
 * The reverse attribute provides a hint to rendering systems that the slider is being used to
 * indicate the value of a quantity for which the normal sense of the upper and lower bounds is
 * reversed. For example, an interaction that is used to indicate a depth below sea level might
 * specify both a vertical orientation and set reverse.
 * Note that a slider interaction does not have a default or initial position except where
 * specified by a default value for the associated response variable. The currently selected value,
 * if any, must be clearly indicated to the candidate.
 *
 * @author Jonathon Hare
 */
public final class SliderInteraction extends BlockInteraction {

    private static final long serialVersionUID = -1475285258141745276L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "sliderInteraction";

    /** Name of lowerBound attribute in xml schema. */
    public static final String ATTR_LOWER_BOUND_NAME = "lowerBound";

    /** Name of upperBound attribute in xml schema. */
    public static final String ATTR_UPPER_BOUND_NAME = "upperBound";

    /** Name of step attribute in xml schema. */
    public static final String ATTR_STEP_NAME = "step";

    /** Name of stepLabel attribute in xml schema. */
    public static final String ATTR_STEP_LABEL_NAME = "stepLabel";

    /** Name of orientation attribute in xml schema. */
    public static final String ATTR_ORIENTATION_NAME = "orientation";

    /** Name of reverse attribute in xml schema. */
    public static final String ATTR_REVERSE_NAME = "reverse";

    public SliderInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new FloatAttribute(this, ATTR_LOWER_BOUND_NAME, true));
        getAttributes().add(new FloatAttribute(this, ATTR_UPPER_BOUND_NAME, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_STEP_NAME, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_STEP_LABEL_NAME, false));
        getAttributes().add(new OrientationAttribute(this, ATTR_ORIENTATION_NAME, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_REVERSE_NAME, false));
    }


    public double getLowerBound() {
        return getAttributes().getFloatAttribute(ATTR_LOWER_BOUND_NAME).getComputedNonNullValue();
    }

    public void setLowerBound(final Double lowerBound) {
        getAttributes().getFloatAttribute(ATTR_LOWER_BOUND_NAME).setValue(lowerBound);
    }


    public double getUpperBound() {
        return getAttributes().getFloatAttribute(ATTR_UPPER_BOUND_NAME).getComputedNonNullValue();
    }

    public void setUpperBound(final Double upperBound) {
        getAttributes().getFloatAttribute(ATTR_UPPER_BOUND_NAME).setValue(upperBound);
    }


    public Integer getStep() {
        return getAttributes().getIntegerAttribute(ATTR_STEP_NAME).getComputedValue();
    }

    public void setStep(final Integer step) {
        getAttributes().getIntegerAttribute(ATTR_STEP_NAME).setValue(step);
    }

    public boolean getStepLabel() {
        return getAttributes().getBooleanAttribute(ATTR_STEP_LABEL_NAME).getComputedNonNullValue();
    }

    public void setStepLabel(final Boolean stepLabel) {
        getAttributes().getBooleanAttribute(ATTR_STEP_LABEL_NAME).setValue(stepLabel);
    }



    public Orientation getOrientation() {
        return getAttributes().getOrientationAttribute(ATTR_ORIENTATION_NAME).getComputedValue();
    }

    public void setOrientation(final Orientation orientation) {
        getAttributes().getOrientationAttribute(ATTR_ORIENTATION_NAME).setValue(orientation);
    }


    public Boolean getReverse() {
        return getAttributes().getBooleanAttribute(ATTR_REVERSE_NAME).getValue();
    }

    public void setReverse(final Boolean reverse) {
        getAttributes().getBooleanAttribute(ATTR_REVERSE_NAME).setValue(reverse);
    }

    @Override
    protected void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        if (responseDeclaration!=null) {
            context.checkVariableType(this, responseDeclaration, VariableType.RESPONSE);
            context.checkSignature(this, responseDeclaration, Signature.SINGLE_INTEGER, Signature.SINGLE_FLOAT);
        }
    }

    @Override
    public boolean validateResponse(final InteractionBindingContext interactionBindingContext, final Value responseValue) {
        if (responseValue.isNull()) {
            /* Null responses are considered to be invalid, as far as I'm interpreting things */
            return false;
        }

        final ResponseDeclaration responseDeclaration = getResponseDeclaration();
        if (responseDeclaration == null) {
            return false;
        }

        /* Make sure response is between the required min and max */
        if (responseDeclaration.hasBaseType(BaseType.FLOAT)) {
            final double doubleValue = ((FloatValue) responseValue).doubleValue();
            if (doubleValue < getLowerBound() || doubleValue > getUpperBound()) {
                return false;
            }
        }
        else {
            final int intValue = ((IntegerValue) responseValue).intValue();
            final int lowerBound = (int) Math.floor(getLowerBound());
            final int upperBound = (int) Math.ceil(getUpperBound());
            if (intValue < lowerBound || intValue > upperBound) {
                return false;
            }
        }
        return true;
    }
}
