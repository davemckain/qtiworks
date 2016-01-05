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
package uk.ac.ed.ph.qtiworks.web.controller.anonymous;

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksDeploymentSettings;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.services.AssessmentDataService;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.FilespaceManager;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageDataImportException;
import uk.ac.ed.ph.qtiworks.services.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.utils.MultipartFileWrapper;
import uk.ac.ed.ph.qtiworks.web.GlobalRouter;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionLaunchService;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionTicket;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * <h2>NOTE: This API is still currently a sketch. Do not rely on it yet!</h2>
 *
 * This is a <strong>very</strong> simple standalone REST-like runner for uploading
 * and launching assessments. It could be useful for authoring systems who want to
 * use QTIWorks for previewing/trying assessments.
 *
 * <h2>Usage</h2>
 *
 * POST an IMS Content Package containing a QTI assessment to the URL
 * <code>$QTIWORKS_BASE_URL/anonymous/simplerestrunner</code>.
 * <p>
 * QTIWorks will attempt to import this data as a new assessment and check that it is launchable.
 * <ul>
 *   <li>
 *     If the assessment is launchable, QTIWorks will return an HTTP 303 redirection
 *     response containing a URL that can be used to launch new candidate sessions on the
 *     assessment. This URL can be passed to a browser. (For convenience, this launch URL will
 *     respond to both GET and POST requests, even though GET is technically inappropriate.)
 *   </li>
 *   <li>
 *     If the assessment data is inappropriate (e.g. not a QTI assessment) or if the assessment
 *     has errors which prevent it from being launched, then QTIWorks will send back
 *     a 400 or 500 response. Further details about the error will be available within the
 *     {@value #ERROR_HEADER} HTTP response header.
 *   </li>
 * </ul>
 * Security note: This implementation uses "security through obscurity". It is theoretically
 * possible for other people to access the uploaded assessment data, either by guessing the
 * launch URL that is generated here.
 *
 * @author David McKain
 */
@Controller
public class SimpleRestRunner {

    private static final Logger logger = LoggerFactory.getLogger(SimpleRestRunner.class);

    /** HTTP header used for communicating errors back the client */
    public static final String ERROR_HEADER = "X-QTIWorks-SimpleRestRunner-ErrorCode";

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private CandidateSessionLaunchService candidateSessionLaunchService;

    @Resource
    private AssessmentDataService assessmentDataService;

    @Resource
    private FilespaceManager filespaceManager;

    @Resource
    private QtiWorksDeploymentSettings qtiWorksDeploymentSettings;

    //--------------------------------------------------------------------

    @RequestMapping(value="/simplerestrunner", method=RequestMethod.POST)
    public void simpleRestRunner(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        /* Import POST payload as an Assessment */
        final Assessment assessment;
        try {
            assessment = importPostPayloadAsAssessment(request);
        }
        catch (final IOException e) {
            /* Couldn't read in POST data */
            response.setHeader(ERROR_HEADER, "post-read-error");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        catch (final AssessmentPackageDataImportException e) {
            /* QTIWorks didn't like the assessment package content */
            response.setHeader(ERROR_HEADER, "assessment-content-error (" + e.getFailure().getReason().toString() + ")");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            /* Check if assessment can be launched */
            final AssessmentPackage assessmentPackage = assessmentDataService.ensureSelectedAssessmentPackage(assessment);
            if (!assessmentPackage.isLaunchable()) {
                /* Assessment isn't launchable, so reject it
                 * (Client can examine header and subsequently choose to validate the assessment.) */
                response.setHeader(ERROR_HEADER, "assessment-not-launchable");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            /* Create a delivery and a token to pass back for subsequently accessing this delivery */
            final Delivery delivery = assessmentManagementService.createDemoDelivery(assessment);
            final String deliveryToken = candidateSessionLaunchService.generateWebServiceDeliveryToken(delivery);

            /* Create the URL that can be returned for launching candidate sessions on this delivery */
            final String deliveryLaunchUrl = qtiWorksDeploymentSettings.getBaseUrl()
                    + "/anonymous/simplerestrunner/launcher/" + delivery.getId()
                    + "/" + deliveryToken;
            response.setHeader("Location", deliveryLaunchUrl);
            response.sendError(HttpServletResponse.SC_SEE_OTHER);
            return;
        }
        catch (final PrivilegeException e) {
            /* This should not happen if access control logic has been done correctly */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        finally {
            /* (Assessment will be deleted during the scheduled data cleanup operation */
        }
    }

    @RequestMapping(value="/simplerestrunner/launcher/{did}/{deliveryToken}", method={RequestMethod.GET, RequestMethod.POST})
    public void launchCandidateSession(final HttpSession httpSession, final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable final long did, @PathVariable final String deliveryToken)
            throws IOException {
        try {
            /* Create new candidate session */
            final String sessionExistReturnUrl = "/anonymous/simplerestrunner/exit";
            final CandidateSessionTicket candidateSessionTicket = candidateSessionLaunchService.launchWebServiceCandidateSession(httpSession, did, deliveryToken, sessionExistReturnUrl);

            /* Redirect to candidate dispatcher */
            final String launchUrl = request.getContextPath()
                    + GlobalRouter.buildSessionStartWithinContextUrl(candidateSessionTicket);
            response.setHeader("Location", launchUrl);
            response.sendError(HttpServletResponse.SC_SEE_OTHER);
        }
        catch (final DomainEntityNotFoundException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (final CandidateException e) {
            /* This should not happen if underlying logic has been done correctly */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
    }

    @RequestMapping(value="/simplerestrunner/exit", method=RequestMethod.GET)
    @ResponseBody()
    public String showExitPage() {
        return "You may now close this window.";
    }

    //--------------------------------------------------------------------

    private Assessment importPostPayloadAsAssessment(final HttpServletRequest request)
            throws IOException, AssessmentPackageDataImportException {
        /* First we must upload the POST payload into a temp file */
        final String uploadContentType = request.getContentType();
        final File uploadFile = filespaceManager.createTempFile();
        FileUtils.copyInputStreamToFile(request.getInputStream(), uploadFile);
        final MultipartFile multipartFile = new MultipartFileWrapper(uploadFile, uploadContentType);

        /* Then we import this temp file as an assessment */
        try {
            return assessmentManagementService.importAssessment(multipartFile, true);
        }
        catch (final PrivilegeException e) {
            /* (This should not happen if authentication logic has been done correctly) */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        finally {
            /* Make sure we delete the temp file */
            if (!uploadFile.delete()) {
                logger.warn("Could not delete temp file {}", uploadFile);
            }
        }
    }
}