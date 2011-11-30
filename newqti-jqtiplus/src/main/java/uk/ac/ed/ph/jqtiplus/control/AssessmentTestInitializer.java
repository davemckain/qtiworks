/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.Ordering;
import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.state.AbstractPartState;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemRefState;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemState;
import uk.ac.ed.ph.jqtiplus.state.AssessmentSectionState;
import uk.ac.ed.ph.jqtiplus.state.AssessmentTestState;
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
 * @author  David McKain
 * @version $Revision: 2775 $
 */
final class AssessmentTestInitializer {

    protected static Logger logger = LoggerFactory.getLogger(AssessmentTestInitializer.class);
    
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
    
    private AssessmentTest test;
    private AssessmentTestState testState;
    private final Map<Identifier, List<AbstractPartState>> abstractPartStateMap;
    private final Map<SectionPartStateKey, SectionPartState> sectionPartStateMap;
    
    public AssessmentTestInitializer(AssessmentTest test,  AssessmentTestState testState) {
        this.test = test;
        this.testState = testState;
        this.abstractPartStateMap = new HashMap<Identifier, List<AbstractPartState>>();
        this.sectionPartStateMap = new HashMap<SectionPartStateKey, SectionPartState>();
    }
    
    public void run() {
        logger.debug("Initialising state Object for test {}", test.getIdentifier());
        abstractPartStateMap.clear();
        
        List<TestPartState> testPartStates = new ArrayList<TestPartState>();
        for (TestPart testPart : test.getTestParts()) {
            /* Process test part */
            List<RuntimeTreeNode> runtimeChildNodes = doTestPart(testPart);
            logger.debug("Result of processing testPart {} is {}", testPart.getIdentifier(), runtimeChildNodes);
            
            /* Build up state */
            TestPartState testPartState = buildTestPartState(testPart, runtimeChildNodes);
            testPartStates.add(testPartState);
        }
        
        testState.initialize(testPartStates, abstractPartStateMap, sectionPartStateMap);
        logger.info("Resulting state for test {} is {}", test.getIdentifier(), testState);
    }

    private List<RuntimeTreeNode> doTestPart(TestPart testPart) {
        logger.debug("Initialising testPart {}", testPart.getIdentifier());
        
        /* Process each AssessmentSection */
        List<RuntimeTreeNode> runtimeChildNodes = new ArrayList<RuntimeTreeNode>();
        for (AssessmentSection section : testPart.getAssessmentSections()) {
            RuntimeTreeNode sectionNode = doAssessmentSectionState(section);
            
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
            throw new QTILogicException("Unexpected logic branch: sectionPart=" + sectionPart);
        }
        return result;
    }
    
