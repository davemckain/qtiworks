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
package org.qtitools.qti.node.test.flow;

import uk.ac.ed.ph.jqtiplus.node.QtiNode;

import java.io.Serializable;

/**
 * Basic abstract node of linked list.
 * <p>
 * Contains index (position in linked list; first position = 0), references to
 * previous and next node in linked list and assessment object (test, test part,
 * section, item reference, precondition or branch rule).
 * 
 * @author Jiri Kajaba
 */
public abstract class Node implements Serializable {

    private static final long serialVersionUID = 5652335587772504040L;

    private int index;

    private Node prev;

    private Node next;

    private final QtiNode object;

    /**
     * Constructs node.
     * 
     * @param prev previous node in linked list
     * @param object assessment object (test, test part, section, item
     *            reference, precondition or branch rule)
     */
    protected Node(Node prev, QtiNode object) {
        if (prev != null) {
            index = prev.index + 1;
            this.prev = prev;
            prev.next = this;
        }
        this.object = object;
    }

    /**
     * Returns true if this node is border node; false otherwise.
     * <p>
     * Border node is node on border. It is either start or end node, and it is
     * not middle node.
     * 
     * @return true if this node is border node; false otherwise
     */
    public boolean isBorder() {
        return false;
    }

    /**
     * Returns true if this node is start node; false otherwise.
     * <p>
     * Start node is node on beginning of test, test part, section or item
     * reference.
     * 
     * @return true if this node is start node; false otherwise
     */
    public boolean isStart() {
        return false;
    }

    /**
     * Returns true if this node is end node; false otherwise.
     * <p>
     * End node is node on end of test, test part, section or item reference.
     * 
     * @return true is this node is end node; false otherwise
     */
    public boolean isEnd() {
        return false;
    }

    /**
     * Returns true if this node is middle node; false otherwise.
     * <p>
     * Middle node is node which is between start and end node. There can be
     * more middle nodes between one start and end node.
     * 
     * @return true is this node is middle node; false otherwise
     */
    public boolean isMiddle() {
        return false;
    }

    /**
     * Returns true if this node is item reference node; false otherwise.
     * <p>
     * Item reference node is middle node with item reference object.
     * 
     * @return true if this node is item reference node; false otherwise
     */
    public boolean isItemRef() {
        return false;
    }

    /**
     * Returns true if this node is jump node; false otherwise.
     * <p>
     * Jump node is middle node with precondition or branch rule object.
     * 
     * @return true if this node is jump node; false otherwise
     */
    public boolean isJump() {
        return false;
    }

    /**
     * Returns true if this node is precondition node; false otherwise.
     * <p>
     * Precondition node is jump node with precondition object.
     * 
     * @return true is this node is precondition node; false otherwise
     */
    public boolean isPreCondition() {
        return false;
    }

    /**
     * Returns true if this node is branch rule node; false otherwise.
     * <p>
     * Branch rule node is jump node with branch rule object.
     * 
     * @return true is this node is branch rule node; false otherwise
     */
    public boolean isBranchRule() {
        return false;
    }

    /**
     * Gets index (position in linked list) of this node.
     * <p>
     * Index of first node in linked list is 0.
     * 
     * @return index (position in linked list) of this node
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets previous node in linked list (can be null).
     * 
     * @return previous node in linked list (can be null)
     */
    public Node getPrev() {
        return prev;
    }

    /**
     * Gets next node in linked list (can be null).
     * 
     * @return next node in linked list (can be null)
     */
    public Node getNext() {
        return next;
    }

    /**
     * Gets assessment object (test, test part, section, item reference,
     * precondition or branch rule) of this node.
     * 
     * @return assessment object (test, test part, section, item reference,
     *         precondition or branch rule) of this node
     */
    public QtiNode getObject() {
        return object;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(index);

        builder.append(": ");
        if (isStart()) {
            builder.append("START");
        }
        else if (isEnd()) {
            builder.append("END");
        }
        else if (isItemRef()) {
            builder.append("ITEM_REF");
        }
        else if (isPreCondition()) {
            builder.append("CONDITION");
        }
        else if (isJump()) {
            builder.append("BRANCH");
        }
        else {
            builder.append("UNKNOWN");
        }

        builder.append("(");
        builder.append(object.computeXPathComponent());

        builder.append(") - PREV = ");
        builder.append(prev != null ? prev.index : "NONE");
        builder.append(", NEXT = ");
        builder.append(next != null ? next.index : "NONE");

        return builder.toString();
    }
}
