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
package uk.ac.ed.ph.qtiworks.config;

import uk.ac.ed.ph.qtiworks.mathassess.MathAssessExtensionPackage;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.xmlutils.SchemaCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.SimpleSchemaCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.SimpleXsltStylesheetCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetCache;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * Defines webapp-level configuration
 *
 * @author David McKain
 */
@Configuration
@ComponentScan(basePackages={"uk.ac.ed.ph.qtiworks.services"})
public class WebApplicationConfiguration {

    public static final long MAX_UPLOAD_SIZE = 1024 * 1024 * 8;

    @Resource
    private WebApplicationContext webApplicationContext;

    @Bean
    public String contextPath() {
        return webApplicationContext.getServletContext().getContextPath();
    }

    @Bean
    public SchemaCache schemaCache() {
        return new SimpleSchemaCache();
    }

    @Bean
    public XsltStylesheetCache stylesheetCache() {
        return new SimpleXsltStylesheetCache();
    }

    @Bean
    public MathAssessExtensionPackage mathAssessExtensionPackage() {
        return new MathAssessExtensionPackage(stylesheetCache());
    }

    @Bean(initMethod="init", destroyMethod="destroy")
    public JqtiExtensionManager jqtiExtensionManager() {
        return new JqtiExtensionManager(mathAssessExtensionPackage());
    }

    @Bean
    public QtiXmlReader qtiXmlReader() {
        return new QtiXmlReader(jqtiExtensionManager(), schemaCache());
    }

    @Bean
    public AssessmentRenderer renderer() {
        return new AssessmentRenderer(jqtiExtensionManager(), contextPath(), stylesheetCache());
    }

    @Bean
    MessageSource messageSource() {
        final ResourceBundleMessageSource result = new ResourceBundleMessageSource();
        result.setBasename("messages");
        return result;
    }

    @Bean
    MultipartResolver multipartResolver() {
        final CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(MAX_UPLOAD_SIZE);
        return resolver;
    }
}
