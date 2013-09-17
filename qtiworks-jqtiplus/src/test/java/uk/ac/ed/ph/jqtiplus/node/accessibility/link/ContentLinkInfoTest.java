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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.accessibility.link;

import uk.ac.ed.ph.jqtiplus.node.accessibility.AccessElement;
import uk.ac.ed.ph.jqtiplus.node.accessibility.AccessibilityInfo;
import uk.ac.ed.ph.jqtiplus.node.accessibility.ApipAccessibility;
import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.P;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.notification.NotificationType;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.DummyValidationContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * FIXME: Document this type
 *
 * @author Zack Pierce
 */
public class ContentLinkInfoTest {

    DummyValidationContext context;
    NotificationRecorder recorder;
    AssessmentItem item;
    ItemBody itemBody;
    P paragraph;
    ApipAccessibility apipAccessibility;
    AccessibilityInfo accessibilityInfo;
    AccessElement accessElement;

    @Before
    public void before() {
        context = new DummyValidationContext();
        recorder = new NotificationRecorder(NotificationLevel.WARNING);
        context.addNotificationListener(recorder);
        item = new AssessmentItem();
        itemBody = new ItemBody(item);
        item.setItemBody(itemBody);
        paragraph = new P(itemBody);
        itemBody.getNodeGroups().getBlockGroup().getChildren().add(paragraph);
        apipAccessibility = new ApipAccessibility(item);
        item.setApipAccessibility(apipAccessibility);
        accessibilityInfo = new AccessibilityInfo(apipAccessibility);
        apipAccessibility.setAccessibilityInfo(accessibilityInfo);
        accessElement = new AccessElement(accessibilityInfo);
        accessibilityInfo.getAccessElements().add(accessElement);
    }

    public void assertNoNotificationsRecorded() {
        Assert.assertEquals(0, recorder.getNotifications().size());
    }

    @Test
    public void testValidateCorrectObjectLinkToApipProducesNoErrors() {
        final ContentLinkInfo cli = new ContentLinkInfo(accessElement);
        cli.setObjectLink(new ObjectLink(null));
        cli.setApipLinkIdentifierRef(Identifier.parseString("hello"));
        cli.validate(context);
        assertNoNotificationsRecorded();
    }

    @Test
    public void testValidateCorrectObjectLinkToQtiProducesNoErrors() {
        final String targetId = "hello";
        paragraph.setId(Identifier.parseString(targetId));
        final ContentLinkInfo cli = new ContentLinkInfo(accessElement);
        cli.setObjectLink(new ObjectLink(null));
        cli.setQtiLinkIdentifierRef(targetId);
        cli.validate(context);
        assertNoNotificationsRecorded();
    }

    @Test
    public void testValidateMissingObjectLinkAndTextLinkProducesError() {
        final ContentLinkInfo cli = new ContentLinkInfo(accessElement);
        cli.setApipLinkIdentifierRef(Identifier.parseString("hello"));
        cli.validate(context);
        Assert.assertEquals(1, recorder.getNotifications().size());
        Assert.assertEquals(NotificationLevel.ERROR, recorder.getNotifications().get(0).getNotificationLevel());
        Assert.assertEquals(NotificationType.MODEL_VALIDATION, recorder.getNotifications().get(0).getNotificationType());
        Assert.assertEquals("Not enough children: objectLinkOrTextLink. Expected at least: 1, but found: 0", recorder.getNotifications().get(0).getMessage());
    }

    @Test
    public void testValidateHavingNeitherIdRefProducesError() {
        final ContentLinkInfo cli = new ContentLinkInfo(null);
        cli.setObjectLink(new ObjectLink(null));
        cli.setQtiLinkIdentifierRef(null);
        cli.setApipLinkIdentifierRef(null);
        cli.validate(context);
        Assert.assertEquals(1, recorder.getNotifications().size());
        Assert.assertEquals(NotificationLevel.ERROR, recorder.getNotifications().get(0).getNotificationLevel());
        Assert.assertEquals(NotificationType.MODEL_VALIDATION, recorder.getNotifications().get(0).getNotificationType());
        Assert.assertTrue(recorder.getNotifications().get(0).getMessage().startsWith("contentLinkInfo must have either the qtiLinkIdentifierRef or apipLinkIdentifierRef specified, but both are null."));
    }

    @Test
    public void testValidateHavingBothIdRefsProducesError() {
        final String targetId = "hello";
        paragraph.setId(Identifier.parseString(targetId));
        final ContentLinkInfo cli = new ContentLinkInfo(accessElement);
        cli.setObjectLink(new ObjectLink(cli));
        cli.setQtiLinkIdentifierRef(targetId);
        cli.setApipLinkIdentifierRef(Identifier.parseString("helloAgain"));
        cli.validate(context);
        Assert.assertEquals(1, recorder.getNotifications().size());
        Assert.assertEquals(NotificationLevel.ERROR, recorder.getNotifications().get(0).getNotificationLevel());
        Assert.assertEquals(NotificationType.MODEL_VALIDATION, recorder.getNotifications().get(0).getNotificationType());
        Assert.assertEquals("contentLinkInfo must only have either qtiLinkIdentifierRef or apipLinkIdentifierRef specified, but not both.", recorder.getNotifications().get(0).getMessage());
    }

    @Test
    public void testValidateQtiReferencesNoMatchingIdElementProducesError() {
        paragraph.setId(Identifier.parseString("wrongId"));
        final ContentLinkInfo cli = new ContentLinkInfo(accessElement);
        cli.setObjectLink(new ObjectLink(cli));
        cli.setQtiLinkIdentifierRef("targetId");
        cli.validate(context);
        Assert.assertEquals(1, recorder.getNotifications().size());
        Assert.assertEquals(NotificationLevel.ERROR, recorder.getNotifications().get(0).getNotificationLevel());
        Assert.assertEquals(NotificationType.MODEL_VALIDATION, recorder.getNotifications().get(0).getNotificationType());
        Assert.assertEquals("contentLinkInfo with qtiLinkIdentifierRef of 'targetId' does not point to an extant Qti content element.", recorder.getNotifications().get(0).getMessage());
    }

    @Test
    public void testValidateQtiRefButNoAssociatedQtiContentProducesError() {
        final ContentLinkInfo cli = new ContentLinkInfo(null);
        cli.setObjectLink(new ObjectLink(cli));
        cli.setQtiLinkIdentifierRef("targetId");
        cli.validate(context);
        Assert.assertEquals(1, recorder.getNotifications().size());
        Assert.assertEquals(NotificationLevel.ERROR, recorder.getNotifications().get(0).getNotificationLevel());
        Assert.assertEquals(NotificationType.MODEL_VALIDATION, recorder.getNotifications().get(0).getNotificationType());
        Assert.assertEquals("No QTI content container found associated with this accessibility metadata", recorder.getNotifications().get(0).getMessage());
    }

}
