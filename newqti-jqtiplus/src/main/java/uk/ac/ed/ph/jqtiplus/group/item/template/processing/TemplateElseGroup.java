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

package uk.ac.ed.ph.jqtiplus.group.item.template.processing;


import uk.ac.ed.ph.jqtiplus.group.AbstractNodeGroup;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateCondition;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateElse;


/**
 * Group of templateElse child.
 * 
 * @author Jonathon Hare
 */
public class TemplateElseGroup extends AbstractNodeGroup
{
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs group.
     *
     * @param parent parent of created group
     */
    public TemplateElseGroup(TemplateCondition parent)
    {
        super(parent, TemplateElse.CLASS_TAG, false);
    }

    /**
     * Gets child.
     *
     * @return child
     * @see #setTemplateElse
     */
    public TemplateElse getTemplateElse()
    {
        return (TemplateElse) getChild();
    }

    /**
     * Sets new child.
     *
     * @param templateElse new child
     * @see #getTemplateElse
     */
    public void setTemplateElse(TemplateElse templateElse)
    {
        setChild(templateElse);
    }

    /**
     * Creates child with given QTI class name.
     * <p>
     * Parameter classTag is needed only if group can contain children with different QTI class names.
     * @param classTag QTI class name (this parameter is ignored)
     * @return created child
     */
    public TemplateElse create(String classTag)
    {
        return new TemplateElse((TemplateCondition) getParent());
    }
}
