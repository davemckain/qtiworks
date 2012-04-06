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
package uk.ac.ed.ph.jqtiplus.node.shared.declaration;


import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.shared.FieldValueGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.shared.FieldValue;
import uk.ac.ed.ph.jqtiplus.node.shared.FieldValueParent;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * Default value of variableDeclaration.
 * Specification of defaultValue is not good. Instead of [1..*] fieldValues, defaultValue should contain 1 baseValue
 * or multiple or ordered or record expression. It would be much more powerful and consistent.
 * 
 * @author Jiri Kajaba
 */
public class DefaultValue extends AbstractNode implements FieldValueParent {

    private static final long serialVersionUID = -3631226504088317341L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "defaultValue";

    /** Name of interpretation attribute in xml schema. */
    public static final String ATTR_INTERPRETATION_NAME = "interpretation";

    public DefaultValue(VariableDeclaration parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new StringAttribute(this, ATTR_INTERPRETATION_NAME, false));

        getNodeGroups().add(new FieldValueGroup(this, 1, null));
    }

    /**
     * Creates object with a given value.
     * 
     * @param parent parent of this object
     * @param value value to use
     */
    public DefaultValue(VariableDeclaration parent, Value value) {
        this(parent);

        getFieldValues().addAll(FieldValue.computeValues(this, value));
        evaluate();
    }


    @Override
    public VariableDeclaration getParent() {
        return (VariableDeclaration) super.getParent();
    }

    /**
     * Gets value of interpretation attribute.
     * 
     * @return value of interpretation attribute
     * @see #setInterpretation
     */
    public String getInterpretation() {
        return getAttributes().getStringAttribute(ATTR_INTERPRETATION_NAME).getComputedValue();
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
     * Gets fieldValue children.
     * 
     * @return fieldValue children
     */
    public List<FieldValue> getFieldValues() {
        return getNodeGroups().getFieldValueGroup().getFieldValues();
    }

    @Override
    public Cardinality getCardinality() {
        return getParent().getCardinality();
    }

    @Override
    public BaseType getBaseType() {
        return getParent().getBaseType();
    }

    @Override
    protected void validateChildren(ValidationContext context) {
        super.validateChildren(context);

        final Cardinality cardinality = getParent().getCardinality();
        if (cardinality != null) {
            if (cardinality.isSingle() && getFieldValues().size() > 1) {
                context.add(new ValidationError(this, "Invalid values count. Expected: " + 1 + ". Found: " + getFieldValues().size()));
            }
        }
    }

    /**
     * Evaluates value of this defaultValue.
     * 
     * @return evaluated value of this defaultValue
     */
    public Value evaluate() {
        return FieldValue.computeValue(getCardinality(), getFieldValues());
    }

    //    @Override
    //    public void load(Node sourceNode) {
    //        super.load(sourceNode);
    //        evaluate();
    //    }
}
