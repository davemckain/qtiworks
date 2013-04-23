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

import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.GapImgGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.graphic.AssociableHotspotGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapImg;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.AssociableHotspot;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.AssociableHotspotContainer;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
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
 * A graphic gap-match interaction is a graphical interaction with a set
 * of gaps that are defined as areas (hotspots) of the graphic image and
 * an additional set of gap choices that are defined outside the image.
 * The candidate must associate the gap choices with the gaps in the image
 * and be able to review the image with the gaps filled in context, as
 * indicated by their choices. Care should be taken when designing these
 * interactions to ensure that the gaps in the image are a suitable size
 * to receive the required gap choices. It must be clear to the candidate
 * which hotspot each choice has been associated with. When associated,
 * choices must appear wholly inside the gaps if at all possible and,
 * where overlaps are required, should not hide each other completely.
 * If the candidate indicates the association by positioning the choice
 * over the gap (e.g., drag and drop) the system should 'snap' it to the
 * nearest position that satisfies these requirements.
 * The graphicGapMatchInteraction must be bound to a response variable
 * with base-type directedPair and multiple cardinality. The choices
 * represent the source of the pairing and the gaps in the image (the
 * hotspots) the targets. Unlike the simple gapMatchInteraction, each
 * gap can have several choices associated with it if desired,
 * furthermore, the same choice may be associated with an
 * associableHotspot multiple times, in which case the corresponding
 * directed pair appears multiple times in the value of the response
 * variable.
 * Contains : gapImg [1..*]
 * An ordered list of choices for filling the gaps. There may be fewer
 * choices than gaps if required.
 * Contains : associableHotspot [1..*]
 * The hotspots that define the gaps that are to be filled by the
 * candidate. If the delivery system does not support pointer-based
 * selection then the order in which the gaps is given must be the order
 * in which they are offered to the candidate for selection. For example,
 * the 'tab order' in simple keyboard navigation. The default hotspot
 * must not be defined.
 *
 * @author Jonathon Hare
 */
public final class GraphicGapMatchInteraction extends GraphicInteraction implements AssociableHotspotContainer {

    private static final long serialVersionUID = 3722218691380560119L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "graphicGapMatchInteraction";

    public GraphicGapMatchInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getNodeGroups().add(new GapImgGroup(this, 1));
        getNodeGroups().add(new AssociableHotspotGroup(this, 1));
    }

    public List<GapImg> getGapImgs() {
        return getNodeGroups().getGapImgGroup().getGapImgs();
    }

    public List<AssociableHotspot> getAssociableHotspots() {
        return getNodeGroups().getAssociableHotspotGroup().getAssociableHotspots();
    }

    @Override
    public void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        if (responseDeclaration!=null) {
            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isDirectedPair()) {
                context.fireValidationError(this, "Response variable must have directed pair base type");
            }
            if (!responseDeclaration.getCardinality().isMultiple()) {
                context.fireValidationError(this, "Response variable must have multiple cardinality");
            }
        }

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

        /* Create hashes that will track the number of associations for each <gapImg> */
        final Map<Identifier, Integer> gapImgAssociationCounts = new HashMap<Identifier, Integer>();
        final List<GapImg> gapImgs = getGapImgs();
        for (final GapChoice gapImg : gapImgs) {
            gapImgAssociationCounts.put(gapImg.getIdentifier(), Integer.valueOf(0));
        }
        /* Same for each <associableChoice> */
        final Set<Identifier> associableHotspotIdentifiers = new HashSet<Identifier>();
        final Map<Identifier, Integer> responseAssociableHotspotCounts = new HashMap<Identifier, Integer>();
        final List<AssociableHotspot> associableHotspots = getAssociableHotspots();
        for (final AssociableHotspot hotspot : associableHotspots) {
            final Identifier hotspotIdentifier = hotspot.getIdentifier();
            associableHotspotIdentifiers.add(hotspotIdentifier);
            responseAssociableHotspotCounts.put(hotspotIdentifier, Integer.valueOf(0));
        }
        /* Go through each association in the response and tally things up */
        for (final DirectedPairValue responseAssociation : responseAssociations) {
            final Identifier gapImgIdentifier = responseAssociation.sourceValue();
            final Identifier hotspotIdentifier = responseAssociation.destValue();

            Integer count = responseAssociableHotspotCounts.get(hotspotIdentifier);
            if (count == null) { /* (Bad identifier in response) */
                return false;
            }
            responseAssociableHotspotCounts.put(hotspotIdentifier, count + 1);

            count = gapImgAssociationCounts.get(gapImgIdentifier);
            if (count == null) { /* (Bad identifier in response) */
                return false;
            }
            gapImgAssociationCounts.put(gapImgIdentifier, count + 1);
        }

        /* Make sure the correct number of associations were made to <gapImg> */
        for (final GapChoice gapImg : gapImgs) {
            if (!validateChoice(gapImg, gapImgAssociationCounts.get(gapImg.getIdentifier()))) {
                return false;
            }
        }

        /* Same for <associableHotspot> */
        for (final AssociableHotspot hotspot : associableHotspots) {
            if (!validateChoice(hotspot, responseAssociableHotspotCounts.get(hotspot.getIdentifier()))) {
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

    private boolean validateChoice(final AssociableHotspot hotspot, final int associateCount) {
        final int matchMin = hotspot.getMatchMin();
        final int matchMax = hotspot.getMatchMax();
        if (associateCount < matchMin) {
            return false;
        }
        else if (matchMax != 0 && associateCount > matchMax) {
            return false;
        }
        return true;
    }
}
