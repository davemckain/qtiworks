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
package uk.ac.ed.ph.jqtiplus.attribute;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Implementation of attribute with single value (normal attribute).
 * 
 * @author Jiri Kajaba
 */
public abstract class SingleAttribute<V> extends AbstractAttribute<V> {

    private static final long serialVersionUID = 7394997591576564116L;

    public SingleAttribute(XmlNode owner, String localName) {
        super(owner, localName, null, null, true);
    }
    
    public SingleAttribute(XmlNode owner, String localName, boolean required) {
        super(owner, localName, null, null, required);
    }

    public SingleAttribute(XmlNode owner, String localName, String namespaceUri) {
        super(owner, localName, namespaceUri, null, null, true);
    }

    public SingleAttribute(XmlNode owner, String localName, V defaultValue) {
        super(owner, localName, defaultValue, defaultValue, false);
    }

    public SingleAttribute(XmlNode owner, String localName, V defaultValue, V value, boolean required) {
        super(owner, localName, defaultValue, value, required);
    }
    
    public SingleAttribute(XmlNode owner, String localName, String namespaceUri, V defaultValue,  V value, boolean required) {
        super(owner, localName, namespaceUri, defaultValue, value, required);
    }
    
    @Override
    public final void load(Element owner, Node node, LoadingContext context) {
        load(owner, node.getNodeValue(), context);
    }

    @Override
    public final void load(Element owner, String value, LoadingContext context) {
        if (value != null) {
            try {
                this.value = parseQtiString(value);
            }
            catch (final QtiParseException ex) {
                this.value = null;
                context.modelBuildingError(ex, owner);
            }
        }
        else {
            this.value = null;
        }
    }

    @Override
    public final String valueToQtiString() {
        return value != null ? toQtiString(value) : "";
    }

    @Override
    public final String defaultValueToQtiString() {
        return defaultValue != null ? toQtiString(defaultValue) : "";
    }

}
