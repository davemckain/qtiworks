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
package uk.ac.ed.ph.jqtiplus.node.expression;

import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * This interface is definition how to handle all expressions.
 * Every expression must implement this interface.
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public interface Expression extends ExpressionParent {

    /** Display name of this interface. */
    public static final String DISPLAY_NAME = "expression";

    /**
     * Gets parent of this expression.
     *
     * @return parent of this expression
     */
    @Override
    ExpressionParent getParent();

    /**
     * Gets expression type of this expression.
     *
     * @return expression type of this expression
     */
    ExpressionType getType();

    /**
     * Gets list of all possible produced cardinalities after evaluation (possible cardinalities of evaluated result).
     * Result of this method can change in time.
     * <p>
     * Static example is expression or. Expression or can produce only single cardinality and it cannot change.
     * <p>
     * Dynamic example is expression variable. Expression variable can produce any cardinality before evaluation. After evaluation it produces cardinality of
     * its result (and it can change every evaluation call!).
     * <p>
     * Null expression, empty containers (multiple, ordered, record), or NULL values produces all cardinalities. So they are compatible with anything.
     *
     * @return list of all possible produced cardinalities after evaluation
     */
    Cardinality[] getProducedCardinalities(ValidationContext context);

    /**
     * Gets list of all possible produced baseTypes after evaluation (possible baseTypes of evaluated result).
     * Result of this method can change in time.
     * <p>
     * Static example is expression or. Expression or can produce only boolean baseType and it cannot change.
     * <p>
     * Dynamic example is expression variable. Expression variable can produce any baseType before evaluation. After evaluation it produces baseType of its
     * result (and it can change every evaluation call!).
     * <p>
     * Null expression, empty containers (multiple, ordered, record), or NULL values produces all baseTypes. So they are compatible with anything.
     *
     * @return list of all possible produced baseTypes after evaluation
     */
    BaseType[] getProducedBaseTypes(ValidationContext context);

    /**
     * Gets all children of this expression.
     *
     * @deprecated Use {@link #getExpressions()}.
     *
     * @return all children of this expression
     */
    @Deprecated
    List<Expression> getChildren();

    /**
     * Evaluates this expression.
     * <ol>
     * <li>evaluates all subexpressions (calls <code>evaluate</code> method for every subexpression)
     * <li>validates this expression (calls <code>validate</code> method)
     * <li>evaluates this expression (calls <code>evaluateSelf</code> method)
     * </ol>
     *
     * @return result of evaluation, which will not be null (but might be a {@link NullValue}!)
     */
    Value evaluate(ProcessingContext context);
}
