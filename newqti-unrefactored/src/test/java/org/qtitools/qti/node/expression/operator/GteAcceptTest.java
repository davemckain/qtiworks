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
 * Test of <code>Gte</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Gte
 */
@RunWith(Parameterized.class)
public class GteAcceptTest extends ExpressionAcceptTest {
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // null {"<gte>" +
                "<null/>" +
                "<null/>" +
            "</gte>", null}, {"<gte>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<null/>" +
            "</gte>", null}, {"<gte>" +
                "<null/>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</gte>", null},
            // integer {"<gte>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='integer'>2</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>-7</baseValue>" +
                "<baseValue baseType='integer'>-5</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='integer'>-3</baseValue>" +
                "<baseValue baseType='integer'>-3</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
                "<baseValue baseType='integer'>-5</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
                "<baseValue baseType='integer'>2</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>0</baseValue>" +
                "<baseValue baseType='integer'>0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
            "</gte>", true},
            // float {"<gte>" +
                "<baseValue baseType='float'>1.5</baseValue>" +
                "<baseValue baseType='float'>2.3</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='float'>1.4</baseValue>" +
                "<baseValue baseType='float'>1.4</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>2.8</baseValue>" +
                "<baseValue baseType='float'>1.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>-7.4</baseValue>" +
                "<baseValue baseType='float'>-5.6</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='float'>-3.3</baseValue>" +
                "<baseValue baseType='float'>-3.3</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>-4.1</baseValue>" +
                "<baseValue baseType='float'>-5.9</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>-4.6</baseValue>" +
                "<baseValue baseType='float'>2.7</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='float'>2.6</baseValue>" +
                "<baseValue baseType='float'>-4.3</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>0.0</baseValue>" +
                "<baseValue baseType='float'>0.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
            "</gte>", true},
            // integer + float {"<gte>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='float'>2.3</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='float'>1.5</baseValue>" +
                "<baseValue baseType='integer'>2</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='float'>1.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<baseValue baseType='float'>1.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>2.8</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>-7</baseValue>" +
                "<baseValue baseType='float'>-5.6</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='float'>-7.4</baseValue>" +
                "<baseValue baseType='integer'>-5</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='integer'>-3</baseValue>" +
                "<baseValue baseType='float'>-3.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
                "<baseValue baseType='float'>-5.9</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>-4.1</baseValue>" +
                "<baseValue baseType='integer'>-5</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
                "<baseValue baseType='float'>2.7</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='float'>-4.6</baseValue>" +
                "<baseValue baseType='integer'>2</baseValue>" +
            "</gte>", false}, {"<gte>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<baseValue baseType='float'>-4.3</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>2.6</baseValue>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>0</baseValue>" +
                "<baseValue baseType='float'>0.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
            "</gte>", true}, {"<gte>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
            "</gte>", true},
        });
    }

    /**
     * Constructs <code>Gte</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public GteAcceptTest(String xml, Boolean expectedValue) {
        super(xml, (expectedValue != null) ? BooleanValue.valueOf(expectedValue) : NullValue.INSTANCE);
    }
}
