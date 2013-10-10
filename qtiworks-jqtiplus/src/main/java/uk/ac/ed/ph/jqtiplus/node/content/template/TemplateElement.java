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
package uk.ac.ed.ph.jqtiplus.node.content.template;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.VisibilityModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AbstractFlowBodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.VisibilityMode;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * Abstract parent of template elements
 *
 * NB: This subclasses {@link AbstractFlowBodyElement} even though it should only really
 * subclass {@link TemplateElement}. However, all concrete subclasses of this end up
 * implementing {@link FlowStatic} so this makes no difference.
 *
 * @author Jonathon Hare
 */
public abstract class TemplateElement extends AbstractFlowBodyElement {

    private static final long serialVersionUID = 8626613658424084195L;

    /** Name of templateIdentifier attribute in xml schema. */
    public static final String ATTR_TEMPLATE_IDENTIFIER_NAME = "templateIdentifier";

    /** Name of showHide attribute in xml schema. */
    public static final String ATTR_VISIBILITY_MODE_NAME = VisibilityMode.QTI_CLASS_NAME;

    /** Default value of showHide attribute. */
    public static final VisibilityMode ATTR_VISIBILITY_MODE_DEFAULT_VALUE = VisibilityMode.SHOW_IF_MATCH;

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    public TemplateElement(final QtiNode parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new VisibilityModeAttribute(this, ATTR_VISIBILITY_MODE_NAME, ATTR_VISIBILITY_MODE_DEFAULT_VALUE, true));
        getAttributes().add(new IdentifierAttribute(this, ATTR_TEMPLATE_IDENTIFIER_NAME, true));
        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, true));
    }

    public VisibilityMode getVisibilityMode() {
        return getAttributes().getVisibilityModeAttribute(ATTR_VISIBILITY_MODE_NAME).getComputedValue();
    }

    public void setVisibilityMode(final VisibilityMode visibilityMode) {
        getAttributes().getVisibilityModeAttribute(ATTR_VISIBILITY_MODE_NAME).setValue(visibilityMode);
    }


    public Identifier getTemplateIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_TEMPLATE_IDENTIFIER_NAME).getComputedValue();
    }

    public void setTemplateIdentifier(final Identifier templateIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_TEMPLATE_IDENTIFIER_NAME).setValue(templateIdentifier);
    }


    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    public void setIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    @Override
    public void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Identifier templateIdentifier = getTemplateIdentifier();
        if (templateIdentifier != null) {
            final VariableDeclaration declaration = context.checkLocalVariableReference(this, templateIdentifier);
            if (declaration!=null) {
                context.checkVariableType(this, declaration, VariableType.TEMPLATE);
                context.checkSignature(this, declaration, Signature.SINGLE_IDENTIFIER, Signature.MULTIPLE_IDENTIFIER);
            }
        }

        if (!hasChildNodes()) {
            context.fireValidationWarning(this, "Feedback should contain something.");
        }
    }

    /**
     * Returns true if this feedback can be displayed.
     *
     * @return true if this feedback can be displayed; false otherwise
     */
    public boolean isVisible(final ItemProcessingContext itemContext) {
        final Value templateValue = itemContext.evaluateVariableValue(getTemplateIdentifier(), VariableType.TEMPLATE);
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
