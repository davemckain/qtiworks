/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.value;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.types.Identifier;


/**
 * Implementation of <code>BaseType</code> directedPair value.
 * <p>
 * A space separated list of two identifier values with the source identifier first, followed by the
 * destination identifier.
 * <p>
 * Example values: 'A B', 'choice5 choice1', 'apple fruit', 'carrot vegetable'. Character ' is not part
 * of directedPair value.
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single and <code>BaseType</code> is always directedPair.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @see uk.ac.ed.ph.jqtiplus.value.IdentifierValue
 * 
 * @author Jiri Kajaba
 */
public class DirectedPairValue extends AbstractPairValue {
    
    private static final long serialVersionUID = -5296923514616265943L;

    /**
     * Constructs <code>DirectedPairValue</code> from given pair of identifiers.
     *
     * @param sourceValue source (first) identifier
     * @param destValue destination (second) identifier
     */
    public DirectedPairValue(Identifier sourceValue, Identifier destValue) {
        super(sourceValue, destValue);
    }
    
    public DirectedPairValue(String sourceValue, String destValue) {
        super(sourceValue, destValue);
    }

    /**
     * Constructs <code>DirectedPairValue</code> from given <code>String</code> representation.
     *
     * @param value <code>String</code> representation of <code>DirectedPairValue</code>
     * @throws QTIParseException if <code>String</code> representation of <code>DirectedPairValue</code> is not valid
     */
    public DirectedPairValue(String value)
    {
        super(value);
    }

    public BaseType getBaseType()
    {
        return BaseType.DIRECTED_PAIR;
    }

    @Override
    public boolean isDirected()
    {
        return true;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null || !getClass().equals(object.getClass()))
            return false;

        DirectedPairValue value = (DirectedPairValue) object;

        return sourceValue.equals(value.sourceValue) && destValue.equals(value.destValue);
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
