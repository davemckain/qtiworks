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

import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.validation.AttributeValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

/**
 * Node's attribute implementation.
 * 
 * @author Jiri Kajaba
 */
public abstract class AbstractAttribute<V> implements Attribute<V> {

    private static final long serialVersionUID = -3172377961902212482L;

    /** Owner of this attribute. */
    private final XmlNode owner;

    /** Name of this attribute. */
    private final String localName;
    
    private final String namespaceUri;
    
    /** Is this attribute mandatory (true) or optional (false). */
    private final boolean required;
    
    private boolean foreign;
    
    protected V value;
    protected V defaultValue;

    /**
     * (This constructor is useful for standard QTI attributes)
     */
    public AbstractAttribute(XmlNode owner, String localName, V value, V defaultValue, boolean required) {
        this(owner, localName, "", value, defaultValue, required, false);
    }

    /**
     * (This constructor is useful for foreign attributes)
     */
    public AbstractAttribute(XmlNode owner, String localName, String namespaceUri, V value,
            V defaultValue, boolean required, boolean foreign) {
        ConstraintUtilities.ensureNotNull(owner, "owner");
        ConstraintUtilities.ensureNotNull(localName, "localName");
        this.owner = owner;
        this.localName = localName;
        this.namespaceUri = namespaceUri!=null ? namespaceUri : "";
        this.defaultValue = defaultValue;
        this.value = value;
        this.required = required;
        this.foreign = foreign;
    }

    @Override
    public XmlNode getOwner() {
        return owner;
    }

    @Override
    public String getLocalName() {
        return localName;
    }
    
    @Override
    public String getNamespaceUri() {
        return namespaceUri;
    }
    
    @Override
    public boolean isForeign() {
        return foreign;
    }
    
    @Override
    public void setForeign(boolean foreign) {
        this.foreign = foreign;
    }

    @Override
    public V getDefaultValue() {
        return defaultValue;
    }
    
    @Override
    public V getValue() {
        return value;
    }
    
    @Override
    public String computeXPath() {
        return (owner != null ? owner.computeXPath() + "/" : "") 
                + "@"
                + ((namespaceUri!=null) ? "{" + namespaceUri + "}" : "")
                + localName;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(localName=" + localName
                + ",namespaceUri=" + namespaceUri
                + ",required=" + required
                + ",foreign=" + foreign
                + ",value=" + value
                + ",defaultValue=" + defaultValue
                + ")";
    }

    @Override
    @ToRefactor
    public final String toXmlString(boolean printDefaultValue) {
        final StringBuilder builder = new StringBuilder();

        final String value = valueToString();
        // if (value.length() == 0 && getLoadedValue() != null)
        // value = getLoadedValue();
        final String defaultValue = defaultValueToString();

        if (value.length() != 0 && (!value.equals(defaultValue) || required || printDefaultValue)) {
            builder.append(localName);
            builder.append("=\"");
            builder.append(AbstractNode.escapeForXmlString(value, true));
            builder.append("\"");
        }

        return builder.toString();
    }

    /** FIXME: Remove the xmlns stuff from this */
    @ToRefactor
    @Override
    public void validate(ValidationContext context) {
        if (!foreign) {
            // if (getLoadingProblem() != null)
            // result.add(new AttributeValidationError(this,
            // getLoadingProblem().getMessage()));
            // else
            System.out.println("TESTING: this=" + this + ",value=" + value);
            if (required && value==null) {
                context.add(new AttributeValidationError(this, "Required attribute is not defined: " + localName));
            }
        }
        else {
            /* FIXME: Kill this stuff here! */
            if (!(localName.startsWith("xmlns:") || localName.startsWith("xsi:") || localName.startsWith("xml:"))) {
                context.add(new ValidationWarning(this, "Unsupported attribute: " + localName));
            }
        }
    }
}
