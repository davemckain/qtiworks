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
 * Test of <code>StringMatch</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.StringMatch
 */
@RunWith(Parameterized.class)
public class StringMatchAcceptTest extends ExpressionAcceptTest {
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // null {"<stringMatch caseSensitive='true'>" +
                "<null/>" +
                "<null/>" +
            "</stringMatch>", null}, {"<stringMatch caseSensitive='true'>" +
                "<null/>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", null}, {"<stringMatch caseSensitive='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<null/>" +
            "</stringMatch>", null},
            // -------------------------------------------------------------------------------------
            // substring = false, caseSensitive = true {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>String</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", false},
            // substring = false, caseSensitive = true {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false},
            // substring = false, caseSensitive = true {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='false'>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", false},
            // substring = false, caseSensitive = false {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>String</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true},
            // substring = false, caseSensitive = false {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false},
            // substring = false, caseSensitive = false {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='false'>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", false},
            // -------------------------------------------------------------------------------------
            // substring = true, caseSensitive = true {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>String</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", false},
            // substring = true, caseSensitive = true {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false},
            // substring = true, caseSensitive = true {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='true' substring='true'>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true},
            // substring = true, caseSensitive = false {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>String</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true},
            // substring = true, caseSensitive = false {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>String</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>STRING</baseValue>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
            "</stringMatch>", false},
            // substring = true, caseSensitive = false {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>stringabc</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>abcstring</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true}, {"<stringMatch caseSensitive='false' substring='true'>" +
                "<baseValue baseType='string'>abcstringabc</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</stringMatch>", true},
        });
    }

    /**
     * Constructs <code>StringMatch</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public StringMatchAcceptTest(String xml, Boolean expectedValue) {
        super(xml, (expectedValue != null) ? BooleanValue.valueOf(expectedValue) : NullValue.INSTANCE);
    }
}
