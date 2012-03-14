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
package uk.ac.ed.ph.jqtiplus.attribute.enumerate;

import uk.ac.ed.ph.jqtiplus.attribute.EnumerateAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.SingleAttribute;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.ToleranceMode;

/**
 * Attribute with toleranceMode value.
 * 
 * @author Jiri Kajaba
 */
public class ToleranceModeAttribute extends SingleAttribute<ToleranceMode> implements EnumerateAttribute<ToleranceMode> {

    private static final long serialVersionUID = 1737056425484603310L;

    public ToleranceModeAttribute(XmlNode parent, String localName) {
        super(parent, localName);
    }

    public ToleranceModeAttribute(XmlNode parent, String localName, ToleranceMode defaultValue) {
        super(parent, localName, defaultValue);
    }

    @Override
    protected ToleranceMode parseValue(String value) {
        return ToleranceMode.parseToleranceMode(value);
    }

    /**
     * Gets all supported values of this attribute.
     * 
     * @return all supported values of this attribute
     */
    @Override
    public ToleranceMode[] getSupportedValues() {
        return ToleranceMode.values();
    }
}
