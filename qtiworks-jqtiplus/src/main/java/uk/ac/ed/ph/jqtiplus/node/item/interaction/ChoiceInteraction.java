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
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The choice interaction presents A set of choices to the candidate. The
 * candidate's task is to select one or more of the choices, up to A maximum of
 * maxChoices. There is no corresponding minimum number of choices. The interaction
 * is always initialized with no choices selected.
 * The choiceInteraction must be bound to A response variable with A baseType of
 * identifier and single or multiple cardinality.
 * Attribute : shuffle [1]: boolean = false
 * If the shuffle attribute is true then the delivery engine must randomize the
 * order in which the choices are presented subject to the fixed attribute.
 * Attribute : maxChoices [1]: integer = 1
 * The maximum number of choices that the candidate is allowed to select. If
 * maxChoices is 0 then there is no restriction. If maxChoices is greater than 1 (or 0)
 * then the interaction must be bound to A response with multiple cardinality.
 * Attribute : minChoices [0..1]: integer = 0
 * The minimum number of choices that the candidate is required to select to form A valid
 * response. If minChoices is 0 then the candidate is not required to select any choices.
 * minChoices must be less than or equal to the limit imposed by maxChoices.
 * Contains : simpleChoice [1..*]
 * An ordered list of the choices that are displayed to the user. The order is the order
 * of the choices presented to the user, unless shuffle is true.
 * 
 * @author Jonathon Hare
 */
public class ChoiceInteraction extends BlockInteraction implements SimpleChoiceContainer, Shuffleable {

    private static final long serialVersionUID = 7280640816320200269L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "choiceInteraction";

    /** Name of shuffle attribute in xml schema. */
    public static String ATTR_SHUFFLE_NAME = "shuffle";

    /** Name of maxChoices attribute in xml schema. */
    public static String ATTR_MAX_CHOICES_NAME = "maxChoices";

    /** Default value of maxChoices attribute . */
    public static int ATTR_MAX_CHOICES_DEFAULT_VALUE = 1;

    /** Name of minChoices attribute in xml schema. */
    public static String ATTR_MIN_CHOICES_NAME = "minChoices";

    /** Default value of minChoices attribute . */
    public static int ATTR_MIN_CHOICES_DEFAULT_VALUE = 0;

    public ChoiceInteraction(XmlNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BooleanAttribute(this, ATTR_SHUFFLE_NAME));
        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_CHOICES_NAME, ATTR_MAX_CHOICES_DEFAULT_VALUE, ATTR_MAX_CHOICES_DEFAULT_VALUE, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_CHOICES_NAME, ATTR_MIN_CHOICES_DEFAULT_VALUE));

        getNodeGroups().add(new SimpleChoiceGroup(this, 1));
    }

    /**
     * Gets an unmodifiable list of the child elements. Use the other
     * methods on ChoiceInteraction to add children to the correct group.
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
    public SimpleChoice getSimpleChoice(Identifier identifier) {
        for (final SimpleChoice choice : getSimpleChoices()) {
            if (choice.getIdentifier() != null && choice.getIdentifier().equals(identifier)) {
                return choice;
            }
        }
        return null;
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);

        if (getMaxChoices() != 0 && getMinChoices() > getMaxChoices()) {
            context.add(new ValidationError(this, "Minimum number of choices can't be bigger than maximum number"));
        }

        if (getResponseIdentifier() != null) {
            final ResponseDeclaration declaration = getResponseDeclaration();
            if (declaration != null && declaration.getBaseType() != null && !declaration.getBaseType().isIdentifier()) {
                context.add(new ValidationError(this, "Response variable must have identifier base type"));
            }

            if (declaration != null && getMaxChoices() == 1 &&
                    declaration.getCardinality() != null && !declaration.getCardinality().isSingle() &&
                    !declaration.getCardinality().isMultiple()) {
                context.add(new ValidationError(this, "Response variable must have single or multiple cardinality"));
            }

            if (declaration != null && getMaxChoices() != 1 && declaration.getCardinality() != null && !declaration.getCardinality().isMultiple()) {
                context.add(new ValidationError(this, "Response variable must have multiple cardinality"));
            }
        }
    }

    @Override
    public void initialize(ItemSessionController itemController) {
        super.initialize(itemController);
        itemController.shuffleInteractionChoiceOrder(this, getSimpleChoices());
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
            for (final SingleValue value : (ListValue) responseValue) {
                responseChoiceIdentifiers.add(((IdentifierValue) value).identifierValue());
            }
        }
        else {
            /* (Single response) */
            responseChoiceIdentifiers.add(((IdentifierValue) responseValue).identifierValue());
        }
        
        /* Check the number of responses */
        final int minChoices = getMinChoices();
        final int maxChoices = getMaxChoices();
        if (responseChoiceIdentifiers.size() < minChoices) {
            return false;
        }
        if (maxChoices != 0 && responseChoiceIdentifiers.size() > maxChoices) {
            return false;
        }
        

        /* Make sure each choice is a valid identifier */
        final Set<Identifier> simpleChoiceIdentifiers = new HashSet<Identifier>();
        for (final SimpleChoice simpleChoice : getSimpleChoices()) {
            simpleChoiceIdentifiers.add(simpleChoice.getIdentifier());
        }
        for (final Identifier responseChoiceIdentifier : responseChoiceIdentifiers) {
            if (!simpleChoiceIdentifiers.contains(responseChoiceIdentifier)) {
                return false;
            }
        }

        return true;
    }
}