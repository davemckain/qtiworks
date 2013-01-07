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
package uk.ac.ed.ph.qtiworks.test.integration;

import static org.junit.Assert.assertEquals;

import uk.ac.ed.ph.qtiworks.samples.LanguageSampleSet;
import uk.ac.ed.ph.qtiworks.samples.MathAssessSampleSet;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment.Feature;
import uk.ac.ed.ph.qtiworks.samples.StandardQtiSampleSet;
import uk.ac.ed.ph.qtiworks.samples.StompSampleSet;
import uk.ac.ed.ph.qtiworks.samples.UpmcSampleSet;
import uk.ac.ed.ph.qtiworks.test.utils.TestUtils;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Integration test that runs {@link QtiXmlReader} on each IMS sample checking the validity
 * of each one that's supposed to be valid.
 *
 * @author David McKain
 */
@RunWith(Parameterized.class)
public class QtiXmlReaderSampleTests extends AbstractIntegrationTest {
    
    @Parameters
    public static Collection<Object[]> data() {
        return TestUtils.makeTestParameters(
                StandardQtiSampleSet.instance(),
                MathAssessSampleSet.instance(),
                UpmcSampleSet.instance(),
                StompSampleSet.instance(),
                LanguageSampleSet.instance()
        );
    }
    
    public QtiXmlReaderSampleTests(QtiSampleAssessment qtiSampleAssessment) {
       super(qtiSampleAssessment);
        
    }
    
    @Test
    public void test() throws Exception {
        XmlReadResult xmlReadResult = readSampleXml();
        if (!xmlReadResult.isSchemaValid() && !qtiSampleAssessment.hasFeature(Feature.NOT_SCHEMA_VALID)) {
            System.out.println("Schema validation expected success but failed. Details are: "
                    + ObjectDumper.dumpObject(xmlReadResult, DumpMode.DEEP));
        }
        assertEquals("Schema validation assertion failed on " + xmlReadResult.getXmlParseResult().getSystemId(),
                !qtiSampleAssessment.hasFeature(Feature.NOT_SCHEMA_VALID), xmlReadResult.isSchemaValid());
    }
}
