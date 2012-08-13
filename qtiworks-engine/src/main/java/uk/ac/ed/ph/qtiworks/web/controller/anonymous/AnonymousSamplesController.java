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
package uk.ac.ed.ph.qtiworks.web.controller.anonymous;

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.domain.dao.SampleCategoryDao;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.SampleCategory;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;

import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for browsing and trying the public samples
 *
 * @author David McKain
 */
@Controller
public class AnonymousSamplesController {

    @Resource
    private SampleCategoryDao sampleCategoryDao;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private CandidateSessionStarter candidateSessionStarter;

    @Resource
    private AnonymousRouter anonymousRouter;

    @RequestMapping(value="/samples/list", method=RequestMethod.GET)
    public String listSamples(final Model model) {
        /* Look up all Assessments, grouped by SampleCategory.
         * TODO: We're exposing the DAO layer here, which smells a bit but is OK here.
         */
        final Map<SampleCategory, List<Assessment>> sampleAssessmentMap = new LinkedHashMap<SampleCategory, List<Assessment>>();
        for (final SampleCategory sampleCategory : sampleCategoryDao.getAll()) {
            final List<Assessment> assessmentsForCategory = assessmentDao.getForSampleCategory(sampleCategory);
            sampleAssessmentMap.put(sampleCategory, assessmentsForCategory);
        }

        model.addAttribute("sampleAssessmentMap", sampleAssessmentMap);
        return "samples/list";
    }

    /**
     * Starts a new {@link CandidateItemSession} on the given sample, using the special
     * {@link ItemDelivery} created when bootstrapping the samples.
     */
    @RequestMapping(value="/samples/{aid}", method=RequestMethod.POST)
    public String startItemSession(@PathVariable final long aid)
            throws PrivilegeException, DomainEntityNotFoundException, RuntimeValidationException {
        final String exitUrl = anonymousRouter.buildWithinContextUrl("/samples/list");

        final CandidateItemSession candidateItemSession = candidateSessionStarter.createSystemSampleSession(aid, exitUrl);
        return redirectToCandidateSession(candidateItemSession);
    }

    private String redirectToCandidateSession(final CandidateItemSession candidateItemSession) {
        return "redirect:/candidate/session/" + candidateItemSession.getId()
                + "/" + candidateItemSession.getSessionToken();
    }
}
