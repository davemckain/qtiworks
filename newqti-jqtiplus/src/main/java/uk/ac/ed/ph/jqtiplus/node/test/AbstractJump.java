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


import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractObject;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * Parent for all jump objects (preCondition and branchRule).
 * 
 * @author Jiri Kajaba
 */
public abstract class AbstractJump extends AbstractObject implements ExpressionParent
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs object.
     *
     * @param parent parent of created object
     */
    public AbstractJump(AbstractPart parent)
    {
        super(parent);

        getNodeGroups().add(new ExpressionGroup(this, 1, 1));
    }

    @Override
    public AbstractPart getParent()
    {
        return (AbstractPart) super.getParent();
    }

    /**
     * Gets expression child.
     *
     * @return expression child
     * @see #setExpression
     */
    public Expression getExpression()
    {
        return getNodeGroups().getExpressionGroup().getExpression();
    }

    /**
     * Sets new expression child.
     *
     * @param expression new expression child
     * @see #getExpression
     */
    public void setExpression(Expression expression)
    {
        getNodeGroups().getExpressionGroup().setExpression(expression);
    }

    public Cardinality[] getRequiredCardinalities(ValidationContext context, int index)
    {
        return new Cardinality[] {Cardinality.SINGLE};
    }

    public BaseType[] getRequiredBaseTypes(ValidationContext context, int index)
    {
        return new BaseType[] {BaseType.BOOLEAN};
    }

    @Override
    public ValidationResult validate(ValidationContext context)
    {
        ValidationResult result = super.validate(context);

        TestPart parentTestPart = getParent().getParentTestPart();
        if (parentTestPart.getNavigationMode() != null && parentTestPart.getSubmissionMode() != null)
        {
            if (getParent() != parentTestPart && !parentTestPart.areJumpsEnabled())
                result.add(new ValidationWarning(this, "Jump will be ignored for modes: " +
                        parentTestPart.getNavigationMode() + " " + parentTestPart.getSubmissionMode()));
        }

        return result;
    }

    /**
     * Evaluates condition of this jump.
     *
     * @return evaluated condition of this jump
     */
    public boolean evaluate(ProcessingContext context) {
        Value value = getExpression().evaluate(context);

        if (value.isNull()) {
            return false;
        }
        boolean result = ((BooleanValue) value).booleanValue();

        return result;
    }
}
