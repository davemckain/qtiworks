/* $Id: TestProcessingContext.java 2782 2011-08-18 16:17:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.exception.QTIProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.internal.util.Pair;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemRefState;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;

import java.util.List;
import java.util.Map;

/**
 * FIXME: We need to merge this somehow with {@link ValidationContext}
 * 
 * @author   David McKain
 * @revision $Revision: 2782 $
 */
public interface TestProcessingContext extends TestValidationContext, ProcessingContext {

    Pair<VariableDeclaration, Map<AssessmentItemRefState, AssessmentItemRefController>> resolveDottedVariableReference(VariableReferenceIdentifier variableReferenceIdentifier);
    
    AssessmentItemRefController getItemRefController(AssessmentItemRefState itemRefState);
    
    Map<AssessmentItemRefState, AssessmentItemRefController> getItemRefControllers(AssessmentItemRef itemRef);
    
    List<AssessmentItemRefState> lookupItemRefStates();
    
    /** Called during outcome processing when there's a {@link QTIProcessingInterrupt} */
    void terminate();
    
}
