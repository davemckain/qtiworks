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
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.List;

/**
 * Controls the validation of single {@link AssessmentItem}s
 *
 * @see AssessmentObjectValidator
 *
 * @author David McKain
 */
public class ItemValidationController extends AbstractValidationContext<AssessmentItem> implements ItemValidationContext {

    protected final ResolvedAssessmentItem resolvedAssessmentItem;
    protected final AssessmentItem item;

    public ItemValidationController(final JqtiExtensionManager jqtiExtensionManager, final ResolvedAssessmentItem resolvedAssessmentItem) {
        super(jqtiExtensionManager, resolvedAssessmentItem);
        this.resolvedAssessmentItem = resolvedAssessmentItem;
        this.item = subject;
    }

    @Override
    public final ResolvedAssessmentItem getResolvedAssessmentItem() {
        return resolvedAssessmentItem;
    }

    @Override
    public final boolean isSubjectItem() {
        return true;
    }

    @Override
    public final boolean isSubjectTest() {
        return false;
    }

    @Override
    public final AssessmentItem getSubjectItem() {
        return item;
    }

    @Override
    public final VariableDeclaration isValidLocalVariableReference(final Identifier variableReferenceIdentifier) {
        final List<VariableDeclaration> variableDeclarations = resolvedAssessmentItem.resolveVariableReference(variableReferenceIdentifier);
        if (variableDeclarations==null) {
            /* Item lookup failed, which is impossible here */
            throw new QtiLogicException("Unexpected logic branch");
        }
        else if (variableDeclarations.size()==1) {
            /* Found and unique, which is what we want */
            final VariableDeclaration declaration = variableDeclarations.get(0);
            if (declaration.hasValidSignature()) {
                return declaration;
            }
        }
        return null;
    }

    @Override
    public final VariableDeclaration checkLocalVariableReference(final QtiNode owner, final Identifier variableReferenceIdentifier) {
        final List<VariableDeclaration> variableDeclarations = resolvedAssessmentItem.resolveVariableReference(variableReferenceIdentifier);
        if (variableDeclarations==null) {
            /* Item lookup failed, which is impossible here */
            throw new QtiLogicException("Unexpected logic branch");
        }
        else if (variableDeclarations.size()==1) {
            /* Found and unique, which is what we want */
            final VariableDeclaration declaration = variableDeclarations.get(0);
            if (!declaration.hasValidSignature()) {
                fireValidationWarning(owner, "Item variable referenced by identifier '" + variableReferenceIdentifier
                        + "' has an invalid cardinality/baseType combination so no further validation will be performed on this reference");
                return null;
            }
            return declaration;
        }
        else if (variableDeclarations.isEmpty()) {
            /* No variable found */
            fireValidationError(owner, "Item variable referenced by identifier '" + variableReferenceIdentifier + "' has not been declared");
            return null;
        }
        else {
            /* Multiple matches for identifier */
            fireValidationError(owner, variableDeclarations.size() + " item variables have been declared with the same identifier '" + variableDeclarations + "'");
            return null;
        }
    }
}