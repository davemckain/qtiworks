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
package uk.ac.ed.ph.jqtiplus.node.expression;

import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xperimental.control.ProcessingContext;

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
    public ExpressionParent getParent();

    /**
     * Gets expression type of this expression.
     * 
     * @return expression type of this expression
     */
    public ExpressionType getType();

    /**
     * Returns true if value of evaluation can change every evaluation call; false otherwise.
     * It checks this expression and all of its children (if any child is variable, this expression becomes variable too).
     * Example of variable expressions: variable and outcome expressions.
     * 
     * @return true if value of evaluation can change every evaluation call; false otherwise
     */
    public boolean isVariable();

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
     * @param context TODO
     * @return list of all possible produced cardinalities after evaluation
     */
    public Cardinality[] getProducedCardinalities(ValidationContext context);

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
     * @param context TODO
     * @return list of all possible produced baseTypes after evaluation
     */
    public BaseType[] getProducedBaseTypes(ValidationContext context);

    /**
     * Gets all children of this expression.
     * 
     * @return all children of this expression
     */
    public List<Expression> getChildren();

    // FIXME: Make sure this can now be deleted
    //    /**
    //     * Resets this expression and all its children state (set same state like before first evaluation).
    //     * @param context TODO
    //     */
    //    public void reset(ProcessingContext context);

    /**
     * Evaluates this expression.
     * <ol>
     * <li>evaluates all subexpressions (calls <code>evaluate</code> method for every subexpression)
     * <li>validates this expression (calls <code>validate</code> method)
     * <li>evaluates this expression (calls <code>evaluateSelf</code> method)
     * </ol>
     * 
     * @param context TODO
     * @return result of evaluation
     * @throws RuntimeValidationException
     */
    public Value evaluate(ProcessingContext context) throws RuntimeValidationException;

    /**
     * Returns true if evaluated result of this expression is NULL; false otherwise.
     * 
     * @param context TODO
     * @return true if evaluated result of this expression is NULL; false otherwise
     * @throws NullPointerException if this expression is not evaluated yet
     */
    public boolean isNull(ProcessingContext context) throws NullPointerException;

    /**
     * Gets cardinality of evaluated result.
     * 
     * @param context TODO
     * @return cardinality of evaluated result
     * @throws NullPointerException if this expression is not evaluated yet
     */
    public Cardinality getCardinality(ProcessingContext context) throws NullPointerException;

    /**
     * Gets baseType of evaluated result.
     * 
     * @param context TODO
     * @return baseType of evaluated result
     * @throws NullPointerException if this expression is not evaluated yet
     */
    public BaseType getBaseType(ProcessingContext context) throws NullPointerException;

    /**
     * Gets evaluated result or null if this expression is not evaluated yet.
     * 
     * @param context TODO
     * @return evaluated result or null if this expression is not evaluated yet
     */
    public Value getValue(ProcessingContext context);
}
