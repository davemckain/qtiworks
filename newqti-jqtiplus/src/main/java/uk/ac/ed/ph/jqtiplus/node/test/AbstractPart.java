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

import uk.ac.ed.ph.jqtiplus.group.test.BranchRuleGroup;
import uk.ac.ed.ph.jqtiplus.group.test.ItemSessionControlGroup;
import uk.ac.ed.ph.jqtiplus.group.test.PreConditionGroup;

import java.util.List;

/**
 * Abstract super class for test part and section part.
 * 
 * @author Jiri Kajaba
 */
public abstract class AbstractPart extends UniqueControlObject {

    private static final long serialVersionUID = 2243928073967479375L;

    /**
     * Constructs part.
     * 
     * @param parent parent of constructed part
     */
    public AbstractPart(ControlObject<?> parent) {
        super(parent);

        getNodeGroups().add(0, new PreConditionGroup(this));
        getNodeGroups().add(1, new BranchRuleGroup(this));
        getNodeGroups().add(2, new ItemSessionControlGroup(this));
    }

    /**
     * Gets parent test part of this part (returns itself if this part is instance of test part).
     * 
     * @return parent test part of this part (returns itself if this part is instance of test part)
     */
    public TestPart getParentTestPart() {
        ControlObject<?> parent = this;

        while (!(parent instanceof TestPart)) {
            parent = parent.getParent();
        }

        return (TestPart) parent;
    }

    /**
     * Gets preCondition children.
     * 
     * @return preCondition children
     */
    public List<PreCondition> getPreConditions() {
        return getNodeGroups().getPreConditionGroup().getPreConditions();
    }

    /**
     * Gets branchRule children.
     * 
     * @return branchRule children
     */
    public List<BranchRule> getBranchRules() {
        return getNodeGroups().getBranchRuleGroup().getBranchRules();
    }

    /**
     * Gets itemSessionControl child.
     * 
     * @return itemSessionControl child
     * @see #setItemSessionControlNode
     * @see #getItemSessionControl
     */
    public ItemSessionControl getItemSessionControlNode() {
        return getNodeGroups().getItemSessionControlGroup().getItemSessionControl();
    }

    /**
     * Sets new itemSessionControl child.
     * 
     * @param itemSessionControl new itemSessionControl child
     * @see #getItemSessionControlNode
     */
    public void setItemSessionControlNode(ItemSessionControl itemSessionControl) {
        getNodeGroups().getItemSessionControlGroup().setItemSessionControl(itemSessionControl);
    }

    /**
     * Gets itemSessionControl object for this part.
     * <p>
     * Use this method instead of {@code getItemSessionControlNode}, because returned object of this method contains all fields (they are inherited from parent
     * if they are not defined in this part).
     * 
     * @return itemSessionControl object for this part
     */
    public abstract ItemSessionControl getItemSessionControl();

    /**
     * Returns true if it is safe to jump from this object; false otherwise.
     * <p>
     * It is not safe to jump from shuffled not fixed object (or if any parent is shuffled and not fixed), because object could be moved after jump target (it
     * is not allowed).
     * 
     * @return true if it is safe to jump from this object; false otherwise
     */
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
    public boolean isJumpSafeTarget() {
        return true;
    }
}
