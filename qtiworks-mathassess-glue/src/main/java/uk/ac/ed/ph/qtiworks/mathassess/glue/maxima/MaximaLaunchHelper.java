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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.mathassess.glue.maxima;

import uk.ac.ed.ph.jacomax.JacomaxConfigurationException;
import uk.ac.ed.ph.jacomax.JacomaxRuntimeException;
import uk.ac.ed.ph.jacomax.JacomaxSimpleConfigurator;
import uk.ac.ed.ph.jacomax.MaximaConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for configuring, checking and launching Maxima.
 *
 * @author David McKain
 */
public final class MaximaLaunchHelper {

    private static final Logger logger = LoggerFactory.getLogger(MaximaLaunchHelper.class);

    /**
     * Uses the {@link JacomaxSimpleConfigurator} to try to obtain a potential
     * configuration for Maxima. Returns a non-null {@link MaximaConfiguration} on success,
     * null on failure.
     */
    public static MaximaConfiguration tryMaximaConfiguration() {
        try {
            return JacomaxSimpleConfigurator.configure();
        }
        catch (final JacomaxConfigurationException e) {
            logger.info("Failed to infer a MaximaConfiguration", e);
            return null;
        }
    }

    /**
     * Checks whether Maxima can be successfully configured, instantiated then immediately
     * shut down.
     *
     * @return
     */
    public static boolean isMaximaWorking() {
        final MaximaConfiguration maximaConfiguration = tryMaximaConfiguration();
        if (maximaConfiguration!=null) {
            SimpleQtiMaximaProcessManager simpleQtiMaximaProcessManager = null;
            QtiMaximaProcess maximaProcess = null;
            try {
                simpleQtiMaximaProcessManager = new SimpleQtiMaximaProcessManager(maximaConfiguration);
                maximaProcess = simpleQtiMaximaProcessManager.obtainProcess();
                return true;
            }
            catch (final JacomaxRuntimeException e) {
                return false;
            }
            finally {
                if (simpleQtiMaximaProcessManager!=null && maximaProcess!=null) {
                    try {
                        simpleQtiMaximaProcessManager.returnProcess(maximaProcess);
                    }
                    catch (final JacomaxRuntimeException e) {
                        logger.warn("Unexpected Exception terminating maxima process", e);
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
