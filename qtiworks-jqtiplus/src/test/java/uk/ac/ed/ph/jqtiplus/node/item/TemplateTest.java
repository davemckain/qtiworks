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
package uk.ac.ed.ph.jqtiplus.node.item;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;

import org.junit.Test;

public class TemplateTest {

    private static String fileName = "Template.xml";

    @Test
    public void test() throws Exception {
        final ItemSessionController itemSessionController = UnitTestHelper.loadTestAssessmentItemForControl(fileName, TemplateTest.class);
        final ItemSessionState itemState = itemSessionController.getItemSessionState();
        final AssessmentItem item = itemSessionController.getItem();

        assertNull(itemState.getResponseValue("response"));
        assertNull(itemState.getOverriddenDefaultValue("response"));
        assertNull(itemState.getOverriddenCorrectResponseValue("response"));

        assertNull(itemState.getTemplateValue("template1"));
        assertEquals("initial", item.getTemplateDeclaration("template1").getDefaultValue().evaluate().toQtiString());
        assertEquals("initial", itemSessionController.computeDefaultValue("template1").toQtiString());

        assertNull(itemState.getTemplateValue("template2"));
        assertEquals("initial", item.getTemplateDeclaration("template2").getDefaultValue().evaluate().toQtiString());
        assertEquals("initial", itemSessionController.computeDefaultValue("template2").toQtiString());

        itemSessionController.initialize();

        assertEquals("incorrect", itemState.getResponseValue("response").toQtiString());
        assertEquals("incorrect", itemSessionController.computeDefaultValue("response").toQtiString());
        assertEquals("correct", itemSessionController.computeCorrectResponse("response").toQtiString());

        assertEquals("final", itemState.getTemplateValue("template1").toQtiString());
        assertEquals("initial", itemSessionController.computeDefaultValue("template1").toQtiString());

        assertEquals("initial", itemState.getTemplateValue("template2").toQtiString());
        assertEquals("initial", itemSessionController.computeDefaultValue("template2").toQtiString());

    }

}
