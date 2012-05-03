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
package uk.ac.ed.ph.qtiworks.domain;

import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.mail.SimpleMailMessage;

/**
 * Trivial bean encapsulating details for a system-generated mail message.
 * <p>
 * This will end up as a Spring {@link SimpleMailMessage}, which this is somewhat
 * similar to.
 *
 * @author David McKain
 */
public final class SystemMailMessage {

    @NotNull
    private InstructorUser fromUser;

    @NotNull @Size(min=1)
    private final List<InstructorUser> toUsers;

    @NotNull @Size(min=1)
    private String subject;

    @NotNull
    private String templateResourceName;

    private final List<Pair<String,?>> patterns;

    public SystemMailMessage() {
        this.toUsers = new ArrayList<InstructorUser>();
        this.patterns = new ArrayList<Pair<String,?>>();
    }


    public InstructorUser getFromUser() {
        return fromUser;
    }

    public void setFromUser(final InstructorUser fromInstructorUser) {
        this.fromUser = fromInstructorUser;
    }


    public List<InstructorUser> getToUsers() {
        return Collections.unmodifiableList(toUsers);
    }

    public void setToUsers(final List<InstructorUser> toUsers) {
        this.toUsers.clear();
        this.toUsers.addAll(toUsers);
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }


    public String getTemplateResourceName() {
        return templateResourceName;
    }

    public void setTemplateResourceName(final String templateResourceName) {
        this.templateResourceName = templateResourceName;
    }


    public void addPattern(final String pattern, final String replacement) {
        patterns.add(new Pair<String, String>(pattern, replacement));
    }

    public void addPattern(final String pattern, final InstructorUser replacement) {
        patterns.add(new Pair<String, InstructorUser>(pattern, replacement));
    }

    public void addPattern(final String pattern, final Date replacement) {
        patterns.add(new Pair<String, Date>(pattern, replacement));
    }

    public List<Pair<String, ?>> getPatterns() {
        return Collections.unmodifiableList(patterns);
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
