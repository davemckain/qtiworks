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
package uk.ac.ed.ph.jqtiplus.running;

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.Ordering;
import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.state.SectionPartState;
import uk.ac.ed.ph.jqtiplus.state.SectionPartStateKey;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Document this type!
 *
 * Usage: use once and discard; not thread safe.
 *
 * @author David McKain
 */
public final class AssessmentTestPlanner {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentTestPlanner.class);

    /**
     * Private class used to build up computed tree structure below {@link TestPart}s.
     */
    private static class RuntimeTreeNode {

        private final SectionPart sectionPart;
        private final List<RuntimeTreeNode> childNodes;

        public RuntimeTreeNode(final SectionPart sectionPart, final List<RuntimeTreeNode> childNodes) {
            this.sectionPart = sectionPart;
            this.childNodes = childNodes;
        }

        public SectionPart getSectionPart() {
            return sectionPart;
        }

        public List<RuntimeTreeNode> getChildNodes() {
            return childNodes;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                    + "(sectionPart=" + sectionPart
                    + ",childNodes=" + childNodes
                    + ")";
        }
    }

    private final AssessmentTest test;

    private final Map<Identifier, List<TestPlanNode>> testPlanNodesByIdentifierMap;
    private final TestPlanNode testPlanRootNode;

    @Deprecated
    private final Map<SectionPartStateKey, SectionPartState> sectionPartStateMap;

    public AssessmentTestPlanner(final AssessmentTest test) {
        this.test = test;
        this.testPlanNodesByIdentifierMap = new HashMap<Identifier, List<TestPlanNode>>();
        this.testPlanRootNode = new TestPlanNode(TestNodeType.ROOT, null, 0);
        this.sectionPartStateMap = new HashMap<SectionPartStateKey, SectionPartState>();
    }

    public TestPlan run() {
        logger.debug("Creating a test plan for test {}", test.getIdentifier());
        testPlanNodesByIdentifierMap.clear();

        final List<TestPlanNode> testPlanNodes = new ArrayList<TestPlanNode>();
        for (final TestPart testPart : test.getTestParts()) {
            /* Process test part */
            final List<RuntimeTreeNode> runtimeChildNodes = doTestPart(testPart);
            logger.debug("Result of processing testPart {} is {}", testPart.getIdentifier(), runtimeChildNodes);

            /* Build up tree */
            final TestPlanNode testPlanNode = recordTestPartPlan(testPart, runtimeChildNodes);
            testPlanNodes.add(testPlanNode);
        }

        final TestPlan result = new TestPlan(testPlanRootNode, testPlanNodesByIdentifierMap);
        logger.info("Computed test plan for test {} is {}", test.getIdentifier(), result);
        return result;
    }

    private List<RuntimeTreeNode> doTestPart(final TestPart testPart) {
        logger.debug("Initialising testPart {}", testPart.getIdentifier());

        /* Process each AssessmentSection */
        final List<RuntimeTreeNode> runtimeChildNodes = new ArrayList<RuntimeTreeNode>();
        for (final AssessmentSection section : testPart.getAssessmentSections()) {
            final RuntimeTreeNode sectionNode = doAssessmentSectionState(section);

            /* Flatten out invisible AssessmentSections */
            if (section.getVisible()) {
                runtimeChildNodes.add(sectionNode);
            }
            else {
                runtimeChildNodes.addAll(sectionNode.getChildNodes());
            }
        }
        return runtimeChildNodes;
    }

    private RuntimeTreeNode doSectionPart(final SectionPart sectionPart) {
        RuntimeTreeNode result;
        if (sectionPart instanceof AssessmentSection) {
            result = doAssessmentSectionState((AssessmentSection) sectionPart);
        }
        else if (sectionPart instanceof AssessmentItemRef) {
            result = doAssessmentItemRef((AssessmentItemRef) sectionPart);
        }
        else {
            throw new QtiLogicException("Unexpected logic branch: sectionPart=" + sectionPart);
        }
        return result;
    }

    private RuntimeTreeNode doAssessmentSectionState(final AssessmentSection section) {
        logger.debug("Initialising instance of assessmentSection {}", section.getIdentifier());

        /* We first need to select which children we're going to have */
        List<SectionPart> afterSelection;
        if (section.getSelection() != null) {
            /* Perform requested selection */
            afterSelection = selectSectionParts(section);
        }
        else {
            /* Select all children */
            afterSelection = section.getSectionParts();
        }
        logger.info("After selection for section {} have {}", section, afterSelection);

        /* Set up each selected child */
        final List<RuntimeTreeNode> childNodes = new ArrayList<RuntimeTreeNode>();
        for (final SectionPart sectionPart : afterSelection) {
            childNodes.add(doSectionPart(sectionPart));
        }
        logger.info("Initialisation of child Nodes for section {} resulted in {}", section, childNodes);

        /* Then we do ordering, if requested */
        List<RuntimeTreeNode> afterOrdering;
        final Ordering ordering = section.getOrdering();
        if (ordering != null && ordering.getShuffle()) {
            afterOrdering = orderSectionParts(childNodes);
        }
        else {
            afterOrdering = childNodes;
        }
        logger.info("Ordering of child Nodes for section {} resulted in {}", section, afterOrdering);

        /* Flatten invisible child assessmentSections */
        final List<RuntimeTreeNode> afterFlattening = new ArrayList<RuntimeTreeNode>();
        for (final RuntimeTreeNode childNode : afterOrdering) {
            if (childNode.getSectionPart() instanceof AssessmentSection) {
                final AssessmentSection childSection = (AssessmentSection) childNode.getSectionPart();
                if (childSection.getVisible()) {
                    afterFlattening.add(childNode);
                }
                else {
                    afterFlattening.addAll(childNode.getChildNodes());
                }
            }
            else {
                afterFlattening.add(childNode);
            }
        }

        return new RuntimeTreeNode(section, afterFlattening);
    }

    private RuntimeTreeNode doAssessmentItemRef(final AssessmentItemRef itemRef) {
        logger.debug("Initialising instance of assessmentItemRef {}", itemRef.getIdentifier());
        return new RuntimeTreeNode(itemRef, Collections.<RuntimeTreeNode> emptyList());
    }

    private List<SectionPart> selectSectionParts(final AssessmentSection assessmentSection) {
        final List<SectionPart> children = assessmentSection.getSectionParts();
        final int childCount = children.size();

        /* Work out how many selections to make for each child */
        final int[] selectionCounts = new int[childCount]; /* (Number of selections made per child) */
        int requiredCount = 0;
        for (int i = 0; i < childCount; i++) {
            if (children.get(i).getRequired()) {
                selectionCounts[i]++;
                requiredCount++;
            }
        }
        final int toSelect = assessmentSection.getSelection().getSelect() - requiredCount;
        final Random random = new Random(System.currentTimeMillis());
        if (assessmentSection.getSelection().getWithReplacement()) {
            for (int i = 0; i < toSelect; i++) {
                final int index = random.nextInt(childCount);
                selectionCounts[index]++;
            }
        }
        else {
            for (int i = 0; i < toSelect; i++) {
                int index = random.nextInt(childCount - requiredCount - i);
                for (int j = 0; j < selectionCounts.length; j++) {
                    if (selectionCounts[j] == 0) {
                        index--;
                    }
                    if (index == -1) {
                        selectionCounts[j]++;
                        break;
                    }
                }
            }
        }

        /* Now perform selection */
        final List<SectionPart> result = new ArrayList<SectionPart>();
        for (int i = 0; i < childCount; i++) {
            final SectionPart sectionPart = children.get(i);
            for (int j = 0; j < selectionCounts[i]; j++) {
                result.add(sectionPart);
            }
        }
        return result;
    }

    private List<RuntimeTreeNode> orderSectionParts(final List<RuntimeTreeNode> childNodes) {
        /* Merge all invisible assessmentSections with keepTogether=false now */
        final List<RuntimeTreeNode> beforeShuffle = new ArrayList<RuntimeTreeNode>();
        for (final RuntimeTreeNode item : childNodes) {
            final SectionPart sectionPart = item.getSectionPart();
            if (sectionPart instanceof AssessmentSection) {
                final AssessmentSection section = (AssessmentSection) sectionPart;
                if (!section.getVisible() && !section.getKeepTogether()) {
                    beforeShuffle.addAll(item.getChildNodes());
                }
                else {
                    beforeShuffle.add(item);
                }
            }
            else {
                beforeShuffle.add(item);
            }
        }

        /* Extract the entries to be shuffled */
        final List<RuntimeTreeNode> toShuffle = new ArrayList<RuntimeTreeNode>();
        for (final RuntimeTreeNode item : beforeShuffle) {
            if (!item.getSectionPart().getFixed()) {
                toShuffle.add(item);
            }
        }

        /* Perform shuffle */
        Collections.shuffle(toShuffle);

        /* Merge the shuffled items in */
        final List<RuntimeTreeNode> afterShuffle = new ArrayList<RuntimeTreeNode>();
        final Iterator<RuntimeTreeNode> shuffledIterator = toShuffle.iterator();
        for (final RuntimeTreeNode item : beforeShuffle) {
            if (!item.getSectionPart().getFixed()) {
                afterShuffle.add(shuffledIterator.next());
            }
            else {
                afterShuffle.add(item);
            }
        }

        /* Finally, merge remaining invisible assessmentSections with keepTogether=true */
        final List<RuntimeTreeNode> result = new ArrayList<RuntimeTreeNode>();
        for (final RuntimeTreeNode item : afterShuffle) {
            final SectionPart sectionPart = item.getSectionPart();
            if (sectionPart instanceof AssessmentSection) {
                final AssessmentSection section = (AssessmentSection) sectionPart;
                if (!section.getVisible() && section.getKeepTogether()) {
                    result.addAll(item.getChildNodes());
                }
                else {
                    result.add(item);
                }
            }
            else {
                result.add(item);
            }
        }

        /* That's it... phew! */
        return result;
    }

    //------------------------------------------------------

    private TestPlanNode recordTestPartPlan(final TestPart testPart, final List<RuntimeTreeNode> runtimeChildNodes) {
        final TestPlanNode result = recordTestPlanNode(testPlanRootNode, TestNodeType.TEST_PART, testPart.getIdentifier());
        recordChildPlans(result, runtimeChildNodes);
        return result;
    }

    private List<TestPlanNode> recordChildPlans(final TestPlanNode targetParent, final List<RuntimeTreeNode> treeNodes) {
        final List<TestPlanNode> result = new ArrayList<TestPlanNode>();
        for (final RuntimeTreeNode treeNode : treeNodes) {
            result.add(recordChildPlans(targetParent, treeNode));
        }
        return result;
    }

    private TestPlanNode recordChildPlans(final TestPlanNode targetParent, final RuntimeTreeNode treeNode) {
        final SectionPart sectionPart = treeNode.getSectionPart();
        TestPlanNode result;
        if (sectionPart instanceof AssessmentSection) {
            result = recordTestPlanNode(targetParent, TestNodeType.ASSESSMENT_SECTION, sectionPart.getIdentifier());
        }
        else if (sectionPart instanceof AssessmentItemRef) {
            result = recordTestPlanNode(targetParent, TestNodeType.ASSESSMENT_ITEM_REF, sectionPart.getIdentifier());
        }
        else {
            throw new QtiLogicException("Unexpected logic branch: sectionPart=" + sectionPart);
        }
        recordChildPlans(result, treeNode.getChildNodes());
        return result;
    }

    private int computeCurrentInstanceCount(final Identifier identifier) {
        final List<TestPlanNode> nodesForIdentifier = testPlanNodesByIdentifierMap.get(identifier);
        return nodesForIdentifier!=null ? nodesForIdentifier.size() : 0;
    }

    private TestPlanNode recordTestPlanNode(final TestPlanNode parent, final TestNodeType testNodeType,
            final Identifier identifier) {
        /* Compute instance number for this identifier */
        final int instanceNumber = 1 + computeCurrentInstanceCount(identifier);

        /* Create resulting Node and add to tree */
        final TestPlanNode result = new TestPlanNode(testNodeType, identifier, instanceNumber);
        parent.addChild(result);

        /* Record nodes for this Identifier */
        List<TestPlanNode> nodesForIdentifier = testPlanNodesByIdentifierMap.get(identifier);
        if (nodesForIdentifier == null) {
            nodesForIdentifier = new ArrayList<TestPlanNode>();
            testPlanNodesByIdentifierMap.put(identifier, nodesForIdentifier);
        }
        nodesForIdentifier.add(result);

        return result;
    }
}