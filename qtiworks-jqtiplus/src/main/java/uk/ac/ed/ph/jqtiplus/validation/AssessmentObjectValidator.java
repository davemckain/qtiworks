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
package uk.ac.ed.ph.jqtiplus.validation;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.notification.Notification;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationType;
import uk.ac.ed.ph.jqtiplus.reading.AssessmentObjectXmlLoader;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup;

import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs model validation on a {@link ResolvedAssessmentItem} or {@link ResolvedAssessmentTest}.
 * <p>
 * You won't normally want to use this on its own. See {@link AssessmentObjectXmlLoader} for a more
 * end-to-end solution.
 *
 * @see AssessmentObjectXmlLoader
 *
 * @author David McKain
 */
public final class AssessmentObjectValidator {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentObjectValidator.class);

    private final JqtiExtensionManager jqtiExtensionManager;

    public AssessmentObjectValidator(final JqtiExtensionManager jqtiExtensionManager) {
        this.jqtiExtensionManager = jqtiExtensionManager;
    }

    public ItemValidationResult validateItem(final ResolvedAssessmentItem resolvedAssessmentItem) {
        Assert.notNull(resolvedAssessmentItem);
        logger.debug("Validating {}", resolvedAssessmentItem);
        final ItemValidationResult result = new ItemValidationResult(resolvedAssessmentItem);
        final AssessmentItem item = resolvedAssessmentItem.getItemLookup().extractIfSuccessful();
        if (item!=null) {
            final RootNodeLookup<ResponseProcessing> resolvedResponseProcessingTemplate = resolvedAssessmentItem.getResolvedResponseProcessingTemplateLookup();
            if (resolvedResponseProcessingTemplate!=null && !resolvedResponseProcessingTemplate.wasSuccessful()) {
                result.add(new Notification(item.getResponseProcessing(), null, NotificationType.MODEL_VALIDATION, NotificationLevel.ERROR,
                        "Resolution of ResponseProcessing template failed. Further details are attached elsewhere."));
            }
            final ItemValidationController itemValidationController = new ItemValidationController(jqtiExtensionManager, resolvedAssessmentItem);
            itemValidationController.addNotificationListener(result);
            item.validate(itemValidationController);
        }
        else {
            result.add(new Notification(null, null, NotificationType.MODEL_VALIDATION, NotificationLevel.ERROR,
                    "AssessmentItem was not successfully instantiated"));
        }
        return result;
    }

    public TestValidationResult validateTest(final ResolvedAssessmentTest resolvedAssessmentTest) {
        Assert.notNull(resolvedAssessmentTest);
        logger.debug("Validating {}", resolvedAssessmentTest);
        final TestValidationResult result = new TestValidationResult(resolvedAssessmentTest);
        final AssessmentTest test = resolvedAssessmentTest.getTestLookup().extractIfSuccessful();
        if (test!=null) {
            /* Validate each unique item first */
            for (final Entry<URI, ResolvedAssessmentItem> entry : resolvedAssessmentTest.getResolvedAssessmentItemBySystemIdMap().entrySet()) {
                final URI itemSystemId = entry.getKey();
                final ResolvedAssessmentItem resolvedAssessmentItem = entry.getValue();

                /* Create sensible message for referring to this item in case we need to record any errors below */
                final StringBuilder itemReferenceBuilder = new StringBuilder("Referenced item at System ID ")
                    .append(itemSystemId)
                    .append(" referenced by ");
                final List<AssessmentItemRef> itemRefs = resolvedAssessmentTest.getItemRefsBySystemIdMap().get(itemSystemId);
                if (itemRefs.size()>1) {
                    itemReferenceBuilder.append("identifiers ");
                    for (int i=0,size=itemRefs.size(); i<size; i++) {
                        itemReferenceBuilder.append(itemRefs.get(i).getIdentifier());
                        itemReferenceBuilder.append((i<size-2) ? ", " : " and ");
                    }
                }
                else {
                    itemReferenceBuilder.append("identifier ")
                        .append(itemRefs.get(0).getIdentifier());
                }
                final String itemReferenceDescription = itemReferenceBuilder.toString();

                final ItemValidationResult itemValidationResult = validateItem(resolvedAssessmentItem);
                result.addItemValidationResult(itemValidationResult);
                if (resolvedAssessmentItem.getItemLookup().wasSuccessful()) {
                    if (itemValidationResult.hasModelValidationErrors()) {
                        result.add(new Notification(test, null, NotificationType.MODEL_VALIDATION, NotificationLevel.ERROR,
                                itemReferenceDescription
                                + " has errors. Please see the attached validation result for this item for further information"));
                    }
                    if (itemValidationResult.hasModelValidationWarnings()) {
                        result.add(new Notification(test, null, NotificationType.MODEL_VALIDATION, NotificationLevel.WARNING,
                                itemReferenceDescription
                                + " has warnings. Please see the attached validation result for this item for further information"));
                    }
                }
                else {
                    result.add(new Notification(test, null, NotificationType.MODEL_VALIDATION, NotificationLevel.ERROR,
                            itemReferenceDescription
                            + " was not successfully instantiated. Please see the attached validation result of this item for further information."));
                }
            }

            /* Then validate the test itself */
            final TestValidationController testValidationController = new TestValidationController(jqtiExtensionManager, resolvedAssessmentTest);
            testValidationController.addNotificationListener(result);
            test.validate(testValidationController);
        }
        else {
            result.add(new Notification(null, null, NotificationType.MODEL_VALIDATION, NotificationLevel.ERROR,
                    "Provision of AssessmentTest failed"));
        }
        return result;
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(jqtiExtensionManager=" + jqtiExtensionManager
                + ")";
    }
}
