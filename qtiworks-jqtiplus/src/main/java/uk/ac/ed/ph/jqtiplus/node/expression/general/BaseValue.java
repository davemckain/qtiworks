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
package uk.ac.ed.ph.jqtiplus.node.expression.general;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.BaseTypeAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractSimpleFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The simplest expression returns a single value from the set defined by the given baseType.
 * <p>
 * Added optional attribute identifier.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class BaseValue extends AbstractSimpleFunctionalExpression {

    private static final long serialVersionUID = -8675475225499495315L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "baseValue";

    /** Name of baseType attribute in xml schema. */
    public static final String ATTR_BASE_TYPE_NAME = BaseType.QTI_CLASS_NAME;

    /** Single value of this baseValue. */
    private SingleValue singleValue;

    public BaseValue(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BaseTypeAttribute(this, ATTR_BASE_TYPE_NAME, true));
    }

    @Override
    public final String computeXPathComponent() {
        if (singleValue != null) {
            return getQtiClassName() + "[.=\"" + StringUtilities.escapeForXmlString(singleValue.toQtiString(), true) + "\"]";
        }
        return super.computeXPathComponent();
    }

    public BaseType getBaseTypeAttrValue() {
        return getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).getComputedValue();
    }

    public void setBaseTypeAttrValue(final BaseType baseType) {
        getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).setValue(baseType);
    }


    public SingleValue getSingleValue() {
        return singleValue;
    }

    public void setSingleValue(final SingleValue singleValue) {
        this.singleValue = singleValue;
    }

    @Override
    protected void loadChildren(final Element element, final LoadingContext context) {
        final BaseType baseType = getBaseTypeAttrValue();
        if (baseType!=null) {
            try {
                /*
                 * NB: The original JQTI always trimmed the element content.
                 * This behaviour is arguably not good for string values, so we now leave it to
                 * each BaseType instance to trim if appropriate.
                 */
                singleValue = baseType.parseSingleValueLax(element.getTextContent());
            }
            catch (final QtiParseException e) {
                context.modelBuildingError(e, element);
            }
        }
    }

    @Override
    protected void fireBodySaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        qtiSaxDocumentFirer.fireText(singleValue.toQtiString());
    }

    @Override
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        if (singleValue != null) {
            return new BaseType[] { singleValue.getBaseType() };
        }

        if (getBaseTypeAttrValue() != null) {
            return new BaseType[] { getBaseTypeAttrValue() };
        }

        return super.getProducedBaseTypes(context);
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        if (singleValue == null) {
            context.fireValidationError(this, "Value is not defined.");
        }

        if (singleValue != null && getBaseTypeAttrValue() != null && singleValue.getBaseType() != getBaseTypeAttrValue()) {
            context.fireValidationError(this, "BaseType of value does not match. Expected: " + getBaseTypeAttrValue()
                    + ", but found: "
                    + singleValue.getBaseType());
        }
    }

    @Override
    protected SingleValue evaluateValidSelf(final Value[] childValues) {
        return singleValue;
    }

    @Override
    public String toString() {
        return super.toString()
                + "(value=" + singleValue
                + ")";
    }
}
