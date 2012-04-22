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
package uk.ac.ed.ph.qtiworks.mathassess.glue.maxima;

import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcessManager;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.SimpleQtiMaximaProcessManager;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MathsContentOutputValueWrapper;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This tests the up-conversion process by feeding some raw Maxima input to Maxima,
 * getting MathML back, up-converting and then checking that the input and the output
 * are "the same" (module lots of irritating brackets).
 * 
 * @author David McKain
 */
@RunWith(Parameterized.class)
public class CircularMaximaUpconversionTests extends QtiMaximaSessionTestBase {

    private static final Logger logger = LoggerFactory.getLogger(CircularMaximaUpconversionTests.class);
    
    public static final String TEST_RESOURCE_NAME = "circular-maxima-upconversion-tests.txt";
    
    @Parameters
    public static Collection<String[]> data() throws Exception {
        return TestFileHelper.readAndParseSingleLineInputTestResource(TEST_RESOURCE_NAME);
    }
    
    private final String inputMaxima;
    private final String expectedOutputMaxima;
    
    public CircularMaximaUpconversionTests(final String inputMaxima, final String expectedOutputMaxima) {
        this.inputMaxima = inputMaxima;
        this.expectedOutputMaxima = expectedOutputMaxima;
    }
    
    @Test
    public void runTest() throws Throwable {
        QtiMaximaProcessManager factory = new SimpleQtiMaximaProcessManager();
        QtiMaximaProcess session = factory.obtainProcess();
        MathsContentOutputValueWrapper result = null;
        try {
            result = session.executeMathOutput(inputMaxima, false);
            Assert.assertEquals(expectedOutputMaxima, result.getMaximaInput());
        }
        catch (Throwable e) {
            logger.error("Input was: " + inputMaxima);
            logger.error("Exception was " + e);
            if (result!=null) {
                logger.error("Up-converted PMathML was " + result.getPMathML());
                logger.error("Resulting Maxima was     " + result.getMaximaInput());
                logger.error("Expected Maxima was      " + expectedOutputMaxima);
            }
            throw e;
        }
        finally {
            factory.returnProcess(session);
        }
    }
}
