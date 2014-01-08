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
 * * Neither the localName of the University of Edinburgh nor the localNames of its
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
package uk.ac.ed.ph.jqtiplus.attribute.value;

import uk.ac.ed.ph.jqtiplus.attribute.SingleAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiAttributeException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.types.DataTypeBinder;

/**
 * Attribute with integer value.
 *
 * @author Jiri Kajaba
 */
public final class IntegerAttribute extends SingleAttribute<Integer> {

    private static final long serialVersionUID = 6169314176032331265L;

    public IntegerAttribute(final QtiNode parent, final String localName, final boolean required) {
        super(parent, localName, required);
    }

    public IntegerAttribute(final QtiNode parent, final String localName, final int defaultValue, final boolean required) {
        super(parent, localName, Integer.valueOf(defaultValue), required);
    }

    public IntegerAttribute(final QtiNode parent, final String localName, final String namespaceUri, final boolean required) {
        super(parent, localName, namespaceUri, null, required);
    }

    /**
     * Wrapper on {@link #getComputedValue()} that ensures that the result is non-null,
     * returning a primitive int.
     */
    public int getComputedNonNullValue() {
        final Integer computed = super.getComputedValue();
        if (computed==null) {
            throw new QtiAttributeException("Did not expect integer attribute '" + getLocalName() + "' to have a null computed value");
        }
        return computed.intValue();
    }

    @Override
    public Integer parseDomAttributeValue(final String domAttributeValue) {
        return Integer.valueOf(DataTypeBinder.parseInteger(domAttributeValue));
    }

    @Override
    public String toDomAttributeValue(final Integer value) {
        return value.toString();
    }
}
