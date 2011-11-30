/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
 */

package org.qtitools.qti.node.item.interaction;

import static org.junit.Assert.assertEquals;


import uk.ac.ed.ph.jqtiplus.control.AssessmentItemController;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.StringInteraction;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemState;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TextEntryInteractionTest {

    public static RecordValue createRecordResult(String stringValue, double floatValue, Integer integerValue, int leftDigits, int rightDigits, int ndp,
            int nsf, Integer exponent) {
        RecordValue rv = new RecordValue();

        rv.add(StringInteraction.KEY_STRING_VALUE_NAME, new StringValue(stringValue));
        rv.add(StringInteraction.KEY_FLOAT_VALUE_NAME, new FloatValue(floatValue));
        rv.add(StringInteraction.KEY_INTEGER_VALUE_NAME, integerValue == null ? null : new IntegerValue(integerValue));
        rv.add(StringInteraction.KEY_LEFT_DIGITS_NAME, new IntegerValue(leftDigits));
        rv.add(StringInteraction.KEY_RIGHT_DIGITS_NAME, new IntegerValue(rightDigits));
        rv.add(StringInteraction.KEY_NDP_NAME, new IntegerValue(ndp));
        rv.add(StringInteraction.KEY_NSF_NAME, new IntegerValue(nsf));
        rv.add(StringInteraction.KEY_EXPONENT_NAME, exponent == null ? null : new IntegerValue(exponent));

        return rv;
    }

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                        { "TextEntryInteraction-basic.xml", "foo", new StringValue("foo"), null, true },
                        { "TextEntryInteraction-patternMask.xml", "foobarbob", new StringValue("foobarbob"), null, true },
                        { "TextEntryInteraction-patternMask.xml", "foobob", new StringValue("foobob"), null, false },
                        { "TextEntryInteraction-patternMask.xml", "foobobz", new StringValue("foobobz"), null, false },
                        { "TextEntryInteraction-basic-float.xml", "1", new FloatValue("1"), new StringValue("1"), true },
                        { "TextEntryInteraction-basic-float.xml", "1.0", new FloatValue("1.0"), new StringValue("1.0"), true },
                        { "TextEntryInteraction-basic-float.xml", "1.0e2", new FloatValue("1.0e2"), new StringValue("1.0e2"), true },
                        { "TextEntryInteraction-basic-integer.xml", "1", new IntegerValue("1"), new StringValue("1"), true },
                        { "TextEntryInteraction-basic-record.xml", "1", createRecordResult("1", 1.0, 1, 1, 0, 0, 1, null), new StringValue("1"), true },
                        { "TextEntryInteraction-basic-record.xml", "1.23e2", createRecordResult("1.23e2", 123.0, null, 1, 2, 0, 3, 2),
                                new StringValue("1.23e2"), true },
                        { "TextEntryInteraction-basic-record.xml", "1.23e-2", createRecordResult("1.23e-2", 0.0123, null, 1, 2, 4, 3, -2),
                                new StringValue("1.23e-2"), true },
                        { "TextEntryInteraction-basic-integer-radix.xml", "465", new IntegerValue(243), new StringValue("465"), true } });
    }

    private static String RESPONSE_NAME = "response";

    private static String STRING_RESPONSE_NAME = "stringResponse";

    private String fileName;

    private String stringResponse;

    private Value expectedResponse;

    private Value expectedStringResponse;

    private boolean expectedValidates;

    public TextEntryInteractionTest(String fileName, String stringResponse, Value expectedResponse, Value expectedStringResponse, boolean expectedValidates) {
        this.fileName = fileName;
        this.stringResponse = stringResponse;
        this.expectedResponse = expectedResponse;
        this.expectedStringResponse = expectedStringResponse;
        this.expectedValidates = expectedValidates;
    }

    @Test
    public void test() {
        AssessmentItemController itemController = UnitTestHelper.loadItemForControl(fileName, TextEntryInteractionTest.class);
        AssessmentItemState itemState = itemController.getItemState();

        Map<String, List<String>> responses = new HashMap<String, List<String>>();
        responses.put(RESPONSE_NAME, Arrays.asList(new String[] { stringResponse }));

        itemController.setResponses(responses);

        assertEquals(expectedResponse, itemState.getResponseValue(RESPONSE_NAME));
        assertEquals(expectedStringResponse, itemState.getResponseValue(STRING_RESPONSE_NAME));
        assertEquals(expectedValidates, itemController.validateResponses().size() == 0);
    }
}
