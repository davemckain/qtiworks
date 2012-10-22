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
import uk.ac.ed.ph.jqtiplus.node.test.Selection;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.notification.NotificationFirer;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeInstanceKey;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.net.URI;
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
 * This class generates a {@link TestPlan} for the given {@link AssessmentTest}.
 * <p>
 * This is the first step in delivering a test to a candidate.
 *
 * Usage: use once and discard; not thread safe.
 *
 * @author David McKain
 */
public final class TestPlanner {

    private static final Logger logger = LoggerFactory.getLogger(TestPlanner.class);

    /** Private class used to build up a temporary tree structure below {@link TestPart}s. */
    private static class BuildTreeNode {

        private final SectionPart sectionPart;
        private final List<BuildTreeNode> childNodes;

        public BuildTreeNode(final SectionPart sectionPart, final List<BuildTreeNode> childNodes) {
            this.sectionPart = sectionPart;
            this.childNodes = childNodes;
        }

        public SectionPart getSectionPart() {
            return sectionPart;
        }

        public List<BuildTreeNode> getChildNodes() {
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

    private final TestProcessingMap testProcessingMap;
    private final ResolvedAssessmentTest resolvedAssessmentTest;
    private final AssessmentTest test;
    private final NotificationFirer notificationFirer;

    private final Map<Identifier, List<TestPlanNode>> testPlanNodesByIdentifierMap;
    private final TestPlanNode testPlanRootNode;

    public TestPlanner(final TestProcessingMap testProcessingMap, final NotificationFirer notificationFirer) {
        this.testProcessingMap = testProcessingMap;
        this.resolvedAssessmentTest = testProcessingMap.getResolvedAssessmentTest();
        this.test = resolvedAssessmentTest.getTestLookup().extractIfSuccessful();
        this.notificationFirer = notificationFirer;
        this.testPlanNodesByIdentifierMap = new HashMap<Identifier, List<TestPlanNode>>();
        this.testPlanRootNode = new TestPlanNode(TestNodeType.ROOT, null);
    }

    public TestPlan generateTestPlan() {
        if (test==null) {
            throw new IllegalStateException("Test lookup did not succeed, so test cannot be run");
        }
        logger.debug("Creating a test plan for test {}", test.getIdentifier());
        testPlanNodesByIdentifierMap.clear();

        final List<TestPlanNode> testPlanNodes = new ArrayList<TestPlanNode>();
        for (final TestPart testPart : test.getTestParts()) {
            /* Process test part */
            final List<BuildTreeNode> runtimeChildNodes = doTestPart(testPart);
            logger.debug("Result of processing testPart {} is {}", testPart.getIdentifier(), runtimeChildNodes);

            /* Build up tree */
            final TestPlanNode testPlanNode = recordTestPartPlan(testPart, runtimeChildNodes);
            testPlanNodes.add(testPlanNode);
        }

        final TestPlan result = new TestPlan(testPlanRootNode, testPlanNodesByIdentifierMap);
        logger.info("Computed test plan for test {} is {}", test.getIdentifier(), result);
        return result;
    }

    private List<BuildTreeNode> doTestPart(final TestPart testPart) {
        logger.debug("Handling testPart {}", testPart.getIdentifier());

        /* Process each AssessmentSection */
        final List<BuildTreeNode> runtimeChildNodes = new ArrayList<BuildTreeNode>();
        for (final AssessmentSection section : testPart.getAssessmentSections()) {
            final BuildTreeNode sectionNode = doAssessmentSection(section);

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

    private BuildTreeNode doAssessmentSection(final AssessmentSection section) {
        logger.debug("Handling assessmentSection {}", section.getIdentifier());

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

        /* Handle each selected child */
        final List<BuildTreeNode> childNodes = new ArrayList<BuildTreeNode>();
        for (final SectionPart sectionPart : afterSelection) {
            final BuildTreeNode selectedSectionPart = doSectionPart(sectionPart);
            if (selectedSectionPart!=null) {
                childNodes.add(selectedSectionPart);
            }
        }
        logger.debug("Initialisation of child Nodes for section {} resulted in {}", section, childNodes);

        /* Then we do ordering, if requested */
        List<BuildTreeNode> afterOrdering;
        final Ordering ordering = section.getOrdering();
        if (ordering != null && ordering.getShuffle()) {
            afterOrdering = orderSectionParts(childNodes);
        }
        else {
            afterOrdering = childNodes;
        }
        logger.info("Ordering of child Nodes for section {} resulted in {}", section, afterOrdering);

        /* Flatten invisible child assessmentSections */
        final List<BuildTreeNode> afterFlattening = new ArrayList<BuildTreeNode>();
        for (final BuildTreeNode childNode : afterOrdering) {
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

        return new BuildTreeNode(section, afterFlattening);
    }

    private BuildTreeNode doSectionPart(final SectionPart sectionPart) {
        BuildTreeNode result;
        if (sectionPart instanceof AssessmentSection) {
            result = doAssessmentSection((AssessmentSection) sectionPart);
        }
        else if (sectionPart instanceof AssessmentItemRef) {
            result = doAssessmentItemRef((AssessmentItemRef) sectionPart);
        }
        else {
            throw new QtiLogicException("Unexpected logic branch: sectionPart=" + sectionPart);
        }
        return result;
    }


    private BuildTreeNode doAssessmentItemRef(final AssessmentItemRef itemRef) {
        logger.debug("Handling assessmentItemRef {}", itemRef.getIdentifier());
        final URI itemSystemId = resolvedAssessmentTest.getSystemIdByItemRefMap().get(itemRef);
        if (testProcessingMap.getItemProcessingMapMap().containsKey(itemSystemId)) {
            return new BuildTreeNode(itemRef, Collections.<BuildTreeNode> emptyList());
        }
        else {
            notificationFirer.fireRuntimeWarning(itemRef,
                    "This item referred by identifier " + itemRef.getIdentifier()
                    + " was not successfully resolved so is being dropped from the test plan");
        }
        return null;
    }

    private List<SectionPart> selectSectionParts(final AssessmentSection assessmentSection) {
        final List<SectionPart> children = assessmentSection.getSectionParts();
        final Selection selection = assessmentSection.getSelection();
        final int childCount = children.size();
        int requestedSelections = selection.getSelect();

        /* Handle edge and corner cases */
        if (requestedSelections < 0) {
            notificationFirer.fireRuntimeWarning(assessmentSection,
                    "The requested number of selections (" + requestedSelections
                    + ") is negative and is being treated as an empty selection");
            requestedSelections = 0;
        }
        if (requestedSelections==0) {
            return Collections.emptyList();
        }
        if (!selection.getWithReplacement() && requestedSelections > childCount) {
            /* Trivial corner case: not enough children for selection without replacement */
            notificationFirer.fireRuntimeWarning(assessmentSection,
                    "The requested number of selections (" + requestedSelections
                    + ") is greater than the number of children (" + childCount
                    + "), which makes selection without replacement impossible. "
                    + "As a result, all children have been selected.");
            return children;
        }

        final int[] selectionsPerChild = new int[childCount]; /* (Number of selections to make per child) */
        int requiredChildCount = 0; /* (Number of children marked as required) */

        /* Note any required selections */
        for (int i=0; i<childCount; i++) {
            if (children.get(i).getRequired()) {
                selectionsPerChild[i]++;
                requiredChildCount++;
            }
        }
        if (requiredChildCount > requestedSelections) {
            notificationFirer.fireRuntimeWarning(assessmentSection,
                    "The requested number of selections (" + requestedSelections
                    + ") was smaller than the number of children marked as 'required' ("
                    + requiredChildCount
                    + "). All required children will be selected");
            requestedSelections = requiredChildCount;
        }

        /* Now decide how many selections to make from remaining children */
        final int remainingSelections = requestedSelections - requiredChildCount;
        if (remainingSelections > 0) {
            final Random random = new Random(System.currentTimeMillis());
            if (selection.getWithReplacement()) {
                /* Selection with replacement */
                for (int i=0; i<remainingSelections; i++) {
                    final int index = random.nextInt(childCount);
                    selectionsPerChild[index]++;
                }
            }
            else {
                /* Selection without replacement */
                for (int i=0; i<remainingSelections; i++) {
                    int index = random.nextInt(childCount - requiredChildCount - i);
                    for (int j=0; j<selectionsPerChild.length; j++) {
                        if (selectionsPerChild[j] == 0) {
                            index--;
                        }
                        if (index == -1) {
                            selectionsPerChild[j]++;
                            break;
                        }
                    }
                }
            }
        }

        /* Now perform selection */
        final List<SectionPart> result = new ArrayList<SectionPart>();
        for (int i = 0; i < childCount; i++) {
            final SectionPart sectionPart = children.get(i);
            for (int j = 0; j < selectionsPerChild[i]; j++) {
                result.add(sectionPart);
            }
        }
        return result;
    }

    private List<BuildTreeNode> orderSectionParts(final List<BuildTreeNode> childNodes) {
        /* Merge all invisible assessmentSections with keepTogether=false now */
        final List<BuildTreeNode> beforeShuffle = new ArrayList<BuildTreeNode>();
        for (final BuildTreeNode item : childNodes) {
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
        final List<BuildTreeNode> toShuffle = new ArrayList<BuildTreeNode>();
        for (final BuildTreeNode item : beforeShuffle) {
            if (!item.getSectionPart().getFixed()) {
                toShuffle.add(item);
            }
        }

        /* Perform shuffle */
        Collections.shuffle(toShuffle);

        /* Merge the shuffled items in */
        final List<BuildTreeNode> afterShuffle = new ArrayList<BuildTreeNode>();
        final Iterator<BuildTreeNode> shuffledIterator = toShuffle.iterator();
        for (final BuildTreeNode item : beforeShuffle) {
            if (!item.getSectionPart().getFixed()) {
                afterShuffle.add(shuffledIterator.next());
            }
            else {
                afterShuffle.add(item);
            }
        }

        /* Finally, merge remaining invisible assessmentSections with keepTogether=true */
        final List<BuildTreeNode> result = new ArrayList<BuildTreeNode>();
        for (final BuildTreeNode item : afterShuffle) {
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

    private TestPlanNode recordTestPartPlan(final TestPart testPart, final List<BuildTreeNode> runtimeChildNodes) {
        final TestPlanNode result = recordTestPlanNode(testPlanRootNode, TestNodeType.TEST_PART, testPart.getIdentifier());
        recordChildPlans(result, runtimeChildNodes);
        return result;
    }

    private List<TestPlanNode> recordChildPlans(final TestPlanNode targetParent, final List<BuildTreeNode> treeNodes) {
        final List<TestPlanNode> result = new ArrayList<TestPlanNode>();
        for (final BuildTreeNode treeNode : treeNodes) {
            result.add(recordChildPlans(targetParent, treeNode));
        }
        return result;
    }

    private TestPlanNode recordChildPlans(final TestPlanNode targetParent, final BuildTreeNode treeNode) {
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
        final TestPlanNodeInstanceKey testPlanNodeInstanceKey = new TestPlanNodeInstanceKey(identifier, instanceNumber);
        final TestPlanNode result = new TestPlanNode(testNodeType, testPlanNodeInstanceKey);
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