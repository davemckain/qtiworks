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
package uk.ac.ed.ph.jqtiplus.types;

import uk.ac.ed.ph.jqtiplus.exception.QtiEvaluationException;
import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
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

    private final double floatValue;
    private final VariableReferenceIdentifier variableReferenceValue;
    private final String serializedValue;

    /**
     * Creates a new floatOrVariableRef holding the given float value
     */
    public FloatOrVariableRef(final double floatValue) {
        this.floatValue = floatValue;
        this.variableReferenceValue = null;
        this.serializedValue = Double.toString(floatValue);
    }

    /**
     * Creates a new floatOrVariableRef holding the given variable reference
     */
    public FloatOrVariableRef(final VariableReferenceIdentifier variableReferenceIdentifier) {
        Assert.notNull(variableReferenceIdentifier, "variableReferenceIdentifier");
        this.floatValue = 0;
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
            final VariableReferenceIdentifier variableReferenceIdentifier = new VariableReferenceIdentifier(string);
            return new FloatOrVariableRef(variableReferenceIdentifier);
        }
    }

    /** Returns true if this instance holds an explicit float */
    public boolean isFloat() {
        return variableReferenceValue==null;
    }

    /** Returns true of this instance holds a variable reference */
    public boolean isVariableRef() {
        return variableReferenceValue!=null;
    }

    /**
     * Returns the explicit float held by this instance,
     * returning 0 if this actually holds a variable reference.
     * (The caller should use {@link #isFloat()} to check first.)
     */
    public double getDouble() {
        return floatValue;
    }

    /**
     * Returns the explicit variable reference identifier held by this instance,
     *  returning null if this actually holds an float.
     * (The caller should use {@link #isVariableRef()} to check first.)
     */
    public VariableReferenceIdentifier getVariableReferenceIdentifier() {
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
     * Evaluates this instance. If this holds an explicit float then its value is returned as-is.
     * Otherwise, the given {@link ProcessingContext} is used to look up the value of the variable
     * that this type refers to. The result in all cases will be a float.
     */
    public double evaluate(final ProcessingContext context) {
        if (isVariableRef()) {
            /* FIXME: What to do if we get NULL result? Is an Exception the best thing? */
            final Value result = context.lookupVariableValue(variableReferenceValue, VariableType.TEMPLATE, VariableType.OUTCOME);
            if (result.hasSignature(Signature.SINGLE_FLOAT)) {
                return ((FloatValue) result).doubleValue();
            }
            throw new QtiEvaluationException("Variable referenced by " + variableReferenceValue + " was expected to be float");
        }
        else {
            return floatValue;
        }
    }
}
