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
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.mathassess.MathAssessExtensionPackage;
import uk.ac.ed.ph.qtiworks.services.base.SystemEmailService;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.xmlutils.SchemaCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.SimpleSchemaCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.SimpleXsltStylesheetCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.ejb.HibernatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Configuration for the main QTIWorks services
 *
 * @author David McKain
 */
@Configuration
@ComponentScan(basePackages={"uk.ac.ed.ph.qtiworks.rendering", "uk.ac.ed.ph.qtiworks.services"})
@EnableTransactionManagement
public class ServicesConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ServicesConfiguration.class);

    @Resource
    private QtiWorksDeploymentSettings qtiWorksDeploymentSettings;

    @Bean
    public IdentityContext identityContext() {
        return new IdentityContext();
    }

    @Bean
    RequestTimestampContext requestTimestampContext() {
        return new RequestTimestampContext();
    }

    @Bean
    public LocalValidatorFactoryBean jsr303Validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public MailSender mailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(qtiWorksDeploymentSettings.getEmailSmtpHost());
        return mailSender;
    }

    @Bean
    public SystemEmailService systemEmailService() {
        final SystemEmailService emailService = new SystemEmailService();
        return emailService;
    }

    @Resource(name="extraJpaProperties")
    private Properties extraJpaProperties;

    @Bean(destroyMethod="close")
    public DataSource dataSource() {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(qtiWorksDeploymentSettings.getJdbcDriverClassName());
        dataSource.setUrl(qtiWorksDeploymentSettings.getJdbcUrl());
        dataSource.setUsername(qtiWorksDeploymentSettings.getJdbcUsername());
        dataSource.setPassword(qtiWorksDeploymentSettings.getJdbcPassword());
        return dataSource;
    }

    @Bean
    public Properties jpaProperties() {
        final Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", qtiWorksDeploymentSettings.getHibernateDialect());
        jpaProperties.put("hibernate.id.new_generator_mappings", Boolean.TRUE);
        jpaProperties.putAll(extraJpaProperties);
        return jpaProperties;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean() {
        final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPersistenceProviderClass(HibernatePersistence.class);
        emf.setDataSource(dataSource());
        emf.setJpaProperties(jpaProperties());
        emf.setPackagesToScan("uk.ac.ed.ph.qtiworks.domain.entities");
        return emf;
    }

    /**
     * Translates persistence Exceptions thrown by {@link Repository} classes like DAOs.
     * See section 12.6.4 of Spring documentation.
     * You don't need to use this bean anywhere.
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    /**
     * (See also use of {@link EnableTransactionManagement} above)
     */
    @Bean
    public JpaTransactionManager jpaTransactionManager() {
        final JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean().getObject());
        return jpaTransactionManager;
    }

    @Bean
    public SchemaCache schemaCache() {
        return new SimpleSchemaCache();
    }

    @Bean
    public XsltStylesheetCache stylesheetCache() {
        return new SimpleXsltStylesheetCache();
    }

    @Bean(initMethod="init", destroyMethod="destroy")
    public JqtiExtensionManager jqtiExtensionManager() {
        final List<JqtiExtensionPackage<?>> extensionPackages = new ArrayList<JqtiExtensionPackage<?>>();

        /* Enable MathAssess extensions if requested */
        if (qtiWorksDeploymentSettings.isEnableMathAssessExtension()) {
            logger.info("Enabling the MathAssess extensions");
            extensionPackages.add(new MathAssessExtensionPackage(stylesheetCache()));
        }

        return new JqtiExtensionManager(extensionPackages);
    }

    @Bean
    public QtiXmlReader qtiXmlReader() {
        return new QtiXmlReader(jqtiExtensionManager(), schemaCache());
    }

    @Bean
    public QtiSerializer qtiSerializer() {
        return new QtiSerializer(jqtiExtensionManager());
    }

    /**
     * MIME type definitions used when serving up content. I have copied a generic Linux
     * <code>/etc/mime.types</code> into the project so that we don't get OS-specific results.
     */
    @Bean
    public FileTypeMap fileTypeMap() {
        return new MimetypesFileTypeMap(getClass().getClassLoader().getResourceAsStream("mime.types"));
    }
}
