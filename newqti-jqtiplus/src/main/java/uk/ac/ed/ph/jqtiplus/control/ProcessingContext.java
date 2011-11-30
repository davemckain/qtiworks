/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;


import java.io.Serializable;
import java.util.Map;

/**
 * FIXME: This now extends {@link AssessmentItemOrTestLogic}, since I cast this a few times. There's scope
 * for refactoring and tidying things up, as some "evaluation"-type methods take a {@link ProcessingContext}
 * whereas others take {@link AssessmentItemOrTestLogic} or related. It's a bit messy, isn't it?!
 * @author dmckain
 *
 */
public interface ProcessingContext extends ValidationContext, Serializable {
    
    Value lookupVariable(VariableDeclaration variableDeclaration);
    
    /**
     * NB: Returns null if variable is not defined!
     */
    Value lookupVariable(Identifier identifier);
    
    Value lookupVariable(Identifier identifier, VariableType... permittedTypes);
    
	void setOutcomeValue(OutcomeDeclaration outcomeDeclaration, Value value);
	
	void setOutcomeValueFromLookupTable(OutcomeDeclaration outcomeDeclaration, NumberValue value);
	
	/** NB: Returns null if expression is not found or if value has not been set */
    Value getExpressionValue(Expression expression);
    
    Map<String, Value> exportExpressionValues();
    
    void setExpressionValue(Expression expression, Value value);

}
