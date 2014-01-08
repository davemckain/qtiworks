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
package uk.ac.ed.ph.jqtiplus.attribute.value;

import uk.ac.ed.ph.jqtiplus.attribute.EnumerateAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.SingleAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiAttributeException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.types.DataTypeBinder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Attribute with boolean value.
 *
 * @author Jiri Kajaba
 */
public final class BooleanAttribute extends SingleAttribute<Boolean> implements EnumerateAttribute<Boolean> {

    private static final long serialVersionUID = -6757069543350955429L;

    /** Read-only view of all allowed values */
    private static final List<Boolean> valuesView = Collections.unmodifiableList(Arrays.asList(Boolean.TRUE, Boolean.FALSE));

    public BooleanAttribute(final QtiNode parent, final String localName, final boolean required) {
        super(parent, localName, required);
    }

    public BooleanAttribute(final QtiNode parent, final String localName, final boolean defaultValue, final boolean required) {
        super(parent, localName, Boolean.valueOf(defaultValue), required);
    }

    public BooleanAttribute(final QtiNode parent, final String localName, final String namespaceUri, final boolean defaultValue, final boolean required) {
        super(parent, localName, namespaceUri, Boolean.valueOf(defaultValue), required);
    }

    /**
     * Wrapper on {@link #getComputedValue()} that ensures that the result is non-null,
     * returning a raw boolean
     */
    public boolean getComputedNonNullValue() {
        final Boolean computed = super.getComputedValue();
        if (computed==null) {
            throw new QtiAttributeException("Did not expect boolean attribute " + getLocalName() + " to have a null computed value");
        }
        return computed.booleanValue();
    }

    @Override
    public List<Boolean> getSupportedValues() {
        return valuesView;
    }

    @Override
    public Boolean parseDomAttributeValue(final String domAttributeValue) {
        return Boolean.valueOf(DataTypeBinder.parseBoolean(domAttributeValue));
    }

    @Override
    public String toDomAttributeValue(final Boolean value) {
        return value.toString();
    }

}
