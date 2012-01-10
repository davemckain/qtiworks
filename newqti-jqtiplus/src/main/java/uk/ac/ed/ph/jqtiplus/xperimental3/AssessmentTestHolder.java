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
package uk.ac.ed.ph.jqtiplus.xperimental3;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.xperimental2.FrozenResourceLookup;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * FIXME: Document this
 * 
 * @author David McKain
 */
public final class AssessmentTestHolder implements Serializable {

    private static final long serialVersionUID = -8302050952592265206L;

    /** {@link AssessmentTest} lookup */
    private final FrozenResourceLookup<AssessmentTest> testLookup;
    
    /** Resolved System ID for each {@link AssessmentItemRef} */
    private final Map<AssessmentItemRef, URI> systemIdByItemRefMap;
    
    /** List of {@link AssessmentItemRef}s corresponding to each unique resolved item System ID */
    private final Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap;
    
    /** {@link AssessmentItemHolder} for each unique item System ID. */
    private final Map<URI, AssessmentItemHolder> assessmentItemHolderMap;

    public AssessmentTestHolder(final FrozenResourceLookup<AssessmentTest> testLookup,
            final Map<AssessmentItemRef, URI> systemIdByItemRefMap,
            final Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap, 
            final Map<URI, AssessmentItemHolder> assessmentItemHolderMap) {
        this.testLookup = testLookup;
        this.systemIdByItemRefMap = Collections.unmodifiableMap(systemIdByItemRefMap);
        this.itemRefsBySystemIdMap = Collections.unmodifiableMap(itemRefsBySystemIdMap);
        this.assessmentItemHolderMap = Collections.unmodifiableMap(assessmentItemHolderMap);
    }
    
    public FrozenResourceLookup<AssessmentTest> getTestLookup() {
        return testLookup;
    }

    public Map<AssessmentItemRef, URI> getSystemIdByItemRefMap() {
        return systemIdByItemRefMap;
    }
    
    public Map<URI, List<AssessmentItemRef>> getItemRefsBySystemIdMap() {
        return itemRefsBySystemIdMap;
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public Map<URI, AssessmentItemHolder> getAssessmentItemHolderMap() {
        return assessmentItemHolderMap;
    }
    
    public AssessmentItemHolder getAssessmentItemHolder(AssessmentItemRef itemRef) {
        URI systemId = systemIdByItemRefMap.get(itemRef);
        return systemId!=null ? assessmentItemHolderMap.get(systemId) : null;
    }
    
    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(testLookup=" + testLookup
                + ",systemIdByItemRefMap=" + systemIdByItemRefMap
                + ",itemRefsBySystemIdMap=" + itemRefsBySystemIdMap
                + ",assessmentItemHolderMap=" + assessmentItemHolderMap
                + ")";
    }
}
