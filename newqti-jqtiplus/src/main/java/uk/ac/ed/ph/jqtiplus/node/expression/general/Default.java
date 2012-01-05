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

import uk.ac.ed.ph.jqtiplus.control.AssessmentItemRefController;
import uk.ac.ed.ph.jqtiplus.control.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * This expression looks up the declaration of an itemVariable and returns the associated defaultValue or NULL
 * if no default value was declared. When used in outcomes processing item identifier prefixing (see variable) may be
 * used to obtain the default value from an individual item.
 * 
 * @author Jiri Kajaba
 */
public class Default extends LookupExpression {

    private static final long serialVersionUID = -1031669571748673912L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "default";

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public Default(ExpressionParent parent) {
        super(parent);
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    //----------------------------------------------------------------------

    @Override
    protected Value evaluateInThisItem(ItemProcessingContext itemContext, Identifier itemVariableIdentifier) {
        return itemContext.computeDefaultValue(itemVariableIdentifier);
    }

    @Override
    protected Value evaluateInThisTest(TestProcessingContext testContext, Identifier testVariableIdentifier) {
        /* Default don't get overridden in tests */
        /* FIXME: Should we even be allowed to access test variables here? */
        final VariableDeclaration variableDeclaration = testContext.getTest().getVariableDeclaration(testVariableIdentifier);
        return variableDeclaration != null ? variableDeclaration.getDefaultValue().evaluate() : NullValue.INSTANCE;
    }

    @Override
    protected Value evaluateInReferencedItem(int depth, AssessmentItemRefController itemRefController, Identifier itemVariableIdentifier) {
        return itemRefController.getItemController().computeDefaultValue(itemVariableIdentifier);
    }
}
