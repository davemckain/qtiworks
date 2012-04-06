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
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.SimpleMatchSetGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleAssociableChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleMatchSet;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleMatchSetContainer;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.value.DirectedPairValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A match interaction is a blockInteraction that presents candidates
 * with two sets of choices and allows them to create associates between
 * pairs of choices in the two sets, but not between pairs of choices in
 * the same set. Further restrictions can still be placed on the
 * allowable associations using the matchMax and matchGroup attributes of
 * the choices.
 * The matchInteraction must be bound to a response variable with base-type
 * directedPair and either single or multiple cardinality.
 * Attribute : shuffle [1]: boolean = false
 * If the shuffle attribute is true then the delivery engine must randomize
 * the order in which the choices are presented within each set, subject to
 * the fixed attribute of the choices themselves.
 * Attribute : maxAssociations [1]: integer = 1
 * The maximum number of associations that the candidate is allowed to make.
 * If maxAssociations is 0 then there is no restriction. If maxAssociations
 * is greater than 1 (or 0) then the interaction must be bound to a response
 * with multiple cardinality.
 * Attribute : minAssociations [0..1]: integer = 0
 * The minimum number of associations that the candidate is required to make
 * to form a valid response. If minAssociations is 0 then the candidate is not
 * required to make any associations. minAssociations must be less than or
 * equal to the limit imposed by maxAssociations.
 * Contains : simpleMatchSet [2]
 * The two sets of choices, the first set defines the source choices and the
 * second set the targets.
 * 
 * @author Jonathon Hare
 */
public class MatchInteraction extends BlockInteraction implements SimpleMatchSetContainer, Shuffleable {

    private static final long serialVersionUID = 8556474552543752269L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "matchInteraction";

    /** Name of shuffle attribute in xml schema. */
    public static String ATTR_SHUFFLE_NAME = "shuffle";

    /** Default value of shuffle attribute. */
    public static Boolean ATTR_SHUFFLE_DEFAULT_VALUE = Boolean.FALSE;

    /** Name of maxAssociations attribute in xml schema. */
    public static String ATTR_MAX_ASSOCIATIONS_NAME = "maxAssociations";

    /** Default value of maxAssociations attribute . */
    public static int ATTR_MAX_ASSOCIATIONS_DEFAULT_VALUE = 1;

    /** Name of minAssociations attribute in xml schema. */
    public static String ATTR_MIN_ASSOCIATIONS_NAME = "minAssociations";

    /** Default value of minAssociations attribute . */
    public static int ATTR_MIN_ASSOCIATIONS_DEFAULT_VALUE = 0;

    public MatchInteraction(XmlNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BooleanAttribute(this, ATTR_SHUFFLE_NAME, ATTR_SHUFFLE_DEFAULT_VALUE, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_ASSOCIATIONS_NAME, ATTR_MAX_ASSOCIATIONS_DEFAULT_VALUE, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_ASSOCIATIONS_NAME, ATTR_MIN_ASSOCIATIONS_DEFAULT_VALUE, false));

