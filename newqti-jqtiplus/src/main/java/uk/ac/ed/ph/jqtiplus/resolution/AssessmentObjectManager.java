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
package uk.ac.ed.ph.jqtiplus.resolution;

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.RootObject;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.provision.RootObjectProvider;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
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

    private final RootObjectProvider resourceProvider;
    
    public AssessmentObjectManager(final RootObjectProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    //-------------------------------------------------------------------
    // AssessmentItem stuff
    
    public ResolvedAssessmentItem resolveAssessmentItem(URI systemId, ModelRichness modelRichness) {
        return resolveAssessmentItem(systemId, modelRichness, new CachedResourceProvider(resourceProvider, modelRichness));
    }
    
    private ResolvedAssessmentItem resolveAssessmentItem(URI systemId, ModelRichness modelRichness, CachedResourceProvider providerHelper) {
        RootObjectLookup<AssessmentItem> itemLookup = providerHelper.getLookup(systemId, AssessmentItem.class);
        return initAssessmentItemHolder(itemLookup, modelRichness, providerHelper);
    }
    
    public ResolvedAssessmentItem resolveAssessmentItem(AssessmentItem assessmentItem, ModelRichness modelRichness) {
        RootObjectLookup<AssessmentItem> itemWrapper = new RootObjectLookup<AssessmentItem>(assessmentItem);
        return initAssessmentItemHolder(itemWrapper, modelRichness, new CachedResourceProvider(resourceProvider, modelRichness));
    }
    
    private ResolvedAssessmentItem initAssessmentItemHolder(RootObjectLookup<AssessmentItem> itemLookup, ModelRichness modelRichness, CachedResourceProvider providerHelper) {
        RootObjectLookup<ResponseProcessing> resolvedResponseProcessingTemplateLookup = null;
        AssessmentItem item = itemLookup.extractIfSuccessful();
        if (item!=null) {
            resolvedResponseProcessingTemplateLookup = resolveResponseProcessingTemplate(item, providerHelper);
        }
        return new ResolvedAssessmentItem(modelRichness, itemLookup, resolvedResponseProcessingTemplateLookup);
    }
     
    private RootObjectLookup<ResponseProcessing> resolveResponseProcessingTemplate(AssessmentItem item, CachedResourceProvider providerHelper) {
        ResponseProcessing responseProcessing = item.getResponseProcessing();
        RootObjectLookup<ResponseProcessing> result = null;
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
                    result = providerHelper.getLookup(templateSystemId, ResponseProcessing.class);
                }
                else {
                    /* No template supplied */
                    logger.warn("responseProcessing contains no rules and does not declare a template or templateLocation, so returning null template");
                }
            }
            else {
                logger.info("AssessmentItem contains ResponseRules, so no template will be resolved");
            }
        }
        else {
            logger.info("AssessmentItem contains no ResponseProcessing, so no template can be resolved");
        }
        return result;
    }
    
    public ItemValidationResult validateItem(URI systemId) {
        return validateItem(resolveAssessmentItem(systemId, ModelRichness.FOR_VALIDATION));
    }
    
    private ItemValidationResult validateItem(ResolvedAssessmentItem resolvedAssessmentItem) {
        final ItemValidationResult result = new ItemValidationResult(resolvedAssessmentItem);
        AssessmentItem item = resolvedAssessmentItem.getItemLookup().extractIfSuccessful();
        if (item!=null) {
            RootObjectLookup<ResponseProcessing> resolvedResponseProcessingTemplate = resolvedAssessmentItem.getResolvedResponseProcessingTemplateLookup();
            if (resolvedResponseProcessingTemplate!=null && !resolvedResponseProcessingTemplate.wasSuccessful()) {
                result.add(new ValidationError(item.getResponseProcessing(), "Resolution of ResponseProcessing template failed. Further details are attached elsewhere."));
            }
            item.validate(new ItemValidationContextImpl(result, resolvedAssessmentItem), result);
        }
        else {
            result.add(new ValidationError(null, "AssessmentItem was not successfully instantiated"));
        }
        return result;
    }
    
    abstract class AbstractValidationContextImpl<E extends AssessmentObject> implements ValidationContext {
        
        protected final AbstractValidationResult validationResult;
        protected final ResolvedAssessmentObject<E> resolvedAssessmentObject;
        protected final E subject;
        
        AbstractValidationContextImpl(final AbstractValidationResult validationResult, final ResolvedAssessmentObject<E> resolvedAssessmentObject) {
            this.validationResult = validationResult;
            this.resolvedAssessmentObject = resolvedAssessmentObject;
            this.subject = resolvedAssessmentObject.getObjectLookup().extractEnsuringSuccessful();
        }
        
        @Override
        public final AbstractValidationResult getValidationResult() {
            return validationResult;
        }
        
        @Override
        public final ResolvedAssessmentObject<E> getResolvedAssessmentObject() {
            return resolvedAssessmentObject;
        }
        
        @Override
        public final AssessmentObject getSubject() {
            return subject;
        }
        
        @Override
        public final VariableDeclaration checkVariableReference(XmlNode source, Identifier variableDeclarationIdentifier, VariableType... allowedTypes) {
            VariableDeclaration result = null;
            try {
                VariableDeclaration declaration = resolvedAssessmentObject.resolveVariableReference(variableDeclarationIdentifier);
                if (declaration.isType(allowedTypes)) {
                    result = declaration;
                }
                else {
                    StringBuilder messageBuilder = new StringBuilder("Variable with identifier ")
                        .append(variableDeclarationIdentifier)
                        .append(" is a ")
                        .append(declaration.getVariableType().getName())
                        .append(" variable but must be a ");
                    for (int i=0; i<allowedTypes.length; i++) {
                        messageBuilder.append(allowedTypes[i].getName())
                            .append(i < allowedTypes.length-1 ? ", " : " or ");
                    }
                    messageBuilder.append("variable");
                    validationResult.add(new ValidationError(source, messageBuilder.toString()));
                }

            }
            catch (VariableResolutionException e) {
                validationResult.add(new ValidationError(source, e.getMessage()));
            }
            return result;
        }
        
        @Override
        public final VariableDeclaration checkVariableReference(XmlNode source, VariableReferenceIdentifier variableReferenceIdentifier, VariableType... allowedTypes) {
            VariableDeclaration result = null;
            try {
                VariableDeclaration declaration = resolvedAssessmentObject.resolveVariableReference(variableReferenceIdentifier);
                if (declaration.isType(allowedTypes)) {
                    result = declaration;
                }
                else {
                    StringBuilder messageBuilder = new StringBuilder("Variable referenced as ")
                        .append(variableReferenceIdentifier)
                        .append(" is a ")
                        .append(declaration.getVariableType().getName())
                        .append(" variable but must be a ");
                    for (int i=0; i<allowedTypes.length; i++) {
                        messageBuilder.append(allowedTypes[i].getName())
                            .append(i < allowedTypes.length-1 ? ", " : " or ");
                    }
                    messageBuilder.append("variable");
                    validationResult.add(new ValidationError(source, messageBuilder.toString()));
                }

            }
            catch (VariableResolutionException e) {
                validationResult.add(new ValidationError(source, e.getMessage()));
            }
            return result;
        }
    }
    
    class ItemValidationContextImpl extends AbstractValidationContextImpl<AssessmentItem> {
        
        ItemValidationContextImpl(final ItemValidationResult validationResult, final ResolvedAssessmentItem resolvedAssessmentItem) {
            super(validationResult, resolvedAssessmentItem);
        }
        
        @Override
        public ResolvedAssessmentItem getResolvedAssessmentItem() {
            return (ResolvedAssessmentItem) resolvedAssessmentObject;
        }
        
        @Override
        public ResolvedAssessmentTest getResolvedAssessmentTest() {
            throw fail();
        }

        @Override
        public boolean isValidatingItem() {
            return true;
        }
        
        @Override
        public boolean isValidatingTest() {
            return false;
        }
        
        @Override
        public AssessmentItem getSubjectItem() {
            return subject;
        }

        @Override
        public AssessmentTest getSubjectTest() {
            throw fail();
        }
        
        private QtiLogicException fail() {
            return new QtiLogicException("Current ValidationContext is for an item, not a test");
        }
    }
    
    //-------------------------------------------------------------------
    // AssessmentTest stuff
    
    public ResolvedAssessmentTest resolveAssessmentTest(URI systemId, ModelRichness modelRichness) {
        return resolveAssessmentTest(systemId, modelRichness, new CachedResourceProvider(resourceProvider, modelRichness));
    }
    
    private ResolvedAssessmentTest resolveAssessmentTest(URI systemId, ModelRichness modelRichness, CachedResourceProvider providerHelper) {
        RootObjectLookup<AssessmentTest> testLookup = providerHelper.getLookup(systemId, AssessmentTest.class);
        return initAssessmentTestHolder(testLookup, modelRichness, providerHelper);
    }
    
    public ResolvedAssessmentTest resolveAssessmentTest(AssessmentTest assessmentTest, ModelRichness modelRichness) {
        RootObjectLookup<AssessmentTest> testWrapper = new RootObjectLookup<AssessmentTest>(assessmentTest);
        return initAssessmentTestHolder(testWrapper, modelRichness, new CachedResourceProvider(resourceProvider, modelRichness));
    }
    
    private ResolvedAssessmentTest initAssessmentTestHolder(RootObjectLookup<AssessmentTest> testLookup, ModelRichness modelRichness, CachedResourceProvider providerHelper) {
        Map<AssessmentItemRef, URI> systemIdByItemRefMap = new HashMap<AssessmentItemRef, URI>();
        Map<Identifier, List<AssessmentItemRef>> itemRefsByIdentifierMap = new HashMap<Identifier, List<AssessmentItemRef>>();
        Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap = new HashMap<URI, List<AssessmentItemRef>>();
        Map<URI, ResolvedAssessmentItem> resolvedAssessmentItemMap = new HashMap<URI, ResolvedAssessmentItem>();
        
        /* Look up test */
        if (testLookup.wasSuccessful()) {
            /* Resolve the system ID of each assessmentItemRef */
            AssessmentTest test = testLookup.extractIfSuccessful();
            for (AssessmentItemRef itemRef : test.searchItemRefs()) {
                Identifier identifier = itemRef.getIdentifier();
                if (identifier!=null) {
                    List<AssessmentItemRef> itemRefsByIdentifier = itemRefsByIdentifierMap.get(identifier);
                    if (itemRefsByIdentifier==null) {
                        itemRefsByIdentifier = new ArrayList<AssessmentItemRef>();
                        itemRefsByIdentifierMap.put(identifier, itemRefsByIdentifier);
                    }
                    itemRefsByIdentifier.add(itemRef);
                }
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
            
            /* Resolve each unique item */
            for (URI itemSystemId : itemRefsBySystemIdMap.keySet()) {
                resolvedAssessmentItemMap.put(itemSystemId, resolveAssessmentItem(itemSystemId, modelRichness, providerHelper));
            }
        }
        return new ResolvedAssessmentTest(modelRichness, testLookup, itemRefsByIdentifierMap,
                systemIdByItemRefMap, itemRefsBySystemIdMap, resolvedAssessmentItemMap);
    }
    
    public TestValidationResult validateTest(URI systemId) {
        return validateTest(resolveAssessmentTest(systemId, ModelRichness.FOR_VALIDATION));
    }
    
    private TestValidationResult validateTest(ResolvedAssessmentTest testResolutionContext) {
        final TestValidationResult result = new TestValidationResult(testResolutionContext);
        AssessmentTest test = testResolutionContext.getTestLookup().extractIfSuccessful();
        if (test!=null) {
            /* Validate each unique item first */
            for (Entry<URI, ResolvedAssessmentItem> entry : testResolutionContext.getResolvedAssessmentItemMap().entrySet()) {
                URI itemSystemId = entry.getKey();
                ResolvedAssessmentItem itemHolder = entry.getValue();
                StringBuilder messageBuilder = new StringBuilder("Referenced item at System ID ")
                    .append(itemSystemId)
                    .append(" referenced by identifiers ");
                List<AssessmentItemRef> itemRefs = testResolutionContext.getItemRefsBySystemIdMap().get(itemSystemId);
                for (int i=0,size=itemRefs.size(); i<size; i++) {
                    messageBuilder.append(itemRefs.get(i).getIdentifier());
                    messageBuilder.append((i<size-1) ? ", " : " and ");
                }
                
                if (itemHolder.getItemLookup().wasSuccessful()) {
                    ItemValidationResult itemValidationResult = validateItem(itemHolder);
                    result.addItemValidationResult(itemValidationResult);
                    if (itemValidationResult.hasErrors()) {
                        result.add(new ValidationError(test, messageBuilder.toString()
                                + " has errors. Please see the attached validation result for this item for further information."));
                    }
                    if (itemValidationResult.hasWarnings()) {
                        result.add(new ValidationError(test, messageBuilder.toString()
                                + " has warnings. Please see the attached validation result for this item for further information."));
                    }
                }
                else {
                    result.add(new ValidationError(test, messageBuilder.toString()
                            + " was not successfully instantiated. Further details are attached elsewhere."));
                }
            }
            
            /* Then validate the test itself */
            test.validate(new TestValidationContextImpl(result, testResolutionContext), result);
        }
        else {
            result.add(new ValidationError(null, "Provision of AssessmentTest failed"));
        }
        return result;
    }
    
    class TestValidationContextImpl extends AbstractValidationContextImpl<AssessmentTest> {
        
        TestValidationContextImpl(final TestValidationResult result, final ResolvedAssessmentTest resolvedAssessmentTest) {
            super(result, resolvedAssessmentTest);
        }
        
        @Override
        public ResolvedAssessmentItem getResolvedAssessmentItem() {
            throw fail();
        }
        
        @Override
        public ResolvedAssessmentTest getResolvedAssessmentTest() {
            return (ResolvedAssessmentTest) resolvedAssessmentObject;
        }


        @Override
        public boolean isValidatingItem() {
            return false;
        }
        
        @Override
        public boolean isValidatingTest() {
            return true;
        }
        
        @Override
        public AssessmentItem getSubjectItem() {
            throw fail();
        }

        @Override
        public AssessmentTest getSubjectTest() {
            return subject;
        }
        
        private QtiLogicException fail() {
            return new QtiLogicException("Current ValidationContext is for a test, not an item");
        }
    }

    
    //-------------------------------------------------------------------
    
    private URI resolveUri(RootObject baseObject, URI href) {
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
                + ")";
    }
}
