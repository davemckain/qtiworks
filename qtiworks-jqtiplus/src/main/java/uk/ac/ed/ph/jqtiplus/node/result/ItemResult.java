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
package uk.ac.ed.ph.jqtiplus.node.result;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.SessionStatusAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.group.result.CandidateCommentGroup;

/**
 * Result of one selected assessmentItem.
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public final class ItemResult extends AbstractResult implements ResultNode {

    private static final long serialVersionUID = -8853021160737704001L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "itemResult";

    /** Name of sequenceIndex attribute in xml schema. */
    public static final String ATTR_SEQUENCE_INDEX_NAME = "sequenceIndex";

    /** Name of sessionStatus attribute in xml schema. */
    public static final String ATTR_SESSION_STATUS_NAME = "sessionStatus";

    public ItemResult(final AssessmentResult parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerAttribute(this, ATTR_SEQUENCE_INDEX_NAME, false));
        getAttributes().add(new SessionStatusAttribute(this, ATTR_SESSION_STATUS_NAME, true));

        getNodeGroups().add(new CandidateCommentGroup(this));
    }

    public Integer getSequenceIndex() {
        return getAttributes().getIntegerAttribute(ATTR_SEQUENCE_INDEX_NAME).getComputedValue();
    }

    public void setSequenceIndex(final Integer sequenceIndex) {
        getAttributes().getIntegerAttribute(ATTR_SEQUENCE_INDEX_NAME).setValue(sequenceIndex);
    }


    public SessionStatus getSessionStatus() {
        return getAttributes().getSessionStatusAttribute(ATTR_SESSION_STATUS_NAME).getComputedValue();
    }

    public void setSessionStatus(final SessionStatus sessionStatus) {
        getAttributes().getSessionStatusAttribute(ATTR_SESSION_STATUS_NAME).setValue(sessionStatus);
    }


    public CandidateComment getCandidateComment() {
        return getNodeGroups().getCandidateCommentGroup().getCandidateComment();
    }

    public void setCandidateComment(final CandidateComment candidateComment) {
        getNodeGroups().getCandidateCommentGroup().setCandidateComment(candidateComment);
    }
}
