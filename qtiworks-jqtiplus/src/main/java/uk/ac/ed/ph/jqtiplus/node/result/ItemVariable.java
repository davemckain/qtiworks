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

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.BaseTypeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.CardinalityAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.VariableReferenceIdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;

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

    /** Default value of baseType attribute. */
    public static final BaseType ATTR_BASE_TYPE_DEFAULT_VALUE = null;

    public ItemVariable(AbstractResult parent, String localName) {
        super(parent, localName);

        getAttributes().add(new VariableReferenceIdentifierAttribute(this, ATTR_IDENTIFIER_NAME));
        getAttributes().add(new CardinalityAttribute(this, ATTR_CARDINALITY_NAME));
        getAttributes().add(new BaseTypeAttribute(this, ATTR_BASE_TYPE_NAME, ATTR_BASE_TYPE_DEFAULT_VALUE));
    }

    /**
     * Gets value of identifier attribute.
     * 
     * @return value of identifier attribute
     * @see #setIdentifier
     */
    public VariableReferenceIdentifier getIdentifier() {
        return getAttributes().getVariableReferenceIdentifierAttribute(ATTR_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of identifier attribute.
     * 
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    public void setIdentifier(VariableReferenceIdentifier identifier) {
        getAttributes().getVariableReferenceIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
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

    @Override
    public final String computeXPathComponent() {
        final VariableReferenceIdentifier identifier = getIdentifier();
        if (identifier != null) {
            return getClassTag() + "[@identifier=\"" + identifier + "\"]";
        }
        return super.computeXPathComponent();
    }
}
