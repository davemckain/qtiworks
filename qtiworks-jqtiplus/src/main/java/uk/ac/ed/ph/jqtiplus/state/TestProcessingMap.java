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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the key information about an {@link AssessmentTest} used during processing.
 * <p>
 * Usage: an instance of this class can be safely used by multiple Threads
 *
 * @see ItemProcessingMap
 * @see TestProcessingInitializer
 *
 * @author David McKain
 */
public final class TestProcessingMap implements Serializable {

    private static final long serialVersionUID = -1103858865281372725L;

    private final ResolvedAssessmentTest resolvedAssessmentTest;
    private final boolean isValid;

    private final List<AbstractPart> abstractPartList;
    private final Map<AbstractPart, Integer> abstractPartToGlobalIndexMap;

    private final Map<AbstractPart, EffectiveItemSessionControl> effectiveItemSessionControlMap;

    private final Map<Identifier, OutcomeDeclaration> validOutcomeDeclarationMap;
    private final ResponseDeclaration durationResponseDeclaration;

    /**
     * Map of {@link ItemProcessingMap}s for each {@link AssessmentItem} that was successfully
     * looked up (but not necessarily valid), keyed on the resolved System ID of the
     * {@link AssessmentItemRef} referring to it.
     */
    private final Map<URI, ItemProcessingMap> itemProcessingMapMap;

    public TestProcessingMap(final ResolvedAssessmentTest resolvedAssessmentTest, final boolean isValid,
            final List<AbstractPart> abstractPartListBuilder,
            final Map<AbstractPart, EffectiveItemSessionControl> effectiveItemSessionControlMap,
            final Map<Identifier, OutcomeDeclaration> outcomeDeclarationMapBuilder,
            final ResponseDeclaration durationResponseDeclaration,
            final Map<URI, ItemProcessingMap> itemProcessingMapMapBuilder) {
        this.resolvedAssessmentTest = resolvedAssessmentTest;
        this.durationResponseDeclaration = durationResponseDeclaration;
        this.isValid = isValid;

        /* Record AbstractParts */
        this.abstractPartList = Collections.unmodifiableList(abstractPartListBuilder);
        this.abstractPartToGlobalIndexMap = new HashMap<AbstractPart, Integer>();
        for (int i=0; i<abstractPartListBuilder.size(); i++) {
            abstractPartToGlobalIndexMap.put(abstractPartListBuilder.get(i), Integer.valueOf(i));
        }

        /* Record the EffectiveItemSessionControl for each Node */
        this.effectiveItemSessionControlMap = Collections.unmodifiableMap(effectiveItemSessionControlMap);

        /* Record (valid) outcome variables in test */
        this.validOutcomeDeclarationMap = Collections.unmodifiableMap(new LinkedHashMap<Identifier, OutcomeDeclaration>(outcomeDeclarationMapBuilder));

        /* Record maps for each referenced item */
        this.itemProcessingMapMap = Collections.unmodifiableMap(new LinkedHashMap<URI, ItemProcessingMap>(itemProcessingMapMapBuilder));
    }

    public boolean isValid() {
        return isValid;
    }

    public ResolvedAssessmentTest getResolvedAssessmentTest() {
        return resolvedAssessmentTest;
    }

    public List<AbstractPart> getAbstractPartList() {
        return abstractPartList;
    }

    public int getAbstractPartGlobalIndex(final AbstractPart abstractPart) {
        final Integer result = abstractPartToGlobalIndexMap.get(abstractPart);
        return result!=null ? result.intValue() : -1;
    }


    public Map<AbstractPart, EffectiveItemSessionControl> getEffectiveItemSessionControlMap() {
        return effectiveItemSessionControlMap;
    }

    public boolean isValidVariableIdentifier(final Identifier identifier) {
        return validOutcomeDeclarationMap.containsKey(identifier) ||
                QtiConstants.VARIABLE_DURATION_IDENTIFIER.equals(identifier);
    }

    public Map<Identifier, OutcomeDeclaration> getValidOutcomeDeclarationMap() {
        return validOutcomeDeclarationMap;
    }

    public ResponseDeclaration getDurationResponseDeclaration() {
        return durationResponseDeclaration;
    }

    public Map<URI, ItemProcessingMap> getItemProcessingMapMap() {
        return itemProcessingMapMap;
    }

    public AbstractPart resolveAbstractPart(final TestPlanNode testPlanNode) {
        if (testPlanNode.getTestNodeType()==TestNodeType.ROOT) {
            throw new IllegalArgumentException("This method should not be called for " + testPlanNode.getTestNodeType());
        }
        final int abstractPartGlobalIndex = testPlanNode.getAbstractPartGlobalIndex();
        if (abstractPartGlobalIndex<0 || abstractPartGlobalIndex>=abstractPartList.size()) {
            throw new IllegalStateException("Global index of " + testPlanNode + " is out of bounds");
        }
        return abstractPartList.get(abstractPartGlobalIndex);
    }

    public EffectiveItemSessionControl resolveEffectiveItemSessionControl(final TestPlanNode testPlanNode) {
        if (testPlanNode.getTestNodeType()==TestNodeType.ROOT) {
            throw new IllegalArgumentException("This method should not be called for " + testPlanNode.getTestNodeType());
        }
        final AbstractPart abstractPart = resolveAbstractPart(testPlanNode);
        return effectiveItemSessionControlMap.get(abstractPart);
    }

    public ItemProcessingMap resolveItemProcessingMap(final TestPlanNode testPlanNode) {
        if (testPlanNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF) {
            throw new IllegalArgumentException("Expected " + TestNodeType.ASSESSMENT_ITEM_REF + " but got " + testPlanNode.getTestNodeType());
        }
        final AbstractPart abstractPart = resolveAbstractPart(testPlanNode);
        if (!(abstractPart instanceof AssessmentItemRef)) {
            throw new IllegalStateException("Expected" + testPlanNode
                    + " to resolve to an " + AssessmentItemRef.class.getSimpleName()
                    + " but got " + abstractPart);
        }
        final AssessmentItemRef assessmentItemRef = (AssessmentItemRef) abstractPart;
        final URI resolvedUri = resolvedAssessmentTest.getSystemIdByItemRefMap().get(assessmentItemRef);
        if (resolvedUri==null) {
            throw new IllegalStateException("System ID resolution lookup failed for " + assessmentItemRef + " resolved from " + testPlanNode);
        }
        final ItemProcessingMap itemProcessingMap = itemProcessingMapMap.get(resolvedUri);
        if (itemProcessingMap==null) {
            throw new IllegalStateException("ItemProcessingMap lookup failed for " + resolvedUri + " resolved from " + testPlanNode);
        }
        return itemProcessingMap;
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
