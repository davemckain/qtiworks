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
package uk.ac.ed.ph.jqtiplus.node.shared;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.BaseTypeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.CardinalityAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.DefaultValueGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableNode;
import uk.ac.ed.ph.jqtiplus.node.UniqueNode;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;

/**
 * Item variables are declared by variable declarations.
 * All variables must be declared except for the built-in session variables which are declared implicitly
 * and must not be declared. The purpose of the declaration is to associate an identifier with the variable
 * and to identify the runtime type of the variable's value.
 * 
 * @author Jiri Kajaba
 */
public abstract class VariableDeclaration extends AbstractNode implements UniqueNode<Identifier> {

    private static final long serialVersionUID = -2027681803807985451L;

    /** Name of cardinality attribute in xml schema. */
    public static final String ATTR_CARDINALITY_NAME = Cardinality.QTI_CLASS_NAME;

    /** Name of baseType attribute in xml schema. */
    public static final String ATTR_BASE_TYPE_NAME = BaseType.QTI_CLASS_NAME;

    /** Default value of baseType attribute. */
    public static final BaseType ATTR_BASE_TYPE_DEFAULT_VALUE = null;

    public VariableDeclaration(AssessmentObject parent, String localName) {
        super(parent, localName);

        getAttributes().add(new IdentifierAttribute(this, IdentifiableNode.ATTR_IDENTIFIER_NAME));
        getAttributes().add(new CardinalityAttribute(this, ATTR_CARDINALITY_NAME));
        getAttributes().add(new BaseTypeAttribute(this, ATTR_BASE_TYPE_NAME, ATTR_BASE_TYPE_DEFAULT_VALUE));

        getNodeGroups().add(new DefaultValueGroup(this));
    }

    public abstract VariableType getVariableType();
    
    public boolean isType(VariableType... allowedTypes) {
        if (allowedTypes.length==0) {
            /* Interpret as "no restriction" */
            return true;
        }
        for (VariableType type : allowedTypes) {
            if (type==getVariableType()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets value of identifier attribute.
     * 
     * @return value of identifier attribute
     * @see #setIdentifier
     */
    @Override
    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of identifier attribute.
     * 
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    @Override
    public void setIdentifier(Identifier identifier) {
        getAttributes().getIdentifierAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    /**
     * Gets value of cardinality attribute.
     * 
     * @return value of cardinality attribute
     * @see #setCardinality
     */
    public Cardinality getCardinality() {
        return getAttributes().getCardinalityAttribute(ATTR_CARDINALITY_NAME).getValue();
    }

    /**
     * Sets new value of cardinality attribute.
     * 
     * @param cardinality new value of cardinality attribute
     * @see #getCardinality
     */
    public void setCardinality(Cardinality cardinality) {
        getAttributes().getCardinalityAttribute(ATTR_CARDINALITY_NAME).setValue(cardinality);
    }

    /**
     * Gets value of baseType attribute.
     * 
     * @return value of baseType attribute
     * @see #setBaseType
     */
    public BaseType getBaseType() {
        return getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).getValue();
    }

    /**
     * Sets new value of baseType attribute.
     * 
     * @param baseType new value of baseType attribute
     * @see #getBaseType
     */
    public void setBaseType(BaseType baseType) {
        getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).setValue(baseType);
    }

    /**
     * Gets defaultValue child.
     * 
     * @return defaultValue child
     * @see #setDefaultValue
     */
    public DefaultValue getDefaultValue() {
        return getNodeGroups().getDefaultValueGroup().getDefaultValue();
    }

    /**
     * Sets new defaultValue child.
     * 
     * @param defaultValue new defaultValue child
     * @see #getDefaultValue
     */
    public void setDefaultValue(DefaultValue defaultValue) {
        getNodeGroups().getDefaultValueGroup().setDefaultValue(defaultValue);
    }

    @Override
    protected void validateAttributes(ValidationContext context) {
        super.validateAttributes(context);

        validateUniqueIdentifier(context.getValidationResult(), getAttributes().getIdentifierAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME), getIdentifier());

        final Cardinality cardinality = getCardinality();
        if (cardinality != null) {
            if (!cardinality.isRecord() && getBaseType() == null) {
                context.add(new ValidationError(this, "Attribute (" + ATTR_BASE_TYPE_NAME + ") is not defined."));
            }

            if (cardinality.isRecord() && getBaseType() != null) {
                context.add(new ValidationWarning(this, "Attribute (" + ATTR_BASE_TYPE_NAME + ") should not be defined."));
            }
        }
    }

    @Override
    public final String computeXPathComponent() {
        final Identifier identifier = getIdentifier();
        if (identifier != null) {
            return getClassTag() + "[@identifier=\"" + identifier.toString() + "\"]";
        }
        return super.computeXPathComponent();
    }

    @Override
    public String toString() {
        return super.toString() + "(identifier=" + getIdentifier() + ")";
    }
}
