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

import uk.ac.ed.ph.jqtiplus.exception.QTIAttributeException;
import uk.ac.ed.ph.jqtiplus.exception.QTIBaseTypeException;
import uk.ac.ed.ph.jqtiplus.exception.QTICardinalityException;
import uk.ac.ed.ph.jqtiplus.exception.QTIRuntimeException;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionRefuseTest;

/**
 * Test of <code>Substring</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Substring
 */
@RunWith(Parameterized.class)
public class SubstringRefuseTest extends ExpressionRefuseTest {
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // attributes {"<substring caseSensitive='True'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</substring>", QTIAttributeException.class}, {"<substring caseSensitive='TRUE'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</substring>", QTIAttributeException.class}, {"<substring caseSensitive='1.0'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</substring>", QTIAttributeException.class},
            // multiple {"<substring caseSensitive='true'>" +
                "<multiple>" +
                    "<baseValue baseType='string'>string</baseValue>" +
                "</multiple>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</substring>", QTICardinalityException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='string'>string</baseValue>" +
                "</multiple>" +
            "</substring>", QTICardinalityException.class},
            // ordered {"<substring caseSensitive='true'>" +
                "<ordered>" +
                    "<baseValue baseType='string'>string</baseValue>" +
                "</ordered>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</substring>", QTICardinalityException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='string'>string</baseValue>" +
                "</ordered>" +
            "</substring>", QTICardinalityException.class},
            // record {"<substring caseSensitive='true'>" +
                "<recordEx identifiers='key_1'>" +
                    "<baseValue baseType='string'>string</baseValue>" +
                "</recordEx>" +
                "<baseValue baseType='string'>string</baseValue>" +
            "</substring>", QTICardinalityException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<recordEx identifiers='key_1'>" +
                    "<baseValue baseType='string'>string</baseValue>" +
                "</recordEx>" +
            "</substring>", QTICardinalityException.class},
            // identifier {"<substring caseSensitive='true'>" +
                "<baseValue baseType='identifier'>identifier</baseValue>" +
                "<baseValue baseType='string'>identifier</baseValue>" +
            "</substring>", QTIBaseTypeException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>identifier</baseValue>" +
                "<baseValue baseType='identifier'>identifier</baseValue>" +
            "</substring>", QTIBaseTypeException.class},
            // boolean {"<substring caseSensitive='true'>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
                "<baseValue baseType='string'>true</baseValue>" +
            "</substring>", QTIBaseTypeException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>true</baseValue>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
            "</substring>", QTIBaseTypeException.class},
            // integer {"<substring caseSensitive='true'>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='string'>1</baseValue>" +
            "</substring>", QTIBaseTypeException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>1</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</substring>", QTIBaseTypeException.class},
            // float {"<substring caseSensitive='true'>" +
                "<baseValue baseType='float'>1</baseValue>" +
                "<baseValue baseType='string'>1</baseValue>" +
            "</substring>", QTIBaseTypeException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>1</baseValue>" +
                "<baseValue baseType='float'>1</baseValue>" +
            "</substring>", QTIBaseTypeException.class},
            // point {"<substring caseSensitive='true'>" +
                "<baseValue baseType='point'>1 1</baseValue>" +
                "<baseValue baseType='string'>1 1</baseValue>" +
            "</substring>", QTIBaseTypeException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>1 1</baseValue>" +
                "<baseValue baseType='point'>1 1</baseValue>" +
            "</substring>", QTIBaseTypeException.class},
            // pair {"<substring caseSensitive='true'>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='string'>identifier_1 identifier_2</baseValue>" +
            "</substring>", QTIBaseTypeException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
            "</substring>", QTIBaseTypeException.class},
            // directedPair {"<substring caseSensitive='true'>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='string'>identifier_1 identifier_2</baseValue>" +
            "</substring>", QTIBaseTypeException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
            "</substring>", QTIBaseTypeException.class},
            // duration {"<substring caseSensitive='true'>" +
                "<baseValue baseType='duration'>1</baseValue>" +
                "<baseValue baseType='string'>1</baseValue>" +
            "</substring>", QTIBaseTypeException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>1</baseValue>" +
                "<baseValue baseType='duration'>1</baseValue>" +
            "</substring>", QTIBaseTypeException.class},
            // file {"<substring caseSensitive='true'>" +
                "<baseValue baseType='file'>file</baseValue>" +
                "<baseValue baseType='string'>file</baseValue>" +
            "</substring>", QTIBaseTypeException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>file</baseValue>" +
                "<baseValue baseType='file'>file</baseValue>" +
            "</substring>", QTIBaseTypeException.class},
            // uri {"<substring caseSensitive='true'>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
                "<baseValue baseType='string'>uri</baseValue>" +
            "</substring>", QTIBaseTypeException.class}, {"<substring caseSensitive='true'>" +
                "<baseValue baseType='string'>uri</baseValue>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
            "</substring>", QTIBaseTypeException.class},
        });
    }

    /**
     * Constructs <code>Substring</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedException expected exception during evaluation of tested expression
     */
    public SubstringRefuseTest(String xml, Class<? extends QTIRuntimeException> expectedException) {
        super(xml, expectedException);
    }
}
