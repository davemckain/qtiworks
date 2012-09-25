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

import uk.ac.ed.ph.jqtiplus.JqtiPlus;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.OutcomeDeclarationGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.processing.OutcomeProcessingGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TestFeedbackGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TestPartGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableNode;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.net.URI;
import java.util.List;

/**
 * A test is A group of assessmentItems with an associated set of rules that determine which of the items the candidate sees,
 * in what order, and in what way the candidate interacts with them. The rules describe the valid paths through the test,
 * when responses are submitted for response processing and when (if at all) feedback is to be given.
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public final class AssessmentTest extends ControlObject<String> implements AssessmentObject {

    private static final long serialVersionUID = 7638099859697920203L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "assessmentTest";

    /** Name of title attribute in xml schema. */
    public static final String ATTR_TITLE_NAME = "title";

    /** Name of toolName attribute in xml schema. */
    public static final String ATTR_TOOL_NAME_NAME = "toolName";

    /** Name of toolVersion attribute in xml schema. */
    public static final String ATTR_TOOL_VERSION_NAME = "toolVersion";

    /** Name of duration built-in variable. */
    public static final String VARIABLE_DURATION_NAME = "duration";

    /** Identifier of duration built-in variable. */
    public static final Identifier VARIABLE_DURATION_IDENTIFIER = new Identifier(VARIABLE_DURATION_NAME, false);

    private URI systemId;
    private ModelRichness modelRichness;

    //    /**
    //     * Provides current time.
    //     * This approach is because of testing. Timer for automated testing purposes can return discrete values
    //     * instead of real time. You do not need to modify timer in real test.
    //     */
    //    private Timer timer;

    /**
     * Constructs assessmentTest.
     */
    public AssessmentTest() {
        super(null, QTI_CLASS_NAME); // Test doesn't have any parent.

        getAttributes().add(new StringAttribute(this, IdentifiableNode.ATTR_IDENTIFIER_NAME, true));

        getAttributes().add(new StringAttribute(this, ATTR_TITLE_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_TOOL_NAME_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_TOOL_VERSION_NAME, false));

        getNodeGroups().add(0, new OutcomeDeclarationGroup(this));
        getNodeGroups().add(new TestPartGroup(this));
        getNodeGroups().add(new OutcomeProcessingGroup(this));
        getNodeGroups().add(new TestFeedbackGroup(this));

        //        timer = new Timer();
    }

    /**
     * Convenience constructor for assessmentTest.
     * Sets the JQTI toolName and toolVersion automatically
     *
     * @param identifier Value of identifier attribute
     * @param title Value of title attribute
     */
    public AssessmentTest(final String identifier, final String title) {
        this();

        setIdentifier(identifier);
        setTitle(title);

        setToolName(JqtiPlus.TOOL_NAME);
        setToolVersion(JqtiPlus.TOOL_VERSION);
    }

    @Override
    public AssessmentObjectType getType() {
        return AssessmentObjectType.ASSESSMENT_TEST;
    }

    @Override
    public URI getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(final URI systemId) {
        this.systemId = systemId;
    }


    @Override
    public ModelRichness getModelRichness() {
        return modelRichness;
    }

    @Override
    public void setModelRichness(final ModelRichness modelRichness) {
        this.modelRichness = modelRichness;
    }


    @Override
    public String getIdentifier() {
        return getAttributes().getStringAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    @Override
    public void setIdentifier(final String identifier) {
        getAttributes().getStringAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    @Override
    public List<TestPart> getChildren() {
        return getNodeGroups().getTestPartGroup().getTestParts();
    }

    @Override
    public String getTitle() {
        return getAttributes().getStringAttribute(ATTR_TITLE_NAME).getComputedValue();
    }

    @Override
    public void setTitle(final String title) {
        getAttributes().getStringAttribute(ATTR_TITLE_NAME).setValue(title);
    }


    @Override
    public String getToolName() {
        return getAttributes().getStringAttribute(ATTR_TOOL_NAME_NAME).getComputedValue();
    }

    @Override
    public void setToolName(final String toolName) {
        getAttributes().getStringAttribute(ATTR_TOOL_NAME_NAME).setValue(toolName);
    }


    @Override
    public String getToolVersion() {
        return getAttributes().getStringAttribute(ATTR_TOOL_VERSION_NAME).getComputedValue();
    }

    @Override
    public void setToolVersion(final String toolVersion) {
        getAttributes().getStringAttribute(ATTR_TOOL_VERSION_NAME).setValue(toolVersion);
    }


    @Override
    public List<OutcomeDeclaration> getOutcomeDeclarations() {
        return getNodeGroups().getOutcomeDeclarationGroup().getOutcomeDeclarations();
    }

    @Override
    public OutcomeDeclaration getOutcomeDeclaration(final Identifier identifier) {
        Assert.notNull(identifier);
        for (final OutcomeDeclaration declaration : getOutcomeDeclarations()) {
            if (declaration.getIdentifier() != null && declaration.getIdentifier().equals(identifier)) {
                return declaration;
            }
        }
        return null;
    }

    @Override
    public VariableDeclaration getVariableDeclaration(final Identifier identifier) {
        Assert.notNull(identifier);
        return getOutcomeDeclaration(identifier);
    }

    //    /**
    //     * Gets value of outcomeDeclaration with given identifier or null.
    //     *
    //     * @param identifier given identifier
    //     * @return value of outcomeDeclaration with given identifier or null
    //     */
    //    @ToRemove
    //    public Value getOutcomeValue(String identifier)
    //    {
    //        OutcomeDeclaration declaration = getOutcomeDeclaration(identifier);
    //
    //        return (declaration != null) ? declaration.getValue() : null;
    //    }

    public List<TestPart> getTestParts() {
        return getNodeGroups().getTestPartGroup().getTestParts();
    }


    public OutcomeProcessing getOutcomeProcessing() {
        return getNodeGroups().getOutcomeProcessingGroup().getOutcomeProcessing();
    }

    public void setOutcomeProcessing(final OutcomeProcessing outcomeProcessing) {
        getNodeGroups().getOutcomeProcessingGroup().setOutcomeProcessing(outcomeProcessing);
    }


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

    // timer has moved to AssessmentTestController
    //    /**
    //     * Gets current timer.
    //     *
    //     * @return current timer
    //     * @see #setTimer
    //     */
    //    public Timer getTimer()
    //    {
    //        return timer;
    //    }
    //
    //    /**
    //     * Sets new timer.
    //     *
    //     * @param timer new timer
    //     * @see #getTimer
    //     */
    //    public void setTimer(Timer timer)
    //    {
    //        this.timer = timer;
    //    }

    @Override
    public String toString() {
        return super.toString()
                + "(systemId=" + systemId
                + ",modelRichness=" + modelRichness
                + ")";
    }

    // (These have all moved to AssessmentTestController)
    //    /**
    //     * Returns current result of this test (only test itself, no items).
    //     *
    //     * @param parent parent of created result
    //     * @return current result of this test (only test itself, no items)
    //     */
    //    @ToRefactor
    //    public TestResult getTestResult(AssessmentResult parent)
    //    {
    //        TestResult result = new TestResult(parent);
    //
    //        result.setIdentifier(getIdentifier());
    //        result.setDateStamp(new Date());
    //
    //        for (OutcomeDeclaration declaration : getOutcomeDeclarations())
    //        {
    //            OutcomeVariable variable = new OutcomeVariable(result, declaration, null);
    //            result.getItemVariables().add(variable);
    //        }
    //
    //        result.getItemVariables().add(new OutcomeVariable(result, VARIABLE_DURATION_NAME, new DurationValue(getDuration() / 1000.0)));
    //
    //        for (TestPart testPart : getTestParts())
    //            processDuration(result, testPart);
    //
    //        return result;
    //    }
    //
    //    @ToRefactor
    //    private void processDuration(TestResult result, AbstractPart parent)
    //    {
    //        if (!(parent instanceof AssessmentItemRef))
    //        {
    //            String identifier = parent.getIdentifier() + "." + VARIABLE_DURATION_NAME;
    //            DurationValue duration = new DurationValue(parent.getDuration() / 1000.0);
    //
    //            result.getItemVariables().add(new OutcomeVariable(result, identifier, duration));
    //        }
    //
    //        for (AbstractPart child : parent.getChildren())
    //            processDuration(result, child);
    //    }
    //
    //    /**
    //     * Returns current result of whole assessment (test and all its items).
    //     *
    //     * @return current result of whole assessment (test and all its items)
    //     */
    //    @ToRefactor
    //    public AssessmentResult getAssessmentResult()
    //    {
    //        AssessmentResult result = new AssessmentResult();
    //
    //        result.setTestResult(getTestResult(result));
    //
    //        List<AssessmentItemRef> itemRefs = lookupItemRefs(null);
    //        int sequenceIndex = 1;
    //        for (AssessmentItemRef itemRef : itemRefs)
    //            result.getItemResults().addAll(itemRef.getItemResult(result, sequenceIndex++, null));
    //
    //        return result;
    //    }
}
