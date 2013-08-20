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
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Key used to refer to a particular instance of an {@link AbstractPart} as
 * a {@link TestPlanNode} within a {@link TestPlan}.
 * <p>
 * This is a composite of the {@link Identifier} and global position of the corresponding
 * {@link AbstractPart} in the original test, plus the instance number for this {@link AbstractPart}
 * (taking into account selection with replacement).
 *
 * @see AbstractPart
 * @see TestPlan
 *
 * @author David McKain
 */
public final class TestPlanNodeKey implements Serializable {

    private static final long serialVersionUID = 1928489721725826864L;

    /** Identifier used to refer to this {@link AbstractPart} in the enclosing {@link AssessmentTest} */
    private final Identifier identifier;

    /** Global index of the corresponding {@link AbstractPart} in the test, starting at 0 */
    private final int abstractPartGlobalIndex;

    /**
     * Instance number of the corresponding {@link AbstractPart} within the {@link TestPlan},
     * starting at 1.
     * <p>
     * This is normally 1, but will be greater if selection with
     * replacement results in the {@link AbstractPart} being selected multiple times.
     */
    private final int instanceNumber;

    private final String stringRepresentation;

    private static final Pattern keyPattern = Pattern.compile("(.+?):(\\d+):(\\d+)");

    public TestPlanNodeKey(final Identifier identifier, final int abstractPartGlobalIndex, final int instanceNumber) {
        this.identifier = identifier;
        this.abstractPartGlobalIndex = abstractPartGlobalIndex;
        this.instanceNumber = instanceNumber;
        this.stringRepresentation = identifier.toString() + ":" + abstractPartGlobalIndex + ":" + instanceNumber;
    }

    public static TestPlanNodeKey fromString(final String string) {
        Assert.notNull(string);
        final Matcher matcher = keyPattern.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(TestPlanNodeKey.class.getSimpleName() + " did not follow the expected pattern");
        }
        final String identifierString = matcher.group(1);
        final Identifier identifier;
        try {
            identifier = Identifier.parseString(identifierString);
        }
        catch (final QtiParseException e) {
            throw new IllegalArgumentException("Bad identfifier " + identifierString + " within " + string);
        }
        final int abstractPartGlobalIndex = Integer.valueOf(matcher.group(2));
        if (abstractPartGlobalIndex<0) {
            throw new IllegalArgumentException("Expected abstractPart global index " + abstractPartGlobalIndex + " in " + string + " to be non-negative");
        }
        final int instanceNumber = Integer.valueOf(matcher.group(3));
        if (instanceNumber<=0) {
            throw new IllegalArgumentException("Expected instance number " + instanceNumber + " in " + string + " to be strictly positive");
        }

        return new TestPlanNodeKey(identifier, abstractPartGlobalIndex, instanceNumber);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public int getAbstractPartGlobalIndex() {
        return abstractPartGlobalIndex;
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

    @Override
    public int hashCode() {
        return stringRepresentation.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof TestPlanNodeKey)) {
            return false;
        }
        final TestPlanNodeKey other = (TestPlanNodeKey) obj;
        return stringRepresentation.equals(other.stringRepresentation);
    }
}
