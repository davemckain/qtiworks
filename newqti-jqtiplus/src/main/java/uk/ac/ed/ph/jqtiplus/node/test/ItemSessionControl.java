/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;


/**
 * When items are referenced as part of A test, the test may impose constraints on how many attempts
 * and which states are allowed. These constraints can be specified for individual items, for whole
 * sections, or for an entire testPart. By default, A setting at testPart level affects all items in
 * that part unless the setting is overridden at the assessmentSection level or ultimately at the
 * individual assessmentItemRef. The defaults given below are used only in the absence of any
 * applicable constraint.
 * 
 * @author Jiri Kajaba
 */
public class ItemSessionControl extends AbstractNode
{
    private static final long serialVersionUID = 1L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "itemSessionControl";

    /** Name of maxAttempts attribute in xml schema. */
    public static final String ATTR_MAX_ATTEMPTS_NAME = "maxAttempts";
    /** Default value of maxAttempts attribute. */
    public static final Integer ATTR_MAX_ATTEMPTS_DEFAULT_VALUE = null;
    /** Default value of getMaxAttempts method. */
    public static final int MAX_ATTEMPTS_DEFAULT_VALUE = 1;

    /** Name of showFeedback attribute in xml schema. */
    public static final String ATTR_SHOW_FEEDBACK_NAME = "showFeedback";
    /** Default value of showFeedback attribute. */
    public static final Boolean ATTR_SHOW_FEEDBACK_DEFAULT_VALUE = null;
    /** Default value of getShowFeedback method. */
    public static final boolean SHOW_FEEDBACK_DEFAULT_VALUE = false;

    /** Name of allowReview attribute in xml schema. */
    public static final String ATTR_ALLOW_REVIEW_NAME = "allowReview";
    /** Default value of allowReview attribute. */
    public static final Boolean ATTR_ALLOW_REVIEW_DEFAULT_VALUE = null;
    /** Default value of getAllowReview method. */
    public static final boolean ALLOW_REVIEW_DEFAULT_VALUE = true;

    /** Name of showSolution attribute in xml schema. */
    public static final String ATTR_SHOW_SOLUTION_NAME = "showSolution";
    /** Default value of showSolution attribute. */
    public static final Boolean ATTR_SHOW_SOLUTION_DEFAULT_VALUE = null;
    /** Default value of getShowSolution method. */
    public static final boolean SHOW_SOLUTION_DEFAULT_VALUE = false;

    /** Name of allowComment attribute in xml schema. */
    public static final String ATTR_ALLOW_COMMENT_NAME = "allowComment";
    /** Default value of allowComment attribute. */
    public static final Boolean ATTR_ALLOW_COMMENT_DEFAULT_VALUE = null;
    /** Default value of getAllowComment method. */
    public static final boolean ALLOW_COMMENT_DEFAULT_VALUE = false;

    /** Name of allowSkipping attribute in xml schema. */
    public static final String ATTR_ALLOW_SKIPPING_NAME = "allowSkipping";
    /** Default value of allowSkipping attribute. */
    public static final Boolean ATTR_ALLOW_SKIPPING_DEFAULT_VALUE = null;
    /** Default value of getAllowSkipping method. */
    public static final boolean ALLOW_SKIPPING_DEFAULT_VALUE = true;

    /** Name of validateResponses attribute in xml schema. */
    public static final String ATTR_VALIDATE_RESPONSES_NAME = "validateResponses";
    /** Default value of validateResponses attribute. */
    public static final Boolean ATTR_VALIDATE_RESPONSES_DEFAULT_VALUE = null;
    /** Default value of getValidateResponses method. */
    public static final boolean VALIDATE_RESPONSES_DEFAULT_VALUE = false;

