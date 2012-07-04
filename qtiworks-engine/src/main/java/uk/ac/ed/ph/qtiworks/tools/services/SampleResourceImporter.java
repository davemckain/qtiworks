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
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.domain.dao.ItemDeliveryDao;
import uk.ac.ed.ph.qtiworks.domain.dao.ItemDeliverySettingsDao;
import uk.ac.ed.ph.qtiworks.domain.dao.SampleCategoryDao;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackageImportType;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.SampleCategory;
import uk.ac.ed.ph.qtiworks.samples.DeliveryStyle;
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
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
@Service
public class SampleResourceImporter {

    private static final Logger logger = LoggerFactory.getLogger(SampleResourceImporter.class);

    public static final String DEFAULT_IMPORT_TITLE = "QTI Sample";

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private BootstrapServices bootstrapServices;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private SampleCategoryDao sampleCategoryDao;

    @Resource
    private AssessmentPackageDao assessmentPackageDao;

    @Resource
    private ItemDeliveryDao itemDeliveryDao;

    @Resource
    private ItemDeliverySettingsDao itemDeliverySettingsDao;

    //-------------------------------------------------

    /**
     * Imports any (valid) QTI samples that are not already registered in the DB.
     * This creates a user to own these samples if this hasn't been done so already.
     */
    public void importQtiSamples() {
        /* Create sample owner if required */
        final InstructorUser sampleOwner = bootstrapServices.createInternalSystemUser(DomainConstants.QTI_SAMPLE_OWNER_LOGIN_NAME,
                DomainConstants.QTI_SAMPLE_OWNER_FIRST_NAME, DomainConstants.QTI_SAMPLE_OWNER_LAST_NAME);

        /* Set up sample ItemDeliverySettings */
        final Map<DeliveryStyle, ItemDeliverySettings> itemDeliverySettingsMap = importDeliverySettings(sampleOwner);

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
            importSampleSet(sampleOwner, qtiSampleSet, sampleCategories, importedSampleAssessments, itemDeliverySettingsMap);
        }
    }

    private List<SampleCategory> getExistingSampleCategories() {
        return sampleCategoryDao.getAll();
    }

    private Map<DeliveryStyle, ItemDeliverySettings> importDeliverySettings(final InstructorUser sampleOwner) {
        final Map<String, ItemDeliverySettings> deliverySettingsByTitleMap = new HashMap<String, ItemDeliverySettings>();
        for (final ItemDeliverySettings existingOptions : itemDeliverySettingsDao.getForOwner(sampleOwner)) {
            deliverySettingsByTitleMap.put(existingOptions.getTitle(), existingOptions);
        }

        /* Go through all sample options, persisting any that aren't yet in the DB.
         * NB: This does not update existing options!
         */
        final Map<DeliveryStyle, ItemDeliverySettings> deliverySettingsMap = buildDeliverySettingsMap();
        for (final ItemDeliverySettings options : deliverySettingsMap.values()) {
            if (!deliverySettingsByTitleMap.containsKey(options.getTitle())) {
                /* New options */
                options.setOwner(sampleOwner);
                options.setPublic(true);
                itemDeliverySettingsDao.persist(options);
                logger.info("Created ItemDeliverySettings {}", options);
                deliverySettingsByTitleMap.put(options.getTitle(), options);
            }
        }
        /* Now convert back to Map keyed on DeliveryStyle. */
        final Map<DeliveryStyle, ItemDeliverySettings> result = new HashMap<DeliveryStyle, ItemDeliverySettings>();
        for (final DeliveryStyle deliveryStyle : DeliveryStyle.values()) {
            result.put(deliveryStyle, deliverySettingsByTitleMap.get(deliverySettingsMap.get(deliveryStyle).getTitle()));
        }
        return result;
    }

    private Map<DeliveryStyle, ItemDeliverySettings> buildDeliverySettingsMap() {
        final Map<DeliveryStyle, ItemDeliverySettings> result = new HashMap<DeliveryStyle, ItemDeliverySettings>();
        /* Yes, the next bit is not efficient but it's readable! */
        for (final DeliveryStyle deliveryStyle : DeliveryStyle.values()) {
            final ItemDeliverySettings options = new ItemDeliverySettings();
            options.setMaxAttempts(Integer.valueOf(0));
            options.setAuthorMode(true);
            options.setAllowPlayback(true);
            options.setAllowResult(true);
            options.setAllowSource(true);
            options.setAllowPlayback(true);
            switch (deliveryStyle) {
                case MATHASSESS_STANDARD:
                    options.setTitle("MathAssess standard");
                    options.setPrompt("This is a typical MathAssess item. It has rich feedback, so this demo lets you try "
                            + "the item as many times as you like. There is no template processing (randomisation) in this "
                            + "example.");
                    options.setAllowClose(true);
                    options.setAllowResetWhenInteracting(true);
                    options.setAllowResetWhenClosed(true);
                    break;

                case MATHASSESS_TEMPLATED:
                    options.setTitle("MathAssess templated");
                    options.setPrompt("This is a typical MathAssess item. It has rich feedback, so this demo lets you try "
                            + "the item as many times as you like. This item contains template processing (randomisation), "
                            + "so we have provided a button to let you reinitialise your session with a freshly generated instance of the item");
                    options.setAllowClose(true);
                    options.setAllowReinitWhenInteracting(true);
                    options.setAllowReinitWhenClosed(true);
                    options.setAllowResetWhenInteracting(true);
                    options.setAllowResetWhenClosed(true);
                    break;

                case IMS_ADAPTIVE:
                    options.setTitle("IMS adaptive");
                    options.setPrompt("This is an adaptive item, potentially involving more than one step to complete. "
                            + "This example lets you try this out as many times as you like, and also reset the question "
                            + "if you want to start again. A model solution is provided.");
                    options.setMaxAttempts(Integer.valueOf(1));
                    options.setAllowClose(true);
                    options.setAllowResetWhenInteracting(true);
                    options.setAllowResetWhenClosed(true);
                    options.setAllowSolutionWhenInteracting(true);
                    options.setAllowSolutionWhenClosed(true);
                    break;

                case IMS_STANDARD:
                    options.setTitle("IMS standard");
                    options.setPrompt("This is a typical standard IMS sample question. It has a model solution, but no "
                            + "feedback. In this example, we'll let you give you one attempt at the question before it "
                            + "closes. You can still reset the question and try it again, though.");
                    options.setMaxAttempts(Integer.valueOf(1));
                    options.setAllowClose(true);
                    options.setAllowResetWhenClosed(true);
                    options.setAllowSolutionWhenInteracting(true);
                    options.setAllowSolutionWhenClosed(true);
                    break;

                case IMS_FEEDBACK:
                    options.setTitle("IMS feedback");
                    options.setPrompt("This is a typical standard IMS sample question. It has a model solution... "
                            + "and this one also has feedback. In this example, we'll let you make as many attempts as you like. "
                            + "You can still reset the question and try it again, though.");
                    options.setAllowClose(true);
                    options.setAllowResetWhenInteracting(true);
                    options.setAllowResetWhenClosed(true);
                    options.setAllowSolutionWhenInteracting(true);
                    options.setAllowSolutionWhenClosed(true);
                    break;

                case IMS_NO_RESPONSE_PROCESSING:
                    options.setTitle("IMS no response processing");
                    options.setPrompt("This is a very basic standard IMS sample question. It has no response processing (scoring) "
                            + "built in so isn't very interactve. There is also no model solution. It's therefore not much fun "
                            + "to play around with!");
                    options.setAllowResetWhenInteracting(true);
                    break;

                case IMS_STANDARD_TEMPLATED:
                    options.setTitle("IMS standard templated");
                    options.setPrompt("This is a standard IMS sample question demonstrating template processing (randomisation). "
                            + "It has no model solution and no feedback, so it's fun to render but not much fun to actually try out. "
                            + "Try playing with the 'Reinitialize' option to generate the question with different values");
                    options.setAllowResetWhenInteracting(true);
                    options.setAllowReinitWhenInteracting(true);
                    break;

                case LANGUAGE_STANDARD:
                    options.setTitle("Language standard");
                    options.setPrompt("This question has a model solution and feedback. In this example, we'll let you try "
                            + "the item once and see the result. You can always reset it and try again afterwards if you like.");
                    options.setMaxAttempts(Integer.valueOf(1));
                    options.setAllowResetWhenInteracting(true);
                    options.setAllowSolutionWhenInteracting(true);
                    break;

                default:
                    options.setTitle("Default");
                    options.setPrompt("This item hasn't been categoried, so we probably haven't presented it with the most "
                            + "useful set of options to show it in its best light!");
                    options.setAllowClose(true);
                    options.setAllowResetWhenInteracting(true);
                    options.setAllowResetWhenClosed(true);
                    options.setAllowReinitWhenInteracting(true);
                    options.setAllowReinitWhenInteracting(true);
                    options.setAllowSolutionWhenInteracting(true);
                    options.setAllowSolutionWhenClosed(true);
                    break;
            }
            result.put(deliveryStyle, options);
        }
        return result;
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
            final Map<String, Assessment> importedSampleAssessments,
            final Map<DeliveryStyle, ItemDeliverySettings> itemDeliverySettingsMap) {
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
                importSampleAssessment(sampleOwner, qtiSampleAssessment, resultingSampleCategory, itemDeliverySettingsMap);
            }
        }
    }

    private Assessment importSampleAssessment(final InstructorUser owner,
            final QtiSampleAssessment qtiSampleAssessment, final SampleCategory sampleCategory,
            final Map<DeliveryStyle, ItemDeliverySettings> itemDeliverySettingsMap) {
        Assert.ensureNotNull(qtiSampleAssessment, "qtiSampleAssessment");
        logger.info("Importing QTI sample {}", qtiSampleAssessment);

        /* Create AssessmentPackage entity */
        final AssessmentPackage assessmentPackage = new AssessmentPackage();
        assessmentPackage.setAssessmentType(qtiSampleAssessment.getType());
        assessmentPackage.setAssessmentHref(qtiSampleAssessment.getAssessmentHref());
        assessmentPackage.setQtiFileHrefs(new HashSet<String>(Arrays.asList(qtiSampleAssessment.getAssessmentHref())));
        assessmentPackage.setSafeFileHrefs(qtiSampleAssessment.getFileHrefs());
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

        /* We'll use last part of href as assessment name.
         * This works OK as all of our samples are of the form set/name.xml */
        final String assessmentHref = qtiSampleAssessment.getAssessmentHref();
        final String assessmentName = assessmentHref.replaceFirst("^.+/", "");
        assessment.setName(ServiceUtilities.trimString(assessmentName, DomainConstants.ASSESSMENT_NAME_MAX_LENGTH));

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

        if (assessment.getAssessmentType()==AssessmentObjectType.ASSESSMENT_ITEM) {
            /* Create default delivery settings */
            final ItemDeliverySettings itemDeliverySettings = itemDeliverySettingsMap.get(qtiSampleAssessment.getDeliveryStyle());
            assessment.setDefaultDeliverySettings(itemDeliverySettings);
            assessmentDao.update(assessment);

            /* Create a Delivery using these options (if there isn't one already) */
            final List<ItemDelivery> demoDeliveries = itemDeliveryDao.getForAssessmentPackageAndType(assessmentPackage, ItemDeliveryType.SYSTEM_DEMO);
            if (demoDeliveries.isEmpty()) {
                final ItemDelivery defaultDelivery = new ItemDelivery();
                defaultDelivery.setAssessmentPackage(assessmentPackage);
                defaultDelivery.setItemDeliveryType(ItemDeliveryType.SYSTEM_DEMO);
                defaultDelivery.setItemDeliverySettings(itemDeliverySettings);
                defaultDelivery.setOpen(true);
                defaultDelivery.setTitle("System demo delivery");
                itemDeliveryDao.persist(defaultDelivery);
            }
        }
        return assessment;
    }
}
