/* $Id: ItemProcessingContext.java 2778 2011-08-17 13:29:05Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * FIXME: We need to merge this somehow with {@link ValidationContext}
 * 
 * @author   dmckain
 * @revision $Revision: 2778 $
 */
public interface ItemProcessingContext extends ItemValidationContext, ProcessingContext {
    
    Value computeDefaultValue(Identifier identifier);
    
    Value computeCorrectReponse(Identifier responseIdentifier);
    
	void setTemplateValue(TemplateDeclaration outcomeDeclaration, Value value);
	
	void setVariableValue(VariableDeclaration variableDeclaration, Value value);
	
	void setOverriddenResponseDefaultValue(ResponseDeclaration responseDeclaration, Value value);
	
	void setOverriddenCorrectResponseValue(ResponseDeclaration responseDeclaration, Value value);
	
	void setOverriddenOutcomeDefaultValue(OutcomeDeclaration outcomeDeclaration, Value value);

}
