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

import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidator;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;

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

    private final QtiXmlReader qtiXmlReader;
    private final ResourceLocator inputResourceLocator;

    public AssessmentObjectManager(final QtiXmlReader qtiXmlReader, final ResourceLocator inputResourceLocator) {
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

    public ResolvedAssessmentItem resolveAssessmentItem(final URI systemId) {
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(inputResourceLocator, false);
        final AssessmentObjectResolver assessmentObjectResolver = new AssessmentObjectResolver(qtiObjectReader);
        return assessmentObjectResolver.resolveAssessmentItem(systemId);
    }

    public ItemValidationResult resolveAndValidateItem(final URI systemId) {
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(inputResourceLocator, true);
        final ResolvedAssessmentItem resolvedAssessmentItem = new AssessmentObjectResolver(qtiObjectReader).resolveAssessmentItem(systemId);
        final AssessmentObjectValidator assessmentObjectValidator = new AssessmentObjectValidator(qtiObjectReader.getJqtiExtensionManager());
        return assessmentObjectValidator.validateItem(resolvedAssessmentItem);
    }

    //-------------------------------------------------------------------
    // AssessmentTest resolution & validation

    public ResolvedAssessmentTest resolveAssessmentTest(final URI systemId) {
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(inputResourceLocator, false);
        final AssessmentObjectResolver assessmentObjectResolver = new AssessmentObjectResolver(qtiObjectReader);
        return assessmentObjectResolver.resolveAssessmentTest(systemId);
    }

    public TestValidationResult resolveAndValidateTest(final URI systemId) {
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
