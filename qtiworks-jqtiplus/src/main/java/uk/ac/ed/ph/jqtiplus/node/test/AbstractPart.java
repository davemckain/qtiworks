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

import uk.ac.ed.ph.jqtiplus.group.test.BranchRuleGroup;
import uk.ac.ed.ph.jqtiplus.group.test.ItemSessionControlGroup;
import uk.ac.ed.ph.jqtiplus.group.test.PreConditionGroup;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.util.List;

/**
 * Abstract super class for {@link TestPart} and {@link SectionPart}.
 * <p>
 * NB: This is not explicitly defined in the QTI specification, but is convenient here.
 *
 * @author Jiri Kajaba
 */
public abstract class AbstractPart extends UniqueControlObject {

    private static final long serialVersionUID = 2243928073967479375L;

    public AbstractPart(final ControlObject<?> parent,  final String qtiClassName) {
        super(parent, qtiClassName);

        getNodeGroups().add(0, new PreConditionGroup(this));
        getNodeGroups().add(1, new BranchRuleGroup(this));
        getNodeGroups().add(2, new ItemSessionControlGroup(this));
    }

    public List<PreCondition> getPreConditions() {
        return getNodeGroups().getPreConditionGroup().getPreConditions();
    }

    public List<BranchRule> getBranchRules() {
        return getNodeGroups().getBranchRuleGroup().getBranchRules();
    }


    public ItemSessionControl getItemSessionControl() {
        return getNodeGroups().getItemSessionControlGroup().getItemSessionControl();
    }

    public void setItemSessionControl(final ItemSessionControl itemSessionControl) {
        getNodeGroups().getItemSessionControlGroup().setItemSessionControl(itemSessionControl);
    }

    /**
     * Returns the {@link TestPart} enclosing this {@link AbstractJump}
     * (returns itself if this part is instance of test part).
     */
    public TestPart getEnclosingTestPart() {
        return getNearestAncestorOrSelf(TestPart.class);
    }

    /**
     * Searches this node and its descendants for the first {@link AbstractPart} having the
     * given {@link Identifier}, returning null if not found.
     * <p>
     * (Note that this will never return "self" for an {@link AssessmentTest}, since its identifier is a String).
     *
     * @param identifier identifier of requested object
     * @return object with given identifier or null
     */
    public final AbstractPart lookupFirstDescendantOrSelf(final Identifier identifier) {
        if (identifier.equals(getIdentifier())) {
            return this;
        }
        return lookupFirstDescendant(identifier);
    }

    /**
     * Checks whether all {@link PreCondition}s are met, using the given {@link TestProcessingContext}
     * to evaluate them.
     *
     * @return false if any {@link PreCondition} fails to be met, true otherwise.
     */
    public boolean arePreConditionsMet(final TestProcessingContext testProcessingContext) {
      	for (final PreCondition preCondition : getPreConditions()) {
    		if (!preCondition.evaluatesTrue(testProcessingContext)) {
    			return false;
    		}
    	}
      	return true;
    }

    /**
     * Returns true if it is safe to jump from this object; false otherwise.
     * <p>
     * It is not safe to jump from shuffled not fixed object (or if any parent is shuffled and not fixed), because object could be moved after jump target (it
     * is not allowed).
     *
     * @return true if it is safe to jump from this object; false otherwise
     */
    @ToRefactor
    @Deprecated
    public boolean isJumpSafeSource() {
        return true;
    }

    /**
     * Returns true if this object is safe target of jump; false otherwise.
     * <p>
     * It is not save to jump on not required object in selection group (same for all its parents), because object could disappear (not be selected) and jump
     * target is no longer valid.
     * <p>
     * It is not safe to jump on shuffled not fixed object (or if any parent is shuffled and not fixed), because object could be moved before jump source (it is
     * not allowed).
     *
     * @return true if this object is safe target of jump; false otherwise
     */
    @ToRefactor
    @Deprecated
    public boolean isJumpSafeTarget() {
        return true;
    }
}
