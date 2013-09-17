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
package uk.ac.ed.ph.jqtiplus.node.item;


import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;

import java.net.URI;

/**
 * Used to associate an external stylesheet with an assessmentItem, assessmentTest, a rubricBlock within an assessmentSection, a feedback block, or a template block.
 *
 * @author Jonathon Hare
 */
public class Stylesheet extends AbstractNode {

    private static final long serialVersionUID = -8528135114011904600L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "stylesheet";

    /** Name of href attribute in xml schema. */
    public static final String ATTR_HREF_NAME = "href";

    /** Name of type attribute in xml schema. */
    public static final String ATTR_TYPE_NAME = "type";

    /** Name of media attribute in xml schema. */
    public static final String ATTR_MEDIA_NAME = "media";

    /** Name of title attribute in xml schema. */
    public static final String ATTR_TITLE_NAME = "title";

    public Stylesheet(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new UriAttribute(this, ATTR_HREF_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_TYPE_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_MEDIA_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_TITLE_NAME, null, false));
    }

    /**
     * Gets value of href attribute.
     *
     * @return value of href attribute
     * @see #setHref
     */
    public URI getHref() {
        return getAttributes().getUriAttribute(ATTR_HREF_NAME).getComputedValue();
    }

    /**
     * Sets new value of href attribute.
     *
     * @param href new value of href attribute
     * @see #getHref
     */
    public void setHref(final URI href) {
        getAttributes().getUriAttribute(ATTR_HREF_NAME).setValue(href);
    }

    /**
     * Gets value of type attribute.
     *
     * @return value of type attribute
     * @see #setType
     */
    public String getType() {
        return getAttributes().getStringAttribute(ATTR_TYPE_NAME).getComputedValue();
    }

    /**
     * Sets new value of type attribute.
     *
     * @param type new value of type attribute
     * @see #getType
     */
    public void setType(final String type) {
        getAttributes().getStringAttribute(ATTR_TYPE_NAME).setValue(type);
    }

    /**
     * Gets value of media attribute.
     *
     * @return value of media attribute
     * @see #setMedia
     */
    public String getMedia() {
        return getAttributes().getStringAttribute(ATTR_MEDIA_NAME).getComputedValue();
    }

    /**
     * Sets new value of media attribute.
     *
     * @param media new value of media attribute
     * @see #getMedia
     */
    public void setMedia(final String media) {
        getAttributes().getStringAttribute(ATTR_MEDIA_NAME).setValue(media);
    }

    /**
     * Gets value of title attribute.
     *
     * @return value of title attribute
     * @see #setTitle
     */
    public String getTitle() {
        return getAttributes().getStringAttribute(ATTR_TITLE_NAME).getComputedValue();
    }

    /**
     * Sets new value of title attribute.
     *
     * @param title new value of title attribute
     * @see #getTitle
     */
    public void setTitle(final String title) {
        getAttributes().getStringAttribute(ATTR_TITLE_NAME).setValue(title);
    }
}
