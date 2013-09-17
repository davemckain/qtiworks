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
import uk.ac.ed.ph.jqtiplus.node.accessibility.link.CharacterLink;
import uk.ac.ed.ph.jqtiplus.node.accessibility.link.CharacterStringLink;
import uk.ac.ed.ph.jqtiplus.node.accessibility.link.FullString;
import uk.ac.ed.ph.jqtiplus.node.accessibility.link.StringLink;
import uk.ac.ed.ph.jqtiplus.node.accessibility.link.TextLink;
import uk.ac.ed.ph.jqtiplus.node.accessibility.link.WordLink;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Group for the various children of textLink:
 * fullString, wordLink, characterLink, and characterStringLink
 *
 * @author Zack Pierce
 */
public class StringLinkGroup extends ComplexNodeGroup<TextLink, StringLink> {

    private static final long serialVersionUID = -507947723327183531L;

    private static final Set<String> allowedTextualLinkLocalNames;

    static {
        final HashSet<String> names = new HashSet<String>();
        names.add(CharacterLink.QTI_CLASS_NAME);
        names.add(CharacterStringLink.QTI_CLASS_NAME);
        names.add(FullString.QTI_CLASS_NAME);
        names.add(WordLink.QTI_CLASS_NAME);
        allowedTextualLinkLocalNames = ObjectUtilities.unmodifiableSet(names);
    }

    public StringLinkGroup(final TextLink parent) {
        super(parent, StringLink.DISPLAY_NAME, allowedTextualLinkLocalNames, 1, 1);
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.group.NodeGroup#create(java.lang.String)
     */
    @Override
    public StringLink create(final String qtiClassName) {
        if (FullString.QTI_CLASS_NAME.equals(qtiClassName)) {
            return new FullString(getParent());
        }
        else if (CharacterLink.QTI_CLASS_NAME.equals(qtiClassName)) {
            return new CharacterLink(getParent());
        }
        else if (CharacterStringLink.QTI_CLASS_NAME.equals(qtiClassName)) {
            return new CharacterStringLink(getParent());
        }
        else if (WordLink.QTI_CLASS_NAME.equals(qtiClassName)) {
            return new WordLink(getParent());
        }
        throw new QtiIllegalChildException(getParent(), qtiClassName);
    }

    /**
     * @return {@link CharacterLink} child, or null if it does not exist.
     */
    public CharacterLink getCharacterLink() {
        return QueryUtils.findFirstShallowInstance(CharacterLink.class, children);
    }

    /**
     * @return {@link CharacterStringLink} child, or null if it does not exist.
     */
    public CharacterStringLink getCharacterStringLink() {
        return QueryUtils.findFirstShallowInstance(CharacterStringLink.class, children);
    }

    /**
     * @return {@link WordLink} child, or null if it does not exist.
     */
    public WordLink getWordLink() {
        return QueryUtils.findFirstShallowInstance(WordLink.class, children);
    }

    /**
     * @return {@link FullString} child, or null if it does not exist.
     */
    public FullString getFullString() {
        return QueryUtils.findFirstShallowInstance(FullString.class, children);
    }
}
