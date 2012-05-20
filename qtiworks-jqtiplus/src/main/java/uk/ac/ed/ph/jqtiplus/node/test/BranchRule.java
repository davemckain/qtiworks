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

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ItemFlowValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;

/**
 * A branchRule is A simple expression attached to TestPart or assessmentSection or assessmentItemRef.
 * It must evaluate to true if this branchRule should be applied.
 * <p>
 * If the expression evaluates to false, or has A NULL value, this branchRule will not be applied.
 * <p>
 * BranchRules are evaluated after part or section or item is finished (not presented).
 * <p>
 * BranchRule is not real expression (doesn't implement Expression interface).
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public class BranchRule extends AbstractJump {

    private static final long serialVersionUID = -6025143798114714329L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "branchRule";

    /** Name of target attribute in xml schema. */
    public static final String ATTR_TARGET_NAME = "target";

    /** Special target for exiting assessmentTest. */
    public static final Identifier EXIT_TEST = new Identifier("EXIT_TEST", false);

    /** Special target for exiting testPart. */
    public static final Identifier EXIT_TEST_PART = new Identifier("EXIT_TESTPART", false);

    /** Special target for exiting assessmentSection. */
    public static final Identifier EXIT_SECTION = new Identifier("EXIT_SECTION", false);

    public BranchRule(AbstractPart parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IdentifierAttribute(this, ATTR_TARGET_NAME, true));
    }

    @Override
    public final String computeXPathComponent() {
        final Identifier target = getTarget();
        if (target != null) {
            return getQtiClassName() + "[@target=\"" + target + "\"]";
        }
        return super.computeXPathComponent();
    }

    /**
     * Gets value of target attribute.
     *
     * @return value of target attribute
     * @see #setTarget
     */
    public Identifier getTarget() {
        return getAttributes().getIdentifierAttribute(ATTR_TARGET_NAME).getComputedValue();
    }

    /**
     * Gets target ControlOject of this branchRule or null it target doesn't exist.
     * <ol>
     * <li>if target is EXIT_TEST, returns assessmentTest</li>
     * <li>if target is EXIT_TESTPART, returns testPart which should be exited</li>
     * <li>if target is EXIT_SECTION, returns assessmentSection which should be exited</li>
     * <li>otherwise returns target ControlObject</li>
     * </ol>
     *
     * @return target ControlObject of this branchRule or null it target doesn't exist
     */
    public ControlObject<?> getTargetControlObject() {
        ControlObject<?> result;
        if (isExitTest()) {
            result = getRootObject(AssessmentTest.class);
        }
        else if (isExitTestPart()) {
            if (getParent() instanceof TestPart) {
                result = null;
            }
            else {
                result = getParent().getParentTestPart();
            }
        }
        else if (isExitSection()) {
            if (getParent() instanceof SectionPart) {
                result = ((SectionPart) getParent()).getParentSection();
            }
            else {
                result = null;
            }
        }
        else {
            result = getRootObject(AssessmentTest.class).lookupDescendentOrSelf(getTarget());
        }
        return result;
    }

    /**
     * Sets new value of target attribute.
     *
     * @param target new value of target attribute
     * @see #getTarget
     */
    public void setTarget(Identifier target) {
        getAttributes().getIdentifierAttribute(ATTR_TARGET_NAME).setValue(target);
    }

    /**
     * Returns true is target is EXIT_TEST or EXIT_TEST_PART or EXIT_SECTION; false otherwise.
     *
     * @return true is target is EXIT_TEST or EXIT_TEST_PART or EXIT_SECTION; false otherwise
     */
    public boolean isSpecial() {
        return isExitTest() || isExitTestPart() || isExitSection();
    }

    /**
     * Returns true is target is EXIT_TEST; false otherwise.
     *
     * @return true is target is EXIT_TEST; false otherwise
     */
    public boolean isExitTest() {
        return getTarget().equals(EXIT_TEST);
    }

    /**
     * Returns true is target is EXIT_TEST_PART; false otherwise.
     *
     * @return true is target is EXIT_TEST_PART; false otherwise
     */
    public boolean isExitTestPart() {
        return getTarget().equals(EXIT_TEST_PART);
    }

    /**
     * Returns true is target is EXIT_SECTION; false otherwise.
     *
     * @return true is target is EXIT_SECTION; false otherwise
     */
    public boolean isExitSection() {
        return getTarget().equals(EXIT_SECTION);
    }

    @Override
    protected void validateAttributes(ValidationContext context) {
        super.validateAttributes(context);

        final Identifier target = getTarget();
        final TestPart parentTestPart = getParent().getParentTestPart();
        if (target != null && parentTestPart.areJumpsEnabled()) {
            if (isSpecial()) {
                if (isExitTestPart()) {
                    if (getParent() instanceof TestPart) {
                        context.add(new ItemFlowValidationError(this, "Invalid special target: " + target));
                    }
                }
                else if (isExitSection()) {
                    if (getParent() instanceof TestPart || getParent().getParent() instanceof TestPart) {
                        context.add(new ItemFlowValidationError(this, "Invalid special target: " + target));
                    }
                }
            }
            else {
                final AbstractPart targetPart = context.getSubjectTest().lookupDescendentOrSelf(target);

                if (targetPart == null) {
                    context.add(new ItemFlowValidationError(this, "Cannot find target: " + target));
                }
                else {
                    final int parentIdex = getParent().getGlobalIndex();
                    final int targetIndex = targetPart.getGlobalIndex();

                    if (getParent() instanceof TestPart && (targetPart instanceof AssessmentSection || targetPart instanceof AssessmentItemRef)) {
                        context.add(new ItemFlowValidationError(this, "Cannot jump from testPart to " + targetPart.getQtiClassName() + ": " + target));
                    }
                    else if (targetIndex <= parentIdex) {
                        context.add(new ItemFlowValidationError(this, "Cannot jump back to: " + target));
                    }
                    else if (targetPart.isChildOf(getParent())) {
                        context.add(new ItemFlowValidationError(this, "Cannot jump to own child: " + target));
                    }
                    else {
                        if (!getParent().isJumpSafeSource()) {
                            context.add(new ValidationWarning(this, "It is not safe to jump from this node. Check selection and ordering settings."));
                        }

                        if (!targetPart.isJumpSafeTarget()) {
                            context.add(new ValidationWarning(this, "Target is not safe for jump: " + target + " Check selection and ordering settings."));
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns true if given target is special (EXIT_TEST, EXIT_TESTPART, EXIT_SECTION); false otherwise.
     *
     * @param target given target
     * @return true if given target is special (EXIT_TEST, EXIT_TESTPART, EXIT_SECTION); false otherwise
     */
    public static boolean isSpecial(Identifier target) {
        return target != null && (target.equals(EXIT_TEST) || target.equals(EXIT_TEST_PART) || target.equals(EXIT_SECTION));
    }
}
