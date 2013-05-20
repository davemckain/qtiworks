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
package uk.ac.ed.ph.jqtiplus.node.result;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.BaseTypeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.CardinalityAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Signature;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * Variable is value (of any cardinality and/or baseType) with identifier.
 *
 * @author Jiri Kajaba
 */
public abstract class ItemVariable extends AbstractNode {

    private static final long serialVersionUID = -7574012966913693854L;

    /** Display name of this class. */
    public static final String DISPLAY_NAME = "itemVariable";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    /** Name of cardinality attribute in xml schema. */
    public static final String ATTR_CARDINALITY_NAME = Cardinality.QTI_CLASS_NAME;

    /** Name of baseType attribute in xml schema. */
    public static final String ATTR_BASE_TYPE_NAME = BaseType.QTI_CLASS_NAME;

    public ItemVariable(final AbstractResult parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, true));
        getAttributes().add(new CardinalityAttribute(this, ATTR_CARDINALITY_NAME));
        getAttributes().add(new BaseTypeAttribute(this, ATTR_BASE_TYPE_NAME, false));
    }

    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    public void setIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
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

    /**
     * Returns the {@link VariableType} of this item variable.
     */
    public abstract VariableType getVariableType();

    /**
     * Computes the {@link Signature} of this declaration from its {@link Cardinality}
     * and {@link BaseType}, returning null if this cannot be determined.
     */
    public Signature computeSignature() {
        final Cardinality cardinality = getCardinality();
        final BaseType baseType = getBaseType();
        return Signature.getSignature(cardinality, baseType);
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

    public abstract Value getComputedValue();

    @Override
    public final String computeXPathComponent() {
        final Identifier identifier = getIdentifier();
        if (identifier != null) {
            return getQtiClassName() + "[@identifier=\"" + identifier + "\"]";
        }
        return super.computeXPathComponent();
    }
}
