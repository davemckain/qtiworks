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
package uk.ac.ed.ph.qtiworks.domain.entities.lti;

import uk.ac.ed.ph.qtiworks.domain.entities.BaseEntity;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.TimestampedOnCreation;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

/**
 * Encapsulates a single LTI link
 *
 * @author David McKain
 */
@Entity
@Table(name="lti_links")
@SequenceGenerator(name="ltiLinkSequence", sequenceName="lti_link_sequence", initialValue=1, allocationSize=10)
public class LtiLink implements BaseEntity, TimestampedOnCreation {

    private static final long serialVersionUID = 3492080657015295877L;

    @Id
    @GeneratedValue(generator="ltiLinkSequence")
    @Column(name="lid")
    private Long id;

    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    /** {@link InstructorUser} who created this link */
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="creator_uid", updatable=false)
    private InstructorUser creator;

    /** Delivery corresponding to this link */
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="did", updatable=false)
    private ItemDelivery delivery;

    /** Consumer key for this launch */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=false)
    @Column(name="consumer_key", updatable=false, unique=false)
    private String consumerKey;

    /** Consumer secret for this launch */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=false)
    @Column(name="consumer_secret", updatable=false, unique=false)
    private String consumerSecret;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }


    @Override
    public Date getCreationTime() {
        return ObjectUtilities.safeClone(creationTime);
    }

    @Override
    public void setCreationTime(final Date creationTime) {
        this.creationTime = ObjectUtilities.safeClone(creationTime);
    }


    public InstructorUser getCreator() {
        return creator;
    }

    public void setCreator(final InstructorUser creator) {
        this.creator = creator;
    }


    public ItemDelivery getDelivery() {
        return delivery;
    }

    public void setDelivery(final ItemDelivery delivery) {
        this.delivery = delivery;
    }


    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(final String consumerKey) {
        this.consumerKey = consumerKey;
    }


    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(final String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }
}
