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
 * Implementation of <code>BaseType</code> pair value.
 * <p>
 * A space separated list of two identifier values.
 * <p>
 * Example values: 'A B', 'choice5 choice1', 'apple pear', 'carrot potato'. Character ' is not part
 * of pair value.
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single and <code>BaseType</code> is always pair.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @see uk.ac.ed.ph.jqtiplus.value.IdentifierValue
 * 
 * @author Jiri Kajaba
 */
public class PairValue extends AbstractPairValue {
    
    private static final long serialVersionUID = -9157898996344626699L;

    /**
     * Constructs <code>PairValue</code> from given pair of identifiers.
     *
     * @param sourceValue source (first) identifier
     * @param destValue destination (second) identifier
     */
    public PairValue(Identifier sourceValue, Identifier destValue) {
        super(sourceValue, destValue);
    }
    
    public PairValue(String sourceValue, String destValue) {
        super(sourceValue, destValue);
    }

    /**
     * Constructs <code>PairValue</code> from given <code>String</code> representation.
     *
     * @param value <code>String</code> representation of <code>PairValue</code>
     * @throws QTIParseException if <code>String</code> representation of <code>PairValue</code> is not valid
     */
    public PairValue(String value)
    {
        super(value);
    }

    public BaseType getBaseType()
    {
        return BaseType.PAIR;
    }

    @Override
    public boolean isDirected()
    {
        return false;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null || !getClass().equals(object.getClass()))
            return false;

        PairValue value = (PairValue) object;

        return (sourceValue.equals(value.sourceValue) && destValue.equals(value.destValue)) ||
                (sourceValue.equals(value.destValue) && destValue.equals(value.sourceValue));
    }
}
