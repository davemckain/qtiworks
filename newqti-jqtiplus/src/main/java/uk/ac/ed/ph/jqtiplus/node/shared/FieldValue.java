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
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * A class that can represent A single value of any baseType in variable declarations and result reports.
 * The base-type is defined by the baseType attribute of the declaration except in the case of variables
 * with record cardinality.
 * <p>
 * This class has different name (fieldValue instead of value) in specification. Name value was already taken.
 * 
 * @author Jiri Kajaba
 */
public class FieldValue extends AbstractNode {

    private static final long serialVersionUID = -3645062478164419548L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "value";

    /** Name of fieldIdentifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "fieldIdentifier";

    /** Name of baseType attribute in xml schema. */
    public static final String ATTR_BASE_TYPE_NAME = BaseType.CLASS_TAG;

    /** Default value of baseType attribute. */
    public static final BaseType ATTR_BASE_TYPE_DEFAULT_VALUE = null;

    /** Single value of this fieldValue. */
    private SingleValue singleValue;

    /**
     * Creates object.
     * 
     * @param parent parent of this object
     */
    public FieldValue(FieldValueParent parent) {
        super(parent);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, null));
        getAttributes().add(new BaseTypeAttribute(this, ATTR_BASE_TYPE_NAME, ATTR_BASE_TYPE_DEFAULT_VALUE));
    }

    @Override
    public FieldValueParent getParent() {
        return (FieldValueParent) super.getParent();
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public final String computeXPathComponent() {
        if (singleValue != null) {
            return getClassTag() + "[.='" + singleValue + "']";
        }
        return super.computeXPathComponent();
    }

    /**
     * Gets value of fieldIdentifier attribute.
     * 
     * @return value of fieldIdentifier attribute
     * @see #setIdentifier
     */
    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of fieldIdentifier attribute.
     * 
     * @param identifier new value of fieldIdentifier attribute
     * @see #getIdentifier
     */
    public void setIdentifier(Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
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
     * @param baseType new value of baseType attribute
     * @see #getBaseTypeAttrValue
     */
    public void setBaseTypeAttrValue(BaseType baseType) {
        getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).setValue(baseType);
    }

    /**
     * Gets baseType of this fieldValue.
     * <ol>
     * <li>if cardinality of parent variableDeclaration is not record, uses parent's baseType</li>
     * <li>if cardinality of parent variableDeclaration is record, uses its own baseType</li>
     * </ol>
     * 
     * @return baseType of this fieldValue
     */
    public BaseType getBaseType() {
        if (getParent().getCardinality() == null) {
            return null;
        }
        else if (!getParent().getCardinality().isRecord()) {
            return getParent().getBaseType();
        }
        else {
            return getBaseTypeAttrValue();
        }
    }

    /**
     * Gets single value of this fieldValue.
     * 
     * @return single value of this fieldValue
     * @see #setSingleValue
     */
    public SingleValue getSingleValue() {
        return singleValue;
    }

    /**
     * Sets new single value of this fieldValue.
     * 
     * @param singleValue new single value of this fieldValue
     * @see #getSingleValue
     */
    public void setSingleValue(SingleValue singleValue) {
        this.singleValue = singleValue;
    }

    @Override
    protected void readChildren(Element element, LoadingContext context) {
        if (getBaseType() != null && element.getTextContent().length() != 0) {
            try {
                singleValue = getBaseType().parseSingleValue(element.getTextContent());
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
    protected void validateAttributes(ValidationContext context, AbstractValidationResult result) {
        super.validateAttributes(context, result);

        final Cardinality cardinality = getParent().getCardinality();
        if (cardinality != null) {
            if (cardinality.isRecord() && getIdentifier() == null) {
                result.add(new ValidationError(this, "Attribute (" + ATTR_IDENTIFIER_NAME + ") is not defined."));
            }

            if (cardinality.isRecord() && getBaseTypeAttrValue() == null) {
                result.add(new ValidationError(this, "Attribute (" + ATTR_BASE_TYPE_NAME + ") is not defined."));
            }

            if (!cardinality.isRecord() && getIdentifier() != null) {
                result.add(new ValidationWarning(this, "Attribute (" + ATTR_IDENTIFIER_NAME + ") should not be defined."));
            }

            if (!cardinality.isRecord() && getBaseTypeAttrValue() != null) {
                result.add(new ValidationWarning(this, "Attribute (" + ATTR_BASE_TYPE_NAME + ") should not be defined."));
            }
        }
    }

    @Override
    protected void validateChildren(ValidationContext context, AbstractValidationResult result) {
        super.validateChildren(context, result);

        if (singleValue == null) {
            result.add(new ValidationError(this, "Value is not defined."));
        }

        if (singleValue != null && getBaseType() != null && singleValue.getBaseType() != getBaseType()) {
            result.add(new ValidationError(this, "BaseType of value does not match. Expected: " + getBaseType() + ", but found: " + singleValue.getBaseType()));
        }
    }

    /**
     * Constructs value (of any cardinality) from given list of fieldValues (list of single values).
     * If list of fieldValues is empty returns NullValue.
     * 
     * @param cardinality requested cardinality
     * @param values given list of fieldValues (list of single values)
     * @return value (of any cardinality) from given list of fieldValues (list of single values)
     * @see #getValues
     */
    public static Value getValue(Cardinality cardinality, List<FieldValue> values) {
        if (values.size() == 0) {
            return NullValue.INSTANCE;
        }

        switch (cardinality) {
            case SINGLE: {
                return values.get(0).getSingleValue();
            }
            case MULTIPLE: {
                final MultipleValue value = new MultipleValue();

                for (final FieldValue fieldValue : values) {
                    value.add(fieldValue.getSingleValue());
                }

                return value;
            }
            case ORDERED: {
                final OrderedValue value = new OrderedValue();

                for (final FieldValue fieldValue : values) {
                    value.add(fieldValue.getSingleValue());
                }

                return value;
            }
            case RECORD: {
                final RecordValue value = new RecordValue();

                for (final FieldValue fieldValue : values) {
                    value.add(fieldValue.getIdentifier(), fieldValue.getSingleValue());
                }

                return value;
            }
            default:
                throw new AssertionError("Unsupported " + Cardinality.CLASS_TAG + ": " + cardinality);
        }
    }

    /**
     * Constructs list of fieldValues (list of single values) from given value (of any cardinality).
     * If given value is null (java) or NULL (qti) returns empty list.
     * 
     * @param parent parent of constructed fieldValues
     * @param value given value (of any cardinality)
     * @return list of fieldValues (list of single values) from given value (of any cardinality)
     * @see #getValue
     */
    public static List<FieldValue> getValues(FieldValueParent parent, Value value) {
        final List<FieldValue> values = new ArrayList<FieldValue>();

        if (value == null || value.isNull()) {
            return values;
        }

        switch (value.getCardinality()) {
            case SINGLE: {
                final FieldValue fieldValue = new FieldValue(parent);
                fieldValue.setSingleValue((SingleValue) value);

                values.add(fieldValue);

                break;
            }
            case MULTIPLE:
            case ORDERED: {
                final ListValue list = (ListValue) value;
                for (int i = 0; i < list.size(); i++) {
                    final FieldValue fieldValue = new FieldValue(parent);
                    fieldValue.setSingleValue(list.get(i));

                    values.add(fieldValue);
                }

                break;
            }
            case RECORD: {
                final RecordValue record = (RecordValue) value;
                for (final Identifier identifier : record.keySet()) {
                    final SingleValue singleValue = record.get(identifier);

                    final FieldValue fieldValue = new FieldValue(parent);
                    fieldValue.setIdentifier(identifier);
                    fieldValue.setBaseTypeAttrValue(singleValue.getBaseType());
                    fieldValue.setSingleValue(singleValue);

                    values.add(fieldValue);
                }

                break;
            }
            default:
                throw new AssertionError("Unsupported " + Cardinality.CLASS_TAG + ": " + value.getCardinality());
        }

        return values;
    }
}
