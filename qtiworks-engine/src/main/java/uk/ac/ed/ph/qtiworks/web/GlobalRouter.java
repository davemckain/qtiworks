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
package uk.ac.ed.ph.qtiworks.web;

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionTicket;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global webapp router and helper.
 *
 * @author David McKain
 */
public final class GlobalRouter {

    public static final String FLASH = "flashMessage";

    /**
     * Records the given message using Spring's Flash Message functionality. The message will be displayed in
     * in the (JSP) view.
     *
     * @param redirectAttributes
     * @param message
     */
    public static void addFlashMessage(final RedirectAttributes redirectAttributes, final String message) {
        redirectAttributes.addFlashAttribute(GlobalRouter.FLASH, message);
    }

    //----------------------------------------------------

    /**
     * Generates an internal redirect to a {@link CandidateSession}, using Spring's <code>redirect:</code> format.
     * This is suitable for returning in a Spring MVC controller.
     *
     * @param candidateSessionTicket ticket for the session to connect to
     * @return Spring redirect URL
     */
    public static String buildSessionStartRedirect(final CandidateSessionTicket candidateSessionTicket) {
        return "redirect:" + buildSessionStartWithinContextUrl(candidateSessionTicket);
    }

    /**
     * Generates an internal redirect to a {@link CandidateSession}, using Spring's <code>redirect:</code> format.
     * This is suitable for returning in a Spring MVC controller.
     *
     * @param candidateSession candidate session to connect to
     * @param xsrfToken XSRF token previously granted for accesing the session
     * @return Spring redirect URL
     */
    public static String buildSessionStartRedirect(final CandidateSession candidateSession, final String xsrfToken) {
        return "redirect:" + buildSessionStartWithinContextUrl(candidateSession, xsrfToken);
    }

    //----------------------------------------------------

    /**
     * Creates a within context URL for launching the {@link CandidateSession} corresponding to the
     * given {@link CandidateSessionTicket}.
     *
     * @param candidateSessionTicket ticket for the session to connect to
     * @return within-context session launch URL
     */
    public static String buildSessionStartWithinContextUrl(final CandidateSessionTicket candidateSessionTicket) {
        return buildSessionStartWithinContextUrl(candidateSessionTicket.getCandidateSessionId(),
                candidateSessionTicket.getXsrfToken(),
                candidateSessionTicket.getAssessmentObjectType());
    }

    /**
     * Creates a within context URL for launching the {@link CandidateSession}.
     *
     * @param candidateSession candidate session to connect to
     * @param xsrfToken XSRF token previously granted for accesing the session
     * @return within-context session launch URL
     */
    public static String buildSessionStartWithinContextUrl(final CandidateSession candidateSession, final String xsrfToken) {
        return buildSessionStartWithinContextUrl(candidateSession.getId(), xsrfToken, candidateSession.getDelivery().getAssessment().getAssessmentType());
    }

    private static String buildSessionStartWithinContextUrl(final long xid, final String xsrfToken, final AssessmentObjectType assessmentObjectType) {
        return "/candidate/"
                + (assessmentObjectType==AssessmentObjectType.ASSESSMENT_ITEM ? "itemsession" : "testsession")
                + "/" + xid
                + "/" + xsrfToken;
    }
}

