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
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.group.test.TimeLimitsGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.List;

/**
 * Abstract parent for assessmentTest, testPart, assessmentSection and assessmentItemRef.
 *
 * @param <E> type of the identifier attribute for this Object. This is required since
 *   {@link AssessmentTest} identifiers are arbitrary strings; whereas others are proper identifiers.
 *
 * @author Jiri Kajaba
 * @author David McKain (Refactored)
 */
public abstract class ControlObject<E> extends AbstractNode implements IdentifiableNode<E> {

    private static final long serialVersionUID = 3477216040498945052L;

    public ControlObject(final ControlObject<?> parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getNodeGroups().add(new TimeLimitsGroup(this));
    }

    @Override
    public ControlObject<?> getParent() {
        return (ControlObject<?>) super.getParent();
    }


    public TimeLimits getTimeLimits() {
        return getNodeGroups().getTimeLimitsGroup().getTimeLimits();
    }

    public void setTimeLimits(final TimeLimits timeLimits) {
        getNodeGroups().getTimeLimitsGroup().setTimeLimit(timeLimits);
    }

    /**
     * Gets abstractPart children.
     */
    public abstract List<? extends AbstractPart> getChildAbstractParts();

    /**
     * Returns global index (position) of this object in test.
     * <p>
     * This method is used for validation of branchRule. It is not possible to jump back (jump on object with lower global index).
     *
     * @return global index (position) of this object in test
     */
    public int computeAbstractPartGlobalIndex() {
        int result = 0;
        final ControlObject<?> parentControlObject = getParent();
        if (parentControlObject != null) {
            result += parentControlObject.computeAbstractPartGlobalIndex() + 1;
            for (final ControlObject<?> child : parentControlObject.getChildAbstractParts()) {
                if (child == this) {
                    break;
                }
                result += child.computeAbstractPartDescendantCount() + 1;
            }
        }
        return result;
    }

    private int computeAbstractPartDescendantCount() {
        int count = 0;
        for (final ControlObject<?> child : getChildAbstractParts()) {
            count++;
            count += child.computeAbstractPartDescendantCount();
        }
        return count;
    }

    /**
     * Searches descendants of this Object for the first {@link AbstractPart} having the
     * given {@link Identifier}, or null if not found.
     *
     * @param identifier identifier of requested object
     * @return object with given identifier or null
     */
    public final AbstractPart lookupFirstDescendant(final Identifier identifier) {
        AbstractPart result;
        for (final AbstractPart child : getChildAbstractParts()) {
            result = child.searchFirstDescendantOrSelf(identifier);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public final String computeXPathComponent() {
        final E identifier = getIdentifier();
        if (identifier != null) {
            return getQtiClassName() + "[@identifier=\"" + identifier + "\"]";
        }
        return super.computeXPathComponent();
    }
}
