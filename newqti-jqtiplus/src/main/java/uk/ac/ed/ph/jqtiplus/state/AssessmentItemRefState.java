/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.types.Identifier;


import java.util.Collections;

/**
 * Encapsulates the runtime state of an {@link AssessmentItemRef}
 * 
 * @author David McKain
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class AssessmentItemRefState extends SectionPartState {

	private static final long serialVersionUID = -1407010070268750764L;
	
	private final AssessmentItemState itemState;
	
	private boolean presented;
	private boolean responded;
	private boolean skipped;
	private boolean timedOut;

	private SessionStatus sessionStatus;
	private String candidateComment;

	private final TimeRecord timeRecord;

	public AssessmentItemRefState(AssessmentTestState testState, Identifier identifier, int siblingIndex, AssessmentItemState itemState) {
		super(testState, identifier, siblingIndex, Collections.<SectionPartState>emptyList());
		this.itemState = itemState;
		this.sessionStatus = SessionStatus.INITIAL;
		this.timeRecord = new TimeRecord(this);
	}
	
	public AssessmentItemState getItemState() {
		return itemState;
	}

	public TimeRecord getTimeRecord() {
		return timeRecord;
	}
	   
	//---------------------------------------------------------------

	public boolean isPresented() {
		return presented;
	}

	public void setPresented(boolean presented) {
		this.presented = presented;
	}


	public boolean isResponded() {
		return responded;
	}

	public void setResponded(boolean responded) {
		this.responded = responded;
	}


	public boolean isSkipped() {
		return skipped;
	}

	public void setSkipped(boolean skipped) {
		this.skipped = skipped;
	}


	public boolean isTimedOut() {
		return timedOut;
	}

	public void setTimedOut(boolean timedOut) {
		this.timedOut = timedOut;
	}


	public SessionStatus getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(SessionStatus sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	
	public String getCandidateComment() {
		return candidateComment;
	}

	public void setCandidateComment(String candidateComment) {
		this.candidateComment = candidateComment;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + hashCode()
            + "(testIdentifier=" + testIdentifier
            + ",siblingIndex=" + siblingIndex
			+ ",presented=" + presented
			+ ",responded=" + responded
			+ ",finished=" + finished
			+ ",skipped=" + skipped
			+ ",timedOut=" + timedOut
			+ ",sessionStatus=" + sessionStatus
			+ ",candidateComment=" + candidateComment
			+ ",timeRecord=" + timeRecord
			+ ")";
	}
}
