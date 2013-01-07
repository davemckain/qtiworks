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
 * Tests <code>IdentifierValue</code> implementation of <code>equals</code> and <code>hashCode</code> methods.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.IdentifierValue
 */
@RunWith(Parameterized.class)
public class IdentifierValueTest extends ValueTest {

    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // null
                {false, new IdentifierValue("identifier"), null},
                // NullValue
                {false, new IdentifierValue("identifier"), NullValue.INSTANCE},
                // IdentifierValue
                {true, new IdentifierValue("identifier"), new IdentifierValue("identifier")}, {false, new IdentifierValue("identifier"), new IdentifierValue("Identifier")}, {false, new IdentifierValue("identifier"), new IdentifierValue("IDENTIFIER")}, {false, new IdentifierValue("identifier_1"), new IdentifierValue("identifier_2")},
                // BooleanValue
                {false, new IdentifierValue("identifier"), BooleanValue.TRUE}, {false, new IdentifierValue("identifier"), BooleanValue.FALSE},
                // IntegerValue
                {false, new IdentifierValue("identifier"), new IntegerValue(1)},
                // FloatValue
                {false, new IdentifierValue("identifier"), new FloatValue(1)},
                // StringValue
                {false, new IdentifierValue("identifier"), new StringValue("string")},
                // PointValue
                {false, new IdentifierValue("identifier"), new PointValue(1, 1)},
                // PairValue
                {false, new IdentifierValue("identifier"), new PairValue("ident1", "ident2")},
                // DirectedPairValue
                {false, new IdentifierValue("identifier"), new DirectedPairValue("ident1", "ident2")},
                // DurationValue
                {false, new IdentifierValue("identifier"), new DurationValue(1)},
                // FileValue
                {false, new IdentifierValue("identifier"), ValueTestUtils.createTestFileValue("file")},
                // UriValue
                {false, new IdentifierValue("identifier"), new UriValue("uri")},
                // MultipleValue
                {false, new IdentifierValue("identifier"), MultipleValue.emptyValue()}, {false, new IdentifierValue("identifier"), MultipleValue.createMultipleValue(new IdentifierValue("identifier"))},
                // OrderedValue
                {false, new IdentifierValue("identifier"), OrderedValue.emptyValue()}, {false, new IdentifierValue("identifier"), OrderedValue.createOrderedValue(new IdentifierValue("identifier"))},
                // RecordValue
                {false, new IdentifierValue("identifier"), RecordValue.emptyRecord()}, {false, new IdentifierValue("identifier"), RecordValue.createRecordValue("identifier", new IdentifierValue("identifier"))},
                });
    }

    /**
     * Constructs this test.
     *
     * @param equals true if given values are equal; false otherwise
     * @param value1 first value
     * @param value2 second value
     */
    public IdentifierValueTest(final boolean equals, final Value value1, final Value value2) {
        super(equals, value1, value2);
    }
}
