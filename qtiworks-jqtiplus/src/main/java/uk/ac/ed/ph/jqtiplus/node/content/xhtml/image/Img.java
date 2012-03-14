/* Copyright (c) 2012, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.content.xhtml.image;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AtomicInline;

import java.net.URI;
import java.util.List;

/**
 * Attribute : src [1]: uri
 * Attribute : alt [1]: string256
 * Attribute : longdesc [0..1]: uri
 * Attribute : height [0..1]: length
 * Attribute : width [0..1]: length
 * 
 * @author Jonathon Hare
 */
public class Img extends BodyElement implements AtomicInline {

    private static final long serialVersionUID = 5705344980101577516L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "img";

    /** Name of src attribute in xml schema. */
    public static final String ATTR_SRC_NAME = "src";

    /** Name of alt attribute in xml schema. */
    public static final String ATTR_ALT_NAME = "alt";

    /** Name of longdesc attribute in xml schema. */
    public static final String ATTR_LONGDESC_NAME = "longdesc";

    /** Name of height attribute in xml schema. */
    public static final String ATTR_HEIGHT_NAME = "height";

    /** Name of width attribute in xml schema. */
    public static final String ATTR_WIDTH_NAME = "width";

    public Img(XmlNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new UriAttribute(this, ATTR_SRC_NAME));
        getAttributes().add(new StringAttribute(this, ATTR_ALT_NAME));
        getAttributes().add(new UriAttribute(this, ATTR_LONGDESC_NAME, null, null, false));
        getAttributes().add(new StringAttribute(this, ATTR_HEIGHT_NAME, null, null, false));
        getAttributes().add(new StringAttribute(this, ATTR_WIDTH_NAME, null, null, false));
    }

    @Override
    public List<? extends XmlNode> getChildren() {
        return null;
    }

    /**
     * Gets value of src attribute.
     * 
     * @return value of src attribute
     * @see #setSrc
     */
    public URI getSrc() {
        return getAttributes().getUriAttribute(ATTR_SRC_NAME).getComputedValue();
    }

    /**
     * Sets new value of src attribute.
     * 
     * @param src new value of src attribute
     * @see #getSrc
     */
    public void setSrc(URI src) {
        getAttributes().getUriAttribute(ATTR_SRC_NAME).setValue(src);
    }

    /**
     * Gets value of alt attribute.
     * 
     * @return value of alt attribute
     * @see #setAlt
     */
    public String getAlt() {
        return getAttributes().getStringAttribute(ATTR_ALT_NAME).getComputedValue();
    }

    /**
     * Sets new value of alt attribute.
     * 
     * @param alt new value of alt attribute
     * @see #getAlt
     */
    public void setAlt(String alt) {
        getAttributes().getStringAttribute(ATTR_ALT_NAME).setValue(alt);
    }

    /**
     * Gets value of longdesc attribute.
     * 
     * @return value of longdesc attribute
     * @see #setLongdesc
     */
    public URI getLongdesc() {
        return getAttributes().getUriAttribute(ATTR_LONGDESC_NAME).getComputedValue();
    }

    /**
     * Sets new value of longdesc attribute.
     * 
     * @param longdesc new value of longdesc attribute
     * @see #getLongdesc
     */
    public void setLongdesc(URI longdesc) {
        getAttributes().getUriAttribute(ATTR_LONGDESC_NAME).setValue(longdesc);
    }

    /**
     * Gets value of height attribute.
     * 
     * @return value of height attribute
     * @see #setHeight
     */
    public String getHeight() {
        return getAttributes().getStringAttribute(ATTR_HEIGHT_NAME).getComputedValue();
    }

    /**
     * Sets new value of height attribute.
     * 
     * @param height new value of height attribute
     * @see #getHeight
     */
    public void setHeight(String height) {
        getAttributes().getStringAttribute(ATTR_HEIGHT_NAME).setValue(height);
    }

    /**
     * Gets value of width attribute.
     * 
     * @return value of width attribute
     * @see #setWidth
     */
    public String getWidth() {
        return getAttributes().getStringAttribute(ATTR_WIDTH_NAME).getComputedValue();
    }

    /**
     * Sets new value of width attribute.
     * 
     * @param width new value of width attribute
     * @see #getWidth
     */
    public void setWidth(String width) {
        getAttributes().getStringAttribute(ATTR_WIDTH_NAME).setValue(width);
    }
}
