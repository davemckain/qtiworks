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

import uk.ac.ed.ph.jqtiplus.ToRefactor;
import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract super class for all expressions.
 * <p>
 * Most expressions will want to override either {@link AbstractFunctionalExpression}
 * or {@link AbstractSimpleFunctionalExpression}.
 *
 * @author Jiri Kajaba (original)
 * @author David McKain (refactored)
 */
public abstract class AbstractExpression extends AbstractNode implements Expression {

    private static final long serialVersionUID = -9080931661495452921L;

    /** Expression logger. Used with all expressions. */
    private static Logger logger = LoggerFactory.getLogger(AbstractExpression.class);

    public AbstractExpression(final ExpressionParent parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getNodeGroups().add(new ExpressionGroup(this, getType().getMinimum(), getType().getMaximum()));
    }

    @Override
    public ExpressionParent getParent() {
        return (ExpressionParent) super.getParent();
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.getType(getQtiClassName());
    }

    @ToRefactor
    @Override
    public Cardinality[] getRequiredCardinalities(final ValidationContext context, final int index) {
        return getType().getRequiredCardinalities(index);
    }

    /**
     * Gets list of all acceptable cardinalities which can child expression at given position produce.
     * <p>
     * This method is used when same cardinality is required (contains, match).
     *
     * @param index position of child expression in this parent
     * @param includeParent whether parent requirements should be used during calculation
     * @return list of all acceptable cardinalities which can child expression at given position produce
     * @see #getRequiredCardinalities
     */
    @ToRefactor
    protected final Cardinality[] getRequiredSameCardinalities(final ValidationContext context, final int index, final boolean includeParent) {
        Cardinality[] required = getType().getRequiredCardinalities(index);

        if (includeParent) {
            required = Cardinality.intersection(required, getParentRequiredCardinalities(context));
        }

        for (int i = 0; i < index && i < getChildren().size(); i++) {
            final Expression child = getChildren().get(i);

            final Cardinality[] newRequired = Cardinality.intersection(required, child.getProducedCardinalities(context));
            if (newRequired.length == 0) {
                break;
            }

            required = newRequired;
        }

        return required;
    }

