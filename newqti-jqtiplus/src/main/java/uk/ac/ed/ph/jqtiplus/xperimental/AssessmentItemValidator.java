/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xperimental;

import uk.ac.ed.ph.jqtiplus.control.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.node.AssessmentItemOrTest;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  David McKain
 * @version $Revision$
 */
public final class AssessmentItemValidator implements ItemValidationContext {
    
    private static final Logger logger = LoggerFactory.getLogger(AssessmentItemValidator.class);
    
    private final AssessmentItem item;
    private final ReferenceResolver resolver;
        
    public AssessmentItemValidator(final AssessmentItem item, final ReferenceResolver resolver) {
        this.item = item;
        this.resolver = resolver;
    }
    
    public ValidationResult validate() {
        logger.info("Validating item {}", item);
        ValidationResult result = new ValidationResult(item);
        item.validate(this, result);
        return result;
    }
    
    @Override
    public AssessmentItem getItem() {
        return item;
    }
    
    @Override
    public AssessmentItemOrTest getOwner() {
        return item;
    }
    
    @Override
    public ResponseProcessing getResolvedResponseProcessing() throws ReferencingException {
        ResponseProcessing responseProcessing = item.getResponseProcessing();
        if (responseProcessing==null) {
            /* No responseProcessing */
            return null;
        }
        if (!responseProcessing.getResponseRules().isEmpty()) {
            /* Processing already resolved */
            return responseProcessing;
        }
        ResolutionResult<ResponseProcessing> resolutionResult = null;
        List<URI> attemptedUris = new ArrayList<URI>();
        URI templateUri = responseProcessing.getTemplate();
        if (templateUri!=null) {
            attemptedUris.add(templateUri);
            resolutionResult = resolver.resolve(item, templateUri, ResponseProcessing.class);
        }
        if (resolutionResult==null) {
            templateUri = responseProcessing.getTemplateLocation();
            if (templateUri!=null) {
                attemptedUris.add(templateUri);
                resolutionResult = resolver.resolve(item, templateUri, ResponseProcessing.class);
            }
        }
        if (resolutionResult==null) {
            throw new ReferencingException("Could not load responseProcessing template from URI(s) " + attemptedUris);
        }
        logger.info("Resolved responseProcessing template using href {} to {}", templateUri, resolutionResult);
        return resolutionResult.getJQTIObject();
    }
    
    @Override
    public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
        VariableDeclaration declaration = null;
        Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();
        
        /* (In Items, we only allow local references) */
        if (localIdentifier!=null) {
            declaration = item.getVariableDeclaration(localIdentifier);
        }
        return declaration;
    }

    //-------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(item=" + item
            + ",resolver=" + resolver
            + ")";
    }
}
