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
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

/**
 * Represents the <code>branchRule</code> QTI class
 *
 * @author Jiri Kajaba
 * @author David McKain (refactored)
 */
public final class BranchRule extends AbstractJump {

    private static final long serialVersionUID = -6025143798114714329L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "branchRule";

    /** Name of target attribute in xml schema. */
    public static final String ATTR_TARGET_NAME = "target";

    /** Special target for exiting assessmentTest. */
    public static final Identifier EXIT_TEST = Identifier.assumedLegal("EXIT_TEST");

    /** Special target for exiting testPart. */
    public static final Identifier EXIT_TESTPART = Identifier.assumedLegal("EXIT_TESTPART");

    /** Special target for exiting assessmentSection. */
    public static final Identifier EXIT_SECTION = Identifier.assumedLegal("EXIT_SECTION");

    public BranchRule(final AbstractPart parent) {
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

    public Identifier getTarget() {
        return getAttributes().getIdentifierAttribute(ATTR_TARGET_NAME).getComputedValue();
    }

    public void setTarget(final Identifier target) {
        getAttributes().getIdentifierAttribute(ATTR_TARGET_NAME).setValue(target);
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Identifier targetIdentifier = getTarget();
        final AbstractPart owner = getParent();
        if (owner==null) {
            return;
        }
        if (owner instanceof TestPart) {
            /* We allow branchRule with EXIT_TEST here */
            if (!EXIT_TEST.equals(targetIdentifier)) {
                context.fireValidationError(this, "Only EXIT_TEST is allowed as the target of a branchRule on a testPart");
            }
        }
        else {
            /* We're at an assessmentSection or assessmentItemRef. We allow
             * an EXIT_SECTION, EXIT_TEST_PART, or branch to any assessmentSection
             * or assessmentItemRef provided it is "after" this Node and is not the
             * subject or selection or ordering.
             */
            final TestPart testPart = owner.getEnclosingTestPart();
            if (testPart==null) {
                return;
            }
            if (!testPart.areJumpsEnabled()) {
                context.fireValidationError(this, "branchRules on assessmentSections or assessmentItemRefs only apply within testParts with linear navigationMode and individual submissionMode");
            }
            else if (!isSpecial(targetIdentifier)) {
                /* Target must be an assessmentItemRef or assessmentSection within the current testPart,
                 * and coming after the current Node. We also check to make sure it is not within a selection or
                 * ordering.
                 */
                final AbstractPart targetAbstractPart = testPart.lookupFirstDescendant(targetIdentifier);
                if (targetAbstractPart!=null) {
                    final int thisPartGlobalIndex = owner.computeAbstractPartGlobalIndex();
                    final int targetPartGlobalIndex = targetAbstractPart.computeAbstractPartGlobalIndex();
                    if (targetPartGlobalIndex <= thisPartGlobalIndex) {
                        context.fireValidationError(this, "branchRule target " + targetIdentifier + " must come after " + owner.getIdentifier());
                    }
                    /* Make sure target is not within a selection or ordering */
                    if (targetAbstractPart.isInScopeOfOrderingOrSelection()) {
                        context.fireValidationWarning(this, "branchRule target " + targetIdentifier + " is subject to selection or ordering. A successful branch therefore cannot be guaranteed");
                    }
                }
                else {
                    context.fireValidationError(this, "branchRule target " + targetIdentifier + " was not found within the testPart with identifier " + testPart.getIdentifier());
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
    public static boolean isSpecial(final Identifier target) {
        return target.equals(EXIT_TEST) || target.equals(EXIT_TESTPART) || target.equals(EXIT_SECTION);
    }
}
