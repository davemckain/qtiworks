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
package uk.ac.ed.ph.qtiworks.test.integration;

import uk.ac.ed.ph.qtiworks.samples.LanguageSampleSet;
import uk.ac.ed.ph.qtiworks.samples.MathAssessSampleSet;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment.Feature;
import uk.ac.ed.ph.qtiworks.samples.StandardQtiSampleSet;
import uk.ac.ed.ph.qtiworks.samples.StompSampleSet;
import uk.ac.ed.ph.qtiworks.samples.UpmcSampleSet;
import uk.ac.ed.ph.qtiworks.test.utils.TestUtils;

import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Integration test that checks that template processing runs correctly on the sample items
 * 
 * @author David McKain
 */
@RunWith(Parameterized.class)
public class TemplateProcessingSampleTests extends AbstractIntegrationTest {
    
    @Parameters
    public static Collection<Object[]> data() {
        return TestUtils.makeTestParameters(
                StandardQtiSampleSet.instance().withoutFeature(Feature.NOT_SCHEMA_VALID),
                MathAssessSampleSet.instance().withoutFeature(Feature.NOT_SCHEMA_VALID),
                UpmcSampleSet.instance().withoutFeature(Feature.NOT_SCHEMA_VALID),
                StompSampleSet.instance().withoutFeature(Feature.NOT_SCHEMA_VALID),
                LanguageSampleSet.instance().withoutFeature(Feature.NOT_SCHEMA_VALID)
        );
    }
    
    public TemplateProcessingSampleTests(QtiSampleAssessment qtiSampleAssessment) {
        super(qtiSampleAssessment);
    }
    
    @Test
    public void test() throws Exception {
        final ItemSessionController itemSessionController = createItemSessionController();
        ItemSessionState itemSessionState = itemSessionController.getItemSessionState();
        
        Assert.assertFalse(itemSessionState.isInitialized());
        Assert.assertTrue(itemSessionState.getTemplateValues().isEmpty());
        itemSessionController.performTemplateProcessing();
        Assert.assertTrue(itemSessionState.isInitialized());
    }
}