    /**
     * Constructs object.
     *
     * @param parent parent of created object
     */
    public ItemSessionControl(AbstractPart parent)
    {
        super(parent);

        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_ATTEMPTS_NAME, ATTR_MAX_ATTEMPTS_DEFAULT_VALUE));
        getAttributes().add(new BooleanAttribute(this, ATTR_SHOW_FEEDBACK_NAME, ATTR_SHOW_FEEDBACK_DEFAULT_VALUE));
        getAttributes().add(new BooleanAttribute(this, ATTR_ALLOW_REVIEW_NAME, ATTR_ALLOW_REVIEW_DEFAULT_VALUE));
        getAttributes().add(new BooleanAttribute(this, ATTR_SHOW_SOLUTION_NAME, ATTR_SHOW_SOLUTION_DEFAULT_VALUE));
        getAttributes().add(new BooleanAttribute(this, ATTR_ALLOW_COMMENT_NAME, ATTR_ALLOW_COMMENT_DEFAULT_VALUE));
        getAttributes().add(new BooleanAttribute(this, ATTR_ALLOW_SKIPPING_NAME, ATTR_ALLOW_SKIPPING_DEFAULT_VALUE));
        getAttributes().add(new BooleanAttribute(this, ATTR_VALIDATE_RESPONSES_NAME, ATTR_VALIDATE_RESPONSES_DEFAULT_VALUE));
    }

    @Override
    public AbstractPart getParent()
    {
        return (AbstractPart) super.getParent();
    }

    @Override
    public String getClassTag()
    {
        return CLASS_TAG;
    }

    /**
     * Gets value of maxAttempts attribute.
     *
     * @return value of maxAttempts attribute
     * @see #setMaxAttemptsAttrValue
     */
    public Integer getMaxAttemptsAttrValue()
    {
        return getAttributes().getIntegerAttribute(ATTR_MAX_ATTEMPTS_NAME).getValue();
    }

    /**
     * Sets new value of maxAttempts attribute.
     *
     * @param maxAttempts new value of maxAttempts attribute
     * @see #getMaxAttemptsAttrValue
     */
    public void setMaxAttemptsAttrValue(Integer maxAttempts)
    {
        getAttributes().getIntegerAttribute(ATTR_MAX_ATTEMPTS_NAME).setValue(maxAttempts);
    }

    /**
     * Gets value of showFeedback attribute.
     *
     * @return value of showFeedback attribute
     * @see #setShowFeedbackAttrValue
     */
    public Boolean getShowFeedbackAttrValue()
    {
        return getAttributes().getBooleanAttribute(ATTR_SHOW_FEEDBACK_NAME).getValue();
    }

    /**
     * Sets new value of showFeedback attribute.
     *
     * @param showFeedback new value of showFeedback attribute
     * @see #getShowFeedbackAttrValue
     */
    public void setShowFeedbackAttrValue(Boolean showFeedback)
    {
        getAttributes().getBooleanAttribute(ATTR_SHOW_FEEDBACK_NAME).setValue(showFeedback);
    }

    /**
     * Gets value of allowReview attribute.
     *
     * @return value of allowReview attribute
     * @see #setAllowReviewAttrValue
     */
    public Boolean getAllowReviewAttrValue()
    {
        return getAttributes().getBooleanAttribute(ATTR_ALLOW_REVIEW_NAME).getValue();
    }

    /**
     * Sets new value of allowReview attribute.
     *
     * @param allowReview new value of allowReview attribute
     * @see #getAllowReviewAttrValue
     */
    public void setAllowReviewAttrValue(Boolean allowReview)
    {
        getAttributes().getBooleanAttribute(ATTR_ALLOW_REVIEW_NAME).setValue(allowReview);
    }

    /**
     * Gets value of showSolution attribute.
     *
     * @return value of showSolution attribute
     * @see #setShowSolutionAttrValue
     */
    public Boolean getShowSolutionAttrValue()
    {
        return getAttributes().getBooleanAttribute(ATTR_SHOW_SOLUTION_NAME).getValue();
    }

    /**
     * Sets new value of showSolution attribute.
     *
     * @param showSolution new value of showSolution attribute
     * @see #getShowSolutionAttrValue
     */
    public void setShowSolutionAttrValue(Boolean showSolution)
    {
        getAttributes().getBooleanAttribute(ATTR_SHOW_SOLUTION_NAME).setValue(showSolution);
    }

    /**
     * Gets value of allowComment attribute.
     *
     * @return value of allowComment attribute
     * @see #setAllowCommentAttrValue
     */
    public Boolean getAllowCommentAttrValue()
    {
        return getAttributes().getBooleanAttribute(ATTR_ALLOW_COMMENT_NAME).getValue();
    }

    /**
     * Sets new value of allowComment attribute.
     *
     * @param allowComment new value of allowComment attribute
     * @see #getAllowCommentAttrValue
     */
    public void setAllowCommentAttrValue(Boolean allowComment)
    {
        getAttributes().getBooleanAttribute(ATTR_ALLOW_COMMENT_NAME).setValue(allowComment);
    }

    /**
     * Gets value of allowSkipping attribute.
     *
     * @return value of allowSkipping attribute
     * @see #setAllowSkippingAttrValue
     */
    public Boolean getAllowSkippingAttrValue()
    {
        return getAttributes().getBooleanAttribute(ATTR_ALLOW_SKIPPING_NAME).getValue();
    }

    /**
     * Sets new value of allowSkipping attribute.
     *
     * @param allowSkipping new value of allowSkipping attribute
     * @see #getAllowSkippingAttrValue
     */
    public void setAllowSkippingAttrValue(Boolean allowSkipping)
    {
        getAttributes().getBooleanAttribute(ATTR_ALLOW_SKIPPING_NAME).setValue(allowSkipping);
    }

    /**
     * Gets value of validateResponses attribute.
     *
     * @return value of validateResponses attribute
     * @see #setValidateResponsesAttrValue
     */
    public Boolean getValidateResponsesAttrValue()
    {
        return getAttributes().getBooleanAttribute(ATTR_VALIDATE_RESPONSES_NAME).getValue();
    }

    /**
     * Sets new value of validateResponses attribute.
     *
     * @param validateResponses new value of validateResponses attribute
     * @see #getValidateResponsesAttrValue
     */
    public void setValidateResponsesAttrValue(Boolean validateResponses)
    {
        getAttributes().getBooleanAttribute(ATTR_VALIDATE_RESPONSES_NAME).setValue(validateResponses);
    }

    @Override
    public String toXmlString(int depth, boolean printDefaultAttributes)
    {
        return attrToXmlString(depth, printDefaultAttributes).length() > 0 ? super.toXmlString(depth, printDefaultAttributes) : "";
    }

    @Override
    protected String attrToXmlString(int depth, boolean printDefaultAttributes)
    {
        getAttributes().getIntegerAttribute(ATTR_MAX_ATTEMPTS_NAME).setDefaultValue(getMaxAttemptsDefaultValue());
        getAttributes().getBooleanAttribute(ATTR_SHOW_FEEDBACK_NAME).setDefaultValue(getShowFeedbackDefaultValue());
        getAttributes().getBooleanAttribute(ATTR_ALLOW_REVIEW_NAME).setDefaultValue(getAllowReviewDefaultValue());
        getAttributes().getBooleanAttribute(ATTR_SHOW_SOLUTION_NAME).setDefaultValue(getShowSolutionDefaultValue());
        getAttributes().getBooleanAttribute(ATTR_ALLOW_COMMENT_NAME).setDefaultValue(getAllowCommentDefaultValue());
        getAttributes().getBooleanAttribute(ATTR_ALLOW_SKIPPING_NAME).setDefaultValue(getAllowSkippingDefaultValue());
        getAttributes().getBooleanAttribute(ATTR_VALIDATE_RESPONSES_NAME).setDefaultValue(getValidateResponsesDefaultValue());

        String result = getAttributes().toXmlString(depth, printDefaultAttributes);

        getAttributes().getIntegerAttribute(ATTR_MAX_ATTEMPTS_NAME).setDefaultValue(ATTR_MAX_ATTEMPTS_DEFAULT_VALUE);
        getAttributes().getBooleanAttribute(ATTR_SHOW_FEEDBACK_NAME).setDefaultValue(ATTR_SHOW_FEEDBACK_DEFAULT_VALUE);
        getAttributes().getBooleanAttribute(ATTR_ALLOW_REVIEW_NAME).setDefaultValue(ATTR_ALLOW_REVIEW_DEFAULT_VALUE);
        getAttributes().getBooleanAttribute(ATTR_SHOW_SOLUTION_NAME).setDefaultValue(ATTR_SHOW_SOLUTION_DEFAULT_VALUE);
        getAttributes().getBooleanAttribute(ATTR_ALLOW_COMMENT_NAME).setDefaultValue(ATTR_ALLOW_COMMENT_DEFAULT_VALUE);
        getAttributes().getBooleanAttribute(ATTR_ALLOW_SKIPPING_NAME).setDefaultValue(ATTR_ALLOW_SKIPPING_DEFAULT_VALUE);
        getAttributes().getBooleanAttribute(ATTR_VALIDATE_RESPONSES_NAME).setDefaultValue(ATTR_VALIDATE_RESPONSES_DEFAULT_VALUE);

        return result;
    }

    /**
     * Gets final maxAttempts value.
     * <ol>
     * <li>returns value of corresponding attribute if it is not null
     * <li>returns default value if parent of this <code>itemSessionControl</code> is <code>TestPart</code>
     * <li>returns parent's final maxAttempts value (parent must be <code>SectionPart</code>)
     * </ol>
     *
     * @return final maxAttempts value
     */
    public int getMaxAttempts()
    {
        Integer maxAttempts = getMaxAttemptsAttrValue();
        if (maxAttempts != null)
            return maxAttempts;

        return getParentsMaxAttempts();
    }

    /**
     * Gets parent's (parent of parent of this itemSessionControl) final maxAttempts value or default
     * value if parent of this itemSessionControl is TestPart.
     *
     * @return parent's final maxAttempts value or default value
     */
    protected Integer getParentsMaxAttempts()
    {
        AbstractPart parent = getParent();

        if (parent instanceof TestPart)
            return MAX_ATTEMPTS_DEFAULT_VALUE;

        assert parent instanceof SectionPart;

        return ((SectionPart) parent).getParent().getItemSessionControl().getMaxAttempts();
    }

    /**
     * Gets default value of maxAttempts attribute.
     * <p>
     * If attribute should be printed, it returns null.
     * Because attribute's current value will be different as its default value, it will be printed.
     * <p>
     * If attribute should not be printed, it returns current attribute's value.
     * Because attribute's current value will be same as its default value, it will not be printed.
     * <p>
     * Attribute should be printed if and only if it holds some additional information.
     * <p>
     * This method returns opposite value what everybody would expect.
     *
     * @return default value of maxAttempts attribute
     */
    private Integer getMaxAttemptsDefaultValue()
    {
        Integer maxAttempts = getMaxAttemptsAttrValue();
        if (maxAttempts == null || maxAttempts.equals(getParentsMaxAttempts()))
            return maxAttempts; // Attribute should not be printed.
        // -> returns same default value like attribute's current value
        // -> attribute will not be printed, because its value is same like its default value

        return null; // Attribute should be printed.
    }

    /**
     * Gets final showFeedback value.
     * <ol>
     * <li>returns value of corresponding attribute if it is not null
     * <li>returns default value if parent of this <code>itemSessionControl</code> is <code>TestPart</code>
     * <li>returns parent's final showFeedback value (parent must be <code>SectionPart</code>)
     * </ol>
     *
     * @return final showFeedback value
     */
    public boolean getShowFeedback()
    {
        Boolean showFeedback = getShowFeedbackAttrValue();
        if (showFeedback != null)
            return showFeedback;

        return getParentsShowFeedback();
    }

    /**
     * Gets parent's (parent of parent of this itemSessionControl) final showFeedback value or default
     * value if parent of this itemSessionControl is TestPart.
     *
     * @return parent's final showFeedback value or default value
     */
    protected boolean getParentsShowFeedback()
    {
        AbstractPart parent = getParent();

        if (parent instanceof TestPart)
            return SHOW_FEEDBACK_DEFAULT_VALUE;

        assert parent instanceof SectionPart;

        return ((SectionPart) parent).getParent().getItemSessionControl().getShowFeedback();
    }

    /**
     * Gets default value of showFeedback attribute.
     * <p>
     * If attribute should be printed, it returns null.
     * Because attribute's current value will be different as its default value, it will be printed.
     * <p>
     * If attribute should not be printed, it returns current attribute's value.
     * Because attribute's current value will be same as its default value, it will not be printed.
     * <p>
     * Attribute should be printed if and only if it holds some additional information.
     * <p>
     * This method returns opposite value what everybody would expect.
     *
     * @return default value of showFeedback attribute
     */
    private Boolean getShowFeedbackDefaultValue()
    {
        Boolean showFeedback = getShowFeedbackAttrValue();
        if (showFeedback == null || showFeedback.equals(getParentsShowFeedback()))
            return showFeedback;

        return null;
    }

    /**
     * Gets final allowReview value.
     * <ol>
     * <li>returns value of corresponding attribute if it is not null
     * <li>returns default value if parent of this <code>itemSessionControl</code> is <code>TestPart</code>
     * <li>returns parent's final allowReview value (parent must be <code>SectionPart</code>)
     * </ol>
     *
     * @return final allowReview value
     */
    public boolean getAllowReview()
    {
        Boolean allowReview = getAllowReviewAttrValue();
        if (allowReview != null)
            return allowReview;

        return getParentsAllowReview();
    }

    /**
     * Gets parent's (parent of parent of this itemSessionControl) final allowReview value or default
     * value if parent of this itemSessionControl is TestPart.
     *
     * @return parent's final allowReview value or default value
     */
    protected boolean getParentsAllowReview()
    {
        AbstractPart parent = getParent();

        if (parent instanceof TestPart)
            return ALLOW_REVIEW_DEFAULT_VALUE;

        assert parent instanceof SectionPart;

        return ((SectionPart) parent).getParent().getItemSessionControl().getAllowReview();
    }

    /**
     * Gets default value of allowReview attribute.
     * <p>
     * If attribute should be printed, it returns null.
     * Because attribute's current value will be different as its default value, it will be printed.
     * <p>
     * If attribute should not be printed, it returns current attribute's value.
     * Because attribute's current value will be same as its default value, it will not be printed.
     * <p>
     * Attribute should be printed if and only if it holds some additional information.
     * <p>
     * This method returns opposite value what everybody would expect.
     *
     * @return default value of allowReview attribute
     */
    private Boolean getAllowReviewDefaultValue()
    {
        Boolean allowReview = getAllowReviewAttrValue();
        if (allowReview == null || allowReview.equals(getParentsAllowReview()))
            return allowReview;

        return null;
    }

    /**
     * Gets final showSolution value.
     * <ol>
     * <li>returns value of corresponding attribute if it is not null
     * <li>returns default value if parent of this <code>itemSessionControl</code> is <code>TestPart</code>
     * <li>returns parent's final showSolution value (parent must be <code>SectionPart</code>)
     * </ol>
     *
     * @return final showSolution value
     */
    public boolean getShowSolution()
    {
        Boolean showSolution = getShowSolutionAttrValue();
        if (showSolution != null)
            return showSolution;

        return getParentsShowSolution();
    }

    /**
     * Gets parent's (parent of parent of this itemSessionControl) final showSolution value or default
     * value if parent of this itemSessionControl is TestPart.
     *
     * @return parent's final showSolution value or default value
     */
    protected boolean getParentsShowSolution()
    {
        AbstractPart parent = getParent();

        if (parent instanceof TestPart)
            return SHOW_SOLUTION_DEFAULT_VALUE;

        assert parent instanceof SectionPart;

        return ((SectionPart) parent).getParent().getItemSessionControl().getShowSolution();
    }

    /**
     * Gets default value of showSolution attribute.
     * <p>
     * If attribute should be printed, it returns null.
     * Because attribute's current value will be different as its default value, it will be printed.
     * <p>
     * If attribute should not be printed, it returns current attribute's value.
     * Because attribute's current value will be same as its default value, it will not be printed.
     * <p>
     * Attribute should be printed if and only if it holds some additional information.
     * <p>
     * This method returns opposite value what everybody would expect.
     *
     * @return default value of showSolution attribute
     */
    private Boolean getShowSolutionDefaultValue()
    {
        Boolean showSolution = getShowSolutionAttrValue();
        if (showSolution == null || showSolution.equals(getParentsShowSolution()))
            return showSolution;

        return null;
    }

    /**
     * Gets final allowComment value.
     * <ol>
     * <li>returns value of corresponding attribute if it is not null
     * <li>returns default value if parent of this <code>itemSessionControl</code> is <code>TestPart</code>
     * <li>returns parent's final allowComment value (parent must be <code>SectionPart</code>)
     * </ol>
     *
     * @return final allowComment value
     */
    public boolean getAllowComment()
    {
        Boolean allowComment = getAllowCommentAttrValue();
        if (allowComment != null)
            return allowComment;

        return getParentsAllowComment();
    }

    /**
     * Gets parent's (parent of parent of this itemSessionControl) final allowComment value or default
     * value if parent of this itemSessionControl is TestPart.
     *
     * @return parent's final allowComment value or default value
     */
    protected boolean getParentsAllowComment()
    {
        AbstractPart parent = getParent();

        if (parent instanceof TestPart)
            return ALLOW_COMMENT_DEFAULT_VALUE;

        assert parent instanceof SectionPart;

        return ((SectionPart) parent).getParent().getItemSessionControl().getAllowComment();
    }

    /**
     * Gets default value of allowComment attribute.
     * <p>
     * If attribute should be printed, it returns null.
     * Because attribute's current value will be different as its default value, it will be printed.
     * <p>
     * If attribute should not be printed, it returns current attribute's value.
     * Because attribute's current value will be same as its default value, it will not be printed.
     * <p>
     * Attribute should be printed if and only if it holds some additional information.
     * <p>
     * This method returns opposite value what everybody would expect.
     *
     * @return default value of allowComment attribute
     */
    private Boolean getAllowCommentDefaultValue()
    {
        Boolean allowComment = getAllowCommentAttrValue();
        if (allowComment == null || allowComment.equals(getParentsAllowComment()))
            return allowComment;

        return null;
    }

    /**
     * Gets final allowSkipping value.
     * <ol>
     * <li>returns value of corresponding attribute if it is not null
     * <li>returns default value if parent of this <code>itemSessionControl</code> is <code>TestPart</code>
     * <li>returns parent's final allowSkipping value (parent must be <code>SectionPart</code>)
     * </ol>
     *
     * @return final allowSkipping value
     */
    public boolean getAllowSkipping()
    {
        Boolean allowSkipping = getAllowSkippingAttrValue();
        if (allowSkipping != null)
            return allowSkipping;

        return getParentsAllowSkipping();
    }

    /**
     * Gets parent's (parent of parent of this itemSessionControl) final allowSkipping value or default
     * value if parent of this itemSessionControl is TestPart.
     *
     * @return parent's final allowSkipping value or default value
     */
    protected boolean getParentsAllowSkipping()
    {
        AbstractPart parent = getParent();

        if (parent instanceof TestPart)
            return ALLOW_SKIPPING_DEFAULT_VALUE;

        assert parent instanceof SectionPart;

        return ((SectionPart) parent).getParent().getItemSessionControl().getAllowSkipping();
    }

    /**
     * Gets default value of allowSkipping attribute.
     * <p>
     * If attribute should be printed, it returns null.
     * Because attribute's current value will be different as its default value, it will be printed.
     * <p>
     * If attribute should not be printed, it returns current attribute's value.
     * Because attribute's current value will be same as its default value, it will not be printed.
     * <p>
     * Attribute should be printed if and only if it holds some additional information.
     * <p>
     * This method returns opposite value what everybody would expect.
     *
     * @return default value of allowSkipping attribute
     */
    private Boolean getAllowSkippingDefaultValue()
    {
        Boolean allowSkipping = getAllowSkippingAttrValue();
        if (allowSkipping == null || allowSkipping.equals(getParentsAllowSkipping()))
            return allowSkipping;

        return null;
    }

    /**
     * Gets final validateResponses value.
     * <ol>
     * <li>returns value of corresponding attribute if it is not null
     * <li>returns default value if parent of this <code>itemSessionControl</code> is <code>TestPart</code>
     * <li>returns parent's final validateResponse value (parent must be <code>SectionPart</code>)
     * </ol>
     *
     * @return final validateResponses value
     */
    public boolean getValidateResponses()
    {
        Boolean validateResponses = getValidateResponsesAttrValue();
        if (validateResponses != null)
            return validateResponses;

        return getParentsValidateResponses();
    }

    /**
     * Gets parent's (parent of parent of this itemSessionControl) final validateResponses value or default
     * value if parent of this itemSessionControl is TestPart.
     *
     * @return parent's final validateResponses value or default value
     */
    protected boolean getParentsValidateResponses()
    {
        AbstractPart parent = getParent();

        if (parent instanceof TestPart)
            return VALIDATE_RESPONSES_DEFAULT_VALUE;

        assert parent instanceof SectionPart;

        return ((SectionPart) parent).getParent().getItemSessionControl().getValidateResponses();
    }

    /**
     * Gets default value of validateResponses attribute.
     * <p>
     * If attribute should be printed, it returns null.
     * Because attribute's current value will be different as its default value, it will be printed.
     * <p>
     * If attribute should not be printed, it returns current attribute's value.
     * Because attribute's current value will be same as its default value, it will not be printed.
     * <p>
     * Attribute should be printed if and only if it holds some additional information.
     * <p>
     * This method returns opposite value what everybody would expect.
     *
     * @return default value of validateResponses attribute
     */
    private Boolean getValidateResponsesDefaultValue()
    {
        Boolean validateResponses = getValidateResponsesAttrValue();
        if (validateResponses == null || validateResponses.equals(getParentsValidateResponses()))
            return validateResponses;

        return null;
    }
}
