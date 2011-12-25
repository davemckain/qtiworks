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

package uk.ac.ed.ph.jqtiplus.node.item;


import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;

import java.net.URI;


/**
 * Used to associate an external stylesheet with an assessmentItem.
 * 
 * @author Jonathon Hare
 */
public class Stylesheet extends AbstractNode {
    private static final long serialVersionUID = 1L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "stylesheet";
    
    /** Name of href attribute in xml schema. */
    public static final String ATTR_HREF_NAME = "href";

    /** Name of type attribute in xml schema. */
    public static final String ATTR_TYPE_NAME = "type";
    
    /** Name of media attribute in xml schema. */
    public static final String ATTR_MEDIA_NAME = "media";
    
    /** Name of title attribute in xml schema. */
    public static final String ATTR_TITLE_NAME = "title";
    
    /**
     * Construct A stylesheet 
     * @param parent Parent assessmentItem
     */
    public Stylesheet(AssessmentItem parent) {
        super(parent);
        
        getAttributes().add(new UriAttribute(this, ATTR_HREF_NAME));
        getAttributes().add(new StringAttribute(this, ATTR_TYPE_NAME));
        getAttributes().add(new StringAttribute(this, ATTR_MEDIA_NAME, null, null, false));
        getAttributes().add(new StringAttribute(this, ATTR_TITLE_NAME, null, null, false));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    /**
     * Gets value of href attribute.
     *
     * @return value of href attribute
     * @see #setHref
     */
    public URI getHref()
    {
        return getAttributes().getUriAttribute(ATTR_HREF_NAME).getValue();
    }

    /**
     * Sets new value of href attribute.
     *
     * @param href new value of href attribute
     * @see #getHref
     */
    public void setHref(URI href)
    {
        getAttributes().getUriAttribute(ATTR_HREF_NAME).setValue(href);
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
     * Gets value of media attribute.
     *
     * @return value of media attribute
     * @see #setMedia
     */
    public String getMedia()
    {
        return getAttributes().getStringAttribute(ATTR_MEDIA_NAME).getValue();
    }

    /**
     * Sets new value of media attribute.
     *
     * @param media new value of media attribute
     * @see #getMedia
     */
    public void setMedia(String media)
    {
        getAttributes().getStringAttribute(ATTR_MEDIA_NAME).setValue(media);
    }
    
    /**
     * Gets value of title attribute.
     *
     * @return value of title attribute
     * @see #setTitle
     */
    public String getTitle()
    {
        return getAttributes().getStringAttribute(ATTR_TITLE_NAME).getValue();
    }

    /**
     * Sets new value of title attribute.
     *
     * @param title new value of title attribute
     * @see #getTitle
     */
    public void setTitle(String title)
    {
        getAttributes().getStringAttribute(ATTR_TITLE_NAME).setValue(title);
    }    
}
