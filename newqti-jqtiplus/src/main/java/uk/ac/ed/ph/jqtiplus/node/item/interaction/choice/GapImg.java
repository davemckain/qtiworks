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

package uk.ac.ed.ph.jqtiplus.node.item.interaction.choice;

import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.ObjectGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object;

import java.util.List;


/**
 * A gap image contains a single image object to be inserted into a gap by the candidate.
 * 
 * Attribute : objectLabel [0..1]: string
 * An optional label for the image object to be inserted.
 * 
 * Contains : object [1]
 * 
 * @author Jonathon Hare
 *
 */
public class GapImg extends GapChoice {
    private static final long serialVersionUID = 1L;

    /** Name of this class in xml schema. */
    public static String CLASS_TAG = "gapImg";
    
    /** Name of objectLabel attribute in xml schema. */
    public static String ATTR_OBJECT_LABEL_NAME = "objectLabel";
    
    /**
     * Constructs object.
     *
     * @param parent parent of constructed object
     */
    public GapImg(XmlNode parent) {
        super(parent);
        
        getAttributes().add(new IntegerAttribute(this, ATTR_OBJECT_LABEL_NAME, null, null, false));
        
        getNodeGroups().add(new ObjectGroup(this, true));
    }

    /**
     * Gets value of objectLabel attribute.
     *
     * @return value of objectLabel attribute
     * @see #setObjectLabel
     */
    public String getObjectLabel()
    {
        return getAttributes().getStringAttribute(ATTR_OBJECT_LABEL_NAME).getValue();
    }

    /**
     * Sets new value of objectLabel attribute.
     *
     * @param objectLabel new value of objectLabel attribute
     * @see #getObjectLabel
     */
    public void setObjectLabel(String objectLabel)
    {
        getAttributes().getStringAttribute(ATTR_OBJECT_LABEL_NAME).setValue(objectLabel);
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public List<? extends XmlNode> getChildren() {
        return getNodeGroups().getObjectGroup().getChildren();
    }
        
    /**
     * Gets object child.
     *
     * @return object child
     * @see #setObject
     */
    public Object getObject() {
        return getNodeGroups().getObjectGroup().getObject();
    }

    /**
     * Sets new object child.
     *
     * @param object new object child
     * @see #getObject
     */
    public void setObject(Object object)
    {
        getNodeGroups().getObjectGroup().setObject(object);
    }
}
