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
package uk.ac.ed.ph.jqtiplus.node.expression.outcome;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * This class defines the concept of a sub-set of the items selected in an assessmentTest.
 * The attributes define criteria that must be matched by all members of the sub-set.
 * It is used to control a number of expressions in outcomeProcessing for returning information about
 * the test as a whole, or arbitrary subsets of it.
 *
 * @author Jiri Kajaba
 */
public abstract class ItemSubset extends AbstractFunctionalExpression {

    private static final long serialVersionUID = -773393912325015078L;

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "sectionIdentifier";

    /** Name of includeCategory attribute in xml schema. */
    public static final String ATTR_INCLUDE_CATEGORIES_NAME = "includeCategory";

    /** Name of excludeCategory attribute in xml schema. */
    public static final String ATTR_EXCLUDE_CATEGORIES_NAME = "excludeCategory";

    public ItemSubset(final ExpressionParent parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, false));
        getAttributes().add(new StringMultipleAttribute(this, ATTR_INCLUDE_CATEGORIES_NAME, false));
        getAttributes().add(new StringMultipleAttribute(this, ATTR_EXCLUDE_CATEGORIES_NAME, false));
    }

    public Identifier getSectionIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    public void setSectionIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }


    public List<String> getIncludeCategories() {
        return getAttributes().getStringMultipleAttribute(ATTR_INCLUDE_CATEGORIES_NAME).getComputedValue();
    }

    public void setIncludeCategories(final List<String> value) {
        getAttributes().getStringMultipleAttribute(ATTR_INCLUDE_CATEGORIES_NAME).setValue(value);
    }


    public List<String> getExcludeCategories() {
        return getAttributes().getStringMultipleAttribute(ATTR_EXCLUDE_CATEGORIES_NAME).getComputedValue();
    }

    public void setExcludeCategories(final List<String> value) {
        getAttributes().getStringMultipleAttribute(ATTR_EXCLUDE_CATEGORIES_NAME).setValue(value);
    }


    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        if (context instanceof TestValidationContext) {
            final TestValidationContext testValidationContext = (TestValidationContext) context;
            final Identifier sectionIdentifier = getSectionIdentifier();
            if (sectionIdentifier!=null) {
                final AbstractPart target = testValidationContext.getSubjectTest().lookupFirstDescendant(sectionIdentifier);
                if (target!=null) {
                    if (!(target instanceof AssessmentSection)) {
                        context.fireValidationError(this, "Target of " + sectionIdentifier + " is not an assessmentSection");
                    }
                }
                else {
                    context.fireValidationError(this, "Cannot find target of sectionIdentifier " + sectionIdentifier);
                }
            }
        }
        else {
            context.fireValidationError(this, "Outcome expression can be used only in outcome processing.");
        }
    }

    @Override
    protected final Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        final TestProcessingContext testProcessingContext = (TestProcessingContext) context;

        final List<TestPlanNode> matchedTestPlanNodes = testProcessingContext.computeItemSubset(getSectionIdentifier(), getIncludeCategories(), getExcludeCategories());
        return handleSubset(testProcessingContext, matchedTestPlanNodes);
    }

    protected abstract Value handleSubset(TestProcessingContext testProcessingContext, List<TestPlanNode> matchedTestPlanNodes);
}
