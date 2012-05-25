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
package uk.ac.ed.ph.qtiworks.tools;

import uk.ac.ed.ph.qtiworks.config.BaseServicesConfiguration;
import uk.ac.ed.ph.qtiworks.config.JpaProductionConfiguration;
import uk.ac.ed.ph.qtiworks.config.ServicesConfiguration;
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.dao.InstructorUserDao;
import uk.ac.ed.ph.qtiworks.domain.dao.ItemDeliveryDao;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateFileSubmission;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.services.AssessmentCandidateService;
import uk.ac.ed.ph.qtiworks.utils.NullMultipartFile;

import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Dev utility class for running arbitrary JPA code
 *
 * @author David McKain
 */
public final class JpaRunner {

    public static void main(final String[] args) throws Exception {
        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(JpaProductionConfiguration.class, BaseServicesConfiguration.class, ServicesConfiguration.class);
        ctx.refresh();

        final AssessmentCandidateService assessmentCandidateService = ctx.getBean(AssessmentCandidateService.class);
        final RequestTimestampContext requestTimestampContext = ctx.getBean(RequestTimestampContext.class);
        requestTimestampContext.setCurrentRequestTimestamp(new Date());

        final InstructorUserDao instructorUserDao = ctx.getBean(InstructorUserDao.class);
        final InstructorUser dave = instructorUserDao.requireFindByLoginName("dmckain");
        final IdentityContext identityContext = ctx.getBean(IdentityContext.class);
        identityContext.setCurrentThreadEffectiveIdentity(dave);
        identityContext.setCurrentThreadUnderlyingIdentity(dave);

        final ItemDeliveryDao itemDeliveryDao = ctx.getBean(ItemDeliveryDao.class);
        final ItemDelivery itemDelivery = itemDeliveryDao.findById(4L); /* (choice.xml) */

        final CandidateItemSession candidateItemSession = assessmentCandidateService.createCandidateSession(itemDelivery);

        /* Render initial state */
        final RenderingOptions renderingOptions = new RenderingOptions();
        renderingOptions.setContextPath("/context");
        renderingOptions.setSerializationMethod(SerializationMethod.HTML5_MATHJAX);
        renderingOptions.setAttemptUrl("/attempt");
        renderingOptions.setExitUrl("/exit");
        renderingOptions.setResetUrl("/reset");
        renderingOptions.setSourceUrl("/source");
        renderingOptions.setResultUrl("/result");
        System.out.println("Rendering after init:\n" + assessmentCandidateService.renderCurrentState(candidateItemSession, renderingOptions));

        /* Do bad attempt == file submission */
        final CandidateFileSubmission fileSubmission = assessmentCandidateService.importFileResponse(candidateItemSession, new NullMultipartFile());
        final Map<Identifier, CandidateFileSubmission> fileResponseMap = new HashMap<Identifier, CandidateFileSubmission>();
        fileResponseMap.put(new Identifier("RESPONSE"), fileSubmission);
        assessmentCandidateService.handleAttempt(candidateItemSession, null, fileResponseMap);

        /* Do invalid attempt */
        final Map<Identifier, StringResponseData> stringResponseMap = new HashMap<Identifier, StringResponseData>();
        stringResponseMap.put(new Identifier("RESPONSE"), new StringResponseData("x"));
        assessmentCandidateService.handleAttempt(candidateItemSession, stringResponseMap, null);

        /* Then valid attempt */
        stringResponseMap.clear();
        stringResponseMap.put(new Identifier("RESPONSE"), new StringResponseData("ChoiceA"));
        assessmentCandidateService.handleAttempt(candidateItemSession, stringResponseMap, null);

        /* Render new state */
        System.out.println("Rendering after first proper attempt:\n" + assessmentCandidateService.renderCurrentState(candidateItemSession, renderingOptions));

        /* Then reset state */
        assessmentCandidateService.resetCandidateSession(candidateItemSession);

        /* Then end session */
        assessmentCandidateService.endCandidateSession(candidateItemSession);

        ctx.close();
    }
}
