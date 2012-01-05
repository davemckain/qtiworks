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

import uk.ac.ed.ph.jqtiplus.node.test.BranchRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Branch rule node is jump node with branch rule assessment object.
 * 
 * @author Jiri Kajaba
 */
public class BranchRuleNode extends JumpNode {

    private static final long serialVersionUID = 7467994989744491365L;
    private static final Logger logger = LoggerFactory.getLogger(BranchRuleNode.class);

    /**
     * Constructs node.
     * 
     * @param prev previous node in linked list
     * @param object assessment object (branch rule)
     */
    public BranchRuleNode(Node prev, BranchRule object) {
        super(prev, object);
    }

    @Override
    public boolean isBranchRule() {
        return true;
    }

    /**
     * Gets assessment object (branch rule) of this node.
     * 
     * @return assessment object (branch rule) of this node
     */
    public BranchRule getBranchRule() {
        return (BranchRule) getJump();
    }

    @Override
    protected Node evaluate(boolean condition) {
        logger.debug("Evaluation of branch rule {} started. Condition is {}.", getIndex(),
                condition);

        Node target = null;
        if (condition)
            target = getTarget();
        else
            target = getNext();

        logger.debug("Evaludation of branch rule {} finished. Next target is {}.", getIndex(),
                target.getIndex());

        return target;
    }
}
