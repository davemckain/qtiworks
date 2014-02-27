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
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.InlineChoiceGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.InlineChoice;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An inline choice is an inlineInteraction that presents the user with a set of
 * choices, each of which is a simple piece of text. The candidate's task is to
 * select one of the choices. Unlike the choiceInteraction, the delivery engine
 * must allow the candidate to review their choice within the context of the
 * surrounding text.
 * The inlineChoiceInteraction must be bound to a response variable with A
 * baseType of identifier and single cardinality only.
 * Contains : inlineChoice [1..*]
 * An ordered list of the choices that are displayed to the user. The order is
 * the order of the choices presented to the user unless shuffle is true.
 * Attribute : shuffle [1]: boolean = false
 * If the shuffle attribute is true then the delivery engine must randomize the
 * order in which the choices are presented subject to the fixed attribute.
 * Attribute : required [0..1]: boolean = false
 * If true then a choice must be selected by the candidate in order to form A
 * valid response to the interaction.
 *
 * @author Jonathon Hare
 */
public final class InlineChoiceInteraction extends InlineInteraction implements Shuffleable<InlineChoice> {

    private static final long serialVersionUID = 1331855266262194665L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "inlineChoiceInteraction";

    /** Name of shuffle attribute in xml schema. */
    public static final String ATTR_SHUFFLE_NAME = "shuffle";

    /** Default value of shuffle attribute. */
    public static final boolean ATTR_SHUFFLE_DEFAULT_VALUE = false;

    /** Name of required attribute in xml schema. */
    public static final String ATTR_REQUIRED_NAME = "required";

    /** Default value of required attribute. */
    public static final boolean ATTR_REQUIRED_DEFAULT_VALUE = false;

    public InlineChoiceInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BooleanAttribute(this, ATTR_SHUFFLE_NAME, ATTR_SHUFFLE_DEFAULT_VALUE, true));
        getAttributes().add(new BooleanAttribute(this, ATTR_REQUIRED_NAME, ATTR_REQUIRED_DEFAULT_VALUE, false));

        getNodeGroups().add(new InlineChoiceGroup(this, 1));
    }


    @Override
    public boolean getShuffle() {
        return getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).getComputedNonNullValue();
    }

    @Override
    public void setShuffle(final boolean shuffle) {
        getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).setValue(Boolean.valueOf(shuffle));
    }


    public boolean getRequired() {
        return getAttributes().getBooleanAttribute(ATTR_REQUIRED_NAME).getComputedNonNullValue();
    }

    public void setRequired(final Boolean required) {
        getAttributes().getBooleanAttribute(ATTR_REQUIRED_NAME).setValue(required);
    }


    public List<InlineChoice> getInlineChoices() {
        return getNodeGroups().getInlineChoiceGroup().getInlineChoices();
    }

    /**
     * Gets simpleChoice child with given identifier or null.
     *
     * @param identifier given identifier
     * @return simpleChoice with given identifier or null
     */
    public InlineChoice getInlineChoice(final Identifier identifier) {
        for (final InlineChoice choice : getInlineChoices()) {
            if (choice.getIdentifier() != null && choice.getIdentifier().equals(identifier)) {
                return choice;
            }
        }
        return null;
    }

    @Override
    protected void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        if (responseDeclaration!=null) {
            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isIdentifier()) {
                context.fireValidationError(this, "Response variable must have identifier base type");
            }

            if (!responseDeclaration.getCardinality().isSingle()) {
                context.fireValidationError(this, "Response variable must have single cardinality");
            }
        }
    }

    @Override
    public List<List<InlineChoice>> computeShuffleableChoices() {
        return Interaction.wrapSingleChoiceList(getInlineChoices());
    }

    @Override
    public boolean validateResponse(final InteractionBindingContext interactionBindingContext, final Value responseValue) {
        if (responseValue.isNull()) {
            if (getRequired()) {
                return false;
            }
        }
        else {
            /* Make sure the identifier corresponds to that of one of the choices */
            final Set<Identifier> inlineChoiceIdentifiers = new HashSet<Identifier>();
            for (final InlineChoice choice : getInlineChoices()) {
                inlineChoiceIdentifiers.add(choice.getIdentifier());
            }
            if (!inlineChoiceIdentifiers.contains(((IdentifierValue) responseValue).identifierValue())) {
                return false;
            }
        }

        return true;
    }
}