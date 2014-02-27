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
 * This expression looks up the value of a response variable and then transforms it using the associated mapping,
 * which must have been declared. The result is a single float. If the response variable has single cardinality then
 * the value returned is simply the mapped target value from the map. If the response variable has multiple or ordered
 * cardinality then the value returned is the sum of the mapped target values. This expression cannot be applied to
 * variables of record cardinality.
 * <p>
 * For example, if a mapping associates the identifiers {A,B,C,D} with the values {0,1,0.5,0} respectively then mapResponse will map the single value 'C' to the
 * numeric value 0.5 and the set of values {C,B} to the value 1.5.
 * <p>
 * If a container contains multiple instances of the same value then that value is counted once only. To continue the example above {B,B,C} would still map to
 * 1.5 and not 2.5.
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public final class MapResponse extends AbstractFunctionalExpression {

    private static final long serialVersionUID = -45151156141657308L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "mapResponse";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    public MapResponse(final ExpressionParent parent) {
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
                    if (((ResponseDeclaration) declaration).getMapping() == null) {
                        context.fireValidationError(this, "Cannot find mapping for response declaration " + getIdentifier());
                    }
                }
            }
        }
    }

    @Override
    protected Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        final Identifier referenceIdentifier = getIdentifier();
        final ResponseDeclaration responseDeclaration = (ResponseDeclaration) context.ensureVariableDeclaration(referenceIdentifier, VariableType.RESPONSE);
        final Value responseValue = context.evaluateVariableValue(referenceIdentifier, VariableType.RESPONSE);

        return responseDeclaration.getMapping().computeTargetValue(responseValue);
    }
}
