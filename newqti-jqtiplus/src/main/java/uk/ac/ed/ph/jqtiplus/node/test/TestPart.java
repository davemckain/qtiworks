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

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.NavigationModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.SubmissionModeAttribute;
import uk.ac.ed.ph.jqtiplus.group.test.AssessmentSectionGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TestFeedbackGroup;

import java.util.List;

/**
 * Part of assessment test.
 * 
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public class TestPart extends AbstractPart {

    private static final long serialVersionUID = 1808165853579519400L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "testPart";

    /** Name of navigationMode attribute in xml schema. */
    public static final String ATTR_NAVIGATION_MODE_NAME = NavigationMode.CLASS_TAG;

    /** Name of submissionMode attribute in xml schema. */
    public static final String ATTR_SUBMISSION_MODE_NAME = SubmissionMode.CLASS_TAG;

    /** ItemSessionControl with default values. It is used if no other is provided. */
    private final ItemSessionControl defaultItemSessionControl;

    /**
     * Constructs testPart.
     * 
     * @param parent parent of this testPart.
     */
    public TestPart(AssessmentTest parent) {
        super(parent);

        getAttributes().add(new NavigationModeAttribute(this, ATTR_NAVIGATION_MODE_NAME));
        getAttributes().add(new SubmissionModeAttribute(this, ATTR_SUBMISSION_MODE_NAME));

        getNodeGroups().add(new AssessmentSectionGroup(this));
        getNodeGroups().add(new TestFeedbackGroup(this));

        defaultItemSessionControl = new ItemSessionControl(this);
    }

    @Override
    public AssessmentTest getParent() {
        return (AssessmentTest) super.getParent();
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public List<AssessmentSection> getChildren() {
        return getAssessmentSections();
    }

    /**
     * Gets value of navigationMode attribute.
     * 
     * @return value of navigationMode attribute
     * @see #setNavigationMode
     */
    public NavigationMode getNavigationMode() {
        return getAttributes().getNavigationModeAttribute(ATTR_NAVIGATION_MODE_NAME).getValue();
    }

    /**
     * Sets new value of navigationMode attribute.
     * 
     * @param navigationMode new value of navigationMode attribute
     * @see #getNavigationMode
     */
    public void setNavigationMode(NavigationMode navigationMode) {
        getAttributes().getNavigationModeAttribute(ATTR_NAVIGATION_MODE_NAME).setValue(navigationMode);
    }

    /**
     * Gets value of submissionMode attribute.
     * 
     * @return value of submissionMode attribute
     * @see #setSubmissionMode
     */
    public SubmissionMode getSubmissionMode() {
        return getAttributes().getSubmissionModeAttribuye(ATTR_SUBMISSION_MODE_NAME).getValue();
    }

    /**
     * Sets new value of submissionMode attribute.
     * 
     * @param submissionMode new value of submissionMode attribute
     * @see #getSubmissionMode
     */
    public void setSubmissionMode(SubmissionMode submissionMode) {
        getAttributes().getSubmissionModeAttribuye(ATTR_SUBMISSION_MODE_NAME).setValue(submissionMode);
    }

    /**
     * Gets itemSessionControl child if specified; defaultItemSessionControl otherwise.
     * 
     * @return itemSessionControl child if specified; defaultItemSessionControl otherwise
     */
    @Override
    public ItemSessionControl getItemSessionControl() {
        final ItemSessionControl itemSessionControl = getItemSessionControlNode();

        return itemSessionControl != null ? itemSessionControl : defaultItemSessionControl;
    }

    /**
     * Gets assessmentSection children.
     * 
     * @return assessmentSection children
     */
    public List<AssessmentSection> getAssessmentSections() {
        return getNodeGroups().getAssessmentSectionGroup().getAssessmentSections();
    }

    /**
     * Gets testFeedback children.
     * 
     * @return testFeedback children
     */
    public List<TestFeedback> getTestFeedbacks() {
        return getNodeGroups().getTestFeedbackGroup().getTestFeedbacks();
    }

    //    /**
    //     * Gets all viewable testFeedbacks with given access.
    //     * Tests if feedbacks can be displayed.
    //     *
    //     * @param requestedAccess given access
    //     * @return all testFeedbacks with given access
    //     */
    //    @ToRefactor
    //    public List<TestFeedback> getTestFeedbacks(TestFeedbackAccess requestedAccess)
    //    {
    //        List<TestFeedback> result = new ArrayList<TestFeedback>();
    //
    //        for (TestFeedback feedback : getTestFeedbacks())
    //            if (feedback.isVisible(requestedAccess))
    //                result.add(feedback);
    //
    //        return result;
    //    }

    /**
     * Returns true if jumps (preConditions and branchRules) are enabled; false otherwise.
     * Jumps (preConditions and branchRules) are enabled only in linear individual mode.
     * This is only convenient method for testing linear individual mode.
     * 
     * @return true if jumps (preCondition and branchRule) are enabled; false otherwise
     */
    public boolean areJumpsEnabled() {
        return getNavigationMode() == NavigationMode.LINEAR && getSubmissionMode() == SubmissionMode.INDIVIDUAL;
    }
}
