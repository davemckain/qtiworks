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

import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksProperties;
import uk.ac.ed.ph.qtiworks.examples.ChoiceRunningExample;
import uk.ac.ed.ph.qtiworks.rendering.AbstractRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.AuthorViewRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.ItemRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.rendering.TestRenderingOptions;

import uk.ac.ed.ph.jqtiplus.SimpleJqtiFacade;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.running.TestPlanner;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.running.TestSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.SimpleXsltStylesheetCache;

import java.net.URI;

import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Some helpers for the rendering examples
 *
 * @author David McKain
 */
public final class RenderingExampleHelpers {

    /**
     * Loads in the example assessmentItem having the given URI using a
     * {@link ClassPathResourceLocator}, then constructs a fresh {@link ItemSessionState}
     * and an {@link ItemSessionController}.
     *
     * @see ChoiceRunningExample
     *
     * @param itemUri
     */
    public static ItemSessionController createItemSessionController(final ResourceLocator assessmentResourceLocator, final URI itemUri) {
        System.out.println("Running " + itemUri + " from the ClassPath");

        final SimpleJqtiFacade simpleJqtiFacade = new SimpleJqtiFacade();
        final ResolvedAssessmentItem resolvedAssessmentItem = simpleJqtiFacade.loadAndResolveAssessmentItem(assessmentResourceLocator, itemUri);
        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(resolvedAssessmentItem, true).initialize();
        final ItemSessionState itemSessionState = new ItemSessionState();
        final ItemSessionController itemSessionController = simpleJqtiFacade.createItemSessionController(itemSessionControllerSettings, itemProcessingMap, itemSessionState);
        return itemSessionController;
    }

    public static TestSessionController createTestSessionController(final ResourceLocator assessmentResourceLocator, final URI testUri) {
        System.out.println("Running " + testUri + " from the ClassPath");

        final SimpleJqtiFacade simpleJqtiFacade = new SimpleJqtiFacade();
        final ResolvedAssessmentTest resolvedAssessmentTest = simpleJqtiFacade.loadAndResolveAssessmentTest(assessmentResourceLocator, testUri);
        final TestProcessingMap testProcessingMap = new TestProcessingInitializer(resolvedAssessmentTest, true).initialize();

        final TestPlanner testPlanner = simpleJqtiFacade.createTestPlanner(testProcessingMap);
        final TestPlan testPlan = testPlanner.generateTestPlan();

        final TestSessionState testSessionState = new TestSessionState(testPlan);
        final TestSessionControllerSettings testSessionControllerSettings = new TestSessionControllerSettings();
        final TestSessionController testSessionController = simpleJqtiFacade.createTestSessionController(testSessionControllerSettings, testProcessingMap, testSessionState);
        return testSessionController;
    }

    /**
     * Creates and initialises an {@link AssessmentRenderer}, with reasonable default values
     * for these examples.
     */
    public static AssessmentRenderer createAssessmentRenderer() {
        final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        /* (The version number is appended to JS/CSS resource links as query strings to ensure
         * that new versions get picked when new releases are deployed. This doesn't really
         * matter here, so we'll use a fake version number)
         */
        final QtiWorksProperties qtiWorksProperties = new QtiWorksProperties();
        qtiWorksProperties.setQtiWorksVersion("VERSION");

        final AssessmentRenderer renderer = new AssessmentRenderer();
        renderer.setQtiWorksProperties(qtiWorksProperties);
        renderer.setJsr303Validator(validator);
        renderer.setXsltStylesheetCache(new SimpleXsltStylesheetCache());
        renderer.setWebappContextPath("/qtiworks"); /* (Used for certain links) */
        renderer.init();
        return renderer;
    }

    public static TestRenderingOptions createTestRenderingOptions() {
        final TestRenderingOptions result = new TestRenderingOptions();
        setBaseOptions(result);

        /* (These URLs are just for example - they don't point to anything concrete) */
        result.setTestPartNavigationUrl("/test-part-navigation");
        result.setSelectTestItemUrl("/select-item");
        result.setAdvanceTestItemUrl("/finish-item");
        result.setEndTestPartUrl("/end-test-part");
        result.setReviewTestPartUrl("/review-test-part");
        result.setReviewTestItemUrl("/review-item");
        result.setShowTestItemSolutionUrl("/item-solution");
        result.setAdvanceTestPartUrl("/advance-test-part");
        result.setExitTestUrl("/exit-test");
        return result;
    }

    public static ItemRenderingOptions createItemRenderingOptions() {
        final ItemRenderingOptions result = new ItemRenderingOptions();
        setBaseOptions(result);

        /* (These URLs are just for example - they don't point to anything concrete) */
        result.setEndUrl("/close");
        result.setSoftResetUrl("/reset-soft");
        result.setHardResetUrl("/reset-hard");
        result.setSolutionUrl("/solution");
        result.setExitUrl("/terminate");
        return result;
    }

    public static AuthorViewRenderingOptions createAuthorViewRenderingOptions() {
        final AuthorViewRenderingOptions result = new AuthorViewRenderingOptions();
        setBaseOptions(result);
        return result;
    }

    private static void setBaseOptions(final AbstractRenderingOptions result) {
        result.setSerializationMethod(SerializationMethod.HTML5_MATHJAX);

        /* (These URLs are just for example - they don't point to anything concrete) */
        result.setServeFileUrl("/file");
        result.setResponseUrl("/response");
        result.setAuthorViewUrl("/author-view");
        result.setSourceUrl("/source");
        result.setStateUrl("/state");
        result.setResultUrl("/result");
        result.setValidationUrl("/validation");
    }

}
