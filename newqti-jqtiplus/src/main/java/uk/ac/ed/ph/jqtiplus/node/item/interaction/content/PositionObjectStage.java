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

package uk.ac.ed.ph.jqtiplus.node.item.interaction.content;

import uk.ac.ed.ph.jqtiplus.group.content.ObjectGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.PositionObjectInteractionGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractObject;
import uk.ac.ed.ph.jqtiplus.node.XmlObject;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.PositionObjectInteraction;

import java.util.List;


/**
 * positionObjectStage
 * 
 * @author Jonathon Hare
 *
 */
public class PositionObjectStage extends AbstractObject implements Block {
    private static final long serialVersionUID = 1L;

    /** Name of this class in xml schema. */
    public static String CLASS_TAG = "positionObjectStage";

    /**
     * Constructs object.
     *
     * @param parent parent of constructed object
     */
    public PositionObjectStage(XmlObject parent) {
        super(parent);
        
        getNodeGroups().add(new ObjectGroup(this, true));
        getNodeGroups().add(new PositionObjectInteractionGroup(this, 1));
    }

    /**
     * Gets positionObjectInteraction children.
     * @return positionObjectInteraction children
     */
    public List<PositionObjectInteraction> getPositionObjectInteractions() {
        return getNodeGroups().getPositionObjectInteractionGroup().getPositionObjectInteractions();
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
    
    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }
}
