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
import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.exception2.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.ResponseData.ResponseDataType;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
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
public final class ExtendedTextInteraction extends BlockInteraction implements StringInteraction {

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

    public ExtendedTextInteraction(final QtiNode parent) {
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

    public Integer getMaxStrings() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_STRINGS_NAME).getComputedValue();
    }

    public void setMaxStrings(final Integer maxStrings) {
        getAttributes().getIntegerAttribute(ATTR_MAX_STRINGS_NAME).setValue(maxStrings);
    }

    public int getMinStrings() {
        return getAttributes().getIntegerAttribute(ATTR_MIN_STRINGS_NAME).getComputedNonNullValue();
    }

    public void setMinStrings(final Integer minStrings) {
        getAttributes().getIntegerAttribute(ATTR_MIN_STRINGS_NAME).setValue(minStrings);
    }

    public Integer getExpectedLines() {
        return getAttributes().getIntegerAttribute(ATTR_EXPECTED_LINES_NAME).getComputedValue();
    }

    public void setExpectedLines(final Integer expectedLines) {
        getAttributes().getIntegerAttribute(ATTR_EXPECTED_LINES_NAME).setValue(expectedLines);
    }


    public TextFormat getFormat() {
        return getAttributes().getTextFormatAttribute(ATTR_FORMAT_NAME).getComputedValue();
    }

    public void setFormat(final TextFormat format) {
        getAttributes().getTextFormatAttribute(ATTR_FORMAT_NAME).setValue(format);
    }


    @Override
    public int getBase() {
        return getAttributes().getIntegerAttribute(ATTR_BASE_NAME).getComputedNonNullValue();
    }

    @Override
    public void setBase(final Integer base) {
        getAttributes().getIntegerAttribute(ATTR_BASE_NAME).setValue(base);
    }

    @Override
    public Integer getExpectedLength() {
        return getAttributes().getIntegerAttribute(ATTR_EXPECTED_LENGTH_NAME).getComputedValue();
    }

    @Override
    public void setExpectedLength(final Integer expectedLength) {
        getAttributes().getIntegerAttribute(ATTR_EXPECTED_LENGTH_NAME).setValue(expectedLength);
    }

    @Override
    public String getPatternMask() {
        return getAttributes().getStringAttribute(ATTR_PATTERN_MASK_NAME).getComputedValue();
    }

    @Override
    public void setPatternMask(final String patternMask) {
        getAttributes().getStringAttribute(ATTR_PATTERN_MASK_NAME).setValue(patternMask);
    }

    @Override
    public String getPlaceholderText() {
        return getAttributes().getStringAttribute(ATTR_PLACEHOLDER_TEXT_NAME).getComputedValue();
    }

    @Override
    public void setPlaceholderText(final String placeholderText) {
        getAttributes().getStringAttribute(ATTR_PLACEHOLDER_TEXT_NAME).setValue(placeholderText);
    }

    @Override
    public Identifier getStringIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_STRING_IDENTIFIER_NAME).getComputedValue();
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
        return getRootNode(AssessmentItem.class).getResponseDeclaration(getStringIdentifier());
    }

    @Override
    protected void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        final Integer maxStrings = getMaxStrings();
        final int minStrings = getMinStrings();
        if (maxStrings != null) {
            if (maxStrings.intValue() < minStrings) {
                context.fireValidationError(this, "maxStrings cannot be smaller than minStrings");
            }
        }

        if (responseDeclaration != null) {
            if (minStrings > 1 || (maxStrings != null && maxStrings.intValue() > 1)) {
                if (!responseDeclaration.getCardinality().isList()) {
                    if (responseDeclaration.getCardinality().isRecord()) {
                        context.fireValidationError(this,
                                "JQTI doesn't currently support binding multiple strings to a record container (the spec is very unclear here)");
                    }
                    else {
                        context.fireValidationError(this, "Response variable must have multiple or ordered cardinality");
                    }
                }
            }

            if (!responseDeclaration.getCardinality().isRecord()) {
                if (responseDeclaration.getBaseType() != null && !(responseDeclaration.getBaseType().isString() || responseDeclaration.getBaseType().isNumeric())) {
                    context.fireValidationError(this, "Response variable must have string or numeric base type");
                }
            }

            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isFloat() && getBase() != 10) {
                context.fireValidationWarning(this, "JQTI currently doesn't support radix conversion for floats. Base attribute will be ignored.");
            }
        }

        final Identifier stringIdentifier = getStringIdentifier();
        if (stringIdentifier != null) {
            final VariableDeclaration stringDeclaration = context.checkVariableReference(this, stringIdentifier);
            if (stringDeclaration!=null) {
                context.checkVariableType(this, stringDeclaration, VariableType.RESPONSE);
                if ((getMinStrings() > 1 || (getMaxStrings() != null && getMaxStrings() > 1))
                        && stringDeclaration.getCardinality() != null && !stringDeclaration.getCardinality().isList()) {
                    context.fireValidationError(this, "StringIdentifier response variable must have multiple or ordered cardinality");
                }
                if (stringDeclaration.getBaseType() != null && !stringDeclaration.getBaseType().isString()) {
                    context.fireValidationError(this, "StringIdentifier response variable must have String base type");
                }
            }
        }
    }


    @Override
    public void bindResponse(final ItemSessionController itemSessionController, final ResponseData responseData) throws ResponseBindingException {
        super.bindResponse(itemSessionController, responseData);

        /* Also handle stringIdentifier binding if required */
        if (getStringIdentifier() != null) {
            final Value value = parseResponse(getStringIdentifierResponseDeclaration(), responseData);
            itemSessionController.getItemSessionState().setResponseValue(getStringIdentifierResponseDeclaration(), value);
        }
    }

    @Override
    protected Value parseResponse(final ResponseDeclaration responseDeclaration, final ResponseData responseData) throws ResponseBindingException {
        if (responseData.getType()!=ResponseDataType.STRING) {
            throw new ResponseBindingException(responseDeclaration, responseData, "extendedTextInteraction must be bound to string response data");
        }
        final List<String> stringResponseData = ((StringResponseData) responseData).getResponseData();
        final Cardinality responseCardinality = responseDeclaration.getCardinality();
        final BaseType responseBaseType = responseDeclaration.getBaseType();
        final int base = getBase();

        /* Handle record special case */
        Value result;
        try {
            if (responseCardinality.isRecord()) {
                String responseString;
                if (stringResponseData != null) {
                    if (stringResponseData.size() > 1) {
                        throw new ResponseBindingException(responseDeclaration, responseData, "Response to extendedTextEntryInteraction bound to a record variable should contain at most 1 element");
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
                    for (final String stringResponseDatum : stringResponseData) {
                        values.add(IntegerValue.parseString(stringResponseDatum, base));
                    }

                    if (responseCardinality == Cardinality.MULTIPLE) {
                        result = MultipleValue.createMultipleValue(values);
                    }
                    else {
                        result = OrderedValue.createOrderedValue(values);
                    }
                }
                else {
                    result = IntegerValue.parseString(stringResponseData.get(0), base);
                }
            }
            else {
                result = super.parseResponse(responseDeclaration, responseData);
            }
        }
        catch (final QtiParseException e) {
            throw new ResponseBindingException(responseDeclaration, responseData, e);
        }

        return result;
    }

    @Override
    public boolean validateResponse(final ItemSessionController itemSessionController, final Value responseValue) {
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
