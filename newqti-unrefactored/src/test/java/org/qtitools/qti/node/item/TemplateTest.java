/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package org.qtitools.qti.node.item;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import uk.ac.ed.ph.jqtiplus.control.AssessmentItemController;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemState;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;

import org.junit.Test;

public class TemplateTest {
    static String fileName = "Template.xml";
    
    @Test
    public void test() {
        AssessmentItemController itemController = UnitTestHelper.loadItemForControl(fileName, TemplateTest.class);
        AssessmentItemState itemState = itemController.getItemState();
        AssessmentItem item = itemController.getItem();
        
        assertNull(itemState.getResponseValue("response"));
        assertNull(itemState.getOverriddenDefaultValue("response"));
        assertNull(itemState.getOverriddenCorrectResponseValue("response"));
        
        assertNull(itemState.getTemplateValue("template1"));
        assertEquals("initial", item.getTemplateDeclaration("template1").getDefaultValue().evaluate().toString());
        assertEquals("initial", itemController.computeDefaultValue("template1").toString());
            
//        assertEquals(null, item.getTemplateDeclaration("template1").getValue());
//        assertEquals("initial", item.getTemplateDeclaration("template1").getDefaultValue().getValue().toString());
        
        assertNull(itemState.getTemplateValue("template2"));
        assertEquals("initial", item.getTemplateDeclaration("template2").getDefaultValue().evaluate().toString());
        assertEquals("initial", itemController.computeDefaultValue("template2").toString());
        
//        assertEquals(null, item.getTemplateDeclaration("template2").getValue());
//        assertEquals("initial", item.getTemplateDeclaration("template2").getDefaultValue().getValue().toString());
        
        itemController.initialize(null);
        
        assertEquals("incorrect", itemState.getResponseValue("response").toString());
        assertEquals("incorrect", itemController.computeDefaultValue("response").toString());
        assertEquals("correct", itemController.computeCorrectResponse("response").toString());
        
//        assertEquals("incorrect", item.getResponseDeclaration("response").getValue().toString());
//        assertEquals("incorrect", item.getResponseDeclaration("response").getDefaultValue().getValue().toString());
//        assertEquals("correct", item.getResponseDeclaration("response").getCorrectResponse().getValue().toString());
        
        assertEquals("final", itemState.getTemplateValue("template1").toString());
        assertEquals("initial", itemController.computeDefaultValue("template1").toString());
//        
//        assertEquals("final", item.getTemplateDeclaration("template1").getValue().toString());
//        assertEquals("initial", item.getTemplateDeclaration("template1").getDefaultValue().getValue().toString());
        
        assertEquals("initial", itemState.getTemplateValue("template2").toString());
        assertEquals("initial", itemController.computeDefaultValue("template2").toString());
        
//        assertEquals("initial", item.getTemplateDeclaration("template2").getValue().toString());
//        assertEquals("initial", item.getTemplateDeclaration("template2").getDefaultValue().getValue().toString());
    }

}
