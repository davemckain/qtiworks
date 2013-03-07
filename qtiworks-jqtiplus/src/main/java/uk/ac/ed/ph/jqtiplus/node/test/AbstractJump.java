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

import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

/**
 * Parent for all jump objects (preCondition and branchRule).
 *
 * @see PreCondition
 * @see BranchRule
 *
 * @author Jiri Kajaba
 */
public abstract class AbstractJump extends AbstractNode implements ExpressionParent {

    private static final long serialVersionUID = -1364623115711904856L;

    public AbstractJump(final AbstractPart parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getNodeGroups().add(new ExpressionGroup(this, 1, 1));
    }

    @Override
    public AbstractPart getParent() {
        return (AbstractPart) super.getParent();
    }


    public Expression getExpression() {
        return getNodeGroups().getExpressionGroup().getExpression();
    }

    public void setExpression(final Expression expression) {
        getNodeGroups().getExpressionGroup().setExpression(expression);
    }


    @Override
    public Cardinality[] getRequiredCardinalities(final ValidationContext context, final int index) {
        return new Cardinality[] { Cardinality.SINGLE };
    }

    @Override
    public BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        return new BaseType[] { BaseType.BOOLEAN };
    }

    @ToRefactor
    @Override
    protected void validateThis(final ValidationContext context) {
        final TestPart parentTestPart = getParent().getEnclosingTestPart();
        if (parentTestPart.getNavigationMode() != null && parentTestPart.getSubmissionMode() != null) {
            if (getParent() != parentTestPart && !parentTestPart.areJumpsEnabled()) {
                context.fireValidationWarning(this, "Jump will be ignored for modes: " +
                        parentTestPart.getNavigationMode() + " " + parentTestPart.getSubmissionMode());
            }
        }
    }

    /**
     * Evaluates condition of this jump.
     *
     * @return evaluated condition of this jump
     */
    public boolean evaluatesTrue(final TestProcessingContext testProcessingContext) {
        final Value value = getExpression().evaluate(testProcessingContext);
        return BooleanValue.TRUE.equals(value);
    }
}
