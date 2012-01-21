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
package uk.ac.ed.ph.jqtiplus.node.expression.general;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.BaseTypeAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;

import org.w3c.dom.Element;

/**
 * The simplest expression returns a single value from the set defined by the given baseType.
 * <p>
 * Added optional attribute identifier.
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public class BaseValue extends AbstractExpression {

    private static final long serialVersionUID = -8675475225499495315L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "baseValue";

    /** Name of baseType attribute in xml schema. */
    public static final String ATTR_BASE_TYPE_NAME = BaseType.CLASS_TAG;

    /** Single value of this baseValue. */
    private SingleValue singleValue;

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public BaseValue(ExpressionParent parent) {
        super(parent);

        getAttributes().add(new BaseTypeAttribute(this, ATTR_BASE_TYPE_NAME));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public final String computeXPathComponent() {
        if (singleValue != null) {
            return getClassTag() + "[.=\"" + escapeForXmlString(singleValue.toString(), true) + "\"]";
        }
        return super.computeXPathComponent();
    }

    /**
     * Gets value of baseType attribute.
     * 
     * @return value of baseType attribute
     * @see #setBaseTypeAttrValue
     */
    public BaseType getBaseTypeAttrValue() {
        return getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).getValue();
    }

    /**
     * Sets new value of baseType attribute.
     * 
     * @param baseType new value of baseType attribute.
     * @see #getBaseTypeAttrValue
     */
    public void setBaseTypeAttrValue(BaseType baseType) {
        getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).setValue(baseType);
    }

    /**
     * Gets single value of this baseValue.
     * 
     * @return single value of this baseValue
     * @see #setSingleValue
     */
    public SingleValue getSingleValue() {
        return singleValue;
    }

    /**
     * Sets new single value of this baseValue.
     * 
     * @param singleValue new single value of this baseValue
     * @see #getSingleValue
     */
    public void setSingleValue(SingleValue singleValue) {
        this.singleValue = singleValue;
    }

    @Override
    protected void readChildren(Element element, LoadingContext context) {
        if (getBaseTypeAttrValue() != null && element.getTextContent().length() != 0) {
            try {
                singleValue = getBaseTypeAttrValue().parseSingleValue(element.getTextContent());
            }
            catch (final QTIParseException e) {
                context.modelBuildingError(e, element);
            }
        }
    }

    @Override
    protected String bodyToXmlString(int depth, boolean printDefaultAttributes) {
        return singleValue != null ? escapeForXmlString(singleValue.toString(), false) : "";
    }

    @Override
    public BaseType[] getProducedBaseTypes(ValidationContext context) {
        if (singleValue != null) {
            return new BaseType[] { singleValue.getBaseType() };
        }

        if (getBaseTypeAttrValue() != null) {
            return new BaseType[] { getBaseTypeAttrValue() };
        }

        return super.getProducedBaseTypes(context);
    }

    @Override
    protected void validateChildren(ValidationContext context) {
        super.validateChildren(context);

        if (singleValue == null) {
            context.add(new ValidationError(this, "Value is not defined."));
        }

        if (singleValue != null && getBaseTypeAttrValue() != null && singleValue.getBaseType() != getBaseTypeAttrValue()) {
            context.add(new ValidationError(this, "BaseType of value does not match. Expected: " + getBaseTypeAttrValue()
                    + ", but found: "
                    + singleValue.getBaseType()));
        }
    }

    @Override
    protected SingleValue evaluateSelf(ProcessingContext context, int depth) {
        return singleValue;
    }

    @Override
    public String toString() {
        return singleValue != null ? singleValue.toString() : "<NONE>";
    }
}
