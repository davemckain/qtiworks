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

import uk.ac.ed.ph.qtiworks.domain.entities.BaseEntity;
import uk.ac.ed.ph.qtiworks.domain.entities.User;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Concrete "not allowed" {@link Exception} thrown when a {@link User} does
 * not have an appropriate {@link Privilege} (possibly to access a particular
 * entity, but not necessarily).
 *
 * @author David McKain
 */
public final class PrivilegeException extends NotAllowedException {

    private static final long serialVersionUID = 963799679125087234L;

    protected final User user;
    protected final ImmutableList<Privilege> privileges;
    protected final ImmutableList<BaseEntity> targetEntities;

    public PrivilegeException(final User user, final Privilege privilege) {
        super("User " + user.getBusinessKey()
                + " does not have the required Privilege " + privilege);
        this.user = user;
        this.privileges = ImmutableList.of(privilege);
        this.targetEntities = null;
    }

    public PrivilegeException(final User user, final Privilege... privileges) {
        super("User " + user.getBusinessKey()
                + " does not have any of the required Privilege(s) " + Arrays.toString(privileges));
        this.user = user;
        this.privileges = ImmutableList.copyOf(privileges);
        this.targetEntities = null;
    }

    public PrivilegeException(final User user, final BaseEntity targetEntity, final Privilege... privileges) {
        super("User " + user.getBusinessKey()
                + " does not have any of the required Privilege(s) " + Arrays.toString(privileges)
                + " on target entity " + describeEntities(targetEntity));
        this.user = user;
        this.privileges = ImmutableList.copyOf(privileges);
        this.targetEntities = ImmutableList.of(targetEntity);
    }

    public PrivilegeException(final User user, final Privilege privilege, final BaseEntity... targetEntities) {
        super("User " + user.getBusinessKey()
                + " does not have the required Privilege " + privilege
                + " on target entities " + describeEntities(targetEntities));
        this.user = user;
        this.privileges = ImmutableList.of(privilege);
        this.targetEntities = ImmutableList.copyOf(targetEntities);
    }

    public PrivilegeException(final User user, final BaseEntity[] targetEntities, final Privilege... privileges) {
        super("User " + user.getBusinessKey()
                + " does not have any of the required Privilege(s) " + Arrays.toString(privileges)
                + " on target entities " +  describeEntities(targetEntities));
        this.user = user;
        this.privileges = ImmutableList.copyOf(privileges);
        this.targetEntities = ImmutableList.copyOf(targetEntities);
    }

    private static String describeEntities(final BaseEntity... targetEntities) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<targetEntities.length; i++) {
            final BaseEntity target = targetEntities[i];
            stringBuilder.append(target)
                .append(i<targetEntities.length-2 ? ", " : (i==targetEntities.length-2 ? " and " : ""));
        };
        return stringBuilder.toString();
    }

    public User getUser() {
        return user;
    }

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public List<BaseEntity> getTargetEntities() {
        return targetEntities;
    }
}
