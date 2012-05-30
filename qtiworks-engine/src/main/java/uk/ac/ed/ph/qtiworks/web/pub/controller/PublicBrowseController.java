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
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.domain.dao.SampleCategoryDao;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.SampleCategory;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.web.CacheableWebOutputStreamer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
@Controller
public class PublicBrowseController {

    /** Default age for any cacheable resources */
    public static final long CACHE_AGE = 60 * 60;

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private SampleCategoryDao sampleCategoryDao;

    @Resource
    private AssessmentPackageDao assessmentPackageDao;

    @RequestMapping(value="/samples/list", method=RequestMethod.GET)
    public String listSamples(final Model model) {
        /* Look up all Assessments, grouped by SampleCategory.
         * TODO: We're exposing the DAO layer here, which smells a bit but is OK here.
         */
        final Map<SampleCategory, List<AssessmentPackage>> sampleAssessmentMap = new LinkedHashMap<SampleCategory, List<AssessmentPackage>>();
        for (final SampleCategory sampleCategory : sampleCategoryDao.getAll()) {
            final List<AssessmentPackage> assessmentsForCategory = assessmentPackageDao.getForSampleCategory(sampleCategory);
            sampleAssessmentMap.put(sampleCategory, assessmentsForCategory);
        }

        model.addAttribute("sampleAssessmentMap", sampleAssessmentMap);
        return "public/samples/list";
    }

    /**
     * Serves the source of the given {@link AssessmentPackage}
     *
     * @see AssessmentManagementService#streamPackageSource(AssessmentPackage, java.io.OutputStream)
     *
     * @throws IOException
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     */
    @RequestMapping(value="/package/{apid}/source", method=RequestMethod.GET)
    public void streamPackageSource(@PathVariable final long apid,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, PrivilegeException, DomainEntityNotFoundException {
        /* Look up package and make sure caller has read permission on it */
        final AssessmentPackage assessmentPackage = assessmentManagementService.getAssessmentPackage(apid);

        final String resourceEtag = ServiceUtilities.computeSha1Digest(request.getRequestURI());
        final String requestEtag = request.getHeader("If-None-Match");
        if (resourceEtag.equals(requestEtag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else {
            final CacheableWebOutputStreamer outputStreamer = new CacheableWebOutputStreamer(response, resourceEtag, CACHE_AGE);
            assessmentManagementService.streamPackageSource(assessmentPackage, outputStreamer);
        }
    }
}
