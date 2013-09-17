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
package uk.ac.ed.ph.jqtiplus.group.accessibility.link;

import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;
import uk.ac.ed.ph.jqtiplus.group.ComplexNodeGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.accessibility.link.ContentLinkInfo;
import uk.ac.ed.ph.jqtiplus.node.accessibility.link.Link;
import uk.ac.ed.ph.jqtiplus.node.accessibility.link.ObjectLink;
import uk.ac.ed.ph.jqtiplus.node.accessibility.link.TextLink;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Group that holds either a single objectLink or a single textLink
 *
 * @author Zack Pierce
 */
public class LinkGroup extends ComplexNodeGroup<ContentLinkInfo, Link> {

    private static final long serialVersionUID = -7333407901601134922L;

    private static final Set<String> allowedLinkLocalNames;

    static {
        final HashSet<String> names = new HashSet<String>();
        names.add(ObjectLink.QTI_CLASS_NAME);
        names.add(TextLink.QTI_CLASS_NAME);
        allowedLinkLocalNames = ObjectUtilities.unmodifiableSet(names);
    }

    public LinkGroup(final ContentLinkInfo parent) {
        super(parent, Link.DISPLAY_NAME, allowedLinkLocalNames, 1, 1);
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.group.NodeGroup#create(java.lang.String)
     */
    @Override
    public Link create(final String qtiClassName) {
        if (ObjectLink.QTI_CLASS_NAME.equals(qtiClassName)) {
            return new ObjectLink(getParent());
        }
        else if (TextLink.QTI_CLASS_NAME.equals(qtiClassName)) {
            return new TextLink(getParent());
        }
        throw new QtiIllegalChildException(getParent(), qtiClassName);
    }

    /**
     * @return {@link ObjectLink} child, or null if it does not exist.
     */
    public ObjectLink getObjectLink() {
        return QueryUtils.findFirstShallowInstance(ObjectLink.class, children);
    }

    /**
     * Sets the Link child to be an ObjectLink, deleting or replacing any existent child.
     *
     * If the supplied objectLink is null, any existing child will simply be deleted.
     * @param objectLink
     */
    public void setObjectLink(final ObjectLink objectLink) {
        children.clear();
        if (objectLink != null) {
            children.add(objectLink);
        }
    }

    /**
     * @return {@link TextLink} child, or null if it does not exist.
     */
    public TextLink getTextLink() {
        return QueryUtils.findFirstShallowInstance(TextLink.class, children);
    }

    /**
     * Sets the Link child to be a TextLink, deleting or replacing any existent child.
     *
     * If the supplied textLink is null, any existing child will simply be deleted.
     * @param textLink
     */
    public void setTextLink(final TextLink textLink) {
        children.clear();
        if (textLink != null) {
            children.add(textLink);
        }
    }

}
