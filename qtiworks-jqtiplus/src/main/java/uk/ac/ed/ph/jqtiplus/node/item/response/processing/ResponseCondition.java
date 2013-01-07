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
package uk.ac.ed.ph.jqtiplus.node.item.response.processing;

import uk.ac.ed.ph.jqtiplus.exception.QtiProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseElseGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseElseIfGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseIfGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of if-elseif-else response rule (behaviour is same like in other programming languages).
 * <p>
 * If the expression given in the responseIf or responseElseIf evaluates to true then the sub-rules contained within it are followed and any following
 * responseElseIf or responseElse parts are ignored for this response condition.
 * <p>
 * If the expression given in the responseIf or responseElseIf does not evaluate to true then consideration passes to the next responseElseIf or, if there are
 * no more responseElseIf parts then the sub-rules of the responseElse are followed (if specified).
 *
 * @author Jonathon Hare
 */
public final class ResponseCondition extends ResponseRule {

    private static final long serialVersionUID = -7662549073016916667L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "responseCondition";

    public ResponseCondition(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getNodeGroups().add(new ResponseIfGroup(this));
        getNodeGroups().add(new ResponseElseIfGroup(this));
        getNodeGroups().add(new ResponseElseGroup(this));
    }


    public ResponseIf getResponseIf() {
        return getNodeGroups().getResponseIfGroup().getResponseIf();
    }

    public void setResponseIf(final ResponseIf responseIf) {
        getNodeGroups().getResponseIfGroup().setResponseIf(responseIf);
    }


    public List<ResponseElseIf> getResponseElseIfs() {
        return getNodeGroups().getResponseElseIfGroup().getResponseElseIfs();
    }


    public ResponseElse getResponseElse() {
        return getNodeGroups().getResponseElseGroup().getResponseElse();
    }

    public void setResponseElse(final ResponseElse responseElse) {
        getNodeGroups().getResponseElseGroup().setResponseElse(responseElse);
    }


    private List<ResponseConditionChild> getConditionChildren() {
        final List<ResponseConditionChild> children = new ArrayList<ResponseConditionChild>();

        children.add(getResponseIf());
        children.addAll(getResponseElseIfs());
        if (getResponseElse() != null) {
            children.add(getResponseElse());
        }

        return children;
    }

    @Override
    public void evaluate(final ItemProcessingContext context) throws QtiProcessingInterrupt {
        for (final ResponseConditionChild child : getConditionChildren()) {
            if (child.evaluate(context)) {
                return;
            }
        }
    }
}
