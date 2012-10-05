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
package uk.ac.ed.ph.jqtiplus.resolution;

import uk.ac.ed.ph.jqtiplus.exception.QtiException;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;

/**
 * {@link Exception} thrown when an attempt to resolve a variable via a
 * {@link VariableReferenceIdentifier} fails to succeed.
 *
 * @author David McKain
 */
@Deprecated
public final class VariableResolutionException extends QtiException {

    private static final long serialVersionUID = -6987026133309921007L;

    public static enum VariableResolutionFailureReason {

        // Item-specific failures

        /**
         * Indicates that the lookup of the {@link AssessmentObject} behind a {@link ResolvedAssessmentObject} had failed.
         * (Therefore ALL variable resolutions will automatically fail.)
         */
        THIS_ITEM_LOOKUP_FAILURE("This AssessmentItem was not successfully looked up"),

        ITEM_VARIABLE_NOT_DECLARED("The referenced item variable was not declared"),

        // Test-specific failures

        /**
         * Indicates that the lookup of the {@link AssessmentObject} behind a {@link ResolvedAssessmentObject} had failed.
         * (Therefore ALL variable resolutions will automatically fail.)
         */
        THIS_TEST_LOOKUP_FAILURE("This AssessmentTest was not successfully looked up"),

        TEST_VARIABLE_NOT_DECLARED("The referenced test variable was not declared"),

        UNMATCHED_ASSESSMENT_ITEM_REF_IDENTIFIER("The variable reference did not match a declared AssessmentItemRef"),

        NON_UNIQUE_ASSESSMENT_ITEM_REF_IDENTIFIER("The variable reference matched multiple AssessmentItemRefs"),

        TEST_ITEM_LOOKUP_FAILURE("The referenced AssessmentItem was not successfully looked up"),

        TEST_MAPPED_ITEM_VARIABLE_NOT_DECLARED("The (mapped) item variable within the referenced AssessmentItem was not declared"),

        ;

        private final String description;

        private VariableResolutionFailureReason(final String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

    private final VariableReferenceIdentifier variableReferenceIdentifier;
    private final VariableResolutionFailureReason reason;

    public VariableResolutionException(final Identifier variableReferenceIdentifier, final VariableResolutionFailureReason reason) {
        this(variableReferenceIdentifier.toVariableReferenceIdentifier(), reason);
    }

    public VariableResolutionException(final VariableReferenceIdentifier variableReferenceIdentifier, final VariableResolutionFailureReason reason) {
        super("Resolution of variable reference " + variableReferenceIdentifier
                + " failed: " + reason.getDescription());
        this.variableReferenceIdentifier = variableReferenceIdentifier;
        this.reason = reason;
    }

    public VariableReferenceIdentifier getVariableReferenceIdentifier() {
        return variableReferenceIdentifier;
    }

    public VariableResolutionFailureReason getReason() {
        return reason;
    }
}
