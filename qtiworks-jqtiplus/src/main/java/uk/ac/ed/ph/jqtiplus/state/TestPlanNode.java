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
import uk.ac.ed.ph.jqtiplus.internal.util.BeanToStringOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.PropertyOptions;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an instance of an {@link AbstractPart} (i.e. {@link TestPart},
 * {@link AssessmentSection} or {@link AssessmentItemRef}) within a {@link TestPlan}.
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

    /**
     * Computed {@link TestPlanNodeKey} for this node.
     * <p>
     * (This will be null for the {@link TestNodeType#ROOT} Node)
     */
    private final TestPlanNodeKey key;

    /** Parent Node (set internally) */
    private TestPlanNode parentNode;

    /** Index within siblings, starting at 0 */
    private int siblingIndex;

    /** Type of Node represented */
    private final TestNodeType testNodeType;

    /**
     * Computed {@link EffectiveItemSessionControl} for this node.
     * <p>
     * (This will be null for the {@link TestNodeType#ROOT} Node)
     */
    private final EffectiveItemSessionControl effectiveItemSessionControl;

    /**
     * Title of the corresponding {@link SectionPart} if this is an
     * {@link TestNodeType#ASSESSMENT_ITEM_REF} or {@link TestNodeType#ASSESSMENT_SECTION},
     * null otherwise.
     */
    private final String sectionPartTitle;

    /**
     * Resolved System ID (href) if this is an {@link TestNodeType#ASSESSMENT_ITEM_REF},
     * otherwise null.
     */
    private final URI itemSystemId;

    /** Children of this Node */
    private final List<TestPlanNode> children;

    public TestPlanNode(final TestNodeType testNodeType, final TestPlanNodeKey key,
            final EffectiveItemSessionControl effectiveItemSessionControl) {
        this(testNodeType, key, effectiveItemSessionControl, null, null);
    }

    public TestPlanNode(final TestNodeType testNodeType, final TestPlanNodeKey key,
            final EffectiveItemSessionControl effectiveItemSessionControl,
            final String sectionPartTitle, final URI itemSystemId) {
        Assert.notNull(testNodeType, "TestNodeType");
        this.parentNode = null;
        this.siblingIndex = -1; // Must be set by addChild() later
        this.testNodeType = testNodeType;
        this.key = key;
        this.effectiveItemSessionControl = effectiveItemSessionControl;
        this.children = new ArrayList<TestPlanNode>();
        this.sectionPartTitle = sectionPartTitle;
        this.itemSystemId = itemSystemId;
    }

    public static TestPlanNode createRoot() {
        return new TestPlanNode(TestNodeType.ROOT, null, null);
    }

    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    @ObjectDumperOptions(DumpMode.IGNORE)
    public TestPlanNode getParent() {
        return parentNode;
    }

    public int getSiblingIndex() {
        return siblingIndex;
    }

    public TestNodeType getTestNodeType() {
        return testNodeType;
    }

    public TestPlanNodeKey getKey() {
        return key;
    }

    public EffectiveItemSessionControl getEffectiveItemSessionControl() {
        return effectiveItemSessionControl;
    }

    public URI getItemSystemId() {
        return itemSystemId;
    }

    public String getSectionPartTitle() {
        return sectionPartTitle;
    }

    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public Identifier getIdentifier() {
        return key!=null ? key.getIdentifier() : null;
    }

    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public int getAbstractPartGlobalIndex() {
        return key!=null ? key.getAbstractPartGlobalIndex() : -1;
    }

    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public int getInstanceNumber() {
        return key!=null ? key.getInstanceNumber() : 1;
    }

    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public List<TestPlanNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public TestPlanNode getChildAt(final int index) {
        if (index<0 || index>=children.size()) {
            throw new IndexOutOfBoundsException();
        }
        return children.get(index);
    }

    public int getChildCount() {
        return children.size();
    }

    public void addChild(final TestPlanNode childNode) {
        childNode.siblingIndex = children.size();
        childNode.parentNode = this;
        children.add(childNode);
    }

    public boolean hasPreviousSibling() {
        return parentNode!=null && siblingIndex>0;
    }

    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public TestPlanNode getPreviousSibling() {
        return hasPreviousSibling() ? parentNode.getChildAt(siblingIndex-1) : null;
    }

    public boolean hasFollowingSibling() {
        return parentNode!=null && siblingIndex+1 < parentNode.getChildCount();
    }

    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public TestPlanNode getFollowingSibling() {
        return hasFollowingSibling() ? parentNode.getChildAt(siblingIndex+1) : null;
    }

    public boolean hasAncestor(final TestPlanNode node) {
        if (parentNode==null) {
            return false;
        }
        if (ObjectUtilities.nullSafeEquals(parentNode.key, node.key)) {
            return true;
        }
        return parentNode.hasAncestor(node);
    }

    public List<TestPlanNode> searchAncestorsOrSelf() {
        return searchAncestorsOrSelf(null);
    }

    public List<TestPlanNode> searchAncestorsOrSelf(final TestNodeType testNodeType) {
        final ArrayList<TestPlanNode> resultBuilder = new ArrayList<TestPlanNode>();
        buildAncestorsOrSelf(resultBuilder, this, testNodeType);
        return Collections.unmodifiableList(resultBuilder);
    }

    public List<TestPlanNode> searchAncestors() {
        return searchAncestors(null);
    }

    public List<TestPlanNode> searchAncestors(final TestNodeType testNodeType) {
        final ArrayList<TestPlanNode> resultBuilder = new ArrayList<TestPlanNode>();
        if (parentNode!=null) {
            buildAncestorsOrSelf(resultBuilder, parentNode, testNodeType);
        }
        return Collections.unmodifiableList(resultBuilder);
    }

    private void buildAncestorsOrSelf(final List<TestPlanNode> resultBuilder, final TestPlanNode testPlanNode, final TestNodeType testNodeType) {
        if (testNodeType==null || testPlanNode.getTestNodeType()==testNodeType) {
            resultBuilder.add(testPlanNode);
        }
        final TestPlanNode parent = testPlanNode.getParent();
        if (parent!=null) {
            buildAncestorsOrSelf(resultBuilder, parent, testNodeType);
        }
    }

    public boolean hasDescendant(final TestPlanNode node) {
        return node.hasAncestor(this);
    }

    public List<TestPlanNode> searchDescendantsOrSelf() {
        return searchDescendantsOrSelf(null);
    }

    public List<TestPlanNode> searchDescendantsOrSelf(final TestNodeType testNodeType) {
        final ArrayList<TestPlanNode> resultBuilder = new ArrayList<TestPlanNode>();
        buildDescendantsOrSelf(resultBuilder, this, testNodeType);
        return Collections.unmodifiableList(resultBuilder);
    }

    public List<TestPlanNode> searchDescendants() {
        return searchDescendants(null);
    }

    public List<TestPlanNode> searchDescendants(final TestNodeType testNodeType) {
        final ArrayList<TestPlanNode> resultBuilder = new ArrayList<TestPlanNode>();
        for (final TestPlanNode childNode : getChildren()) {
            buildDescendantsOrSelf(resultBuilder, childNode, testNodeType);
        }
        return Collections.unmodifiableList(resultBuilder);
    }

    private void buildDescendantsOrSelf(final List<TestPlanNode> resultBuilder, final TestPlanNode testPlanNode, final TestNodeType testNodeType) {
        if (testNodeType==null || testPlanNode.getTestNodeType()==testNodeType) {
            resultBuilder.add(testPlanNode);
        }
        for (final TestPlanNode childNode : testPlanNode.getChildren()) {
            buildDescendantsOrSelf(resultBuilder, childNode, testNodeType);
        }
    }

    public TestPlanNode searchEnclosingTestPartNode() {
        final List<TestPlanNode> enclosingTestPartNodes = searchAncestorsOrSelf(TestNodeType.TEST_PART);
        return enclosingTestPartNodes.size()==1 ? enclosingTestPartNodes.get(0) : null;
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof TestPlanNode)) {
            return false;
        }
        final TestPlanNode other = (TestPlanNode) obj;

        /* We have to be a bit careful here with the tree structure, so we'll check parent & child separately */
        if (!(ObjectUtilities.nullSafeEquals(key, other.key)
                && siblingIndex==other.siblingIndex
                && testNodeType.equals(other.testNodeType)
                && ObjectUtilities.nullSafeEquals(effectiveItemSessionControl, other.effectiveItemSessionControl)
                && ObjectUtilities.nullSafeEquals(sectionPartTitle, other.sectionPartTitle)
                && ObjectUtilities.nullSafeEquals(itemSystemId, other.itemSystemId))) {
            return false;
        }
        /* Check parent now */
        if ((parentNode!=null && other.parentNode==null)
                || (parentNode==null && other.parentNode!=null)
                || (parentNode!=null && other.parentNode!=null && !ObjectUtilities.nullSafeEquals(parentNode.key, other.parentNode.key))) {
            return false;
        }
        /* Check keys on children */
        if (children.size()!=other.children.size()) {
            return false;
        }
        for (int i=0; i<children.size(); i++) {
            if (!(ObjectUtilities.nullSafeEquals(children.get(i), other.children.get(i)))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return key!=null ? key.hashCode() : super.hashCode();
    }
}
