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
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerOrVariableRefAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.IntegerOrVariableRef;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the <code>repeat</code> expression.
 * <p>
 * Note that this implements {@link AbstractExpression} rather than {@link AbstractFunctionalExpression},
 * as we need to re-evaluate the child elements over and over, rather than evaluating them once and reusing
 * their values.
 *
 * @author David McKain
 */
public final class Repeat extends AbstractExpression {

    private static final long serialVersionUID = 5067099219155350914L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "repeat";

    /** Name of n attribute in xml schema. */
    public static final String ATTR_NUMBER_REPEATS_NAME = "numberRepeats";

    public Repeat(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerOrVariableRefAttribute(this, ATTR_NUMBER_REPEATS_NAME, true));
    }

    public IntegerOrVariableRef getNumberRepeats() {
        return getAttributes().getIntegerOrVariableRefAttribute(ATTR_NUMBER_REPEATS_NAME).getValue();
    }

    public void setNumberRepeats(final IntegerOrVariableRef index) {
        getAttributes().getIntegerOrVariableRefAttribute(ATTR_NUMBER_REPEATS_NAME).setValue(index);
    }

    @Override
    public BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        return getRequiredSameBaseTypes(context, index, true);
    }

    @Override
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        if (getChildren().size() == 1) {
            return getChildren().get(0).getProducedBaseTypes(context);
        }

        return super.getProducedBaseTypes(context);
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final IntegerOrVariableRef numberRepeatsTemplate = getNumberRepeats();
        if (numberRepeatsTemplate.isConstantInteger()) {
            final int numberRepeats = numberRepeatsTemplate.getConstantIntegerValue().intValue();
            if (numberRepeats < 1) {
                context.fireAttributeValidationError(getAttributes().get(ATTR_NUMBER_REPEATS_NAME),
                        "Attribute " + ATTR_NUMBER_REPEATS_NAME + " (" + numberRepeats + ") must be at least 1");
            }
        }
    }

    @Override
    protected Value evaluateValidSelfAndChildren(final ProcessingContext context, final int depth) {
        /* Check runtime value of numberRepeats */
        final Value numberRepeatsValue = getNumberRepeats().evaluate(this, context);
        if (numberRepeatsValue.isNull()) {
            context.fireRuntimeWarning(this, "numberRepeats evaluated to NULL. Returning NULL");
            return NullValue.INSTANCE;
        }
        final int numberRepeats = ((IntegerValue) numberRepeatsValue).intValue();
        if (numberRepeats < 1) {
            context.fireRuntimeWarning(this, "numberRepeats ended up being less than 1. Returning NULL");
            return NullValue.INSTANCE;
        }

        /* Now evaluate child expression repeatedly and build up result */
        final List<SingleValue> resultList = new ArrayList<SingleValue>();
        for (int i=0; i<numberRepeats; i++) {
            final Value[] childValues = evaluateChildren(context, depth);
            if (isAnyChildNull(childValues)) {
                return NullValue.INSTANCE;
            }
            for (int j=0; j<childValues.length; j++) {
                resultList.add((SingleValue) childValues[j]);
            }
        }
        return OrderedValue.createOrderedValue(resultList);
    }
}
