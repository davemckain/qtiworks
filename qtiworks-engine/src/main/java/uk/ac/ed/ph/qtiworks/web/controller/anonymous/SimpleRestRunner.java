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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

/**
 * Very simple standalone REST-like runner.
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
    private AnonymousRouter anonymousRouter;

    @Resource
    private QtiWorksDeploymentSettings qtiWorksDeploymentSettings;

    //--------------------------------------------------------------------

    @RequestMapping(value="/simplerestrunner", method=RequestMethod.POST)
    public void simpleRestRunner(final HttpSession httpSession, final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        /* Upload POST payload to temp file */
        final String uploadContentType = request.getContentType();
        final File uploadFile = filespaceManager.createTempFile();
        try {
            FileUtils.copyInputStreamToFile(request.getInputStream(), uploadFile);
        }
        catch (final IOException e) {
            response.setHeader(ERROR_HEADER, "post-read-error");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        final MultipartFile multipartFile = new MultipartFileWrapper(uploadFile, uploadContentType);

        /* Import and validate this Assessment */
        final Assessment assessment;
        try {
            assessment = assessmentManagementService.importAssessment(multipartFile, true);
        }
        catch (final AssessmentPackageDataImportException e) {
            response.setHeader(ERROR_HEADER, "assessment-content-error (" + e.getFailure().getReason().toString() + ")");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        catch (final PrivilegeException e) {
            /* This should not happen if authentication logic has been done correctly */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        finally {
            if (!uploadFile.delete()) {
                logger.warn("Could not delete temp file {}", uploadFile);
            }
        }

        try {
            /* Check if assessment can be launched */
            final AssessmentPackage assessmentPackage = assessmentDataService.ensureSelectedAssessmentPackage(assessment);
            if (!assessmentPackage.isLaunchable()) {
                /* Assessment isn't launchable.
                 * (Client can examine header and subsequently choose to validate the assessment.) */
                response.setHeader(ERROR_HEADER, "assessment-not-launchable");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            /* Try to launch candidate session */
            final Delivery delivery = assessmentManagementService.createDemoDelivery(assessment);
            final String returnUrl = anonymousRouter.buildWithinContextUrl("/standalonerunner");
            final CandidateSessionTicket candidateSession = candidateSessionLaunchService.launchAnonymousCandidateSession(httpSession, delivery, returnUrl);

            /* Send redirect to candidate session */
            final String candidateSessionUrl = qtiWorksDeploymentSettings.getBaseUrl() + GlobalRouter.buildSessionStartWithinContextUrl(candidateSession);
            response.setHeader("Location", candidateSessionUrl);
            response.sendError(HttpServletResponse.SC_SEE_OTHER);
            return;
        }
        catch (final PrivilegeException e) {
            /* This should not happen if access control logic has been done correctly */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        catch (final CandidateException e) {
            /* This should not happen if underlying logic has been done correctly */
            throw QtiWorksRuntimeException.unexpectedException(e);
        }
        finally {
            /* (Assessment will be deleted during the scheduled data cleanup operation */
        }
    }
}