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
package uk.ac.ed.ph.jqtiplus.running;

import uk.ac.ed.ph.jqtiplus.exception.QtiInvalidLookupException;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * Extension of {@link ProcessingContext} callback, available when running an {@link AssessmentItem}
 *
 * @see ItemProcessingController
 * @see TestProcessingContext
 *
 * @author David McKain
 */
public interface ItemProcessingContext extends ProcessingContext, ItemValidationContext {

    /**
     * Returns the {@link ItemSessionState} attached to this context.
     */
    ItemSessionState getItemSessionState();

    /**
     * Computes the current default value of the variable having the
     * given {@link Identifier}. The result will be not null (though may be a {@link NullValue}).
     *
     * @param identifier identifier of the required variable, which must not be null
     * @return computed default value, which will not be null.
     *
     * @throws QtiInvalidLookupException
     */
    Value computeDefaultValue(Identifier identifier);

    /**
     * Computes the current correct response for the {@link ResponseDeclaration} having the
     * given {@link Identifier}. The result will be not null (though may be a {@link NullValue}).
     *
     * @param identifier identifier of the required variable, which must not be null
     * @return computed correct response value, which will not be null.
     *
     * @throws QtiInvalidLookupException
     */
    Value computeCorrectResponse(Identifier responseIdentifier);

    boolean isCorrect();
    boolean isIncorrect();


}