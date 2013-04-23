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
import uk.ac.ed.ph.jqtiplus.group.item.interaction.graphic.HotspotChoiceGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.HotspotChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.HotspotChoiceContainer;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A graphic order interaction is a graphic interaction with a corresponding
 * set of choices that are defined as areas of the graphic image. The
 * candidate's task is to impose an ordering on the areas (hotspots). The
 * order hotspot interaction should only be used when the spacial relationship
 * of the choices with respect to each other (as represented by the graphic
 * image) is important to the needs of the item. Otherwise, orderInteraction
 * should be used instead with separate material for each option.
 * The delivery engine must clearly indicate all defined area(s) of the image.
 * The order hotspot interaction must be bound to a response variable with a
 * baseType of identifier and ordered cardinality.
 * Contains : hotspotChoice [1..*]
 * The hotspots that define the choices that are to be ordered by the candidate.
 * If the delivery system does not support pointer-based selection then the order
 * in which the choices are given must be the order in which they are offered to
 * the candidate for selection. For example, the 'tab order' in hotspot keyboard
 * navigation.
 * Attribute : minChoices [0..1]: integer
 * The minimum number of choices that the candidate must select and order to
 * form a valid response to the interaction. If specified, minChoices must be 1
 * or greater but must not exceed the number of choices available. If unspecified,
 * all of the choices must be ordered and maxChoices is ignored.
 * Attribute : maxChoices [0..1]: integer
 * The maximum number of choices that the candidate may select and order when
 * responding to the interaction. Used in conjunction with minChoices, if specified,
 * maxChoices must be greater than or equal to minChoices and must not exceed the
 * number of choices available. If unspecified, all of the choices may be ordered.
 *
 * @author Jonathon Hare
 */
public final class GraphicOrderInteraction extends GraphicInteraction implements HotspotChoiceContainer {

    private static final long serialVersionUID = 1043633373106381307L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "graphicOrderInteraction";

    /** Name of maxChoices attribute in xml schema. */
    public static final String ATTR_MAX_CHOICES_NAME = "maxChoices";

    /** Name of minChoices attribute in xml schema. */
    public static final String ATTR_MIN_CHOICES_NAME = "minChoices";

    public GraphicOrderInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_CHOICES_NAME, false));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_CHOICES_NAME, false));

        getNodeGroups().add(new HotspotChoiceGroup(this, 1));
    }

    public Integer getMaxChoices() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).getComputedValue();
    }

    public void setMaxChoices(final Integer maxChoices) {
        getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).setValue(maxChoices);
    }


    public Integer getMinChoices() {
        return getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).getComputedValue();
    }

    public void setMinChoices(final Integer minChoices) {
        getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).setValue(minChoices);
    }


    /**
     * Gets hotspotChoice children.
     *
     * @return hotspotChoice children
     */
    public List<HotspotChoice> getHotspotChoices() {
        return getNodeGroups().getHotspotChoiceGroup().getHotspotChoices();
    }

    /**
     * Gets hotspotChoice child with given identifier or null.
     *
     * @param identifier given identifier
     * @return hotspotChoice with given identifier or null
     */
    public HotspotChoice getHotspotChoice(final Identifier identifier) {
        for (final HotspotChoice choice : getHotspotChoices()) {
            if (choice.getIdentifier() != null && choice.getIdentifier().equals(identifier)) {
                return choice;
            }
        }

        return null;
    }

    @Override
    public void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        final Integer minChoices = getMinChoices();
        final Integer maxChoices = getMaxChoices();

        if (minChoices != null && minChoices.intValue() < 1) {
            context.fireValidationError(this, "Minimum number of choices can't be less than one");
        }

        if (maxChoices != null && minChoices != null && maxChoices.intValue() < minChoices.intValue()) {
            context.fireValidationError(this, "Maximum number of choices must be greater or equal to minimum number of choices");
        }

        if (maxChoices != null && maxChoices.intValue() > getHotspotChoices().size()) {
            context.fireValidationError(this, "Maximum number of choices cannot be larger than the number of choice children");
        }

        if (responseDeclaration!=null) {
            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isIdentifier()) {
                context.fireValidationError(this, "Response variable must have identifier base type");
            }

            if (!responseDeclaration.getCardinality().isOrdered()) {
                context.fireValidationError(this, "Response variable must have ordered cardinality");
            }
        }
    }


    @Override
    public boolean validateResponse(final InteractionBindingContext interactionBindingContext, final Value responseValue) {
        /* Extract response values */
        final Set<Identifier> responseChoiceIdentifiers = new HashSet<Identifier>();
        if (responseValue.isNull()) {
            /* (Empty response) */
        }
        else if (responseValue.getCardinality().isList()) {
            /* (Container response) */
            for (final SingleValue hotspotChoiceIdentifier : (ListValue) responseValue) {
                responseChoiceIdentifiers.add(((IdentifierValue) hotspotChoiceIdentifier).identifierValue());
            }
        }
        else {
            /* (Single response - this won't actually happen) */
            responseChoiceIdentifiers.add(((IdentifierValue) responseValue).identifierValue());
        }

        /* Chheck min/max (if set) */
        final Integer maxChoices = getMaxChoices();
        final Integer minChoices = getMinChoices();
        if (maxChoices != null && minChoices != null) {
            if (responseChoiceIdentifiers.size() < minChoices.intValue() || responseChoiceIdentifiers.size() > maxChoices.intValue()) {
                return false;
            }
        }

        /* Check that each identifier is valid */
        final Set<Identifier> choiceIdentifiers = new HashSet<Identifier>();
        for (final HotspotChoice choice : getHotspotChoices()) {
            choiceIdentifiers.add(choice.getIdentifier());
        }
        for (final Identifier choiceIdentifier : responseChoiceIdentifiers) {
            if (!choiceIdentifiers.contains(choiceIdentifier)) {
                return false;
            }
        }

        return true;
    }
}