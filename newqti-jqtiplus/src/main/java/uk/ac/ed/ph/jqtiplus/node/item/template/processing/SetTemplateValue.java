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

package uk.ac.ed.ph.jqtiplus.node.item.template.processing;

import uk.ac.ed.ph.jqtiplus.control.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.node.XmlObject;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;



/**
 * The setTemplateValue rule sets the value of A template variable to the value 
 * obtained from the associated expression. A template variable can be updated 
 * with reference to A previously assigned value, in other words, the template 
 * variable being set may appear in the expression where it takes the value 
 * previously assigned to it.
 * 
 * @author Jonathon Hare
 */
public class SetTemplateValue extends ProcessTemplateValue
{
    private static final long serialVersionUID = 1L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "setTemplateValue";

    /**
     * Constructs rule.
     *
     * @param parent parent of this rule
     */
    public SetTemplateValue(XmlObject parent)
    {
        super(parent);
    }

    @Override
    public String getClassTag()
    {
        return CLASS_TAG;
    }

    @Override
    public Cardinality[] getRequiredCardinalities(ValidationContext context, int index)
    {
        ItemValidationContext itemContext = (ItemValidationContext) context;
        if (getIdentifier() != null)
        {
            TemplateDeclaration declaration = itemContext.getItem().getTemplateDeclaration(getIdentifier());
            if (declaration != null && declaration.getCardinality() != null)
                return new Cardinality[] {declaration.getCardinality()};
        }

        return Cardinality.values();
    }

    @Override
    public BaseType[] getRequiredBaseTypes(ValidationContext context, int index)
    {
        ItemValidationContext itemContext = (ItemValidationContext) context;
        if (getIdentifier() != null)
        {
            TemplateDeclaration declaration = itemContext.getItem().getTemplateDeclaration(getIdentifier());
            if (declaration != null && declaration.getBaseType() != null)
                return new BaseType[] {declaration.getBaseType()};
        }

        return BaseType.values();
    }

    @Override
    public void evaluate(ProcessingContext context) throws RuntimeValidationException {
        ItemProcessingContext itemContext = (ItemProcessingContext) context;
        Value value = getExpression().evaluate(context);

        TemplateDeclaration declaration = itemContext.getItem().getTemplateDeclaration(getIdentifier());
        if (declaration == null) {
            throw new QTIEvaluationException("Cannot find " + TemplateDeclaration.CLASS_TAG + ": " + getIdentifier());
        }
        
        ((ItemProcessingContext) context).setTemplateValue(declaration, value);
    }
    
    @Override
    protected void validateAttributes(ValidationContext context, ValidationResult result)
    {
        ItemValidationContext itemContext = (ItemValidationContext) context;
        super.validateAttributes(context, result);

        if (getIdentifier() != null && itemContext.getItem().getTemplateDeclaration(getIdentifier()) == null)
            result.add(new ValidationError(this, "Cannot find " + TemplateDeclaration.CLASS_TAG + ": " + getIdentifier()));
    }
}
