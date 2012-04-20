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

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.OrientationAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.SimpleChoiceGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleChoiceContainer;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.Orientation;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * In an order interaction the candidate's task is to reorder the choices, the
 * order in which the choices are displayed initially is significant. By default
 * the candidate's task is to order all of the choices but A subset of the choices
 * can be requested using the maxChoices and minChoices attributes. When specified,
 * the candidate must select A subset of the choices and impose an ordering on them.
 * If A default value is specified for the response variable associated with an
 * order interaction then its value should be used to override the order of the
 * choices specified here.
 * By its nature, an order interaction may be difficult to render in an unanswered
 * state, especially in the default case where all choices are to be ordered.
 * Implementors should be aware of the issues concerning the use of default values
 * described in the section on Response Variables.
 * The orderInteraction must be bound to A response variable with A baseType of
 * identifier and ordered cardinality only.
 * Contains : simpleChoice [1..*]
 * An ordered list of the choices that are displayed to the user. The order is the
 * initial order of the choices presented to the user, unless shuffle is true.
 * Attribute : shuffle [1]: boolean = false
 * If the shuffle attribute is true then the delivery engine must randomize the order
 * in which the choices are initially presented subject to the fixed attribute.
 * Attribute : minChoices [0..1]: integer
 * The minimum number of choices that the candidate must select and order to form A
 * valid response to the interaction. If specified, minChoices must be 1 or greater
 * but must not exceed the number of choices available. If unspecified, all of the
 * choices must be ordered and maxChoices is ignored.
 * Attribute : maxChoices [0..1]: integer
 * The maximum number of choices that the candidate may select and order when
 * responding to the interaction. Used in conjunction with minChoices, if specified,
 * maxChoices must be greater than or equal to minChoices and must not exceed the number
 * of choices available. If unspecified, all of the choices may be ordered.
 * Attribute : orientation [0..1]: orientation
 * The orientation attribute provides A hint to rendering systems that the ordering has
 * an inherent vertical or horizontal interpretation.
 *
 * @author Jonathon Hare
 */
public class OrderInteraction extends BlockInteraction implements SimpleChoiceContainer, Shuffleable {

    private static final long serialVersionUID = 4283024380579062066L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "orderInteraction";

    /** Name of shuffle attribute in xml schema. */
    public static final String ATTR_SHUFFLE_NAME = "shuffle";

    /** Default value of shuffle attribute. */
    public static final Boolean ATTR_SHUFFLE_DEFAULT_VALUE = Boolean.FALSE;

    /** Name of maxChoices attribute in xml schema. */
    public static final String ATTR_MAX_CHOICES_NAME = "maxChoices";

    /** Name of minChoices attribute in xml schema. */
    public static final String ATTR_MIN_CHOICES_NAME = "minChoices";

    /** Name of orientation attribute in xml schema. */
    public static final String ATTR_ORIENTATION_NAME = "orientation";

