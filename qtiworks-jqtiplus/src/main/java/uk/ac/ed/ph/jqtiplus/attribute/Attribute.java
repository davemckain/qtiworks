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
package uk.ac.ed.ph.jqtiplus.attribute;

import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;

/**
 * Represents an attribute of a {@link QtiNode}.
 *
 * @param <V> the type of value encoded by this Attribute. (Note that V does not usually correspond to the
 * {@link Value} class hierarchy.)
 *
 * @author Jiri Kajaba
 */
public interface Attribute<V> extends Serializable {

    /**
     * Gets the {@link QtiNode} owning this attribute.
     *
     * NB: This was previously called getParent()
     *
     * @return parent node of attribute
     */
    QtiNode getOwner();

    /**
     * Gets XML local name of attribute.
     *
     * @return name of attribute
     */
    String getLocalName();

    /**
     * Gets the namespace URI for this attribute, with an empty String corresponding to
     * "no namespace".
     */
    String getNamespaceUri();

    /**
     * Computes a pseudo XPath expression for this Attribute.
     *
     * NOTE: This uses the form {nsURI}localName for non-QTI elements, so is not
     * a "proper" XPath.
     */
    String computeXPath();

    /**
     * Returns true if attribute is mandatory; false otherwise (attribute is
     * optional).
     *
     * @return true if attribute is mandatory; false otherwise (attribute is
     *         optional)
     */
    boolean isRequired();

    /**
     * Returns true if this attribute's value has been explicitly set.
     * <p>
     * This is equivalent to {@link #getValue()} returning non-null.
     */
    boolean isSet();

    /**
     * Returns the default value of the attribute, which is the effective value used if the
     * attribute has not been explicitly set. This will be null if there is no default value.
     * The result of this should be assumed immutable.
     */
    V getDefaultValue();

    /**
     * Gets the explicitly-set value of this attribute, returning null if this
     * attribute has not been explicitly set. The value should be assumed immutable.
     * <p>
     * In JQTI+, this will return null if the value has not been explicitly set. This is
     * different from the original JQTI behaviour!
     *
     * @see #getComputedValue()
     *
     * @return value of attribute
     */
    V getValue();

    /**
     * Sets the value of this attribute.
     *
     * @param value new value of attribute, which may be null to indicate that the attribute's
     * value should be unset.
     */
    void setValue(V value);

    /**
     * Gets the "computed" value of this attribute, which is defined to be the
     * explicitly-set value (if not null), or the default value.
     * <p>
     * Note that if there is no default value, then this will return null.
     * <p>
     * (This method is new in JQTI+. The original JQTI did not differentiate between
     * whether an attribute was explicitly set or reverted to default.)
     *
     * @see #getValue()
     * @see #getDefaultValue()
     */
    V getComputedValue();

    /**
     * Validates this attribute's value to ensure it fits the general
     * restrictions placed on the attribute itself, such as whether
     * a value has been specified.
     * <p>
     * It should *not* be used for additional node-specific validation,
     * such as checking whether a value is positive etc.
     */
    void validateBasic(ValidationContext context);

    /**
     * Parses value from given DOM attribute value
     *
     * @param domAttributeValue DOM attribute value, which must not be null
     * @return parsed value
     */
    V parseDomAttributeValue(String domAttributeValue);

    /**
     * Converts the given value to a corresponding DOM attribute string value,
     * which will not be null.
     *
     * @param value value to convert
     * @return DOM attribute value, which will not be null but may be an empty string
     */
    String toDomAttributeValue(V value);
}
