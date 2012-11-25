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
package uk.ac.ed.ph.qtiworks.domain.dao;

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEventCategory;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;

import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO implementation for the {@link CandidateEvent} entity.
 *
 * @author David McKain
 */
@Repository
@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
public class CandidateEventDao extends GenericDao<CandidateEvent> {

    @PersistenceContext
    private EntityManager em;

    public CandidateEventDao() {
        super(CandidateEvent.class);
    }

    public List<CandidateEvent> getForSession(final CandidateSession candidateSession) {
        final TypedQuery<CandidateEvent> query = em.createNamedQuery("CandidateEvent.getForSession", CandidateEvent.class);
        query.setParameter("candidateSession", candidateSession);
        return query.getResultList();
    }

    public List<CandidateEvent> getForSession(final CandidateSession candidateSession, final CandidateEventCategory eventCategory) {
        final TypedQuery<CandidateEvent> query = em.createNamedQuery("CandidateEvent.getInCategoryForSession", CandidateEvent.class);
        query.setParameter("candidateSession", candidateSession);
        query.setParameter("candidateEventCategory", eventCategory);
        return query.getResultList();
    }

    public List<CandidateEvent> getForSessionReversed(final CandidateSession candidateSession, final CandidateEventCategory eventCategory) {
        final TypedQuery<CandidateEvent> query = em.createNamedQuery("CandidateEvent.getInCategoryForSessionReversed", CandidateEvent.class);
        query.setParameter("candidateSession", candidateSession);
        query.setParameter("candidateEventCategory", eventCategory);
        return query.getResultList();
    }

    public CandidateEvent getNewestEventInSession(final CandidateSession candidateSession) {
        final TypedQuery<CandidateEvent> query = em.createNamedQuery("CandidateEvent.getForSessionReversed", CandidateEvent.class);
        query.setParameter("candidateSession", candidateSession);
        query.setMaxResults(1);
        return extractNullableFindResult(query);
    }

    public CandidateEvent getNewestEventInSession(final CandidateSession candidateSession, final CandidateEventCategory eventCategory) {
        final TypedQuery<CandidateEvent> query = em.createNamedQuery("CandidateEvent.getInCategoryForSessionReversed", CandidateEvent.class);
        query.setParameter("candidateSession", candidateSession);
        query.setParameter("candidateEventCategory", eventCategory);
        query.setMaxResults(1);
        return extractNullableFindResult(query);
    }

    public CandidateEvent getNewestTestItemEventInSession(final CandidateSession candidateSession, final TestPlanNodeKey itemKey) {
        final TypedQuery<CandidateEvent> query = em.createNamedQuery("CandidateEvent.getTestItemEventsForSessionReversed", CandidateEvent.class);
        query.setParameter("candidateSession", candidateSession);
        query.setParameter("itemKey", itemKey.toString());
        query.setMaxResults(1);
        return extractNullableFindResult(query);
    }
}
