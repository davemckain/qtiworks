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
package org.qtitools.qti.node.expression;

import static org.junit.Assert.fail;

import uk.ac.ed.ph.jqtiplus.exception.QTIRuntimeException;

import org.junit.Test;

/**
 * Superclass for expression refuse tests.
 * <p>
 * Evaluates expression and compares thrown exception (test is successful only if exception is thrown)
 * with expected exception.
 * <p>
 * Number of required subexpressions and presence of required attributes are not tested, because it
 * should be tested by validator of xml.
 */
public abstract class ExpressionRefuseTest extends ExpressionTest {
    private Class<? extends QTIRuntimeException> expectedException;

    /**
     * Constructs expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedException expected exception during evaluation of tested expression
     */
    public ExpressionRefuseTest(String xml, Class<? extends QTIRuntimeException> expectedException) {
        super(xml);

        this.expectedException = expectedException;
    }

    /**
     * Tests evaluation of tested expression.
     */
    @Test
    public void testEvaluate() {
        try {
            getExpression().evaluate();
        }
        catch (Throwable ex) {
            if (!expectedException.equals(ex.getClass()))
                fail("Unexpected exception, expected<" + expectedException.getName() +
                        "> but was<" + ex.getClass().getName() + "> with message: " + ex.getMessage());
            else
                return; // This exception was expected. Test was successful.
        }
        fail("Expected exception: " + expectedException.getName());
    }
}
