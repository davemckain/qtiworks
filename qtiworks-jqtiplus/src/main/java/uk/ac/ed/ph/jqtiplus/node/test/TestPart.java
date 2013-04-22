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

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.NavigationModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.SubmissionModeAttribute;
import uk.ac.ed.ph.jqtiplus.group.test.AssessmentSectionGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TestFeedbackGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the <code>testPart</code> class
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public final class TestPart extends AbstractPart {

    private static final long serialVersionUID = 1808165853579519400L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "testPart";

    /** Name of navigationMode attribute in xml schema. */
    public static final String ATTR_NAVIGATION_MODE_NAME = NavigationMode.QTI_CLASS_NAME;

    /** Name of submissionMode attribute in xml schema. */
    public static final String ATTR_SUBMISSION_MODE_NAME = SubmissionMode.QTI_CLASS_NAME;

    public TestPart(final AssessmentTest parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new NavigationModeAttribute(this, ATTR_NAVIGATION_MODE_NAME, true));
        getAttributes().add(new SubmissionModeAttribute(this, ATTR_SUBMISSION_MODE_NAME, true));

        getNodeGroups().add(new AssessmentSectionGroup(this));
        getNodeGroups().add(new TestFeedbackGroup(this));
    }

    @Override
    public AssessmentTest getParent() {
        return (AssessmentTest) super.getParent();
    }

    @Override
    public List<AssessmentSection> getChildAbstractParts() {
        return getAssessmentSections();
    }


    public NavigationMode getNavigationMode() {
        return getAttributes().getNavigationModeAttribute(ATTR_NAVIGATION_MODE_NAME).getComputedValue();
    }

    public void setNavigationMode(final NavigationMode navigationMode) {
        getAttributes().getNavigationModeAttribute(ATTR_NAVIGATION_MODE_NAME).setValue(navigationMode);
    }


    public SubmissionMode getSubmissionMode() {
        return getAttributes().getSubmissionModeAttribuye(ATTR_SUBMISSION_MODE_NAME).getComputedValue();
    }

    public void setSubmissionMode(final SubmissionMode submissionMode) {
        getAttributes().getSubmissionModeAttribuye(ATTR_SUBMISSION_MODE_NAME).setValue(submissionMode);
    }

    public List<AssessmentSection> getAssessmentSections() {
        return getNodeGroups().getAssessmentSectionGroup().getAssessmentSections();
    }

    public List<TestFeedback> getTestFeedbacks() {
        return getNodeGroups().getTestFeedbackGroup().getTestFeedbacks();
    }

    public List<TestFeedback> searchTestFeedbacks(final TestFeedbackAccess testFeedbackAccess) {
        Assert.notNull(testFeedbackAccess, "testFeedbackAccess");
        final List<TestFeedback> result = new ArrayList<TestFeedback>();
        for (final TestFeedback testFeedback : getTestFeedbacks()) {
            if (testFeedbackAccess.equals(testFeedback.getTestFeedbackAccess())) {
                result.add(testFeedback);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Gets all visible testFeedbacks with given access.
     *
     * @param testFeedbackAccess given access
     * @return all testFeedbacks with given access
     */
    public List<TestFeedback> searchVisibleTestFeedbacks(final TestSessionState testSessionState, final TestFeedbackAccess testFeedbackAccess) {
        Assert.notNull(testSessionState, "testSessionState");
        Assert.notNull(testFeedbackAccess, "testFeedbackAccess");
        final List<TestFeedback> result = new ArrayList<TestFeedback>();
        for (final TestFeedback testFeedback : getTestFeedbacks()) {
            if (testFeedback.isVisible(testSessionState, testFeedbackAccess)) {
                result.add(testFeedback);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns true if jumps (preConditions and branchRules) are enabled; false otherwise.
     * Jumps (preConditions and branchRules) are enabled only in linear individual mode.
     * This is only convenient method for testing linear individual mode.
     *
     * @return true if jumps (preCondition and branchRule) are enabled; false otherwise
     */
    public boolean areJumpsEnabled() {
        return getNavigationMode()==NavigationMode.LINEAR && getSubmissionMode()==SubmissionMode.INDIVIDUAL;
    }
}
