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
package uk.ac.ed.ph.jqtiplus.node.expression.general;

import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.legacy.AssessmentItemRefAttemptController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * This expression looks up the declaration of an itemVariable and returns the associated defaultValue or NULL
 * if no default value was declared. When used in outcomes processing item identifier prefixing (see variable) may be
 * used to obtain the default value from an individual item.
 *
 * @author Jiri Kajaba
 */
public final class Default extends LookupExpression {

    private static final long serialVersionUID = -1031669571748673912L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "default";

    public Default(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);
    }

    //----------------------------------------------------------------------

    @Override
    protected void validateResolvedVariableReference(final ValidationContext context, final VariableReferenceIdentifier variableReferenceIdentifier, final VariableDeclaration resolvedDeclaration) {
        /* Nothing to do */
    }

    //----------------------------------------------------------------------

    @Override
    protected Value evaluateInThisItem(final ItemProcessingContext itemContext, final Identifier itemVariableIdentifier) {
        return itemContext.computeDefaultValue(itemVariableIdentifier);
    }

    @Override
    protected Value evaluateInThisTest(final TestProcessingContext testContext, final Identifier testVariableIdentifier) {
        /* Default don't get overridden in tests */
        /* FIXME: Should we even be allowed to access test variables here? */
        final VariableDeclaration variableDeclaration = testContext.getSubjectTest().getVariableDeclaration(testVariableIdentifier);
        return variableDeclaration != null ? variableDeclaration.getDefaultValue().evaluate() : NullValue.INSTANCE;
    }

    @Override
    protected Value evaluateInReferencedItem(final int depth, final AssessmentItemRefAttemptController itemRefController, final Identifier itemVariableIdentifier) {
        return itemRefController.getItemController().computeDefaultValue(itemVariableIdentifier);
    }
}
