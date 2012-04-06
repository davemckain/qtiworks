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
package uk.ac.ed.ph.jqtiplus.attribute.value;

import uk.ac.ed.ph.jqtiplus.attribute.SingleAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;

/**
 * Attribute with long value.
 * 
 * @author Jiri Kajaba
 */
public final class LongAttribute extends SingleAttribute<Long> {

    private static final long serialVersionUID = 1138880928751132617L;

    public LongAttribute(XmlNode parent, String localName, boolean required) {
        super(parent, localName, required);
    }

    public LongAttribute(XmlNode parent, String localName, Long defaultValue, boolean required) {
        super(parent, localName, defaultValue, required);
    }

    @Override
    protected Long parseQtiString(String value) {
        if (value == null || value.length() == 0) {
            throw new QtiParseException("Invalid long '" + value + "'. Length is not valid.");
        }

        final String originalValue = value;

        // Removes + sign because of Long.parseLong cannot handle it.
        if (value.startsWith("+")) {
            value = value.substring(1);
            if (value.length() == 0 || !Character.isDigit(value.codePointAt(0))) {
                throw new QtiParseException("Invalid long '" + originalValue + "'.");
            }
        }

        try {
            return Long.parseLong(value);
        }
        catch (final NumberFormatException ex) {
            throw new QtiParseException("Invalid long '" + originalValue + "'.", ex);
        }
    }
    
    @Override
    protected String toQtiString(Long value) {
        return value.toString();
    }
}