    @Override
    @ToRefactor
    public BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        return getType().getRequiredBaseTypes(index);
    }

    /**
     * Gets list of all acceptable baseTypes which can child expression at given position produce.
     * <p>
     * This method is used when same baseType is required (contains, delete, index, match, ...).
     *
     * @param index position of child expression in this parent
     * @param includeParent whether parent requirements should be used during calculation
     * @return list of all acceptable baseTypes which can child expression at given position produce
     * @see #getRequiredBaseTypes
     */
    @ToRefactor
    protected final BaseType[] getRequiredSameBaseTypes(final ValidationContext context, final int index, final boolean includeParent) {
        BaseType[] required = getType().getRequiredBaseTypes(index);

        if (includeParent) {
            required = BaseType.intersection(required, getParentRequiredBaseTypes(context));
        }

        for (int i = 0; i < index && i < getChildren().size(); i++) {
            final Expression child = getChildren().get(i);

            final BaseType[] newRequired = BaseType.intersection(required, child.getProducedBaseTypes(context));
            if (newRequired.length == 0) {
                break;
            }

            required = newRequired;
        }

        return required;
    }

    @Override
    @ToRefactor
    public Cardinality[] getProducedCardinalities(final ValidationContext context) {
        return getType().getProducedCardinalities();
    }

    @Override
    @ToRefactor
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        return getType().getProducedBaseTypes();
    }

    /**
     * Gets list of all possible produced baseTypes after evaluation (possible baseTypes of evaluated result).
     * <p>
     * This method is used in numerical expressions (product, subtract, sum).
     * <ol>
     * <li>if any of children doesn't produce integer nor float, result is empty set</li>
     * <li>if one of children produces only float, result is float</li>
     * <li>if none of all children produces float, result is integer</li>
     * <li>otherwise result is set of integer and float</li>
     * </ol>
     *
     * @return list of all possible produced baseTypes after evaluation (possible baseTypes of evaluated result)
     * @see #getProducedBaseTypes
     */
    @ToRefactor
    protected final BaseType[] getProducedNumericalBaseTypes(final ValidationContext context) {
        boolean floatFound = false;
        for (final Expression child : getChildren()) {
            final BaseType[] produced = child.getProducedBaseTypes(context);
            final boolean integerPresent = Arrays.binarySearch(produced, BaseType.INTEGER) >= 0;
            final boolean floatPresent = Arrays.binarySearch(produced, BaseType.FLOAT) >= 0;
            if (!integerPresent && !floatPresent) {
                return new BaseType[] {};
            }

            if (!integerPresent && floatPresent) {
                return new BaseType[] { BaseType.FLOAT };
            }

            if (floatPresent) {
                floatFound = true;
            }
        }

        return (getChildren().size() == 0 || floatFound) ? getType().getProducedBaseTypes() : new BaseType[] { BaseType.INTEGER };
    }

    /**
     * Gets list of all acceptable cardinalities for this expression from its parent.
     * <ol>
     * <li>evaluates index of this expression in parent</li>
     * <li>calls parent's <code>getRequiredCardinalities(index)</code> method</li>
     * </ol>
     * If this expression doesn't have any parent (it is legal for testing, but not for real use case),
     * returns list of all cardinalities.
     *
     * @return list of all acceptable cardinalities for this expression from its parent
     */
    protected final Cardinality[] getParentRequiredCardinalities(final ValidationContext context) {
        if (getParent() != null) {
            final int index = getParent().getNodeGroups().getGroupSupporting(getQtiClassName()).getChildren().indexOf(this);
            return getParent().getRequiredCardinalities(context, index);
        }
        return Cardinality.values();
    }

    /**
     * Gets list of all acceptable baseTypes for this expression from its parent.
     * <ol>
     * <li>evaluates index of this expression in parent</li>
     * <li>calls parent's <code>getRequiredBaseTypes(index)</code> method</li>
     * </ol>
     * If this expression doesn't have any parent (it is legal for testing, but not for real use case),
     * returns list of all baseTypes.
     *
     * @return list of all acceptable baseTypes for this expression from its parent
     */
    @ToRefactor
    protected final BaseType[] getParentRequiredBaseTypes(final ValidationContext context) {
        if (getParent() != null) {
            final int index = getParent().getNodeGroups().getGroupSupporting(getQtiClassName()).getChildren().indexOf(this);
            return getParent().getRequiredBaseTypes(context, index);
        }
        return BaseType.values();
    }

    public boolean isThisExpressionValid(final ValidationContext context) {
        context.setCheckpoint(NotificationLevel.ERROR);
        validateThis(context);
        return context.clearCheckpoint()==0;
    }

    /**
     * Partial implementation of {@link #validateThis(ValidationContext)} that checks the
     * input/output variable signatures.
     * <p>
     * Subclasses should override this to do additional validation.
     * <p>
     * NB: I'm not currently keen on the way this bit of logic works so it is subject to
     * change in future, but overridden methods in subclasses should be safe enough.
     */
    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        validateChildExpressionSignatures(this, context);
    }

    /**
     * Checks the produced {@link Signature}s of the children of the given {@link ExpressionParent}
     * and matches them against those required by the {@link ExpressionParent}. Any mismatches are
     * reported as validation errors.
     * <p>
     * This is a static helper method as this logic also needs to be used on the outer
     * {@link ExpressionParent}s that aren't themselves {@link Expression}, and single inheritance
     * makes this a bit messy.
     * <p>
     * This method is slightly refactored from the original JQTI, which reported validation errors
     * as if they were problem with the child {@link Expression}, rather than the parent {@link ExpressionParent}.
     * <p>
     * NOTE: I'm not currently keen on the produced/required logic in this class, but don't have
     * resources to refactor this any more at present!
     */
    public static void validateChildExpressionSignatures(final ExpressionParent expressionParent, final ValidationContext context) {
        final List<Expression> childExpressions = expressionParent.getExpressions();
        for (int index=0; index<childExpressions.size(); index++) {
            final Expression child = childExpressions.get(index);

            final Cardinality[] requiredCardinalities = expressionParent.getRequiredCardinalities(context, index);
            final Cardinality[] childProducedCardinalities = child.getProducedCardinalities(context);
            if (!checkCompatibility(requiredCardinalities, childProducedCardinalities)) {
                context.fireValidationError(expressionParent, "Child expression " + child.getQtiClassName()
                        + " at index " + index
                        + " is required to evaluate to a value with cardinalities "
                        + Arrays.toString(requiredCardinalities)
                        + " but produces cardinalities "
                        + Arrays.toString(childProducedCardinalities));
            }

            final BaseType[] requiredBaseTypes = expressionParent.getRequiredBaseTypes(context, index);
            final BaseType[] childProducedBaseTypes = child.getProducedBaseTypes(context);
            if (!checkCompatibility(requiredBaseTypes, childProducedBaseTypes)) {
                context.fireValidationError(expressionParent, "Child expression " + child.getQtiClassName()
                        + " at index " + index
                        + " is required to evaluate to a value with baseTypes "
                        + Arrays.toString(requiredBaseTypes)
                        + " but produces baseTypes "
                        + Arrays.toString(childProducedBaseTypes));
            }
        }
    }

    /**
     * Returns true if list of produced objects contains at least one object from list of required objects (or both
     * lists are empty); false otherwise.
     *
     * @param required list with required objects
     * @param produced list with produced objects
     * @return true if both lists are empty or intersection of these lists is not empty; false otherwise
     */
    private static boolean checkCompatibility(final Object[] required, final Object[] produced) {
        if (required.length == 0 && produced.length == 0) {
            return true;
        }
        for (final Object object : produced) {
            if (Arrays.binarySearch(required, object) >= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Expression> getExpressions() {
        return getNodeGroups().getExpressionGroup().getExpressions();
    }

    @Override
    public List<Expression> getChildren() {
        return getExpressions();
    }

    /**
     * Returns true if any subexpression is NULL; false otherwise.
     */
    protected static boolean isAnyChildNull(final Value[] childValues) {
        for (final Value childValue : childValues) {
            if (childValue.isNull()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Evaluates this Expression (and its children)
     */
    @Override
    public final Value evaluate(final ProcessingContext context) {
        final Value result = evaluate(context, 0);
        Assert.notNull(result, "result of evaluation");
        return result;
    }

    /**
     * Evaluates this expression and all its children.
     *
     * @param depth of this expression in expression tree (root's depth = 0)
     * @return result of evaluation
     * @see #evaluate(ProcessingContext)
     */
    protected Value evaluate(final ProcessingContext context, final int depth) {
        Value result;
        final boolean thisIsValid = context.isSubjectValid() || isThisExpressionValid(context);
        if (thisIsValid) {
            /* Expression is valid, so evaluate it */
            result =  evaluateValidSelfAndChildren(context, depth);
        }
        else {
            /* Expression is not valid, so register a warning and return NULL */
            context.fireRuntimeWarning(this, "Expression is not valid and will not be evaluated. Returning NULL instead");
            result = NullValue.INSTANCE;
        }

        /* Log result of evaluation. */
        if (logger.isTraceEnabled() || logger.isDebugEnabled()) {
            final String format = "{}{} -> {}({})";
            final Object[] arguments = new Object[] { formatIndent(depth), getClass().getSimpleName(), result.getBaseType(), result };
            if (!(getParent() instanceof Expression)) {
                logger.debug(format, arguments);
            }
            else {
                logger.trace(format, arguments);
            }
        }
        return result;
    }

    /**
     * Subclasses should fill in to evaluate the children and produce a result from this.
     *
     * @param context
     * @param depth
     * @return
     */
    protected abstract Value evaluateValidSelfAndChildren(final ProcessingContext context, final int depth);

    protected Value[] evaluateChildren(final ProcessingContext context, final int depth) {
        final List<Expression> children = getChildren();
        final Value[] childValues = new Value[children.size()];
        for (int i=0; i<children.size(); i++) {
            final Expression child = children.get(i);
            Value childValue;
            if (child instanceof AbstractFunctionalExpression) {
                childValue = ((AbstractFunctionalExpression) child).evaluate(context, depth + 1);
            }
            else {
                /* (This only happens if an extension implements Expression directly) */
                childValue = child.evaluate(context);
            }
            childValues[i] = childValue;
        }
        return childValues;
    }

    protected static String formatIndent(final int depth) {
        return "(" + depth + ") ";
    }

}
