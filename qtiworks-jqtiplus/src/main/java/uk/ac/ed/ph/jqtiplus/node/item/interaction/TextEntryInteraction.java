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
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.exception.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.ResponseData.ResponseDataType;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A textEntry interaction is an inlineInteraction that obtains A
 * simple piece of text from the candidate. Like inlineChoiceInteraction,
 * the delivery engine must allow the candidate to review their choice
 * within the context of the surrounding text.
 * The textEntryInteraction must be bound to A response variable with single
 * cardinality only. The baseType must be one of string, integer, or float.
 * Note: Spec is slightly wrong: record response is also allowed from inherited
 * StringInteraction
 *
 * @author Jonathon Hare
 */
public final class TextEntryInteraction extends InlineInteraction implements StringInteraction {

    private static final long serialVersionUID = 1113644056576463196L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "textEntryInteraction";

    public TextEntryInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        //for StringInteraction...
        getAttributes().add(new IntegerAttribute(this, ATTR_BASE_NAME, ATTR_BASE_DEFAULT_VALUE, false));
        getAttributes().add(new IdentifierAttribute(this, ATTR_STRING_IDENTIFIER_NAME, false));
        getAttributes().add(new IntegerAttribute(this, ATTR_EXPECTED_LENGTH_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_PATTERN_MASK_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_PLACEHOLDER_TEXT_NAME, false));
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
        if (responseDeclaration!=null) {
            if (!(responseDeclaration.getCardinality().isSingle() || responseDeclaration.getCardinality().isRecord())) {
                context.fireValidationError(this, "Response variable must have single or record cardinality");
            }
            if (!responseDeclaration.getCardinality().isRecord()) {
                if (!(responseDeclaration.getBaseType().isString() || responseDeclaration.getBaseType().isNumeric())) {
                    context.fireValidationError(this, "Response variable must have string or numeric base type");
                }
            }
            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isFloat() && getBase() != 10) {
                context.fireValidationWarning(this, "JQTI currently doesn't support radix conversion for floats. Base attribute will be ignored.");
            }
        }

        if (getStringIdentifier() != null) {
            final ResponseDeclaration declaration = getStringIdentifierResponseDeclaration();
            if (declaration != null && declaration.getBaseType() != null && !declaration.getBaseType().isString()) {
                context.fireValidationError(this, "StringIdentifier response variable must have String base type");
            }
        }
    }

    @Override
    public final void bindResponse(final InteractionBindingContext interactionBindingContext, final ResponseData responseData) throws ResponseBindingException {
        super.bindResponse(interactionBindingContext, responseData);

        /* Also handle stringIdentifier binding if required */
        if (getStringIdentifier() != null) {
            final ResponseDeclaration stringIdentifierResponseDeclaration = getStringIdentifierResponseDeclaration();
            final Value value = parseResponse(stringIdentifierResponseDeclaration, responseData);
            interactionBindingContext.bindResponseVariable(stringIdentifierResponseDeclaration.getIdentifier(), value);
        }
    }

    @Override
    protected Value parseResponse(final ResponseDeclaration responseDeclaration, final ResponseData responseData) throws ResponseBindingException {
        if (responseData.getType()!=ResponseDataType.STRING) {
            throw new ResponseBindingException(responseDeclaration, responseData, "textInteraction must be bound to string response data");
        }
        final List<String> stringResponseData = ((StringResponseData) responseData).getResponseData();
        if (stringResponseData.size() > 1) {
            throw new ResponseBindingException(responseDeclaration, responseData, "Response to textEntryInteraction should contain at most 1 element");
        }

        final Cardinality responseCardinality = responseDeclaration.getCardinality();
        final BaseType responseBaseType = responseDeclaration.getBaseType();
        final String responseString = !stringResponseData.isEmpty() ? stringResponseData.get(0) : null;
        final int base = getBase();

        Value result;
        try {
            if (responseCardinality.isRecord()) {
                result = TextEntryInteraction.parseRecordValueResponse(responseString, base);
            }
            else if (responseBaseType.isInteger()) {
                if (responseString == null || responseString.trim().length() == 0) {
                    result = NullValue.INSTANCE;
                }
                else {
                    result = IntegerValue.parseString(responseString, base);
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
        if (getPatternMask() != null) {
            if (!responseValue.toQtiString().matches(getPatternMask())) {
                return false;
            }
        }

        return true;
    }

    /**
     * @throws QtiParseException
     */
    protected static Value parseRecordValueResponse(final String responseString, final int base) {
        final Map<Identifier, SingleValue> recordBuilder = new HashMap<Identifier, SingleValue>();

        recordBuilder.put(KEY_STRING_VALUE_NAME, BaseType.STRING.parseSingleValue(responseString));
        recordBuilder.put(KEY_FLOAT_VALUE_NAME, BaseType.FLOAT.parseSingleValue(responseString));

        String exponentIndicator = null;
        if (responseString.contains("e")) {
            exponentIndicator = "e";
        }
        if (responseString.contains("E")) {
            exponentIndicator = "E";
        }

        final String exponentPart = exponentIndicator != null ? responseString.substring(responseString.indexOf(exponentIndicator) + 1) : null;
        final String responseStringAfterExp = exponentIndicator == null ? responseString : responseString.substring(0, responseString.indexOf(exponentIndicator));
        final String rightPart = responseStringAfterExp.contains(".") ? responseStringAfterExp.substring(responseStringAfterExp.indexOf(".") + 1) : null;
        final String leftPart = responseStringAfterExp.contains(".") ? responseStringAfterExp.substring(0, responseStringAfterExp.indexOf(".")) : responseStringAfterExp;

        if (exponentIndicator == null && !responseStringAfterExp.contains(".")) {
            recordBuilder.put(KEY_INTEGER_VALUE_NAME, IntegerValue.parseString(responseStringAfterExp, base));
        }

        recordBuilder.put(KEY_LEFT_DIGITS_NAME, new IntegerValue(leftPart == null ? 0 : leftPart.length()));
        recordBuilder.put(KEY_RIGHT_DIGITS_NAME, new IntegerValue(rightPart == null ? 0 : rightPart.length()));

        if (exponentIndicator != null) {
            int frac = rightPart == null || rightPart.length() == 0 ? 0 : rightPart.length();
            if (exponentPart != null && exponentPart.length() > 0) {
                frac -= Integer.parseInt(exponentPart);
            }

            recordBuilder.put(KEY_NDP_NAME, new IntegerValue(frac));
        }
        else {
            recordBuilder.put(KEY_NDP_NAME, IntegerValue.parseString(rightPart == null || rightPart.length() == 0 ? "0" : rightPart));
        }

        int nsf = leftPart == null || leftPart.length() == 0 ? 0 : new Integer(leftPart).toString().length();
        nsf += rightPart == null || rightPart.length() == 0 ? 0 : rightPart.length();
        recordBuilder.put(KEY_NSF_NAME, new IntegerValue(nsf));

        if (exponentIndicator != null) {
            recordBuilder.put(KEY_EXPONENT_NAME, IntegerValue.parseString(exponentPart!=null && exponentPart.length() == 0 ? "0" : exponentPart));
        }
        return RecordValue.createRecordValue(recordBuilder);
    }
}
