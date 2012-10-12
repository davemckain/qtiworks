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

import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract super class for all expressions.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
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

    @Override
    public Cardinality[] getRequiredCardinalities(final ValidationContext context, final int index) {
        return getType().getRequiredCardinalities(index);
    }

    /**
     * Gets list of all acceptable cardinalities which can child expression at given position produce.
     * <p>
     * This method is used when same cardinality is required (contains, match).
     *
     * @param context TODO
     * @param index position of child expression in this parent
     * @param includeParent whether parent requirements should be used during calculation
     * @return list of all acceptable cardinalities which can child expression at given position produce
     * @see #getRequiredCardinalities
     */
    protected Cardinality[] getRequiredSameCardinalities(final ValidationContext context, final int index, final boolean includeParent) {
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
    public BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        return getType().getRequiredBaseTypes(index);
    }

    /**
     * Gets list of all acceptable baseTypes which can child expression at given position produce.
     * <p>
     * This method is used when same baseType is required (contains, delete, index, match, ...).
     *
     * @param context TODO
     * @param index position of child expression in this parent
     * @param includeParent whether parent requirements should be used during calculation
     * @return list of all acceptable baseTypes which can child expression at given position produce
     * @see #getRequiredBaseTypes
     */
    protected BaseType[] getRequiredSameBaseTypes(final ValidationContext context, final int index, final boolean includeParent) {
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
    public Cardinality[] getProducedCardinalities(final ValidationContext context) {
        return getType().getProducedCardinalities();
    }

    @Override
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
     * @param context TODO
     * @return list of all possible produced baseTypes after evaluation (possible baseTypes of evaluated result)
     * @see #getProducedBaseTypes
     */
    protected BaseType[] getProducedNumericalBaseTypes(final ValidationContext context) {
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
     * @param context TODO
     * @return list of all acceptable cardinalities for this expression from its parent
     */
    protected Cardinality[] getParentRequiredCardinalities(final ValidationContext context) {
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
     * @param context TODO
     * @return list of all acceptable baseTypes for this expression from its parent
     */
    protected BaseType[] getParentRequiredBaseTypes(final ValidationContext context) {
        if (getParent() != null) {
            final int index = getParent().getNodeGroups().getGroupSupporting(getQtiClassName()).getChildren().indexOf(this);
            return getParent().getRequiredBaseTypes(context, index);
        }
        return BaseType.values();
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        final Cardinality[] requiredCardinalities = getParentRequiredCardinalities(context);
        final Cardinality[] producedCardinalities = getProducedCardinalities(context);

        if (!check(requiredCardinalities, producedCardinalities)) {
            context.fireCardinalityValidationError(this, requiredCardinalities, producedCardinalities);
        }

        final BaseType[] requiredBaseTypes = getParentRequiredBaseTypes(context);
        final BaseType[] producedBaseTypes = getProducedBaseTypes(context);

        if (!check(requiredBaseTypes, producedBaseTypes)) {
            context.fireBaseTypeValidationError(this, requiredBaseTypes, producedBaseTypes);
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
    private boolean check(final Object[] required, final Object[] produced) {
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
    public List<Expression> getChildren() {
        return getNodeGroups().getExpressionGroup().getExpressions();
    }

    /**
     * Returns true if any subexpression is NULL; false otherwise.
     */
    protected boolean isAnyChildNull(final Value[] childValues) {
        for (final Value childValue : childValues) {
            if (childValue.isNull()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Evaluates this Expression.
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
    private Value evaluate(final ProcessingContext context, final int depth) {
        if (getChildren().size() > 0) {
            logger.debug("{}{}", formatIndent(depth), getClass().getSimpleName());
        }

        // (1) Evaluates all children (recursively)
        final List<Expression> children = getChildren();
        final Value[] childValues = new Value[children.size()];
        for (int i=0; i<children.size(); i++) {
            final Expression child = children.get(i);
            Value childValue;
            if (child instanceof AbstractExpression) {
                childValue = ((AbstractExpression) child).evaluate(context, depth + 1);
            }
            else {
                /* (This only happens if an extension implements Expression directly) */
                childValue = child.evaluate(context);
            }
            childValues[i] = childValue;
        }

// DM: I think we should be able to catch all validation errors before runtime now, but haven't implemented this yet.
// I have commented out the check here as the context interfaces aren't linked like they used to be.
//      // 2) Validates this expression (but not its children, since they will have been done in 1 above).
//        validateThis(context);

        // 3) Evaluates this expression.
        final Value value = evaluateSelf(context, childValues, depth);

        // Logs result of evaluation.
        final String format = "{}{} -> {}({})";
        final Object[] arguments = new Object[] { formatIndent(depth), getClass().getSimpleName(), value.getBaseType(), value };
        if (!(getParent() instanceof Expression)) {
            logger.debug(format, arguments);
        }
        else {
            logger.trace(format, arguments);
        }

        return value;
    }

    protected static String formatIndent(final int depth) {
        return "(" + depth + ") ";
    }

    /**
     * Evaluates this expression.
     *
     * @param childValues
     * @param depth depth of this expression in expression tree (root's depth = 0)
     *
     * @return result of evaluation, which must not be null
     */
    protected abstract Value evaluateSelf(ProcessingContext context, Value[] childValues, int depth);
}
