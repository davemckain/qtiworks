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
package uk.ac.ed.ph.jqtiplus.node.item.template.processing;

import uk.ac.ed.ph.jqtiplus.exception.QtiEvaluationException;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * @author Jonathon Hare
 */
public final class SetDefaultValue extends ProcessTemplateValue {

    private static final long serialVersionUID = -1151254253813354211L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "setDefaultValue";

    public SetDefaultValue(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);
    }

    @Override
    public Cardinality[] getRequiredCardinalities(final ValidationContext context, final int index) {
        if (getIdentifier() != null) {
            final ResponseDeclaration declaration = getRootNode(AssessmentItem.class).getResponseDeclaration(getIdentifier());
            if (declaration != null && declaration.getCardinality() != null) {
                return new Cardinality[] { declaration.getCardinality() };
            }
        }

        return Cardinality.values();
    }

    @Override
    public BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        if (getIdentifier() != null) {
            final ResponseDeclaration declaration = getRootNode(AssessmentItem.class).getResponseDeclaration(getIdentifier());
            if (declaration != null && declaration.getBaseType() != null) {
                return new BaseType[] { declaration.getBaseType() };
            }
        }

        return BaseType.values();
    }

    @Override
    public void evaluate(final ItemProcessingContext context) throws RuntimeValidationException {
        final Value value = getExpression().evaluate(context);
        final AssessmentItem item = context.getSubjectItem();
        final ItemSessionState itemSessionState = context.getItemSessionState();

        final ResponseDeclaration responseDeclaration = item.getResponseDeclaration(getIdentifier());
        if (responseDeclaration != null) {
            itemSessionState.setOverriddenResponseDefaultValue(responseDeclaration, value);
        }
        else {
            final OutcomeDeclaration outcomeDeclaration = item.getOutcomeDeclaration(getIdentifier());
            if (outcomeDeclaration != null) {
                itemSessionState.setOverriddenOutcomeDefaultValue(outcomeDeclaration, value);
            }
            else {
                throw new QtiEvaluationException("Cannot find response or outcome declaration " + getIdentifier());
            }
        }
    }

    @Override
    protected void validateAttributes(final ValidationContext context) {
        super.validateAttributes(context);

        final Identifier identifier = getIdentifier();
        if (identifier != null) {
            final AssessmentItem item = getRootNode(AssessmentItem.class);
            if (item.getResponseDeclaration(identifier) == null && item.getOutcomeDeclaration(identifier) == null) {
                context.fireValidationError(this, "Cannot find response or outcome declaration " + getIdentifier());
            }
        }
    }
}
