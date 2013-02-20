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
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates the key information about an {@link AssessmentItem} used during processing.
 * <p>
 * Usage: an instance of this class can be safely used by multiple Threads
 *
 * @see ItemProcessingInitializer
 *
 * @author David McKain
 */
public final class ItemProcessingMap implements Serializable {

    private static final long serialVersionUID = -823440766463296396L;

    private final ResolvedAssessmentItem resolvedAssessmentItem;
    private final boolean isValid;
    private final List<Interaction> interactions;
    private final Map<Identifier, Interaction> interactionByResponseIdentifierMap;
    private final Set<Identifier> validVariableIdentifierSet;
    private final Map<Identifier, TemplateDeclaration> validTemplateDeclarationMap;
    private final Map<Identifier, ResponseDeclaration> validResponseDeclarationMap;
    private final Map<Identifier, OutcomeDeclaration> validOutcomeDeclarationMap;

    public ItemProcessingMap(final ResolvedAssessmentItem resolvedAssessmentItem, final boolean isValid,
            final List<Interaction> interactionsBuilder,
            final LinkedHashMap<Identifier, TemplateDeclaration> validTemplateDeclarationMapBuilder,
            final Map<Identifier, ResponseDeclaration> validResponseDeclarationMapBuilder,
            final Map<Identifier, OutcomeDeclaration> validOutcomeDeclarationMapBuilder) {
        this.resolvedAssessmentItem = resolvedAssessmentItem;
        this.isValid = isValid;

        /* Record interactions and build helper map */
        this.interactions = Collections.unmodifiableList(new ArrayList<Interaction>(interactionsBuilder));
        final Map<Identifier, Interaction> interactionMapBuilder = new HashMap<Identifier, Interaction>();
        for (final Interaction interaction : interactions) {
            interactionMapBuilder.put(interaction.getResponseIdentifier(), interaction);
        }
        this.interactionByResponseIdentifierMap = Collections.unmodifiableMap(interactionMapBuilder);

        /* Record (valid) variable declarations */
        this.validTemplateDeclarationMap = Collections.unmodifiableMap(new LinkedHashMap<Identifier, TemplateDeclaration>(validTemplateDeclarationMapBuilder));
        this.validResponseDeclarationMap = Collections.unmodifiableMap(new LinkedHashMap<Identifier, ResponseDeclaration>(validResponseDeclarationMapBuilder));
        this.validOutcomeDeclarationMap = Collections.unmodifiableMap(new LinkedHashMap<Identifier, OutcomeDeclaration>(validOutcomeDeclarationMapBuilder));

        /* Build set of all variable declarations */
        final Set<Identifier> variableIdentifierSetBuilder = new HashSet<Identifier>();
        variableIdentifierSetBuilder.addAll(validTemplateDeclarationMap.keySet());
        variableIdentifierSetBuilder.addAll(validResponseDeclarationMap.keySet());
        variableIdentifierSetBuilder.addAll(validOutcomeDeclarationMap.keySet());
        this.validVariableIdentifierSet = Collections.unmodifiableSet(variableIdentifierSetBuilder);
    }

    public boolean isValid() {
        return isValid;
    }

    public ResolvedAssessmentItem getResolvedAssessmentItem() {
        return resolvedAssessmentItem;
    }

    public Set<Identifier> getValidVariableIdentifierSet() {
        return validVariableIdentifierSet;
    }

    public boolean isValidVariableIdentifier(final Identifier identifier) {
        return validVariableIdentifierSet.contains(identifier);
    }

    public Map<Identifier, TemplateDeclaration> getValidTemplateDeclarationMap() {
        return validTemplateDeclarationMap;
    }

    public Map<Identifier, ResponseDeclaration> getValidResponseDeclarationMap() {
        return validResponseDeclarationMap;
    }

    public Map<Identifier, OutcomeDeclaration> getValidOutcomeDeclarationMap() {
        return validOutcomeDeclarationMap;
    }

    public List<Interaction> getInteractions() {
        return interactions;
    }

    public Map<Identifier, Interaction> getInteractionByResponseIdentifierMap() {
        return interactionByResponseIdentifierMap;
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
