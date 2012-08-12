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
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;

/**
 * Represents the <code>integerOrVariableRef</code> data type
 *
 * @author David McKain
 */
public final class StringOrVariableRef implements Serializable {

    private static final long serialVersionUID = 5566424487613057171L;

    private final String stringValue;
    private final VariableReferenceIdentifier variableReferenceValue;
    private final String serializedValue;

    public StringOrVariableRef(final String stringValue) {
        Assert.ensureNotNull(stringValue);
        this.stringValue = stringValue;
        this.variableReferenceValue = null;
        this.serializedValue = "{" + stringValue + "}";
    }

    public StringOrVariableRef(final VariableReferenceIdentifier variableReferenceIdentifier) {
        Assert.ensureNotNull(variableReferenceIdentifier, "variableReferenceIdentifier");
        this.stringValue = null;
        this.variableReferenceValue = variableReferenceIdentifier;
        this.serializedValue = variableReferenceIdentifier.toString();
    }

    /**
     * @throws QtiParseException
     */
    public static StringOrVariableRef parseString(final String string) {
        Assert.ensureNotNull(string);

        if (string.isEmpty()) {
            throw new QtiParseException("stringOrVariableRef must not be empty");
        }
        if (string.charAt(0)=='{' && string.charAt(string.length()-1)=='}') {
            /* It's a variable reference */
            final VariableReferenceIdentifier variableReferenceIdentifier = new VariableReferenceIdentifier(string.substring(1, string.length()-1));
            return new StringOrVariableRef(variableReferenceIdentifier);
        }
        else {
            /* It's a string */
            return new StringOrVariableRef(string);
        }
    }

    public boolean isString() {
        return stringValue!=null;
    }

    public boolean isVariableRef() {
        return variableReferenceValue!=null;
    }

    public String getString() {
        return stringValue;
    }

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
        if (!(obj instanceof StringOrVariableRef)) {
            return false;
        }
        final StringOrVariableRef other = (StringOrVariableRef) obj;
        return serializedValue.equals(other.serializedValue);
    }

    public String evaluate(final ProcessingContext context) {
        if (isVariableRef()) {
            System.out.println("Looking up " + variableReferenceValue);
            final Value result = context.lookupVariableValue(variableReferenceValue);
            if (result.getCardinality()==Cardinality.SINGLE && result.getBaseType()==BaseType.STRING) {
                return ((StringValue) result).toQtiString();
            }
            throw new QtiEvaluationException("Variable referenced by " + variableReferenceValue + " was expected to be an integer");
        }
        else {
            return stringValue;
        }
    }
}
