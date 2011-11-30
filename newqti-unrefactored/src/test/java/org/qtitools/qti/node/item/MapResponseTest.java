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

import static org.junit.Assert.assertEquals;


import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MapResponseTest {
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][]
        {
            // null identifier, null or empty include and exclude categories (all items)
            { "MapResponse-Single.xml", new String[] {"J"},  -1.0},
            { "MapResponse-Single.xml", new String[] {"A"},  0.0},
            { "MapResponse-Single.xml", new String[] {"B"},  1.0},
            { "MapResponse-Single.xml", new String[] {"C"},  0.5},
            { "MapResponse-Single.xml", new String[] {"D"},  0.0},

            { "MapResponse-Multiple.xml", new String[] {"A"},  0.0},
            { "MapResponse-Multiple.xml", new String[] {"B"},  1.0},
            { "MapResponse-Multiple.xml", new String[] {"C"},  0.5},
            { "MapResponse-Multiple.xml", new String[] {"D"},  0.0},
            { "MapResponse-Multiple.xml", new String[] {"A", "B"},  1.0},
            { "MapResponse-Multiple.xml", new String[] {"C", "B"},  1.5},
            { "MapResponse-Multiple.xml", new String[] {"B", "C"},  1.5},
            { "MapResponse-Multiple.xml", new String[] {"A", "B", "B"},  1.0},
            { "MapResponse-Multiple.xml", new String[] {"B", "B", "C"},  1.5}
        });
    }
    
    private String fileName;
    private Value response;
    double expectedOutcome;
    
    public MapResponseTest(String fileName, String [] responses, double expectedOutcome) {
        this.fileName = fileName;
        this.expectedOutcome = expectedOutcome;
        
        if (responses.length == 1) {
            response = new IdentifierValue(responses[0]);
        } else {
            response = new MultipleValue();
            for (String s : responses)
                ((MultipleValue)response).add(new IdentifierValue(s));
        }
    }
    
    @Test
    public void test() {
        AssessmentItem item = new AssessmentItem();
        item.load(getClass().getResource(fileName), jqtiController);
        
        if (item.getResponseDeclaration("RESPONSE").getCardinality().isMultiple() && response.getCardinality().isSingle())
            response = new MultipleValue((SingleValue)response);
        
        item.getResponseDeclaration("RESPONSE").setValue(response);
        item.processResponses();
        
        assertEquals(expectedOutcome, ((FloatValue)item.getOutcomeValue("OUTCOME")).doubleValue(), 0.1);
    }
}
