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

import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;

import org.w3c.dom.Node;

/**
 * Superclass for all expression tests.
 */
public abstract class ExpressionTest {

    private final String xml;

    private Expression expression;

    /**
     * Constructs expression test.
     * 
     * @param xml xml data used for creation tested expression
     */
    public ExpressionTest(String xml) {
        this.xml = xml;
    }

    /**
     * Gets xml data used for creation tested expression.
     * 
     * @return xml data used for creation tested expression
     */
    protected String getXml() {
        return xml;
    }

    /**
     * Creates tested expression from given xml data.
     * 
     * @return tested expression from give xml data
     */
    protected Expression getExpression() {
        if (expression == null) {
            final Node node = AbstractNode.getElement(xml);
            expression = ExpressionType.getInstance(null, node.getNodeName());
            expression.load(node, jqtiController);

            final ValidationResult result = expression.validate(context, this);

            if (result.getErrors().size() > 0) {
                throw result.getErrors().get(0).createException();
            }
        }

        return expression;
    }
}
