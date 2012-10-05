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

import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.provision.RootNodeProvider;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidator;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationResult;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final RootNodeProvider resourceProvider;

    public AssessmentObjectManager(final RootNodeProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    //-------------------------------------------------------------------
    // AssessmentItem resolution & validation

    public ResolvedAssessmentItem resolveAssessmentItem(final URI systemId, final ModelRichness modelRichness) {
        return resolveAssessmentItem(systemId, modelRichness, new CachedResourceProvider(resourceProvider, modelRichness));
    }

    private ResolvedAssessmentItem resolveAssessmentItem(final URI systemId, final ModelRichness modelRichness, final CachedResourceProvider cachedResourceProvider) {
        final RootNodeLookup<AssessmentItem> itemLookup = cachedResourceProvider.getLookup(systemId, AssessmentItem.class);
        return initResolvedAssessmentItem(itemLookup, modelRichness, cachedResourceProvider);
    }

    public ResolvedAssessmentItem resolveAssessmentItem(final AssessmentItem assessmentItem, final ModelRichness modelRichness) {
        final RootNodeLookup<AssessmentItem> itemWrapper = new RootNodeLookup<AssessmentItem>(assessmentItem);
        return initResolvedAssessmentItem(itemWrapper, modelRichness, new CachedResourceProvider(resourceProvider, modelRichness));
    }

    private ResolvedAssessmentItem initResolvedAssessmentItem(final RootNodeLookup<AssessmentItem> itemLookup, final ModelRichness modelRichness, final CachedResourceProvider cachedResourceProvider) {
        RootNodeLookup<ResponseProcessing> resolvedResponseProcessingTemplateLookup = null;
        final AssessmentItem item = itemLookup.extractIfSuccessful();
        if (item!=null) {
            resolvedResponseProcessingTemplateLookup = resolveResponseProcessingTemplate(item, cachedResourceProvider);
        }
        return new ResolvedAssessmentItem(modelRichness, itemLookup, resolvedResponseProcessingTemplateLookup);
    }

    private RootNodeLookup<ResponseProcessing> resolveResponseProcessingTemplate(final AssessmentItem item, final CachedResourceProvider cachedResourceProvider) {
        final ResponseProcessing responseProcessing = item.getResponseProcessing();
        RootNodeLookup<ResponseProcessing> result = null;
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
                    logger.debug("Resolving RP template at system ID {} " + templateSystemId);
                    result = cachedResourceProvider.getLookup(templateSystemId, ResponseProcessing.class);
                }
                else {
                    /* No template supplied */
                    logger.debug("responseProcessing contains no rules and does not declare a template or templateLocation, so returning null template");
                }
            }
            else {
                logger.debug("AssessmentItem contains ResponseRules, so no template will be resolved");
            }
        }
        else {
            logger.debug("AssessmentItem contains no ResponseProcessing, so no template can be resolved");
        }
        return result;
    }

    public ItemValidationResult resolveAndValidateItem(final URI systemId) {
        return new AssessmentObjectValidator(resourceProvider).validateItem(resolveAssessmentItem(systemId, ModelRichness.FOR_VALIDATION));
    }

    //-------------------------------------------------------------------
    // AssessmentTest resolution & validation

    public ResolvedAssessmentTest resolveAssessmentTest(final URI systemId, final ModelRichness modelRichness) {
        return resolveAssessmentTest(systemId, modelRichness, new CachedResourceProvider(resourceProvider, modelRichness));
    }

    private ResolvedAssessmentTest resolveAssessmentTest(final URI systemId, final ModelRichness modelRichness, final CachedResourceProvider cachedResourceProvider) {
        final RootNodeLookup<AssessmentTest> testLookup = cachedResourceProvider.getLookup(systemId, AssessmentTest.class);
        return initResolvedAssessmentTest(testLookup, modelRichness, cachedResourceProvider);
    }

    public ResolvedAssessmentTest resolveAssessmentTest(final AssessmentTest assessmentTest, final ModelRichness modelRichness) {
        final RootNodeLookup<AssessmentTest> testWrapper = new RootNodeLookup<AssessmentTest>(assessmentTest);
        return initResolvedAssessmentTest(testWrapper, modelRichness, new CachedResourceProvider(resourceProvider, modelRichness));
    }

    private ResolvedAssessmentTest initResolvedAssessmentTest(final RootNodeLookup<AssessmentTest> testLookup, final ModelRichness modelRichness, final CachedResourceProvider cachedResourceProvider) {
        final List<AssessmentItemRef> assessmentItemRefs = new ArrayList<AssessmentItemRef>();
        final Map<AssessmentItemRef, URI> systemIdByItemRefMap = new HashMap<AssessmentItemRef, URI>();
        final Map<Identifier, List<AssessmentItemRef>> itemRefsByIdentifierMap = new HashMap<Identifier, List<AssessmentItemRef>>();
        final Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap = new HashMap<URI, List<AssessmentItemRef>>();
        final Map<URI, ResolvedAssessmentItem> resolvedAssessmentItemMap = new HashMap<URI, ResolvedAssessmentItem>();

        /* Look up test */
        if (testLookup.wasSuccessful()) {
            /* Resolve the system ID of each assessmentItemRef */
            final AssessmentTest test = testLookup.extractIfSuccessful();
            assessmentItemRefs.addAll(QueryUtils.search(AssessmentItemRef.class, test));
            for (final AssessmentItemRef itemRef : assessmentItemRefs) {
                final Identifier identifier = itemRef.getIdentifier();
                if (identifier!=null) {
                    List<AssessmentItemRef> itemRefsByIdentifier = itemRefsByIdentifierMap.get(identifier);
                    if (itemRefsByIdentifier==null) {
                        itemRefsByIdentifier = new ArrayList<AssessmentItemRef>();
                        itemRefsByIdentifierMap.put(identifier, itemRefsByIdentifier);
                    }
                    itemRefsByIdentifier.add(itemRef);
                }
                final URI itemHref = itemRef.getHref();
                if (itemHref!=null) {
                    final URI itemSystemId = resolveUri(test, itemHref);
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
            for (final URI itemSystemId : itemRefsBySystemIdMap.keySet()) {
                resolvedAssessmentItemMap.put(itemSystemId, resolveAssessmentItem(itemSystemId, modelRichness, cachedResourceProvider));
            }
        }
        return new ResolvedAssessmentTest(modelRichness, testLookup, assessmentItemRefs,
                itemRefsByIdentifierMap, systemIdByItemRefMap, itemRefsBySystemIdMap, resolvedAssessmentItemMap);
    }

    public TestValidationResult resolveAndValidateTest(final URI systemId) {
        return new AssessmentObjectValidator(resourceProvider).validateTest(resolveAssessmentTest(systemId, ModelRichness.FOR_VALIDATION));
    }

    //-------------------------------------------------------------------

    private URI resolveUri(final RootNode baseObject, final URI href) {
        final URI baseUri = baseObject.getSystemId();
        if (baseUri==null) {
            throw new IllegalStateException("baseObject " + baseObject + " does not have a systemId set, so cannot resolve references against it");
        }
        return baseUri.resolve(href);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(resourceProvider=" + resourceProvider
                + ")";
    }
}
