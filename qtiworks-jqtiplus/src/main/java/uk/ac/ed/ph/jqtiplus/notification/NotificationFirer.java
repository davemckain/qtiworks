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

import uk.ac.ed.ph.jqtiplus.ToRefactor;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.value.BaseType;

/**
 * Convenience API for firing off {@link Notification}s. Most of this is filled
 * in by {@link AbstractNotificationFirer} so it's easy to implement.
 * <p>
 * An instance of this will only ever be used by a single thread so implementations
 * do NOT need to be thread-safe.
 *
 * @see AbstractNotificationFirer
 *
 * @author David McKain
 */
public interface NotificationFirer {

    void fireNotification(Notification notification);

    void fireValidationError(QtiNode owner, String message);

    void fireValidationWarning(QtiNode owner, String message);

    void fireAttributeValidationError(Attribute<?> attribute, String message);

    void fireAttributeValidationWarning(Attribute<?> attribute, String message);

    @ToRefactor
    void fireBaseTypeValidationError(QtiNode owner, BaseType[] requiredBaseTypes, BaseType[] actualBaseTypes);

    void fireRuntimeInfo(QtiNode owner, String message);

    void fireRuntimeWarning(QtiNode owner, String message);

    void fireRuntimeError(QtiNode owner, String message);

    /**
     * Sets a "checkpoint" for counting notifications. Notifications fired from
     * this point on at the given level or higher will be counted until
     * {@link #clearCheckpoint()} is called,
     * which will return the number of notifications fired since this checkpoint.
     * <p>
     * This can be useful when book-ending bits of logic to see if they fire off
     * any notifications.
     * <p>
     * Checkpoints may NOT be nested.
     */
    void setCheckpoint(NotificationLevel baseLevel);

    /**
     * Clears a checkpoint set previously by {@link #setCheckpoint(NotificationLevel)},
     * returning the number of notifications fired at the prescribed level (or higher)
     * since then.
     */
    int clearCheckpoint();


}
