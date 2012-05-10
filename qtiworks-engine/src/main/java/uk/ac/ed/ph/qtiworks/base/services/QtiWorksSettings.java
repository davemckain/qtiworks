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
package uk.ac.ed.ph.qtiworks.base.services;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

/**
 * Bean injected with the settings from <code>qtiworks.properties</code>
 *
 * @author David McKain
 */
@Component
@ImportResource("classpath:/qtiworks-config.xml")
public final class QtiWorksSettings implements Serializable {

    private static final long serialVersionUID = -8920166056971525690L;

    private @Value("${qtiworks.jdbc.driver}") String jdbcDriverClassName;
    private @Value("${qtiworks.jdbc.url}") String jdbcUrl;
    private @Value("${qtiworks.jdbc.username}") String jdbcUsername;
    private @Value("${qtiworks.jdbc.password}") String jdbcPassword;
    private @Value("${qtiworks.hibernate.dialect}") String hibernateDialect;
    private @Value("${qtiworks.email.enabled}") boolean emailEnabled;
    private @Value("${qtiworks.email.devmode}") boolean emailDevMode;
    private @Value("${qtiworks.email.admin.name}") String emailAdminName;
    private @Value("${qtiworks.email.admin.address}") String emailAdminAddress;
    private @Value("${qtiworks.email.smtp.host}") String emailSmtpHost;
    private @Value("${qtiworks.filesystem.base}") String filesystemBase;
    private @Value("${qtiworks.user.password}") String bootstrapUserPassword;

    public String getJdbcDriverClassName() {
        return jdbcDriverClassName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public String getHibernateDialect() {
        return hibernateDialect;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public boolean isEmailDevMode() {
        return emailDevMode;
    }

    public String getEmailAdminName() {
        return emailAdminName;
    }

    public String getEmailAdminAddress() {
        return emailAdminAddress;
    }

    public String getEmailSmtpHost() {
        return emailSmtpHost;
    }

    public String getFilesystemBase() {
        return filesystemBase;
    }

    public String getBootstrapUserPassword() {
        return bootstrapUserPassword;
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
