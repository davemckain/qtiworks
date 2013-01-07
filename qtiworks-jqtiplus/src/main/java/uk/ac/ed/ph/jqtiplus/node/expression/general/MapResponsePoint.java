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
package uk.ac.ed.ph.jqtiplus.node.expression.general;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * This expression looks up the value of a response variable that must be of base-type point, and transforms it using
 * the associated areaMapping. The transformation is similar to mapResponse except that the points are tested against
 * each area in turn. When mapping containers each area can be mapped once only.
 * <p>
 * For example, if the candidate identified two points that both fall in the same area then the mappedValue is still added to the calculated total just once.
 *
 * @author Jiri Kajaba
 */
public final class MapResponsePoint extends AbstractFunctionalExpression {

    private static final long serialVersionUID = 584338515225138296L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "mapResponsePoint";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    public MapResponsePoint(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, true));
    }

    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    public void setIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Identifier referenceIdentifier = getIdentifier();
        if (referenceIdentifier!=null) {
            final VariableDeclaration declaration = context.checkLocalVariableReference(this, referenceIdentifier);
            if (declaration!=null) {
                if (context.checkVariableType(this, declaration, VariableType.RESPONSE)) {
                    if (declaration.getCardinality().isRecord()) {
                        context.fireValidationError(this, "The " + QTI_CLASS_NAME + " expression cannot be bound to variables with record cardinality");
                    }
                    else if (!declaration.getBaseType().isPoint()) {
                        context.fireValidationError(this, "The " + QTI_CLASS_NAME + " expression can only be bound to variables of point base type");
                    }
                    if (((ResponseDeclaration) declaration).getAreaMapping()==null) {
                        context.fireAttributeValidationError(getAttributes().get(ATTR_IDENTIFIER_NAME), "Cannot find areaMapping for response declaration " + getIdentifier());
                    }
                }
            }
        }
    }

    @Override
    protected Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        final Identifier responseIdentifier = getIdentifier();
        final ResponseDeclaration responseDeclaration = (ResponseDeclaration) context.ensureVariableDeclaration(responseIdentifier, VariableType.RESPONSE);
        final Value responseValue = context.evaluateVariableValue(responseIdentifier, VariableType.RESPONSE);

        return responseDeclaration.getAreaMapping().getTargetValue(responseValue);
    }
}
