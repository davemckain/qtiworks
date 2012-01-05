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
package org.qtitools.qti.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionAcceptTest;

/**
 * Test of <code>IntegerDivide</code> expression.
 * 
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.IntegerDivide
 */
@RunWith(Parameterized.class)
public class IntegerDivideAcceptTest extends ExpressionAcceptTest {

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // null
                { "<integerDivide>" +
                        "<null/>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</integerDivide>", null }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<null/>" +
                        "</integerDivide>", null }, { "<integerDivide>" +
                        "<null/>" +
                        "<null/>" +
                        "</integerDivide>", null }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>0</baseValue>" +
                        "</integerDivide>", null },
                // positive
                { "<integerDivide>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</integerDivide>", 0 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "</integerDivide>", 0 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "<baseValue baseType='integer'>4</baseValue>" +
                        "</integerDivide>", 0 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>4</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</integerDivide>", 1 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</integerDivide>", 3 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>10</baseValue>" +
                        "<baseValue baseType='integer'>5</baseValue>" +
                        "</integerDivide>", 2 },
                // negative
                { "<integerDivide>" +
                        "<baseValue baseType='integer'>-1</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</integerDivide>", -1 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>-1</baseValue>" +
                        "<baseValue baseType='integer'>-3</baseValue>" +
                        "</integerDivide>", 0 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>-1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "</integerDivide>", -1 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>-3</baseValue>" +
                        "<baseValue baseType='integer'>4</baseValue>" +
                        "</integerDivide>", -1 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>-4</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</integerDivide>", -2 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>-4</baseValue>" +
                        "<baseValue baseType='integer'>-3</baseValue>" +
                        "</integerDivide>", 1 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>-3</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</integerDivide>", -3 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>-3</baseValue>" +
                        "<baseValue baseType='integer'>-1</baseValue>" +
                        "</integerDivide>", 3 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>10</baseValue>" +
                        "<baseValue baseType='integer'>-5</baseValue>" +
                        "</integerDivide>", -2 }, { "<integerDivide>" +
                        "<baseValue baseType='integer'>-10</baseValue>" +
                        "<baseValue baseType='integer'>-5</baseValue>" +
                        "</integerDivide>", 2 },
        });
    }

    /**
     * Constructs <code>IntegerDivide</code> expression test.
     * 
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public IntegerDivideAcceptTest(String xml, Integer expectedValue) {
        super(xml, expectedValue != null ? new IntegerValue(expectedValue) : NullValue.INSTANCE);
    }
}
