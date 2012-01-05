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

import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionAcceptTest;

/**
 * Test of <code>DurationGte</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.DurationGte
 */
@RunWith(Parameterized.class)
public class DurationGteAcceptTest extends ExpressionAcceptTest {
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // null {"<durationGTE>" +
                "<null/>" +
                "<null/>" +
            "</durationGTE>", null}, {"<durationGTE>" +
                "<baseValue baseType='duration'>1</baseValue>" +
                "<null/>" +
            "</durationGTE>", null}, {"<durationGTE>" +
                "<null/>" +
                "<baseValue baseType='duration'>1</baseValue>" +
            "</durationGTE>", null},
            // true {"<durationGTE>" +
                "<baseValue baseType='duration'>0</baseValue>" +
                "<baseValue baseType='duration'>0</baseValue>" +
            "</durationGTE>", true}, {"<durationGTE>" +
                "<baseValue baseType='duration'>-0</baseValue>" +
                "<baseValue baseType='duration'>+0</baseValue>" +
            "</durationGTE>", true}, {"<durationGTE>" +
                "<baseValue baseType='duration'>0.1</baseValue>" +
                "<baseValue baseType='duration'>0</baseValue>" +
            "</durationGTE>", true}, {"<durationGTE>" +
                "<baseValue baseType='duration'>1</baseValue>" +
                "<baseValue baseType='duration'>1</baseValue>" +
            "</durationGTE>", true}, {"<durationGTE>" +
                "<baseValue baseType='duration'>1.2</baseValue>" +
                "<baseValue baseType='duration'>1.1</baseValue>" +
            "</durationGTE>", true}, {"<durationGTE>" +
                "<baseValue baseType='duration'>2</baseValue>" +
                "<baseValue baseType='duration'>1</baseValue>" +
            "</durationGTE>", true},
            // false {"<durationGTE>" +
                "<baseValue baseType='duration'>0</baseValue>" +
                "<baseValue baseType='duration'>0.1</baseValue>" +
            "</durationGTE>", false}, {"<durationGTE>" +
                "<baseValue baseType='duration'>1</baseValue>" +
                "<baseValue baseType='duration'>1.1</baseValue>" +
            "</durationGTE>", false}, {"<durationGTE>" +
                "<baseValue baseType='duration'>1</baseValue>" +
                "<baseValue baseType='duration'>2</baseValue>" +
            "</durationGTE>", false},
        });
    }

    /**
     * Constructs <code>DurationGte</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public DurationGteAcceptTest(String xml, Boolean expectedValue) {
        super(xml, (expectedValue != null) ? BooleanValue.valueOf(expectedValue) : NullValue.INSTANCE);
    }
}
