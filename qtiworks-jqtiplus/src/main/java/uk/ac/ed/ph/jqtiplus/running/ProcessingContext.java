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

import uk.ac.ed.ph.jqtiplus.exception.QtiCandidateStateException;
import uk.ac.ed.ph.jqtiplus.exception.QtiInvalidLookupException;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Random;

/**
 * Callback for common tasks during item/test processing,
 * e.g. in template, response and outcome processing.
 *
 * @see ItemProcessingContext
 * @see TestProcessingContext
 *
 * @author David McKain
 */
public interface ProcessingContext extends ValidationContext {

    /**
     * Returns whether or not the subject {@link AssessmentItem} or {@link AssessmentTest} is valid
     */
    boolean isSubjectValid();

    /**
     * Returns a generator that should be obtained to create random
     * numbers if required.
     */
    Random getRandomGenerator();

    /**
     * Looks up the declaration of the variable in the subject item or test having the given
     * {@link Identifier} and having the given permitted variable types.
     * <p>
     * If successful, then the resulting {@link VariableDeclaration} is returned.
     * <p>
     * If no {@link VariableDeclaration} is found satisfying the specified conditions then a
     * {@link QtiInvalidLookupException} is thrown.
     * <p>
     * Note: This also works for built-in variables, returning an "internal" declaration for
     * the variable.
     *
     * @param identifier required variable Identifier, which must not be null
     * @param permittedTypes permitted variable types. An empty array is treated as "any type allowed"
     * @return {@link VariableDeclaration} satisfying the given criteria.
     *
     * @throws QtiInvalidLookupException
     * @throws {@link IllegalArgumentException} if identifier is null
     */
    VariableDeclaration ensureVariableDeclaration(final Identifier identifier, final VariableType... permittedTypes);

    /**
     * Returns the current value of the variable having the
     * given {@link VariableDeclaration}.
     * <p>
     * The returned value will not be null (but may be a {@link NullValue}).
     *
     * @param variableDeclaration required variable declaration
     *
     * @throws QtiCandidateStateException if the current item/test state does not appear to be in sync
     * @throws IllegalArgumentException if variableDeclaration is null
     */
    Value evaluateVariableValue(VariableDeclaration variableDeclaration);

    /**
     * Returns the current value of the variable having the
     * given {@link Identifier} and having the given permitted variable types.
     * <p>
     * The returned value will not be null (but may be a {@link NullValue}).
     *
     * @param identifier required variable Identifier, which must not be null
     * @param permittedTypes permitted variable types. An empty array is treated as "any type allowed"
     *
     * @throws QtiInvalidLookupException if the identifier could not be successfully dereferenced
     * @throws QtiCandidateStateException if the current item/test state does not appear to be in sync
     * @throws IllegalArgumentException if identifier is null
     */
    Value evaluateVariableValue(Identifier identifier, VariableType... permittedTypes);

    /**
     * Sets the value of the variable corresponding to the given {@link VariableDeclaration}.
     * <p>
     * Built-in variables may not be set, except for the <code>completionStatus</code> variable on
     * an item.
     * <p>
     * You <strong>MUST</strong> use this method instead of setting variables directly within the
     * {@link ItemSessionState} or {@link TestSessionState}. This method ensures that built-in
     * variables (e.g. <code>completionStatus</code>) get handled the correct way.
     *
     * @param variableDeclaration required variable declaration
     *
     * @throws IllegalArgumentException if variableDeclaration is null or if it corresponds to
     *   a built-in variable which cannot be directly mutated.
     */
    void setVariableValue(VariableDeclaration variableDeclaration, Value value);
}
