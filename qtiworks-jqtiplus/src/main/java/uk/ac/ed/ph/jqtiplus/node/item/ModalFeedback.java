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
package uk.ac.ed.ph.jqtiplus.node.item;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.VisibilityModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.FlowStaticGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.VisibilityMode;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * Modal Feedback: i.e. feedback possibly presented in a dialog box, which stops any
 * interaction whilst displayed
 *
 * @author Jonathon Hare
 */
public class ModalFeedback extends AbstractNode {

    private static final long serialVersionUID = -3911613199124971014L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "modalFeedback";

    /** Name of outcomeIdentifier attribute in xml schema. */
    public static final String ATTR_OUTCOME_IDENTIFIER_NAME = "outcomeIdentifier";

    /** Name of showHide attribute in xml schema. */
    public static final String ATTR_VISIBILITY_MODE_NAME = VisibilityMode.QTI_CLASS_NAME;

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    /** Name of title attribute in xml schema. */
    public static final String ATTR_TITLE_NAME = "title";

    public ModalFeedback(final AssessmentItem parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new VisibilityModeAttribute(this, ATTR_VISIBILITY_MODE_NAME, true));
        getAttributes().add(new IdentifierAttribute(this, ATTR_OUTCOME_IDENTIFIER_NAME, true));
        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_TITLE_NAME, null, false));

        getNodeGroups().add(new FlowStaticGroup(this));
    }

    public List<FlowStatic> getFlowStatics() {
        return getNodeGroups().getFlowStaticGroup().getFlowStatics();
    }

    /**
     * @deprecated Please now use {@link #getFlowStatics()}
     */
    @Deprecated
    public List<FlowStatic> getChildren() {
        return getFlowStatics();
    }

    /**
     * Gets value of showHide attribute.
     *
     * @return value of showHide attribute
     * @see #setVisibilityMode
     */
    public VisibilityMode getVisibilityMode() {
        return getAttributes().getVisibilityModeAttribute(ATTR_VISIBILITY_MODE_NAME).getComputedValue();
    }

    /**
     * Sets new value of showHide attribute.
     *
     * @param visibilityMode new value of showHide attribute
     * @see #getVisibilityMode
     */
    public void setVisibilityMode(final VisibilityMode visibilityMode) {
        getAttributes().getVisibilityModeAttribute(ATTR_VISIBILITY_MODE_NAME).setValue(visibilityMode);
    }

    /**
     * Gets value of outcomeIdentifier attribute.
     *
     * @return value of outcomeIdentifier attribute
     * @see #setOutcomeIdentifier
     */
    public Identifier getOutcomeIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_OUTCOME_IDENTIFIER_NAME).getComputedValue();
    }

    /**
     * Sets new value of outcomeIdentifier attribute.
     *
     * @param outcomeIdentifier new value of outcomeIdentifier attribute
     * @see #getOutcomeIdentifier
     */
    public void setOutcomeIdentifier(final Identifier outcomeIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_OUTCOME_IDENTIFIER_NAME).setValue(outcomeIdentifier);
    }

    /**
     * Gets value of identifier attribute.
     *
     * @return value of identifier attribute
     */
    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    /**
     * Sets new value of identifier attribute.
     *
     * @param identifier new value of identifier attribute
     */
    public void setIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    @Override
    public void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Identifier outcomeIdentifier = getOutcomeIdentifier();
        if (outcomeIdentifier!=null) {
            final VariableDeclaration declaration = context.checkLocalVariableReference(this, getOutcomeIdentifier());
            if (declaration!=null) {
                context.checkVariableType(this, declaration, VariableType.OUTCOME);
                if (!declaration.hasSignature(Signature.SINGLE_IDENTIFIER, Signature.MULTIPLE_IDENTIFIER)) {
                    context.fireValidationError(this, "Expected outcome variable to be a single or multiple identifier");
                }
            }
        }
    }

    @Override
    protected void validateChildren(final ValidationContext context) {
        super.validateChildren(context);

        if (getFlowStatics().isEmpty()) {
            context.fireValidationWarning(this, "ModalFeedback is empty.");
        }
    }

    /**
     * Returns true if this feedback can be displayed.
     *
     * @return true if this feedback can be displayed; false otherwise
     */
    public boolean isVisible(final ItemProcessingContext itemProcessingContext) {
        final Identifier outcomeIdentifier = getOutcomeIdentifier();
        final Identifier identifier = getIdentifier();
        final VisibilityMode visibilityMode = getVisibilityMode();
        if (outcomeIdentifier==null || identifier==null || visibilityMode==null) {
            return false;
        }

        final IdentifierValue identifierValue = new IdentifierValue(identifier);
        final Value outcomeValue = itemProcessingContext.evaluateVariableValue(outcomeIdentifier, VariableType.OUTCOME);

        boolean identifierMatches;
        if (outcomeValue.hasCardinality(Cardinality.SINGLE)) {
            identifierMatches = outcomeValue.equals(identifierValue);
        }
        else {
            identifierMatches = ((MultipleValue) outcomeValue).contains(identifierValue);
        }
        return identifierMatches && visibilityMode.equals(VisibilityMode.SHOW_IF_MATCH)
                || !identifierMatches && visibilityMode.equals(VisibilityMode.HIDE_IF_MATCH);
    }
}
