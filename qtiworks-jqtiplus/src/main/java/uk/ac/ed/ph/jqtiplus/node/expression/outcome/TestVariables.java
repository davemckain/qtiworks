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

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.BaseTypeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * This expression, which can only be used in outcomes processing, simultaneously looks up the value
 * of an itemVariable in a sub-set of the items referred to in a test. Only variables with single
 * cardinality are considered, all NULL values are ignored. The result has cardinality multiple and
 * base-type as specified below.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class TestVariables extends ItemSubset {

    private static final long serialVersionUID = 9071109513721979269L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "testVariables";

    /** Name of variableIdentifier attribute in xml schema. */
    public static final String ATTR_VARIABLE_IDENTIFIER_NAME = "variableIdentifier";

    /** Name of baseType attribute in xml schema. */
    public static final String ATTR_BASE_TYPE_NAME = BaseType.QTI_CLASS_NAME;

    /** Name of weightIdentifier attribute in xml schema. */
    public static final String ATTR_WEIGHT_IDENTIFIER_NAME = "weightIdentifier";

    public TestVariables(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IdentifierAttribute(this, ATTR_VARIABLE_IDENTIFIER_NAME, true));
        getAttributes().add(new BaseTypeAttribute(this, ATTR_BASE_TYPE_NAME, false));
        getAttributes().add(new IdentifierAttribute(this, ATTR_WEIGHT_IDENTIFIER_NAME, false));
    }


    public Identifier getVariableIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_VARIABLE_IDENTIFIER_NAME).getComputedValue();
    }

    public void setVariableIdentifier(final Identifier variableIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_VARIABLE_IDENTIFIER_NAME).setValue(variableIdentifier);
    }


    public BaseType getBaseTypeAttrValue() {
        return getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).getComputedValue();
    }

    public void setBaseTypeAttrValue(final BaseType baseType) {
        getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).setValue(baseType);
    }


    public Identifier getWeightIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_WEIGHT_IDENTIFIER_NAME).getComputedValue();
    }

    public void setWeightIdentifier(final Identifier weightIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_WEIGHT_IDENTIFIER_NAME).setValue(weightIdentifier);
    }

    @Override
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        if (getBaseTypeAttrValue() != null) {
            return new BaseType[] { getBaseTypeAttrValue() };
        }

        return new BaseType[] { BaseType.INTEGER, BaseType.FLOAT };
    }

    @Override
    protected Value handleSubset(final TestProcessingContext testProcessingContext, final List<TestPlanNode> matchedTestPlanNodes) {
        final List<SingleValue> values = new ArrayList<SingleValue>();
        final BaseType baseType = getBaseTypeAttrValue();
        boolean floatFound = false;
        for (final TestPlanNode itemRefNode : matchedTestPlanNodes) {
            final ItemProcessingContext itemProcessingContext = testProcessingContext.getItemProcessingContext(itemRefNode);
            final AssessmentItemRef assessmentItemRef = (AssessmentItemRef) testProcessingContext.getTestProcessingMap().resolveAbstractPart(itemRefNode);
            final Value value = itemProcessingContext.evaluateVariableValue(getVariableIdentifier());
            if (!value.isNull() && value.getCardinality() == Cardinality.SINGLE) {
                if ((baseType != null && value.getBaseType() == baseType) ||
                        (baseType == null && value.getBaseType().isNumeric())) {
                    if (getWeightIdentifier() != null && (baseType == null || baseType.isFloat())) {
                        final double weight = assessmentItemRef.lookupWeight(getWeightIdentifier());
                        final double number = ((NumberValue) value).doubleValue();
                        values.add(new FloatValue(number * weight));
                        floatFound = true;
                    }
                    else {
                        values.add((SingleValue) value);
                        if (value.getBaseType().isFloat()) {
                            floatFound = true;
                        }
                    }
                }
            }
        }

        final List<SingleValue> resultValues = new ArrayList<SingleValue>();
        for (SingleValue value : values) {
            if (baseType == null && value.getBaseType().isInteger() && floatFound) {
                value = new FloatValue(((IntegerValue) value).doubleValue());
            }
            resultValues.add(value);
        }
        return MultipleValue.createMultipleValue(resultValues);
    }
}
