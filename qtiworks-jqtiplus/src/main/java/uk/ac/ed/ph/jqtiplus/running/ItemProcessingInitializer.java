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
package uk.ac.ed.ph.jqtiplus.running;

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * This helper class analyses a {@link ResolvedAssessmentItem} and generates an
 * {@link ItemProcessingMap} that can be reused by {@link ItemProcessingController}s.
 *
 * @see ItemProcessingMap
 * @see ItemProcessingController
 *
 * @author David McKain
 */
public final class ItemProcessingInitializer {

    private final ResolvedAssessmentItem resolvedAssessmentItem;
    private final boolean isValid;
    private final LinkedHashMap<Identifier, TemplateDeclaration> validTemplateDeclarationMapBuilder;
    private final LinkedHashMap<Identifier, ResponseDeclaration> validResponseDeclarationMapBuilder;
    private final LinkedHashMap<Identifier, OutcomeDeclaration> validOutcomeDeclarationMapBuilder;

    public ItemProcessingInitializer(final ItemValidationResult itemValidationResult) {
        this(itemValidationResult.getResolvedAssessmentItem(), itemValidationResult.isValid());
    }

    public ItemProcessingInitializer(final ResolvedAssessmentItem resolvedAssessmentItem, final boolean isValid) {
        this.resolvedAssessmentItem = resolvedAssessmentItem;
        this.isValid = isValid;
        this.validTemplateDeclarationMapBuilder = new LinkedHashMap<Identifier, TemplateDeclaration>();
        this.validResponseDeclarationMapBuilder = new LinkedHashMap<Identifier, ResponseDeclaration>();
        this.validOutcomeDeclarationMapBuilder = new LinkedHashMap<Identifier, OutcomeDeclaration>();
    }

    /**
     * Builds and returns the {@link ItemProcessingMap}.
     *
     * @return resulting {@link ItemProcessingMap}, or null if the underlying {@link AssessmentItem}
     *   lookup was unsuccessful
     */
    public ItemProcessingMap initialize() {
        if (!resolvedAssessmentItem.getItemLookup().wasSuccessful()) {
            return null;
        }
        final AssessmentItem item = resolvedAssessmentItem.getItemLookup().extractAssumingSuccessful();

        /* We will always use the built-in variables in their expected way, even if their identifiers end up non-unique */
        validResponseDeclarationMapBuilder.put(QtiConstants.VARIABLE_DURATION_IDENTIFIER, item.getDurationResponseDeclaration());
        validResponseDeclarationMapBuilder.put(QtiConstants.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER, item.getNumAttemptsResponseDeclaration());
        validOutcomeDeclarationMapBuilder.put(QtiConstants.VARIABLE_COMPLETION_STATUS_IDENTIFIER, item.getCompletionStatusOutcomeDeclaration());

        /* Then go through rest of variables, rejecting ones whose identifiers are non-unique */
        for (final TemplateDeclaration declaration : item.getTemplateDeclarations()) {
            doTemplateVariable(declaration);
        }
        for (final ResponseDeclaration declaration : item.getResponseDeclarations()) {
            doResponseVariable(declaration);
        }
        for (final OutcomeDeclaration declaration : item.getOutcomeDeclarations()) {
            doOutcomeVariable(declaration);
        }

        /* Record all interactions */
        final List<Interaction> interactions = item.getItemBody().findInteractions();

        /* That's it! */
        return new ItemProcessingMap(resolvedAssessmentItem, isValid, interactions,
                validTemplateDeclarationMapBuilder, validResponseDeclarationMapBuilder, validOutcomeDeclarationMapBuilder);
    }

    private void doTemplateVariable(final TemplateDeclaration declaration) {
        final List<VariableDeclaration> declarations = resolvedAssessmentItem.resolveVariableReference(declaration.getIdentifier());
        if (declarations.size()==1) {
            validTemplateDeclarationMapBuilder.put(declaration.getIdentifier(), declaration);
        }
    }

    private void doResponseVariable(final ResponseDeclaration declaration) {
        final List<VariableDeclaration> declarations = resolvedAssessmentItem.resolveVariableReference(declaration.getIdentifier());
        if (declarations.size()==1) {
            validResponseDeclarationMapBuilder.put(declaration.getIdentifier(), declaration);
        }
    }

    private void doOutcomeVariable(final OutcomeDeclaration declaration) {
        final List<VariableDeclaration> declarations = resolvedAssessmentItem.resolveVariableReference(declaration.getIdentifier());
        if (declarations.size()==1) {
            validOutcomeDeclarationMapBuilder.put(declaration.getIdentifier(), declaration);
        }
    }
}
