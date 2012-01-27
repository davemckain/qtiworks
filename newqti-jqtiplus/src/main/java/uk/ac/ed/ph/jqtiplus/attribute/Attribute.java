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

import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.validation.Validatable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Node's attribute interface.
 * 
 * @author Jiri Kajaba
 */
public interface Attribute extends Validatable {

    /**
     * Gets parent node of attribute.
     * 
     * @return parent node of attribute
     */
    public XmlNode getParent();

    /**
     * Gets name of attribute.
     * 
     * @return name of attribute
     */
    public String getName();

    public String computeXPath();

    /**
     * Returns true if attribute is mandatory; false otherwise (attribute is
     * optional).
     * 
     * @return true if attribute is mandatory; false otherwise (attribute is
     *         optional)
     */
    public boolean isRequired();

    /**
     * Returns true if attribute is supported; false otherwise (attribute is not
     * supported).
     * This approach enables to load test even if there are unknown (not
     * supported) attributes.
     * 
     * @return true if attribute is supported; false otherwise (attribute is not
     *         supported)
     */
    public boolean isSupported();

    /**
     * Sets whenever this attribute is supported or not.
     * 
     * @param supported if true this attribute is supported; otherwise this
     *            attribute is unsupported
     */
    public void setSupported(boolean supported);

    /**
     * Loads attribute's value from given source node.
     * Source node must contain attributes (one of them can be this attribute).
     * 
     * @param node source node
     */
    public void load(Element owner, Node node, LoadingContext context);

    /**
     * Loads attribute's value from given source string.
     * Source string must contain only attribute's value, nothing else.
     * 
     * @param value source string
     */
    public void load(Element owner, String value, LoadingContext context);

    /**
     * Gets attribute converted to string (name="value").
     * If value is not defined or is same as defaultValue (and printDefaultValue
     * is false),
     * returns empty (but not null) string.
     * 
     * @param printDefaultValue if true, default value is printed; otherwise
     *            default value is not printed
     * @return attribute converted to string (name="value")
     */
    public String toXmlString(boolean printDefaultValue);

    /**
     * Gets attribute's value converted to string.
     * If value is not defined, returns empty (but not null) string.
     * 
     * @return attribute's value converted to string
     */
    public String valueToString();

    /**
     * Gets attribute's defaultValue converted to string.
     * If defaultValue is not defined, returns empty (but not null) string.
     * 
     * @return attribute's defaultValue converted to string
     */
    public String defaultValueToString();
}
