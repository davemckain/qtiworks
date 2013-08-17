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
package uk.ac.ed.ph.jqtiplus.group.expression;

import uk.ac.ed.ph.jqtiplus.group.ComplexNodeGroup;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;

import java.util.List;

/**
 * Group of expression children.
 *
 * @author Jonathon Hare
 */
public final class ExpressionGroup extends ComplexNodeGroup<ExpressionParent, Expression> {

    private static final long serialVersionUID = 891305708750072316L;

    public ExpressionGroup(final ExpressionParent parent, final int minimum, final Integer maximum) {
        super(parent, Expression.DISPLAY_NAME,  ExpressionType.getQtiClassNames(), minimum, maximum);
    }

    public ExpressionGroup(final ExpressionParent parent, final int minimum, final int maximum) {
        this(parent, minimum, Integer.valueOf(maximum));
    }

    /**
     * Gets first child, or null if there are no children
     *
     * @return child
     * @see #setExpression
     */
    public Expression getExpression() {
        return children.isEmpty() ? null : children.get(0);
    }

    /**
     * Sets new single child, removing all existing children
     *
     * @param expression new child
     * @see #getExpression
     */
    public void setExpression(final Expression expression) {
        children.clear();
        children.add(expression);
    }

    public List<Expression> getExpressions() {
        return children;
    }

    @Override
    public Expression create(final String qtiClassName) {
        return ExpressionType.getInstance(getParent(), qtiClassName);
    }
}
