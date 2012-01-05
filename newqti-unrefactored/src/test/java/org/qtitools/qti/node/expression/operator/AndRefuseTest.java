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
 * Test of <code>And</code> expression.
 * 
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.And
 */
@RunWith(Parameterized.class)
public class AndRefuseTest extends ExpressionRefuseTest {

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // multiple
                { "<and>" +
                        "<multiple>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</multiple>" +
                        "</and>", QTICardinalityException.class },
                // ordered
                { "<and>" +
                        "<ordered>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</ordered>" +
                        "</and>", QTICardinalityException.class },
                // record
                { "<and>" +
                        "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</recordEx>" +
                        "</and>", QTICardinalityException.class },
                // identifier
                { "<and>" +
                        "<baseValue baseType='identifier'>true</baseValue>" +
                        "</and>", QTIBaseTypeException.class },
                // boolean + integer
                { "<and>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</and>", QTIBaseTypeException.class },
                // integer
                { "<and>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</and>", QTIBaseTypeException.class },
                // float
                { "<and>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "</and>", QTIBaseTypeException.class },
                // string
                { "<and>" +
                        "<baseValue baseType='string'>true</baseValue>" +
                        "</and>", QTIBaseTypeException.class },
                // point
                { "<and>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "</and>", QTIBaseTypeException.class },
                // pair
                { "<and>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "</and>", QTIBaseTypeException.class },
                // directedPair
                { "<and>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "</and>", QTIBaseTypeException.class },
                // duration
                { "<and>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "</and>", QTIBaseTypeException.class },
                // file
                { "<and>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "</and>", QTIBaseTypeException.class },
                // uri
                { "<and>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</and>", QTIBaseTypeException.class },
        });
    }

    /**
     * Constructs <code>And</code> expression test.
     * 
     * @param xml xml data used for creation tested expression
     * @param expectedException expected exception during evaluation of tested
     *            expression
     */
    public AndRefuseTest(String xml, Class<? extends QTIRuntimeException> expectedException) {
        super(xml, expectedException);
    }
}
