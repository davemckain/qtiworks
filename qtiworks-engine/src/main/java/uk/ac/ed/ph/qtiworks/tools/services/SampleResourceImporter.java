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
package uk.ac.ed.ph.qtiworks.tools.services;

import uk.ac.ed.ph.qtiworks.base.services.QtiWorksSettings;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.domain.dao.InstructorUserDao;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackageImportType;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.samples.LanguageSampleSet;
import uk.ac.ed.ph.qtiworks.samples.MathAssessSampleSet;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment.Feature;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleCollection;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleSet;
import uk.ac.ed.ph.qtiworks.samples.StandardQtiSampleSet;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementServices;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bootstrap service for importing the bundled QTI samples into the
 * domain model.
 *
 * @author David McKain
 */
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
@Service
public class SampleResourceImporter {

    private static final Logger logger = LoggerFactory.getLogger(SampleResourceImporter.class);

    public static final String DEFAULT_IMPORT_TITLE = "QTI Sample";

    @Resource
    private AssessmentManagementServices assessmentManagementServices;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private AssessmentPackageDao assessmentPackageDao;

    @Resource
    private InstructorUserDao instructorUserDao;

    @Resource
    private QtiWorksSettings qtiWorksSettings;

    //-------------------------------------------------

    public void importQtiSamples() {
        final InstructorUser sampleOwner = new InstructorUser();
        sampleOwner.setLoginName(DomainConstants.QTI_SAMPLE_OWNER_LOGIN_NAME);
        sampleOwner.setFirstName(DomainConstants.QTI_SAMPLE_OWNER_FIRST_NAME);
        sampleOwner.setLastName(DomainConstants.QTI_SAMPLE_OWNER_LAST_NAME);
        sampleOwner.setEmailAddress(qtiWorksSettings.getEmailAdminAddress());
        sampleOwner.setPasswordDigest(ServiceUtilities.computePasswordDigest("Doesn't matter as login to this account is disabled"));
        sampleOwner.setLoginDisabled(true);
        sampleOwner.setSysAdmin(false);
        instructorUserDao.persist(sampleOwner);

        final QtiSampleCollection qtiSampleCollection = new QtiSampleCollection(
                StandardQtiSampleSet.instance().withoutFeature(Feature.NOT_FULLY_VALID),
                MathAssessSampleSet.instance().withoutFeature(Feature.NOT_FULLY_VALID),
                LanguageSampleSet.instance().withoutFeature(Feature.NOT_FULLY_VALID)
        );
        for (final QtiSampleSet qtiSampleSet : qtiSampleCollection) {
            for (final QtiSampleAssessment qtiSampleAssessment : qtiSampleSet.getQtiSampleAssessments()) {
                importSampleAssessment(sampleOwner, qtiSampleAssessment);
            }
        }
    }

    private Assessment importSampleAssessment(final InstructorUser owner, final QtiSampleAssessment qtiSampleAssessment) {
        Assert.ensureNotNull(qtiSampleAssessment, "qtiSampleAssessment");

        /* Create AssessmentPackage entity */
        final AssessmentPackage assessmentPackage = new AssessmentPackage();
        assessmentPackage.setAssessmentType(qtiSampleAssessment.getType());
        assessmentPackage.setAssessmentHref(qtiSampleAssessment.getAssessmentHref());
        assessmentPackage.setFileHrefs(qtiSampleAssessment.getFileHrefs());
        assessmentPackage.setImportType(AssessmentPackageImportType.BUNDLED_SAMPLE);
        assessmentPackage.setSandboxPath(null);
        assessmentPackage.setImporter(owner);

        /* Create owning Assessment entity */
        final Assessment assessment = new Assessment();
        assessment.setAssessmentType(assessmentPackage.getAssessmentType());
        assessment.setOwner(owner);

        /* FIXME: This is not great! */
        assessment.setName(ServiceUtilities.trimString(qtiSampleAssessment.getAssessmentHref(), DomainConstants.ASSESSMENT_NAME_MAX_LENGTH));

        /* Guess a title */
        final String guessedTitle = assessmentManagementServices.guessAssessmentTitle(assessmentPackage);
        final String resultingTitle = !StringUtilities.isNullOrEmpty(guessedTitle) ? guessedTitle : DEFAULT_IMPORT_TITLE;
        assessment.setTitle(ServiceUtilities.trimSentence(resultingTitle, DomainConstants.ASSESSMENT_TITLE_MAX_LENGTH));

        /* Relate Assessment & AssessmentPackage */
        assessmentPackage.setAssessment(assessment);
        assessmentPackage.setImportVersion(Long.valueOf(1L));
        assessment.setPackageImportVersion(Long.valueOf(1L));

        /* Persist entities */
        assessmentDao.persist(assessment);
        assessmentPackageDao.persist(assessmentPackage);

        return assessment;
    }
}
