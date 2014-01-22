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
package uk.ac.ed.ph.jqtiplus.node.content;

import uk.ac.ed.ph.jqtiplus.group.content.BlockGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;
import uk.ac.ed.ph.jqtiplus.node.content.variable.FeedbackBlock;
import uk.ac.ed.ph.jqtiplus.node.content.variable.FeedbackElement;
import uk.ac.ed.ph.jqtiplus.node.content.variable.FeedbackInline;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;

import java.util.List;

/**
 * The item body contains the text, graphics, media objects, and interactions that describe
 * the item's content and information about how it is structured. The body is presented by
 * combining it with stylesheet information, either explicitly or implicitly using the default
 * style rules of the delivery or authoring system.
 * The body must be presented to the candidate when the associated item session is in the
 * interacting state. In this state, the candidate must be able to interact with each of the
 * visible interactions and therefore set or update the values of the associated response
 * variables. The body may be presented to the candidate when the item session is in the
 * closed or review state. In these states, although the candidate's responses should be
 * visible, the interactions must be disabled so as to prevent the candidate from setting or
 * updating the values of the associated response variables. Finally, the body may be
 * presented to the candidate in the solution state, in which case the correct values of
 * the response variables must be visible and the associated interactions disabled.
 * The content model employed by this specification uses many concepts taken directly
 * from [XHTML]. In effect, this part of the specification defines a profile of XHTML. Only
 * some of the elements defined in XHTML are allowable in an assessmentItem and of those that
 * are, some have additional constraints placed on their attributes. Only those elements from
 * XHTML that are explicitly defined within this specification can be used. See XHTML Elements
 * for details. Finally, this specification defines some new elements which are used to
 * represent the interactions and to control the display of Integrated Feedback and content
 * restricted to one or more of the defined content views.
 *
 * @author Jonathon Hare
 * @author Jiri Kajaba
 */
public final class ItemBody extends BodyElement {

    private static final long serialVersionUID = 5141415636417548133L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "itemBody";

    public ItemBody(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getNodeGroups().add(new BlockGroup(this));
    }

    public List<Block> getBlocks() {
        return getNodeGroups().getBlockGroup().getBlocks();
    }

    /**
     * Computes a snapshot of all the interactions within this itemBody.
     * The returned list cannot be used for adding new interactions to the itemBody.
     * <p>
     * (This performs a deep search.)
     *
     * @return list of interactions in the itemBody.
     */
    public List<Interaction> findInteractions() {
        return QueryUtils.search(Interaction.class, getBlocks());
    }

    /**
     * Determine whether any feedback should be shown.
     *
     * @return true if any feedback should be shown; false otherwise;
     */
    public boolean willShowFeedback(final ItemProcessingContext itemContext) {
        for (final FeedbackElement feedbackElement : findFeedbackInlines()) {
            if (feedbackElement.isVisible(itemContext)) {
                return true;
            }
        }
        for (final FeedbackElement feedbackElement : findFeedbackBlocks()) {
            if (feedbackElement.isVisible(itemContext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if this item has any inline or block feedback.
     *
     * @return true if the item has inline or block feedback; false otherwise;
     */
    public boolean hasFeedback() {
        if (findFeedbackInlines().size() > 0) {
            return true;
        }
        if (findFeedbackBlocks().size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Computes a snapshot of all the feedbackInline elements within this itemBody.
     * The returned list cannot be used for adding
     * new feedbackInline elements to the itemBody.
     *
     * @return list of feedbackInline elements in the itemBody.
     */
    private List<FeedbackInline> findFeedbackInlines() {
        return QueryUtils.search(FeedbackInline.class, getBlocks());
    }

    /**
     * Computes a snapshot of all the feedbackBlock elements within this itemBody.
     * The returned list cannot be used for adding
     * new feedbackBlock elements to the itemBody.
     *
     * @return list of feedbackBlock elements in the itemBody.
     */
    private List<FeedbackBlock> findFeedbackBlocks() {
        return QueryUtils.search(FeedbackBlock.class, getBlocks());
    }
}
