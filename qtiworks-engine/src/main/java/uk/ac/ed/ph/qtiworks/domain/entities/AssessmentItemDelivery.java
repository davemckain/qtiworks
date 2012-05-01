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
package uk.ac.ed.ph.qtiworks.domain.entities;

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Corresponds to a particular "delivery" of an {@link AssessmentItem} to a group of candidates.
 * <p>
 * This is going to be very simple in the first instance, but will get more complicated in future.
 *
 * FIXME: Will need to generalise and split out for item/test delivery in future.
 *
 * @author David McKain
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name="assessment_item_deliveries")
@SequenceGenerator(name="assessmentDeliverySequence", sequenceName="assessment_delivery_sequence", initialValue=1, allocationSize=5)
public class AssessmentItemDelivery extends AssessmentDelivery {

    private static final long serialVersionUID = 7693569112981982946L;

    /** Maximum number of attempts, as defined by {@link ItemSesessionControl} */
    @Basic(optional=false)
    @Column(name="max_attempts")
    private Integer maxAttempts;

    //------------------------------------------------------------

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(final Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

}
