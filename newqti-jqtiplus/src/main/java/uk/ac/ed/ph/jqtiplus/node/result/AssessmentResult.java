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
package uk.ac.ed.ph.jqtiplus.node.result;

import uk.ac.ed.ph.jqtiplus.group.result.ContextGroup;
import uk.ac.ed.ph.jqtiplus.group.result.ItemResultGroup;
import uk.ac.ed.ph.jqtiplus.group.result.TestResultGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.RootNode;

import java.net.URI;
import java.util.List;

/**
 * An Assessment Result is used to report the results of A candidate's interaction with A test and/or one or more items
 * attempted. Information about the test is optional, in some systems it may be possible to interact with items that are
 * not organized into A test at all. For example, items that are organized with learning resources and presented
 * individually in A formative context.
 * 
 * @author Jiri Kajaba
 */
public class AssessmentResult extends AbstractNode implements RootNode {

    private static final long serialVersionUID = 7910600704491621036L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "assessmentResult";

    private URI systemId;

    /**
     * Constructs assessmentResult.
     */
    public AssessmentResult() {
        super(null);

        getNodeGroups().add(new ContextGroup(this));
        getNodeGroups().add(new TestResultGroup(this));
        getNodeGroups().add(new ItemResultGroup(this));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public URI getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(URI systemId) {
        this.systemId = systemId;
    }

    /**
     * Gets context child.
     * 
     * @return context child
     * @see #setContext
     */
    public Context getContext() {
        return getNodeGroups().getContextGroup().getContext();
    }

    /**
     * Sets new context child.
     * 
     * @param context new context child
     * @see #getContext
     */
    public void setContext(Context context) {
        getNodeGroups().getContextGroup().setContext(context);
    }

    /**
     * Gets testResult child.
     * 
     * @return testResult child
     * @see #setTestResult
     */
    public TestResult getTestResult() {
        return getNodeGroups().getTestResultGroup().getTestResult();
    }

    /**
     * Sets new testResult child.
     * 
     * @param testResult new testResult child
     * @see #getTestResult
     */
    public void setTestResult(TestResult testResult) {
        getNodeGroups().getTestResultGroup().setTestResult(testResult);
    }

    /**
     * Gets itemResult children.
     * 
     * @return itemResult children
     */
    public List<ItemResult> getItemResults() {
        return getNodeGroups().getItemResultGroup().getItemResults();
    }

    /**
     * Gets itemResult for given item.
     * 
     * @param identifier identifier of requested itemResult
     * @return itemResult for given item
     */
    public ItemResult getItemResult(String identifier) {
        for (final ItemResult itemResult : getItemResults()) {
            if (itemResult.getIdentifier() != null && itemResult.getIdentifier().equals(identifier)) {
                return itemResult;
            }
        }

        return null;
    }

    @Override
    public String toXmlString(int depth, boolean printDefaultAttributes) {
        final StringBuilder builder = new StringBuilder();

        builder.append(RootNode.XML);
        builder.append(NEW_LINE);
        builder.append(super.toXmlString(depth, printDefaultAttributes));

        return builder.toString();
    }

    @Override
    public String toString() {
        return super.toString() + "(systemId=" + systemId + ")";
    }

    //    /**
    //     * Shows how to use assessmentResult outside of library.
    //     *
    //     * @param args ignored
    //     * @throws URISyntaxException ignored
    //     */
    //    public static void main(String[] args) throws URISyntaxException
    //    {
    //        // Only for this example. You don't need to do this in your code.
    //        AssessmentResult result = new AssessmentResult();
    //
    //        // Call this to get assessmentResult.
    //        // AssessmentResult result = test.getAssessmentResult();
    //
    //        { // You must set contex into assessmentResult.
    //            Context context = new Context(result);
    //            result.setContext(context);
    //
    //            // You should set sessionIdentifier into contex (it is not required according to current specification).
    //            SessionIdentifier sessionIdentifier = new SessionIdentifier(context);
    //            context.getSessionIdentifiers().add(sessionIdentifier);
    //            sessionIdentifier.setSourceId(new URI("SOURCE_ID_URI"));
    //            sessionIdentifier.setIdentifier("IDENTIFIER_OF_SESSION");
    //        }
    //
    //        { // Do not forget to set this on assessmentItemRef.
    //            AssessmentItemRef itemRef = new AssessmentItemRef(null); // Only for this example.
    //
    //            itemRef.setSessionStatus(SessionStatus.PENDING_RESPONSE_PROCESSING); // Set appropriate sessionStatus.
    //            itemRef.setCandidateComment("Candidate comment"); // Set candidate's comment if required.
    //        }
    //
    //        // That's it. You don't need to do anything else.
    //    }
}
