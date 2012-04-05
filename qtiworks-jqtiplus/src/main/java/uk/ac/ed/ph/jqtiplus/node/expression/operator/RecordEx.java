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

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.AttributeValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * The record operator takes 0 or more single sub-expressions of any base-type. The result is A container with record
 * cardinality containing the values of the sub-expressions.
 * <p>
 * All sub-expressions with NULL values are ignored. If no sub-expressions are given (or all are NULL) then the result is NULL.
 * <p>
 * This operator is not in specification, but it is needed for testing and to allow implementation of other expressions (for example fieldValue).
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public class RecordEx extends AbstractFunctionalExpression {

    private static final long serialVersionUID = -3277492769483531993L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "recordEx";

    /** Name of identifiers attribute in xml schema. */
    public static final String ATTR_IDENTIFIERS_NAME = "identifiers";

    public RecordEx(ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IdentifierMultipleAttribute(this, ATTR_IDENTIFIERS_NAME, null));
    }

    /**
     * Gets value of identifiers attribute.
     * 
     * @return value of identifiers attribute
     */
    public List<Identifier> getIdentifiers() {
        return getAttributes().getIdentifierMultipleAttribute(ATTR_IDENTIFIERS_NAME).getValue();
    }
    
    public void setIdentifiers(List<Identifier> value) {
        getAttributes().getIdentifierMultipleAttribute(ATTR_IDENTIFIERS_NAME).setValue(value);
    }

    /**
     * Gets child of this expression with given identifier or null.
     * Identifier is not part of child, but this expression.
     * 
     * @param identifier identifier of child
     * @return Gets child of this expression with given name or null
     */
    public Expression getChild(String identifier) {
        final int index = getIdentifiers().indexOf(identifier);
        if (index != -1 && index < getChildren().size()) {
            return getChildren().get(index);
        }

        return null;
    }

    @Override
    protected void validateAttributes(ValidationContext context) {
        super.validateAttributes(context);

        if (getIdentifiers().size() != getChildren().size()) {
            context.add(new AttributeValidationError(getAttributes().get(ATTR_IDENTIFIERS_NAME), "Invalid number of identifiers. Expected: " + getChildren()
                    .size() + ", but found: " + getIdentifiers().size() + "."));
        }

        final List<Identifier> identifiers = new ArrayList<Identifier>();
        for (final Identifier identifier : getIdentifiers()) {
            if (!identifiers.contains(identifier)) {
                identifiers.add(identifier);
            }
            else {
                context.add(new AttributeValidationError(getAttributes().get(ATTR_IDENTIFIERS_NAME), "Duplicate identifier: " + identifier));
            }
        }
    }

    @Override
    protected void validateChildren(ValidationContext context) {
        super.validateChildren(context);

        if (getChildren().size() == 0) {
            context.add(new ValidationWarning(this, "Container should contain some children."));
        }
    }

    @Override
    protected Value evaluateSelf(Value[] childValues) {
        final RecordValue container = new RecordValue();

        for (int i=0; i<childValues.length; i++) {
            final Identifier identifier = getIdentifiers().get(i++);
            final Value value = childValues[i];
            if (!value.isNull()) {
                container.add(identifier, (SingleValue) value);
            }
        }

        return container.isNull() ? NullValue.INSTANCE : container;
    }
}
