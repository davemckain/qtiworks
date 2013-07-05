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
package uk.ac.ed.ph.qtiworks.services.dao;

import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.User;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO implementation for the {@link CandidateSession} entity.
 *
 * @author David McKain
 */
@Repository
@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
public class CandidateSessionDao extends GenericDao<CandidateSession> {

    @PersistenceContext
    private EntityManager em;

    public CandidateSessionDao() {
        super(CandidateSession.class);
    }

    public List<CandidateSession> getForCandidate(final User candidate) {
        final TypedQuery<CandidateSession> query = em.createNamedQuery("CandidateSession.getForCandidate", CandidateSession.class);
        query.setParameter("candidate", candidate);
        return query.getResultList();
    }

    public long countForDelivery(final Delivery delivery) {
        final Query query = em.createNamedQuery("CandidateSession.countForDelivery");
        query.setParameter("delivery", delivery);
        return extractCountResult(query);
    }

    public long countNonTerminatedForDelivery(final Delivery delivery) {
        final Query query = em.createNamedQuery("CandidateSession.countNonTerminatedForDelivery");
        query.setParameter("delivery", delivery);
        return extractCountResult(query);
    }

    public long countForAssessment(final Assessment assessment) {
        final Query query = em.createNamedQuery("CandidateSession.countForAssessment");
        query.setParameter("assessment", assessment);
        return extractCountResult(query);
    }

    public long countCandidateRoleForAssessment(final Assessment assessment) {
        final Query query = em.createNamedQuery("CandidateSession.countCandidateRoleForAssessment");
        query.setParameter("assessment", assessment);
        return extractCountResult(query);
    }

    public List<CandidateSession> getNonTerminatedForAssessment(final Assessment assessment) {
        final TypedQuery<CandidateSession> query = em.createNamedQuery("CandidateSession.getNonTerminatedForAssessment", CandidateSession.class);
        query.setParameter("assessment", assessment);
        return query.getResultList();
    }

    public long countNonTerminatedForAssessment(final Assessment assessment) {
        final Query query = em.createNamedQuery("CandidateSession.countNonTerminatedForAssessment");
        query.setParameter("assessment", assessment);
        return extractCountResult(query);
    }

    public long countNonTerminatedCandidateRoleForAssessment(final Assessment assessment) {
        final Query query = em.createNamedQuery("CandidateSession.countNonTerminatedCandidateRoleForAssessment");
        query.setParameter("assessment", assessment);
        return extractCountResult(query);
    }

    public List<CandidateSession> getForDelivery(final Delivery delivery) {
        final TypedQuery<CandidateSession> query = em.createNamedQuery("CandidateSession.getForDelivery", CandidateSession.class);
        query.setParameter("delivery", delivery);
        return query.getResultList();
    }

    public List<CandidateSession> getNonTerminatedForDeliveryAndCandidate(final Delivery delivery, final User candidate) {
        final TypedQuery<CandidateSession> query = em.createNamedQuery("CandidateSession.getNonTerminatedForDeliveryAndCandidate", CandidateSession.class);
        query.setParameter("delivery", delivery);
        query.setParameter("candidate", candidate);
        return query.getResultList();
    }

    public List<CandidateSession> getAll() {
        final TypedQuery<CandidateSession> query = em.createNamedQuery("CandidateSession.getAll", CandidateSession.class);
        return query.getResultList();
    }

}
