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

import uk.ac.ed.ph.jqtiplus.internal.util.BeanToStringOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.PropertyOptions;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an instance of a {@link TestPart}, {@link AssessmentSection}
 * or {@link AssessmentItemRef} within a {@link TestPlan}.
 *
 * @see TestPlan
 *
 * @author David McKain
 */
public final class TestPlanNode implements Serializable {

    private static final long serialVersionUID = -1618684181224400175L;

    public static enum TestNodeType {
        ROOT, /* NB: ROOT is only used internally */
        TEST_PART,
        ASSESSMENT_SECTION,
        ASSESSMENT_ITEM_REF,
        ;
    }

    /** Parent Node (set internally) */
    private TestPlanNode parentNode;

    /** Global index of the corresponding {@link AbstractPart} in the test, starting at 0 */
    private final int abstractPartGlobalIndex;

    /** Index within siblings, starting at 0 */
    private int siblingIndex;

    /** Type of Node represented */
    private final TestNodeType testNodeType;

    /**
     * Computed {@link TestPlanNodeInstanceKey} for this node.
     * <p>
     * (This will be null for the {@link TestNodeType#ROOT} Node)
     */
    private final TestPlanNodeInstanceKey testPlanNodeInstanceKey;

    /** Children of this Node */
    private final List<TestPlanNode> children;

    public TestPlanNode(final TestNodeType testNodeType, final int abstractPartGlobalIndex,
            final TestPlanNodeInstanceKey testPlanNodeInstanceKey) {
        super();
        this.parentNode = null;
        this.abstractPartGlobalIndex = abstractPartGlobalIndex;
        this.siblingIndex = -1;
        this.testNodeType = testNodeType;
        this.testPlanNodeInstanceKey = testPlanNodeInstanceKey;
        this.children = new ArrayList<TestPlanNode>();
    }

    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    @ObjectDumperOptions(DumpMode.IGNORE)
    public TestPlanNode getParent() {
        return parentNode;
    }

    public int getAbstractPartGlobalIndex() {
        return abstractPartGlobalIndex;
    }

    public int getSiblingIndex() {
        return siblingIndex;
    }

    public TestNodeType getTestNodeType() {
        return testNodeType;
    }

    public TestPlanNodeInstanceKey getTestPlanNodeInstanceKey() {
        return testPlanNodeInstanceKey;
    }

    public Identifier getIdentifier() {
        return testPlanNodeInstanceKey!=null ? testPlanNodeInstanceKey.getIdentifier() : null;
    }

    public int getInstanceNumber() {
        return testPlanNodeInstanceKey!=null ? testPlanNodeInstanceKey.getInstanceNumber() : 1;
    }

    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public List<TestPlanNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public List<TestPlanNode> searchDescendantsOrSelf(final TestNodeType testNodeType) {
        final ArrayList<TestPlanNode> resultBuilder = new ArrayList<TestPlanNode>();
        buildDescendantsOrSelf(resultBuilder, this, testNodeType);
        return Collections.unmodifiableList(resultBuilder);
    }

    public List<TestPlanNode> searchDescendants(final TestNodeType testNodeType) {
        final ArrayList<TestPlanNode> resultBuilder = new ArrayList<TestPlanNode>();
        for (final TestPlanNode childNode : getChildren()) {
            buildDescendantsOrSelf(resultBuilder, childNode, testNodeType);
        }
        return Collections.unmodifiableList(resultBuilder);
    }

    private void buildDescendantsOrSelf(final List<TestPlanNode> resultBuilder, final TestPlanNode testPlanNode, final TestNodeType testNodeType) {
        if (testPlanNode.getTestNodeType()==testNodeType) {
            resultBuilder.add(testPlanNode);
        }
        for (final TestPlanNode childNode : testPlanNode.getChildren()) {
            buildDescendantsOrSelf(resultBuilder, childNode, testNodeType);
        }
    }

    public void addChild(final TestPlanNode childNode) {
        childNode.siblingIndex = children.size();
        childNode.parentNode = this;
        children.add(childNode);
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
