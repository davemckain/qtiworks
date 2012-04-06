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

import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.graphic.HotspotChoiceGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.HotspotChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.HotspotChoiceContainer;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.Collections;
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
public class GraphicOrderInteraction extends GraphicInteraction implements HotspotChoiceContainer {

    private static final long serialVersionUID = 1043633373106381307L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "graphicOrderInteraction";

    /** Name of maxChoices attribute in xml schema. */
    public static String ATTR_MAX_CHOICES_NAME = "maxChoices";

    /** Name of minChoices attribute in xml schema. */
    public static String ATTR_MIN_CHOICES_NAME = "minChoices";

    public GraphicOrderInteraction(XmlNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_CHOICES_NAME, false));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_CHOICES_NAME, false));

        getNodeGroups().add(new HotspotChoiceGroup(this, 1));
    }

    /**
     * Gets an unmodifiable list of the child elements. Use the other
     * methods on GraphicOrderInteraction to add children to the correct group.
     */
    @Override
    public List<? extends XmlNode> getChildren() {
        final List<XmlNode> children = new ArrayList<XmlNode>();
        children.addAll(super.getChildren());
        children.addAll(getNodeGroups().getHotspotChoiceGroup().getHotspotChoices());

        return Collections.unmodifiableList(children);
    }

    /**
     * Sets new value of maxChoices attribute.
     * 
     * @param maxChoices new value of maxChoices attribute
     * @see #getMaxChoices
     */
    public void setMaxChoices(Integer maxChoices) {
        getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).setValue(maxChoices);
    }

    /**
     * Gets value of maxChoices attribute.
     * 
     * @return value of maxChoices attribute
     * @see #setMaxChoices
     */
    public Integer getMaxChoices() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).getComputedValue();
    }

    /**
     * Sets new value of minChoices attribute.
     * 
     * @param minChoices new value of minChoices attribute
     * @see #getMinChoices
     */
    public void setMinChoices(Integer minChoices) {
        getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).setValue(minChoices);
    }

    /**
     * Gets value of minChoices attribute.
     * 
     * @return value of minChoices attribute
     * @see #setMinChoices
     */
    public Integer getMinChoices() {
        return getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).getComputedValue();
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
    public HotspotChoice getHotspotChoice(Identifier identifier) {
        for (final HotspotChoice choice : getHotspotChoices()) {
            if (choice.getIdentifier() != null && choice.getIdentifier().equals(identifier)) {
                return choice;
            }
        }

        return null;
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);

        if (getMinChoices() != null && getMinChoices() < 1) {
            context.add(new ValidationError(this, "Minimum number of choices can't be less than one"));
        }

        if (getMaxChoices() != null && getMinChoices() != null && getMaxChoices() < getMinChoices()) {
            context.add(new ValidationError(this, "Maximum number of choices must be greater or equal to minimum number of choices"));
        }

        if (getMaxChoices() != null && getMaxChoices() > getHotspotChoices().size()) {
            context.add(new ValidationError(this, "Maximum number of choices cannot be larger than the number of choice children"));
        }

        if (getResponseIdentifier() != null) {
            final ResponseDeclaration declaration = getResponseDeclaration();
            if (declaration != null && declaration.getBaseType() != null && !declaration.getBaseType().isIdentifier()) {
                context.add(new ValidationError(this, "Response variable must have identifier base type"));
            }

            if (declaration != null && declaration.getCardinality() != null && !declaration.getCardinality().isOrdered()) {
                context.add(new ValidationError(this, "Response variable must have ordered cardinality"));
            }
        }
    }


    @Override
    public boolean validateResponse(ItemSessionController itemController, Value responseValue) {
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