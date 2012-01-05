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
package org.qtitools.qti.node.expression.general;

import uk.ac.ed.ph.jqtiplus.value.IntegerValue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionAcceptTest;

/**
 * Test of <code>RandomInteger</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.general.RandomInteger
 */
@RunWith(Parameterized.class)
public class RandomIntegerAcceptTest extends ExpressionAcceptTest {
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // min = max, step = 1 {"<randomIntegerEx min='-3' max='-3' seed='0'/>", -3}, {"<randomIntegerEx min='-1' max='-1' seed='0'/>", -1}, {"<randomIntegerEx min='0' max='0' seed='0'/>", 0}, {"<randomIntegerEx min='1' max='1' seed='0'/>", 1}, {"<randomIntegerEx min='4' max='4' seed='0'/>", 4},
            // min = 0, max = 10, step = 1 {"<randomIntegerEx min='0' max='10' seed='10'/>", 5},
            // min = -10, max = 0, step = 1 {"<randomIntegerEx min='-10' max='0' seed='10'/>", -5},
            // min = -3, max = 4, step = 1 {"<randomIntegerEx min='-3' max='4' step='1' seed='4096'/>", -3}, {"<randomIntegerEx min='-3' max='4' step='1' seed='4480'/>", -2}, {"<randomIntegerEx min='-3' max='4' step='1' seed='6144'/>", -1}, {"<randomIntegerEx min='-3' max='4' step='1' seed='12416'/>", 0}, {"<randomIntegerEx min='-3' max='4' step='1' seed='1536'/>", 1}, {"<randomIntegerEx min='-3' max='4' step='1' seed='0'/>", 2}, {"<randomIntegerEx min='-3' max='4' step='1' seed='256'/>", 3}, {"<randomIntegerEx min='-3' max='4' step='1' seed='2048'/>", 4},
            // min = -3, max = 4, step = 2 {"<randomIntegerEx min='-3' max='4' step='2' seed='4096'/>", -3}, {"<randomIntegerEx min='-3' max='4' step='2' seed='6144'/>", -1}, {"<randomIntegerEx min='-3' max='4' step='2' seed='0'/>", 1}, {"<randomIntegerEx min='-3' max='4' step='2' seed='256'/>", 3},
            // min = -3, max = 4, step = 3 {"<randomIntegerEx min='-3' max='4' step='3' seed='0'/>", -3}, {"<randomIntegerEx min='-3' max='4' step='3' seed='2'/>", 0}, {"<randomIntegerEx min='-3' max='4' step='3' seed='3'/>", 3},
            // min = -3, max = 4, step = 4 {"<randomIntegerEx min='-3' max='4' step='4' seed='4096'/>", -3}, {"<randomIntegerEx min='-3' max='4' step='4' seed='0'/>", 1},
            // min = -3, max = 4, step = 5 {"<randomIntegerEx min='-3' max='4' step='5' seed='4096'/>", -3}, {"<randomIntegerEx min='-3' max='4' step='5' seed='0'/>", 2},
            // min = -3, max = 4, step = 6 {"<randomIntegerEx min='-3' max='4' step='6' seed='4096'/>", -3}, {"<randomIntegerEx min='-3' max='4' step='6' seed='0'/>", 3},
            // min = -3, max = 4, step = 7 {"<randomIntegerEx min='-3' max='4' step='7' seed='4096'/>", -3}, {"<randomIntegerEx min='-3' max='4' step='7' seed='0'/>", 4},
            // min = -3, max = 4, step = 8 {"<randomIntegerEx min='-3' max='4' step='8' seed='0'/>", -3},
        });
    }

    /**
     * Constructs <code>RandomInteger</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public RandomIntegerAcceptTest(String xml, Integer expectedValue) {
        super(xml, new IntegerValue(expectedValue));
    }
}
