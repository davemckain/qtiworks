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
package uk.ac.ed.ph.jqtiplus.value;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests <code>StringValue</code> implementation of <code>equals</code> and <code>hashCode</code> methods.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.StringValue
 */
@RunWith(Parameterized.class)
public class StringValueTest extends ValueTest {

    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // null
                {false, new StringValue("string"), null},
                // NullValue
                {false, new StringValue("string"), NullValue.INSTANCE},
                // Empty string should be treated as NULL
                {true, new StringValue(""), NullValue.INSTANCE},
                // Blank but non-empty string is not NULL though
                {false, new StringValue(" "), NullValue.INSTANCE},
                // IdentifierValue
                {false, new StringValue("string"), new IdentifierValue("identifier")},
                // BooleanValue
                {false, new StringValue("string"), BooleanValue.TRUE}, {false, new StringValue("string"), BooleanValue.FALSE},
                // IntegerValue
                {false, new StringValue("string"), new IntegerValue(1)},
                // FloatValue
                {false, new StringValue("string"), new FloatValue(1)},
                // StringValue
                {true, new StringValue("string"), new StringValue("string")}, {false, new StringValue("string"), new StringValue("String")}, {false, new StringValue("string"), new StringValue("STRING")}, {false, new StringValue("string 1"), new StringValue("string 2")},
                // PointValue
                {false, new StringValue("string"), new PointValue(1, 1)},
                // PairValue
                {false, new StringValue("string"), new PairValue("ident1", "ident2")},
                // DirectedPairValue
                {false, new StringValue("string"), new DirectedPairValue("ident1", "ident2")},
                // DurationValue
                {false, new StringValue("string"), new DurationValue(1)},
                // FileValue
                {false, new StringValue("string"), ValueTestUtils.createTestFileValue("file")},
                // UriValue
                {false, new StringValue("string"), new UriValue("uri")},
                // MultipleValue
                {false, new StringValue("string"), MultipleValue.emptyValue()}, {false, new StringValue("string"), MultipleValue.createMultipleValue(new StringValue("string"))},
                // OrderedValue
                {false, new StringValue("string"), OrderedValue.emptyValue()}, {false, new StringValue("string"), OrderedValue.createOrderedValue(new StringValue("string"))},
                // RecordValue
                {false, new StringValue("string"), RecordValue.emptyRecord()}, {false, new StringValue("string"), RecordValue.createRecordValue("identifier", new StringValue("string"))},
                });
    }

    /**
     * Constructs this test.
     *
     * @param equals true if given values are equal; false otherwise
     * @param value1 first value
     * @param value2 second value
     */
    public StringValueTest(final boolean equals, final Value value1, final Value value2) {
        super(equals, value1, value2);
    }
}
