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

import static org.junit.Assert.assertEquals;

import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TextEntryInteractionTest {

    public static RecordValue createRecordResult(final String stringValue, final double floatValue, final Integer integerValue, final int leftDigits, final int rightDigits, final int ndp,
            final int nsf, final Integer exponent) {
        final Map<Identifier, SingleValue> recordBuilder = new HashMap<Identifier, SingleValue>();

        recordBuilder.put(StringInteraction.KEY_STRING_VALUE_NAME, new StringValue(stringValue));
        recordBuilder.put(StringInteraction.KEY_FLOAT_VALUE_NAME, new FloatValue(floatValue));
        if (integerValue!=null) {
            recordBuilder.put(StringInteraction.KEY_INTEGER_VALUE_NAME, new IntegerValue(integerValue));
        }
        recordBuilder.put(StringInteraction.KEY_LEFT_DIGITS_NAME, new IntegerValue(leftDigits));
        recordBuilder.put(StringInteraction.KEY_RIGHT_DIGITS_NAME, new IntegerValue(rightDigits));
        recordBuilder.put(StringInteraction.KEY_NDP_NAME, new IntegerValue(ndp));
        recordBuilder.put(StringInteraction.KEY_NSF_NAME, new IntegerValue(nsf));
        if (exponent!=null) {
            recordBuilder.put(StringInteraction.KEY_EXPONENT_NAME, new IntegerValue(exponent));
        }

        return (RecordValue) RecordValue.createRecordValue(recordBuilder);
    }

    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters(name="{index}: {0} {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "TextEntryInteraction-basic.xml", "foo", new StringValue("foo"), null, true },
                { "TextEntryInteraction-patternMask.xml", "foobarbob", new StringValue("foobarbob"), null, true },
                { "TextEntryInteraction-patternMask.xml", "foobob", new StringValue("foobob"), null, false },
                { "TextEntryInteraction-patternMask.xml", "foobobz", new StringValue("foobobz"), null, false },
                { "TextEntryInteraction-basic-float.xml", "1", new FloatValue("1"), new StringValue("1"), true },
                { "TextEntryInteraction-basic-float.xml", "1.0", new FloatValue("1.0"), new StringValue("1.0"), true },
                { "TextEntryInteraction-basic-float.xml", "1.0e2", new FloatValue("1.0e2"), new StringValue("1.0e2"), true },
                { "TextEntryInteraction-basic-integer.xml", "1", IntegerValue.parseString("1"), new StringValue("1"), true },
                { "TextEntryInteraction-basic-record.xml", "1", createRecordResult("1", 1.0, 1, 1, 0, 0, 1, null), new StringValue("1"), true },
                { "TextEntryInteraction-basic-record.xml", "1.23e2", createRecordResult("1.23e2", 123.0, null, 1, 2, 0, 3, 2),
                        new StringValue("1.23e2"), true },
                { "TextEntryInteraction-basic-record.xml", "1.23e-2", createRecordResult("1.23e-2", 0.0123, null, 1, 2, 4, 3, -2),
                        new StringValue("1.23e-2"), true },
                { "TextEntryInteraction-basic-integer-radix.xml", "465", new IntegerValue(243), new StringValue("465"), true }
        });
    }

    private static String RESPONSE_NAME = "response";
    private static String STRING_RESPONSE_NAME = "stringResponse";

    private final String fileName;
    private final String stringResponse;
    private final Value expectedResponse;
    private final Value expectedStringResponse;
    private final boolean expectedValidates;

    public TextEntryInteractionTest(final String fileName, final String stringResponse, final Value expectedResponse, final Value expectedStringResponse, final boolean expectedValidates) {
        this.fileName = fileName;
        this.stringResponse = stringResponse;
        this.expectedResponse = expectedResponse;
        this.expectedStringResponse = expectedStringResponse;
        this.expectedValidates = expectedValidates;
    }

    @Test
    public void test() throws Exception {
        final ItemSessionController itemSessionController = UnitTestHelper.loadUnitTestAssessmentItemForControl("item/interactions/" + fileName, true);
        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();
        final Date timestamp = new Date();
        itemSessionController.initialize(timestamp);
        itemSessionController.performTemplateProcessing(timestamp);
        itemSessionController.enterItem(timestamp);

        final Map<Identifier, ResponseData> responses = new HashMap<Identifier, ResponseData>();
        responses.put(Identifier.parseString(RESPONSE_NAME), new StringResponseData(stringResponse));

        itemSessionController.bindResponses(timestamp, responses);

        final Set<Identifier> unboundResponses = itemSessionState.getUnboundResponseIdentifiers();
        final Set<Identifier> invalidResponses = itemSessionState.getInvalidResponseIdentifiers();
        assertEquals(0, unboundResponses.size());
        assertEquals(expectedValidates, invalidResponses.isEmpty());
        assertEquals(expectedResponse, itemSessionState.getUncommittedResponseValue(Identifier.parseString(RESPONSE_NAME)));
        assertEquals(expectedStringResponse, itemSessionState.getUncommittedResponseValue(Identifier.parseString(STRING_RESPONSE_NAME)));
    }
}
