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

import uk.ac.ed.ph.qtiworks.mathassess.glue.MathAssessCasException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcessManager;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import uk.ac.ed.ph.jacomax.MaximaConfiguration;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetCache;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link QtiMaximaProcessManager} that pools running Maxima processes
 * for increased performance.
 * <p>
 * (This is implemented as a POJO with {@link #init()} and {@link #shutdown()} lifecycle methods.)
 *
 * @author David McKain
 */
public final class QtiMaximaProcessPoolManager implements QtiMaximaProcessManager {

    private static final Logger logger = LoggerFactory.getLogger(QtiMaximaProcessPoolManager.class);

    private StylesheetCache stylesheetCache;
    private MaximaConfiguration maximaConfiguration;

    private GenericObjectPool<QtiMaximaProcess> qtiMaximaProcessPool;

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
        final PooledQtiMaximaProcessFactory factory = new PooledQtiMaximaProcessFactory();
        factory.setMaximaConfiguration(maximaConfiguration);
        factory.setStylesheetCache(stylesheetCache);
        factory.init();

        logger.info("Creating QtiMaximaProcess Object pool");
        qtiMaximaProcessPool = new GenericObjectPool<QtiMaximaProcess>(factory);
        qtiMaximaProcessPool.setTestOnBorrow(true);
        qtiMaximaProcessPool.setTestOnReturn(true);
    }

    public void shutdown() {
        logger.info("Closing QtiMaximaProcess Object pool");
        try {
            qtiMaximaProcessPool.close();
        }
        catch (final Exception e) {
            throw new MathAssessCasException("Could not shut down process pool", e);
        }
    }

    //---------------------------------------------------------

    @Override
    public QtiMaximaProcess obtainProcess() {
        try {
            return qtiMaximaProcessPool.borrowObject();
        }
        catch (final Exception e) {
            throw new MathAssessCasException("Could not obtain QtiMaximaProcess from pool", e);
        }
    }

    @Override
    public void returnProcess(final QtiMaximaProcess process) {
        try {
            qtiMaximaProcessPool.returnObject(process);
        }
        catch (final Exception e) {
            throw new MathAssessCasException("Could not return QtiMaximaProcess to pool", e);
        }
    }
}
