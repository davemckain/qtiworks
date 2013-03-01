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
package uk.ac.ed.ph.qtiworks.mathassess.glue.maxima;

import uk.ac.ed.ph.jacomax.MaximaConfiguration;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trivial base for tests of {@link QtiMaximaProcess}s
 *
 * @author David McKain
 */
public abstract class QtiMaximaSessionTestBase {

    private static final Logger logger = LoggerFactory.getLogger(QtiMaximaSessionTestBase.class);

    private QtiMaximaProcessManager processManager;
    protected QtiMaximaProcess process;

    @Before
    public void setup() {
        /* Use the JacomaxSimplerConfigurer to try to establish a working
         * Maxima configuration. If this fails, we shall skip the test so
         * that people can build the full QTIWorks project successfully,
         * even if they don't have Maxima installed and/or don't plan to use
         * the MathAssess extensions.
         */
        final MaximaConfiguration maximaConfiguration = MaximaLaunchHelper.tryMaximaConfiguration();
        if (maximaConfiguration==null) {
            /* Configuration failed, so use JUnit's Assume class to skip */
            logger.warn("Failed to establish a Maxima configuration. Assuming Maxima is not installed and allowing test to succeed");
            Assume.assumeNotNull(maximaConfiguration);
            return;
        }

        /* Configuration was successful, so set up test */
        processManager = new SimpleQtiMaximaProcessManager(maximaConfiguration);
        process = processManager.obtainProcess();
    }

    @After
    public void teardown() {
        if (process!=null && processManager!=null) {
            processManager.returnProcess(process);
        }
    }
}
