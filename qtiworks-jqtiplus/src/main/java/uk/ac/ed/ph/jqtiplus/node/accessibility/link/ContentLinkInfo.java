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
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.group.accessibility.link.LinkGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.ContentContainer;
import uk.ac.ed.ph.jqtiplus.node.accessibility.AccessElement;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

/**
 * FIXME: Document this type
 *
 * @author Zack Pierce
 */
public class ContentLinkInfo extends AbstractNode implements AccessibilityNode {

    private static final long serialVersionUID = -3814738853269263616L;

    public static final String QTI_CLASS_NAME = "contentLinkInfo";

    private static final String ATTR_APIP_LINK_IDENTIFIER_REF = "apipLinkIdentifierRef";

    private static final String ATTR_QTI_LINK_IDENTIFIER_REF = "qtiLinkIdentifierRef";

    public ContentLinkInfo(final AccessElement parent) {
        super(parent, QTI_CLASS_NAME);
        getAttributes().add(new StringAttribute(this, ATTR_QTI_LINK_IDENTIFIER_REF, false));
        getAttributes().add(new IdentifierReferenceAttribute(this, ATTR_APIP_LINK_IDENTIFIER_REF, false));
        getNodeGroups().add(new LinkGroup(this));
    }

    public String getQtiLinkIdentifierRef() {
        return getAttributes().getStringAttribute(ATTR_QTI_LINK_IDENTIFIER_REF).getValue();
    }

    public void setQtiLinkIdentifierRef(final String identifier) {
        getAttributes().getStringAttribute(ATTR_QTI_LINK_IDENTIFIER_REF).setValue(identifier);
    }

    public Identifier getApipLinkIdentifierRef() {
        return getAttributes().getIdentifierRefAttribute(ATTR_APIP_LINK_IDENTIFIER_REF).getValue();
    }

    public void setApipLinkIdentifierRef(final Identifier identifier) {
        getAttributes().getIdentifierRefAttribute(ATTR_APIP_LINK_IDENTIFIER_REF).setValue(identifier);
    }

    /**
     * @return the optional {@link ObjectLink} child, or null if it does not exist.
     */
    public ObjectLink getObjectLink() {
        return getNodeGroups().getLinkGroup().getObjectLink();
    }

    /**
     * Sets this ContentLink to have an ObjectLink, deleting or replacing any existent ObjectLink or TextLink child.
     *
     * If the supplied objectLink is null, any existing child will simply be deleted.
     * @param objectLink
     */
    public void setObjectLink(final ObjectLink objectLink) {
        getNodeGroups().getLinkGroup().setObjectLink(objectLink);
    }

    /**
     * @return the optional {@link TextLink} child, or null if it does not exist.
     */
    public TextLink getTextLink() {
        return getNodeGroups().getLinkGroup().getTextLink();
    }

    /**
     * Sets this ContentLink to have an TextLink, deleting or replacing any existent ObjectLink or TextLink child.
     *
     * If the supplied textLink is null, any existing child will simply be deleted.
     * @param textLink
     */
    public void setTextLink(final TextLink textLink) {
        getNodeGroups().getLinkGroup().setTextLink(textLink);
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#validateThis(uk.ac.ed.ph.jqtiplus.validation.ValidationContext)
     */
    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final String qtiIdRef = getQtiLinkIdentifierRef();
        if (qtiIdRef == null && getApipLinkIdentifierRef() == null) {
            context.fireValidationError(
                    this,
                    QTI_CLASS_NAME
                            + " must have either the qtiLinkIdentifierRef or apipLinkIdentifierRef specified, but both are null.");
        }
        else if (qtiIdRef != null) {
            if (getApipLinkIdentifierRef() != null) {
                context.fireValidationError(
                        this,
                        QTI_CLASS_NAME
                                + " must only have either qtiLinkIdentifierRef or apipLinkIdentifierRef specified, but not both.");
            }
            final ContentContainer container = QueryUtils.findRelatedTopLevelContentContainer(this);
            if (container == null) {
                context.fireValidationError(this, "No QTI content container found associated with this accessibility metadata");
            }
            else if (QueryUtils.findQtiDescendantOrSelf(container, qtiIdRef) == null) {
                context.fireValidationError(this, QTI_CLASS_NAME + " with " + ATTR_QTI_LINK_IDENTIFIER_REF + " of '"
                        + qtiIdRef + "' does not point to an extant Qti content element.");
            }
        }
    }
}
