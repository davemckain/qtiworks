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
import uk.ac.ed.ph.jqtiplus.node.expression.general.LookupExpression;

import java.io.Serializable;

/**
 * Special version of {@link Identifier} that allows period (.) characters in them.
 * <p>
 * These are used for prefixed variable references in {@link LookupExpression}s.
 *
 * @see Identifier
 *
 * @author David McKain
 */
public final class ComplexReferenceIdentifier implements Serializable, Comparable<ComplexReferenceIdentifier> {

    private static final long serialVersionUID = 1842878881636384148L;

    private final String value;
    private final boolean dotted;

    /**
     * Parses the given Identifier String, making sure it follows the required syntax.
     * <p>
     * Use this factory method for user-supplied identifiers.
     *
     * @throws QtiParseException if value is not a valid identifier
     */
    public static ComplexReferenceIdentifier parseString(final String value) {
        verifyIdentifier(value);
        return new ComplexReferenceIdentifier(value);
    }

    /**
     * Creates an {@link ComplexReferenceIdentifier} from the given String, without checking its syntax.
     * This should ONLY be used for identifiers that are known to be valid, such as the ones
     * defined in the QTI specification.
     */
    public static ComplexReferenceIdentifier assumedLegal(final String value) {
        return new ComplexReferenceIdentifier(value);
    }

    private ComplexReferenceIdentifier(final String value) {
        this.value = value;
        this.dotted = value.indexOf('.')!=-1;
    }

    public boolean isDotted() {
        return dotted;
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
        if (!(obj instanceof ComplexReferenceIdentifier)) {
            return false;
        }
        final ComplexReferenceIdentifier other = (ComplexReferenceIdentifier) obj;
        return value.equals(other.value);
    }

    @Override
    public int compareTo(final ComplexReferenceIdentifier other) {
        return value.compareTo(other.value);
    }

    private static void verifyIdentifier(final String value) {
        Assert.notNull(value);

        if (value.isEmpty()) {
            throw new QtiParseException("Invalid identifier '" + value + "': Must not be empty");
        }

        /* Check first character */
        if (!Character.isLetter(value.codePointAt(0)) && value.charAt(0) != '_') {
            throw new QtiParseException("Invalid identifier '" + value + "': First character '" + value.charAt(0) + "' is not valid");
        }

        /* Check remaining characters */
        for (int i = 1; i < value.length(); i++) {
            if (!Character.isLetterOrDigit(value.codePointAt(i)) && value.charAt(i) != '_' && value.charAt(i) != '-'
                    && value.charAt(i) != '.') {
                throw new QtiParseException("Invalid identifier '" + value + "': Character '" + value.charAt(i) + "' at position " + (i + 1) + " is not valid");
            }
        }
    }
}