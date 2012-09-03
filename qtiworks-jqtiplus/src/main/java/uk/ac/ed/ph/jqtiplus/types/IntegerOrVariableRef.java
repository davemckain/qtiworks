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
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;

/**
 * Represents the <code>integerOrVariableRef</code> data type
 *
 * @author David McKain
 */
public final class IntegerOrVariableRef implements Serializable {

    private static final long serialVersionUID = 1215189767076373746L;

    private final int integerValue;
    private final VariableReferenceIdentifier variableReferenceValue;
    private final String serializedValue;

    /**
     * Creates a new integerOrVariableRef holding the given integer value
     */
    public IntegerOrVariableRef(final int integerValue) {
        this.integerValue = integerValue;
        this.variableReferenceValue = null;
        this.serializedValue = Integer.toString(integerValue);
    }

    /**
     * Creates a new integerOrVariableRef holding the given variable reference
     */
    public IntegerOrVariableRef(final VariableReferenceIdentifier variableReferenceIdentifier) {
        Assert.ensureNotNull(variableReferenceIdentifier, "variableReferenceIdentifier");
        this.integerValue = 0;
        this.variableReferenceValue = variableReferenceIdentifier;
        this.serializedValue = variableReferenceIdentifier.toString();
    }

    /**
     * Parses a new integerOrVariableRef from the given String, as defined in the QTI spec.
     *
     * @throws QtiParseException
     */
    public static IntegerOrVariableRef parseString(final String string) {
        Assert.ensureNotNull(string);

        if (string.isEmpty()) {
            throw new QtiParseException("integerOrVariableRef must not be empty");
        }
        try {
            /* Try to parse as in integer */
            final int integer = DataTypeBinder.parseInteger(string);
            return new IntegerOrVariableRef(integer);
        }
        catch (final QtiParseException e) {
            /* Parse as a variable reference */
            final VariableReferenceIdentifier variableReferenceIdentifier = new VariableReferenceIdentifier(string);
            return new IntegerOrVariableRef(variableReferenceIdentifier);
        }
    }

    /** Returns true if this instance holds an explicit integer */
    public boolean isInteger() {
        return variableReferenceValue==null;
    }

    /** Returns true of this instance holds a variable reference */
    public boolean isVariableRef() {
        return variableReferenceValue!=null;
    }

    /**
     * Returns the explicit integer held by this instance,
     * returning 0 if this actually holds a variable reference.
     * (The caller should use {@link #isInteger()} to check first.)
     */
    public int getInteger() {
        return integerValue;
    }

    /**
     * Returns the explicit variable reference identifier held by this instance,
     *  returning null if this actually holds an integer.
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
        if (!(obj instanceof IntegerOrVariableRef)) {
            return false;
        }
        final IntegerOrVariableRef other = (IntegerOrVariableRef) obj;
        return serializedValue.equals(other.serializedValue);
    }

    /**
     * Evaluates this instance. If this holds an explicit integer then its value is returned as-is.
     * Otherwise, the given {@link ProcessingContext} is used to look up the value of the variable
     * that this type refers to. The result in all cases will be an integer.
     */
    public int evaluate(final ProcessingContext context) {
        if (isVariableRef()) {
            final Value result = context.lookupVariableValue(variableReferenceValue, VariableType.TEMPLATE, VariableType.OUTCOME);
            if (result.getCardinality()==Cardinality.SINGLE && result.getBaseType()==BaseType.INTEGER) {
                return ((IntegerValue) result).intValue();
            }
            throw new QtiEvaluationException("Variable referenced by " + variableReferenceValue + " was expected to be an integer");
        }
        else {
            return integerValue;
        }
    }
}
