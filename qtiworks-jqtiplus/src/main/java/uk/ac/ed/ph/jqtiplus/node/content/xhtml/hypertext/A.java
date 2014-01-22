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
package uk.ac.ed.ph.jqtiplus.node.content.xhtml.hypertext;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AbstractSimpleInline;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.net.URI;

/**
 * Although A inherits from simpleInline it must not contain, either directly or indirectly, another A.
 *
 * Attribute : href [1]: uri
 * Attribute : type [0..1]: mimeType
 *
 * @author Jonathon Hare
 */
public final class A extends AbstractSimpleInline {

    private static final long serialVersionUID = -838798085866010380L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "a";

    /** Name of href attribute in xml schema. */
    public static final String ATTR_HREF_NAME = "href";

    /** Name of type attribute in xml schema. */
    public static final String ATTR_TYPE_NAME = "type";

    public A(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new UriAttribute(this, ATTR_HREF_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_TYPE_NAME, false));
    }

    public URI getHref() {
        return getAttributes().getUriAttribute(ATTR_HREF_NAME).getComputedValue();
    }

    public void setHref(final URI href) {
        getAttributes().getUriAttribute(ATTR_HREF_NAME).setValue(href);
    }


    public String getType() {
        return getAttributes().getStringAttribute(ATTR_TYPE_NAME).getComputedValue();
    }

    public void setType(final String type) {
        getAttributes().getStringAttribute(ATTR_TYPE_NAME).setValue(type);
    }


    @Override
    public void validateThis(final ValidationContext context) {
        super.validateThis(context);
        /* Although A inherits from simpleInline it must not contain, either directly or indirectly, another A. */
        if (QueryUtils.hasDescendant(A.class, this)) {
            context.fireValidationError(this, "The " + QTI_CLASS_NAME + " class cannot contain " + QTI_CLASS_NAME + " children");
        }
    }
}
