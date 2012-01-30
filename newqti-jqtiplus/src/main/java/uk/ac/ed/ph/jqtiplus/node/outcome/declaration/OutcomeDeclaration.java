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
package uk.ac.ed.ph.jqtiplus.node.outcome.declaration;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ViewMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.group.NodeGroup;
import uk.ac.ed.ph.jqtiplus.group.NodeGroupList;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.LookupTableGroup;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.block.ContainerBlock;
import uk.ac.ed.ph.jqtiplus.node.content.variable.PrintedVariable;
import uk.ac.ed.ph.jqtiplus.node.expression.general.LookupExpression;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.TestFeedback;
import uk.ac.ed.ph.jqtiplus.node.test.View;
import uk.ac.ed.ph.jqtiplus.resolution.VariableResolutionException;
import uk.ac.ed.ph.jqtiplus.validation.AttributeValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Outcome variables are declared by outcome declarations.
 * 
 * @author Jiri Kajaba
 */
public class OutcomeDeclaration extends VariableDeclaration {
    
    private static final long serialVersionUID = -5519664280437668195L;
    
    private static final Logger logger = LoggerFactory.getLogger(OutcomeDeclaration.class);

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "outcomeDeclaration";

    /** Name of view attribute in xml schema. */
    public static final String ATTR_VIEWS_NAME = View.QTI_CLASS_NAME;

    /** Default value of view attribute. */
    public static final List<View> ATTR_VIEWS_DEFAULT_VALUE = null;

    /** Name of interpretation attribute in xml schema. */
    public static final String ATTR_INTERPRETATION_NAME = "interpretation";

    /** Default value of interpretation attribute. */
    public static final String ATTR_INTERPRETATION_DEFAULT_VALUE = null;

    /** Name of longInterpretation attribute in xml schema. */
    public static final String ATTR_LONG_INTERPRETATION = "longInterpretation";

    /** Default value of longInterpretation attribute. */
    public static final URI ATTR_LONG_INTERPRETATION_DEFAULT_VALUE = null;

    /** Name of normalMaximum attribute in xml schema. */
    public static final String ATTR_NORMAL_MAXIMUM_NAME = "normalMaximum";

    /** Default value of normalMaximum attribute. */
    public static final Double ATTR_NORMAL_MAXIMUM_DEFAULT_VALUE = null;

    /** Name of normalMinimum attribute in xml schema. */
    public static final String ATTR_NORMAL_MINIMUM_NAME = "normalMinimum";

    /** Default value of normalMinimum attribute. */
    public static final Double ATTR_NORMAL_MINIMUM_DEFAULT_VALUE = null;

    /** Name of masteryValue attribute in xml schema. */
    public static final String ATTR_MASTERY_VALUE_NAME = "masteryValue";

    /** Default value of masteryValue attribute. */
    public static final Double ATTR_MASTERY_VALUE_DEFAULT_VALUE = null;

