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
package uk.ac.ed.ph.qtiworks.examples.rendering;

import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.AuthorViewRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.TestAuthorViewRenderingRequest;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;

import java.net.URI;
import java.util.Date;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.output.StringBuilderWriter;

/**
 * Example showing the author view of a test
 *
 * @author David McKain
 */
public final class TestAuthorViewRenderingExample {

    public static void main(final String[] args) {
        /* We'll be loading the following sample test from the classpath */
        final ClassPathResourceLocator assessmentResourceLocator = new ClassPathResourceLocator();
        final URI testUri = URI.create("classpath:/uk/ac/ed/ph/qtiworks/samples/testimplementation/dave/test-testFeedback.xml");

        /* Read and set up state & controller */
        final TestSessionController testSessionController = RenderingExampleHelpers.createTestSessionController(assessmentResourceLocator, testUri);
        final TestSessionState testSessionState = testSessionController.getTestSessionState();

        /* Enter test */
        System.out.println("\nInitialising");
        final Date timestamp1 = new Date();
        testSessionController.initialize(timestamp1);
        testSessionController.enterTest(timestamp1);

        /* Enter testPart */
        final Date timestamp2 = ObjectUtilities.addToTime(timestamp1, 1000L);
        testSessionController.enterNextAvailableTestPart(timestamp2);

        /* Select first item */
        final Date timestamp3 = ObjectUtilities.addToTime(timestamp1, 5000L);
        final TestPlanNode firstItemRef = testSessionState.getTestPlan().searchNodes(TestNodeType.ASSESSMENT_ITEM_REF).get(0);
        testSessionController.selectItemNonlinear(timestamp3, firstItemRef.getKey());

        /* Create rendering request */
        final AuthorViewRenderingOptions renderingOptions = RenderingExampleHelpers.createAuthorViewRenderingOptions();
        final TestAuthorViewRenderingRequest renderingRequest = new TestAuthorViewRenderingRequest();
        renderingRequest.setTestSessionController(testSessionController);
        renderingRequest.setAssessmentResourceLocator(assessmentResourceLocator);
        renderingRequest.setAssessmentResourceUri(testUri);
        renderingRequest.setTestSessionController(testSessionController);
        renderingRequest.setRenderingOptions(renderingOptions);
        renderingRequest.setAuthorMode(true);
        renderingRequest.setValidated(true);
        renderingRequest.setValid(true);

        /* Set up result */
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        final StreamResult result = new StreamResult(stringBuilderWriter);

        System.out.println("\nRendering");
        final AssessmentRenderer renderer = RenderingExampleHelpers.createAssessmentRenderer();
        renderer.renderTestAuthorView(renderingRequest, null/* (=Ignore notifications) */, result);
        final String rendered = stringBuilderWriter.toString();
        System.out.println("Rendered HTML: " + rendered);
    }
}
