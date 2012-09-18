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
package uk.ac.ed.ph.jqtiplus.node.content;

import uk.ac.ed.ph.jqtiplus.group.content.BlockGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;
import uk.ac.ed.ph.jqtiplus.node.content.template.TemplateElement;
import uk.ac.ed.ph.jqtiplus.node.content.variable.FeedbackBlock;
import uk.ac.ed.ph.jqtiplus.node.content.variable.FeedbackElement;
import uk.ac.ed.ph.jqtiplus.node.content.variable.FeedbackInline;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * from [XHTML]. In effect, this part of the specification defines A profile of XHTML. Only
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
public class ItemBody extends BodyElement {

    private static final long serialVersionUID = 5141415636417548133L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "itemBody";

    public ItemBody(final XmlNode parent) {
        super(parent, QTI_CLASS_NAME);

        getNodeGroups().add(new BlockGroup(this));
    }

    /**
     * Determine whether any feedback should be shown.
     *
     * @return true if any feedback should be shown; false otherwise;
     */
    public boolean willShowFeedback(final ItemProcessingContext itemContext) {
        for (final FeedbackElement feedbackElement : getFeedbackInline()) {
            if (feedbackElement.isVisible(itemContext)) {
                return true;
            }
        }
        for (final FeedbackElement feedbackElement : getFeedbackBlock()) {
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
        if (getFeedbackInline().size() > 0) {
            return true;
        }
        if (getFeedbackBlock().size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Get A snapshot of all the feedbackInline elements within this itemBody.
     * The returned list cannot be used for adding
     * new feedbackInline elements to the itemBody.
     *
     * @return list of feedbackInline elements in the itemBody.
     */
    private List<FeedbackInline> getFeedbackInline() {
        return QueryUtils.search(FeedbackInline.class, getBlocks());
    }

    /**
     * Get A snapshot of all the feedbackBlock elements within this itemBody.
     * The returned list cannot be used for adding
     * new feedbackBlock elements to the itemBody.
     *
     * @return list of feedbackBlock elements in the itemBody.
     */
    private List<FeedbackBlock> getFeedbackBlock() {
        return QueryUtils.search(FeedbackBlock.class, getBlocks());
    }

    /**
     * Get a snapshot of all the interactions within this itemBody.
     * The returned list cannot be used for adding
     * new interactions to the itemBody.
     *
     * @return list of interactions in the itemBody.
     */
    public List<Interaction> getInteractions() {
        return QueryUtils.search(Interaction.class, getBlocks());
    }


    /**
     * Get a snapshot of all the interactions within this itemBody, as a
     * Map keyed on responseIdentifier.
     * <p>
     * The returned {@link Map} cannot be used for adding new interactions to the itemBody.
     *
     * @return Map of interactions in the itemBody, keyed on responseIdentifier.
     */
    public Map<Identifier, Interaction> getInteractionMap() {
        final Map<Identifier, Interaction> result = new HashMap<Identifier, Interaction>();
        for (final Interaction interaction : getInteractions()) {
            result.put(interaction.getResponseIdentifier(), interaction);
        }
        return result;
    }

    /**
     * Get the interaction for a given responseIdentifier.
     * <p>
     * NB: This performs a deep search of the item body. If you need to find multiple interactions
     * at once, use {@link #getInteractionMap()}.
     *
     * @see #getInteractionMap()
     *
     * @param responseIdentifier responseIdentifier to search with.
     * @return interaction with matching responseIdentifier, or null if not found.
     */
    public Interaction getInteraction(final Identifier responseIdentifier) {
        for (final Interaction interaction : getInteractions()) {
            if (interaction.getResponseIdentifier() != null && interaction.getResponseIdentifier().equals(responseIdentifier)) {
                return interaction;
            }
        }
        return null;
    }

    /**
     * Get a snapshot of all the templates within this itemBody.
     * The returned list is unmodifiable and cannot be used for adding
     * new templates to the itemBody.
     *
     * @return unmodifiable list of interactions in the itemBody.
     */
    public List<TemplateElement> getTemplates() {
        return QueryUtils.search(TemplateElement.class, getBlocks());
    }

    /**
     * Get the templates for a given templateIdentifier.
     *
     * @param templateIdentifier templateIdentifier to search with.
     * @return unmodifiable list of templates with matching templateIdentifier, or null if not found.
     */
    public List<TemplateElement> getTemplates(final Identifier templateIdentifier) {
        final List<TemplateElement> templates = new ArrayList<TemplateElement>();
        for (final TemplateElement template : getTemplates()) {
            if (template.getTemplateIdentifier() != null && template.getTemplateIdentifier().equals(templateIdentifier)) {
                templates.add(template);
            }
        }

        return Collections.unmodifiableList(templates);
    }

    public List<Block> getBlocks() {
        return getNodeGroups().getBlockGroup().getBlocks();
    }
}
