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
package uk.ac.ed.ph.jqtiplus.types;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests {@link Identifier} implementation of parsing value from <code>String</code>.
 * <p>
 * This test contains only invalid <code>String</code> representations.
 */
@RunWith(Parameterized.class)
public class IdentifierRefuseTest {

    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "" }, { " " }, { "-identifier" }, { ".identifier" }, { "1identifier" },
                { "identifier:identifier" }, { "`A" }, { "A`" }, { "�A" }, { "A�" }, { "!A" }, { "A!" }, { "\"A" }, { "A\"" }, { "�A" }, { "A�" }, { "$A" },
                { "A$" }, { "%A" }, { "A%" }, { "^A" }, { "A^" }, { "&A" }, { "A&" }, { "*A" }, { "A*" }, { "(A" }, { "A(" }, { ")A" }, { "A)" }, { "=A" },
                { "A=" }, { "+A" }, { "A+" }, { "[A" }, { "A[" }, { "{A" }, { "A{" }, { "]A" }, { "A]" }, { "}A" }, { "A}" }, { ";A" }, { "A;" }, { ":A" },
                { "A:" }, { "'A" }, { "A'" }, { "@A" }, { "A@" }, { "#A" }, { "A#" }, { "~A" }, { "A~" }, { "\\A" }, { "A\\" }, { "|A" }, { "A|" }, { ",A" },
                { "A," }, { "<A" }, { "A<" }, { ">A" }, { "A>" }, { "/A" }, { "A/" }, { "?A" }, { "A?" }, { "*A" }, { "A*" },
        });
    }

    private final String string;

    public IdentifierRefuseTest(final String string) {
        this.string = string;
    }

    /**
     * Tests parsing value from <code>String</code> representation.
     *
     * @throws QtiParseException if test was successful
     */
    @Test(expected = QtiParseException.class)
    public void testParseIdentifier() throws QtiParseException {
        Identifier.parseString(string);
    }
}
