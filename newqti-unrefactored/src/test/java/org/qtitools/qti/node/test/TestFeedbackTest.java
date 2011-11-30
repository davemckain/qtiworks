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

package org.qtitools.qti.node.test;

import static org.junit.Assert.assertEquals;

import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.TestFeedback;
import uk.ac.ed.ph.jqtiplus.node.test.TestFeedbackAccess;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestFeedbackTest
{
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
            {"TestFeedback-01.xml", "Test", TestFeedbackAccess.DURING, new String[] {"T-D-S-1", "T-D-S-2", "T-D-S-3", "T-D-H-2", "T-D-H-3"}},
            {"TestFeedback-01.xml", "Test", TestFeedbackAccess.AT_END, new String[] {"T-E-S-1"}},
            {"TestFeedback-01.xml", "P01", TestFeedbackAccess.DURING, new String[] {"P-D-S-1", "P-D-S-2", "P-D-S-3", "P-D-H-2", "P-D-H-3"}},
            {"TestFeedback-01.xml", "P01", TestFeedbackAccess.AT_END, new String[] {"P-E-S-1"}},
        });
    }

    private String fileName;
    private String identifier;
    private TestFeedbackAccess access;
    private List<String> expectedFeedbackTitles;

    public TestFeedbackTest(String fileName, String identifier, TestFeedbackAccess access, String[] expectedFeedbackTitles)
    {
        this.fileName = fileName;
        this.identifier = identifier;
        this.access = access;
        this.expectedFeedbackTitles = (expectedFeedbackTitles != null)
            ? Arrays.asList(expectedFeedbackTitles) : new ArrayList<String>();
    }

    private List<String> getFeedbackTitles(AssessmentTest test, String identifier, TestFeedbackAccess access)
    {
        List<String> feedbackTitles = new ArrayList<String>();

        List<TestFeedback> feedbacks = null;

        if (test.getIdentifier().equals(identifier))
            feedbacks = test.getTestFeedbacks(access);
        else
            for (TestPart testPart : test.getTestParts())
                if (testPart.getIdentifier().equals(identifier))
                {
                    feedbacks = testPart.getTestFeedbacks(access);
                    break;
                }

        if (feedbacks == null)
            throw new QTIEvaluationException("Cannot find: " + identifier);

        for (TestFeedback feedback : feedbacks)
            feedbackTitles.add(feedback.getTitle());

        return feedbackTitles;
    }

    @Test
    public void test()
    {
        AssessmentTest test = new AssessmentTest();
        test.load(getClass().getResource(fileName), jqtiController);

//        System.out.println(test.toXmlString());
//        System.out.println();

        test.getOutcomeProcessing().evaluate();

        List<String> feedbackTitles = getFeedbackTitles(test, identifier, access);

        assertEquals(expectedFeedbackTitles, feedbackTitles);
    }
}
