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
import uk.ac.ed.ph.jqtiplus.control.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
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
    public static final String CLASS_TAG = "mapResponsePoint";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public MapResponsePoint(ExpressionParent parent) {
        super(parent);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
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
    public void validate(ValidationContext context, AbstractValidationResult result) {
        final ItemValidationContext itemContext = (ItemValidationContext) context;
        super.validate(context, result);

        final ResponseDeclaration responseDeclaration = itemContext.getItem().getResponseDeclaration(getIdentifier());
        if (responseDeclaration != null) {
            if (responseDeclaration.getCardinality().isRecord()) {
                result.add(new ValidationError(this, "The " + CLASS_TAG + " expression can only be bound to variables of single or container cardinalities."));
            }

            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isPoint()) {
                result.add(new ValidationError(this, "The " + CLASS_TAG + " expression can only be bound to variables of point base type."));
            }
        }
    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth) {
        final ItemProcessingContext itemContext = (ItemProcessingContext) context;
        final ResponseDeclaration responseDeclaration = itemContext.getItem().getResponseDeclaration(getIdentifier());
        final Value responseValue = itemContext.lookupVariable(getIdentifier());

        return responseDeclaration.getAreaMapping().getTargetValue(responseValue);
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
