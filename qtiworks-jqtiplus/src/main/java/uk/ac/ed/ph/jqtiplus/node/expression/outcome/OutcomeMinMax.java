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
package uk.ac.ed.ph.jqtiplus.node.expression.outcome;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Super class for outcomeMaximum and outcomeMinimum expressions.
 * <p>
 * Defines all attributes, because they are same for both child expressions.
 *
 * @author Jiri Kajaba
 */
public abstract class OutcomeMinMax extends ItemSubset {

    private static final long serialVersionUID = -3681037862858609105L;

    /** Name of outcomeIdentifier attribute in xml schema. */
    public static final String ATTR_OUTCOME_IDENTIFIER_NAME = "outcomeIdentifier";

    /** Name of weightIdentifier attribute in xml schema. */
    public static final String ATTR_WEIGHT_IDENTIFIER_NAME = "weightIdentifier";

    public OutcomeMinMax(final ExpressionParent parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new IdentifierAttribute(this, ATTR_OUTCOME_IDENTIFIER_NAME, true));
        getAttributes().add(new IdentifierAttribute(this, ATTR_WEIGHT_IDENTIFIER_NAME, false));
    }

    public Identifier getOutcomeIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_OUTCOME_IDENTIFIER_NAME).getComputedValue();
    }

    public void setOutcomeIdentifier(final Identifier outcomeIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_OUTCOME_IDENTIFIER_NAME).setValue(outcomeIdentifier);
    }


    public Identifier getWeightIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_WEIGHT_IDENTIFIER_NAME).getComputedValue();
    }

    public void setWeightIdentifier(final Identifier weightIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_WEIGHT_IDENTIFIER_NAME).setValue(weightIdentifier);
    }


    @Override
    protected final Value handleSubset(final TestProcessingContext testProcessingContext, final List<TestPlanNode> matchedTestPlanNodes) {
        final List<SingleValue> resultValues = new ArrayList<SingleValue>();
        for (final TestPlanNode itemRefNode : matchedTestPlanNodes) {
            final ItemProcessingContext itemProcessingContext = testProcessingContext.getItemProcessingContext(itemRefNode);
            final VariableDeclaration declaration = itemProcessingContext.checkLocalVariableReference(this, getOutcomeIdentifier());
            if (itemProcessingContext.checkVariableType(this, declaration, VariableType.OUTCOME)) {
                final OutcomeDeclaration outcomeDeclaration = (OutcomeDeclaration) declaration;
                if (outcomeDeclaration.getCardinality().isSingle()) {
                    if (!outcomeDeclaration.getBaseType().isNumeric() || outcomeDeclaration.getNormalMaximum() == null) {
                        return NullValue.INSTANCE;
                    }
                    final AssessmentItemRef assessmentItemRef = (AssessmentItemRef) testProcessingContext.getTestProcessingMap().resolveAbstractPart(itemRefNode);
                    final double maximum = getMinOrMax(outcomeDeclaration);
                    final double weight = assessmentItemRef.lookupWeight(getWeightIdentifier());

                    resultValues.add(new FloatValue(maximum * weight));
                }
            }
        }
        return MultipleValue.createMultipleValue(resultValues);
    }

    protected abstract double getMinOrMax(OutcomeDeclaration outcomeDeclaration);
}