        getNodeGroups().add(new SimpleMatchSetGroup(this, 2, 2));
    }

    /**
     * Gets an unmodifiable list of the child elements. Use the other
     * methods on ChoiceInteraction to add children to the correct group.
     */
    @Override
    public List<? extends XmlNode> getChildren() {
        final List<XmlNode> children = new ArrayList<XmlNode>();
        children.addAll(super.getChildren());
        children.addAll(getNodeGroups().getSimpleMatchSetGroup().getSimpleMatchSets());

        return Collections.unmodifiableList(children);
    }

    /**
     * Sets new value of shuffle attribute.
     * 
     * @param shuffle new value of shuffle attribute
     * @see #getShuffle
     */
    @Override
    public void setShuffle(Boolean shuffle) {
        getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).setValue(shuffle);
    }

    /**
     * Gets value of shuffle attribute.
     * 
     * @return value of shuffle attribute
     * @see #setShuffle
     */
    @Override
    public Boolean getShuffle() {
        return getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).getComputedValue();
    }

    /**
     * Sets new value of maxAssociations attribute.
     * 
     * @param maxAssociations new value of maxAssociations attribute
     * @see #getMaxAssociations
     */
    public void setMaxAssociations(Integer maxAssociations) {
        getAttributes().getIntegerAttribute(ATTR_MAX_ASSOCIATIONS_NAME).setValue(maxAssociations);
    }

    /**
     * Gets value of maxAssociations attribute.
     * 
     * @return value of maxAssociations attribute
     * @see #setMaxAssociations
     */
    public Integer getMaxAssociations() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_ASSOCIATIONS_NAME).getComputedValue();
    }

    /**
     * Sets new value of minAssociations attribute.
     * 
     * @param minAssociations new value of minAssociations attribute
     * @see #getMinAssociations
     */
    public void setMinAssociations(Integer minAssociations) {
        getAttributes().getIntegerAttribute(ATTR_MIN_ASSOCIATIONS_NAME).setValue(minAssociations);
    }

    /**
     * Gets value of minAssociations attribute.
     * 
     * @return value of minAssociations attribute
     * @see #setMinAssociations
     */
    public Integer getMinAssociations() {
        return getAttributes().getIntegerAttribute(ATTR_MIN_ASSOCIATIONS_NAME).getComputedValue();
    }

    /**
     * Gets simpleMatchSet children.
     * 
     * @return simpleMatchSet children
     */
    public List<SimpleMatchSet> getSimpleMatchSets() {
        return getNodeGroups().getSimpleMatchSetGroup().getSimpleMatchSets();
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);

        if (getMaxAssociations() != 0 && getMinAssociations() > getMaxAssociations()) {
            context.add(new ValidationError(this, "Minimum number of associations can't be bigger than maximum number"));
        }

        if (getResponseIdentifier() != null) {
            final ResponseDeclaration declaration = getResponseDeclaration();
            if (declaration != null && declaration.getBaseType() != null && !declaration.getBaseType().isDirectedPair()) {
                context.add(new ValidationError(this, "Response variable must have directedPair base type"));
            }

            if (declaration != null && getMaxAssociations() == 1 &&
                    declaration.getCardinality() != null && !declaration.getCardinality().isSingle() &&
                    !declaration.getCardinality().isMultiple()) {
                context.add(new ValidationError(this, "Response variable must have single or multiple cardinality"));
            }

            if (declaration != null && getMaxAssociations() != 1 && declaration.getCardinality() != null && !declaration.getCardinality().isMultiple()) {
                context.add(new ValidationError(this, "Response variable must have multiple cardinality"));
            }
        }
    }

    @Override
    public void initialize(ItemSessionController itemController) {
        super.initialize(itemController);

        final List<SimpleMatchSet> simpleMatchSets = getSimpleMatchSets();
        final List<List<SimpleAssociableChoice>> choiceLists = new ArrayList<List<SimpleAssociableChoice>>(simpleMatchSets.size());
        for (final SimpleMatchSet simpleMatchSet : simpleMatchSets) {
            choiceLists.add(simpleMatchSet.getSimpleAssociableChoices());
        }
        itemController.shuffleInteractionChoiceOrders(this, choiceLists);
    }

    @Override
    public boolean validateResponse(ItemSessionController itemController, Value responseValue) {
        /* Extract response values */
        final List<DirectedPairValue> responseAssociations = new ArrayList<DirectedPairValue>();
        if (responseValue.isNull()) {
            /* Empty response */
        }
        else if (responseValue.getCardinality().isList()) {
            /* (Multiple response) */
            for (final SingleValue association : (ListValue) responseValue) {
                responseAssociations.add((DirectedPairValue) association);
            }
        }
        else {
            /* (Single response) */
            responseAssociations.add((DirectedPairValue) responseValue);
        }

        /* Validate min/max */
        final int maxAssociations = getMaxAssociations().intValue();
        final int minAssociations = getMinAssociations().intValue();
        if (responseAssociations.size() < minAssociations) {
            return false;
        }
        else if (maxAssociations != 0 && responseAssociations.size() > maxAssociations) {
            return false;
        }

        /* Valiate each value */
        final List<SimpleMatchSet> simpleMatchSets = getSimpleMatchSets();
        final List<SimpleAssociableChoice> leftChoices = simpleMatchSets.get(0).getSimpleAssociableChoices();
        final List<SimpleAssociableChoice> rightChoices = simpleMatchSets.get(1).getSimpleAssociableChoices();
        final Map<Identifier, Integer> responseLeftAssociationCounts = new HashMap<Identifier, Integer>();
        final Map<Identifier, Integer> responseRightAssociationCounts = new HashMap<Identifier, Integer>();
        for (final SimpleAssociableChoice leftChoice : leftChoices) {
            responseLeftAssociationCounts.put(leftChoice.getIdentifier(), Integer.valueOf(0));
        }
        for (final SimpleAssociableChoice rightChoice : rightChoices) {
            responseRightAssociationCounts.put(rightChoice.getIdentifier(), Integer.valueOf(0));
        }

        for (final DirectedPairValue association : responseAssociations) {
            final Identifier leftIdentifier = association.sourceValue();
            final Identifier rightIdentifier = association.destValue();
            final Integer leftCount = responseLeftAssociationCounts.get(leftIdentifier);
            final Integer rightCount = responseRightAssociationCounts.get(rightIdentifier);
            if (leftCount == null || rightCount == null) { /* (Bad identifier in response) */
                return false;
            }
            responseLeftAssociationCounts.put(leftIdentifier, leftCount + 1);
            responseRightAssociationCounts.put(rightIdentifier, rightCount + 1);
        }

        for (final SimpleAssociableChoice leftChoice : leftChoices) {
            final Integer leftCount = responseLeftAssociationCounts.get(leftChoice.getIdentifier());
            if (!validateResponseChoice(leftChoice, leftCount)) {
                return false;
            }
        }
        for (final SimpleAssociableChoice rightChoice : rightChoices) {
            final Integer rightCount = responseRightAssociationCounts.get(rightChoice.getIdentifier());
            if (!validateResponseChoice(rightChoice, rightCount)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateResponseChoice(SimpleAssociableChoice choice, int responseAssociateCount) {
        final int matchMin = choice.getMatchMin();
        final int matchMax = choice.getMatchMax();
        if (responseAssociateCount < matchMin) {
            return false;
        }
        else if (matchMax != 0 && responseAssociateCount > matchMax) {
            return false;
        }
        return true;
    }
}