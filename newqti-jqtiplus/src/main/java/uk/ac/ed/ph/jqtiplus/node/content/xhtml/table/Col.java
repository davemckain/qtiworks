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

package uk.ac.ed.ph.jqtiplus.node.content.xhtml.table;

import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;

import java.util.List;


/**
 * col
 * 
 * @author Jonathon Hare
 *
 */
public class Col extends BodyElement {
    private static final long serialVersionUID = 1L;

    /** Name of this class in xml schema. */
    public static String CLASS_TAG = "col";
    
    /** Name of span attribute in xml schema. */
    public static final String ATTR_SPAN_NAME = "span";
    
    /** Default value of span attribute. */
    public static final int ATTR_SPAN_DEFAULT_VALUE = 1;
    
    /**
     * Constructs object.
     *
     * @param parent parent of constructed object
     */
    public Col(XmlNode parent) {
        super(parent);
        
        getAttributes().add(new IntegerAttribute(this, ATTR_SPAN_NAME, ATTR_SPAN_DEFAULT_VALUE));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public List<? extends XmlNode> getChildren() {
        return null;
    }
    
    /**
     * Gets value of span attribute.
     *
     * @return value of span attribute
     * @see #setSpan
     */
    public Integer getSpan()
    {
        return getAttributes().getIntegerAttribute(ATTR_SPAN_NAME).getValue();
    }

    /**
     * Sets new value of span attribute.
     *
     * @param span new value of span attribute
     * @see #getSpan
     */
    public void setSpan(Integer span)
    {
        getAttributes().getIntegerAttribute(ATTR_SPAN_NAME).setValue(span);
    }
}
