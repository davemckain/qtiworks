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
package uk.ac.ed.ph.jqtiplus.resolution;

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps up the lookup of an {@link AssessmentItem} and its corresponding
 * {@link ResponseProcessing} template (where applicable).
 *
 * @author David McKain
 */
public final class ResolvedAssessmentItem extends ResolvedAssessmentObject<AssessmentItem> {

    private static final long serialVersionUID = -8302050952592265206L;

    /** {@link AssessmentItem} lookup */
    private final RootNodeLookup<AssessmentItem> itemLookup;

    /** Resolved {@link ResponseProcessing} template, if specified, otherwise null */
    private final RootNodeLookup<ResponseProcessing> resolvedResponseProcessingTemplateLookup;

    public ResolvedAssessmentItem(final RootNodeLookup<AssessmentItem> itemLookup, final RootNodeLookup<ResponseProcessing> resolvedResponseProcessingTemplateLookup) {
        super(itemLookup);
        this.itemLookup = itemLookup;
        this.resolvedResponseProcessingTemplateLookup = resolvedResponseProcessingTemplateLookup;
    }

    @Override
    public AssessmentObjectType getType() {
        return AssessmentObjectType.ASSESSMENT_ITEM;
    }

    public RootNodeLookup<AssessmentItem> getItemLookup() {
        return itemLookup;
    }

    public RootNodeLookup<ResponseProcessing> getResolvedResponseProcessingTemplateLookup() {
        return resolvedResponseProcessingTemplateLookup;
    }

    /**
     * Returns the {@link VariableDeclaration}(s) having the given {@link Identifier}. This will
     * return 0 or 1 items in a valid item, but can return more if the same identifier has been
     * (incorrectly) declared multiple times.
     * <p>
     * Returns null if the item lookup was unsuccessful.
     *
     * @param variableReferenceIdentifier
     */
    public List<VariableDeclaration> resolveVariableReference(final Identifier variableReferenceIdentifier) {
        if (!itemLookup.wasSuccessful()) {
            return null;
        }
        final AssessmentItem item = itemLookup.extractAssumingSuccessful();
        final List<VariableDeclaration> result = new ArrayList<VariableDeclaration>();
        if (variableReferenceIdentifier.equals(QtiConstants.VARIABLE_DURATION_IDENTIFIER)) {
            result.add(item.getDurationResponseDeclaration());
        }
        else if (variableReferenceIdentifier.equals(QtiConstants.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER)) {
            result.add(item.getNumAttemptsResponseDeclaration());
        }
        else if (variableReferenceIdentifier.equals(QtiConstants.VARIABLE_COMPLETION_STATUS_IDENTIFIER)) {
            result.add(item.getCompletionStatusOutcomeDeclaration());
        }
        else {
            for (final TemplateDeclaration templateDeclaration : item.getTemplateDeclarations()) {
                if (templateDeclaration.getIdentifier().equals(variableReferenceIdentifier)) {
                    result.add(templateDeclaration);
                }
            }
            for (final ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
                if (responseDeclaration.getIdentifier().equals(variableReferenceIdentifier)) {
                    result.add(responseDeclaration);
                }
            }
            for (final OutcomeDeclaration putcomeDeclaration : item.getOutcomeDeclarations()) {
                if (putcomeDeclaration.getIdentifier().equals(variableReferenceIdentifier)) {
                    result.add(putcomeDeclaration);
                }
            }
        }
        return result;
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(itemLookup=" + itemLookup
                + ",resolvedResponseProcessingTemplateLookup=" + resolvedResponseProcessingTemplateLookup
                + ")";
    }
}
