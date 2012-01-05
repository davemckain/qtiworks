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

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionAcceptTest;

/**
 * Test of <code>IsNull</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.IsNull
 */
@RunWith(Parameterized.class)
public class IsNullAcceptTest extends ExpressionAcceptTest {
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // null {"<isNull>" +
                "<null/>" +
            "</isNull>", true},
            // identifier {"<isNull>" +
                "<baseValue baseType='identifier'>identifier</baseValue>" +
            "</isNull>", false},
            // boolean {"<isNull>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
            "</isNull>", false}, {"<isNull>" +
                "<baseValue baseType='boolean'>false</baseValue>" +
            "</isNull>", false},
            // integer {"<isNull>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</isNull>", false},
            // float {"<isNull>" +
                "<baseValue baseType='float'>1.2</baseValue>" +
            "</isNull>", false},
            // string {"<isNull>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</isNull>", false},
            // point {"<isNull>" +
                "<baseValue baseType='point'>1 1</baseValue>" +
            "</isNull>", false},
            // pair {"<isNull>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
            "</isNull>", false},
            // directedPair {"<isNull>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
            "</isNull>", false},
            // duration {"<isNull>" +
                "<baseValue baseType='duration'>1.2</baseValue>" +
            "</isNull>", false},
            // file {"<isNull>" +
                "<baseValue baseType='file'>file</baseValue>" +
            "</isNull>", false},
            // uri {"<isNull>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
            "</isNull>", false},
            // multiple {"<isNull>" +
                "<multiple/>" +
            "</isNull>", true}, {"<isNull>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</multiple>" +
            "</isNull>", false},
            // ordered {"<isNull>" +
                "<ordered/>" +
            "</isNull>", true}, {"<isNull>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</ordered>" +
            "</isNull>", false},
            // record {"<isNull>" +
                "<recordEx/>" +
            "</isNull>", true}, {"<isNull>" +
                "<recordEx identifiers='key_1'>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</recordEx>" +
            "</isNull>", false},
        });
    }

    /**
     * Constructs <code>IsNull</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public IsNullAcceptTest(String xml, Boolean expectedValue) {
        super(xml, BooleanValue.valueOf(expectedValue));
    }
}
