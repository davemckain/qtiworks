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
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.Ordering;
import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;
import uk.ac.ed.ph.jqtiplus.node.test.Selection;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.running.TestPlanner;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.state.marshalling.TestPlanXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the shape of an {@link AssessmentTest} once {@link Ordering} and
 * {@link Selection} rules have been applied.
 * <p>
 * {@link AbstractPart}s within the {@link AssessmentTest} are represented by
 * {@link TestPlanNode}s within this {@link TestPlan}. These Nodes are uniquely
 * identified using synthesised {@link TestPlanNodeKey} Objects, which are derived
 * from {@link Identifier}s but are unique, even if the same {@link SectionPart}s is selected
 * more than once (with replacement).
 * <p>
 * Nodes corresponding to {@link AssessmentItemRef}s will <strong>not</strong> be included in the plan if
 * they fail to satisfy the following properties:
 * <ul>
 * <li>Their identifier is unique amongst other such Nodes</li>
 * <li>The resulting item was successfully looked up</li>
 * </ul>
 * <p>
 * An instance of this class should be consider immutable once created.
 *
 * @see TestPlanNode
 * @see TestPlanNodeKey
 * @see TestPlanner
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class TestPlan implements Serializable {

    private static final long serialVersionUID = 5176553452095038589L;

    /** Root of the {@link TestPlanNode} tree */
    private final TestPlanNode testPlanRootNode;

    /** List of all {@link TestPlanNode}s, in depth-first search order starting at 0 */
    private final List<TestPlanNode> testPlanNodeList;

    /**
     * Map of the {@link TestPlanNode}s, keyed on {@link TestPlanNodeKey}.
     *
     * (NB: The root Node is not included here as it has null key)
     */
    private final Map<TestPlanNodeKey, TestPlanNode> testPlanNodesByKeyMap;

    /**
     * Map of the {@link TestPlanNode}s corresponding to the {@link Identifier}s of each
     * {@link AbstractPart} selected in this {@link TestPlan}.
     * <p>
     * There will be multiple elements in the case of non-unique {@link Identifier}s and
     * selection with replacement.
     */
    private final Map<Identifier, List<TestPlanNode>> testPlanNodesByIdentifierMap;

    /**
     * This general constructor is used by {@link TestPlanXmlMarshaller}. It performs a depth-first
     * search starting at the given root node.
     */
    public TestPlan(final TestPlanNode testPlanRootNode) {
        /* Do depth-first search */
        this.testPlanRootNode = testPlanRootNode;
        this.testPlanNodeList = testPlanRootNode.searchDescendantsOrSelf();

        /* Need to populate testPlanNodesByIdentifier */
        final Map<TestPlanNodeKey, TestPlanNode> testPlanNodeMapBuilder = new HashMap<TestPlanNodeKey, TestPlanNode>();
        final Map<Identifier, List<TestPlanNode>> testPlanNodesByIdentifierMapBuilder = new LinkedHashMap<Identifier, List<TestPlanNode>>();
        for (int i=1; i<testPlanNodeList.size(); i++) { /* (Not interested in root node, so starting at 1 here) */
            final TestPlanNode testPlanNode = testPlanNodeList.get(i);
            final TestPlanNodeKey key = testPlanNode.getKey();
            if (key==null) {
                throw new IllegalArgumentException("Did not expect to find a Node " + testPlanNode + " with null key");
            }
            testPlanNodeMapBuilder.put(key, testPlanNode);
            List<TestPlanNode> nodesByIdentifier = testPlanNodesByIdentifierMapBuilder.get(key.getIdentifier());
            if (nodesByIdentifier==null) {
                nodesByIdentifier = new ArrayList<TestPlanNode>();
                testPlanNodesByIdentifierMapBuilder.put(key.getIdentifier(), nodesByIdentifier);
            }
            nodesByIdentifier.add(testPlanNode);
        }
        this.testPlanNodesByKeyMap = Collections.unmodifiableMap(testPlanNodeMapBuilder);
        this.testPlanNodesByIdentifierMap = Collections.unmodifiableMap(testPlanNodesByIdentifierMapBuilder);
    }

    /**
     * (This constructor is used by the {@link TestPlanner}, which will have already computed all
     * of the raw information being represented.)
     */
    public TestPlan(final TestPlanNode testPlanRootNode, final List<TestPlanNode> testPlanNodeList,
            final Map<TestPlanNodeKey, TestPlanNode> testPlanNodesByKeyMap,
            final Map<Identifier, List<TestPlanNode>> testPlanNodesByIdentifierMap) {
        this.testPlanRootNode = testPlanRootNode;
        this.testPlanNodeList = Collections.unmodifiableList(testPlanNodeList);
        this.testPlanNodesByKeyMap = Collections.unmodifiableMap(testPlanNodesByKeyMap);
        this.testPlanNodesByIdentifierMap = Collections.unmodifiableMap(testPlanNodesByIdentifierMap);
    }

    public TestPlanNode getTestPlanRootNode() {
        return testPlanRootNode;
    }

    public List<TestPlanNode> getTestPartNodes() {
        return testPlanRootNode.getChildren();
    }

    public TestPlanNode getTestPartNode(final Identifier identifier) {
        for (final TestPlanNode testPartNode : getTestPartNodes()) {
            if (identifier.equals(testPartNode.getIdentifier())) {
                return testPartNode;
            }
        }
        return null;
    }

    /**
     * Returns a flat {@link List} of all {@link TestPlanNode}s in global (depth-first) order
     */
    public List<TestPlanNode> getTestPlanNodeList() {
        return testPlanNodeList;
    }

    /**
     * Returns the {@link TestPlanNode} corresponding to the given {@link TestPlanNodeKey}
     *
     * @throws IllegalArgumentException if key is null or does not correspond to a node in this
     * {@link TestPlan}
     */
    public TestPlanNode getNode(final TestPlanNodeKey key) {
        Assert.notNull(key, "key");
        final TestPlanNode result = testPlanNodesByKeyMap.get(key);
        if (result==null) {
            throw new IllegalArgumentException("No TestPlanNode with " + key + " found in this TestPlan");
        }
        return result;
    }

    /**
     * Returns the {@link TestPlanNode} at the required global index (depth-first)
     *
     * @throws IllegalArgumentException if global index is out of range.
     */
    public TestPlanNode getNodeAtGlobalIndex(final int globalIndex) {
        if (globalIndex < 0 || globalIndex >= testPlanNodeList.size()) {
            throw new IllegalArgumentException("GlobalIndex " + globalIndex + " is out of range of this TestPlan");
        }
        return testPlanNodeList.get(globalIndex);
    }

    /**
     * Computes the global (depth-first) index of the given {@link TestPlanNode} in this {@link TestPlan},
     * starting at 0 with the root node.
     *
     * @throws IllegalArgumentException if the given {@link TestPlanNode} is null or is not in this
     *   {@link TestPlan}
     */
    public int getGlobalIndex(final TestPlanNode testPlanNode) {
        Assert.notNull(testPlanNode, "testPlanNode");
        final TestPlanNodeKey key = testPlanNode.getKey();
        int index = 0;
        for (final TestPlanNode searchNode : testPlanNodeList) {
            if (key.equals(searchNode.getKey())) {
                return index;
            }
            index++;
        }
        throw new IllegalArgumentException("No TestPlanNode with " + key + " found in this TestPlan");
    }

    /**
     * Returns a List of all {@link TestPlanNode}s corresponding to the {@link AbstractPart}
     * having the given {@link Identifier}. The result may be null if:
     * <ul>
     *   <li>
     *     The {@link Identifier} is invalid
     *   </li>
     *   <li>
     *     The {@link Identifier} referred to an {@link AssessmentItemRef}
     *     which was not successfully resolved
     *   </li>
     *   <li>
     *     The {@link Identifier} successfully referred to a {@link SectionPart},
     *     but the {@link SectionPart} was not selected in this {@link TestPlan} as the result
     *     of a {@link Selection} rule.
     *   </li>
     * </ul>
     *
     * @param identifier Identifier to check
     * @return List of corresponding {@link TestPlanNode}. This will never by empty, but may be null.
     */
    public List<TestPlanNode> getNodes(final Identifier identifier) {
        final List<TestPlanNode> nodesForIdentifier = testPlanNodesByIdentifierMap.get(identifier);
        if (nodesForIdentifier==null) {
            return null;
        }
        return Collections.unmodifiableList(nodesForIdentifier);
    }

    /**
     * Returns the {@link TestPlanNode}s corresponding to the given instance of the {@link AbstractPart}
     * having the given {@link Identifier}. The result may be null if:
     * <ul>
     *   <li>
     *     The {@link Identifier} is invalid
     *   </li>
     *   <li>
     *     The {@link Identifier} referred to an {@link AssessmentItemRef}
     *     which was not successfully resolved
     *   </li>
     *   <li>
     *     The {@link Identifier} successfully referred to a {@link SectionPart},
     *     but the {@link SectionPart} was not selected in this {@link TestPlan} as the result
     *     of a {@link Selection} rule.
     *   </li>
     *   <li>
     *     The instance number was out of bounds of the number of instances that were actually
     *     selected in this {@link TestPart} as the result of a {@link Selection} rule.
     *   </li>
     * </ul>
     *
     * @param identifier Identifier to check
     * @return List of corresponding {@link TestPlanNode}. This may be null.
     */
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


    public List<TestPlanNode> searchNodes(final TestNodeType testNodeType) {
        return testPlanRootNode.searchDescendants(testNodeType);
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(testPlanRootNode=" + testPlanRootNode
                + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof TestPlan)) {
            return false;
        }
        final TestPlan other = (TestPlan) obj;
        return testPlanRootNode.equals(other.testPlanRootNode)
                && testPlanNodeList.equals(other.testPlanNodeList);
    }

    @Override
    public int hashCode() {
        return testPlanRootNode.hashCode();
    }

    //-------------------------------------------------------------------

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
                .append('(')
                .append(testPlanNode.getKey())
                .append(")\n");
            buildStructure(result, testPlanNode.getChildren(), indent + 1);
        }
    }

}
