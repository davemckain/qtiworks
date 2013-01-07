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
package uk.ac.ed.ph.jqtiplus.node.item.template.declaration;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;

/**
 * Template declarations declare item variables that are to be used specifically for the purposes of cloning items.
 * <p>
 * They can have their value set only during templateProcessing. They are referred to within the itemBody in order to individualize the clone and possibly also
 * within the responseProcessing rules if the cloning process affects the way the item is scored.
 * <p>
 * Template variables are instantiated as part of an item session. Their values are initialized during templateProcessing and thereafter behave as constants
 * within the session.
 *
 * @author Jonathon Hare
 */
public final class TemplateDeclaration extends VariableDeclaration {

    private static final long serialVersionUID = -3230507842769055868L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "templateDeclaration";

    /** Name of paramVariable attribute in xml schema. */
    public static final String ATTR_PARAM_VARIABLE_NAME = "paramVariable";

    /** Default value of paramVariable attribute. */
    public static final boolean ATTR_PARAM_VARIABLE_DEFAULT_VALUE = false;

    /** Name of mathVariable attribute in xml schema. */
    public static final String ATTR_MATH_VARIABLE_NAME = "mathVariable";

    /** Default value of mathVariable attribute. */
    public static final boolean ATTR_MATH_VARIABLE_DEFAULT_VALUE = false;

    public TemplateDeclaration(final AssessmentItem parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BooleanAttribute(this, ATTR_PARAM_VARIABLE_NAME, ATTR_PARAM_VARIABLE_DEFAULT_VALUE, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_MATH_VARIABLE_NAME, ATTR_MATH_VARIABLE_DEFAULT_VALUE, false));
    }

    @Override
    public VariableType getVariableType() {
        return VariableType.TEMPLATE;
    }

    public boolean getParamVariable() {
        return getAttributes().getBooleanAttribute(ATTR_PARAM_VARIABLE_NAME).getComputedNonNullValue();
    }

    public void setParamVariable(final Boolean paramVariable) {
        getAttributes().getBooleanAttribute(ATTR_PARAM_VARIABLE_NAME).setValue(paramVariable);
    }


    public boolean getMathVariable() {
        return getAttributes().getBooleanAttribute(ATTR_MATH_VARIABLE_NAME).getComputedNonNullValue();
    }

    public void setMathVariable(final Boolean mathVariable) {
        getAttributes().getBooleanAttribute(ATTR_MATH_VARIABLE_NAME).setValue(mathVariable);
    }
}
