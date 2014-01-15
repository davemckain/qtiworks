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
package uk.ac.ed.ph.jqtiplus.running;

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;
import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup;
import uk.ac.ed.ph.jqtiplus.state.EffectiveItemSessionControl;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationResult;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This helper class analyses a {@link ResolvedAssessmentTest} and generates an
 * {@link TestProcessingMap} that can be reused by {@link TestSessionController}s.
 *
 * @see TestProcessingMap
 * @see ItemProcessingInitializer
 * @see TestSessionController
 *
 * @author David McKain
 */
public final class TestProcessingInitializer {

    private final ResolvedAssessmentTest resolvedAssessmentTest;
    private final TestValidationResult testValidationResult;
    private final boolean isTestValid;
    private final LinkedHashMap<Identifier, OutcomeDeclaration> outcomeDeclarationMapBuilder;

    /**
     * Preferred constructor. Accepts a full {@link TestValidationResult}, which contains
     * good validity information about each item within.
     */
    public TestProcessingInitializer(final TestValidationResult testValidationResult) {
        this.testValidationResult = testValidationResult;
        this.resolvedAssessmentTest = testValidationResult.getResolvedAssessmentTest();
        this.isTestValid = testValidationResult.isValid();
        this.outcomeDeclarationMapBuilder = new LinkedHashMap<Identifier, OutcomeDeclaration>();
    }

    /**
     * Alternative constructor. Accepts a {@link ResolvedAssessmentTest} and overall indication
     * of whether the test is valid as a whole.
     */
    public TestProcessingInitializer(final ResolvedAssessmentTest resolvedAssessmentTest, final boolean isTestValid) {
        this.testValidationResult = null;
        this.resolvedAssessmentTest = resolvedAssessmentTest;
        this.isTestValid = isTestValid;
        this.outcomeDeclarationMapBuilder = new LinkedHashMap<Identifier, OutcomeDeclaration>();
    }

    /**
     * Builds and returns the {@link TestProcessingMap}.
     *
     * @return resulting {@link TestProcessingMap}, or null if the underlying {@link AssessmentTest}
     *   lookup was unsuccessful
     */
    public TestProcessingMap initialize() {
        if (!resolvedAssessmentTest.getTestLookup().wasSuccessful()) {
            return null;
        }
        final AssessmentTest test = resolvedAssessmentTest.getTestLookup().extractAssumingSuccessful();

        /* Extract all usable AbstractParts. (Currently, usable == all but this may change) */
        final List<AbstractPart> abstractParts = QueryUtils.search(AbstractPart.class, test);

        /* Compute effective values for ItemSessionControl for each AbstractPart */
        final Map<AbstractPart, EffectiveItemSessionControl> effectiveItemSessionControlMap = computeEffectiveItemSessionControlMap(test);

        /* Extract test's duration variable declaration */
        final ResponseDeclaration durationResponseDeclaration = test.getDurationResponseDeclaration();

        /* Record all valid outcomeVariable declarations */
        for (final OutcomeDeclaration declaration : test.getOutcomeDeclarations()) {
            doOutcomeVariable(declaration);
        }

        /* Now repeat this for each resolved item that was successfully looked up */
        final Map<URI, ItemProcessingMap> itemProcessingMapBuilder = new LinkedHashMap<URI, ItemProcessingMap>();
        if (testValidationResult!=null) {
            /* This initializer was built from validation result, so we can be quite fine grained */
            for (final ItemValidationResult itemValidationResult : testValidationResult.getItemValidationResults()) {
                final RootNodeLookup<AssessmentItem> itemLookup = itemValidationResult.getResolvedAssessmentItem().getItemLookup();
                if (itemLookup.wasSuccessful()) {
                    final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(itemValidationResult).initialize();
                    itemProcessingMapBuilder.put(itemLookup.getSystemId(), itemProcessingMap);
                }
            }
        }
        else {
            /* This initializer was just built from resolved test. In this case, the best we
             * can say about the validity of individual items is by using the overall validity
             * of the test, which may result in some false negatives.
             */
            for (final ResolvedAssessmentItem resolvedAssessmentItem : resolvedAssessmentTest.getResolvedAssessmentItemBySystemIdMap().values()) {
                final RootNodeLookup<AssessmentItem> itemLookup = resolvedAssessmentItem.getItemLookup();
                if (itemLookup.wasSuccessful()) {
                    final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(resolvedAssessmentItem, isTestValid).initialize();
                    itemProcessingMapBuilder.put(itemLookup.getSystemId(), itemProcessingMap);
                }
            }

        }

        /* That's it! */
        return new TestProcessingMap(resolvedAssessmentTest, isTestValid,
                abstractParts, effectiveItemSessionControlMap, outcomeDeclarationMapBuilder,
                durationResponseDeclaration, itemProcessingMapBuilder);
    }

    private void doOutcomeVariable(final OutcomeDeclaration declaration) {
        final List<VariableDeclaration> variableDeclarations = resolvedAssessmentTest.resolveTestVariable(declaration.getIdentifier());
        if (variableDeclarations.size()==1) {
            outcomeDeclarationMapBuilder.put(declaration.getIdentifier(), declaration);
        }
    }

    private Map<AbstractPart, EffectiveItemSessionControl> computeEffectiveItemSessionControlMap(final AssessmentTest test) {
        return new EffectiveItemSessionControlBuilder(test).run();
    }

    /**
     * Helper class to compute the {@link EffectiveItemSessionControl} for each {@link AbstractPart}
     * in the {@link AssessmentTest};
     *
     * @author David McKain
     */
    private static class EffectiveItemSessionControlBuilder {

        private final AssessmentTest test;

        private final Map<AbstractPart, EffectiveItemSessionControl> resultBuilder;

        public EffectiveItemSessionControlBuilder(final AssessmentTest test) {
            this.test = test;
            this.resultBuilder = new HashMap<AbstractPart, EffectiveItemSessionControl>();
        }

        public Map<AbstractPart, EffectiveItemSessionControl> run() {
            final EffectiveItemSessionControl defaultControl = EffectiveItemSessionControl.createDefault();
            for (final TestPart testPart : test.getTestParts()) {
                handleTestPart(testPart, defaultControl);
            }
            return resultBuilder;
        }

        private void handleTestPart(final TestPart testPart, final EffectiveItemSessionControl defaultControl) {
            final ItemSessionControl itemSessionControl = testPart.getItemSessionControl();
            final EffectiveItemSessionControl controlForTestPart = EffectiveItemSessionControl.override(defaultControl, itemSessionControl);
            resultBuilder.put(testPart, controlForTestPart);

            for (final AssessmentSection assessmentSection : testPart.getAssessmentSections()) {
                handleSectionPart(assessmentSection, controlForTestPart);
            }
        }

        private void handleSectionPart(final SectionPart sectionPart, final EffectiveItemSessionControl currentControl) {
            final EffectiveItemSessionControl controlForSectionPart = EffectiveItemSessionControl.override(currentControl, sectionPart.getItemSessionControl());
            resultBuilder.put(sectionPart, controlForSectionPart);

            if (sectionPart instanceof AssessmentSection) {
                final AssessmentSection assessmentSection = (AssessmentSection) sectionPart;
                for (final SectionPart childPart : assessmentSection.getSectionParts()) {
                    handleSectionPart(childPart, controlForSectionPart);
                }
            }
        }
    }
}
