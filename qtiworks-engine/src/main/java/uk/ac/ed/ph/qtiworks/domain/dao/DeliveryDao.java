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
package uk.ac.ed.ph.qtiworks.domain.dao;

import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliverySettings;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO implementation for the {@link Delivery} entity.
 *
 * @author David McKain
 */
@Repository
@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
public class DeliveryDao extends GenericDao<Delivery> {

    @PersistenceContext
    private EntityManager em;

    public DeliveryDao() {
        super(Delivery.class);
    }

    public List<Delivery> getForAssessment(final Assessment assessment) {
        final TypedQuery<Delivery> query = em.createNamedQuery("Delivery.getForAssessment", Delivery.class);
        query.setParameter("assessment", assessment);
        return query.getResultList();
    }

    public List<Delivery> getForAssessmentAndType(final Assessment assessment, final DeliveryType deliveryType) {
        final TypedQuery<Delivery> query = em.createNamedQuery("Delivery.getForAssessmentAndType", Delivery.class);
        query.setParameter("assessment", assessment);
        query.setParameter("deliveryType", deliveryType);
        return query.getResultList();
    }

    public long countForAssessmentAndType(final Assessment assessment, final DeliveryType deliveryType) {
        final Query query = em.createNamedQuery("Delivery.countForAssessmentAndType");
        query.setParameter("assessment", assessment);
        query.setParameter("deliveryType", deliveryType);
        return extractCountResult(query);
    }

    public List<Delivery> getUsingSettings(final DeliverySettings deliverySettings) {
        final TypedQuery<Delivery> query = em.createNamedQuery("Delivery.getUsingSettings", Delivery.class);
        query.setParameter("deliverySettings", deliverySettings);
        return query.getResultList();
    }

    public long countUsingSettings(final DeliverySettings deliverySettings) {
        final Query query = em.createNamedQuery("Delivery.countUsingSettings");
        query.setParameter("deliverySettings", deliverySettings);
        return extractCountResult(query);
    }

}
