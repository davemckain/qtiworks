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
package uk.ac.ed.ph.jqtiplus.value;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

/**
 * Superclass for pair and directedPair values.
 * <p>
 * Classes <code>PairValue</code> and <code>DirectedPairValue</code> have different only isDirected and equals methods.
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single.
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @author Jiri Kajaba
 */
public abstract class AbstractPairValue extends SingleValue {

    private static final long serialVersionUID = -4423395166584360682L;

    /** Source value. */
    protected Identifier sourceValue;

    /** Destination value. */
    protected Identifier destValue;

    /**
     * Constructs <code>AbstractPairValue</code> from given pair of identifiers.
     * 
     * @param sourceValue source (first) identifier
     * @param destValue destination (second) identifier
     */
    public AbstractPairValue(Identifier sourceValue, Identifier destValue) {
        this.sourceValue = sourceValue;
        this.destValue = destValue;
    }

    /**
     * @throws QtiParseException if either value is not a valid identifier
     */
    public AbstractPairValue(String sourceValue, String destValue) {
        this.sourceValue = new Identifier(sourceValue);
        this.destValue = new Identifier(destValue);
    }

    /**
     * Constructs <code>AbstractPairValue</code> from given <code>String</code> representation.
     * 
     * @param value <code>String</code> representation of <code>AbstractPairValue</code>
     * @throws QtiParseException if <code>String</code> representation of <code>AbstractPairValue</code> is not valid
     */
    public AbstractPairValue(String value) {
        if (value != null) {
            value = value.trim();
        }

        if (value == null || value.length() == 0) {
            throw new QtiParseException("Invalid pair '" + value + "'. Length is not valid.");
        }

        if (!value.equals(value.trim())) {
            throw new QtiParseException("Invalid pair '" + value + "'.");
        }

        final String[] parts = value.split(" ");
        if (parts.length != 2) {
            throw new QtiParseException("Invalid pair '" + value + "'. Number of parts is not valid.");
        }

        try {
            this.sourceValue = new Identifier(parts[0]);
            this.destValue = new Identifier(parts[1]);
        }
        catch (final QtiParseException ex) {
            throw new QtiParseException("Invalid pair '" + value + "'.", ex);
        }
    }

    /**
     * Returns true if this pair is directed false otherwise.
     * 
     * @return true if this pair is directed false otherwise
     */
    public abstract boolean isDirected();

    /**
     * Returns source (first) identifier.
     * 
     * @return source (first) identifier
     */
    public final Identifier sourceValue() {
        return sourceValue;
    }

    /**
     * Returns destination (second) identifier.
     * 
     * @return destination (second) identifier
     */
    public final Identifier destValue() {
        return destValue;
    }

    @Override
    public final String toString() {
        return sourceValue + " " + destValue;
    }
}
