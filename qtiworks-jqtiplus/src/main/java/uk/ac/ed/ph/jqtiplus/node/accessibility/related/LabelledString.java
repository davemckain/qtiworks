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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.accessibility.related;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseUtils;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Helper base class for elements containing textual body contents and a contentLinkIdentifier attribute.
 *
 * @author Zack Pierce
 */
public class LabelledString extends AbstractNode implements ContentLinkIdentifierBearer, AccessibilityNode {

    private static final long serialVersionUID = 721887805676767346L;

    private String textContent;

    public LabelledString(final QtiNode parent, final String qtiClassName) {
        super(parent, qtiClassName);
        getAttributes().add(
                new IdentifierAttribute(this, ContentLinkIdentifierBearer.ATTR_CONTENT_LINK_IDENTIFIER, true));
    }

    @Override
    public Identifier getContentLinkIdentifier() {
        return getAttributes().getIdentifierAttribute(ContentLinkIdentifierBearer.ATTR_CONTENT_LINK_IDENTIFIER)
                .getValue();
    }

    @Override
    public void setContentLinkIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ContentLinkIdentifierBearer.ATTR_CONTENT_LINK_IDENTIFIER).setValue(
                identifier);
    }

    @Override
    protected void loadChildren(final Element element, final LoadingContext context) {
        this.textContent = XmlParseUtils.getDirectTextualContent(element);
    }

    @Override
    public boolean hasChildNodes() {
        return false;
    }

    @Override
    protected void fireBodySaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        if (textContent != null) {
            qtiSaxDocumentFirer.fireText(textContent);
        }
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(final String textContent) {
        this.textContent = textContent;
    }

}
