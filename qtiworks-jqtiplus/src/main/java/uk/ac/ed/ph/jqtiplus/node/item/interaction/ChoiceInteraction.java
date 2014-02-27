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
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.SimpleChoiceGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleChoiceContainer;
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
 * The choice interaction presents a set of choices to the candidate. The
 * candidate's task is to select one or more of the choices, up to a maximum of
 * maxChoices. There is no corresponding minimum number of choices. The interaction
 * is always initialized with no choices selected.
 * The choiceInteraction must be bound to a response variable with a baseType of
 * identifier and single or multiple cardinality.
 * Attribute : shuffle [1]: boolean = false
 * If the shuffle attribute is true then the delivery engine must randomize the
 * order in which the choices are presented subject to the fixed attribute.
 * Attribute : maxChoices [1]: integer = 1
 * The maximum number of choices that the candidate is allowed to select. If
 * maxChoices is 0 then there is no restriction. If maxChoices is greater than 1 (or 0)
 * then the interaction must be bound to a response with multiple cardinality.
 * Attribute : minChoices [0..1]: integer = 0
 * The minimum number of choices that the candidate is required to select to form a valid
 * response. If minChoices is 0 then the candidate is not required to select any choices.
 * minChoices must be less than or equal to the limit imposed by maxChoices.
 * Contains : simpleChoice [1..*]
 * An ordered list of the choices that are displayed to the user. The order is the order
 * of the choices presented to the user, unless shuffle is true.
 *
 * @author Jonathon Hare
 */
public final class ChoiceInteraction extends BlockInteraction implements SimpleChoiceContainer,
        Shuffleable<SimpleChoice> {

    private static final long serialVersionUID = 7280640816320200269L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "choiceInteraction";

    /** Name of shuffle attribute in xml schema. */
    public static final String ATTR_SHUFFLE_NAME = "shuffle";

    /** Name of maxChoices attribute in xml schema. */
    public static final String ATTR_MAX_CHOICES_NAME = "maxChoices";

    /** Default value of maxChoices attribute . */
    public static final int ATTR_MAX_CHOICES_DEFAULT_VALUE = 0;

    /** Name of minChoices attribute in xml schema. */
    public static final String ATTR_MIN_CHOICES_NAME = "minChoices";

    /** Default value of minChoices attribute . */
    public static final int ATTR_MIN_CHOICES_DEFAULT_VALUE = 0;

    public ChoiceInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BooleanAttribute(this, ATTR_SHUFFLE_NAME, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_CHOICES_NAME, ATTR_MAX_CHOICES_DEFAULT_VALUE, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_CHOICES_NAME, ATTR_MIN_CHOICES_DEFAULT_VALUE, false));

        getNodeGroups().add(new SimpleChoiceGroup(this, 1));
    }


    @Override
    public void setShuffle(final boolean shuffle) {
        getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).setValue(Boolean.valueOf(shuffle));
    }

    @Override
    public boolean getShuffle() {
        return getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).getComputedNonNullValue();
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
    protected void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        if (getMaxChoices() != 0 && getMinChoices() > getMaxChoices()) {
            context.fireValidationError(this, "Minimum number of choices can't be bigger than maximum number");
        }

        if (responseDeclaration!=null) {
            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isIdentifier()) {
                context.fireValidationError(this, "Response variable must have identifier base type");
            }

            if (getMaxChoices() == 1 && !responseDeclaration.hasCardinality(Cardinality.SINGLE, Cardinality.MULTIPLE)) {
                context.fireValidationError(this, "Response variable must have single or multiple cardinality");
            }

            if (getMaxChoices() != 1 && !responseDeclaration.getCardinality().isMultiple()) {
                context.fireValidationError(this, "Response variable must have multiple cardinality");
            }
        }
    }

    @Override
    public List<List<SimpleChoice>> computeShuffleableChoices() {
        return Interaction.wrapSingleChoiceList(getSimpleChoices());
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