/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.attribute;


import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.validation.AttributeValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Node's attribute implementation.
 * 
 * @author Jiri Kajaba
 */
public abstract class AbstractAttribute implements Attribute
{
    private static final long serialVersionUID = 1L;

    /** Logger. */
    protected static Logger logger = LoggerFactory.getLogger(Attribute.class);

    /** Parent of this attribute. */
    private XmlNode parent;

    /** Name of this attribute. */
    private String name;

    /** Is this attribute mandatory (true) or optional (false). */
    private boolean required;

    /** Is this attribute supported (true) or not supported (false). */
    private boolean supported;

//    /**
//     * Loaded value.
//     *
//     * @see #getLoadedValue()
//     */
//    private String loadedValue;
//
//    /**
//     * Loading problem.
//     *
//     * @see #getLoadingProblem()
//     */
//    private QTIParseException loadingProblem;

    /**
     * Constructs attribute.
     *
     * @param parent parent of constructed attribute
     * @param name name of constructed attribute
     * @param required if true this attribute is required; otherwise this attribute is optional
     * @param supported if true this attribute is supported; otherwise this attribute is unsupported
     */
    public AbstractAttribute(XmlNode parent, String name, boolean required, boolean supported)
    {
        this.parent = parent;
        this.name = name;
        this.required = required;
        this.supported = supported;
    }

    public XmlNode getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }

    public String computeXPath()
    {
        return (parent!=null ? parent.computeXPath() + "/" : "") + "@" + name;
    }

    public boolean isRequired()
    {
        return required;
    }

    public boolean isSupported()
    {
        return supported;
    }

    /**
     * Sets whenever this attribute is supported or not.
     *
     * @param supported if true this attribute is supported; otherwise this attribute is unsupported
     */
    public void setSupported(boolean supported)
    {
        this.supported = supported;
    }

//    public String getLoadedValue()
//    {
//        return loadedValue;
//    }
//
//    /**
//     * Sets new loaded value.
//     *
//     * @param loadedValue new loaded value
//     * @see #getLoadedValue
//     */
//    protected void setLoadedValue(String loadedValue)
//    {
//        this.loadedValue = loadedValue;
//    }
//
//    public QTIParseException getLoadingProblem()
//    {
//        return loadingProblem;
//    }
//
//    /**
//     * Sets new loading problem.
//     *
//     * @param loadingProblem loading problem.
//     * @see #getLoadingProblem
//     */
//    protected void setLoadingProblem(QTIParseException loadingProblem)
//    {
//        this.loadingProblem = loadingProblem;
//    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(name);
        builder.append("(");
        builder.append("value = \"");
        builder.append(valueToString());
        builder.append("\"");
        if (!isRequired())
        {
            builder.append(", defaultValue = \"");
            builder.append(defaultValueToString());
            builder.append("\"");
        }
        builder.append(")");

        return builder.toString();
    }

    public String toXmlString(boolean printDefaultValue)
    {
        StringBuilder builder = new StringBuilder();

        String value = valueToString();
//        if (value.length() == 0 && getLoadedValue() != null)
//            value = getLoadedValue();
        String defaultValue = defaultValueToString();

        if (value.length() != 0 && (!value.equals(defaultValue) || required || printDefaultValue))
        {
            builder.append(name);
            builder.append("=\"");
            builder.append(AbstractNode.escapeForXmlString(value, true));
            builder.append("\"");
        }

        return builder.toString();
    }

    public void validate(ValidationContext context, ValidationResult result) {
        if (supported) {
//            if (getLoadingProblem() != null)
//                result.add(new AttributeValidationError(this, getLoadingProblem().getMessage()));
//            else
            if (required && valueToString().length() == 0)
                result.add(new AttributeValidationError(this, "Required attribute is not defined: " + name));
        }
        else {
            if (!(name.startsWith("xmlns:") || name.startsWith("xsi:") || name.startsWith("xml:")))
                result.add(new ValidationWarning(this, "Unsupported attribute: " + name));
        }
    }
}
