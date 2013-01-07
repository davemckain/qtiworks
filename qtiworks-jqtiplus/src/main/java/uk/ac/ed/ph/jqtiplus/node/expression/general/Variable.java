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
package uk.ac.ed.ph.jqtiplus.node.expression.general;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedTestVariableReference;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of <code>variable</code>
 *
 * @author Jiri Kajaba
 */
public final class Variable extends LookupExpression {

    private static final long serialVersionUID = -6019493794571357535L;

    private static Logger logger = LoggerFactory.getLogger(Variable.class);

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "variable";

    /** Name of weightIdentifier attribute in xml schema. */
    public static final String ATTR_WEIGHT_IDENTIFIER_NAME = "weightIdentifier";

    public Variable(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IdentifierAttribute(this, ATTR_WEIGHT_IDENTIFIER_NAME, false));
    }

    public Identifier getWeightIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_WEIGHT_IDENTIFIER_NAME).getComputedValue();
    }

    public void setWeightIdentifier(final Identifier weightIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_WEIGHT_IDENTIFIER_NAME).setValue(weightIdentifier);
    }

    //----------------------------------------------------------------------

    @Override
    protected void validateResolvedItemVariableReference(final ValidationContext context,
            final VariableDeclaration resolvedDeclaration) {
        final Identifier weightIdentifier = getWeightIdentifier();
        if (weightIdentifier!=null) {
            context.fireAttributeValidationError(getAttributes().get(ATTR_WEIGHT_IDENTIFIER_NAME),
                    "Weights may only be used when referencing item variables from tests");
        }
    }

    @Override
    protected void validateResolvedTestVariableReference(final ValidationContext context, final ResolvedTestVariableReference resolvedReference) {
        final Identifier weightIdentifier = getWeightIdentifier();
        if (weightIdentifier!=null) {
            if (resolvedReference.isItemVariableReference()) {
                final AssessmentItemRef assessmentItemRef = resolvedReference.getAssessmentItemRef();
                if (assessmentItemRef.getWeight(weightIdentifier)==null) {
                    context.fireAttributeValidationError(getAttributes().get(ATTR_WEIGHT_IDENTIFIER_NAME),
                            "Cannot find weight " + weightIdentifier);
                }
            }
        }
    }

    //----------------------------------------------------------------------

    @Override
    protected Value evaluateInThisItem(final ItemProcessingContext itemProcessingContext, final Identifier itemVariableIdentifier) {
        return itemProcessingContext.evaluateVariableValue(itemVariableIdentifier);
    }

    @Override
    public Value evaluateInThisTest(final TestProcessingContext testProcessingContext, final Identifier testVariableIdentifier) {
        return testProcessingContext.evaluateVariableValue(testVariableIdentifier);
    }

    @Override
    public Value evaluateInReferencedItem(final ItemProcessingContext itemProcessingContext,
            final AssessmentItemRef assessmentItemRef, final TestPlanNode testPlanNode,
            final Identifier itemVariableIdentifier) {
        Value result = itemProcessingContext.evaluateVariableValue(itemVariableIdentifier);

        /* Maybe apply weight */
        final Identifier weightIdentifier = getWeightIdentifier();
        if (weightIdentifier != null && result instanceof NumberValue) {
            result = applyWeight(assessmentItemRef, (NumberValue) result, weightIdentifier);
        }

        return result;
    }


    private FloatValue applyWeight(final AssessmentItemRef itemRef, final NumberValue value, final Identifier weightIdentifier) {
        final double number = value.doubleValue();
        final double weight = itemRef.lookupWeight(weightIdentifier);

        logger.debug("Applying weight with identifier {} having value {}.", new Object[] { weightIdentifier, weight });

        final FloatValue result = new FloatValue(number * weight);

        return result;
    }
}
