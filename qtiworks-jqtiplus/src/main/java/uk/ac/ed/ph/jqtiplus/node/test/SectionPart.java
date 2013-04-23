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
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;

/**
 * Abstract super class for assessmentSection and assessmentItemRef.
 *
 * @author Jiri Kajaba
 */
public abstract class SectionPart extends AbstractPart {

    private static final long serialVersionUID = 7937260989328452910L;

    /** Display name of this part. */
    public static final String DISPLAY_NAME = "sectionPart";

    /** Name of required attribute in xml schema. */
    public static final String ATTR_REQUIRED_NAME = "required";

    /** Default value of required attribute. */
    public static final boolean ATTR_REQUIRED_DEFAULT_VALUE = false;

    /** Name of fixed attribute in xml schema. */
    public static final String ATTR_FIXED_NAME = "fixed";

    /** Default value of fixed attribute. */
    public static final boolean ATTR_FIXED_DEFAULT_VALUE = false;

    public SectionPart(final AbstractPart parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new BooleanAttribute(this, ATTR_REQUIRED_NAME, ATTR_REQUIRED_DEFAULT_VALUE, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_FIXED_NAME, ATTR_FIXED_DEFAULT_VALUE, false));
    }

    @Override
    public AbstractPart getParent() {
        return (AbstractPart) super.getParent();
    }

    public AssessmentSection getParentSection() {
        return getParent() instanceof AssessmentSection ? (AssessmentSection) getParent() : null;
    }


    public boolean getRequired() {
        return getAttributes().getBooleanAttribute(ATTR_REQUIRED_NAME).getComputedNonNullValue();
    }

    public void setRequired(final Boolean required) {
        getAttributes().getBooleanAttribute(ATTR_REQUIRED_NAME).setValue(required);
    }


    public boolean getFixed() {
        return getAttributes().getBooleanAttribute(ATTR_FIXED_NAME).getComputedNonNullValue();
    }

    public void setFixed(final Boolean fixed) {
        getAttributes().getBooleanAttribute(ATTR_FIXED_NAME).setValue(fixed);
    }
}