    public OrderInteraction(final XmlNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BooleanAttribute(this, ATTR_SHUFFLE_NAME, ATTR_SHUFFLE_DEFAULT_VALUE, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_CHOICES_NAME, false));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_CHOICES_NAME, false));
        getAttributes().add(new OrientationAttribute(this, ATTR_ORIENTATION_NAME, false));

        getNodeGroups().add(new SimpleChoiceGroup(this, 1));
    }

    /**
     * Gets an unmodifiable list of the child elements. Use the other
     * methods on OrderInteraction to add children to the correct group.
     */
    @Override
    public List<? extends XmlNode> getChildren() {
        final List<XmlNode> children = new ArrayList<XmlNode>();
        children.addAll(super.getChildren());
        children.addAll(getNodeGroups().getSimpleChoiceGroup().getSimpleChoices());

        return Collections.unmodifiableList(children);
    }

    /**
     * Sets new value of shuffle attribute.
     *
     * @param shuffle new value of shuffle attribute
     * @see #getShuffle
     */
    @Override
    public void setShuffle(final boolean shuffle) {
        getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).setValue(Boolean.valueOf(shuffle));
    }

    /**
     * Gets value of shuffle attribute.
     *
     * @return value of shuffle attribute
     * @see #setShuffle
     */
    @Override
    public boolean getShuffle() {
        return getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).getComputedNonNullValue();
    }

    /**
     * Sets new value of maxChoices attribute.
     *
     * @param maxChoices new value of maxChoices attribute
     * @see #getMaxChoices
     */
    public void setMaxChoices(final Integer maxChoices) {
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
    public void setMinChoices(final Integer minChoices) {
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
     * Sets new value of orientation attribute.
     *
     * @param orientation new value of orientation attribute
     * @see #getOrientation
     */
    public void setOrientation(final Orientation orientation) {
        getAttributes().getOrientationAttribute(ATTR_ORIENTATION_NAME).setValue(orientation);
    }

    /**
     * Gets value of orientation attribute.
     *
     * @return value of orientation attribute
     * @see #setOrientation
     */
    public Orientation getOrientation() {
        return getAttributes().getOrientationAttribute(ATTR_ORIENTATION_NAME).getComputedValue();
    }

    /**
     * Gets simpleChoice children.
     *
     * @return simpleChoice children
     */
    public List<SimpleChoice> getSimpleChoices() {
        return getNodeGroups().getSimpleChoiceGroup().getSimpleChoices();
    }

    /**
     * Gets simpleChoice child with given identifier or null.
     *
     * @param identifier given identifier
     * @return simpleChoice with given identifier or null
     */
    public SimpleChoice getSimpleChoice(final Identifier identifier) {
        for (final SimpleChoice choice : getSimpleChoices()) {
            if (choice.getIdentifier() != null && choice.getIdentifier().equals(identifier)) {
                return choice;
            }
        }

        return null;
    }

    @Override
    public void validate(final ValidationContext context) {
        super.validate(context);
        final Integer maxChoices = getMaxChoices();
        final Integer minChoices = getMinChoices();

        if (minChoices != null && minChoices.intValue() < 1) {
            context.add(new ValidationError(this, "Minimum number of choices can't be less than one"));
        }

        if (maxChoices != null && minChoices != null && maxChoices.intValue() < minChoices.intValue()) {
            context.add(new ValidationError(this, "Maximum number of choices must be greater or equal to minimum number of choices"));
        }

        if (maxChoices != null && maxChoices.intValue() > getSimpleChoices().size()) {
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
    public void initialize(final ItemSessionController itemSessionController) {
        super.initialize(itemSessionController);
        itemSessionController.shuffleInteractionChoiceOrder(this, getSimpleChoices());
    }

    @Override
    public boolean validateResponse(final ItemSessionController itemSessionController, final Value responseValue) {
        /* Extract response values */
        final Set<Identifier> responseChoiceIdentifiers = new HashSet<Identifier>();
        if (responseValue.isNull()) {
            /* (Empty response) */
        }
        else if (responseValue.getCardinality().isList()) {
            /* (Multiple response) */
            for (final SingleValue hotspotChoiceIdentifier : (ListValue) responseValue) {
                responseChoiceIdentifiers.add(((IdentifierValue) hotspotChoiceIdentifier).identifierValue());
            }
        }
        else {
            /* (Single response - this won't actually happen) */
            responseChoiceIdentifiers.add(((IdentifierValue) responseValue).identifierValue());
        }

        /* Validate min/max (if set) */
        final Integer maxChoices = getMaxChoices();
        final Integer minChoices = getMinChoices();
        if (maxChoices != null && minChoices != null) {
            if (responseChoiceIdentifiers.size() < minChoices.intValue() || responseChoiceIdentifiers.size() > maxChoices.intValue()) {
                return false;
            }
        }

        /* Check that each identifier is valid */
        final Set<Identifier> choiceIdentifiers = new HashSet<Identifier>();
        for (final SimpleChoice choice : getSimpleChoices()) {
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