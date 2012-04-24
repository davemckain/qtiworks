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
package uk.ac.ed.ph.qtiworks.tools;

import uk.ac.ed.ph.qtiworks.config.JpaProductionConfiguration;
import uk.ac.ed.ph.qtiworks.config.PersistenceConfiguration;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Dev utility class for running arbitrary JPA code
 *
 * @author David McKain
 */
public final class JpaRunner {

    public static void main(final String[] args) {
        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(JpaProductionConfiguration.class, PersistenceConfiguration.class);
        ctx.refresh();

        final LocalContainerEntityManagerFactoryBean emf = ctx.getBean(LocalContainerEntityManagerFactoryBean.class);
        final EntityManagerFactory entityManagerFactory = emf.getObject();
        final EntityManager em = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        final InstructorUser user = new InstructorUser();
        user.setLoginName("dmckain2");
        user.setFirstName("David");
        user.setLastName("McKain");
        user.setEmailAddress("david.mckain@ed.ac.uk");
        user.setPasswordDigest("X");
        user.setSysAdmin(true);
        em.persist(user);

        em.flush();
        transaction.commit();
        entityManagerFactory.close();
    }
}
