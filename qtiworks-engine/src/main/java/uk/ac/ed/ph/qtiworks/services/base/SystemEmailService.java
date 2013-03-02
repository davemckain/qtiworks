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
package uk.ac.ed.ph.qtiworks.services.base;

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksDeploymentSettings;
import uk.ac.ed.ph.qtiworks.domain.SystemMailMessage;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.utils.IoUtilities;
import uk.ac.ed.ph.qtiworks.web.view.ElFunctions;

import uk.ac.ed.ph.jqtiplus.internal.util.Pair;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

/**
 * System email service
 * <p>
 * This can be enabled or disabled within {@link QtiWorksDeploymentSettings}; it falls back
 * to logging the messages when disabled.
 *
 * @author David McKain
 */
@Service
public final class SystemEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SystemEmailService.class);

    @Resource
    private QtiWorksDeploymentSettings qtiWorksDeploymentSettings;

    @Resource
    private Validator jsr303Validator;

    @Resource
    private MailSender mailSender;

    //-------------------------------------------------

    public void sendEmail(final SystemMailMessage message) {
        /* Validate the raw message */
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(message, "message");
        jsr303Validator.validate(message, errors);
        if (errors.hasErrors()) {
            throw new QtiWorksRuntimeException("Invalid CstMailMessage Object: " + errors);
        }

        /* Construct Spring SimpleMailMessage */
        final SimpleMailMessage simpleMailMessage = constructSimpleMailMessage(message);

        /* Send email (if turned on) */
        if (qtiWorksDeploymentSettings.isEmailEnabled()) {
            try {
                mailSender.send(simpleMailMessage);
            }
            catch (final Exception e) {
                logger.error("Could not send message " + simpleMailMessage, e);
                throw new QtiWorksRuntimeException("Exception sending mail message", e);
            }
        }
        else {
            logger.warn("Email sending has been disabled within the application configuration, so not sending {}", simpleMailMessage);
        }
    }

    private SimpleMailMessage constructSimpleMailMessage(final SystemMailMessage message) {
        /* Read in message */
        final String templateResourceName = message.getTemplateResourceName();
        final InputStream templateStream = getClass().getClassLoader().getResourceAsStream(templateResourceName);
        if (templateStream==null) {
            throw new QtiWorksRuntimeException("Could not locate template resource in ClassPath at " + templateResourceName);
        }
        String template;
        try {
            template = IoUtilities.readUnicodeStream(templateStream);
        }
        catch (final IOException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }

        /* Register "global" pattern for admin email address that can be used within any
         * mail message template */
        message.addPattern("$ADMIN_EMAIL$", qtiWorksDeploymentSettings.getAdminEmailAddress());

        /* Apply replacements */
        for (final Pair<String,?> pattern : message.getPatterns()) {
            final String first = pattern.getFirst();
            final String second = formatString(pattern.getSecond());
            template = template.replace(first, second);
        }
        /* Now build message, faking things slightly in developer mode! */
        final SimpleMailMessage result = new SimpleMailMessage();
        result.setFrom(formatEmailAddress(message.getFromUser()));
        result.setSubject(message.getSubject());

        final List<InstructorUser> toUsers = message.getToUsers();
        final String[] toUsersAsStrings = new String[toUsers.size()];
        for (int i=0; i<toUsersAsStrings.length; i++) {
            toUsersAsStrings[i] = formatEmailAddress(toUsers.get(i));
        }
        if (qtiWorksDeploymentSettings.isEmailDevMode()) {
            final String adminAddress = qtiWorksDeploymentSettings.getAdminName() + " <" + qtiWorksDeploymentSettings.getAdminEmailAddress() + ">";
            result.setText("(Developer Mode is on - this would have been sent to "
                    + StringUtilities.join(toUsersAsStrings, " ")
                    + ")\n\n" + template);
            result.setTo(adminAddress);
        }
        else {
            result.setText(template);
            result.setTo(toUsersAsStrings);
        }

        /* That's it! */
        return result;
    }

    private String formatString(final Object object) {
        String result;
        if (object instanceof InstructorUser) {
            final InstructorUser user = (InstructorUser) object;
            result = user.getFirstName() + " " + user.getLastName();
        }
        else if (object instanceof Date) {
            final Date date = (Date) object;
            result = ElFunctions.formatDayDateAndTime(date);
        }
        else {
            result = object.toString();
        }
        return result;
    }

    private String formatEmailAddress(final InstructorUser user) {
        return user.getFirstName() + " " + user.getLastName()
            + " <" + user.getEmailAddress() + ">";
    }
}
