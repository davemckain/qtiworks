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

import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionAcceptTest;

/**
 * Test of <code>Inside</code> expression.
 * 
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Inside
 */
@RunWith(Parameterized.class)
public class InsideAcceptTest extends ExpressionAcceptTest {

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // null
                { "<inside shape='default'>" +
                        "<null/>" +
                        "</inside>", null }, { "<inside shape='default'>" +
                        "<multiple/>" +
                        "</inside>", null }, { "<inside shape='default'>" +
                        "<ordered/>" +
                        "</inside>", null },
                // single - default
                // single - rect
                { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<baseValue baseType='point'>2 3</baseValue>" +
                        "</inside>", true }, { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<baseValue baseType='point'>1 4</baseValue>" +
                        "</inside>", true }, { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<baseValue baseType='point'>3 4</baseValue>" +
                        "</inside>", true }, { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<baseValue baseType='point'>3 2</baseValue>" +
                        "</inside>", true }, { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<baseValue baseType='point'>1 2</baseValue>" +
                        "</inside>", true }, { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<baseValue baseType='point'>0 3</baseValue>" +
                        "</inside>", false }, { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<baseValue baseType='point'>4 3</baseValue>" +
                        "</inside>", false }, { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<baseValue baseType='point'>5 2</baseValue>" +
                        "</inside>", false }, { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<baseValue baseType='point'>2 1</baseValue>" +
                        "</inside>", false },
                // single - circle
                { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>4 3</baseValue>" +
                        "</inside>", true }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>6 3</baseValue>" +
                        "</inside>", true }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>4 5</baseValue>" +
                        "</inside>", true }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>2 3</baseValue>" +
                        "</inside>", true }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>4 1</baseValue>" +
                        "</inside>", true }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>5 2</baseValue>" +
                        "</inside>", true }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>5 4</baseValue>" +
                        "</inside>", true }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>3 4</baseValue>" +
                        "</inside>", true }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>3 2</baseValue>" +
                        "</inside>", true }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>6 2</baseValue>" +
                        "</inside>", false }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>6 4</baseValue>" +
                        "</inside>", false }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>5 5</baseValue>" +
                        "</inside>", false }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>3 5</baseValue>" +
                        "</inside>", false }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>2 4</baseValue>" +
                        "</inside>", false }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>2 2</baseValue>" +
                        "</inside>", false }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>3 1</baseValue>" +
                        "</inside>", false }, { "<inside shape='circle' coords='4,3,2'>" +
                        "<baseValue baseType='point'>5 1</baseValue>" +
                        "</inside>", false },
                // single - poly
                { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>2 2</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>8 3</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>10 6</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>2 12</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>5 6</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>6 9</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>4 8</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>3 10</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>3 3</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>6 4</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>9 6</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>6 7</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>5 9</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>3 11</baseValue>" +
                        "</inside>", true }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>3 2</baseValue>" +
                        "</inside>", false }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>5 2</baseValue>" +
                        "</inside>", false }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>9 4</baseValue>" +
                        "</inside>", false }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>9 7</baseValue>" +
                        "</inside>", false }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>4 11</baseValue>" +
                        "</inside>", false }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>3 9</baseValue>" +
                        "</inside>", false }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>4 6</baseValue>" +
                        "</inside>", false }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>3 6</baseValue>" +
                        "</inside>", false }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>1 14</baseValue>" +
                        "</inside>", false }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>1 13</baseValue>" +
                        "</inside>", false }, { "<inside shape='poly' coords='2,2,8,3,10,6,2,12,5,6,2,2'>" +
                        "<baseValue baseType='point'>-2 15</baseValue>" +
                        "</inside>", false },
                // single - ellipse
                { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>6 4</baseValue>" +
                        "</inside>", true }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>10 4</baseValue>" +
                        "</inside>", true }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>6 6</baseValue>" +
                        "</inside>", true }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>2 4</baseValue>" +
                        "</inside>", true }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>6 2</baseValue>" +
                        "</inside>", true }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>8 5</baseValue>" +
                        "</inside>", true }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>5 5</baseValue>" +
                        "</inside>", true }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>2 4</baseValue>" +
                        "</inside>", true }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>4 3</baseValue>" +
                        "</inside>", true }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>7 3</baseValue>" +
                        "</inside>", true }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>10 5</baseValue>" +
                        "</inside>", false }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>8 6</baseValue>" +
                        "</inside>", false }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>4 6</baseValue>" +
                        "</inside>", false }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>2 3</baseValue>" +
                        "</inside>", false }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>4 2</baseValue>" +
                        "</inside>", false }, { "<inside shape='ellipse' coords='6,4,4,2'>" +
                        "<baseValue baseType='point'>7 2</baseValue>" +
                        "</inside>", false },
                // multiple
                { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<multiple>" +
                        "<baseValue baseType='point'>0 3</baseValue>" +
                        "<baseValue baseType='point'>4 3</baseValue>" +
                        "<baseValue baseType='point'>2 3</baseValue>" +
                        "<baseValue baseType='point'>5 2</baseValue>" +
                        "<baseValue baseType='point'>2 1</baseValue>" +
                        "</multiple>" +
                        "</inside>", true }, { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<multiple>" +
                        "<baseValue baseType='point'>0 3</baseValue>" +
                        "<baseValue baseType='point'>4 3</baseValue>" +
                        "<baseValue baseType='point'>5 2</baseValue>" +
                        "<baseValue baseType='point'>2 1</baseValue>" +
                        "</multiple>" +
                        "</inside>", false },
                // ordered
                { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<ordered>" +
                        "<baseValue baseType='point'>0 3</baseValue>" +
                        "<baseValue baseType='point'>4 3</baseValue>" +
                        "<baseValue baseType='point'>2 3</baseValue>" +
                        "<baseValue baseType='point'>5 2</baseValue>" +
                        "<baseValue baseType='point'>2 1</baseValue>" +
                        "</ordered>" +
                        "</inside>", true }, { "<inside shape='rect' coords='1,2,3,4'>" +
                        "<ordered>" +
                        "<baseValue baseType='point'>0 3</baseValue>" +
                        "<baseValue baseType='point'>4 3</baseValue>" +
                        "<baseValue baseType='point'>5 2</baseValue>" +
                        "<baseValue baseType='point'>2 1</baseValue>" +
                        "</ordered>" +
                        "</inside>", false },
        });
    }

    /**
     * Constructs <code>Inside</code> expression test.
     * 
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public InsideAcceptTest(String xml, Boolean expectedValue) {
        super(xml, expectedValue != null ? BooleanValue.valueOf(expectedValue) : NullValue.INSTANCE);
    }
}
