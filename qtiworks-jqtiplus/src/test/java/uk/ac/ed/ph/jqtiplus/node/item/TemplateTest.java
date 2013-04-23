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
package uk.ac.ed.ph.jqtiplus.node.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.NullValue;

import java.util.Date;

import org.junit.Test;

public class TemplateTest {

    private static String testFileName = "item/Template.xml";

    @Test
    public void test() throws Exception {
        final ItemSessionController itemSessionController = UnitTestHelper.loadUnitTestAssessmentItemForControl(testFileName, true);
        final Date timestamp = new Date();
        itemSessionController.initialize(timestamp);

        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();
        final AssessmentItem item = itemSessionController.getSubjectItem();

        final Identifier responseIdentifier = Identifier.parseString("response");
        assertEquals(NullValue.INSTANCE, itemSessionState.getResponseValue(responseIdentifier));
        assertNull(itemSessionState.getOverriddenDefaultValue(responseIdentifier));
        assertNull(itemSessionState.getOverriddenCorrectResponseValue(responseIdentifier));

        final Identifier template1Identifier = Identifier.parseString("template1");
        assertEquals("initial", itemSessionState.getTemplateValue(template1Identifier).toQtiString());
        assertEquals("initial", item.getTemplateDeclaration(template1Identifier).getDefaultValue().evaluate().toQtiString());
        assertEquals("initial", itemSessionController.computeDefaultValue(template1Identifier).toQtiString());

        final Identifier template2Identifier = Identifier.parseString("template2");
        assertEquals("initial", itemSessionState.getTemplateValue(template2Identifier).toQtiString());
        assertEquals("initial", item.getTemplateDeclaration(template2Identifier).getDefaultValue().evaluate().toQtiString());
        assertEquals("initial", itemSessionController.computeDefaultValue(template2Identifier).toQtiString());

        itemSessionController.performTemplateProcessing(timestamp);

        assertEquals("incorrect", itemSessionState.getResponseValue(responseIdentifier).toQtiString());
        assertEquals("incorrect", itemSessionController.computeDefaultValue(responseIdentifier).toQtiString());
        assertEquals("correct", itemSessionController.computeCorrectResponse(responseIdentifier).toQtiString());

        assertEquals("final", itemSessionState.getTemplateValue(template1Identifier).toQtiString());
        assertEquals("initial", itemSessionController.computeDefaultValue(template1Identifier).toQtiString());

        assertEquals("initial", itemSessionState.getTemplateValue(template2Identifier).toQtiString());
        assertEquals("initial", itemSessionController.computeDefaultValue(template2Identifier).toQtiString());

    }

}
