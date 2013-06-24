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
package uk.ac.ed.ph.qtiworks.manager.services;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksDeploymentSettings;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackageImportType;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.SampleCategory;
import uk.ac.ed.ph.qtiworks.domain.entities.SystemUser;
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.samples.DeliveryStyle;
import uk.ac.ed.ph.qtiworks.samples.LanguageSampleSet;
import uk.ac.ed.ph.qtiworks.samples.MathAssessSampleSet;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment.Feature;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleSet;
import uk.ac.ed.ph.qtiworks.samples.StandardQtiSampleSet;
import uk.ac.ed.ph.qtiworks.samples.StompSampleSet;
import uk.ac.ed.ph.qtiworks.samples.TestImplementationSampleSet;
import uk.ac.ed.ph.qtiworks.samples.UpmcSampleSet;
import uk.ac.ed.ph.qtiworks.services.AssessmentPackageFileService;
import uk.ac.ed.ph.qtiworks.services.DataDeletionService;
import uk.ac.ed.ph.qtiworks.services.base.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliverySettingsDao;
import uk.ac.ed.ph.qtiworks.services.dao.SampleCategoryDao;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentAndPackage;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class SampleResourceImporter {

    private static final Logger logger = LoggerFactory.getLogger(SampleResourceImporter.class);

    public static final String DEFAULT_IMPORT_TITLE = "QTI Sample";

    @Resource
    private QtiWorksDeploymentSettings qtiWorksDeploymentSettings;

    @Resource
    private AssessmentPackageFileService assessmentPackageFileService;

    @Resource
    private ManagerServices managerServices;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private SampleCategoryDao sampleCategoryDao;

    @Resource
    private AssessmentPackageDao assessmentPackageDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private DeliverySettingsDao deliverySettingsDao;

    @Resource
    private DataDeletionService dataDeletionService;

    //-------------------------------------------------

    /**
     * Wipes then re-imports the QTI samples. Any data collected from existing samples will
     * be deleted in the process.
     */
    public void reimportQtiSamples() {
        /* Get sample owner, creating if required */
        final SystemUser sampleOwner = ensureSampleOwner();

        /* Reset user (which will delete all existing samples) */
    	dataDeletionService.resetUser(sampleOwner);

    	/* Then import samples */
    	doUpdateQtiSamples(sampleOwner);
    }

    /**
     * Imports any (valid) QTI samples that are not already registered in the DB.
     * This creates a user to own these samples if this hasn't been done so already.
     */
    public void updateQtiSamples() {
        /* Get sample owner, creating if required */
        final SystemUser sampleOwner = ensureSampleOwner();

        /* Then update samples */
        doUpdateQtiSamples(sampleOwner);
    }

    //-------------------------------------------------

    private void doUpdateQtiSamples(final SystemUser sampleOwner) {
        /* Set up sample DeliverySettings */
        final Map<DeliveryStyle, DeliverySettings> deliverySettingsMap = importDeliverySettings(sampleOwner);

        /* Find out which SampleCategories already exist */
        final List<SampleCategory> sampleCategories = getExistingSampleCategories();

        /* Find out what sample Assessments are already loaded in the DB */
        final Map<String, Assessment> importedSampleAssessments = getImportedSampleAssessments(sampleOwner);
        logger.debug("Existing samples are {}", importedSampleAssessments);

        /* Pick out all of the valid samples */
        final QtiSampleSet[] qtiSampleSets = new QtiSampleSet[] {
                StandardQtiSampleSet.instance().withoutFeatures(Feature.NOT_FULLY_VALID, Feature.NOT_RUNNABLE),
                MathAssessSampleSet.instance().withoutFeatures(Feature.NOT_FULLY_VALID, Feature.NOT_RUNNABLE),
                UpmcSampleSet.instance().withoutFeatures(Feature.NOT_FULLY_VALID, Feature.NOT_RUNNABLE),
                StompSampleSet.instance().withoutFeatures(Feature.NOT_FULLY_VALID, Feature.NOT_RUNNABLE),
                LanguageSampleSet.instance().withoutFeatures(Feature.NOT_FULLY_VALID, Feature.NOT_RUNNABLE),
                TestImplementationSampleSet.instance().withoutFeatures(Feature.NOT_FULLY_VALID, Feature.NOT_RUNNABLE)
        };

        /* If MathAssess extensions are not enabled, filter out assessments that need them */
        if (!qtiWorksDeploymentSettings.isEnableMathAssessExtension()) {
            for (int i=0; i<qtiSampleSets.length; i++) {
                qtiSampleSets[i] = qtiSampleSets[i].withoutFeatures(Feature.REQUIRES_MATHASSES);
            }
        }

        /* Now import assessments (if not done already) */
        for (final QtiSampleSet qtiSampleSet : qtiSampleSets) {
            final int importCount = handleSampleSet(sampleOwner, qtiSampleSet, sampleCategories, importedSampleAssessments, deliverySettingsMap);
            if (importCount>0) {
            	logger.info("Imported {} sample(s) into set '{}'", importCount, qtiSampleSet.getTitle());
            }
        }
    }

    private SystemUser ensureSampleOwner() {
        return managerServices.ensureInternalSystemUser(UserRole.INSTRUCTOR, DomainConstants.QTI_SAMPLE_OWNER_LOGIN_NAME,
                DomainConstants.QTI_SAMPLE_OWNER_FIRST_NAME, DomainConstants.QTI_SAMPLE_OWNER_LAST_NAME);
    }

    private List<SampleCategory> getExistingSampleCategories() {
        return sampleCategoryDao.getAll();
    }

    private Map<DeliveryStyle, DeliverySettings> importDeliverySettings(final SystemUser sampleOwner) {
        final Map<String, DeliverySettings> deliverySettingsByTitleMap = new HashMap<String, DeliverySettings>();
        for (final DeliverySettings existingOptions : deliverySettingsDao.getForOwner(sampleOwner)) {
            deliverySettingsByTitleMap.put(existingOptions.getTitle(), existingOptions);
        }

        /* Go through all sample options, persisting any that aren't yet in the DB.
         * NB: This does not update existing options!
         */
        final Map<DeliveryStyle, DeliverySettings> deliverySettingsMap = buildDeliverySettingsMap();
        for (final DeliverySettings options : deliverySettingsMap.values()) {
            if (!deliverySettingsByTitleMap.containsKey(options.getTitle())) {
                /* New options */
                options.setOwner(sampleOwner);
                options.setPublic(true);
                deliverySettingsDao.persist(options);
                logger.debug("Created ItemDeliverySettings {}", options);
                deliverySettingsByTitleMap.put(options.getTitle(), options);
            }
        }
        /* Now convert back to Map keyed on DeliveryStyle. */
        final Map<DeliveryStyle, DeliverySettings> result = new HashMap<DeliveryStyle, DeliverySettings>();
        for (final DeliveryStyle deliveryStyle : DeliveryStyle.values()) {
            result.put(deliveryStyle, deliverySettingsByTitleMap.get(deliverySettingsMap.get(deliveryStyle).getTitle()));
        }
        return result;
    }


    private Map<DeliveryStyle, DeliverySettings> buildDeliverySettingsMap() {
        final Map<DeliveryStyle, DeliverySettings> result = new HashMap<DeliveryStyle, DeliverySettings>();
        /* Yes, the next bit is not efficient but it's readable! */
        for (final DeliveryStyle deliveryStyle : DeliveryStyle.values()) {
            DeliverySettings deliverySettings;
            switch (deliveryStyle) {
                case MATHASSESS_STANDARD: {
                    final ItemDeliverySettings itemDeliverySettings = createBaseItemDeliverySettings();
                    itemDeliverySettings.setTitle("MathAssess standard");
                    itemDeliverySettings.setPrompt("This is a typical MathAssess item. It has rich feedback, so this demo lets you try "
                            + "the item as many times as you like. There is no template processing (randomisation) in this "
                            + "example.");
                    itemDeliverySettings.setAllowEnd(true);
                    itemDeliverySettings.setAllowSoftResetWhenOpen(true);
                    itemDeliverySettings.setAllowSoftResetWhenEnded(true);
                    deliverySettings = itemDeliverySettings;
                    break;
                }

                case MATHASSESS_TEMPLATED: {
                    final ItemDeliverySettings itemDeliverySettings = createBaseItemDeliverySettings();
                    itemDeliverySettings.setTitle("MathAssess templated");
                    itemDeliverySettings.setPrompt("This is a typical MathAssess item. It has rich feedback, so this demo lets you try "
                            + "the item as many times as you like. This item contains template processing (randomisation), "
                            + "so we have provided a button to let you reinitialise your session with a freshly generated instance of the item");
                    itemDeliverySettings.setAllowEnd(true);
                    itemDeliverySettings.setAllowHardResetWhenOpen(true);
                    itemDeliverySettings.setAllowHardResetWhenEnded(true);
                    itemDeliverySettings.setAllowSoftResetWhenOpen(true);
                    itemDeliverySettings.setAllowSoftResetWhenEnded(true);
                    deliverySettings = itemDeliverySettings;
                    break;
                }

                case IMS_ADAPTIVE: {
                    final ItemDeliverySettings itemDeliverySettings = createBaseItemDeliverySettings();
                    itemDeliverySettings.setTitle("IMS adaptive");
                    itemDeliverySettings.setPrompt("This is an adaptive item, potentially involving more than one step to complete. "
                            + "This example lets you try this out as many times as you like, and also reset the question "
                            + "if you want to start again. A model solution is provided.");
                    itemDeliverySettings.setMaxAttempts(Integer.valueOf(1));
                    itemDeliverySettings.setAllowEnd(true);
                    itemDeliverySettings.setAllowSoftResetWhenOpen(true);
                    itemDeliverySettings.setAllowSoftResetWhenEnded(true);
                    itemDeliverySettings.setAllowSolutionWhenOpen(true);
                    itemDeliverySettings.setAllowSolutionWhenEnded(true);
                    deliverySettings = itemDeliverySettings;
                    break;
                }

                case IMS_STANDARD: {
                    final ItemDeliverySettings itemDeliverySettings = createBaseItemDeliverySettings();
                    itemDeliverySettings.setTitle("IMS standard");
                    itemDeliverySettings.setPrompt("This is a typical standard IMS sample question. It has a model solution, but no "
                            + "feedback. In this example, we'll let you give you one attempt at the question before it "
                            + "closes. You can still reset the question and try it again, though.");
                    itemDeliverySettings.setMaxAttempts(Integer.valueOf(1));
                    itemDeliverySettings.setAllowEnd(true);
                    itemDeliverySettings.setAllowSoftResetWhenEnded(true);
                    itemDeliverySettings.setAllowSolutionWhenOpen(true);
                    itemDeliverySettings.setAllowSolutionWhenEnded(true);
                    deliverySettings = itemDeliverySettings;
                    break;
                }

                case IMS_FEEDBACK: {
                    final ItemDeliverySettings itemDeliverySettings = createBaseItemDeliverySettings();
                    itemDeliverySettings.setTitle("IMS feedback");
                    itemDeliverySettings.setPrompt("This is a typical standard IMS sample question. It has a model solution... "
                            + "and this one also has feedback. In this example, we'll let you make as many attempts as you like. "
                            + "You can still reset the question and try it again, though.");
                    itemDeliverySettings.setAllowEnd(true);
                    itemDeliverySettings.setAllowSoftResetWhenOpen(true);
                    itemDeliverySettings.setAllowSoftResetWhenEnded(true);
                    itemDeliverySettings.setAllowSolutionWhenOpen(true);
                    itemDeliverySettings.setAllowSolutionWhenEnded(true);
                    deliverySettings = itemDeliverySettings;
                    break;
                }

                case IMS_NO_RESPONSE_PROCESSING: {
                    final ItemDeliverySettings itemDeliverySettings = createBaseItemDeliverySettings();
                    itemDeliverySettings.setTitle("IMS no response processing");
                    itemDeliverySettings.setPrompt("This is a very basic standard IMS sample question. It has no response processing (scoring) "
                            + "built in so isn't very interactve. There is also no model solution. It's therefore not much fun "
                            + "to play around with!");
                    itemDeliverySettings.setAllowSoftResetWhenOpen(true);
                    deliverySettings = itemDeliverySettings;
                    break;
                }

                case IMS_STANDARD_TEMPLATED: {
                    final ItemDeliverySettings itemDeliverySettings = createBaseItemDeliverySettings();
                    itemDeliverySettings.setTitle("IMS standard templated");
                    itemDeliverySettings.setPrompt("This is a standard IMS sample question demonstrating template processing (randomisation). "
                            + "It has no model solution and no feedback, so it's fun to render but not much fun to actually try out. "
                            + "Try playing with the 'Reinitialize' option to generate the question with different values");
                    itemDeliverySettings.setAllowSoftResetWhenOpen(true);
                    itemDeliverySettings.setAllowHardResetWhenOpen(true);
                    deliverySettings = itemDeliverySettings;
                    break;
                }

                case LANGUAGE_STANDARD: {
                    final ItemDeliverySettings itemDeliverySettings = createBaseItemDeliverySettings();
                    itemDeliverySettings.setTitle("Language standard");
                    itemDeliverySettings.setPrompt("This question has a model solution and feedback. In this example, we'll let you try "
                            + "the item once and see the result. You can always reset it and try again afterwards if you like.");
                    itemDeliverySettings.setMaxAttempts(Integer.valueOf(1));
                    itemDeliverySettings.setAllowSoftResetWhenOpen(true);
                    itemDeliverySettings.setAllowSolutionWhenOpen(true);
                    deliverySettings = itemDeliverySettings;
                    break;
                }

                case TEST_WORK_IN_PROGRESS: {
                    final TestDeliverySettings testDeliverySettings = createBaseTestDeliverySettings();
                    testDeliverySettings.setTitle("Test standard");
                    deliverySettings = testDeliverySettings;
                    break;
                }

                default:
                    throw new QtiWorksLogicException("No DeliverySettings registered for style " + deliveryStyle);
            }
            result.put(deliveryStyle, deliverySettings);
        }
        return result;
    }

    private ItemDeliverySettings createBaseItemDeliverySettings() {
        final ItemDeliverySettings settings = new ItemDeliverySettings();
        settings.setMaxAttempts(Integer.valueOf(0));
        settings.setAuthorMode(true);
        return settings;
    }

    private TestDeliverySettings createBaseTestDeliverySettings() {
        final TestDeliverySettings settings = new TestDeliverySettings();
        settings.setAuthorMode(true);
        return settings;
    }

    private Map<String, Assessment> getImportedSampleAssessments(final SystemUser sampleOwner) {
        final List<AssessmentAndPackage> samples = assessmentDao.getForOwner(sampleOwner);
        final Map<String, Assessment> result = new HashMap<String, Assessment>();
        for (final AssessmentAndPackage sample : samples) {
            final AssessmentPackage assessmentPackage = sample.getAssessmentPackage();
            if (assessmentPackage==null) {
                throw new QtiWorksLogicException("Sample assessment " + sample + " has no current AssessmentPackage");
            }
            result.put(assessmentPackage.getAssessmentHref(), sample.getAssessment());
        }
        return result;
    }

    private int handleSampleSet(final SystemUser sampleOwner,
            final QtiSampleSet qtiSampleSet, final List<SampleCategory> existingSampleCategories,
            final Map<String, Assessment> importedSampleAssessments,
            final Map<DeliveryStyle, DeliverySettings> deliverySettingsMap) {
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
            resultingSampleCategory.setDescription(qtiSampleSet.getDescription());
            logger.debug("Creating new sample category {}", sampleCategoryTitle);
            sampleCategoryDao.persist(resultingSampleCategory);

        }
        int importCount = 0;
        for (final QtiSampleAssessment qtiSampleAssessment : qtiSampleSet.getQtiSampleAssessments()) {
            if (!importedSampleAssessments.containsKey(qtiSampleAssessment.getAssessmentHref())) {
                importSampleAssessment(sampleOwner, qtiSampleAssessment, resultingSampleCategory, deliverySettingsMap);
                importCount++;
            }
        }
        return importCount;
    }

    private Assessment importSampleAssessment(final SystemUser owner,
            final QtiSampleAssessment qtiSampleAssessment, final SampleCategory sampleCategory,
            final Map<DeliveryStyle, DeliverySettings> deliverySettingsMap) {
        Assert.notNull(qtiSampleAssessment, "qtiSampleAssessment");
        logger.debug("Importing QTI sample {}", qtiSampleAssessment);

        /* Create AssessmentPackage entity */
        final AssessmentPackage assessmentPackage = new AssessmentPackage();
        assessmentPackage.setImportVersion(Long.valueOf(1L));
        assessmentPackage.setAssessmentType(qtiSampleAssessment.getType());
        assessmentPackage.setAssessmentHref(qtiSampleAssessment.getAssessmentHref());
        assessmentPackage.setQtiFileHrefs(new HashSet<String>(Arrays.asList(qtiSampleAssessment.getAssessmentHref())));
        assessmentPackage.setSafeFileHrefs(qtiSampleAssessment.getFileHrefs());
        assessmentPackage.setImportType(AssessmentPackageImportType.BUNDLED_SAMPLE);
        assessmentPackage.setSandboxPath(null);
        assessmentPackage.setImporter(owner);
        assessmentPackage.setValidated(true);
        /* (We're only picking valid samples!) */
        assessmentPackage.setValid(true);
        assessmentPackage.setLaunchable(true);
        assessmentPackage.setErrorCount(0);
        assessmentPackage.setWarningCount(0);
        assessmentPackage.setImportVersion(Long.valueOf(1L));
        assessmentPackageDao.persist(assessmentPackage);

        /* Create owning Assessment entity */
        final Assessment assessment = new Assessment();
        assessment.setAssessmentType(assessmentPackage.getAssessmentType());
        assessment.setOwner(owner);
        assessment.setPublic(true);
        assessment.setSelectedAssessmentPackage(assessmentPackage);
        assessment.setPackageImportVersion(Long.valueOf(1L));
        assessment.setSampleCategory(sampleCategory);

        /* We'll use last part of href as assessment name.
         * This works OK as all of our samples are of the form set/name.xml */
        final String assessmentHref = qtiSampleAssessment.getAssessmentHref();
        final String assessmentName = assessmentHref.replaceFirst("^.+/", "");
        assessment.setName(ServiceUtilities.trimString(assessmentName, DomainConstants.ASSESSMENT_NAME_MAX_LENGTH));

        /* Guess a title */
        final String guessedTitle = assessmentPackageFileService.guessAssessmentTitle(assessmentPackage);
        final String resultingTitle = !StringUtilities.isNullOrEmpty(guessedTitle) ? guessedTitle : DEFAULT_IMPORT_TITLE;
        assessment.setTitle(ServiceUtilities.trimSentence(resultingTitle, DomainConstants.ASSESSMENT_TITLE_MAX_LENGTH));

        /* Persist/relate entities */
        assessmentPackage.setAssessment(assessment);
        assessmentDao.persist(assessment);
        assessmentPackageDao.update(assessmentPackage);

        /* Create default delivery settings */
        final DeliverySettings deliverySettings = deliverySettingsMap.get(qtiSampleAssessment.getDeliveryStyle());
        assessment.setDefaultDeliverySettings(deliverySettings);
        assessmentDao.update(assessment);

        /* Create a Delivery using these options (if there isn't one already) */
        final List<Delivery> demoDeliveries = deliveryDao.getForAssessmentAndType(assessment, DeliveryType.SYSTEM_DEMO);
        if (demoDeliveries.isEmpty()) {
            final Delivery defaultDelivery = new Delivery();
            defaultDelivery.setAssessment(assessment);
            defaultDelivery.setDeliveryType(DeliveryType.SYSTEM_DEMO);
            defaultDelivery.setDeliverySettings(deliverySettings);
            defaultDelivery.setOpen(true);
            defaultDelivery.setTitle("System demo delivery");
            deliveryDao.persist(defaultDelivery);
        }
        return assessment;
    }
}