    private RuntimeTreeNode doAssessmentSectionState(AssessmentSection section) {
        logger.debug("Initialising instance of assessmentSection {}", section.getIdentifier());

        /* We first need to select which children we're going to have */
        List<SectionPart> afterSelection = new ArrayList<SectionPart>();
        if (section.getSelection()!=null) {
            /* Perform requested selection */
            afterSelection = selectSectionParts(section);
        }
        else {
            /* Select all children */
            afterSelection = section.getSectionParts();
        }
        logger.info("After selection for section {} have {}", section, afterSelection);
        
        /* Set up each selected child */
        List<RuntimeTreeNode> childNodes = new ArrayList<RuntimeTreeNode>();
        for (SectionPart sectionPart : afterSelection) {
            childNodes.add(doSectionPart(sectionPart));
        }
        logger.info("Initialisation of child Nodes for section {} resulted in {}", section, childNodes);

        /* Then we do ordering, if requested */
        List<RuntimeTreeNode> afterOrdering;
        Ordering ordering = section.getOrdering();
        if (ordering!=null && ordering.getShuffle()) {
            afterOrdering = orderSectionParts(childNodes);
        }
        else {
            afterOrdering = childNodes;
        }
        logger.info("Ordering of child Nodes for section {} resulted in {}", section, afterOrdering);
        
        /* Flatten invisible child assessmentSections */
        List<RuntimeTreeNode> afterFlattening = new ArrayList<RuntimeTreeNode>();
        for (RuntimeTreeNode childNode : afterOrdering) {
            if (childNode.getSectionPart() instanceof AssessmentSection) {
                AssessmentSection childSection = (AssessmentSection) childNode.getSectionPart();
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
        return new RuntimeTreeNode(itemRef, Collections.<RuntimeTreeNode>emptyList());
    }

    private List<SectionPart> selectSectionParts(AssessmentSection assessmentSection) {
        List<SectionPart> children = assessmentSection.getSectionParts();
        int childCount = children.size();
        
        /* Work out how many selections to make for each child */
        int[] selectionCounts = new int[childCount]; /* (Number of selections made per child) */
        int requiredCount = 0;
        for (int i=0; i<childCount; i++) {
            if (children.get(i).getRequired()) {
                selectionCounts[i]++;
                requiredCount++;
            }
        }
        int toSelect = assessmentSection.getSelection().getSelect() - requiredCount;
        Random random = new Random(System.currentTimeMillis());
        if (assessmentSection.getSelection().getWithReplacement()) {
            for (int i=0; i<toSelect; i++) {
                int index = random.nextInt(childCount);
                selectionCounts[index]++;
            }
        }
        else {
            for (int i=0; i<toSelect; i++) {
                int index = random.nextInt(childCount - requiredCount - i);
                for (int j=0; j<selectionCounts.length; j++) {
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
        List<SectionPart> result = new ArrayList<SectionPart>();
        for (int i=0; i<childCount; i++) {
            SectionPart sectionPart = children.get(i);
            for (int j=0; j<selectionCounts[i]; j++) {
                result.add(sectionPart);
            }
        }
        return result;
    }
    
    private List<RuntimeTreeNode> orderSectionParts(List<RuntimeTreeNode> childNodes) {
        /* Merge all invisible assessmentSections with keepTogether=false now */
        List<RuntimeTreeNode> beforeShuffle = new ArrayList<RuntimeTreeNode>();
        for (RuntimeTreeNode item : childNodes) {
            SectionPart sectionPart = item.getSectionPart();
            if (sectionPart instanceof AssessmentSection) {
                AssessmentSection section = (AssessmentSection) sectionPart;
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
        List<RuntimeTreeNode> toShuffle = new ArrayList<RuntimeTreeNode>();
        for (RuntimeTreeNode item : beforeShuffle) {
            if (!item.getSectionPart().getFixed()) {
                toShuffle.add(item);
            }
        }
        
        /* Perform shuffle */
        Collections.shuffle(toShuffle);
        
        /* Merge the shuffled items in */
        List<RuntimeTreeNode> afterShuffle = new ArrayList<RuntimeTreeNode>();
        Iterator<RuntimeTreeNode> shuffledIterator = toShuffle.iterator();
        for (RuntimeTreeNode item : beforeShuffle) {
            if (!item.getSectionPart().getFixed()) {
                afterShuffle.add(shuffledIterator.next());
            }
            else {
                afterShuffle.add(item);
            }
        }
        
        /* Finally, merge remaining invisible assessmentSections with keepTogether=true */
        List<RuntimeTreeNode> result = new ArrayList<RuntimeTreeNode>();
        for (RuntimeTreeNode item : afterShuffle) {
            SectionPart sectionPart = item.getSectionPart();
            if (sectionPart instanceof AssessmentSection) {
                AssessmentSection section = (AssessmentSection) sectionPart;
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
        TestPartState result = new TestPartState(testState, testPart.getIdentifier(), buildState(runtimeChildNodes));
        registerAbstractPartStateLookup(testPart, result);
        return result;
    }
    
    private List<SectionPartState> buildState(List<RuntimeTreeNode> treeNodes) {
        List<SectionPartState> result = new ArrayList<SectionPartState>();
        for (int i=0, size=treeNodes.size(); i<size; i++) {
            RuntimeTreeNode treeNode = treeNodes.get(i);
            result.add(buildState(treeNode, i));
        }
        return result;
    }
    
    private SectionPartState buildState(RuntimeTreeNode treeNode, int siblingIndex) {
        SectionPart sectionPart = treeNode.getSectionPart();
        SectionPartState result;
        if (sectionPart instanceof AssessmentSection) {
            AssessmentSection section = (AssessmentSection) sectionPart;
            List<SectionPartState> childStates = buildState(treeNode.getChildNodes());
            result = new AssessmentSectionState(testState, section.getIdentifier(), siblingIndex, childStates);
        }
        else if (sectionPart instanceof AssessmentItemRef) {
            AssessmentItemRef itemRef = (AssessmentItemRef) sectionPart;

            AssessmentItemState itemState = new AssessmentItemState();
            AssessmentItemRefState itemRefState = new AssessmentItemRefState(testState, itemRef.getIdentifier(), siblingIndex, itemState);
            itemState.setTimeRecord(itemRefState.getTimeRecord());
            result = itemRefState;
        }
        else {
            throw new QTILogicException("Unexpected logic branch: sectionPart=" + sectionPart);
        }
        registerAbstractPartStateLookup(sectionPart, result);
        sectionPartStateMap.put(result.getSectionPartStateKey(), result);
        return result;
    }
    
    private void registerAbstractPartStateLookup(AbstractPart abstractPart, AbstractPartState abstractPartState) {
        List<AbstractPartState> statesForIdentifier = abstractPartStateMap.get(abstractPart.getIdentifier());
        if (statesForIdentifier==null) {
            statesForIdentifier = new ArrayList<AbstractPartState>();
            abstractPartStateMap.put(abstractPart.getIdentifier(), statesForIdentifier);
        }
        statesForIdentifier.add(abstractPartState);
    }
}