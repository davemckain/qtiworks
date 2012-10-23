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
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.group.test.TimeLimitGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.xperimental.ToCheck;

import java.util.List;

/**
 * Abstract parent for assessmentTest, testPart, assessmentSection and assessmentItemRef.
 *
 * @param <E> type of the identifier attribute for this Object. This is required since
 *   {@link AssessmentTest} identifiers are arbitrary strings; whereas others are proper identifiers.
 *
 * @author Jiri Kajaba
 * @author David McKain
 */
public abstract class ControlObject<E> extends AbstractNode implements IdentifiableNode<E> {

    private static final long serialVersionUID = 3477216040498945052L;

    public ControlObject(final ControlObject<?> parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getNodeGroups().add(new TimeLimitGroup(this));
    }

    @Override
    public ControlObject<?> getParent() {
        return (ControlObject<?>) super.getParent();
    }


    public TimeLimit getTimeLimit() {
        return getNodeGroups().getTimeLimitGroup().getTimeLimit();
    }

    public void setTimeLimit(final TimeLimit timeLimit) {
        getNodeGroups().getTimeLimitGroup().setTimeLimit(timeLimit);
    }

    /**
     * Gets abstractPart children.
     *
     * @return abstractPart children
     */
    public abstract List<? extends AbstractPart> getChildren();

    //    /**
    //     * Returns true if at least one child item reference was already presented to user; false otherwise.
    //     * <p>
    //     * Once object is presented it remains presented for ever.
    //     *
    //     * @return true if at least one child item reference was already presented to user; false otherwise
    //     */
    //    public boolean isPresented() {
    //        for (AbstractPart child : getChildren()) {
    //            if (child.isPresented()) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }
    //
    //    /**
    //     * Returns true if this object is finished; false otherwise.
    //     * <p>
    //     * Finished state has different meaning for different object types (see overriding methods).
    //     * <p>
    //     * Once object is finished it remains finished for ever.
    //     *
    //     * @return true if this object is finished; false otherwise
    //     * @see #setFinished
    //     */
    //    public boolean isFinished()
    //    {
    //        return finished;
    //    }
    //
    //    /**
    //     * Sets this object to finished state.
    //     *
    //     * @see #isFinished
    //     */
    //    public void setFinished()
    //    {
    //        finished = true;
    //    }

    /**
     * Returns global index (position) of this object in test.
     * <p>
     * This method is used for validation of branchRule. It is not possible to jump back (jump on object with lower global index).
     *
     * @return global index (position) of this object in test
     */
    @ToCheck
    public int getGlobalIndex() {
        int index = 0;

        if (getParent() != null) {
            index += getParent().getGlobalIndex() + 1;

            for (final ControlObject<?> child : getParent().getChildren()) {
                if (child == this) {
                    break;
                }

                index += child.getGlobalChildrenCount() + 1;
            }
        }

        return index;
    }

    @ToCheck
    private int getGlobalChildrenCount() {
        int count = 0;

        for (final ControlObject<?> child : getChildren()) {
            count++;
            count += child.getGlobalChildrenCount();
        }

        return count;
    }

    /**
     * Returns true if given parameter is direct or indirect parent of this object; false otherwise.
     *
     * @param parent given parameter
     * @return true if given parameter is direct or indirect parent of this object; false otherwise
     */
    @ToCheck
    public final boolean isChildOf(final ControlObject<?> parent) {
        if (getParent() == null) {
            return false;
        }
        if (getParent() == parent) {
            return true;
        }
        return getParent().isChildOf(parent);
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
        for (final AbstractPart child : getChildren()) {
            result = child.lookupFirstDescendantOrSelf(identifier);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Lookups for item reference with given identifier.
     *
     * @param identifier identifier of requested item reference
     * @return item reference with given identifier or null if identifier is not found or doesn't correspond
     *         to an {@link AssessmentItemRef}
     */
    @ToCheck
    @Deprecated
    public final AssessmentItemRef lookupItemRef(final Identifier identifier) {
        final AbstractPart descendent = lookupFirstDescendant(identifier);
        if (descendent instanceof AssessmentItemRef) {
            return (AssessmentItemRef) descendent;
        }
        return null;
    }

    /**
     * Returns true if given identifier is identifier of one of built-in variables; false otherwise.
     *
     * @param identifier given identifier
     * @return true if given identifier is identifier of one of built-in variables; false otherwise
     */
    @ToCheck
    public boolean isBuiltInVariable(final Identifier identifier) {
        if (identifier != null && identifier.equals(AssessmentTest.VARIABLE_DURATION_IDENTIFIER)) {
            return true;
        }
        return false;
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
