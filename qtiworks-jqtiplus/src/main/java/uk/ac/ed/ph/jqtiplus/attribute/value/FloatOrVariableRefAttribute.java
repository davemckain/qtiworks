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
package uk.ac.ed.ph.jqtiplus.attribute.value;

import uk.ac.ed.ph.jqtiplus.attribute.SingleAttribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.types.FloatOrVariableRef;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.Signature;

/**
 * Attribute with {@link FloatOrVariableRef} value.
 *
 * @author David McKain
 */
public final class FloatOrVariableRefAttribute extends SingleAttribute<FloatOrVariableRef> {

    private static final long serialVersionUID = -2290502264287066199L;

    public FloatOrVariableRefAttribute(final QtiNode owner, final String localName, final boolean required) {
        super(owner, localName, required);
    }

    public FloatOrVariableRefAttribute(final QtiNode owner, final String localName, final FloatOrVariableRef defaultValue, final boolean required) {
        super(owner, localName, defaultValue, required);
    }

    @Override
    public FloatOrVariableRef parseDomAttributeValue(final String domAttributeValue) {
        return FloatOrVariableRef.parseString(domAttributeValue);
    }

    @Override
    public String toDomAttributeValue(final FloatOrVariableRef value) {
        return value.toString();
    }

    @Override
    public void validateBasic(final ValidationContext context) {
        super.validateBasic(context);

        /* If variable reference, make sure it refers to the right type of variable */
        if (value!=null && value.isVariableRef()) {
            final Identifier variableReferenceIdentifier = value.getIdentifier();
            final VariableDeclaration variableDeclaration = context.checkLocalVariableReference(owner, variableReferenceIdentifier);
            if (variableDeclaration!=null) {
                context.checkVariableType(owner, variableDeclaration, VariableType.TEMPLATE, VariableType.OUTCOME);
                context.checkSignature(owner, variableDeclaration, Signature.SINGLE_FLOAT);
            }
        }
    }

}
