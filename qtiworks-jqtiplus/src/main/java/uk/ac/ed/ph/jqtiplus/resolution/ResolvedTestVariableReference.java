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
package uk.ac.ed.ph.jqtiplus.resolution;

import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

/**
 * Encapsulates a resolved variable reference within an {@link AssessmentTest}, which
 * might be a reference to a test variable or an item variable within a particular instance
 * of an {@link AssessmentItemRef}.
 *
 * @author David McKain
 */
public final class ResolvedTestVariableReference {

    private final VariableDeclaration targetVariableDeclaration;
    private final AssessmentItemRef assessmentItemRef;
    private final Integer instanceNumber;

    public ResolvedTestVariableReference(final VariableDeclaration testVariableDeclaration) {
        this.targetVariableDeclaration = testVariableDeclaration;
        this.assessmentItemRef = null;
        this.instanceNumber = null;
    }

    public ResolvedTestVariableReference(final AssessmentItemRef assessmentItemRef, final VariableDeclaration itemVariableDeclaration) {
        this(assessmentItemRef, itemVariableDeclaration, null);
    }

    public ResolvedTestVariableReference(final AssessmentItemRef assessmentItemRef, final VariableDeclaration itemVariableDeclaration, final Integer instanceNumber) {
        this.targetVariableDeclaration = itemVariableDeclaration;
        this.assessmentItemRef = assessmentItemRef;
        this.instanceNumber = instanceNumber;
    }

    public boolean isItemVariableReference() {
        return assessmentItemRef!=null;
    }

    public boolean isTestVariableReference() {
        return assessmentItemRef==null;
    }

    public VariableDeclaration getVariableDeclaration() {
        return targetVariableDeclaration;
    }

    public AssessmentItemRef getAssessmentItemRef() {
        return assessmentItemRef;
    }

    public Integer getInstanceNumber() {
        return instanceNumber;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(variableDeclaration=" + targetVariableDeclaration
                + ",assessmentItemRef=" + assessmentItemRef
                + ",instanceNumber=" + instanceNumber
                + ")";
    }
}
