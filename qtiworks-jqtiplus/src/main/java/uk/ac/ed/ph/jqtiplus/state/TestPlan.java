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
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.running.AssessmentTestPlanner;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * FIXME: Document this type!
 * TODO: Document that this is immutable!
 *
 * @see AssessmentTestPlanner
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class TestPlan implements Serializable {

    private static final long serialVersionUID = 5176553452095038589L;

    private final TestPlanNode testPlanRootNode;
    private final Map<Identifier, List<TestPlanNode>> testPlanNodesByIdentifierMap;

    public TestPlan(final TestPlanNode testPlanRootNode, final Map<Identifier, List<TestPlanNode>> testPlanNodesByIdentifierMap) {
        this.testPlanRootNode = testPlanRootNode;
        this.testPlanNodesByIdentifierMap = testPlanNodesByIdentifierMap;
    }

    public TestPlanNode getTestPlanRootNode() {
        return testPlanRootNode;
    }

    public TestPlanNode getNodeInstance(final Identifier identifier, final int instanceNumber) {
        final List<TestPlanNode> nodesForIdentifier = testPlanNodesByIdentifierMap.get(identifier);
        if (nodesForIdentifier==null) {
            return null;
        }
        if (instanceNumber<0 || instanceNumber>=nodesForIdentifier.size()) {
            return null;
        }
        return nodesForIdentifier.get(instanceNumber);
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(testPlanRootNode=" + testPlanRootNode
                + ")";
    }

    public String debugStructure() {
        final StringBuilder result = new StringBuilder();
        buildStructure(result, testPlanRootNode.getChildren(), 0);
        return result.toString();
    }

    private void buildStructure(final StringBuilder result, final List<TestPlanNode> testPlanNodes, final int indent) {
        for (final TestPlanNode testPlanNode : testPlanNodes) {
            for (int i = 0; i < indent; i++) {
                result.append("  ");
            }
            result.append(testPlanNode.getTestNodeType())
                    .append('[')
                    .append(testPlanNode.getIdentifier())
                    .append('/')
                    .append(testPlanNode.getInstanceNumber())
                    .append("]\n");
            buildStructure(result, testPlanNode.getChildren(), indent + 1);
        }
    }
}
