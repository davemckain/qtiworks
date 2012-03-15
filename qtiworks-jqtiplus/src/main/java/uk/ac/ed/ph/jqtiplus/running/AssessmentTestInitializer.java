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
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.Ordering;
import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.state.AbstractPartState;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemRefState;
import uk.ac.ed.ph.jqtiplus.state.AssessmentSectionState;
import uk.ac.ed.ph.jqtiplus.state.AssessmentTestState;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.SectionPartState;
import uk.ac.ed.ph.jqtiplus.state.SectionPartStateKey;
import uk.ac.ed.ph.jqtiplus.state.TestPartState;
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
 * Helper that performs the selection and ordering of the underlying Objects in the {@link AssessmentTest}.
 * 
 * @author David McKain
 */
final class AssessmentTestInitializer {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentTestInitializer.class);

    /**
     * Private class used to build up computed tree structure below {@link TestPart}s.
     */
    private static class RuntimeTreeNode {

        private final SectionPart sectionPart;

        private final List<RuntimeTreeNode> childNodes;

        public RuntimeTreeNode(SectionPart sectionPart, List<RuntimeTreeNode> childNodes) {
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
            return getClass().getSimpleName() + "@" + hashCode()
                    + "(sectionPart=" + sectionPart
                    + ",childNodes=" + childNodes
                    + ")";
        }
    }

    private final AssessmentTest test;

    private final AssessmentTestState testState;

    private final Map<Identifier, List<AbstractPartState>> abstractPartStateMap;

    private final Map<SectionPartStateKey, SectionPartState> sectionPartStateMap;

    public AssessmentTestInitializer(AssessmentTest test, AssessmentTestState testState) {
        this.test = test;
        this.testState = testState;
        this.abstractPartStateMap = new HashMap<Identifier, List<AbstractPartState>>();
        this.sectionPartStateMap = new HashMap<SectionPartStateKey, SectionPartState>();
    }

    public void run() {
        logger.debug("Initialising state Object for test {}", test.getIdentifier());
        abstractPartStateMap.clear();

        final List<TestPartState> testPartStates = new ArrayList<TestPartState>();
        for (final TestPart testPart : test.getTestParts()) {
            /* Process test part */
            final List<RuntimeTreeNode> runtimeChildNodes = doTestPart(testPart);
            logger.debug("Result of processing testPart {} is {}", testPart.getIdentifier(), runtimeChildNodes);

            /* Build up state */
            final TestPartState testPartState = buildTestPartState(testPart, runtimeChildNodes);
            testPartStates.add(testPartState);
        }

        testState.initialize(testPartStates, abstractPartStateMap, sectionPartStateMap);
        logger.info("Resulting state for test {} is {}", test.getIdentifier(), testState);
    }

    private List<RuntimeTreeNode> doTestPart(TestPart testPart) {
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

    private RuntimeTreeNode doSectionPart(SectionPart sectionPart) {
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

    private RuntimeTreeNode doAssessmentSectionState(AssessmentSection section) {
        logger.debug("Initialising instance of assessmentSection {}", section.getIdentifier());

        /* We first need to select which children we're going to have */
        List<SectionPart> afterSelection = new ArrayList<SectionPart>();
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

    private RuntimeTreeNode doAssessmentItemRef(AssessmentItemRef itemRef) {
        logger.debug("Initialising instance of assessmentItemRef {}", itemRef.getIdentifier());
        return new RuntimeTreeNode(itemRef, Collections.<RuntimeTreeNode> emptyList());
    }

    private List<SectionPart> selectSectionParts(AssessmentSection assessmentSection) {
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

    private List<RuntimeTreeNode> orderSectionParts(List<RuntimeTreeNode> childNodes) {
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

    private TestPartState buildTestPartState(TestPart testPart, List<RuntimeTreeNode> runtimeChildNodes) {
        final TestPartState result = new TestPartState(testState, testPart.getIdentifier(), buildState(runtimeChildNodes));
        registerAbstractPartStateLookup(testPart, result);
        return result;
    }

    private List<SectionPartState> buildState(List<RuntimeTreeNode> treeNodes) {
        final List<SectionPartState> result = new ArrayList<SectionPartState>();
        for (int i = 0, size = treeNodes.size(); i < size; i++) {
            final RuntimeTreeNode treeNode = treeNodes.get(i);
            result.add(buildState(treeNode, i));
        }
        return result;
    }

    private SectionPartState buildState(RuntimeTreeNode treeNode, int siblingIndex) {
        final SectionPart sectionPart = treeNode.getSectionPart();
        SectionPartState result;
        if (sectionPart instanceof AssessmentSection) {
            final AssessmentSection section = (AssessmentSection) sectionPart;
            final List<SectionPartState> childStates = buildState(treeNode.getChildNodes());
            result = new AssessmentSectionState(testState, section.getIdentifier(), siblingIndex, childStates);
        }
        else if (sectionPart instanceof AssessmentItemRef) {
            final AssessmentItemRef itemRef = (AssessmentItemRef) sectionPart;

            final ItemSessionState itemState = new ItemSessionState();
            final AssessmentItemRefState itemRefState = new AssessmentItemRefState(testState, itemRef.getIdentifier(), siblingIndex, itemState);
// FIXME: Need to set item's duration to the correct value here. We used to have an ItemTimeRecord but this has been removed
//            itemState.setTimeRecord(itemRefState.getTimeRecord());
            result = itemRefState;
        }
        else {
            throw new QtiLogicException("Unexpected logic branch: sectionPart=" + sectionPart);
        }
        registerAbstractPartStateLookup(sectionPart, result);
        sectionPartStateMap.put(result.getSectionPartStateKey(), result);
        return result;
    }

    private void registerAbstractPartStateLookup(AbstractPart abstractPart, AbstractPartState abstractPartState) {
        List<AbstractPartState> statesForIdentifier = abstractPartStateMap.get(abstractPart.getIdentifier());
        if (statesForIdentifier == null) {
            statesForIdentifier = new ArrayList<AbstractPartState>();
            abstractPartStateMap.put(abstractPart.getIdentifier(), statesForIdentifier);
        }
        statesForIdentifier.add(abstractPartState);
    }
}