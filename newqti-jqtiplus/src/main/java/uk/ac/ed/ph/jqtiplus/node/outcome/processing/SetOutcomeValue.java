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
package uk.ac.ed.ph.jqtiplus.node.outcome.processing;

import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.LookupTable;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xperimental.control.ProcessingContext;

/**
 * The setOutcomeValue rule sets the value of an outcome variable to the value obtained from the associated expression.
 * An outcome variable can be updated with reference to A previously assigned value, in other words, the outcome variable
 * being set may appear in the expression where it takes the value previously assigned to it.
 * <p>
 * Special care is required when using the numeric base-types because floating point values can not be assigned to integer variables and vice-versa. The
 * truncate, round, or integerToFloat operators must be used to achieve numeric type conversion.
 * 
 * @author Jiri Kajaba
 */
public class SetOutcomeValue extends ProcessOutcomeValue {

    private static final long serialVersionUID = -7790249550437462840L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "setOutcomeValue";

    /**
     * Constructs rule.
     * 
     * @param parent parent of this rule
     */
    public SetOutcomeValue(XmlNode parent) {
        super(parent);
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public Cardinality[] getRequiredCardinalities(ValidationContext context, int index) {
        if (getIdentifier() != null) {
            final OutcomeDeclaration declaration = getRootObject(AssessmentTest.class).getOutcomeDeclaration(getIdentifier());
            if (declaration != null && declaration.getCardinality() != null) {
                return new Cardinality[] { declaration.getCardinality() };
            }
        }

        return Cardinality.values();
    }

    @Override
    public BaseType[] getRequiredBaseTypes(ValidationContext context, int index) {
        if (getIdentifier() != null) {
            final OutcomeDeclaration declaration = getRootObject(AssessmentTest.class).getOutcomeDeclaration(getIdentifier());
            if (declaration != null && declaration.getBaseType() != null) {
                return new BaseType[] { declaration.getBaseType() };
            }
        }

        return BaseType.values();
    }

    @Override
    public void validate(ValidationContext context, AbstractValidationResult result) {
        super.validate(context, result);

        if (getIdentifier() != null) {
            final OutcomeDeclaration declaration = getRootObject(AssessmentTest.class).getOutcomeDeclaration(getIdentifier());
            if (declaration != null && declaration.getLookupTable() != null) {
                result.add(new ValidationWarning(this, "Never used " + LookupTable.DISPLAY_NAME
                        + " in "
                        + OutcomeDeclaration.CLASS_TAG
                        + ": "
                        + getIdentifier()));
            }
        }
    }

    @Override
    public void evaluate(ProcessingContext context) throws RuntimeValidationException {
        final Value value = getExpression().evaluate(context);

        final OutcomeDeclaration declaration = context.getOwner().getOutcomeDeclaration(getIdentifier());
        if (declaration == null) {
            throw new QTIEvaluationException("Cannot find " + OutcomeDeclaration.CLASS_TAG + ": " + getIdentifier());
        }
        context.setOutcomeValue(declaration, value);
    }
}
