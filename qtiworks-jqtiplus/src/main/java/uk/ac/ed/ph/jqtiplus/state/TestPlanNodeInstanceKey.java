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
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.io.Serializable;

/**
 * Pairing of an {@link Identifier} and an {@link Integer} used for
 * referring to a specific instance of a {@link AbstractPart}
 * within a {@link TestPlan}, which can happen because of selection
 * with replacement (or identifier duplication).
 *
 * @author David McKain
 */
public final class TestPlanNodeInstanceKey implements Serializable {

    private static final long serialVersionUID = 1928489721725826864L;

    /** Identifier used to refer to this {@link AbstractPart} in the enclosing {@link AssessmentTest} */
    private final Identifier identifier;

    /**
     * Instance number of this {@link Identifier} within the {@link TestPlan}, starting at 1.
     * This can be greater than 1 in the following cases:
     * (a) selection with replacement
     * (b) identifiers being reused (which is invalid)
     */
    private final int instanceNumber;

    public TestPlanNodeInstanceKey(final Identifier identifier, final int instanceNumber) {
        this.identifier = identifier;
        this.instanceNumber = instanceNumber;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    @Override
    public String toString() {
        return "(" + identifier.toString() + "," + instanceNumber + ")";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof TestPlanNodeInstanceKey)) {
            return false;
        }
        final TestPlanNodeInstanceKey other = (TestPlanNodeInstanceKey) obj;
        return ObjectUtilities.nullSafeEquals(identifier, other.identifier)
                && instanceNumber==other.instanceNumber;
    }
}
