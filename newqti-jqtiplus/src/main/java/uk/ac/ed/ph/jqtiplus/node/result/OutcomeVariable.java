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
package uk.ac.ed.ph.jqtiplus.node.result;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ViewMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.group.shared.FieldValueGroup;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.FieldValue;
import uk.ac.ed.ph.jqtiplus.node.shared.FieldValueParent;
import uk.ac.ed.ph.jqtiplus.node.test.View;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.net.URI;
import java.util.List;

/**
 * Outcome variables are declared by outcome declarations.
 * 
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public class OutcomeVariable extends ItemVariable implements FieldValueParent {

    private static final long serialVersionUID = -8458195126681286797L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "outcomeVariable";

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

    //    /** Value of this variableDeclaration. */
    //    private Value value;

    public OutcomeVariable(AbstractResult parent) {
        super(parent, QTI_CLASS_NAME);
        
        getAttributes().add(new ViewMultipleAttribute(this, ATTR_VIEWS_NAME, ATTR_VIEWS_DEFAULT_VALUE));
        getAttributes().add(new StringAttribute(this, ATTR_INTERPRETATION_NAME, ATTR_INTERPRETATION_DEFAULT_VALUE));
        getAttributes().add(new UriAttribute(this, ATTR_LONG_INTERPRETATION, ATTR_LONG_INTERPRETATION_DEFAULT_VALUE));
        getAttributes().add(new FloatAttribute(this, ATTR_NORMAL_MAXIMUM_NAME, ATTR_NORMAL_MAXIMUM_DEFAULT_VALUE));
        getAttributes().add(new FloatAttribute(this, ATTR_NORMAL_MINIMUM_NAME, ATTR_NORMAL_MINIMUM_DEFAULT_VALUE));
        getAttributes().add(new FloatAttribute(this, ATTR_MASTERY_VALUE_NAME, ATTR_MASTERY_VALUE_DEFAULT_VALUE));

        getNodeGroups().add(new FieldValueGroup(this, null, null));
    }

    /**
     * Creates new outcomeVariable from given outcomeDeclaration.
     * 
     * @param parent parent of created outcomeVariable
     * @param declaration given outcomeDeclaration
     * @param value if provided, replaces value from outcomeDeclaration
     */
    public OutcomeVariable(AbstractResult parent, OutcomeDeclaration declaration, Value value) {
        this(parent);
        if (declaration != null) {
            setIdentifier(declaration.getIdentifier().toVariableReferenceIdentifier());
            setCardinality(declaration.getCardinality());
            setBaseType(declaration.getBaseType());
            getFieldValues().addAll(FieldValue.getValues(this, value));
            getViews().addAll(declaration.getViews());
            setInterpretation(declaration.getInterpretation());
            setLongInterpretation(declaration.getLongInterpretation());
            setNormalMaximum(declaration.getNormalMaximum());
            setNormalMinimum(declaration.getNormalMinimum());
            setMasteryValue(declaration.getMasteryValue());

            //            evaluate();
        }
    }

    /**
     * Creates new outcomeVariable from given identifier and value.
     * 
     * @param parent parent of created outcomeVariable
     * @param identifier identifier of created outcomeVariable (may be null)
     * @param value of created outcomeVariable (may be null)
     */
    public OutcomeVariable(AbstractResult parent, VariableReferenceIdentifier identifier, Value value) {
        this(parent);

        setIdentifier(identifier);
        if (value != null) {
            setCardinality(value.getCardinality());
            setBaseType(value.getBaseType());
            getFieldValues().addAll(FieldValue.getValues(this, value));

            //            evaluate();
        }
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
     * Gets value of this variableDeclaration.
     * 
     * @return value of this variableDeclaration
     */
    public Value getValue() {
        return FieldValue.getValue(getCardinality(), getFieldValues());
        //        return value;
    }

    //    /**
    //     * Evaluates value of this itemVariable.
    //     *
    //     * @return evaluated value of this itemVariable
    //     */
    //    public Value evaluate()
    //    {
    //        value = FieldValue.getValue(getCardinality(), getFieldValues());
    //
    //        return value;
    //    }

    /**
     * Gets fieldValue children.
     * 
     * @return fieldValue children
     */
    public List<FieldValue> getFieldValues() {
        return getNodeGroups().getFieldValueGroup().getFieldValues();
    }

    //    @Override
    //    public void load(JQTIExtensionManager jqtiController, Element sourceElement) {
    //        super.load(jqtiController, sourceElement);
    //        evaluate();
    //    }
}
