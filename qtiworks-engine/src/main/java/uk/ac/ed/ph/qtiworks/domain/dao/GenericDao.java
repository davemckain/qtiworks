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

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.entities.BaseEntity;
import uk.ac.ed.ph.qtiworks.domain.entities.TimestampedOnCreation;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Convenience partial DAO implementation that uses generics to give surprisingly
 * nice results.
 * <p>
 * The only slightly smelly bit is the requirement to pass an instance of the
 * domain entity class in the constructor.
 *
 * @author David McKain
 */
@Repository
@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
public abstract class GenericDao<E extends BaseEntity> {

    private static final Logger logger = LoggerFactory.getLogger(GenericDao.class);

    private final Class<E> entityClass;

    @PersistenceContext
    private EntityManager em;

    @Resource
    private RequestTimestampContext requestTimestampContext;

    protected GenericDao(final Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public EntityManager getEm() {
        return em;
    }

    public E findById(final Long id) {
        return em.find(entityClass, id);
    }

    public E requireFindById(final Long id) throws DomainEntityNotFoundException {
        Assert.ensureNotNull(id, "Entity ID");
        final E result = findById(id);
        ensureFindSuccess(result, id);
        return result;
    }

    protected void ensureFindSuccess(final E result, final Object key) throws DomainEntityNotFoundException {
        if (result==null) {
            throw new DomainEntityNotFoundException(entityClass, key);
        }
    }

    /**
     * Helper to extract the result of a complex "find" query, which would be expected to return 0
     * or 1 results. This returns null if no results were found.
     */
    protected E extractNullableFindResult(final TypedQuery<E> query) {
        final List<E> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    protected long extractCountResult(final Query query) {
        return ((Long) query.getSingleResult()).longValue();
    }

    @Transactional(readOnly=false, propagation=Propagation.REQUIRED)
    public E update(final E entity) {
        try {
            return em.merge(entity);
        }
        catch (final RuntimeException e) {
            logger.warn("update() failed on entity {}", entity, e);
            throw e;
        }
    }

    @Transactional(readOnly=false, propagation=Propagation.REQUIRED)
    public E persist(final E entity) {
        /* Set timestamp if not done by caller */
        if (entity instanceof TimestampedOnCreation) {
            final TimestampedOnCreation timestampedEntity = (TimestampedOnCreation) entity;
            if (timestampedEntity.getCreationTime()==null) {
                timestampedEntity.setCreationTime(requestTimestampContext.getCurrentRequestTimestamp());
            }
        }
        try {
            em.persist(entity);
        }
        catch (final RuntimeException e) {
            logger.warn("persist() failed on entity {}", entity, e);
            throw e;
        }
        return entity;
    }

    @Transactional(readOnly=false, propagation=Propagation.REQUIRED)
    public void remove(final E entity) {
        try {
            em.remove(entity);
        }
        catch (final RuntimeException e) {
            logger.warn("remove() failed on entity {}", entity, e);
            throw e;
        }
    }

    @Transactional(readOnly=false, propagation=Propagation.REQUIRED)
    public void flush() {
        try {
            em.flush();
        }
        catch (final RuntimeException e) {
            logger.warn("flush() failed on DAO for {}", entityClass.getSimpleName(), e);
            throw e;
        }
    }
}
