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
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractSimpleFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The field-value operator takes a sub-expression with a record container value. The result is the value
 * of the field with the specified fieldIdentifier. If there is no field with that identifier then the
 * result of the operator is NULL.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class FieldValue extends AbstractSimpleFunctionalExpression {

    private static final long serialVersionUID = 5076276250973789782L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "fieldValue";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "fieldIdentifier";

    public FieldValue(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, true));
    }

    @Override
    public final String computeXPathComponent() {
        final Identifier identifier = getFieldIdentifier();
        if (identifier != null) {
            return getQtiClassName() + "[@identifier=\"" + identifier + "\"]";
        }
        return super.computeXPathComponent();
    }


    public Identifier getFieldIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    public void setFieldIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }


    @Override
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        if (getFieldIdentifier() == null || getChildren().size() == 0 || !(getChildren().get(0) instanceof RecordEx)) {
            return super.getProducedBaseTypes(context);
        }

        final RecordEx record = (RecordEx) getChildren().get(0);
        final Expression child = record.getChild(getFieldIdentifier());

        return child != null ? child.getProducedBaseTypes(context) : super.getProducedBaseTypes(context);
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        if (getFieldIdentifier() != null && getChildren().size() != 0 && getChildren().get(0) instanceof RecordEx) {
            final RecordEx record = (RecordEx) getChildren().get(0);
            if (!record.getIdentifiers().contains(getFieldIdentifier())) {
                context.fireValidationWarning(this, "Cannot find field with identifier: " + getFieldIdentifier());
            }
        }
    }

    @Override
    protected Value evaluateValidSelf(final Value[] childValues) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        final RecordValue record = (RecordValue) childValues[0];
        final Value value = record.get(getFieldIdentifier());

        return value!=null ? value : NullValue.INSTANCE;
    }
}
