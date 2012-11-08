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
package uk.ac.ed.ph.qtiworks.web.controller.candidate;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateForbiddenException;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateTestDeliveryService;
import uk.ac.ed.ph.qtiworks.web.CacheableWebOutputStreamer;
import uk.ac.ed.ph.qtiworks.web.NonCacheableWebOutputStreamer;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Controller for candidate test sessions
 *
 * @author David McKain
 */
@Controller
public class CandidateTestController {

    @Resource
    private CandidateTestDeliveryService candidateTestDeliveryService;

    //----------------------------------------------------
    // Rendering

    /**
     * Renders the current state of the given session
     */
    @RequestMapping(value="/testsession/{xid}/{sessionToken}", method=RequestMethod.GET)
    public void renderCurrentTestSessionState(@PathVariable final long xid, @PathVariable final String sessionToken,
            final WebRequest webRequest, final HttpServletResponse response)
            throws DomainEntityNotFoundException, IOException, CandidateForbiddenException {
        /* Create appropriate options that link back to this controller */
        final RenderingOptions renderingOptions = createTestRenderingOptions(webRequest, xid, sessionToken);

        final NonCacheableWebOutputStreamer outputStreamer = new NonCacheableWebOutputStreamer(response);
        candidateTestDeliveryService.renderCurrentState(xid, sessionToken, renderingOptions, outputStreamer);
    }

    private RenderingOptions createTestRenderingOptions(final WebRequest webRequest, final long xid, final String sessionToken) {
        final String sessionBaseUrl = "/candidate/testsession/" + xid + "/" + sessionToken;
        final RenderingOptions renderingOptions = new RenderingOptions();
        renderingOptions.setContextPath(webRequest.getContextPath());
        renderingOptions.setSerializationMethod(SerializationMethod.HTML5_MATHJAX);
        renderingOptions.setAttemptUrl(sessionBaseUrl + "/attempt");
        renderingOptions.setCloseUrl(sessionBaseUrl + "/close");
        renderingOptions.setSolutionUrl(sessionBaseUrl + "/solution");
        renderingOptions.setResetUrl(sessionBaseUrl + "/reset");
        renderingOptions.setReinitUrl(sessionBaseUrl + "/reinit");
        renderingOptions.setResultUrl(sessionBaseUrl + "/result");
        renderingOptions.setTerminateUrl(sessionBaseUrl + "/terminate");
        renderingOptions.setPlaybackUrlBase(sessionBaseUrl+ "/playback");
        renderingOptions.setSourceUrl(sessionBaseUrl + "/source");
        renderingOptions.setServeFileUrl(sessionBaseUrl + "/file");
        renderingOptions.setSelectItemUrl(sessionBaseUrl + "/select");
        renderingOptions.setEndTestPartUrl(sessionBaseUrl + "/endtestpart");
        renderingOptions.setExitTestPartUrl(sessionBaseUrl + "/exittestpart");
        renderingOptions.setTestPartNavigationUrl(sessionBaseUrl + "/navigation");
        return renderingOptions;
    }

    //----------------------------------------------------
    // Attempt handling

    /**
     * Handles submission of candidate responses
     */
    @RequestMapping(value="/testsession/{xid}/{sessionToken}/attempt", method=RequestMethod.POST)
    public String handleAttempt(final HttpServletRequest request, @PathVariable final long xid,
            @PathVariable final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException {
        /* First need to extract responses */
        final Map<Identifier, StringResponseData> stringResponseMap = extractStringResponseData(request);

        /* Extract and import file responses (if appropriate) */
        Map<Identifier, MultipartFile> fileResponseMap = null;
        if (request instanceof MultipartHttpServletRequest) {
            fileResponseMap = extractFileResponseData((MultipartHttpServletRequest) request);
        }

        /* Call up service layer */
        candidateTestDeliveryService.handleAttempt(xid, sessionToken, stringResponseMap, fileResponseMap);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionToken);
    }

    /**
     * FIXME: copy & pasted from {@link ItemSessionController}
     * @throws BadResponseWebPayloadException
     */
    private Map<Identifier, MultipartFile> extractFileResponseData(final MultipartHttpServletRequest multipartRequest) {
        final Map<Identifier, MultipartFile> fileResponseMap = new HashMap<Identifier, MultipartFile>();
        @SuppressWarnings("unchecked")
        final Set<String> parameterNames = multipartRequest.getParameterMap().keySet();
        for (final String name : parameterNames) {
            if (name.startsWith("qtiworks_uploadpresented_")) {
                final String responseIdentifierString = name.substring("qtiworks_uploadpresented_".length());
                final Identifier responseIdentifier;
                try {
                    responseIdentifier = Identifier.parseString(responseIdentifierString);
                }
                catch (final QtiParseException e) {
                    throw new BadResponseWebPayloadException("Bad response identifier encoded in parameter  " + name, e);
                }
                final String multipartName = "qtiworks_uploadresponse_" + responseIdentifierString;
                final MultipartFile multipartFile = multipartRequest.getFile(multipartName);
                if (multipartFile==null) {
                    throw new BadResponseWebPayloadException("Expected to find multipart file with name " + multipartName);
                }
                fileResponseMap.put(responseIdentifier, multipartFile);
            }
        }
        return fileResponseMap;
    }

