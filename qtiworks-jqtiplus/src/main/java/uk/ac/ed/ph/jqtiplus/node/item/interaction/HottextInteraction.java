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
import uk.ac.ed.ph.jqtiplus.group.content.BlockStaticGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.BlockStatic;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.content.Hottext;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.AssessmentItemAttemptController;
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
 * The hottext interaction presents a set of choices to the candidate
 * represented as selectable runs of text embedded within a surrounding
 * context, such as a simple passage of text. Like choiceInteraction,
 * the candidate's task is to select one or more of the choices, up to
 * a maximum of maxChoices. The interaction is initialized from the
 * defaultValue of the associated response variable, a NULL value
 * indicating that no choices are selected (the usual case).
 * The hottextInteraction must be bound to a response variable with a
 * baseType of identifier and single or multiple cardinality.
 * Attribute : maxChoices [1]: integer = 1
 * The maximum number of choices that can be selected by the candidate.
 * If matchChoices is 0 there is no restriction. If maxChoices is greater
 * than 1 (or 0) then the interaction must be bound to a response with
 * multiple cardinality.
 * Attribute : minChoices [0..1]: integer = 0
 * The minimum number of choices that the candidate is required to select
 * to form a valid response. If minChoices is 0 then the candidate is not
 * required to select any choices. minChoices must be less than or equal
 * to the limit imposed by maxChoices.
 * Contains : blockStatic [1..*]
 * The content of the interaction is simply a piece of content, such as a
 * simple passage of text, that contains the hottext areas.
 * 
 * @author Jonathon Hare
 */
public class HottextInteraction extends BlockInteraction {

    private static final long serialVersionUID = 9164925050514182744L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "hottextInteraction";

    /** Name of maxChoices attribute in xml schema. */
    public static String ATTR_MAX_CHOICES_NAME = "maxChoices";

    /** Default value of maxChoices attribute . */
    public static int ATTR_MAX_CHOICES_DEFAULT_VALUE = 1;

    /** Name of minChoices attribute in xml schema. */
    public static String ATTR_MIN_CHOICES_NAME = "minChoices";

    /** Default value of minChoices attribute . */
    public static int ATTR_MIN_CHOICES_DEFAULT_VALUE = 0;

    public HottextInteraction(XmlNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_CHOICES_NAME, ATTR_MAX_CHOICES_DEFAULT_VALUE, ATTR_MAX_CHOICES_DEFAULT_VALUE, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_CHOICES_NAME, ATTR_MIN_CHOICES_DEFAULT_VALUE));

        getNodeGroups().add(new BlockStaticGroup(this, 1));
    }

    /**
     * Gets an unmodifiable list of the child elements. Use the other
     * methods on ChoiceInteraction to add children to the correct group.
     */
    @Override
    public List<? extends XmlNode> getChildren() {
        final List<XmlNode> children = new ArrayList<XmlNode>();
        children.addAll(super.getChildren());
        children.addAll(getNodeGroups().getBlockStaticGroup().getBlockStatics());

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
     * Gets blockStatic children.
     * 
     * @return blockStatic children
     */
    public List<BlockStatic> getBlockStatics() {
        return getNodeGroups().getBlockStaticGroup().getBlockStatics();
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
    public boolean validateResponse(AssessmentItemAttemptController itemController, Value responseValue) {
        /* Extract response values */
        final Set<Identifier> responseHottextIdentifiers = new HashSet<Identifier>();
        if (responseValue.isNull()) {
            /* (Empty response) */
        }
        else if (responseValue.getCardinality().isList()) {
            /* (Container response) */
            for (final SingleValue value : (ListValue) responseValue) {
                responseHottextIdentifiers.add(((IdentifierValue) value).identifierValue());
            }
        }
        else {
            /* (Single response) */
            responseHottextIdentifiers.add(((IdentifierValue) responseValue).identifierValue());
        }

        /* Check the number of responses */
        final int minChoices = getMinChoices().intValue();
        final int maxChoices = getMaxChoices().intValue();
        if (responseHottextIdentifiers.size() < minChoices) {
            return false;
        }
        if (maxChoices != 0 && responseHottextIdentifiers.size() > maxChoices) {
            return false;
        }

        /* Make sure each choice is a valid identifier */
        final Set<Identifier> hottextIdentifiers = new HashSet<Identifier>();
        final List<Hottext> hottexts = search(Hottext.class);
        for (final Hottext hottext : hottexts) {
            hottextIdentifiers.add(hottext.getIdentifier());
        }
        for (final Identifier responseHottextIdentifier : responseHottextIdentifiers) {
            if (!hottextIdentifiers.contains(responseHottextIdentifier)) {
                return false;
            }
        }

        return true;
    }

}