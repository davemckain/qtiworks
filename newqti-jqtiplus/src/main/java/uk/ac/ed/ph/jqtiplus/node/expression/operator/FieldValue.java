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
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xperimental.control.ProcessingContext;

/**
 * The field-value operator takes A sub-expression with A record container value. The result is the value
 * of the field with the specified fieldIdentifier. If there is no field with that identifier then the
 * result of the operator is NULL.
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public class FieldValue extends AbstractExpression {

    private static final long serialVersionUID = 5076276250973789782L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "fieldValue";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "fieldIdentifier";

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public FieldValue(ExpressionParent parent) {
        super(parent);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public final String computeXPathComponent() {
        final Identifier identifier = getIdentifier();
        if (identifier != null) {
            return getClassTag() + "[@identifier=\"" + identifier + "\"]";
        }
        return super.computeXPathComponent();
    }

    /**
     * Gets value of identifier attribute.
     * 
     * @return value of identifier attribute
     * @see #setIdentifier
     */
    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of identifier attribute.
     * 
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    public void setIdentifier(Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    @Override
    public BaseType[] getProducedBaseTypes(ValidationContext context) {
        if (getIdentifier() == null || getChildren().size() == 0 || !(getChildren().get(0) instanceof RecordEx)) {
            return super.getProducedBaseTypes(context);
        }

        final RecordEx record = (RecordEx) getChildren().get(0);
        final Expression child = record.getChild(getIdentifier().toString());

        return child != null ? child.getProducedBaseTypes(context) : super.getProducedBaseTypes(context);
    }

    @Override
    protected void validateAttributes(ValidationContext context) {
        super.validateAttributes(context);

        if (getIdentifier() != null && getChildren().size() != 0 && getChildren().get(0) instanceof RecordEx) {
            final RecordEx record = (RecordEx) getChildren().get(0);
            if (!record.getIdentifiers().contains(getIdentifier())) {
                context.add(new ValidationWarning(this, "Cannot find field with identifier: " + getIdentifier()));
            }
        }
    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth) {
        if (isAnyChildNull(context)) {
            return NullValue.INSTANCE;
        }

        final RecordValue record = (RecordValue) getFirstChild().getValue(context);
        final Value value = record.get(getIdentifier());

        if (value == null || value.isNull()) {
            return NullValue.INSTANCE;
        }

        return value;
    }
}
