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
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;


/**
 * The delete operator takes two sub-expressions which must both have the same base-type. The first
 * sub-expression must have single cardinality and the second must be A multiple or ordered container.
 * The result is A new container derived from the second sub-expression with all instances of the first
 * sub-expression removed.
 * <p>
 * For example, when applied to A and {B,A,C,A} the result is the container {B,C}.
 * <p>
 * If either sub-expression is NULL the result of the operator is NULL.
 * <p>
 * The delete operator should not be used on sub-expressions with A base-type of float because of the
 * poorly defined comparison of values. It must not be used on sub-expressions with A base-type of duration.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * 
 * @author Jiri Kajaba
 */
public class Delete extends AbstractExpression
{
    private static final long serialVersionUID = 1L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "delete";

    /**
     * Constructs expression.
     *
     * @param parent parent of this expression
     */
    public Delete(ExpressionParent parent)
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
        Cardinality[] required = super.getRequiredCardinalities(context, index);

        if (index == 1)
            required = Cardinality.intersection(required, getParentRequiredCardinalities(context));

        return required;
    }

    @Override
    public BaseType[] getRequiredBaseTypes(ValidationContext context, int index)
    {
        return getRequiredSameBaseTypes(context, index, true);
    }

    @Override
    public Cardinality[] getProducedCardinalities(ValidationContext context)
    {
        if (getChildren().size() != 2)
            return super.getProducedCardinalities(context);

        return getChildren().get(1).getProducedCardinalities(context);
    }

    @Override
    public BaseType[] getProducedBaseTypes(ValidationContext context)
    {
        if (getChildren().size() != 2)
            return super.getProducedBaseTypes(context);

        return getChildren().get(1).getProducedBaseTypes(context);
    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth)
    {
        if (isAnyChildNull(context))
            return NullValue.INSTANCE;

        ListValue value = (ListValue) ((ListValue) getSecondChild().getValue(context)).clone();
        value.removeAll((SingleValue) getFirstChild().getValue(context));

        if (value.isNull())
            return NullValue.INSTANCE;

        return value;
    }
}
