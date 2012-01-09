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
package uk.ac.ed.ph.jqtiplus.xperimental2;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FIXME: Document this
 * 
 * @author David McKain
 */
public final class AssessmentTestStaticState extends AssessmentObjectStaticState<AssessmentTest> {

    private static final long serialVersionUID = -8302050952592265206L;
    
    /** Resolved System ID for each {@link AssessmentItemRef} */
    private final Map<AssessmentItemRef, URI> systemIdByItemRefMap;
    
    /** List of {@link AssessmentItemRef}s corresponding to each unique resolved item System ID */
    private final Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap;
    
    /** {@link AssessmentItemStaticState} for each unique System ID. null values denote problem with item (e.g. not found, not an item) */
    private final Map<URI, AssessmentItemStaticState> assessmentItemStaticStateMap;

    public AssessmentTestStaticState(final AssessmentTest test) {
        super(test);
        this.systemIdByItemRefMap = new HashMap<AssessmentItemRef, URI>();
        this.itemRefsBySystemIdMap = new HashMap<URI, List<AssessmentItemRef>>();
        this.assessmentItemStaticStateMap = new HashMap<URI, AssessmentItemStaticState>();
    }
    
    public AssessmentTest getTest() {
        return assessmentObject;
    }

    public Map<AssessmentItemRef, URI> getSystemIdByItemRefMap() {
        return systemIdByItemRefMap;
    }
    
    public Map<URI, List<AssessmentItemRef>> getItemRefsBySystemIdMap() {
        return itemRefsBySystemIdMap;
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public Map<URI, AssessmentItemStaticState> getAssessmentItemStaticStateMap() {
        return assessmentItemStaticStateMap;
    }
    
    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(test=" + assessmentObject
                + ",systemIdByItemRefMap=" + systemIdByItemRefMap
                + ",itemRefsBySystemIdMap=" + itemRefsBySystemIdMap
                + ",assessmentItemStaticStateMap=" + assessmentItemStaticStateMap
                + ")";
    }
}
