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

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.services.AssessmentCandidateService;

import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for candidate item sessions
 *
 * @author David McKain
 */
@Controller
public class CandidateItemController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateItemController.class);

    @Resource
    private AssessmentCandidateService assessmentCandidateService;

    /**
     * Starts a new {@link CandidateItemSession} for the given {@link ItemDelivery}.
     *
     * FIXME: This should be a POST only!
     */
    @RequestMapping(value="/delivery/{did}", method=RequestMethod.GET)
    public String startCandidateItemSession(@PathVariable final long did)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        logger.info("Creating new CandidateItemSession for delivery {}", did);
        final ItemDelivery itemDelivery = assessmentCandidateService.lookupItemDelivery(did);
        final CandidateItemSession candidateSession = assessmentCandidateService.createCandidateSession(itemDelivery);
        final ItemSessionState itemSessionState = assessmentCandidateService.initialiseSession(candidateSession);

        return "redirect:/web/public/session/" + candidateSession.getId();
    }

    /**
     * Renders the current state of the given session
     */
    @RequestMapping(value="/session/{xid}", method=RequestMethod.GET)
    public void renderItem() {

    }

}
