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

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.VariableResolutionException.VariableResolutionFailureReason;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Wraps up the lookup of an {@link AssessmentTest} and all of the unique
 * {@link AssessmentItem}s it refers to, as well as some other useful information.
 * 
 * @author David McKain
 */
public final class ResolvedAssessmentTest extends ResolvedAssessmentObject<AssessmentTest> {

    private static final long serialVersionUID = -8302050952592265206L;
    
    /** {@link AssessmentTest} lookup */
    private final RootObjectLookup<AssessmentTest> testLookup;

    /** 
     * Lookup map for {@link AssessmentItemRef} by identifier. Valid tests should have one
     * entry in the value per key, but invalid tests might have multiple entries.
     */
    private final Map<Identifier, List<AssessmentItemRef>> itemRefsByIdentifierMap;
    
    /** Resolved System ID for each {@link AssessmentItemRef} */
    private final Map<AssessmentItemRef, URI> systemIdByItemRefMap;
    
    /** List of {@link AssessmentItemRef}s corresponding to each unique resolved item System ID */
    private final Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap;
    
    /** {@link ResolvedAssessmentItem} for each unique item System ID. */
    private final Map<URI, ResolvedAssessmentItem> resolvedAssessmentItemMap;

    public ResolvedAssessmentTest(final ModelRichness modelRichness, 
            final RootObjectLookup<AssessmentTest> testLookup,
            final Map<Identifier, List<AssessmentItemRef>> itemRefsByIdentifierMap,
            final Map<AssessmentItemRef, URI> systemIdByItemRefMap,
            final Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap, 
            final Map<URI, ResolvedAssessmentItem> resolvedAssessmentItemMap) {
        super(modelRichness, testLookup);
        this.testLookup = testLookup;
        this.itemRefsByIdentifierMap = Collections.unmodifiableMap(itemRefsByIdentifierMap);
        this.systemIdByItemRefMap = Collections.unmodifiableMap(systemIdByItemRefMap);
        this.itemRefsBySystemIdMap = Collections.unmodifiableMap(itemRefsBySystemIdMap);
        this.resolvedAssessmentItemMap = Collections.unmodifiableMap(resolvedAssessmentItemMap);
    }
    
    @Override
    public AssessmentObjectType getType() {
        return AssessmentObjectType.ASSESSMENT_TEST;
    }
    
    public RootObjectLookup<AssessmentTest> getTestLookup() {
        return testLookup;
    }

    public Map<AssessmentItemRef, URI> getSystemIdByItemRefMap() {
        return systemIdByItemRefMap;
    }
    
    public Map<Identifier, List<AssessmentItemRef>> getItemRefsByIdentifierMap() {
        return itemRefsByIdentifierMap;
    }
    
    public Map<URI, List<AssessmentItemRef>> getItemRefsBySystemIdMap() {
        return itemRefsBySystemIdMap;
    }
    
    @ObjectDumperOptions(DumpMode.TO_STRING)
    public Map<URI, ResolvedAssessmentItem> getResolvedAssessmentItemMap() {
        return resolvedAssessmentItemMap;
    }
    
    public ResolvedAssessmentItem getResolvedAssessmentItem(AssessmentItemRef itemRef) {
        URI systemId = systemIdByItemRefMap.get(itemRef);
        return systemId!=null ? resolvedAssessmentItemMap.get(systemId) : null;
    }

    @Override
    public VariableDeclaration resolveVariableReference(Identifier variableDeclarationIdentifier) throws VariableResolutionException {
        /* (These only ever reference variables within the current test) */
        if (!testLookup.wasSuccessful()) {
            throw new VariableResolutionException(variableDeclarationIdentifier, VariableResolutionFailureReason.THIS_TEST_LOOKUP_FAILURE);
        }
        AssessmentTest test = testLookup.extractIfSuccessful();
        VariableDeclaration result = test.getVariableDeclaration(variableDeclarationIdentifier);
        if (result==null) {
            throw new VariableResolutionException(variableDeclarationIdentifier, VariableResolutionFailureReason.TEST_VARIABLE_NOT_DECLARED);
        }
        return result;
    }
    
    public VariableDeclaration resolveItemVariableReference(Identifier itemRefIdentifier, Identifier itemVarIdentifier) throws VariableResolutionException {
        VariableReferenceIdentifier dottedVariableReference = new VariableReferenceIdentifier(itemRefIdentifier, itemVarIdentifier);
        return resolveItemVariableReference(dottedVariableReference, itemRefIdentifier, itemVarIdentifier);
    }
    
    private VariableDeclaration resolveItemVariableReference(VariableReferenceIdentifier dottedVariableReference, Identifier itemRefIdentifier, Identifier itemVarIdentifier) throws VariableResolutionException {
        if (!testLookup.wasSuccessful()) {
            throw new VariableResolutionException(dottedVariableReference, VariableResolutionFailureReason.THIS_TEST_LOOKUP_FAILURE);
        }
        VariableDeclaration result = null;
        final List<AssessmentItemRef> itemRefs = itemRefsByIdentifierMap.get(itemRefIdentifier);
        if (itemRefs==null) {
            /* FAIL: No assessmenetItemRef having this identifier */
            throw new VariableResolutionException(dottedVariableReference, VariableResolutionFailureReason.UNMATCHED_ASSESSMENT_ITEM_REF_IDENTIFIER);
        }
        else if (itemRefs.size()>1) {
            /* FAIL: Multiple item refs matching identifier */
            throw new VariableResolutionException(dottedVariableReference, VariableResolutionFailureReason.NON_UNIQUE_ASSESSMENT_ITEM_REF_IDENTIFIER);

        }
        else {
            final AssessmentItemRef itemRef = itemRefs.get(0);
            final ResolvedAssessmentItem resolvedItem = getResolvedAssessmentItem(itemRef);
            RootObjectLookup<AssessmentItem> itemLookup = resolvedItem.getItemLookup();
            if (!itemLookup.wasSuccessful()) {
                throw new VariableResolutionException(dottedVariableReference, VariableResolutionFailureReason.TEST_ITEM_LOOKUP_FAILURE);
            }
            final AssessmentItem item = itemLookup.extractIfSuccessful();
            final Identifier mappedItemVarIdentifier = itemRef.resolveVariableMapping(itemVarIdentifier);
            result = item.getVariableDeclaration(mappedItemVarIdentifier);
            if (result==null) {
                throw new VariableResolutionException(dottedVariableReference, VariableResolutionFailureReason.TEST_MAPPED_ITEM_VARIABLE_NOT_DECLARED);
            }
        }
        return result;
    }
    
    @Override
    public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) throws VariableResolutionException {
        VariableDeclaration result;
        if (variableReferenceIdentifier.isDotted()) {
            result = resolveItemVariableReference(variableReferenceIdentifier, 
                    variableReferenceIdentifier.getAssessmentItemRefIdentifier(), variableReferenceIdentifier.getAssessmentItemItemVariableIdentifier());
        }
        else {
            result = resolveVariableReference(variableReferenceIdentifier.getLocalIdentifier());
        }
        return result;
    }
    
    
    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(modelRichness=" + modelRichness
                + ",testLookup=" + testLookup
                + ",itemRefsByIdentifierMap=" + itemRefsByIdentifierMap
                + ",systemIdByItemRefMap=" + systemIdByItemRefMap
                + ",itemRefsBySystemIdMap=" + itemRefsBySystemIdMap
                + ",resolvedAssessmentItemMap=" + resolvedAssessmentItemMap
                + ")";
    }
}
