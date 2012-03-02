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
package org.qtitools.qti.node.test.flow;

import uk.ac.ed.ph.jqtiplus.control.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractJump;
import uk.ac.ed.ph.jqtiplus.node.test.NavigationMode;
import uk.ac.ed.ph.jqtiplus.node.test.SubmissionMode;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jump node is middle node with precondition or branch rule assessment object.
 * 
 * @author Jiri Kajaba
 */
public abstract class JumpNode extends MiddleNode {

    private static final long serialVersionUID = 1871403478916277985L;

    private static final Logger logger = LoggerFactory.getLogger(JumpNode.class);

    private Node target;

    /**
     * Constructs node.
     * 
     * @param prev previous node in linked list
     * @param object assessment object (precondition or branch rule)
     */
    protected JumpNode(Node prev, AbstractJump object) {
        super(prev, object);
    }

    @Override
    public boolean isJump() {
        return true;
    }

    /**
     * Gets assessment object (precondition or branch rule) of this node.
     * 
     * @return assessment object (precondition or branch rule) of this node
     */
    public AbstractJump getJump() {
        return (AbstractJump) getObject();
    }

    /**
     * Gets alternative (not default) target of this jump node.
     * <p>
     * Default target can be obtained with {@code getNext} method.
     * 
     * @return alternative (not default) target of this jump node
     * @see #setTarget
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Sets new alternative (not default) target of this jump node.
     * <p>
     * Default target can be obtained with {@code getNext} method.
     * 
     * @param target new alternative (not default) target of this jump node
     * @see #getTarget
     */
    public void setTarget(Node target) {
        this.target = target;
    }

    /**
     * Evaluates target of this jump node (which node will be next in item flow
     * after this jump).
     * 
     * @return next node in item flow after this jump
     */
    public Node evaluate(TestProcessingContext context) {
        logger.debug("Evaluation of jump {} started.", getIndex());

        Node target = null;

        final TestPart parentTestPart = getJump().getParent().getParentTestPart();
        if (getJump().getParent() instanceof TestPart ||
                parentTestPart.getNavigationMode() == NavigationMode.LINEAR &&
                parentTestPart.getSubmissionMode() == SubmissionMode.INDIVIDUAL) {
            final boolean condition = getJump().evaluate(context);
            target = evaluate(condition);
        }
        else {
            logger.warn("Jump {} is ignored for nonlinear and/or simultaneous mode.", getIndex());

            target = getNext();
        }

        logger.debug("Evaludation of jump {} finished. Next target is {}.", getIndex(), target.getIndex());

        return target;
    }

    /**
     * Evaluates target of this jump node (which node will be next in item flow
     * after this jump).
     * 
     * @param condition result of evaluation of jump's expression
     * @return next node in item flow after this jump
     */
    protected abstract Node evaluate(boolean condition);

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(super.toString());

        builder.append(", TARGET = ");
        builder.append(target != null ? target.getIndex() : "NONE");

        return builder.toString();
    }
}
