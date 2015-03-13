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

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.notification.Notification;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationListener;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.notification.NotificationType;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentObject;

import java.io.Serializable;
import java.util.List;

/**
 * Encapsulates the result of doing validation on a {@link ResolvedAssessmentObject}
 *
 * @author David McKain
 */
public abstract class AssessmentObjectValidationResult<E extends AssessmentObject>
        implements NotificationListener, Serializable  {

    private static final long serialVersionUID = -6570165277334622467L;

    private final ResolvedAssessmentObject<E> resolvedAssessmentObject;
    private final NotificationRecorder notificationRecorder;
    private boolean hasModelValidationErrors;
    private boolean hasModelValidationWarnings;

    public AssessmentObjectValidationResult(final ResolvedAssessmentObject<E> resolvedAssessmentObject) {
        this.resolvedAssessmentObject = resolvedAssessmentObject;
        this.notificationRecorder = new NotificationRecorder(NotificationLevel.WARNING);
        this.hasModelValidationErrors = false;
        this.hasModelValidationWarnings = false;
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public ResolvedAssessmentObject<E> getResolvedAssessmentObject() {
        return resolvedAssessmentObject;
    }

    public boolean hasModelValidationErrors() {
        return hasModelValidationErrors;
    }

    public boolean hasModelValidationWarnings() {
        return hasModelValidationWarnings;
    }

    public List<Notification> getNotifications() {
        return notificationRecorder.getNotifications();
    }

    public List<Notification> getNotificationsAtLevel(final NotificationLevel requiredLevel) {
        return notificationRecorder.getNotificationsAtLevel(requiredLevel);
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public List<Notification> getModelValidationErrors() {
        return getNotificationsAtLevel(NotificationLevel.ERROR);
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public List<Notification> getModelValidationWarnings() {
        return getNotificationsAtLevel(NotificationLevel.WARNING);
    }

    @Override
    public void onNotification(final Notification notification) {
        add(notification);
    }

    public void add(final Notification notification) {
        final NotificationType notificationType = notification.getNotificationType();
        final NotificationLevel notificationLevel = notification.getNotificationLevel();
        if (notificationType==NotificationType.MODEL_VALIDATION) {
            if (notificationLevel.compareTo(NotificationLevel.WARNING) >= 0) {
                notificationRecorder.onNotification(notification);
                if (!hasModelValidationErrors && notificationLevel==NotificationLevel.ERROR) {
                    hasModelValidationErrors = true;
                }
                else if (!hasModelValidationWarnings && notificationLevel==NotificationLevel.WARNING) {
                    hasModelValidationWarnings = true;
                }
            }
            else {
                throw new IllegalArgumentException("Validation notifications (of type "
                        + NotificationType.MODEL_VALIDATION
                        + ") are currently only supported at level "
                        + NotificationLevel.WARNING
                        + " or higher");
            }
        }
        else {
            throw new IllegalArgumentException("Validation notifications are expected to have type " + NotificationType.MODEL_VALIDATION);
        }
    }

    /**
     * Returns whether this {@link AssessmentObject} is "fully valid", in that schema validation succeeded,
     * the JQTI+ model build succeeded, and the additional JQTI+ validation checks all succeeded without warnings.
     */
    public boolean isValid() {
        return resolvedAssessmentObject.getRootNodeLookup().wasSuccessful()
                && !hasModelValidationWarnings()
                && !hasModelValidationErrors();
    }

    /**
     * Returns whether this {@link AssessmentObject} is almost "fully valid", in that schema validation succeeded,
     * the JQTI+ model build succeeded, and the additional JQTI+ validation checks all succeeded,
     * possibly with warnings.
     */
    public boolean isValidationWarningsOnly() {
        return resolvedAssessmentObject.getRootNodeLookup().wasSuccessful()
                && hasModelValidationWarnings()
                && !hasModelValidationErrors();
    }
}
