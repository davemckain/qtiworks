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

import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.BadResourceException;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceHolder;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceProvider;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceUsage;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationContext;
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
 * Item validation: read item, full validation. use cache RP template if available, otherwise look up new one (schema validating)
 * and record the lookup within the validation result. Will then return a ValidationResult
 * that contains full details of the item + RP template reads within.
 * 
 * Item evaluation: read item, no validation, resolve RP template. Return state ready to go.
 * 
 * Test validation: read test, full validation, use cache to locate items, recording validated lookups on each unique resolved System ID.
 * Will need to resolve (and validate) each RP template as well, which should hit cache as it's
 * likely that the same template will be used frequently within a test. ValidationResult should
 * contain full details.
 * Only validate each unique item (identified by URI).
 * Validation of items would use caching on RP templates as above.
 *
 * @author David McKain
 */
public final class AssessmentObjectManager {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentObjectManager.class);

    private final ResourceProvider resourceProvider;
    private final ResourceLookupCache resourceProviderResultCache;
    
    public AssessmentObjectManager(final ResourceProvider resourceProvider, final ResourceLookupCache resourceProviderResultCache) {
        this.resourceProvider = resourceProvider;
        this.resourceProviderResultCache = resourceProviderResultCache;
    }

    //-------------------------------------------------------------------
    // AssessmentItem stuff
    
    public AssessmentItemStaticState provideAssessmentItem(URI systemId, ResourceUsage resourceUsage)
            throws ResourceNotFoundException, BadResourceException {
        AssessmentItem item = resourceProviderResultCache.getResource(resourceProvider, systemId, resourceUsage, AssessmentItem.class);
        AssessmentItemStaticState result = new AssessmentItemStaticState(item);
        return result;
    }
    
    public ValidationResult validateItem(URI systemId) throws ResourceNotFoundException, BadResourceException {
        /* (This doesn't hit cache for item) */
        return validateItem(provideAssessmentItem(systemId, ResourceUsage.FOR_VALIDATION));
    }
    
    public ValidationResult validateItem(AssessmentItem item) {
        AssessmentItemStaticState itemStaticState = new AssessmentItemStaticState(item);
        return validateItem(itemStaticState);
    }
    
    public FrozenResourceLookup<ResponseProcessing> resolveResponseProcessingTemplate(AssessmentItemStaticState itemStaticState, ResourceUsage resourceUsage) {
        AssessmentItem item = itemStaticState.getItem();
        ResponseProcessing responseProcessing = item.getResponseProcessing();
        FrozenResourceLookup<ResponseProcessing> result = null;
        if (responseProcessing!=null) {
            if (responseProcessing.getResponseRules().isEmpty()) {
                /* ResponseProcessing present but no rules, so should be a template. First make sure there's a URI specified */
                URI templateSystemId = null;
                if (responseProcessing.getTemplate() != null) {
                    /* We try template attribute first... */
                    templateSystemId = resolveUri(item, responseProcessing.getTemplate());
                }
                else if (responseProcessing.getTemplateLocation() != null) {
                    /* ... then templateLocation */
                    templateSystemId = resolveUri(item, responseProcessing.getTemplateLocation());
                }
                if (templateSystemId!=null) {
                    /* If here, then a template should exist */
                    logger.info("Resolving RP template at system ID {} " + templateSystemId);
                    result = itemStaticState.getResolvedResponseProcessingTemplate();
                    if (result==null) {
                        /* Template not seen yet, so look up */
                        result = resourceProviderResultCache.getFrozenResource(resourceProvider, templateSystemId, resourceUsage, ResponseProcessing.class);
                        itemStaticState.setResolvedResponseProcessingTemplate(result);
                    }
                }
                else {
                    /* No template supplied */
                    logger.warn("responseProcessing contains no rules and does not declare a template or templateLocation, so returning null template");
                }
            }
            else {
                logger.warn("AssessmentItem contains ResponseRules, so no template will be resolved");
            }
        }
        else {
            logger.warn("AssessmentItem contains no ResponseProcessing, so no template can be resolved");
        }
        return result;
    }
    
    private ValidationResult validateItem(AssessmentItemStaticState itemStaticState) {
        AssessmentItem item = itemStaticState.getItem();
        final ValidationResult result = new ValidationResult(item);
        
        /* Resolve ResponseProcessing template, if required */
        ResponseProcessing responseProcessing = item.getResponseProcessing();
        if (responseProcessing!=null) {
            if (responseProcessing.getResponseRules().isEmpty()) {
                FrozenResourceLookup<ResponseProcessing> resolvedTemplate = resolveResponseProcessingTemplate(itemStaticState, ResourceUsage.FOR_VALIDATION);
                if (resolvedTemplate!=null) {
                    try {
                        result.addResolutionResult(resolvedTemplate.thaw());
                    }
                    catch (ResourceNotFoundException e) {
                        result.add(new ValidationError(item, "Could not find responseProcessing template at systemId " + resolvedTemplate.getSystemId(), e));
                    }
                    catch (BadResourceException e) {
                        result.add(new ValidationError(item, "Target of responseProcessing template at systemId " + resolvedTemplate.getSystemId() + "  was not a responseProcessing Object", e));
                    }
                }
                else {
                    /* No template supplied */
                    result.add(new ValidationWarning(item, "responseProcessing contains no rules and does not declare a template or templateLocation"));
                }
            }
            else {
                /* Contains ResponseRules, so no template resolution required */
            }
        }
        else {
            result.add(new ValidationWarning(item, "No responseProcessing present"));
        }
            
        /* Now validate item */
        item.validate(new ItemValidationContextImpl(itemStaticState), result);
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
    
    class ItemValidationContextImpl implements ItemValidationContext {
        
        private final AssessmentItemStaticState itemStaticState;
        
        public ItemValidationContextImpl(final AssessmentItemStaticState itemStaticState) {
            this.itemStaticState = itemStaticState;
        }
        
        @Override
        public AssessmentItem getItem() {
            return itemStaticState.getItem();
        }
        
        @Override
        public AssessmentObject getOwner() {
            return getItem();
        }
        
        @Override
        public ResponseProcessing getResolvedResponseProcessingTemplate() {
            try {
                return itemStaticState.getResolvedResponseProcessingTemplate().thaw().getRequiredQtiObject();
            }
            catch (ResourceNotFoundException e) {
                return null;
            }
            catch (BadResourceException e) {
                return null;
            }
        }
        
        @Override
        public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
            return AssessmentObjectManager.this.resolveVariableReference(itemStaticState, variableReferenceIdentifier);
        }
    }
    
    //-------------------------------------------------------------------
    // AssessmentTest stuff
    
    public AssessmentTestStaticState provideAssessmentTest(URI systemId)
            throws ResourceNotFoundException, BadResourceException {
        ResourceHolder<AssessmentTest> resourceResult = resourceProviderResultCache.getResource(resourceProvider, systemId, AssessmentTest.class);
        AssessmentTestStaticState result = initTestStaticState(resourceResult.getRequiredQtiObject());
        return result;
    }
    
    public ValidationResult validateTest(URI systemId) throws ResourceNotFoundException, BadResourceException {
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
            catch (BadResourceException e) {
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
                catch (BadResourceException e) {
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
            throws ResourceNotFoundException, BadResourceException {
        Map<URI, AssessmentItemStaticState> itemStaticStateMap = testStaticState.getAssessmentItemStaticStateMap();
        AssessmentItemStaticState result = null;
        try {
            ResourceHolder<AssessmentItem> itemResult = resourceProviderResultCache.getResource(resourceProvider, itemSystemId, AssessmentItem.class);
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
