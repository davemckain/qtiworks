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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.domain.Privilege;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.services.domain.ItemDeliverySettingsTemplate;
import uk.ac.ed.ph.qtiworks.services.domain.TestDeliverySettingsTemplate;

import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bottom layer service providing basic data operations on {@link Assessment} and related
 * entities.
 * <p>
 * This is NO checking of {@link Privilege}s at this level.
 *
 * FIXME: Merge this with {@link EntityGraphService}
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class AssessmentDataService {

    public ItemDeliverySettingsTemplate createItemDeliverySettingsTemplate() {
        final ItemDeliverySettingsTemplate template = new ItemDeliverySettingsTemplate();
        template.setTitle("Item Delivery Settings");
        template.setAllowEnd(true);
        template.setAllowHardResetWhenEnded(true);
        template.setAllowHardResetWhenOpen(true);
        template.setAllowSoftResetWhenEnded(true);
        template.setAllowSoftResetWhenOpen(true);
        template.setAllowSolutionWhenEnded(true);
        template.setAllowSolutionWhenOpen(true);
        template.setMaxAttempts(0);
        template.setPrompt(null);
        return template;
    }

    public TestDeliverySettingsTemplate createTestDeliverySettingsTemplate() {
        final TestDeliverySettingsTemplate template = new TestDeliverySettingsTemplate();
        template.setTitle("Test Delivery Settings");
        return template;
    }

    public DeliverySettings createDefaultDeliverySettings(final User assessmentRunner, final AssessmentObjectType assessmentType) {
        switch (assessmentType) {
            case ASSESSMENT_ITEM: {
                final ItemDeliverySettingsTemplate template = createItemDeliverySettingsTemplate();
                final ItemDeliverySettings itemDeliverySettings = new ItemDeliverySettings();
                mergeItemDeliverySettings(template, itemDeliverySettings);
                itemDeliverySettings.setTitle("Default item delivery settings");
                if (assessmentRunner.getUserRole()==UserRole.INSTRUCTOR) {
                    itemDeliverySettings.setPrompt("This assessment item is being delivered using a set of. default 'delivery settings'."
                            + " You probably want to create and use your own settings here.");
                }
                else {
                    itemDeliverySettings.setPrompt("This assessment item is being delivered using a set of default 'delivery settings'."
                            + " You can create and use your settings when logged into QTIWorks"
                            + " via its LTI instructor connector or with an explicit QTIWorks system account.");
                }
                return itemDeliverySettings;
            }

            case ASSESSMENT_TEST: {
                final TestDeliverySettingsTemplate template = createTestDeliverySettingsTemplate();
                final TestDeliverySettings testDeliverySettings = new TestDeliverySettings();
                mergeTestDeliverySettings(template, testDeliverySettings);
                testDeliverySettings.setTitle("Default test delivery settings");

                return testDeliverySettings;
            }

            default:
                throw new QtiLogicException("Unexpected switch case " + assessmentType);
        }
    }

    public void mergeItemDeliverySettings(final ItemDeliverySettingsTemplate template, final ItemDeliverySettings target) {
        target.setTitle(template.getTitle().trim());
        target.setTemplateProcessingLimit(template.getTemplateProcessingLimit());
        target.setAllowEnd(template.isAllowEnd());
        target.setAllowHardResetWhenEnded(template.isAllowHardResetWhenEnded());
        target.setAllowHardResetWhenOpen(template.isAllowHardResetWhenOpen());
        target.setAllowSoftResetWhenEnded(template.isAllowSoftResetWhenEnded());
        target.setAllowSoftResetWhenOpen(template.isAllowSoftResetWhenOpen());
        target.setAllowSolutionWhenEnded(template.isAllowSolutionWhenEnded());
        target.setAllowSolutionWhenOpen(template.isAllowSolutionWhenOpen());
        target.setAllowCandidateComment(template.isAllowCandidateComment());
        target.setMaxAttempts(template.getMaxAttempts());
        target.setPrompt(StringUtilities.nullIfEmpty(template.getPrompt()));
    }

    public void mergeItemDeliverySettings(final ItemDeliverySettings template, final ItemDeliverySettingsTemplate target) {
        target.setTitle(template.getTitle());
        target.setTemplateProcessingLimit(template.getTemplateProcessingLimit());
        target.setAllowEnd(template.isAllowEnd());
        target.setAllowHardResetWhenEnded(template.isAllowHardResetWhenEnded());
        target.setAllowHardResetWhenOpen(template.isAllowHardResetWhenOpen());
        target.setAllowSoftResetWhenEnded(template.isAllowSoftResetWhenEnded());
        target.setAllowSoftResetWhenOpen(template.isAllowSoftResetWhenOpen());
        target.setAllowSolutionWhenEnded(template.isAllowSolutionWhenEnded());
        target.setAllowSolutionWhenOpen(template.isAllowSolutionWhenOpen());
        target.setAllowCandidateComment(template.isAllowCandidateComment());
        target.setMaxAttempts(template.getMaxAttempts());
        target.setPrompt(StringUtilities.nullIfEmpty(template.getPrompt()));
    }

    public void mergeTestDeliverySettings(final TestDeliverySettingsTemplate template, final TestDeliverySettings target) {
        target.setTemplateProcessingLimit(template.getTemplateProcessingLimit());
        target.setTitle(template.getTitle().trim());
    }

    public void mergeTestDeliverySettings(final TestDeliverySettings template, final TestDeliverySettingsTemplate target) {
        target.setTemplateProcessingLimit(template.getTemplateProcessingLimit());
        target.setTitle(template.getTitle());
    }

    public DeliverySettings getEffectiveDeliverySettings(final User candidate, final Delivery delivery) {
        Assert.notNull(candidate, "candidate");
        Assert.notNull(delivery, "delivery");
        DeliverySettings result = delivery.getDeliverySettings();
        if (result==null) {
            result = createDefaultDeliverySettings(candidate, delivery.getAssessment().getAssessmentType());
        }
        return result;
    }
}
