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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.utils.LruHashMap;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentObject;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Middle layer service responsible for instantiating and caching {@link ItemProcessingMap}
 * and {@link TestProcessingMap} Objects.
 *
 * FIXME: For the time being, we'll use an {@link LruHashMap}, though this is probably not the best choice.
 *
 * @author David McKain
 */
@Service
public class AssessmentObjectManagementService {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentObjectManagementService.class);

    private int cacheMissCount;
    private int cacheHitCount;

    @Resource
    private QtiXmlReader qtiXmlReader;

    @Resource
    private AssessmentPackageFileService assessmentPackageFileService;

    private final LruHashMap<Long, Object> cache;

    public AssessmentObjectManagementService() {
        this.cache = new LruHashMap<Long, Object>();
        this.cacheMissCount = 0;
        this.cacheHitCount = 0;
    }

    public ItemProcessingMap getItemProcessingMap(final AssessmentPackage assessmentPackage) {
        final Long assessmentPackageId = assessmentPackage.getId();
        ItemProcessingMap result;
        synchronized (cache) {
            result = (ItemProcessingMap) cache.get(assessmentPackageId);
            if (result!=null) {
                logger.debug("Cache HIT for package {}", assessmentPackage);
                cacheHitCount++;
            }
            else {
                logger.debug("Cache MISS for package {}. Reading and resolving XML", assessmentPackage);
                cacheMissCount++;
                final ResolvedAssessmentItem resolvedAssessmentItem = resolveAssessmentObject(assessmentPackage);
                result = new ItemProcessingInitializer(resolvedAssessmentItem, assessmentPackage.isValid()).initialize();
                cache.put(assessmentPackageId, result);
            }
        }
        return result;
    }

    public TestProcessingMap getTestProcessingMap(final AssessmentPackage assessmentPackage) {
        final Long assessmentPackageId = assessmentPackage.getId();
        TestProcessingMap result;
        synchronized (cache) {
            result = (TestProcessingMap) cache.get(assessmentPackageId);
            if (result!=null) {
                logger.debug("Cache HIT for package {}", assessmentPackage);
                cacheHitCount++;
            }
            else {
                logger.debug("Cache MISS for package {}. Reading and resolving XML", assessmentPackage);
                cacheMissCount++;
                final ResolvedAssessmentTest resolvedAssessmentTest = resolveAssessmentObject(assessmentPackage);
                result = new TestProcessingInitializer(resolvedAssessmentTest, assessmentPackage.isValid()).initialize();
                cache.put(assessmentPackageId, result);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <E extends ResolvedAssessmentObject<?>>
    E resolveAssessmentObject(final AssessmentPackage assessmentPackage) {
        final ResourceLocator inputResourceLocator = assessmentPackageFileService.createResolvingResourceLocator(assessmentPackage);
        final URI assessmentObjectSystemId = assessmentPackageFileService.createAssessmentObjectUri(assessmentPackage);
        final QtiObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(inputResourceLocator);
        final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
        E result;
        final AssessmentObjectType assessmentObjectType = assessmentPackage.getAssessmentType();
        if (assessmentObjectType==AssessmentObjectType.ASSESSMENT_ITEM) {
            result = (E) objectManager.resolveAssessmentItem(assessmentObjectSystemId, ModelRichness.FULL_ASSUMED_VALID);
        }
        else if (assessmentObjectType==AssessmentObjectType.ASSESSMENT_TEST) {
            result = (E) objectManager.resolveAssessmentTest(assessmentObjectSystemId, ModelRichness.FULL_ASSUMED_VALID);
        }
        else {
            throw new QtiWorksLogicException("Unexpected branch " + assessmentObjectType);
        }
        return result;
    }

    //--------------------------------------------------------------------------
    // Reporting

    public int getCacheUsage() {
        return cache.size();
    }

    public int getCacheMaxSize() {
        return cache.getMaxSize();
    }

    public int getCacheMissCount() {
        return cacheMissCount;
    }

    public int getCacheHitCount() {
        return cacheHitCount;
    }

    public int getCachePurgeCount() {
        return cache.getPurgeCount();
    }

    public Map<Long, Object> getCacheView() {
        return Collections.unmodifiableMap(cache);
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
