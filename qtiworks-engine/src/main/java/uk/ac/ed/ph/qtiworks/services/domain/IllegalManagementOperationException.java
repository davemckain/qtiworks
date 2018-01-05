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
package uk.ac.ed.ph.qtiworks.services.domain;

import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.TestDeliverySettings;
import uk.ac.ed.ph.qtiworks.services.AssessmentManagementService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This encapsulates illegal operations in the {@link AssessmentManagementService}.
 * <p>
 * This was added in 1.0-beta12. Earlier versions had explicit Exception classes for 2
 * of these operations: IncompatiableDeliverySettingsException and
 * CannotChangeAssessmentTypeException. The addition of a further class of error
 * invoked a refactoring to avoid having too many Exception classes.
 *
 * @author David McKain
 */
@ResponseStatus(value=HttpStatus.CONFLICT)
public final class IllegalManagementOperationException extends Exception {

    private static final long serialVersionUID = 6607538440151663102L;

    public static enum OperationFailureReason {

        /**
         * {@link Assessment} cannot be changed from item to test or vice versa after initial creation
         */
        CANNOT_CHANGE_ASSESSMENT_TYPE,

        /**
         * It is not allowed to use an {@link ItemDeliverySettings} on a test,
         * ora {@link TestDeliverySettings} on a standalone item.
         */
        INCOMPATIBLE_DELIVERY_SETTINGS,

        /**
         * User-invoked updates or deletions to a {@link Delivery} may only be done on
         * {@link DeliveryType#USER_CREATED} Deliveries.
         */
        DELIVERY_NOT_USER_CREATED,

        ;
    }

    private final EnumerableClientFailure<OperationFailureReason> failure;

    public IllegalManagementOperationException(final EnumerableClientFailure<OperationFailureReason> failure) {
        super(failure.toString());
        this.failure = failure;
    }

    public IllegalManagementOperationException(final EnumerableClientFailure<OperationFailureReason> failure, final Throwable cause) {
        super(failure.toString(), cause);
        this.failure = failure;
    }

    public IllegalManagementOperationException(final OperationFailureReason reason) {
        this(new EnumerableClientFailure<OperationFailureReason>(reason));
    }

    public IllegalManagementOperationException(final OperationFailureReason reason, final Throwable cause) {
        this(new EnumerableClientFailure<OperationFailureReason>(reason), cause);
    }

    public IllegalManagementOperationException(final OperationFailureReason reason, final Object... arguments) {
        this(new EnumerableClientFailure<OperationFailureReason>(reason, arguments));
    }


    public EnumerableClientFailure<OperationFailureReason> getFailure() {
        return failure;
    }
}
