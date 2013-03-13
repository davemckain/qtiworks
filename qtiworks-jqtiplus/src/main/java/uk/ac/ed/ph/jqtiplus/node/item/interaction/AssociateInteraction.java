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
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.SimpleAssociableChoiceGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleAssociableChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleAssociableChoiceContainer;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.PairValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An associate interaction is a blockInteraction that presents candidates
 * with a number of choices and allows them to create associations between them.
 * The associateInteraction must be bound to a response variable with base-type
 * pair and either single or multiple cardinality.
 * Attribute : shuffle [1]: boolean = false
 * If the shuffle attribute is true then the delivery engine must randomize the order
 * in which the choices are presented subject to the fixed attribute of the choice.
 * Attribute : maxAssociations [1]: integer = 1
 * The maximum number of associations that the candidate is allowed to make. If
 * maxAssociations is 0 then there is no restriction. If maxAssociations is greater
 * than 1 (or 0) then the interaction must be bound to a response with multiple cardinality.
 * Attribute : minAssociations [0..1]: integer = 0
 * The minimum number of associations that the candidate is required to make to form a valid
 * response. If minAssociations is 0 then the candidate is not required to make any associations.
 * minAssociations must be less than or equal to the limit imposed by maxAssociations.
 * Contains : simpleAssociableChoice [1..*]
 * An ordered set of choices.
 *
 * @author Jonathon Hare
 */
