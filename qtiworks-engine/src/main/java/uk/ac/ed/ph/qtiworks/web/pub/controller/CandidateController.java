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
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.web.pub.controller;

import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.samples.MathAssessSampleSet;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleCollection;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleResource;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleResource.Feature;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleSet;
import uk.ac.ed.ph.qtiworks.samples.StandardQtiSampleSet;
import uk.ac.ed.ph.qtiworks.services.CandidateUploadService;
import uk.ac.ed.ph.qtiworks.utils.IoUtilities;
import uk.ac.ed.ph.qtiworks.web.exception.QtiSampleNotFoundException;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.FileResponseData;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * First stab at controller for doing candidate (item) sessions
 *
 * FIXME: I've killed off most of the old rendering parameters. We'll need to create a better
 * set once we work out exactly what variants need to be rendered.
 *
 * @author David McKain
 */
@Controller
public class CandidateController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    public static final QtiSampleCollection demoSampleCollection = new QtiSampleCollection(
            StandardQtiSampleSet.instance().withoutFeature(Feature.NOT_FULLY_VALID),
            MathAssessSampleSet.instance().withoutFeature(Feature.NOT_FULLY_VALID)
    );

    private static final String CURRENT_ITEM_HANDLE = "currentItemHandle";

    @Resource
    private JqtiExtensionManager jqtiExtensionManager;

    @Resource
    private CandidateUploadService candidateUploadService;

    @Resource
    private QtiXmlReader qtiXmlReader;

    @Resource
    private AssessmentRenderer renderer;

    @RequestMapping(value="/listSamples", method=RequestMethod.GET)
    public String listSamples(final Model model) {
        model.addAttribute("demoSampleCollection", demoSampleCollection);
        return "listSamples";
    }

    /**
     * FIXME: Make the result of this cacheable
     * @throws IOException
     */
    @RequestMapping(value="/source/{setIndex}/{itemIndex}", method=RequestMethod.GET)
    public void getSource(final HttpServletResponse response, @PathVariable final int setIndex, @PathVariable final int itemIndex) throws IOException {
        final QtiSampleResource qtiSampleResource = findSampleResource(setIndex, itemIndex);
        final ResourceLocator sampleResourceLocator = getSampleResourceLocator();
        final InputStream sampleInputStream = sampleResourceLocator.findResource(qtiSampleResource.toClassPathUri());
        response.setContentType("application/xml");
        IoUtilities.transfer(sampleInputStream, response.getOutputStream(), false);
    }

    private QtiSampleResource findSampleResource(final int setIndex, final int itemIndex) {
        final List<QtiSampleSet> demoSampleSets = demoSampleCollection.getQtiSampleSets();
        if (setIndex < 0 || setIndex >= demoSampleSets.size()) {
            throw new QtiSampleNotFoundException("Could not find demo sample set with index " + setIndex);
        }
        final QtiSampleSet demoSampleSet = demoSampleSets.get(setIndex);
        final List<QtiSampleResource> demoSampleResources = demoSampleSet.getQtiSampleResources();
        if (itemIndex <0 || itemIndex >= demoSampleResources.size()) {
            throw new QtiSampleNotFoundException("Could not find demo sample resource with index " + setIndex + " in set " + demoSampleSet);
        }
        return demoSampleResources.get(itemIndex);
    }

    private ResolvedAssessmentItem resolveSampleItem(final int setIndex, final int itemIndex) {
        final QtiSampleResource demoSampleResource = findSampleResource(setIndex, itemIndex);

        /* Load and resolve item */
        final ResourceLocator sampleResourceLocator = getSampleResourceLocator();
        final URI sampleResourceUri = demoSampleResource.toClassPathUri();
        final QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(sampleResourceLocator);
        final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
        return objectManager.resolveAssessmentItem(sampleResourceUri, ModelRichness.FULL_ASSUMED_VALID);
    }

    /**
     * Starts a new item session using the sample resource with the given path
     */
    @RequestMapping(value="/newSession/{setIndex}/{itemIndex}", method=RequestMethod.GET)
    public String newSampleItemSession(final HttpSession httpSession, @PathVariable final int setIndex, @PathVariable final int itemIndex) {
        logger.info("newSampleItemSession(setIndex={}, itemIndex={})", setIndex, itemIndex);
        final ResolvedAssessmentItem resolvedSampleItem = resolveSampleItem(setIndex, itemIndex);

        /* Create new item session */
        final ItemSessionState itemSessionState = new ItemSessionState();

        /* TEMP: Create handle to store in HTTP session */
        final SampleItemHandle handle = new SampleItemHandle(setIndex, itemIndex, resolvedSampleItem, itemSessionState);
        httpSession.setAttribute(CURRENT_ITEM_HANDLE, handle);

        /* Redirect to session handler */
        return "redirect:/dispatcher/itemSession";
    }

    /**
     * FIXME: Might want to create a sandboxed version of this.
     */
    private ResourceLocator getSampleResourceLocator() {
        return new ClassPathResourceLocator();
    }

    private SampleItemHandle getCurrentSampleItemHandle(final HttpSession httpSession) throws NoItemInSessionException {
        final SampleItemHandle sampleItemHandle = (SampleItemHandle) httpSession.getAttribute(CURRENT_ITEM_HANDLE);
        if (sampleItemHandle==null) {
            throw new NoItemInSessionException();
        }
        return sampleItemHandle;
    }

    /** FIXME: This should be POST only */
    @RequestMapping(value="/endItemSession", method={ RequestMethod.GET, RequestMethod.POST })
    public String endItemSession(final HttpSession httpSession) {
        /* TEMP: Remove stuff stored in HttpSession */
        httpSession.removeAttribute(CURRENT_ITEM_HANDLE);

        /* TEMP: Go back to the listing */
        return "redirect:/dispatcher/listSamples";
    }

    /** FIXME: This should be POST only
     * @throws NoItemInSessionException
     */
    @RequestMapping(value="/resetItemSession", method={ RequestMethod.GET, RequestMethod.POST })
    public String resetItemSession(final HttpSession httpSession) throws NoItemInSessionException {
        /* TEMP: Extract current item & state from HTTP session */
        final SampleItemHandle sampleItemHandle = getCurrentSampleItemHandle(httpSession);
        sampleItemHandle.itemSessionState = new ItemSessionState();

        /* Redirect to session handler */
        return "redirect:/dispatcher/itemSession";
    }

    /**
     * FIXME: This isn't really required, other than saving me having to embed control URLs
     * into the rendering output at the moment. This will probably die later.
     *
     * @param httpSession
     * @return
     * @throws NoItemInSessionException
     */
    @RequestMapping(value="/itemSource", method={ RequestMethod.GET })
    public String getCurrentItemSource(final HttpSession httpSession)
            throws NoItemInSessionException {
        /* TEMP: Extract current item & state from HTTP session */
        final SampleItemHandle sampleItemHandle = getCurrentSampleItemHandle(httpSession);

        return "redirect:/dispatcher/source/" + sampleItemHandle.setIndex + "/" + sampleItemHandle.itemIndex;
    }

    @RequestMapping(value="/itemResult", method={ RequestMethod.GET })
    public void getItemResult(final HttpServletResponse response, final HttpSession httpSession)
            throws NoItemInSessionException, IOException {
        /* TEMP: Extract current item & state from HTTP session */
        final SampleItemHandle sampleItemHandle = getCurrentSampleItemHandle(httpSession);
        final ResolvedAssessmentItem resolvedAssessmentItem = sampleItemHandle.resolvedSampleItem;
        final ItemSessionState itemSessionState = sampleItemHandle.itemSessionState;

        final ItemSessionController itemController = new ItemSessionController(jqtiExtensionManager, resolvedAssessmentItem, itemSessionState);
        final ItemResult itemResult = itemController.computeItemResult();
        response.setContentType("application/xml");
        renderer.serializeJqtiObject(itemResult, response.getOutputStream());
    }

    /** FIXME: Separate out GET and POST
     * @throws NoItemInSessionException */
    @RequestMapping(value="/itemSession", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public String sampleItemSession(final HttpServletRequest request, final HttpSession httpSession)
            throws NoItemInSessionException {
        /* TEMP: Extract current item & state from HTTP session */
        final SampleItemHandle sampleItemHandle = getCurrentSampleItemHandle(httpSession);
        final ResolvedAssessmentItem resolvedAssessmentItem = sampleItemHandle.resolvedSampleItem;
        final ItemSessionState itemSessionState = sampleItemHandle.itemSessionState;

        String pageContent = null;
        try {
            if (!itemSessionState.isInitialized()) {
                /* Session hasn't been initialized yet */
                pageContent = serveFreshItem(resolvedAssessmentItem, itemSessionState);
            }
            else {
                /* Session initialised, so handle responses */
                pageContent = handleResponseSubmission(request, resolvedAssessmentItem, itemSessionState);
            }
        }
        catch (final RuntimeValidationException e) {
            throw new QtiLogicException("Unexpected RuntimeValidationException encountered", e);
        }
        return pageContent;
    }

    private String serveFreshItem(final ResolvedAssessmentItem resolvedAssessmentItem, final ItemSessionState itemSessionState)
            throws RuntimeValidationException {
        final ItemSessionController itemSessionController = new ItemSessionController(jqtiExtensionManager, resolvedAssessmentItem, itemSessionState);
        itemSessionController.initialize();

        return renderer.renderFreshStandaloneItem(itemSessionController, SerializationMethod.HTML5_MATHJAX);
    }

    private String handleResponseSubmission(final HttpServletRequest request, final ResolvedAssessmentItem resolvedAssessmentItem, final ItemSessionState itemSessionState)
            throws RuntimeValidationException {
        /* First need to extract responses */
        final Map<String, ResponseData> responseMap = new HashMap<String, ResponseData>();
        extractStringResponseData(request, responseMap);
        if (request instanceof MultipartHttpServletRequest) {
            extractFileResponseData((MultipartHttpServletRequest) request, responseMap);
        }
        logger.debug("Extracted responses {}", responseMap);

        /* Bind responses */
        final ItemSessionController itemSessionController = new ItemSessionController(jqtiExtensionManager, resolvedAssessmentItem, itemSessionState);
        List<Identifier> invalidResponseIdentifiers = null;
        final List<Identifier> badResponseIdentifiers = itemSessionController.bindResponses(responseMap);
        if (badResponseIdentifiers.isEmpty()) {
            logger.debug("Responses bound successfully, so continuing to response validation step");
            invalidResponseIdentifiers = itemSessionController.validateResponses();
            if (invalidResponseIdentifiers.isEmpty()) {
                logger.debug("Responses validated successfully, so invoking response processing");
                itemSessionController.processResponses();
            }
            else {
                logger.debug("Invalid responses submitted to {}, not invoking response processing", invalidResponseIdentifiers);
            }
        }
        else {
            logger.debug("Bad responses submitted to {}, so no response processing invoked", badResponseIdentifiers);
        }

        return renderer.renderRespondedStandaloneItem(itemSessionController,
                responseMap, badResponseIdentifiers, invalidResponseIdentifiers,
                SerializationMethod.HTML5_MATHJAX);
    }

    private void extractFileResponseData(final MultipartHttpServletRequest multipartRequest, final Map<String, ResponseData> responseMap) {
        @SuppressWarnings("unchecked")
        final Set<String> parameterNames = multipartRequest.getParameterMap().keySet();
        for (final String name : parameterNames) {
            if (name.startsWith("qtiworks_uploadpresented_")) {
                final String responseIdentifier = name.substring("qtiworks_uploadpresented_".length());
                FileResponseData fileResponseData = null;
                final MultipartFile multipartFile = multipartRequest.getFile("qtiworks_uploadresponse_" + responseIdentifier);
                if (multipartFile!=null) {
                    fileResponseData = candidateUploadService.importData(multipartFile);
                }
                responseMap.put(responseIdentifier, fileResponseData);
            }
        }
    }

    private void extractStringResponseData(final HttpServletRequest request, final Map<String, ResponseData> responseMap) {
        @SuppressWarnings("unchecked")
        final Set<String> parameterNames = request.getParameterMap().keySet();
        for (final String name : parameterNames) {
            if (name.startsWith("qtiworks_presented_")) {
                final String responseIdentifier = name.substring("qtiworks_presented_".length());
                final String[] responseValues = request.getParameterValues("qtiworks_response_" + responseIdentifier);
                final StringResponseData stringResponseData = new StringResponseData(responseValues);
                responseMap.put(responseIdentifier, stringResponseData);
            }
        }
    }

    /**
     * FIXME: This currently can be used to serve up anything within the ClassPath.
     * This needs to be integrated with a proper sandboxed URI.
     *
     * FIXME: Would be nice to allow this to be cached at some point.
     */
    @RequestMapping(value="/assessmentResource", method=RequestMethod.GET)
    public void assessmentResource(@RequestParam("uri") final URI uri, final HttpServletResponse response) throws IOException {
        final ResourceLocator resourceLocator = getSampleResourceLocator();
        final InputStream resourceStream = resourceLocator.findResource(uri);
        if (resourceStream==null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final String contentType = getResourceContentType(uri);
        response.setContentType(contentType);
        IoUtilities.transfer(resourceStream, response.getOutputStream(), false);
    }

    private String getResourceContentType(final URI uri) {
        return URLConnection.getFileNameMap().getContentTypeFor(uri.toString());
    }

    /**
     * TEMP! Thrown if there is no item currently stored within the HTTP session.
     *
     * @author David McKain
     */
    @ResponseStatus(value=HttpStatus.NOT_FOUND)
    public static final class NoItemInSessionException extends Exception {

        private static final long serialVersionUID = 5304107064642640157L;

    }

    /**
     * TEMP! Encapsulates details about the item currently being played within the
     * current HTTP session.
     *
     * @author David McKain
     */
    protected static final class SampleItemHandle implements Serializable {

        private static final long serialVersionUID = 2271804020181344518L;

        public int setIndex;
        public int itemIndex;
        public ResolvedAssessmentItem resolvedSampleItem;
        public ItemSessionState itemSessionState;

        public SampleItemHandle(final int setIndex, final int itemIndex, final ResolvedAssessmentItem resolvedSampleItem, final ItemSessionState itemSessionState) {
            this.setIndex = setIndex;
            this.itemIndex = itemIndex;
            this.resolvedSampleItem = resolvedSampleItem;
            this.itemSessionState = itemSessionState;
        }
    }
}
