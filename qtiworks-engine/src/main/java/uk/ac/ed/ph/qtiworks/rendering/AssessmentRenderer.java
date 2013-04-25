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
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.rendering;

import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksProperties;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEventNotification;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.test.NavigationMode;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.state.EffectiveItemSessionControl;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPartSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.state.marshalling.ItemSessionStateXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.state.marshalling.TestSessionStateXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.XsltResourceResolver;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.Charsets;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

/**
 * FIXME: Redocument this!
 *
 * TODO: Need to add support for coping with Content MathML, and possibly annotated MathML
 * containing a mixture of C & P. The idea would be that we use the PMathML, if available,
 * or convert the CMathML to PMathML once all substitutions have been made. Potential
 * complexity exists if we add support for substituting both CMathML and PMathML in a
 * MathML expression containing both PMathML and CMathML annotations. What guarantee is there
 * that we get the same result? I think the spec needs more thought wrt MathML.
 *
 * An instance of this class is safe to use concurrently by multiple threads.
 *
 * @author David McKain
 */
@Service
public class AssessmentRenderer {

    private static final URI terminatedXsltUri = URI.create("classpath:/rendering-xslt/terminated.xsl");
    private static final URI itemStandaloneXsltUri = URI.create("classpath:/rendering-xslt/item-standalone.xsl");
    private static final URI testItemXsltUri = URI.create("classpath:/rendering-xslt/test-item.xsl");
    private static final URI testEntryXsltUri = URI.create("classpath:/rendering-xslt/test-entry.xsl");
    private static final URI testPartNavigationXsltUri = URI.create("classpath:/rendering-xslt/test-testpart-navigation.xsl");
    private static final URI testPartFeedbackXsltUri = URI.create("classpath:/rendering-xslt/test-testpart-feedback.xsl");
    private static final URI testFeedbackXsltUri = URI.create("classpath:/rendering-xslt/test-feedback.xsl");

    @Resource
    private QtiWorksProperties qtiWorksProperties;

    @Resource
    private JqtiExtensionManager jqtiExtensionManager;

    @Resource
    private XsltStylesheetCache xsltStylesheetCache;

    @Resource
    private Validator jsr303Validator;

    @Resource
    private String webappContextPath;

    private XsltStylesheetManager stylesheetManager;

    //----------------------------------------------------

    public QtiWorksProperties getQtiWorksProperties() {
        return qtiWorksProperties;
    }

    public void setQtiWorksProperties(final QtiWorksProperties qtiWorksProperties) {
        this.qtiWorksProperties = qtiWorksProperties;
    }


    public JqtiExtensionManager getJqtiExtensionManager() {
        return jqtiExtensionManager;
    }

    public void setJqtiExtensionManager(final JqtiExtensionManager jqtiExtensionManager) {
        this.jqtiExtensionManager = jqtiExtensionManager;
    }


    public XsltStylesheetCache getXsltStylesheetCache() {
        return xsltStylesheetCache;
    }

    public void setXsltStylesheetCache(final XsltStylesheetCache xsltStylesheetCache) {
        this.xsltStylesheetCache = xsltStylesheetCache;
    }


    public Validator getJsr303Validator() {
        return jsr303Validator;
    }

    public void setJsr303Validator(final Validator jsr303Validator) {
        this.jsr303Validator = jsr303Validator;
    }


    public String getWebappContextPath() {
        return webappContextPath;
    }

    public void setWebappContextPath(final String webappContextPath) {
        this.webappContextPath = webappContextPath;
    }

    //----------------------------------------------------

    @PostConstruct
    public void init() {
        this.stylesheetManager = new XsltStylesheetManager(new ClassPathResourceLocator(), xsltStylesheetCache);
    }

    //----------------------------------------------------

    /**
     * Renders a terminated session, sending the results as UTF-8 encoded XML
     * to the given {@link OutputStream}.
     * <p>
     * The caller is responsible for closing this stream afterwards.
     */
    public void renderTeminated(final AbstractRenderingRequest<?> request, final OutputStream resultStream) {
        Assert.notNull(resultStream, "resultStream");

        final Map<String, Object> xsltParameters = new HashMap<String, Object>();
        setBaseRenderingParameters(xsltParameters);

        doTransform(request, terminatedXsltUri, xsltParameters, resultStream);
    }

