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
package uk.ac.ed.ph.jqtiplus.node.content.template;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.VisibilityModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.VisibilityMode;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * Abstract parent of feedback elements
 *
 * @author Jonathon Hare
 */
public abstract class TemplateElement extends BodyElement {

    private static final long serialVersionUID = 8626613658424084195L;

    /** Name of templateIdentifier attribute in xml schema. */
    public static final String ATTR_TEMPLATE_IDENTIFIER_NAME = "templateIdentifier";

    /** Name of showHide attribute in xml schema. */
    public static final String ATTR_VISIBILITY_MODE_NAME = VisibilityMode.QTI_CLASS_NAME;

    /** Default value of showHide attribute. */
    public static final VisibilityMode ATTR_VISIBILITY_MODE_DEFAULT_VALUE = VisibilityMode.SHOW_IF_MATCH;

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    public TemplateElement(final XmlNode parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new VisibilityModeAttribute(this, ATTR_VISIBILITY_MODE_NAME, ATTR_VISIBILITY_MODE_DEFAULT_VALUE, true));
        getAttributes().add(new IdentifierAttribute(this, ATTR_TEMPLATE_IDENTIFIER_NAME, true));
        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, true));
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

    /**
     * Gets value of identifier attribute.
     *
     * @return value of identifier attribute
     * @see #setIdentifier(Identifier)
     */
    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    /**
     * Sets new value of identifier attribute.
     *
     * @param identifier new value of identifier attribute
     * @see #getIdentifier()
     */
    public void setIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    @Override
    public void validateAttributes(final ValidationContext context) {
        super.validateAttributes(context);

        final Identifier templateIdentifier = getTemplateIdentifier();
        if (templateIdentifier != null) {
            context.checkVariableReference(this, templateIdentifier);
            final VariableDeclaration declaration = context.checkVariableReference(this, templateIdentifier);
            if (declaration!=null) {
                context.checkVariableType(this, declaration, VariableType.TEMPLATE);
                context.checkCardinality(this, declaration, Cardinality.SINGLE, Cardinality.MULTIPLE);
                context.checkBaseType(this, declaration, BaseType.IDENTIFIER);
            }
        }
    }

    @Override
    protected void validateChildren(final ValidationContext context) {
        super.validateChildren(context);

        if (!hasChildNodes()) {
            context.add(new ValidationWarning(this, "Feedback should contain something."));
        }
    }

    /**
     * Returns true if this feedback can be displayed.
     *
     * @return true if this feedback can be displayed; false otherwise
     */
    public boolean isVisible(final ItemProcessingContext itemContext) {
        final Value templateValue = itemContext.lookupVariableValue(getTemplateIdentifier(), VariableType.TEMPLATE);
        final IdentifierValue identifierValue = new IdentifierValue(getIdentifier());

        boolean identifierCheck;
        if (templateValue.getCardinality() == Cardinality.SINGLE) {
            identifierCheck = templateValue.equals(identifierValue);
        }
        else {
            identifierCheck = ((MultipleValue) templateValue).contains(identifierValue);
        }

        return identifierCheck && getVisibilityMode().equals(VisibilityMode.SHOW_IF_MATCH) ||
                !identifierCheck && getVisibilityMode().equals(VisibilityMode.HIDE_IF_MATCH);
    }
}
