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
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;

/**
 * Represents the <code>floatOrVariableRef</code> data type
 *
 * @author David McKain
 */
public final class FloatOrVariableRef implements Serializable {

    private static final long serialVersionUID = 1215189767076373746L;

    private final FloatValue constantFloatValue;
    private final Identifier variableReferenceValue;
    private final String serializedValue;

    /**
     * Creates a new floatOrVariableRef holding the given float value
     */
    public FloatOrVariableRef(final double floatValue) {
        this.constantFloatValue = new FloatValue(floatValue);
        this.variableReferenceValue = null;
        this.serializedValue = Double.toString(floatValue);
    }

    /**
     * Creates a new floatOrVariableRef holding the given variable reference
     */
    public FloatOrVariableRef(final Identifier variableReferenceIdentifier) {
        Assert.notNull(variableReferenceIdentifier, "variableReferenceIdentifier");
        this.constantFloatValue = null;
        this.variableReferenceValue = variableReferenceIdentifier;
        this.serializedValue = variableReferenceIdentifier.toString();
    }

    /**
     * Parses a new floatOrVariableRef from the given String, as defined in the QTI spec.
     *
     * @throws QtiParseException
     */
    public static FloatOrVariableRef parseString(final String string) {
        Assert.notNull(string);

        if (string.isEmpty()) {
            throw new QtiParseException("floatOrVariableRef must not be empty");
        }
        try {
            /* Try to parse as a float */
            final double floatValue = DataTypeBinder.parseFloat(string);
            return new FloatOrVariableRef(floatValue);
        }
        catch (final QtiParseException e) {
            /* Try to parse as a variable reference */
            final Identifier variableReferenceIdentifier = Identifier.parseString(string);
            return new FloatOrVariableRef(variableReferenceIdentifier);
        }
    }

    /** Returns true if this instance holds an explicit float */
    public boolean isConstantFloat() {
        return variableReferenceValue==null;
    }

    /** Returns true of this instance holds a variable reference */
    public boolean isVariableRef() {
        return variableReferenceValue!=null;
    }

    /**
     * Returns the explicit float held by this instance,
     * returning null if this actually holds a variable reference.
     * (The caller should use {@link #isConstantFloat()} to check first.)
     */
    public FloatValue getConstantFloatValue() {
        return constantFloatValue;
    }

    /**
     * Returns the explicit variable reference identifier held by this instance,
     * returning null if this actually holds an float.
     * (The caller should use {@link #isVariableRef()} to check first.)
     */
    public Identifier getIdentifier() {
        return variableReferenceValue;
    }

    @Override
    public String toString() {
        return serializedValue;
    }

    @Override
    public int hashCode() {
        return serializedValue.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FloatOrVariableRef)) {
            return false;
        }
        final FloatOrVariableRef other = (FloatOrVariableRef) obj;
        return serializedValue.equals(other.serializedValue);
    }

    /**
     * Evaluates this holder. If this holds an explicit float then its value is returned as-is.
     * Otherwise, the given {@link ProcessingContext} is used to look up the value of the variable
     * that this type refers to. The result will either be an {@link FloatValue} or {@link NullValue}
     * <p>
     * If the variable cannot be successfully resolved or is of the wrong type then a runtime error
     * is recorded and a {@link NullValue} will be returned.
     */
    public Value evaluate(final Expression expression, final ProcessingContext context) {
        if (isConstantFloat()) {
            return constantFloatValue;
        }
        else {
            final Value result = context.evaluateVariableValue(variableReferenceValue, VariableType.TEMPLATE, VariableType.OUTCOME);
            if (result.hasSignature(Signature.SINGLE_FLOAT)) {
                return result;
            }
            else {
                context.fireRuntimeError(expression, "Variable referenced by "
                        + variableReferenceValue + " was expected to be a single float - returning NULL");
                return NullValue.INSTANCE;
            }
        }
    }

    /**
     * Wrapper for {@link #evaluate(Expression, ProcessingContext)} that substitutes a replacement value and emits a
     * runtime warning if the result was NULL.
     */
    public double evaluateNotNull(final Expression expression, final ProcessingContext context,
            final String messageOnNull, final double replacementOnNull) {
        final Value evaluated = evaluate(expression, context);
        double result;
        if (evaluated.isNull()) {
            context.fireRuntimeWarning(expression, messageOnNull);
            result = replacementOnNull;
        }
        else {
            result = ((FloatValue) evaluated).doubleValue();
        }
        return result;
    }
}
