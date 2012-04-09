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
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.TextFormatAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.exception2.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.ResponseData.ResponseDataType;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.TextFormat;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * An extended text interaction is a blockInteraction that allows the
 * candidate to enter an extended amount of text.
 * The extendedTextInteraction must be bound to a response variable with
 * baseType of string, integer, or float. When bound to response variable
 * with single cardinality a single string of text is required from the
 * candidate. When bound to a response variable with multiple or ordered
 * cardinality several separate text strings may be required, see maxStrings
 * below.
 * Attribute : maxStrings [0..1]: integer
 * The maxStrings attribute is required when the interaction is bound to
 * a response variable that is a container. A Delivery Engine must use the
 * value of this attribute to control the maximum number of separate strings
 * accepted from the candidate. When multiple strings are accepted,
 * expectedLength applies to each string.
 * Attribute : minStrings [0..1]: integer = 0
 * The minStrings attribute specifies the minimum number separate (non-empty)
 * strings required from the candidate to form a valid response. If minStrings
 * is 0 then the candidate is not required to enter any strings at all.
 * minStrings must be less than or equal to the limit imposed by maxStrings.
 * If the interaction is not bound to a container then there is a special case
 * in which minStrings may be 1. In this case the candidate must enter a non-empty
 * string to form a valid response. More complex constraints on the form of the
 * string can be controlled with the patternMask attribute.
 * Attribute : expectedLines [0..1]: integer
 * The expectedLines attribute provides a hint to the candidate as to the expected
 * number of lines of input required. A Delivery Engine should use the value of
 * this attribute to set the size of the response box, where applicable. This is
 * not a validity constraint.
 * Attribute : format [0..1]: textFormat = plain
 * Used to control the format of the text entered by the candidate. See textFormat
 * below. This attribute affects the way the value of the associated response
 * variable should be interpreted by response processing engines and also controls
 * the way it should be captured in the delivery engine.
 *
 * @author Jonathon Hare
 */
public class ExtendedTextInteraction extends BlockInteraction implements StringInteraction {

    private static final long serialVersionUID = 8382652026744422992L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "extendedTextInteraction";

    /** Name of maxStrings attribute in xml schema. */
    public static final String ATTR_MAX_STRINGS_NAME = "maxStrings";

    /** Name of minStrings attribute in xml schema. */
    public static final String ATTR_MIN_STRINGS_NAME = "minStrings";

    /** Name of minStrings attribute in xml schema. */
    public static final int ATTR_MIN_STRINGS_DEFAULT_VALUE = 0;

    /** Name of expectedLines attribute in xml schema. */
    public static final String ATTR_EXPECTED_LINES_NAME = "expectedLines";

    /** Name of format attribute in xml schema. */
    public static final String ATTR_FORMAT_NAME = "format";

    /** Default value of format attribute. */
    public static final TextFormat ATTR_FORMAT_DEFAULT_VALUE = TextFormat.PLAIN;

