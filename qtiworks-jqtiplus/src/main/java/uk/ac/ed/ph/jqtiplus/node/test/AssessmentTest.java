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

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.OutcomeDeclarationGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.processing.OutcomeProcessingGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TestFeedbackGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TestPartGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableNode;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the <code>assessmentTest</code> QTI class
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 * @author David McKain
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

    /** (Implicit) declaration of <code>duration</code> variable */
    private final ResponseDeclaration durationResponseDeclaration;

    /** System ID of this RootNode (optional) */
    private URI systemId;

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

        /* create a special declaration for the internal duration variable */
        durationResponseDeclaration = new ResponseDeclaration(this);
        durationResponseDeclaration.setIdentifier(QtiConstants.VARIABLE_DURATION_IDENTIFIER);
        durationResponseDeclaration.setCardinality(Cardinality.SINGLE);
        durationResponseDeclaration.setBaseType(BaseType.FLOAT);
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
    public String getIdentifier() {
        return getAttributes().getStringAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    @Override
    public void setIdentifier(final String identifier) {
        getAttributes().getStringAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    @Override
    public List<TestPart> getChildAbstractParts() {
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

    public List<TestPart> getTestParts() {
        return getNodeGroups().getTestPartGroup().getTestParts();
    }

    public TestPart getTestPart(final Identifier testPartIdentifier) {
        for (final TestPart testPart : getTestParts()) {
            if (testPartIdentifier.equals(testPart.getIdentifier())) {
                return testPart;
            }
        }
        return null;
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

    public List<TestFeedback> searchTestFeedbacks(final TestFeedbackAccess testFeedbackAccess) {
        Assert.notNull(testFeedbackAccess, "testFeedbackAccess");
        final List<TestFeedback> result = new ArrayList<TestFeedback>();
        for (final TestFeedback testFeedback : getTestFeedbacks()) {
            if (testFeedbackAccess.equals(testFeedback.getTestFeedbackAccess())) {
                result.add(testFeedback);
            }
        }
        return result;
    }

    //---------------------------------------------------------------
    // Built-in variables

    /**
     * Returns {@link ResponseDeclaration} for the implicitly-defined
     * {@link QtiConstants#VARIABLE_DURATION_IDENTIFIER} variable
     */
    public ResponseDeclaration getDurationResponseDeclaration() {
        return durationResponseDeclaration;
    }

    //---------------------------------------------------------------

    @Override
    public String toString() {
        return super.toString()
                + "(systemId=" + systemId
                + ")";
    }
}
