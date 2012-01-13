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
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;

import java.io.Serializable;
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
public final class ResolvedAssessmentTest implements Serializable {

    private static final long serialVersionUID = -8302050952592265206L;
    
    private final ModelRichness modelRichness;

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
        this.modelRichness = modelRichness;
        this.testLookup = testLookup;
        this.itemRefsByIdentifierMap = Collections.unmodifiableMap(itemRefsByIdentifierMap);
        this.systemIdByItemRefMap = Collections.unmodifiableMap(systemIdByItemRefMap);
        this.itemRefsBySystemIdMap = Collections.unmodifiableMap(itemRefsBySystemIdMap);
        this.resolvedAssessmentItemMap = Collections.unmodifiableMap(resolvedAssessmentItemMap);
    }
    
    public RootObjectLookup<AssessmentTest> getTestLookup() {
        return testLookup;
    }

    public Map<AssessmentItemRef, URI> getSystemIdByItemRefMap() {
        return systemIdByItemRefMap;
    }
    
    public Map<URI, List<AssessmentItemRef>> getItemRefsBySystemIdMap() {
        return itemRefsBySystemIdMap;
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public Map<URI, ResolvedAssessmentItem> getResolvedAssessmentItemMap() {
        return resolvedAssessmentItemMap;
    }
    
    public ResolvedAssessmentItem getResolvedAssessmentItem(AssessmentItemRef itemRef) {
        URI systemId = systemIdByItemRefMap.get(itemRef);
        return systemId!=null ? resolvedAssessmentItemMap.get(systemId) : null;
    }
    
    public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
        if (!testLookup.wasSuccessful()) {
            return null;
        }
        final AssessmentTest test = testLookup.extractIfSuccessful();
        final Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();

        /* (In tests, we allow both local and item references) */
        VariableDeclaration declaration = null;
        if (localIdentifier != null) {
            /* Referring to another test variable */
            declaration = test.getVariableDeclaration(localIdentifier);
        }
        else {
            /* It's a special ITEM.VAR reference */
            final Identifier itemRefIdentifier = variableReferenceIdentifier.getAssessmentItemRefIdentifier();
            final Identifier itemVarIdentifier = variableReferenceIdentifier.getAssessmentItemItemVariableIdentifier();
            final List<AssessmentItemRef> itemRefs = itemRefsByIdentifierMap.get(itemRefIdentifier);
            if (itemRefs==null) {
                /* FAIL Couldn't resolve item */
            }
            else if (itemRefs.size()>1) {
                /* FAIL: Multiple item refs matching identifier */
            }
            else {
                final AssessmentItemRef itemRef = itemRefs.get(0);
                final ResolvedAssessmentItem itemHolder = getResolvedAssessmentItem(itemRef);
                final Identifier mappedItemVarIdentifier = itemRef.resolveVariableMapping(itemVarIdentifier);
                declaration = itemHolder.resolveVariableReference(mappedItemVarIdentifier);
            }
        }
        return declaration;
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
