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
package uk.ac.ed.ph.jqtiplus.validation;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.notification.ModelNotification;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;

import java.io.Serializable;
import java.util.List;

/**
 * Partial base class for result of validation. Container of validation items.
 *
 * @author Jiri Kajaba
 * @author David McKain
 */
public abstract class AbstractValidationResult implements Serializable {

    private static final long serialVersionUID = 7987550924957601153L;

    private final NotificationRecorder notificationRecorder;
    private boolean hasErrors;
    private boolean hasWarnings;
    private boolean hasInfos;

    public AbstractValidationResult() {
        this.notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        this.hasErrors = false;
        this.hasWarnings = false;
        this.hasInfos = false;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public boolean hasWarnings() {
        return hasWarnings;
    }

    public boolean hasInfos() {
        return hasInfos;
    }

    public List<ModelNotification> getNotifications() {
        return notificationRecorder.getNotifications();
    }

    public List<ModelNotification> getNotificationsAtLevel(final NotificationLevel requiredLevel) {
        return notificationRecorder.getNotificationsAtLevel(requiredLevel);
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public List<ModelNotification> getErrors() {
        return getNotificationsAtLevel(NotificationLevel.ERROR);
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public List<ModelNotification> getWarnings() {
        return getNotificationsAtLevel(NotificationLevel.WARNING);
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public List<ModelNotification> getInfos() {
        return getNotificationsAtLevel(NotificationLevel.INFO);
    }

    public void add(final ModelNotification notification) {
        notificationRecorder.onNotification(notification);
        final NotificationLevel notificationLevel = notification.getNotificationLevel();
        if (!hasErrors && notificationLevel==NotificationLevel.ERROR) {
            hasErrors = true;
        }
        else if (!hasWarnings && notificationLevel==NotificationLevel.WARNING) {
            hasWarnings = true;
        }
        else if (!hasInfos && notificationLevel==NotificationLevel.INFO) {
            hasInfos = true;
        }
    }

}
