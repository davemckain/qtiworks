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
package uk.ac.ed.ph.jqtiplus.resolution;

import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;

import java.io.Serializable;

/**
 * Wraps up the lookup of an {@link AssessmentItem} and its corresponding
 * {@link ResponseProcessing} template (where applicable). 
 * 
 * @author David McKain
 */
public final class ResolvedAssessmentItem implements Serializable {

    private static final long serialVersionUID = -8302050952592265206L;

    /** {@link AssessmentItem} lookup */
    private final RootObjectLookup<AssessmentItem> itemLookup;
    
    /** Resolved {@link ResponseProcessing} template, if specified, otherwise null */
    private final RootObjectLookup<ResponseProcessing> resolvedResponseProcessingTemplateLookup;
    
    private final ModelRichness modelRichness;

    public ResolvedAssessmentItem(final ModelRichness modelRichness, final RootObjectLookup<AssessmentItem> itemLookup, final RootObjectLookup<ResponseProcessing> resolvedResponseProcessingTemplateLookup) {
        this.itemLookup = itemLookup;
        this.resolvedResponseProcessingTemplateLookup = resolvedResponseProcessingTemplateLookup;
        this.modelRichness = modelRichness;
    }
    
    public RootObjectLookup<AssessmentItem> getItemLookup() {
        return itemLookup;
    }
    
    public ModelRichness getModelRichness() {
        return modelRichness;
    }
    
    public RootObjectLookup<ResponseProcessing> getResolvedResponseProcessingTemplateLookup() {
        return resolvedResponseProcessingTemplateLookup;
    }
    
    public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
        final Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();
        if (localIdentifier != null) {
            return resolveVariableReference(localIdentifier);
        }
        /* FIXME: Should probably blow up with localIdentifier is not right here! */
        return null;
    }
    
    public VariableDeclaration resolveVariableReference(Identifier variableDeclarationIdentifier) {
        if (!itemLookup.wasSuccessful()) {
            return null;
        }
        AssessmentItem item = itemLookup.extractIfSuccessful();
        return item.getVariableDeclaration(variableDeclarationIdentifier);
    }
    
    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(modelRichness=" + modelRichness
                + ",itemLookup=" + itemLookup
                + ",resolvedResponseProcessingTemplateLookup=" + resolvedResponseProcessingTemplateLookup
                + ")";
    }
}
