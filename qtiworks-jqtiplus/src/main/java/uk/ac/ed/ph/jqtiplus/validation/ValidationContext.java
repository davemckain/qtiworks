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
package uk.ac.ed.ph.jqtiplus.validation;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.notification.NotificationFirer;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentObject;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;

/**
 * Callback interface to enable {@link QtiNode} to validate themselves.
 *
 * @author David McKain
 */
public interface ValidationContext extends NotificationFirer {

    /**
     * Provides access to the underlying {@link JqtiExtensionManager} behind this process
     */
    JqtiExtensionManager getJqtiExtensionManager();

    /**
     * Returns true if handling an {@link AssessmentItem}, false otherwise.
     */
    boolean isSubjectItem();

    /**
     * Returns true if handling an {@link AssessmentTest}, false otherwise.
     */
    boolean isSubjectTest();

    /**
     * Returns the {@link AssessmentObject} being handled, which will not be null.
     */
    AssessmentObject getSubject();

    /**
     * Returns the {@link AssessmentItem} being handled, if this is the case.
     *
     * @throws QtiLogicException if not handling an item
     */
    AssessmentItem getSubjectItem();

    /**
     * Returns the {@link AssessmentTest} being handling, if this is the case.
     *
     * @throws QtiLogicException if not handling a test
     */
    AssessmentTest getSubjectTest();

    ResolvedAssessmentObject<?> getResolvedAssessmentObject();

    ResolvedAssessmentItem getResolvedAssessmentItem();

    ResolvedAssessmentTest getResolvedAssessmentTest();

    //------------------------------------------------------

    /**
     * Checks that the variable having the given {@link Identifier} can be successfully and uniquely
     * dereferenced. Returns the resulting {@link VariableDeclaration} if successful, otherwise null.
     *
     * @see #checkVariableReference(QtiNode, Identifier)
     */
    VariableDeclaration isValidVariableReference(Identifier variableReferenceIdentifier);

    /**
     * Checks that the test (outcome) variable having the given {@link Identifier} can be successfully and uniquely
     * dereferenced. Returns the resulting {@link VariableDeclaration} if successful, otherwise null.
     *
     * @see #checkTestVariableReference(QtiNode, Identifier)
     */
    OutcomeDeclaration isValidTestVariableReference(Identifier variableReferenceIdentifier);

    /**
     * Checks that the variable reference {@link Identifier} resolves successfully and uniquely.
     * A validation error is recorded and {@link NullValue} is returned if this is unsuccessful.
     * <p>
     * Returns a valid {@link VariableDeclaration} corresponding to the resulting variable if successful,
     * otherwise null.
     *
     * @see #isValidVariableReference(QtiNode, Identifier)
     */
    VariableDeclaration checkVariableReference(QtiNode owner, Identifier variableReferenceIdentifier);

    /**
     * Checks that the test (outcome) variable having the given {@link Identifier} can be
     * successfully and uniquely dereferenced. A validation error is recorded and {@link NullValue}
     * is returned if this is unsuccessful.
     * <p>
     * Returns a valid {@link OutcomeDeclaration} corresponding to the resulting test variable if successful,
     * otherwise null.
     *
     * @see #isValidTestVariableReference(Identifier)
     */
    OutcomeDeclaration checkTestVariableReference(final QtiNode owner, final Identifier variableReferenceIdentifier);

    /**
     * Checks that the given {@link VariableDeclaration} is of one of the stated {@link VariableType}s,
     * returning true if successful.
     * <p>
     * A validation error is recorded and false is returned if unsuccessful.
     *
     * @param owner {@link QtiNode} being validated
     * @param variableDeclaration declaration to check. This is allowed to be null,
     *   in which case no check is performed and false is returned.
     * @param allowedTypes array of allowed {@link VariableType}s
     */
    boolean checkVariableType(QtiNode owner, VariableDeclaration variableDeclaration, VariableType... allowedTypes);

    /**
     * Checks that the given {@link VariableDeclaration} has one of the given {@link Signature}s,
     * returning true if successful.
     * <p>
     * A validation error  is recorded and false is returned if unsuccessful.
     *
     * @param owner {@link QtiNode} being validated
     * @param variableDeclaration declaration to check. This is allowed to be null,
     *   in which case no check is performed and false is returned
     * @param allowedSignatures array of allowed {@link Signature}s
     */
    boolean checkSignature(QtiNode owner, VariableDeclaration variableDeclaration, Signature... allowedSignatures);
}
