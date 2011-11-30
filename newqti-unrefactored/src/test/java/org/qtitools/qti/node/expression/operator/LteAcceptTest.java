/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
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
 * Test of <code>Lte</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Lte
 */
@RunWith(Parameterized.class)
public class LteAcceptTest extends ExpressionAcceptTest
{
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][]
        {
            // null
            {"<lte>" +
                "<null/>" +
                "<null/>" +
            "</lte>", null},
            {"<lte>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<null/>" +
            "</lte>", null},
            {"<lte>" +
                "<null/>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</lte>", null},
            // integer
            {"<lte>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='integer'>2</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='integer'>-7</baseValue>" +
                "<baseValue baseType='integer'>-5</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>-3</baseValue>" +
                "<baseValue baseType='integer'>-3</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
                "<baseValue baseType='integer'>-5</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
                "<baseValue baseType='integer'>2</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='integer'>0</baseValue>" +
                "<baseValue baseType='integer'>0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
            "</lte>", true},
            // float
            {"<lte>" +
                "<baseValue baseType='float'>1.5</baseValue>" +
                "<baseValue baseType='float'>2.3</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>1.4</baseValue>" +
                "<baseValue baseType='float'>1.4</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>2.8</baseValue>" +
                "<baseValue baseType='float'>1.0</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='float'>-7.4</baseValue>" +
                "<baseValue baseType='float'>-5.6</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>-3.3</baseValue>" +
                "<baseValue baseType='float'>-3.3</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>-4.1</baseValue>" +
                "<baseValue baseType='float'>-5.9</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='float'>-4.6</baseValue>" +
                "<baseValue baseType='float'>2.7</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>2.6</baseValue>" +
                "<baseValue baseType='float'>-4.3</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='float'>0.0</baseValue>" +
                "<baseValue baseType='float'>0.0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
            "</lte>", true},
            // integer + float
            {"<lte>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='float'>2.3</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>1.5</baseValue>" +
                "<baseValue baseType='integer'>2</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='float'>1.0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<baseValue baseType='float'>1.0</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='float'>2.8</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='integer'>-7</baseValue>" +
                "<baseValue baseType='float'>-5.6</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>-7.4</baseValue>" +
                "<baseValue baseType='integer'>-5</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>-3</baseValue>" +
                "<baseValue baseType='float'>-3.0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
                "<baseValue baseType='float'>-5.9</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='float'>-4.1</baseValue>" +
                "<baseValue baseType='integer'>-5</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
                "<baseValue baseType='float'>2.7</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>-4.6</baseValue>" +
                "<baseValue baseType='integer'>2</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<baseValue baseType='float'>-4.3</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='float'>2.6</baseValue>" +
                "<baseValue baseType='integer'>-4</baseValue>" +
            "</lte>", false},
            {"<lte>" +
                "<baseValue baseType='integer'>0</baseValue>" +
                "<baseValue baseType='float'>0.0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
                "<baseValue baseType='float'>+0.0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
                "<baseValue baseType='integer'>+0</baseValue>" +
            "</lte>", true},
            {"<lte>" +
                "<baseValue baseType='integer'>-0</baseValue>" +
                "<baseValue baseType='float'>-0.0</baseValue>" +
            "</lte>", true},
        });
    }

    /**
     * Constructs <code>Lte</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public LteAcceptTest(String xml, Boolean expectedValue)
    {
        super(xml, (expectedValue != null) ? BooleanValue.valueOf(expectedValue) : NullValue.INSTANCE);
    }
}
