/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.node.content.variable;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.control.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.TestValidationContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.XmlObject;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.InlineStatic;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * This is the only way how to show variables to actor.
 * 
 * @author Jonathon Hare
 */
public class PrintedVariable extends BodyElement implements FlowStatic, InlineStatic, TextOrVariable
{
    private static final long serialVersionUID = 1L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "printedVariable";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    /** Name of format attribute in xml schema. */
    public static final String ATTR_FORMAT_NAME = "format";
    /** Default value of format attribute. */
    public static final String ATTR_FORMAT_DEFAULT_VALUE = null;

    /** Name of base attribute in xml schema. */
    public static final String ATTR_BASE_NAME = "base";
    /** Default value of base attribute. */
    public static final int ATTR_BASE_DEFAULT_VALUE = 10;

    /**
     * Constructs block.
     *
     * @param parent parent of this block.
     */
    public PrintedVariable(XmlObject parent)
    {
        super(parent);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME));
        getAttributes().add(new StringAttribute(this, ATTR_FORMAT_NAME, ATTR_FORMAT_DEFAULT_VALUE));
        getAttributes().add(new IntegerAttribute(this, ATTR_BASE_NAME, ATTR_BASE_DEFAULT_VALUE));
    }

    @Override
    public String getClassTag()
    {
        return CLASS_TAG;
    }

    /**
     * Gets value of identifier attribute.
     *
     * @return value of identifier attribute
     * @see #setIdentifier
     */
    public Identifier getIdentifier()
    {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of identifier attribute.
     *
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    public void setIdentifier(Identifier identifier)
    {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    /**
     * Gets value of format attribute.
     *
     * @return value of format attribute
     * @see #setFormat
     */
    public String getFormat()
    {
        return getAttributes().getStringAttribute(ATTR_FORMAT_NAME).getValue();
    }

    /**
     * Sets new value of format attribute.
     *
     * @param format new value of format attribute
     * @see #getFormat
     */
    public void setFormat(String format)
    {
        getAttributes().getStringAttribute(ATTR_FORMAT_NAME).setValue(format);
    }

    /**
     * Gets value of base attribute.
     *
     * @return value of base attribute
     * @see #setBase
     */
    public Integer getBase()
    {
        return getAttributes().getIntegerAttribute(ATTR_BASE_NAME).getValue();
    }

    /**
     * Sets new value of base attribute.
     *
     * @param base new value of base attribute
     * @see #getBase
     */
    public void setBase(Integer base)
    {
        getAttributes().getIntegerAttribute(ATTR_BASE_NAME).setValue(base);
    }
    
    @Override
    public void validateAttributes(ValidationContext context, ValidationResult result) {
        super.validateAttributes(context, result);

        if (getIdentifier() != null) {
            VariableDeclaration declaration = null;
            if (context instanceof ItemValidationContext) {
                ItemValidationContext itemContext = (ItemValidationContext) context;
                AssessmentItem item = itemContext.getItem();
                declaration = item.getOutcomeDeclaration(getIdentifier());
                if (declaration == null) {
                    declaration = item.getTemplateDeclaration(getIdentifier());
                }
            }
            else {
                TestValidationContext testContext = (TestValidationContext) context;
                AssessmentTest test = testContext.getTest();
                declaration = test.getOutcomeDeclaration(getIdentifier());
            }
            if (declaration == null) {
                result.add(new ValidationError(this, "Cannot find " + OutcomeDeclaration.CLASS_TAG + ": " + getIdentifier()));
            }

// DM: For MathAssess, we're relaxing the following test to allow record variables (which is how we encode MathsContent variables)
// to be used here as well. We perhaps ought to test whether the record really is a MathsContent as well, but I don't want to
// pollute this code with too much MathAssess-specfic stuff.
//            if (declaration != null && declaration.getCardinality() != null && !declaration.getCardinality().isSingle())
//                result.add(new ValidationError(this, "Invalid cardinality. Expected: " + Cardinality.SINGLE + ", but found: " + declaration.getCardinality()));
            if (declaration != null && declaration.getCardinality() != null
                    && !(declaration.getCardinality().isSingle() || declaration.getCardinality().isRecord())) {
                result.add(new ValidationError(this, "Invalid cardinality. Expected: " + Cardinality.SINGLE + ", but found: " + declaration.getCardinality()
                        + ". (Note that " + Cardinality.RECORD + " is also supported, even though this is not strictly compliant with the spec.)"));
            }
        }
    }

    public SingleValue evaluate(ProcessingContext context) {
        Identifier identifier = getIdentifier();
        Value value = context.lookupVariable(identifier, VariableType.OUTCOME, VariableType.TEMPLATE);
        if (value==null) {
            throw new QTIEvaluationException("Outcome or template variable doesn't exist " + identifier);
        }
        if (!value.isNull() && !(value.getCardinality()==Cardinality.SINGLE && value.getCardinality()==Cardinality.RECORD)) {
            throw new QTIEvaluationException("Outcome or response variable is wrong cardinality: " +
                    value.getCardinality() + " Expected: " + Cardinality.SINGLE);
        }

        return (SingleValue) value;
    }

    @Override
    public List<? extends XmlNode> getChildren() {
        return null;
    }
}
