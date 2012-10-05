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
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.notification.AbstractNotificationFirer;
import uk.ac.ed.ph.jqtiplus.notification.Notification;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentObject;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Signature;

/**
 * Partial implementation of {@link ValidationContext}
 *
 * @author David McKain
 */
abstract class AbstractValidationContext<E extends AssessmentObject> extends AbstractNotificationFirer
        implements ValidationContext {

    protected final JqtiExtensionManager jqtiExtensionManager;
    protected final AbstractValidationResult validationResult;
    protected final ResolvedAssessmentObject<E> resolvedAssessmentObject;
    protected final E subject;

    AbstractValidationContext(final JqtiExtensionManager jqtiExtensionManager, final AbstractValidationResult validationResult, final ResolvedAssessmentObject<E> resolvedAssessmentObject) {
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.validationResult = validationResult;
        this.resolvedAssessmentObject = resolvedAssessmentObject;
        this.subject = resolvedAssessmentObject.getRootNodeLookup().extractAssumingSuccessful();
    }

    @Override
    public void fireNotification(final Notification notification) {
        validationResult.add(notification);
    }

    @Override
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
        Assert.notNull(variableDeclaration);
        if (allowedTypes.length==0) {
            throw new IllegalArgumentException("Expected at leas one VariableType to be specified");
        }
        boolean result;
        if (variableDeclaration.isType(allowedTypes)) {
            result = true;
        }
        else {
            final StringBuilder messageBuilder = new StringBuilder("Variable ")
                .append(variableDeclaration)
                .append(" must be a ");
            for (int i=0; i<allowedTypes.length; i++) {
                messageBuilder.append(allowedTypes[i].getName())
                    .append(i < allowedTypes.length-1 ? ", " : " or ");
            }
            messageBuilder.append(" variable but is a ")
                .append(variableDeclaration.getVariableType().getName())
                .append(" variable");
            fireValidationError(owner, messageBuilder.toString());
            result = false;
        }
        return result;
    }

    @Override
    public boolean checkSignature(final QtiNode owner, final VariableDeclaration variableDeclaration,
            final Signature... allowedSignatures) {
        Assert.notNull(variableDeclaration);
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
    public boolean checkBaseType(final QtiNode owner, final VariableDeclaration variableDeclaration,
            final BaseType... allowedBaseTypes) {
        Assert.notNull(variableDeclaration);
        if (allowedBaseTypes.length==0) {
            throw new IllegalArgumentException("Expected at least one baseType to be specified");
        }
        boolean result;
        final BaseType baseType = variableDeclaration.getBaseType();
        if (baseType!=null && baseType.isOneOf(allowedBaseTypes)) {
            result = true;
        }
        else {
            final StringBuilder messageBuilder = new StringBuilder("Variable ")
                .append(variableDeclaration)
                .append(" must have baseType ");
            for (int i=0; i<allowedBaseTypes.length; i++) {
                messageBuilder.append(allowedBaseTypes[i].toQtiString())
                    .append(i < allowedBaseTypes.length-1 ? ", " : " or ");
            }
            if (baseType!=null) {
                messageBuilder.append(" but has baseType ")
                    .append(variableDeclaration.getBaseType().toQtiString());
            }
            fireValidationError(owner, messageBuilder.toString());
            result = false;
        }
        return result;
    }

    @Override
    public boolean checkCardinality(final QtiNode owner, final VariableDeclaration variableDeclaration,
            final Cardinality... allowedSignatures) {
        Assert.notNull(variableDeclaration);
        boolean result;
        if (variableDeclaration.getCardinality().isOneOf(allowedSignatures)) {
            result = true;
        }
        else {
            final StringBuilder messageBuilder = new StringBuilder("Variable ")
                .append(variableDeclaration)
                .append(" must have cardinality ");
            for (int i=0; i<allowedSignatures.length; i++) {
                messageBuilder.append(allowedSignatures[i].toQtiString())
                    .append(i < allowedSignatures.length-1 ? ", " : " or ");
            }
            messageBuilder.append(" but has cardinality ")
                .append(variableDeclaration.getCardinality().toQtiString());
            fireValidationError(owner, messageBuilder.toString());
            result = false;
        }
        return result;
    }
}