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
 * Test of <code>Match</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Match
 */
@RunWith(Parameterized.class)
public class MatchAcceptTest extends ExpressionAcceptTest {
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // null {"<match>" +
                "<null/>" +
                "<null/>" +
            "</match>", null}, {"<match>" +
                "<null/>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</match>", null}, {"<match>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<null/>" +
            "</match>", null},
            // identifier {"<match>" +
                "<baseValue baseType='identifier'>identifier</baseValue>" +
                "<baseValue baseType='identifier'>identifier</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='identifier'>identifier1</baseValue>" +
                "<baseValue baseType='identifier'>identifier2</baseValue>" +
            "</match>", false},
            // boolean {"<match>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
                "<baseValue baseType='boolean'>false</baseValue>" +
            "</match>", false}, {"<match>" +
                "<baseValue baseType='boolean'>false</baseValue>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
            "</match>", false}, {"<match>" +
                "<baseValue baseType='boolean'>false</baseValue>" +
                "<baseValue baseType='boolean'>false</baseValue>" +
            "</match>", true},
            // integer {"<match>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='integer'>2</baseValue>" +
            "</match>", false},
            // float {"<match>" +
                "<baseValue baseType='float'>1.2</baseValue>" +
                "<baseValue baseType='float'>1.2</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='float'>1.2</baseValue>" +
                "<baseValue baseType='float'>3.4</baseValue>" +
            "</match>", false},
            // string {"<match>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>String</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>String</baseValue>" +
            "</match>", false}, {"<match>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
            "</match>", false},
            // point {"<match>" +
                "<baseValue baseType='point'>1 1</baseValue>" +
                "<baseValue baseType='point'>1 1</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='point'>1 2</baseValue>" +
                "<baseValue baseType='point'>3 4</baseValue>" +
            "</match>", false},
            // pair {"<match>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='pair'>identifier_2 identifier_1</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='pair'>identifier_3 identifier_4</baseValue>" +
            "</match>", false},
            // directedPair {"<match>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='directedPair'>identifier_2 identifier_1</baseValue>" +
            "</match>", false}, {"<match>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='directedPair'>identifier_3 identifier_4</baseValue>" +
            "</match>", false},
            // file {"<match>" +
                "<baseValue baseType='file'>file</baseValue>" +
                "<baseValue baseType='file'>file</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='file'>File</baseValue>" +
                "<baseValue baseType='file'>File</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='file'>FILE</baseValue>" +
                "<baseValue baseType='file'>FILE</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='file'>file</baseValue>" +
                "<baseValue baseType='file'>File</baseValue>" +
            "</match>", false}, {"<match>" +
                "<baseValue baseType='file'>file</baseValue>" +
                "<baseValue baseType='file'>FILE</baseValue>" +
            "</match>", false},
            // uri {"<match>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='uri'>Uri</baseValue>" +
                "<baseValue baseType='uri'>Uri</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='uri'>URI</baseValue>" +
                "<baseValue baseType='uri'>URI</baseValue>" +
            "</match>", true}, {"<match>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
                "<baseValue baseType='uri'>Uri</baseValue>" +
            "</match>", false}, {"<match>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
                "<baseValue baseType='uri'>URI</baseValue>" +
            "</match>", false},
            // multiple {"<match>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</multiple>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</multiple>" +
            "</match>", true}, {"<match>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</multiple>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</multiple>" +
            "</match>", true}, {"<match>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</multiple>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</multiple>" +
            "</match>", true}, {"<match>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</multiple>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</multiple>" +
            "</match>", false}, {"<match>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</multiple>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>4</baseValue>" +
                    "<baseValue baseType='integer'>5</baseValue>" +
                    "<baseValue baseType='integer'>6</baseValue>" +
                "</multiple>" +
            "</match>", false},
            // ordered {"<match>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</ordered>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</ordered>" +
            "</match>", true}, {"<match>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</ordered>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</ordered>" +
            "</match>", true}, {"<match>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</ordered>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</ordered>" +
            "</match>", false}, {"<match>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</ordered>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</ordered>" +
            "</match>", false}, {"<match>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</ordered>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>4</baseValue>" +
                    "<baseValue baseType='integer'>5</baseValue>" +
                    "<baseValue baseType='integer'>6</baseValue>" +
                "</ordered>" +
            "</match>", false},
            // record {"<match>" +
                "<recordEx identifiers='key_1'>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</recordEx>" +
                "<recordEx identifiers='key_1'>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</recordEx>" +
            "</match>", true}, {"<match>" +
                "<recordEx identifiers='key_1 key_2 key_3'>" +
                    "<baseValue baseType='boolean'>1</baseValue>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='float'>1</baseValue>" +
                "</recordEx>" +
                "<recordEx identifiers='key_1 key_2 key_3'>" +
                    "<baseValue baseType='boolean'>1</baseValue>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='float'>1</baseValue>" +
                "</recordEx>" +
            "</match>", true}, {"<match>" +
                "<recordEx identifiers='key_1 key_2 key_3'>" +
                    "<baseValue baseType='boolean'>1</baseValue>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='float'>1</baseValue>" +
                "</recordEx>" +
                "<recordEx identifiers='key_1 key_2 key_3'>" +
                    "<baseValue baseType='float'>1</baseValue>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='boolean'>1</baseValue>" +
                "</recordEx>" +
            "</match>", false}, {"<match>" +
                "<recordEx identifiers='key_1 key_2 key_3'>" +
                    "<baseValue baseType='boolean'>1</baseValue>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='float'>1</baseValue>" +
                "</recordEx>" +
                "<recordEx identifiers='key_1'>" +
                    "<baseValue baseType='boolean'>1</baseValue>" +
                "</recordEx>" +
            "</match>", false}, {"<match>" +
                "<recordEx identifiers='key_1 key_2 key_3'>" +
                    "<baseValue baseType='boolean'>1</baseValue>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='float'>1</baseValue>" +
                "</recordEx>" +
                "<recordEx identifiers='key_1 key_2 key_3'>" +
                    "<baseValue baseType='boolean'>0</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='float'>2</baseValue>" +
                "</recordEx>" +
            "</match>", false},
        });
    }

    /**
     * Constructs <code>Match</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public MatchAcceptTest(String xml, Boolean expectedValue) {
        super(xml, (expectedValue != null) ? BooleanValue.valueOf(expectedValue) : NullValue.INSTANCE);
    }
}
