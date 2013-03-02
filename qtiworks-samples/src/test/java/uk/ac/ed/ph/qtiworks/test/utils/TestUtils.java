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
package uk.ac.ed.ph.qtiworks.test.utils;

import uk.ac.ed.ph.qtiworks.mathassess.MathAssessExtensionPackage;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment.Feature;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleSet;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.SimpleXsltStylesheetCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Helper utilities for integration tests
 *
 * @author David McKain
 */
public final class TestUtils {

    public static Collection<Object[]> makeTestParameters(final QtiSampleSet... qtiSampleSets) {
        final List<Object[]> result = new ArrayList<Object[]>();
        for (final QtiSampleSet qtiSampleSet : qtiSampleSets) {
            for (final QtiSampleAssessment qtiSampleAssessment : qtiSampleSet) {
                result.add(new Object[] { qtiSampleAssessment });
            }
        }
        return result;
    }

    public static MathAssessExtensionPackage buildMathAssessExtensionPackage() {
        return new MathAssessExtensionPackage(new SimpleXsltStylesheetCache());
    }

    public static JqtiExtensionManager buildJqtiExtensionManager(final QtiSampleAssessment qtiSampleAssessment) {
        /* Load extensions if required */
        final List<JqtiExtensionPackage<?>> jqtiExtensionPackages = new ArrayList<JqtiExtensionPackage<?>>();
        if (qtiSampleAssessment.hasFeature(Feature.REQUIRES_MATHASSES)) {
            jqtiExtensionPackages.add(buildMathAssessExtensionPackage());
        }
        return new JqtiExtensionManager(jqtiExtensionPackages);
    }

}
