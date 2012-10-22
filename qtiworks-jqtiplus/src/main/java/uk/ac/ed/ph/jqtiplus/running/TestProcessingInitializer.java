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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.running;

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationResult;

import java.net.URI;
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

    private final TestValidationResult testValidationResult;
    private final ResolvedAssessmentTest resolvedAssessmentTest;
    private final LinkedHashMap<Identifier, OutcomeDeclaration> outcomeDeclarationMapBuilder;

    public TestProcessingInitializer(final TestValidationResult testValidationResult) {
        this.testValidationResult = testValidationResult;
        this.resolvedAssessmentTest = testValidationResult.getResolvedAssessmentTest();
        this.outcomeDeclarationMapBuilder = new LinkedHashMap<Identifier, OutcomeDeclaration>();
    }

    public TestProcessingMap initialize() {
        if (!resolvedAssessmentTest.getTestLookup().wasSuccessful()) {
            throw new IllegalStateException("Test lookup did not succeed, so test cannot be run");
        }
        final AssessmentTest test = resolvedAssessmentTest.getTestLookup().extractAssumingSuccessful();

        /* Extract test's duration variable declaration */
        final ResponseDeclaration durationResponseDeclaration = test.getDurationResponseDeclaration();

        /* Record all valid outcomeVariable declarations */
        for (final OutcomeDeclaration declaration : test.getOutcomeDeclarations()) {
            doOutcomeVariable(declaration);
        }

        /* Now repeat this for each resolved item that was successfully looked up */
        final Map<URI, ItemProcessingMap> itemProcessingMapBuilder = new LinkedHashMap<URI, ItemProcessingMap>();
        for (final ItemValidationResult itemValidationResult : testValidationResult.getItemValidationResults()) {
            final RootNodeLookup<AssessmentItem> itemLookup = itemValidationResult.getResolvedAssessmentItem().getItemLookup();
            if (itemLookup.wasSuccessful()) {
                final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(itemValidationResult).initialize();
                itemProcessingMapBuilder.put(itemLookup.getSystemId(), itemProcessingMap);
            }
        }

        /* That's it! */
        return new TestProcessingMap(resolvedAssessmentTest, testValidationResult.isValid(),
                outcomeDeclarationMapBuilder, durationResponseDeclaration, itemProcessingMapBuilder);
    }

    private void doOutcomeVariable(final OutcomeDeclaration declaration) {
        final List<OutcomeDeclaration> declarations = resolvedAssessmentTest.resolveTestVariable(declaration.getIdentifier());
        if (declarations.size()==1) {
            outcomeDeclarationMapBuilder.put(declaration.getIdentifier(), declaration);
        }
    }

}
