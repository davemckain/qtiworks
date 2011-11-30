/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
 */

package org.qtitools.qti.controller;

import uk.ac.ed.ph.jqtiplus.exception.QTIException;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.ControlObject;
import uk.ac.ed.ph.jqtiplus.node.test.SubmissionMode;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.qtitools.qti.node.test.flow.ItemFlow;

import uk.ac.ed.ph.snuggletex.SerializationMethod;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCoordinator {

    private static Logger logger = LoggerFactory.getLogger(TestCoordinator.class);

    private final TestCoordinatorState testCoordinatorState;

    private final String testHref;

    private final String view;

    private final Renderer renderer;

    private final String resourceBasePath;

    private final SerializationMethod serializationMethod;
    
    private final Map<String, Object> testParameters;

    private final Map<String, Object> renderingParameters;

    /*
     * rendering debug mode
     */
    private boolean debug = false;

    /*
     * rendering validation mode (if set to true, validation errors/warnings
     * will be shown)
     */
    private boolean validate = false;

    /*
     * Maximum amount of time allowed for network delay
     */
    private static long magic_time = 15000;

    public TestCoordinator(TestCoordinatorState testCoordinatorState, String testHref, String view, Renderer renderer,
            String resourceBasePath, Map<String, Object> testParameters, Map<String, Object> renderingParameters,
            SerializationMethod serializationMethod) {
        this.testCoordinatorState = testCoordinatorState;
        this.testHref = testHref;
        this.view = view;
        this.renderer = renderer;
        this.resourceBasePath = resourceBasePath;
        this.testParameters = testParameters;
        this.renderingParameters = renderingParameters;
        this.serializationMethod = serializationMethod;
    }

    /**
     * Enable or disable debugging
     * 
     * @param doDebug
     */
    public void setDebug(boolean doDebug) {
        if (debug != doDebug) {
            debug = doDebug;
            testCoordinatorState.setCachedRenderedContent(null);
        }
    }

    /**
     * Enable or disable validation
     * 
     * @param doValidate
     *            true if validation is wanted, false otherwise
     */
    public void setValidate(boolean doValidate) {
        if (validate != doValidate) {
            validate = doValidate;
            testCoordinatorState.setCachedRenderedContent(null);
        }
    }

    /**
     * Determine if the test is completed
     * 
     * @return true if complete; false otherwise
     */
    public boolean isCompleted() {
        return getTestController().isTestComplete();
    }

    /**
     * Get the underlying controller object
     * 
     * @return the test controller
     */
    public AssessmentTestController getTestController() {
        return testCoordinatorState.getTestController();
    }

    /**
     * Get the underlying AssessmentTest object
     * 
     * @return the assessmentTest
     */
    public AssessmentTest getTest() {
        return getTestController().getTest();
    }

    /**
     * Get the underlying ItemFlow object
     * 
     * @return the itemFlow associated with the AssessmentTestController
     */
    public ItemFlow getFlow() {
        return getTestController().getItemFlow();
    }

    public String getRenderableContent() throws QTIException {
        if (testCoordinatorState.getCachedRenderedContent() == null) {
            getCurrentQuestion();
        }
        return testCoordinatorState.getCachedRenderedContent();
    }

    public String flashMessage(String message) throws QTIException {
        testCoordinatorState.setFlash(message);
        testCoordinatorState.setCachedRenderedContent(null);
        String content = getRenderableContent();
        testCoordinatorState.setFlash(null);
        testCoordinatorState.setCachedRenderedContent(null);
        return content;
    }

    public void getNextQuestion(boolean includeFinished) throws QTIException {
        getTestController().getNextItemHREF(includeFinished);

        // invalidate renderedContent
        testCoordinatorState.setCachedRenderedContent(null);
    }

    public void getPreviousQuestion(boolean includeFinished) throws QTIException {
        getTestController().getPrevItemHREF(includeFinished);

        // invalidate renderedContent
        testCoordinatorState.setCachedRenderedContent(null);
    }

    public void getCurrentQuestion() throws QTIException {
        AssessmentTestController testController = testCoordinatorState.getTestController();

        logger.info("getCurrentQuestion() - " + testController.getCurrentItem());

        // if the renderedContent is still active, just return
        if (testCoordinatorState.getCachedRenderedContent() != null)
            return;

        if (testController.isTestComplete()) {
            // ItemData id = new ItemData();
            // id.setImageBasePath(getImageBasePath());
            // id.setTestTitle(test.getTestTitle());
            // if (test.getAssessmentFeedback() != null)
            // id.getAssessmentFeedback().addAll(test.getAssessmentFeedback());
            // if (test.getTestPartFeedback() != null)
            // id.getTestPartFeedback().addAll(test.getTestPartFeedback());
            // cachedRenderedContent = RenderingEngine.renderItem(renderer,
            // view, id);

            Map<String, Object> params = new HashMap<String, Object>();

            // set the test title from the cached version (to avoid lookups)
            params.put("title", testController.getTestTitle());

            XSLTParamBuilder xsltParameterBuilder = new XSLTParamBuilder();
            params.put("assessmentFeedback", xsltParameterBuilder.convertFeedback(testController.getAssessmentFeedback()));
            params.put("testPartFeedback", xsltParameterBuilder.convertFeedback(testController.getTestPartFeedback()));
            // params.put("testOutcomeValues",
            // xsltParameterBuilder.convertOutcomes(testController.getTest().getOutcomeValues()));
            // params.put("testOutcomeDeclarations",
            // xsltParameterBuilder.convertOutcomeDeclarations(testController.getTest().getOutcomeDeclarations()));

            if (view != null)
                params.put("view", view);

            AssessmentItem blank = new AssessmentItem();
            renderContent(blank, "", params, null);
        }
        else {
            // DM: Hmmmm... this was all commented out when I inherited it,
            // which leads me to think
            // that it never really worked? The new version of renderContent()
            // should fix these issues
            // ItemSessionControl itemSessionControl =
            // test.getCurrentItemSessionControl();

            // String identifier = test.getCurrentItemIdentifier();
            // byte [] render = null;
            // String href = test.getCurrentItemHREF();

            // this deals with content packages that have mutliple levels, and
            // item images are relative to the item, not the test
            // String base = "";
            // if (href.lastIndexOf("/") != -1) {
            // base = href.substring(0, href.lastIndexOf("/"));
            // }
            // ai = new R2Q2AssessmentItem(new URI(href), testdir,
            // itemSessionControl.getShowFeedback(), getImageBasePath()+base,
            // test.getCurrentItemRef().getTemplateDefaults());
            // render = ai.render();

            // assemble and render (call service)
            renderContent(testController.getCurrentItem(), testController.getCurrentItemHREF(), makeItemParameters(),
                    testController.getCurrentItem().getResponseValues());
        }
    }

    private Map<String, Object> makeItemParameters() {
        AssessmentTestController testController = testCoordinatorState.getTestController();
        Map<String, Object> params = new HashMap<String, Object>();
        
        // set the question id
        params.put("questionId", getCurrentQuestionId());

        // set the test title from the cached version (to avoid lookups)
        params.put("title", testController.getTestTitle());

        // set the section titles
        params.put("sectionTitles", testController.getCurrentSectionTitles());

        // set the rubric
        XSLTParamBuilder xsltParameterBuilder = new XSLTParamBuilder();
        params.put("rubric", xsltParameterBuilder.convertRubric(testController.getRubricBlocks()));
        params.put("assessmentFeedback", xsltParameterBuilder.convertFeedback(testController.getAssessmentFeedback()));
        params.put("testPartFeedback", xsltParameterBuilder.convertFeedback(testController.getTestPartFeedback()));

        // set the state for rendering controls
        params.put("previousEnabled", testController.previousEnabled());
        params.put("backwardEnabled", testController.backwardEnabled());
        params.put("nextEnabled", testController.nextEnabled());
        params.put("forwardEnabled", testController.forwardEnabled());
        params.put("submitEnabled", testController.submitEnabled());
        params.put("skipEnabled", testController.skipEnabled());

        params.put("numberSelected", testController.getNumberSelected());
        params.put("numberRemaining", testController.getNumberRemaining());
        params.put("timeSelected", testController.getTimeSelected());
        params.put("timeRemaining", testController.getTimeRemaining());

        if (debug)
            params.put("debug", true);
        if (testCoordinatorState.getFlash() != null)
            params.put("flash", testCoordinatorState.getFlash());
        if (view != null)
            params.put("view", view);

        if (testController.getCurrentItemRef().getItemSessionControl().getAllowComment()) {
            params.put("allowCandidateComment", true);
        }
        if (validate) {
            if (getTest().validate().getAllItems().size() != 0) {
                params.put("validation", getTest().validate().toString());
            }
        }
        return params;
    }

    public String getCurrentQuestionId() {
        AssessmentTestController testController = testCoordinatorState.getTestController();
        if (testController.getCurrentItemRef() == null || isCompleted())
            return null;
        return testController.getCurrentItemRef().getIdentifier();
    }

    public void skipCurrentQuestion() throws QTIException {
        getTestController().skipCurrentItem();
        getNextQuestion(false);
    }

    public void setCurrentResponse(Map<String, List<String>> responses) throws FileNotFoundException, URISyntaxException,
            QTIException {
        AssessmentTestController testController = testCoordinatorState.getTestController();
        Map<AssessmentItemRef, Map<String, Value>> testPartItems = testCoordinatorState.getTestPartItems();

        testController.setCurrentItemResponses(responses);

        if (responses.containsKey("candidateComment"))
            testController.getCurrentItemRef().setCandidateComment(responses.get("candidateComment").get(0));

        Map<String, Value> itemOutcomes = testController.getCurrentItemRef().getItem().getOutcomeValues();

        boolean se = !testController.getCurrentItemRef().isFinished();
        ControlObject co = TimeReport.getLowestRemainingTimeControlObject(testController.getCurrentItemRef());
        if (co != null) {
            if (TimeReport.getRemainingTime(co) < (-1 * magic_time)) {
                se = false;
            }
        }

        if (testController.getCurrentTestPart().getSubmissionMode() == SubmissionMode.INDIVIDUAL) {
            if (se) {
                testController.setCurrentItemOutcomes(itemOutcomes);
            }
            else {
                if (!testController.getCurrentItemRef().isTimedOut())
                    testController.getCurrentItemRef().timeOut();
            }
        }
        else {
            if (se) {
                testPartItems.put(testController.getCurrentItemRef(), itemOutcomes);
            }
            else {
                if (!testController.getCurrentItemRef().isTimedOut())
                    testController.getCurrentItemRef().timeOut();
            }

            if (!testController.getItemFlow().hasNextItemRef(true)) {
                // write all vars

                for (AssessmentItemRef key : testPartItems.keySet()) {
                    key.setOutcomes(testPartItems.get(key));
                }
                testController.getTest().processOutcome();
                testPartItems.clear();
            }
        }

        // set the render
        if ((testController.getAssessmentFeedback() == null || testController.getAssessmentFeedback().size() == 0)
                && (testController.getTestPartFeedback() == null || testController.getTestPartFeedback().size() == 0)
                && !testController.getCurrentItem().getItemBody().willShowFeedback()
                && !testController.getCurrentItem().getAdaptive() && testController.getCurrentItemRef().isFinished()) {
            // there is no feedback (at the test level), so we'll show the next
            // item instead
            getNextQuestion(false);

            // TODO: this is broken if there is item feedback, as you won't see
            // it!!! Also it will break adaptive items!!!
            // test.getCurrentItem().getItemBody().willShowFeedback()
            // test.getCurrentItem().getAdaptive()
        }
        else {
            renderContent(testController.getCurrentItem(), testController.getCurrentItemHREF(), makeItemParameters(),
                    testController.getCurrentItemResponses());
        }
    }

    /**
     * Get the test report xml string
     * 
     * @return report xml string
     */
    public String getReport() {
        return getTestController().getReport();
    }

    private void renderContent(AssessmentItem assessmentItem, String itemToTestHref, Map<String, Object> itemParameters,
            Map<String, Value> responses) {
        AssessmentTestController testController = testCoordinatorState.getTestController();

        /*
         * Resolve itemHref so that it's relative to the "package" rather than
         * test
         */
        String itemHref;
        try {
            itemHref = new URI(testHref).resolve(itemToTestHref).toString();
        }
        catch (URISyntaxException e) {
            throw new QTIRenderingException("Could not resolve item href " + itemToTestHref + " against test href " + testHref, e);
        }

        boolean isResponded;
        if (testController.getCurrentItemRef() == null) {
            isResponded = false;
        }
        else {
            isResponded = testController.getCurrentItemRef().isResponded();
        }
        testCoordinatorState.setCachedRenderedContent(renderer.renderTestItem(testController.getTest(), assessmentItem,
                resourceBasePath, itemHref, isResponded, responses, testParameters, itemParameters, renderingParameters,
                serializationMethod));
    }
}
