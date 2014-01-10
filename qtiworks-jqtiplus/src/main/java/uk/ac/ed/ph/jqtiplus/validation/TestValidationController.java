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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.validation;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedTestVariableReference;
import uk.ac.ed.ph.jqtiplus.types.ComplexReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.List;

/**
 * Controls the validation of {@link AssessmentTest}s
 *
 * @see AssessmentObjectValidator
 *
 * @author David McKain
 */
public class TestValidationController extends AbstractValidationContext<AssessmentTest> implements TestValidationContext {

    protected final ResolvedAssessmentTest resolvedAssessmentTest;

    public TestValidationController(final JqtiExtensionManager jqtiExtensionManager,
            final ResolvedAssessmentTest resolvedAssessmentTest) {
        super(jqtiExtensionManager, resolvedAssessmentTest);
        this.resolvedAssessmentTest = resolvedAssessmentTest;
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
    public AssessmentTest getSubjectTest() {
        return subject;
    }

    @Override
    public VariableDeclaration isValidLocalVariableReference(final Identifier variableReferenceIdentifier) {
        final List<VariableDeclaration> variableDeclarations = resolvedAssessmentTest.resolveTestVariable(variableReferenceIdentifier);
        if (variableDeclarations==null) {
            /* Test lookup failed, which is impossible here */
            throw new QtiLogicException("Unexpected logic branch");
        }
        else if (variableDeclarations.size()==1) {
            /* Found and unique which is what we want */
            final VariableDeclaration variableDeclaration = variableDeclarations.get(0);
            if (variableDeclaration.hasValidSignature()) {
                return variableDeclaration;
            }
        }
        return null;
    }

    @Override
    public VariableDeclaration checkLocalVariableReference(final QtiNode owner, final Identifier variableReferenceIdentifier) {
        final List<VariableDeclaration> variableDeclarations = resolvedAssessmentTest.resolveTestVariable(variableReferenceIdentifier);
        if (variableDeclarations==null) {
            /* Test lookup failed, which is impossible here */
            throw new QtiLogicException("Unexpected logic branch");
        }
        if (variableDeclarations.size()==1) {
            /* Found and unique which is what we want */
            final VariableDeclaration variableDeclaration = variableDeclarations.get(0);
            if (!variableDeclaration.hasValidSignature()) {
                fireValidationWarning(owner, "Test outcome variable referenced by identifier '" + variableReferenceIdentifier
                        + "' has an invalid cardinality/baseType combination so no further validation will be performed on this reference");
                return null;
            }
            return variableDeclaration;
        }
        else if (variableDeclarations.isEmpty()) {
            /* No variable found */
            fireValidationError(owner, "Test outcome variable referenced by identifier '" + variableReferenceIdentifier + "' has not been declared");
            return null;
        }
        else {
            /* Multiple matches for identifier */
            fireValidationError(owner, variableDeclarations.size() + " matches were found for the test outcome variable having identifier '" + variableReferenceIdentifier + "'");
            return null;
        }
    }

    @Override
    public ResolvedTestVariableReference isValidComplexVariableReference(final ComplexReferenceIdentifier variableReferenceIdentifier) {
        final List<ResolvedTestVariableReference> resolvedReferences = resolvedAssessmentTest.resolveVariableReference(variableReferenceIdentifier);
        if (resolvedReferences==null) {
            /* Test lookup failed, which is impossible here */
            throw new QtiLogicException("Unexpected logic branch");
        }
        if (resolvedReferences.size()==1) {
            /* Found and unique which is what we want */
            final ResolvedTestVariableReference resolvedReference = resolvedReferences.get(0);
            final VariableDeclaration declaration = resolvedReference.getVariableDeclaration();
            if (declaration.hasValidSignature()) {
                return resolvedReference;
            }
        }
        return null;
    }

    @Override
    public ResolvedTestVariableReference checkComplexVariableReference(final QtiNode owner, final ComplexReferenceIdentifier variableReferenceIdentifier) {
        final List<ResolvedTestVariableReference> resolvedReferences = resolvedAssessmentTest.resolveVariableReference(variableReferenceIdentifier);
        if (resolvedReferences==null) {
            /* Test lookup failed, which is impossible here */
            throw new QtiLogicException("Unexpected logic branch");
        }
        if (resolvedReferences.size()==1) {
            /* Found and unique which is what we want */
            final ResolvedTestVariableReference resolvedReference = resolvedReferences.get(0);
            final VariableDeclaration declaration = resolvedReference.getVariableDeclaration();
            if (!declaration.hasValidSignature()) {
                fireValidationWarning(owner, "Test or referenced item variable referenced by identifier '" + variableReferenceIdentifier
                        + "' has an invalid cardinality/baseType combination so no further validation will be performed on this reference");
                return null;
            }
            return resolvedReference;
        }
        else if (resolvedReferences.isEmpty()) {
            /* No variable found */
            fireValidationError(owner, "Test or referenced item variable referenced by identifier '" + variableReferenceIdentifier + "' has not been declared");
            return null;
        }
        else {
            /* Multiple matches for identifier */
            fireValidationError(owner, resolvedReferences.size() + " matches were found for the test or referenced item variable having identifier " + variableReferenceIdentifier);
            return null;
        }
    }
}