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

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

/**
 * Encapsulates the special case of QTI "identifiers" that may contain a single
 * period character within them, which is used in test outcome processing to
 * refer to variables within particular items.
 * 
 * @see VariableReferenceIdentifier
 * @author David McKain
 */
public final class VariableReferenceIdentifier {

    private final String value;

    private final Identifier localIdentifier;

    private final Identifier assessmentItemRefIdentifier;

    private final Identifier assessmentItemItemVariableIdentifier;

    /**
     * @throws QTIParseException if value is not a valid identifier (definition)
     */
    public VariableReferenceIdentifier(String value) {
        if (value != null) {
            value = value.trim();
        }

        if (value == null || value.length() == 0) {
            throw new QTIParseException("Invalid reference identifier '" + value + "'. Must not be empty or blank.");
        }

        /* First character. */
        if (!Character.isLetter(value.codePointAt(0)) && value.charAt(0) != '_') {
            throw new QTIParseException("Invalid reference identifier '" + value + "'. First character '" + value.charAt(0) + "' is not valid.");
        }

        /* Rest of characters. */
        int dotPos = -1;
        for (int i = 1; i < value.length(); i++) {
            if (value.charAt(i) == '.') {
                if (dotPos != -1) {
                    throw new QTIParseException("Invalid reference identifier '" + value + "'. Only one period (.) character is allowed in this identifier.");
                }
                dotPos = i;
            }
            else if (!Character.isLetterOrDigit(value.codePointAt(i)) && value.charAt(i) != '_' && value.charAt(i) != '-') {
                throw new QTIParseException("Invalid reference identifier '" + value + "'. Character '" + value.charAt(i) + "' at position " + (i + 1)
                        + " is not valid.");
            }
        }
        this.value = value;
        if (dotPos == -1) {
            this.localIdentifier = new Identifier(value, false);
            this.assessmentItemRefIdentifier = null;
            this.assessmentItemItemVariableIdentifier = null;
        }
        else {
            this.localIdentifier = null;
            this.assessmentItemRefIdentifier = new Identifier(value.substring(0, dotPos), false);
            this.assessmentItemItemVariableIdentifier = new Identifier(value.substring(dotPos + 1));
        }
    }

    /**
     * @see Identifier#toVariableReferenceIdentifier()
     */
    public VariableReferenceIdentifier(Identifier localIdentifier) {
        this.localIdentifier = localIdentifier;
        this.assessmentItemItemVariableIdentifier = null;
        this.assessmentItemRefIdentifier = null;
        this.value = localIdentifier.toString();
    }
    
    public VariableReferenceIdentifier(Identifier assessmentItemRefIdentifier, Identifier assessmentItemItemVariableIdentifier) {
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
    public boolean equals(Object obj) {
        if (!(obj instanceof VariableReferenceIdentifier)) {
            return false;
        }
        final VariableReferenceIdentifier other = (VariableReferenceIdentifier) obj;
        return value.equals(other.value);
    }
}
