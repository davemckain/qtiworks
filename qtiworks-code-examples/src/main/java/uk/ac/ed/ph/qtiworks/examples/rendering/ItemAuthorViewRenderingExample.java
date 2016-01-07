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
import uk.ac.ed.ph.qtiworks.rendering.ItemAuthorViewRenderingRequest;

import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;
import java.util.Date;

import javax.xml.transform.stream.StreamResult;

import com.google.common.io.CharStreams;

/**
 * Example rendering the author view of an item immediately after entry.
 *
 * <h3>How to run</h3>
 *
 * You can run this via Maven as follows:
 * <pre>
 * mvn exec:java -Dexec.mainClass=uk.ac.ed.ph.qtiworks.examples.rendering.ItemAuthorViewRenderingExample
 * </pre>
 *
 * @author David McKain
 */
public final class ItemAuthorViewRenderingExample {

    public static void main(final String[] args) {
        /* We'll be loading the following sample item from the classpath */
        final ResourceLocator assessmentResourceLocator = new ClassPathResourceLocator();
        final URI itemUri = URI.create("classpath:/uk/ac/ed/ph/qtiworks/samples/ims/choice.xml");

        /* Read and set up state & controller */
        final ItemSessionController itemSessionController = RenderingExampleHelpers.createItemSessionController(assessmentResourceLocator, itemUri);
        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();

        /* Enter item */
        System.out.println("\nInitialising");
        final Date timestamp = new Date();
        itemSessionController.initialize(timestamp);
        itemSessionController.performTemplateProcessing(timestamp);
        itemSessionController.enterItem(timestamp);

        /* Create rendering request */
        final AuthorViewRenderingOptions renderingOptions = RenderingExampleHelpers.createAuthorViewRenderingOptions();
        final ItemAuthorViewRenderingRequest renderingRequest = new ItemAuthorViewRenderingRequest();
        renderingRequest.setAssessmentResourceLocator(assessmentResourceLocator);
        renderingRequest.setAssessmentResourceUri(itemUri);
        renderingRequest.setRenderingOptions(renderingOptions);
        renderingRequest.setItemSessionState(itemSessionState);
        renderingRequest.setValidated(true);
        renderingRequest.setValid(true);
        renderingRequest.setAuthorMode(true);

        /* Set up result */
        final StringBuilder stringBuilder = new StringBuilder();
        final StreamResult result = new StreamResult(CharStreams.asWriter(stringBuilder));

        System.out.println("\nRendering");
        final AssessmentRenderer renderer = RenderingExampleHelpers.createAssessmentRenderer();
        renderer.renderItemAuthorView(renderingRequest, null /* (=Ignore notifications) */, result);
        final String rendered = stringBuilder.toString();
        System.out.println("Rendered HTML: " + rendered);
    }
}
