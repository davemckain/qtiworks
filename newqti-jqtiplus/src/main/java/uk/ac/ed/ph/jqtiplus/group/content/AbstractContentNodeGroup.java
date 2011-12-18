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

package uk.ac.ed.ph.jqtiplus.group.content;

import uk.ac.ed.ph.jqtiplus.group.AbstractNodeGroup;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.TextRun;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Group of content children.
 * 
 * @author Jonathon Hare
 */
public abstract class AbstractContentNodeGroup extends AbstractNodeGroup {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs group.
     *
     * @param parent parent of created group
     * @param name name of node group
     * @param required is group required
     */
    public AbstractContentNodeGroup(XmlNode parent, String name, boolean required) 
    {
        super(parent, name, required);
    }
    
    /**
     * Constructs group.
     *
     * @param parent parent of created group
     * @param name name of node group
     * @param minimum minimum number of children 
     * @param maximum maximum number of children
     */
    public AbstractContentNodeGroup(XmlNode parent, String name, Integer minimum, Integer maximum) 
    {
        super(parent, name, minimum, maximum);
    }

    @Override
    public void load(Element node, LoadingContext context) {
        NodeList childNodes = node.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE && getAllSupportedClasses().contains(childNode.getLocalName())) {
                XmlNode child = createChild((Element) childNode, context);
                getChildren().add(child);
                child.load((Element) childNode, context);
            }
            else if (childNode.getNodeType() == Node.TEXT_NODE && getAllSupportedClasses().contains(TextRun.DISPLAY_NAME)) {
                TextRun child = (TextRun) create(TextRun.DISPLAY_NAME);
                getChildren().add(child);
                child.load(((Text) childNode));
            }
        }
    }
}
