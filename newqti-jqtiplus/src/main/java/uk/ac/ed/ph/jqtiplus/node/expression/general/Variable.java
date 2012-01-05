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

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.control.AssessmentItemRefController;
import uk.ac.ed.ph.jqtiplus.control.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.AttributeValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This expression looks up the value of an itemVariable that has been declared in A corresponding
 * variableDeclaration or is one of the built-in variables. The result has the base-type and cardinality
 * declared for the variable subject to the type promotion of weighted outcomes (see below).
 * <p>
 * During outcomes processing, values taken from an individual item session can be looked up by prefixing the name of the item variable with the identifier
 * assigned to the item in the assessmentItemRef, separated by A period character.
 * <p>
 * For example, to obtain the value of the SCORE variable in the item referred to as Q01 you would use A variable instance with identifier Q01.SCORE.
 * <p>
 * When looking up the value of A response variable it always takes the value assigned to it by the candidate's last submission. Unsubmitted responses are not
 * available during expression evaluation.
 * <p>
 * The value of an item variable taken from an item instantiated multiple times from the same assessmentItemRef (through the use of selection withReplacement)
 * is taken from the last instance submitted if submission is simultaneous, otherwise it is undefined.
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public class Variable extends LookupExpression {

    private static final long serialVersionUID = -6019493794571357535L;

    private static Logger logger = LoggerFactory.getLogger(Variable.class);

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "variable";

    /** Name of weightIdentifier attribute in xml schema. */
    public static final String ATTR_WEIGHT_IDENTIFIER_NAME = "weightIdentifier";

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public Variable(ExpressionParent parent) {
        super(parent);

        getAttributes().add(new IdentifierAttribute(this, ATTR_WEIGHT_IDENTIFIER_NAME, null));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    /**
     * Gets value of weightIdentifier attribute.
     * 
     * @return value of weightIdentifier attribute
     * @see #setWeightIdentifier
     */
    public Identifier getWeightIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_WEIGHT_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of weightIdentifier attribute.
     * 
     * @param weightIdentifier new value of weightIdentifier attribute
     * @see #getWeightIdentifier
     */
    public void setWeightIdentifier(Identifier weightIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_WEIGHT_IDENTIFIER_NAME).setValue(weightIdentifier);
    }

    //----------------------------------------------------------------------

    @Override
    protected void validateAdditionalAttributes(ValidationResult result, AssessmentItemRef resolvedItemReference) {
        if (resolvedItemReference == null) {
            if (getWeightIdentifier() != null) {
                result.add(new AttributeValidationError(getAttributes().get(ATTR_WEIGHT_IDENTIFIER_NAME),
                        "Weights may only be used when referencing item variables from tests"));
            }
        }
        else {
            final Identifier weightIdentifier = getWeightIdentifier();
            if (weightIdentifier != null) {
                if (resolvedItemReference.getWeight(weightIdentifier) == null) {
                    result.add(new ValidationWarning(getAttributes().get(ATTR_WEIGHT_IDENTIFIER_NAME), "Cannot find weight " + weightIdentifier));
                }
            }
        }
    }

    //----------------------------------------------------------------------

    @Override
    protected Value evaluateInThisItem(ItemProcessingContext itemContext, Identifier itemVariableIdentifier) {
        return itemContext.lookupVariable(itemVariableIdentifier);
    }

    @Override
    protected Value evaluateInThisTest(TestProcessingContext testContext, Identifier testVariableIdentifier) {
        return testContext.lookupVariable(testVariableIdentifier);
    }

    @Override
    protected Value evaluateInReferencedItem(int depth, AssessmentItemRefController itemRefController, Identifier itemVariableIdentifier) {
        Value result = itemRefController.getItemController().lookupVariable(itemVariableIdentifier);

        /* Maybe apply weight */
        final Identifier weightIdentifier = getWeightIdentifier();
        if (weightIdentifier != null && result instanceof NumberValue) {
            result = applyWeight(depth, itemRefController.getItemRef(), (NumberValue) result, weightIdentifier);
        }

        return result;
    }

    private FloatValue applyWeight(int depth, AssessmentItemRef itemRef, NumberValue value, Identifier weightIdentifier) {
        final double number = value.doubleValue();
        final double weight = itemRef.lookupWeight(weightIdentifier);

        logger.debug("{}Applying weight with identifier {} having value {}.", new Object[] { getIndent(depth), weightIdentifier, weight });

        final FloatValue result = new FloatValue(number * weight);

        return result;
    }
}
