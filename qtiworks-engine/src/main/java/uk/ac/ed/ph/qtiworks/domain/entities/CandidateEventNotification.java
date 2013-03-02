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
package uk.ac.ed.ph.qtiworks.domain.entities;

import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationType;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * A runtime notification message generated while executing a particular {@link CandidateEvent}.
 * This is used for debugging and general auditing.
 *
 * @author David McKain
 */
@Entity
@Table(name="candidate_event_notifications")
@SequenceGenerator(name="candidateEventNotificationSequence", sequenceName="candidate_event_notification_sequence", initialValue=1, allocationSize=10)
@NamedQueries({
    @NamedQuery(name="CandidateEventNotification.getForEvent",
            query="SELECT n"
                + "  FROM CandidateEventNotification n"
                + "  WHERE n.candidateEvent = :candidateEvent"
                + "  ORDER BY n.id")
})
public class CandidateEventNotification implements BaseEntity {

    private static final long serialVersionUID = -2429213365619136433L;

    @Id
    @GeneratedValue(generator="candidateEventNotificationSequence")
    @Column(name="xnid")
    private Long xnid;

    /** {@link CandidateEvent} owning this notification */
    @ManyToOne(optional=false)
    @JoinColumn(name="xeid")
    private CandidateEvent candidateEvent;

    /** Type of notification */
    @Basic(optional=false)
    @Column(name="notification_type", updatable=false, length=16)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    /** Level of notification */
    @Basic(optional=false)
    @Column(name="notification_level", updatable=false, length=16)
    @Enumerated(EnumType.STRING)
    private NotificationLevel notificationLevel;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="node_qti_class_name", updatable=false)
    private String nodeQtiClassName;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="attr_local_name", updatable=false)
    private String attributeLocalName;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="attr_ns_uri", updatable=false)
    private String attributeNamespaceUri;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="system_id", updatable=false)
    private String systemId;

    @Basic(optional=true)
    @Column(name="line_number", updatable=false)
    private Integer lineNumber;

    @Basic(optional=true)
    @Column(name="column_number", updatable=false)
    private Integer columnNumber;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=false)
    @Column(name="message", updatable=false)
    private String message;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return xnid;
    }

    @Override
    public void setId(final Long id) {
        this.xnid = id;
    }


    public CandidateEvent getCandidateEvent() {
        return candidateEvent;
    }

    public void setCandidateEvent(final CandidateEvent candidateEvent) {
        this.candidateEvent = candidateEvent;
    }


    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(final NotificationType notificationType) {
        this.notificationType = notificationType;
    }


    public NotificationLevel getNotificationLevel() {
        return notificationLevel;
    }

    public void setNotificationLevel(final NotificationLevel notificationLevel) {
        this.notificationLevel = notificationLevel;
    }


    public String getNodeQtiClassName() {
        return nodeQtiClassName;
    }

    public void setNodeQtiClassName(final String nodeQtiClassName) {
        this.nodeQtiClassName = nodeQtiClassName;
    }


    public String getAttributeLocalName() {
        return attributeLocalName;
    }

    public void setAttributeLocalName(final String attributeLocalName) {
        this.attributeLocalName = attributeLocalName;
    }


    public String getAttributeNamespaceUri() {
        return attributeNamespaceUri;
    }

    public void setAttributeNamespaceUri(final String attributeNamespaceUri) {
        this.attributeNamespaceUri = attributeNamespaceUri;
    }


    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(final String systemId) {
        this.systemId = systemId;
    }


    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(final Integer lineNumber) {
        this.lineNumber = lineNumber;
    }


    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(final Integer columnNumber) {
        this.columnNumber = columnNumber;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(xnid=" + xnid
                + ",notificationType=" + notificationType
                + ",notificationLevel=" + notificationLevel
                + ",message=" + message
                + ")";
    }
}
