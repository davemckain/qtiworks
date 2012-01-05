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
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableObject;
import uk.ac.ed.ph.jqtiplus.node.UniqueNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;

/**
 * @author David McKain
 */
public abstract class UniqueControlObject extends ControlObject<Identifier> implements UniqueNode<Identifier> {

    private static final long serialVersionUID = -4531023498836564003L;

    /** Name of duration built-in variable. */
    public static final String VARIABLE_DURATION_NAME = "duration";

    protected boolean finished;

    /**
     * Constructs object.
     * 
     * @param parent parent of constructed object
     */
    public UniqueControlObject(ControlObject<?> parent) {
        super(parent);

        getAttributes().add(new IdentifierAttribute(this, IdentifiableObject.ATTR_IDENTIFIER_NAME));
    }

    /**
     * Gets value of identifier attribute.
     * 
     * @return value of identifier attribute
     * @see #setIdentifier
     */
    @Override
    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(IdentifiableObject.ATTR_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of identifier attribute.
     * 
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    @Override
    public void setIdentifier(Identifier identifier) {
        getAttributes().getIdentifierAttribute(IdentifiableObject.ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    @Override
    protected void validateAttributes(ValidationContext context, ValidationResult result) {
        super.validateAttributes(context, result);

        validateUniqueIdentifier(result, getAttributes().getIdentifierAttribute(IdentifiableObject.ATTR_IDENTIFIER_NAME), getIdentifier());
    }
}
