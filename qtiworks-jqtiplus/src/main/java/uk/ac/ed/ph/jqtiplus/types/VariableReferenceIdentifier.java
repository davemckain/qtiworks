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

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.io.Serializable;

/**
 * FIXME: This needs to support double-dotted identifiers.
 *
 * Encapsulates an identifier used as a variable *reference*. This concept isn't made
 * explicit in the QTI specification.
 * <p>
 * Readers should note the potential complexities described in Section 15.1. Also note
 * that this section is ambiguous...
 *
 * @author David McKain
 */
@ToRefactor
public final class VariableReferenceIdentifier implements Serializable {

    private static final long serialVersionUID = 7038955921323452832L;

    private final String value;

    private final Identifier localIdentifier;

    private final Identifier assessmentItemRefIdentifier;

    private final Identifier assessmentItemItemVariableIdentifier;

    public static VariableReferenceIdentifier parseString(final String value) {
        return new VariableReferenceIdentifier(value);
    }

    /**
     * @throws QtiParseException if value is not a valid identifier (definition)
     */
    private VariableReferenceIdentifier(final String value) {
        Assert.notNull(value);

        if (value.isEmpty()) {
            throw new QtiParseException("Invalid identifier '" + value + "': Must not be empty");
        }

        /* First character. */
        if (!Character.isLetter(value.codePointAt(0)) && value.charAt(0) != '_') {
            throw new QtiParseException("Invalid reference identifier '" + value + "': First character '" + value.charAt(0) + "' is not valid");
        }

        /* Rest of characters. */
        int dotPos = -1;
        for (int i = 1; i < value.length(); i++) {
            if (value.charAt(i) == '.') {
                if (dotPos != -1) {
                    throw new QtiParseException("Invalid reference identifier '" + value + "': Only one period (.) character is allowed in this identifier");
                }
                dotPos = i;
            }
            else if (!Character.isLetterOrDigit(value.codePointAt(i)) && value.charAt(i) != '_' && value.charAt(i) != '-') {
                throw new QtiParseException("Invalid reference identifier '" + value + "': Character '" + value.charAt(i) + "' at position " + (i + 1)
                        + " is not valid");
            }
        }
        this.value = value;
        if (dotPos == -1) {
            this.localIdentifier = new Identifier(value);
            this.assessmentItemRefIdentifier = null;
            this.assessmentItemItemVariableIdentifier = null;
        }
        else {
            this.localIdentifier = null;
            this.assessmentItemRefIdentifier = new Identifier(value.substring(0, dotPos));
            this.assessmentItemItemVariableIdentifier = Identifier.parseString(value.substring(dotPos + 1));
        }
    }

    /**
     * @see Identifier#toVariableReferenceIdentifier()
     */
    public VariableReferenceIdentifier(final Identifier localIdentifier) {
        this.localIdentifier = localIdentifier;
        this.assessmentItemItemVariableIdentifier = null;
        this.assessmentItemRefIdentifier = null;
        this.value = localIdentifier.toString();
    }

    public VariableReferenceIdentifier(final Identifier assessmentItemRefIdentifier, final Identifier assessmentItemItemVariableIdentifier) {
        this.localIdentifier = null;
        this.assessmentItemRefIdentifier = assessmentItemRefIdentifier;
        this.assessmentItemItemVariableIdentifier = assessmentItemItemVariableIdentifier;
        this.value = assessmentItemRefIdentifier.toString() + "." + assessmentItemItemVariableIdentifier.toString();
    }

    public Identifier getLocalIdentifier() {
        return localIdentifier;
    }

    public Identifier getAssessmentItemRefIdentifier() {
        return assessmentItemRefIdentifier;
    }

    public Identifier getAssessmentItemItemVariableIdentifier() {
        return assessmentItemItemVariableIdentifier;
    }

    public boolean isDotted() {
        return localIdentifier==null;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof VariableReferenceIdentifier)) {
            return false;
        }
        final VariableReferenceIdentifier other = (VariableReferenceIdentifier) obj;
        return value.equals(other.value);
    }
}
