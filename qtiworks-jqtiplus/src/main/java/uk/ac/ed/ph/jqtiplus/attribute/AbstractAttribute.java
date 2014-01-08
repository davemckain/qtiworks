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

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

/**
 * Node's attribute implementation.
 *
 * @author Jiri Kajaba
 */
public abstract class AbstractAttribute<V> implements Attribute<V> {

    private static final long serialVersionUID = -3172377961902212482L;

    /** Owner of this attribute. Must not be null. */
    protected final QtiNode owner;

    /** XML local name of this attribute. Must not be null. */
    protected final String localName;

    /** XML namespace URI for this attribute, with an empty String corresponding to "no namespace". Must not be null */
    protected final String namespaceUri;

    /** Is this attribute mandatory (true) or optional (false). */
    protected final boolean required;

    /** Attribute value (may be null) */
    protected V value;

    /** Attribute default value (may be null) */
    protected V defaultValue;

    public AbstractAttribute(final QtiNode owner, final String localName, final V defaultValue, final boolean required) {
        this(owner, localName, "", defaultValue, required);
    }

    public AbstractAttribute(final QtiNode owner, final String localName, final String namespaceUri, final V defaultValue, final boolean required) {
        Assert.notNull(owner, "owner");
        Assert.notNull(localName, "localName");
        Assert.notNull(namespaceUri, "namespaceUri");
        this.owner = owner;
        this.localName = localName;
        this.namespaceUri = namespaceUri;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    @Override
    public final QtiNode getOwner() {
        return owner;
    }

    @Override
    public final String getLocalName() {
        return localName;
    }

    @Override
    public final String getNamespaceUri() {
        return namespaceUri;
    }

    @Override
    public final boolean isRequired() {
        return required;
    }

    @Override
    public final boolean isSet() {
        return value!=null;
    }

    @Override
    public final V getDefaultValue() {
        return defaultValue;
    }

    @Override
    public final V getValue() {
        return value;
    }

    @Override
    public final void setValue(final V value) {
        this.value = value;
    }

    @Override
    public V getComputedValue() {
        return value!=null ? value : defaultValue;
    }

    @Override
    public final String computeXPath() {
        return (owner != null ? owner.computeXPath() + "/" : "")
                + "@"
                + (!namespaceUri.isEmpty() ? "{" + namespaceUri + "}" : "")
                + localName;
    }

    /**
     * Default implementation of attribute validation that simply
     * checks the value has been set if required.
     * <p>
     * Subclasses may choose to add additional validation of the
     * attribute.
     */
    @Override
    public void validateBasic(final ValidationContext context) {
        if (required && value==null) {
            context.fireAttributeValidationError(this, "Required attribute '" + localName + "' has not been assigned a value");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(localName=" + localName
                + ",namespaceUri=" + namespaceUri
                + ",required=" + required
                + ",defaultValue=" + defaultValue
                + ",value=" + value
                + ")";
    }
}
