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

import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ItemSessionControlTest
{
    private static class Control
    {
        private String itemIdentifier;
        private int maxAttempts;
        private boolean showFeedback;
        private boolean allowReview;
        private boolean showSolution;
        private boolean allowComment;
        private boolean allowSkipping;
        private boolean validateResponses;

        public Control
            ( String itemIdentifier
            , int maxAttempts
            , boolean showFeedback
            , boolean allowReview
            , boolean showSolution
            , boolean allowComment
            , boolean allowSkipping
            , boolean validateResponses )
        {
            this.itemIdentifier = itemIdentifier;
            this.maxAttempts = maxAttempts;
            this.showFeedback = showFeedback;
            this.allowReview = allowReview;
            this.showSolution = showSolution;
            this.allowComment = allowComment;
            this.allowSkipping = allowSkipping;
            this.validateResponses = validateResponses;
        }

        public String getItemIdentifier()
        {
            return itemIdentifier;
        }

        public void assertEquals(ItemSessionControl itemSessionControl)
        {
            Assert.assertEquals(ItemSessionControl.ATTR_MAX_ATTEMPTS_NAME, maxAttempts, itemSessionControl.getMaxAttempts());
            Assert.assertEquals(ItemSessionControl.ATTR_SHOW_FEEDBACK_NAME, showFeedback, itemSessionControl.getShowFeedback());
            Assert.assertEquals(ItemSessionControl.ATTR_ALLOW_REVIEW_NAME, allowReview, itemSessionControl.getAllowReview());
            Assert.assertEquals(ItemSessionControl.ATTR_SHOW_SOLUTION_NAME, showSolution, itemSessionControl.getShowSolution());
            Assert.assertEquals(ItemSessionControl.ATTR_ALLOW_COMMENT_NAME, allowComment, itemSessionControl.getAllowComment());
            Assert.assertEquals(ItemSessionControl.ATTR_ALLOW_SKIPPING_NAME, allowSkipping, itemSessionControl.getAllowSkipping());
            Assert.assertEquals(ItemSessionControl.ATTR_VALIDATE_RESPONSES_NAME, validateResponses, itemSessionControl.getValidateResponses());
        }
    }

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
            {"ItemSessionControl-01.xml", new Control[] {
                new Control("I01", 1, false, true, false, false, true, false),
                new Control("I02", 1, false, true, false, false, true, false),
                new Control("I03", 1, false, true, false, false, true, false),
                new Control("I04", 1, false, true, false, false, true, false),
                new Control("I05", 1, false, true, false, false, true, false),
            }},
            {"ItemSessionControl-02.xml", new Control[] {
                new Control("I01", 1, false, true, false, false, true, false),
                new Control("I02", 1, false, true, false, false, true, false),
                new Control("I03", 1, false, true, false, false, true, false),
                new Control("I04", 1, false, true, false, false, true, false),
                new Control("I05", 1, false, true, false, false, true, false),
            }},
            {"ItemSessionControl-03.xml", new Control[] {
                new Control("I01", 1, true, true, false, false, true, false),
                new Control("I02", 3, true, true, false, false, true, false),
                new Control("I03", 1, true, true, false, false, true, false),
                new Control("I04", 3, true, true, false, false, true, false),
                new Control("I05", 2, true, true, false, false, true, false),
            }},
        });
    }

    private String fileName;
    private Control[] expectedControl;

    public ItemSessionControlTest(String fileName, Control[] expectedControl)
    {
        this.fileName = fileName;
        this.expectedControl = expectedControl;
    }

    @Test
    public void test()
    {
        AssessmentTest firstTest = new AssessmentTest();
        firstTest.load(getClass().getResource(fileName), jqtiController);

//        System.out.println("---> FIRST TEST <---");
//        System.out.println(firstTest.toXmlString());
//        System.out.println();

        AssessmentTest secondTest = new AssessmentTest();
        secondTest.load(firstTest.toXmlString(), jqtiController);

//        System.out.println("---> SECOND TEST <---");
//        System.out.println(secondTest.toXmlString());
//        System.out.println();

        for (int i = 0; i < expectedControl.length; i++)
        {
            AssessmentItemRef firstTestsItem = firstTest.lookupItemRef(expectedControl[i].getItemIdentifier());
            AssessmentItemRef secondTestsItem = secondTest.lookupItemRef(expectedControl[i].getItemIdentifier());

            expectedControl[i].assertEquals(firstTestsItem.getItemSessionControl());
            expectedControl[i].assertEquals(secondTestsItem.getItemSessionControl());
        }
    }
}
