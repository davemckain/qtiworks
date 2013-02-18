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
package uk.ac.ed.ph.qtiworks.tools;

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.base.services.QtiWorksDeploymentSettings;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.support.ResourcePropertySource;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public abstract class StandaloneRunner {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneRunner.class);

    private final Class<?>[] configClasses;

    protected StandaloneRunner(final Class<?>... configClasses) {
        this.configClasses = configClasses;
    }

    void run(final String[] args) throws Exception {
        if (args.length==0) {
            throw new QtiWorksRuntimeException("Provide a path to the QTIWorks deployment properties file (relative to current directory)");
        }
        final String configPath = args[0];
        final File configFile = new File(System.getProperty("user.dir"), configPath);
        final String springResourceUri = configFile.toURI().toString();
        logger.info("Loading QTIWorks deployment properties from {}", springResourceUri);
        ResourcePropertySource resourcePropertySource;
        try {
            resourcePropertySource = new ResourcePropertySource(springResourceUri);
        }
        catch (final IOException e) {
            throw new QtiWorksRuntimeException("Failed to load QTIWorks deployment properties from " + springResourceUri);
        }

        logger.info("Setting up Spring ApplicationContext");
        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().getPropertySources().addFirst(resourcePropertySource);
        ctx.register(configClasses);
        ctx.refresh();

        final QtiWorksDeploymentSettings qtiWorksDeploymentSettings = ctx.getBean(QtiWorksDeploymentSettings.class);
        System.out.println("TEST: " + qtiWorksDeploymentSettings.getJdbcUsername());

        try {
            doWork(ctx);
        }
        finally {
            ctx.close();
        }
    }

    protected abstract void doWork(AnnotationConfigApplicationContext ctx)
            throws Exception;
}
