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

import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.group.accessibility.link.StringLinkGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;

/**
 * A link to the textual portion of the identifier-referenced content element.
 *
 * <p>The particular portion of the text referenced is controlled by the child elements:
 * {@link CharacterLink}, {@link CharacterStringLink}, {@link FullString},
 * and {@link WordLink}.</p>
 *
 * <p>TODO : Add convenience setters for the child options</p>
 *
 * @author Zack Pierce
 */
public class TextLink extends AbstractNode implements Link, AccessibilityNode {

    private static final long serialVersionUID = 7765175490211170308L;

    public static final String QTI_CLASS_NAME = "textLink";

    public TextLink(final ContentLinkInfo parent) {
        super(parent, QTI_CLASS_NAME);
        getNodeGroups().add(new StringLinkGroup(this));
    }

    /**
     * @return the optional {@link CharacterLink} child, or null if it does not exist.
     */
    public CharacterLink getCharacterLink() {
        return getNodeGroups().getStringLinkGroup().getCharacterLink();
    }

    /**
     * @return the optional {@link CharacterStringLink} child, or null if it does not exist.
     */
    public CharacterStringLink getCharacterStringLink() {
        return getNodeGroups().getStringLinkGroup().getCharacterStringLink();
    }

    /**
     * @return the optional {@link WordLink} child, or null if it does not exist.
     */
    public WordLink getWordLink() {
        return getNodeGroups().getStringLinkGroup().getWordLink();
    }

    /**
     * @return the optional {@link FullString} child, or null if it does not exist.
     */
    public FullString getFullString() {
        return getNodeGroups().getStringLinkGroup().getFullString();
    }

    /**
     * Convenience for checking whether this TextLink contains a FullString child.
     * @return true if the FullString child is non-null, false otherwise.
     */
    public boolean hasFullString() {
        return getFullString() != null;
    }
}
