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

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.base.services.QtiWorksSettings;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.domain.dao.InstructorUserDao;
import uk.ac.ed.ph.qtiworks.domain.dao.ItemDeliveryDao;
import uk.ac.ed.ph.qtiworks.domain.dao.SampleCategoryDao;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackageImportType;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.SampleCategory;
import uk.ac.ed.ph.qtiworks.samples.LanguageSampleSet;
import uk.ac.ed.ph.qtiworks.samples.MathAssessSampleSet;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment.Feature;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleCollection;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleSet;
import uk.ac.ed.ph.qtiworks.samples.StandardQtiSampleSet;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private SampleCategoryDao sampleCategoryDao;

    @Resource
    private AssessmentPackageDao assessmentPackageDao;

    @Resource
    private ItemDeliveryDao itemDeliveryDao;

    @Resource
    private InstructorUserDao instructorUserDao;

    @Resource
    private QtiWorksSettings qtiWorksSettings;

    //-------------------------------------------------

    /**
     * Imports any (valid) QTI samples that are not already registered in the DB.
     * This creates a user to own these samples if this hasn't been done so already.
     */
    public void importQtiSamples() {
        /* Create sample owner if required */
        final InstructorUser sampleOwner = importSampleOwnerUserIfRequired();

        /* Find out which SampleCategories already exist */
        final List<SampleCategory> sampleCategories = getExistingSampleCategories();

        /* Find out what sample Assessments are already loaded in the DB */
        final Map<String, Assessment> importedSampleAssessments = getImportedSampleAssessments(sampleOwner);
        logger.info("Existing samples are {}", importedSampleAssessments);

        final QtiSampleCollection qtiSampleCollection = new QtiSampleCollection(
                StandardQtiSampleSet.instance().withoutFeature(Feature.NOT_FULLY_VALID),
                MathAssessSampleSet.instance().withoutFeature(Feature.NOT_FULLY_VALID),
                LanguageSampleSet.instance().withoutFeature(Feature.NOT_FULLY_VALID)
        );
        for (final QtiSampleSet qtiSampleSet : qtiSampleCollection) {
            importSampleSet(sampleOwner, qtiSampleSet, sampleCategories, importedSampleAssessments);
        }
    }

    private InstructorUser importSampleOwnerUserIfRequired() {
        InstructorUser sampleOwner = instructorUserDao.findByLoginName(DomainConstants.QTI_SAMPLE_OWNER_LOGIN_NAME);
        if (sampleOwner==null) {
            sampleOwner = new InstructorUser();
            sampleOwner.setLoginName(DomainConstants.QTI_SAMPLE_OWNER_LOGIN_NAME);
            sampleOwner.setFirstName(DomainConstants.QTI_SAMPLE_OWNER_FIRST_NAME);
            sampleOwner.setLastName(DomainConstants.QTI_SAMPLE_OWNER_LAST_NAME);
            sampleOwner.setEmailAddress(qtiWorksSettings.getEmailAdminAddress());
            sampleOwner.setPasswordDigest(ServiceUtilities.computePasswordDigest("Doesn't matter as login to this account is disabled"));
            sampleOwner.setLoginDisabled(true);
            sampleOwner.setSysAdmin(false);
            instructorUserDao.persist(sampleOwner);
            logger.info("Created User {} to own the sample assessments", sampleOwner);
        }
        return sampleOwner;
    }

    private List<SampleCategory> getExistingSampleCategories() {
        return sampleCategoryDao.getAll();
    }

    private Map<String, Assessment> getImportedSampleAssessments(final InstructorUser sampleOwner) {
        final List<Assessment> sampleAssessments = assessmentDao.getForOwner(sampleOwner);
        final Map<String, Assessment> result = new HashMap<String, Assessment>();
        for (final Assessment sampleAssessment : sampleAssessments) {
            final AssessmentPackage assessmentPackage = assessmentPackageDao.getCurrentAssessmentPackage(sampleAssessment);
            if (assessmentPackage==null) {
                throw new QtiWorksLogicException("Sample assessment " + sampleAssessment + " has no current AssessmentPackage");
            }
            result.put(assessmentPackage.getAssessmentHref(), sampleAssessment);
        }
        return result;
    }

    private void importSampleSet(final InstructorUser sampleOwner,
            final QtiSampleSet qtiSampleSet, final List<SampleCategory> existingSampleCategories,
            final Map<String, Assessment> importedSampleAssessments) {
        final String sampleCategoryTitle = qtiSampleSet.getTitle();
        SampleCategory resultingSampleCategory = null;
        for (final SampleCategory sampleCategory : existingSampleCategories) {
            if (sampleCategory.getTitle().equals(sampleCategoryTitle)) {
                resultingSampleCategory = sampleCategory;
                break;
            }
        }
        if (resultingSampleCategory==null) {
            /* Category doesn't exist yet */
            resultingSampleCategory = new SampleCategory();
            resultingSampleCategory.setTitle(sampleCategoryTitle);
            logger.info("Creating new sample category {}", sampleCategoryTitle);
            sampleCategoryDao.persist(resultingSampleCategory);

        }
        for (final QtiSampleAssessment qtiSampleAssessment : qtiSampleSet.getQtiSampleAssessments()) {
            if (!importedSampleAssessments.containsKey(qtiSampleAssessment.getAssessmentHref())) {
                importSampleAssessment(sampleOwner, qtiSampleAssessment, resultingSampleCategory);
            }
        }
    }

    private Assessment importSampleAssessment(final InstructorUser owner,
            final QtiSampleAssessment qtiSampleAssessment, final SampleCategory sampleCategory) {
        Assert.ensureNotNull(qtiSampleAssessment, "qtiSampleAssessment");
        logger.info("Importing QTI sample {}", qtiSampleAssessment);

        /* Create AssessmentPackage entity */
        final AssessmentPackage assessmentPackage = new AssessmentPackage();
        assessmentPackage.setAssessmentType(qtiSampleAssessment.getType());
        assessmentPackage.setAssessmentHref(qtiSampleAssessment.getAssessmentHref());
        assessmentPackage.setFileHrefs(qtiSampleAssessment.getFileHrefs());
        assessmentPackage.setImportType(AssessmentPackageImportType.BUNDLED_SAMPLE);
        assessmentPackage.setSandboxPath(null);
        assessmentPackage.setImporter(owner);
        assessmentPackage.setValidated(true);
        assessmentPackage.setValid(true); /* (We're only picking valid samples!) */

        /* Create owning Assessment entity */
        final Assessment assessment = new Assessment();
        assessment.setAssessmentType(assessmentPackage.getAssessmentType());
        assessment.setOwner(owner);
        assessment.setPublic(true);
        assessment.setSampleCategory(sampleCategory);

        /* FIXME: This is not great! */
        assessment.setName(ServiceUtilities.trimString(qtiSampleAssessment.getAssessmentHref(), DomainConstants.ASSESSMENT_NAME_MAX_LENGTH));

        /* Guess a title */
        final String guessedTitle = assessmentManagementService.guessAssessmentTitle(assessmentPackage);
        final String resultingTitle = !StringUtilities.isNullOrEmpty(guessedTitle) ? guessedTitle : DEFAULT_IMPORT_TITLE;
        assessment.setTitle(ServiceUtilities.trimSentence(resultingTitle, DomainConstants.ASSESSMENT_TITLE_MAX_LENGTH));

        /* Relate Assessment & AssessmentPackage */
        assessmentPackage.setAssessment(assessment);
        assessmentPackage.setImportVersion(Long.valueOf(1L));
        assessment.setPackageImportVersion(Long.valueOf(1L));

        /* Persist entities */
        assessmentDao.persist(assessment);
        assessmentPackageDao.persist(assessmentPackage);

        /* Create a default delivery */
        if (assessment.getAssessmentType()==AssessmentObjectType.ASSESSMENT_ITEM) {
            final ItemDelivery defaultDelivery = new ItemDelivery();
            defaultDelivery.setAssessmentPackage(assessmentPackage);
            defaultDelivery.setMaxAttempts(Integer.valueOf(0));
            defaultDelivery.setOpen(true);
            defaultDelivery.setTitle("Temporary default bootstrap delivery");
            defaultDelivery.setAllowClose(true);
            defaultDelivery.setAllowResetWhenInteracting(true);
            defaultDelivery.setAllowResetWhenClosed(true);
            defaultDelivery.setAllowReinitWhenInteracting(true);
            defaultDelivery.setAllowReinitWhenInteracting(true);
            defaultDelivery.setAllowSolutionWhenInteracting(true);
            defaultDelivery.setAllowSolutionWhenClosed(true);
            defaultDelivery.setAllowPlayback(true);
            defaultDelivery.setAllowResult(true);
            defaultDelivery.setAllowSource(true);
            assessmentPackage.setDefaultDelivery(defaultDelivery);
            itemDeliveryDao.persist(defaultDelivery);
            assessmentPackageDao.update(assessmentPackage);
        }

        return assessment;
    }
}
