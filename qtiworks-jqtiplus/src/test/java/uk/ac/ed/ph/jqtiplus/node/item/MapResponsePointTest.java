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

import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.PointValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MapResponsePointTest {

    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "MapResponsePoint-Single.xml", new String[] { "1 1" }, 1.0 },
                { "MapResponsePoint-Single.xml", new String[] { "1 3" }, 2.0 },
                { "MapResponsePoint-Single.xml", new String[] { "3 1" }, 3.0 },
                { "MapResponsePoint-Single.xml", new String[] { "3 3" }, 4.0 },
                { "MapResponsePoint-Single.xml", new String[] { "10 10" }, -1.0 },
                { "MapResponsePoint-Multiple.xml", new String[] { "1 1" }, 1.0 },
                { "MapResponsePoint-Multiple.xml", new String[] { "1 1", "1 1" }, 1.0 },
                { "MapResponsePoint-Multiple.xml", new String[] { "1 1", "1 3" }, 3.0 },
                { "MapResponsePoint-Multiple.xml", new String[] { "1 1", "1 3", "3 1" }, 6.0 },
                { "MapResponsePoint-Multiple.xml", new String[] { "1 1", "1 3", "3 1", "3 3" }, 10.0 },
                { "MapResponsePoint-Multiple.xml", new String[] { "1 1", "1 3", "3 1", "3 3", "3 3", "3 1" }, 10.0 },
        });
    }

    private final String fileName;
    private Value response;
    private final double expectedOutcome;

    public MapResponsePointTest(final String fileName, final String[] responses, final double expectedOutcome) {
        this.fileName = fileName;
        this.expectedOutcome = expectedOutcome;

        if (responses.length == 1) {
            response = PointValue.parseString(responses[0]);
        }
        else {
            final List<PointValue> values = new ArrayList<PointValue>();
            for (final String s : responses) {
                values.add(PointValue.parseString(s));
            }
            response = MultipleValue.createMultipleValue(values);
        }
    }

    @Test
    public void test() throws Exception {
        final ItemSessionController itemSessionController = UnitTestHelper.loadUnitTestAssessmentItemForControl("item/mapResponse/" + fileName, true);
        final Date timestamp = new Date();
        itemSessionController.initialize(timestamp);
        itemSessionController.performTemplateProcessing(timestamp);
        itemSessionController.enterItem(timestamp);

        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();
        final AssessmentItem item = itemSessionController.getSubjectItem();

        final Identifier responseIdentifier = Identifier.assumedLegal("RESPONSE");

        if (item.getResponseDeclaration(responseIdentifier).getCardinality().isMultiple() && response.getCardinality().isSingle()) {
            response = MultipleValue.createMultipleValue((SingleValue) response);
        }

        itemSessionState.setResponseValue(responseIdentifier, response);
        itemSessionController.performResponseProcessing(timestamp);

        assertEquals(new FloatValue(expectedOutcome), itemSessionState.getOutcomeValue(Identifier.assumedLegal("OUTCOME")));
    }
}
