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
package uk.ac.ed.ph.jqtiplus.node.item.interaction.choice;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.VisibilityModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.VisibilityMode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.util.List;

/**
 * Many of the interactions involve choosing one or more predefined choices.
 * These choices all have the following attributes in common:
 * Attribute : identifier [1]: identifier
 * The identifier of the choice. This identifier must not be used by any
 * other choice or item variable.
 * Attribute : fixed [0..1]: boolean = false
 * If fixed is true for a choice then the position of this choice within the
 * interaction must not be changed by the delivery engine even if the
 * immediately enclosing interaction supports the shuffling of choices. If no
 * value is specified then the choice is free to be shuffled.
 * In Item Templates, the visibility of choices can be controlled by setting
 * the value(s) of an associated template variable during template processing.
 * For information about item templates see Item Templates.
 * Attribute : templateIdentifier [0..1]: identifier
 * The identifier of a template variable that must have a base-type of
 * identifier and be either single of multiple cardinality. When the associated
 * interaction is part of an Item Template the value of the identified template
 * variable is used to control the visibility of the choice. When a choice is
 * hidden it is not selectable and its content is not visible to the candidate
 * unless otherwise stated.
 * Attribute : showHide [0..1]: showHide = show
 * The showHide attribute determines how the visibility of the choice is controlled.
 * If set to show then the choice is hidden by default and shown only if the
 * associated template variable matches, or contains, the identifier of the choice.
 * If set to hide then the choice is shown by default and hidden if the associated
 * template variable matches, or contains, the choice's identifier.
 *
 * @author Jonathon Hare
 */
public abstract class Choice extends BodyElement {

    private static final long serialVersionUID = 4158676852427101853L;

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    /** Name of fixed attribute in xml schema. */
    public static final String ATTR_FIXED_NAME = "fixed";

    /** Default value of fixed attribute. */
    public static final boolean ATTR_FIXED_DEFAULT_VALUE = false;

    /** Name of templateIdentifier attribute in xml schema. */
    public static final String ATTR_TEMPLATE_IDENTIFIER_NAME = "templateIdentifier";

    /** Name of showHide attribute in xml schema. */
    public static final String ATTR_VISIBILITY_MODE_NAME = "showHide";

    public Choice(final QtiNode parent, final String qtiClassName) {
        super(parent, qtiClassName);
        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, true));
        getAttributes().add(new BooleanAttribute(this, ATTR_FIXED_NAME, ATTR_FIXED_DEFAULT_VALUE, false));
        getAttributes().add(new IdentifierAttribute(this, ATTR_TEMPLATE_IDENTIFIER_NAME, false));
        getAttributes().add(new VisibilityModeAttribute(this, ATTR_VISIBILITY_MODE_NAME, false));
    }

    /**
     * Gets value of identifier attribute.
     *
     * @return value of identifier attribute
     * @see #setIdentifier
     */
    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    /**
     * Sets new value of identifier attribute.
     *
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    public void setIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    /**
     * Gets value of fixed attribute.
     *
     * @return value of fixed attribute
     * @see #setFixed
     */
    public boolean getFixed() {
        return getAttributes().getBooleanAttribute(ATTR_FIXED_NAME).getComputedNonNullValue();
    }

    /**
     * Sets new value of fixed attribute.
     *
     * @param fixed new value of fixed attribute
     * @see #getFixed
     */
    public void setFixed(final Boolean fixed) {
        getAttributes().getBooleanAttribute(ATTR_FIXED_NAME).setValue(fixed);
    }

    /**
     * Gets value of showHide attribute.
     *
     * @return value of showHide attribute
     * @see #setVisibilityMode
     */
    public VisibilityMode getVisibilityMode() {
        return getAttributes().getVisibilityModeAttribute(ATTR_VISIBILITY_MODE_NAME).getComputedValue();
    }

    /**
     * Sets new value of showHide attribute.
     *
     * @param visibilityMode new value of showHide attribute
     * @see #getVisibilityMode
     */
    public void setVisibilityMode(final VisibilityMode visibilityMode) {
        getAttributes().getVisibilityModeAttribute(ATTR_VISIBILITY_MODE_NAME).setValue(visibilityMode);
    }

    /**
     * Gets value of templateIdentifier attribute.
     *
     * @return value of templateIdentifier attribute
     * @see #setTemplateIdentifier
     */
    public Identifier getTemplateIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_TEMPLATE_IDENTIFIER_NAME).getComputedValue();
    }

    /**
     * Sets new value of templateIdentifier attribute.
     *
     * @param templateIdentifier new value of templateIdentifier attribute
     * @see #getTemplateIdentifier
     */
    public void setTemplateIdentifier(final Identifier templateIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_TEMPLATE_IDENTIFIER_NAME).setValue(templateIdentifier);
    }

    @Override
    public void validateThis(final ValidationContext context) {
        super.validateThis(context);
        /* As per info model, the choice's identifier must not be used by any other choice or item variable */
        final Identifier identifier = getIdentifier();
        final AssessmentItem item = ((ItemValidationContext) context).getSubjectItem();
        final List<Choice> choices = QueryUtils.search(Choice.class, item.getItemBody());
        for (final Choice choice : choices) {
            if (choice != this && choice.getIdentifier().equals(identifier)) {
                context.fireValidationError(this, "The identifier " + identifier + " of this choice is used by another choice");
            }
        }
        if (item.getTemplateDeclaration(identifier) != null) {
            context.fireValidationError(this, "The identifier " + identifier + " of this choice is used by a template variable");
        }
        if (item.getResponseDeclaration(identifier) != null) {
            context.fireValidationError(this, "The identifier " + identifier + " of this choice is used by a response variable");
        }
        if (item.getOutcomeDeclaration(identifier) != null) {
            context.fireValidationError(this, "The identifier " + identifier + " of this choice is used by a outcome variable");
        }
    }

}
