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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.notification;

import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;

import java.io.Serializable;

/**
 * This encapsulates a notification message generated during validation
 * or processing.
 *
 * @author David McKain
 */
public final class Notification implements Serializable {

    private static final long serialVersionUID = 6723715622589447687L;

    /** {@link QtiNode} that is the subject of this notification. (May be null if not known) */
    private final QtiNode qtiNode;

    /** Optional {@link Attribute} that is the subject of this notification. (May be null) */
    private final Attribute<?> attribute;

    /** Type of notification */
    private final NotificationType notificationType;

    /** Severity of this notification */
    private final NotificationLevel notificationLevel;

    /** Message (legacy form - no i18n) */
    private final String message;

    public Notification(final QtiNode qtiNode, final Attribute<?> attribute, final NotificationType notificationType, final NotificationLevel notificationLevel, final String message) {
        Assert.notNull(notificationType, "notificationType");
        Assert.notNull(notificationLevel, "notificationLevel");
        this.qtiNode = qtiNode;
        this.attribute = attribute;
        this.notificationType = notificationType;
        this.notificationLevel = notificationLevel;
        this.message = message;
    }

    public QtiNode getQtiNode() {
        return qtiNode;
    }

    public Attribute<?> getAttribute() {
        return attribute;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public NotificationLevel getNotificationLevel() {
        return notificationLevel;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
