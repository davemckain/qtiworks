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

import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedTestVariableReference;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * Implementation of <code>correct</code>.
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public final class Correct extends LookupExpression {

    private static final long serialVersionUID = -280130278009155973L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "correct";

    public Correct(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);
    }

    //----------------------------------------------------------------------

    @Override
    protected void validateResolvedItemVariableReference(final ValidationContext context,
            final VariableDeclaration resolvedDeclaration) {
        /* Ensure that the referenced variable is a response variable. */
        context.checkVariableType(this, resolvedDeclaration, VariableType.RESPONSE);
    }

    @Override
    protected void validateResolvedTestVariableReference(final ValidationContext context,
            final ResolvedTestVariableReference resolvedReference) {
        context.checkVariableType(this, resolvedReference.getVariableDeclaration(), VariableType.RESPONSE);
    }

    //----------------------------------------------------------------------

    @Override
    public Value evaluateInThisItem(final ItemProcessingContext itemProcessingContext, final Identifier itemVariableIdentifier) {
        final Value correctResponseValue = itemProcessingContext.computeCorrectResponse(itemVariableIdentifier); /* (May be null) */
        return correctResponseValue!=null ? correctResponseValue : NullValue.INSTANCE;
    }

    @Override
    public Value evaluateInThisTest(final TestProcessingContext testContext, final Identifier testVariableIdentifier) {
        /* Tests do not contain response variables, so the result here is always null */
        return NullValue.INSTANCE;
    }

    @Override
    public Value evaluateInReferencedItem(final ItemProcessingContext itemProcessingContext,
            final AssessmentItemRef assessmentItemRef, final TestPlanNode testPlanNode,
            final Identifier itemVariableIdentifier) {
        return evaluateInThisItem(itemProcessingContext, itemVariableIdentifier);
    }
}