    /**
     * FIXME: copy & pasted from {@link ItemSessionController}
     * @throws BadResponseWebPayloadException
     */
    private Map<Identifier, StringResponseData> extractStringResponseData(final HttpServletRequest request) {
        final Map<Identifier, StringResponseData> responseMap = new HashMap<Identifier, StringResponseData>();
        @SuppressWarnings("unchecked")
        final Set<String> parameterNames = request.getParameterMap().keySet();
        for (final String name : parameterNames) {
            if (name.startsWith("qtiworks_presented_")) {
                final String responseIdentifierString = name.substring("qtiworks_presented_".length());
                final Identifier responseIdentifier;
                try {
                    responseIdentifier = Identifier.parseString(responseIdentifierString);
                }
                catch (final QtiParseException e) {
                    throw new BadResponseWebPayloadException("Bad response identifier encoded in parameter  " + name, e);
                }
                final String[] responseValues = request.getParameterValues("qtiworks_response_" + responseIdentifierString);
                final StringResponseData stringResponseData = new StringResponseData(responseValues);
                responseMap.put(responseIdentifier, stringResponseData);
            }
        }
        return responseMap;
    }

    //----------------------------------------------------
    // Test navigation and lifecycle

    /**
     * Selects the navigaton menu for the current test part
     */
    @RequestMapping(value="/testsession/{xid}/{sessionToken}/navigation", method=RequestMethod.POST)
    public String showNavigationMenu(@PathVariable final long xid, @PathVariable final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException {
        candidateTestDeliveryService.selectNavigationMenu(xid, sessionToken);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionToken);
    }

    /**
     * Selects the requested item instance
     */
    @RequestMapping(value="/testsession/{xid}/{sessionToken}/select/{key}", method=RequestMethod.POST)
    public String selectItem(@PathVariable final long xid, @PathVariable final String sessionToken, @PathVariable final String key)
            throws DomainEntityNotFoundException, CandidateForbiddenException {
        candidateTestDeliveryService.selectItem(xid, sessionToken, TestPlanNodeKey.fromString(key));

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionToken);
    }

    /**
     * Ends the current test part
     */
    @RequestMapping(value="/testsession/{xid}/{sessionToken}/endtestpart", method=RequestMethod.POST)
    public String endCurrentTestPart(@PathVariable final long xid, @PathVariable final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException {
        candidateTestDeliveryService.endCurrentTestPart(xid, sessionToken);

        /* Redirect to rendering of current session state */
        return redirectToRenderSession(xid, sessionToken);
    }

    /**
     * Exits the test
     *
     * FIXME: This should just exit the testPart once we support multiple ones.
     */
    @RequestMapping(value="/testsession/{xid}/{sessionToken}/exittestpart", method=RequestMethod.POST)
    public String exitCurrentTestPart(@PathVariable final long xid, @PathVariable final String sessionToken)
            throws DomainEntityNotFoundException, CandidateForbiddenException {
        final CandidateSession candidateSession = candidateTestDeliveryService.exitCurrentTestPart(xid, sessionToken);

        String redirect = redirectToExitUrl(candidateSession.getExitUrl());
        if (redirect==null) {
            /* No/unsafe redirect specified, so get the rendered to generate an "assessment is complete" page */
            redirect = redirectToRenderSession(xid, sessionToken);
        }
        return redirect;
    }

    //----------------------------------------------------

    /**
     * Serves the given (white-listed) file in the given {@link AssessmentPackage}
     * @throws CandidateForbiddenException
     *
     * @see AssessmentManagementService#streamPackageSource(AssessmentPackage, java.io.OutputStream)
     */
    @RequestMapping(value="/testsession/{xid}/{sessionToken}/file", method=RequestMethod.GET)
    public void streamPackageFile(@PathVariable final long xid, @PathVariable final String sessionToken,
            @RequestParam("href") final String href,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, DomainEntityNotFoundException, CandidateForbiddenException {
        final CandidateSession candidateSession = candidateTestDeliveryService.lookupCandidateSession(xid, sessionToken);
        final String resourceUniqueTag = request.getRequestURI() + "/" + href;
        final String resourceEtag = ServiceUtilities.computeSha1Digest(resourceUniqueTag);
        final String requestEtag = request.getHeader("If-None-Match");
        if (resourceEtag.equals(requestEtag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else {
            final CacheableWebOutputStreamer outputStreamer = new CacheableWebOutputStreamer(response, resourceEtag, CandidateItemController.CACHEABLE_MAX_AGE);
            candidateTestDeliveryService.streamAssessmentFile(candidateSession, href, outputStreamer);
        }
    }

    //----------------------------------------------------
    // Redirections

    private String redirectToRenderSession(final long xid, final String sessionToken) {
        return "redirect:/candidate/testsession/" + xid + "/" + sessionToken;
    }

    private String redirectToExitUrl(final String exitUrl) {
        if (exitUrl!=null && (exitUrl.startsWith("/") || exitUrl.startsWith("http://") || exitUrl.startsWith("https://"))) {
            return "redirect:" + exitUrl;
        }
        return null;
    }
}
