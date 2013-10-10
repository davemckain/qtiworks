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
package uk.ac.ed.ph.jqtiplus.node.shared;

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.BaseTypeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.CardinalityAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.DefaultValueGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableNode;
import uk.ac.ed.ph.jqtiplus.node.UniqueNode;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Signature;

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

    public VariableDeclaration(final AssessmentObject parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new IdentifierAttribute(this, IdentifiableNode.ATTR_IDENTIFIER_NAME, true));
        getAttributes().add(new CardinalityAttribute(this, ATTR_CARDINALITY_NAME));
        getAttributes().add(new BaseTypeAttribute(this, ATTR_BASE_TYPE_NAME, false));

        getNodeGroups().add(new DefaultValueGroup(this));
    }

    public abstract VariableType getVariableType();

    public boolean isType(final VariableType... allowedTypes) {
        for (final VariableType type : allowedTypes) {
            if (type==getVariableType()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCardinality(final Cardinality... allowedCardinalities) {
        final Cardinality cardinality = getCardinality();
        return cardinality!=null && cardinality.isOneOf(allowedCardinalities);
    }

    public boolean hasBaseType(final BaseType... allowedBaseType) {
        final BaseType baseType = getBaseType();
        return baseType!=null && baseType.isOneOf(allowedBaseType);
    }

    public boolean hasSignature(final Signature... allowedSignatures) {
        boolean matches = false;
        final Cardinality cardinality = getCardinality();
        final BaseType baseType = getBaseType();
        for (final Signature signature : allowedSignatures) {
            if (cardinality==Cardinality.RECORD) {
                matches = signature==Signature.RECORD;
            }
            else {
                matches = signature.getCardinality()==cardinality
                        && signature.getBaseType()==baseType;
            }
            if (matches) {
                break;
            }
        }
        return matches;
    }

    @Override
    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    @Override
    public void setIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).setValue(identifier);
    }


    public Cardinality getCardinality() {
        return getAttributes().getCardinalityAttribute(ATTR_CARDINALITY_NAME).getComputedValue();
    }

    public void setCardinality(final Cardinality cardinality) {
        getAttributes().getCardinalityAttribute(ATTR_CARDINALITY_NAME).setValue(cardinality);
    }


    public BaseType getBaseType() {
        return getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).getComputedValue();
    }

    public void setBaseType(final BaseType baseType) {
        getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).setValue(baseType);
    }


    public DefaultValue getDefaultValue() {
        return getNodeGroups().getDefaultValueGroup().getDefaultValue();
    }

    public void setDefaultValue(final DefaultValue defaultValue) {
        getNodeGroups().getDefaultValueGroup().setDefaultValue(defaultValue);
    }

    /**
     * Computes the {@link Signature} of this declaration from its {@link Cardinality}
     * and {@link BaseType}, returning null if this cannot be determined.
     */
    public Signature computeSignature() {
        final Cardinality cardinality = getCardinality();
        final BaseType baseType = getBaseType();
        return Signature.getSignature(cardinality, baseType);
    }

    public boolean hasValidSignature() {
        final Cardinality cardinality = getCardinality();
        final BaseType baseType = getBaseType();
        return cardinality!=null && !(cardinality==Cardinality.RECORD && baseType!=null);
    }

    public static boolean isReservedIdentifier(final Identifier identifier) {
        Assert.notNull(identifier, "identifier");
        return identifier.equals(QtiConstants.VARIABLE_DURATION_IDENTIFIER)
                || identifier.equals(QtiConstants.VARIABLE_COMPLETION_STATUS_IDENTIFIER)
                || identifier.equals(QtiConstants.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER);
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Identifier identifier = getIdentifier();
        if (identifier!=null) {
            /* Make sure identifier is unique */
            validateUniqueIdentifier(context, getAttributes().getIdentifierAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME), identifier);

            /* Make sure identifier is not reserved */
            if (isReservedIdentifier(identifier)) {
                context.fireValidationError(this, "The identifier" + identifier + " is reserved for in-built response variables");
            }
        }

        final Cardinality cardinality = getCardinality();
        if (cardinality!=null) {
            final BaseType baseType = getBaseType();
            if (!cardinality.isRecord() && baseType==null) {
                context.fireValidationError(this, "Attribute '" + ATTR_BASE_TYPE_NAME + "' must be defined for variables with cardinality " + cardinality);
            }

            if (cardinality.isRecord() && baseType!=null) {
                context.fireValidationWarning(this, "Attribute '" + ATTR_BASE_TYPE_NAME + "' must not be defined for variables with cardinality " + cardinality);
            }
        }
    }

    @Override
    public final String computeXPathComponent() {
        final Identifier identifier = getIdentifier();
        if (identifier != null) {
            return getQtiClassName() + "[@identifier=\"" + identifier.toString() + "\"]";
        }
        return super.computeXPathComponent();
    }

    @Override
    public String toString() {
        return super.toString() + "(identifier=" + getIdentifier() + ")";
    }
}
