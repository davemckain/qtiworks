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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.validation;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedTestVariableReference;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;

import java.util.List;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
class AbstractTestValidationContext extends AbstractValidationContext<AssessmentTest> {

    private final ResolvedAssessmentTest resolvedAssessmentTest;

    AbstractTestValidationContext(final JqtiExtensionManager jqtiExtensionManager, final TestValidationResult result, final ResolvedAssessmentTest resolvedAssessmentTest) {
        super(jqtiExtensionManager, result, resolvedAssessmentTest);
        this.resolvedAssessmentTest = resolvedAssessmentTest;
    }

    @Override
    public ResolvedAssessmentItem getResolvedAssessmentItem() {
        throw fail();
    }

    @Override
    public ResolvedAssessmentTest getResolvedAssessmentTest() {
        return resolvedAssessmentTest;
    }


    @Override
    public boolean isSubjectItem() {
        return false;
    }

    @Override
    public boolean isSubjectTest() {
        return true;
    }

    @Override
    public AssessmentItem getSubjectItem() {
        throw fail();
    }

    @Override
    public AssessmentTest getSubjectTest() {
        return subject;
    }

    @Override
    public OutcomeDeclaration checkTestVariableReference(final QtiNode owner, final Identifier variableReferenceIdentifier) {
        final List<OutcomeDeclaration> outcomeDeclarations = resolvedAssessmentTest.resolveTestVariable(variableReferenceIdentifier);
        if (outcomeDeclarations==null) {
            /* Test lookup failed, which is impossible here */
            throw new QtiLogicException("Unexpected logic branch");
        }
        else if (outcomeDeclarations.size()==1) {
            /* Found and unique which is what we want */
            return outcomeDeclarations.get(0);
        }
        if (outcomeDeclarations.isEmpty()) {
            /* No variable found */
            fireValidationError(owner, "Test outcome variable referenced by identifier '" + variableReferenceIdentifier + "' has not been declared");
            return null;
        }
        else {
            /* Multiple matches for identifier */
            fireValidationError(owner, outcomeDeclarations.size() + " matches were found for the test outcome variable having identifier '" + variableReferenceIdentifier + "'");
            return null;
        }
    }

    @Override
    public VariableDeclaration checkVariableReference(final QtiNode owner, final Identifier variableReferenceIdentifier) {
        final List<ResolvedTestVariableReference> resolvedReferences = resolvedAssessmentTest.resolveVariableReference(variableReferenceIdentifier);
        if (resolvedReferences==null) {
            /* Test lookup failed, which is impossible here */
            throw new QtiLogicException("Unexpected logic branch");
        }
        else if (resolvedReferences.size()==1) {
            /* Found and unique which is what we want */
            return resolvedReferences.get(0).getVariableDeclaration();
        }
        if (resolvedReferences.isEmpty()) {
            /* No variable found */
            fireValidationError(owner, "Test (or referenced item) variable referenced by identifier '" + variableReferenceIdentifier + "' has not been declared");
            return null;
        }
        else {
            /* Multiple matches for identifier */
            fireValidationError(owner, resolvedReferences.size() + " matches were found for the test (or referenced item) variable having identifier " + variableReferenceIdentifier);
            return null;
        }
    }

    @Override
    public VariableDeclaration checkVariableReference(final QtiNode owner, final VariableReferenceIdentifier variableReferenceIdentifier) {
        return checkVariableReference(owner, variableReferenceIdentifier.asIdentifier());
    }

    private QtiLogicException fail() {
        return new QtiLogicException("Current ValidationContext is for a test, not an item");
    }
}