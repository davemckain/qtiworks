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
import uk.ac.ed.ph.qtiworks.web.QtiWorksApplicationContextHelper;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Base template for standalone launches
 *
 * @author David McKain
 */
public abstract class StandaloneRunTemplate {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneRunTemplate.class);

    protected abstract Class<?>[] getConfigClasses();

    protected void validateRemainingArguments(@SuppressWarnings("unused") final String[] remainingArgs) {
        /* Do nothing */
    }

    protected abstract void doWork(AnnotationConfigApplicationContext ctx, String[] remainingArgs)
            throws Exception;

    protected void run(final String[] args) throws Exception {
        /* Extract first argument, which would be a Spring Resource URI pointing to the
         * location of the QTIWorks deployment properties file
         */
        if (args.length==0) {
            throw new QtiWorksRuntimeException("Provide a path to the QTIWorks deployment properties file (relative to current directory)");
        }
        final String deploymentPropertiesResourceUri = extractDeploymentPropertiesUri(args[0]);
        logger.info("Will load deployment properties from Spring Resource URI {}", deploymentPropertiesResourceUri);

        /* Pop first argument and check what's left */
        final String[] remainingArgs = new String[args.length-1];
        System.arraycopy(args, 1, remainingArgs, 0, remainingArgs.length);
        validateRemainingArguments(remainingArgs);

        logger.info("Setting up Spring ApplicationContext");
        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        QtiWorksApplicationContextHelper.registerConfigPropertySources(ctx, deploymentPropertiesResourceUri);
        ctx.register(getConfigClasses());
        ctx.refresh();

        /* Now let subclass do work */
        try {
            doWork(ctx, remainingArgs);
        }
        finally {
            ctx.close();
        }
    }

    private String extractDeploymentPropertiesUri(final String path) {
        String result = null;
        try {
            /* First check if we were passed an absolute URI */
            final URI pathAsUri = new URI(path);
            if (pathAsUri.isAbsolute()) {
                result = path;
            }
        }
        catch (final URISyntaxException e) {
            /* Handled below */
        }
        if (result==null) {
            final File configFile = new File(System.getProperty("user.dir"), path);
            result = configFile.toURI().toString();
        }
        return result;
    }

}
