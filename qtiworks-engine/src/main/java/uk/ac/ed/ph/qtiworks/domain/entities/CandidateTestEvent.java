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

import uk.ac.ed.ph.jqtiplus.state.TestSessionState;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Subclass of {@link CandidateEvent} for events specific to a test
 *
 * @see CandidateEvent
 * @see CandidateItemEvent
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_test_events")
@NamedQueries({
    @NamedQuery(name="CandidateTestEvent.getForSession",
            query="SELECT e"
                + "  FROM CandidateTestEvent e"
                + "  WHERE e.candidateSession = :candidateSession"
                + "  ORDER BY e.id"),
    @NamedQuery(name="CandidateTestEvent.getForSessionReversed",
            query="SELECT e"
                + "  FROM CandidateTestEvent e"
                + "  WHERE e.candidateSession = :candidateSession"
                + "  ORDER BY e.id DESC")
})
public class CandidateTestEvent extends CandidateEvent implements BaseEntity {

    private static final long serialVersionUID = 6121745930649659116L;

    /** Type of event */
    @Basic(optional=false)
    @Column(name="test_event_type", updatable=false, length=16)
    @Enumerated(EnumType.STRING)
    private CandidateTestEventType testEventType;

    /** Value of the <code>duration</code> test variable (at the time this event was created) */
    @Basic(optional=false)
    @Column(name="duration", updatable=false)
    private double duration;

    /**
     * {@link TestSessionState} serialized in a custom XML format.
     *
     * @see TestSesssionStateXmlMarshaller
     */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=false)
    @Column(name="test_session_state_xml", updatable=false)
    private String testSessionStateXml;

    //------------------------------------------------------------

    public CandidateTestEvent() {
        super(CandidateEventCategory.TEST);
    }

    //----------------------------------------------------------

    public CandidateTestEventType getTestEventType() {
        return testEventType;
    }

    public void setTestEventType(final CandidateTestEventType testEventType) {
        this.testEventType = testEventType;
    }


    public double getDuration() {
        return duration;
    }

    public void setDuration(final double duration) {
        this.duration = duration;
    }


    public String getTestSessionStateXml() {
        return testSessionStateXml;
    }

    public void setTestSessionStateXml(final String testSessionStateXml) {
        this.testSessionStateXml = testSessionStateXml;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(id=" + getId()
                + ",eventCategory=" + getEventCategory()
                + ",testEventType=" + testEventType
                + ")";
    }
}
