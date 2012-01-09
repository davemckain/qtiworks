/* Copyright (c) 2012, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.xperimental2;

import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.BadResultException;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceProvider;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceRequireResult;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Document this type
 * 
 * Item validation: use cache RP template if available, otherwise look up new one and record the lookup within the validation result.
 * 
 * Test validation: use cache to locate items, recording lookups as they are done. Only validate each unique item (identified by URI).
 * Validation of items would use caching on RP templates as above.
 *
 * @author David McKain
 */
public final class AssessmentObjectManager {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentObjectManager.class);

    private final ResourceProvider resourceProvider;
    private final ResourceProviderResultCache resourceProviderResultCache;
    
    public AssessmentObjectManager(final ResourceProvider resourceProvider, final ResourceProviderResultCache resourceProviderResultCache) {
        this.resourceProvider = resourceProvider;
        this.resourceProviderResultCache = resourceProviderResultCache;
    }

    //-------------------------------------------------------------------
    
    /** FIXME: Hit cache or not? */
    public AssessmentItemStaticState provideAssessmentItem(URI systemId)
            throws ResourceNotFoundException, BadResultException {
        ResourceRequireResult<AssessmentItem> resourceResult = resourceProviderResultCache.provideQtiResource(resourceProvider, systemId, AssessmentItem.class);
        AssessmentItemStaticState result = new AssessmentItemStaticState(resourceResult.getRequiredQtiObject());
        return result;
    }
    
    public ValidationResult validateItem(URI systemId) throws ResourceNotFoundException, BadResultException {
        /* (This doesn't hit cache for item) */
        return validateItem(provideAssessmentItem(systemId));
    }
    
    public ValidationResult validateItem(AssessmentItem item) {
        AssessmentItemStaticState itemStaticState = new AssessmentItemStaticState(item);
        return validateItem(itemStaticState);
    }
    
    private ValidationResult validateItem(AssessmentItemStaticState itemStaticState) {
        AssessmentItem item = itemStaticState.getItem();
        final ValidationResult result = new ValidationResult(item);
        
        /* First resolve response processing template if no rules have been specified */
        ResponseProcessing responseProcessing = item.getResponseProcessing();
        if (responseProcessing!=null && responseProcessing.getResponseRules().isEmpty()) {
            /* ResponseProcessing present but no rules, so should be a template. First make sure there's a URI specified */
            URI templateUri = null;
            if (responseProcessing.getTemplate() != null) {
                /* We try template attribute first... */
                templateUri = resolveUri(item, responseProcessing.getTemplate());
            }
            else if (responseProcessing.getTemplateLocation() != null) {
                /* ... then templateLocation */
                templateUri = resolveUri(item, responseProcessing.getTemplateLocation());
            }
            
            if (templateUri!=null) {
                /* Resolve the template */
                ResponseProcessing template = null;
                if (itemStaticState.isResponseProcessingTemplateResolved()) {
                    /* We've already resolved the RP template */
                    template = itemStaticState.getResolvedResponseProcessingTemplate();
                    if (template==null) {
                        result.add(new ValidationWarning(item, "responseProcessing contains no rules and does not declare a template or templateLocation"));
                    }
                }
                else {
                    try {
                        ResourceRequireResult<ResponseProcessing> templateHolder = resourceProviderResultCache.provideQtiResource(resourceProvider, templateUri, ResponseProcessing.class);
                        itemStaticState.setResolvedResponseProcessingTemplate(templateHolder.getRequiredQtiObject());
                    }
                    catch (ResourceNotFoundException e) {
                        result.add(new ValidationError(item, "Could not find responseProcessing template at systemId " + templateUri, e));
                    }
                    catch (BadResultException e) {
                        result.add(new ValidationError(item, "Target of responseProcessing template at systemId " + templateUri + "  was not a responseProcessing Object", e));
                    }
                    itemStaticState.setResponseProcessingTemplateResolved(true);
                }
            }
            else {
                /* No template supplied */
                result.add(new ValidationWarning(item, "responseProcessing contains no rules and does not declare a template or templateLocation"));
            }
        }
        else {
            result.add(new ValidationWarning(item, "No responseProcessing present"));
        }
        
        /* Now validate item */
        item.validate(this, result);
        return result;
    }
    
    public VariableDeclaration resolveVariableReference(AssessmentItemStaticState itemStaticState, VariableReferenceIdentifier variableReferenceIdentifier) {
        VariableDeclaration declaration = null;
        final Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();

        /* (In Items, we only allow local references) */
        if (localIdentifier != null) {
            declaration = itemStaticState.getItem().getVariableDeclaration(localIdentifier);
        }
        return declaration;
    }
    
    //-------------------------------------------------------------------
    
    public AssessmentTestStaticState provideAssessmentTest(URI systemId)
            throws ResourceNotFoundException, BadResultException {
        ResourceRequireResult<AssessmentTest> resourceResult = resourceProviderResultCache.provideQtiResource(resourceProvider, systemId, AssessmentTest.class);
        AssessmentTestStaticState result = initTestStaticState(resourceResult.getRequiredQtiObject());
        return result;
    }
    
    public ValidationResult validateTest(URI systemId) throws ResourceNotFoundException, BadResultException {
        /* (This doesn't hit cache) */
        return validateTest(provideAssessmentTest(systemId));
    }
    
    public ValidationResult validateTest(AssessmentTest test) {
        AssessmentTestStaticState testStaticState = initTestStaticState(test);
        return validateTest(testStaticState);
    }
    
    private AssessmentTestStaticState initTestStaticState(AssessmentTest test) {
        AssessmentTestStaticState result = new AssessmentTestStaticState(test);
        Map<AssessmentItemRef, URI> systemIdByItemRefMap = result.getSystemIdByItemRefMap();
        Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap = result.getItemRefsBySystemIdMap();
        
        /* Resolve the system ID of each assessmentItemRef */
        for (AssessmentItemRef itemRef : test.searchItemRefs()) {
            URI itemHref = itemRef.getHref();
            if (itemHref!=null) {
                URI itemSystemId = resolveUri(test, itemHref);
                systemIdByItemRefMap.put(itemRef, itemSystemId);
                List<AssessmentItemRef> itemRefs = itemRefsBySystemIdMap.get(itemSystemId);
                if (itemRefs==null) {
                    itemRefs = new ArrayList<AssessmentItemRef>();
                    itemRefsBySystemIdMap.put(itemSystemId, itemRefs);
                }
                itemRefs.add(itemRef);
            }
        }
        return result;
    }
    
    private ValidationResult validateTest(AssessmentTestStaticState testStaticState) {
        AssessmentTest test = testStaticState.getTest();
        Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap = testStaticState.getItemRefsBySystemIdMap();
        final ValidationResult result = new ValidationResult(test);
        
        /* Resolve and validate each unique item */
        for (Entry<URI, List<AssessmentItemRef>> entry : itemRefsBySystemIdMap.entrySet()) {
            URI itemSystemId = entry.getKey();
            List<AssessmentItemRef> itemRefs = entry.getValue();
            String messageStart = "Referenced item at System ID " + itemSystemId
                    + " referenced by identifiers " + itemRefs;
            try {
                AssessmentItemStaticState itemStaticState = initItemState(testStaticState, itemSystemId);
                testStaticState.getAssessmentItemStaticStateMap().put(itemSystemId, itemStaticState);
                ValidationResult itemValidationResult = validateItem(itemStaticState);
                result.addChildResult(itemValidationResult);
                
                if (itemValidationResult.hasErrors()) {
                    result.add(new ValidationError(test, messageStart
                            + " has errors. Please see the attached validation result for this item for further information."));
                }
                if (itemValidationResult.hasWarnings()) {
                    result.add(new ValidationError(test, messageStart
                            + " has warnings. Please see the attached validation result for this item for further information."));
                }

            }
            catch (ResourceNotFoundException e) {
                result.add(new ValidationError(test, messageStart
                        + " could not be found", e));
            }
            catch (BadResultException e) {
                result.add(new ValidationError(test, messageStart
                        + " was read in successfully but is not an assessmentItem", e));
            }
        }
        
        /* Finally validate the test itself */
        test.validate(this, result);
        return result;
    }
    
    private AssessmentItemStaticState getItemState(AssessmentTestStaticState testStaticState, AssessmentItemRef itemRef) {
        URI itemSystemId = testStaticState.getSystemIdByItemRefMap().get(itemRef);
        AssessmentItemStaticState result = null;
        if (itemSystemId!=null) {
            Map<URI, AssessmentItemStaticState> itemStaticStateMap = testStaticState.getAssessmentItemStaticStateMap();
            if (!itemStaticStateMap.containsKey(itemSystemId)) {
                /* Item hasn't been resolved yet */
                try {
                    result = initItemState(testStaticState, itemSystemId);
                }
                catch (ResourceNotFoundException e) {
                    logger.warn("Item with systemId {} not found", itemSystemId.toString(), e);
                }
                catch (BadResultException e) {
                    logger.warn("Item with systemId {} not found", itemSystemId.toString(), e);
                }
            }
            else {
                result = itemStaticStateMap.get(itemSystemId);
            }
        }
        return result;
    }
    
    private AssessmentItemStaticState initItemState(AssessmentTestStaticState testStaticState, URI itemSystemId)
            throws ResourceNotFoundException, BadResultException {
        Map<URI, AssessmentItemStaticState> itemStaticStateMap = testStaticState.getAssessmentItemStaticStateMap();
        AssessmentItemStaticState result = null;
        try {
            ResourceRequireResult<AssessmentItem> itemResult = resourceProviderResultCache.provideQtiResource(resourceProvider, itemSystemId, AssessmentItem.class);
            AssessmentItem resolvedItem = itemResult.getRequiredQtiObject();
            result = new AssessmentItemStaticState(resolvedItem);
        }
        finally {
            itemStaticStateMap.put(itemSystemId, result);
        }
        return result;
    }
     
    public VariableDeclaration resolveVariableReference(AssessmentTestStaticState testStaticState, VariableReferenceIdentifier variableReferenceIdentifier) {
        final AssessmentTest test = testStaticState.getTest();
        final Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();

        /* (In tests, we allow both local and item references) */
        VariableDeclaration declaration = null;
        if (localIdentifier != null) {
            /* Referring to another test variable */
            declaration = test.getVariableDeclaration(localIdentifier);
        }
        else {
            /* It's a special ITEM.VAR reference */
            final Identifier itemRefIdentifier = variableReferenceIdentifier.getAssessmentItemRefIdentifier();
            final Identifier itemVarIdentifier = variableReferenceIdentifier.getAssessmentItemItemVariableIdentifier();
            final AssessmentItemRef itemRef = test.lookupItemRef(itemRefIdentifier);
            if (itemRef != null) {
                Identifier mappedItemVarIdentifier = itemRef.resolveVariableMapping(itemVarIdentifier);
                final AssessmentItemStaticState itemStaticState = getItemState(testStaticState, itemRef);
                if (itemStaticState!=null) {
                    declaration = itemStaticState.getItem().getVariableDeclaration(mappedItemVarIdentifier);
                }
                else {
                    logger.warn("Variable lookup " + variableReferenceIdentifier + " failed because resolution of target item failed");
                }
            }
        }
        return declaration;
    }
    
    //-------------------------------------------------------------------
    
    private URI resolveUri(RootNode baseObject, URI href) {
        URI baseUri = baseObject.getSystemId();
        if (baseUri==null) {
            throw new IllegalStateException("baseObject " + baseObject + " does not have a systemId set, so cannot resolve references against it");
        }
        return baseUri.resolve(href);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(resourceProvider=" + resourceProvider
                + ",resourceProviderResultCache=" + resourceProviderResultCache
                + ")";
    }
}
