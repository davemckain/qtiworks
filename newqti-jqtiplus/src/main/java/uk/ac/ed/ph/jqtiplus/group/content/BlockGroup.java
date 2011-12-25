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

import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.ContentType;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;

import java.util.List;




/**
 * Group of block children.
 * 
 * @author Jonathon Hare
 */
public class BlockGroup extends AbstractContentNodeGroup
{
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs group.
     *
     * @param parent parent of created group
     */
    public BlockGroup(XmlNode parent)
    {
        super(parent, Block.DISPLAY_NAME, null, null);

        getAllSupportedClasses().clear();
        for (ContentType type : ContentType.blockValues())
            getAllSupportedClasses().add(type.getClassTag());
    }

    @Override
    public XmlNode getParent()
    {
        return (XmlNode) super.getParent();
    }

    @Override
    public boolean isGeneral()
    {
        return true;
    }

    /**
     * Gets child.
     *
     * @return child
     * @see #setBlock
     */
    public Block getBlock()
    {
        return (getChildren().size() != 0) ? (Block) getChildren().get(0) : null;
    }

    /**
     * Sets new child.
     *
     * @param block new child
     * @see #getBlock
     */
    public void setBlock(Block block)
    {
        getChildren().clear();
        getChildren().add(block);
    }

    /**
     * Gets list of all children.
     *
     * @return list of all children
     */
    @SuppressWarnings ("unchecked")
    public List<Block> getBlocks()
    {
        return (List<Block>) ((List<? extends XmlNode>) getChildren());
    }

    /**
     * Creates child with given QTI class name.
     * <p>
     * Parameter classTag is needed only if group can contain children with different QTI class names.
     * @param classTag QTI class name (this parameter is needed)
     * @return created child
     */
    public Block create(String classTag)
    {
        return ContentType.getBlockInstance(getParent(), classTag);
    }
}
