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
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
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
 * FIXME: Refactor this. It has become very messy since starting to implement the test spec!
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
    public void renderTeminated(final TerminatedRenderingRequest renderingRequest, final OutputStream resultStream) {
        Assert.notNull(renderingRequest, "renderingRequest");
        Assert.notNull(resultStream, "resultStream");

        /* Check request is valid */
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(renderingRequest, "terminatedRenderingRequest");
        jsr303Validator.validate(renderingRequest, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Invalid " + renderingRequest.getClass().getSimpleName()
                    + " Object: " + errors);
        }

        final Map<String, Object> xsltParameters = new HashMap<String, Object>();
        setBaseRenderingParameters(xsltParameters, renderingRequest);

        doTransform(renderingRequest, terminatedXsltUri, renderingRequest.getAssessmentResourceUri(),
                resultStream, xsltParameters);
    }

    /**
     * Renders the given {@link StandaloneItemRenderingRequest}, sending the results as UTF-8 encoded XML
     * to the given {@link OutputStream}.
     * <p>
     * The caller is responsible for closing this stream afterwards.
     *
     * @throws QtiRenderingException if an unexpected Exception happens during rendering
     */
    public void renderStandaloneItem(final StandaloneItemRenderingRequest renderingRequest,
            final List<CandidateEventNotification> notifications, final OutputStream resultStream) {
        Assert.notNull(renderingRequest, "renderingRequest");
        Assert.notNull(resultStream, "resultStream");

        /* Check request is valid */
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(renderingRequest, "standloneItemRenderingRequest");
        jsr303Validator.validate(renderingRequest, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Invalid " + renderingRequest.getClass().getSimpleName()
                    + " Object: " + errors);
        }

        /* Pass request info to XSLT as parameters */
        final XsltParamBuilder xsltParamBuilder = new XsltParamBuilder(jqtiExtensionManager);
        final Map<String, Object> xsltParameters = new HashMap<String, Object>();
        setBaseRenderingParameters(xsltParameters, renderingRequest);
        setItemRenderingParameters(xsltParameters, renderingRequest);
        setNotificationParameters(xsltParameters, xsltParamBuilder, notifications);

        doTransform(renderingRequest, itemStandaloneXsltUri, renderingRequest.getAssessmentItemUri(),
                resultStream, xsltParameters);
    }

    /**
     * Renders the given {@link TestItemREnderingRequest}, sending the results as UTF-8 encoded XML
     * to the given {@link OutputStream}.
     * <p>
     * The caller is responsible for closing this stream afterwards.
     *
     * @throws QtiRenderingException if an unexpected Exception happens during rendering
     */
    public void renderTestItem(final TestItemRenderingRequest renderingRequest,
            final List<CandidateEventNotification> notifications, final OutputStream resultStream) {
        Assert.notNull(renderingRequest, "renderingRequest");
        Assert.notNull(resultStream, "resultStream");

        /* Check request is valid */
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(renderingRequest, "testItemRenderingRequest");
        jsr303Validator.validate(renderingRequest, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Invalid " + renderingRequest.getClass().getSimpleName()
                    + " Object: " + errors);
        }

        /* Pass request info to XSLT as parameters */
        final XsltParamBuilder xsltParamBuilder = new XsltParamBuilder(jqtiExtensionManager);
        final Map<String, Object> xsltParameters = new HashMap<String, Object>();

        setBaseRenderingParameters(xsltParameters, renderingRequest);
        setItemRenderingParameters(xsltParameters, renderingRequest);
        setTestRenderingParameters(xsltParameters, renderingRequest);
        setNotificationParameters(xsltParameters, xsltParamBuilder, notifications);
        xsltParameters.put("itemKey", renderingRequest.getItemKey().toString());
        xsltParameters.put("showFeedback", Boolean.valueOf(renderingRequest.isShowFeedback()));

        /* Set navigation action permissions */
        xsltParameters.put("testPartNavigationAllowed", Boolean.valueOf(renderingRequest.isTestPartNavigationAllowed()));
        xsltParameters.put("finishItemAllowed", Boolean.valueOf(renderingRequest.isFinishItemAllowed()));
        xsltParameters.put("reviewTestPartAllowed", Boolean.valueOf(renderingRequest.isReviewTestPartAllowed()));
        xsltParameters.put("testItemSolutionAllowed", Boolean.valueOf(renderingRequest.isTestItemSolutionAllowed()));

        doTransform(renderingRequest, testItemXsltUri, renderingRequest.getAssessmentItemUri(),
                resultStream, xsltParameters);
    }

    private void setNotificationParameters(final Map<String, Object> xsltParameters,
            final XsltParamBuilder xsltParamBuilder, final List<CandidateEventNotification> notifications) {
        if (notifications!=null) {
            xsltParameters.put("notifications", xsltParamBuilder.notificationsToElements(notifications));
        }
    }

    private void setTestRenderingParameters(final Map<String, Object> xsltParameters,
            final TestRenderingRequest renderingRequest) {
        /* FIXME: Add remaining things here! */
        xsltParameters.put("testSystemId", renderingRequest.getAssessmentResourceUri().toString());

        /* Pass ItemSessionState (as DOM Document) */
        final TestSessionState testSessionState = renderingRequest.getTestSessionState();
        xsltParameters.put("testSessionState", TestSessionStateXmlMarshaller.marshal(testSessionState).getDocumentElement());

    }

    private void setItemRenderingParameters(final Map<String, Object> xsltParameters,
            final StandaloneItemRenderingRequest renderingRequest) {
        /* Set config parameters */
        xsltParameters.put("prompt", renderingRequest.getPrompt());

        /* Set control parameters */
        xsltParameters.put("renderingMode", renderingRequest.getRenderingMode().toString());
        xsltParameters.put("closeAllowed", Boolean.valueOf(renderingRequest.isCloseAllowed()));
        xsltParameters.put("resetAllowed", Boolean.valueOf(renderingRequest.isResetAllowed()));
        xsltParameters.put("reinitAllowed", Boolean.valueOf(renderingRequest.isReinitAllowed()));
        xsltParameters.put("solutionAllowed", Boolean.valueOf(renderingRequest.isSolutionAllowed()));
        xsltParameters.put("sourceAllowed", Boolean.valueOf(renderingRequest.isSourceAllowed()));
        xsltParameters.put("resultAllowed", Boolean.valueOf(renderingRequest.isResultAllowed()));

        /* Pass ItemSessionState (as DOM Document) */
        final ItemSessionState itemSessionState = renderingRequest.getItemSessionState();
        xsltParameters.put("itemSessionState", ItemSessionStateXmlMarshaller.marshal(itemSessionState).getDocumentElement());
    }

    private void setBaseRenderingParameters(final Map<String, Object> xsltParameters,
            final AbstractRenderingRequest renderingRequest) {
        final RenderingOptions renderingOptions = renderingRequest.getRenderingOptions();
        /* Set config & control parameters */
        xsltParameters.put("webappContextPath", renderingOptions.getContextPath());
        xsltParameters.put("qtiWorksVersion", qtiWorksProperties.getQtiWorksVersion());
        xsltParameters.put("authorMode", renderingRequest.isAuthorMode());
        xsltParameters.put("serializationMethod", renderingOptions.getSerializationMethod().toString());
        xsltParameters.put("attemptUrl", renderingOptions.getAttemptUrl());
        xsltParameters.put("closeUrl", renderingOptions.getCloseUrl());
        xsltParameters.put("resetUrl", renderingOptions.getResetUrl());
        xsltParameters.put("reinitUrl", renderingOptions.getReinitUrl());
        xsltParameters.put("terminateUrl", renderingOptions.getTerminateUrl());
        xsltParameters.put("solutionUrl", renderingOptions.getSolutionUrl());
        xsltParameters.put("sourceUrl", renderingOptions.getSourceUrl());
        xsltParameters.put("resultUrl", renderingOptions.getResultUrl());
        xsltParameters.put("serveFileUrl", renderingOptions.getServeFileUrl());
        xsltParameters.put("testPartNavigationUrl", renderingOptions.getTestPartNavigationUrl());
        xsltParameters.put("selectTestItemUrl", renderingOptions.getSelectTestItemUrl());
        xsltParameters.put("finishTestItemUrl", renderingOptions.getFinishTestItemUrl());
        xsltParameters.put("endTestPartUrl", renderingOptions.getEndTestPartUrl());
        xsltParameters.put("reviewTestPartUrl", renderingOptions.getReviewTestPartUrl());
        xsltParameters.put("reviewTestItemUrl", renderingOptions.getReviewTestItemUrl());
        xsltParameters.put("showTestItemSolutionUrl", renderingOptions.getShowTestItemSolutionUrl());
        xsltParameters.put("advanceTestPartUrl", renderingOptions.getAdvanceTestPartUrl());
        xsltParameters.put("exitTestUrl", renderingOptions.getExitTestUrl());
    }

    //----------------------------------------------------

    /**
     * FIXME: Document this!
     *
     * The caller is responsible for closing this stream afterwards.
     *
     * @param renderingRequest
     * @param resultStream
     *
     * @throws QtiRenderingException if an unexpected Exception happens during rendering
     */
    public void renderTestEntryPage(final TestEntryRenderingRequest renderingRequest,
            final List<CandidateEventNotification> notifications, final OutputStream resultStream) {
        Assert.notNull(renderingRequest, "renderingRequest");
        Assert.notNull(resultStream, "resultStream");

        /* Check request is valid */
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(renderingRequest, "testEntryRenderingRequest");
        jsr303Validator.validate(renderingRequest, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Invalid " + renderingRequest.getClass().getSimpleName()
                    + " Object: " + errors);
        }

        final RenderingOptions renderingOptions = renderingRequest.getRenderingOptions();

        /* Pass request info to XSLT as parameters */
        final XsltParamBuilder xsltParamBuilder = new XsltParamBuilder(jqtiExtensionManager);
        final Map<String, Object> xsltParameters = new HashMap<String, Object>();
        setNotificationParameters(xsltParameters, xsltParamBuilder, notifications);
        setBaseRenderingParameters(xsltParameters, renderingRequest);
        setTestRenderingParameters(xsltParameters, renderingRequest);

        xsltParameters.put("webappContextPath", renderingOptions.getContextPath());
        xsltParameters.put("authorMode", renderingRequest.isAuthorMode());
        xsltParameters.put("serializationMethod", renderingOptions.getSerializationMethod().toString());

        /* Pass TestSessionState as XML */
        final TestSessionState testSessionState = renderingRequest.getTestSessionState();
        xsltParameters.put("testSessionState", TestSessionStateXmlMarshaller.marshal(testSessionState).getDocumentElement());

        doTransform(renderingRequest, testEntryXsltUri, renderingRequest.getAssessmentResourceUri(),
                resultStream, xsltParameters);
    }

    /**
     * FIXME: Document this!
     *
     * The caller is responsible for closing this stream afterwards.
     *
     * @param renderingRequest
     * @param resultStream
     *
     * @throws QtiRenderingException if an unexpected Exception happens during rendering
     */
    public void renderTestPartNavigation(final TestPartNavigationRenderingRequest renderingRequest,
            final List<CandidateEventNotification> notifications, final OutputStream resultStream) {
        Assert.notNull(renderingRequest, "renderingRequest");
        Assert.notNull(resultStream, "resultStream");

        /* Check request is valid */
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(renderingRequest, "testPartNavigationRenderingRequest");
        jsr303Validator.validate(renderingRequest, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Invalid " + renderingRequest.getClass().getSimpleName()
                    + " Object: " + errors);
        }

        final RenderingOptions renderingOptions = renderingRequest.getRenderingOptions();

        /* Pass request info to XSLT as parameters */
        final XsltParamBuilder xsltParamBuilder = new XsltParamBuilder(jqtiExtensionManager);
        final Map<String, Object> xsltParameters = new HashMap<String, Object>();
        setNotificationParameters(xsltParameters, xsltParamBuilder, notifications);
        setBaseRenderingParameters(xsltParameters, renderingRequest);
        setTestRenderingParameters(xsltParameters, renderingRequest);

        xsltParameters.put("webappContextPath", renderingOptions.getContextPath());
        xsltParameters.put("authorMode", renderingRequest.isAuthorMode());
        xsltParameters.put("serializationMethod", renderingOptions.getSerializationMethod().toString());

        /* Pass TestSessionState as XML */
        final TestSessionState testSessionState = renderingRequest.getTestSessionState();
        xsltParameters.put("testSessionState", TestSessionStateXmlMarshaller.marshal(testSessionState).getDocumentElement());

        /* Set navigation action permissions */
        xsltParameters.put("endTestPartAllowed", Boolean.valueOf(renderingRequest.isEndTestPartAllowed()));

        doTransform(renderingRequest, testPartNavigationXsltUri, renderingRequest.getAssessmentResourceUri(),
                resultStream, xsltParameters);
    }

    /**
     * FIXME: Document this!
     *
     * The caller is responsible for closing this stream afterwards.
     *
     * @param renderingRequest
     * @param resultStream
     *
     * @throws QtiRenderingException if an unexpected Exception happens during rendering
     */
    public void renderTestPartFeedback(final TestPartFeedbackRenderingRequest renderingRequest,
            final List<CandidateEventNotification> notifications, final OutputStream resultStream) {
        Assert.notNull(renderingRequest, "renderingRequest");
        Assert.notNull(resultStream, "resultStream");

        /* Check request is valid */
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(renderingRequest, "testPartFeedbackRenderingRequest");
        jsr303Validator.validate(renderingRequest, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Invalid " + renderingRequest.getClass().getSimpleName()
                    + " Object: " + errors);
        }

        final RenderingOptions renderingOptions = renderingRequest.getRenderingOptions();

        /* Pass request info to XSLT as parameters */
        final XsltParamBuilder xsltParamBuilder = new XsltParamBuilder(jqtiExtensionManager);
        final Map<String, Object> xsltParameters = new HashMap<String, Object>();
        setNotificationParameters(xsltParameters, xsltParamBuilder, notifications);
        setBaseRenderingParameters(xsltParameters, renderingRequest);
        setTestRenderingParameters(xsltParameters, renderingRequest);

        xsltParameters.put("webappContextPath", renderingOptions.getContextPath());
        xsltParameters.put("authorMode", renderingRequest.isAuthorMode());
        xsltParameters.put("serializationMethod", renderingOptions.getSerializationMethod().toString());

        /* Pass TestSessionState as XML */
        final TestSessionState testSessionState = renderingRequest.getTestSessionState();
        xsltParameters.put("testSessionState", TestSessionStateXmlMarshaller.marshal(testSessionState).getDocumentElement());

        doTransform(renderingRequest, testPartFeedbackXsltUri, renderingRequest.getAssessmentResourceUri(),
                resultStream, xsltParameters);
    }
    /**
     * FIXME: Document this!
     *
     * The caller is responsible for closing this stream afterwards.
     *
     * @param renderingRequest
     * @param resultStream
     *
     * @throws QtiRenderingException if an unexpected Exception happens during rendering
     */
    public void renderTestFeedback(final TestFeedbackRenderingRequest renderingRequest,
            final List<CandidateEventNotification> notifications, final OutputStream resultStream) {
        Assert.notNull(renderingRequest, "renderingRequest");
        Assert.notNull(resultStream, "resultStream");

        /* Check request is valid */
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(renderingRequest, "testFeedbackRenderingRequest");
        jsr303Validator.validate(renderingRequest, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Invalid " + renderingRequest.getClass().getSimpleName()
                    + " Object: " + errors);
        }

        final RenderingOptions renderingOptions = renderingRequest.getRenderingOptions();

        /* Pass request info to XSLT as parameters */
        final XsltParamBuilder xsltParamBuilder = new XsltParamBuilder(jqtiExtensionManager);
        final Map<String, Object> xsltParameters = new HashMap<String, Object>();
        setNotificationParameters(xsltParameters, xsltParamBuilder, notifications);
        setBaseRenderingParameters(xsltParameters, renderingRequest);
        setTestRenderingParameters(xsltParameters, renderingRequest);

        xsltParameters.put("webappContextPath", renderingOptions.getContextPath());
        xsltParameters.put("authorMode", renderingRequest.isAuthorMode());
        xsltParameters.put("serializationMethod", renderingOptions.getSerializationMethod().toString());

        /* Pass TestSessionState as XML */
        final TestSessionState testSessionState = renderingRequest.getTestSessionState();
        xsltParameters.put("testSessionState", TestSessionStateXmlMarshaller.marshal(testSessionState).getDocumentElement());

        doTransform(renderingRequest, testFeedbackXsltUri, renderingRequest.getAssessmentResourceUri(),
                resultStream, xsltParameters);
    }

    //----------------------------------------------------

    private void doTransform(final AbstractRenderingRequest renderingRequest, final URI stylesheetUri,
            final URI inputUri,
            final OutputStream resultStream, final Map<String, Object> xsltParameters) {
        final Templates templates = stylesheetManager.getCompiledStylesheet(stylesheetUri);
        Transformer transformer;
        try {
            transformer = templates.newTransformer();
        }
        catch (final TransformerConfigurationException e) {
            throw new QtiRenderingException("Could not complile stylesheet " + stylesheetUri, e);
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
                throw new QtiRenderingException("Could not write HTML5 prolog to resultStream", e);
            }
        }

        /* Set up Source */
        final ResourceLocator assessmentResourceLocator = renderingRequest.getAssessmentResourceLocator();
        final InputStream assessmentStream = assessmentResourceLocator.findResource(inputUri);
        final StreamSource assessmentSource = new StreamSource(assessmentStream, inputUri.toString());

        /* Set up Result */
        final StreamResult result = new StreamResult(resultStream);

        /* Do transform */
        try {
            transformer.setURIResolver(new XsltResourceResolver(assessmentResourceLocator));
            transformer.transform(assessmentSource, result);
        }
        catch (final TransformerException e) {
            throw new QtiRenderingException("Unexpected Exception doing XSLT transform", e);
        }
    }
}
