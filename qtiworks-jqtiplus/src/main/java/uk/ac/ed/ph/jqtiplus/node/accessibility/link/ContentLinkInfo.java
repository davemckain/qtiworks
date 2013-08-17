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
package uk.ac.ed.ph.jqtiplus.node.accessibility.link;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierReferenceAttribute;
import uk.ac.ed.ph.jqtiplus.group.accessibility.link.LinkGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

/**
 * FIXME: Document this type
 *
 * @author Zack Pierce
 */
public class ContentLinkInfo extends AbstractNode {

    private static final long serialVersionUID = -3814738853269263616L;

    public static final String QTI_CLASS_NAME = "contentLinkInfo";

    private static final String ATTR_APIP_LINK_IDENTIFIER_REF = "apipLinkIdentifierRef";

    private static final String ATTR_QTI_LINK_IDENTIFIER_REF = "qtiLinkIdentifierRef";

    public ContentLinkInfo(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);
        getAttributes().add(new IdentifierReferenceAttribute(this, ATTR_QTI_LINK_IDENTIFIER_REF, false));
        getAttributes().add(new IdentifierReferenceAttribute(this, ATTR_APIP_LINK_IDENTIFIER_REF, false));
        getNodeGroups().add(new LinkGroup(this));
    }

    public Identifier getQtiLinkIdentifierRef() {
        return getAttributes().getIdentifierRefAttribute(ATTR_QTI_LINK_IDENTIFIER_REF).getValue();
    }

    public Identifier getApipLinkIdentifierRef() {
        return getAttributes().getIdentifierRefAttribute(ATTR_APIP_LINK_IDENTIFIER_REF).getValue();
    }

    /**
     * @return the optional {@link ObjectLink} child, or null if it does not exist.
     */
    public ObjectLink getObjectLink() {
        return getNodeGroups().getLinkGroup().getObjectLink();
    }

    /**
     * @return the optional {@link TextLink} child, or null if it does not exist.
     */
    public TextLink getTextLink() {
        return getNodeGroups().getLinkGroup().getTextLink();
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#validateThis(uk.ac.ed.ph.jqtiplus.validation.ValidationContext)
     */
    @Override
    protected void validateThis(final ValidationContext context) {
        // TODO : validate that either the qtiLinkIdentifierRef or the apipLinkIdentifierRef attribute has a value
        super.validateThis(context);
    }
}
