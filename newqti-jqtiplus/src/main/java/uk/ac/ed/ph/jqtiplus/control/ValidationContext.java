/* $Id: ValidationContext.java 2775 2011-08-15 07:59:08Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.xperimental.ReferencingException;

/**
 * FIXME: We need to merge this slightly with {@link ItemProcessingContext}
 * 
 * @author  David McKain
 * @version $Revision: 2775 $
 */
public interface ValidationContext {
    
    AssessmentObject getOwner();
    
    /**
     * Resolves a reference to a variable specified using a {@link VariableReferenceIdentifier}.
     * <p>
     * This encapsulates the special forms for referring to items within tests.
     * 
     * @throws ReferencingException
     */
    VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier)
            throws ReferencingException;
    
}
