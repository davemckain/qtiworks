/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils.legacy;

import uk.ac.ed.ph.jqtiplus.control.TestValidationContext;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.QTIParseError;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author  David McKain
 * @version $Revision: 2801 $
 */
public final class AssessmentTestManager implements TestValidationContext {
    
    private static final Logger logger = LoggerFactory.getLogger(AssessmentTestManager.class);
    
    private final QTIObjectManager qtiObjectManager;
    private final AssessmentTest test;
    
    private final Map<URI, AssessmentItemManager> resolvedItemManagers;
    
    public AssessmentTestManager(QTIObjectManager qtiObjectManager, AssessmentTest assessmentTest) {
        ConstraintUtilities.ensureNotNull(qtiObjectManager, "qtiObjectManager");
        ConstraintUtilities.ensureNotNull(assessmentTest, "assessmentItem");
        this.qtiObjectManager = qtiObjectManager;
        this.test = assessmentTest;
        this.resolvedItemManagers = new HashMap<URI, AssessmentItemManager>();
    }
    
    public QTIObjectManager getQTIObjectManager() {
        return qtiObjectManager;
    }
    
    public AssessmentObject getOwner() {
        return test;
    }
    
    public AssessmentTest getTest() {
        return test;
    }
    
    //-------------------------------------------------------------------
    
    public ValidationResult validateTest() {
        logger.info("Performing JQTI validation on " + this);
        return test.validate(this);
    }
    
    //-------------------------------------------------------------------
    
    public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
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
    
    public AssessmentItemManager resolveItem(AssessmentItemRef assessmentItemRef) {
        URI baseUri = test.getSystemId();
        URI rawHref = assessmentItemRef.getHref();
        URI resolvedItemUri = baseUri.resolve(rawHref);
        
        AssessmentItemManager result = resolvedItemManagers.get(resolvedItemUri);
        if (result==null) {
            AssessmentItem assessmentItem = resolveItem(resolvedItemUri, rawHref);
            result = new AssessmentItemManager(qtiObjectManager, assessmentItem);
            resolvedItemManagers.put(resolvedItemUri, result);
        }
        return result;
    }
    
    private AssessmentItem resolveItem(URI resolvedItemUri, URI rawHref) {
        logger.info("Requesting the referenced assessmentItem with href={} resolved to URI {}", rawHref, resolvedItemUri);
        try {
            QTIReadResult<AssessmentItem> qtiReadResult = qtiObjectManager.getQTIObject(resolvedItemUri, AssessmentItem.class);
            XMLParseResult xmlParseResult = qtiReadResult.getXMLParseResult();
            if (xmlParseResult.isValidated() && !xmlParseResult.isSchemaValid()) {
                throw new QTIXMLReferencingException("Schema validation failed on referenced assessmentItem at URI " + resolvedItemUri, xmlParseResult);
            }
            List<QTIParseError> qtiParseErrors = qtiReadResult.getQTIParseErrors();
            if (!qtiParseErrors.isEmpty()) {
                throw new QTIXMLReferencingException("JQTI Object construction failed on responseProcessing template", xmlParseResult, qtiParseErrors);
            }
            logger.info("Resolved assessmentItem with href {} to {}", rawHref, qtiReadResult);
            return qtiReadResult.getJQTIObject();
        }
        catch (QTIXMLResourceNotFoundException e) {
            throw new QTIXMLReferencingException("Could not load referenced assessmentItem from URI " + resolvedItemUri, e);
        }
    }
    
    //-------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(qtiObjectManager=" + qtiObjectManager
            + ",test=" + test
            + ")";
    }
}
