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
package uk.ac.ed.ph.jqtiplus.node.expression.outcome;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.util.List;

/**
 * This class defines the concept of A sub-set of the items selected in an assessmentTest.
 * The attributes define criteria that must be matched by all members of the sub-set.
 * It is used to control A number of expressions in outcomeProcessing for returning information about
 * the test as A whole, or arbitrary subsets of it.
 *
 * @author Jiri Kajaba
 */
public abstract class ItemSubset extends AbstractExpression {

    private static final long serialVersionUID = -773393912325015078L;

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "sectionIdentifier";

    /** Name of includeCategory attribute in xml schema. */
    public static final String ATTR_INCLUDE_CATEGORIES_NAME = "includeCategory";

    /** Name of excludeCategory attribute in xml schema. */
    public static final String ATTR_EXCLUDE_CATEGORIES_NAME = "excludeCategory";

    /**
     * Constructs expression.
     *
     * @param parent parent of this expression
     */
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
        if (getSectionIdentifier() != null && context.getSubjectTest().lookupDescendentOrSelf(getSectionIdentifier()) == null) {
            context.fireValidationWarning(this, "Cannot find control object: " + getSectionIdentifier());
        }

        if (getNearestAncestor(OutcomeProcessing.class)==null) {
            context.fireValidationError(this, "Outcome expression can be used only in outcome processing.");
        }
    }
}
