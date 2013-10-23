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

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.TestFeedbackAccessAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.VisibilityModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.group.accessibility.ApipAccessibilityGroup;
import uk.ac.ed.ph.jqtiplus.group.content.FlowStaticGroup;
import uk.ac.ed.ph.jqtiplus.group.item.StylesheetGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.ContentContainer;
import uk.ac.ed.ph.jqtiplus.node.accessibility.ApipAccessibility;
import uk.ac.ed.ph.jqtiplus.node.accessibility.ApipAccessibilityBearer;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.item.Stylesheet;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
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
 * @author Zack Pierce
 */
public final class TestFeedback extends AbstractNode implements ContentContainer, ApipAccessibilityBearer {

    private static final long serialVersionUID = 6567681516055125776L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "testFeedback";

    /** Name of access attribute in xml schema. */
    public static final String ATTR_ACCESS_NAME = TestFeedbackAccess.QTI_CLASS_NAME;

    /** Name of showHide attribute in xml schema. */
    public static final String ATTR_VISIBILITY_MODE_NAME = VisibilityMode.QTI_CLASS_NAME;

    /** Name of outcomeIdentifier attribute in xml schema. */
    public static final String ATTR_OUTCOME_IDENTIFIER_NAME = "outcomeIdentifier";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_OUTCOME_VALUE_NAME = "identifier";

    /** Name of title attribute in xml schema. */
    public static final String ATTR_TITLE_NAME = "title";

    public TestFeedback(final ControlObject<?> parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new TestFeedbackAccessAttribute(this, ATTR_ACCESS_NAME, true));
        getAttributes().add(new VisibilityModeAttribute(this, ATTR_VISIBILITY_MODE_NAME, true));
        getAttributes().add(new IdentifierAttribute(this, ATTR_OUTCOME_IDENTIFIER_NAME, true));
        getAttributes().add(new IdentifierAttribute(this, ATTR_OUTCOME_VALUE_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_TITLE_NAME, false));

        getNodeGroups().add(new FlowStaticGroup(this));
        getNodeGroups().add(new StylesheetGroup(this));
        getNodeGroups().add(new ApipAccessibilityGroup(this, false));
    }


    public TestFeedbackAccess getTestFeedbackAccess() {
        return getAttributes().getTestFeedbackAttribute(ATTR_ACCESS_NAME).getComputedValue();
    }

    public void setTestFeedbackAccess(final TestFeedbackAccess testFeedbackAccess) {
        getAttributes().getTestFeedbackAttribute(ATTR_ACCESS_NAME).setValue(testFeedbackAccess);
    }


    public VisibilityMode getVisibilityMode() {
        return getAttributes().getVisibilityModeAttribute(ATTR_VISIBILITY_MODE_NAME).getComputedValue();
    }

    public void setVisibilityMode(final VisibilityMode visibilityMode) {
        getAttributes().getVisibilityModeAttribute(ATTR_VISIBILITY_MODE_NAME).setValue(visibilityMode);
    }


    public Identifier getOutcomeIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_OUTCOME_IDENTIFIER_NAME).getComputedValue();
    }

    public void setOutcomeIdentifier(final Identifier outcomeIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_OUTCOME_IDENTIFIER_NAME).setValue(outcomeIdentifier);
    }


    public Identifier getOutcomeValue() {
        return getAttributes().getIdentifierAttribute(ATTR_OUTCOME_VALUE_NAME).getComputedValue();
    }

    public void setOutcomeValue(final Identifier outcomeValue) {
        getAttributes().getIdentifierAttribute(ATTR_OUTCOME_VALUE_NAME).setValue(outcomeValue);
    }


    public String getTitle() {
        return getAttributes().getStringAttribute(ATTR_TITLE_NAME).getComputedValue();
    }

    public void setTitle(final String title) {
        getAttributes().getStringAttribute(ATTR_TITLE_NAME).setValue(title);
    }


    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Identifier outcomeIdentifier = getOutcomeIdentifier();
        if (outcomeIdentifier!=null) {
            final VariableDeclaration declaration = context.checkLocalVariableReference(this, outcomeIdentifier);
            if (declaration!=null) {
                context.checkVariableType(this, declaration, VariableType.OUTCOME);
            }
        }

        if (getChildren().size() == 0) {
            context.fireValidationWarning(this, "Feedback should contain something.");
        }
    }

    public List<FlowStatic> getChildren() {
        return getNodeGroups().getFlowStaticGroup().getFlowStatics();
    }

    /**
     * Returns true if this feedback can be displayed for given requested access; false otherwise.
     *
     * @param requestedAccess given requested access
     * @return true if this feedback can be displayed for given requested access; false otherwise
     */
    public boolean isVisible(final TestSessionState testSessionState, final TestFeedbackAccess requestedAccess) {
        if (getTestFeedbackAccess() != requestedAccess) {
            return false;
        }

        boolean match = false;

        final Value outcomeValue = testSessionState.getOutcomeValue(getOutcomeIdentifier());
        if (outcomeValue != null && !outcomeValue.isNull() && outcomeValue.getBaseType().isIdentifier()) {
            if (outcomeValue.getCardinality() == Cardinality.SINGLE &&
                    ((IdentifierValue) outcomeValue).identifierValue().equals(getOutcomeValue())) {
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
                throw new QtiLogicException("Unsupported " + getVisibilityMode().getClass().getSimpleName() +
                        ": " + getVisibilityMode());
        }
    }

    /**
     * Gets mutable list of stylesheet children.
     *
     * @return stylesheet children
     */
    public List<Stylesheet> getStylesheets() {
        return getNodeGroups().getStylesheetGroup().getStylesheets();
    }

    /**
     * Gets apipAccessibility child
     *
     * @return apipAccessibility child
     * @see #setApipAccessibility
     */
    public ApipAccessibility getApipAccessibility() {
        return getNodeGroups().getApipAccessibilityGroup().getApipAccessibility();
    }

    /**
     * Sets apipAccessibility child
     * @param apipAccessibility
     */
    public void setApipAccessibility(final ApipAccessibility apipAccessibility) {
        getNodeGroups().getApipAccessibilityGroup().setApipAccessibility(apipAccessibility);
    }
}
