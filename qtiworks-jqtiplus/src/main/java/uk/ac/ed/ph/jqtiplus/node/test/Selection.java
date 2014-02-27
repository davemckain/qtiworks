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
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

/**
 * The selection class specifies the rules used to select the child elements of a section for each test
 * session. If no selection rules are given we assume that all elements are to be selected.
 *
 * @author Jiri Kajaba
 */
public final class Selection extends AbstractNode {

    private static final long serialVersionUID = 1716825756388015143L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "selection";

    /** Name of select attribute in xml schema. */
    public static final String ATTR_SELECT_NAME = "select";

    /** Name of withReplacement attribute in xml schema. */
    public static final String ATTR_WITH_REPLACEMENT_NAME = "withReplacement";

    /** Default value of withReplacement attribute. */
    public static final boolean ATTR_WITH_REPLACEMENT_DEFAULT_VALUE = false;

    public Selection(final AssessmentSection parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerAttribute(this, ATTR_SELECT_NAME, true));
        getAttributes().add(new BooleanAttribute(this, ATTR_WITH_REPLACEMENT_NAME, ATTR_WITH_REPLACEMENT_DEFAULT_VALUE, false));
    }

    @Override
    public AssessmentSection getParent() {
        return (AssessmentSection) super.getParent();
    }


    public int getSelect() {
        return getAttributes().getIntegerAttribute(ATTR_SELECT_NAME).getComputedNonNullValue();
    }

    public void setSelect(final Integer select) {
        getAttributes().getIntegerAttribute(ATTR_SELECT_NAME).setValue(select);
    }


    public boolean getWithReplacement() {
        return getAttributes().getBooleanAttribute(ATTR_WITH_REPLACEMENT_NAME).getComputedNonNullValue();
    }

    public void setWithReplacement(final Boolean withReplacement) {
        getAttributes().getBooleanAttribute(ATTR_WITH_REPLACEMENT_NAME).setValue(withReplacement);
    }


    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final int select = getSelect();
        final AssessmentSection section = getParent();
        if (section != null) {
            int required = 0;
            for (final SectionPart part : section.getSectionParts()) {
                if (part.getRequired()) {
                    required++;
                }
            }

            if (select < required) {
                context.fireValidationError(this, "Invalid selection. Required at least " + required + " but found " + select);
            }

            if (!getWithReplacement() && select > section.getSectionParts().size()) {
                context.fireValidationError(this, "Invalid selection. Required no more than " + section.getSectionParts().size()
                        + ", but found "
                        + select);
            }
        }
    }
}
