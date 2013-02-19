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
package uk.ac.ed.ph.qtiworks.config;

import uk.ac.ed.ph.qtiworks.QtiWorksDeploymentException;
import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

/**
 * Helper to set up the Spring {@link ApplicationContext} and {@link PropertySources}
 * for QTIWorks
 *
 * @author David McKain
 */
public class QtiWorksApplicationContextHelper {

    /** Resoure URI for fixed QTIWorks properties */
    public static final String QTIWORKS_PROPERTIES_URI = "classpath:/qtiworks.properties";

    private static final Logger logger = LoggerFactory.getLogger(QtiWorksApplicationContextHelper.class);

    public static void registerConfigPropertySources(final AbstractApplicationContext applicationContext,
            final String deploymentPropertiesUri) {
        /* Load static properties, bundled within application */
        final ConfigurableEnvironment environment = applicationContext.getEnvironment();
        final MutablePropertySources propertySources = environment.getPropertySources();
        final ResourcePropertySource staticPropertiesSource;
        try {
            staticPropertiesSource = new ResourcePropertySource(QTIWORKS_PROPERTIES_URI);
        }
        catch (final IOException e) {
            throw new QtiWorksLogicException("Unexpected failure to locate " + QTIWORKS_PROPERTIES_URI + " - should be in ClassPath!");
        }

        /* Next try to load deployment properties from provided URI */
        logger.info("Loading QTIWorks deployment configuration resource from {} via Spring Resource API", deploymentPropertiesUri);
        ResourcePropertySource deploymentPropertiesSource;
        try {
            deploymentPropertiesSource = new ResourcePropertySource(deploymentPropertiesUri);
        }
        catch (final IOException e) {
            throw new QtiWorksDeploymentException("Failed to load QTIWorks deployment properties from " + deploymentPropertiesUri);
        }

        /* Register these property sources with the environment for the rest of the bootstrap */
        propertySources.addFirst(staticPropertiesSource);
        propertySources.addFirst(deploymentPropertiesSource);
    }
}
