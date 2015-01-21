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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for {@link StringInteraction}. (Ideally {@link StringInteraction} would be an
 * abstract class, but {@link TextEntryInteraction} is an inline interaction whereas
 * {@link ExtendedTextInteraction} is a block interaction, so that doesn't work!)
 *
 * @author David McKain
 */
public final class StringInteractionHelper {

    /**
     * Helper method to parse responses bound to record cardinality variables.
     *
     * @param responseString raw response String, which must not be null
     * @param base to use
     *
     * @throws QtiParseException
     */
    public static Value parseRecordValueResponse(final String responseString, final int base) {
        final Map<Identifier, SingleValue> recordBuilder = new HashMap<Identifier, SingleValue>();

        recordBuilder.put(StringInteraction.KEY_STRING_VALUE_NAME, BaseType.STRING.parseSingleValue(responseString));
        recordBuilder.put(StringInteraction.KEY_FLOAT_VALUE_NAME, BaseType.FLOAT.parseSingleValue(responseString));

        String exponentIndicator = null;
        if (responseString.contains("e")) {
            exponentIndicator = "e";
        }
        if (responseString.contains("E")) {
            exponentIndicator = "E";
        }

        final String exponentPart = exponentIndicator != null ? responseString.substring(responseString.indexOf(exponentIndicator) + 1) : ""; /* (Not null) */
        final String responseStringAfterExp = exponentIndicator == null ? responseString : responseString.substring(0, responseString.indexOf(exponentIndicator)); /* (Not null) */
        final String rightPart = responseStringAfterExp.contains(".") ? responseStringAfterExp.substring(responseStringAfterExp.indexOf(".") + 1) : ""; /* (Not null) */
        final String leftPart = responseStringAfterExp.contains(".") ? responseStringAfterExp.substring(0, responseStringAfterExp.indexOf(".")) : responseStringAfterExp; /* (Not null) */

        if (exponentIndicator == null && !responseStringAfterExp.contains(".")) {
            recordBuilder.put(StringInteraction.KEY_INTEGER_VALUE_NAME, IntegerValue.parseString(responseStringAfterExp, base));
        }

        recordBuilder.put(StringInteraction.KEY_LEFT_DIGITS_NAME, new IntegerValue(leftPart.length()));
        recordBuilder.put(StringInteraction.KEY_RIGHT_DIGITS_NAME, new IntegerValue(rightPart.length()));

        if (exponentIndicator != null) {
            int frac = rightPart.length();
            if (exponentPart.length() > 0) {
                frac -= Integer.parseInt(exponentPart);
            }
            recordBuilder.put(StringInteraction.KEY_NDP_NAME, new IntegerValue(frac));
        }
        else {
            recordBuilder.put(StringInteraction.KEY_NDP_NAME, IntegerValue.parseString(rightPart.isEmpty() ? "0" : rightPart));
        }

        int nsf = (leftPart.isEmpty()) ? 0 : new Integer(leftPart).toString().length();
        nsf += rightPart.length();
        recordBuilder.put(StringInteraction.KEY_NSF_NAME, new IntegerValue(nsf));

        if (exponentIndicator != null) {
            recordBuilder.put(StringInteraction.KEY_EXPONENT_NAME, IntegerValue.parseString(exponentPart.isEmpty() ? "0" : exponentPart));
        }
        return RecordValue.createRecordValue(recordBuilder);
    }
}