    /**
     * Renders the given {@link ItemRenderingRequest}, sending the results as
     * UTF-8 encoded XML to the given {@link OutputStream}.
     * <p>
     * The rendering shows the current state of the item, unless {@link ItemRenderingRequest#isSolutionMode()}
     * returns true, in which case the model solution is rendered.
     * <p>
     * The caller is responsible for closing this stream afterwards.
     *
     * @throws QtiWorksRenderingException if an unexpected Exception happens during rendering
     */
    public void renderItem(final ItemRenderingRequest request,
            final List<CandidateEventNotification> notifications, final OutputStream resultStream) {
        Assert.notNull(request, "request");
        Assert.notNull(resultStream, "resultStream");

        /* Check request is valid */
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "itemRenderingRequest");
        jsr303Validator.validate(request, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Invalid " + request.getClass().getSimpleName()
                    + " Object: " + errors);
        }

        /* Pass request info to XSLT as parameters */
        final Map<String, Object> xsltParameters = new HashMap<String, Object>();
        setBaseRenderingParameters(xsltParameters, request, notifications);

        /* Pass ItemSessionState (as DOM Document) */
        final ItemSessionState itemSessionState = request.getItemSessionState();
        xsltParameters.put("itemSessionState", ItemSessionStateXmlMarshaller.marshal(itemSessionState).getDocumentElement());

        /* Set control parameters */
        xsltParameters.put("prompt", request.getPrompt());
        xsltParameters.put("solutionMode", Boolean.valueOf(request.isSolutionMode()));
        xsltParameters.put("endAllowed", Boolean.valueOf(request.isEndAllowed()));
        xsltParameters.put("softSoftResetAllowed", Boolean.valueOf(request.isSoftResetAllowed()));
        xsltParameters.put("hardResetAllowed", Boolean.valueOf(request.isHardResetAllowed()));
        xsltParameters.put("solutionAllowed", Boolean.valueOf(request.isSolutionAllowed()));
        xsltParameters.put("candidateCommentAllowed", Boolean.valueOf(request.isCandidateCommentAllowed()));

        /* Set action URLs */
        final ItemRenderingOptions renderingOptions = request.getRenderingOptions();
        xsltParameters.put("endUrl", renderingOptions.getEndUrl());
        xsltParameters.put("softResetUrl", renderingOptions.getSoftResetUrl());
        xsltParameters.put("hardResetUrl", renderingOptions.getHardResetUrl());
        xsltParameters.put("exitUrl", renderingOptions.getExitUrl());
        xsltParameters.put("solutionUrl", renderingOptions.getSolutionUrl());

        /* Perform transform */
        doTransform(request, itemStandaloneXsltUri, xsltParameters, resultStream);
    }

    /**
     * Renders the given {@link TestItemRenderingDetails}, sending the results as UTF-8 encoded XML
     * to the given {@link OutputStream}.
     * <p>
     * The caller is responsible for closing this stream afterwards.
     *
     * @throws QtiWorksRenderingException if an unexpected Exception happens during rendering
     */
    public void renderTest(final TestRenderingRequest request,
            final List<CandidateEventNotification> notifications, final OutputStream resultStream) {
        Assert.notNull(request, "renderingRequest");
        Assert.notNull(resultStream, "resultStream");

        /* Check request is valid */
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "testRenderingRequest");
        jsr303Validator.validate(request, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Invalid " + request.getClass().getSimpleName()
                    + " Object: " + errors);
        }

        /* Set up general XSLT parameters */
        final Map<String, Object> xsltParameters = new HashMap<String, Object>();
        setBaseRenderingParameters(xsltParameters, request, notifications);

        final TestSessionController testSessionController = request.getTestSessionController();
        final TestSessionState testSessionState = testSessionController.getTestSessionState();
        xsltParameters.put("testSessionState", TestSessionStateXmlMarshaller.marshal(testSessionState).getDocumentElement());
        xsltParameters.put("testSystemId", request.getAssessmentResourceUri().toString());

        /* Pass rendering options */
        final TestRenderingOptions renderingOptions = request.getRenderingOptions();
        xsltParameters.put("testPartNavigationUrl", renderingOptions.getTestPartNavigationUrl());
        xsltParameters.put("selectTestItemUrl", renderingOptions.getSelectTestItemUrl());
        xsltParameters.put("finishTestItemUrl", renderingOptions.getFinishTestItemUrl());
        xsltParameters.put("endTestPartUrl", renderingOptions.getEndTestPartUrl());
        xsltParameters.put("reviewTestPartUrl", renderingOptions.getReviewTestPartUrl());
        xsltParameters.put("reviewTestItemUrl", renderingOptions.getReviewTestItemUrl());
        xsltParameters.put("showTestItemSolutionUrl", renderingOptions.getShowTestItemSolutionUrl());
        xsltParameters.put("advanceTestPartUrl", renderingOptions.getAdvanceTestPartUrl());
        xsltParameters.put("exitTestUrl", renderingOptions.getExitTestUrl());

        final TestRenderingMode testRenderingMode = request.getTestRenderingMode();
        if (testRenderingMode==TestRenderingMode.ITEM_REVIEW) {
            doRenderTestItemReview(request, xsltParameters, resultStream);
        }
        else if (testRenderingMode==TestRenderingMode.ITEM_SOLUTION) {
            doRenderTestItemSolution(request, xsltParameters, resultStream);
        }
        else {
            /* Render current state */
            final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
            if (testSessionState.isEnded()) {
                /* At end of test, so show overall test feedback */
                doRenderTestFeedback(request, xsltParameters, resultStream);
            }
            else if (currentTestPartKey!=null) {
                final TestPartSessionState currentTestPartSessionState = testSessionState.getTestPartSessionStates().get(currentTestPartKey);
                final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
                if (currentItemKey!=null) {
                    /* An item is selected, so render it in appropriate state */
                    doRenderCurrentTestItem(request, xsltParameters, resultStream);
                }
                else {
                    /* No item selected */
                    if (currentTestPartSessionState.isEnded()) {
                        /* testPart has ended, so must be showing testPart feedback */
                        doRenderTestPartFeedback(request, xsltParameters, resultStream);
                    }
                    else {
                        /* testPart not ended, so we must be showing the navigation menu in nonlinear mode */
                        doRenderTestPartNavigation(request, xsltParameters, resultStream);
                    }
                }
            }
            else {
                /* No current testPart == start of multipart test */
                doRenderTestEntry(request, xsltParameters, resultStream);
            }
        }
    }

    private void doRenderTestEntry(final TestRenderingRequest request,
            final Map<String, Object> xsltParameters, final OutputStream resultStream) {
        doTransform(request, testEntryXsltUri, xsltParameters, resultStream);
    }

    private void doRenderTestPartNavigation(final TestRenderingRequest request,
            final Map<String, Object> xsltParameters, final OutputStream resultStream) {
        /* Determine whether candidate may exist testPart */
        final TestSessionController testSessionController = request.getTestSessionController();
        xsltParameters.put("endTestPartAllowed", Boolean.valueOf(testSessionController.mayEndCurrentTestPart()));

        doTransform(request, testPartNavigationXsltUri, xsltParameters, resultStream);
    }

    private void doRenderTestPartFeedback(final TestRenderingRequest request,
            final Map<String, Object> xsltParameters, final OutputStream resultStream) {
        doTransform(request, testPartFeedbackXsltUri, xsltParameters, resultStream);
    }

    private void doRenderTestFeedback(final TestRenderingRequest request,
            final Map<String, Object> xsltParameters, final OutputStream resultStream) {
        doTransform(request, testFeedbackXsltUri, xsltParameters, resultStream);
    }

    private void doRenderCurrentTestItem(final TestRenderingRequest request,
            final Map<String, Object> xsltParameters, final OutputStream resultStream) {
        /* Extract the item to be rendered */
        final TestSessionController testSessionController = request.getTestSessionController();
        final TestSessionState testSessionState = testSessionController.getTestSessionState();
        final TestPlanNodeKey itemKey = testSessionState.getCurrentItemKey();

        /* Set item parameters */
        final URI itemSystemId = setTestItemParameters(request, itemKey, xsltParameters);

        /* Set specific parameters for this rendering */
        final TestPart currentTestPart = testSessionController.getCurrentTestPart();
        final NavigationMode navigationMode = currentTestPart.getNavigationMode();
        xsltParameters.put("reviewMode", Boolean.FALSE);
        xsltParameters.put("solutionMode", Boolean.FALSE);
        xsltParameters.put("testPartNavigationAllowed", Boolean.valueOf(navigationMode==NavigationMode.NONLINEAR));
        xsltParameters.put("finishItemAllowed", Boolean.valueOf(navigationMode==NavigationMode.LINEAR && testSessionController.mayEndItemLinear()));
        xsltParameters.put("endTestPartAllowed", Boolean.FALSE);

        /* We finally do the transform on the _item_ (NB!) */
        doTransform(request, testItemXsltUri, itemSystemId, xsltParameters, resultStream);
    }

    private void doRenderTestItemReview(final TestRenderingRequest request,
            final Map<String, Object> xsltParameters, final OutputStream resultStream) {
        /* Extract item to review */
        final TestPlanNodeKey reviewItemKey = request.getModalItemKey();

        /* Set item parameters */
        final URI itemSystemId = setTestItemParameters(request, reviewItemKey, xsltParameters);

        /* Set specific parameters for this rendering */
        xsltParameters.put("reviewMode", Boolean.TRUE);
        xsltParameters.put("solutionMode", Boolean.FALSE);
        xsltParameters.put("testPartNavigationAllowed", Boolean.FALSE);
        xsltParameters.put("finishItemAllowed", Boolean.FALSE);
        xsltParameters.put("endTestPartAllowed", Boolean.FALSE);

        /* We finally do the transform on the _item_ (NB!) */
        doTransform(request, testItemXsltUri, itemSystemId, xsltParameters, resultStream);
    }

    private void doRenderTestItemSolution(final TestRenderingRequest request,
            final Map<String, Object> xsltParameters, final OutputStream resultStream) {
        /* Extract item to review */
        final TestPlanNodeKey solutionItemKey = request.getModalItemKey();

        /* Set item parameters */
        final URI itemSystemId = setTestItemParameters(request, solutionItemKey, xsltParameters);

        /* Set specific parameters for this rendering */
        xsltParameters.put("reviewMode", Boolean.TRUE);
        xsltParameters.put("solutionMode", Boolean.TRUE);
        xsltParameters.put("testPartNavigationAllowed", Boolean.FALSE);
        xsltParameters.put("finishItemAllowed", Boolean.FALSE);
        xsltParameters.put("endTestPartAllowed", Boolean.FALSE);

        /* We finally do the transform on the _item_ (NB!) */
        doTransform(request, testItemXsltUri, itemSystemId, xsltParameters, resultStream);
    }

    //----------------------------------------------------

    private URI setTestItemParameters(final TestRenderingRequest request, final TestPlanNodeKey itemKey,
            final Map<String, Object> xsltParameters) {
        final TestSessionController testSessionController = request.getTestSessionController();
        final TestSessionState testSessionState = testSessionController.getTestSessionState();
        final TestPlanNode itemRefNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(itemKey);
        if (itemRefNode==null) {
            throw new QtiWorksRenderingException("Failed to locate item with key " + itemKey + " in TestPlan");
        }
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(itemKey);
        if (itemSessionState==null) {
            throw new QtiWorksRenderingException("Failed to locate ItemSessionState for item with key " + itemKey);
        }

        /* Add item-specific parameters */
        xsltParameters.put("itemSessionState", ItemSessionStateXmlMarshaller.marshal(itemSessionState).getDocumentElement());
        xsltParameters.put("itemKey", itemKey.toString());

        /* Pass ItemSessionControl parameters */
        /* (Add any future additional itemSessionControl parameters here as required) */
        final EffectiveItemSessionControl effectiveItemSessionControl = itemRefNode.getEffectiveItemSessionControl();
        xsltParameters.put("allowComment", Boolean.valueOf(effectiveItemSessionControl.isAllowComment()));
        xsltParameters.put("showFeedback", Boolean.valueOf(effectiveItemSessionControl.isShowFeedback()));
        xsltParameters.put("showSolution", Boolean.valueOf(effectiveItemSessionControl.isShowSolution()));

        /* The caller should reset the following parameters to suit */
        xsltParameters.put("reviewMode", Boolean.FALSE);
        xsltParameters.put("solutionMode", Boolean.FALSE);
        xsltParameters.put("testPartNavigationAllowed", Boolean.FALSE);
        xsltParameters.put("finishItemAllowed", Boolean.FALSE);
        xsltParameters.put("endTestPartAllowed", Boolean.FALSE);

        return itemRefNode.getItemSystemId();
    }

    private <P extends AbstractRenderingOptions> void setBaseRenderingParameters(final Map<String, Object> xsltParameters,
            final AbstractRenderingRequest<P> request, final List<CandidateEventNotification> notifications) {
        setBaseRenderingParameters(xsltParameters);

        /* Pass notifications */
        if (notifications!=null) {
            xsltParameters.put("notifications", new XsltParamBuilder().notificationsToElements(notifications));
        }

        /* Pass common control parameters */
        xsltParameters.put("authorMode", request.isAuthorMode());
        xsltParameters.put("sourceAllowed", Boolean.valueOf(request.isSourceAllowed()));
        xsltParameters.put("resultAllowed", Boolean.valueOf(request.isResultAllowed()));

        /* Pass common action URLs */
        final P renderingOptions = request.getRenderingOptions();
        xsltParameters.put("serveFileUrl", renderingOptions.getServeFileUrl());
        xsltParameters.put("responseUrl", renderingOptions.getResponseUrl());
        xsltParameters.put("sourceUrl", renderingOptions.getSourceUrl());
        xsltParameters.put("resultUrl", renderingOptions.getResultUrl());
    }

    private void setBaseRenderingParameters(final Map<String, Object> xsltParameters) {
        xsltParameters.put("qtiWorksVersion", qtiWorksProperties.getQtiWorksVersion());
        xsltParameters.put("webappContextPath", webappContextPath);
    }

    //----------------------------------------------------

    private void doTransform(final AbstractRenderingRequest<?> renderingRequest, final URI stylesheetUri,
            final Map<String, Object> xsltParameters,
            final OutputStream resultStream) {
        doTransform(renderingRequest, stylesheetUri, renderingRequest.getAssessmentResourceUri(), xsltParameters, resultStream);
    }

    private void doTransform(final AbstractRenderingRequest<?> renderingRequest, final URI stylesheetUri,
            final URI inputUri, final Map<String, Object> xsltParameters, final OutputStream resultStream) {
        final Templates templates = stylesheetManager.getCompiledStylesheet(stylesheetUri);
        Transformer transformer;
        try {
            transformer = templates.newTransformer();
        }
        catch (final TransformerConfigurationException e) {
            throw new QtiWorksRenderingException("Could not complile stylesheet " + stylesheetUri, e);
        }
        if (xsltParameters!=null) {
            for (final Entry<String, Object> paramEntry : xsltParameters.entrySet()) {
                transformer.setParameter(paramEntry.getKey(), paramEntry.getValue());
            }
        }

        /* Set system ID of the input document */
        transformer.setParameter("systemId", inputUri);

        /* Configure requested serialization */
        final SerializationMethod serializationMethod = renderingRequest.getRenderingOptions().getSerializationMethod();
        transformer.setParameter("serializationMethod", serializationMethod.toString());
        transformer.setParameter("outputMethod", serializationMethod.getMethod());
        transformer.setParameter("contentType", serializationMethod.getContentType());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, serializationMethod.getContentType());
        transformer.setOutputProperty(OutputKeys.METHOD, serializationMethod.getMethod());
        transformer.setOutputProperty("include-content-type", "no");

        /* If we're building HTML5, add in its custom pseudo-DOCTYPE as we can't generate this in XSLT.
         * (NB: This only works sanely as we've hard-coded a reasonable encoding.)
         */
        if (serializationMethod==SerializationMethod.HTML5_MATHJAX) {
            try {
                resultStream.write("<!DOCTYPE html>\n".getBytes(Charsets.UTF_8));
            }
            catch (final IOException e) {
                throw new QtiWorksRenderingException("Could not write HTML5 prolog to resultStream", e);
            }
        }

        /* Set up Source */
        final ResourceLocator assessmentResourceLocator = renderingRequest.getAssessmentResourceLocator();
        final InputStream assessmentStream = assessmentResourceLocator.findResource(inputUri);
        final StreamSource assessmentSource = new StreamSource(assessmentStream, inputUri.toString());

        /* Set up Result */
        final StreamResult result = new StreamResult(resultStream);

        /* Perform transform */
        try {
            transformer.setURIResolver(new XsltResourceResolver(assessmentResourceLocator));
            transformer.transform(assessmentSource, result);
        }
        catch (final TransformerException e) {
            throw new QtiWorksRenderingException("Unexpected Exception doing XSLT transform", e);
        }
    }
}
