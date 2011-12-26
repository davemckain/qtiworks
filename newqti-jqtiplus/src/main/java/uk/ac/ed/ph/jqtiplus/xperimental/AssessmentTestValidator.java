/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xperimental;

import uk.ac.ed.ph.jqtiplus.control.TestValidationContext;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author  David McKain
 * @version $Revision: 2801 $
 */
public final class AssessmentTestValidator implements TestValidationContext {
    
    private static final Logger logger = LoggerFactory.getLogger(AssessmentTestValidator.class);
    
    private final AssessmentTest test;
    private final ReferenceResolver resolver;
    
    public AssessmentTestValidator(final AssessmentTest test, final ReferenceResolver resolver) {
        this.test = test;
        this.resolver = resolver;
    }
    
    public ValidationResult validate() {
        logger.info("Validating test {}", test);
        ValidationResult result = new ValidationResult(test);
        test.validate(this, result);
        return result;
    }
    
    @Override
    public AssessmentObject getOwner() {
        return test;
    }
    
    @Override 
    public AssessmentTest getTest() {
        return test;
    }
    
    @Override
    public AssessmentItemValidator resolveItem(AssessmentItemRef assessmentItemRef) throws ReferencingException {
        ResolutionResult<AssessmentItem> resolved = resolver.resolve(test, assessmentItemRef.getHref(), AssessmentItem.class);
        if (resolved==null) {
            throw new ReferencingException("Could not resolve referenced item with href " + assessmentItemRef);
        }
        AssessmentItem item = resolved.getJQTIObject();
        return new AssessmentItemValidator(item, resolver);
    }
    
    @Override
    public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier)
            throws ReferencingException {
        VariableDeclaration declaration = null;
        Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();
        
        /* (In tests, we allow both local and item references) */
        if (localIdentifier!=null) {
            /* Referring to another test variable */
            declaration = test.getVariableDeclaration(localIdentifier);
        }
        else {
            /* It's a special ITEM.VAR reference */
            Identifier itemRefIdentifier = variableReferenceIdentifier.getAssessmentItemRefIdentifier();
            Identifier itemVarIdentifier = variableReferenceIdentifier.getAssessmentItemItemVariableIdentifier();
            AssessmentItemRef itemRef = test.lookupItemRef(itemRefIdentifier);
            if (itemRef!=null) {
                AssessmentItem item = resolveItem(itemRef).getItem();
                declaration = item.getVariableDeclaration(itemRef.resolveVariableMapping(itemVarIdentifier));
            }
        }
        return declaration;
    }

    //-------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(test=" + test
            + ",resolver=" + resolver
            + ")";
    }
}
