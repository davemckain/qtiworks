/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in node and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of node code must retain the above copyright notice, this
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

package uk.ac.ed.ph.jqtiplus.validation;

import uk.ac.ed.ph.jqtiplus.node.XmlNode;

/**
 * Abstract implementation of ValidationItem.
 * 
 * @author Jiri Kajaba
 */
public abstract class AbstractValidationItem implements ValidationItem
{
    /** Source of this item. */
    private Validatable source;

    /** Source node of this item. */
    private XmlNode node;

    /** Message of this item. */
    private String message;

    /**
     * Constructs validation item.
     *
     * @param source source of constructed item
     * @param node source node of constructed item
     * @param message message of constructed item
     */
    public AbstractValidationItem(Validatable source, XmlNode node, String message)
    {
        this.source = source;
        this.node = node;
        this.message = message;
    }

    public Validatable getSource()
    {
        return source;
    }

    public XmlNode getNode()
    {
        return node;
    }
    
    public void setNode(XmlNode node)
    {
        this.node = node;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(getType());
        builder.append(": ");
        builder.append(getMessage());
        builder.append(" (");
        builder.append(getNode().computeXPath());
        builder.append(")");

        return builder.toString();
    }
}
