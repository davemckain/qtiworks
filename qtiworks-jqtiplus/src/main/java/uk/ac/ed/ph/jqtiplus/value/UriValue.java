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
package uk.ac.ed.ph.jqtiplus.value;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.types.DataTypeBinder;

import java.net.URI;

/**
 * Implementation of <code>BaseType</code> uri value.
 * <p>
 * Bound using the XML schema any URI type.
 * <p>
 * This class uses implementation of stadard java URI class.
 * <p>
 * Example values: 'http://www.example.com/', 'images/icon.gif', 'mailto:user@example.com'. Character ' is not part of uri value.
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single and <code>BaseType</code> is always uri.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @see java.net.URI
 * @author Jiri Kajaba
 */
public final class UriValue extends SingleValue {

    private static final long serialVersionUID = -748965776874283350L;

    private final URI uriValue;

    public UriValue(final URI value) {
        Assert.notNull(value);
        this.uriValue = value;
    }

    /**
     * Constructs <code>UriValue</code> from given <code>String</code> representation.
     *
     * @param value <code>String</code> representation of <code>UriValue</code>
     * @throws QtiParseException if <code>String</code> representation of <code>UriValue</code> is not valid
     */
    public UriValue(final String value) {
        this.uriValue = DataTypeBinder.parseUri(value);
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.URI;
    }

    /**
     * Returns the value of this <code>UriValue</code> as a <code>URI</code>.
     *
     * @return the value of this <code>UriValue</code> as a <code>URI</code>
     */
    public URI uriValue() {
        return uriValue;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof UriValue)) {
            return false;
        }

        final UriValue other = (UriValue) object;
        return uriValue.equals(other.uriValue);
    }

    @Override
    public int hashCode() {
        return uriValue.hashCode();
    }

    @Override
    public String toQtiString() {
        return DataTypeBinder.toString(uriValue);
    }
}
