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
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.TestFeedbackAccessAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.VisibilityModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.FlowStaticGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.state.AssessmentTestState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * Test level feedback implementation.
 * 
 * @see TestFeedbackAccess
 * @see VisibilityMode
 * @author Jiri Kajaba
 */
public class TestFeedback extends AbstractNode {

    private static final long serialVersionUID = 6567681516055125776L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "testFeedback";

    /** Name of access attribute in xml schema. */
    public static final String ATTR_ACCESS_NAME = TestFeedbackAccess.CLASS_TAG;

    /** Name of showHide attribute in xml schema. */
    public static final String ATTR_VISIBILITY_MODE_NAME = VisibilityMode.CLASS_TAG;

    /** Name of outcomeIdentifier attribute in xml schema. */
    public static final String ATTR_OUTCOME_IDENTIFIER_NAME = "outcomeIdentifier";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_OUTCOME_VALUE_NAME = "identifier";

    /** Name of title attribute in xml schema. */
    public static final String ATTR_TITLE_NAME = "title";

    /** Default value of title attribute. */
    public static final String ATTR_TITLE_DEFAULT_VALUE = null;

    /**
     * Constructs feedback.
     * 
     * @param parent parent of constructed feedback
     */
    public TestFeedback(ControlObject<?> parent) {
        super(parent);

        getAttributes().add(new TestFeedbackAccessAttribute(this, ATTR_ACCESS_NAME));
        getAttributes().add(new VisibilityModeAttribute(this, ATTR_VISIBILITY_MODE_NAME));
        getAttributes().add(new IdentifierAttribute(this, ATTR_OUTCOME_IDENTIFIER_NAME));
        getAttributes().add(new IdentifierAttribute(this, ATTR_OUTCOME_VALUE_NAME));
        getAttributes().add(new StringAttribute(this, ATTR_TITLE_NAME, ATTR_TITLE_DEFAULT_VALUE));

        getNodeGroups().add(new FlowStaticGroup(this));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    /**
     * Gets value of access attribute.
     * 
     * @return value of access attribute
     * @see #setTestFeedbackAccess
     */
    public TestFeedbackAccess getTestFeedbackAccess() {
        return getAttributes().getTestFeedbackAttribute(ATTR_ACCESS_NAME).getValue();
    }

    /**
     * Sets new value of access attribute.
     * 
     * @param testFeedbackAccess new value of access attribute
     * @see #getTestFeedbackAccess
     */
    public void setTestFeedbackAccess(TestFeedbackAccess testFeedbackAccess) {
        getAttributes().getTestFeedbackAttribute(ATTR_ACCESS_NAME).setValue(testFeedbackAccess);
    }

    /**
     * Gets value of showHide attribute.
     * 
     * @return value of showHide attribute
     * @see #setVisibilityMode
     */
    public VisibilityMode getVisibilityMode() {
        return getAttributes().getVisibilityModeAttribute(ATTR_VISIBILITY_MODE_NAME).getValue();
    }

    /**
     * Sets new value of showHide attribute.
     * 
     * @param visibilityMode new value of showHide attribute
     * @see #getVisibilityMode
     */
    public void setVisibilityMode(VisibilityMode visibilityMode) {
        getAttributes().getVisibilityModeAttribute(ATTR_VISIBILITY_MODE_NAME).setValue(visibilityMode);
    }

    /**
     * Gets value of outcomeIdentifier attribute.
     * 
     * @return value of outcomeIdentifier attribute
     * @see #setOutcomeIdentifier
     */
    public Identifier getOutcomeIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_OUTCOME_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of outcomeIdentifier attribute.
     * 
     * @param outcomeIdentifier new value of outcomeIdentifier attribute
     * @see #getOutcomeIdentifier
     */
    public void setOutcomeIdentifier(Identifier outcomeIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_OUTCOME_IDENTIFIER_NAME).setValue(outcomeIdentifier);
    }

    /**
     * Gets value of identifier attribute.
     * 
     * @return value of identifier attribute
     * @see #setOutcomeValue
     */
    public Identifier getOutcomeValue() {
        return getAttributes().getIdentifierAttribute(ATTR_OUTCOME_VALUE_NAME).getValue();
    }

    /**
     * Sets new value of identifier attribute.
     * 
     * @param outcomeValue new value of identifier attribute
     * @see #getOutcomeValue
     */
    public void setOutcomeValue(Identifier outcomeValue) {
        getAttributes().getIdentifierAttribute(ATTR_OUTCOME_VALUE_NAME).setValue(outcomeValue);
    }

    /**
     * Gets value of title attribute.
     * 
     * @return value of title attribute
     * @see #setTitle
     */
    public String getTitle() {
        return getAttributes().getStringAttribute(ATTR_TITLE_NAME).getValue();
    }

    /**
     * Sets new value of title attribute.
     * 
     * @param title new value of title attribute
     * @see #getTitle()
     */
    public void setTitle(String title) {
        getAttributes().getStringAttribute(ATTR_TITLE_NAME).setValue(title);
    }

    @Override
    protected void validateAttributes(ValidationContext context, AbstractValidationResult result) {
        super.validateAttributes(context, result);

        if (getOutcomeIdentifier() != null && getRootObject(AssessmentTest.class).getOutcomeDeclaration(getOutcomeIdentifier()) == null) {
            result.add(new ValidationWarning(this, "Cannot find " + OutcomeDeclaration.CLASS_TAG + ": " + getOutcomeIdentifier()));
        }
    }

    @Override
    protected void validateChildren(ValidationContext context, AbstractValidationResult result) {
        super.validateChildren(context, result);

        if (getChildren().size() == 0) {
            result.add(new ValidationWarning(this, "Feedback should contain something."));
        }
    }

    private List<FlowStatic> getChildren() {
        return getNodeGroups().getFlowStaticGroup().getFlowStatics();
    }

    /**
     * Returns true if this feedback can be displayed for given requested access; false otherwise.
     * 
     * @param requestedAccess given requested access
     * @return true if this feedback can be displayed for given requested access; false otherwise
     */
    public boolean isVisible(AssessmentTestState testState, TestFeedbackAccess requestedAccess) {
        if (getTestFeedbackAccess() != requestedAccess) {
            return false;
        }

        boolean match = false;

        final Value outcomeValue = testState.getOutcomeValue(getOutcomeIdentifier());
        if (outcomeValue != null && !outcomeValue.isNull() && outcomeValue.getBaseType().isIdentifier()) {
            if (outcomeValue.getCardinality() == Cardinality.SINGLE &&
                    ((IdentifierValue) outcomeValue).stringValue().equals(getOutcomeValue())) {
                match = true;
            }
            if (outcomeValue.getCardinality() == Cardinality.MULTIPLE &&
                    ((MultipleValue) outcomeValue).contains(new IdentifierValue(getOutcomeValue()))) {
                match = true;
            }
        }

        switch (getVisibilityMode()) {
            case SHOW_IF_MATCH:
                return match;

            case HIDE_IF_MATCH:
                return !match;

            default:
                throw new AssertionError("Unsupported " + getVisibilityMode().getClass().getSimpleName() +
                        ": " + getVisibilityMode());
        }
    }
}
