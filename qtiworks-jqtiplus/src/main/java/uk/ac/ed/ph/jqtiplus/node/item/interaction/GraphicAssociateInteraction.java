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

import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.graphic.AssociableHotspotGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.AssociableHotspot;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.AssociableHotspotContainer;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.PairValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A graphic associate interaction is a graphic interaction with a
 * corresponding set of choices that are defined as areas of the graphic
 * image. The candidate's task is to associate the areas (hotspots) with
 * each other. The graphic associate interaction should only be used
 * when the graphical relationship of the choices with respect to each
 * other (as represented by the graphic image) is important to the needs
 * of the item. Otherwise, associateInteraction should be used instead
 * with separate Material for each option.
 * The delivery engine must clearly indicate all defined area(s) of the image.
 * The associateHotspotInteraction must be bound to a response variable
 * with base-type pair and either single or multiple cardinality.
 * Attribute : maxAssociations [1]: integer = 1
 * The maximum number of associations that the candidate is allowed to make.
 * If maxAssociations is 0 there is no restriction. If maxAssociations is
 * greater than 1 (or 0) then the interaction must be bound to a response
 * with multiple cardinality.
 * Contains : associableHotspot [1..*]
 * The hotspots that define the choices that are to be associated by the
 * candidate. If the delivery system does not support pointer-based
 * selection then the order in which the choices are given must be the
 * order in which they are offered to the candidate for selection. For
 * example, the 'tab order' in simple keyboard navigation.
 *
 * @author Jonathon Hare
 */
public final class GraphicAssociateInteraction extends GraphicInteraction implements AssociableHotspotContainer {

    private static final long serialVersionUID = -4225511237180886978L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "graphicAssociateInteraction";

    /** Name of maxAssociations attribute in xml schema. */
    public static final String ATTR_MAX_ASSOCIATIONS_NAME = "maxAssociations";

    /** Default value of maxAssociations attribute. */
    public static final int ATTR_MAX_ASSOCIATIONS_DEFAULT_VALUE = 1;

    public GraphicAssociateInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_ASSOCIATIONS_NAME, ATTR_MAX_ASSOCIATIONS_DEFAULT_VALUE, true));

        getNodeGroups().add(new AssociableHotspotGroup(this, 1));
    }

    public int getMaxAssociations() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_ASSOCIATIONS_NAME).getComputedNonNullValue();
    }

    public void setMaxAssociations(final int maxAssociations) {
        getAttributes().getIntegerAttribute(ATTR_MAX_ASSOCIATIONS_NAME).setValue(Integer.valueOf(maxAssociations));
    }


    public List<AssociableHotspot> getAssociableHotspots() {
        return getNodeGroups().getAssociableHotspotGroup().getAssociableHotspots();
    }

    /**
     * Gets associableHotspot child with given identifier or null.
     *
     * @param identifier given identifier
     * @return associableHotspot with given identifier or null
     */
    public AssociableHotspot getAssociableHotspot(final Identifier identifier) {
        for (final AssociableHotspot choice : getAssociableHotspots()) {
            if (choice.getIdentifier() != null && choice.getIdentifier().equals(identifier)) {
                return choice;
            }
        }

        return null;
    }

    @Override
    public void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
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
    public boolean validateResponse(final InteractionBindingContext interactionBindingContext, final Value responseValue) {
        /* Extract response values */
        final List<PairValue> responseAssociations = new ArrayList<PairValue>();
        if (responseValue.isNull()) {
            /* (Empty response) */
        }
        else if (responseValue.getCardinality().isList()) {
            /* (Container response) */
            for (final SingleValue association : (ListValue) responseValue) {
                responseAssociations.add((PairValue) association);
            }
        }
        else {
            /* (Single response) */
            responseAssociations.add((PairValue) responseValue);
        }

        /* Check constraints */
        final int maxAssociations = getMaxAssociations();
        if (maxAssociations != 0 && responseAssociations.size() > maxAssociations) {
            return false;
        }

        /* Validate each assocation */
        final List<AssociableHotspot> associableHotspots = getAssociableHotspots();
        final Map<Identifier, Integer> responseAssociationCounts = new HashMap<Identifier, Integer>();
        for (final AssociableHotspot associableHostspot : associableHotspots) {
            responseAssociationCounts.put(associableHostspot.getIdentifier(), Integer.valueOf(0));
        }
        for (final PairValue responseAssociation : responseAssociations) {
            final Identifier sourceIdentifier = responseAssociation.sourceValue();
            final Identifier destIdentifier = responseAssociation.destValue();
            final Integer sourceCount = responseAssociationCounts.get(sourceIdentifier);
            final Integer destCount = responseAssociationCounts.get(destIdentifier);
            if (sourceCount == null || destCount == null) { /* (Bad identifier in response) */
                return false;
            }
            responseAssociationCounts.put(sourceIdentifier, sourceCount + 1);
            responseAssociationCounts.put(destIdentifier, destCount + 1);
        }
        for (final AssociableHotspot associableHotspot : associableHotspots) {
            final Integer assCount = responseAssociationCounts.get(associableHotspot.getIdentifier());
            if (!validateChoice(associableHotspot, assCount)) {
                return false;
            }
        }

        return true;
    }

    private boolean validateChoice(final AssociableHotspot choice, final int responseAssociateCount) {
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
