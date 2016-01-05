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
package uk.ac.ed.ph.qtiworks.mathassess.pooling;

import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import uk.ac.ed.ph.jacomax.MaximaConfiguration;
import uk.ac.ed.ph.jacomax.MaximaInteractiveProcess;
import uk.ac.ed.ph.jacomax.MaximaProcessLauncher;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetCache;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link PooledObjectFactory} catering {@link QtiMaximaProcess}s.
 *
 * @author David McKain
 */
final class PooledQtiMaximaProcessFactory implements PooledObjectFactory<QtiMaximaProcess> {

    private static final Logger logger = LoggerFactory.getLogger(PooledQtiMaximaProcessFactory.class);

    private StylesheetCache stylesheetCache;
    private MaximaConfiguration maximaConfiguration;

    private MaximaProcessLauncher maximaProcessLauncher;

    public StylesheetCache getStylesheetCache() {
        return stylesheetCache;
    }

    public void setStylesheetCache(final StylesheetCache stylesheetCache) {
        this.stylesheetCache = stylesheetCache;
    }


    public MaximaConfiguration getMaximaConfiguration() {
        return maximaConfiguration;
    }

    public void setMaximaConfiguration(final MaximaConfiguration maximaConfiguration) {
        this.maximaConfiguration = maximaConfiguration;
    }

    //---------------------------------------------------------

    public void init() {
        Assert.notNull(maximaConfiguration, "maximaConfiguration");
        Assert.notNull(stylesheetCache, "stylesheetCache");
        maximaProcessLauncher = new MaximaProcessLauncher(maximaConfiguration);
    }

    @Override
    public PooledObject<QtiMaximaProcess> makeObject() {
        logger.debug("Creating new pooled Maxima process");
        final MaximaInteractiveProcess maximaInteractiveProcess = maximaProcessLauncher.launchInteractiveProcess();
        final QtiMaximaProcess process = new QtiMaximaProcess(maximaInteractiveProcess, stylesheetCache);
        process.init();
        return new DefaultPooledObject<QtiMaximaProcess>(process);
    }

    @Override
    public void activateObject(final PooledObject<QtiMaximaProcess> obj) {
        logger.debug("Activating Maxima process and setting new random state");
        final QtiMaximaProcess process = obj.getObject();
        process.setRandomState();
    }

    @Override
    public void passivateObject(final PooledObject<QtiMaximaProcess> obj) {
        logger.debug("Resetting Maxima process and passivating");
        final QtiMaximaProcess process = obj.getObject();
        if (process.isTerminated()) {
            throw new IllegalStateException("Expected pool to verify Objects before passivation");
        }
        try {
            process.reset();
        }
        catch (final Exception e) {
            logger.warn("Could not reset process - terminating so that it is no longer considered valid");
            process.terminate();
        }
    }

    @Override
    public boolean validateObject(final PooledObject<QtiMaximaProcess> obj) {
        final QtiMaximaProcess process = obj.getObject();
        return !process.isTerminated();
    }

    @Override
    public void destroyObject(final PooledObject<QtiMaximaProcess> obj) {
        logger.debug("Terminating pooled Maxima process");
        final QtiMaximaProcess process = obj.getObject();
        process.terminate();
    }
}
