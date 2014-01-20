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

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.group.test.BranchRuleGroup;
import uk.ac.ed.ph.jqtiplus.group.test.ItemSessionControlGroup;
import uk.ac.ed.ph.jqtiplus.group.test.PreConditionGroup;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableNode;
import uk.ac.ed.ph.jqtiplus.node.UniqueNode;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.util.List;

/**
 * Abstract super class for {@link TestPart} and {@link SectionPart}.
 * <p>
 * NB: This is not explicitly defined in the QTI specification, but is convenient here.
 *
 * @author Jiri Kajaba
 * @author David McKain (refactored)
 */
public abstract class AbstractPart extends ControlObject<Identifier> implements UniqueNode<Identifier> {

    private static final long serialVersionUID = 2243928073967479375L;

    public AbstractPart(final ControlObject<?> parent,  final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new IdentifierAttribute(this, IdentifiableNode.ATTR_IDENTIFIER_NAME, true));

        getNodeGroups().add(0, new PreConditionGroup(this));
        getNodeGroups().add(1, new BranchRuleGroup(this));
        getNodeGroups().add(2, new ItemSessionControlGroup(this));
    }

    @Override
    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    @Override
    public void setIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).setValue(identifier);
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
        return searchNearestAncestorOrSelf(TestPart.class);
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
    public final AbstractPart searchFirstDescendantOrSelf(final Identifier identifier) {
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
     * Returns whether or not this {@link AbstractPart} is in "scope" of an {@link Ordering}
     * or {@link Selection} defined on an ancestor {@link AssessmentSection}.
     */
    public boolean isInScopeOfOrderingOrSelection() {
        final ControlObject<?> parentNode = getParent();
        if (parentNode==null || !(parentNode instanceof AssessmentSection)) {
            return false;
        }
        else {
            final AssessmentSection parentSection = (AssessmentSection) parentNode;
            if (parentSection.getSelection()!=null || parentSection.getOrdering()!=null) {
                return true;
            }
            return parentSection.isInScopeOfOrderingOrSelection();
        }
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Identifier identifier = getIdentifier();
        if (identifier!=null) {
            final IdentifierAttribute identifierAttribute = getAttributes().getIdentifierAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME);
            validateUniqueIdentifier(context, identifierAttribute, identifier);
            if (BranchRule.isSpecial(identifier)) {
                context.fireAttributeValidationError(identifierAttribute, "The identifier " + identifier
                        + " is reserved in tests as a special target for branchRule");
            }
        }
    }
}
