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
package org.qtitools.mathassess.tools.glue.extras.pooling;

import org.qtitools.mathassess.tools.qticasbridge.QTICASBridgeException;
import org.qtitools.mathassess.tools.qticasbridge.maxima.QTIMaximaSession;
import org.qtitools.mathassess.tools.qticasbridge.maxima.QTIMaximaSessionManager;
import org.qtitools.mathassess.tools.utilities.ConstraintUtilities;

import uk.ac.ed.ph.jacomax.MaximaConfiguration;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetCache;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link QTIMaximaSessionManager} that pools running Maxima processes
 * for increased performance.
 * <p>
 * (This is implemented as a POJO with {@link #init()} and {@link #shutdown()} lifecycle methods.)
 *
 * @author David McKain
 */
public class PooledQTIMaximaSessionManager implements QTIMaximaSessionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(PooledQTIMaximaSessionManager.class);
    
    private StylesheetCache stylesheetCache;
    private MaximaConfiguration maximaConfiguration;
    
    private GenericObjectPool qtiMaximaSessionPool;
    
    public StylesheetCache getStylesheetCache() {
        return stylesheetCache;
    }
    
    public void setStylesheetCache(StylesheetCache stylesheetCache) {
        this.stylesheetCache = stylesheetCache;
    }

    
    public MaximaConfiguration getMaximaConfiguration() {
        return maximaConfiguration;
    }
    
    public void setMaximaConfiguration(MaximaConfiguration maximaConfiguration) {
        this.maximaConfiguration = maximaConfiguration;
    }
    
    //---------------------------------------------------------

    public void init() {
        ConstraintUtilities.ensureNotNull(maximaConfiguration, "maximaConfiguration");
        ConstraintUtilities.ensureNotNull(stylesheetCache, "stylesheetCache");
        PoolableQTIMaximaSessionFactory factory = new PoolableQTIMaximaSessionFactory();
        factory.setMaximaConfiguration(maximaConfiguration);
        factory.setStylesheetCache(stylesheetCache);
        factory.init();
        
        logger.info("Creating QTIMaximaSession Object pool");
        qtiMaximaSessionPool = new GenericObjectPool(factory);
        qtiMaximaSessionPool.setTestOnBorrow(true);
        qtiMaximaSessionPool.setTestOnReturn(true);
    }
    
    public void shutdown() {
        logger.info("Closing QTIMaximaSession Object pool");
        try {
            qtiMaximaSessionPool.close();
        }
        catch (Exception e) {
            throw new QTICASBridgeException("Could not shut down process pool", e);
        }
    }
    
    //---------------------------------------------------------
    
    public QTIMaximaSession obtainSession() {
        try {
            return (QTIMaximaSession) qtiMaximaSessionPool.borrowObject();
        }
        catch (Exception e) {
            throw new QTICASBridgeException("Could not obtain QTIMaximaSession from pool", e);
        }
    }
    
    public void returnSession(QTIMaximaSession session) {
        try {
            qtiMaximaSessionPool.returnObject(session);
        }
        catch (Exception e) {
            throw new QTICASBridgeException("Could not return QTIMaximaSession to pool", e);
        }
    }
}