    public ExtendedTextInteraction(final XmlNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_STRINGS_NAME, false));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_STRINGS_NAME, ATTR_MIN_STRINGS_DEFAULT_VALUE, false));
        getAttributes().add(new IntegerAttribute(this, ATTR_EXPECTED_LINES_NAME, false));
        getAttributes().add(new TextFormatAttribute(this, ATTR_FORMAT_NAME, ATTR_FORMAT_DEFAULT_VALUE, false));

        //for StringInteraction...
        getAttributes().add(new IntegerAttribute(this, ATTR_BASE_NAME, ATTR_BASE_DEFAULT_VALUE, false));
        getAttributes().add(new IdentifierAttribute(this, ATTR_STRING_IDENTIFIER_NAME, false));
        getAttributes().add(new IntegerAttribute(this, ATTR_EXPECTED_LENGTH_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_PATTERN_MASK_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_PLACEHOLDER_TEXT_NAME, false));
    }

    /**
     * Sets new value of maxStrings attribute.
     *
     * @param maxStrings new value of maxStrings attribute
     * @see #getMaxStrings
     */
    public void setMaxStrings(final Integer maxStrings) {
        getAttributes().getIntegerAttribute(ATTR_MAX_STRINGS_NAME).setValue(maxStrings);
    }

    /**
     * Gets value of maxStrings attribute.
     *
     * @return value of maxStrings attribute
     * @see #setMaxStrings
     */
    public Integer getMaxStrings() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_STRINGS_NAME).getComputedValue();
    }

    /**
     * Sets new value of minStrings attribute.
     *
     * @param minStrings new value of minStrings attribute
     * @see #getMinStrings
     */
    public void setMinStrings(final Integer minStrings) {
        getAttributes().getIntegerAttribute(ATTR_MIN_STRINGS_NAME).setValue(minStrings);
    }

    /**
     * Gets value of minStrings attribute.
     *
     * @return value of minStrings attribute
     * @see #setMinStrings
     */
    public int getMinStrings() {
        return getAttributes().getIntegerAttribute(ATTR_MIN_STRINGS_NAME).getComputedNonNullValue();
    }

    /**
     * Sets new value of expectedLines attribute.
     *
     * @param expectedLines new value of expectedLines attribute
     * @see #getExpectedLines
     */
    public void setExpectedLines(final Integer expectedLines) {
        getAttributes().getIntegerAttribute(ATTR_EXPECTED_LINES_NAME).setValue(expectedLines);
    }

    /**
     * Gets value of expectedLines attribute.
     *
     * @return value of expectedLines attribute
     * @see #setExpectedLines
     */
    public Integer getExpectedLines() {
        return getAttributes().getIntegerAttribute(ATTR_EXPECTED_LINES_NAME).getComputedValue();
    }

    /**
     * Sets new value of format attribute.
     *
     * @param format new value of format attribute
     * @see #getFormat
     */
    public void setFormat(final TextFormat format) {
        getAttributes().getTextFormatAttribute(ATTR_FORMAT_NAME).setValue(format);
    }

    /**
     * Gets value of format attribute.
     *
     * @return value of format attribute
     * @see #setFormat
     */
    public TextFormat getFormat() {
        return getAttributes().getTextFormatAttribute(ATTR_FORMAT_NAME).getComputedValue();
    }

    @Override
    public int getBase() {
        return getAttributes().getIntegerAttribute(ATTR_BASE_NAME).getComputedNonNullValue();
    }

    @Override
    public Integer getExpectedLength() {
        return getAttributes().getIntegerAttribute(ATTR_EXPECTED_LENGTH_NAME).getComputedValue();
    }

    @Override
    public String getPatternMask() {
        return getAttributes().getStringAttribute(ATTR_PATTERN_MASK_NAME).getComputedValue();
    }

    @Override
    public String getPlaceholderText() {
        return getAttributes().getStringAttribute(ATTR_PLACEHOLDER_TEXT_NAME).getComputedValue();
    }

    @Override
    public Identifier getStringIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_STRING_IDENTIFIER_NAME).getComputedValue();
    }

    @Override
    public void setBase(final Integer base) {
        getAttributes().getIntegerAttribute(ATTR_BASE_NAME).setValue(base);
    }

    @Override
    public void setExpectedLength(final Integer expectedLength) {
        getAttributes().getIntegerAttribute(ATTR_EXPECTED_LENGTH_NAME).setValue(expectedLength);
    }

    @Override
    public void setPatternMask(final String patternMask) {
        getAttributes().getStringAttribute(ATTR_PATTERN_MASK_NAME).setValue(patternMask);
    }

    @Override
    public void setPlaceholderText(final String placeholderText) {
        getAttributes().getStringAttribute(ATTR_PLACEHOLDER_TEXT_NAME).setValue(placeholderText);
    }

    @Override
    public void setStringIdentifier(final Identifier stringIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_STRING_IDENTIFIER_NAME).setValue(stringIdentifier);
    }

    @Override
    public ResponseDeclaration getStringIdentifierResponseDeclaration() {
        if (getStringIdentifier() == null) {
            return null;
        }
        return getRootObject(AssessmentItem.class).getResponseDeclaration(getStringIdentifier());
    }

    @Override
    public void validate(final ValidationContext context) {
        super.validate(context);

        final Integer maxStrings = getMaxStrings();
        final int minStrings = getMinStrings();
        if (maxStrings != null) {
            if (maxStrings.intValue() < minStrings) {
                context.add(new ValidationError(this, "maxStrings cannot be smaller than minStrings"));
            }
        }

        if (getResponseIdentifier() != null) {
            final ResponseDeclaration declaration = getResponseDeclaration();

            if (minStrings > 1 || (maxStrings != null && maxStrings.intValue() > 1)) {
                if (declaration != null && declaration.getCardinality() != null && !declaration.getCardinality().isList()) {
                    if (declaration.getCardinality().isRecord()) {
                        context.add(new ValidationError(this,
                                "JQTI doesn't currently support binding multiple strings to a record container (the spec is very unclear here)"));
                    }
                    else {
                        context.add(new ValidationError(this, "Response variable must have multiple or ordered cardinality"));
                    }
                }
            }

            if (declaration != null && declaration.getCardinality() != null && !declaration.getCardinality().isRecord()) {
                if (declaration.getBaseType() != null && !(declaration.getBaseType().isString() || declaration.getBaseType().isNumeric())) {
                    context.add(new ValidationError(this, "Response variable must have string or numeric base type"));
                }
            }

            if (declaration != null && declaration.getBaseType() != null && !declaration.getBaseType().isFloat() && getBase() != 10) {
                context.add(new ValidationWarning(this, "JQTI currently doesn't support radix conversion for floats. Base attribute will be ignored."));
            }
        }

        if (getStringIdentifier() != null) {
            final ResponseDeclaration declaration = getStringIdentifierResponseDeclaration();
            if (declaration!=null) {
                if ((getMinStrings() > 1 || (getMaxStrings() != null && getMaxStrings() > 1))
                        && declaration.getCardinality() != null && !declaration.getCardinality().isList()) {
                    context.add(new ValidationError(this, "StringIdentifier response variable must have multiple or ordered cardinality"));
                }
                if (declaration.getBaseType() != null && !declaration.getBaseType().isString()) {
                    context.add(new ValidationError(this, "StringIdentifier response variable must have String base type"));
                }
            }

        }
    }


    @Override
    public void bindResponse(final ItemSessionController itemController, final ResponseData responseData) throws ResponseBindingException {
        super.bindResponse(itemController, responseData);

        /* Also handle stringIdentifier binding if required */
        if (getStringIdentifier() != null) {
            final Value value = parseResponse(getStringIdentifierResponseDeclaration(), responseData);
            itemController.getItemSessionState().setResponseValue(getStringIdentifierResponseDeclaration(), value);
        }
    }

    @Override
    protected Value parseResponse(final ResponseDeclaration responseDeclaration, final ResponseData responseData) throws ResponseBindingException {
        if (responseData.getType()!=ResponseDataType.STRING) {
            throw new ResponseBindingException("extendedTextInteraction must be bound to string response data");
        }
        final List<String> stringResponseData = ((StringResponseData) responseData).getResponseData();
        final Cardinality responseCardinality = responseDeclaration.getCardinality();
        final BaseType responseBaseType = responseDeclaration.getBaseType();
        final int base = getBase();

        //handle record special case
        Value result;
        if (responseCardinality.isRecord()) {
            String responseString;
            if (stringResponseData != null) {
                if (stringResponseData.size() > 1) {
                    throw new ResponseBindingException("Response to extendedTextEntryInteraction bound to a record variable should contain at most 1 element");
                }
                responseString = stringResponseData.get(0);
            }
            else {
                responseString = "";
            }
            result = TextEntryInteraction.parseRecordValueResponse(responseString, getBase());
        }
        else if (responseBaseType.isInteger()) {
            if (responseCardinality.isList()) {
                final List<IntegerValue> values = new ArrayList<IntegerValue>(stringResponseData.size());
                for (String stringResponseDatum : stringResponseData) {
                    values.add(new IntegerValue(stringResponseDatum, base));
                }

                if (responseCardinality == Cardinality.MULTIPLE) {
                    result = new MultipleValue(values);
                }
                else {
                    result = new OrderedValue(values);
                }
            }
            else {
                result = new IntegerValue(stringResponseData.get(0), base);
            }
        }
        else {
            result = super.parseResponse(responseDeclaration, responseData);
        }
        return result;
    }

    @Override
    public boolean validateResponse(final ItemSessionController itemController, final Value responseValue) {
        /* Gather up the values */
        final List<SingleValue> responseEntries = new ArrayList<SingleValue>();
        if (responseValue.isNull()) {
            /* (Empty response) */
        }
        else if (responseValue.getCardinality().isList()) {
            /* (Container response) */
            final ListValue listValue = (ListValue) responseValue;
            for (final SingleValue v : listValue) {
                responseEntries.add(v);
            }
        }
        else {
            /* (Single response) */
            responseEntries.add((SingleValue) responseValue);
        }

        /* Now do the validation */
        final Integer maxStrings = getMaxStrings();
        final int minStrings = getMinStrings();
        final String patternMask = getPatternMask();
        if (responseEntries.size() >= 0 && responseEntries.size() < minStrings) {
            return false;
        }
        if (maxStrings != null && responseEntries.size() > maxStrings.intValue()) {
            return false;
        }
        if (patternMask != null) {
            final Pattern pattern = Pattern.compile(patternMask);
            for (final SingleValue responseEntry : responseEntries) {
                if (!pattern.matcher(responseEntry.toQtiString()).matches()) {
                    return false;
                }
            }
        }

        return true;
    }

}
