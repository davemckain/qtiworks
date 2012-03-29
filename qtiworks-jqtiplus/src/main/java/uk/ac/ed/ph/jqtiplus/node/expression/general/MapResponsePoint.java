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
package uk.ac.ed.ph.jqtiplus.node.expression.general;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * This expression looks up the value of A response variable that must be of base-type point, and transforms it using
 * the associated areaMapping. The transformation is similar to mapResponse except that the points are tested against
 * each area in turn. When mapping containers each area can be mapped once only.
 * <p>
 * For example, if the candidate identified two points that both fall in the same area then the mappedValue is still added to the calculated total just once.
 * 
 * @author Jiri Kajaba
 */
public class MapResponsePoint extends AbstractExpression {

    private static final long serialVersionUID = 584338515225138296L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "mapResponsePoint";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    public MapResponsePoint(ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME));
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
    public void setIdentifier(Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);

        final ResponseDeclaration responseDeclaration = context.getSubjectItem().getResponseDeclaration(getIdentifier());
        if (responseDeclaration != null) {
            if (responseDeclaration.getCardinality().isRecord()) {
                context.add(new ValidationError(this, "The " + QTI_CLASS_NAME + " expression can only be bound to variables of single or container cardinalities."));
            }

            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isPoint()) {
                context.add(new ValidationError(this, "The " + QTI_CLASS_NAME + " expression can only be bound to variables of point base type."));
            }
        }
    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, Value[] childValues, int depth) {
        final ItemProcessingContext itemContext = (ItemProcessingContext) context;
        final ResponseDeclaration responseDeclaration = itemContext.getSubjectItem().getResponseDeclaration(getIdentifier());
        final Value responseValue = itemContext.lookupVariableValue(getIdentifier(), VariableType.RESPONSE);

        return responseDeclaration.getAreaMapping().getTargetValue(responseValue);
    }
}
