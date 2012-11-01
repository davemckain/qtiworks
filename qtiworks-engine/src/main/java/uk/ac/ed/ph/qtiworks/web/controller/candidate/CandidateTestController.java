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
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateForbiddenException;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateItemDeliveryService;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for candidate test sessions
 *
 * @author David McKain
 */
@Controller
public class CandidateTestController {

    @Resource
    private CandidateItemDeliveryService candidateItemDeliveryService;

    //----------------------------------------------------
    // Rendering

    /**
     * Renders the current state of the given session
     *
     * FIXME: This is currently just a placeholder!
     *
     * @throws IOException
     * @throws CandidateForbiddenException
     */
    @ResponseBody
    @RequestMapping(value="/testsession/{xid}/{sessionToken}", method=RequestMethod.GET)
    public String renderCurrentTestSessionState(@PathVariable final long xid, @PathVariable final String sessionToken,
            final WebRequest webRequest, final HttpServletResponse response)
            throws DomainEntityNotFoundException, IOException, CandidateForbiddenException {
        final CandidateSession candidateSession = candidateItemDeliveryService.lookupCandidateSession(xid, sessionToken);

        final StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("This is test session " ).append(candidateSession.getId());

        return resultBuilder.toString();
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
