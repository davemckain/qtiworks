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

package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.BaseTypeValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;


/**
 * The match operator takes two sub-expressions which must both have the same base-type and cardinality.
 * The result is A single boolean with A value of true if the two expressions represent the same value
 * and false if they do not. If either sub-expression is NULL then the operator results in NULL.
 * <p>
 * The match operator must not be confused with broader notions of equality such as numerical equality.
 * To avoid confusion, the match operator should not be used to compare subexpressions with base-types
 * of float and must not be used on sub-expressions with A base-type of duration.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * 
 * @author Jiri Kajaba
 */
public class Match extends AbstractExpression
{
    private static final long serialVersionUID = 1L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "match";

    /**
     * Constructs expression.
     *
     * @param parent parent of this expression
     */
    public Match(ExpressionParent parent)
    {
        super(parent);
    }

    @Override
    public String getClassTag()
    {
        return CLASS_TAG;
    }

    @Override
    public Cardinality[] getRequiredCardinalities(ValidationContext context, int index)
    {
        return getRequiredSameCardinalities(context, index, false);
    }

    @Override
    public BaseType[] getRequiredBaseTypes(ValidationContext context, int index)
    {
        return getRequiredSameBaseTypes(context, index, false);
    }

    @Override
    protected void validateChildren(ValidationContext context, ValidationResult result)
    {
        super.validateChildren(context, result);

        for (Expression expression : getChildren())
        {
            Cardinality[] cardinalities = expression.getProducedCardinalities(context);
            if (cardinalities.length == 1 && cardinalities[0].isRecord())
            {
                for (Expression ex : expression.getChildren())
                {
                    BaseType[] baseTypes = ex.getProducedBaseTypes(context);
                    if (baseTypes.length == 1 && baseTypes[0].isDuration())
                        result.add(new BaseTypeValidationError(this, BaseType.values(new BaseType[] {BaseType.DURATION}), baseTypes));
                }
            }
        }
    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth)
    {
        if (isAnyChildNull(context)) {
            return NullValue.INSTANCE;
        }

        Value firstValue = getFirstChild().getValue(context);
        Value secondValue = getSecondChild().getValue(context);

        return BooleanValue.valueOf(firstValue.equals(secondValue));
    }
}
