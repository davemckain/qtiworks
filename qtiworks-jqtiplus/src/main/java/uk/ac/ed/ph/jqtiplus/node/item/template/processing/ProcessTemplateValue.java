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
package uk.ac.ed.ph.jqtiplus.node.item.template.processing;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;

import java.util.List;

/**
 * Abstract parent for mutator rules
 *
 * @see SetCorrectResponse
 * @see SetDefaultValue
 * @see SetTemplateValue
 *
 * @author Jonathon Hare
 */
public abstract class ProcessTemplateValue extends TemplateRule implements ExpressionParent {

    private static final long serialVersionUID = 4332256442863602196L;

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    public ProcessTemplateValue(final QtiNode parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, true));

        getNodeGroups().add(new ExpressionGroup(this, 1, 1));
    }

    @Override
    public final String computeXPathComponent() {
        final Identifier identifier = getIdentifier();
        if (identifier != null) {
            return getQtiClassName() + "[@identifier=\"" + identifier + "\"]";
        }
        return super.computeXPathComponent();
    }

    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    public void setIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    @Override
    public List<Expression> getExpressions() {
        return getNodeGroups().getExpressionGroup().getExpressions();
    }

    public Expression getExpression() {
        return getNodeGroups().getExpressionGroup().getExpression();
    }

    public void setExpression(final Expression expression) {
        getNodeGroups().getExpressionGroup().setExpression(expression);
    }

    @Override
    public final Cardinality[] getRequiredCardinalities(final ValidationContext context, final int index) {
        final Identifier referenceIdentifier = getIdentifier();
        if (referenceIdentifier!=null) {
            final VariableDeclaration declaration = context.isValidLocalVariableReference(referenceIdentifier);
            if (declaration!=null) {
                return new Cardinality[] {  declaration.getCardinality() };
            }
        }
        return Cardinality.values();
    }

    @Override
    public final BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        final Identifier referenceIdentifier = getIdentifier();
        if (referenceIdentifier!=null) {
            final VariableDeclaration declaration = context.isValidLocalVariableReference(referenceIdentifier);
            if (declaration!=null && declaration.getBaseType()!=null) {
                return new BaseType[] { declaration.getBaseType() };
            }
        }
        return BaseType.values();
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        AbstractExpression.validateChildExpressionSignatures(this, context);
    }
}
