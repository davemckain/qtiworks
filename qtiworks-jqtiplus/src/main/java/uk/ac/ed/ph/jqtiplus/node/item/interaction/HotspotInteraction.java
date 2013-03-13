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
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A hotspot interaction is a graphical interaction with a corresponding
 * set of choices that are defined as areas of the graphic image. The
 * candidate's task is to select one or more of the areas (hotspots).
 * The hotspot interaction should only be used when the spatial relationship
 * of the choices with respect to each other (as represented by the graphic
 * image) is important to the needs of the item. Otherwise, choiceInteraction
 * should be used instead with separate material for each option.
 * The delivery engine must clearly indicate the selected area(s) of the
 * image and may also indicate the unselected areas as well. Interactions
 * with hidden hotspots are achieved with the selectPointInteraction.
 * The hotspot interaction must be bound to a response variable with a
 * baseType of identifier and single or multiple cardinality.
 * Attribute : maxChoices [1]: integer = 1
 * The maximum number of choices that the candidate is allowed to select.
 * If maxChoices is 0 there is no restriction. If maxChoices is greater
 * than 1 (or 0) then the interaction must be bound to a response with
 * multiple cardinality.
 * Attribute : minChoices [0..1]: integer = 0
 * The minimum number of choices that the candidate is required to select
 * to form a valid response. If minChoices is 0 then the candidate is not
 * required to select any choices. minChoices must be less than or equal
 * to the limit imposed by maxChoices.
 * Contains : hotspotChoice [1..*] {ordered}
 * The hotspots that define the choices that can be selected by the candidate.
 * If the delivery system does not support pointer-based selection then the
 * order in which the choices are given must be the order in which they are
 * offered to the candidate for selection. For example, the 'tab order' in
 * simple keyboard navigation. If hotspots overlap then those listed first
 * hide overlapping hotspots that appear later. The default hotspot, if
 * defined, must appear last.
 *
 * @author Jonathon Hare
 */
public final class HotspotInteraction extends GraphicInteraction implements HotspotChoiceContainer {

    private static final long serialVersionUID = 7817305977968014345L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "hotspotInteraction";

    /** Name of maxChoices attribute in xml schema. */
    public static final String ATTR_MAX_CHOICES_NAME = "maxChoices";

    /** Default value of maxChoices attribute. */
    public static final int ATTR_MAX_CHOICES_DEFAULT_VALUE = 0;

    /** Name of minChoices attribute in xml schema. */
    public static final String ATTR_MIN_CHOICES_NAME = "minChoices";

    /** Default value of minChoices attribute. */
    public static final int ATTR_MIN_CHOICES_DEFAULT_VALUE = 0;

    public HotspotInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_CHOICES_NAME, ATTR_MAX_CHOICES_DEFAULT_VALUE, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_CHOICES_NAME, ATTR_MIN_CHOICES_DEFAULT_VALUE, false));

        getNodeGroups().add(new HotspotChoiceGroup(this, 1));
    }

    public int getMaxChoices() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).getComputedNonNullValue();
    }

    public void setMaxChoices(final int maxChoices) {
        getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).setValue(Integer.valueOf(maxChoices));
    }


    public int getMinChoices() {
        return getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).getComputedNonNullValue();
    }

    public void setMinChoices(final Integer minChoices) {
        getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).setValue(minChoices);
    }



    public List<HotspotChoice> getHotspotChoices() {
        return getNodeGroups().getHotspotChoiceGroup().getHotspotChoices();
    }


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
        final int minChoices = getMinChoices();
        final int maxChoices = getMaxChoices();

        if (maxChoices < minChoices) {
            context.fireValidationError(this, "Maximum number of choices must be greater or equal to minimum number of choices");
        }

        if (responseDeclaration != null) {
            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isIdentifier()) {
                context.fireValidationError(this, "Response variable must have identifier base type");
            }

            if (maxChoices == 1 && !responseDeclaration.hasCardinality(Cardinality.SINGLE, Cardinality.MULTIPLE)) {
                context.fireValidationError(this, "Response variable must have single or multiple cardinality");
            }

            if (maxChoices != 1 && !responseDeclaration.hasCardinality(Cardinality.MULTIPLE)) {
                context.fireValidationError(this, "Response variable must have multiple cardinality");
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
            /* (Single response) */
            responseChoiceIdentifiers.add(((IdentifierValue) responseValue).identifierValue());
        }

        /* Validate min/max */
        final int maxChoices = getMaxChoices();
        final int minChoices = getMinChoices();
        if (responseChoiceIdentifiers.size() < minChoices) {
            return false;
        }
        if (maxChoices != 0 && responseChoiceIdentifiers.size() > maxChoices) {
            return false;
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