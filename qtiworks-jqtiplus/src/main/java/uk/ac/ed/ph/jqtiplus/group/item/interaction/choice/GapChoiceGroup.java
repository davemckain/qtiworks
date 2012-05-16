/* Copyright (c) 2012, University of Edinburgh.
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
package uk.ac.ed.ph.jqtiplus.group.item.interaction.choice;

import uk.ac.ed.ph.jqtiplus.group.content.AbstractContentNodeGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.ContentType;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapChoice;

import java.util.List;

/**
 * Group of gapChoice children.
 *
 * @author Jonathon Hare
 */
public final class GapChoiceGroup extends AbstractContentNodeGroup<GapChoice> {

    private static final long serialVersionUID = 3797738603491602458L;

    /**
     * Constructs group.
     *
     * @param parent parent of created group
     */
    public GapChoiceGroup(final XmlNode parent) {
        this(parent, 1);
    }

    /**
     * Constructs group.
     *
     * @param parent parent of created group
     * @param minimum minimum number of children
     */
    public GapChoiceGroup(final XmlNode parent, final int minimum) {
        super(parent, GapChoice.DISPLAY_NAME, ContentType.getGapChoiceQtiClassNames(), minimum, null);
    }

    @Override
    public boolean isGeneral() {
        return true;
    }

    /**
     * Gets child.
     *
     * @return child
     * @see #setGapChoice
     */
    public GapChoice getGapChoice() {
        return getChildren().size() != 0 ? (GapChoice) getChildren().get(0) : null;
    }

    /**
     * Sets new child.
     *
     * @param gapChoice new child
     * @see #getGapChoice
     */
    public void setGapChoice(final GapChoice gapChoice) {
        getChildren().clear();
        getChildren().add(gapChoice);
    }

    /**
     * Gets list of all children.
     *
     * @return list of all children
     */
    @SuppressWarnings("unchecked")
    public List<GapChoice> getGapChoices() {
        return (List<GapChoice>) (List<? extends XmlNode>) getChildren();
    }

    /**
     * Creates child with given QTI class name.
     * <p>
     * Parameter classTag is needed only if group can contain children with different QTI class names.
     *
     * @param classTag QTI class name (this parameter is needed)
     * @return created child
     */
    @Override
    public GapChoice create(final String classTag) {
        return ContentType.getGapChoiceInstance(getParent(), classTag);
    }
}
