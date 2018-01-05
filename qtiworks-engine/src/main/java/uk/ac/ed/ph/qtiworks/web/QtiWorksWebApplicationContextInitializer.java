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

import uk.ac.ed.ph.qtiworks.QtiWorksDeploymentException;
import uk.ac.ed.ph.qtiworks.config.JpaProductionConfiguration;
import uk.ac.ed.ph.qtiworks.config.PropertiesConfiguration;
import uk.ac.ed.ph.qtiworks.config.QtiWorksApplicationContextHelper;
import uk.ac.ed.ph.qtiworks.config.QtiWorksProfiles;
import uk.ac.ed.ph.qtiworks.config.ServicesConfiguration;
import uk.ac.ed.ph.qtiworks.config.WebApplicationConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.StandardServletEnvironment;

/**
 * Initialises the {@link AnnotationConfigWebApplicationContext} used for the QTIWorks
 * web application.
 * <p>
 * This first searches for a required parameter called {@value #DEPLOYMENT_PROPERTIES_FILE_PARAM}
 * using the standard properties set in the Spring {@link StandardServletEnvironment}.
 * This parameter specifies the Spring {@link Resource} URI of a file providing the runtime
 * deployment configuration for the QTIWorks web application. If no parameter is found, or if it
 * resolves to something which can't be loaded then a {@link QtiWorksDeploymentException} is
 * thrown and the application will not start.
 *
 * @author David McKain
 */
public class QtiWorksWebApplicationContextInitializer implements ApplicationContextInitializer<AnnotationConfigWebApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(QtiWorksWebApplicationContextInitializer.class);

    /**
     * Name of the parameter specifying the URI of the <code>qtiworks-deployment.properties</code>
     * providing the runtime configuration for the application.
     */
    public static final String DEPLOYMENT_PROPERTIES_FILE_PARAM = "qtiWorksDeploymentPropertiesUri";

    @Override
    public void initialize(final AnnotationConfigWebApplicationContext applicationContext) {
        /* Set appropriate profile */
        final ConfigurableEnvironment environment = applicationContext.getEnvironment(); /* (Should be StandardServletEnvironment) */
        environment.setActiveProfiles(QtiWorksProfiles.WEBAPP);

        /* Extract URI of deployment configuration. */
        logger.info("Searching for required paremeter {} within {}", DEPLOYMENT_PROPERTIES_FILE_PARAM, environment.getPropertySources());
        final String deploymentPropertiesUri = environment.getProperty(DEPLOYMENT_PROPERTIES_FILE_PARAM);
        if (deploymentPropertiesUri==null) {
            throw new QtiWorksDeploymentException("QTIWorks configuration error - required parameter " + DEPLOYMENT_PROPERTIES_FILE_PARAM
                    + " was not found after searching " + environment.getPropertySources());
        }

        /* Register property sources */
        QtiWorksApplicationContextHelper.registerConfigPropertySources(applicationContext, deploymentPropertiesUri);

        /* Then set up ApplicationContext */
        logger.info("Initialising QTIWorks webapp ApplicationContext");

        applicationContext.register(
            PropertiesConfiguration.class,
            JpaProductionConfiguration.class,
            ServicesConfiguration.class,
            WebApplicationConfiguration.class
        );
    }
}
