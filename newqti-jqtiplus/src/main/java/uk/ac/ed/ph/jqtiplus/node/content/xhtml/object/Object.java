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

package uk.ac.ed.ph.jqtiplus.node.content.xhtml.object;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.ObjectFlowGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.XmlObject;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.InlineStatic;

import java.util.List;


/**
 * Contains : objectFlow [*]
 * 
 * Attribute : data [1]: string
 * The data attribute provides A URI for locating the data associated with the object.
 * 
 * Attribute : type [1]: mimeType
 * 
 * Attribute : width [0..1]: length
 * 
 * Attribute : height [0..1]: length
 * 
 * @author Jonathon Hare
 *
 */
public class Object extends BodyElement implements InlineStatic, FlowStatic {
    private static final long serialVersionUID = 1L;

    /** Name of this class in xml schema. */
    public static String CLASS_TAG = "object";
    
    /** Name of data attribute in xml schema. */
    public static final String ATTR_DATA_NAME = "data";

    /** Name of type attribute in xml schema. */
    public static final String ATTR_TYPE_NAME = "type";

    /** Name of width attribute in xml schema. */
    public static final String ATTR_WIDTH_NAME = "width";

    /** Name of height attribute in xml schema. */
    public static final String ATTR_HEIGHT_NAME = "height";

    /**
     * Constructs object.
     *
     * @param parent parent of constructed object
     */
    public Object(XmlObject parent) {
        super(parent);
        
        getAttributes().add(new StringAttribute(this, ATTR_DATA_NAME));
        getAttributes().add(new StringAttribute(this, ATTR_TYPE_NAME));
        getAttributes().add(new StringAttribute(this, ATTR_WIDTH_NAME, null, null, false));
        getAttributes().add(new StringAttribute(this, ATTR_HEIGHT_NAME, null, null, false));
        
        getNodeGroups().add(new ObjectFlowGroup(this));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public List<? extends XmlNode> getChildren() {
        return getNodeGroups().getObjectFlowGroup().getObjectFlows();
    }
    
    /**
     * Gets value of data attribute.
     *
     * @return value of data attribute
     * @see #setData
     */
    public String getData()
    {
        return getAttributes().getStringAttribute(ATTR_DATA_NAME).getValue();
    }

    /**
     * Sets new value of data attribute.
     *
     * @param data new value of data attribute
     * @see #getData
     */
    public void setData(String data)
    {
        getAttributes().getStringAttribute(ATTR_DATA_NAME).setValue(data);
    }
    
    /**
     * Gets value of type attribute.
     *
     * @return value of type attribute
     * @see #setType
     */
    public String getType()
    {
        return getAttributes().getStringAttribute(ATTR_TYPE_NAME).getValue();
    }

    /**
     * Sets new value of type attribute.
     *
     * @param type new value of type attribute
     * @see #getType
     */
    public void setType(String type)
    {
        getAttributes().getStringAttribute(ATTR_TYPE_NAME).setValue(type);
    }
    
    /**
     * Gets value of width attribute.
     *
     * @return value of width attribute
     * @see #setWidth
     */
    public String getWidth()
    {
        return getAttributes().getStringAttribute(ATTR_WIDTH_NAME).getValue();
    }

    /**
     * Sets new value of width attribute.
     *
     * @param width new value of width attribute
     * @see #getWidth
     */
    public void setWidth(String width)
    {
        getAttributes().getStringAttribute(ATTR_WIDTH_NAME).setValue(width);
    }
    
    /**
     * Gets value of height attribute.
     *
     * @return value of height attribute
     * @see #setHeight
     */
    public String getHeight()
    {
        return getAttributes().getStringAttribute(ATTR_HEIGHT_NAME).getValue();
    }

    /**
     * Sets new value of width attribute.
     *
     * @param height new value of height attribute
     * @see #getHeight
     */
    public void setHeight(String height)
    {
        getAttributes().getStringAttribute(ATTR_HEIGHT_NAME).setValue(height);
    }
}
