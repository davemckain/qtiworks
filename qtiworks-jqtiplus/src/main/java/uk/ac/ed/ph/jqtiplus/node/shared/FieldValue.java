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

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.BaseTypeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * A class that can represent a single value of any baseType in variable declarations and result reports.
 * The base-type is defined by the baseType attribute of the declaration except in the case of variables
 * with record cardinality.
 * <p>
 * This class has different name (fieldValue instead of value) in specification. Name value was already taken.
 *
 * @author Jiri Kajaba
 */
public final class FieldValue extends AbstractNode {

    private static final long serialVersionUID = -3645062478164419548L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "value";

    /** Name of fieldIdentifier attribute in xml schema. */
    public static final String ATTR_FIELD_IDENTIFIER_NAME = "fieldIdentifier";

    /** Name of baseType attribute in xml schema. */
    public static final String ATTR_BASE_TYPE_NAME = BaseType.QTI_CLASS_NAME;

    /** Single value of this fieldValue. */
    private SingleValue singleValue;

    public FieldValue(final FieldValueParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IdentifierAttribute(this, ATTR_FIELD_IDENTIFIER_NAME, false));
        getAttributes().add(new BaseTypeAttribute(this, ATTR_BASE_TYPE_NAME, false));
    }

    public FieldValue(final FieldValueParent parent, final SingleValue singleValue) {
        this(parent);
        setSingleValue(singleValue);
    }

    @Override
    public FieldValueParent getParent() {
        return (FieldValueParent) super.getParent();
    }

    @Override
    public final String computeXPathComponent() {
        if (singleValue != null) {
            return getQtiClassName() + "[.='" + singleValue + "']";
        }
        return super.computeXPathComponent();
    }


    public Identifier getFieldIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_FIELD_IDENTIFIER_NAME).getComputedValue();
    }

    public void setFieldIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_FIELD_IDENTIFIER_NAME).setValue(identifier);
    }


    public BaseType getBaseTypeAttrValue() {
        return getAttributes().getBaseTypeAttribute(ATTR_BASE_TYPE_NAME).getComputedValue();
    }

    public void setBaseTypeAttrValue(final BaseType baseType) {
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
    public void setSingleValue(final SingleValue singleValue) {
        this.singleValue = singleValue;
    }

    @Override
    protected void loadChildren(final Element element, final LoadingContext context) {
        final BaseType baseType = getBaseType();
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
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Cardinality cardinality = getParent().getCardinality();
        if (cardinality != null) {
            if (cardinality.isRecord() && getFieldIdentifier() == null) {
                context.fireValidationError(this, "Attribute (" + ATTR_FIELD_IDENTIFIER_NAME + ") is not defined.");
            }

            if (cardinality.isRecord() && getBaseTypeAttrValue() == null) {
                context.fireValidationError(this, "Attribute (" + ATTR_BASE_TYPE_NAME + ") is not defined.");
            }

            if (!cardinality.isRecord() && getFieldIdentifier() != null) {
                context.fireValidationWarning(this, "Attribute (" + ATTR_FIELD_IDENTIFIER_NAME + ") should not be defined.");
            }

            if (!cardinality.isRecord() && getBaseTypeAttrValue() != null) {
                context.fireValidationWarning(this, "Attribute (" + ATTR_BASE_TYPE_NAME + ") should not be defined.");
            }
        }

        if (singleValue==null) {
            context.fireValidationError(this, "Value is not defined.");
        }
        else if (getBaseType() != null && singleValue.getBaseType() != getBaseType()) {
            context.fireValidationError(this, "BaseType of value does not match. Expected: " + getBaseType() + ", but found: " + singleValue.getBaseType());
        }
    }

    /**
     * Constructs value (of any cardinality) from given list of fieldValues (list of single values).
     * If list of fieldValues is empty returns NullValue.
     *
     * @param cardinality requested cardinality
     * @param values given list of fieldValues (list of single values)
     * @return value (of any cardinality) from given list of fieldValues (list of single values)
     */
    public static Value computeValue(final Cardinality cardinality, final List<FieldValue> values) {
        if (values.isEmpty()) {
            return NullValue.INSTANCE;
        }

        switch (cardinality) {
            case SINGLE: {
                return values.get(0).getSingleValue();
            }
            case MULTIPLE: {
                final List<SingleValue> singleValues = new ArrayList<SingleValue>();
                for (final FieldValue fieldValue : values) {
                    singleValues.add(fieldValue.getSingleValue());
                }
                return MultipleValue.createMultipleValue(singleValues);
            }
            case ORDERED: {
                final List<SingleValue> singleValues = new ArrayList<SingleValue>();
                for (final FieldValue fieldValue : values) {
                    singleValues.add(fieldValue.getSingleValue());
                }
                return OrderedValue.createOrderedValue(singleValues);
            }
            case RECORD: {
                final Map<Identifier, SingleValue> recordBuilder = new HashMap<Identifier, SingleValue>();
                for (final FieldValue fieldValue : values) {
                    recordBuilder.put(fieldValue.getFieldIdentifier(), fieldValue.getSingleValue());
                }
                return RecordValue.createRecordValue(recordBuilder);
            }
            default:
                throw new QtiLogicException("Unsupported " + Cardinality.QTI_CLASS_NAME + ": " + cardinality);
        }
    }

    /**
     * Constructs list of fieldValues (list of single values) from given value (of any cardinality).
     * If given value is null (java) or NULL (qti) returns empty list.
     *
     * @param parent parent of constructed fieldValues
     * @param value given value (of any cardinality)
     * @return list of fieldValues (list of single values) from given value (of any cardinality)
     */
    public static List<FieldValue> computeValues(final FieldValueParent parent, final Value value) {
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
                    fieldValue.setFieldIdentifier(identifier);
                    fieldValue.setBaseTypeAttrValue(singleValue.getBaseType());
                    fieldValue.setSingleValue(singleValue);

                    values.add(fieldValue);
                }

                break;
            }
            default:
                throw new QtiLogicException("Unsupported " + Cardinality.QTI_CLASS_NAME + ": " + value.getCardinality());
        }

        return values;
    }
}
