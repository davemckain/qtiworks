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

import uk.ac.ed.ph.jqtiplus.group.result.ContextGroup;
import uk.ac.ed.ph.jqtiplus.group.result.ItemResultGroup;
import uk.ac.ed.ph.jqtiplus.group.result.TestResultGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.RootNode;

import java.net.URI;
import java.util.List;

/**
 * An Assessment Result is used to report the results of a candidate's interaction with a test and/or one or more items
 * attempted. Information about the test is optional, in some systems it may be possible to interact with items that are
 * not organized into a test at all. For example, items that are organized with learning resources and presented
 * individually in a formative context.
 *
 * @author Jiri Kajaba
 */
public final class AssessmentResult extends AbstractNode implements RootNode, ResultNode {

    private static final long serialVersionUID = 7910600704491621036L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "assessmentResult";

    private URI systemId;

    public AssessmentResult() {
        super(null, QTI_CLASS_NAME);

        getNodeGroups().add(new ContextGroup(this));
        getNodeGroups().add(new TestResultGroup(this));
        getNodeGroups().add(new ItemResultGroup(this));
    }

    @Override
    public URI getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(final URI systemId) {
        this.systemId = systemId;
    }


    public Context getContext() {
        return getNodeGroups().getContextGroup().getContext();
    }

    public void setContext(final Context context) {
        getNodeGroups().getContextGroup().setContext(context);
    }


    public TestResult getTestResult() {
        return getNodeGroups().getTestResultGroup().getTestResult();
    }

    public void setTestResult(final TestResult testResult) {
        getNodeGroups().getTestResultGroup().setTestResult(testResult);
    }


    public List<ItemResult> getItemResults() {
        return getNodeGroups().getItemResultGroup().getItemResults();
    }

    /**
     * Gets itemResult for given item.
     *
     * @param identifier identifier of requested itemResult
     * @return itemResult for given item, null if given item is not found.
     */
    public ItemResult getItemResult(final String identifier) {
        Assert.notNull(identifier);
        for (final ItemResult itemResult : getItemResults()) {
            if (identifier.equals(itemResult.getIdentifier())) {
                return itemResult;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString()
                + "(systemId=" + systemId
                + ")";
    }
}
