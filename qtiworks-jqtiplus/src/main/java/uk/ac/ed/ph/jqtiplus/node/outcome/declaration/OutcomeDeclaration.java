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
package uk.ac.ed.ph.jqtiplus.node.outcome.declaration;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ViewMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.LookupTableGroup;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.View;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.net.URI;
import java.util.List;

/**
 * Outcome variables are declared by outcome declarations.
 *
 * May also optionally be used as a root node for independent outcome declaration resource
 * files with associated standards metadata.
 *
 * @author Jiri Kajaba
 */
public final class OutcomeDeclaration extends VariableDeclaration implements RootNode {

    private static final long serialVersionUID = -5519664280437668195L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "outcomeDeclaration";

    /** Name of view attribute in xml schema. */
    public static final String ATTR_VIEWS_NAME = View.QTI_CLASS_NAME;

    /** Name of interpretation attribute in xml schema. */
    public static final String ATTR_INTERPRETATION_NAME = "interpretation";

    /** Name of longInterpretation attribute in xml schema. */
    public static final String ATTR_LONG_INTERPRETATION = "longInterpretation";

    /** Name of normalMaximum attribute in xml schema. */
    public static final String ATTR_NORMAL_MAXIMUM_NAME = "normalMaximum";

    /** Name of normalMinimum attribute in xml schema. */
    public static final String ATTR_NORMAL_MINIMUM_NAME = "normalMinimum";

    /** Name of masteryValue attribute in xml schema. */
    public static final String ATTR_MASTERY_VALUE_NAME = "masteryValue";

    /** System ID of this RootNode (optional) */
    private URI systemId;

    public OutcomeDeclaration(final AssessmentObject parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new ViewMultipleAttribute(this, ATTR_VIEWS_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_INTERPRETATION_NAME, false));
        getAttributes().add(new UriAttribute(this, ATTR_LONG_INTERPRETATION, false));
        getAttributes().add(new FloatAttribute(this, ATTR_NORMAL_MAXIMUM_NAME, false));
        getAttributes().add(new FloatAttribute(this, ATTR_NORMAL_MINIMUM_NAME, false));
        getAttributes().add(new FloatAttribute(this, ATTR_MASTERY_VALUE_NAME, false));

        getNodeGroups().add(new LookupTableGroup(this));
    }

    @Override
    public VariableType getVariableType() {
        return VariableType.OUTCOME;
    }

    /**
     * Gets value of view attribute.
     *
     * @return value of view attribute
     */
    public List<View> getViews() {
        return getAttributes().getViewMultipleAttribute(ATTR_VIEWS_NAME).getComputedValue();
    }

    public void setViews(final List<View> value) {
        getAttributes().getViewMultipleAttribute(ATTR_VIEWS_NAME).setValue(value);
    }


    public String getInterpretation() {
        return getAttributes().getStringAttribute(ATTR_INTERPRETATION_NAME).getComputedValue();
    }

    public void setInterpretation(final String interpretation) {
        getAttributes().getStringAttribute(ATTR_INTERPRETATION_NAME).setValue(interpretation);
    }


    public URI getLongInterpretation() {
        return getAttributes().getUriAttribute(ATTR_LONG_INTERPRETATION).getComputedValue();
    }

    public void setLongInterpretation(final URI longInterpretation) {
        getAttributes().getUriAttribute(ATTR_LONG_INTERPRETATION).setValue(longInterpretation);
    }


    public Double getNormalMaximum() {
        return getAttributes().getFloatAttribute(ATTR_NORMAL_MAXIMUM_NAME).getComputedValue();
    }

    public void setNormalMaximum(final Double normalMaximum) {
        getAttributes().getFloatAttribute(ATTR_NORMAL_MAXIMUM_NAME).setValue(normalMaximum);
    }


    public Double getNormalMinimum() {
        return getAttributes().getFloatAttribute(ATTR_NORMAL_MINIMUM_NAME).getComputedValue();
    }

    public void setNormalMinimum(final Double normalMinimum) {
        getAttributes().getFloatAttribute(ATTR_NORMAL_MINIMUM_NAME).setValue(normalMinimum);
    }


    public Double getMasteryValue() {
        return getAttributes().getFloatAttribute(ATTR_MASTERY_VALUE_NAME).getComputedValue();
    }

    public void setMasteryValue(final Double masteryValue) {
        getAttributes().getFloatAttribute(ATTR_MASTERY_VALUE_NAME).setValue(masteryValue);
    }


    public LookupTable<?,?> getLookupTable() {
        return getNodeGroups().getLookupTableGroup().getLookupTable();
    }

    public void setLookupTable(final LookupTable<?,?> lookupTable) {
        getNodeGroups().getLookupTableGroup().setLookupTable(lookupTable);
    }


    @Override
    protected void validateThis(final ValidationContext context) {
        if (getNormalMaximum() != null) {
            if (getCardinality() != null && !getCardinality().isSingle()) {
                context.fireAttributeValidationWarning(getAttributes().get(ATTR_NORMAL_MAXIMUM_NAME),
                        "Attribute " + ATTR_NORMAL_MAXIMUM_NAME
                        + " will be ignored for cardinality: "
                        + getCardinality());
            }
            else if (getBaseType() != null && !getBaseType().isNumeric()) {
                context.fireAttributeValidationWarning(getAttributes().get(ATTR_NORMAL_MAXIMUM_NAME),
                        "Attribute " + ATTR_NORMAL_MAXIMUM_NAME
                        + " will be ignored for baseType: "
                        + getBaseType());
            }
            else if (getNormalMaximum().doubleValue() <= 0) {
                context.fireAttributeValidationError(getAttributes().get(ATTR_NORMAL_MAXIMUM_NAME),
                        "Attribute " + ATTR_NORMAL_MAXIMUM_NAME
                        + " must be positive.");
            }
        }

        if (getNormalMinimum() != null) {
            if (getCardinality() != null && !getCardinality().isSingle()) {
                context.fireAttributeValidationWarning(getAttributes().get(ATTR_NORMAL_MINIMUM_NAME),
                        "Attribute " + ATTR_NORMAL_MINIMUM_NAME
                        + " will be ignored for cardinality: "
                        + getCardinality());
            }
            else if (getBaseType() != null && !getBaseType().isNumeric()) {
                context.fireAttributeValidationWarning(getAttributes().get(ATTR_NORMAL_MINIMUM_NAME),
                        "Attribute " + ATTR_NORMAL_MINIMUM_NAME
                        + " will be ignored for baseType: "
                        + getBaseType());
            }
        }

        if (getCardinality() != null && getCardinality().isSingle() &&
                getBaseType() != null && getBaseType().isNumeric() &&
                getNormalMaximum() != null && getNormalMinimum() != null
                && getNormalMaximum().doubleValue() < getNormalMinimum().doubleValue()) {
            context.fireAttributeValidationError(getAttributes().get(ATTR_NORMAL_MAXIMUM_NAME),
                    "Attribute " + ATTR_NORMAL_MAXIMUM_NAME
                    + " cannot be lower than attribute "
                    + ATTR_NORMAL_MINIMUM_NAME);
        }
    }

    @Override
    public URI getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(final URI systemId) {
        this.systemId = systemId;
    }

    @Override
    public String toString() {
        return super.toString()
                + "(systemId=" + systemId
                + ")";
    }
}
