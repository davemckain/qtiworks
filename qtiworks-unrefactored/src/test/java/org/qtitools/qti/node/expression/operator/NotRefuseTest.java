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
package org.qtitools.qti.node.expression.operator;

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
 * Test of <code>Not</code> expression.
 * 
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Not
 */
@RunWith(Parameterized.class)
public class NotRefuseTest extends ExpressionRefuseTest {

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // multiple
                { "<not>" +
                        "<multiple>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</multiple>" +
                        "</not>", QtiCardinalityException.class },
                // ordered
                { "<not>" +
                        "<ordered>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</ordered>" +
                        "</not>", QtiCardinalityException.class },
                // record
                { "<not>" +
                        "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</recordEx>" +
                        "</not>", QtiCardinalityException.class },
                // identifier
                { "<not>" +
                        "<baseValue baseType='identifier'>true</baseValue>" +
                        "</not>", QtiBaseTypeException.class },
                // integer
                { "<not>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</not>", QtiBaseTypeException.class },
                // float
                { "<not>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "</not>", QtiBaseTypeException.class },
                // string
                { "<not>" +
                        "<baseValue baseType='string'>true</baseValue>" +
                        "</not>", QtiBaseTypeException.class },
                // point
                { "<not>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "</not>", QtiBaseTypeException.class },
                // pair
                { "<not>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "</not>", QtiBaseTypeException.class },
                // directedPair
                { "<not>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "</not>", QtiBaseTypeException.class },
                // duration
                { "<not>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "</not>", QtiBaseTypeException.class },
                // file
                { "<not>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "</not>", QtiBaseTypeException.class },
                // uri
                { "<not>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</not>", QtiBaseTypeException.class },
        });
    }

    /**
     * Constructs <code>Not</code> expression test.
     * 
     * @param xml xml data used for creation tested expression
     * @param expectedException expected exception during evaluation of tested
     *            expression
     */
    public NotRefuseTest(String xml, Class<? extends QtiRuntimeException> expectedException) {
        super(xml, expectedException);
    }
}