public final class AssociateInteraction extends BlockInteraction implements SimpleAssociableChoiceContainer,
        Shuffleable<SimpleAssociableChoice> {

    private static final long serialVersionUID = -6064451970355204988L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "associateInteraction";

    /** Name of shuffle attribute in xml schema. */
    public static final String ATTR_SHUFFLE_NAME = "shuffle";

    /** Name of maxAssociations attribute in xml schema. */
    public static final String ATTR_MAX_ASSOCIATIONS_NAME = "maxAssociations";

    /** Default value of maxAssociations attribute. */
    public static final int ATTR_MAX_ASSOCIATIONS_DEFAULT_VALUE = 1;

    /** Name of minAssociations attribute in xml schema. */
    public static final String ATTR_MIN_ASSOCIATIONS_NAME = "minAssociations";

    /** Default value of minAssociations attribute. */
    public static final int ATTR_MIN_ASSOCIATIONS_DEFAULT_VALUE = 0;

    public AssociateInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BooleanAttribute(this, ATTR_SHUFFLE_NAME, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_ASSOCIATIONS_NAME, ATTR_MAX_ASSOCIATIONS_DEFAULT_VALUE, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_ASSOCIATIONS_NAME, ATTR_MIN_ASSOCIATIONS_DEFAULT_VALUE, false));

        getNodeGroups().add(new SimpleAssociableChoiceGroup(this, 1));
    }

    @Override
    public boolean getShuffle() {
        return getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).getComputedNonNullValue();
    }

    @Override
    public void setShuffle(final boolean shuffle) {
        getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).setValue(Boolean.valueOf(shuffle));
    }


    public int getMaxAssociations() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_ASSOCIATIONS_NAME).getComputedNonNullValue();
    }

    public void setMaxAssociations(final int maxAssociations) {
        getAttributes().getIntegerAttribute(ATTR_MAX_ASSOCIATIONS_NAME).setValue(Integer.valueOf(maxAssociations));
    }


    public int getMinAssociations() {
        return getAttributes().getIntegerAttribute(ATTR_MIN_ASSOCIATIONS_NAME).getComputedNonNullValue();
    }

    public void setMinAssociations(final Integer minAssociations) {
        getAttributes().getIntegerAttribute(ATTR_MIN_ASSOCIATIONS_NAME).setValue(minAssociations);
    }


    public List<SimpleAssociableChoice> getSimpleAssociableChoices() {
        return getNodeGroups().getSimpleAssociableChoiceGroup().getSimpleAssociableChoices();
    }

    /**
     * Gets simpleAssociableChoice child with given identifier or null.
     *
     * @param identifier given identifier
     * @return simpleAssociableChoice with given identifier or null
     */
    public SimpleAssociableChoice getSimpleAssociableChoice(final Identifier identifier) {
        for (final SimpleAssociableChoice choice : getSimpleAssociableChoices()) {
            if (choice.getIdentifier() != null && choice.getIdentifier().equals(identifier)) {
                return choice;
            }
        }
        return null;
    }

    @Override
    public void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        if (getMinAssociations() > getMaxAssociations()) {
            context.fireValidationError(this, "Minimum number of associations must be less than or equal to maximum number of associations");
        }

        if (responseDeclaration != null) {
            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isPair()) {
                context.fireValidationError(this, "Response variable must have pair base type");
            }

            if (getMaxAssociations() != 1 && !responseDeclaration.getCardinality().isMultiple()) {
                context.fireValidationError(this, "Response variable must have multiple cardinality when maxAssociations is not 1");
            }
            else if (!(responseDeclaration.getCardinality().isSingle() || responseDeclaration.getCardinality().isMultiple())) {
                context.fireValidationError(this, "Response variable must have single or multiple cardinality");
            }
        }
    }

    @Override
    public List<List<SimpleAssociableChoice>> computeShuffleableChoices() {
        return Interaction.wrapSingleChoiceList(getSimpleAssociableChoices());
    }

    @Override
    public boolean validateResponse(final InteractionBindingContext interactionBindingContext, final Value responseValue) {
        /* Extract response values */
        final List<PairValue> responseAssociations = new ArrayList<PairValue>();
        if (responseValue.isNull()) {
            /* (Empty response) */
        }
        else if (responseValue.getCardinality().isList()) {
            /* (Container response) */
            for (final PairValue association : ((ListValue) responseValue).values(PairValue.class)) {
                responseAssociations.add(association);
            }
        }
        else {
            /* (Single response) */
            responseAssociations.add((PairValue) responseValue);
        }

        /* Validate min/min */
        final int maxAssociations = getMaxAssociations();
        final int minAssociations = getMinAssociations();
        if (responseAssociations.size() < minAssociations) {
            return false;
        }
        if (maxAssociations != 0 && responseAssociations.size() > maxAssociations) {
            return false;
        }

        /* Validate each choice */
        final List<SimpleAssociableChoice> choices = getSimpleAssociableChoices();
        final Map<Identifier, Integer> responseChoiceCounts = new HashMap<Identifier, Integer>();
        for (final SimpleAssociableChoice choice : choices) {
            responseChoiceCounts.put(choice.getIdentifier(), Integer.valueOf(0));
        }
        for (final PairValue responseAssociation : responseAssociations) {
            final Identifier sourceIdentifier = responseAssociation.sourceValue();
            final Identifier destIdentifier = responseAssociation.destValue();
            final Integer sourceCount = responseChoiceCounts.get(sourceIdentifier);
            final Integer destCount = responseChoiceCounts.get(destIdentifier);
            if (sourceCount == null || destCount == null) { /* (Bad identifier in response) */
                return false;
            }
            responseChoiceCounts.put(sourceIdentifier, sourceCount + 1);
            responseChoiceCounts.put(destIdentifier, destCount + 1);
        }

        for (final SimpleAssociableChoice choice : choices) {
            final Integer responseChoiceCount = responseChoiceCounts.get(choice.getIdentifier());
            if (!validateChoice(choice, responseChoiceCount.intValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean validateChoice(final SimpleAssociableChoice choice, final int responseCountCount) {
        final int matchMin = choice.getMatchMin();
        final int matchMax = choice.getMatchMax();
        if (responseCountCount < matchMin) {
            return false;
        }
        else if (matchMax != 0 && responseCountCount > matchMax) {
            return false;
        }
        return true;
    }
}