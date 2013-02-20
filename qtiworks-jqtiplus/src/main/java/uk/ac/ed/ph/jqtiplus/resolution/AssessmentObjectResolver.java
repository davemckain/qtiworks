/* Copyright (c) 2012-2013, University of Edinburgh.
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

import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.provision.RootNodeProvider;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs "resolution" of an {@link AssessmentItem} or {@link AssessmentTest}, using
 * a {@link RootNodeProvider} to obtain the item/test and referenced RP templates and test
 * items.
 * <p>
 * This provides a rich {@link ResolvedAssessmentItem} or {@link ResolvedAssessmentTest}
 * which is useful for the running/delivery of assessments.
 *
 * @author David McKain
 */
public final class AssessmentObjectResolver {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentObjectResolver.class);

    private final RootNodeProvider rootNodeProvider;

    public AssessmentObjectResolver(final RootNodeProvider rootNodeProvider) {
        this.rootNodeProvider = rootNodeProvider;
    }

    //-------------------------------------------------------------------
    // AssessmentItem resolution

    public ResolvedAssessmentItem resolveAssessmentItem(final URI systemId) {
        return resolveAssessmentItem(systemId, new CachedResourceProvider(rootNodeProvider));
    }

    private ResolvedAssessmentItem resolveAssessmentItem(final URI systemId, final CachedResourceProvider cachedResourceProvider) {
        final RootNodeLookup<AssessmentItem> itemLookup = cachedResourceProvider.getLookup(systemId, AssessmentItem.class);
        return initResolvedAssessmentItem(itemLookup, cachedResourceProvider);
    }

    public ResolvedAssessmentItem resolveAssessmentItem(final AssessmentItem assessmentItem) {
        final RootNodeLookup<AssessmentItem> itemWrapper = new RootNodeLookup<AssessmentItem>(assessmentItem);
        return initResolvedAssessmentItem(itemWrapper, new CachedResourceProvider(rootNodeProvider));
    }

    private ResolvedAssessmentItem initResolvedAssessmentItem(final RootNodeLookup<AssessmentItem> itemLookup, final CachedResourceProvider cachedResourceProvider) {
        RootNodeLookup<ResponseProcessing> resolvedResponseProcessingTemplateLookup = null;
        final AssessmentItem item = itemLookup.extractIfSuccessful();
        if (item!=null) {
            resolvedResponseProcessingTemplateLookup = resolveResponseProcessingTemplate(item, cachedResourceProvider);
        }
        return new ResolvedAssessmentItem(itemLookup, resolvedResponseProcessingTemplateLookup);
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

    //-------------------------------------------------------------------
    // AssessmentTest resolution

    public ResolvedAssessmentTest resolveAssessmentTest(final URI systemId) {
        return resolveAssessmentTest(systemId, new CachedResourceProvider(rootNodeProvider));
    }

    private ResolvedAssessmentTest resolveAssessmentTest(final URI systemId, final CachedResourceProvider cachedResourceProvider) {
        final RootNodeLookup<AssessmentTest> testLookup = cachedResourceProvider.getLookup(systemId, AssessmentTest.class);
        return initResolvedAssessmentTest(testLookup, cachedResourceProvider);
    }

    public ResolvedAssessmentTest resolveAssessmentTest(final AssessmentTest assessmentTest) {
        final RootNodeLookup<AssessmentTest> testWrapper = new RootNodeLookup<AssessmentTest>(assessmentTest);
        return initResolvedAssessmentTest(testWrapper, new CachedResourceProvider(rootNodeProvider));
    }

    private ResolvedAssessmentTest initResolvedAssessmentTest(final RootNodeLookup<AssessmentTest> testLookup, final CachedResourceProvider cachedResourceProvider) {
        final List<AssessmentItemRef> assessmentItemRefs = new ArrayList<AssessmentItemRef>();
        final Map<AssessmentItemRef, URI> systemIdByItemRefMap = new LinkedHashMap<AssessmentItemRef, URI>();
        final Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap = new LinkedHashMap<URI, List<AssessmentItemRef>>();
        final Map<URI, ResolvedAssessmentItem> resolvedAssessmentItemMap = new LinkedHashMap<URI, ResolvedAssessmentItem>();

        /* Look up test */
        if (testLookup.wasSuccessful()) {
            final AssessmentTest test = testLookup.extractIfSuccessful();

            /* Resolve the system ID of each assessmentItemRef */
            assessmentItemRefs.addAll(QueryUtils.search(AssessmentItemRef.class, test));
            for (final AssessmentItemRef itemRef : assessmentItemRefs) {
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
                resolvedAssessmentItemMap.put(itemSystemId, resolveAssessmentItem(itemSystemId, cachedResourceProvider));
            }
        }
        return new ResolvedAssessmentTest(testLookup, assessmentItemRefs,
                systemIdByItemRefMap, itemRefsBySystemIdMap, resolvedAssessmentItemMap);
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
                + "(rootNodeProvider=" + rootNodeProvider
                + ")";
    }
}
