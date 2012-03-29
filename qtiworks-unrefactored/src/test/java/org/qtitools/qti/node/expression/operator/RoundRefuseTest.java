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
import uk.ac.ed.ph.jqtiplus.exception.QtiCardinalityException;
import uk.ac.ed.ph.jqtiplus.exception.QtiRuntimeException;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionRefuseTest;

/**
 * Test of <code>Round</code> expression.
 * 
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Round
 */
@RunWith(Parameterized.class)
public class RoundRefuseTest extends ExpressionRefuseTest {

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // multiple
                { "<round>" +
                        "<multiple>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "</multiple>" +
                        "</round>", QtiCardinalityException.class },
                // ordered
                { "<round>" +
                        "<ordered>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "</ordered>" +
                        "</round>", QtiCardinalityException.class },
                // record
                { "<round>" +
                        "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "</recordEx>" +
                        "</round>", QtiCardinalityException.class },
                // identifier
                { "<round>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "</round>", QtiBaseTypeException.class },
                // boolean
                { "<round>" +
                        "<baseValue baseType='boolean'>1</baseValue>" +
                        "</round>", QtiBaseTypeException.class },
                // integer
                { "<round>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</round>", QtiBaseTypeException.class },
                // string
                { "<round>" +
                        "<baseValue baseType='string'>1.0</baseValue>" +
                        "</round>", QtiBaseTypeException.class },
                // point
                { "<round>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "</round>", QtiBaseTypeException.class },
                // pair
                { "<round>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "</round>", QtiBaseTypeException.class },
                // directedPair
                { "<round>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "</round>", QtiBaseTypeException.class },
                // duration
                { "<round>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "</round>", QtiBaseTypeException.class },
                // file
                { "<round>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "</round>", QtiBaseTypeException.class },
                // uri
                { "<round>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</round>", QtiBaseTypeException.class },
        });
    }

    /**
     * Constructs <code>Round</code> expression test.
     * 
     * @param xml xml data used for creation tested expression
     * @param expectedException expected exception during evaluation of tested
     *            expression
     */
    public RoundRefuseTest(String xml, Class<? extends QtiRuntimeException> expectedException) {
        super(xml, expectedException);
    }
}
