/* Copyright (c) 2012-2013, University of Edinburgh.
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
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AbstractFlowBodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AtomicInline;

import java.net.URI;

/**
 * Represents the <tt>img</tt> QTI class
 *
 * @author Jonathon Hare
 */
public final class Img extends AbstractFlowBodyElement implements AtomicInline {

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

    public Img(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new UriAttribute(this, ATTR_SRC_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_ALT_NAME, true));
        getAttributes().add(new UriAttribute(this, ATTR_LONGDESC_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_HEIGHT_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_WIDTH_NAME, false));
    }

    public URI getSrc() {
        return getAttributes().getUriAttribute(ATTR_SRC_NAME).getComputedValue();
    }

    public void setSrc(final URI src) {
        getAttributes().getUriAttribute(ATTR_SRC_NAME).setValue(src);
    }


    public String getAlt() {
        return getAttributes().getStringAttribute(ATTR_ALT_NAME).getComputedValue();
    }

    public void setAlt(final String alt) {
        getAttributes().getStringAttribute(ATTR_ALT_NAME).setValue(alt);
    }


    public URI getLongdesc() {
        return getAttributes().getUriAttribute(ATTR_LONGDESC_NAME).getComputedValue();
    }

    public void setLongdesc(final URI longdesc) {
        getAttributes().getUriAttribute(ATTR_LONGDESC_NAME).setValue(longdesc);
    }


    public String getHeight() {
        return getAttributes().getStringAttribute(ATTR_HEIGHT_NAME).getComputedValue();
    }

    public void setHeight(final String height) {
        getAttributes().getStringAttribute(ATTR_HEIGHT_NAME).setValue(height);
    }


    public String getWidth() {
        return getAttributes().getStringAttribute(ATTR_WIDTH_NAME).getComputedValue();
    }

    public void setWidth(final String width) {
        getAttributes().getStringAttribute(ATTR_WIDTH_NAME).setValue(width);
    }
}
