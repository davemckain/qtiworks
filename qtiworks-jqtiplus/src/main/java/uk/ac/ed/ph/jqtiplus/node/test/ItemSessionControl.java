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
import uk.ac.ed.ph.jqtiplus.state.EffectiveItemSessionControl;

/**
 * Represents the <code>itemSessionControl</code> QTI class.
 * <p>
 * This is significantly different from the implementation of this class
 * in the orignal JQTI.
 *
 * @see EffectiveItemSessionControl
 *
 * @author Jiri Kajaba (original)
 * @author David McKain (refactored)
 */
public final class ItemSessionControl extends AbstractNode {

    private static final long serialVersionUID = 4320465731424106788L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "itemSessionControl";

    /** Name of maxAttempts attribute in xml schema. */
    public static final String ATTR_MAX_ATTEMPTS_NAME = "maxAttempts";

    /** Default value of getMaxAttempts method. */
    public static final int MAX_ATTEMPTS_DEFAULT_VALUE = 1;

    /** Name of showFeedback attribute in xml schema. */
    public static final String ATTR_SHOW_FEEDBACK_NAME = "showFeedback";

    /** Default value of getShowFeedback method. */
    public static final boolean SHOW_FEEDBACK_DEFAULT_VALUE = false;

    /** Name of allowReview attribute in xml schema. */
    public static final String ATTR_ALLOW_REVIEW_NAME = "allowReview";

    /** Default value of getAllowReview method. */
    public static final boolean ALLOW_REVIEW_DEFAULT_VALUE = true;

    /** Name of showSolution attribute in xml schema. */
    public static final String ATTR_SHOW_SOLUTION_NAME = "showSolution";

    /** Default value of getShowSolution method. */
    public static final boolean SHOW_SOLUTION_DEFAULT_VALUE = false;

    /** Name of allowComment attribute in xml schema. */
    public static final String ATTR_ALLOW_COMMENT_NAME = "allowComment";

    /** Default value of getAllowComment method. */
    public static final boolean ALLOW_COMMENT_DEFAULT_VALUE = false;

    /** Name of allowSkipping attribute in xml schema. */
    public static final String ATTR_ALLOW_SKIPPING_NAME = "allowSkipping";

    /** Default value of getAllowSkipping method. */
    public static final boolean ALLOW_SKIPPING_DEFAULT_VALUE = true;

    /** Name of validateResponses attribute in xml schema. */
    public static final String ATTR_VALIDATE_RESPONSES_NAME = "validateResponses";

    /** Default value of getValidateResponses method. */
    public static final boolean VALIDATE_RESPONSES_DEFAULT_VALUE = false;

    public ItemSessionControl(final AbstractPart parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_ATTEMPTS_NAME, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_SHOW_FEEDBACK_NAME, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_ALLOW_REVIEW_NAME, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_SHOW_SOLUTION_NAME, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_ALLOW_COMMENT_NAME, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_ALLOW_SKIPPING_NAME, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_VALIDATE_RESPONSES_NAME, false));
    }

    @Override
    public AbstractPart getParent() {
        return (AbstractPart) super.getParent();
    }

    public Integer getMaxAttempts() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_ATTEMPTS_NAME).getComputedValue();
    }

    public void setMaxAttempts(final Integer maxAttempts) {
        getAttributes().getIntegerAttribute(ATTR_MAX_ATTEMPTS_NAME).setValue(maxAttempts);
    }


    public Boolean getShowFeedback() {
        return getAttributes().getBooleanAttribute(ATTR_SHOW_FEEDBACK_NAME).getComputedValue();
    }

    public void setShowFeedback(final Boolean showFeedback) {
        getAttributes().getBooleanAttribute(ATTR_SHOW_FEEDBACK_NAME).setValue(showFeedback);
    }


    public Boolean getAllowReview() {
        return getAttributes().getBooleanAttribute(ATTR_ALLOW_REVIEW_NAME).getComputedValue();
    }

    public void setAllowReview(final Boolean allowReview) {
        getAttributes().getBooleanAttribute(ATTR_ALLOW_REVIEW_NAME).setValue(allowReview);
    }


    public Boolean getShowSolution() {
        return getAttributes().getBooleanAttribute(ATTR_SHOW_SOLUTION_NAME).getComputedValue();
    }

    public void setShowSolution(final Boolean showSolution) {
        getAttributes().getBooleanAttribute(ATTR_SHOW_SOLUTION_NAME).setValue(showSolution);
    }


    public Boolean getAllowComment() {
        return getAttributes().getBooleanAttribute(ATTR_ALLOW_COMMENT_NAME).getComputedValue();
    }

    public void setAllowComment(final Boolean allowComment) {
        getAttributes().getBooleanAttribute(ATTR_ALLOW_COMMENT_NAME).setValue(allowComment);
    }


    public Boolean getAllowSkipping() {
        return getAttributes().getBooleanAttribute(ATTR_ALLOW_SKIPPING_NAME).getComputedValue();
    }

    public void setAllowSkipping(final Boolean allowSkipping) {
        getAttributes().getBooleanAttribute(ATTR_ALLOW_SKIPPING_NAME).setValue(allowSkipping);
    }


    public Boolean getValidateResponses() {
        return getAttributes().getBooleanAttribute(ATTR_VALIDATE_RESPONSES_NAME).getComputedValue();
    }

    public void setValidateResponses(final Boolean validateResponses) {
        getAttributes().getBooleanAttribute(ATTR_VALIDATE_RESPONSES_NAME).setValue(validateResponses);
    }
}
