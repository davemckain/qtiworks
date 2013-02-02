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
package uk.ac.ed.ph.jqtiplus.reading;

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectResolver;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidator;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;

/**
 * Convenient facade for loading, resolving and validating {@link AssessmentItem}s
 * and {@link AssessmentTest}s from XML using a {@link QtiXmlReader} for the low
 * level XML parsing and a {@link ResourceLocator} for locating and finding the
 * required XML resources.
 *
 * @see QtiXmlReader
 * @see ResourceLocator
 *
 * @author David McKain
 */
public final class AssessmentObjectXmlLoader {

    private final QtiXmlReader qtiXmlReader;
    private final ResourceLocator inputResourceLocator;

    public AssessmentObjectXmlLoader(final QtiXmlReader qtiXmlReader, final ResourceLocator inputResourceLocator) {
        this.qtiXmlReader = qtiXmlReader;
        this.inputResourceLocator = inputResourceLocator;
    }

    public QtiXmlReader getQtiXmlReader() {
        return qtiXmlReader;
    }

    public ResourceLocator getInputResourceLocator() {
        return inputResourceLocator;
    }

    //-------------------------------------------------------------------
    // AssessmentItem resolution & validation

    public ResolvedAssessmentItem loadAndResolveAssessmentItem(final URI systemId) {
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(inputResourceLocator, false);
        final AssessmentObjectResolver assessmentObjectResolver = new AssessmentObjectResolver(qtiObjectReader);
        return assessmentObjectResolver.resolveAssessmentItem(systemId);
    }

    public ItemValidationResult loadResolveAndValidateItem(final URI systemId) {
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(inputResourceLocator, true);
        final ResolvedAssessmentItem resolvedAssessmentItem = new AssessmentObjectResolver(qtiObjectReader).resolveAssessmentItem(systemId);
        final AssessmentObjectValidator assessmentObjectValidator = new AssessmentObjectValidator(qtiObjectReader.getJqtiExtensionManager());
        return assessmentObjectValidator.validateItem(resolvedAssessmentItem);
    }

    //-------------------------------------------------------------------
    // AssessmentTest resolution & validation

    public ResolvedAssessmentTest loadAndResolveAssessmentTest(final URI systemId) {
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(inputResourceLocator, false);
        final AssessmentObjectResolver assessmentObjectResolver = new AssessmentObjectResolver(qtiObjectReader);
        return assessmentObjectResolver.resolveAssessmentTest(systemId);
    }

    public TestValidationResult loadResolveAndValidateTest(final URI systemId) {
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(inputResourceLocator, true);
        final ResolvedAssessmentTest resolvedAssessmentTest = new AssessmentObjectResolver(qtiObjectReader).resolveAssessmentTest(systemId);
        final AssessmentObjectValidator assessmentObjectValidator = new AssessmentObjectValidator(qtiObjectReader.getJqtiExtensionManager());
        return assessmentObjectValidator.validateTest(resolvedAssessmentTest);
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(qtiXmlReader=" + qtiXmlReader
                + ",inputResourceLocator=" + inputResourceLocator
                + ")";
    }
}
