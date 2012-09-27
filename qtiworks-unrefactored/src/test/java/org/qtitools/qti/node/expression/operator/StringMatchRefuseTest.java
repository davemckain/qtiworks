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

import uk.ac.ed.ph.jqtiplus.exception.QtiBaseTypeException;
import uk.ac.ed.ph.jqtiplus.exception.QtiBaseTypeException;
import uk.ac.ed.ph.jqtiplus.exception.QtiCardinalityException;
import uk.ac.ed.ph.jqtiplus.exception.QtiRuntimeException;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionRefuseTest;

/**
 * Test of <code>StringMatch</code> expression.
 * 
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.StringMatch
 */
@RunWith(Parameterized.class)
public class StringMatchRefuseTest extends ExpressionRefuseTest {

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // attributes
                { "<stringMatch caseSensitive='True'>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='TRUE'>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='1.0'>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class },
                // multiple
                { "<stringMatch caseSensitive='true'>" +
                        "<multiple>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</multiple>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</stringMatch>", QtiCardinalityException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<multiple>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</multiple>" +
                        "</stringMatch>", QtiCardinalityException.class },
                // ordered
                { "<stringMatch caseSensitive='true'>" +
                        "<ordered>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</ordered>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</stringMatch>", QtiCardinalityException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<ordered>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</ordered>" +
                        "</stringMatch>", QtiCardinalityException.class },
                // record
                { "<stringMatch caseSensitive='true'>" +
                        "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</recordEx>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</stringMatch>", QtiCardinalityException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</recordEx>" +
                        "</stringMatch>", QtiCardinalityException.class },
                // identifier
                { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='string'>identifier</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>identifier</baseValue>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class },
                // boolean
                { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='string'>true</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>true</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class },
                // integer
                { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='string'>1</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>1</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class },
                // float
                { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>1</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class },
                // point
                { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='string'>1 1</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>1 1</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class },
                // pair
                { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='string'>identifier_1 identifier_2</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class },
                // directedPair
                { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='string'>identifier_1 identifier_2</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class },
                // duration
                { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='string'>1</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>1</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class },
                // file
                { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='string'>file</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>file</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class },
                // uri
                { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "<baseValue baseType='string'>uri</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class }, { "<stringMatch caseSensitive='true'>" +
                        "<baseValue baseType='string'>uri</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</stringMatch>", QtiBaseTypeException.class },
        });
    }

    /**
     * Constructs <code>StringMatch</code> expression test.
     * 
     * @param xml xml data used for creation tested expression
     * @param expectedException expected exception during evaluation of tested
     *            expression
     */
    public StringMatchRefuseTest(String xml, Class<? extends QtiRuntimeException> expectedException) {
        super(xml, expectedException);
    }
}
