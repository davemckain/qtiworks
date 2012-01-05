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
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;

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

    /** Default value of identifier attribute. */
    public static final Identifier ATTR_IDENTIFIER_DEFAULT_VALUE = null;

    /** Name of includeCategory attribute in xml schema. */
    public static final String ATTR_INCLUDE_CATEGORIES_NAME = "includeCategory";

    /** Default value of includeCategory attribute. */
    public static final List<String> ATTR_INCLUDE_CATEGORIES_DEFAULT_VALUE = null;

    /** Name of excludeCategory attribute in xml schema. */
    public static final String ATTR_EXCLUDE_CATEGORIES_NAME = "excludeCategory";

    /** Default value of excludeCategory attribute. */
    public static final List<String> ATTR_EXCLUDE_CATEGORIES_DEFAULT_VALUE = null;

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public ItemSubset(ExpressionParent parent) {
        super(parent);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, ATTR_IDENTIFIER_DEFAULT_VALUE));
        getAttributes().add(new StringMultipleAttribute(this, ATTR_INCLUDE_CATEGORIES_NAME, ATTR_INCLUDE_CATEGORIES_DEFAULT_VALUE));
        getAttributes().add(new StringMultipleAttribute(this, ATTR_EXCLUDE_CATEGORIES_NAME, ATTR_EXCLUDE_CATEGORIES_DEFAULT_VALUE));
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    /**
     * Gets value of identifier attribute.
     * 
     * @return value of identifier attribute
     * @see #setIdentifier
     */
    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of identifier attribute.
     * 
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    public void setIdentifier(Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    /**
     * Gets value of includeCategory attribute.
     * 
     * @return value of includeCategory attribute
     */
    public List<String> getIncludeCategories() {
        return getAttributes().getStringMultipleAttribute(ATTR_INCLUDE_CATEGORIES_NAME).getValues();
    }

    /**
     * Gets value of excludeCategory attribute.
     * 
     * @return value of excludeCategory attribute
     */
    public List<String> getExcludeCategories() {
        return getAttributes().getStringMultipleAttribute(ATTR_EXCLUDE_CATEGORIES_NAME).getValues();
    }

    @Override
    protected void validateAttributes(ValidationContext context, ValidationResult result) {
        if (getIdentifier() != null && getRootNode(AssessmentTest.class).lookupDescendentOrSelf(getIdentifier()) == null) {
            result.add(new ValidationWarning(this, "Cannot find control object: " + getIdentifier()));
        }
    }

    @Override
    public void validate(ValidationContext context, ValidationResult result) {
        XmlNode parent = getParent();

        while (parent != null) {
            if (parent instanceof OutcomeProcessing) {
                break;
            }
            parent = parent.getParent();
        }

        if (parent == null) {
            result.add(new ValidationError(this, "Outcome expression can be used only in outcome processing."));
        }
    }
}
