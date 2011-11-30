/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import uk.ac.ed.ph.jqtiplus.control.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
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
 * Not thread-safe
 * FIXME: Document this!
 * 
 * @author  David McKain
 * @version $Revision: 2720 $
 */
public final class AssessmentItemManager implements ItemValidationContext {
    
    protected static Logger logger = LoggerFactory.getLogger(AssessmentItemManager.class);
    
    private final QTIObjectManager qtiObjectManager;
    private final AssessmentItem item;
    private ResponseProcessing resolvedResponseProcessing;
    
    public AssessmentItemManager(QTIObjectManager qtiObjectManager, AssessmentItem assessmentItem) {
        ConstraintUtilities.ensureNotNull(qtiObjectManager, "qtiObjectManager");
        ConstraintUtilities.ensureNotNull(assessmentItem, "assessmentItem");
        this.qtiObjectManager = qtiObjectManager;
        this.item = assessmentItem;
        this.resolvedResponseProcessing = null;
    }
    
    public QTIObjectManager getQTIObjectManager() {
        return qtiObjectManager;
    }
    
    public AssessmentItemOrTest getOwner() {
        return item;
    }
    
    public AssessmentItem getItem() {
        return item;
    }
    
    //-------------------------------------------------------------------
    
    public ValidationResult validateItem() {
        logger.info("Performing JQTI validation on " + this);
        return item.validate(this);
    }
    
    //-------------------------------------------------------------------
    
    public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
        VariableDeclaration declaration = null;
        Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();
        
        /* (In Items, we only allow local references) */
        if (localIdentifier!=null) {
            declaration = item.getVariableDeclaration(localIdentifier);
        }
        return declaration;
    }
    
    /**
     * @throws QTIXMLReferencingException
     * @throws QTIXMLException
     */
    public ResponseProcessing getResolvedResponseProcessing() {
        if (resolvedResponseProcessing==null) {
            resolvedResponseProcessing = resolveResponseProcessing();
        }
        return resolvedResponseProcessing;
    }
    
    private ResponseProcessing resolveResponseProcessing() {
        ResponseProcessing responseProcessing = item.getResponseProcessing();
        if (responseProcessing==null) {
            /* No responseProcessing */
            return null;
        }
        if (!responseProcessing.getResponseRules().isEmpty()) {
            /* Processing already resolved */
            return responseProcessing;
        }
        QTIReadResult<ResponseProcessing> qtiReadResult = null;
        QTIXMLResourceNotFoundException firstNotFoundException = null;
        List<URI> attemptedUris = new ArrayList<URI>();
        URI templateUri = responseProcessing.getTemplate();
        if (templateUri!=null) {
            attemptedUris.add(templateUri);
            try {
                qtiReadResult = resolveRPTemplate(templateUri);
            }
            catch (QTIXMLResourceNotFoundException e) {
                firstNotFoundException = e;
            }
        }
        if (qtiReadResult==null) {
            templateUri = responseProcessing.getTemplateLocation();
            if (templateUri!=null) {
                attemptedUris.add(templateUri);
                try {
                    qtiReadResult = resolveRPTemplate(templateUri);
                }
                catch (QTIXMLResourceNotFoundException e) {
                    if (firstNotFoundException==null) {
                        firstNotFoundException = e;
                    }
                }
            }
        }
        if (qtiReadResult==null) {
            throw new QTIXMLReferencingException("Could not load responseProcessing template from URI(s) " + attemptedUris, firstNotFoundException);
        }
        XMLParseResult xmlParseResult = qtiReadResult.getXMLParseResult();
        if (xmlParseResult.isValidated() && !xmlParseResult.isSchemaValid()) {
            throw new QTIXMLReferencingException("Schema validation failed on resolved responseProcessing template", xmlParseResult);
        }
        QTIParseException qtiParseException = qtiReadResult.getQTIParseException();
        if (qtiParseException!=null) {
            throw new QTIXMLReferencingException("JQTI Object construction failed on responseProcessing template", qtiParseException);
        }
        logger.info("Resolved responseProcessing template using href {} to {}", templateUri, qtiReadResult);
        return qtiReadResult.getJQTIObject();
    }
    
    /**
     * @throws QTIXMLResourceNotFoundException
     */
    private QTIReadResult<ResponseProcessing> resolveRPTemplate(URI templateUri) {
        logger.debug("Attempting to request a responseProcessing template with URI {} from {}", templateUri, QTIObjectManager.class.getSimpleName());
        URI baseUri = item.getSystemId();
        URI resolved = baseUri.resolve(templateUri.toString());
        
        /* We'll normally do schema validation on the template, unless it's one of the standard templates, which
         * will be assumed to be valid.
         */
        return qtiObjectManager.getQTIObject(resolved, ResponseProcessing.class);
    }
    
    //-------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(qtiObjectManager=" + qtiObjectManager
            + ",item=" + item
            + ",resolvedResponseProcessing=" + resolvedResponseProcessing
            + ")";
    }
}
