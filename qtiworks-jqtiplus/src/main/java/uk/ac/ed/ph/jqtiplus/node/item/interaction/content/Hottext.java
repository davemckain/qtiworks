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
package uk.ac.ed.ph.jqtiplus.node.item.interaction.content;

import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.InlineStaticGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Flow;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.InlineStatic;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.Choice;

import java.net.URI;
import java.util.List;

import javax.xml.XMLConstants;

/**
 * A hottext area is used within the content of an hottextInteraction to
 * provide the individual choices. It must not contain any nested
 * interactions or other hottext areas.
 * When a hottext choice is hidden (by the value of an associated template
 * variable) the content of the choice must still be presented to the
 * candidate as if it were simply part of the surrounding material. In the
 * case of hottext, the effect of hiding the choice is simply to make the
 * run of text unselectable by the candidate.
 * Contains : inlineStatic [*]
 *
 * @author Jonathon Hare
 */
public final class Hottext extends Choice implements InlineStatic, FlowStatic {

    private static final long serialVersionUID = 1456204540250149804L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "hottext";

    public Hottext(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new UriAttribute(this, Flow.ATTR_BASE_URI_NAME, XMLConstants.XML_NS_URI, false));
        getNodeGroups().add(new InlineStaticGroup(this));
    }

    @Override
    public URI getBaseUri() {
        return getAttributes().getUriAttribute(Flow.ATTR_BASE_URI_NAME, XMLConstants.XML_NS_URI).getComputedValue();
    }

    @Override
    public void setBaseUri(final URI base) {
        getAttributes().getUriAttribute(Flow.ATTR_BASE_URI_NAME, XMLConstants.XML_NS_URI).setValue(base);
    }

    public List<InlineStatic> getInlineStatics() {
        return getNodeGroups().getInlineStaticGroup().getInlineStatics();
    }

    /**
     * @deprecated Please now use {@link #getInlineStatics()}
     */
    @Deprecated
    public List<InlineStatic> getChildren() {
        return getInlineStatics();
    }
}