    public OutcomeDeclaration(AssessmentObject parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new ViewMultipleAttribute(this, ATTR_VIEWS_NAME, ATTR_VIEWS_DEFAULT_VALUE));
        getAttributes().add(new StringAttribute(this, ATTR_INTERPRETATION_NAME, ATTR_INTERPRETATION_DEFAULT_VALUE));
        getAttributes().add(new UriAttribute(this, ATTR_LONG_INTERPRETATION, ATTR_LONG_INTERPRETATION_DEFAULT_VALUE));
        getAttributes().add(new FloatAttribute(this, ATTR_NORMAL_MAXIMUM_NAME, ATTR_NORMAL_MAXIMUM_DEFAULT_VALUE));
        getAttributes().add(new FloatAttribute(this, ATTR_NORMAL_MINIMUM_NAME, ATTR_NORMAL_MINIMUM_DEFAULT_VALUE));
        getAttributes().add(new FloatAttribute(this, ATTR_MASTERY_VALUE_NAME, ATTR_MASTERY_VALUE_DEFAULT_VALUE));

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
        return getAttributes().getViewMultipleAttribute(ATTR_VIEWS_NAME).getValues();
    }

    /**
     * Gets value of interpretation attribute.
     * 
     * @return value of interpretation attribute
     * @see #setInterpretation
     */
    public String getInterpretation() {
        return getAttributes().getStringAttribute(ATTR_INTERPRETATION_NAME).getValue();
    }

    /**
     * Sets new value of interpretation attribute.
     * 
     * @param interpretation new value of interpretation attribute
     * @see #getInterpretation
     */
    public void setInterpretation(String interpretation) {
        getAttributes().getStringAttribute(ATTR_INTERPRETATION_NAME).setValue(interpretation);
    }

    /**
     * Gets value of longInterpretation attribute.
     * 
     * @return value of longInterpretation attribute
     * @see #setLongInterpretation
     */
    public URI getLongInterpretation() {
        return getAttributes().getUriAttribute(ATTR_LONG_INTERPRETATION).getValue();
    }

    /**
     * Sets new value of longInterpretation attribute.
     * 
     * @param longInterpretation new value of longInterpretation attribute
     * @see #getLongInterpretation
     */
    public void setLongInterpretation(URI longInterpretation) {
        getAttributes().getUriAttribute(ATTR_LONG_INTERPRETATION).setValue(longInterpretation);
    }

    /**
     * Gets value of normalMaximum attribute.
     * 
     * @return value of normalMaximum attribute
     * @see #setNormalMaximum
     */
    public Double getNormalMaximum() {
        return getAttributes().getFloatAttribute(ATTR_NORMAL_MAXIMUM_NAME).getValue();
    }

    /**
     * Sets new value of normalMaximum attribute.
     * 
     * @param normalMaximum new value of normalMaximum attribute
     * @see #getNormalMaximum
     */
    public void setNormalMaximum(Double normalMaximum) {
        getAttributes().getFloatAttribute(ATTR_NORMAL_MAXIMUM_NAME).setValue(normalMaximum);
    }

    /**
     * Gets value of normalMinimum attribute.
     * 
     * @return value of normalMinimum attribute
     * @see #setNormalMinimum
     */
    public Double getNormalMinimum() {
        return getAttributes().getFloatAttribute(ATTR_NORMAL_MINIMUM_NAME).getValue();
    }

    /**
     * Sets new value of normalMinimum attribute.
     * 
     * @param normalMinimum new value of normalMinimum attribute
     * @see #getNormalMinimum()
     */
    public void setNormalMinimum(Double normalMinimum) {
        getAttributes().getFloatAttribute(ATTR_NORMAL_MINIMUM_NAME).setValue(normalMinimum);
    }

    /**
     * Gets value of masteryValue attribute.
     * 
     * @return value of masteryValue attribute
     * @see #setMasteryValue
     */
    public Double getMasteryValue() {
        return getAttributes().getFloatAttribute(ATTR_MASTERY_VALUE_NAME).getValue();
    }

    /**
     * Sets new value of masteryValue attribute.
     * 
     * @param masteryValue new value of masteryValue attribute
     * @see #getMasteryValue
     */
    public void setMasteryValue(Double masteryValue) {
        getAttributes().getFloatAttribute(ATTR_MASTERY_VALUE_NAME).setValue(masteryValue);
    }

    /**
     * Gets lookupTable child.
     * 
     * @return lookupTable child
     * @see #setLookupTable
     */
    public LookupTable getLookupTable() {
        return getNodeGroups().getLookupTableGroup().getLookupTable();
    }

    /**
     * Sets new lookupTable child.
     * 
     * @param lookupTable new lookupTable child
     * @see #getLookupTable
     */
    public void setLookupTable(LookupTable lookupTable) {
        getNodeGroups().getLookupTableGroup().setLookupTable(lookupTable);
    }

    @Override
    protected void validateAttributes(ValidationContext context) {
        super.validateAttributes(context);

        if (getNormalMaximum() != null) {
            if (getCardinality() != null && !getCardinality().isSingle()) {
                context.add(new ValidationWarning(getAttributes().get(ATTR_NORMAL_MAXIMUM_NAME), "Attribute " + ATTR_NORMAL_MAXIMUM_NAME
                        + " will be ignored for cardinality: "
                        + getCardinality()));
            }
            else if (getBaseType() != null && !getBaseType().isNumeric()) {
                context.add(new ValidationWarning(getAttributes().get(ATTR_NORMAL_MAXIMUM_NAME), "Attribute " + ATTR_NORMAL_MAXIMUM_NAME
                        + " will be ignored for baseType: "
                        + getBaseType()));
            }
            else if (getNormalMaximum() <= 0) {
                context.add(new AttributeValidationError(getAttributes().get(ATTR_NORMAL_MAXIMUM_NAME), "Attribute " + ATTR_NORMAL_MAXIMUM_NAME
                        + " must be positive."));
            }
        }

        if (getNormalMinimum() != null) {
            if (getCardinality() != null && !getCardinality().isSingle()) {
                context.add(new ValidationWarning(getAttributes().get(ATTR_NORMAL_MINIMUM_NAME), "Attribute " + ATTR_NORMAL_MINIMUM_NAME
                        + " will be ignored for cardinality: "
                        + getCardinality()));
            }
            else if (getBaseType() != null && !getBaseType().isNumeric()) {
                context.add(new ValidationWarning(getAttributes().get(ATTR_NORMAL_MINIMUM_NAME), "Attribute " + ATTR_NORMAL_MINIMUM_NAME
                        + " will be ignored for baseType: "
                        + getBaseType()));
            }
        }

        if (getCardinality() != null && getCardinality().isSingle() &&
                getBaseType() != null && getBaseType().isNumeric() &&
                getNormalMaximum() != null && getNormalMinimum() != null && getNormalMaximum() < getNormalMinimum()) {
            context.add(new AttributeValidationError(getAttributes().get(ATTR_NORMAL_MAXIMUM_NAME), "Attribute " + ATTR_NORMAL_MAXIMUM_NAME
                    + " cannot be lower than attribute "
                    + ATTR_NORMAL_MINIMUM_NAME
                    + "."));
        }
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);

        // DM: I've commented this out, since I don't think a warning should be given if test variables are not read;
        // which would be comment in summative assessment scenarios
        //        if (context instanceof TestValidationContext && !isRead(context, getParentRoot())) {
        //            context.add(new ValidationWarning(this, "Outcome declaration is never read."));
        //        }
    }

    /**
     * Returns true if this outcomeDeclaration is read by given node or its children; false otherwise.
     * 
     * @param xmlNode node
     * @return true if this outcomeDeclaration is read by given node or its children; false otherwise
     */
    private boolean isRead(ValidationContext context, XmlNode xmlNode) {
        if (xmlNode instanceof PrintedVariable) {
            final PrintedVariable printedVariable = (PrintedVariable) xmlNode;
            if (printedVariable.getIdentifier() != null && printedVariable.getIdentifier().equals(getIdentifier())) {
                return true;
            }
        }
        else if (xmlNode instanceof LookupExpression) {
            final LookupExpression expression = (LookupExpression) xmlNode;
            VariableDeclaration targetVariableDeclaration;
            try {
                targetVariableDeclaration = expression.lookupTargetVariableDeclaration(context);
                if (targetVariableDeclaration != null && targetVariableDeclaration.equals(this)) {
                    return true;
                }
            }
            catch (VariableResolutionException e) {
                logger.warn("Refactor this:", e);
            }
        }
        else if (xmlNode instanceof TestFeedback) {
            final TestFeedback feedback = (TestFeedback) xmlNode;
            if (feedback.getOutcomeIdentifier() != null && feedback.getOutcomeIdentifier().equals(getIdentifier())) {
                return true;
            }
        }
        if (xmlNode instanceof ContainerBlock) {
            final ContainerBlock container = (ContainerBlock) xmlNode;
            for (final XmlNode block : container.getChildren()) {
                if (isRead(context, block)) {
                    return true;
                }
            }
        }

        final NodeGroupList groups = xmlNode.getNodeGroups();
        for (int i = 0; i < groups.size(); i++) {
            final NodeGroup group = groups.get(i);
            for (final XmlNode child : group.getChildren()) {
                if (isRead(context, child)) {
                    return true;
                }
            }
        }
        return false;
    }
}
