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
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Abstract parent for assessmentTest, testPart, assessmentSection and assessmentItemRef.
 *
 * @param <E> type of the identifier attribute for this Object. This is required since {@link AssessmentTest} identifiers are arbitrary strings; whereas others
 *            are
 *            proper identifiers.
 * @author Jiri Kajaba
 * @author David McKain
 */
public abstract class ControlObject<E> extends AbstractNode implements IdentifiableNode<E> {

    private static final long serialVersionUID = 3477216040498945052L;

    protected boolean finished;

    public ControlObject(ControlObject<?> parent, String qtiClassName) {
        super(parent, qtiClassName);

        getNodeGroups().add(new TimeLimitGroup(this));
    }

    @Override
    public ControlObject<?> getParent() {
        return (ControlObject<?>) super.getParent();
    }

    /**
     * Gets timeLimit child.
     *
     * @return timeLimit child
     * @see #setTimeLimit
     */
    public TimeLimit getTimeLimit() {
        return getNodeGroups().getTimeLimitGroup().getTimeLimit();
    }

    /**
     * Sets new timeLimit child.
     *
     * @param timeLimit new timeLimit child
     * @see #getTimeLimit
     */
    public void setTimeLimit(TimeLimit timeLimit) {
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
    @ToRefactor
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
    public boolean isChildOf(ControlObject<?> parent) {
        if (getParent() == null) {
            return false;
        }
        if (getParent() == parent) {
            return true;
        }
        return getParent().isChildOf(parent);
    }

    /**
     * Returns the child {@link AbstractPart} having the given {@link Identifier}, null if not found.
     *
     * @param identifier identifier of requested object
     * @return object with given identifier or null
     */
    public AbstractPart getChildPart(Identifier identifier) {
        for (final AbstractPart child : getChildren()) {
            if (identifier.equals(child.getIdentifier())) {
                return child;
            }
        }
        return null;
    }

    /**
     * Searches this Object and its descendents for the {@link ControlObject} having the
     * given {@link Identifier}, returning null if not found.
     * <p>
     * (Note that this will never return "self" for an {@link AssessmentTest}, since its identifier is a String).
     *
     * @param identifier identifier of requested object
     * @return object with given identifier or null
     */
    public AbstractPart lookupDescendentOrSelf(Identifier identifier) {
        if (getIdentifier() != null && getIdentifier().equals(identifier)) {
            return (AbstractPart) this;
        }
        return lookupDescendent(identifier);
    }

    /**
     * Searches descendents of this Object for the {@link AbstractPart} having the
     * given {@link Identifier}, or null if not found.
     *
     * @param identifier identifier of requested object
     * @return object with given identifier or null
     */
    public AbstractPart lookupDescendent(Identifier identifier) {
        AbstractPart result;
        for (final AbstractPart child : getChildren()) {
            result = child.lookupDescendentOrSelf(identifier);
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
    public AssessmentItemRef lookupItemRef(Identifier identifier) {
        final AbstractPart descendent = lookupDescendent(identifier);
        if (descendent instanceof AssessmentItemRef) {
            return (AssessmentItemRef) descendent;
        }
        return null;
    }

    public List<AssessmentItemRef> searchItemRefs() {
        final List<AssessmentItemRef> resultBuilder = new ArrayList<AssessmentItemRef>();
        searchItemRefs(this, resultBuilder);
        return resultBuilder;
    }

    public LinkedHashSet<AssessmentItemRef> searchUniqueItemRefs() {
        final LinkedHashSet<AssessmentItemRef> resultBuilder = new LinkedHashSet<AssessmentItemRef>();
        searchItemRefs(this, resultBuilder);
        return resultBuilder;
    }

    private static void searchItemRefs(ControlObject<?> start, Collection<AssessmentItemRef> resultBuilder) {
        if (start instanceof AssessmentItemRef) {
            resultBuilder.add((AssessmentItemRef) start);
        }
        else {
            for (final AbstractPart child : start.getChildren()) {
                searchItemRefs(child, resultBuilder);
            }
        }
    }

// NB: The 2 methods below are probably obsolete. They also don't handle the fact that
// identifiers in tests are Strings, whereas elsewhere they're Identifiers.
//    /**
//     * Returns all item references of given parent (identifier).
//     * <ul>
//     * <li>If you pass identifier of test or null, you will get all item references in test.</li>
//     * <li>If you pass identifier of section, you will get all item references in this section.</li>
//     * <li>If you pass identifier of item reference, you will get this item reference.</li>
//     * <li>If you pass unknown identifier, you will get empty list.</li>
//     * </ul>
//     *
//     * @param identifier identifier of requested parent
//     * @return all item references of given parent (identifier)
//     * @see #lookupItemRefs(String, List, List)
//     */
//    @Deprecated
//    @ToRefactor
//    /* NB: Maybe this needs to account for selection/ordering? */
//    public List<AssessmentItemRef> lookupItemRefs(String identifier) {
//        return lookupItemRefs(identifier, null, null);
//    }
//
//    /**
//     * Returns all item references of given parent (identifier) with given conditions.
//     *
//     * @param identifier identifier of requested parent
//     * @param includeCategories returned item reference must contain this category
//     * @param excludeCategories returned item reference must not contain this category
//     * @return all item references of given parent (identifier) with given conditions
//     * @see #lookupItemRefs(String)
//     */
//    @Deprecated
//    @ToRefactor
//    /* NB: Maybe this needs to account for selection/ordering? */
//    public List<AssessmentItemRef> lookupItemRefs(String identifier, List<String> includeCategories, List<String> excludeCategories) {
//        if (getIdentifier() != null && getIdentifier().equals(identifier)) {
//            identifier = null;
//        }
//
//        final List<AssessmentItemRef> itemRefs = new ArrayList<AssessmentItemRef>();
//
//        for (final AbstractPart child : getChildren()) {
//            itemRefs.addAll(child.lookupItemRefs(identifier, includeCategories, excludeCategories));
//        }
//
//        return itemRefs;
//    }

    /**
     * Returns true if given identifier is identifier of one of built-in variables; false otherwise.
     *
     * @param identifier given identifier
     * @return true if given identifier is identifier of one of built-in variables; false otherwise
     */
    @ToRefactor
    public boolean isBuiltInVariable(Identifier identifier) {
        if (identifier != null && identifier.equals(AssessmentTest.VARIABLE_DURATION_IDENTIFIER)) {
            return true;
        }
        return false;
    }

    //    /**
    //     * Lookups for value of variable with given identifier.
    //     *
    //     * @param identifier identifier of requested variable
    //     * @return value of variable with given identifier or null
    //     */
    //    public Value lookupValue(String identifier)
    //    {
    //        if (identifier != null && identifier.equals(VARIABLE_DURATION_NAME))
    //            return new FloatValue(getDuration() / 1000.0);
    //
    //        return null;
    //    }
    //
    //    /**
    //     * Gets total number of item references of this control object.
    //     *
    //     * @return total number of item references of this control object
    //     */
    //    public int getTotalCount()
    //    {
    //        int result = 0;
    //        for (ControlObject<?> child : getChildren())
    //            result += child.getTotalCount();
    //
    //        return result;
    //    }
    //
    //    /**
    //     * Gets total number of presented item references of this control object.
    //     *
    //     * @return total number of presented item references of this control object
    //     */
    //    public int getPresentedCount()
    //    {
    //        int result = 0;
    //        for (ControlObject<?> child : getChildren())
    //            result += child.getPresentedCount();
    //
    //        return result;
    //    }
    //
    //    /**
    //     * Gets total number of finished item references of this control object.
    //     *
    //     * @return total number of finished item references of this control object
    //     */
    //    public int getFinishedCount()
    //    {
    //        int result = 0;
    //        for (ControlObject<?> child : getChildren())
    //            result += child.getFinishedCount();
    //
    //        return result;
    //    }
    //
    //    /**
    //     * Gets total time spent <em>inside</em> this object including navigation time.
    //     *
    //     * @return total time spent <em>inside</em> this object including navigation time
    //     * @see #getResponseTime
    //     */
    //    public long getTotalTime()
    //    {
    //        long total = 0;
    //        for (ControlObject<?> child : getChildren())
    //            total += child.getTotalTime();
    //
    //        return total;
    //    }
    //
    //    /**
    //     * Gets total time spent <em>inside</em> this object excluding navigation time.
    //     * <p>
    //     * This methods returns pure response (thinking) time (navigation time is not included).
    //     *
    //     * @return total time spent <em>inside</em> this object excluding navigation time
    //     * @see #getTotalTime
    //     */
    //    public long getResponseTime()
    //    {
    //        long response = 0;
    //        for (ControlObject<?> child : getChildren())
    //            response += child.getResponseTime();
    //
    //        return response;
    //    }
    //
    //    /**
    //     * Gets value of duration built-in variable.
    //     * <p>
    //     * Duration for test or test part or section means total time (thinking time including navigation time).
    //     * <p>
    //     * Duration for item reference means response time (thinking time excluding navigation time).
    //     *
    //     * @return value of duration built-in variable
    //     */
    //    public long getDuration()
    //    {
    //        return getTotalTime();
    //    }
    //
    //    /**
    //     * Returns true if time used by this control object is higher or equal than minimum time limit; false otherwise.
    //     * <p>
    //     * Time used by this control object is calculated as:
    //     * <ul>
    //     * <li>If this control object is {@code AssessmentItemRef} method {@code getDuration} is used.</li>
    //     * <li>If this control object if not {@code AssessmentItemRef} method {@code getTotalTime} is used.</li>
    //     * </ul>
    //     * <p>
    //     * This method is not implemented and returns always true.
    //     *
    //     * @return true if time used by this control object is higher or equal than minimum time limit; false otherwise
    //     */
    //    public boolean passMinimumTimeLimit()
    //    {
    //        return true;
    //    }
    //
    //    /**
    //     * Returns true if time used by this control object is lower or equal than maximum time limit; false otherwise.
    //     * This method checks first this control object and then recursively all its parents
    //     * and returns true only if every tested object passed check.
    //     * <p>
    //     * Time used by this control object is calculated as:
    //     * <ul>
    //     * <li>If this control object is {@code AssessmentItemRef} method {@code getDuration} is used.</li>
    //     * <li>If this control object if not {@code AssessmentItemRef} method {@code getTotalTime} is used.</li>
    //     * </ul>
    //     * <p>
    //     * This method is used for check if item can be shown to the user.
    //     *
    //     * @return true if time used by this control object is lower or equal than maximum time limit; false otherwise
    //     */
    //    public boolean passMaximumTimeLimit()
    //    {
    //        if (getTimeLimit() != null && getTimeLimit().getMaximumMillis() != null)
    //        {
    //            if (getDuration() >= getTimeLimit().getMaximumMillis())
    //                return false;
    //        }
    //
    //        return (getParent() != null) ? getParent().passMaximumTimeLimit() : true;
    //    }

    @Override
    public final String computeXPathComponent() {
        final E identifier = getIdentifier();
        if (identifier != null) {
            return getQtiClassName() + "[@identifier=\"" + identifier + "\"]";
        }
        return super.computeXPathComponent();
    }
}
