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
import uk.ac.ed.ph.jqtiplus.group.content.BlockStaticGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.GapChoiceGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.BlockStatic;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapChoiceContainer;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.content.Gap;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.DirectedPairValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A gap match interaction is a blockInteraction that contains a number
 * gaps that the candidate can fill from an associated set of choices.
 * The candidate must be able to review the content with the gaps
 * filled in context, as indicated by their choices.
 * The gapMatchInteraction must be bound to a response variable with
 * base-type directedPair and either single or multiple cardinality,
 * depending on the number of gaps. The choices represent the source
 * of the pairing and gaps the targets. Each gap can have at most one
 * choice associated with it. The maximum occurrence of the choices is
 * controlled by the matchMax attribute of gapChoice.
 * Attribute : shuffle [1]: boolean = false
 * If the shuffle attribute is true then the delivery engine must
 * randomize the order in which the choices are presented (not the gaps),
 * subject to the fixed attribute of the choices themselves.
 * Contains : gapChoice [1..*]
 * An ordered list of choices for filling the gaps. There may be fewer
 * choices than gaps if required.
 * Contains : blockStatic [1..*]
 * The content of the interaction is simply a piece of content that
 * contains the gaps. If the block contains more than one gap then the
 * interaction must be bound to a response with multiple cardinality.
 *
 * @author Jonathon Hare
 */
public final class GapMatchInteraction extends BlockInteraction implements GapChoiceContainer,
        Shuffleable<GapChoice> {

    private static final long serialVersionUID = 5859875788265167537L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "gapMatchInteraction";

    /** Name of shuffle attribute in xml schema. */
    public static final String ATTR_SHUFFLE_NAME = "shuffle";

    /** Default value of shuffle attribute. */
    public static final boolean ATTR_SHUFFLE_DEFAULT_VALUE = false;

    public GapMatchInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BooleanAttribute(this, ATTR_SHUFFLE_NAME, ATTR_SHUFFLE_DEFAULT_VALUE, true));

        getNodeGroups().add(new GapChoiceGroup(this, 1));
        getNodeGroups().add(new BlockStaticGroup(this, 1));
    }


    @Override
    public boolean getShuffle() {
        return getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).getComputedNonNullValue();
    }


    @Override
    public void setShuffle(final boolean shuffle) {
        getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).setValue(Boolean.valueOf(shuffle));
    }

    public List<GapChoice> getGapChoices() {
        return getNodeGroups().getGapChoiceGroup().getGapChoices();
    }

    public List<BlockStatic> getBlockStatics() {
        return getNodeGroups().getBlockStaticGroup().getBlockStatics();
    }

    /**
     * Gets gapChoice child with given identifier or null.
     *
     * @param identifier given identifier
     * @return gapChoice with given identifier or null
     */
    public GapChoice getGapChoice(final Identifier identifier) {
        for (final GapChoice choice : getGapChoices()) {
            if (choice.getIdentifier() != null && choice.getIdentifier().equals(identifier)) {
                return choice;
            }
        }

        return null;
    }

    @Override
    protected void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        if (getResponseIdentifier() != null) {
            if (responseDeclaration!=null) {
                if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isDirectedPair()) {
                    context.fireValidationError(this, "Response variable must have directed pair base type");
                }

                if (countGaps() == 1 &&
                        !responseDeclaration.getCardinality().isSingle() &&
                        !responseDeclaration.getCardinality().isMultiple()) {
                    context.fireValidationError(this, "Response variable must have single or multiple cardinality");
                }

                if (countGaps() != 1 && !responseDeclaration.getCardinality().isMultiple()) {
                    context.fireValidationError(this, "Response variable must have multiple cardinality");
                }
            }
        }
    }

    private List<Gap> findGaps() {
        return QueryUtils.search(Gap.class, getNodeGroups().getBlockStaticGroup().getBlockStatics());
    }

    private int countGaps() {
        return findGaps().size();
    }

    @Override
    public List<List<GapChoice>> computeShuffleableChoices() {
        return Interaction.wrapSingleChoiceList(getGapChoices());
    }

    @Override
    public boolean validateResponse(final InteractionBindingContext interactionBindingContext, final Value responseValue) {
        /* Extract response values */
        final List<DirectedPairValue> responseAssociations = new ArrayList<DirectedPairValue>();
        if (responseValue.isNull()) {
            /* Empty response */
        }
        else if (responseValue.getCardinality().isList()) {
            /* (Container response) */
            for (final SingleValue association : (ListValue) responseValue) {
                responseAssociations.add((DirectedPairValue) association);
            }
        }
        else {
            /* (Single response) */
            responseAssociations.add((DirectedPairValue) responseValue);
        }

        /* Create hashes that will track the number of associations for each gapChoice */
        final List<GapChoice> gapChoices = getGapChoices();
        final Map<Identifier, Integer> responseGapChoiceAssociationCounts = new HashMap<Identifier, Integer>();
        for (final GapChoice gapChoice : gapChoices) {
            responseGapChoiceAssociationCounts.put(gapChoice.getIdentifier(), Integer.valueOf(0));
        }
        /* Work out which <gap>s require an association */
        final Set<Identifier> gapIdentifiers = new HashSet<Identifier>();
        final Set<Identifier> requiredGapIdentifiers = new HashSet<Identifier>();
        final Map<Identifier, Integer> responseGapAssociationCounts = new HashMap<Identifier, Integer>();
        final List<Gap> gaps = findGaps();
        for (final Gap gap : findGaps()) {
            final Identifier gapIdentifier = gap.getIdentifier();
            gapIdentifiers.add(gapIdentifier);
            responseGapAssociationCounts.put(gapIdentifier, Integer.valueOf(0));
            if (gap.getRequired()) {
                requiredGapIdentifiers.add(gapIdentifier);
            }
        }
        /* Go through each association in the response and tally things up */
        for (final DirectedPairValue responseAssociation : responseAssociations) {
            final Identifier gapTextIdentifier = responseAssociation.sourceValue();
            final Identifier gapIdentifier = responseAssociation.destValue();

            if (!gapIdentifiers.contains(gapIdentifier)) { /* (Bad identifier in response) */
                return false;
            }
            responseGapAssociationCounts.put(gapIdentifier, responseGapAssociationCounts.get(gapIdentifier) + 1);
            requiredGapIdentifiers.remove(gapIdentifier);

            final Integer count = responseGapChoiceAssociationCounts.get(gapTextIdentifier);
            if (count == null) { /* (Bad identifier in response) */
                return false;
            }
            responseGapChoiceAssociationCounts.put(gapTextIdentifier, count + 1);
        }

        /* Make sure the correct number of associations were made to gapChoices*/
        for (final GapChoice gapChoice : gapChoices) {
            if (!validateChoice(gapChoice, responseGapChoiceAssociationCounts.get(gapChoice.getIdentifier()))) {
                return false;
            }
        }

        /* Make sure all required <gap> associations were used */
        if (!requiredGapIdentifiers.isEmpty()) {
            return false;
        }
        /* Make sure each <gap> has no more than 1 association */
        for (final Gap gap : gaps) {
            if (responseGapAssociationCounts.get(gap.getIdentifier()) > 1) {
                return false;
            }
        }

        return true;
    }

    private boolean validateChoice(final GapChoice choice, final int responseAssociateCount) {
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