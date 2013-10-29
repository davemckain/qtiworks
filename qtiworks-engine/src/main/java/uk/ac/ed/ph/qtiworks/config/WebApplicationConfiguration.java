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

import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksDeploymentSettings;
import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksProperties;
import uk.ac.ed.ph.qtiworks.web.LoggingHandlerExceptionResolver;
import uk.ac.ed.ph.qtiworks.web.ThreadLocalCleaner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Defines webapp-level configuration
 *
 * @author David McKain
 */
@Configuration
@ComponentScan(basePackages={"uk.ac.ed.ph.qtiworks.web.services"})
public class WebApplicationConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WebApplicationConfiguration.class);

    public static final long MAX_UPLOAD_SIZE = 1024 * 1024 * 8;

    @Resource
    private WebApplicationContext applicationContext;

    @Resource
    private QtiWorksProperties qtiWorksProperties;

    @Resource
    private QtiWorksDeploymentSettings qtiWorksDeploymentSettings;

    @PostConstruct
    public void passConfigToServletContext() {
        /* Stash configuration beans into the ServletContext to make them available to JSPs */
        final ServletContext servletContext = applicationContext.getServletContext();
        servletContext.setAttribute("qtiWorksProperties", qtiWorksProperties);
        servletContext.setAttribute("qtiWorksDeploymentSettings", qtiWorksDeploymentSettings);
        logger.info("Stashed configuration beans {} and {} into ServletContext", qtiWorksProperties, qtiWorksDeploymentSettings);
    }

    @Bean(destroyMethod="purgeBlacklistedThreadLocals")
    public ThreadLocalCleaner threadLocalCleaner() {
        return new ThreadLocalCleaner();
    }

    @Bean
    public String webappContextPath() {
        return applicationContext.getServletContext().getContextPath();
    }

    @Bean
    MessageSource messageSource() {
        final ResourceBundleMessageSource result = new ResourceBundleMessageSource();
        result.setUseCodeAsDefaultMessage(true); /* Handy for debugging! */
        result.setBasename("messages");
        return result;
    }

    @Bean
    MultipartResolver multipartResolver() {
        final CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(MAX_UPLOAD_SIZE);
        return resolver;
    }

    /**
     * Bean to log any intercepted Exceptions.
     * <p>
     * This will be noticed by each {@link DispatcherServlet} set up for
     * the various MVC configurations.
     */
    @Bean
    LoggingHandlerExceptionResolver loggingHandlerExceptionResolver() {
        return new LoggingHandlerExceptionResolver();
    }
}
