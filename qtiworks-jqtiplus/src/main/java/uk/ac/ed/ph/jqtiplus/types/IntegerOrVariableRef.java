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
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;

/**
 * FIXME: Document this type!
 *
 * @author David McKain
 */
public final class IntegerOrVariableRef implements Serializable {

    private static final long serialVersionUID = 1215189767076373746L;

    private final Integer integerValue;
    private final VariableReferenceIdentifier variableReferenceValue;
    private final String stringValue;

    public IntegerOrVariableRef(final int integerValue) {
        this.integerValue = Integer.valueOf(integerValue);
        this.variableReferenceValue = null;
        this.stringValue = Integer.toString(integerValue);
    }

    public IntegerOrVariableRef(final VariableReferenceIdentifier variableReferenceIdentifier) {
        Assert.ensureNotNull(variableReferenceIdentifier, "variableReferenceIdentifier");
        this.integerValue = null;
        this.variableReferenceValue = variableReferenceIdentifier;
        this.stringValue = variableReferenceIdentifier.toString();
    }

    /**
     * @throws QtiParseException
     */
    public static IntegerOrVariableRef parseString(final String string) {
        Assert.ensureNotNull(string);

        if (string.isEmpty()) {
            throw new QtiParseException("IntegerOrVariableRef must not be empty");
        }
        final char firstCharacter = string.charAt(0);
        if (firstCharacter>='0' && firstCharacter<='9') {
            /* It's an integer */
            final int integer = DataTypeBinder.parseInteger(string);
            return new IntegerOrVariableRef(integer);
        }
        else {
            /* It must be a variable reference */
            final VariableReferenceIdentifier variableReferenceIdentifier = new VariableReferenceIdentifier(string);
            return new IntegerOrVariableRef(variableReferenceIdentifier);
        }
    }

    public boolean isInteger() {
        return integerValue!=null;
    }

    public boolean isVariableRef() {
        return variableReferenceValue!=null;
    }

    public Integer getInteger() {
        return integerValue;
    }

    public VariableReferenceIdentifier getVariableReferenceIdentifier() {
        return variableReferenceValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    @Override
    public int hashCode() {
        return stringValue.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof IntegerOrVariableRef)) {
            return false;
        }
        final IntegerOrVariableRef other = (IntegerOrVariableRef) obj;
        return stringValue.equals(other.stringValue);
    }

    public int evaluate(final ProcessingContext context) {
        if (isVariableRef()) {
            final Value result = context.lookupVariableValue(variableReferenceValue);
            if (result instanceof IntegerValue) {
                return ((IntegerValue) result).intValue();
            }
            throw new QtiEvaluationException("Variable referenced by " + variableReferenceValue + " was expected to be an integer");
        }
        else {
            return integerValue.intValue();
        }
    }
}
