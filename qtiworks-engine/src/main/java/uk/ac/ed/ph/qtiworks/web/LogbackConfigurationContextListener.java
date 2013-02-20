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
package uk.ac.ed.ph.qtiworks.web;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public final class LogbackConfigurationContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(LogbackConfigurationContextListener.class);

    public static final String LOGBACK_CONFIG_FILE_PARAM = "logbackConfigFile";

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        final String logbackConfigFilePath = servletContextEvent.getServletContext().getInitParameter(LOGBACK_CONFIG_FILE_PARAM);
        if (logbackConfigFilePath!=null) {
            loadLogbackConfiguration(logbackConfigFilePath);
        }
        else {
            logger.warn("Default (quiet) logback configuration will be used. Set the {} init-param if you want to override this", LOGBACK_CONFIG_FILE_PARAM);
        }
    }

    private void loadLogbackConfiguration(final String logbackConfigFilePath) {
        final File logbackConfigFile = new File(logbackConfigFilePath);
        if (!logbackConfigFile.exists()) {
            logger.warn("Logback configuration file at path {} does not exist - using bundled logback configuration", logbackConfigFile);
        }
        else if (!logbackConfigFile.canRead()) {
            logger.warn("Logback configuration file at path {} is not readable - using bundled logback configuration", logbackConfigFile);
        }
        else {
            logger.info("Configuring Logback with selected config file at {}", logbackConfigFile);
            final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.reset();
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            try {
                configurator.doConfigure(logbackConfigFile);
            }
            catch (final JoranException e) {
                logger.warn("Logback configuration file at path {} failed to configure - using bundled logback configuration", logbackConfigFile, e);
            }
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        /* Do nothing */
    }
}
