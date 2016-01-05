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
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.notification.ListenerNotificationFirer;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentObject;
import uk.ac.ed.ph.jqtiplus.types.ComplexReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.Signature;

/**
 * Partial implementation of {@link ValidationContext}
 *
 * @author David McKain
 */
abstract class AbstractValidationContext<E extends AssessmentObject> extends ListenerNotificationFirer
        implements ValidationContext {

    protected final JqtiExtensionManager jqtiExtensionManager;
    protected final ResolvedAssessmentObject<E> resolvedAssessmentObject;
    protected final E subject;

    public AbstractValidationContext(final JqtiExtensionManager jqtiExtensionManager, final ResolvedAssessmentObject<E> resolvedAssessmentObject) {
        Assert.notNull(jqtiExtensionManager, "jqtiExtensionManager");
        Assert.notNull(resolvedAssessmentObject, "resolvedAssessmentObject");
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.resolvedAssessmentObject = resolvedAssessmentObject;
        this.subject = resolvedAssessmentObject.getRootNodeLookup().extractAssumingSuccessful();
    }

    public final ResolvedAssessmentObject<E> getResolvedAssessmentObject() {
        return resolvedAssessmentObject;
    }

    @Override
    public final JqtiExtensionManager getJqtiExtensionManager() {
        return jqtiExtensionManager;
    }

    @Override
    public final AssessmentObject getSubject() {
        return subject;
    }

    @Override
    public boolean checkVariableType(final QtiNode owner, final VariableDeclaration variableDeclaration,
            final VariableType... allowedTypes) {
        if (variableDeclaration==null) {
            return false;
        }
        if (allowedTypes.length==0) {
            throw new IllegalArgumentException("Expected at least one VariableType to be specified");
        }
        boolean result;
        if (variableDeclaration.isType(allowedTypes)) {
            result = true;
        }
        else {
            final StringBuilder messageBuilder = new StringBuilder("Variable ")
                .append(variableDeclaration.getIdentifier())
                .append(" must be a ");
            for (int i=0; i<allowedTypes.length; i++) {
                messageBuilder.append(allowedTypes[i].getName());
                if (i < allowedTypes.length - 1) {
                    messageBuilder.append(i == allowedTypes.length - 1 ? " or " : ", ");
                }
            }
            final VariableType variableType = variableDeclaration.getVariableType();
            messageBuilder.append(" variable but is ")
                .append(variableType==VariableType.OUTCOME ? "an " : "a ")
                .append(variableType.getName())
                .append(" variable");
            fireValidationError(owner, messageBuilder.toString());
            result = false;
        }
        return result;
    }

    @Override
    public boolean checkSignature(final QtiNode owner, final VariableDeclaration variableDeclaration,
            final Signature... allowedSignatures) {
        if (variableDeclaration==null) {
            return false;
        }
        if (allowedSignatures.length==0) {
            throw new IllegalArgumentException("Expected at least one Signature to be specified");
        }
        boolean found = false;
        for (final Signature signature : allowedSignatures) {
            if (variableDeclaration.hasSignature(signature)) {
                found = true;
                break;
            }
        }

        if (!found) {
            final StringBuilder messageBuilder = new StringBuilder("Variable ")
                .append(variableDeclaration)
                .append(" must have signature ");
            for (int i=0; i<allowedSignatures.length; i++) {
                messageBuilder.append(allowedSignatures[i].getDisplayName())
                    .append(i < allowedSignatures.length-1 ? ", " : " or ");
            }
            messageBuilder.append(" but has signature ")
                .append(variableDeclaration.computeSignature().getDisplayName());
            fireValidationError(owner, messageBuilder.toString());
            found = false;
        }
        return found;
    }

    @Override
    public final VariableDeclaration isValidLocalVariableReference(final ComplexReferenceIdentifier variableReferenceIdentifier) {
        if (variableReferenceIdentifier.isDotted()) {
            return null;
        }
        return isValidLocalVariableReference(Identifier.assumedLegal(variableReferenceIdentifier.toString()));
    }


    @Override
    public final VariableDeclaration checkLocalVariableReference(final QtiNode owner, final ComplexReferenceIdentifier variableReferenceIdentifier) {
        if (variableReferenceIdentifier.isDotted()) {
            fireValidationWarning(owner, "Variable references containing period (.) characters cannot be used here");
            return null;
        }
        return checkLocalVariableReference(owner, Identifier.assumedLegal(variableReferenceIdentifier.toString()));
    }

}