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

import uk.ac.ed.ph.jqtiplus.node.test.PreCondition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Precondition node is jump node with precondition assessment object.
 * 
 * @author Jiri Kajaba
 */
public class PreConditionNode extends JumpNode {

    private static final long serialVersionUID = -3782618631062124181L;
    private static final Logger logger = LoggerFactory.getLogger(PreConditionNode.class);

    /**
     * Constructs node.
     * 
     * @param prev previous node in linked list
     * @param object assessment object (precondition)
     */
    public PreConditionNode(Node prev, PreCondition object) {
        super(prev, object);
    }

    @Override
    public boolean isPreCondition() {
        return true;
    }

    /**
     * Gets assessment object (precondition) of this node.
     * 
     * @return assessment object (precondition) of this node
     */
    public PreCondition getPreCondition() {
        return (PreCondition) getJump();
    }

    @Override
    protected Node evaluate(boolean condition) {
        logger.debug("Evaluation of precondition {} started. Condition is {}.", getIndex(),
                condition);

        Node target = null;
        if (condition)
            target = getNext();
        else
            target = getTarget();

        logger.debug("Evaludation of precondition {} finished. Next target is {}.", getIndex(),
                target.getIndex());

        return target;
    }
}
