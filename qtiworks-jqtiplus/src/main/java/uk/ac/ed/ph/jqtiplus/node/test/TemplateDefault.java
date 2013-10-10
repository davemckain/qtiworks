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
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * The default value of a template variable in an item can be overridden based on the test context in which the template
 * is instantiated. The value is obtained by evaluating an expression defined within the reference to the item at test
 * level and which may therefore depend on the values of variables taken from other items in the test or from outcomes
 * defined at test level itself.
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public final class TemplateDefault extends AbstractNode implements ExpressionParent {

    private static final long serialVersionUID = 8370382226052240583L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "templateDefault";

    /** Name of templateIdentifier attribute in xml schema. */
    public static final String ATTR_TEMPLATE_IDENTIFIER_NAME = "templateIdentifier";

    public TemplateDefault(final AssessmentItemRef parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IdentifierAttribute(this, ATTR_TEMPLATE_IDENTIFIER_NAME, true));

        getNodeGroups().add(new ExpressionGroup(this, 1, 1));
    }

    @Override
    public AssessmentItemRef getParent() {
        return (AssessmentItemRef) super.getParent();
    }


    public Identifier getTemplateIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_TEMPLATE_IDENTIFIER_NAME).getComputedValue();
    }

    public void setTemplateIdentifier(final Identifier templateIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_TEMPLATE_IDENTIFIER_NAME).setValue(templateIdentifier);
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
        final Identifier templateIdentifier = getTemplateIdentifier();
        if (templateIdentifier!=null) {
            final VariableDeclaration declaration = context.isValidLocalVariableReference(templateIdentifier);
            if (declaration!=null && declaration.getVariableType()==VariableType.TEMPLATE) {
                return new Cardinality[] {  declaration.getCardinality() };
            }
        }
        return Cardinality.values();
    }

    @Override
    public BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        final Identifier templateIdentifier = getTemplateIdentifier();
        if (templateIdentifier!=null) {
            final VariableDeclaration declaration = context.isValidLocalVariableReference(templateIdentifier);
            if (declaration!=null && declaration.getVariableType()==VariableType.TEMPLATE && declaration.getBaseType()!=null) {
                return new BaseType[] {  declaration.getBaseType() };
            }
        }
        return BaseType.values();
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        AbstractExpression.validateChildExpressionSignatures(this, context);
    }

    public Value evaluate(final ProcessingContext context) {
        return getExpression().evaluate(context);
    }
}
